package com.googlecode.androidrules.geo;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;


public class GeoManager {
	
	private Context mContext;
	
	public GeoManager(Context context) {
		mContext = context;
	}

    /** Return List of <Address> from searched location */
    public List<Address> geoDecode(String searchedLocation) {
        try {
            Geocoder geo = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geo.getFromLocationName(searchedLocation, 10);
            if (addresses != null && addresses.size() > 0) {
               return addresses;
            }
        }
        catch(Exception ex) {
        }

        return null;
    }

    /** launches an activity on the url */
    public void launchExternal(String url) {
        Intent popup = new Intent(mContext, GeoPopup.class);
        popup.putExtra("url", url);
        popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(popup);
    }
}
