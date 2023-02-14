package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.convert.UserConvert.USER_CONVERT;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenx
 * @since 2020-11-09
 */
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    private final UserMapper userMapper;

    public UserEntity findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public UserDTO findById(String id) {
        UserEntity userEntity = getById(id);
        return USER_CONVERT.toDTO(userEntity);
    }
}
