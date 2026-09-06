package com.ywy.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywy.user.domain.po.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}