package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;

public class RingAction extends Action {

    private static MediaPlayer mMediaPlayer;
    private Context mContext;
    private String statusMessage = "";

    public RingAction(Context context) {
        mContext = context;
    }

    /** Retrieves the ringtone from the options
     * @param context Context to get the preferences from
     */
    private String getRingtoneFromPreferences() {
        String res = "";
        SharedPreferences prefs = mContext.getSharedPreferences("TalkMyPhone", 0);
        String ringtone = prefs.getString("ringtone", "");
        if (ringtone.equals("")) {
            ringtone = Settings.System.DEFAULT_RINGTONE_URI.toString();
        }
        return res;
    }

    /** builds a new player
     *
     * @param context context to build the player in
     * @param ringtone ringtone to set in the player
     */
    private void buildNewPlayer(String ringtone) {
        Uri ringtoneUri = Uri.parse(ringtone);
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mContext, ringtoneUri);
        } catch (Exception e) {
            statusMessage += e.toString();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mMediaPlayer.setLooping(true);
    }

    /**
     * Destroys any previous instance of the player
     */
    public static void destroyPreviousPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
    }

    private void play() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
            try {
                mMediaPlayer.prepare();
            } catch (Exception e) {
                statusMessage += e.toString();
            }
            mMediaPlayer.start();
        }
    }

    @Override
    public void execute(Intent intent) {
        // first, destroy previous player
        destroyPreviousPlayer();

        // Get the ringtone
        String ringtone = getRingtoneFromPreferences();
        if (ringtone.equals("")) {
            statusMessage += "Unable to ring, change the ringtone in the options";
        } else {
            statusMessage += "Ringing phone";
            buildNewPlayer(ringtone);
            play();
        }
        setVariable("statusMessage", statusMessage);
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        String [] res = {""};
        return res;
    }

    @Override
    public String[] getProvidedVariables() {
        String [] res = {"statusMessage"};
        return res;
    }
}
