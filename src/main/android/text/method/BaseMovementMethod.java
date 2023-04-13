package android.text.method;

import android.text.Layout;
import android.text.Spannable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
/* loaded from: classes3.dex */
public class BaseMovementMethod implements MovementMethod {
    @Override // android.text.method.MovementMethod
    public boolean canSelectArbitrarily() {
        return false;
    }

    @Override // android.text.method.MovementMethod
    public void initialize(TextView widget, Spannable text) {
    }

    @Override // android.text.method.MovementMethod
    public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        int movementMetaState = getMovementMetaState(text, event);
        boolean handled = handleMovementKey(widget, text, keyCode, movementMetaState, event);
        if (handled) {
            MetaKeyKeyListener.adjustMetaAfterKeypress(text);
            MetaKeyKeyListener.resetLockedMeta(text);
        }
        return handled;
    }

    @Override // android.text.method.MovementMethod
    public boolean onKeyOther(TextView widget, Spannable text, KeyEvent event) {
        int movementMetaState = getMovementMetaState(text, event);
        int keyCode = event.getKeyCode();
        if (keyCode != 0 && event.getAction() == 2) {
            int repeat = event.getRepeatCount();
            boolean handled = false;
            for (int i = 0; i < repeat && handleMovementKey(widget, text, keyCode, movementMetaState, event); i++) {
                handled = true;
            }
            if (handled) {
                MetaKeyKeyListener.adjustMetaAfterKeypress(text);
                MetaKeyKeyListener.resetLockedMeta(text);
            }
            return handled;
        }
        return false;
    }

    @Override // android.text.method.MovementMethod
    public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.text.method.MovementMethod
    public void onTakeFocus(TextView widget, Spannable text, int direction) {
    }

    @Override // android.text.method.MovementMethod
    public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override // android.text.method.MovementMethod
    public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
        return false;
    }

    @Override // android.text.method.MovementMethod
    public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
        float vscroll;
        float hscroll;
        if ((event.getSource() & 2) != 0) {
            switch (event.getAction()) {
                case 8:
                    if ((event.getMetaState() & 1) != 0) {
                        vscroll = 0.0f;
                        hscroll = event.getAxisValue(9);
                    } else {
                        float vscroll2 = event.getAxisValue(9);
                        vscroll = -vscroll2;
                        hscroll = event.getAxisValue(10);
                    }
                    boolean handled = false;
                    if (hscroll < 0.0f) {
                        handled = false | scrollLeft(widget, text, (int) Math.ceil(-hscroll));
                    } else if (hscroll > 0.0f) {
                        handled = false | scrollRight(widget, text, (int) Math.ceil(hscroll));
                    }
                    if (vscroll < 0.0f) {
                        return handled | scrollUp(widget, text, (int) Math.ceil(-vscroll));
                    }
                    if (vscroll > 0.0f) {
                        return handled | scrollDown(widget, text, (int) Math.ceil(vscroll));
                    }
                    return handled;
                default:
                    return false;
            }
        }
        return false;
    }

    protected int getMovementMetaState(Spannable buffer, KeyEvent event) {
        int metaState = MetaKeyKeyListener.getMetaState(buffer, event) & (-1537);
        return KeyEvent.normalizeMetaState(metaState) & (-194);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean handleMovementKey(TextView widget, Spannable buffer, int keyCode, int movementMetaState, KeyEvent event) {
        switch (keyCode) {
            case 19:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return mo116up(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 2)) {
                    return top(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 4096)) {
                    return previousParagraph(widget, buffer);
                }
                return false;
            case 20:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return down(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 2)) {
                    return bottom(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 4096)) {
                    return nextParagraph(widget, buffer);
                }
                return false;
            case 21:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return left(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 4096)) {
                    return leftWord(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 2)) {
                    return lineStart(widget, buffer);
                }
                return false;
            case 22:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return right(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 4096)) {
                    return rightWord(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 2)) {
                    return lineEnd(widget, buffer);
                }
                return false;
            case 92:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return pageUp(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 2)) {
                    return top(widget, buffer);
                }
                return false;
            case 93:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return pageDown(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 2)) {
                    return bottom(widget, buffer);
                }
                return false;
            case 122:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return home(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 4096)) {
                    return top(widget, buffer);
                }
                return false;
            case 123:
                if (KeyEvent.metaStateHasNoModifiers(movementMetaState)) {
                    return end(widget, buffer);
                }
                if (KeyEvent.metaStateHasModifiers(movementMetaState, 4096)) {
                    return bottom(widget, buffer);
                }
                return false;
            default:
                return false;
        }
    }

    protected boolean left(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean right(TextView widget, Spannable buffer) {
        return false;
    }

    /* renamed from: up */
    protected boolean mo116up(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean down(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean pageUp(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean pageDown(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean top(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean bottom(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean lineStart(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean lineEnd(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean leftWord(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean rightWord(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean home(TextView widget, Spannable buffer) {
        return false;
    }

    protected boolean end(TextView widget, Spannable buffer) {
        return false;
    }

    public boolean previousParagraph(TextView widget, Spannable buffer) {
        return false;
    }

    public boolean nextParagraph(TextView widget, Spannable buffer) {
        return false;
    }

    private int getTopLine(TextView widget) {
        return widget.getLayout().getLineForVertical(widget.getScrollY());
    }

    private int getBottomLine(TextView widget) {
        return widget.getLayout().getLineForVertical(widget.getScrollY() + getInnerHeight(widget));
    }

    private int getInnerWidth(TextView widget) {
        return (widget.getWidth() - widget.getTotalPaddingLeft()) - widget.getTotalPaddingRight();
    }

    private int getInnerHeight(TextView widget) {
        return (widget.getHeight() - widget.getTotalPaddingTop()) - widget.getTotalPaddingBottom();
    }

    private int getCharacterWidth(TextView widget) {
        return (int) Math.ceil(widget.getPaint().getFontSpacing());
    }

    private int getScrollBoundsLeft(TextView widget) {
        Layout layout = widget.getLayout();
        int topLine = getTopLine(widget);
        int bottomLine = getBottomLine(widget);
        if (topLine > bottomLine) {
            return 0;
        }
        int left = Integer.MAX_VALUE;
        for (int line = topLine; line <= bottomLine; line++) {
            int lineLeft = (int) Math.floor(layout.getLineLeft(line));
            if (lineLeft < left) {
                left = lineLeft;
            }
        }
        return left;
    }

    private int getScrollBoundsRight(TextView widget) {
        Layout layout = widget.getLayout();
        int topLine = getTopLine(widget);
        int bottomLine = getBottomLine(widget);
        if (topLine > bottomLine) {
            return 0;
        }
        int right = Integer.MIN_VALUE;
        for (int line = topLine; line <= bottomLine; line++) {
            int lineRight = (int) Math.ceil(layout.getLineRight(line));
            if (lineRight > right) {
                right = lineRight;
            }
        }
        return right;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollLeft(TextView widget, Spannable buffer, int amount) {
        int minScrollX = getScrollBoundsLeft(widget);
        int scrollX = widget.getScrollX();
        if (scrollX > minScrollX) {
            widget.scrollTo(Math.max(scrollX - (getCharacterWidth(widget) * amount), minScrollX), widget.getScrollY());
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollRight(TextView widget, Spannable buffer, int amount) {
        int maxScrollX = getScrollBoundsRight(widget) - getInnerWidth(widget);
        int scrollX = widget.getScrollX();
        if (scrollX < maxScrollX) {
            widget.scrollTo(Math.min((getCharacterWidth(widget) * amount) + scrollX, maxScrollX), widget.getScrollY());
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollUp(TextView widget, Spannable buffer, int amount) {
        Layout layout = widget.getLayout();
        int top = widget.getScrollY();
        int topLine = layout.getLineForVertical(top);
        if (layout.getLineTop(topLine) == top) {
            topLine--;
        }
        if (topLine >= 0) {
            Touch.scrollTo(widget, layout, widget.getScrollX(), layout.getLineTop(Math.max((topLine - amount) + 1, 0)));
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollDown(TextView widget, Spannable buffer, int amount) {
        Layout layout = widget.getLayout();
        int innerHeight = getInnerHeight(widget);
        int bottom = widget.getScrollY() + innerHeight;
        int bottomLine = layout.getLineForVertical(bottom);
        if (layout.getLineTop(bottomLine + 1) < bottom + 1) {
            bottomLine++;
        }
        int limit = layout.getLineCount() - 1;
        if (bottomLine <= limit) {
            Touch.scrollTo(widget, layout, widget.getScrollX(), layout.getLineTop(Math.min((bottomLine + amount) - 1, limit) + 1) - innerHeight);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollPageUp(TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();
        int top = widget.getScrollY() - getInnerHeight(widget);
        int topLine = layout.getLineForVertical(top);
        if (topLine >= 0) {
            Touch.scrollTo(widget, layout, widget.getScrollX(), layout.getLineTop(topLine));
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollPageDown(TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();
        int innerHeight = getInnerHeight(widget);
        int bottom = widget.getScrollY() + innerHeight + innerHeight;
        int bottomLine = layout.getLineForVertical(bottom);
        if (bottomLine <= layout.getLineCount() - 1) {
            Touch.scrollTo(widget, layout, widget.getScrollX(), layout.getLineTop(bottomLine + 1) - innerHeight);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollTop(TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();
        if (getTopLine(widget) >= 0) {
            Touch.scrollTo(widget, layout, widget.getScrollX(), layout.getLineTop(0));
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollBottom(TextView widget, Spannable buffer) {
        Layout layout = widget.getLayout();
        int lineCount = layout.getLineCount();
        if (getBottomLine(widget) <= lineCount - 1) {
            Touch.scrollTo(widget, layout, widget.getScrollX(), layout.getLineTop(lineCount) - getInnerHeight(widget));
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollLineStart(TextView widget, Spannable buffer) {
        int minScrollX = getScrollBoundsLeft(widget);
        int scrollX = widget.getScrollX();
        if (scrollX > minScrollX) {
            widget.scrollTo(minScrollX, widget.getScrollY());
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean scrollLineEnd(TextView widget, Spannable buffer) {
        int maxScrollX = getScrollBoundsRight(widget) - getInnerWidth(widget);
        int scrollX = widget.getScrollX();
        if (scrollX < maxScrollX) {
            widget.scrollTo(maxScrollX, widget.getScrollY());
            return true;
        }
        return false;
    }
}
