package com.googlecode.androidrules.actions;

import android.content.Intent;

public class StopRingingAction extends Action {

    @Override
    public void execute(Intent intent) {
        RingAction.destroyPreviousPlayer();
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        String [] res = {""};
        return res;
    }

    @Override
    public String[] getProvidedVariables() {
        String [] res = {""};
        return res;
    }
}
