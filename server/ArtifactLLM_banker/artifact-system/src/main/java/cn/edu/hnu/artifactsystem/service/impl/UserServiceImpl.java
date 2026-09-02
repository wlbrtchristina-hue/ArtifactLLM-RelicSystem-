
package cn.edu.hnu.artifactsystem.service.impl;

import cn.edu.hnu.artifactcommon.constant.SystemConstant;
import cn.edu.hnu.artifactcommon.pojo.dto.UserDTO;
import cn.edu.hnu.artifactcommon.pojo.vo.LoginVO;
import cn.edu.hnu.artifactcommon.pojo.vo.UserVO;
import cn.edu.hnu.artifactcommon.utils.CaptchaUtil;
import cn.edu.hnu.artifactcommon.utils.EncryptUtil;
import cn.edu.hnu.artifactcommon.utils.JwtUtil;
import cn.edu.hnu.artifactsystem.entity.User;
import cn.edu.hnu.artifactsystem.mapper.UserMapper;
import cn.edu.hnu.artifactsystem.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private CaptchaUtil captchaUtil;

    @Resource
    private cn.edu.hnu.artifactcommon.utils.RedisUtil redisUtil;

    @Override
    public UserVO register(UserDTO userDTO) {
        if (!captchaUtil.validateCaptcha(userDTO.getCaptchaUuid(), userDTO.getCaptcha())) {
            throw new RuntimeException("图形验证码错误");
        }

        String email = userDTO.getEmail();
        String emailCode = userDTO.getEmailCode();
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("邮箱不能为空");
        }
        if (emailCode == null || emailCode.isEmpty()) {
            throw new RuntimeException("邮箱验证码不能为空");
        }
        
        String key = "artifact:auth:code:" + email;
        Object cachedCode = redisUtil.get(key);
        if (cachedCode == null || !cachedCode.toString().equals(emailCode)) {
             throw new RuntimeException("邮箱验证码错误或已失效");
        }
        // 删除验证码（防止重复使用）
        redisUtil.del(key);

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        if (username == null || username.length() < 3 || username.length() > 20 || !username.matches("^[A-Za-z0-9_]+$")) {
            throw new RuntimeException("用户名格式不合法");
        }
        if (password == null || password.length() < 6) {
            throw new RuntimeException("密码长度至少6位");
        }

        if (checkUsernameExists(userDTO.getUsername())) {
            throw new cn.edu.hnu.artifactcommon.exception.BusinessException(500, "用户名已存在");
        }
        cn.edu.hnu.artifactsystem.entity.User emailExist = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<cn.edu.hnu.artifactsystem.entity.User>().eq("email", email));
        if (emailExist != null) {
            throw new cn.edu.hnu.artifactcommon.exception.BusinessException(500, "邮箱已被使用");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encryptPassword(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(SystemConstant.ROLE_USER); // 默认角色为普通用户
        user.setStatus(SystemConstant.STATUS_NORMAL); // 默认状态为正常
        user.setCreateTime(LocalDateTime.now());

        // 保存用户
        userMapper.insert(user);

        // 转换为VO并返回
        return convertToVO(user);
    }

    @Override
    public LoginVO login(UserDTO userDTO) {
        // 验证验证码
        if (!captchaUtil.validateCaptcha(userDTO.getCaptchaUuid(), userDTO.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }

        // 根据用户名查询用户
        User user = findByUsername(userDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String encryptedPassword = encryptPassword(userDTO.getPassword());
        if (!user.getPassword().equals(encryptedPassword)) {
            if (user.getPassword().equals(userDTO.getPassword())) {
                user.setPassword(encryptedPassword);
                userMapper.updateById(user);
            } else {
                throw new RuntimeException("密码错误");
            }
        }

        if (!SystemConstant.STATUS_NORMAL.equals(user.getStatus())) {
            if ("admin".equals(user.getUsername())) {
                user.setStatus(SystemConstant.STATUS_NORMAL);
            } else {
                throw new RuntimeException("账户已被禁用");
            }
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        // 构建登录响应
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUser(convertToVO(user));

        return loginVO;
    }

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        return user != null ? convertToVO(user) : null;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public UserVO getUserByUsername(String username) {
        User user = findByUsername(username);
        return user != null ? convertToVO(user) : null;
    }

    /**
     * 密码加密
     */
    private String encryptPassword(String password) {
        return EncryptUtil.md5(password);
    }

    /**
     * 实体转VO
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 不返回密码
        return userVO;
    }
}
