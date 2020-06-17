package events;

import application.Lauren;
import enums.Game;
import enums.GameType;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import utils.helper.Utilities;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class RowSystemEvents extends ListenerAdapter {

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        Game game = null;
        GameType type = null;
        long id = event.getChannel().getIdLong();

        if (id == Lauren.config.ludoCasual) {
            game = Game.LUDO;
            type = GameType.CASUAL;
        }
        if (id == Lauren.config.ludoRanked) {
            game = Game.LUDO;
            type = GameType.RANKED;
        }
        if (id == Lauren.config.poolCasual) {
            game = Game.POOL;
            type = GameType.CASUAL;
        }
        if (id == Lauren.config.poolRanked) {
            game = Game.POOL;
            type = GameType.CASUAL;
        }

        if (game == null) return;

        event.getChannel().sendMessage("<a:sim:704295025374265387> " + Utilities.getFullName(event.getUser()) + ", você entrou na fila de partida")
                .queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        event.getReaction().removeReaction(event.getUser()).queue();
    }
}
