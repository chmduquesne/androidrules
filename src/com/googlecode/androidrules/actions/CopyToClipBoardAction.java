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
            appendResult("Text copied");
        }
        catch(Exception ex) {
            appendResult("Clipboard access failed");
        }
    }

}
