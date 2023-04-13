package com.android.internal.telephony.util;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
/* loaded from: classes.dex */
public class VoicemailNotificationSettingsUtil {
    public static void setVibrationEnabled(Context context, boolean z) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(getVoicemailVibrationSharedPrefsKey(), z);
        edit.commit();
    }

    public static boolean isVibrationEnabled(Context context) {
        NotificationChannel channel = NotificationChannelController.getChannel(NotificationChannelController.CHANNEL_ID_VOICE_MAIL, context);
        return channel != null ? channel.shouldVibrate() : getVibrationPreference(context);
    }

    public static boolean getVibrationPreference(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        migrateVoicemailVibrationSettingsIfNeeded(context, defaultSharedPreferences);
        return defaultSharedPreferences.getBoolean(getVoicemailVibrationSharedPrefsKey(), false);
    }

    public static void setRingtoneUri(Context context, Uri uri) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uri2 = uri != null ? uri.toString() : PhoneConfigurationManager.SSSS;
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(getVoicemailRingtoneSharedPrefsKey(), uri2);
        edit.commit();
    }

    public static Uri getRingtoneUri(Context context) {
        NotificationChannel channel = NotificationChannelController.getChannel(NotificationChannelController.CHANNEL_ID_VOICE_MAIL, context);
        return channel != null ? channel.getSound() : getRingTonePreference(context);
    }

    public static Uri getRingTonePreference(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        migrateVoicemailRingtoneSettingsIfNeeded(context, defaultSharedPreferences);
        String string = defaultSharedPreferences.getString(getVoicemailRingtoneSharedPrefsKey(), Settings.System.DEFAULT_NOTIFICATION_URI.toString());
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return Uri.parse(string);
    }

    private static void migrateVoicemailVibrationSettingsIfNeeded(Context context, SharedPreferences sharedPreferences) {
        String voicemailVibrationSharedPrefsKey = getVoicemailVibrationSharedPrefsKey();
        TelephonyManager from = TelephonyManager.from(context);
        if (sharedPreferences.contains(voicemailVibrationSharedPrefsKey) || from.getPhoneCount() != 1) {
            return;
        }
        if (sharedPreferences.contains("button_voicemail_notification_vibrate_key")) {
            sharedPreferences.edit().putBoolean(voicemailVibrationSharedPrefsKey, sharedPreferences.getBoolean("button_voicemail_notification_vibrate_key", false)).remove("button_voicemail_notification_vibrate_when_key").commit();
        }
        if (sharedPreferences.contains("button_voicemail_notification_vibrate_when_key")) {
            sharedPreferences.edit().putBoolean(voicemailVibrationSharedPrefsKey, sharedPreferences.getString("button_voicemail_notification_vibrate_when_key", "never").equals("always")).remove("button_voicemail_notification_vibrate_key").commit();
        }
    }

    private static void migrateVoicemailRingtoneSettingsIfNeeded(Context context, SharedPreferences sharedPreferences) {
        String voicemailRingtoneSharedPrefsKey = getVoicemailRingtoneSharedPrefsKey();
        TelephonyManager from = TelephonyManager.from(context);
        if (!sharedPreferences.contains(voicemailRingtoneSharedPrefsKey) && from.getPhoneCount() == 1 && sharedPreferences.contains("button_voicemail_notification_ringtone_key")) {
            sharedPreferences.edit().putString(voicemailRingtoneSharedPrefsKey, sharedPreferences.getString("button_voicemail_notification_ringtone_key", null)).remove("button_voicemail_notification_ringtone_key").commit();
        }
    }

    private static String getVoicemailVibrationSharedPrefsKey() {
        return "voicemail_notification_vibrate_" + SubscriptionManager.getDefaultSubscriptionId();
    }

    private static String getVoicemailRingtoneSharedPrefsKey() {
        return "voicemail_notification_ringtone_" + SubscriptionManager.getDefaultSubscriptionId();
    }
}
