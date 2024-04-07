package com.yuhtin.lauren.module.impl.misc;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.config.YamlConfiguration;
import com.yuhtin.lauren.module.ConfigurableModule;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.EmbedUtil;
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

public class LootGeneratorModule extends ConfigurableModule {

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

        EmbedBuilder embed = EmbedUtil.empty();
        embed.setAuthor("Loot Radiante", null, "https://cdn.discordapp.com/emojis/724759930653114399.png?v=1");

        embed.setThumbnail("https://www.pcguia.pt/wp-content/uploads/2019/11/lootbox.jpg");

        String arrow = "   <:seta:771540348157689886>";
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

        TaskHelper.runTaskTimerAsync(() -> {
            dropLoot(lauren, embed);
        }, 10, 55, TimeUnit.MINUTES);

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

    private void dropLoot(Lauren lauren, EmbedBuilder embed) {
        if (new Random().nextInt(100) > 10) return;

        int randomValue = new Random().nextInt(allowedChannels.size());
        long channelID = allowedChannels.get(randomValue);
        TextChannel channel = lauren.getJda().getTextChannelById(channelID);

        if (channel == null) {
            lauren.getLogger().warning("Can't select a random channel to drop a loot");
            return;
        }

        lauren.getLogger().info("Dropped loot on channel " + channel.getName());

        channel.sendMessageEmbeds(embed.build())
                .addActionRow(Button.success("lootbox", "Resgatar!"))
                .queue(message -> cache.put(channelID + ":" + message.getId(), null));
    }

    @SubscribeEvent
    public void onButtonClicked(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("lootbox")) return;

        event.deferEdit().queue(hook -> {
            PlayerModule playerModule = Module.instance(PlayerModule.class);
            if (playerModule == null) {
                hook.editOriginal("Ocorreu um erro ao resgatar o shardloot").queue();
                return;
            }

            User user = event.getUser();
            playerModule.retrieve(user.getIdLong()).queue(player -> {
                if (player == null) return;

                player.setLootBoxes(player.getLootBoxes() + 1);

                user.openPrivateChannel().queue(privateChannel -> {
                    if (user.hasPrivateChannel() && privateChannel != null) {
                        String nickname = user.getName();

                        privateChannel.sendMessage("<:felizpakas:742373250037710918> " +
                                        "Parabéns **" + nickname + "**, você capturou uma lootbox, você pode abrir ela mais tarde " +
                                        "usando `/lootbox`")
                                .queue();
                    }
                });

                event.editMessage("O jogador <@" + user.getId() + "> resgatou um lootbox!").queue();
            });
        });
    }


    @Override
    public void reload() {
        super.reload();

        allowedChannels.clear();

        List<String> stringList = getConfig().getStringList("lootbox-channels");
        if (stringList == null) return;

        for (String channels : stringList) {
            allowedChannels.add(Long.parseLong(channels));
        }
    }
}
