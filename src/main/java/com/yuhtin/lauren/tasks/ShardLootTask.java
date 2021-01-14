package com.yuhtin.lauren.tasks;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class ShardLootTask {

    private final PlayerController playerController;
    private final ShardManager shardManager;
    private final EventWaiter eventWaiter;
    private final Logger logger;

    public void startRunnable() {
        this.logger.info("Registered ShardLootTask");

        final List<Long> allowedChannels = Arrays.asList(
                704342124732350645L,
                700673056414367825L
        );

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Shard Loot", null, "https://cdn.discordapp.com/emojis/772285522852839445.png?v=1");

        Guild guild = shardManager.getShards().get(0).getGuilds().get(0);
        embed.setFooter("© ^Aincrad™ servidor de jogos", guild.getIconUrl());

        embed.setThumbnail("https://www.pcguia.pt/wp-content/uploads/2019/11/lootbox.jpg");
        embed.setColor(Color.MAGENTA);

        embed.setDescription("Você encontrou um **shardloot**, seja o primeiro a reajir\n" +
                "no ícone abaixo para garantir seus **<:boost_emoji:772285522852839445> shards**\n"
        );

        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                logger.info("Running ShardLootTask");

                if (new Random().nextInt(100) > 25) return;

                int value = new Random().nextInt(allowedChannels.size());
                long channelID = allowedChannels.get(value);

                TextChannel channel = guild.getTextChannelById(channelID);

                if (channel == null) {
                    logger.warning("Can't select a random channel to drop a loot");
                    return;
                }

                logger.info("Dropped shardloot on channel " + channel.getName());

                Message message = channel.sendMessage(embed.build()).complete();
                message.addReaction(":boost_emoji:772285522852839445").queue();

                eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> !event.getMember().getUser().isBot()
                                && event.getMessageIdLong() == message.getIdLong(),
                        event -> {
                            message.delete().queue();

                            Player player = playerController.get(event.getUserIdLong());
                            int shard = 30 + new Random().nextInt(50);

                            player.addMoney(shard);

                            PrivateChannel privateChannel = event.getUser().openPrivateChannel().complete();
                            if (event.getUser().hasPrivateChannel()) {

                                String nickname = event.getMember().getNickname();
                                if (nickname == null) nickname = event.getMember().getEffectiveName();

                                privateChannel.sendMessage("<:felizpakas:742373250037710918> " +
                                        "Parabéns **" + nickname + "**, você capturou um shardloot, " +
                                        "você recebeu <:boost_emoji:772285522852839445> **$" + shard + " shards**")
                                        .queue();

                            }

                            logger.info("The player " + Utilities.INSTANCE.getFullName(event.getUser()) + " getted the sharddrop");

                        }, 25, TimeUnit.SECONDS,

                        () -> message.delete().queue());
            }
        }, 10, 55, TimeUnit.MINUTES);

    }
}
