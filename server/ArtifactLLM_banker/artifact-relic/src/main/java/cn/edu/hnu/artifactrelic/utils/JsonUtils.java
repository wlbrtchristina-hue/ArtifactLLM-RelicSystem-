package cn.edu.hnu.artifactrelic.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JSON处理工具类
 */
@Component
public class JsonUtils {

    /**
     * 将对象转换为JSON字符串
     */
    public static String toJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return JSON.toJSONString(obj);
    }

    /**
     * 将JSON字符串解析为指定类型
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将JSON字符串解析为JSONArray
     */
    public static JSONArray parseArray(String json) {
        if (!StringUtils.hasText(json)) {
            return new JSONArray();
        }
        try {
            return JSON.parseArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}