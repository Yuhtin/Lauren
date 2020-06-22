package com.yuhtin.lauren.events;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.match.Game;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.models.enums.GameType;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.models.enums.GameMode;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class RowSystemEvents extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        GameType type = null;
        GameMode mode = null;
        long id = event.getChannel().getIdLong();

        if (id == Lauren.config.ludoCasual) {
            type = GameType.LUDO;
            mode = GameMode.CASUAL;
        }
        if (id == Lauren.config.ludoRanked) {
            type = GameType.LUDO;
            mode = GameMode.RANKED;
        }
        if (id == Lauren.config.poolCasual) {
            type = GameType.POOL;
            mode = GameMode.CASUAL;
        }
        if (id == Lauren.config.poolRanked) {
            type = GameType.POOL;
            mode = GameMode.RANKED;
        }

        if (type == null) return;

        MatchController.getByType(type, mode).add(event.getUser().getIdLong());
        Logger.log("The player " + Utilities.getFullName(event.getUser()) + " entred in the row " + type + " " + mode).save();

        event.getChannel().sendMessage("<a:sim:704295025374265387> " + Utilities.getFullName(event.getUser()) + ", você entrou na fila de partida")
                .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        event.getReaction().removeReaction(event.getUser()).queue();
    }
}
