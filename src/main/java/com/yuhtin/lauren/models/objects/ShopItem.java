package com.yuhtin.lauren.models.objects;

import com.yuhtin.lauren.models.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ShopItem {

    private final ItemType type;
    private int price;

}
