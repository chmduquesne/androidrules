package com.googlecode.androidrules.actions;

import com.googlecode.androidrules.PhoneCallListener;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class NotifyCallAction extends Action {

    private Context mContext;

    public NotifyCallAction(Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        PhoneStateListener phoneListener = new PhoneCallListener(mContext);
        TelephonyManager telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

}
