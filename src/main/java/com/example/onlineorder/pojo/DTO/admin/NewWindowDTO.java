package com.example.onlineorder.pojo.DTO.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增取餐窗口请求体（DTO）。
 * <p>
 * 窗口编号（window_id）是自增主键，由数据库自动生成，所以这里不需要前端传；
 * 只需要指定该窗口由哪个员工负责。
 */
@Data
public class NewWindowDTO {

    /** 负责员工编号（employee 表的外键） */
    @NotNull(message = "负责员工编号不能为空")
    private Long empId;
}
