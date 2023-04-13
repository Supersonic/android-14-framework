package android.util;

import com.android.internal.util.StringPool;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class JsonReader implements Closeable {
    private static final String FALSE = "false";
    private static final String TRUE = "true";

    /* renamed from: in */
    private final Reader f469in;
    private String name;
    private boolean skipping;
    private JsonToken token;
    private String value;
    private int valueLength;
    private int valuePos;
    private final StringPool stringPool = new StringPool();
    private boolean lenient = false;
    private final char[] buffer = new char[1024];
    private int pos = 0;
    private int limit = 0;
    private int bufferStartLine = 1;
    private int bufferStartColumn = 1;
    private final List<JsonScope> stack = new ArrayList();

    public JsonReader(Reader in) {
        push(JsonScope.EMPTY_DOCUMENT);
        this.skipping = false;
        if (in == null) {
            throw new NullPointerException("in == null");
        }
        this.f469in = in;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        expect(JsonToken.BEGIN_ARRAY);
    }

    public void endArray() throws IOException {
        expect(JsonToken.END_ARRAY);
    }

    public void beginObject() throws IOException {
        expect(JsonToken.BEGIN_OBJECT);
    }

    public void endObject() throws IOException {
        expect(JsonToken.END_OBJECT);
    }

    private void expect(JsonToken expected) throws IOException {
        peek();
        if (this.token != expected) {
            throw new IllegalStateException("Expected " + expected + " but was " + peek());
        }
        advance();
    }

    public boolean hasNext() throws IOException {
        peek();
        return (this.token == JsonToken.END_OBJECT || this.token == JsonToken.END_ARRAY) ? false : true;
    }

    public JsonToken peek() throws IOException {
        JsonToken jsonToken = this.token;
        if (jsonToken != null) {
            return jsonToken;
        }
        switch (C34401.$SwitchMap$android$util$JsonScope[peekStack().ordinal()]) {
            case 1:
                replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                JsonToken firstToken = nextValue();
                if (!this.lenient && this.token != JsonToken.BEGIN_ARRAY && this.token != JsonToken.BEGIN_OBJECT) {
                    throw new IOException("Expected JSON document to start with '[' or '{' but was " + this.token);
                }
                return firstToken;
            case 2:
                return nextInArray(true);
            case 3:
                return nextInArray(false);
            case 4:
                return nextInObject(true);
            case 5:
                return objectValue();
            case 6:
                return nextInObject(false);
            case 7:
                try {
                    JsonToken token = nextValue();
                    if (this.lenient) {
                        return token;
                    }
                    throw syntaxError("Expected EOF");
                } catch (EOFException e) {
                    JsonToken jsonToken2 = JsonToken.END_DOCUMENT;
                    this.token = jsonToken2;
                    return jsonToken2;
                }
            case 8:
                throw new IllegalStateException("JsonReader is closed");
            default:
                throw new AssertionError();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.util.JsonReader$1 */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class C34401 {
        static final /* synthetic */ int[] $SwitchMap$android$util$JsonScope;

        static {
            int[] iArr = new int[JsonScope.values().length];
            $SwitchMap$android$util$JsonScope = iArr;
            try {
                iArr[JsonScope.EMPTY_DOCUMENT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.EMPTY_ARRAY.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.NONEMPTY_ARRAY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.EMPTY_OBJECT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.DANGLING_NAME.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.NONEMPTY_OBJECT.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.NONEMPTY_DOCUMENT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.CLOSED.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private JsonToken advance() throws IOException {
        peek();
        JsonToken result = this.token;
        this.token = null;
        this.value = null;
        this.name = null;
        return result;
    }

    public String nextName() throws IOException {
        peek();
        if (this.token != JsonToken.NAME) {
            throw new IllegalStateException("Expected a name but was " + peek());
        }
        String result = this.name;
        advance();
        return result;
    }

    public String nextString() throws IOException {
        peek();
        if (this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected a string but was " + peek());
        }
        String result = this.value;
        advance();
        return result;
    }

    public boolean nextBoolean() throws IOException {
        peek();
        if (this.token != JsonToken.BOOLEAN) {
            throw new IllegalStateException("Expected a boolean but was " + this.token);
        }
        boolean result = this.value == TRUE;
        advance();
        return result;
    }

    public void nextNull() throws IOException {
        peek();
        if (this.token != JsonToken.NULL) {
            throw new IllegalStateException("Expected null but was " + this.token);
        }
        advance();
    }

    public double nextDouble() throws IOException {
        peek();
        if (this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected a double but was " + this.token);
        }
        double result = Double.parseDouble(this.value);
        advance();
        return result;
    }

    public long nextLong() throws IOException {
        long result;
        peek();
        if (this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected a long but was " + this.token);
        }
        try {
            result = Long.parseLong(this.value);
        } catch (NumberFormatException e) {
            double asDouble = Double.parseDouble(this.value);
            long result2 = (long) asDouble;
            if (result2 != asDouble) {
                throw new NumberFormatException(this.value);
            }
            result = result2;
        }
        advance();
        return result;
    }

    public int nextInt() throws IOException {
        int result;
        peek();
        if (this.token != JsonToken.STRING && this.token != JsonToken.NUMBER) {
            throw new IllegalStateException("Expected an int but was " + this.token);
        }
        try {
            result = Integer.parseInt(this.value);
        } catch (NumberFormatException e) {
            double asDouble = Double.parseDouble(this.value);
            int result2 = (int) asDouble;
            if (result2 != asDouble) {
                throw new NumberFormatException(this.value);
            }
            result = result2;
        }
        advance();
        return result;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.value = null;
        this.token = null;
        this.stack.clear();
        this.stack.add(JsonScope.CLOSED);
        this.f469in.close();
    }

    public void skipValue() throws IOException {
        this.skipping = true;
        try {
            if (!hasNext() || peek() == JsonToken.END_DOCUMENT) {
                throw new IllegalStateException("No element left to skip");
            }
            int count = 0;
            do {
                JsonToken token = advance();
                if (token != JsonToken.BEGIN_ARRAY && token != JsonToken.BEGIN_OBJECT) {
                    if (token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT) {
                        count--;
                        continue;
                    }
                }
                count++;
            } while (count != 0);
        } finally {
            this.skipping = false;
        }
    }

    private JsonScope peekStack() {
        List<JsonScope> list = this.stack;
        return list.get(list.size() - 1);
    }

    private JsonScope pop() {
        List<JsonScope> list = this.stack;
        return list.remove(list.size() - 1);
    }

    private void push(JsonScope newTop) {
        this.stack.add(newTop);
    }

    private void replaceTop(JsonScope newTop) {
        List<JsonScope> list = this.stack;
        list.set(list.size() - 1, newTop);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private JsonToken nextInArray(boolean firstElement) throws IOException {
        if (firstElement) {
            replaceTop(JsonScope.NONEMPTY_ARRAY);
        } else {
            switch (nextNonWhitespace()) {
                case 44:
                    break;
                case 59:
                    checkLenient();
                    break;
                case 93:
                    pop();
                    JsonToken jsonToken = JsonToken.END_ARRAY;
                    this.token = jsonToken;
                    return jsonToken;
                default:
                    throw syntaxError("Unterminated array");
            }
        }
        switch (nextNonWhitespace()) {
            case 44:
            case 59:
                break;
            default:
                this.pos--;
                return nextValue();
            case 93:
                if (firstElement) {
                    pop();
                    JsonToken jsonToken2 = JsonToken.END_ARRAY;
                    this.token = jsonToken2;
                    return jsonToken2;
                }
                break;
        }
        checkLenient();
        this.pos--;
        this.value = "null";
        JsonToken jsonToken3 = JsonToken.NULL;
        this.token = jsonToken3;
        return jsonToken3;
    }

    private JsonToken nextInObject(boolean firstElement) throws IOException {
        if (firstElement) {
            switch (nextNonWhitespace()) {
                case 125:
                    pop();
                    JsonToken jsonToken = JsonToken.END_OBJECT;
                    this.token = jsonToken;
                    return jsonToken;
                default:
                    this.pos--;
                    break;
            }
        } else {
            switch (nextNonWhitespace()) {
                case 44:
                case 59:
                    break;
                case 125:
                    pop();
                    JsonToken jsonToken2 = JsonToken.END_OBJECT;
                    this.token = jsonToken2;
                    return jsonToken2;
                default:
                    throw syntaxError("Unterminated object");
            }
        }
        int quote = nextNonWhitespace();
        switch (quote) {
            case 39:
                checkLenient();
            case 34:
                this.name = nextString((char) quote);
                break;
            default:
                checkLenient();
                this.pos--;
                String nextLiteral = nextLiteral(false);
                this.name = nextLiteral;
                if (nextLiteral.isEmpty()) {
                    throw syntaxError("Expected name");
                }
                break;
        }
        replaceTop(JsonScope.DANGLING_NAME);
        JsonToken jsonToken3 = JsonToken.NAME;
        this.token = jsonToken3;
        return jsonToken3;
    }

    private JsonToken objectValue() throws IOException {
        switch (nextNonWhitespace()) {
            case 58:
                break;
            default:
                throw syntaxError("Expected ':'");
            case 61:
                checkLenient();
                if (this.pos < this.limit || fillBuffer(1)) {
                    char[] cArr = this.buffer;
                    int i = this.pos;
                    if (cArr[i] == '>') {
                        this.pos = i + 1;
                        break;
                    }
                }
                break;
        }
        replaceTop(JsonScope.NONEMPTY_OBJECT);
        return nextValue();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private JsonToken nextValue() throws IOException {
        int c = nextNonWhitespace();
        switch (c) {
            case 34:
                break;
            case 39:
                checkLenient();
                break;
            case 91:
                push(JsonScope.EMPTY_ARRAY);
                JsonToken jsonToken = JsonToken.BEGIN_ARRAY;
                this.token = jsonToken;
                return jsonToken;
            case 123:
                push(JsonScope.EMPTY_OBJECT);
                JsonToken jsonToken2 = JsonToken.BEGIN_OBJECT;
                this.token = jsonToken2;
                return jsonToken2;
            default:
                this.pos--;
                return readLiteral();
        }
        this.value = nextString((char) c);
        JsonToken jsonToken3 = JsonToken.STRING;
        this.token = jsonToken3;
        return jsonToken3;
    }

    private boolean fillBuffer(int minimum) throws IOException {
        int i;
        int i2;
        int i3;
        int i4 = 0;
        while (true) {
            i = this.pos;
            if (i4 >= i) {
                break;
            }
            if (this.buffer[i4] == '\n') {
                this.bufferStartLine++;
                this.bufferStartColumn = 1;
            } else {
                this.bufferStartColumn++;
            }
            i4++;
        }
        int i5 = this.limit;
        if (i5 != i) {
            int i6 = i5 - i;
            this.limit = i6;
            char[] cArr = this.buffer;
            System.arraycopy(cArr, i, cArr, 0, i6);
        } else {
            this.limit = 0;
        }
        this.pos = 0;
        do {
            Reader reader = this.f469in;
            char[] cArr2 = this.buffer;
            int i7 = this.limit;
            int total = reader.read(cArr2, i7, cArr2.length - i7);
            if (total == -1) {
                return false;
            }
            i2 = this.limit + total;
            this.limit = i2;
            if (this.bufferStartLine == 1 && (i3 = this.bufferStartColumn) == 1 && i2 > 0 && this.buffer[0] == 65279) {
                this.pos++;
                this.bufferStartColumn = i3 - 1;
                continue;
            }
        } while (i2 < minimum);
        return true;
    }

    private int getLineNumber() {
        int result = this.bufferStartLine;
        for (int i = 0; i < this.pos; i++) {
            if (this.buffer[i] == '\n') {
                result++;
            }
        }
        return result;
    }

    private int getColumnNumber() {
        int result = this.bufferStartColumn;
        for (int i = 0; i < this.pos; i++) {
            if (this.buffer[i] == '\n') {
                result = 1;
            } else {
                result++;
            }
        }
        return result;
    }

    private int nextNonWhitespace() throws IOException {
        while (true) {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                int i2 = i + 1;
                this.pos = i2;
                char c = cArr[i];
                switch (c) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ':
                        break;
                    case '#':
                        checkLenient();
                        skipToEndOfLine();
                        break;
                    case '/':
                        if (i2 == this.limit && !fillBuffer(1)) {
                            return c;
                        }
                        checkLenient();
                        char[] cArr2 = this.buffer;
                        int i3 = this.pos;
                        char peek = cArr2[i3];
                        switch (peek) {
                            case '*':
                                this.pos = i3 + 1;
                                if (!skipTo("*/")) {
                                    throw syntaxError("Unterminated comment");
                                }
                                this.pos += 2;
                                continue;
                            case '/':
                                this.pos = i3 + 1;
                                skipToEndOfLine();
                                continue;
                            default:
                                return c;
                        }
                    default:
                        return c;
                }
            } else {
                throw new EOFException("End of input");
            }
        }
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        char c;
        do {
            if (this.pos < this.limit || fillBuffer(1)) {
                char[] cArr = this.buffer;
                int i = this.pos;
                this.pos = i + 1;
                c = cArr[i];
                if (c == '\r') {
                    return;
                }
            } else {
                return;
            }
        } while (c != '\n');
    }

    private boolean skipTo(String toFind) throws IOException {
        while (true) {
            if (this.pos + toFind.length() <= this.limit || fillBuffer(toFind.length())) {
                for (int c = 0; c < toFind.length(); c++) {
                    if (this.buffer[this.pos + c] != toFind.charAt(c)) {
                        break;
                    }
                }
                return true;
            }
            return false;
            int c2 = this.pos;
            this.pos = c2 + 1;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0050, code lost:
        if (r0 != null) goto L29;
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0052, code lost:
        r0 = new java.lang.StringBuilder();
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0058, code lost:
        r0.append(r7.buffer, r1, r7.pos - r1);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private String nextString(char quote) throws IOException {
        StringBuilder builder = null;
        do {
            int start = this.pos;
            while (true) {
                int i = this.pos;
                if (i >= this.limit) {
                    break;
                }
                char[] cArr = this.buffer;
                int i2 = i + 1;
                this.pos = i2;
                char c = cArr[i];
                if (c == quote) {
                    if (this.skipping) {
                        return "skipped!";
                    }
                    if (builder == null) {
                        return this.stringPool.get(cArr, start, (i2 - start) - 1);
                    }
                    builder.append(cArr, start, (i2 - start) - 1);
                    return builder.toString();
                } else if (c == '\\') {
                    if (builder == null) {
                        builder = new StringBuilder();
                    }
                    builder.append(this.buffer, start, (this.pos - start) - 1);
                    builder.append(readEscapeCharacter());
                    start = this.pos;
                }
            }
        } while (fillBuffer(1));
        throw syntaxError("Unterminated string");
    }

    private String nextLiteral(boolean assignOffsetsOnly) throws IOException {
        String result;
        StringBuilder builder = null;
        this.valuePos = -1;
        this.valueLength = 0;
        int i = 0;
        while (true) {
            int i2 = this.pos;
            if (i2 + i < this.limit) {
                switch (this.buffer[i2 + i]) {
                    case '\t':
                    case '\n':
                    case '\f':
                    case '\r':
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                }
            } else if (i < this.buffer.length) {
                if (!fillBuffer(i + 1)) {
                    this.buffer[this.limit] = 0;
                }
            } else {
                if (builder == null) {
                    builder = new StringBuilder();
                }
                builder.append(this.buffer, this.pos, i);
                this.valueLength += i;
                this.pos += i;
                i = 0;
                if (!fillBuffer(1)) {
                }
            }
        }
        if (assignOffsetsOnly && builder == null) {
            this.valuePos = this.pos;
            result = null;
        } else if (this.skipping) {
            result = "skipped!";
        } else if (builder == null) {
            result = this.stringPool.get(this.buffer, this.pos, i);
        } else {
            builder.append(this.buffer, this.pos, i);
            result = builder.toString();
        }
        this.valueLength += i;
        this.pos += i;
        return result;
    }

    public String toString() {
        return getClass().getSimpleName() + " near " + ((Object) getSnippet());
    }

    private char readEscapeCharacter() throws IOException {
        if (this.pos == this.limit && !fillBuffer(1)) {
            throw syntaxError("Unterminated escape sequence");
        }
        char[] cArr = this.buffer;
        int i = this.pos;
        int i2 = i + 1;
        this.pos = i2;
        char escaped = cArr[i];
        switch (escaped) {
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'u':
                if (i2 + 4 <= this.limit || fillBuffer(4)) {
                    String hex = this.stringPool.get(this.buffer, this.pos, 4);
                    this.pos += 4;
                    return (char) Integer.parseInt(hex, 16);
                }
                throw syntaxError("Unterminated escape sequence");
            default:
                return escaped;
        }
    }

    private JsonToken readLiteral() throws IOException {
        this.value = nextLiteral(true);
        if (this.valueLength == 0) {
            throw syntaxError("Expected literal value");
        }
        JsonToken decodeLiteral = decodeLiteral();
        this.token = decodeLiteral;
        if (decodeLiteral == JsonToken.STRING) {
            checkLenient();
        }
        return this.token;
    }

    private JsonToken decodeLiteral() throws IOException {
        char[] cArr;
        char c;
        char[] cArr2;
        char c2;
        char[] cArr3;
        char c3;
        int i = this.valuePos;
        if (i == -1) {
            return JsonToken.STRING;
        }
        int i2 = this.valueLength;
        if (i2 == 4 && (('n' == (c3 = (cArr3 = this.buffer)[i]) || 'N' == c3) && (('u' == cArr3[i + 1] || 'U' == cArr3[i + 1]) && (('l' == cArr3[i + 2] || 'L' == cArr3[i + 2]) && ('l' == cArr3[i + 3] || 'L' == cArr3[i + 3]))))) {
            this.value = "null";
            return JsonToken.NULL;
        } else if (i2 == 4 && (('t' == (c2 = (cArr2 = this.buffer)[i]) || 'T' == c2) && (('r' == cArr2[i + 1] || 'R' == cArr2[i + 1]) && (('u' == cArr2[i + 2] || 'U' == cArr2[i + 2]) && ('e' == cArr2[i + 3] || 'E' == cArr2[i + 3]))))) {
            this.value = TRUE;
            return JsonToken.BOOLEAN;
        } else if (i2 == 5 && (('f' == (c = (cArr = this.buffer)[i]) || 'F' == c) && (('a' == cArr[i + 1] || 'A' == cArr[i + 1]) && (('l' == cArr[i + 2] || 'L' == cArr[i + 2]) && (('s' == cArr[i + 3] || 'S' == cArr[i + 3]) && ('e' == cArr[i + 4] || 'E' == cArr[i + 4])))))) {
            this.value = FALSE;
            return JsonToken.BOOLEAN;
        } else {
            this.value = this.stringPool.get(this.buffer, i, i2);
            return decodeNumber(this.buffer, this.valuePos, this.valueLength);
        }
    }

    private JsonToken decodeNumber(char[] chars, int offset, int length) {
        int i;
        char c;
        int i2 = offset;
        char c2 = chars[i2];
        if (c2 == '-') {
            i2++;
            c2 = chars[i2];
        }
        if (c2 == '0') {
            i = i2 + 1;
            c = chars[i];
        } else if (c2 >= '1' && c2 <= '9') {
            i = i2 + 1;
            c = chars[i];
            while (c >= '0' && c <= '9') {
                i++;
                c = chars[i];
            }
        } else {
            return JsonToken.STRING;
        }
        if (c == '.') {
            i++;
            c = chars[i];
            while (c >= '0' && c <= '9') {
                i++;
                c = chars[i];
            }
        }
        if (c == 'e' || c == 'E') {
            int i3 = i + 1;
            char c3 = chars[i3];
            if (c3 == '+' || c3 == '-') {
                i3++;
                c3 = chars[i3];
            }
            if (c3 >= '0' && c3 <= '9') {
                i = i3 + 1;
                char c4 = chars[i];
                while (c4 >= '0' && c4 <= '9') {
                    i++;
                    c4 = chars[i];
                }
            } else {
                return JsonToken.STRING;
            }
        }
        if (i == offset + length) {
            return JsonToken.NUMBER;
        }
        return JsonToken.STRING;
    }

    private IOException syntaxError(String message) throws IOException {
        throw new MalformedJsonException(message + " at line " + getLineNumber() + " column " + getColumnNumber());
    }

    private CharSequence getSnippet() {
        StringBuilder snippet = new StringBuilder();
        int beforePos = Math.min(this.pos, 20);
        snippet.append(this.buffer, this.pos - beforePos, beforePos);
        int afterPos = Math.min(this.limit - this.pos, 20);
        snippet.append(this.buffer, this.pos, afterPos);
        return snippet;
    }
}
