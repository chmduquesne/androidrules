package com.googlecode.androidrules.geo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class GeoPopup extends Activity {

    final String[] items = {"Maps", "Navigation", "Street View"};
    
    private boolean isAppInstalled(String uri) {
    	PackageManager pm = getPackageManager();
    	boolean installed = false;
    	try {
    		pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
    		installed = true;
    	} catch (PackageManager.NameNotFoundException e) {
    		installed = false;
    	}
    	return installed;
    }

    /** Called when the activity is first created. */
    // TODO: generate a dialog with only the installed apps.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            final String url = getIntent().getStringExtra("url");
            final Activity popup = this;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Geo App");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.cancel();

                    String intentUrl = "";
                    if (items[item].compareTo("Maps") == 0) {
                        intentUrl = "geo:" + url;
                    } else if (items[item].compareTo("Navigation") == 0) {
                        intentUrl = "google.navigation:" + url;
                    } else if (items[item].compareTo("Street View") == 0) {
                        intentUrl = "google.streetview:cbll=" + url;
                    }

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intentUrl));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(GeoPopup.this, "Activity not found", Toast.LENGTH_SHORT).show();
                    }

                    popup.finish();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    try {
                        dialog.cancel();
                        popup.finish();
                    } catch (Exception e) {
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
        }

    }
}
