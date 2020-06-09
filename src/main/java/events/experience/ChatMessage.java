package events.experience;

import dao.PlayerData;
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
        if (event.getAuthor().isBot()) return;

        PlayerData.get(event.getMember().getIdLong()).gainXP(3);
    }
}
