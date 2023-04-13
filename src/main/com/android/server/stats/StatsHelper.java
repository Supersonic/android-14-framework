package com.android.server.stats;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes2.dex */
public final class StatsHelper {
    public static void sendStatsdReadyBroadcast(Context context) {
        context.sendBroadcastAsUser(new Intent("android.app.action.STATSD_STARTED").addFlags(16777216), UserHandle.SYSTEM, "android.permission.DUMP");
    }
}
