package com.yuhtin.lauren;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.yuhtin.lauren.core.entities.SpotifyConfig;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class SpotifyApiTest {
    public static void main(String[] args) throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi spotifyApi = SpotifyConfig.construct();

        Track track = spotifyApi.getTrack("4BqK1WHcYEaiYSFlHssgYs").build().execute();
        System.out.println(track.getName());

        Playlist playlist = spotifyApi.getPlaylist("2TTaADroRn9UsPRriB4oL9").build().execute();
        for (int i = 0; i < playlist.getTracks().getItems().length; i++) {
            Track item = (Track) playlist.getTracks().getItems()[i].getTrack();
            System.out.println(item.getName());
        }

        System.out.println(playlist.getTracks().getItems().length);
    }
}
