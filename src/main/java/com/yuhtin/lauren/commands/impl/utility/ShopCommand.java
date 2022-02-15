package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.models.objects.EventWaiter;
import com.yuhtin.lauren.startup.Startup;
import lombok.val;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "loja",
        type = CommandInfo.CommandType.UTILITY,
        description = "Ver algumas coisinhas que tou vendendo"
)
public class ShopCommand implements Command {

    private final Map<Long, Integer> events = new HashMap<>();
    @Inject
    private ShopEmbed shopEmbed;
    @Inject
    private PlayerController playerController;
    @Inject
    private Logger logger;
    @Inject
    private EventWaiter eventWaiter;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val embed = shopEmbed.getEmbed();
        val shopItems = this.shopEmbed.getShopItems();

        embed.setAuthor("Minha Lojinha v2000", null, event.getJDA().getSelfUser().getAvatarUrl());
        embed.setFooter("Bem-vindo(a) minha lojinha, eu abri ela às", event.getGuild().getIconUrl());

        val message = hook.setEphemeral(true).sendMessageEmbeds(embed.build()).complete();
        for (val emoji : shopItems.keySet()) {
            message.addReaction(emoji).queue();
        }

        val randomInt = new Random().nextInt(1500);

        val user = event.getUser();
        val userId = user.getIdLong();

        if (events.containsKey(userId)) events.replace(userId, randomInt);
        else events.put(userId, randomInt);

        val player = this.playerController.get(userId);
        eventWaiter.waitForEvent(MessageReactionAddEvent.class,
                reaction -> {
                    if (reaction.getUserIdLong() != userId || events.get(userId) != randomInt) return false;

                    val shopItem = shopItems.get(reaction.getReaction().getReactionEmote().getAsReactionCode());
                    if (shopItem == null) return false;

                    if (shopItem.getPrice() > player.getMoney()) {
                        event.getChannel().sendMessage("<:chorano:726207542413230142> Você não tem dinheiro pra realizar essa ação").queue();
                        return false;
                    }

                    return true;
                },

                reaction -> {
                    val shopItem = shopItems.get(reaction.getReaction().getReactionEmote().getAsReactionCode());
                    player.removeMoney(shopItem.getPrice());

                    switch (shopItem.getType()) {
                        case KEY:
                            player.setKeys(player.getKeys() + 1);
                            break;

                        case RENAME_COMMAND:
                            player.addPermission("commands.nickname");
                            break;

                        case PRIME:
                            val role = Startup.getLauren().getGuild().getRoleById(722116789055782912L);
                            if (role == null) {
                                logger.warning("The player "
                                        + user.getAsTag() +
                                        " buyed Prime role, but i can't give"
                                );

                                break;
                            }

                            Startup.getLauren()
                                    .getGuild()
                                    .addRoleToMember(userId, role)
                                    .queue();
                            break;

                        default: break;
                    }

                    message.editMessage("<:feliz_pra_caralho:760202116504485948> " +
                                    "Você comprou **"
                                    + shopItem.getType().getName() +
                                    "** por **"
                                    + "<:boost_emoji:772285522852839445> $" +
                                    shopItem.getPrice()
                                    + " shards**")
                            .setEmbeds(new MessageEmbed[]{})
                            .queue();

                    events.remove(userId);

                }, 1, TimeUnit.MINUTES, () -> {
                    if (events.get(userId) == randomInt)
                        events.remove(userId);
                    message.clearReactions().queue();
                });
    }
}
