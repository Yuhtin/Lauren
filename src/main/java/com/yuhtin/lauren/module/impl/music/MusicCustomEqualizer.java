package com.yuhtin.lauren.module.impl.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class MusicCustomEqualizer extends EqualizerFactory {

    private static final float[] BASS_BOOST = {
            0.2f, 0.15f, 0.1f,
            0.05f, 0.0f, -0.05f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f,
            -0.1f, -0.1f, -0.1f
    };

    public void equalize(AudioPlayer player, EqualizerPreset preset, float diff) {
        player.setFilterFactory(this);

        switch (preset) {
            case HIGH:
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    setGain(i, BASS_BOOST[i] + diff);
                }

                break;
            case BASS_BOOST:
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    setGain(i, BASS_BOOST[i] + 0.12f);
                }

                for (int i = 0; i < BASS_BOOST.length; i++) {
                    setGain(i, -BASS_BOOST[i] + 0.013f);
                }

                break;
            case LOW_BASS:
                for (int i = 0; i < BASS_BOOST.length; i++) {
                    setGain(i, -BASS_BOOST[i] + diff);
                }

                break;
            default:

                break;
        }


    }

}
