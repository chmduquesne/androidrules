package com.googlecode.androidrules.actions;

import android.app.Activity;
import android.content.Intent;
import android.telephony.gsm.SmsManager;

public class NotifySmsSentAction extends Action {

    @Override
    public void execute(Intent intent) {
        String res = "";
        switch (intent.getIntExtra("ResultCode", Activity.RESULT_OK))
        {
            case Activity.RESULT_OK:
                res = "SMS sent";
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                res = "Generic failure";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                res = "No service";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                res = "Null PDU";
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                res = "Radio off";
                break;
        }
        appendResult(res);
    }

}
