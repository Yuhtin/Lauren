package music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class GuildMusicManager {
    public final AudioPlayer player;
    public final TrackScheduler scheduler;

    public GuildMusicManager(AudioPlayer player) {
        this.player = player;
        this.player.setVolume(35);

        this.scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}
