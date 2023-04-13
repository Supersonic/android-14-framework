package android.view;

import android.graphics.RecordingCanvas;
import android.graphics.Rect;
/* loaded from: classes4.dex */
public interface WindowCallbacks {
    boolean onContentDrawn(int i, int i2, int i3, int i4);

    void onPostDraw(RecordingCanvas recordingCanvas);

    void onRequestDraw(boolean z);

    void onWindowDragResizeEnd();

    void onWindowDragResizeStart(Rect rect, boolean z, Rect rect2, Rect rect3);

    void onWindowSizeIsChanging(Rect rect, boolean z, Rect rect2, Rect rect3);
}
