package android.view.inputmethod;
/* loaded from: classes4.dex */
public final class InputConnectionCallProto {
    public static final long GET_CURSOR_CAPS_MODE = 1146756268037L;
    public static final long GET_EXTRACTED_TEXT = 1146756268038L;
    public static final long GET_SELECTED_TEXT = 1146756268035L;
    public static final long GET_SURROUNDING_TEXT = 1146756268036L;
    public static final long GET_TEXT_AFTER_CURSOR = 1146756268034L;
    public static final long GET_TEXT_BEFORE_CURSOR = 1146756268033L;

    /* loaded from: classes4.dex */
    public final class GetTextBeforeCursor {
        public static final long FLAGS = 1120986464258L;
        public static final long LENGTH = 1120986464257L;
        public static final long RESULT = 1138166333443L;

        public GetTextBeforeCursor() {
        }
    }

    /* loaded from: classes4.dex */
    public final class GetTextAfterCursor {
        public static final long FLAGS = 1120986464258L;
        public static final long LENGTH = 1120986464257L;
        public static final long RESULT = 1138166333443L;

        public GetTextAfterCursor() {
        }
    }

    /* loaded from: classes4.dex */
    public final class GetSelectedText {
        public static final long FLAGS = 1120986464257L;
        public static final long RESULT = 1138166333442L;

        public GetSelectedText() {
        }
    }

    /* loaded from: classes4.dex */
    public final class GetSurroundingText {
        public static final long AFTER_LENGTH = 1120986464258L;
        public static final long BEFORE_LENGTH = 1120986464257L;
        public static final long FLAGS = 1120986464259L;
        public static final long RESULT = 1146756268036L;

        public GetSurroundingText() {
        }

        /* loaded from: classes4.dex */
        public final class SurroundingText {
            public static final long OFFSET = 1120986464260L;
            public static final long SELECTION_END = 1120986464259L;
            public static final long SELECTION_START = 1120986464258L;
            public static final long TEXT = 1138166333441L;

            public SurroundingText() {
            }
        }
    }

    /* loaded from: classes4.dex */
    public final class GetCursorCapsMode {
        public static final long REQ_MODES = 1120986464257L;
        public static final long RESULT = 1120986464258L;

        public GetCursorCapsMode() {
        }
    }

    /* loaded from: classes4.dex */
    public final class GetExtractedText {
        public static final long FLAGS = 1120986464258L;
        public static final long REQUEST = 1146756268033L;
        public static final long RESULT = 1138166333443L;

        public GetExtractedText() {
        }

        /* loaded from: classes4.dex */
        public final class ExtractedTextRequest {
            public static final long FLAGS = 1120986464258L;
            public static final long HINT_MAX_CHARS = 1120986464260L;
            public static final long HINT_MAX_LINES = 1120986464259L;
            public static final long TOKEN = 1120986464257L;

            public ExtractedTextRequest() {
            }
        }
    }
}
