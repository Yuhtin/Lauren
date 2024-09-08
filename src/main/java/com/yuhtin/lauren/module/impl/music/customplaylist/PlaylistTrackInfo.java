package com.yuhtin.lauren.module.impl.music.customplaylist;

import com.yuhtin.lauren.util.MusicUtil;

public record PlaylistTrackInfo(String addedBy,
                                long addedByUserId,
                                String trackName,
                                String trackUrl,
                                long duration) {

    public String toString() {
        return "`[" + MusicUtil.getTimeStamp(duration) + "]` **" + trackName + "** - Adicionado por " + addedBy + " | [Link](" + trackUrl + ")";
    }

}