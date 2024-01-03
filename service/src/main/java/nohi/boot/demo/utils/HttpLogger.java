package nohi.boot.demo.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p></p>
 * @date 2024/01/03 11:05
 **/
@Slf4j
public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
      log.debug(message);
    }
}
