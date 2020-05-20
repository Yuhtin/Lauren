package commands;

import application.Lauren;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import utils.helper.Utilities;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        this.name = "config";
        this.aliases = new String[]{"configurar", "cfg", "editar", "edit"};
        this.help = "Configurações do bot.";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!Utilities.isPermission(event.getMember(), event.getChannel(), Permission.ADMINISTRATOR))
            return;
        event.getMessage().delete().queue();
        String[] arguments = event.getMessage().getContentRaw().split(" ");
        if (arguments.length < 2) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("Configurações do bot", "https://google.com", "https://pt.seaicons.com/wp-content/uploads/2015/07/Settings-L-icon.png")
                    .setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
                    .setColor(event.getMember().getColor())
                    .setTimestamp(Instant.now())
                    .setFooter("Utilizado por " + event.getMember().getUser().getName() + "#" + event.getMember().getUser().getDiscriminator(), event.getMember().getUser().getAvatarUrl())
                    .setDescription(
                            "\n" +
                                    " **• " + Lauren.config.prefix + "config** setprefix <prefixo>\n" +
                                    "  Atual: " + Lauren.config.prefix + "\n" +
                                    "  Use para trocar o meu identificador\n\n" +
                                    " **• " + Lauren.config.prefix + "config** setregistration <messageid>\n" +
                                    "  Atual: " + Lauren.config.resgistrationId + "\n" +
                                    "  Troque o ID da mensagem de registro\n\n" +
                                    " **• " + Lauren.config.prefix + "config** setlog <true/false>\n" +
                                    "  Atual: " + (Lauren.config.log ? "Ativado" : "Desativado") + "\n" +
                                    "  Ativar ou desativar o salvamento de logs\n\n" +
                                    "\n");
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }
        String value = arguments[2];
        if (arguments[1].equalsIgnoreCase("setprefix")) {
            Lauren.config.updatePrefix(value);
            event.getChannel().sendMessage("<a:sim:704295025374265387> O meu prefixo foi alterado para '" + value + "'. Reinicie o bot para realizar a troca.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));

            return;
        }
        if (arguments[1].equalsIgnoreCase("setregistration")) {
            try {
                Lauren.config.setResgistrationId(Long.parseLong(value));
                Lauren.config.updateConfig();
            } catch (Exception exception) {
                event.getChannel().sendMessage("<a:nao:704295026036834375> O valor inserido é invalido: '" + value + "' (insira um id).").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            event.getChannel().sendMessage("<a:sim:704295025374265387> O id da mensagem de registro foi alterado para '" + value + "'.").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
        if (arguments[1].equalsIgnoreCase("setlog")) {
            try {
                Lauren.config.setLog(Boolean.parseBoolean(value));
                Lauren.config.updateConfig();
            } catch (Exception exception) {
                event.getChannel().sendMessage("<a:nao:704295026036834375> O valor inserido é invalido: '" + value + "' (insira true ou false).").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
                return;
            }
            event.getChannel().sendMessage("<a:sim:704295025374265387> As logs foram " + (Lauren.config.log ? "ativadas" : "desativadas") + ".").queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return;
        }
    }
}
