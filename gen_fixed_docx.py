# -*- coding: utf-8 -*-
from docx import Document
from docx.shared import Pt, RGBColor
from docx.oxml.ns import qn

OUT = r"C:\Users\LIN\IdeaProjects\design\OnlineOrder\软件园食堂线上点单系统-修正版.docx"

doc = Document()

# ---------- 字体 ----------
def set_style_east_asia(style, name="微软雅黑"):
    rpr = style.element.get_or_add_rPr()
    rfonts = rpr.get_or_add_rFonts()
    rfonts.set(qn("w:eastAsia"), name)

def set_font(run, name="微软雅黑", size=None, bold=None, color=None):
    run.font.name = name
    rpr = run._element.get_or_add_rPr()
    rfonts = rpr.get_or_add_rFonts()
    rfonts.set(qn("w:eastAsia"), name)
    if size is not None:
        run.font.size = Pt(size)
    if bold is not None:
        run.font.bold = bold
    if color is not None:
        run.font.color.rgb = RGBColor(*color)

for sname in ["Normal", "Heading 1", "Heading 2", "Heading 3", "Title"]:
    try:
        set_style_east_asia(doc.styles[sname])
    except Exception:
        pass

doc.styles["Normal"].font.name = "微软雅黑"
doc.styles["Normal"].font.size = Pt(11)

# ---------- 辅助函数 ----------
def para(text, bold=False, size=None, color=None, style=None):
    p = doc.add_paragraph(style=style)
    r = p.add_run(text)
    set_font(r, size=size, bold=bold, color=color)
    return p

def bullet(text, bold=False):
    p = doc.add_paragraph(style="List Bullet")
    r = p.add_run(text)
    set_font(r, bold=bold)
    return p

def simple_table(headers, rows):
    table = doc.add_table(rows=1, cols=len(headers))
    table.style = "Table Grid"
    for i, h in enumerate(headers):
        r = table.rows[0].cells[i].paragraphs[0].add_run(h)
        set_font(r, bold=True)
    for row in rows:
        cells = table.add_row().cells
        for i, val in enumerate(row):
            r = cells[i].paragraphs[0].add_run(val)
            set_font(r)
    return table

def entity_table(title, rows):
    doc.add_heading(title, level=3)
    table = doc.add_table(rows=1, cols=4)
    table.style = "Table Grid"
    hdr = table.rows[0].cells
    for i, h in enumerate(["字段", "含义", "类型", "键 / 约束"]):
        r = hdr[i].paragraphs[0].add_run(h)
        set_font(r, bold=True)
    for field, meaning, dtype, key in rows:
        cells = table.add_row().cells
        r0 = cells[0].paragraphs[0].add_run(field)
        set_font(r0, bold=key.startswith("PK"))
        set_font(cells[1].paragraphs[0].add_run(meaning))
        set_font(cells[2].paragraphs[0].add_run(dtype))
        set_font(cells[3].paragraphs[0].add_run(key))
    return table

# ================= 标题 =================
doc.add_heading("基于SpringBoot的软件园食堂线上点单系统（修正版）", level=0)

para("本稿在原需求基础上修订，主要修正：补充账号/登录体系与支付、取消订单等缺失功能；"
     "实体由 6 张规范为 7 张；解决订单表与消费记录字段重复问题；补齐表关系与基数；"
     "并补全字段类型供审查。管理员与员工共用员工端登录入口，管理员为唯一特权账号；"
     "软件层定为 JDK 17 + Spring Boot 4.2。", color=(0x59, 0x59, 0x59))

# ================= 一、技术栈 =================
doc.add_heading("一、技术栈", level=1)
bullet("数据库：MySQL 9.7 LTS（社区版，8.4 为上一代 LTS，均可）")
bullet("软件层：Java JDK 17（与 Spring Boot 4.2 匹配）")
bullet("框架：Spring Boot 4.2")
bullet("建议补充：ORM 二选一（MyBatis / Spring Data JPA）；前端 微信小程序 或 Web")

# ================= 二、需求 =================
doc.add_heading("二、需求", level=1)

doc.add_heading("2.1 角色", level=2)
bullet("顾客、员工、管理员（管理员为员工端的特权账号/角色，与员工共用同一登录入口，不单独设端）")

doc.add_heading("2.2 核心功能", level=2)

doc.add_heading("顾客端", level=3)
bullet("微信 OpenID 授权登录")
bullet("查看菜品列表")
bullet("下单（选择桌号 / 打包，选择取餐窗口）")
bullet("支付（微信支付）")
bullet("查看是否出餐")
bullet("查看消费记录")
bullet("取消未出餐订单（可选）")

doc.add_heading("员工端（员工与管理员共用同一登录入口）", level=3)
bullet("账号登录（员工 / 管理员账号均在此登录）")
bullet("接收并服务订单")
bullet("订单完成后修改订单状态（备餐中 → 已出餐 → 已完成）")
bullet("检查消费记录")

para("管理员专属权限（管理员账号登录后额外可见，普通员工不可访问）：", bold=True)
bullet("管理员工信息与账户（新增 / 修改员工、重置账号等）")
bullet("管理菜品信息（含上下架、库存、价格）")
bullet("查看营业额、订单完成率、订单数量等统计信息")

doc.add_heading("2.3 业务规则", level=2)
bullet("一名顾客可以下多个订单（顾客 : 订单 = 1 : N）")
bullet("一名员工可以处理多个订单（员工经取餐窗口间接关联订单）")
bullet("每个取餐窗口由一名员工负责（窗口 : 员工 = N : 1）")
bullet("每个订单结账只产生一条消费/结算记录（订单 : 消费记录 = 1 : 1）")
bullet("订单与菜品为多对多，通过订单明细关联（订单 : 菜品 = N : M）")
bullet("下单时锁定库存，取消订单时回补库存（扣减时机需最终确认）")
bullet("系统仅一个管理员账号，通过员工端登录，拥有管理员工信息/账户与菜品信息的权限，普通员工无此权限")

# ================= 三、数据库表设计 =================
doc.add_heading("三、数据库表设计（含字段类型，7 张表）", level=1)

doc.add_heading("3.1 字段类型与枚举约定", level=2)
bullet("引擎 InnoDB；字符集 utf8mb4；排序规则 utf8mb4_0900_ai_ci", bold=True)
bullet("主键统一用 BIGINT UNSIGNED AUTO_INCREMENT（代理键）")
bullet("金额用 DECIMAL(10,2)；折扣率用 DECIMAL(4,3)；时间用 DATETIME；状态/枚举用 TINYINT（取值见下表）")
bullet("外键列均建立普通索引；查询高频列（订单号、菜品号等）建索引")

para("枚举取值映射：", bold=True)
simple_table(
    ["字段", "取值", "含义"],
    [
        ("employee.role", "1 / 2", "员工 / 管理员（管理员账号唯一）"),
        ("employee.gender", "0 / 1 / 2", "未知 / 男 / 女"),
        ("dish.status", "0 / 1", "下架 / 上架"),
        ("discount_rule.status", "0 / 1", "停用 / 启用"),
        ("orders.status", "0 / 1 / 2 / 3 / 4", "待接单 / 备餐中 / 已出餐 / 已完成 / 已取消"),
        ("orders.pay_method", "NULL / 1 / 2 / 3", "未支付 / 微信支付 / 支付宝 / 其他"),
    ],
)

para("折扣率说明：discount_rate 为实付比例，0.850 表示按原价 85% 结算（即打 8.5 折）；"
     "threshold 为满额阈值（订单原价总额达到该金额才可享受该折扣）。", color=(0x59, 0x59, 0x59))

doc.add_heading("3.2 表结构", level=2)

entity_table("1. 顾客 customer", [
    ("customer_id", "顾客编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("nickname", "称呼", "VARCHAR(50) NULL", ""),
    ("phone", "手机号", "VARCHAR(20) NOT NULL", "UNIQUE"),
    ("wechat_openid", "微信 OpenID", "VARCHAR(64) NOT NULL", "UNIQUE"),
    ("created_at", "注册时间", "DATETIME DEFAULT CURRENT_TIMESTAMP", ""),
])

entity_table("2. 员工 employee", [
    ("employee_id", "员工编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("username", "登录账号", "VARCHAR(50) NOT NULL", "UNIQUE"),
    ("password_hash", "密码哈希（BCrypt）", "VARCHAR(100) NOT NULL", ""),
    ("role", "角色（员工 / 管理员，管理员账号唯一）", "TINYINT NOT NULL DEFAULT 1", ""),
    ("name", "姓名", "VARCHAR(50) NOT NULL", ""),
    ("gender", "性别", "TINYINT DEFAULT 0", ""),
    ("birth_date", "出生日期", "DATE NULL", ""),
    ("id_card", "证件号", "VARCHAR(18) NOT NULL", "UNIQUE"),
])

entity_table("3. 取餐窗口 pickup_window", [
    ("window_id", "窗口编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("employee_id", "负责员工编号", "BIGINT UNSIGNED NOT NULL", "FK → employee"),
])

entity_table("4. 菜品 dish", [
    ("dish_id", "菜品编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("name", "名称", "VARCHAR(100) NOT NULL", ""),
    ("category", "类别", "VARCHAR(50) NOT NULL", ""),
    ("price", "价格", "DECIMAL(10,2) NOT NULL", ""),
    ("stock", "剩余库存数", "INT UNSIGNED NOT NULL DEFAULT 0", ""),
    ("status", "上架 / 下架", "TINYINT NOT NULL DEFAULT 1", ""),
])

entity_table("5. 折扣规则 discount_rule", [
    ("rule_id", "折扣规则编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("rule_name", "折扣名", "VARCHAR(50) NOT NULL", ""),
    ("threshold", "满额阈值", "DECIMAL(10,2) NOT NULL", ""),
    ("discount_rate", "折扣率（实付比例 0.850 = 8.5 折）", "DECIMAL(4,3) NOT NULL", ""),
    ("status", "启用 / 停用状态", "TINYINT NOT NULL DEFAULT 1", ""),
    ("created_at", "创建日期", "DATETIME DEFAULT CURRENT_TIMESTAMP", ""),
    ("valid_from", "有效期起", "DATETIME NULL", "可选"),
    ("valid_to", "有效期止", "DATETIME NULL", "可选"),
])

entity_table("6. 订单 orders", [
    ("order_id", "订单编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("customer_id", "下单顾客编号", "BIGINT UNSIGNED NOT NULL", "FK → customer, INDEX"),
    ("window_id", "取餐窗口编号", "BIGINT UNSIGNED NOT NULL", "FK → pickup_window, INDEX"),
    ("table_no", "下单桌号", "VARCHAR(20) NULL", "打包时为空"),
    ("status", "订单状态", "TINYINT NOT NULL DEFAULT 0", "0待接单/1备餐中/2已出餐/3已完成/4已取消"),
    ("created_at", "下单时间", "DATETIME DEFAULT CURRENT_TIMESTAMP", ""),
    ("total_amount", "原价总额（Σ 单价快照×数量）", "DECIMAL(10,2) NOT NULL DEFAULT 0", ""),
    ("payable_amount", "实付金额（折扣后）", "DECIMAL(10,2) NOT NULL DEFAULT 0", ""),
    ("discount_rule_id", "使用的折扣规则编号", "BIGINT UNSIGNED NULL", "FK → discount_rule, INDEX"),
    ("paid_at", "支付时间", "DATETIME NULL", "未支付为空"),
    ("pay_method", "支付方式", "TINYINT NULL", "NULL未支付/1微信/2支付宝/3其他"),
])

entity_table("7. 订单明细 order_item", [
    ("item_id", "明细编号", "BIGINT UNSIGNED AUTO_INCREMENT", "PK"),
    ("order_id", "订单编号", "BIGINT UNSIGNED NOT NULL", "FK → orders, INDEX"),
    ("dish_id", "菜品编号", "BIGINT UNSIGNED NOT NULL", "FK → dish, INDEX"),
    ("quantity", "数量", "INT UNSIGNED NOT NULL DEFAULT 1", ""),
    ("unit_price_snapshot", "单价快照", "DECIMAL(10,2) NOT NULL", "下单时锁价"),
])

doc.add_heading("3.3 关于消费记录的说明", level=2)
para("业务规则“每个订单结账只产生一条消费记录”为 1:1 关系，因此将结账字段"
     "（payable_amount、discount_rule_id、paid_at、pay_method）直接并入 orders，不单独建表（推荐）。")
para("若后续要做独立财务对账，可再拆出 payment_record 表，并从 orders 移除对应结账字段："
     "order_id（PK, FK → orders）· amount（DECIMAL(10,2)）· discount_rate_snapshot（DECIMAL(4,3)）· "
     "paid_at（DATETIME）· pay_method（TINYINT）。")

# ================= 四、表关系 =================
doc.add_heading("四、表关系（外键与基数）", level=1)
simple_table(
    ["关系", "基数", "外键约束"],
    [
        ("顾客 → 订单", "1 : N", "orders.customer_id → customer.customer_id"),
        ("员工 → 取餐窗口", "1 : N", "pickup_window.employee_id → employee.employee_id"),
        ("取餐窗口 → 订单", "1 : N", "orders.window_id → pickup_window.window_id"),
        ("订单 → 订单明细", "1 : N", "order_item.order_id → orders.order_id"),
        ("菜品 → 订单明细", "1 : N", "order_item.dish_id → dish.dish_id"),
        ("折扣规则 → 订单", "1 : N", "orders.discount_rule_id → discount_rule.rule_id"),
        ("订单 → 消费记录（可选）", "1 : 1", "payment_record.order_id → orders.order_id"),
    ],
)

# ================= 五、待确认与下一步 =================
doc.add_heading("五、待确认项与下一步", level=1)
para("以下口径确认后即可生成建表语句（DDL）：", bold=True)
bullet("确认枚举取值是否符合预期（订单状态、角色、性别、支付方式等，见 3.1 映射表）")
bullet("确认“一名员工能否负责多个窗口”：若一名员工只负责一个窗口，则 pickup_window.employee_id 加 UNIQUE")
bullet("确认 order_item 是否需要唯一约束 UNIQUE(order_id, dish_id)（禁止同订单重复菜品）")
bullet("确认库存扣减时机（下单锁定 vs 支付后扣）")
bullet("确认是否需要在订单表增加“取餐号/业务单号”展示字段（当前直接用 order_id）")
bullet("审查通过 → 生成完整建表语句（DDL，含注释与索引）")

doc.save(OUT)
print("saved:", OUT)
