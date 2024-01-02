package nohi.boot.demo.web;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.service.db.CarMaintainHisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 团队考勤
 *
 * @author NOHI
 * @date 2023/12/28 14:48
 */
@Tag(name = "测试")
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Autowired
    CarMaintainHisService carMaintainHisService;

    @Value("${data.url}")
    private String dataUrl;



    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "sync")
    @GetMapping("/sync")
    public Object syncUser() throws InterruptedException {
        String url = "http://114.55.63.4:888/SVHx002/RptDataListDV?queryname=rpt_SVHistory002&ECEvent=querybypage&sessionid=5b3e5dba-c6b2-41b7-8b46-90776a62d7b6&seriolid=0.7680936864139332";
//        url = "http://127.0.0.1:8888/mock/car";
        long start = System.currentTimeMillis();
        Set<String> pageError = new HashSet<>();
        for (int i = 10; i <= 300 ; i++) {
            pageError.add(carMaintainHisService.synData(url,i, 1000));
            TimeUnit.SECONDS.sleep(10);
        }
        long end = System.currentTimeMillis();
        log.info("==耗时[{}]", (end - start));
        log.error("错误页:{}", pageError);
        return "success";
    }

    @ApiOperationSupport(author = "thisisnohi@163.com")
    @Operation(summary = "sync")
    @GetMapping("/sync/{pagesize}/{from}/{to}")
    public Object sync(@PathVariable("pagesize") int pagesize, @PathVariable("from") int from, @PathVariable("to") int to) throws InterruptedException {
        String url = dataUrl;
        long start = System.currentTimeMillis();
        Set<String> pageError = new HashSet<>();

        if (from <= to) {
            for (int i = from; i <= to ; i++) {
                pageError.add(carMaintainHisService.synData(url,i, pagesize));
                log.info("1第[{}]页 pagesize=[{}]", i, pagesize);
                TimeUnit.SECONDS.sleep(4);
            }
        } else {
            for (int i = from; i <= to ; i--) {
                pageError.add(carMaintainHisService.synData(url,i, pagesize));
                log.info("2第[{}]页 pagesize=[{}]", i, pagesize);
                TimeUnit.SECONDS.sleep(4);
            }
        }


        long end = System.currentTimeMillis();
        log.info("==耗时[{}]", (end - start));
        log.error("错误页:{}", pageError);
        return "success";
    }

}
