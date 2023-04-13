package android.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
@Deprecated
/* loaded from: classes4.dex */
public class DialerFilter extends RelativeLayout {
    public static final int DIGITS_AND_LETTERS = 1;
    public static final int DIGITS_AND_LETTERS_NO_DIGITS = 2;
    public static final int DIGITS_AND_LETTERS_NO_LETTERS = 3;
    public static final int DIGITS_ONLY = 4;
    public static final int LETTERS_ONLY = 5;
    EditText mDigits;
    EditText mHint;
    ImageView mIcon;
    InputFilter[] mInputFilters;
    private boolean mIsQwerty;
    EditText mLetters;
    int mMode;
    EditText mPrimary;

    public DialerFilter(Context context) {
        super(context);
    }

    public DialerFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mInputFilters = new InputFilter[]{new InputFilter.AllCaps()};
        EditText editText = (EditText) findViewById(16908293);
        this.mHint = editText;
        if (editText == null) {
            throw new IllegalStateException("DialerFilter must have a child EditText named hint");
        }
        editText.setFilters(this.mInputFilters);
        EditText editText2 = this.mHint;
        this.mLetters = editText2;
        editText2.setKeyListener(TextKeyListener.getInstance());
        this.mLetters.setMovementMethod(null);
        this.mLetters.setFocusable(false);
        EditText editText3 = (EditText) findViewById(16908300);
        this.mPrimary = editText3;
        if (editText3 == null) {
            throw new IllegalStateException("DialerFilter must have a child EditText named primary");
        }
        editText3.setFilters(this.mInputFilters);
        EditText editText4 = this.mPrimary;
        this.mDigits = editText4;
        editText4.setKeyListener(DialerKeyListener.getInstance());
        this.mDigits.setMovementMethod(null);
        this.mDigits.setFocusable(false);
        this.mIcon = (ImageView) findViewById(16908294);
        setFocusable(true);
        this.mIsQwerty = true;
        setMode(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        ImageView imageView = this.mIcon;
        if (imageView != null) {
            imageView.setVisibility(focused ? 0 : 8);
        }
    }

    public boolean isQwertyKeyboard() {
        return this.mIsQwerty;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        switch (keyCode) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 66:
                break;
            case 67:
                switch (this.mMode) {
                    case 1:
                        handled = this.mDigits.onKeyDown(keyCode, event) & this.mLetters.onKeyDown(keyCode, event);
                        break;
                    case 2:
                        handled = this.mLetters.onKeyDown(keyCode, event);
                        if (this.mLetters.getText().length() == this.mDigits.getText().length()) {
                            setMode(1);
                            break;
                        }
                        break;
                    case 3:
                        if (this.mDigits.getText().length() == this.mLetters.getText().length()) {
                            this.mLetters.onKeyDown(keyCode, event);
                            setMode(1);
                        }
                        handled = this.mDigits.onKeyDown(keyCode, event);
                        break;
                    case 4:
                        handled = this.mDigits.onKeyDown(keyCode, event);
                        break;
                    case 5:
                        handled = this.mLetters.onKeyDown(keyCode, event);
                        break;
                }
            default:
                switch (this.mMode) {
                    case 1:
                        handled = this.mLetters.onKeyDown(keyCode, event);
                        if (KeyEvent.isModifierKey(keyCode)) {
                            this.mDigits.onKeyDown(keyCode, event);
                            handled = true;
                            break;
                        } else {
                            boolean isPrint = event.isPrintingKey();
                            if (isPrint || keyCode == 62 || keyCode == 61) {
                                char c = event.getMatch(DialerKeyListener.CHARACTERS);
                                if (c != 0) {
                                    handled &= this.mDigits.onKeyDown(keyCode, event);
                                    break;
                                } else {
                                    setMode(2);
                                    break;
                                }
                            }
                        }
                        break;
                    case 2:
                    case 5:
                        handled = this.mLetters.onKeyDown(keyCode, event);
                        break;
                    case 3:
                    case 4:
                        handled = this.mDigits.onKeyDown(keyCode, event);
                        break;
                }
        }
        if (handled) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean a = this.mLetters.onKeyUp(keyCode, event);
        boolean b = this.mDigits.onKeyUp(keyCode, event);
        return a || b;
    }

    public int getMode() {
        return this.mMode;
    }

    public void setMode(int newMode) {
        switch (newMode) {
            case 1:
                makeDigitsPrimary();
                this.mLetters.setVisibility(0);
                this.mDigits.setVisibility(0);
                break;
            case 2:
                makeLettersPrimary();
                this.mLetters.setVisibility(0);
                this.mDigits.setVisibility(4);
                break;
            case 3:
                makeDigitsPrimary();
                this.mLetters.setVisibility(4);
                this.mDigits.setVisibility(0);
                break;
            case 4:
                makeDigitsPrimary();
                this.mLetters.setVisibility(8);
                this.mDigits.setVisibility(0);
                break;
            case 5:
                makeLettersPrimary();
                this.mLetters.setVisibility(0);
                this.mDigits.setVisibility(8);
                break;
        }
        int oldMode = this.mMode;
        this.mMode = newMode;
        onModeChange(oldMode, newMode);
    }

    private void makeLettersPrimary() {
        if (this.mPrimary == this.mDigits) {
            swapPrimaryAndHint(true);
        }
    }

    private void makeDigitsPrimary() {
        if (this.mPrimary == this.mLetters) {
            swapPrimaryAndHint(false);
        }
    }

    private void swapPrimaryAndHint(boolean makeLettersPrimary) {
        Editable lettersText = this.mLetters.getText();
        Editable digitsText = this.mDigits.getText();
        KeyListener lettersInput = this.mLetters.getKeyListener();
        KeyListener digitsInput = this.mDigits.getKeyListener();
        if (makeLettersPrimary) {
            this.mLetters = this.mPrimary;
            this.mDigits = this.mHint;
        } else {
            this.mLetters = this.mHint;
            this.mDigits = this.mPrimary;
        }
        this.mLetters.setKeyListener(lettersInput);
        this.mLetters.setText(lettersText);
        Editable lettersText2 = this.mLetters.getText();
        Selection.setSelection(lettersText2, lettersText2.length());
        this.mDigits.setKeyListener(digitsInput);
        this.mDigits.setText(digitsText);
        Editable digitsText2 = this.mDigits.getText();
        Selection.setSelection(digitsText2, digitsText2.length());
        this.mPrimary.setFilters(this.mInputFilters);
        this.mHint.setFilters(this.mInputFilters);
    }

    public CharSequence getLetters() {
        if (this.mLetters.getVisibility() == 0) {
            return this.mLetters.getText();
        }
        return "";
    }

    public CharSequence getDigits() {
        if (this.mDigits.getVisibility() == 0) {
            return this.mDigits.getText();
        }
        return "";
    }

    public CharSequence getFilterText() {
        if (this.mMode != 4) {
            return getLetters();
        }
        return getDigits();
    }

    public void append(String text) {
        switch (this.mMode) {
            case 1:
                this.mDigits.getText().append((CharSequence) text);
                this.mLetters.getText().append((CharSequence) text);
                return;
            case 2:
            case 5:
                this.mLetters.getText().append((CharSequence) text);
                return;
            case 3:
            case 4:
                this.mDigits.getText().append((CharSequence) text);
                return;
            default:
                return;
        }
    }

    public void clearText() {
        Editable text = this.mLetters.getText();
        text.clear();
        Editable text2 = this.mDigits.getText();
        text2.clear();
        if (this.mIsQwerty) {
            setMode(1);
        } else {
            setMode(4);
        }
    }

    public void setLettersWatcher(TextWatcher watcher) {
        Spannable text = this.mLetters.getText();
        Spannable span = text;
        span.setSpan(watcher, 0, text.length(), 18);
    }

    public void setDigitsWatcher(TextWatcher watcher) {
        Spannable text = this.mDigits.getText();
        Spannable span = text;
        span.setSpan(watcher, 0, text.length(), 18);
    }

    public void setFilterWatcher(TextWatcher watcher) {
        if (this.mMode != 4) {
            setLettersWatcher(watcher);
        } else {
            setDigitsWatcher(watcher);
        }
    }

    public void removeFilterWatcher(TextWatcher watcher) {
        Spannable text;
        if (this.mMode != 4) {
            text = this.mLetters.getText();
        } else {
            text = this.mDigits.getText();
        }
        text.removeSpan(watcher);
    }

    protected void onModeChange(int oldMode, int newMode) {
    }
}
