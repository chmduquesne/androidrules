package com.googlecode.androidrules;

import com.googlecode.androidrules.contacts.ContactsManager;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneCallListener extends PhoneStateListener {

    private Context mContext;

    public PhoneCallListener(Context context) {
        mContext = context;
    }

    public void onCallStateChanged(int state,String incomingNumber) {
        switch(state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                ContactsManager contactsManager = new ContactsManager(mContext);
                String contact = contactsManager.getContactName(incomingNumber);
                Intent i = new Intent(BroadcastsHandlerService.MESSAGE_TO_TRANSMIT);
                i.putExtra("message", contact + "is calling");
                mContext.sendBroadcast(i);
                break;
        }
    }
}
