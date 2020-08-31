package com.yuhtin.lauren.core.match;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.match.controller.MatchController;
import com.yuhtin.lauren.service.PlayerService;
import com.yuhtin.lauren.models.enums.GameMode;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Match {

    public String id, urlPrint;
    public Game game;
    public Long winPlayer, startTime, finishTime;
    public long channel;
    public List<Long> players, confirmedPlayers = new ArrayList<>();

    public Match(Game game) {
        this.game = game;

        id = Utilities.randomString();
    }

    public void createChannel() {
        ChannelAction<TextChannel> textChannel = Lauren.guild.createTextChannel("match-" + id);

        players.forEach(id -> textChannel.addMemberPermissionOverride(id, EnumSet.of(Permission.VIEW_CHANNEL), null));
        textChannel.addPermissionOverride(Lauren.guild.getPublicRole(), null, EnumSet.of(Permission.VIEW_CHANNEL));
        textChannel.queue(createdChannel -> channel = createdChannel.getIdLong());

        TextChannel channel = Lauren.bot.getTextChannelById(this.channel);
        channel.sendMessage("Confirme sua presença, clique no <a:sim:704295025374265387>").queue(message -> message.addReaction("a:sim:704295025374265387"));

        if (game.mode == GameMode.RANKED)
            TaskHelper.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (startTime == null) {
                        channel.delete().queue();
                        players.forEach(MatchController.playersInQueue::remove);
                    }
                }
            }, 20, TimeUnit.SECONDS);
        else {
            confirmedPlayers = players;
            if (!startMatch()) {
                Logger.log("Error in match " + id, LogType.ERROR).save();
            }
        }
    }

    public boolean startMatch() {
        if (players.size() > confirmedPlayers.size())
            return false;

        MatchController.insert(this);
        return true;
    }

    public void finishMatch(Long winPlayer, String urlPrint) {
        this.winPlayer = winPlayer;
        this.urlPrint = urlPrint;
        this.finishTime = System.currentTimeMillis();

        new Thread(() -> players.forEach(id -> PlayerService.INSTANCE.get(id).computMatch(this).save())).start();

        MatchController.finishMatch(this);
    }

    public boolean containsPlayer(Long userID) {
        return players.contains(userID);
    }

    public void insertPlayer(Long userID) {
        players.add(userID);
    }
}
