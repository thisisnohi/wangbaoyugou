package nohi.boot.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.demo.dto.query.TeamUserSignInfo;
import nohi.boot.demo.entity.TeamSign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamSignMapper extends BaseMapper<TeamSign> {

    /**
     * 查询用户下时间段内考勤数据
     *
     * @param name      用户名
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    List<TeamUserSignInfo> selectUserKaoQin(@Param("name") String name, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
