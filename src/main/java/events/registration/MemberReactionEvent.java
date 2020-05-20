package events.registration;

import application.Lauren;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class MemberReactionEvent extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getMember() == null || event.getMember().getUser().isBot() || event.getMessageIdLong() != Lauren.config.resgistrationId)
            return;
        Role boy = event.getJDA().getRoleById("701293438821335040");
        Role girl = event.getJDA().getRoleById("701293834780540938");
        if (boy == null || girl == null) return;

        event.getGuild().addRoleToMember(event.getMember(), event.getReactionEmote().getId().equals("704293077623504957") ? girl : boy).queue();
        event.getChannel().sendMessage("<a:sim:704295025374265387> " + event.getMember().getEffectiveName() + " o seu cargo foi adicionado!").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getMember() == null || event.getMember().getUser().isBot() || event.getMessageIdLong() != Lauren.config.resgistrationId)
            return;
        Role boy = event.getJDA().getRoleById("701293438821335040");
        Role girl = event.getJDA().getRoleById("701293834780540938");
        if (boy == null || girl == null) return;

        event.getGuild().removeRoleFromMember(event.getMember(), event.getReactionEmote().getId().equals("704293077623504957") ? girl : boy).queue();
        event.getChannel().sendMessage("<a:nao:704295026036834375> " + event.getMember().getEffectiveName() + " o seu cargo foi removido!").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
    }
}
