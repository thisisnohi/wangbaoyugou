package nohi.boot.kqjs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.kqjs.entity.QuarterJs;

import java.util.List;

/**
 * <p>
 * 季度结算 Mapper 接口
 * </p>
 *
 * @author NOHI
 * @since 2024-07-17
 */
public interface QuarterJsMapper extends BaseMapper<QuarterJs> {

    /**
     * 根据对象查询
     *
     * @param info 查询对象
     * @return 列表
     */
    List<QuarterJs> selectByExample(QuarterJs info);
}
