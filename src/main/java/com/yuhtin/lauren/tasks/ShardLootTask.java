package com.yuhtin.lauren.tasks;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ShardLootTask {

    private static final ShardLootTask INSTANCE = new ShardLootTask();
    @Setter private EventWaiter eventWaiter;

    public static ShardLootTask getInstance() {
        return INSTANCE;
    }

    public void startRunnable() {
        Logger.log("Registered ShardLootTask");

        final List<Long> allowedChannels = Arrays.asList(
                704342124732350645L,
                700673056414367825L
        );

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Shard Loot", null, "https://cdn.discordapp.com/emojis/772285522852839445.png?v=1");
        embed.setFooter("© ^Aincrad™ servidor de jogos", Lauren.getInstance().getGuild().getIconUrl());

        embed.setThumbnail("https://www.pcguia.pt/wp-content/uploads/2019/11/lootbox.jpg");
        embed.setColor(Color.MAGENTA);

        embed.setDescription("Você encontrou um **shardloot**, seja o primeiro a reajir\n" +
                "no ícone abaixo para garantir seus **<:boost_emoji:772285522852839445> shards**\n"
        );

        TaskHelper.runTaskTimerAsync(new TimerTask() {
            @Override
            public void run() {
                Logger.log("Running ShardLootTask");

                if (new Random().nextInt(100) > 45) return;

                int value = new Random().nextInt(allowedChannels.size());
                long channelID = allowedChannels.get(value);

                TextChannel channel = Lauren.getInstance().getGuild().getTextChannelById(channelID);

                if (channel == null) {
                    Logger.log("Can't select a random channel to drop a loot", LogType.ERROR);
                    return;
                }

                Logger.log("Dropped shardloot on channel " + channel.getName());

                Message message = channel.sendMessage(embed.build()).complete();
                message.addReaction(":boost_emoji:772285522852839445").queue();

                eventWaiter.waitForEvent(MessageReactionAddEvent.class, event -> !event.getMember().getUser().isBot(),
                        event -> {
                            message.clearReactions().queue();

                            String nickname = event.getMember().getNickname();
                            if (nickname == null) nickname = event.getMember().getEffectiveName();

                            int shard = 20 + new Random().nextInt(30);

                            channel.sendMessage("<:felizpakas:742373250037710918> " +
                                    "Parabéns **" + nickname + "**, você capturou um shardloot, " +
                                    "você recebeu <:boost_emoji:772285522852839445> **$" + shard + " shards**")
                                    .queue();

                            Player player = PlayerController.INSTANCE.get(event.getUserIdLong());
                            player.addMoney(shard);

                            Logger.log("The player " + Utilities.INSTANCE.getFullName(event.getUser()) + " getted the sharddrop");
                        }, 25, TimeUnit.SECONDS,

                        () -> {

                            message.editMessage("<a:tchau:751941650728747140> " +
                                    "Infelizmente acabou o tempo e ninguém coletou o loot.")
                                    .queue();
                            message.clearReactions().queue();

                        });
            }
        }, 10, 30, TimeUnit.MINUTES);

    }
}
