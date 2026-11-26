package com.example.onlineorder.pojo.DTO.admin;

import com.example.onlineorder.common.constant.DishStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishDTO {
    @NotBlank(message = "菜品名不能为空")
    private String name;

    @NotBlank(message = "分类不能为空")
    private String category;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private Integer stock = 0;

    private DishStatusEnum status = DishStatusEnum.ON_SALE;
}
