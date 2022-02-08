package com.yuhtin.lauren.commands.impl.admin;

import com.google.inject.Inject;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.commands.CommandData;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.utils.helper.UserUtil;
import net.dv8tion.jda.api.Permission;

@CommandData(
        name = "configlevel",
        type = CommandData.CommandType.ADMIN,
        alias = {},
        description = "Configurar os rewards de um level"
)
public class ConfigLevelCommand implements CommandExecutor {

    @Inject private Logger logger;
    @Inject private XpController xpController;
    @Inject private SQLConnection sqlConnection;

    @Override
    public void execute(CommandEvent event) {
        if (!UserUtil.hasPermission(event.getMember(), event.getMessage(), Permission.ADMINISTRATOR, true))
            return;

        /*int identifier;
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

        Level level = this.xpController.getLevelByXp().get(identifier);
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
        try (PreparedStatement statement = this.sqlConnection.findConnection().prepareStatement(sql)) {

            statement.executeUpdate();
        } catch (Exception exception) {
            this.logger.log(LogType.WARNING, "Can't update level rewards", exception);
        }

        event.getChannel()
                .sendMessage("<:felizpakas:742373250037710918>" +
                        " Você adicionou **" + event.getMessage().getMentionedRoles().size() + "**" +
                        " cargo(s) ao nível **" + identifier + "**")
                .queue();*/
    }
}
