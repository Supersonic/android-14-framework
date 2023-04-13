package android.service.selectiontoolbar;

import android.content.Context;
import android.graphics.Rect;
import android.p008os.IBinder;
import android.service.selectiontoolbar.SelectionToolbarRenderService;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import java.io.PrintWriter;
/* loaded from: classes3.dex */
public class FloatingToolbarRoot extends LinearLayout {
    private static final boolean DEBUG = false;
    private static final String TAG = "FloatingToolbarRoot";
    private final Rect mContentRect;
    private int mLastDownX;
    private int mLastDownY;
    private final IBinder mTargetInputToken;
    private final SelectionToolbarRenderService.TransferTouchListener mTransferTouchListener;

    public FloatingToolbarRoot(Context context, IBinder targetInputToken, SelectionToolbarRenderService.TransferTouchListener transferTouchListener) {
        super(context);
        this.mContentRect = new Rect();
        this.mLastDownX = -1;
        this.mLastDownY = -1;
        this.mTargetInputToken = targetInputToken;
        this.mTransferTouchListener = transferTouchListener;
        setFocusable(false);
    }

    public void setContentRect(Rect contentRect) {
        this.mContentRect.set(contentRect);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == 0) {
            this.mLastDownX = (int) event.getX();
            int y = (int) event.getY();
            this.mLastDownY = y;
            if (!this.mContentRect.contains(this.mLastDownX, y)) {
                this.mTransferTouchListener.onTransferTouch(getViewRootImpl().getInputToken(), this.mTargetInputToken);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.println("FloatingToolbarRoot:");
        pw.print(prefix + "  ");
        pw.print("last down X: ");
        pw.println(this.mLastDownX);
        pw.print(prefix + "  ");
        pw.print("last down Y: ");
        pw.println(this.mLastDownY);
    }
}
