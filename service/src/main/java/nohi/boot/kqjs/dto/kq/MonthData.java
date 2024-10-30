package nohi.boot.kqjs.dto.kq;


import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p></p>
 * @date 2024/07/17 16:28
 **/
@Data
public class MonthData {
    /**
     * 列属性
     */
    List<?> columnList;
    /**
     * 数据属性
     */
    List<?> dataList;

    Map<String, Map<String, Map<String, Object>>>  dataMap;
}
