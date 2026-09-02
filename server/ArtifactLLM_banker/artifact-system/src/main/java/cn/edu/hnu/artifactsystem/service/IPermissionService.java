
package cn.edu.hnu.artifactsystem.service;

import cn.edu.hnu.artifactsystem.entity.Permission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 权限服务接口
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * 分页查询权限列表
     */
    Page<Permission> getPermissionPage(int current, int size, String name, Integer type, Integer status);

    /**
     * 根据ID获取权限
     */
    Permission getPermissionById(Long id);

    /**
     * 添加权限
     */
    Permission addPermission(Permission permission);

    /**
     * 更新权限
     */
    boolean updatePermission(Permission permission);

    /**
     * 删除权限
     */
    boolean deletePermission(Long id);

    /**
     * 批量删除权限
     */
    boolean batchDeletePermissions(List<Long> ids);

    /**
     * 获取所有权限
     */
    List<Permission> getAllPermissions();

    /**
     * 获取树形结构权限列表
     */
    List<Permission> getPermissionTree();

    /**
     * 根据用户ID获取权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);

    /**
     * 根据角色ID获取权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);
}
