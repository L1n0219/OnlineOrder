package com.example.onlineorder.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.onlineorder.mapper.EmployeeMapper;
import com.example.onlineorder.pojo.entity.Employee;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限数据源实现。
 * <p>
 * Sa-Token 在做角色/权限校验（{@code checkRole}、{@code checkPermission}）时，
 * 不会自己去查数据库，而是回调这个接口，问「当前登录用户有哪些角色、哪些权限」。
 * 所以只要拦截器里用了 {@code StpUtil.checkRole("admin")}，就必须要实现这个类，
 * 否则 Sa-Token 拿不到角色信息，永远判定为无权限。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 返回当前用户的权限码列表。
     * <p>
     * 本项目只用「角色」做控制（员工/管理员），没有细分到权限码，
     * 所以这里直接返回空列表。以后如果需要精细权限（比如「只能看不能改」），
     * 在这里返回对应权限码即可。
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回当前用户的角色名列表。
     * <p>
     * 登录时我们用 {@code StpUtil.login(employeeId)} 把员工编号当作 loginId，
     * 所以这里拿到 loginId 后，反查员工表，把 role 数字翻译成角色名：
     * role=2 → "admin"，role=1 → "staff"。
     * 拦截器里的 {@code checkRole("admin")} 就是拿这个列表去比对的。
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();

        // loginId 可能是 Long 也可能是 String（取决于 Sa-Token 内部存储），统一转成 Long
        Long employeeId = Long.valueOf(String.valueOf(loginId));
        Employee emp = employeeMapper.selectById(employeeId);

        // 查不到用户就返回空列表，checkRole 自然会判定为无权限
        if (emp == null) {
            return roles;
        }

        if (emp.getRole() != null && emp.getRole() == 2) {
            roles.add("admin");   // 管理员
        } else {
            roles.add("staff");   // 普通员工
        }
        return roles;
    }
}
