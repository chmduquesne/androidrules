package com.googlecode.androidrules.actions;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.util.Log;

public abstract class Action {

    // map of the variables set during the action (has to be cleared after the necessary variables have been propagated)
    private Map<String, String> providedVariables = new HashMap<String, String>();

    // map of the names to propagate the variable as
    private Map<String, String> variablesToPropagate = new HashMap<String, String>();

    /*
     * Here you should the code of the action. It can use:
     * - global variables, related to the current state of the application
     * - local variables, passed through the intent with methods such as putExtra.
     */

    public abstract void execute(Intent intent);

    /*
     *  Here you should list all the extra parameters the action expects to find in the intent argument of its execute method
     */
    public abstract String [] getExpectedIntentExtraParameters();


    /*
     *  Here you should declare all the variables your action locally sets
     */
    public abstract String [] getProvidedVariables();


    /*
     * Checks if the variable has been listed as provided
     * @param variable variable name to check
     */
    private boolean isListedAsProvided(String variable) {
        for (String var : getProvidedVariables()) {
            if (variable.equals(var)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Sets the value of the variable
     * @param variable name of the variable
     * @param value value to set
     */
    public void setVariable(String variable, String value) {
        if (!isListedAsProvided(variable)) {
            throw new Error("Tried to set an unlisted variable");
        }
        providedVariables.put(variable, value);
        Log.d("androidrules", this + " " + providedVariables.toString());

    }

    /*
     * Sets the content of @variable to be propagated as @propagateName
     * @param name name of the variable
     * @param propagateName name to propagate with
     */
    public Action setPropagate(String variable, String propagateName) {
        if (!isListedAsProvided(variable)) {
            throw new Error("Tried to propagate an unlisted variable");
        }
        variablesToPropagate.put(variable, propagateName);
        Log.d("androidrules", this + " " + variablesToPropagate.toString());
        return this;
    }

    /*
     * Actually propagates the variables
     * @param intent Intent to decorate
     */
    public void setPropagatedVariables(Intent intent) {
        Log.d("androidrules", this + " propagating variables");
        for (String key: variablesToPropagate.keySet()) {
            String propagateName = variablesToPropagate.get(key);
            String propagateValue = providedVariables.get(key);
            Log.d("androidrules", "propagating " + key + " as " + propagateName );
            intent.putExtra(propagateName, propagateValue);
        }
    }

    /*
     * Clears the variables set by the action
     */
    public void clearVariables() {
        Log.d("androidrules", this + " " + "clearing variables");
        providedVariables = new HashMap<String, String>();
    }

}
