package nohi.boot.kqjs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.kqjs.entity.KqMembers;

/**
 * 人员
 * @author NOHI
 * @date 2025/1/25 22:11
 */
public interface KqMembersMapper extends BaseMapper<KqMembers> {

    int insertSelective(KqMembers kqMembers);
}
