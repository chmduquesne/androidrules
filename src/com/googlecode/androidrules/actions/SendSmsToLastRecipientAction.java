package com.googlecode.androidrules.actions;

import java.util.ArrayList;

import com.googlecode.androidrules.contacts.ContactsManager;
import com.googlecode.androidrules.contacts.Phone;
import com.googlecode.androidrules.sms.SmsMmsManager;

import android.content.Context;
import android.content.Intent;

public class SendSmsToLastRecipientAction extends Action {

    private Context mContext;
    private String statusMessage = "";

    public SendSmsToLastRecipientAction(Context context) {
        mContext = context;
    }

    /** sends a SMS to the specified contact */
    private void sendSMS(String message, String contact) {
        SmsMmsManager smsMmsManager = new SmsMmsManager(mContext);
        ContactsManager contactsManager = new ContactsManager(mContext);
        if (Phone.isCellPhoneNumber(contact)) {
            statusMessage += "Sending sms to " + contactsManager.getContactName(contact);
            smsMmsManager.sendSMSByPhoneNumber(message, contact);
        } else {
            ArrayList<Phone> mobilePhones = contactsManager.getMobilePhones(contact);
            if (mobilePhones.size() > 1) {
                statusMessage += "Specify more details:";

                for (Phone phone : mobilePhones) {
                    statusMessage += phone.contactName + " - " + phone.cleanNumber;
                }
            } else if (mobilePhones.size() == 1) {
                Phone phone = mobilePhones.get(0);
                statusMessage += "Sending sms to " + phone.contactName + " (" + phone.cleanNumber + ")";
                smsMmsManager.sendSMSByPhoneNumber(message, phone.cleanNumber);
            } else {
                statusMessage += "No match for \"" + contact + "\"";
            }
        }
    }

    @Override
    public void execute(Intent intent) {
        if (SendOrReadSmsAction.lastRecipient == null) {
            statusMessage += "Error: no recipient registered.";
        } else {
            String args = intent.getStringExtra("args");
            sendSMS(args, SendOrReadSmsAction.lastRecipient);
        }
        setVariable("statusMessage", statusMessage);
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        String [] res = {""};
        return res;
    }

    @Override
    public String[] getProvidedVariables() {
        String [] res = {"statusMessage"};
        return res;
    }
}
