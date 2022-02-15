package com.yuhtin.lauren.events;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.Serializer;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Yuhtin
 * Github: https://github.com/Yuhtin
 */
public class WebhookMessageEvent extends ListenerAdapter {

    @Inject private PlayerController playerController;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() != 787402363999617086L) return;

        val content = event.getMessage().getContentRaw();

        val voteResponse = Serializer.getVote().deserialize(content);
        if (voteResponse == null) return;

        val member = event.getGuild().getMemberById(voteResponse.getUser());
        if (member == null) return;

        val channel = member.getUser().openPrivateChannel().complete();
        if (channel != null) {
            channel.sendMessage("✨ Você recebeu suas recompensas por votar, para mais informações, digite `/votar` em nosso servidor").queue();
        }

        val player = playerController.get(voteResponse.getUser());
        player.executeVote();
    }
}
