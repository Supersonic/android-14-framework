package com.android.server.p014wm;

import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import com.android.server.p014wm.SurfaceAnimator;
import java.io.PrintWriter;
/* renamed from: com.android.server.wm.AnimationAdapter */
/* loaded from: classes2.dex */
public interface AnimationAdapter {
    void dump(PrintWriter printWriter, String str);

    void dumpDebug(ProtoOutputStream protoOutputStream);

    default int getBackgroundColor() {
        return 0;
    }

    long getDurationHint();

    default boolean getShowBackground() {
        return false;
    }

    boolean getShowWallpaper();

    long getStatusBarTransitionsStartTime();

    void onAnimationCancelled(SurfaceControl surfaceControl);

    default boolean shouldDeferAnimationFinish(Runnable runnable) {
        return false;
    }

    void startAnimation(SurfaceControl surfaceControl, SurfaceControl.Transaction transaction, int i, SurfaceAnimator.OnAnimationFinishedCallback onAnimationFinishedCallback);

    default void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        dumpDebug(protoOutputStream);
        protoOutputStream.end(start);
    }
}
