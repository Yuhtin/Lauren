package manager;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import logger.Logger;
import net.dv8tion.jda.api.JDA;

public class CommandStartup {
    public CommandStartup(JDA bot, String folder, String... classes) {
        for (String className : classes) {
            try {
                CommandClientBuilder clientBuilder = new CommandClientBuilder();
                clientBuilder.addCommand((Command) Class.forName(folder + "." + className).newInstance());
                clientBuilder.setOwnerId("702518526753243156");
                clientBuilder.setPrefix("$");
                bot.addEventListener(clientBuilder.build());
                Logger.log("A new Command has been registered: " + className + ".class").save();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException exception) {
                Logger.log("Unable to find class " + className + ".class").save();
            }
        }
    }
}
