package android.view;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public class ViewHierarchyEncoder {
    private static final byte SIG_BOOLEAN = 90;
    private static final byte SIG_BYTE = 66;
    private static final byte SIG_DOUBLE = 68;
    private static final short SIG_END_MAP = 0;
    private static final byte SIG_FLOAT = 70;
    private static final byte SIG_INT = 73;
    private static final byte SIG_LONG = 74;
    private static final byte SIG_MAP = 77;
    private static final byte SIG_SHORT = 83;
    private static final byte SIG_STRING = 82;
    private final DataOutputStream mStream;
    private final Map<String, Short> mPropertyNames = new HashMap(200);
    private short mPropertyId = 1;
    private Charset mCharset = Charset.forName("utf-8");
    private boolean mUserPropertiesEnabled = true;

    public ViewHierarchyEncoder(ByteArrayOutputStream stream) {
        this.mStream = new DataOutputStream(stream);
    }

    public void setUserPropertiesEnabled(boolean enabled) {
        this.mUserPropertiesEnabled = enabled;
    }

    public void beginObject(Object o) {
        startPropertyMap();
        addProperty("meta:__name__", o.getClass().getName());
        addProperty("meta:__hash__", o.hashCode());
    }

    public void endObject() {
        endPropertyMap();
    }

    public void endStream() {
        startPropertyMap();
        addProperty("__name__", "propertyIndex");
        for (Map.Entry<String, Short> entry : this.mPropertyNames.entrySet()) {
            writeShort(entry.getValue().shortValue());
            writeString(entry.getKey());
        }
        endPropertyMap();
    }

    public void addProperty(String name, boolean v) {
        writeShort(createPropertyIndex(name));
        writeBoolean(v);
    }

    public void addProperty(String name, short s) {
        writeShort(createPropertyIndex(name));
        writeShort(s);
    }

    public void addProperty(String name, int v) {
        writeShort(createPropertyIndex(name));
        writeInt(v);
    }

    public void addProperty(String name, float v) {
        writeShort(createPropertyIndex(name));
        writeFloat(v);
    }

    public void addProperty(String name, String s) {
        writeShort(createPropertyIndex(name));
        writeString(s);
    }

    public void addUserProperty(String name, String s) {
        if (this.mUserPropertiesEnabled) {
            addProperty(name, s);
        }
    }

    public void addPropertyKey(String name) {
        writeShort(createPropertyIndex(name));
    }

    private short createPropertyIndex(String name) {
        Short index = this.mPropertyNames.get(name);
        if (index == null) {
            short s = this.mPropertyId;
            this.mPropertyId = (short) (s + 1);
            index = Short.valueOf(s);
            this.mPropertyNames.put(name, index);
        }
        return index.shortValue();
    }

    private void startPropertyMap() {
        try {
            this.mStream.write(77);
        } catch (IOException e) {
        }
    }

    private void endPropertyMap() {
        writeShort((short) 0);
    }

    private void writeBoolean(boolean v) {
        try {
            this.mStream.write(90);
            this.mStream.write(v ? 1 : 0);
        } catch (IOException e) {
        }
    }

    private void writeShort(short s) {
        try {
            this.mStream.write(83);
            this.mStream.writeShort(s);
        } catch (IOException e) {
        }
    }

    private void writeInt(int i) {
        try {
            this.mStream.write(73);
            this.mStream.writeInt(i);
        } catch (IOException e) {
        }
    }

    private void writeFloat(float v) {
        try {
            this.mStream.write(70);
            this.mStream.writeFloat(v);
        } catch (IOException e) {
        }
    }

    private void writeString(String s) {
        if (s == null) {
            s = "";
        }
        try {
            this.mStream.write(82);
            byte[] bytes = s.getBytes(this.mCharset);
            short len = (short) Math.min(bytes.length, 32767);
            this.mStream.writeShort(len);
            this.mStream.write(bytes, 0, len);
        } catch (IOException e) {
        }
    }
}
