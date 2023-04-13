package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Slog;
/* loaded from: classes.dex */
public class BrickReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Slog.w("BrickReceiver", "!!! BRICKING DEVICE !!!");
        android.os.SystemService.start("brick");
    }
}
