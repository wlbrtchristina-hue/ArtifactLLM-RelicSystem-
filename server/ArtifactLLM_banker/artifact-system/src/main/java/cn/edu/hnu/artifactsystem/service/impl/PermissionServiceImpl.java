
package cn.edu.hnu.artifactsystem.service.impl;

import cn.edu.hnu.artifactsystem.entity.Permission;
import cn.edu.hnu.artifactsystem.entity.RolePermission;
import cn.edu.hnu.artifactsystem.entity.UserRole;
import cn.edu.hnu.artifactsystem.mapper.PermissionMapper;
import cn.edu.hnu.artifactsystem.mapper.RolePermissionMapper;
import cn.edu.hnu.artifactsystem.mapper.UserRoleMapper;
import cn.edu.hnu.artifactsystem.service.IPermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public Page<Permission> getPermissionPage(int current, int size, String name, Integer type, Integer status) {
        Page<Permission> page = new Page<>(current, size);
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }

        if (type != null) {
            queryWrapper.eq("type", type);
        }

        if (status != null) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByAsc("sort_order");
        return permissionMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Permission getPermissionById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public Permission addPermission(Permission permission) {
        permission.setCreateTime(LocalDateTime.now());
        permissionMapper.insert(permission);
        return permission;
    }

    @Override
    public boolean updatePermission(Permission permission) {
        permission.setUpdateTime(LocalDateTime.now());
        return permissionMapper.updateById(permission) > 0;
    }

    @Override
    public boolean deletePermission(Long id) {
        // 删除权限前，先检查是否有角色使用该权限
        List<Long> roleIds = rolePermissionMapper.selectRoleIdsByPermissionId(id);
        if (roleIds != null && !roleIds.isEmpty()) {
            throw new RuntimeException("该权限已被角色使用，无法删除");
        }

        // 删除权限
        return permissionMapper.deleteById(id) > 0;
    }

    @Override
    public boolean batchDeletePermissions(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        for (Long id : ids) {
            deletePermission(id);
        }
        return true;
    }

    @Override
    public List<Permission> getAllPermissions() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0); // 只查询正常状态的权限
        queryWrapper.orderByAsc("sort_order");
        return permissionMapper.selectList(queryWrapper);
    }

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = getAllPermissions();

        // 构建父子关系
        Map<Long, Permission> permissionMap = new HashMap<>();
        for (Permission permission : allPermissions) {
            permissionMap.put(permission.getId(), permission);
        }

        List<Permission> rootPermissions = new ArrayList<>();
        for (Permission permission : allPermissions) {
            if (permission.getParentId() == null || permission.getParentId() == 0) {
                rootPermissions.add(permission);
            } else {
                Permission parent = permissionMap.get(permission.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }

        return rootPermissions;
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        // 先获取用户的所有角色ID
        List<Long> roleIds = userRoleMapper.selectRoleIdsByUserId(userId);

        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 再获取这些角色的所有权限ID
        List<Long> permissionIds = new ArrayList<>();
        for (Long roleId : roleIds) {
            List<Long> ids = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);
            if (ids != null) {
                permissionIds.addAll(ids);
            }
        }

        // 去重
        permissionIds = permissionIds.stream().distinct().collect(Collectors.toList());

        // 最后根据权限ID获取权限信息
        if (permissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        return permissionMapper.selectBatchIds(permissionIds);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        // 先获取角色的所有权限ID
        List<Long> permissionIds = rolePermissionMapper.selectPermissionIdsByRoleId(roleId);

        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据权限ID获取权限信息
        return permissionMapper.selectBatchIds(permissionIds);
    }
}
