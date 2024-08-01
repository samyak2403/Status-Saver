package com.arrowwould.statussaver.photovideo.saveimages.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    public static final String WA_TREE_URI = "wa_tree_uri";
    public static final String WB_TREE_URI = "wb_tree_uri";
    private static SharedPreferences mPreferences;


    private static SharedPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = context.getApplicationContext()
                    .getSharedPreferences(Utils.SHAREDPREFERENCEFILENAME, Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static void setWATree(Context context, String value) {
        getInstance(context).edit().putString(WA_TREE_URI, value).apply();
    }

    public static String getWATree(Context context) {
        return getInstance(context).getString(WA_TREE_URI, "");
    }

    public static void setWBTree(Context context, String value) {
        getInstance(context).edit().putString(WB_TREE_URI, value).apply();
    }

    public static String getWBTree(Context context) {
        return getInstance(context).getString(WB_TREE_URI, "");
    }

}
