
package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactsystem.entity.User;
import cn.edu.hnu.artifactsystem.service.IUserService;
import cn.edu.hnu.artifactsystem.service.IRoleService;
import cn.edu.hnu.artifactcommon.result.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Resource
    private IUserService userService;

    @Resource
    private IRoleService roleService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    public Result<Map<String, Object>> getUserPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username) {

        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }

        queryWrapper.orderByDesc("create_time");
        Page<User> userPage = userService.page(page, queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("items", userPage.getRecords());
        result.put("total", userPage.getTotal());

        return Result.success(result);
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return Result.success(userService.updateById(user));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<Boolean> batchDeleteUsers(@RequestBody List<Long> ids) {
        return Result.success(userService.removeByIds(ids));
    }

    /**
     * 切换用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> toggleUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status);
            return Result.success(userService.updateById(user));
        }
        return Result.success(false);
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/{id}/roles")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        // 获取用户的所有角色
        List<cn.edu.hnu.artifactsystem.entity.Role> roles = roleService.getRolesByUserId(id);
        // 提取角色ID列表
        return Result.success(roles.stream().map(cn.edu.hnu.artifactsystem.entity.Role::getId).toList());
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    public Result<Boolean> assignRolesToUser(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        return Result.success(roleService.assignRolesToUser(id, roleIds));
    }
}
