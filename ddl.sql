-- =====================================================================
-- 软件园食堂线上点单系统 · 数据库建表脚本
-- 目标库：MySQL 9.7 LTS（兼容 8.0/8.4）
-- 引擎：InnoDB   字符集：utf8mb4   排序规则：utf8mb4_0900_ai_ci
-- 说明：
--   1) 库存扣减时机：下单即扣减（事务内），取消订单回补；
--   2) 一名员工可负责多个窗口，故 pickup_window.employee_id 未加 UNIQUE；
--   3) order_item 加 UNIQUE(order_id, dish_id) 禁止同订单重复菜品；
--   4) 取餐/业务单号直接用 order_id，未单独建展示字段。
-- =====================================================================

SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 1. 顾客表
-- ---------------------------------------------------------------------
CREATE TABLE customer (
  customer_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '顾客编号',
  nickname      VARCHAR(50)     NULL                        COMMENT '称呼',
  phone         VARCHAR(20)     NOT NULL                    COMMENT '手机号',
  wechat_openid VARCHAR(64)     NOT NULL                    COMMENT '微信 OpenID',
  created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (customer_id),
  UNIQUE KEY uk_phone  (phone),
  UNIQUE KEY uk_openid (wechat_openid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='顾客';

-- ---------------------------------------------------------------------
-- 2. 员工表
-- ---------------------------------------------------------------------
CREATE TABLE employee (
  employee_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '员工编号',
  username      VARCHAR(50)     NOT NULL                    COMMENT '登录账号',
  password_hash VARCHAR(100)    NOT NULL                    COMMENT '密码哈希（BCrypt）',
  role          TINYINT         NOT NULL DEFAULT 1          COMMENT '角色：1员工 2管理员（管理员账号唯一）',
  name          VARCHAR(50)     NOT NULL                    COMMENT '姓名',
  gender        TINYINT         NOT NULL DEFAULT 0          COMMENT '性别：0未知 1男 2女',
  birth_date    DATE            NULL                        COMMENT '出生日期',
  id_card       VARCHAR(18)     NOT NULL                    COMMENT '证件号',
  PRIMARY KEY (employee_id),
  UNIQUE KEY uk_username (username),
  UNIQUE KEY uk_id_card  (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工';

-- ---------------------------------------------------------------------
-- 3. 取餐窗口表
-- ---------------------------------------------------------------------
CREATE TABLE pickup_window (
  window_id   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '窗口编号',
  employee_id BIGINT UNSIGNED NOT NULL                COMMENT '负责员工编号',
  PRIMARY KEY (window_id),
  KEY idx_employee (employee_id),
  CONSTRAINT fk_window_employee FOREIGN KEY (employee_id)
    REFERENCES employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='取餐窗口';

-- ---------------------------------------------------------------------
-- 4. 菜品表
-- ---------------------------------------------------------------------
CREATE TABLE dish (
  dish_id  BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜品编号',
  name     VARCHAR(100)    NOT NULL                COMMENT '名称',
  category VARCHAR(50)     NOT NULL                COMMENT '类别',
  price    DECIMAL(10,2)   NOT NULL                COMMENT '价格',
  stock    INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '剩余库存数',
  status   TINYINT         NOT NULL DEFAULT 1      COMMENT '状态：0下架 1上架',
  PRIMARY KEY (dish_id),
  KEY idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜品';

-- ---------------------------------------------------------------------
-- 5. 折扣规则表
-- ---------------------------------------------------------------------
CREATE TABLE discount_rule (
  rule_id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '折扣规则编号',
  rule_name     VARCHAR(50)     NOT NULL                COMMENT '折扣名',
  threshold     DECIMAL(10,2)   NOT NULL                COMMENT '满额阈值',
  discount_rate DECIMAL(4,3)    NOT NULL                COMMENT '折扣率（实付比例，0.850=8.5折）',
  status        TINYINT         NOT NULL DEFAULT 1      COMMENT '状态：0停用 1启用',
  created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  valid_from    DATETIME        NULL                    COMMENT '有效期起',
  valid_to      DATETIME        NULL                    COMMENT '有效期止',
  PRIMARY KEY (rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='折扣规则';

-- ---------------------------------------------------------------------
-- 6. 订单表
-- ---------------------------------------------------------------------
CREATE TABLE orders (
  order_id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  customer_id      BIGINT UNSIGNED NOT NULL                COMMENT '下单顾客编号',
  window_id        BIGINT UNSIGNED NOT NULL                COMMENT '取餐窗口编号',
  table_no         VARCHAR(20)     NULL                    COMMENT '下单桌号（打包为空）',
  status           TINYINT         NOT NULL DEFAULT 0      COMMENT '状态：0待接单 1备餐中 2已出餐 3已完成 4已取消',
  created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  total_amount     DECIMAL(10,2)   NOT NULL DEFAULT 0      COMMENT '原价总额',
  payable_amount   DECIMAL(10,2)   NOT NULL DEFAULT 0      COMMENT '实付金额（折扣后）',
  discount_rule_id BIGINT UNSIGNED NULL                    COMMENT '使用的折扣规则编号',
  paid_at          DATETIME        NULL                    COMMENT '支付时间',
  pay_method       TINYINT         NULL                    COMMENT '支付方式：NULL未支付 1微信 2支付宝 3其他',
  PRIMARY KEY (order_id),
  KEY idx_customer (customer_id),
  KEY idx_window   (window_id),
  KEY idx_discount (discount_rule_id),
  KEY idx_status   (status),
  CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id)
    REFERENCES customer (customer_id),
  CONSTRAINT fk_orders_window FOREIGN KEY (window_id)
    REFERENCES pickup_window (window_id),
  CONSTRAINT fk_orders_rule FOREIGN KEY (discount_rule_id)
    REFERENCES discount_rule (rule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单';

-- ---------------------------------------------------------------------
-- 7. 订单明细表
-- ---------------------------------------------------------------------
CREATE TABLE order_item (
  item_id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '明细编号',
  order_id            BIGINT UNSIGNED NOT NULL                COMMENT '订单编号',
  dish_id             BIGINT UNSIGNED NOT NULL                COMMENT '菜品编号',
  quantity            INT UNSIGNED    NOT NULL DEFAULT 1      COMMENT '数量',
  unit_price_snapshot DECIMAL(10,2)   NOT NULL                COMMENT '单价快照（下单时锁价）',
  PRIMARY KEY (item_id),
  UNIQUE KEY uk_order_dish (order_id, dish_id),
  KEY idx_dish (dish_id),
  CONSTRAINT fk_item_order FOREIGN KEY (order_id)
    REFERENCES orders (order_id),
  CONSTRAINT fk_item_dish FOREIGN KEY (dish_id)
    REFERENCES dish (dish_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细';
