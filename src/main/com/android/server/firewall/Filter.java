package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
/* loaded from: classes.dex */
public interface Filter {
    boolean matches(IntentFirewall intentFirewall, ComponentName componentName, Intent intent, int i, int i2, String str, int i3);
}
