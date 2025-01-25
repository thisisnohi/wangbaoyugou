package nohi.boot.kqjs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 群报数数据服务配置
 * @author NOHI
 * @description:
 * @create 2021-01-03 20:32
 **/
@Data
@ConfigurationProperties(prefix = "qun100")
@Configuration
public class Qun100Config {
    // 群报数服务器地址
    private String serviceUrl;
    private String appid;
    private String secret;
}
