
package cn.edu.hnu.artifactcommon.utils;

import cn.edu.hnu.artifactcommon.pojo.vo.UserVO;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息转换工具类
 * 提供用户对象转换的通用方法，避免代码重复
 */
public class UserConvertUtil {

    /**
     * 将UserVO转换为Map格式（兼容前端）
     * @param user 用户视图对象
     * @return 转换后的Map
     */
    public static Map<String, Object> convertUserVOToMap(UserVO user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.put("status", user.getStatus() != null ? user.getStatus() : 0);
        userInfo.put("created_at", user.getCreateTime());
        userInfo.put("last_login", user.getLastLoginTime());
        return userInfo;
    }
}
