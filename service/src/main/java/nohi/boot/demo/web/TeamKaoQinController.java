package nohi.boot.demo.web;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.dto.Page;
import nohi.boot.demo.dto.userInfo.UesrInfoQueryReq;
import nohi.boot.demo.dto.userInfo.UserInfoQueryResp;
import nohi.boot.demo.dto.userInfo.UserInfoReq;
import nohi.boot.demo.dto.userInfo.UserInfoResp;
import nohi.boot.demo.entity.TeamUser;
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

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "同步用户打卡")
    @GetMapping("/sync/{userName}")
    public Object syncUser(@PathVariable("userName") String userName) {
        logger.info("同步用户 {}", userName);

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


    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "同步所有用户打卡数据")
    @GetMapping("/sync-all")
    public Object syncAll() {

        return null;
    }

    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "用户信息查询")
    @PostMapping("/userInfo/query")
    public UserInfoQueryResp userInfoQuery(@RequestBody UesrInfoQueryReq req) {
        log.info("用户信息查询开始，参数:{}",JSON.toJSONString(req));
        UserInfoQueryResp resp = new UserInfoQueryResp();
        TeamUser cond = new TeamUser();
        BeanUtils.copyProperties(req.getSearchParam(),cond);
        Page page = req.getPage();
        PageHelper.startPage(page.getPageIndex(),page.getPageSize());
        List<TeamUser> teamUsers = teamUserService.findListByCond(cond);
        resp.setPage(PageUtils.getPages(teamUsers,page));
        resp.setData((List<TeamUser>) PageUtils.getData(teamUsers));
        resp.setResCode("0");
        return resp;
    }

    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "用户信息维护")
    @PostMapping("/userInfo/save")
    public UserInfoResp userTokenQuery(@RequestBody @Validated UserInfoReq req) throws Exception {
        log.info("用户信息维护开始，参数:{}",JSON.toJSONString(req));
        UserInfoResp resp = new UserInfoResp();
        if(StringUtils.isBlank(req.getData().getId()) ){
            throw new Exception("id不能为空");
        }
        teamUserService.updateByExample(req.getData());
        resp.setResCode("0");
        return resp;
    }

}
