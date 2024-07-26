package nohi.boot.common.dto.page;


import lombok.Data;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>分页查询</p>
 * @date 2024/07/24 16:00
 **/
@Data
public class PageQueryReq<T> {
    private Page page;
    private T data;
}
