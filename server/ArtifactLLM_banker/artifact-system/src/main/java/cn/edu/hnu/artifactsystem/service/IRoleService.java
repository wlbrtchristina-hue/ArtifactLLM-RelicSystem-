
package cn.edu.hnu.artifactsystem.service;

import cn.edu.hnu.artifactsystem.entity.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色服务接口
 */
public interface IRoleService extends IService<Role> {

    /**
     * 分页查询角色列表
     */
    Page<Role> getRolePage(int current, int size, String name, Integer status);

    /**
     * 根据ID获取角色
     */
    Role getRoleById(Long id);

    /**
     * 添加角色
     */
    Role addRole(Role role);

    /**
     * 更新角色
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 批量删除角色
     */
    boolean batchDeleteRoles(List<Long> ids);

    /**
     * 获取所有角色
     */
    List<Role> getAllRoles();

    /**
     * 根据用户ID获取角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 为用户分配角色
     */
    boolean assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 根据角色ID获取权限ID列表
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);

    /**
     * 为角色分配权限
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);
}
