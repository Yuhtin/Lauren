package com.yuhtin.lauren.core.draw.controller;

import com.yuhtin.lauren.application.Lauren;
import com.yuhtin.lauren.core.draw.Draw;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.Instant;

public class DrawEditting {

    public Message message;
    public Long userID;
    public RestAction<PrivateChannel> privateChannel;
    public DrawEditingStatus status = DrawEditingStatus.PRIZE;

    /*
        Required args
     */

    public String prize;
    public int winnersCount, seconds;

    public DrawEditting(Message message, Long userID, RestAction<PrivateChannel> privateChannel) {
        this.message = message;
        this.userID = userID;
        this.privateChannel = privateChannel;
    }

    public Draw build() {
        return new Draw(prize, winnersCount, Instant.now().plusSeconds(seconds), userID, Lauren.bot.getTextChannelById(721881572973871125L), null, false);
    }
}
