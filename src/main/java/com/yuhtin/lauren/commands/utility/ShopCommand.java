package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.models.objects.ShopItem;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "loja",
        type = CommandHandler.CommandType.UTILITY,
        description = "Ver algumas coisinhas que tou vendendo",
        alias = {"shop"}
)
public class ShopCommand extends Command {

    @Getter private static final EventWaiter eventWaiter = new EventWaiter();
    final Map<Long, Integer> events = new HashMap<>();

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder embed = ShopEmbed.getInstance().getEmbed();
        Map<String, ShopItem> shopItems = ShopEmbed.getInstance().getShopItems();

        embed.setAuthor("Minha Lojinha v2000", null, event.getJDA().getSelfUser().getAvatarUrl());
        embed.setFooter("Bem-vindo(a) minha lojinha, eu abri ela às", event.getGuild().getIconUrl());

        Message message = event.getChannel()
                .sendMessage(embed.build())
                .complete();

        for (String emoji : shopItems.keySet()) {
            message.addReaction(emoji).queue();
        }

        int randomInt = new Random().nextInt(1500);

        if (events.containsKey(event.getAuthor().getIdLong())) events.replace(event.getAuthor().getIdLong(), randomInt);
        else events.put(event.getAuthor().getIdLong(), randomInt);

        Player player = PlayerController.INSTANCE.get(event.getAuthor().getIdLong());
        eventWaiter.waitForEvent(MessageReactionAddEvent.class,

                reaction -> {
                    if (reaction.getUserIdLong() == event.getAuthor().getIdLong()
                            && events.get(event.getAuthor().getIdLong()) == randomInt) {

                        ShopItem shopItem = shopItems.get(reaction.getReaction().getReactionEmote().getAsReactionCode());
                        if (shopItem == null) return false;

                        if (shopItem.getPrice() > player.getMoney()) {

                            event.getChannel()
                                    .sendMessage("<:chorano:726207542413230142> " +
                                            "Você não tem dinheiro pra realizar essa ação")
                                    .queue();
                            return false;

                        }

                        return true;

                    }

                    return false;
                },

                reaction -> {

                    ShopItem shopItem = shopItems.get(reaction.getReaction().getReactionEmote().getAsReactionCode());
                    player.removeMoney(shopItem.getPrice());

                    switch (shopItem.getType()) {
                        case KEY:

                            player.setKeys(player.getKeys() + 1);
                            break;

                        case RENAME_COMMAND:

                            player.addPermission("commands.nickname");
                            break;

                        case PRIME:

                            Role role = Lauren.getInstance().getGuild().getRoleById(722116789055782912L);
                            if (role == null) {

                                Logger.log("The player "
                                        + Utilities.INSTANCE.getFullName(event.getAuthor()) +
                                        " buyed Prime role, but i can't give");
                                break;

                            }


                            Lauren.getInstance()
                                    .getGuild()
                                    .addRoleToMember(event.getAuthor().getIdLong(), role)
                                    .queue();
                            break;

                        default:
                            break;

                    }

                    event.getChannel()
                            .sendMessage("<:feliz_pra_caralho:760202116504485948> " +
                                    "Você comprou **"
                                    + shopItem.getType().getName() +
                                    "** por **"
                                    + "<:boost_emoji:772285522852839445> $" +
                                    shopItem.getPrice()
                                    + " shards**")
                            .queue();

                    events.remove(event.getAuthor().getIdLong());

                }, 1, TimeUnit.MINUTES, () -> {

                    if (events.get(event.getAuthor().getIdLong()) == randomInt)
                        events.remove(event.getAuthor().getIdLong());
                    message.clearReactions().queue();
                });
    }
}
