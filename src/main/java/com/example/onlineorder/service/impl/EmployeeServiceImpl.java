package com.example.onlineorder.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.onlineorder.common.exception.BusinessException;
import com.example.onlineorder.mapper.EmployeeMapper;
import com.example.onlineorder.mapper.PickupWindowMapper;
import com.example.onlineorder.pojo.DTO.admin.RegisterDTO;
import com.example.onlineorder.pojo.DTO.admin.UpdateEmployeeDTO;
import com.example.onlineorder.pojo.DTO.auth.LoginDTO;
import com.example.onlineorder.pojo.VO.auth.LoginVO;
import com.example.onlineorder.pojo.entity.Employee;
import com.example.onlineorder.pojo.entity.PickupWindow;
import com.example.onlineorder.service.EmployeeService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
     * 修改员工信息（20 号接口）。
     * <p>
     * 采用「部分更新」：请求体里为 null 的字段保持原值，只改传了值的字段。
     * 校验点：
     * 1) 员工必须存在；
     * 2) 角色只能是 1（员工）或 2（管理员），且系统只允许一个管理员账号；
     * 3) 性别只能是 0/1/2；
     * 4) 出生日期不能晚于今天；
     * 5) 证件号不能与其他人重复（表上有唯一索引，这里提前给出友好提示）。
     */
    @Override
    public void updateEmployee(Long employeeId, UpdateEmployeeDTO dto) {
        if (employeeId == null) {
            throw new BusinessException("员工编号不能为空");
        }
        if (dto == null) {
            throw new BusinessException("请求参数不能为空");
        }

        // 1. 先确认员工存在，避免 updateById 静默影响 0 行
        Employee emp = employeeMapper.selectById(employeeId);
        if (emp == null) {
            throw new BusinessException("未找到编号为 " + employeeId + " 的员工");
        }

        // 2. 姓名
        if (dto.getName() != null) {
            String name = dto.getName().trim();
            if (name.isEmpty()) {
                throw new BusinessException("姓名不能为空");
            }
            if (name.length() > 50) {
                throw new BusinessException("姓名长度不能超过 50 个字符");
            }
            emp.setName(name);
        }

        // 3. 角色：只允许 1/2，且不能出现第二个管理员
        if (dto.getRole() != null) {
            Integer role = dto.getRole();
            if (role != 1 && role != 2) {
                throw new BusinessException("角色只能是 1（员工）或 2（管理员）");
            }
            if (role == 2 && !Objects.equals(emp.getRole(), 2)) {
                Long adminCount = employeeMapper.selectCount(
                        new LambdaQueryWrapper<Employee>().eq(Employee::getRole, 2));
                if (adminCount != null && adminCount > 0) {
                    throw new BusinessException("系统已存在管理员账号，不能把该员工提升为管理员");
                }
            }
            emp.setRole(role);
        }

        // 4. 性别
        if (dto.getGender() != null) {
            Integer gender = dto.getGender();
            if (gender < 0 || gender > 2) {
                throw new BusinessException("性别只能是 0（未知）、1（男）、2（女）");
            }
            emp.setGender(gender);
        }

        // 5. 出生日期
        if (dto.getBirthDate() != null) {
            if (dto.getBirthDate().isAfter(LocalDate.now())) {
                throw new BusinessException("出生日期不能晚于今天");
            }
            emp.setBirthDate(dto.getBirthDate());
        }

        // 6. 证件号：唯一校验（只在真的改了的时候查一次）
        if (dto.getIdCardNum() != null) {
            String idCard = dto.getIdCardNum().trim();
            if (idCard.isEmpty()) {
                throw new BusinessException("证件号不能为空");
            }
            if (idCard.length() > 18) {
                throw new BusinessException("证件号长度不能超过 18 个字符");
            }
            if (!idCard.equals(emp.getIdCardNum())) {
                Long dup = employeeMapper.selectCount(
                        new LambdaQueryWrapper<Employee>().eq(Employee::getIdCardNum, idCard));
                if (dup != null && dup > 0) {
                    throw new BusinessException("该证件号已被其他员工使用");
                }
                emp.setIdCardNum(idCard);
            }
        }

        // 7. 更新：实体是从数据库查出来的，updateById 只会写回这张表
        employeeMapper.updateById(emp);
    }

    /**
     * 重置员工密码（21 号接口）。
     * <p>
     * 管理员给指定员工设置新密码：员工必须存在，新密码用 BCrypt 加密后写回 password_hash。
     * 明文密码不落库、不打日志。
     */
    @Override
    public void resetPassword(Long employeeId, String newPassword) {
        if (employeeId == null) {
            throw new BusinessException("员工编号不能为空");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new BusinessException("新密码不能为空");
        }

        Employee emp = employeeMapper.selectById(employeeId);
        if (emp == null) {
            throw new BusinessException("未找到编号为 " + employeeId + " 的员工");
        }

        emp.setPasswordHash(encoder.encode(newPassword));
        employeeMapper.updateById(emp);
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