package com.yuhtin.lauren.commands.utility;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.models.enums.Reward;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import lombok.Getter;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;
import java.util.concurrent.TimeUnit;

@CommandHandler(
        name = "lootbox",
        type = CommandHandler.CommandType.UTILITY,
        description = "Abrir uma lootbox",
        alias = {"openloot", "abrircaixa", "caixa"}
)
public class LootBoxCommand extends Command {

    // emoji - reward
    private static final String defaultEmoji = "<:filling:772271983072247848>",
            completeError = "❌",
            completeSucess = ":bell:";

    static final Map<String, Reward> rewardMap = new HashMap<>();

    static {
        rewardMap.put("<:xp:772285036174639124>", Reward.EXPERIENCE);
        rewardMap.put("\uD83D\uDCB8", Reward.MONEY);
        rewardMap.put("<:boost_emoji:772285522852839445>", Reward.RANKED_POINTS);
        rewardMap.put("<:users:772286870624272415>", Reward.ROLE);
    }

    boolean running = false;

    @SneakyThrows
    @Override
    protected void execute(CommandEvent event) {

        Player player = PlayerController.INSTANCE.get(event.getAuthor().getIdLong());
        /*if (player.getLootBoxes() == 0) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Você não tem lootboxes para abrir").queue();
            return;
        }*/

        /*if (player.getKeys() == 0) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Você não tem chaves para abrir esta lootbox, user `$shop` e adiquira uma").queue();
            return;
        }*/

        if (running) {
            event.getChannel().sendMessage("<:fodane:764085078187442176> Ops, parece que já tem alguém usando a roleta, aguarde").queue();
            return;
        }

        running = true;

        List<LineRewardController> rewards = new ArrayList<>();

        int delay = 0;
        for (int i = 0; i < 3; i++) {

            LineRewardController line = new LineRewardController(
                    event.getChannel()
                            .sendMessage(defaultEmoji + defaultEmoji + defaultEmoji + " \uD83E\uDDE9")
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
                boolean gain = false;
                for (LineRewardController reward : rewards) {
                    if (reward.getReward() == null) continue;

                    event.getChannel()
                            .sendMessage(
                                    "<@" + event.getAuthor().getId() + ">: <:lauren_loot:771536259062562846> " +
                                            "Você ganhou " + reward.getReward() + " **")
                            .queue();

                    Reward gainReward = rewardMap.get(reward.getReward());
                    switch (gainReward) {
                        case ROLE:
                            Role role = Lauren.getInstance().getGuild().getRoleById(771541080634032149L);
                            Lauren.getInstance().getGuild().addRoleToMember(event.getMember(), role).queue();
                            break;
                        case MONEY:
                            break;
                        case EXPERIENCE:
                            break;
                        case RANKED_POINTS:
                            break;
                    }


                    gain = true;
                }

                if (!gain) {
                    event.getChannel()
                            .sendMessage("<:eita:764084277226373120> Você aparentemente não ganhou nada," +
                                    " vou te dar 1000 <:xp:772285036174639124> de consolação")
                            .queue();

                    //player.gainXP(1000);
                }

                running = false;
            }
        }, 15, TimeUnit.SECONDS);
    }

    public String randomReward() {
        Object[] array = rewardMap.keySet().toArray();

        return (String) array[new Random().nextInt(array.length)];
    }

    public class LineRewardController extends TimerTask {

        private final Message message;
        private String first = defaultEmoji,
                second = defaultEmoji,
                third = defaultEmoji,
                complete;

        @Getter
        private boolean sucess;
        @Getter
        private Reward reward;

        public LineRewardController(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            complete = "❔";

            if (!first.equalsIgnoreCase(defaultEmoji)) {
                if (!second.equalsIgnoreCase(defaultEmoji)) {
                    if (!third.equalsIgnoreCase(defaultEmoji)) {

                        if (first.equalsIgnoreCase(second) && third.equalsIgnoreCase(second)) {

                            complete = completeSucess;
                            reward = rewardMap.get(first);
                            sucess = true;

                        } else complete = completeError;

                        this.cancel();

                    } else third = randomReward();
                } else second = randomReward();
            } else first = randomReward();

            message.editMessage(first + " " + second + " " + third + " " + complete).queue();
        }
    }
}
