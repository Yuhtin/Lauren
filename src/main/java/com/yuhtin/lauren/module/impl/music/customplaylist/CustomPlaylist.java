package com.yuhtin.lauren.module.impl.music.customplaylist;

import com.yuhtin.lauren.database.MongoEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

@RequiredArgsConstructor
public class CustomPlaylist implements MongoEntity<CustomPlaylist> {

    private final long id;

    private final LinkedList<PlaylistTrackInfo> tracks = new LinkedList<>();
    @Setter private int currentIndex;
    @Getter
    @Setter private boolean isAutoPlay;

    @Nullable
    public PlaylistTrackInfo playNext() {
        if (!isAutoPlay) return null;
        if (tracks.isEmpty()) return null;

        if (currentIndex >= tracks.size()) {
            currentIndex = 0;
        }

        PlaylistTrackInfo track = tracks.get(currentIndex);
        currentIndex++;

        return track;
    }

    public void add(PlaylistTrackInfo track) {
        tracks.add(track);
    }

    public PlaylistTrackInfo remove(int index) {
        return tracks.remove(index);
    }

    public void clear() {
        tracks.clear();
    }

    public Collection<PlaylistTrackInfo> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public void shuffle() {
        Collections.shuffle(tracks);
    }

    @Override
    public Long getPrimaryKey() {
        return id;
    }
}
