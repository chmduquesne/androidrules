package com.googlecode.androidrules.actions;

import android.content.Intent;

public abstract class Action {

    private String result;

    public abstract void execute(Intent intent);

    public String getResult() {
        return result;
    }

    public void appendResult(String res) {
        if (result == null) {
            result = res;
        } else {
            result += res;
        }
    }

}
