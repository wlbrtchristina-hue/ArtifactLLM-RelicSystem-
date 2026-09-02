
package cn.edu.hnu.artifactsystem.service;

import cn.edu.hnu.artifactsystem.entity.User;
import cn.edu.hnu.artifactcommon.pojo.dto.UserDTO;
import cn.edu.hnu.artifactcommon.pojo.vo.LoginVO;
import cn.edu.hnu.artifactcommon.pojo.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface IUserService extends IService<User> {

    /**
     * 用户注册
     */
    UserVO register(UserDTO userDTO);

    /**
     * 用户登录
     */
    LoginVO login(UserDTO userDTO);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 根据ID获取用户VO
     */
    UserVO getUserById(Long id);

    /**
     * 检查用户名是否存在
     */
    boolean checkUsernameExists(String username);

    /**
     * 根据用户名获取用户VO
     */
    UserVO getUserByUsername(String username);
}
