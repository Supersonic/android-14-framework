package android.media;

import android.graphics.Rect;
import android.p008os.Parcel;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
/* loaded from: classes2.dex */
public final class TimedText {
    private static final int FIRST_PRIVATE_KEY = 101;
    private static final int FIRST_PUBLIC_KEY = 1;
    private static final int KEY_BACKGROUND_COLOR_RGBA = 3;
    private static final int KEY_DISPLAY_FLAGS = 1;
    private static final int KEY_END_CHAR = 104;
    private static final int KEY_FONT_ID = 105;
    private static final int KEY_FONT_SIZE = 106;
    private static final int KEY_GLOBAL_SETTING = 101;
    private static final int KEY_HIGHLIGHT_COLOR_RGBA = 4;
    private static final int KEY_LOCAL_SETTING = 102;
    private static final int KEY_SCROLL_DELAY = 5;
    private static final int KEY_START_CHAR = 103;
    private static final int KEY_START_TIME = 7;
    private static final int KEY_STRUCT_BLINKING_TEXT_LIST = 8;
    private static final int KEY_STRUCT_FONT_LIST = 9;
    private static final int KEY_STRUCT_HIGHLIGHT_LIST = 10;
    private static final int KEY_STRUCT_HYPER_TEXT_LIST = 11;
    private static final int KEY_STRUCT_JUSTIFICATION = 15;
    private static final int KEY_STRUCT_KARAOKE_LIST = 12;
    private static final int KEY_STRUCT_STYLE_LIST = 13;
    private static final int KEY_STRUCT_TEXT = 16;
    private static final int KEY_STRUCT_TEXT_POS = 14;
    private static final int KEY_STYLE_FLAGS = 2;
    private static final int KEY_TEXT_COLOR_RGBA = 107;
    private static final int KEY_WRAP_TEXT = 6;
    private static final int LAST_PRIVATE_KEY = 107;
    private static final int LAST_PUBLIC_KEY = 16;
    private static final String TAG = "TimedText";
    private int mBackgroundColorRGBA;
    private List<CharPos> mBlinkingPosList;
    private int mDisplayFlags;
    private List<Font> mFontList;
    private int mHighlightColorRGBA;
    private List<CharPos> mHighlightPosList;
    private List<HyperText> mHyperTextList;
    private Justification mJustification;
    private List<Karaoke> mKaraokeList;
    private final HashMap<Integer, Object> mKeyObjectMap;
    private int mScrollDelay;
    private List<Style> mStyleList;
    private Rect mTextBounds;
    private String mTextChars;
    private int mWrapText;

    /* loaded from: classes2.dex */
    public static final class CharPos {
        public final int endChar;
        public final int startChar;

        public CharPos(int startChar, int endChar) {
            this.startChar = startChar;
            this.endChar = endChar;
        }
    }

    /* loaded from: classes2.dex */
    public static final class Justification {
        public final int horizontalJustification;
        public final int verticalJustification;

        public Justification(int horizontal, int vertical) {
            this.horizontalJustification = horizontal;
            this.verticalJustification = vertical;
        }
    }

    /* loaded from: classes2.dex */
    public static final class Style {
        public final int colorRGBA;
        public final int endChar;
        public final int fontID;
        public final int fontSize;
        public final boolean isBold;
        public final boolean isItalic;
        public final boolean isUnderlined;
        public final int startChar;

        public Style(int startChar, int endChar, int fontId, boolean isBold, boolean isItalic, boolean isUnderlined, int fontSize, int colorRGBA) {
            this.startChar = startChar;
            this.endChar = endChar;
            this.fontID = fontId;
            this.isBold = isBold;
            this.isItalic = isItalic;
            this.isUnderlined = isUnderlined;
            this.fontSize = fontSize;
            this.colorRGBA = colorRGBA;
        }
    }

    /* loaded from: classes2.dex */
    public static final class Font {

        /* renamed from: ID */
        public final int f279ID;
        public final String name;

        public Font(int id, String name) {
            this.f279ID = id;
            this.name = name;
        }
    }

    /* loaded from: classes2.dex */
    public static final class Karaoke {
        public final int endChar;
        public final int endTimeMs;
        public final int startChar;
        public final int startTimeMs;

        public Karaoke(int startTimeMs, int endTimeMs, int startChar, int endChar) {
            this.startTimeMs = startTimeMs;
            this.endTimeMs = endTimeMs;
            this.startChar = startChar;
            this.endChar = endChar;
        }
    }

    /* loaded from: classes2.dex */
    public static final class HyperText {
        public final String URL;
        public final String altString;
        public final int endChar;
        public final int startChar;

        public HyperText(int startChar, int endChar, String url, String alt) {
            this.startChar = startChar;
            this.endChar = endChar;
            this.URL = url;
            this.altString = alt;
        }
    }

    public TimedText(Parcel parcel) {
        HashMap<Integer, Object> hashMap = new HashMap<>();
        this.mKeyObjectMap = hashMap;
        this.mDisplayFlags = -1;
        this.mBackgroundColorRGBA = -1;
        this.mHighlightColorRGBA = -1;
        this.mScrollDelay = -1;
        this.mWrapText = -1;
        this.mBlinkingPosList = null;
        this.mHighlightPosList = null;
        this.mKaraokeList = null;
        this.mFontList = null;
        this.mStyleList = null;
        this.mHyperTextList = null;
        this.mTextBounds = null;
        this.mTextChars = null;
        if (!parseParcel(parcel)) {
            hashMap.clear();
            throw new IllegalArgumentException("parseParcel() fails");
        }
    }

    public TimedText(String text, Rect bounds) {
        this.mKeyObjectMap = new HashMap<>();
        this.mDisplayFlags = -1;
        this.mBackgroundColorRGBA = -1;
        this.mHighlightColorRGBA = -1;
        this.mScrollDelay = -1;
        this.mWrapText = -1;
        this.mBlinkingPosList = null;
        this.mHighlightPosList = null;
        this.mKaraokeList = null;
        this.mFontList = null;
        this.mStyleList = null;
        this.mHyperTextList = null;
        this.mTextBounds = null;
        this.mTextChars = null;
        this.mTextChars = text;
        this.mTextBounds = bounds;
    }

    public String getText() {
        return this.mTextChars;
    }

    public Rect getBounds() {
        return this.mTextBounds;
    }

    private boolean parseParcel(Parcel parcel) {
        parcel.setDataPosition(0);
        if (parcel.dataAvail() == 0) {
            return false;
        }
        int type = parcel.readInt();
        if (type == 102) {
            int type2 = parcel.readInt();
            if (type2 != 7) {
                return false;
            }
            int mStartTimeMs = parcel.readInt();
            this.mKeyObjectMap.put(Integer.valueOf(type2), Integer.valueOf(mStartTimeMs));
            if (parcel.readInt() != 16) {
                return false;
            }
            parcel.readInt();
            byte[] text = parcel.createByteArray();
            if (text == null || text.length == 0) {
                this.mTextChars = null;
            } else {
                this.mTextChars = new String(text);
            }
        } else if (type != 101) {
            Log.m104w(TAG, "Invalid timed text key found: " + type);
            return false;
        }
        while (parcel.dataAvail() > 0) {
            int key = parcel.readInt();
            if (!isValidKey(key)) {
                Log.m104w(TAG, "Invalid timed text key found: " + key);
                return false;
            }
            Object object = null;
            switch (key) {
                case 1:
                    int readInt = parcel.readInt();
                    this.mDisplayFlags = readInt;
                    object = Integer.valueOf(readInt);
                    break;
                case 3:
                    int readInt2 = parcel.readInt();
                    this.mBackgroundColorRGBA = readInt2;
                    object = Integer.valueOf(readInt2);
                    break;
                case 4:
                    int readInt3 = parcel.readInt();
                    this.mHighlightColorRGBA = readInt3;
                    object = Integer.valueOf(readInt3);
                    break;
                case 5:
                    int readInt4 = parcel.readInt();
                    this.mScrollDelay = readInt4;
                    object = Integer.valueOf(readInt4);
                    break;
                case 6:
                    int readInt5 = parcel.readInt();
                    this.mWrapText = readInt5;
                    object = Integer.valueOf(readInt5);
                    break;
                case 8:
                    readBlinkingText(parcel);
                    object = this.mBlinkingPosList;
                    break;
                case 9:
                    readFont(parcel);
                    object = this.mFontList;
                    break;
                case 10:
                    readHighlight(parcel);
                    object = this.mHighlightPosList;
                    break;
                case 11:
                    readHyperText(parcel);
                    object = this.mHyperTextList;
                    break;
                case 12:
                    readKaraoke(parcel);
                    object = this.mKaraokeList;
                    break;
                case 13:
                    readStyle(parcel);
                    object = this.mStyleList;
                    break;
                case 14:
                    int top = parcel.readInt();
                    int left = parcel.readInt();
                    int bottom = parcel.readInt();
                    int right = parcel.readInt();
                    this.mTextBounds = new Rect(left, top, right, bottom);
                    break;
                case 15:
                    int horizontal = parcel.readInt();
                    int vertical = parcel.readInt();
                    this.mJustification = new Justification(horizontal, vertical);
                    object = this.mJustification;
                    break;
            }
            if (object != null) {
                if (this.mKeyObjectMap.containsKey(Integer.valueOf(key))) {
                    this.mKeyObjectMap.remove(Integer.valueOf(key));
                }
                this.mKeyObjectMap.put(Integer.valueOf(key), object);
            }
        }
        return true;
    }

    private void readStyle(Parcel parcel) {
        boolean endOfStyle = false;
        int startChar = -1;
        int endChar = -1;
        int fontId = -1;
        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderlined = false;
        int fontSize = -1;
        int colorRGBA = -1;
        while (!endOfStyle && parcel.dataAvail() > 0) {
            int key = parcel.readInt();
            switch (key) {
                case 2:
                    int flags = parcel.readInt();
                    boolean isBold2 = flags % 2 == 1;
                    boolean isItalic2 = flags % 4 >= 2;
                    boolean isUnderlined2 = flags / 4 == 1;
                    isBold = isBold2;
                    isUnderlined = isUnderlined2;
                    isItalic = isItalic2;
                    break;
                case 103:
                    int startChar2 = parcel.readInt();
                    startChar = startChar2;
                    break;
                case 104:
                    int endChar2 = parcel.readInt();
                    endChar = endChar2;
                    break;
                case 105:
                    int fontId2 = parcel.readInt();
                    fontId = fontId2;
                    break;
                case 106:
                    int fontSize2 = parcel.readInt();
                    fontSize = fontSize2;
                    break;
                case 107:
                    int colorRGBA2 = parcel.readInt();
                    colorRGBA = colorRGBA2;
                    break;
                default:
                    parcel.setDataPosition(parcel.dataPosition() - 4);
                    endOfStyle = true;
                    break;
            }
        }
        Style style = new Style(startChar, endChar, fontId, isBold, isItalic, isUnderlined, fontSize, colorRGBA);
        if (this.mStyleList == null) {
            this.mStyleList = new ArrayList();
        }
        this.mStyleList.add(style);
    }

    private void readFont(Parcel parcel) {
        int entryCount = parcel.readInt();
        for (int i = 0; i < entryCount; i++) {
            int id = parcel.readInt();
            int nameLen = parcel.readInt();
            byte[] text = parcel.createByteArray();
            String name = new String(text, 0, nameLen);
            Font font = new Font(id, name);
            if (this.mFontList == null) {
                this.mFontList = new ArrayList();
            }
            this.mFontList.add(font);
        }
    }

    private void readHighlight(Parcel parcel) {
        int startChar = parcel.readInt();
        int endChar = parcel.readInt();
        CharPos pos = new CharPos(startChar, endChar);
        if (this.mHighlightPosList == null) {
            this.mHighlightPosList = new ArrayList();
        }
        this.mHighlightPosList.add(pos);
    }

    private void readKaraoke(Parcel parcel) {
        int entryCount = parcel.readInt();
        for (int i = 0; i < entryCount; i++) {
            int startTimeMs = parcel.readInt();
            int endTimeMs = parcel.readInt();
            int startChar = parcel.readInt();
            int endChar = parcel.readInt();
            Karaoke kara = new Karaoke(startTimeMs, endTimeMs, startChar, endChar);
            if (this.mKaraokeList == null) {
                this.mKaraokeList = new ArrayList();
            }
            this.mKaraokeList.add(kara);
        }
    }

    private void readHyperText(Parcel parcel) {
        int startChar = parcel.readInt();
        int endChar = parcel.readInt();
        int len = parcel.readInt();
        byte[] url = parcel.createByteArray();
        String urlString = new String(url, 0, len);
        int len2 = parcel.readInt();
        byte[] alt = parcel.createByteArray();
        String altString = new String(alt, 0, len2);
        HyperText hyperText = new HyperText(startChar, endChar, urlString, altString);
        if (this.mHyperTextList == null) {
            this.mHyperTextList = new ArrayList();
        }
        this.mHyperTextList.add(hyperText);
    }

    private void readBlinkingText(Parcel parcel) {
        int startChar = parcel.readInt();
        int endChar = parcel.readInt();
        CharPos blinkingPos = new CharPos(startChar, endChar);
        if (this.mBlinkingPosList == null) {
            this.mBlinkingPosList = new ArrayList();
        }
        this.mBlinkingPosList.add(blinkingPos);
    }

    private boolean isValidKey(int key) {
        if ((key >= 1 && key <= 16) || (key >= 101 && key <= 107)) {
            return true;
        }
        return false;
    }

    private boolean containsKey(int key) {
        if (isValidKey(key) && this.mKeyObjectMap.containsKey(Integer.valueOf(key))) {
            return true;
        }
        return false;
    }

    private Set keySet() {
        return this.mKeyObjectMap.keySet();
    }

    private Object getObject(int key) {
        if (containsKey(key)) {
            return this.mKeyObjectMap.get(Integer.valueOf(key));
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }
}
