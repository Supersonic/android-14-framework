package com.android.server.people.data;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.people.ConversationStatus;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.CancellationSignal;
import com.android.server.LocalServices;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.people.PeopleServiceInternal;
/* loaded from: classes2.dex */
public class ConversationStatusExpirationBroadcastReceiver extends BroadcastReceiver {
    public void scheduleExpiration(Context context, int i, String str, String str2, ConversationStatus conversationStatus) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ((AlarmManager) context.getSystemService(AlarmManager.class)).setExactAndAllowWhileIdle(0, conversationStatus.getEndTimeMillis(), PendingIntent.getBroadcast(context, 10, new Intent("ConversationStatusExpiration").setPackage(PackageManagerShellCommandDataLoader.PACKAGE).setData(new Uri.Builder().scheme("expStatus").appendPath(getKey(i, str, str2, conversationStatus)).build()).addFlags(268435456).putExtra("userId", i), 201326592));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static String getKey(int i, String str, String str2, ConversationStatus conversationStatus) {
        return i + str + str2 + conversationStatus.getId();
    }

    public static IntentFilter getFilter() {
        IntentFilter intentFilter = new IntentFilter("ConversationStatusExpiration");
        intentFilter.addDataScheme("expStatus");
        return intentFilter;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, final Intent intent) {
        String action = intent.getAction();
        if (action != null && "ConversationStatusExpiration".equals(action)) {
            new Thread(new Runnable() { // from class: com.android.server.people.data.ConversationStatusExpirationBroadcastReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ConversationStatusExpirationBroadcastReceiver.lambda$onReceive$0(intent);
                }
            }).start();
        }
    }

    public static /* synthetic */ void lambda$onReceive$0(Intent intent) {
        ((PeopleServiceInternal) LocalServices.getService(PeopleServiceInternal.class)).pruneDataForUser(intent.getIntExtra("userId", ActivityManager.getCurrentUser()), new CancellationSignal());
    }
}
