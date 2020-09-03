package com.yuhtin.lauren.commands.messages;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.time.Instant;

@CommandHandler(
        name = "matchembed",
        type = CommandHandler.CommandType.CUSTOM_MESSAGES,
        description = "Criar um embed para entrar na fila de uma partida",
        alias = {})
public class MatchEmbedMessage extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.MANAGE_CHANNEL, true)) return;

        String[] arguments = event.getArgs().split(" ");
        if (arguments.length < 2) {
            event.getChannel().sendMessage("♻️ Calma kkkk, vou tentar te ajudar ❤️. Use o comando `$matchembed <ludo ou pool> <casual ou ranked>`").queue();
            return;
        }

        String game = arguments[0], type = arguments[1];
        EmbedBuilder embed = new EmbedBuilder().setFooter("Serviço de partida da Lauren ❤️")
                .setColor(Color.ORANGE)
                .setTimestamp(Instant.now());

        if (game.equalsIgnoreCase("ludo")) {
            if (type.equalsIgnoreCase("casual")) {
                embed.setAuthor("\uD83C\uDFB2 LUDO | CASUAL")
                        .setDescription("Opa \uD83D\uDE06 Se você está aqui para entrar na fila de partida, primeiro cheque se está no local certo\n\n" +
                                "\uD83C\uDFC6 Para entrar na fila clique no \uD83C\uDFAE\n\n" +
                                "⚙️ Jogo: Ludo King\n" +
                                "⚔️ Modo de Jogo: Casual");
                Lauren.config.setLudoCasual(event.getChannel().getIdLong());
            } else {
                embed.setAuthor("\uD83C\uDFB2 LUDO | RANKED")
                        .setDescription("Opa \uD83D\uDE06 Se você está aqui para entrar na fila de partida, primeiro cheque se está no local certo\n\n" +
                                "\uD83C\uDFC6 Para entrar na fila clique no \uD83C\uDFAE\n\n" +
                                "⚙️ Jogo: Ludo King\n" +
                                "⚔️ Modo de Jogo: Ranked");
                Lauren.config.setLudoRanked(event.getChannel().getIdLong());
            }
        } else {
            if (type.equalsIgnoreCase("casual")) {
                embed.setAuthor("\uD83C\uDFB1 POOL | CASUAL")
                        .setDescription("Opa \uD83D\uDE06 Se você está aqui para entrar na fila de partida, primeiro cheque se está no local certo\n\n" +
                                "\uD83C\uDFC6 Para entrar na fila clique no \uD83C\uDFAE\n\n" +
                                "⚙️ Jogo: 8BallPool\n" +
                                "⚔️ Modo de Jogo: Casual");
                Lauren.config.setPoolCasual(event.getChannel().getIdLong());
            } else {
                embed.setAuthor("\uD83C\uDFB1 POOL | RANKED")
                        .setDescription("Opa \uD83D\uDE06 Se você está aqui para entrar na fila de partida, primeiro cheque se está no local certo\n\n" +
                                "\uD83C\uDFC6 Para entrar na fila clique no \uD83C\uDFAE\n\n" +
                                "⚙️ Jogo: 8BallPool\n" +
                                "⚔️ Modo de Jogo: Ranked");
                Lauren.config.setPoolRanked(event.getChannel().getIdLong());
            }
        }

        event.getChannel().sendMessage(embed.build()).queue(message -> message.addReaction("\uD83C\uDFAE").queue());
    }
}
