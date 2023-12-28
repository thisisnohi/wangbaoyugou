package nohi.boot.demo.dto.query;

import lombok.Data;
import nohi.boot.demo.entity.TeamSign;

/**
 * <h3>service</h3>
 *
 * @author NOHI
 * @description <p>用户考勤信息</p>
 * @date 2023/12/28 16:06
 **/
@Data
public class TeamUserSignInfo extends TeamSign {
    private String name;
}
