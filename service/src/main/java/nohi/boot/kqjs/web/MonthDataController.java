package nohi.boot.kqjs.web;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.common.dto.page.Page;
import nohi.boot.common.dto.page.PageQueryReq;
import nohi.boot.demo.dto.userInfo.UserInfoQueryResp;
import nohi.boot.demo.entity.TeamUser;
import nohi.boot.demo.utils.PageUtils;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.service.monthdata.MonthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 大楼考勤 前端控制器
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
@Slf4j
@RestController
@RequestMapping("/monthData")
public class MonthDataController {

    @Autowired
    private MonthDataService service;

    @Operation(summary = "月考勤对比")
    @PostMapping("/monthDataCompare")
    public RespMeta<List<?>> monthDataCompare(@RequestBody PageQueryReq<KqQueryDto> req) {
        log.info("月考勤对比:{}", JSONObject.toJSONString(req));
        // 查询
        return service.monthDataCompare(req.getData(), req.getPage());
    }

    @Operation(summary = "月考勤汇总")
    @PostMapping("/monthData")
    public RespMeta<MonthData> monthData(@RequestBody KqQueryDto req) {
        log.info("月考勤汇总:{}", JSONObject.toJSONString(req));
        // 查询
        return service.monthData(req, null);
    }

    @Operation(summary = "月考勤明细")
    @PostMapping("/monthDataDetail")
    public RespMeta<MonthData> monthDataDetail(@RequestBody KqQueryDto req) {
        log.info("月考勤明细:{}", JSONObject.toJSONString(req));
        // 查询
        return service.monthDataDetail(req);
    }
}
