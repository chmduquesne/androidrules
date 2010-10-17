package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;

public class NotifyResultOfActionAction extends Action {

    private Context mContext;

    public NotifyResultOfActionAction (Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        String toSend = intent.getStringExtra("result");
        Intent i = new Intent("ACTION_TALKMYPHONE_MESSAGE_TO_TRANSMIT");
        i.putExtra("message", toSend);
        mContext.sendBroadcast(i);
    }


}
