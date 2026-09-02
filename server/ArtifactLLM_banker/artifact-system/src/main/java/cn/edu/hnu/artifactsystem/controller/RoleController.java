
package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactsystem.entity.Role;
import cn.edu.hnu.artifactsystem.service.IRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Resource
    private IRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping
    public Page<Role> getRolePage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        return roleService.getRolePage(current, size, name, status);
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/all")
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * 添加角色
     */
    @PostMapping
    public Role addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public boolean updateRole(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return roleService.updateRole(role);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public boolean deleteRole(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/batch")
    public boolean batchDeleteRoles(@RequestBody List<Long> ids) {
        return roleService.batchDeleteRoles(ids);
    }

    /**
     * 根据用户ID获取角色列表
     */
    @GetMapping("/user/{userId}")
    public List<Role> getRolesByUserId(@PathVariable Long userId) {
        return roleService.getRolesByUserId(userId);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/user/{userId}")
    public boolean assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return roleService.assignRolesToUser(userId, roleIds);
    }

    /**
     * 根据角色ID获取权限ID列表
     */
    @GetMapping("/{roleId}/permissions")
    public List<Long> getPermissionIdsByRoleId(@PathVariable Long roleId) {
        return roleService.getPermissionIdsByRoleId(roleId);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/{roleId}/permissions")
    public boolean assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return roleService.assignPermissionsToRole(roleId, permissionIds);
    }
}
