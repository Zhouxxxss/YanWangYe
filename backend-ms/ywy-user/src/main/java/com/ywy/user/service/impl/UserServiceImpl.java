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
 * 用户档案服务实现。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileMapper profileMapper;

    private UserProfile ensureProfile(Long userId) {
        UserProfile p = profileMapper.selectOne(
                Wrappers.<UserProfile>lambdaQuery().eq(UserProfile::getUserId, userId));
        if (p == null) {
            p = new UserProfile();
            p.setUserId(userId);
            p.setNickname("研友" + userId);
            profileMapper.insert(p);
        }
        return p;
    }

    @Override
    public ProfileDTO getProfile(Long userId) {
        return toDTO(ensureProfile(userId));
    }

    @Override
    @Transactional
    public ProfileDTO updateProfile(Long userId, ProfileUpdateRequest req) {
        UserProfile p = ensureProfile(userId);
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
        dto.setUserId(p.getUserId());
        return dto;
    }

    @Override
    public UserInfoDTO getUserById(Long id) {
        if (id == null) return null;
        UserProfile p = profileMapper.selectOne(
                Wrappers.<UserProfile>lambdaQuery().eq(UserProfile::getUserId, id));
        if (p == null) throw new BizException("用户不存在");
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(id);
        dto.setNickname(p.getNickname());
        dto.setAvatar(p.getAvatar());
        dto.setCreateTime(p.getCreateTime());
        return dto;
    }
}