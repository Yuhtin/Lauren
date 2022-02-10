package com.yuhtin.lauren.models.objects;

import com.yuhtin.lauren.models.enums.SugestionStage;
import com.yuhtin.lauren.startup.Startup;
import com.yuhtin.lauren.utils.UserUtil;
import lombok.Builder;
import lombok.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

@Builder
@Data
public class Sugestion {

    private String corp;
    private String reason;
    private Message message;
    private SugestionStage stage;
    private User user;

    public EmbedBuilder createSugestionEmbed() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("| Sugestão de " + UserUtil.INSTANCE.getFullName(this.user),
                null, this.user.getAvatarUrl());

        embed.setFooter("© Todos os direitos reservados",  Startup.getLauren().getGuild().getIconUrl());
        embed.setColor(Color.GRAY);

        embed.addField("<a:confete:769423543044800512> Sugestão para o servidor",
                "`" + UserUtil.INSTANCE.protectedString(this.corp) + "`",
                false);

        embed.addField("<:procurando:769423542126247956> Motivo pela qual deve ser aceita",
                "`" + UserUtil.INSTANCE.protectedString(this.reason) + "`",
                false);

        return embed;
    }

    public void updateMessage() {
        if (this.message == null) {

            Startup.getLauren().getLogger().severe("An error occured on suggestion ticket (message is null)");
            return;

        }

        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("| Enviando uma sugestão", null, Startup.getLauren().getGuild().getIconUrl());
        embed.setFooter("© Todos os direitos reservados", Startup.getLauren().getGuild().getIconUrl());
        embed.setColor(Color.GREEN);

        embed.setDescription("<:errado:756770088639791234> O mal uso deste comando irá causar punição para o mesmo");

        embed.addField("<a:confete:769423543044800512> Qual sua sugestão para o servidor?",
                "`" + UserUtil.INSTANCE.protectedString(this.corp) + "`",
                false);

        embed.addField("<:procurando:769423542126247956> Motivo pela qual deve ser aceita?",
                "`" + UserUtil.INSTANCE.protectedString(this.reason) + "`",
                false);

        MessageBuilder builder = new MessageBuilder().setContent("Preencha na ordem abaixo").setEmbed(embed.build());

        this.message.editMessage(builder.build()).queue();
    }

}
