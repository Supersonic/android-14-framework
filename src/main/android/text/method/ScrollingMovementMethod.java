package android.text.method;

import android.text.Layout;
import android.text.Spannable;
import android.view.MotionEvent;
import android.widget.TextView;
/* loaded from: classes3.dex */
public class ScrollingMovementMethod extends BaseMovementMethod implements MovementMethod {
    private static ScrollingMovementMethod sInstance;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.text.method.BaseMovementMethod
    public boolean left(TextView widget, Spannable buffer) {
        return scrollLeft(widget, buffer, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.text.method.BaseMovementMethod
    public boolean right(TextView widget, Spannable buffer) {
        return scrollRight(widget, buffer, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.text.method.BaseMovementMethod
    /* renamed from: up */
    public boolean mo116up(TextView widget, Spannable buffer) {
        return scrollUp(widget, buffer, 1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.text.method.BaseMovementMethod
    public boolean down(TextView widget, Spannable buffer) {
        return scrollDown(widget, buffer, 1);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean pageUp(TextView widget, Spannable buffer) {
        return scrollPageUp(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean pageDown(TextView widget, Spannable buffer) {
        return scrollPageDown(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean top(TextView widget, Spannable buffer) {
        return scrollTop(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean bottom(TextView widget, Spannable buffer) {
        return scrollBottom(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean lineStart(TextView widget, Spannable buffer) {
        return scrollLineStart(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean lineEnd(TextView widget, Spannable buffer) {
        return scrollLineEnd(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean home(TextView widget, Spannable buffer) {
        return top(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean end(TextView widget, Spannable buffer) {
        return bottom(widget, buffer);
    }

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        return Touch.onTouchEvent(widget, buffer, event);
    }

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public void onTakeFocus(TextView widget, Spannable text, int dir) {
        Layout layout = widget.getLayout();
        if (layout != null && (dir & 2) != 0) {
            widget.scrollTo(widget.getScrollX(), layout.getLineTop(0));
        }
        if (layout != null && (dir & 1) != 0) {
            int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
            int line = layout.getLineCount() - 1;
            widget.scrollTo(widget.getScrollX(), layout.getLineTop(line + 1) - (widget.getHeight() - padding));
        }
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ScrollingMovementMethod();
        }
        return sInstance;
    }
}
