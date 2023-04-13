package android.media;

import android.graphics.Color;
import android.util.Log;
import com.android.internal.midi.MidiConstants;
import com.android.net.module.util.NetworkStackConstants;
import com.google.android.mms.pdu.CharacterSets;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
/* compiled from: Cea708CaptionRenderer.java */
/* loaded from: classes2.dex */
class Cea708CCParser {
    public static final int CAPTION_EMIT_TYPE_BUFFER = 1;
    public static final int CAPTION_EMIT_TYPE_COMMAND_CLW = 4;
    public static final int CAPTION_EMIT_TYPE_COMMAND_CWX = 3;
    public static final int CAPTION_EMIT_TYPE_COMMAND_DFX = 16;
    public static final int CAPTION_EMIT_TYPE_COMMAND_DLC = 10;
    public static final int CAPTION_EMIT_TYPE_COMMAND_DLW = 8;
    public static final int CAPTION_EMIT_TYPE_COMMAND_DLY = 9;
    public static final int CAPTION_EMIT_TYPE_COMMAND_DSW = 5;
    public static final int CAPTION_EMIT_TYPE_COMMAND_HDW = 6;
    public static final int CAPTION_EMIT_TYPE_COMMAND_RST = 11;
    public static final int CAPTION_EMIT_TYPE_COMMAND_SPA = 12;
    public static final int CAPTION_EMIT_TYPE_COMMAND_SPC = 13;
    public static final int CAPTION_EMIT_TYPE_COMMAND_SPL = 14;
    public static final int CAPTION_EMIT_TYPE_COMMAND_SWA = 15;
    public static final int CAPTION_EMIT_TYPE_COMMAND_TGW = 7;
    public static final int CAPTION_EMIT_TYPE_CONTROL = 2;
    private static final boolean DEBUG = false;
    private static final String MUSIC_NOTE_CHAR = new String("♫".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    private static final String TAG = "Cea708CCParser";
    private final StringBuffer mBuffer = new StringBuffer();
    private int mCommand = 0;
    private DisplayListener mListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public interface DisplayListener {
        void emitEvent(CaptionEvent captionEvent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Cea708CCParser(DisplayListener listener) {
        this.mListener = new DisplayListener() { // from class: android.media.Cea708CCParser.1
            @Override // android.media.Cea708CCParser.DisplayListener
            public void emitEvent(CaptionEvent event) {
            }
        };
        if (listener != null) {
            this.mListener = listener;
        }
    }

    private void emitCaptionEvent(CaptionEvent captionEvent) {
        emitCaptionBuffer();
        this.mListener.emitEvent(captionEvent);
    }

    private void emitCaptionBuffer() {
        if (this.mBuffer.length() > 0) {
            this.mListener.emitEvent(new CaptionEvent(1, this.mBuffer.toString()));
            this.mBuffer.setLength(0);
        }
    }

    public void parse(byte[] data) {
        int pos = 0;
        while (pos < data.length) {
            pos = parseServiceBlockData(data, pos);
        }
        emitCaptionBuffer();
    }

    private int parseServiceBlockData(byte[] data, int pos) {
        int i = data[pos] & 255;
        this.mCommand = i;
        int pos2 = pos + 1;
        if (i == 16) {
            return parseExt1(data, pos2);
        }
        if (i >= 0 && i <= 31) {
            return parseC0(data, pos2);
        }
        if (i >= 128 && i <= 159) {
            return parseC1(data, pos2);
        }
        if (i >= 32 && i <= 127) {
            return parseG0(data, pos2);
        }
        if (i >= 160 && i <= 255) {
            return parseG1(data, pos2);
        }
        return pos2;
    }

    private int parseC0(byte[] data, int pos) {
        int i = this.mCommand;
        if (i >= 24 && i <= 31) {
            if (i == 24) {
                try {
                    if (data[pos] == 0) {
                        this.mBuffer.append((char) data[pos + 1]);
                    } else {
                        String value = new String(Arrays.copyOfRange(data, pos, pos + 2), CharacterSets.MIMENAME_EUC_KR);
                        this.mBuffer.append(value);
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.m109e(TAG, "P16 Code - Could not find supported encoding", e);
                }
            }
            return pos + 2;
        } else if (i >= 16 && i <= 23) {
            return pos + 1;
        } else {
            switch (i) {
                case 0:
                default:
                    return pos;
                case 3:
                    emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char) i)));
                    return pos;
                case 8:
                    emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char) i)));
                    return pos;
                case 12:
                    emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char) i)));
                    return pos;
                case 13:
                    this.mBuffer.append('\n');
                    return pos;
                case 14:
                    emitCaptionEvent(new CaptionEvent(2, Character.valueOf((char) i)));
                    return pos;
            }
        }
    }

    private int parseC1(byte[] data, int pos) {
        int windowBitmap = this.mCommand;
        switch (windowBitmap) {
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
                int windowId = windowBitmap - 128;
                emitCaptionEvent(new CaptionEvent(3, Integer.valueOf(windowId)));
                break;
            case 136:
                int pos2 = pos + 1;
                emitCaptionEvent(new CaptionEvent(4, Integer.valueOf(data[pos] & 255)));
                return pos2;
            case 137:
                int pos3 = pos + 1;
                emitCaptionEvent(new CaptionEvent(5, Integer.valueOf(data[pos] & 255)));
                return pos3;
            case 138:
                int pos4 = pos + 1;
                emitCaptionEvent(new CaptionEvent(6, Integer.valueOf(data[pos] & 255)));
                return pos4;
            case 139:
                int pos5 = pos + 1;
                emitCaptionEvent(new CaptionEvent(7, Integer.valueOf(data[pos] & 255)));
                return pos5;
            case 140:
                int tenthsOfSeconds = data[pos];
                int pos6 = pos + 1;
                emitCaptionEvent(new CaptionEvent(8, Integer.valueOf(tenthsOfSeconds & 255)));
                return pos6;
            case 141:
                int tenthsOfSeconds2 = data[pos] & 255;
                int pos7 = pos + 1;
                emitCaptionEvent(new CaptionEvent(9, Integer.valueOf(tenthsOfSeconds2)));
                return pos7;
            case 142:
                emitCaptionEvent(new CaptionEvent(10, null));
                break;
            case 143:
                emitCaptionEvent(new CaptionEvent(11, null));
                break;
            case 144:
                int opacity = data[pos];
                int textTag = (opacity & 240) >> 4;
                int penSize = data[pos] & 3;
                int penOffset = (data[pos] & 12) >> 2;
                boolean italic = (data[pos + 1] & 128) != 0;
                boolean underline = (data[pos + 1] & 64) != 0;
                int edgeType = (data[pos + 1] & 56) >> 3;
                int fontTag = 7 & data[pos + 1];
                int pos8 = pos + 2;
                emitCaptionEvent(new CaptionEvent(12, new CaptionPenAttr(penSize, penOffset, textTag, fontTag, edgeType, underline, italic)));
                return pos8;
            case 145:
                int row = data[pos];
                int opacity2 = (row & 192) >> 6;
                int red = (data[pos] & 48) >> 4;
                int green = (data[pos] & 12) >> 2;
                int blue = data[pos] & 3;
                CaptionColor foregroundColor = new CaptionColor(opacity2, red, green, blue);
                int pos9 = pos + 1;
                int opacity3 = (data[pos9] & MidiConstants.STATUS_PROGRAM_CHANGE) >> 6;
                int red2 = (data[pos9] & 48) >> 4;
                int green2 = (data[pos9] & 12) >> 2;
                int blue2 = data[pos9] & 3;
                CaptionColor backgroundColor = new CaptionColor(opacity3, red2, green2, blue2);
                int pos10 = pos9 + 1;
                int red3 = (data[pos10] & 48) >> 4;
                int green3 = (12 & data[pos10]) >> 2;
                int blue3 = data[pos10] & 3;
                CaptionColor edgeColor = new CaptionColor(0, red3, green3, blue3);
                int pos11 = pos10 + 1;
                emitCaptionEvent(new CaptionEvent(13, new CaptionPenColor(foregroundColor, backgroundColor, edgeColor)));
                return pos11;
            case 146:
                int row2 = data[pos] & MidiConstants.STATUS_CHANNEL_MASK;
                int column = data[pos + 1] & 63;
                int pos12 = pos + 2;
                emitCaptionEvent(new CaptionEvent(14, new CaptionPenLocation(row2, column)));
                return pos12;
            case 151:
                int windowId2 = data[pos];
                int opacity4 = (windowId2 & 192) >> 6;
                int red4 = (data[pos] & 48) >> 4;
                int green4 = (data[pos] & 12) >> 2;
                int blue4 = data[pos] & 3;
                CaptionColor fillColor = new CaptionColor(opacity4, red4, green4, blue4);
                int borderType = ((data[pos + 2] & 128) >> 5) | ((data[pos + 1] & MidiConstants.STATUS_PROGRAM_CHANGE) >> 6);
                int red5 = (data[pos + 1] & 48) >> 4;
                int green5 = (data[pos + 1] & 12) >> 2;
                int blue5 = data[pos + 1] & 3;
                CaptionColor borderColor = new CaptionColor(0, red5, green5, blue5);
                boolean wordWrap = (data[pos + 2] & 64) != 0;
                int printDirection = (data[pos + 2] & 48) >> 4;
                int scrollDirection = (data[pos + 2] & 12) >> 2;
                int justify = data[pos + 2] & 3;
                int effectSpeed = (data[pos + 3] & 240) >> 4;
                int effectDirection = (12 & data[pos + 3]) >> 2;
                int displayEffect = 3 & data[pos + 3];
                int pos13 = pos + 4;
                emitCaptionEvent(new CaptionEvent(15, new CaptionWindowAttr(fillColor, borderColor, borderType, wordWrap, printDirection, scrollDirection, justify, effectDirection, effectSpeed, displayEffect)));
                return pos13;
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
                int windowId3 = windowBitmap - 152;
                boolean visible = (data[pos] & NetworkStackConstants.TCPHDR_URG) != 0;
                boolean rowLock = (data[pos] & 16) != 0;
                boolean columnLock = (data[pos] & 8) != 0;
                int priority = data[pos] & 7;
                boolean relativePositioning = (data[pos + 1] & 128) != 0;
                int anchorVertical = data[pos + 1] & Byte.MAX_VALUE;
                int anchorHorizontal = data[pos + 2] & 255;
                int anchorId = (data[pos + 3] & 240) >> 4;
                int rowCount = 15 & data[pos + 3];
                int columnCount = data[pos + 4] & 63;
                int windowStyle = (data[pos + 5] & 56) >> 3;
                int penStyle = 7 & data[pos + 5];
                int pos14 = pos + 6;
                emitCaptionEvent(new CaptionEvent(16, new CaptionWindow(windowId3, visible, rowLock, columnLock, priority, relativePositioning, anchorVertical, anchorHorizontal, anchorId, rowCount, columnCount, penStyle, windowStyle)));
                return pos14;
        }
        return pos;
    }

    private int parseG0(byte[] data, int pos) {
        int i = this.mCommand;
        if (i == 127) {
            this.mBuffer.append(MUSIC_NOTE_CHAR);
        } else {
            this.mBuffer.append((char) i);
        }
        return pos;
    }

    private int parseG1(byte[] data, int pos) {
        this.mBuffer.append((char) this.mCommand);
        return pos;
    }

    private int parseExt1(byte[] data, int pos) {
        int i = data[pos] & 255;
        this.mCommand = i;
        int pos2 = pos + 1;
        if (i >= 0 && i <= 31) {
            return parseC2(data, pos2);
        }
        if (i >= 128 && i <= 159) {
            return parseC3(data, pos2);
        }
        if (i >= 32 && i <= 127) {
            return parseG2(data, pos2);
        }
        if (i >= 160 && i <= 255) {
            return parseG3(data, pos2);
        }
        return pos2;
    }

    private int parseC2(byte[] data, int pos) {
        int i = this.mCommand;
        if (i < 0 || i > 7) {
            if (i >= 8 && i <= 15) {
                return pos + 1;
            }
            if (i >= 16 && i <= 23) {
                return pos + 2;
            }
            if (i >= 24 && i <= 31) {
                return pos + 3;
            }
            return pos;
        }
        return pos;
    }

    private int parseC3(byte[] data, int pos) {
        int i = this.mCommand;
        if (i >= 128 && i <= 135) {
            return pos + 4;
        }
        if (i >= 136 && i <= 143) {
            return pos + 5;
        }
        return pos;
    }

    private int parseG2(byte[] data, int pos) {
        switch (this.mCommand) {
            case 32:
            case 33:
            case 48:
            default:
                return pos;
        }
    }

    private int parseG3(byte[] data, int pos) {
        return pos;
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    private static class Const {
        public static final int CODE_C0_BS = 8;
        public static final int CODE_C0_CR = 13;
        public static final int CODE_C0_ETX = 3;
        public static final int CODE_C0_EXT1 = 16;
        public static final int CODE_C0_FF = 12;
        public static final int CODE_C0_HCR = 14;
        public static final int CODE_C0_NUL = 0;
        public static final int CODE_C0_P16 = 24;
        public static final int CODE_C0_RANGE_END = 31;
        public static final int CODE_C0_RANGE_START = 0;
        public static final int CODE_C0_SKIP1_RANGE_END = 23;
        public static final int CODE_C0_SKIP1_RANGE_START = 16;
        public static final int CODE_C0_SKIP2_RANGE_END = 31;
        public static final int CODE_C0_SKIP2_RANGE_START = 24;
        public static final int CODE_C1_CLW = 136;
        public static final int CODE_C1_CW0 = 128;
        public static final int CODE_C1_CW1 = 129;
        public static final int CODE_C1_CW2 = 130;
        public static final int CODE_C1_CW3 = 131;
        public static final int CODE_C1_CW4 = 132;
        public static final int CODE_C1_CW5 = 133;
        public static final int CODE_C1_CW6 = 134;
        public static final int CODE_C1_CW7 = 135;
        public static final int CODE_C1_DF0 = 152;
        public static final int CODE_C1_DF1 = 153;
        public static final int CODE_C1_DF2 = 154;
        public static final int CODE_C1_DF3 = 155;
        public static final int CODE_C1_DF4 = 156;
        public static final int CODE_C1_DF5 = 157;
        public static final int CODE_C1_DF6 = 158;
        public static final int CODE_C1_DF7 = 159;
        public static final int CODE_C1_DLC = 142;
        public static final int CODE_C1_DLW = 140;
        public static final int CODE_C1_DLY = 141;
        public static final int CODE_C1_DSW = 137;
        public static final int CODE_C1_HDW = 138;
        public static final int CODE_C1_RANGE_END = 159;
        public static final int CODE_C1_RANGE_START = 128;
        public static final int CODE_C1_RST = 143;
        public static final int CODE_C1_SPA = 144;
        public static final int CODE_C1_SPC = 145;
        public static final int CODE_C1_SPL = 146;
        public static final int CODE_C1_SWA = 151;
        public static final int CODE_C1_TGW = 139;
        public static final int CODE_C2_RANGE_END = 31;
        public static final int CODE_C2_RANGE_START = 0;
        public static final int CODE_C2_SKIP0_RANGE_END = 7;
        public static final int CODE_C2_SKIP0_RANGE_START = 0;
        public static final int CODE_C2_SKIP1_RANGE_END = 15;
        public static final int CODE_C2_SKIP1_RANGE_START = 8;
        public static final int CODE_C2_SKIP2_RANGE_END = 23;
        public static final int CODE_C2_SKIP2_RANGE_START = 16;
        public static final int CODE_C2_SKIP3_RANGE_END = 31;
        public static final int CODE_C2_SKIP3_RANGE_START = 24;
        public static final int CODE_C3_RANGE_END = 159;
        public static final int CODE_C3_RANGE_START = 128;
        public static final int CODE_C3_SKIP4_RANGE_END = 135;
        public static final int CODE_C3_SKIP4_RANGE_START = 128;
        public static final int CODE_C3_SKIP5_RANGE_END = 143;
        public static final int CODE_C3_SKIP5_RANGE_START = 136;
        public static final int CODE_G0_MUSICNOTE = 127;
        public static final int CODE_G0_RANGE_END = 127;
        public static final int CODE_G0_RANGE_START = 32;
        public static final int CODE_G1_RANGE_END = 255;
        public static final int CODE_G1_RANGE_START = 160;
        public static final int CODE_G2_BLK = 48;
        public static final int CODE_G2_NBTSP = 33;
        public static final int CODE_G2_RANGE_END = 127;
        public static final int CODE_G2_RANGE_START = 32;
        public static final int CODE_G2_TSP = 32;
        public static final int CODE_G3_CC = 160;
        public static final int CODE_G3_RANGE_END = 255;
        public static final int CODE_G3_RANGE_START = 160;

        private Const() {
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionColor {
        public static final int OPACITY_FLASH = 1;
        public static final int OPACITY_SOLID = 0;
        public static final int OPACITY_TRANSLUCENT = 2;
        public static final int OPACITY_TRANSPARENT = 3;
        public final int blue;
        public final int green;
        public final int opacity;
        public final int red;
        private static final int[] COLOR_MAP = {0, 15, 240, 255};
        private static final int[] OPACITY_MAP = {255, 254, 128, 0};

        public CaptionColor(int opacity, int red, int green, int blue) {
            this.opacity = opacity;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public int getArgbValue() {
            int i = OPACITY_MAP[this.opacity];
            int[] iArr = COLOR_MAP;
            return Color.argb(i, iArr[this.red], iArr[this.green], iArr[this.blue]);
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionEvent {
        public final Object obj;
        public final int type;

        public CaptionEvent(int type, Object obj) {
            this.type = type;
            this.obj = obj;
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionPenAttr {
        public static final int OFFSET_NORMAL = 1;
        public static final int OFFSET_SUBSCRIPT = 0;
        public static final int OFFSET_SUPERSCRIPT = 2;
        public static final int PEN_SIZE_LARGE = 2;
        public static final int PEN_SIZE_SMALL = 0;
        public static final int PEN_SIZE_STANDARD = 1;
        public final int edgeType;
        public final int fontTag;
        public final boolean italic;
        public final int penOffset;
        public final int penSize;
        public final int textTag;
        public final boolean underline;

        public CaptionPenAttr(int penSize, int penOffset, int textTag, int fontTag, int edgeType, boolean underline, boolean italic) {
            this.penSize = penSize;
            this.penOffset = penOffset;
            this.textTag = textTag;
            this.fontTag = fontTag;
            this.edgeType = edgeType;
            this.underline = underline;
            this.italic = italic;
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionPenColor {
        public final CaptionColor backgroundColor;
        public final CaptionColor edgeColor;
        public final CaptionColor foregroundColor;

        public CaptionPenColor(CaptionColor foregroundColor, CaptionColor backgroundColor, CaptionColor edgeColor) {
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.edgeColor = edgeColor;
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionPenLocation {
        public final int column;
        public final int row;

        public CaptionPenLocation(int row, int column) {
            this.row = row;
            this.column = column;
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionWindowAttr {
        public final CaptionColor borderColor;
        public final int borderType;
        public final int displayEffect;
        public final int effectDirection;
        public final int effectSpeed;
        public final CaptionColor fillColor;
        public final int justify;
        public final int printDirection;
        public final int scrollDirection;
        public final boolean wordWrap;

        public CaptionWindowAttr(CaptionColor fillColor, CaptionColor borderColor, int borderType, boolean wordWrap, int printDirection, int scrollDirection, int justify, int effectDirection, int effectSpeed, int displayEffect) {
            this.fillColor = fillColor;
            this.borderColor = borderColor;
            this.borderType = borderType;
            this.wordWrap = wordWrap;
            this.printDirection = printDirection;
            this.scrollDirection = scrollDirection;
            this.justify = justify;
            this.effectDirection = effectDirection;
            this.effectSpeed = effectSpeed;
            this.displayEffect = displayEffect;
        }
    }

    /* compiled from: Cea708CaptionRenderer.java */
    /* loaded from: classes2.dex */
    public static class CaptionWindow {
        public final int anchorHorizontal;
        public final int anchorId;
        public final int anchorVertical;
        public final int columnCount;
        public final boolean columnLock;

        /* renamed from: id */
        public final int f264id;
        public final int penStyle;
        public final int priority;
        public final boolean relativePositioning;
        public final int rowCount;
        public final boolean rowLock;
        public final boolean visible;
        public final int windowStyle;

        public CaptionWindow(int id, boolean visible, boolean rowLock, boolean columnLock, int priority, boolean relativePositioning, int anchorVertical, int anchorHorizontal, int anchorId, int rowCount, int columnCount, int penStyle, int windowStyle) {
            this.f264id = id;
            this.visible = visible;
            this.rowLock = rowLock;
            this.columnLock = columnLock;
            this.priority = priority;
            this.relativePositioning = relativePositioning;
            this.anchorVertical = anchorVertical;
            this.anchorHorizontal = anchorHorizontal;
            this.anchorId = anchorId;
            this.rowCount = rowCount;
            this.columnCount = columnCount;
            this.penStyle = penStyle;
            this.windowStyle = windowStyle;
        }
    }
}
