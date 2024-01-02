package nohi.boot.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author NOHI
 * @description:
 * @create 2021-01-03 20:32
 **/
@Data
@ConfigurationProperties(prefix = "dingtalk.robot")
@Configuration
public class MpConfig {
    private String appKey;
    private String appSecret;
    private String accessToken;
    private String dingTalkServer;
}
