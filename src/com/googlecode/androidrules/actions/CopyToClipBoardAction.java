package com.googlecode.androidrules.actions;

import android.content.Context;
import android.content.Intent;
import android.text.ClipboardManager;

public class CopyToClipBoardAction extends Action {

    private Context mContext;

    public CopyToClipBoardAction(Context context) {
        mContext = context;
    }

    @Override
    public void execute(Intent intent) {
        String text = intent.getStringExtra("args");
        try {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
            setVariable("statusMessage", "Text copied");
        }
        catch(Exception ex) {
            setVariable("statusMessage", "Clipboard access failed");
        }
    }

    @Override
    public String[] getExpectedIntentExtraParameters() {
        String [] res = {"args"};
        return res;
    }

    @Override
    public String[] getProvidedVariables() {
        String [] res = {"statusMessage"};
        return res;
    }
}
