package manager;

import logger.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ListenersStartup {
    public ListenersStartup(JDA bot, String folder, String... classes) {
        for (String className : classes) {
            try {
                ListenerAdapter listener = (ListenerAdapter)Class.forName(folder + "." + className).newInstance();
                bot.addEventListener(listener);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException exception) {
                Logger.log("Unable to find class " + className + ".class").save();
            }
        }
    }
}
