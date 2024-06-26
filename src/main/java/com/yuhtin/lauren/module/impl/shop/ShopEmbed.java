package com.yuhtin.lauren.module.impl.shop;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ShopEmbed {

    private final EmbedBuilder embed = new EmbedBuilder();
    private final Map<String, ShopItem> shopItems = new HashMap<>();

    public void build() {
        shopItems.put("rename_command:775348818555699210", new ShopItem(ItemType.RENAME_COMMAND, 1000));
        shopItems.put("bronzekey:775100121322356766", new ShopItem(ItemType.KEY, 1350));
        shopItems.put("prime:722115525232296056", new ShopItem(ItemType.PRIME, 4000));

        embed.setImage("https://www.flaticon.com/br/premium-icon/icons/svg/384/384993.svg");
        embed.setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()));
        embed.setColor(Color.ORANGE);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<:beacon:771543538252120094> **")
                .append(shopItems.size())
                .append("** item(s) à venda \n\n");

        for (Map.Entry<String, ShopItem> entry : shopItems.entrySet()) {

            String emoji = entry.getKey();
            ShopItem shopItem = entry.getValue();

            stringBuilder.append("<:")
                    .append(emoji)
                    .append("> ")

                    .append(shopItem.getType().getName())

                    .append(" - ")

                    .append("**")
                    .append(shopItem.getPrice())
                    .append(" shards <:boost_emoji:772285522852839445>**")

                    .append("\n");

        }

        embed.setDescription(stringBuilder.toString());
    }

}
