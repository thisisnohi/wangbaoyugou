package nohi.boot.common.dto;


import lombok.Data;
import nohi.boot.common.dto.page.Page;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p>响应信息</p>
 * @date 2024/07/24 16:06
 **/
@Data
public class RespMeta<T> {
    /**
     * 响应状态：0-成功
     */
    private String resCode = "0";
    /**
     * 响应信息
     */
    private String resMsg;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 分页数据
     */
    private Page page;
}
