package com.googlecode.androidrules.contacts;

public class Contact implements Comparable<Contact> {
    public Long id;
    public String name;

    public int compareTo(Contact another) {
        return name.compareTo(another.name);
    }
}
