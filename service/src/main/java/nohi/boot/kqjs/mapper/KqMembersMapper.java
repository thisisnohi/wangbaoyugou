package nohi.boot.kqjs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.kqjs.entity.KqMembers;

public interface KqMembersMapper extends BaseMapper<KqMembers> {

    int insertSelective(KqMembers kqMembers);
}
