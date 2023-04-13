package com.android.server.p013vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.ArraySet;
import java.util.Objects;
import java.util.Set;
/* renamed from: com.android.server.vr.SettingsObserver */
/* loaded from: classes2.dex */
public class SettingsObserver {
    public final ContentObserver mContentObserver;
    public final String mSecureSettingName;
    public final BroadcastReceiver mSettingRestoreReceiver;
    public final Set<SettingChangeListener> mSettingsListeners = new ArraySet();

    /* renamed from: com.android.server.vr.SettingsObserver$SettingChangeListener */
    /* loaded from: classes2.dex */
    public interface SettingChangeListener {
        void onSettingChanged();

        void onSettingRestored(String str, String str2, int i);
    }

    public SettingsObserver(Context context, Handler handler, final Uri uri, final String str) {
        this.mSecureSettingName = str;
        this.mSettingRestoreReceiver = new BroadcastReceiver() { // from class: com.android.server.vr.SettingsObserver.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.os.action.SETTING_RESTORED".equals(intent.getAction()) && Objects.equals(intent.getStringExtra("setting_name"), str)) {
                    SettingsObserver.this.sendSettingRestored(intent.getStringExtra("previous_value"), intent.getStringExtra("new_value"), getSendingUserId());
                }
            }
        };
        ContentObserver contentObserver = new ContentObserver(handler) { // from class: com.android.server.vr.SettingsObserver.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri2) {
                if (uri2 == null || uri.equals(uri2)) {
                    SettingsObserver.this.sendSettingChanged();
                }
            }
        };
        this.mContentObserver = contentObserver;
        context.getContentResolver().registerContentObserver(uri, false, contentObserver, -1);
    }

    public static SettingsObserver build(Context context, Handler handler, String str) {
        return new SettingsObserver(context, handler, Settings.Secure.getUriFor(str), str);
    }

    public void addListener(SettingChangeListener settingChangeListener) {
        this.mSettingsListeners.add(settingChangeListener);
    }

    public final void sendSettingChanged() {
        for (SettingChangeListener settingChangeListener : this.mSettingsListeners) {
            settingChangeListener.onSettingChanged();
        }
    }

    public final void sendSettingRestored(String str, String str2, int i) {
        for (SettingChangeListener settingChangeListener : this.mSettingsListeners) {
            settingChangeListener.onSettingRestored(str, str2, i);
        }
    }
}
