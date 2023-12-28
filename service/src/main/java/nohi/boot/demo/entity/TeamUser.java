package nohi.boot.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
/**
 * Database Table Remarks:
 *   团队用户
 *
 * This class was generated by NOHI MyBatis Generator.
 * This class corresponds to the database table TeamUser
 */
@Setter
@Getter
@TableName(value = "TEAM_USER")
public class TeamUser {
    /**
     *   id主键
     *   TEAM_USER.id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     *   姓名
     *   TEAM_USER.name
     */
    @TableField("name")
    private String name;

    /**
     *   手机号
     *   TEAM_USER.phone
     */
    private String phone;

    /**
     *   accesstoken
     *   TEAM_USER.accesstoken
     */
    private String accesstoken;

    /**
     *   cardid
     *   TEAM_USER.cardid
     */
    private String cardid;

    /**
     *   personId
     *   TEAM_USER.personId
     */
    private String personid;
}
