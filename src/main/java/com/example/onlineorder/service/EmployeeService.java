package com.example.onlineorder.service;

import com.example.onlineorder.pojo.DTO.admin.RegisterDTO;
import com.example.onlineorder.pojo.DTO.admin.UpdateEmployeeDTO;
import com.example.onlineorder.pojo.DTO.auth.LoginDTO;
import com.example.onlineorder.pojo.VO.auth.LoginVO;
import com.example.onlineorder.pojo.entity.PickupWindow;

import java.util.List;

/**
 * 员工 Service 接口。
 */
public interface EmployeeService {

    /**
     * 员工登录：校验用户名密码，成功后生成 token。
     *
     * @param dto 登录请求体（用户名 + 密码）
     * @return 登录结果（token + 员工信息）
     */
    LoginVO login(LoginDTO dto);

    /**
     * 新增员工（也可用于初始化管理员：role 传 2）。
     *
     * @param dto 新增员工的请求体
     */
    void register(RegisterDTO dto);

    /**
     * 修改员工信息（只更新请求体里传了值的字段）。
     *
     * @param employeeId 员工编号
     * @param dto        要修改的内容
     */
    void updateEmployee(Long employeeId, UpdateEmployeeDTO dto);

    /**
     * 重置员工密码（管理员操作）。
     *
     * @param employeeId  员工编号
     * @param newPassword 新密码明文，后端 BCrypt 加密后入库
     */
    void resetPassword(Long employeeId, String newPassword);

    /**
     * 查询某员工负责的所有取餐窗口。
     *
     * @param empId 员工编号
     * @return 该员工负责的窗口列表（一名员工可能负责多个窗口，故返回 List）
     */
    List<PickupWindow> getPickupWindow(Long empId);
}