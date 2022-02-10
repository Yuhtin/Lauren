package com.yuhtin.lauren.core.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;

@Getter
public class GuildMusicManager {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayer player, Guild guild) {
        this.player = player;
        this.player.setVolume(25);

        this.scheduler = new TrackScheduler(player, guild);
        player.addListener(scheduler);
    }

}
