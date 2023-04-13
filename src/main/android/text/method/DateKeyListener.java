package android.text.method;

import com.android.internal.util.ArrayUtils;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
/* loaded from: classes3.dex */
public class DateKeyListener extends NumberKeyListener {
    private static final String SYMBOLS_TO_IGNORE = "yMLd";
    private final char[] mCharacters;
    private final boolean mNeedsAdvancedInput;
    private static final String[] SKELETONS = {"yMd", "yM", "Md"};
    @Deprecated
    public static final char[] CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '/', '-', '.'};
    private static final Object sLock = new Object();
    private static final HashMap<Locale, DateKeyListener> sInstanceCache = new HashMap<>();

    @Override // android.text.method.KeyListener
    public int getInputType() {
        if (this.mNeedsAdvancedInput) {
            return 1;
        }
        return 20;
    }

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return this.mCharacters;
    }

    @Deprecated
    public DateKeyListener() {
        this(null);
    }

    public DateKeyListener(Locale locale) {
        LinkedHashSet<Character> chars = new LinkedHashSet<>();
        boolean success = NumberKeyListener.addDigits(chars, locale) && NumberKeyListener.addFormatCharsFromSkeletons(chars, locale, SKELETONS, SYMBOLS_TO_IGNORE);
        if (success) {
            char[] collectionToArray = NumberKeyListener.collectionToArray(chars);
            this.mCharacters = collectionToArray;
            this.mNeedsAdvancedInput = true ^ ArrayUtils.containsAll(CHARACTERS, collectionToArray);
            return;
        }
        this.mCharacters = CHARACTERS;
        this.mNeedsAdvancedInput = false;
    }

    @Deprecated
    public static DateKeyListener getInstance() {
        return getInstance(null);
    }

    public static DateKeyListener getInstance(Locale locale) {
        DateKeyListener instance;
        synchronized (sLock) {
            HashMap<Locale, DateKeyListener> hashMap = sInstanceCache;
            instance = hashMap.get(locale);
            if (instance == null) {
                instance = new DateKeyListener(locale);
                hashMap.put(locale, instance);
            }
        }
        return instance;
    }
}
