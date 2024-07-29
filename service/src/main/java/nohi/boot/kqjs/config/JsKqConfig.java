package nohi.boot.kqjs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author NOHI
 * @description:
 * @create 2021-01-03 20:32
 **/
@Data
@ConfigurationProperties(prefix = "jskq")
@Configuration
public class JsKqConfig {
    private String filePath;
}
