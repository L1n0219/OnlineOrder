package com.example.onlineorder.pojo.VO.admin;

import lombok.Data;

/**
 * 热销菜品排行视图对象（VO）。
 * <p>
 * 作为「菜品销量排行」接口返回结果中的一个元素，记录某个菜品的名称和
 * 累计销量（下单份数），用于管理端查看热销菜品。
 */

@Data
public class TopDishVO {
    private String dishName;      // 菜品名称
    private Long totalQuantity;   // 总销量（份）
}
