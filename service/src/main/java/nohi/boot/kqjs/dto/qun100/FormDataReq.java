package nohi.boot.kqjs.dto.qun100;


import lombok.Data;

import java.util.Set;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>表单数据查询请求</p>
 * @date 2025/01/24 10:20
 **/
@Data
public class FormDataReq {
    private String formId;
    private Set<String> cids;
    private Integer pageNo = 1;
    // 默认10, 取值范围1到500
    private Integer pageSize = 10;
    private String startTime;
    private String endTime;

}
