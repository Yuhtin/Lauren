package com.yuhtin.lauren.models.embeds;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.ItemType;
import com.yuhtin.lauren.models.objects.ShopItem;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ShopEmbed {

    @Getter private static final ShopEmbed instance = new ShopEmbed();
    @Getter private final EmbedBuilder embed = new EmbedBuilder();
    @Getter private final Map<String, ShopItem> shopItems = new HashMap<>();

    public void build() {
        shopItems.put(":bronzekey:775100121322356766", new ShopItem(ItemType.KEY, 2000, 5, true));

        embed.setImage("https://www.flaticon.com/br/premium-icon/icons/svg/384/384993.svg");
        embed.setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()));
        embed.setColor(Color.ORANGE);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Itens a venda (")
                .append(shopItems.size())
                .append(")\n\n");

        for (Map.Entry<String, ShopItem> entry : shopItems.entrySet()) {

            String emoji = entry.getKey();
            ShopItem shopItem = entry.getValue();

            String items = shopItem.getQuantity() + " à venda";
            stringBuilder.append("<")
                    .append(emoji)
                    .append(">")

                    .append(" - ")
                    .append(shopItem.getType().getName())
                    .append(" (")
                    .append(shopItem.isSelling() ? items : "Expirado")
                    .append(")")
                    .append("\n");

        }

        embed.setDescription(stringBuilder.toString());
    }

}
