package com.yuhtin.lauren.events;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.models.enums.GameType;
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
        event.getReaction().removeReaction(event.getUser()).queue();

        if (MatchController.putPlayerInRow(type, mode, event.getUserIdLong()))
            event.getChannel().sendMessage("<a:sim:704295025374265387> <@" + event.getUserIdLong() + ">, você entrou na fila de partida")
                    .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        else
            event.getChannel().sendMessage("<a:nao:704295026036834375> <@" + event.getUserIdLong() + ">, você já está numa fila, use `$sair` para sair dela")
                    .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
    }
}
