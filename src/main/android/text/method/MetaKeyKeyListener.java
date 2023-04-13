package android.text.method;

import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Spannable;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
/* loaded from: classes3.dex */
public abstract class MetaKeyKeyListener {
    private static final int LOCKED = 67108881;
    private static final int LOCKED_RETURN_VALUE = 2;
    public static final int META_ALT_LOCKED = 512;
    private static final long META_ALT_MASK = 565157566611970L;
    public static final int META_ALT_ON = 2;
    private static final long META_ALT_PRESSED = 2199023255552L;
    private static final long META_ALT_RELEASED = 562949953421312L;
    private static final long META_ALT_USED = 8589934592L;
    public static final int META_CAP_LOCKED = 256;
    private static final long META_CAP_PRESSED = 1099511627776L;
    private static final long META_CAP_RELEASED = 281474976710656L;
    private static final long META_CAP_USED = 4294967296L;
    public static final int META_SELECTING = 2048;
    private static final long META_SHIFT_MASK = 282578783305985L;
    public static final int META_SHIFT_ON = 1;
    public static final int META_SYM_LOCKED = 1024;
    private static final long META_SYM_MASK = 1130315133223940L;
    public static final int META_SYM_ON = 4;
    private static final long META_SYM_PRESSED = 4398046511104L;
    private static final long META_SYM_RELEASED = 1125899906842624L;
    private static final long META_SYM_USED = 17179869184L;
    private static final int PRESSED = 16777233;
    private static final int PRESSED_RETURN_VALUE = 1;
    private static final int RELEASED = 33554449;
    private static final int USED = 50331665;
    private static final Object CAP = new NoCopySpan.Concrete();
    private static final Object ALT = new NoCopySpan.Concrete();
    private static final Object SYM = new NoCopySpan.Concrete();
    private static final Object SELECTING = new NoCopySpan.Concrete();

    public static void resetMetaState(Spannable text) {
        text.removeSpan(CAP);
        text.removeSpan(ALT);
        text.removeSpan(SYM);
        text.removeSpan(SELECTING);
    }

    public static final int getMetaState(CharSequence text) {
        return getActive(text, CAP, 1, 256) | getActive(text, ALT, 2, 512) | getActive(text, SYM, 4, 1024) | getActive(text, SELECTING, 2048, 2048);
    }

    public static final int getMetaState(CharSequence text, KeyEvent event) {
        int metaState = event.getMetaState();
        if (event.getKeyCharacterMap().getModifierBehavior() == 1) {
            return metaState | getMetaState(text);
        }
        return metaState;
    }

    public static final int getMetaState(CharSequence text, int meta) {
        switch (meta) {
            case 1:
                return getActive(text, CAP, 1, 2);
            case 2:
                return getActive(text, ALT, 1, 2);
            case 4:
                return getActive(text, SYM, 1, 2);
            case 2048:
                return getActive(text, SELECTING, 1, 2);
            default:
                return 0;
        }
    }

    public static final int getMetaState(CharSequence text, int meta, KeyEvent event) {
        int metaState = event.getMetaState();
        if (event.getKeyCharacterMap().getModifierBehavior() == 1) {
            metaState |= getMetaState(text);
        }
        if (2048 == meta) {
            return (metaState & 2048) != 0 ? 1 : 0;
        }
        return getMetaState(metaState, meta);
    }

    private static int getActive(CharSequence text, Object meta, int on, int lock) {
        if (text instanceof Spanned) {
            Spanned sp = (Spanned) text;
            int flag = sp.getSpanFlags(meta);
            if (flag == LOCKED) {
                return lock;
            }
            if (flag != 0) {
                return on;
            }
            return 0;
        }
        return 0;
    }

    public static void adjustMetaAfterKeypress(Spannable content) {
        adjust(content, CAP);
        adjust(content, ALT);
        adjust(content, SYM);
    }

    public static boolean isMetaTracker(CharSequence text, Object what) {
        return what == CAP || what == ALT || what == SYM || what == SELECTING;
    }

    public static boolean isSelectingMetaTracker(CharSequence text, Object what) {
        return what == SELECTING;
    }

    private static void adjust(Spannable content, Object what) {
        int current = content.getSpanFlags(what);
        if (current == PRESSED) {
            content.setSpan(what, 0, 0, USED);
        } else if (current == RELEASED) {
            content.removeSpan(what);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void resetLockedMeta(Spannable content) {
        resetLock(content, CAP);
        resetLock(content, ALT);
        resetLock(content, SYM);
        resetLock(content, SELECTING);
    }

    private static void resetLock(Spannable content, Object what) {
        int current = content.getSpanFlags(what);
        if (current == LOCKED) {
            content.removeSpan(what);
        }
    }

    public boolean onKeyDown(View view, Editable content, int keyCode, KeyEvent event) {
        if (keyCode == 59 || keyCode == 60) {
            press(content, CAP);
            return true;
        } else if (keyCode == 57 || keyCode == 58 || keyCode == 78) {
            press(content, ALT);
            return true;
        } else if (keyCode == 63) {
            press(content, SYM);
            return true;
        } else {
            return false;
        }
    }

    private void press(Editable content, Object what) {
        int state = content.getSpanFlags(what);
        if (state != PRESSED) {
            if (state == RELEASED) {
                content.setSpan(what, 0, 0, LOCKED);
            } else if (state != USED) {
                if (state == LOCKED) {
                    content.removeSpan(what);
                } else {
                    content.setSpan(what, 0, 0, PRESSED);
                }
            }
        }
    }

    public static void startSelecting(View view, Spannable content) {
        content.setSpan(SELECTING, 0, 0, PRESSED);
    }

    public static void stopSelecting(View view, Spannable content) {
        content.removeSpan(SELECTING);
    }

    public boolean onKeyUp(View view, Editable content, int keyCode, KeyEvent event) {
        if (keyCode == 59 || keyCode == 60) {
            release(content, CAP, event);
            return true;
        } else if (keyCode == 57 || keyCode == 58 || keyCode == 78) {
            release(content, ALT, event);
            return true;
        } else if (keyCode == 63) {
            release(content, SYM, event);
            return true;
        } else {
            return false;
        }
    }

    private void release(Editable content, Object what, KeyEvent event) {
        int current = content.getSpanFlags(what);
        switch (event.getKeyCharacterMap().getModifierBehavior()) {
            case 1:
                if (current == USED) {
                    content.removeSpan(what);
                    return;
                } else if (current == PRESSED) {
                    content.setSpan(what, 0, 0, RELEASED);
                    return;
                } else {
                    return;
                }
            default:
                content.removeSpan(what);
                return;
        }
    }

    public void clearMetaKeyState(View view, Editable content, int states) {
        clearMetaKeyState(content, states);
    }

    public static void clearMetaKeyState(Editable content, int states) {
        if ((states & 1) != 0) {
            content.removeSpan(CAP);
        }
        if ((states & 2) != 0) {
            content.removeSpan(ALT);
        }
        if ((states & 4) != 0) {
            content.removeSpan(SYM);
        }
        if ((states & 2048) != 0) {
            content.removeSpan(SELECTING);
        }
    }

    public static long resetLockedMeta(long state) {
        if ((256 & state) != 0) {
            state &= -282578783305986L;
        }
        if ((512 & state) != 0) {
            state &= -565157566611971L;
        }
        if ((1024 & state) != 0) {
            return state & (-1130315133223941L);
        }
        return state;
    }

    public static final int getMetaState(long state) {
        int result = 0;
        if ((256 & state) != 0) {
            result = 0 | 256;
        } else if ((1 & state) != 0) {
            result = 0 | 1;
        }
        if ((512 & state) != 0) {
            result |= 512;
        } else if ((2 & state) != 0) {
            result |= 2;
        }
        if ((1024 & state) != 0) {
            return result | 1024;
        }
        if ((4 & state) != 0) {
            return result | 4;
        }
        return result;
    }

    public static final int getMetaState(long state, int meta) {
        switch (meta) {
            case 1:
                if ((256 & state) != 0) {
                    return 2;
                }
                return (1 & state) != 0 ? 1 : 0;
            case 2:
                if ((512 & state) != 0) {
                    return 2;
                }
                return (2 & state) != 0 ? 1 : 0;
            case 3:
            default:
                return 0;
            case 4:
                if ((1024 & state) != 0) {
                    return 2;
                }
                return (4 & state) != 0 ? 1 : 0;
        }
    }

    public static long adjustMetaAfterKeypress(long state) {
        if ((1099511627776L & state) != 0) {
            state = (state & (-282578783305986L)) | 1 | 4294967296L;
        } else if ((281474976710656L & state) != 0) {
            state &= -282578783305986L;
        }
        if ((2199023255552L & state) != 0) {
            state = (state & (-565157566611971L)) | 2 | 8589934592L;
        } else if ((562949953421312L & state) != 0) {
            state &= -565157566611971L;
        }
        if ((4398046511104L & state) != 0) {
            return (state & (-1130315133223941L)) | 4 | 17179869184L;
        }
        if ((1125899906842624L & state) != 0) {
            return state & (-1130315133223941L);
        }
        return state;
    }

    public static long handleKeyDown(long state, int keyCode, KeyEvent event) {
        if (keyCode != 59 && keyCode != 60) {
            if (keyCode != 57 && keyCode != 58 && keyCode != 78) {
                if (keyCode == 63) {
                    return press(state, 4, META_SYM_MASK, 1024L, 4398046511104L, 1125899906842624L, 17179869184L);
                }
                return state;
            }
            return press(state, 2, META_ALT_MASK, 512L, 2199023255552L, 562949953421312L, 8589934592L);
        }
        return press(state, 1, META_SHIFT_MASK, 256L, 1099511627776L, 281474976710656L, 4294967296L);
    }

    private static long press(long state, int what, long mask, long locked, long pressed, long released, long used) {
        if ((state & pressed) == 0) {
            if ((state & released) != 0) {
                return ((~mask) & state) | what | locked;
            }
            if ((state & used) == 0) {
                if ((state & locked) != 0) {
                    return (~mask) & state;
                }
                return what | pressed | state;
            }
        }
        return state;
    }

    public static long handleKeyUp(long state, int keyCode, KeyEvent event) {
        if (keyCode != 59 && keyCode != 60) {
            if (keyCode != 57 && keyCode != 58 && keyCode != 78) {
                if (keyCode == 63) {
                    return release(state, 4, META_SYM_MASK, 4398046511104L, 1125899906842624L, 17179869184L, event);
                }
                return state;
            }
            return release(state, 2, META_ALT_MASK, 2199023255552L, 562949953421312L, 8589934592L, event);
        }
        return release(state, 1, META_SHIFT_MASK, 1099511627776L, 281474976710656L, 4294967296L, event);
    }

    private static long release(long state, int what, long mask, long pressed, long released, long used, KeyEvent event) {
        switch (event.getKeyCharacterMap().getModifierBehavior()) {
            case 1:
                if ((state & used) != 0) {
                    return state & (~mask);
                }
                if ((state & pressed) != 0) {
                    return state | what | released;
                }
                return state;
            default:
                return state & (~mask);
        }
    }

    public long clearMetaKeyState(long state, int which) {
        if ((which & 1) != 0 && (256 & state) != 0) {
            state &= -282578783305986L;
        }
        if ((which & 2) != 0 && (512 & state) != 0) {
            state &= -565157566611971L;
        }
        if ((which & 4) != 0 && (1024 & state) != 0) {
            return state & (-1130315133223941L);
        }
        return state;
    }
}
