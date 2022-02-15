package com.yuhtin.lauren.tasks;

import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.objects.EventWaiter;
import com.yuhtin.lauren.utils.TaskHelper;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class LootGeneratorTask {

    private final PlayerController playerController;
    private final JDA jda;
    private final EventWaiter eventWaiter;
    private final Logger logger;

    public void startRunnable() {
        logger.info("Registered LootGeneratorTask");

        val allowedChannels = Arrays.asList(
                700673056414367825L,
                704342124732350645L
        );

        val embed = new EmbedBuilder();
        embed.setAuthor("Loot Radiante", null, "https://cdn.discordapp.com/emojis/724759930653114399.png?v=1");

        val guild = jda.getGuilds().get(0);
        embed.setFooter("© ^Aincrad™ servidor de jogos", guild.getIconUrl());

        embed.setThumbnail("https://www.pcguia.pt/wp-content/uploads/2019/11/lootbox.jpg");
        embed.setColor(Color.ORANGE);

        val arrow = "   <:seta:771540348157689886>";
        embed.setDescription(
                "Você encontrou uma **lootbox**, seja o primeiro a reajir\n" +
                        "no ícone abaixo para garantir prêmios aleatórios\n" +
                        "\n" +
                        " \uD83C\uDFC6 Prêmios:\n" +
                        arrow + " Experiência **(Garantido)**\n" +
                        arrow + " Pontos de Patente\n" +
                        arrow + " Dinheiro\n" +
                        arrow + " Boost de XP\n" +
                        arrow + " Cargo Sortudo"
        );

        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                logger.info("Running LootGeneratorTask");

                if (new Random().nextInt(100) > 10) return;

                val value = new Random().nextInt(allowedChannels.size());
                val channelID = allowedChannels.get(value);
                val channel = guild.getTextChannelById(channelID);

                if (channel == null) {
                    logger.warning("Can't select a random channel to drop a loot");
                    return;
                }

                logger.info("Dropped loot on channel " + channel.getName());

                val message = channel.sendMessageEmbeds(embed.build()).complete();
                message.addReaction(":radiante:771541052590915585").queue();

                eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> !event.getMember().getUser().isBot()
                                && event.getMessageIdLong() == message.getIdLong(),
                        event -> {
                            message.clearReactions().queue();

                            var nickname = event.getMember().getNickname();
                            if (nickname == null) nickname = event.getMember().getEffectiveName();

                            message.delete().queue();

                            val privateChannel = event.getUser().openPrivateChannel().complete();
                            if (privateChannel != null) {
                                privateChannel.sendMessage("<:felizpakas:742373250037710918> " +
                                        "Parabéns **" + nickname + "**, você capturou uma lootbox, você pode abrir ela mais tarde " +
                                        "usando `/lootbox`")
                                        .queue();

                            }

                            val player = playerController.get(event.getUserIdLong());
                            player.setLootBoxes(player.getLootBoxes() + 1);

                            logger.info("The player " + event.getUser().getAsTag() + " getted the drop");
                        }, 90, TimeUnit.SECONDS,

                        () -> {
                            message.delete().queue();
                            channel.sendMessage("<a:tchau:751941650728747140> " +
                                    "Infelizmente acabou o tempo e ninguém coletou o loot.")
                                    .queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                        });
            }
        }, 25, 60, TimeUnit.MINUTES);
    }
}
