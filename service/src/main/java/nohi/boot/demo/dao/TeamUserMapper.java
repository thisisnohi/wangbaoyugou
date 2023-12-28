package nohi.boot.demo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import nohi.boot.demo.entity.TeamUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeamUserMapper extends BaseMapper<TeamUser> {


    List<TeamUser> findByName(String name);
}
