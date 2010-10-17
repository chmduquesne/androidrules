package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;

public class NotifyBatteryStateAction extends Action {

    private Context mContext;
    private int lastPercentageNotified;

    public NotifyBatteryStateAction (Context context) {
        mContext = context;
        lastPercentageNotified = -1;
    }

    @Override
    public void execute(Intent intent) {
        int level = intent.getIntExtra("level", 0);
        if (lastPercentageNotified == -1) {
            notifyAndSavePercentage(level);
        } else {
            if (level != lastPercentageNotified && level % 5 == 0) {
                notifyAndSavePercentage(level);
            }
        }
    }
    private void notifyAndSavePercentage(int level) {
        lastPercentageNotified = level;
        Intent i = new Intent("ACTION_TALKMYPHONE_MESSAGE_TO_TRANSMIT");
        i.putExtra("message", "Battery level " + level + "%");
        mContext.sendBroadcast(i);
    }
}
