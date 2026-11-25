package com.example.onlineorder.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.EmployeeMapper;
import com.example.onlineorder.mapper.PickupWindowMapper;
import com.example.onlineorder.pojo.DTO.admin.RegisterDTO;
import com.example.onlineorder.pojo.DTO.auth.LoginDTO;
import com.example.onlineorder.pojo.VO.auth.LoginVO;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.EmployeeService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 员工 Service 实现。
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private PickupWindowMapper pickupWindowMapper;

    /** BCrypt 加密器：既用于登录比对，也用于新增员工时把明文密码加密成哈希 */
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 员工登录。
     * <p>
     * 流程：按用户名查员工 → BCrypt 比对密码 → Sa-Token 登录生成 token → 返回 token 和员工信息。
     * 注意：用户不存在和密码错误统一返回「用户名或密码错误」，不区分提示，
     * 避免让攻击者通过报错判断某个账号是否存在。
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 按用户名查员工（username 有唯一索引，最多返回一条）
        Employee emp = employeeMapper.selectOne(
                new LambdaQueryWrapper<Employee>().eq(Employee::getUsername, dto.getUsername()));

        // 2. 用户不存在 或 密码不匹配，统一抛业务异常
        if (emp == null || !encoder.matches(dto.getPassword(), emp.getPasswordHash())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 登录：把员工编号作为 Sa-Token 的登录凭证（loginId）
        StpUtil.login(emp.getEmployeeId());

        // 4. 组装返回结果，token 由前端后续放进 satoken 请求头
        return new LoginVO(
                StpUtil.getTokenValue(),
                emp.getEmployeeId(),
                emp.getUsername(),
                emp.getName(),
                emp.getRole());
    }

    /**
     * 新增员工（初始化管理员时 role 传 2）。
     * <p>
     * 流程：检查用户名是否重复 → 用 BCrypt 加密明文密码 → 组装实体入库。
     * 密码只存 BCrypt 哈希，绝不存明文。
     */
    @Override
    public void register(RegisterDTO dto) {
        // 1. 检查用户名是否已存在（提前给出友好提示，而不是等唯一索引抛异常）
        Long count = employeeMapper.selectCount(
                new LambdaQueryWrapper<Employee>().eq(Employee::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 2. 组装实体，密码用 BCrypt 加密后存哈希
        Employee emp = new Employee();
        emp.setUsername(dto.getUsername());
        emp.setPasswordHash(encoder.encode(dto.getPassword()));
        emp.setName(dto.getName());
        emp.setRole(dto.getRole() == null ? 1 : dto.getRole());
        emp.setGender(dto.getGender() == null ? 0 : dto.getGender());
        emp.setBirthDate(dto.getBirthDate());
        emp.setIdCardNum(dto.getIdCardNum());

        // 3. 入库
        employeeMapper.insert(emp);
    }

    /**
     * 查询某员工负责的所有取餐窗口。
     * <p>
     * pickup_window 表的 employee_id 未加唯一索引，一名员工可能负责多个窗口，
     * 所以这里用 selectList 按 employee_id 查出全部窗口。
     */
    @Override
    public List<PickupWindow> getPickupWindow(Long empId) {
        return pickupWindowMapper.selectList(new LambdaQueryWrapper<PickupWindow>()
                .eq(PickupWindow::getEmployeeId, empId));
    }
}
