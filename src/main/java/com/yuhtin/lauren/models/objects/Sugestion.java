package com.yuhtin.lauren.models.objects;

import com.yuhtin.lauren.models.enums.SugestionStage;
import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

@Builder
@Data
public class Sugestion {

    private String sugestion, reason;
    private Message message;
    private SugestionStage stage;
    private User user;

}
