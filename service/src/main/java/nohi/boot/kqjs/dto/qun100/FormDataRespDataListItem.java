package nohi.boot.kqjs.dto.qun100;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>表单数据查询请求</p>
 * @date 2025/01/24 10:20
 **/
@Data
public class FormDataRespDataListItem {
    private String fid;
    private String fuid;
    private String icon;
    private String nickname;
    private List<FormDataRespCataLog> catalogs;
    private Integer status;
    private Integer signUpStatus;
    private Boolean patch;
    private Date createTime;
    private Date modifyTime;
    private String sno;
}
