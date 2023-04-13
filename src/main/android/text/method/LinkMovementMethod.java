package android.text.method;

import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.textclassifier.TextLinks;
import android.widget.TextView;
/* loaded from: classes3.dex */
public class LinkMovementMethod extends ScrollingMovementMethod {
    private static final int CLICK = 1;
    private static final int DOWN = 3;
    private static Object FROM_BELOW = new NoCopySpan.Concrete();
    private static final int HIDE_FLOATING_TOOLBAR_DELAY_MS = 200;

    /* renamed from: UP */
    private static final int f461UP = 2;
    private static LinkMovementMethod sInstance;

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean canSelectArbitrarily() {
        return true;
    }

    @Override // android.text.method.BaseMovementMethod
    protected boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode, int movementMetaState, KeyEvent event) {
        switch (keyCode) {
            case 23:
            case 66:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState) && event.getAction() == 0 && event.getRepeatCount() == 0 && action(1, widget, buffer)) {
                    return true;
                }
                break;
        }
        return super.handleMovementKey(widget, buffer, keyCode, movementMetaState, event);
    }

    @Override // android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod
    /* renamed from: up */
    protected boolean mo116up(TextView widget, Spannable buffer) {
        if (action(2, widget, buffer)) {
            return true;
        }
        return super.mo116up(widget, buffer);
    }

    @Override // android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod
    protected boolean down(TextView widget, Spannable buffer) {
        if (action(3, widget, buffer)) {
            return true;
        }
        return super.down(widget, buffer);
    }

    @Override // android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod
    protected boolean left(TextView widget, Spannable buffer) {
        if (action(2, widget, buffer)) {
            return true;
        }
        return super.left(widget, buffer);
    }

    @Override // android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod
    protected boolean right(TextView widget, Spannable buffer) {
        if (action(3, widget, buffer)) {
            return true;
        }
        return super.right(widget, buffer);
    }

    private boolean action(int what, TextView widget, Spannable buffer) {
        int areaBot;
        Layout layout = widget.getLayout();
        if (widget.isOffsetMappingAvailable()) {
            return false;
        }
        int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
        int areaTop = widget.getScrollY();
        int areaBot2 = (widget.getHeight() + areaTop) - padding;
        int lineTop = layout.getLineForVertical(areaTop);
        int lineBot = layout.getLineForVertical(areaBot2);
        int first = layout.getLineStart(lineTop);
        int last = layout.getLineEnd(lineBot);
        ClickableSpan[] candidates = (ClickableSpan[]) buffer.getSpans(first, last, ClickableSpan.class);
        int a = Selection.getSelectionStart(buffer);
        int b = Selection.getSelectionEnd(buffer);
        int selStart = Math.min(a, b);
        int selEnd = Math.max(a, b);
        if (selStart < 0 && buffer.getSpanStart(FROM_BELOW) >= 0) {
            int length = buffer.length();
            selEnd = length;
            selStart = length;
        }
        if (selStart > last) {
            selEnd = Integer.MAX_VALUE;
            selStart = Integer.MAX_VALUE;
        }
        if (selEnd < first) {
            selEnd = -1;
            selStart = -1;
        }
        switch (what) {
            case 1:
                if (selStart == selEnd) {
                    return false;
                }
                ClickableSpan[] links = (ClickableSpan[]) buffer.getSpans(selStart, selEnd, ClickableSpan.class);
                if (links.length != 1) {
                    return false;
                }
                ClickableSpan link = links[0];
                if (link instanceof TextLinks.TextLinkSpan) {
                    ((TextLinks.TextLinkSpan) link).onClick(widget, 1);
                    return false;
                }
                link.onClick(widget);
                return false;
            case 2:
                int bestStart = -1;
                int bestEnd = -1;
                int i = 0;
                while (i < candidates.length) {
                    int end = buffer.getSpanEnd(candidates[i]);
                    if (end >= selEnd && selStart != selEnd) {
                        areaBot = areaBot2;
                    } else if (end <= bestEnd) {
                        areaBot = areaBot2;
                    } else {
                        areaBot = areaBot2;
                        bestStart = buffer.getSpanStart(candidates[i]);
                        bestEnd = end;
                    }
                    i++;
                    areaBot2 = areaBot;
                }
                if (bestStart >= 0) {
                    Selection.setSelection(buffer, bestEnd, bestStart);
                    return true;
                }
                return false;
            case 3:
                int bestStart2 = Integer.MAX_VALUE;
                int bestEnd2 = Integer.MAX_VALUE;
                int padding2 = 0;
                while (true) {
                    int areaTop2 = areaTop;
                    if (padding2 < candidates.length) {
                        int start = buffer.getSpanStart(candidates[padding2]);
                        if ((start > selStart || selStart == selEnd) && start < bestStart2) {
                            bestEnd2 = buffer.getSpanEnd(candidates[padding2]);
                            bestStart2 = start;
                        }
                        padding2++;
                        areaTop = areaTop2;
                    } else if (bestEnd2 < Integer.MAX_VALUE) {
                        Selection.setSelection(buffer, bestStart2, bestEnd2);
                        return true;
                    } else {
                        return false;
                    }
                }
            default:
                return false;
        }
    }

    @Override // android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        if (action == 1 || action == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int x2 = x - widget.getTotalPaddingLeft();
            int y2 = y - widget.getTotalPaddingTop();
            int x3 = x2 + widget.getScrollX();
            int y3 = y2 + widget.getScrollY();
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y3);
            int off = layout.getOffsetForHorizontal(line, x3);
            ClickableSpan[] links = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
            if (links.length != 0) {
                ClickableSpan link = links[0];
                if (action == 1) {
                    if (link instanceof TextLinks.TextLinkSpan) {
                        ((TextLinks.TextLinkSpan) link).onClick(widget, 0);
                    } else {
                        link.onClick(widget);
                    }
                } else if (action == 0) {
                    if (widget.getContext().getApplicationInfo().targetSdkVersion >= 28) {
                        widget.hideFloatingToolbar(200);
                    }
                    Selection.setSelection(buffer, buffer.getSpanStart(link), buffer.getSpanEnd(link));
                }
                return true;
            }
            Selection.removeSelection(buffer);
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    @Override // android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public void initialize(TextView widget, Spannable text) {
        Selection.removeSelection(text);
        text.removeSpan(FROM_BELOW);
    }

    @Override // android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
    public void onTakeFocus(TextView view, Spannable text, int dir) {
        Selection.removeSelection(text);
        if ((dir & 1) != 0) {
            text.setSpan(FROM_BELOW, 0, 0, 34);
        } else {
            text.removeSpan(FROM_BELOW);
        }
    }

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new LinkMovementMethod();
        }
        return sInstance;
    }
}
