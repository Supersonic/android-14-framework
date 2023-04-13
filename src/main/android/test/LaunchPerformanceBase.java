package android.test;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
@Deprecated
/* loaded from: classes.dex */
public class LaunchPerformanceBase extends Instrumentation {
    public static final String LOG_TAG = "Launch Performance";
    protected Intent mIntent;
    protected Bundle mResults = new Bundle();

    public LaunchPerformanceBase() {
        Intent intent = new Intent("android.intent.action.MAIN");
        this.mIntent = intent;
        intent.setFlags(268435456);
        setAutomaticPerformanceSnapshots();
    }

    protected void LaunchApp() {
        startActivitySync(this.mIntent);
        waitForIdleSync();
    }
}
