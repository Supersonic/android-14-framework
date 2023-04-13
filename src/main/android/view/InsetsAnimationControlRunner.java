package android.view;

import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.inputmethod.ImeTracker;
/* loaded from: classes4.dex */
public interface InsetsAnimationControlRunner {
    void cancel();

    void dumpDebug(ProtoOutputStream protoOutputStream, long j);

    WindowInsetsAnimation getAnimation();

    int getAnimationType();

    int getControllingTypes();

    ImeTracker.Token getStatsToken();

    int getTypes();

    void notifyControlRevoked(int i);

    void updateSurfacePosition(SparseArray<InsetsSourceControl> sparseArray);

    default boolean controlsType(int type) {
        return (getTypes() & type) != 0;
    }
}
