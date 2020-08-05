package com.yuhtin.lauren.utils.helper;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;

public class SpotifyConnector {

    public static SpotifyApi get(String clientId, String clientSecret, String acessToken, String refreshToken) {
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setAccessToken(acessToken)
                .setRefreshToken(refreshToken)
                .setRedirectUri(SpotifyHttpManager.makeUri("http://localhost:8888/callback/"))
                .build();
    }
}
