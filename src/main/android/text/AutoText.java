package android.text;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.provider.UserDictionary;
import android.view.View;
import com.android.internal.C4057R;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public class AutoText {
    private static final int DEFAULT = 14337;
    private static final int INCREMENT = 1024;
    private static final int RIGHT = 9300;
    private static final int TRIE_C = 0;
    private static final int TRIE_CHILD = 2;
    private static final int TRIE_NEXT = 3;
    private static final char TRIE_NULL = 65535;
    private static final int TRIE_OFF = 1;
    private static final int TRIE_ROOT = 0;
    private static final int TRIE_SIZEOF = 4;
    private static AutoText sInstance = new AutoText(Resources.getSystem());
    private static Object sLock = new Object();
    private Locale mLocale;
    private int mSize;
    private String mText;
    private char[] mTrie;
    private char mTrieUsed;

    private AutoText(Resources resources) {
        this.mLocale = resources.getConfiguration().locale;
        init(resources);
    }

    private static AutoText getInstance(View view) {
        AutoText instance;
        Resources res = view.getContext().getResources();
        Locale locale = res.getConfiguration().locale;
        synchronized (sLock) {
            instance = sInstance;
            if (!locale.equals(instance.mLocale)) {
                instance = new AutoText(res);
                sInstance = instance;
            }
        }
        return instance;
    }

    public static String get(CharSequence src, int start, int end, View view) {
        return getInstance(view).lookup(src, start, end);
    }

    public static int getSize(View view) {
        return getInstance(view).getSize();
    }

    private int getSize() {
        return this.mSize;
    }

    private String lookup(CharSequence src, int start, int end) {
        char c = this.mTrie[0];
        for (int i = start; i < end; i++) {
            char c2 = src.charAt(i);
            while (true) {
                if (c == 65535) {
                    break;
                }
                char[] cArr = this.mTrie;
                if (c2 != cArr[c + 0]) {
                    c = cArr[c + 3];
                } else if (i == end - 1 && cArr[c + 1] != 65535) {
                    char c3 = cArr[c + 1];
                    int len = this.mText.charAt(c3);
                    return this.mText.substring(c3 + 1, c3 + 1 + len);
                } else {
                    c = cArr[c + 2];
                }
            }
            if (c == 65535) {
                return null;
            }
        }
        return null;
    }

    private void init(Resources r) {
        char off;
        XmlResourceParser parser = r.getXml(C4057R.xml.autotext);
        StringBuilder right = new StringBuilder((int) RIGHT);
        char[] cArr = new char[14337];
        this.mTrie = cArr;
        cArr[0] = TRIE_NULL;
        this.mTrieUsed = (char) 1;
        try {
            try {
                XmlUtils.beginDocument(parser, "words");
                while (true) {
                    XmlUtils.nextElement(parser);
                    String element = parser.getName();
                    if (element == null || !element.equals(UserDictionary.Words.WORD)) {
                        break;
                    }
                    String src = parser.getAttributeValue(null, "src");
                    if (parser.next() == 4) {
                        String dest = parser.getText();
                        if (dest.equals("")) {
                            off = 0;
                        } else {
                            off = (char) right.length();
                            right.append((char) dest.length());
                            right.append(dest);
                        }
                        add(src, off);
                    }
                }
                r.flushLayoutCache();
                parser.close();
                this.mText = right.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (XmlPullParserException e2) {
                throw new RuntimeException(e2);
            }
        } catch (Throwable th) {
            parser.close();
            throw th;
        }
    }

    private void add(String src, char off) {
        int slen = src.length();
        int herep = 0;
        this.mSize++;
        for (int i = 0; i < slen; i++) {
            char c = src.charAt(i);
            boolean found = false;
            while (true) {
                char[] cArr = this.mTrie;
                char c2 = cArr[herep];
                if (c2 == 65535) {
                    break;
                } else if (c != cArr[c2 + 0]) {
                    herep = c2 + 3;
                } else if (i == slen - 1) {
                    cArr[c2 + 1] = off;
                    return;
                } else {
                    herep = c2 + 2;
                    found = true;
                }
            }
            if (!found) {
                char node = newTrieNode();
                char[] cArr2 = this.mTrie;
                cArr2[herep] = node;
                cArr2[node + 0] = c;
                cArr2[cArr2[herep] + 1] = TRIE_NULL;
                cArr2[cArr2[herep] + 3] = TRIE_NULL;
                cArr2[cArr2[herep] + 2] = TRIE_NULL;
                if (i == slen - 1) {
                    cArr2[cArr2[herep] + 1] = off;
                    return;
                }
                herep = cArr2[herep] + 2;
            }
        }
    }

    private char newTrieNode() {
        int i = this.mTrieUsed + 4;
        char[] cArr = this.mTrie;
        if (i > cArr.length) {
            char[] copy = new char[cArr.length + 1024];
            System.arraycopy(cArr, 0, copy, 0, cArr.length);
            this.mTrie = copy;
        }
        char ret = this.mTrieUsed;
        this.mTrieUsed = (char) (this.mTrieUsed + 4);
        return ret;
    }
}
