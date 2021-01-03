package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.Reward;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@CommandHandler(
        name = "lootbox",
        type = CommandHandler.CommandType.UTILITY,
        description = "Abrir uma lootbox",
        alias = {"openloot", "abrircaixa", "caixa"}
)
public class LootBoxCommand extends Command {

    boolean running = false;

    @Inject @Named("main") private Logger logger;
    @Inject private PlayerController playerController;

    @Override
    protected void execute(CommandEvent event) {

        Player player = this.playerController.get(event.getAuthor().getIdLong());
        if (player.getLootBoxes() == 0) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Você não tem lootboxes para abrir").queue();
            return;
        }

        if (player.getKeys() == 0) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Você não tem chaves para abrir esta lootbox, use `$shop` e adquira uma").queue();
            return;
        }

        if (running) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Ops, parece que já tem alguém usando a roleta, aguarde").queue();
            return;
        }

        player.setLootBoxes(player.getLootBoxes() - 1);
        player.setKeys(player.getKeys() - 1);

        running = true;

        List<LineRewardController> rewards = new ArrayList<>();

        int delay = 0;
        for (int i = 0; i < 3; i++) {

            LineRewardController line = new LineRewardController(
                    event.getChannel()
                            .sendMessage(":film_frames::film_frames::film_frames: :grey_question:")
                            .complete());

            rewards.add(line);

            TaskHelper.runTaskLater(new TimerTask() {
                @Override
                public void run() {
                    TaskHelper.runTaskTimerAsync(line, 0, 1250, TimeUnit.MILLISECONDS);
                }
            }, delay, TimeUnit.MILLISECONDS);

            delay += 5300;
        }

        TaskHelper.runTaskLater(new TimerTask() {
            @Override
            public void run() {
                running = false;

                boolean givedReward = false;
                for (LineRewardController reward : rewards) {
                    if (reward.getReward() == null) continue;

                    event.getChannel()
                            .sendMessage(
                                    "<@" + event.getAuthor().getId() + ">: <:lauren_loot:771536259062562846> " +
                                            "Você ganhou " + reward.getReward().getEmoji() + " **" + reward.getReward().getName() + "**")
                            .queue();

                    Reward gainReward = reward.getReward();
                    switch (gainReward) {
                        case ROLE:

                            Role role = event.getGuild().getRoleById(771541080634032149L);
                            if (role == null) {

                                logger.warning("The player " + Utilities.INSTANCE.getFullName(event.getAuthor()) + " win the Lucky role but i can't give");
                                break;

                            }

                            event.getGuild().addRoleToMember(event.getMember(), role).queue();
                            break;

                        case MONEY:

                            player.addMoney(1500);
                            break;

                        case EXPERIENCE:

                            player.gainXP(3000);
                            break;

                        case RANKED_POINTS:

                            player.setRankedPoints(player.getRankedPoints() + 40);
                            player.updateRank();
                            break;

                        case BOOST:

                            player.addPermission("earnigns.boost");
                            break;

                    }

                    givedReward = true;
                }

                if (!givedReward) {
                    event.getChannel()
                            .sendMessage("<:eita:764084277226373120> Você aparentemente não ganhou nada," +
                                    " vou te dar 1000 <:xp:772285036174639124> de consolação")
                            .queue();

                    player.gainXP(1000);
                }

            }
        }, 15, TimeUnit.SECONDS);
    }

    public static class LineRewardController extends TimerTask {

        private static final String DEFAULT_EMOJI = ":film_frames:";
        private static final String ERROR = ":x:";
        private static final String SUCESS = ":bell:";

        private final Message message;
        private String first = DEFAULT_EMOJI;
        private String second = DEFAULT_EMOJI;
        private String third = DEFAULT_EMOJI;
        private String complete = ":grey_question:";

        @Getter private Reward reward;

        public LineRewardController(Message message) {
            this.message = message;
        }

        @Override
        public void run() {

            if (!first.equalsIgnoreCase(DEFAULT_EMOJI)) {
                if (!second.equalsIgnoreCase(DEFAULT_EMOJI)) {
                    if (!third.equalsIgnoreCase(DEFAULT_EMOJI)) {

                        if (first.equalsIgnoreCase(second) && third.equalsIgnoreCase(second)) {

                            complete = SUCESS;

                            reward = Arrays.stream(Reward.values())
                                    .filter(field -> field.getEmoji().equalsIgnoreCase(first))
                                    .findFirst()
                                    .orElse(null);

                        } else complete = ERROR;

                        this.cancel();

                    } else third = randomReward();
                } else second = randomReward();
            } else first = randomReward();

            message.editMessage(first + " " + second + " " + third + " " + complete).queue();
        }

        public String randomReward() {
            return Reward.getRandomReward().getEmoji();
        }
    }
}
