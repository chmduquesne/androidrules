package com.googlecode.androidrules.actions;

import java.util.ArrayList;

import com.googlecode.androidrules.contacts.ContactsManager;
import com.googlecode.androidrules.contacts.Phone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DialAction extends Action {

    private Context mContext;
    private String statusMessage = "";

    public DialAction(Context context) {
        mContext = context;
    }

    private boolean dial(String number) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + number));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void execute(Intent intent) {
        String searchedText = intent.getStringExtra("args");

        String number = null;
        String contact = null;

        ContactsManager contactsManager = new ContactsManager(mContext);

        if (Phone.isCellPhoneNumber(searchedText)) {
            number = searchedText;
            contact = contactsManager.getContactName(number);
        } else {
            ArrayList<Phone> mobilePhones = contactsManager.getMobilePhones(searchedText);
            if (mobilePhones.size() > 1) {
                statusMessage += "Specify more details:";

                for (Phone phone : mobilePhones) {
                    statusMessage += phone.contactName + " - " + phone.cleanNumber;
                }
            } else if (mobilePhones.size() == 1) {
                Phone phone = mobilePhones.get(0);
                contact = phone.contactName;
                number = phone.cleanNumber;
            } else {
                statusMessage += "No match for \"" + searchedText + "\"";
            }
        }

        if( number != null) {
            statusMessage += "Dial " + contact + " (" + number + ")";
            if(!dial(number)) {
                statusMessage += "Error can't dial.";
            }
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
