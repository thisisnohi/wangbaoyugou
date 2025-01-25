package nohi.boot.kqjs.service.qun100.db;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nohi.boot.kqjs.entity.PubPara;
import nohi.boot.kqjs.mapper.ParaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公用参数 服务实现类
 * </p>
 *
 * @author NOHI
 * @since 2025-01-24
 */
@Service
public class PubParaServiceImpl extends ServiceImpl<ParaMapper, PubPara> {
    @Autowired
    ParaMapper mapper;

    /**
     * 根据参数名，获取一条参数记录
     *
     * @param paraName 参数名
     * @return 参数永
     */
    public PubPara selectOneByParaName(String paraName) {
        return mapper.selectOneByParaName(paraName);
    }

    public int insertSelective(PubPara para) {
        return mapper.insertSelective(para);
    }

    public int updateSelectiveById(PubPara para) {
        return mapper.updateSelectiveById(para);
    }
}
