package nohi.boot.kqjs.service.qun100.db;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.kqjs.entity.KqMembers;
import nohi.boot.kqjs.mapper.KqMembersMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 打卡人员 服务实现类
 * </p>
 *
 * @author NOHI
 * @since 2025-01-24
 */
@Service
@Slf4j
public class KqMembersServiceImpl extends ServiceImpl<KqMembersMapper, KqMembers> {

    public KqMembers syncMem(KqMembers mem) {
        // 判断用户id是否存在
        KqMembers old = this.getById(mem.getFuid());
        if (null != old) {
            this.baseMapper.updateById(mem);
        } else {
            this.baseMapper.insertSelective(mem);
        }
        return mem;
    }
}
