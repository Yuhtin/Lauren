package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.models.enums.SugestionStage;
import com.yuhtin.lauren.models.objects.EventWaiter;
import com.yuhtin.lauren.models.objects.Sugestion;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.util.EmbedUtil;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "suggest",
        type = CommandInfo.CommandType.UTILITY,
        description = "Sugerir alterações no servidor"
)
public class SugestionCommand implements Command {

    @Setter private static EventWaiter waiter;
    private final Map<Long, Sugestion> sugestionMap = new HashMap<>();

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val user = event.getUser();
        val userID = user.getIdLong();
        if (sugestionMap.containsKey(userID)) {
            hook.sendMessage("<a:tchau:751941650728747140> Você já está fazendo este formulário, preencha em sua DM").queue();
            return;
        }

        val privateChannel = user.openPrivateChannel().complete();
        if (privateChannel == null) {
            hook.sendMessage("<a:tchau:751941650728747140> Você precisa habilitar suas mensagens privadas apartir deste servidor").queue();
            return;
        }

        Runnable runnable = () -> {
            if (sugestionMap.containsKey(userID)) {
                val sugestion = sugestionMap.get(userID);

                sugestion.getMessage()
                        .editMessage("<a:confete:769423543044800512> Operação cancelada por estourar o tempo limite (`5 minutos`)")
                        .queue();

                sugestionMap.remove(userID);
            }
        };

        val builder = Sugestion.builder()
                .stage(SugestionStage.SETTING_EMOJI)
                .message(privateChannel.sendMessage("Loading").complete())
                .user(user).reason(null).corp(null)
                .build();

        builder.getMessage().addReaction("a:nao:704295026036834375").complete();
        hook.sendMessageEmbeds(EmbedUtil.create("<a:sim:704295025374265387> Continue a operação em sua DM")).queue();

        builder.setStage(SugestionStage.SUGESTION);
        sugestionMap.put(userID, builder);

        fillForm(builder, runnable);
        checkReactions(builder, runnable);
        builder.updateMessage();

    }

    private void fillForm(Sugestion sugestion, Runnable cancelRunnable) {
        waiter.waitForEvent(MessageReceivedEvent.class,
                privateMessage -> !privateMessage.getAuthor().isBot()
                        && sugestion.getMessage().getChannel().getIdLong() == privateMessage.getMessage().getChannel().getIdLong(),

                privateMessage -> {

                    SugestionStage stage = sugestion.getStage();
                    String message = privateMessage.getMessage().getContentRaw();
                    if (stage == SugestionStage.SUGESTION) {

                        sugestion.setCorp(message);
                        sugestion.setStage(SugestionStage.SUGESTION_REASON);

                        fillForm(sugestion, cancelRunnable);
                    }

                    if (stage == SugestionStage.SUGESTION_REASON) {

                        sugestion.setReason(message);
                        sugestion.setStage(SugestionStage.CONFIRM);

                        sugestion.getMessage().addReaction("a:sim:704295025374265387").queue();

                    }

                    sugestion.updateMessage();
                }, 5, TimeUnit.MINUTES, cancelRunnable);
    }

    private void checkReactions(Sugestion sugestion, Runnable cancelRunnable) {

        Lauren lauren = Startup.getLauren();
        waiter.waitForEvent(MessageReactionAddEvent.class,
                privateMessage -> privateMessage.getUserIdLong() != lauren.getBot().getSelfUser().getIdLong()
                        && sugestion.getMessage().getIdLong() == privateMessage.getReaction().getMessageIdLong()
                        && (privateMessage.getReactionEmote().getIdLong() == 704295025374265387L
                        || privateMessage.getReactionEmote().getIdLong() == 704295026036834375L),

                privateMessage -> {
                    if (privateMessage.getReactionEmote().getIdLong() == 704295026036834375L) {
                        sugestionMap.remove(privateMessage.getUserIdLong());

                        sugestion.getMessage().delete().queue();
                        privateMessage.getChannel().sendMessage("<a:tchau:751941650728747140> Operação cancelada com sucesso").queue();

                        return;
                    }

                    if (sugestion.getStage() != SugestionStage.CONFIRM) {

                        privateMessage.getChannel().sendMessage("<a:tchau:751941650728747140> Complete o formulário primeiro").queue();
                        checkReactions(sugestion, cancelRunnable);
                        return;
                    }

                    var channel = lauren.getGuild().getTextChannelsByName("sugestões", true).get(0);
                    if (UserUtil.isPrime(lauren.getGuild().getMemberById(privateMessage.getUserIdLong()))) {
                        channel = lauren.getGuild().getTextChannelsByName("sugestões-premium", true).get(0);
                    }

                    channel.sendMessageEmbeds(sugestion.createSugestionEmbed().build()).queue(message -> {
                        message.addReaction("a:sim:704295025374265387").queue();
                        message.addReaction("a:nao:704295026036834375").queue();
                    });

                    privateMessage.getChannel().sendMessage("<a:sim:704295025374265387> Sugestão enviada com sucesso").queue();
                    sugestionMap.remove(privateMessage.getUserIdLong());
                }, 5, TimeUnit.MINUTES, cancelRunnable);
    }


}
