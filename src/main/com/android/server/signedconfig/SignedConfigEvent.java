package com.android.server.signedconfig;

import com.android.internal.util.FrameworkStatsLog;
/* loaded from: classes2.dex */
public class SignedConfigEvent {
    public int type = 0;
    public int status = 0;
    public int version = 0;
    public String fromPackage = null;
    public int verifiedWith = 0;

    public void send() {
        FrameworkStatsLog.write(123, this.type, this.status, this.version, this.fromPackage, this.verifiedWith);
    }
}
