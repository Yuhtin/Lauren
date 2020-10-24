package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.SugestionStage;
import com.yuhtin.lauren.models.objects.Sugestion;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "sugestao",
        type = CommandHandler.CommandType.UTILITY,
        description = "Sugerir alterações no servidor",
        alias = {"sugestão", "sugerir"}
)
public class SugestionCommand extends Command {

    @Setter
    private static EventWaiter waiter;
    private final Map<Long, Sugestion> sugestionMap = new HashMap<>();

    @Override
    protected void execute(CommandEvent event) {

        if (sugestionMap.containsKey(event.getAuthor().getIdLong())) {

            event.getChannel()
                    .sendMessage("<a:tchau:751941650728747140> Você já está fazendo este formulário, preencha em sua DM")
                    .queue();
            return;

        }

        PrivateChannel privateChannel = event.getAuthor().openPrivateChannel().complete();
        if (privateChannel == null) {

            event.getChannel()
                    .sendMessage("<a:tchau:751941650728747140> Você precisa habilitar suas mensagens privadas apartir deste servidor")
                    .queue();
            return;

        }

        Runnable runnable = () -> {
            if (sugestionMap.containsKey(event.getAuthor().getIdLong())) {

                Sugestion sugestion = sugestionMap.get(event.getAuthor().getIdLong());
                sugestion.getMessage()
                        .editMessage("<a:confete:769423543044800512> Operação cancelada por estourar o tempo limite (`5 minutos`)")
                        .queue();
                sugestionMap.remove(event.getAuthor().getIdLong());

            }
        };

        Sugestion builder = Sugestion.builder()
                .stage(SugestionStage.SETTING_EMOJI)
                .message(privateChannel.sendMessage("Loading").complete())
                .user(event.getAuthor())
                .reason(null)
                .sugestion(null)
                .build();

        builder.getMessage().addReaction("a:nao:704295026036834375").complete();
        event.getChannel().sendMessage("<a:sim:704295025374265387> Continue a operação em sua DM").complete();

        builder.setStage(SugestionStage.SUGESTION);
        updateMessage(builder);
        sugestionMap.put(event.getAuthor().getIdLong(), builder);

        fillForm(builder, runnable);
        checkReactions(builder, runnable);
    }

    private void fillForm(Sugestion sugestion, Runnable cancelRunnable) {
        waiter.waitForEvent(PrivateMessageReceivedEvent.class,
                (privateMessage) -> !privateMessage.getAuthor().isBot()
                        && sugestion.getMessage().getChannel().getIdLong() == privateMessage.getMessage().getChannel().getIdLong(),

                (privateMessage) -> {

                    SugestionStage stage = sugestion.getStage();
                    String message = privateMessage.getMessage().getContentRaw();
                    if (stage == SugestionStage.SUGESTION) {

                        sugestion.setSugestion(message);
                        sugestion.setStage(SugestionStage.SUGESTION_REASON);

                        fillForm(sugestion, cancelRunnable);
                    }

                    if (stage == SugestionStage.SUGESTION_REASON) {

                        sugestion.setReason(message);
                        sugestion.setStage(SugestionStage.CONFIRM);

                        sugestion.getMessage().addReaction("a:sim:704295025374265387").queue();

                    }

                    updateMessage(sugestion);
                }, 5, TimeUnit.MINUTES, cancelRunnable);
    }

    private void checkReactions(Sugestion sugestion, Runnable cancelRunnable) {
        waiter.waitForEvent(PrivateMessageReactionAddEvent.class,
                (privateMessage) -> privateMessage.getUserIdLong() != Lauren.bot.getSelfUser().getIdLong()
                        && sugestion.getMessage().getIdLong() == privateMessage.getReaction().getMessageIdLong()
                        && (privateMessage.getReactionEmote().getIdLong() == 704295025374265387L
                        || privateMessage.getReactionEmote().getIdLong() == 704295026036834375L),

                (privateMessage) -> {
                    Logger.log("pass");

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

                    TextChannel channel = Lauren.guild.getTextChannelsByName("sugestões", true).get(0);
                    if (Utilities.INSTANCE.isPrime(Lauren.guild.getMemberById(privateMessage.getUserIdLong()))) {

                        channel = Lauren.guild.getTextChannelsByName("sugestões-premium", true).get(0);

                    }

                    EmbedBuilder embed = new EmbedBuilder();

                    embed.setAuthor("| Sugestão de " + Utilities.INSTANCE.getFullName(sugestion.getUser()),
                            null, sugestion.getUser().getAvatarUrl());

                    embed.setFooter("© Todos os direitos reservados", Lauren.guild.getIconUrl());
                    embed.setColor(Color.GRAY);

                    embed.addField("<a:confete:769423543044800512> Sugestão para o servidor",
                            "`" + protectedString(sugestion.getSugestion()) + "`",
                            false);

                    embed.addField("<:procurando:769423542126247956> Motivo pela qual deve ser aceita",
                            "`" + protectedString(sugestion.getReason()) + "`",
                            false);

                    channel.sendMessage(embed.build()).queue(message -> {
                        message.addReaction("a:sim:704295025374265387").queue();
                        message.addReaction("a:nao:704295026036834375").queue();
                    });

                    privateMessage.getChannel().sendMessage("<a:sim:704295025374265387> Sugestão enviada com sucesso").queue();
                    sugestionMap.remove(privateMessage.getUserIdLong());
                }, 5, TimeUnit.MINUTES, cancelRunnable);
    }

    private void updateMessage(Sugestion sugestion) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("| Enviando uma sugestão", null, Lauren.guild.getIconUrl());
        embed.setFooter("© Todos os direitos reservados", Lauren.guild.getIconUrl());
        embed.setColor(Color.GREEN);

        embed.setDescription("<:errado:756770088639791234> O mal uso deste comando irá causar punição para o mesmo");

        embed.addField("<a:confete:769423543044800512> Qual sua sugestão para o servidor?",
                "`" + protectedString(sugestion.getSugestion()) + "`",
                false);

        embed.addField("<:procurando:769423542126247956> Motivo pela qual deve ser aceita?",
                "`" + protectedString(sugestion.getReason()) + "`",
                false);

        MessageBuilder builder = new MessageBuilder().setContent("Preencha na ordem abaixo").setEmbed(embed.build());

        sugestion.getMessage().editMessage(builder.build()).queue();
    }


    private String protectedString(String value) {
        return value == null ? "Não informado" : value;
    }
}
