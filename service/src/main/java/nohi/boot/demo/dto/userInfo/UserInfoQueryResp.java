package nohi.boot.demo.dto.userInfo;

import lombok.Data;
import nohi.boot.demo.dto.Page;
import nohi.boot.demo.entity.TeamUser;

import java.util.List;

@Data
public class UserInfoQueryResp {
    private List<TeamUser> data;
    private String resCode ;
    private Page page;
}
