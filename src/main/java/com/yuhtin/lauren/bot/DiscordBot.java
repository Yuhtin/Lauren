package com.yuhtin.lauren.bot;

import net.dv8tion.jda.api.JDA;

public interface DiscordBot {

    /**
     * Called before connecting to discord api
     *
     */
    void onLoad();

    /**
     * Called when the bot is ready and connected
     *
     */
    void onReady();

    /**
     * Called when the bot is disabled
     *
     */
    void onDisable();

    /**
     * Receive the JDA instance
     *
     * @param jda the JDA instance
     */
    void setJda(JDA jda);


}
