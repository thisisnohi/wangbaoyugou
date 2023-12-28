package nohi.boot.demo.dto.kaoqin;

import lombok.Data;

import java.util.List;

/**
 * <h3>service</h3>
 *
 * @author NOHI
 * @description <p>考勤请求体</p>
 * @date 2023/12/28 13:47
 **/
@Data
public class KaoQinRespDataRecord {
    private String cardId;
    private String personName;
    private String recDate;
    private String recTime;
    private String verifyMode;
    private String equNo;
    private String equNoTwo;
    private String operDate;
    private String deptId;
    private String systemId;
    private String status;
    private String ndaySign;
}
