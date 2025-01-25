package nohi.boot.kqjs.service.qun100.db;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.entity.KqQun100;
import nohi.boot.kqjs.mapper.KqQun100Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 群报数打卡数据 服务实现类
 * </p>
 *
 * @author NOHI
 * @since 2025-01-24
 */
@Service
public class KqQun100ServiceImpl extends ServiceImpl<KqQun100Mapper, KqQun100> {

    public int deleteDataByDateRange(String startDate, String endDate) {
        return this.getBaseMapper().deleteDataByDateRange(startDate, endDate);
    }

    /**
     * 查询详细信息
     */
    public List<?> queryDetail(KqQueryDto info) {
        return this.getBaseMapper().queryDetail(info);
    }
}
