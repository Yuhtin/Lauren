package events;

import application.Lauren;
import core.logger.Logger;
import models.cache.PlayerDataCache;
import models.data.PlayerData;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MemberEvents extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        PlayerDataCache.insert(new PlayerData(event.getUser().getIdLong()));
        Lauren.data.create(event.getUser().getIdLong());
    }
}
