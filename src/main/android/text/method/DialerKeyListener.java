package android.text.method;

import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
/* loaded from: classes3.dex */
public class DialerKeyListener extends NumberKeyListener {
    public static final char[] CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '#', '*', '+', '-', '(', ')', ',', '/', PhoneNumberUtils.WILD, '.', ' ', ';'};
    private static DialerKeyListener sInstance;

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return CHARACTERS;
    }

    public static DialerKeyListener getInstance() {
        DialerKeyListener dialerKeyListener = sInstance;
        if (dialerKeyListener != null) {
            return dialerKeyListener;
        }
        DialerKeyListener dialerKeyListener2 = new DialerKeyListener();
        sInstance = dialerKeyListener2;
        return dialerKeyListener2;
    }

    @Override // android.text.method.KeyListener
    public int getInputType() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.text.method.NumberKeyListener
    public int lookup(KeyEvent event, Spannable content) {
        int meta = getMetaState(content, event);
        int number = event.getNumber();
        if ((meta & 3) == 0 && number != 0) {
            return number;
        }
        int match = super.lookup(event, content);
        if (match != 0) {
            return match;
        }
        if (meta != 0) {
            KeyCharacterMap.KeyData kd = new KeyCharacterMap.KeyData();
            char[] accepted = getAcceptedChars();
            if (event.getKeyData(kd)) {
                for (int i = 1; i < kd.meta.length; i++) {
                    if (m117ok(accepted, kd.meta[i])) {
                        return kd.meta[i];
                    }
                }
            }
        }
        return number;
    }
}
