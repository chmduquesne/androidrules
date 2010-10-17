package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class OpenAction extends Action {

    private Context mContext;

    public OpenAction(Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        String command = intent.getStringExtra("command");
        String args = intent.getStringExtra("args");

        Intent target = new Intent(Intent.ACTION_VIEW, Uri.parse(command + ":" + args));
        Intent i = Intent.createChooser(target, "TalkMyPhone: choose an activity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

}
