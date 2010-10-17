package com.googlecode.androidrules;

import java.util.ArrayList;


import com.googlecode.androidrules.actions.Action;
import com.googlecode.androidrules.actions.ActionsSequence;
import com.googlecode.androidrules.actions.CopyToClipBoardAction;
import com.googlecode.androidrules.actions.DialAction;
import com.googlecode.androidrules.actions.GeoAction;
import com.googlecode.androidrules.actions.NotifyBatteryStateAction;
import com.googlecode.androidrules.actions.NotifyCallAction;
import com.googlecode.androidrules.actions.NotifyMatchingContactsAction;
import com.googlecode.androidrules.actions.NotifyResultOfActionAction;
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

import android.app.Service;
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

    private Context mContext;
    private ArrayList<Rule> mRules = new ArrayList<Rule>();
    private static boolean isRunning = false;
    
    public static boolean isRunning() {
    	return isRunning;
    }

    public void destroy() {
        for(Rule rule : mRules) {
            rule.enable(false);
        }
        mRules = null;
    }

    public void addRule(IntentFilter filteredEvent, Condition condition, Action action, String settingsName) {
        Rule rule =  new Rule(mContext, filteredEvent, condition, action, settingsName);
        mRules.add(rule);
    }

    public void updateRulesFromSettings() {
        for(Rule rule : mRules) {
            rule.updateFromSettings();
        }
    }
    
	@Override
    public void onCreate(){

		isRunning = true;
        mContext = getApplicationContext();

        addRule(new IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                null,
                new NotifyBatteryStateAction(mContext),
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
                new SendAction(mContext, builder.toString()),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("ring"),
                new RingAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("stop"),
                new ActionsSequence(
                        new SendAction(mContext, "Stopping ongoing actions"),
                        new StopRingingAction(),
                        new StopLocatingPhoneAction(mContext)
                        ),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("copy"),
                new CopyToClipBoardAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("contact"),
                new NotifyMatchingContactsAction(mContext),
                null);

        addRule(new IntentFilter("SMS_SENT"),
                null,
                new NotifySmsSentAction(),
                "notifySmsSent");

        addRule(new IntentFilter("SMS_DELIVERED"),
                null,
                new NotifySmsDeliveredAction(mContext),
                "notifySmsDelivered");

        addRule(new IntentFilter("android.provider.Telephony.SMS_RECEIVED"),
                null,
                new NotifySmsReceivedAction(mContext),
                "notifyIncomingSms");

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("dial"),
                new DialAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("http"),
                new OpenAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("https"),
                new OpenAction(mContext),
                null);

        addRule(new IntentFilter("TALKMYPHONE_RESULT_OF_ACTION"),
                null,
                new NotifyResultOfActionAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("sms"),
                new SendOrReadSmsAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("reply"),
                new SendSmsToLastRecipientAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("where"),
                new StartLocatingPhoneAction(mContext),
                null);

        addRule(new IntentFilter(USER_COMMAND_RECEIVED),
                new ConditionCommandIs("geo"),
                new GeoAction(mContext),
                null);

        addRule(new IntentFilter("android.intent.action.PHONE_STATE"),
                null,
                new NotifyCallAction(mContext),
                "notifyIncomingCalls");

        updateRulesFromSettings();

    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
    public void onDestroy() {
        isRunning = false;
        destroy();
    }
}
