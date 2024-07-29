package nohi.boot.kqjs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.entity.QuarterJs;

import java.util.List;
import java.util.Map;

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

    /**
     * 移动APP考勤信息
     */
    List<Map<String,?>> monthData(KqQueryDto info);

    /**
     * app考勤明细
     */
    List<Map<String,?>> monthDataDetail(KqQueryDto info);


    /**
     * 判断人、工作日是否重复
     */
    List<Map<String,?>> userWorkRepeat(KqQueryDto info);

    /**
     * 结算考勤有，月份考勤无
     */
    List<Map<String,?>> jsWithOutApp(KqQueryDto info);

    /**
     * 月份考勤有，结算考勤无
     */
    List<Map<String,?>> appWithOutJs(KqQueryDto info);

    /**
     * app与结算考勤不一致
     */
    List<Map<String,?>> daysDiff(KqQueryDto info);


}
