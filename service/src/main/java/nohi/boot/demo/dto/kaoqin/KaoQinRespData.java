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
public class KaoQinRespData {
    private List<KaoQinRespDataRecord> records;
    private int total = 1;
    private int size = 10;
    private int current = 10;
    private int pages = 10;

}
