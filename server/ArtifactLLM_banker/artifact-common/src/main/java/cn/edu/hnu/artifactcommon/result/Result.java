
package cn.edu.hnu.artifactcommon.result;

import lombok.Data;

/**
 * 统一响应结果封装类
 */
@Data
public class Result<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        return result;
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> Result<T> success(String message) {
        Result<T> result = success();
        result.setMessage(message);
        return result;
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = success();
        result.setData(data);
        return result;
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = success(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage("操作失败");
        return result;
    }

    /**
     * 失败响应（带消息）
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = error();
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（带状态码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
