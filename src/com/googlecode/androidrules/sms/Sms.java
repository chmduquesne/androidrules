package com.googlecode.androidrules.sms;

import java.util.Date;

public class Sms implements Comparable<Sms> {
    public String message;
    public String number;
    public String sender;
    public Date date;

    public int compareTo(Sms another) {
        return date.compareTo(another.date);
    }
}
