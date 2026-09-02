
package cn.edu.hnu.artifactcommon.pojo.vo;

import lombok.Data;

/**
 * 登录响应视图对象
 */
@Data
public class LoginVO {
    /**
     * JWT令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private UserVO user;
}
