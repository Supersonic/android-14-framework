package android.util;

import android.bluetooth.hci.BluetoothHciProtoEnums;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public final class JsonWriter implements Closeable {
    private String indent;
    private boolean lenient;
    private final Writer out;
    private String separator;
    private final List<JsonScope> stack;

    public JsonWriter(Writer out) {
        ArrayList arrayList = new ArrayList();
        this.stack = arrayList;
        arrayList.add(JsonScope.EMPTY_DOCUMENT);
        this.separator = ":";
        if (out == null) {
            throw new NullPointerException("out == null");
        }
        this.out = out;
    }

    public void setIndent(String indent) {
        if (indent.isEmpty()) {
            this.indent = null;
            this.separator = ":";
            return;
        }
        this.indent = indent;
        this.separator = ": ";
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return this.lenient;
    }

    public JsonWriter beginArray() throws IOException {
        return open(JsonScope.EMPTY_ARRAY, NavigationBarInflaterView.SIZE_MOD_START);
    }

    public JsonWriter endArray() throws IOException {
        return close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, NavigationBarInflaterView.SIZE_MOD_END);
    }

    public JsonWriter beginObject() throws IOException {
        return open(JsonScope.EMPTY_OBJECT, "{");
    }

    public JsonWriter endObject() throws IOException {
        return close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
    }

    private JsonWriter open(JsonScope empty, String openBracket) throws IOException {
        beforeValue(true);
        this.stack.add(empty);
        this.out.write(openBracket);
        return this;
    }

    private JsonWriter close(JsonScope empty, JsonScope nonempty, String closeBracket) throws IOException {
        JsonScope context = peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException("Nesting problem: " + this.stack);
        }
        List<JsonScope> list = this.stack;
        list.remove(list.size() - 1);
        if (context == nonempty) {
            newline();
        }
        this.out.write(closeBracket);
        return this;
    }

    private JsonScope peek() {
        List<JsonScope> list = this.stack;
        return list.get(list.size() - 1);
    }

    private void replaceTop(JsonScope topOfStack) {
        List<JsonScope> list = this.stack;
        list.set(list.size() - 1, topOfStack);
    }

    public JsonWriter name(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        beforeName();
        string(name);
        return this;
    }

    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        beforeValue(false);
        string(value);
        return this;
    }

    public JsonWriter nullValue() throws IOException {
        beforeValue(false);
        this.out.write("null");
        return this;
    }

    public JsonWriter value(boolean value) throws IOException {
        beforeValue(false);
        this.out.write(value ? "true" : "false");
        return this;
    }

    public JsonWriter value(double value) throws IOException {
        if (!this.lenient && (Double.isNaN(value) || Double.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue(false);
        this.out.append((CharSequence) Double.toString(value));
        return this;
    }

    public JsonWriter value(long value) throws IOException {
        beforeValue(false);
        this.out.write(Long.toString(value));
        return this;
    }

    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return nullValue();
        }
        String string = value.toString();
        if (!this.lenient && (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN"))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        beforeValue(false);
        this.out.append((CharSequence) string);
        return this;
    }

    public void flush() throws IOException {
        this.out.flush();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.out.close();
        if (peek() != JsonScope.NONEMPTY_DOCUMENT) {
            throw new IOException("Incomplete document");
        }
    }

    private void string(String value) throws IOException {
        this.out.write("\"");
        int length = value.length();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\b':
                    this.out.write("\\b");
                    break;
                case '\t':
                    this.out.write("\\t");
                    break;
                case '\n':
                    this.out.write("\\n");
                    break;
                case '\f':
                    this.out.write("\\f");
                    break;
                case '\r':
                    this.out.write("\\r");
                    break;
                case '\"':
                case '\\':
                    this.out.write(92);
                    this.out.write(c);
                    break;
                case BluetoothHciProtoEnums.CMD_BLE_RM_DEV_RESOLVING_LIST /* 8232 */:
                case BluetoothHciProtoEnums.CMD_BLE_CLEAR_RESOLVING_LIST /* 8233 */:
                    this.out.write(String.format("\\u%04x", Integer.valueOf(c)));
                    break;
                default:
                    if (c <= 31) {
                        this.out.write(String.format("\\u%04x", Integer.valueOf(c)));
                        break;
                    } else {
                        this.out.write(c);
                        break;
                    }
            }
        }
        this.out.write("\"");
    }

    private void newline() throws IOException {
        if (this.indent == null) {
            return;
        }
        this.out.write("\n");
        for (int i = 1; i < this.stack.size(); i++) {
            this.out.write(this.indent);
        }
    }

    private void beforeName() throws IOException {
        JsonScope context = peek();
        if (context == JsonScope.NONEMPTY_OBJECT) {
            this.out.write(44);
        } else if (context != JsonScope.EMPTY_OBJECT) {
            throw new IllegalStateException("Nesting problem: " + this.stack);
        }
        newline();
        replaceTop(JsonScope.DANGLING_NAME);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.util.JsonWriter$1 */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class C34411 {
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
                $SwitchMap$android$util$JsonScope[JsonScope.DANGLING_NAME.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$util$JsonScope[JsonScope.NONEMPTY_DOCUMENT.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    private void beforeValue(boolean root) throws IOException {
        switch (C34411.$SwitchMap$android$util$JsonScope[peek().ordinal()]) {
            case 1:
                if (!this.lenient && !root) {
                    throw new IllegalStateException("JSON must start with an array or an object.");
                }
                replaceTop(JsonScope.NONEMPTY_DOCUMENT);
                return;
            case 2:
                replaceTop(JsonScope.NONEMPTY_ARRAY);
                newline();
                return;
            case 3:
                this.out.append(',');
                newline();
                return;
            case 4:
                this.out.append((CharSequence) this.separator);
                replaceTop(JsonScope.NONEMPTY_OBJECT);
                return;
            case 5:
                throw new IllegalStateException("JSON must have only one top-level value.");
            default:
                throw new IllegalStateException("Nesting problem: " + this.stack);
        }
    }
}
