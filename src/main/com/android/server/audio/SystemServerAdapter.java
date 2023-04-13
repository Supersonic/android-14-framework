package com.android.server.audio;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import java.util.Objects;
/* loaded from: classes.dex */
public class SystemServerAdapter {
    public final Context mContext;

    public boolean isPrivileged() {
        return true;
    }

    public SystemServerAdapter(Context context) {
        this.mContext = context;
    }

    public static final SystemServerAdapter getDefaultAdapter(Context context) {
        Objects.requireNonNull(context);
        return new SystemServerAdapter(context);
    }

    public void sendMicrophoneMuteChangedIntent() {
        this.mContext.sendBroadcastAsUser(new Intent("android.media.action.MICROPHONE_MUTE_CHANGED").setFlags(1073741824), UserHandle.ALL);
    }

    public void sendDeviceBecomingNoisyIntent() {
        if (this.mContext == null) {
            return;
        }
        Intent intent = new Intent("android.media.AUDIO_BECOMING_NOISY");
        intent.addFlags(67108864);
        intent.addFlags(268435456);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @VisibleForTesting
    public void broadcastStickyIntentToCurrentProfileGroup(Intent intent) {
        for (int i : ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).getCurrentProfileIds()) {
            ActivityManager.broadcastStickyIntent(intent, i);
        }
    }

    public void registerUserStartedReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_STARTED");
        context.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.audio.SystemServerAdapter.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int intExtra;
                UserInfo profileParent;
                if (!"android.intent.action.USER_STARTED".equals(intent.getAction()) || (intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000)) == -10000 || (profileParent = ((UserManager) context2.getSystemService(UserManager.class)).getProfileParent(intExtra)) == null) {
                    return;
                }
                SystemServerAdapter.this.broadcastProfileParentStickyIntent(context2, "android.media.action.HDMI_AUDIO_PLUG", intExtra, profileParent.id);
                SystemServerAdapter.this.broadcastProfileParentStickyIntent(context2, "android.intent.action.HEADSET_PLUG", intExtra, profileParent.id);
            }
        }, UserHandle.ALL, intentFilter, null, null);
    }

    public final void broadcastProfileParentStickyIntent(Context context, String str, int i, int i2) {
        Intent registerReceiverAsUser = context.registerReceiverAsUser(null, UserHandle.of(i2), new IntentFilter(str), null, null);
        if (registerReceiverAsUser != null) {
            ActivityManager.broadcastStickyIntent(registerReceiverAsUser, i);
        }
    }
}
