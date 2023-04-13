package android.service.autofill;

import android.content.Context;
import android.p008os.RemoteException;
import android.util.Log;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
/* loaded from: classes3.dex */
public class InlineSuggestionRoot extends FrameLayout {
    private static final String TAG = "InlineSuggestionRoot";
    private final IInlineSuggestionUiCallback mCallback;
    private float mDownX;
    private float mDownY;
    private final int mTouchSlop;

    public InlineSuggestionRoot(Context context, IInlineSuggestionUiCallback callback) {
        super(context);
        this.mCallback = callback;
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setFocusable(false);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:8:0x002c  */
    /* JADX WARN: Removed duplicated region for block: B:9:0x002e  */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean isSecure;
        switch (event.getActionMasked()) {
            case 0:
                this.mDownX = event.getX();
                this.mDownY = event.getY();
                float distance = MathUtils.dist(this.mDownX, this.mDownY, event.getX(), event.getY());
                isSecure = (event.getFlags() & 2) != 0;
                if (isSecure || distance > this.mTouchSlop) {
                    try {
                        this.mCallback.onTransferTouchFocusToImeWindow(getViewRootImpl().getInputToken(), getContext().getDisplayId());
                        break;
                    } catch (RemoteException e) {
                        Log.m104w(TAG, "RemoteException transferring touch focus to IME");
                        break;
                    }
                }
                break;
            case 2:
                float distance2 = MathUtils.dist(this.mDownX, this.mDownY, event.getX(), event.getY());
                if ((event.getFlags() & 2) != 0) {
                }
                if (isSecure) {
                    break;
                }
                this.mCallback.onTransferTouchFocusToImeWindow(getViewRootImpl().getInputToken(), getContext().getDisplayId());
                break;
        }
        return super.dispatchTouchEvent(event);
    }
}
