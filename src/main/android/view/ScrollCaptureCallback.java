package android.view;

import android.graphics.Rect;
import android.p008os.CancellationSignal;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public interface ScrollCaptureCallback {
    void onScrollCaptureEnd(Runnable runnable);

    void onScrollCaptureImageRequest(ScrollCaptureSession scrollCaptureSession, CancellationSignal cancellationSignal, Rect rect, Consumer<Rect> consumer);

    void onScrollCaptureSearch(CancellationSignal cancellationSignal, Consumer<Rect> consumer);

    void onScrollCaptureStart(ScrollCaptureSession scrollCaptureSession, CancellationSignal cancellationSignal, Runnable runnable);
}
