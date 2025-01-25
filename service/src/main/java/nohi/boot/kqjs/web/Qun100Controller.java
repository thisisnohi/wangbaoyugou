package nohi.boot.kqjs.web;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.service.qun100.Qun100Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 * 群报数
 * </p>
 *
 * @author NOHI
 */
@Slf4j
@RestController
@RequestMapping("/qun100")
public class Qun100Controller {

    @Autowired
    private Qun100Service service;

    @Operation(summary = "同步考勤数据")
    @PostMapping("/synFormData")
    public RespMeta<Void> monthDataCompare(@DateTimeFormat(pattern = "yyyy-MM-dd")Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd")Date endDate) {
        log.info("同步考勤数据 [{} - {}]", startDate, endDate);
        service.synFormData(startDate, endDate);

        return new RespMeta<>();
    }

    @Operation(summary = "query")
    @PostMapping("/query")
    public RespMeta<MonthData> query(@RequestBody KqQueryDto req) {
        log.info("月考勤汇总:{}", JSONObject.toJSONString(req));
        // 查询
        return service.query(req, null);
    }
}
