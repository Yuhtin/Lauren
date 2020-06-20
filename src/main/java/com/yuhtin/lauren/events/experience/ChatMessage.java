package com.yuhtin.lauren.events.experience;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.manager.PlayerDataManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatMessage extends ListenerAdapter {

    /*
        Earn 3 XP for every message sent
        But, if member has patent, these XP has multiplied
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild() || event.getMember() == null) return;
        if (Lauren.guild == null) Lauren.guild = event.getGuild();

        if (event.getAuthor().isBot() || event.getMessage().getContentRaw().startsWith(Lauren.config.prefix)) return;

        if (event.getMessage().getMentionedMembers().size() > 0) {
            User user = event.getMessage().getMentionedMembers().get(0).getUser();
            if (user.equals(event.getJDA().getSelfUser()))
                event.getChannel().sendMessage("Oi bb tudo bem? Se tiver alguma dúvida sobre mim, use `" + Lauren.config.prefix + "ajuda`").queue();
        }

        PlayerDataManager.get(event.getMember().getIdLong()).gainXP(3).updateLevel().save();
    }
}