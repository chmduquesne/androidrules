package com.googlecode.androidrules.actions;

import android.content.Intent;

public class ActionsSequence extends Action {

    Action [] actions;

    public ActionsSequence(Action ... actions) {
        this.actions = actions;
    }

    @Override
    public void execute(Intent intent) {
        for (Action action : actions) {
            action.execute(intent);
            action.setPropagatedVariables(intent);
            action.clearVariables();
        }
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
