package com.android.server.display;

import android.os.SystemClock;
import com.android.server.display.ScreenOffBrightnessSensorController;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes.dex */
public final /* synthetic */ class DisplayPowerController$$ExternalSyntheticLambda6 implements ScreenOffBrightnessSensorController.Clock {
    @Override // com.android.server.display.ScreenOffBrightnessSensorController.Clock
    public final long uptimeMillis() {
        return SystemClock.uptimeMillis();
    }
}
