package com.yuhtin.lauren.commands.impl.utility;

import com.yuhtin.lauren.commands.Command;
import com.yuhtin.lauren.commands.CommandInfo;
import com.yuhtin.lauren.commands.CommandType;
import com.yuhtin.lauren.module.Module;
import com.yuhtin.lauren.module.impl.player.module.PlayerModule;
import com.yuhtin.lauren.util.LoggerUtil;
import com.yuhtin.lauren.util.NumberUtil;
import com.yuhtin.lauren.util.TimeUtils;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;

import java.time.Instant;
import java.util.List;

@CommandInfo(
        name = "perfil",
        type = CommandType.PLAYER,
        description = "Visualizar o seu perfil ou de outro usuário",
        args = {
                "[@user]-Usuário que você deseja ver o perfil"
        }
)
public class ProfileCommand implements Command {

    @Override
    public void execute(CommandInteraction event, InteractionHook hook) throws Exception {
        val userOption = event.getOption("user");
        val target = userOption == null ? event.getMember() : userOption.getAsMember();

        PlayerModule playerModule = Module.instance(PlayerModule.class);
        playerModule.retrieve(target.getIdLong()).thenAccept(player -> {
            if (player == null) {
                LoggerUtil.getLogger().severe("Occured an error on try load player data of " + target.getIdLong());

                hook.sendMessage("Ocorreu um erro em meus dados, defusa aqui <@272879983326658570>\nPlayer ID: " + target.getIdLong()).queue();
                return;
            }

            val roles = rolesToString(target.getRoles());
            val name = target.getNickname() == null ? target.getUser().getName() : target.getNickname();

            val embed = new EmbedBuilder();
            embed.setColor(target.getColor());
            embed.setAuthor("Informações do jogador " + name, null, target.getUser().getAvatarUrl());
            embed.setThumbnail(player.getRank().getUrl());

            embed.addField("⚗️ Experiência", "`Nível " + player.getLevel() + " (" + NumberUtil.format(player.getExperience()) + " XP)`", false);
            embed.addField("🧶 Cargos", (roles.equalsIgnoreCase("") ? "Nenhum" : roles), false);
            embed.addField("✨ Entrou em", TimeUtils.formatToDiscord(target.getTimeJoined().toEpochSecond()), false);
            embed.addField("<:boost_emoji:772285522852839445> Shards", "`" + NumberUtil.format(player.getMoney()) + " shards`", true);
            embed.addField("<:beacon:771543538252120094> Patente", "`" + player.getRank().getName() + "`", true);
            embed.addField("<:lootbox:771545027829563402> LootBoxes", "`" + player.getLootBoxes() + " caixas`", true);
            embed.addField("\uD83D\uDD11 Chaves", "`" + player.getKeys() + " keys`", true);
            embed.addField("<:rename_command:775348818555699210> Votos no servidor", "`" + player.getVotes() + " votos`", true);

            embed.setFooter("Comando usado por " + name, event.getMember().getUser().getAvatarUrl());
            embed.setTimestamp(Instant.now());

            hook.sendMessageEmbeds(embed.build()).queue();
            // TODO: statsController.getStats("Player Command").suplyStats(1);
        });
    }

    private String rolesToString(List<Role> roles) {
        StringBuilder builder = new StringBuilder();

        for (Role role : roles) {
            builder.append(role.getAsMention()).append(", ");
        }

        return builder.toString().isEmpty() ? "" : builder.substring(0, builder.toString().length() - 2);
    }
}
