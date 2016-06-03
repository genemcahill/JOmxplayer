package com.jomxplayer.core;

import java.io.IOException;

/**
 * A Java Process wrapper for easily handling Omxplayer
 */
public class OmxplayerProcess {

    public enum AspectMode {
        LETTERBOX("letterbox"),
        FILL("fill"),
        STRETCH("stretch");

        private String label;

        AspectMode(String label){
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private String filePath;
    private boolean mute;
    private AspectMode aspectMode;
    private int[] window;

    private Process process;

    /**
     * @param filePath - File path of the video to play
     */
    public OmxplayerProcess(String filePath){
        this.filePath = filePath;

        //Make sure that the omxplayer process is killed when the Java application exits
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                OmxplayerProcess.this.stop();
            }
        });
    }

    /**
     * Set wheter or not to play audio
     * @param mute - true mute the video, false play audio
     * @return this instance of Omxplayer
     */
    public OmxplayerProcess setMute(boolean mute) {
        this.mute = mute;
        return this;
    }

    /**
     * Set's the bounding box of video on the screen
     * @param x1 - Initial x position of the video
     * @param y1 - Intial y position of the video
     * @param x2 - Ending x position of the video
     * @param y2 - Ending y position of the video
     * @return this instance of Omxplayer
     */
    public OmxplayerProcess setWindow(int x1, int y1, int x2, int y2) {
        this.window = new int[]{x1,y1,x2,y2};
        return this;
    }

    /**
     * Set's the aspect mode of the video. Default: stretch if win is specified, letterbox otherwise
     * @param aspectMode
     * @return this instance of Omxplayer
     */
    public OmxplayerProcess setAspectMode(AspectMode aspectMode) {
        this.aspectMode = aspectMode;
        return this;
    }

    /**
     * Create a Java process and start playing the video
     * @return this instance of Omxplayer
     */
    public OmxplayerProcess play() {
        if(process == null){
            String command = "omxplayer";
            if(mute){
                command += " -n -1";
            }
            if(window != null){
                command += String.format(" --win '%d %d %d %d'", window[0], window[1], window[2], window[3]);
            }
            if(aspectMode != null){
                command += " --aspect-mode " + aspectMode;
            }

            command = command + " " + filePath;

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);

            try{
                process = pb.start();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Stop the Java process associated with the video
     */
    public void stop() {
        if(process != null){
            try{
                process.getOutputStream().write('q');
                process.getOutputStream().flush();
                process = null;
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
