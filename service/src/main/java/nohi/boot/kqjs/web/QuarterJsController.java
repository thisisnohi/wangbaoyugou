package nohi.boot.kqjs.web;

import com.alibaba.fastjson2.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.common.utils.FileUtils;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.service.impl.QuarterJsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.Map;

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

    @Operation(summary = "结算考勤汇总")
    @PostMapping("/monthData")
    public RespMeta<MonthData> monthData(@RequestBody KqQueryDto req) {
        log.info("月考勤汇总:{}", JSONObject.toJSONString(req));
        // 查询
        return service.monthData(req, null);
    }

    @Operation(summary = "导出月考勤汇总")
    @PostMapping("/exportMonthData")
    @ApiOperationSupport(author = "thisisnohi@163.com")
    public RespMeta<String> exportMonthData(HttpServletResponse response, @RequestBody KqQueryDto req) {
        log.info("导出月考勤汇总[{}]", JSONObject.toJSONString(req));

        RespMeta<String> respMeta = null;
        try {
            respMeta = service.exportMonthData(req);

            if (!FileUtils.exists(respMeta.getData())) {
                respMeta.setResCode("1");
                respMeta.setResMsg("文件不存在[" + respMeta.getData() + "]");
                return respMeta;
            }

            File file = new File(respMeta.getData());
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment;filename=abc.xlsx");

            try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
                byte[] buff = new byte[1024];
                OutputStream os  = response.getOutputStream();
                int i = 0;
                while ((i = bis.read(buff)) != -1) {
                    os.write(buff, 0, i);
                    os.flush();
                }
                // 如果文件流返回，则不能再返回消息
                return null;
            } catch (IOException e) {
                throw e;
            }

        } catch (Exception e) {
            log.error("下载异常:{}", e.getMessage(), e);
            respMeta = new RespMeta<>();
            respMeta.setResCode("1");
            respMeta.setResMsg("下载异常:" + e.getMessage());
        }
        return respMeta;
    }


    @Operation(summary = "结算考勤明细")
    @PostMapping("/monthDataDetail")
    public RespMeta<MonthData> monthDataDetail(@RequestBody KqQueryDto req) {
        log.info("月考勤明细:{}", JSONObject.toJSONString(req));
        // 查询
        return service.monthDataDetail(req);
    }


    @Operation(summary = "导出结算考勤明细")
    @PostMapping("/exportMonthDetail")
    @ApiOperationSupport(author = "thisisnohi@163.com")
    public RespMeta<String> exportMonthDetail(HttpServletResponse response, @RequestBody KqQueryDto req) {
        log.info("导出结算考勤明细[{}]", JSONObject.toJSONString(req));

        RespMeta<String> respMeta = null;
        try {
            respMeta = service.exportMonthDataDetail(req);

            if (!FileUtils.exists(respMeta.getData())) {
                respMeta.setResCode("1");
                respMeta.setResMsg("文件不存在[" + respMeta.getData() + "]");
                return respMeta;
            }

            File file = new File(respMeta.getData());
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment;filename=abc.xlsx");

            try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
                byte[] buff = new byte[1024];
                OutputStream os  = response.getOutputStream();
                int i = 0;
                while ((i = bis.read(buff)) != -1) {
                    os.write(buff, 0, i);
                    os.flush();
                }
                // 如果文件流返回，则不能再返回消息
                return null;
            } catch (IOException e) {
                throw e;
            }

        } catch (Exception e) {
            log.error("下载异常:{}", e.getMessage(), e);
            respMeta = new RespMeta<>();
            respMeta.setResCode("1");
            respMeta.setResMsg("下载异常:" + e.getMessage());
        }
        return respMeta;
    }

    /**
     * 判断人、工作日是否重复
     */
    @Operation(summary = "判断人、工作日是否重复")
    @PostMapping("/userWorkRepeat")
    public RespMeta<List<Map<String,?>>> userWorkRepeat(@RequestBody KqQueryDto req) {
        log.info("判断人、工作日是否重复:{}", JSONObject.toJSONString(req));
        // 查询
        return service.userWorkRepeat(req);
    }

    /**
     * 结算考勤有，月份考勤无
     */
    @Operation(summary = "结算考勤有，月份考勤无")
    @PostMapping("/jsWithOutApp")
    public RespMeta<List<Map<String,?>>> jsWithOutApp(@RequestBody KqQueryDto req) {
        log.info("结算考勤有，月份考勤无:{}", JSONObject.toJSONString(req));
        // 查询
        return service.jsWithOutApp(req);
    }

    /**
     * 月份考勤有，结算考勤无
     */
    @Operation(summary = "月份考勤有，结算考勤无")
    @PostMapping("/appWithOutJs")
    public RespMeta<List<Map<String,?>>> appWithOutJs(@RequestBody KqQueryDto req) {
        log.info("月份考勤有，结算考勤无:{}", JSONObject.toJSONString(req));
        // 查询
        return service.appWithOutJs(req);
    }

    /**
     * app与结算考勤不一致
     */
    @Operation(summary = "app与结算考勤不一致")
    @PostMapping("/daysDiff")
    public RespMeta<List<Map<String,?>>> daysDiff(@RequestBody KqQueryDto req) {
        log.info("app与结算考勤不一致:{}", JSONObject.toJSONString(req));
        // 查询
        return service.daysDiff(req);
    }

}
