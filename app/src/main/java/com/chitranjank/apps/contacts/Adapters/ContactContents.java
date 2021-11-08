package com.chitranjank.apps.contacts.Adapters;

public class ContactContents {
    String ID, pName, pNumber;
    String type,date,duration;

    public ContactContents() {
    }

    public ContactContents(String ID, String pName, String pNumber, String type, String date, String duration) {
        this.ID = ID;
        this.pName = pName;
        this.pNumber = pNumber;
        this.type = type;
        this.date = date;
        this.duration = duration;
    }

    public String getID() {
        return ID;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getDuration() {
        return duration;
    }

    public String getpName() {
        return pName;
    }

    public String getpNumber() {
        return pNumber;
    }
}
