package nohi.boot.demo.dto.userInfo;

import lombok.Data;
import nohi.boot.demo.dto.Page;
import nohi.boot.demo.entity.TeamUser;

@Data
public class UesrInfoQueryReq {

    private Page page;

    private TeamUser searchParam;
}
