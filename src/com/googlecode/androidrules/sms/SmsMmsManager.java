package com.googlecode.androidrules.sms;

import java.util.ArrayList;
import java.util.Date;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.gsm.SmsManager;

import com.googlecode.androidrules.Tools;
import com.googlecode.androidrules.contacts.Phone;

public class SmsMmsManager {

    private Context mContext;

    public SmsMmsManager(Context context) {
        mContext = context;
    }
    // intents for sms sending
    private PendingIntent sentPI = PendingIntent.getBroadcast(mContext, 0,
            new Intent("SMS_SENT"), 0);
    private PendingIntent deliveredPI = PendingIntent.getBroadcast(mContext, 0,
            new Intent("SMS_DELIVERED"), 0);

    /** Sends a sms to the specified phone number */
    public void sendSMSByPhoneNumber(String message, String phoneNumber) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> messages = sms.divideMessage(message);
        for (int i=0; i < messages.size(); i++) {
            sms.sendTextMessage(phoneNumber, null, messages.get(i), sentPI, deliveredPI);
            addSmsToSentBox(message, phoneNumber);
        }
    }

    /**
     * Returns a ArrayList of <Sms> with count sms where the contactId match the argument
     */
    public ArrayList<Sms> getSms(Long contactId, String contactName) {
        ArrayList<Sms> res = new ArrayList<Sms>();

        if(null != contactId) {
            Uri mSmsQueryUri = Uri.parse("content://sms/inbox");
            String columns[] = new String[] { "person", "address", "body", "date", "status"};
            Cursor c = mContext.getContentResolver().query(mSmsQueryUri, columns, "person = " + contactId, null, null);

            if (c.getCount() > 0) {
                for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) {
                    Date date = new Date();
                    date.setTime(Long.parseLong(Tools.getString(c ,"date")));
                    Sms sms = new Sms();
                    sms.date = date;
                    sms.number = Tools.getString(c ,"address");
                    sms.message = Tools.getString(c ,"body");
                    sms.sender = contactName;
                    res.add( sms );
                }
            }
            c.close();
        }
        return res;
    }

    /**
     * Returns a ArrayList of <Sms> with count sms where the contactId match the argument
     */
    public ArrayList<Sms> getAllSentSms() {
        ArrayList<Sms> res = new ArrayList<Sms>();

        Uri mSmsQueryUri = Uri.parse("content://sms/sent");
        String columns[] = new String[] { "address", "body", "date", "status"};
        Cursor c = mContext.getContentResolver().query(mSmsQueryUri, columns, null, null, null);

        if (c.getCount() > 0) {
            for (boolean hasData = c.moveToFirst() ; hasData ; hasData = c.moveToNext()) {
                Date date = new Date();
                date.setTime(Long.parseLong(Tools.getString(c ,"date")));
                Sms sms = new Sms();
                sms.date = date;
                sms.number = Tools.getString(c ,"address");
                sms.message = Tools.getString(c ,"body");
                sms.sender = "Me";
                res.add( sms );

            }
        }
        c.close();

        return res;
    }

    /**
     * Returns a ArrayList of <Sms> with count sms where the contactId match the argument
     */
    public ArrayList<Sms> getSentSms(ArrayList<Phone> phones, ArrayList<Sms> sms) {
        ArrayList<Sms> res = new ArrayList<Sms>();

        for (Sms aSms : sms) {
            Boolean phoneMatch = false;

            for (Phone phone : phones) {
                if (phone.phoneMatch(aSms.number)) {
                    phoneMatch = true;
                    break;
                }
            }

            if (phoneMatch) {
                res.add( aSms );
            }
        }

        return res;
    }

    /** Adds the text of the message to the sent box */
    public void addSmsToSentBox(String message, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("date", System.currentTimeMillis());
        values.put("body", message);
        mContext.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }
}
