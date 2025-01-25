package nohi.boot.kqjs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.kqjs.entity.PubPara;

/**
 * <p>
 * 公用参数 Mapper 接口
 * </p>
 *
 * @author NOHI
 * @since 2025-01-24
 */
public interface ParaMapper extends BaseMapper<PubPara> {
    PubPara selectOneByParaName(String paraName);
    int insertSelective(PubPara para);
    int updateSelectiveById(PubPara para);
}
