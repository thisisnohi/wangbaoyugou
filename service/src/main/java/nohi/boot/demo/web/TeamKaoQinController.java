package nohi.boot.demo.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.config.MpConfig;
import nohi.boot.demo.consts.DingTalkConsts;
import nohi.boot.demo.consts.KaoQinConsts;
import nohi.boot.demo.dto.Page;
import nohi.boot.demo.dto.kaoqin.query.UserDutyTime;
import nohi.boot.demo.dto.userInfo.UesrInfoQueryReq;
import nohi.boot.demo.dto.userInfo.UserInfoQueryResp;
import nohi.boot.demo.dto.userInfo.UserInfoReq;
import nohi.boot.demo.dto.userInfo.UserInfoResp;
import nohi.boot.demo.entity.TeamUser;
import nohi.boot.demo.service.DingTalkClientService;
import nohi.boot.demo.service.KaoQinService;
import nohi.boot.demo.service.db.TeamSignService;
import nohi.boot.demo.service.db.TeamUserService;
import nohi.boot.demo.utils.DateUtils;
import nohi.boot.demo.utils.PageUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import nohi.boot.demo.dto.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 团队考勤
 *
 * @author NOHI
 * @date 2023/12/28 14:48
 */
@Tag(name = "团队考勤")
@RestController
@RequestMapping("/teamKaoQin")
@Slf4j
public class TeamKaoQinController {
    @Autowired
    TeamUserService teamUserService;
    @Autowired
    TeamSignService teamSignService;
    @Autowired
    KaoQinService kaoQinService;
    @Autowired
    DingTalkClientService dingTalkClientService;
    @Autowired
    private MpConfig mpConfig;


    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "同步用户打卡")
    @GetMapping("/sync/{userName}")
    public Object syncUser(@PathVariable("userName") String userName) {
        log.info("同步用户 {}", userName);

        //
        Date startDate = new Date();
        String dateStr = DateUtils.format(startDate);
        // 根据用户名获取用户
        TeamUser user = teamUserService.findTopUserByName(userName);

        // 同步考勤数据
        String msg = kaoQinService.synUserKaoQin("同步考勤", user, startDate, startDate);
        if (StringUtils.isNotBlank(msg)) {
            return msg;
        }
        return teamSignService.selectUserKaoQin(userName, dateStr, dateStr);
    }


    @Operation(summary = "同步所有用户打卡数据")
    @GetMapping("/sync-all")
    public Object syncAll() {
        Date startDate = new Date();
        List<String> msgList = Lists.newArrayList();
        String title = String.format("同步所有用户打卡数据[%s]", DateUtils.format(startDate));
        log.info("{} 开始", title);
        // 获取所有用户
        teamUserService.list().forEach(item -> {
            log.info("{} 同步[{}]", title, item.getName());
            // 同步所有用户考勤
            String msg = kaoQinService.synUserKaoQin("同步考勤", item, startDate, startDate);
            if (StringUtils.isNotBlank(msg)) {
                msgList.add(msg);
            }
        });

        // 同步考勤异常提醒
        if (!msgList.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String item : msgList) {
                sb.append(item).append("\n");
            }
            sb.append("\n消息来自NOHI机器人");
            this.alert("同步考勤数据", sb.toString());
        }
        log.info("{} 结束，msg:{}", title, msgList);
        return msgList;
    }

    @Operation(summary = "考勤预警, type=ON-DUTY-上班  OFF_DUTY:下班")
    @GetMapping("/alert/{type}")
    public Object alert(@PathVariable("type") String type) {
        Date startDate = new Date();
        String dateStr = DateUtils.format(startDate);
        String title = String.format("考勤预警[%s][%s]", dateStr, type);
        // 默认上班
        boolean duty = true;
        if (KaoQinConsts.DutyType.OFF_DUTY.getCode().equals(type)) {
            duty = false;
        }

        log.info("{} duty[{}]开始", title, duty);
        List<UserDutyTime> list = teamSignService.selectUserDutyTime(dateStr);

        StringBuilder sb = new StringBuilder();
        for (UserDutyTime item : list) {
            // 上班
            if (duty) {
                if (StringUtils.isBlank(item.getMinTime()) || item.getMinTime().compareTo(KaoQinConsts.DutyType.ON_DUTY.getValue()) > 0) {
                    sb.append(item.getName()).append(" 今日休息");
                } else {
                    sb.append(item.getName()).append(" 上班时间:").append(item.getMinTime());
                }
            } else {
                if (StringUtils.isBlank(item.getMaxTime()) || item.getMaxTime().compareTo(KaoQinConsts.DutyType.OFF_DUTY.getValue()) < 0) {
                    sb.append(item.getName()).append(" 下班未打卡");
                } else {
                    sb.append(item.getName()).append(" 下班时间:").append(item.getMaxTime());
                }
            }
            sb.append("\n");
        }
        log.info("{} msg:{}", title, sb.toString());

        sb.append("\n\n消息来自NOHI自定义机器人!!!");
        // 预警
        this.alert(dateStr + "打卡提醒", sb.toString());

        return sb.toString();
    }

    /**
     * 标题语法
     */
    private void alert(String msgTitle, String msg) {
        try {
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("markdown");
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            markdown.setTitle(msgTitle);
            markdown.setText(msg);
            request.setMarkdown(markdown);
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//          isAtAll类型如果不为Boolean，请升级至最新SDK
            at.setIsAtAll(true);
            request.setAt(at);
            OapiRobotSendResponse response = dingTalkClientService.execute(DingTalkConsts.ROOT_SEND + "?access_token=" + mpConfig.getAccessToken(), request, OapiRobotSendResponse.class);
            log.debug("响应信息：{}", response.getBody());
        } catch (ApiException e) {
            log.error("消息改善异常:{}", e.getMessage(), e);
        }
    }


    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "用户信息查询")
    @PostMapping("/userInfo/query")
    public UserInfoQueryResp userInfoQuery(@RequestBody UesrInfoQueryReq req) {
        log.info("用户信息查询开始，参数:{}", JSON.toJSONString(req));
        UserInfoQueryResp resp = new UserInfoQueryResp();
        TeamUser cond = new TeamUser();
        BeanUtils.copyProperties(req.getSearchParam(), cond);
        Page page = req.getPage();
        PageHelper.startPage(page.getPageIndex(), page.getPageSize());
        List<TeamUser> teamUsers = teamUserService.findListByCond(cond);
        resp.setPage(PageUtils.getPages(teamUsers, page));
        resp.setData((List<TeamUser>) PageUtils.getData(teamUsers));
        resp.setResCode("0");
        return resp;
    }

    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "用户信息维护")
    @PostMapping("/userInfo/save")
    public UserInfoResp userTokenQuery(@RequestBody @Validated UserInfoReq req) throws Exception {
        log.info("用户信息维护开始，参数:{}", JSON.toJSONString(req));
        UserInfoResp resp = new UserInfoResp();
        if (StringUtils.isBlank(req.getData().getId())) {
            throw new Exception("id不能为空");
        }
        teamUserService.updateByExample(req.getData());
        resp.setResCode("0");
        return resp;
    }

}
