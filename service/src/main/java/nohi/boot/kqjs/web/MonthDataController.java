package nohi.boot.kqjs.web;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.common.dto.RespMeta;
import nohi.boot.common.dto.page.Page;
import nohi.boot.common.dto.page.PageQueryReq;
import nohi.boot.common.utils.FileUtils;
import nohi.boot.demo.dto.userInfo.UserInfoQueryResp;
import nohi.boot.demo.entity.TeamUser;
import nohi.boot.demo.utils.PageUtils;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.dto.kq.MonthData;
import nohi.boot.kqjs.service.monthdata.MonthDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
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

    @Operation(summary = "导出月考勤对比")
    @PostMapping("/exportMonthDataCompare")
    @ApiOperationSupport(author = "thisisnohi@163.com")
    public RespMeta<String> exportMonthDataCompare(HttpServletResponse response, @RequestBody KqQueryDto req) {
        log.info("导出 月考勤对比[{}]", JSONObject.toJSONString(req));

        RespMeta<String> respMeta = null;
        try {
            respMeta = service.exportMonthDataCompare(req);

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

    @Operation(summary = "月考勤汇总")
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


    @Operation(summary = "月考勤明细")
    @PostMapping("/monthDataDetail")
    public RespMeta<MonthData> monthDataDetail(@RequestBody KqQueryDto req) {
        log.info("月考勤明细:{}", JSONObject.toJSONString(req));
        // 查询
        return service.monthDataDetail(req);
    }


    @Operation(summary = "导出月考勤汇总")
    @PostMapping("/exportMonthDataDetail")
    @ApiOperationSupport(author = "thisisnohi@163.com")
    public Object exportMonthDataDetail(HttpServletResponse response, @RequestBody KqQueryDto req) {
        log.info("导出导出月考勤汇总[{}]", JSONObject.toJSONString(req));
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
}
