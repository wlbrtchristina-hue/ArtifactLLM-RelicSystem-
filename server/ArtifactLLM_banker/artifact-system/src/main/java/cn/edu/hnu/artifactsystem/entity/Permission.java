
package cn.edu.hnu.artifactsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 */
@Data
@TableName("sys_permission")
public class Permission {
    /**
     * 权限ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限代码
     */
    private String code;

    /**
     * 权限类型：1-菜单，2-按钮，3-接口
     */
    private Integer type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 权限图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 子权限列表（非数据库字段）
     */
    private List<Permission> children;
}
