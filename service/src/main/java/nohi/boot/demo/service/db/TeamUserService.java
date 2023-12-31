package nohi.boot.demo.service.db;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.dao.TeamUserMapper;
import nohi.boot.demo.entity.TeamUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <h3>SpringBootTest</h3>
 *
 * @author NOHI
 * @description <p>UserServiceImpl</p>
 * @date 2023/01/13 13:05
 **/
@Slf4j
@Service
public class TeamUserService extends ServiceImpl<TeamUserMapper, TeamUser>  {

    @Autowired
    TeamUserMapper mapper;
    @Override
    public TeamUserMapper getBaseMapper() {
        return this.mapper;
    }

    public TeamUser findTopUserByName(String name) {
        return mapper.findByName(name).stream().findFirst().get();
    }

    public List<TeamUser> findListByCond(TeamUser cond){return mapper.findListByCond(cond);}

    public void updateByExample(TeamUser teamUser){
         mapper.updateByExample(teamUser);
    }

}
