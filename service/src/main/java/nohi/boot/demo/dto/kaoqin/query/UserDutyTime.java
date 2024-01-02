package nohi.boot.demo.dto.kaoqin.query;

import lombok.Data;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>用户考勤数据</p>
 * @date 2024/01/02 14:19
 **/
@Data
public class UserDutyTime {
    private String name;
    private String phone;
    private String date;
    private String minTime;
    private String maxTime;

}
