package nohi.boot.demo.service.db;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import nohi.boot.demo.dao.TeamSignMapper;
import nohi.boot.demo.dao.TeamUserMapper;
import nohi.boot.demo.dto.kaoqin.query.UserDutyTime;
import nohi.boot.demo.dto.query.TeamUserSignInfo;
import nohi.boot.demo.entity.TeamSign;
import nohi.boot.demo.entity.TeamUser;
import nohi.boot.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class TeamSignService extends ServiceImpl<TeamSignMapper, TeamSign> {

    @Autowired
    TeamSignMapper mapper;

    @Override
    public TeamSignMapper getBaseMapper() {
        return this.mapper;
    }

    /**
     * 保存用户
     * ID存在则更新，不存在则新增
     *
     * @param info 入参
     */
    public void saveOrUpdateInfo(TeamSign info) {
        /** 对象检查 **/
        if (null == info || StringUtils.isBlank(info.getUserId())) {
            log.warn("用户ID为空");
            throw new RuntimeException("用户ID为空");
        }

        /** 判断是否重复 **/
        QueryWrapper<TeamSign> queryWrapper = new QueryWrapper<TeamSign>();

        queryWrapper.apply("date_format(sign_time,'%Y-%m-%d %H:%i:%s')={0}", DateUtils.format(info.getSignTime(), DateUtils.HYPHEN_TIME));
        queryWrapper.eq("user_id", info.getUserId());
        TeamSign old = mapper.selectOne(queryWrapper);
        if (old != null) {
            log.info("用户[{}][{}]已存在，更新数据", old.getUserId(), old.getSignTime());
            info.setId(old.getId());
            mapper.updateById(info);
        } else {
            log.info("新增[{}][{}]记录", info.getUserId(), info.getSignTime());
            info.setId(UUID.randomUUID().toString());
            mapper.insert(info);
        }
    }

    /**
     * 获取用户考勤
     */
    public List<TeamUserSignInfo> selectUserKaoQin(String name, String startDate, String endDate) {
        return mapper.selectUserKaoQin(name, startDate, endDate);
    }

    /**
     * 根据日期获取所有用户考勤数据
     * @param date 日期
     */
    public List<UserDutyTime> selectUserDutyTime(String date){
        return mapper.selectUserDutyTime(date);
    }

}
