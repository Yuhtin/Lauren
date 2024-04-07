package com.yuhtin.lauren.module.impl.misc;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.config.YamlConfiguration;
import com.yuhtin.lauren.module.ConfigurableModule;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.EmbedUtil;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.TaskHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ShardLootModule extends ConfigurableModule {

    private Lauren lauren;
    private final List<Long> allowedChannels = new ArrayList<>();
    private final Cache<String, Void> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .removalListener((key, value, cause) -> {
                if (key != null) {
                    delete((String) key);
                }
            })
            .build();

    @Override
    public boolean setup(Lauren lauren) {
        this.lauren = lauren;
        setConfig(YamlConfiguration.load("config.yml"));
        reload();

        EmbedBuilder embed = EmbedUtil.of("""
                Você encontrou um **shardloot**, seja o primeiro a reajir
                no ícone abaixo para garantir seus **<:boost_emoji:772285522852839445> shards**
                """
        );

        embed.setAuthor("Shard Loot", null, "https://cdn.discordapp.com/emojis/772285522852839445.png?v=1");
        embed.setThumbnail("https://www.pcguia.pt/wp-content/uploads/2019/11/lootbox.jpg");

        TaskHelper.runTaskTimerAsync(
                () -> dropShardLoot(lauren, embed),
                10, 55, TimeUnit.MINUTES
        );

        return true;
    }

    private void delete(String ids) {
        String[] split = ids.split(":");

        long channelId = Long.parseLong(split[0]);
        long messageId = Long.parseLong(split[1]);

        TextChannel channel = lauren.getJda().getTextChannelById(channelId);
        if (channel == null) return;

        channel.deleteMessageById(messageId).queue();
    }

    private void dropShardLoot(Lauren lauren, EmbedBuilder embed) {
        Logger logger = LoggerUtil.getLogger();
        logger.info("Running ShardLootTask");

        if (new Random().nextInt(100) > 25) return;

        int randomValue = new Random().nextInt(allowedChannels.size());
        long channelID = allowedChannels.get(randomValue);
        TextChannel channel = lauren.getJda().getTextChannelById(channelID);

        if (channel == null) {
            logger.warning("Can't select a random channel to drop a loot");
            return;
        }

        logger.info("Dropped shardloot on channel " + channel.getName());

        channel.sendMessageEmbeds(embed.build())
                .addActionRow(Button.success("shardloot", "Resgatar!"))
                .queue(message -> cache.put(channelID + ":" + message.getId(), null));
    }

    @SubscribeEvent
    public void onButtonClicked(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("shardloot")) return;

        event.deferEdit().queue(hook -> {
            PlayerModule playerModule = Module.instance(PlayerModule.class);
            if (playerModule == null) {
                hook.editOriginal("Ocorreu um erro ao resgatar o shardloot").queue();
                return;
            }

            User user = event.getUser();
            playerModule.retrieve(user.getIdLong()).queue(player -> {
                if (player == null) return;

                int shard = 30 + new Random().nextInt(50);
                player.addMoney(shard);

                user.openPrivateChannel().queue(privateChannel -> {
                    if (user.hasPrivateChannel() && privateChannel != null) {
                        String nickname = user.getName();

                        privateChannel.sendMessage("<:felizpakas:742373250037710918> " +
                                        "Parabéns **" + nickname + "**! Você capturou um shardloot e " +
                                        "recebeu <:boost_emoji:772285522852839445> **" + shard + " shards**")
                                .queue();
                    }
                });

                event.editMessage("O jogador <@" + user.getId() + "> resgatou um shardloot!").queue();
            });
        });
    }

    @Override
    public void reload() {
        super.reload();

        allowedChannels.clear();

        List<String> stringList = getConfig().getStringList("shard-loot-channels");
        if (stringList == null) return;

        for (String channels : stringList) {
            allowedChannels.add(Long.parseLong(channels));
        }
    }
}
