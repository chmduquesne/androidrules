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
        }
    }

}
