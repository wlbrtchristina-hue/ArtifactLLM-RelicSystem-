package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基础控制器类
 * 提供统一的响应构建方法，所有Controller都应继承此类
 */
@RestController
public abstract class BaseController {

    /**
     * 构建成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result对象
     */
    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    /**
     * 构建成功响应（带消息）
     * @param message 响应消息
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result对象
     */
    protected <T> Result<T> success(String message, T data) {
        return Result.success(message, data);
    }

    /**
     * 构建成功响应（仅消息）
     * @param message 响应消息
     * @return Result对象
     */
    protected Result<Void> success(String message) {
        return Result.success(message);
    }

    /**
     * 构建失败响应
     * @param message 错误消息
     * @return Result对象
     */
    protected Result<Void> error(String message) {
        return Result.error(message);
    }

    /**
     * 构建失败响应（带错误码）
     * @param code 错误码
     * @param message 错误消息
     * @return Result对象
     */
    protected Result<Void> error(Integer code, String message) {
        return Result.error(code, message);
    }
}



