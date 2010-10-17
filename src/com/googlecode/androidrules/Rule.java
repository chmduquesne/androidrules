package com.googlecode.androidrules;

import com.googlecode.androidrules.actions.Action;
import com.googlecode.androidrules.conditions.Condition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

/**
 * This class stands for a rule. The action is executed when the filtered event is met if the condition is true.
 */
public class Rule {

    private Context mContext;
    private IntentFilter mIntentFilter;
    private Condition mCondition;
    private Action mAction;
    private String settingsName;
    private BroadcastReceiver mBroadcastReceiver;

    /**
     * Constructor
     * @param context context of the application
     * @param filteredEvent event that is monitored by the rule
     * @param condition condition that must be met to execute the action
     * @param action action to execute
     * @param settingsName settings that must be activated to enable the rule
     */
    public Rule(Context context, IntentFilter filteredEvent, Condition condition, Action action, String settingsName) {
        mContext = context;
        mIntentFilter = filteredEvent;
        mCondition = condition;
        mAction = action;
        this.settingsName = settingsName;
    }

    public void enable(boolean shouldEnable) {
        if (shouldEnable) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    intent.putExtra("ResultCode", getResultCode());
                    if (mCondition == null || mCondition.isTrue(intent)){
                        mAction.execute(intent);
                        String res = mAction.getResult();
                        if (res != null) {
                            Intent i = new Intent("TALKMYPHONE_RESULT_OF_ACTION");
                            i.putExtra("result", res);
                            mContext.sendBroadcast(i);
                        }
                    }
                }
            };
            mContext.registerReceiver(mBroadcastReceiver, mIntentFilter);
        } else {
            if (mBroadcastReceiver != null) {
                mContext.unregisterReceiver(mBroadcastReceiver);
            }
            mBroadcastReceiver = null;
        }
    }

    public void updateFromSettings() {
        // if there is no option in the settings to toggle the rule, default is to enable it
        boolean shouldEnable = true;
        if (settingsName != null) {
            SharedPreferences prefs = mContext.getSharedPreferences("TalkMyPhone", 0);
            shouldEnable = prefs.getBoolean(settingsName, true);
        }
        enable(shouldEnable);
    }
}
