package com.android.server.appop;

import android.annotation.SystemApi;
@SystemApi(client = SystemApi.Client.SYSTEM_SERVER)
/* loaded from: classes.dex */
public interface AppOpsManagerLocal {
    boolean isUidInForeground(int i);
}
