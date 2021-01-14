package com.yuhtin.lauren.events;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.vote.VoteResponse;
import com.yuhtin.lauren.utils.serialization.Serializer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class WebhookMessageEvent extends ListenerAdapter {

    @Inject private PlayerController playerController;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        if (event.getChannel().getIdLong() != 787402363999617086L) return;

        String content = event.getMessage().getContentRaw();

        VoteResponse voteResponse = Serializer.getVote().deserialize(content);
        if (voteResponse == null) return;

        Member member = event.getGuild().getMemberById(voteResponse.getUser());
        if (member == null) return;

        PrivateChannel channel = member.getUser().openPrivateChannel().complete();
        if (channel != null) {

            channel.sendMessage(
                    "✨ Você recebeu suas recompensas por votar, para mais informações, digite `$vote` em nosso servidor"
            ).queue();

        }

        Player player = this.playerController.get(voteResponse.getUser());
        player.executeVote();

    }
}
