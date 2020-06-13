package events.experience;

import application.Lauren;
import models.cache.PlayerDataCache;
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
        assert event.getMember() != null;
        if (event.getAuthor().isBot() || event.getMessage().getContentRaw().startsWith(Lauren.config.prefix)) return;

        if (event.getMessage().getMentionedMembers().size() > 0) {
            User user = event.getMessage().getMentionedMembers().get(0).getUser();
            if (user.equals(event.getJDA().getSelfUser()))
                event.getChannel().sendMessage("Oi bb tudo bem?").queue();
        }

        PlayerDataCache.get(event.getMember()).gainXP(3).updateLevel().save();
    }
}
