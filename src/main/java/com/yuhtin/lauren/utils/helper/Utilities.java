package com.yuhtin.lauren.utils.helper;

import com.yuhtin.lauren.Lauren;
import com.yuhtin.lauren.core.logger.Logger;
import com.yuhtin.lauren.models.enums.LogType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utilities {

    public static final Utilities INSTANCE = new Utilities();

    public boolean isPermission(Member member, MessageChannel channel, Permission permission, boolean showMessage) {
        if (!member.hasPermission(permission)) {
            if (!showMessage) return false;

            MessageAction message = channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função");
            message.queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            Logger.log("Failed to check permissions for user " + getFullName(member.getUser()));
            return false;
        }
        return true;
    }

    public boolean isCommandsChannel(Member member, MessageChannel channel) {
        if (member.hasPermission(Permission.MESSAGE_MANAGE) || channel.getIdLong() == 704342124732350645L) return true;

        channel.sendMessage("<:rindo_de_voce:751941649655136588> Meus comandos só estão liberados em <#704342124732350645>").queue();

        return false;
    }

    public StackTraceElement[] getStackTrace() {
        Throwable throwable = new Throwable();
        throwable.fillInStackTrace();

        return throwable.getStackTrace();
    }

    public boolean isOwner(MessageChannel channel, User user, boolean showMessage) {
        if (Lauren.getInstance().getConfig().ownerID != user.getIdLong()) {
            Logger.log("Failed to check owner permission for user " + getFullName(user));
            if (!showMessage) return false;

            MessageAction message = channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função");
            message.queue(m -> m.delete().queueAfter(5, TimeUnit.SECONDS));
            return false;
        }
        return true;
    }

    public void cleanUp(Path path) throws IOException {
        Files.delete(path);
    }

    public void updateNickByLevel(Long userID, int level) {
        Member member = Lauren.getInstance().getBot().getGuilds().get(0).getMemberById(userID);
        if (member == null) return;

        String nickname = member.getNickname();
        if (nickname == null) nickname = member.getEffectiveName();
        if (nickname.contains("] ")) nickname = nickname.split("] ")[1];

        nickname = Lauren.getInstance().getConfig().formatNickname.replace("@level", "" + level) + nickname;
        if (nickname.length() > 32) nickname = nickname.substring(0, 32);

        try {
            member.modifyNickname(nickname).queue();
        } catch (HierarchyException ignored) {
            Logger.log("Can't update member with role higher my self", LogType.ERROR);
        }
    }

    public String format(double valor) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
        return decimalFormat.format(valor);
    }

    public String getFullName(User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public String rolesToString(List<Role> roles) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < roles.size(); i++) {
            if (i != 0) builder.append(", ");
            builder.append(roles.get(i).getName());
        }

        return builder.toString();
    }

    public String randomString() {
        StringBuilder sb = new StringBuilder();
        String a = "1234567890";
        int i;
        for (int t = 0; t < 6; t++) {
            i = new Random().nextInt(a.length());
            sb.append(a, i, i + 1);
        }

        return sb.toString();
    }

    public void writeToZip(File file, ZipOutputStream zipStream) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(file.getName());

            zipStream.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipStream.write(bytes, 0, length);
            }

            zipStream.closeEntry();
        }
    }

    public File getAttachment(Message.Attachment attachment) {
        File file = new File("temporary/" + attachment.getFileName());
        try {
            if (!file.createNewFile()) return null;
            return attachment.downloadToFile(file).get();
        } catch (Exception exception) {
            return null;
        }
    }

    public boolean isDJ(Member member, MessageChannel channel, boolean message) {
        boolean isDJ = member.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("DJ \uD83C\uDFB6"));

        if (!isDJ && message)
            channel.sendMessage("Ahhh, que pena \uD83D\uDC94 você não pode realizar essa operação").queue();
        return isDJ;
    }

    public boolean isPrime(Member member) {
        if (member == null) return false;

        return member.getRoles().stream().filter(Objects::nonNull).anyMatch(role -> role.getIdLong() == 722116789055782912L);
    }

    public boolean isBooster(Member member) {
        if (member == null) return false;

        return member.getRoles().stream().filter(Objects::nonNull).anyMatch(role -> role.getIdLong() == 750365511430307931L);
    }

    public String protectedString(String value) {
        return value == null ? "Não informado" : value;
    }

    public void foundVersion() {
        Properties properties = new Properties();

        try {
            properties.load(Lauren.class.getClassLoader().getResourceAsStream("project.properties"));
            Lauren.getInstance().setVersion(properties.getProperty("version"));
        } catch (Exception exception) {
            Logger.log("An exception was caught while searching for my client version", LogType.ERROR);
            Logger.error(exception);
        }
    }
}
