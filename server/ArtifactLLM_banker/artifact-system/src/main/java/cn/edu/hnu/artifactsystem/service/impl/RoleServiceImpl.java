
package cn.edu.hnu.artifactsystem.service.impl;

import cn.edu.hnu.artifactsystem.entity.Role;
import cn.edu.hnu.artifactsystem.entity.RolePermission;
import cn.edu.hnu.artifactsystem.entity.UserRole;
import cn.edu.hnu.artifactsystem.mapper.RoleMapper;
import cn.edu.hnu.artifactsystem.mapper.RolePermissionMapper;
import cn.edu.hnu.artifactsystem.mapper.UserRoleMapper;
import cn.edu.hnu.artifactsystem.service.IRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Page<Role> getRolePage(int current, int size, String name, Integer status) {
        Page<Role> page = new Page<>(current, size);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByAsc("sort_order");
        return roleMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public Role addRole(Role role) {
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);
        return role;
    }

    @Override
    public boolean updateRole(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        return roleMapper.updateById(role) > 0;
    }

    @Override
    public boolean deleteRole(Long id) {
        // 删除角色前，先删除角色与用户的关联关系
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.eq("role_id", id);
        userRoleMapper.delete(userRoleWrapper);

        // 删除角色前，先删除角色与权限的关联关系
        QueryWrapper<RolePermission> rolePermissionWrapper = new QueryWrapper<>();
        rolePermissionWrapper.eq("role_id", id);
        rolePermissionMapper.delete(rolePermissionWrapper);

        // 删除角色
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public boolean batchDeleteRoles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long id : ids) {
            deleteRole(id);
        }
        return true;
    }

    @Override
    public List<Role> getAllRoles() {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0); // 只查询正常状态的角色
        queryWrapper.orderByAsc("sort_order");
        return roleMapper.selectList(queryWrapper);
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        // 先查询用户拥有的角色ID
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);

        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 再根据角色ID查询角色信息
        return roleMapper.selectBatchIds(roleIds);
    }

    @Override
    @Transactional
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        // 先删除用户原有的角色关系
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        userRoleMapper.delete(wrapper);

        // 添加新的角色关系
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreateTime(LocalDateTime.now());
                userRoleMapper.insert(userRole);
            }
        }

        return true;
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 先删除角色原有的权限关系
        QueryWrapper<RolePermission> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", roleId);
        rolePermissionMapper.delete(wrapper);

        // 添加新的权限关系
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreateTime(LocalDateTime.now());
                rolePermissionMapper.insert(rolePermission);
            }
        }

        return true;
    }
}
