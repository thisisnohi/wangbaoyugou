package nohi.boot.kqjs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 季度结算
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
@Getter
@Setter
@TableName("CCB_QUARTER_JS")
public class QuarterJs implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdTs;

    /**
     * 项目
     */
    private String project;

    /**
     * 工作日
     */
    private LocalDate workDate;

    /**
     * 姓名
     */
    private String username;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 打卡小时数
     */
    private BigDecimal hours;

    /**
     * 打卡分钟数
     */
    private BigDecimal mins;

    /**
     * 扣减分钟数
     */
    private BigDecimal deduction;

    /**
     * 结算小时数
     */
    private BigDecimal hoursJs;

    /**
     * 结算分钟数
     */
    private BigDecimal minsJs;

    /**
     * 结算人天
     */
    private BigDecimal daysJs;

    /**
     * 考勤状态: N-不算
     */
    private String kqStatus;

    /**
     * 考勤备注
     */
    private String kqMsg;

    /**
     * 备用字段
     */
    private String remark;

    /**
     * 备用字段1
     */
    private String remark1;

    /**
     * 备用字段2
     */
    private String remark2;

    @TableField(exist = false)
    private String workDayStart;
    @TableField(exist = false)
    private String workDayEnd;
}
