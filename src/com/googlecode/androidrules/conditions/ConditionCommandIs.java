package com.googlecode.androidrules.conditions;

import android.content.Intent;

public class ConditionCommandIs extends Condition {

    private String command;

    public ConditionCommandIs(String command) {
        this.command = command;
    }

    @Override
    public boolean isTrue(Intent intent) {
        return command.equals(intent.getStringExtra("command"));
    }

}
