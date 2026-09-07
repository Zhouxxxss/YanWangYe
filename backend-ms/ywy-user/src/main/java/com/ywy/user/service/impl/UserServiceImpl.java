package com.ywy.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ywy.api.dto.user.UserInfoDTO;
import com.ywy.common.exceptions.BizException;
import com.ywy.user.domain.po.UserProfile;
import com.ywy.user.dto.ProfileDTO;
import com.ywy.user.dto.ProfileUpdateRequest;
import com.ywy.user.mapper.UserProfileMapper;
import com.ywy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户档案服务实现。单表 user 的档案列由本服务维护，主键即用户 id。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileMapper profileMapper;

    @Override
    public ProfileDTO getProfile(Long userId) {
        UserProfile p = profileMapper.selectById(userId);
        if (p == null) throw new BizException("用户不存在");
        return toDTO(p);
    }

    @Override
    @Transactional
    public ProfileDTO updateProfile(Long userId, ProfileUpdateRequest req) {
        UserProfile p = profileMapper.selectById(userId);
        if (p == null) throw new BizException("用户不存在");
        if (req.getNickname() != null) p.setNickname(req.getNickname());
        if (req.getAvatar() != null) p.setAvatar(req.getAvatar());
        if (req.getEmail() != null) p.setEmail(req.getEmail());
        if (req.getBio() != null) p.setBio(req.getBio());
        if (req.getStyleToken() != null) p.setStyleToken(req.getStyleToken());
        profileMapper.updateById(p);
        return toDTO(p);
    }

    private ProfileDTO toDTO(UserProfile p) {
        ProfileDTO dto = new ProfileDTO();
        BeanUtils.copyProperties(p, dto);
        dto.setUserId(p.getId());
        return dto;
    }

    @Override
    public UserInfoDTO getUserById(Long id) {
        if (id == null) return null;
        UserProfile p = profileMapper.selectById(id);
        if (p == null) throw new BizException("用户不存在");
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(id);
        dto.setNickname(p.getNickname());
        dto.setAvatar(p.getAvatar());
        dto.setCreateTime(p.getCreateTime());
        return dto;
    }
}