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

    private OmxplayerMonitorThread monitorThread;
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
                log("Shutdown hook called. Attempting to stop Omxplayer Process");
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
    public synchronized OmxplayerProcess play() {
        if(process == null && monitorThread == null){
            monitorThread = new OmxplayerMonitorThread();
            monitorThread.start();
        }
        return this;
    }

    /**
     * Stop the Java process associated with the video
     */
    public synchronized void stop() {
        log("stop() called. Writing 'q' to output stream");
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

    private void log(String msg){
        System.out.println(msg);
    }

    /**
     * A thread used to monitor the Java Process for Omxplayer
     */
    private class OmxplayerMonitorThread extends Thread {

        @Override
        public void run() {
            super.run();
            log("OmxplayerMonitorThread run() called. Attempting to build process");
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
                log(command);
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);

                try{
                    process = pb.start();
                    log("OmxPlayerProcess started. Waiting for process to finish");
                    process.waitFor();
                    log("OmxPlayerProcess finished");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
