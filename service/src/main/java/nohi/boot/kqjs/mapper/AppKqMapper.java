package nohi.boot.kqjs.mapper;

import nohi.boot.common.dto.page.Page;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.entity.AppKq;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * APP打卡 Mapper 接口
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
public interface AppKqMapper extends BaseMapper<AppKq> {

    /**
     * 大楼门禁、移动APP考勤对比
     */
    List<Map<String,?>> monthDataCompare(KqQueryDto info);

    /**
     * 移动APP考勤信息
     */
    List<Map<String,?>> monthData(KqQueryDto info);

    /**
     * app考勤明细
     */
    List<Map<String,?>> monthDataDetail(KqQueryDto info);
}
