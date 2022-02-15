package com.yuhtin.lauren.commands.impl.utility;

import com.google.inject.Inject;
import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.enums.Reward;
import com.yuhtin.lauren.utils.SimpleEmbed;
import com.yuhtin.lauren.utils.TaskHelper;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.util.*;
import java.util.concurrent.TimeUnit;

@CommandInfo(
        name = "lootbox",
        type = CommandInfo.CommandType.UTILITY,
        description = "Abrir uma lootbox"
)
public class LootBoxCommand implements Command {

    boolean running = false;

    @Inject private Logger logger;
    @Inject private PlayerController playerController;

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val player = this.playerController.get(event.getUser().getIdLong());
        if (player.getLootBoxes() == 0) {
            hook.sendMessage("<:fodane:764085078187442176> Você não tem lootboxes para abrir").queue();
            return;
        }

        if (player.getKeys() == 0) {
            hook.sendMessage("<:fodane:764085078187442176> Você não tem chaves para abrir esta lootbox, use `/shop` e adquira uma").queue();
            return;
        }

        if (running) {
            hook.sendMessage("<:fodane:764085078187442176> Ops, parece que já tem alguém usando a roleta, aguarde").queue();
            return;
        }

        player.setLootBoxes(player.getLootBoxes() - 1);
        player.setKeys(player.getKeys() - 1);

        running = true;

        List<LineRewardController> rewards = new ArrayList<>();

        hook.sendMessageEmbeds(SimpleEmbed.of("Rodando lootbox!")).setEphemeral(true).queue();

        int delay = 0;
        for (int i = 0; i < 3; i++) {

            val line = new LineRewardController(event.getTextChannel().sendMessage(":film_frames::film_frames::film_frames: :grey_question:").complete());
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

                    event.getTextChannel().sendMessage(
                                    "<@" + event.getUser().getId() + ">: <:lauren_loot:771536259062562846> " +
                                            "Você ganhou " + reward.getReward().getEmoji() + " **" + reward.getReward().getName() + "**")
                            .queue();

                    Reward gainReward = reward.getReward();
                    switch (gainReward) {
                        case ROLE:

                            Role role = event.getGuild().getRoleById(771541080634032149L);
                            if (role == null) {

                                logger.warning("The player " + event.getUser().getAsTag() + " win the Lucky role but i can't give");
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
                    event.getTextChannel().sendMessage("<:eita:764084277226373120> Você aparentemente não ganhou nada," +
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
