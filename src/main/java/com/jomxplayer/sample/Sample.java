package com.jomxplayer.sample;

import com.jomxplayer.core.OmxplayerProcess;

/**
 * A Sample class used to show OmxplayerProcess capabilities
 */
public class Sample {

    public static void main(String[] args) {

        String videoPath = "~/test.mp4";

        int x1 = 0;
        int y1 = 0;
        int x2 = 1920/2;
        int y2 = 1080/2;

        OmxplayerProcess player = new OmxplayerProcess(videoPath)
                .setMute(true)
                .setAspectMode(OmxplayerProcess.AspectMode.LETTERBOX)
                .setWindow(x1, y1, x2, y2)
                .play();
    }

}
