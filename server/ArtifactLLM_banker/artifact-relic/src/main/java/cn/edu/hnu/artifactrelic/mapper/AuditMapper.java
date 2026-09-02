package cn.edu.hnu.artifactrelic.mapper;

import cn.edu.hnu.artifactrelic.entity.Audit;
import cn.edu.hnu.artifactrelic.vo.AuditVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuditMapper extends BaseMapper<Audit> {
    /**
     * 根据审核状态查询审核列表
     */
    @Select("SELECT a.audit_id, a.audit_status, a.audit_type_id, a.audit_data, " +
            "       a.created_by, a.created_at, a.auditor_id, a.audit_time, a.reject_reason, " +
            "       at.audit_type_name, " +
            "       u1.username as created_by_name, " +
            "       u2.username as auditor_name " +
            "FROM audit a " +
            "LEFT JOIN audit_type at ON a.audit_type_id = at.audit_type_id " +
            "LEFT JOIN sys_user u1 ON a.created_by = u1.id " +
            "LEFT JOIN sys_user u2 ON a.auditor_id = u2.id " +
            "WHERE a.audit_status = #{status} " +
            "ORDER BY a.created_at DESC")
    List<AuditVO> selectByStatus(@Param("status") String status);

    /**
     * 根据审核ID查询审核详情（包含完整的audit_data）
     */
    @Select("SELECT a.audit_id, a.audit_status, a.audit_type_id, a.audit_data, " +
            "       a.created_by, a.created_at, a.auditor_id, a.audit_time, a.reject_reason, " +
            "       at.audit_type_name, " +
            "       u1.username as created_by_name, " +
            "       u2.username as auditor_name " +
            "FROM audit a " +
            "LEFT JOIN audit_type at ON a.audit_type_id = at.audit_type_id " +
            "LEFT JOIN sys_user u1 ON a.created_by = u1.id " +
            "LEFT JOIN sys_user u2 ON a.auditor_id = u2.id " +
            "WHERE a.audit_id = #{auditId}")
    AuditVO selectAuditDetailById(@Param("auditId") Long auditId);
}

