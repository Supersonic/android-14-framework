package android.p008os;

import android.app.slice.SliceItem;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* renamed from: android.os.PersistableBundle */
/* loaded from: classes3.dex */
public final class PersistableBundle extends BaseBundle implements Cloneable, Parcelable, XmlUtils.WriteMapCallback {
    public static final Parcelable.Creator<PersistableBundle> CREATOR;
    public static final PersistableBundle EMPTY;
    private static final String TAG = "PersistableBundle";
    private static final String TAG_PERSISTABLEMAP = "pbundle_as_map";

    static {
        PersistableBundle persistableBundle = new PersistableBundle();
        EMPTY = persistableBundle;
        persistableBundle.mMap = ArrayMap.EMPTY;
        CREATOR = new Parcelable.Creator<PersistableBundle>() { // from class: android.os.PersistableBundle.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PersistableBundle createFromParcel(Parcel in) {
                return in.readPersistableBundle();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PersistableBundle[] newArray(int size) {
                return new PersistableBundle[size];
            }
        };
    }

    public static boolean isValidType(Object value) {
        return (value instanceof Integer) || (value instanceof Long) || (value instanceof Double) || (value instanceof String) || (value instanceof int[]) || (value instanceof long[]) || (value instanceof double[]) || (value instanceof String[]) || (value instanceof PersistableBundle) || value == null || (value instanceof Boolean) || (value instanceof boolean[]);
    }

    public PersistableBundle() {
        this.mFlags = 1;
    }

    public PersistableBundle(int capacity) {
        super(capacity);
        this.mFlags = 1;
    }

    public PersistableBundle(PersistableBundle b) {
        super(b);
        this.mFlags = b.mFlags;
    }

    public PersistableBundle(Bundle b) {
        this(b, true);
    }

    private PersistableBundle(Bundle b, boolean throwException) {
        this(b.getItemwiseMap(), throwException);
    }

    private PersistableBundle(ArrayMap<String, Object> map, boolean throwException) {
        this.mFlags = 1;
        putAll(map);
        int N = this.mMap.size();
        for (int i = N - 1; i >= 0; i--) {
            Object value = this.mMap.valueAt(i);
            if (value instanceof ArrayMap) {
                this.mMap.setValueAt(i, new PersistableBundle((ArrayMap) value, throwException));
            } else if (value instanceof Bundle) {
                this.mMap.setValueAt(i, new PersistableBundle((Bundle) value, throwException));
            } else if (isValidType(value)) {
                continue;
            } else {
                String errorMsg = "Bad value in PersistableBundle key=" + this.mMap.keyAt(i) + " value=" + value;
                if (throwException) {
                    throw new IllegalArgumentException(errorMsg);
                }
                Slog.wtfStack(TAG, errorMsg);
                this.mMap.removeAt(i);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PersistableBundle(Parcel parcelledData, int length) {
        super(parcelledData, length);
        this.mFlags = 1;
    }

    PersistableBundle(PersistableBundle from, boolean deep) {
        super(from, deep);
    }

    public static PersistableBundle forPair(String key, String value) {
        PersistableBundle b = new PersistableBundle(1);
        b.putString(key, value);
        return b;
    }

    public Object clone() {
        return new PersistableBundle(this);
    }

    public PersistableBundle deepCopy() {
        return new PersistableBundle(this, true);
    }

    public void putPersistableBundle(String key, PersistableBundle value) {
        unparcel();
        this.mMap.put(key, value);
    }

    public PersistableBundle getPersistableBundle(String key) {
        unparcel();
        Object o = this.mMap.get(key);
        if (o == null) {
            return null;
        }
        try {
            return (PersistableBundle) o;
        } catch (ClassCastException e) {
            typeWarning(key, o, "Bundle", e);
            return null;
        }
    }

    @Override // com.android.internal.util.XmlUtils.WriteMapCallback
    public void writeUnknownObject(Object v, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (v instanceof PersistableBundle) {
            out.startTag(null, TAG_PERSISTABLEMAP);
            out.attribute(null, "name", name);
            ((PersistableBundle) v).saveToXml(out);
            out.endTag(null, TAG_PERSISTABLEMAP);
            return;
        }
        throw new XmlPullParserException("Unknown Object o=" + v);
    }

    public void saveToXml(XmlSerializer out) throws IOException, XmlPullParserException {
        saveToXml(XmlUtils.makeTyped(out));
    }

    public void saveToXml(TypedXmlSerializer out) throws IOException, XmlPullParserException {
        unparcel();
        for (int i = this.mMap.size() - 1; i >= 0; i--) {
            Object value = this.mMap.valueAt(i);
            if (!isValidType(value)) {
                Slog.m96e(TAG, "Dropping bad data before persisting: " + this.mMap.keyAt(i) + "=" + value);
                this.mMap.removeAt(i);
            }
        }
        XmlUtils.writeMapXml(this.mMap, out, this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.PersistableBundle$MyReadMapCallback */
    /* loaded from: classes3.dex */
    public static class MyReadMapCallback implements XmlUtils.ReadMapCallback {
        MyReadMapCallback() {
        }

        @Override // com.android.internal.util.XmlUtils.ReadMapCallback
        public Object readThisUnknownObjectXml(TypedXmlPullParser in, String tag) throws XmlPullParserException, IOException {
            if (PersistableBundle.TAG_PERSISTABLEMAP.equals(tag)) {
                return PersistableBundle.restoreFromXml(in);
            }
            throw new XmlPullParserException("Unknown tag=" + tag);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        boolean oldAllowFds = parcel.pushAllowFds(false);
        try {
            writeToParcelInner(parcel, flags);
        } finally {
            parcel.restoreAllowFds(oldAllowFds);
        }
    }

    public static PersistableBundle restoreFromXml(XmlPullParser in) throws IOException, XmlPullParserException {
        return restoreFromXml(XmlUtils.makeTyped(in));
    }

    public static PersistableBundle restoreFromXml(TypedXmlPullParser in) throws IOException, XmlPullParserException {
        int event;
        int outerDepth = in.getDepth();
        String startTag = in.getName();
        String[] tagName = new String[1];
        do {
            event = in.next();
            if (event == 1 || (event == 3 && in.getDepth() >= outerDepth)) {
                return new PersistableBundle();
            }
        } while (event != 2);
        return new PersistableBundle((ArrayMap<String, Object>) XmlUtils.readThisArrayMapXml(in, startTag, tagName, new MyReadMapCallback()), false);
    }

    public synchronized String toString() {
        if (this.mParcelledData != null) {
            if (isEmptyParcel()) {
                return "PersistableBundle[EMPTY_PARCEL]";
            }
            return "PersistableBundle[mParcelledData.dataSize=" + this.mParcelledData.dataSize() + NavigationBarInflaterView.SIZE_MOD_END;
        }
        return "PersistableBundle[" + this.mMap.toString() + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public synchronized String toShortString() {
        if (this.mParcelledData != null) {
            if (isEmptyParcel()) {
                return "EMPTY_PARCEL";
            }
            return "mParcelledData.dataSize=" + this.mParcelledData.dataSize();
        }
        return this.mMap.toString();
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        if (this.mParcelledData != null) {
            if (isEmptyParcel()) {
                proto.write(1120986464257L, 0);
            } else {
                proto.write(1120986464257L, this.mParcelledData.dataSize());
            }
        } else {
            proto.write(1138166333442L, this.mMap.toString());
        }
        proto.end(token);
    }

    public void writeToStream(OutputStream outputStream) throws IOException {
        TypedXmlSerializer serializer = Xml.newFastSerializer();
        serializer.setOutput(outputStream, StandardCharsets.UTF_8.name());
        serializer.startTag(null, SliceItem.FORMAT_BUNDLE);
        try {
            saveToXml(serializer);
            serializer.endTag(null, SliceItem.FORMAT_BUNDLE);
            serializer.flush();
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }

    public static PersistableBundle readFromStream(InputStream inputStream) throws IOException {
        try {
            TypedXmlPullParser parser = Xml.newFastPullParser();
            parser.setInput(inputStream, StandardCharsets.UTF_8.name());
            parser.next();
            return restoreFromXml(parser);
        } catch (XmlPullParserException e) {
            throw new IOException(e);
        }
    }
}
