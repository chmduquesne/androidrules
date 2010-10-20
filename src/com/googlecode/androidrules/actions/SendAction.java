package com.googlecode.androidrules.actions;

import com.googlecode.androidrules.BroadcastsHandlerService;

import android.content.Context;
import android.content.Intent;

public class SendAction extends Action {

    private Context mContext;

    public SendAction (Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        String toSend = intent.getStringExtra("message");
        Intent i = new Intent(BroadcastsHandlerService.MESSAGE_TO_TRANSMIT);
        i.putExtra("message", toSend);
        mContext.sendBroadcast(i);
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        String [] res = {"message"};
        return res;
    }

    @Override
    public String[] getProvidedVariables() {
        return null;
    }
}
