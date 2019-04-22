package com.xvoice.postdemo;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gongyuZhou on 2019/4/20.
 */

public class AudioPlayerUtils {

    private MediaPlayer mediaPlayer;
    /*-1:初始状态，0:播放完毕，1播放中*/
    public static int playStatus = -1;
    File tempFile = null;

    private AudioPlayerUtils() {
    }

    private static AudioPlayerUtils poolUtils = null;
    public static AudioPlayerUtils newInstance() {
        if (poolUtils == null) {
            poolUtils = new AudioPlayerUtils();
        }
        poolUtils.init();
        return poolUtils;
    }

    /*
     * 初始化mediaPlayer
     * */
    private void init() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
    }



    /**
     * 播放base64数据类型的声音数据
     *
     * @param base64Str
     */
    public void playBase64( String base64Str) {
        try {
            tempFile=base64ToFile(base64Str);
//            tempFile = base64ToFile(encodeBase64File(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (playStatus) {
            case 1:
                mediaPlayer.stop();
                mediaPlayer.reset();
                break;
            case 0:
                mediaPlayer.reset();
                break;
            case -1:
                break;
        }
        playStatus = 1;
        try {
            mediaPlayer.setDataSource(tempFile.getPath());
//            mediaPlayer.setDataSource(context, Uri.parse
//                    ("android.resource://" + "com.mdks.doctor/" + raw));
            //如果是setDataSource，那么调用完这个文件之后，音频文件没有真正的加载
            //要调用prepare方法
            //异步的加载方式
            mediaPlayer.prepareAsync();
            //设置循环播放
            mediaPlayer.setLooping(false);
            //加载完成时播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    System.out.println("Prepared----完成");
                    mediaPlayer.start();
                }
            });
            //播放完成时调用
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    System.out.println("Completion----播放完毕");
                    mediaPlayer.reset();
                    playStatus = 0;
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将base64字符转换成临时音乐文件
     */
    private File base64ToFile(String base64Str) {
        FileOutputStream outputStream = null;
        if (tempFile == null || !tempFile.exists())
            try {
                tempFile = File.createTempFile("temp", ".mp3");
                byte[] audioByte = Base64.decode(base64Str, Base64.DEFAULT);
                if (tempFile != null) {
                    outputStream = new FileOutputStream(tempFile);
                    outputStream.write(audioByte, 0, audioByte.length);
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return tempFile;
    }

    /**
     * 停止播放，在activity不可见时，停止播放
     */
    public void stop() {
        if (playStatus == 1 && mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

