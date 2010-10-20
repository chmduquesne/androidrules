package com.googlecode.androidrules.actions;

import com.googlecode.androidrules.LocationService;
import android.content.Context;
import android.content.Intent;

public class StartLocatingPhoneAction extends Action {

    private Context mContext;

    public StartLocatingPhoneAction (Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        Intent i = new Intent(mContext, LocationService.class);
        i.setAction(LocationService.START_SERVICE);
        mContext.startService(i);
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
