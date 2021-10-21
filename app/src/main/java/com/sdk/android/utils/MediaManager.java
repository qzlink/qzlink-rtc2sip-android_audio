package com.sdk.android.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import com.sdk.android.Constants;
import com.sdk.android.R;

public class MediaManager {

    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    /**
     * 播放音乐
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(final String filePath, final OnCompletionListener onCompletionListener) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {

        }
    }

    public static void playSound(Context context, int rawId) {
        try {
            final MediaPlayer mediaPlayer = MediaPlayer.create(context, rawId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 暂停播放
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public static void playSound(Context context, String num) {
        int rawId = -1;
        if (Constants.ZERO.equals(num)) {
            rawId = R.raw.dtmf_0;
        } else if (Constants.ONE.equals(num)) {
            rawId = R.raw.dtmf_1;
        } else if (Constants.TWO.equals(num)) {
            rawId = R.raw.dtmf_2;
        } else if (Constants.THREE.equals(num)) {
            rawId = R.raw.dtmf_3;
        } else if (Constants.FOUR.equals(num)) {
            rawId = R.raw.dtmf_4;
        } else if (Constants.FIVE.equals(num)) {
            rawId = R.raw.dtmf_5;
        } else if (Constants.SIX.equals(num)) {
            rawId = R.raw.dtmf_6;
        } else if (Constants.SEVEN.equals(num)) {
            rawId = R.raw.dtmf_7;
        } else if (Constants.EIGHT.equals(num)) {
            rawId = R.raw.dtmf_8;
        } else if (Constants.NINE.equals(num)) {
            rawId = R.raw.dtmf_9;
        } else if (Constants.STAR.equals(num)) {
            rawId = R.raw.dtmf_star;
        } else if (Constants.JING.equals(num)) {
            rawId = R.raw.dtmf_pound;
        }
        MediaManager.playSound(context, rawId);
    }

}
