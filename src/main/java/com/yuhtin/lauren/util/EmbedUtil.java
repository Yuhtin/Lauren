package com.yuhtin.lauren.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmbedUtil {

    private static final Color DEFAULT_COLOR = new Color(57, 12, 147);

    public static MessageEmbed create(String description) {
        return of(description).build();
    }

    public static EmbedBuilder of(String description) {
        return new EmbedBuilder().setColor(getColor()).setDescription(description);
    }

    public static EmbedBuilder empty() {
        return new EmbedBuilder().setColor(getColor());
    }

    public static Color getColor() {
        return DEFAULT_COLOR;
    }

    public static EmbedBuilder createDefaultEmbed(String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(DEFAULT_COLOR);
        embedBuilder.setDescription(description);

        return embedBuilder;
    }
}
