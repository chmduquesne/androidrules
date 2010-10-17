package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;

public class SendAction extends Action {

    private Context mContext;
    private String toSend;

    public SendAction (Context context, String toSend) {
        this.toSend = toSend;
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        Intent i = new Intent("ACTION_TALKMYPHONE_MESSAGE_TO_TRANSMIT");
        i.putExtra("message", toSend);
        mContext.sendBroadcast(i);
    }

}
