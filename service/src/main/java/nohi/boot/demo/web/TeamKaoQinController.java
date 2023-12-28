package nohi.boot.demo.web;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.entity.TeamUser;
import nohi.boot.demo.service.KaoQinService;
import nohi.boot.demo.service.db.TeamSignService;
import nohi.boot.demo.service.db.TeamUserService;
import nohi.boot.demo.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

}
