
package cn.edu.hnu.artifactcommon.context;

/**
 * 用户上下文
 */
public class UserContext {

    /**
     * 用户信息类
     */
    private static class UserInfo {
        private String username;
        private Long userId;

        public UserInfo(String username, Long userId) {
            this.username = username;
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public Long getUserId() {
            return userId;
        }
    }

    /**
     * 使用ThreadLocal存储当前线程的用户信息
     */
    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程的用户信息
     */
    public static void setCurrentUser(String username, Long userId) {
        USER_THREAD_LOCAL.set(new UserInfo(username, userId));
    }

    /**
     * 设置当前线程的用户名
     */
    public static void setCurrentUsername(String username) {
        UserInfo userInfo = USER_THREAD_LOCAL.get();
        if (userInfo != null) {
            USER_THREAD_LOCAL.set(new UserInfo(username, userInfo.getUserId()));
        } else {
            USER_THREAD_LOCAL.set(new UserInfo(username, null));
        }
    }

    /**
     * 设置当前线程的用户ID
     */
    public static void setCurrentUserId(Long userId) {
        UserInfo userInfo = USER_THREAD_LOCAL.get();
        if (userInfo != null) {
            USER_THREAD_LOCAL.set(new UserInfo(userInfo.getUsername(), userId));
        } else {
            USER_THREAD_LOCAL.set(new UserInfo(null, userId));
        }
    }

    /**
     * 获取当前线程的用户名
     */
    public static String getCurrentUsername() {
        UserInfo userInfo = USER_THREAD_LOCAL.get();
        return userInfo != null ? userInfo.getUsername() : null;
    }

    /**
     * 获取当前线程的用户ID
     */
    public static Long getCurrentUserId() {
        UserInfo userInfo = USER_THREAD_LOCAL.get();
        return userInfo != null ? userInfo.getUserId() : null;
    }

    /**
     * 清除当前线程的用户信息
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
