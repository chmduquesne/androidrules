package com.googlecode.androidrules.actions;

import android.content.Intent;

public class AssignLocalVariableAction extends Action {

    private String variable;
    private String value;

    public AssignLocalVariableAction(String variable, String value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public void execute(Intent intent) {
        intent.putExtra(variable, value);
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        return null;
    }

    @Override
    public String[] getProvidedVariables() {
        return null;
    }

}
