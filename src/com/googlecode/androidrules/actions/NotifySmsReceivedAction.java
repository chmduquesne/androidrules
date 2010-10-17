package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

import com.googlecode.androidrules.contacts.ContactsManager;

public class NotifySmsReceivedAction extends Action {

    private Context mContext;

    public NotifySmsReceivedAction(Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        if (bundle != null)
        {
            ContactsManager contactsManager = new ContactsManager(mContext);
            StringBuilder builder = new StringBuilder();
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i<msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                builder.append("SMS from ");
                builder.append(contactsManager.getContactName(msgs[i].getOriginatingAddress()));
                builder.append(": ");
                builder.append(msgs[i].getMessageBody().toString());
                builder.append("\n");
                // TODO : Fix that
                //service.setLastRecipient(msgs[i].getOriginatingAddress());
            }
            Intent i = new Intent("ACTION_TALKMYPHONE_MESSAGE_TO_TRANSMIT");
            i.putExtra("message", builder.toString());
            mContext.sendBroadcast(i);
        }
    }

}
