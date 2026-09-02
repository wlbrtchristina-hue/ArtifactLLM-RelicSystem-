package cn.edu.hnu.artifactsystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 * 用于标记需要特定权限才能访问的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * 需要的权限代码
     * @return 权限代码数组
     */
    String[] value() default {};

    /**
     * 权限验证逻辑：AND或OR
     * AND表示需要拥有所有权限，OR表示拥有任一权限即可
     * @return 逻辑类型
     */
    Logical logical() default Logical.AND;

    /**
     * 权限验证逻辑枚举
     */
    enum Logical {
        AND, OR
    }
}
