package android.hardware.usb;

import android.media.midi.MidiDeviceInfo;
import com.android.internal.util.dump.DualDumpOutputStream;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes2.dex */
public class AccessoryFilter {
    public final String mManufacturer;
    public final String mModel;
    public final String mVersion;

    public AccessoryFilter(String manufacturer, String model, String version) {
        this.mManufacturer = manufacturer;
        this.mModel = model;
        this.mVersion = version;
    }

    public AccessoryFilter(UsbAccessory accessory) {
        this.mManufacturer = accessory.getManufacturer();
        this.mModel = accessory.getModel();
        this.mVersion = accessory.getVersion();
    }

    public AccessoryFilter(AccessoryFilter filter) {
        this.mManufacturer = filter.mManufacturer;
        this.mModel = filter.mModel;
        this.mVersion = filter.mVersion;
    }

    public static AccessoryFilter read(XmlPullParser parser) throws XmlPullParserException, IOException {
        String manufacturer = null;
        String model = null;
        String version = null;
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (MidiDeviceInfo.PROPERTY_MANUFACTURER.equals(name)) {
                manufacturer = value;
            } else if ("model".equals(name)) {
                model = value;
            } else if ("version".equals(name)) {
                version = value;
            }
        }
        return new AccessoryFilter(manufacturer, model, version);
    }

    public void write(XmlSerializer serializer) throws IOException {
        serializer.startTag(null, "usb-accessory");
        String str = this.mManufacturer;
        if (str != null) {
            serializer.attribute(null, MidiDeviceInfo.PROPERTY_MANUFACTURER, str);
        }
        String str2 = this.mModel;
        if (str2 != null) {
            serializer.attribute(null, "model", str2);
        }
        String str3 = this.mVersion;
        if (str3 != null) {
            serializer.attribute(null, "version", str3);
        }
        serializer.endTag(null, "usb-accessory");
    }

    public boolean matches(UsbAccessory acc) {
        if (this.mManufacturer == null || acc.getManufacturer().equals(this.mManufacturer)) {
            if (this.mModel == null || acc.getModel().equals(this.mModel)) {
                String str = this.mVersion;
                return str == null || str.equals(acc.getVersion());
            }
            return false;
        }
        return false;
    }

    public boolean contains(AccessoryFilter accessory) {
        String str = this.mManufacturer;
        if (str == null || Objects.equals(accessory.mManufacturer, str)) {
            String str2 = this.mModel;
            if (str2 == null || Objects.equals(accessory.mModel, str2)) {
                String str3 = this.mVersion;
                return str3 == null || Objects.equals(accessory.mVersion, str3);
            }
            return false;
        }
        return false;
    }

    public boolean equals(Object obj) {
        String str = this.mManufacturer;
        if (str == null || this.mModel == null || this.mVersion == null) {
            return false;
        }
        if (obj instanceof AccessoryFilter) {
            AccessoryFilter filter = (AccessoryFilter) obj;
            return str.equals(filter.mManufacturer) && this.mModel.equals(filter.mModel) && this.mVersion.equals(filter.mVersion);
        } else if (obj instanceof UsbAccessory) {
            UsbAccessory accessory = (UsbAccessory) obj;
            return str.equals(accessory.getManufacturer()) && this.mModel.equals(accessory.getModel()) && this.mVersion.equals(accessory.getVersion());
        } else {
            return false;
        }
    }

    public int hashCode() {
        String str = this.mManufacturer;
        int hashCode = str == null ? 0 : str.hashCode();
        String str2 = this.mModel;
        int hashCode2 = hashCode ^ (str2 == null ? 0 : str2.hashCode());
        String str3 = this.mVersion;
        return hashCode2 ^ (str3 != null ? str3.hashCode() : 0);
    }

    public String toString() {
        return "AccessoryFilter[mManufacturer=\"" + this.mManufacturer + "\", mModel=\"" + this.mModel + "\", mVersion=\"" + this.mVersion + "\"]";
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        long token = dump.start(idName, id);
        dump.write(MidiDeviceInfo.PROPERTY_MANUFACTURER, 1138166333441L, this.mManufacturer);
        dump.write("model", 1138166333442L, this.mModel);
        dump.write("version", 1138166333443L, this.mVersion);
        dump.end(token);
    }
}
