package nohi.boot.kqjs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 大楼考勤
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
@Getter
@Setter
@TableName("CCB_DOOR_KQ")
public class DoorKq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录创建时间
     */
    private LocalDateTime createdTs;

    /**
     * 工作日
     */
    private LocalDate workDate;

    /**
     * 姓名
     */
    private String username;

    /**
     * 打卡时间
     */
    private LocalDateTime cardTime;

    /**
     * 备用字段1
     */
    private String remark1;

    /**
     * 备用字段2
     */
    private String remark2;
}
