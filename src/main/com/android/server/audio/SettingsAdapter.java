package com.android.server.audio;

import android.content.ContentResolver;
import android.provider.Settings;
/* loaded from: classes.dex */
public class SettingsAdapter {
    public static SettingsAdapter getDefaultAdapter() {
        return new SettingsAdapter();
    }

    public int getGlobalInt(ContentResolver contentResolver, String str, int i) {
        return Settings.Global.getInt(contentResolver, str, i);
    }

    public String getGlobalString(ContentResolver contentResolver, String str) {
        return Settings.Global.getString(contentResolver, str);
    }

    public boolean putGlobalInt(ContentResolver contentResolver, String str, int i) {
        return Settings.Global.putInt(contentResolver, str, i);
    }

    public boolean putGlobalString(ContentResolver contentResolver, String str, String str2) {
        return Settings.Global.putString(contentResolver, str, str2);
    }

    public int getSystemIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
        return Settings.System.getIntForUser(contentResolver, str, i, i2);
    }

    public boolean putSystemIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
        return Settings.System.putIntForUser(contentResolver, str, i, i2);
    }

    public int getSecureIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
        return Settings.Secure.getIntForUser(contentResolver, str, i, i2);
    }

    public String getSecureStringForUser(ContentResolver contentResolver, String str, int i) {
        return Settings.Secure.getStringForUser(contentResolver, str, i);
    }

    public boolean putSecureIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
        return Settings.Secure.putIntForUser(contentResolver, str, i, i2);
    }

    public boolean putSecureStringForUser(ContentResolver contentResolver, String str, String str2, int i) {
        return Settings.Secure.putStringForUser(contentResolver, str, str2, i);
    }
}
