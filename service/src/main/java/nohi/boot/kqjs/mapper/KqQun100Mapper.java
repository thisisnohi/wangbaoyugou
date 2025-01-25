package nohi.boot.kqjs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.kqjs.dto.kq.KqQueryDto;
import nohi.boot.kqjs.entity.KqQun100;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *  qun
 * @author NOHI
 * @date 2025/1/25 22:12
 */
public interface KqQun100Mapper extends BaseMapper<KqQun100> {

    /**
     * 根据考勤日期范围，清理数据
     */
    int deleteDataByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);

//    List<Map<String,?>> queryDetail(KqQueryDto info);
}
