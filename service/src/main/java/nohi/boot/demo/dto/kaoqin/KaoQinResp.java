package nohi.boot.demo.dto.kaoqin;

import lombok.Data;

/**
 * <h3>service</h3>
 *
 * @author NOHI
 * @description <p>考勤请求体</p>
 * @date 2023/12/28 13:47
 **/
@Data
public class KaoQinResp {
    private int code;
    private String msg;
    private KaoQinRespData data;

}
