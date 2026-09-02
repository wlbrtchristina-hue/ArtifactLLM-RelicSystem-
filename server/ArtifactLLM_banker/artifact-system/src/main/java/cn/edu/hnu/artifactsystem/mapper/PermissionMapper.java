
package cn.edu.hnu.artifactsystem.mapper;

import cn.edu.hnu.artifactsystem.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限映射器接口
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    // 继承BaseMapper后，基本的CRUD操作已由MyBatis-Plus提供
    // 如需自定义SQL，可在此添加方法并使用@Select、@Insert等注解
}
