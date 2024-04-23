package com.yuhtin.lauren.module.impl.shop;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.module.Module;
import lombok.Getter;

@Getter

public class ShopModule implements Module {

    private final ShopEmbed shopEmbed = new ShopEmbed();

    @Override
    public boolean setup(Lauren lauren) {
        shopEmbed.build();
        return true;
    }
}
