package com.yuhtin.lauren;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.yuhtin.lauren.commands.music.QueueCommand;
import com.yuhtin.lauren.commands.utility.ShopCommand;
import com.yuhtin.lauren.commands.utility.SugestionCommand;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.core.logger.controller.LoggerController;
import com.yuhtin.lauren.core.music.TrackManager;
import com.yuhtin.lauren.core.player.controller.PlayerController;
import com.yuhtin.lauren.core.xp.XpController;
import com.yuhtin.lauren.models.embeds.ShopEmbed;
import com.yuhtin.lauren.models.enums.LogType;
import com.yuhtin.lauren.models.objects.Config;
import com.yuhtin.lauren.service.LocaleManager;
import com.yuhtin.lauren.sql.connection.ConnectionInfo;
import com.yuhtin.lauren.sql.connection.SQLConnection;
import com.yuhtin.lauren.sql.connection.mysql.MySQLConnection;
import com.yuhtin.lauren.sql.connection.sqlite.SQLiteConnection;
import com.yuhtin.lauren.sql.dao.PlayerDAO;
import com.yuhtin.lauren.tasks.*;
import com.yuhtin.lauren.utils.helper.TaskHelper;
import com.yuhtin.lauren.utils.helper.Utilities;
import com.yuhtin.lauren.utils.messages.AsciiBox;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipOutputStream;

@lombok.Data
public class LaurenStartup {

    @Getter private static LaurenStartup instance = new LaurenStartup();
    private Injector injector;

    @Inject private PlayerDAO playerDAO;

    private SQLConnection sqlConnection;
    private ShardManager bot;
    private Guild guild;
    private long startTime;
    private Config config;
    private String version;

    public static void main(String[] args) throws InterruptedException {

        TaskHelper.runAsync(() -> {

            new Thread(LaurenStartup::loadTasks).start();

            LocaleManager.getInstance().searchHost(instance.getConfig().getGeoIpAcessKey());

            instance.getBot().addEventListener(eventWaiter);
            instance.getBot().addEventListener(ShopCommand.getEventWaiter());

            QueueCommand.getBuilder().setEventWaiter(eventWaiter);

            SugestionCommand.setWaiter(eventWaiter);

            LootGeneratorTask.getInstance().setEventWaiter(eventWaiter);
            ShardLootTask.getInstance().setEventWaiter(eventWaiter);

            XpController.getInstance();
            ShopEmbed.getInstance().build();
        });


        String[] loadNonFormated = new String[]{
                "",
                "Lauren v" + instance.getVersion(),
                "Author: Yuhtin#9147",
                "",
                "All systems has loaded",
                "Lauren is now online"
        };

        Logger.log(new AsciiBox()
                .size(50)
                .borders("━", "┃")
                .corners("┏", "┓", "┗", "┛")
                .render(loadNonFormated), LogType.STARTUP);

        Scanner scanner = new Scanner(System.in);
        while (scanner.nextLine().equalsIgnoreCase("stop")) {
            new Thread(LaurenStartup::finish).start();
        }
    }

    private static void loadTasks() {

        LootGeneratorTask.getInstance().startRunnable();
        ShardLootTask.getInstance().startRunnable();

    }

}