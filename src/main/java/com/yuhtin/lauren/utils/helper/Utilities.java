package com.yuhtin.lauren.utils.helper;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.yuhtin.lauren.core.player.Player;
import com.yuhtin.lauren.models.objects.Config;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utilities {

    public static final Utilities INSTANCE = new Utilities();

    @Inject @Named("main") private Logger logger;
    @Inject private Config config;
    @Inject private ShardManager shardManager;

    public boolean isPermission(Member member, MessageChannel channel, Permission permission, boolean showMessage) {

        if (!member.hasPermission(permission)) {

            if (!showMessage) return false;

            channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função")
                    .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));

            return false;

        }

        return true;

    }

    public boolean isOwner(MessageChannel channel, User user, boolean showMessage) {

        if (this.config.getOwnerID() != user.getIdLong()) {

            if (!showMessage) return false;

            channel.sendMessage("<a:nao:704295026036834375> Você não tem permissão para usar esta função")
                    .queue(message -> message.delete().queueAfter(5, TimeUnit.SECONDS));

            return false;

        }

        return true;

    }

    public void cleanUp(Path path) throws IOException {
        Files.delete(path);
    }

    public void updateNickByLevel(Player player, int level) {

        if (player.isHideLevelOnNickname()) return;

        Member member = this.shardManager.getShards().get(0).getGuilds().get(0).getMemberById(player.getUserID());
        if (member == null) return;

        String nickname = member.getNickname();
        if (nickname == null) nickname = member.getEffectiveName();
        if (nickname.contains("] ")) nickname = nickname.split("] ")[1];

        nickname = this.config.getFormatNickname().replace("@level", "" + level) + nickname;

        if (nickname.length() > 32) nickname = nickname.substring(0, 32);

        try { member.modifyNickname(nickname).queue(); } catch (HierarchyException ignored) { }
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


    public String protectedString(String value) {
        return value == null ? "Não informado" : value;
    }

}
