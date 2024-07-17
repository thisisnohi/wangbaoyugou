package nohi.boot.kqjs.web;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.kqjs.service.impl.QuarterJsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 季度结算 前端控制器
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
@Tag(name = "季度结算")
@RestController
@RequestMapping("/quarterJs")
@Slf4j
public class QuarterJsController {

    @Autowired
    QuarterJsServiceImpl service;

    @Operation(summary = "导出结算信息")
    @GetMapping("/export")
    @ApiOperationSupport(author = "thisisnohi@163.com")
    public Object export(String workDateFrom, String workDateTo) {
        log.info("导出结算信息[{},{}]", workDateFrom, workDateTo);
        service.export(workDateFrom, workDateTo);

        return null;
    }
}
