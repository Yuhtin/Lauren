package com.yuhtin.lauren.module.impl.shop;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ShopItem {

    private final ItemType type;
    private int price;

}
