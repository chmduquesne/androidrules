package com.googlecode.androidrules;

import java.util.ArrayList;
import java.util.List;


import com.googlecode.androidrules.actions.Action;
import com.googlecode.androidrules.actions.AssignLocalVariableAction;
import com.googlecode.androidrules.actions.ActionsSequence;
import com.googlecode.androidrules.actions.CopyToClipBoardAction;
import com.googlecode.androidrules.actions.DialAction;
import com.googlecode.androidrules.actions.GeoAction;
import com.googlecode.androidrules.actions.NotifyBatteryStateAction;
import com.googlecode.androidrules.actions.NotifyCallAction;
import com.googlecode.androidrules.actions.NotifyMatchingContactsAction;
import com.googlecode.androidrules.actions.NotifySmsDeliveredAction;
import com.googlecode.androidrules.actions.NotifySmsReceivedAction;
import com.googlecode.androidrules.actions.NotifySmsSentAction;
import com.googlecode.androidrules.actions.OpenAction;
import com.googlecode.androidrules.actions.RingAction;
import com.googlecode.androidrules.actions.SendAction;
import com.googlecode.androidrules.actions.SendOrReadSmsAction;
import com.googlecode.androidrules.actions.SendSmsToLastRecipientAction;
import com.googlecode.androidrules.actions.StartLocatingPhoneAction;
import com.googlecode.androidrules.actions.StopLocatingPhoneAction;
import com.googlecode.androidrules.actions.StopRingingAction;
import com.googlecode.androidrules.conditions.Condition;
import com.googlecode.androidrules.conditions.ConditionCommandIs;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * This class monitors phone events and user actions and triggers actions on events when conditions are met
 */
public class BroadcastsHandlerService extends Service {

    // Intent broadcasted to the system when the user sends a command to talkmyphone via jabber
    public final static String USER_COMMAND_RECEIVED = "com.googlecode.talkmyphone.USER_COMMAND_RECEIVED";

    // Intent broadcasted by the system when it wants to send a message to the user via talkmyphone
    public final static String MESSAGE_TO_TRANSMIT = "com.googlecode.talkmyphone.MESSAGE_TO_TRANSMIT";

    // Rules
    private ArrayList<Rule> mRules = new ArrayList<Rule>();

    // Checks if the service is running
    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
        List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo serviceInfo : services) {
            ComponentName componentName = serviceInfo.service;
            String serviceName = componentName.getClassName();
            if (serviceName.equals(BroadcastsHandlerService.class.getName())) {
                return true;
            }
        }

        return false;
    }

    public void addRule(IntentFilter filteredEvent, Condition condition, Action action, String settingsName) {
        Rule rule =  new Rule(getApplicationContext(), filteredEvent, condition, action, settingsName);
        mRules.add(rule);
    }

    public void updateRulesFromSettings() {
        for(Rule rule : mRules) {
            rule.updateFromSettings();
        }
    }

    @Override
    public void onCreate(){

        Context context = getApplicationContext();

        addRule(new IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                null,
                new NotifyBatteryStateAction(context),
                "notifyBattery");

        StringBuilder builder = new StringBuilder();
        builder.append("Available commands:\n");
        builder.append("- \"?\": shows this help.\n");
        builder.append("- \"dial:#contact#\": dial the specified contact.\n");
        builder.append("- \"reply:#message#\": send a sms to your last recipient with content message.\n");
        builder.append("- \"sms:#contact#[:#message#]\": sends a sms to number with content message or display last sent sms.\n");
        builder.append("- \"contact:#contact#\": display informations of a searched contact.\n");
        builder.append("- \"geo:#address#\": Open Maps or Navigation or Street view on specific address\n");
        builder.append("- \"where\": sends you google map updates about the location of the phone until you send \"stop\"\n");
        builder.append("- \"ring\": rings the phone until you send \"stop\"\n");
        builder.append("- \"copy:#text#\": copy text to clipboard\n");
        builder.append("and you can paste links and open it with the appropriate app\n");
        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("?"),
                new ActionsSequence(
                        new AssignLocalVariableAction("message", builder.toString()),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("ring"),
                new ActionsSequence(
                        new RingAction(context).setPropagate("statusMessage", "message"),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("stop"),
                new ActionsSequence(
                        new AssignLocalVariableAction("message", "Stopping ongoing actions"),
                        new SendAction(context),
                        new StopRingingAction(),
                        new StopLocatingPhoneAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("copy"),
                new ActionsSequence(
                        new CopyToClipBoardAction(context).setPropagate("statusMessage", "message"),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("contact"),
                new NotifyMatchingContactsAction(context),
                null);

        addRule(new IntentFilter("SMS_SENT"),
                null,
                new NotifySmsSentAction(),
                "notifySmsSent");

        addRule(new IntentFilter("SMS_DELIVERED"),
                null,
                new NotifySmsDeliveredAction(context),
                "notifySmsDelivered");

        addRule(new IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
                null,
                new NotifySmsReceivedAction(context),
                "notifyIncomingSms");

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("dial"),
                new ActionsSequence(
                        new DialAction(context).setPropagate("statusMessage", "message"),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("http"),
                new OpenAction(context),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("https"),
                new OpenAction(context),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("sms"),
                new ActionsSequence(
                        new SendOrReadSmsAction(context).setPropagate("statusMessage", "message"),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("reply"),
                new ActionsSequence(
                        new SendSmsToLastRecipientAction(context).setPropagate("statusMessage", "message"),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("where"),
                new StartLocatingPhoneAction(context),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("geo"),
                new ActionsSequence(
                        new GeoAction(context).setPropagate("statusMessage", "message"),
                        new SendAction(context)
                        ),
                null);

        addRule(new IntentFilter("android.intent.action.PHONE_STATE"),
                null,
                new NotifyCallAction(context),
                "notifyIncomingCalls");

        updateRulesFromSettings();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        for(Rule rule : mRules) {
            rule.enable(false);
        }
        mRules = null;
    }
}
