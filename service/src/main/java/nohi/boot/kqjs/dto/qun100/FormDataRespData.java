package nohi.boot.kqjs.dto.qun100;


import lombok.Data;

import java.util.List;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>表单数据查询请求</p>
 * @date 2025/01/24 10:20
 **/
@Data
public class FormDataRespData {
    private Integer total;
    private Boolean hasNameList;
    private List<FormDataRespDataListItem> list;
}
