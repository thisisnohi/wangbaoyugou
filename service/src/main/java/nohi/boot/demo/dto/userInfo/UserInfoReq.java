package nohi.boot.demo.dto.userInfo;

import lombok.Data;
import nohi.boot.demo.entity.TeamUser;

@Data
public class UserInfoReq {
    private TeamUser data;
}
