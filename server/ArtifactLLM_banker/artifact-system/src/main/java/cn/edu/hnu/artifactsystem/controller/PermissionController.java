
package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactsystem.entity.Permission;
import cn.edu.hnu.artifactsystem.service.IPermissionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Resource
    private IPermissionService permissionService;

    /**
     * 分页查询权限列表
     */
    @GetMapping
    public Page<Permission> getPermissionPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        return permissionService.getPermissionPage(current, size, name, type, status);
    }

    /**
     * 根据ID获取权限
     */
    @GetMapping("/{id}")
    public Permission getPermissionById(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    /**
     * 获取所有权限
     */
    @GetMapping("/all")
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    /**
     * 获取树形结构权限列表
     */
    @GetMapping("/tree")
    public List<Permission> getPermissionTree() {
        return permissionService.getPermissionTree();
    }

    /**
     * 添加权限
     */
    @PostMapping
    public Permission addPermission(@RequestBody Permission permission) {
        return permissionService.addPermission(permission);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    public boolean updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return permissionService.updatePermission(permission);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public boolean deletePermission(@PathVariable Long id) {
        return permissionService.deletePermission(id);
    }

    /**
     * 批量删除权限
     */
    @DeleteMapping("/batch")
    public boolean batchDeletePermissions(@RequestBody List<Long> ids) {
        return permissionService.batchDeletePermissions(ids);
    }

    /**
     * 根据用户ID获取权限列表
     */
    @GetMapping("/user/{userId}")
    public List<Permission> getPermissionsByUserId(@PathVariable Long userId) {
        return permissionService.getPermissionsByUserId(userId);
    }

    /**
     * 根据角色ID获取权限列表
     */
    @GetMapping("/role/{roleId}")
    public List<Permission> getPermissionsByRoleId(@PathVariable Long roleId) {
        return permissionService.getPermissionsByRoleId(roleId);
    }
}
