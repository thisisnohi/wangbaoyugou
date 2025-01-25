package nohi.boot.kqjs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Database Table Remarks:
 * 打卡人员
 * <p>
 * This class was generated by NOHI MyBatis Generator.
 * This class corresponds to the database table KqMembers
 */
@Setter
@Getter
@TableName("KQ_MEMBERS")
public class KqMembers {
    @TableId(type = IdType.AUTO)
    // 用户id
    private String fuid;

    /**
     * 记录创建时间
     * KQ_MEMBERS.CREATED_TS
     */
    private Date createdTs;
    // 序号
    private String sno;

    /**
     * 姓名
     * KQ_MEMBERS.USERNAME
     */
    private String username;

    /**
     * 妮称
     * KQ_MEMBERS.NICKNAME
     */
    private String nickname;

    /**
     * 头像
     * KQ_MEMBERS.ICON
     */
    private String icon;

    /**
     * 人员状态： ON-有效； OFF-失效
     * KQ_MEMBERS.USER_STATUS
     */
    private String userStatus;

    /**
     * 备用字段1
     * KQ_MEMBERS.REMARK1
     */
    private String remark1;

    /**
     * 备用字段2
     * KQ_MEMBERS.REMARK2
     */
    private String remark2;
}
