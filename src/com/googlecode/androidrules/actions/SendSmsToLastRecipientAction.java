package com.googlecode.androidrules.actions;

import java.util.ArrayList;

import com.googlecode.androidrules.contacts.ContactsManager;
import com.googlecode.androidrules.contacts.Phone;
import com.googlecode.androidrules.sms.SmsMmsManager;

import android.content.Context;
import android.content.Intent;

public class SendSmsToLastRecipientAction extends Action {

    private Context mContext;

    public SendSmsToLastRecipientAction(Context context) {
        mContext = context;
    }

    /** sends a SMS to the specified contact */
    private void sendSMS(String message, String contact) {
        SmsMmsManager smsMmsManager = new SmsMmsManager(mContext);
        ContactsManager contactsManager = new ContactsManager(mContext);
        if (Phone.isCellPhoneNumber(contact)) {
            appendResult("Sending sms to " + contactsManager.getContactName(contact));
            smsMmsManager.sendSMSByPhoneNumber(message, contact);
        } else {
            ArrayList<Phone> mobilePhones = contactsManager.getMobilePhones(contact);
            if (mobilePhones.size() > 1) {
                appendResult("Specify more details:");

                for (Phone phone : mobilePhones) {
                    appendResult(phone.contactName + " - " + phone.cleanNumber);
                }
            } else if (mobilePhones.size() == 1) {
                Phone phone = mobilePhones.get(0);
                appendResult("Sending sms to " + phone.contactName + " (" + phone.cleanNumber + ")");
                smsMmsManager.sendSMSByPhoneNumber(message, phone.cleanNumber);
            } else {
                appendResult("No match for \"" + contact + "\"");
            }
        }
    }

    @Override
    public void execute(Intent intent) {
        if (SendOrReadSmsAction.lastRecipient == null) {
            appendResult("Error: no recipient registered.");
        } else {
            String args = intent.getStringExtra("args");
            sendSMS(args, SendOrReadSmsAction.lastRecipient);
        }
    }

}
