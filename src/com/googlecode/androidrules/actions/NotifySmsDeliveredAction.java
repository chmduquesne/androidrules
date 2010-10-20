package com.googlecode.androidrules.actions;

import com.googlecode.androidrules.BroadcastsHandlerService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NotifySmsDeliveredAction extends Action {

    private Context mContext;

    public NotifySmsDeliveredAction (Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        String res = "";
        switch (intent.getIntExtra("ResultCode", Activity.RESULT_OK))
        {
            case Activity.RESULT_OK:
                res = "SMS delivered";
                break;
            case Activity.RESULT_CANCELED:
                res = "SMS not delivered";
                break;
        }
        Intent i = new Intent(BroadcastsHandlerService.MESSAGE_TO_TRANSMIT);
        i.putExtra("message", res);
        mContext.sendBroadcast(i);
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        String [] res = {""};
        return res;
    }

    @Override
    public String[] getProvidedVariables() {
        String [] res = {""};
        return res;
    }
}
