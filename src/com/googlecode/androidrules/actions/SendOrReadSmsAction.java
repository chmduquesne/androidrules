package com.googlecode.androidrules.actions;

import java.util.ArrayList;
import java.util.Collections;

import com.googlecode.androidrules.contacts.Contact;
import com.googlecode.androidrules.contacts.ContactsManager;
import com.googlecode.androidrules.contacts.Phone;
import com.googlecode.androidrules.sms.Sms;
import com.googlecode.androidrules.sms.SmsMmsManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SendOrReadSmsAction extends Action {

    private Context mContext;
    public static String lastRecipient;

    public void setLastRecipient(String phoneNumber) {
        if (lastRecipient == null || !phoneNumber.equals(lastRecipient)) {
            lastRecipient = phoneNumber;
            displayLastRecipient(phoneNumber);
        }
    }

    public void displayLastRecipient(String phoneNumber) {
        if (phoneNumber == null) {
            appendResult("Reply contact is not set");
        } else {
            ContactsManager contactsManager = new ContactsManager(mContext);
            String contact = contactsManager.getContactName(phoneNumber);
            if (Phone.isCellPhoneNumber(phoneNumber) && contact.compareTo(phoneNumber) != 0){
                contact += " (" + phoneNumber + ")";
            }
            appendResult("Reply contact is now " + contact);
        }
    }

    public SendOrReadSmsAction(Context context) {
        mContext = context;
    }

    private String makeBold(String s) {
        SharedPreferences prefs = mContext.getSharedPreferences("TalkMyPhone", 0);
        boolean formatChatResponses = prefs.getBoolean("formatResponses", false);
        String res = s;
        if (formatChatResponses) {
           res = " *" + s + "* ";
        }
        return res;
    }

    private String makeItalic(String s) {
        SharedPreferences prefs = mContext.getSharedPreferences("TalkMyPhone", 0);
        boolean formatChatResponses = prefs.getBoolean("formatResponses", false);
        String res = s;
        if (formatChatResponses) {
           res = " _" + s + "_ ";
        }
        return res;
    }

    /** reads (count) SMS from all contacts matching pattern */
    private void readSMS(String searchedText) {
        ContactsManager contactsManager = new ContactsManager(mContext);
        ArrayList<Contact> contacts = contactsManager.getMatchingContacts(searchedText);
        ArrayList<Sms> sentSms = new ArrayList<Sms>();

        SharedPreferences prefs = mContext.getSharedPreferences("TalkMyPhone", 0);
        boolean displaySentSms = prefs.getBoolean("showSentSms", false);
        int smsNumber = prefs.getInt("smsNumber", 5);

        SmsMmsManager smsMmsManager = new SmsMmsManager(mContext);
        if(displaySentSms) {
            sentSms = smsMmsManager.getAllSentSms();
        }

        if (contacts.size() > 0) {
            StringBuilder noSms = new StringBuilder();
            Boolean hasMatch = false;
            for (Contact contact : contacts) {
                ArrayList<Sms> smsList = smsMmsManager.getSms(contact.id, contact.name);
                if(displaySentSms) {
                    smsList.addAll(smsMmsManager.getSentSms(contactsManager.getPhones(contact.id),sentSms));
                    Collections.sort(smsList);
                }

                smsList.subList(Math.max(smsList.size() - smsNumber,0), smsList.size());
                if (smsList.size() > 0) {
                    hasMatch = true;
                    StringBuilder smsContact = new StringBuilder();
                    smsContact.append(makeBold(contact.name));
                    for (Sms sms : smsList) {
                        smsContact.append("\r\n" + makeItalic(sms.date.toLocaleString() + " - " + sms.sender));
                        smsContact.append("\r\n" + sms.message);
                    }
                    if (smsList.size() < smsNumber) {
                        smsContact.append("\r\n" + makeItalic("Only got " + smsList.size() + " sms"));
                    }
                    appendResult(smsContact.toString() + "\r\n");
                } else {
                    noSms.append(contact.name + " - No sms found\r\n");
                }
            }
            if (!hasMatch) {
                appendResult(noSms.toString());
            }
        } else {
            appendResult("No match for \"" + searchedText + "\"");
        }
    }

    /** sends a SMS to the specified contact */
    public void sendSMS(String message, String contact) {
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
        String args  = intent.getStringExtra("args");
        int separatorPos = args.indexOf(":");
        String contact = null;
        String message = null;
        if (-1 != separatorPos) {
            contact = args.substring(0, separatorPos);
            setLastRecipient(contact);
            message = args.substring(separatorPos + 1);
            sendSMS(message, contact);
        } else if (args.length() > 0) {
            contact = args;
            readSMS(contact);
        } else {
            displayLastRecipient(lastRecipient);
        }
    }

}
