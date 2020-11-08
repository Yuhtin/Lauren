package com.yuhtin.lauren.commands.admin;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.mysql.cj.exceptions.NumberOutOfRange;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.xp.Level;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.database.DatabaseController;
import com.yuhtin.lauren.models.annotations.CommandHandler;
import com.yuhtin.lauren.utils.helper.Utilities;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import java.sql.PreparedStatement;

@CommandHandler(
        name = "configlevel",
        type = CommandHandler.CommandType.ADMIN,
        alias = {"levelreward"},
        description = "Configurar os rewards de um level"
)
public class ConfigLevelCommand extends Command {

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.INSTANCE.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR, true))
            return;

        int identifier;
        try {
            identifier = Integer.parseInt(event.getArgs().split(" ")[0]);

            // invalid level
            if (identifier > 35 || identifier < 1) throw new NumberOutOfRange("");
        } catch (Exception exception) {
            event.getChannel()
                    .sendMessage("<:eita:764084277226373120>" +
                            " Você inseriu um valor inválido para o nível (1 a 35), use $configlevel <nivel> <@cargos...>")
                    .queue();
            return;
        }

        Level level = XpController.getInstance().getLevelByXp().get(identifier);
        level.getRolesToGive().clear();

        if (event.getMessage().getMentionedRoles().isEmpty()) {
            event.getChannel().sendMessage("<:eita:764084277226373120> Você precisa mencionar pelo menos um cargo").queue();
            return;
        }

        StringBuilder value = new StringBuilder();
        for (int i = event.getMessage().getMentionedRoles().size() - 1; i >= 0; i--) {
            Role role = event.getMessage().getMentionedRoles().get(i);

            value.append(role.getId()).append(i != 0 ? "," : "");
            level.getRolesToGive().add(role.getIdLong());
        }

        String sql = "update `lauren_levelrewards` set `rewards` = '" + value.toString() + "' where `level` = '" + identifier + "'";
        try (PreparedStatement statement = DatabaseController.get().getConnection().prepareStatement(sql)) {

            statement.executeUpdate();
        } catch (Exception exception) {
            Logger.error(exception);
        }

        event.getChannel()
                .sendMessage("<:felizpakas:742373250037710918>" +
                        " Você adicionou **" + event.getMessage().getMentionedRoles().size() + "**" +
                        " cargo(s) ao nível **" + identifier + "**")
                .queue();
    }
}
