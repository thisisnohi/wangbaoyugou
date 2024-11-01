package nohi.boot.kqjs.dto.kq;


import lombok.Data;

/**
 * <h3>ccb-timesheet</h3>
 *
 * @author NOHI
 * @description <p></p>
 * @date 2024/07/17 16:28
 **/
@Data
public class KqQueryDto {
    private String username;
    private String project;
    private String startDate;
    private String endDate;
    /**
     * 状态： 0-正常 1-异常
     */
    private String status;

    /**
     * 查询结果列
     */
    private String rsColsSql;
    /**
     * 按项目统计？
     */
    private boolean byProject;
    /**
     * 是否检查数据
     */
    private boolean check;
}
