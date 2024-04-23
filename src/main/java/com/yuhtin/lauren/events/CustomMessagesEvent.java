package com.yuhtin.lauren.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CustomMessagesEvent extends ListenerAdapter {

    final List<String> customMessages = Arrays.asList(
            "Oi amor tudo bom?",
            "Como vai seu dia?",
            "Quer uma ajuda pra levantar da cama?",
            "Vamos tomar café juntos?",
            "Estou cansada um pouco, vamos tomar um suco?"
    );

    final Map<Long, Long> delay = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.getChannelType().isGuild()
                || event.getMember() == null
                || event.getAuthor().isBot())
            return;
        
        if (event.getMessage().getMentions().getMembers().isEmpty()
                || !event.getMessage().getMentions().getMembers().contains(event.getGuild().getMember(event.getJDA().getSelfUser()))) return;

        if (delay.containsKey(event.getAuthor().getIdLong())
                && delay.get(event.getAuthor().getIdLong()) > System.currentTimeMillis()) {

            event.getChannel().sendMessage("Acho que já fiz uma pergunta recentemente a ti, volta daqui uns 2 minutinhos :D").queue();
            return;

        }

        String message = customMessages.get(new Random().nextInt(customMessages.size()));
        event.getChannel().sendMessage(message).queue();

        delay.put(event.getAuthor().getIdLong(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2));
    }
}
