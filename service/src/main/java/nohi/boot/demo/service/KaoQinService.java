package nohi.boot.demo.service;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.dto.kaoqin.KaoQinReqParam;
import nohi.boot.demo.dto.kaoqin.KaoQinResp;
import nohi.boot.demo.dto.kaoqin.KaoQinRespDataRecord;
import nohi.boot.demo.entity.TeamSign;
import nohi.boot.demo.entity.TeamUser;
import nohi.boot.demo.service.db.TeamSignService;
import nohi.boot.demo.utils.DateUtils;
import nohi.boot.demo.utils.OkHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤数据处理
 *
 * @author NOHI
 * @date 2023/12/28 11:12
 */
@Service
@Slf4j
public class KaoQinService {
    @Value("${data.kqUrl}")
    private String url;
    public static String HEADER_TOKEN = "Accesstoken";
    public static String HEADER_CARDID = "Cardid";

    @Autowired
    private TeamSignService teamSignService;


    /**
     * 同步用户开始-结束日期内的考勤数据
     *
     * @param user      用户信息
     * @param startDate 开始日期
     * @param endDate   结束日期
     */
    public String synUserKaoQin(String title, TeamUser user, Date startDate, Date endDate) {
        // 返回信息
        String msg = null;
        String startDateStr = DateUtils.format(startDate);
        String endDateStr = DateUtils.format(endDate);
        String subTitle = String.format("%s %s [%s-%s]同步", title, user.getName(), startDateStr, endDateStr);
        log.info("{}", subTitle);

        // 构建请求头、请求参数
        KaoQinReqParam param = this.buildRequestParam(user, startDateStr, endDateStr);
        Map<String, String> headerMap = buildRequestHeaderMap(user);
        try {

            String respString = OkHttpUtils.getInstance().postJson(url, headerMap, JSONObject.toJSONString(param));
            log.info("{} respString:{}", subTitle, respString);
            // 转换响应数据
            KaoQinResp resp = JSONObject.parseObject(respString, KaoQinResp.class);
            log.info("{} resp:{}", subTitle, resp);
            if (null == resp) {
                log.warn("{} 响应报文为空", subTitle);
                msg = user.getName() + "\t获取考勤响应报文为空";
                return msg;
            } else if (resp.getCode() != 0) {
                log.warn("{} 响应数据code不为0，code:{}，msg:{}", subTitle, resp.getCode(), resp.getMsg());
                msg = user.getName() + "\t" + resp.getMsg();
                return msg;
            } else if (null == resp.getData() || null == resp.getData().getRecords() || resp.getData().getRecords().isEmpty()) {
                log.warn("{} 响应数据为空", subTitle);
                msg = user.getName() + "\t 响应数据为空";
                return msg;
            }
            // 保存数据入库
            this.saveKaoQinData(subTitle, resp, user);
        } catch (Exception e) {
            log.error("{} 异常:{}", subTitle, e.getMessage(), e);
            msg = user.getName() + "\t " + e.getMessage();
        }

        return msg;
    }


    /**
     * 保存考勤数据
     *
     * @param resp 返回对象
     * @param user 用户
     */
    private void saveKaoQinData(String title, KaoQinResp resp, TeamUser user) {
        String subTitle = String.format("%s %s 考勤数据保存", title, user.getName());
        log.info("{}", subTitle);
        if (resp == null || resp.getData() == null || null == resp.getData().getRecords()) {
            log.warn("{} 返回数据为空", subTitle);
            return;
        }
        List<KaoQinRespDataRecord> records = resp.getData().getRecords();
        records.forEach(item -> {
            TeamSign signInfo = new TeamSign();
            signInfo.setUserId(user.getId());
            signInfo.setDate(DateUtils.parseDate(item.getRecDate(), DateUtils.HYPHEN_DATE));
            signInfo.setTime(item.getRecTime());
            signInfo.setSignTime(DateUtils.parseDate(item.getOperDate(), DateUtils.HYPHEN_TIME));

            teamSignService.saveOrUpdateInfo(signInfo);
        });
    }

    private Map<String, String> buildRequestHeaderMap(TeamUser user) {
        Map<String, String> map = new HashMap<>();
        map.put(HEADER_TOKEN, user.getAccesstoken());
        map.put(HEADER_CARDID, user.getCardid());
        return map;
    }

    /**
     * 组装请求参数
     */
    private KaoQinReqParam buildRequestParam(TeamUser user, String startDate, String endDate) {
        KaoQinReqParam param = new KaoQinReqParam();
        param.setStartSignDate(startDate);
        param.setEndSignDate(endDate);
        param.setPersonId(user.getPersonid());
        return param;
    }


    public List getUserSign() {

        return null;
    }
}
