
package cn.edu.hnu.artifactcommon.constant;

/**
 * 系统通用常量
 */
public class SystemConstant {
    /**
     * 用户角色常量
     */
    public static final String ROLE_GUEST = "guest";
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADVANCED = "advanced";
    public static final String ROLE_ADMIN = "admin";

    /**
     * 用户状态常量
     */
    public static final Integer STATUS_NORMAL = 0; // 正常
    public static final Integer STATUS_DISABLED = 1; // 禁用

    /**
     * JWT相关常量
     */
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    public static final String JWT_SECRET = "artifact-system-secret-key-2025-12-16-strong-secret";
    public static final Long JWT_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 7天

    /**
     * Redis缓存相关常量
     */
    public static final String REDIS_KEY_PREFIX = "artifact:";
    public static final String REDIS_KEY_CAPTCHA = "captcha:";
    public static final String REDIS_KEY_USER = "user:";
    public static final String REDIS_KEY_TOKEN = "token:";

    private SystemConstant() {
        // 私有构造方法，防止实例化
    }
}
