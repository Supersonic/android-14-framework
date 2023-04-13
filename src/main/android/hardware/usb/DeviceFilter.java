package android.hardware.usb;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Slog;
import com.android.internal.util.dump.DualDumpOutputStream;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes2.dex */
public class DeviceFilter {
    private static final String TAG = DeviceFilter.class.getSimpleName();
    public final int mClass;
    public final String mManufacturerName;
    public final int mProductId;
    public final String mProductName;
    public final int mProtocol;
    public final String mSerialNumber;
    public final int mSubclass;
    public final int mVendorId;

    public DeviceFilter(int vid, int pid, int clasz, int subclass, int protocol, String manufacturer, String product, String serialnum) {
        this.mVendorId = vid;
        this.mProductId = pid;
        this.mClass = clasz;
        this.mSubclass = subclass;
        this.mProtocol = protocol;
        this.mManufacturerName = manufacturer;
        this.mProductName = product;
        this.mSerialNumber = serialnum;
    }

    public DeviceFilter(UsbDevice device) {
        this.mVendorId = device.getVendorId();
        this.mProductId = device.getProductId();
        this.mClass = device.getDeviceClass();
        this.mSubclass = device.getDeviceSubclass();
        this.mProtocol = device.getDeviceProtocol();
        this.mManufacturerName = device.getManufacturerName();
        this.mProductName = device.getProductName();
        this.mSerialNumber = device.getSerialNumber();
    }

    public DeviceFilter(DeviceFilter filter) {
        this.mVendorId = filter.mVendorId;
        this.mProductId = filter.mProductId;
        this.mClass = filter.mClass;
        this.mSubclass = filter.mSubclass;
        this.mProtocol = filter.mProtocol;
        this.mManufacturerName = filter.mManufacturerName;
        this.mProductName = filter.mProductName;
        this.mSerialNumber = filter.mSerialNumber;
    }

    public static DeviceFilter read(XmlPullParser parser) throws XmlPullParserException, IOException {
        int radix;
        String value;
        int count;
        XmlPullParser xmlPullParser = parser;
        int count2 = parser.getAttributeCount();
        int i = 0;
        String serialNumber = null;
        String serialNumber2 = null;
        String manufacturerName = null;
        int deviceProtocol = -1;
        int deviceProtocol2 = -1;
        int deviceSubclass = -1;
        int deviceClass = -1;
        int productId = -1;
        while (i < count2) {
            String name = xmlPullParser.getAttributeName(i);
            String value2 = xmlPullParser.getAttributeValue(i);
            if ("manufacturer-name".equals(name)) {
                manufacturerName = value2;
                count = count2;
            } else if ("product-name".equals(name)) {
                serialNumber2 = value2;
                count = count2;
            } else if ("serial-number".equals(name)) {
                serialNumber = value2;
                count = count2;
            } else {
                if (value2 != null && value2.length() > 2 && value2.charAt(0) == '0' && (value2.charAt(1) == 'x' || value2.charAt(1) == 'X')) {
                    radix = 16;
                    value = value2.substring(2);
                } else {
                    radix = 10;
                    value = value2;
                }
                try {
                    int intValue = Integer.parseInt(value, radix);
                    if ("vendor-id".equals(name)) {
                        productId = intValue;
                        count = count2;
                    } else if ("product-id".equals(name)) {
                        deviceClass = intValue;
                        count = count2;
                    } else if ("class".equals(name)) {
                        deviceSubclass = intValue;
                        count = count2;
                    } else if ("subclass".equals(name)) {
                        deviceProtocol2 = intValue;
                        count = count2;
                    } else if (!"protocol".equals(name)) {
                        count = count2;
                    } else {
                        deviceProtocol = intValue;
                        count = count2;
                    }
                } catch (NumberFormatException e) {
                    count = count2;
                    Slog.m95e(TAG, "invalid number for field " + name, e);
                }
            }
            i++;
            xmlPullParser = parser;
            count2 = count;
        }
        return new DeviceFilter(productId, deviceClass, deviceSubclass, deviceProtocol2, deviceProtocol, manufacturerName, serialNumber2, serialNumber);
    }

    public void write(XmlSerializer serializer) throws IOException {
        serializer.startTag(null, "usb-device");
        int i = this.mVendorId;
        if (i != -1) {
            serializer.attribute(null, "vendor-id", Integer.toString(i));
        }
        int i2 = this.mProductId;
        if (i2 != -1) {
            serializer.attribute(null, "product-id", Integer.toString(i2));
        }
        int i3 = this.mClass;
        if (i3 != -1) {
            serializer.attribute(null, "class", Integer.toString(i3));
        }
        int i4 = this.mSubclass;
        if (i4 != -1) {
            serializer.attribute(null, "subclass", Integer.toString(i4));
        }
        int i5 = this.mProtocol;
        if (i5 != -1) {
            serializer.attribute(null, "protocol", Integer.toString(i5));
        }
        String str = this.mManufacturerName;
        if (str != null) {
            serializer.attribute(null, "manufacturer-name", str);
        }
        String str2 = this.mProductName;
        if (str2 != null) {
            serializer.attribute(null, "product-name", str2);
        }
        String str3 = this.mSerialNumber;
        if (str3 != null) {
            serializer.attribute(null, "serial-number", str3);
        }
        serializer.endTag(null, "usb-device");
    }

    private boolean matches(int clasz, int subclass, int protocol) {
        int i;
        int i2;
        int i3 = this.mClass;
        return (i3 == -1 || clasz == i3) && ((i = this.mSubclass) == -1 || subclass == i) && ((i2 = this.mProtocol) == -1 || protocol == i2);
    }

    public boolean matches(UsbDevice device) {
        if (this.mVendorId == -1 || device.getVendorId() == this.mVendorId) {
            if (this.mProductId == -1 || device.getProductId() == this.mProductId) {
                if (this.mManufacturerName == null || device.getManufacturerName() != null) {
                    if (this.mProductName == null || device.getProductName() != null) {
                        if (this.mSerialNumber == null || device.getSerialNumber() != null) {
                            if (this.mManufacturerName == null || device.getManufacturerName() == null || this.mManufacturerName.equals(device.getManufacturerName())) {
                                if (this.mProductName == null || device.getProductName() == null || this.mProductName.equals(device.getProductName())) {
                                    if (this.mSerialNumber == null || device.getSerialNumber() == null || this.mSerialNumber.equals(device.getSerialNumber())) {
                                        if (matches(device.getDeviceClass(), device.getDeviceSubclass(), device.getDeviceProtocol())) {
                                            return true;
                                        }
                                        int count = device.getInterfaceCount();
                                        for (int i = 0; i < count; i++) {
                                            UsbInterface intf = device.getInterface(i);
                                            if (matches(intf.getInterfaceClass(), intf.getInterfaceSubclass(), intf.getInterfaceProtocol())) {
                                                return true;
                                            }
                                        }
                                        return false;
                                    }
                                    return false;
                                }
                                return false;
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public boolean contains(DeviceFilter device) {
        int i = this.mVendorId;
        if (i == -1 || device.mVendorId == i) {
            int i2 = this.mProductId;
            if (i2 == -1 || device.mProductId == i2) {
                String str = this.mManufacturerName;
                if (str == null || Objects.equals(str, device.mManufacturerName)) {
                    String str2 = this.mProductName;
                    if (str2 == null || Objects.equals(str2, device.mProductName)) {
                        String str3 = this.mSerialNumber;
                        if (str3 == null || Objects.equals(str3, device.mSerialNumber)) {
                            return matches(device.mClass, device.mSubclass, device.mProtocol);
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public boolean equals(Object obj) {
        int i;
        int i2;
        int i3;
        int i4;
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        int i5 = this.mVendorId;
        if (i5 == -1 || (i = this.mProductId) == -1 || (i2 = this.mClass) == -1 || (i3 = this.mSubclass) == -1 || (i4 = this.mProtocol) == -1) {
            return false;
        }
        if (obj instanceof DeviceFilter) {
            DeviceFilter filter = (DeviceFilter) obj;
            if (filter.mVendorId == i5 && filter.mProductId == i && filter.mClass == i2 && filter.mSubclass == i3 && filter.mProtocol == i4) {
                String str8 = filter.mManufacturerName;
                if ((str8 == null || this.mManufacturerName != null) && ((str8 != null || this.mManufacturerName == null) && (((str = filter.mProductName) == null || this.mProductName != null) && ((str != null || this.mProductName == null) && (((str2 = filter.mSerialNumber) == null || this.mSerialNumber != null) && (str2 != null || this.mSerialNumber == null)))))) {
                    return (str8 == null || (str7 = this.mManufacturerName) == null || str7.equals(str8)) && ((str3 = filter.mProductName) == null || (str6 = this.mProductName) == null || str6.equals(str3)) && ((str4 = filter.mSerialNumber) == null || (str5 = this.mSerialNumber) == null || str5.equals(str4));
                }
                return false;
            }
            return false;
        } else if (obj instanceof UsbDevice) {
            UsbDevice device = (UsbDevice) obj;
            if (device.getVendorId() == this.mVendorId && device.getProductId() == this.mProductId && device.getDeviceClass() == this.mClass && device.getDeviceSubclass() == this.mSubclass && device.getDeviceProtocol() == this.mProtocol) {
                if ((this.mManufacturerName == null || device.getManufacturerName() != null) && ((this.mManufacturerName != null || device.getManufacturerName() == null) && ((this.mProductName == null || device.getProductName() != null) && ((this.mProductName != null || device.getProductName() == null) && ((this.mSerialNumber == null || device.getSerialNumber() != null) && (this.mSerialNumber != null || device.getSerialNumber() == null)))))) {
                    return (device.getManufacturerName() == null || this.mManufacturerName.equals(device.getManufacturerName())) && (device.getProductName() == null || this.mProductName.equals(device.getProductName())) && (device.getSerialNumber() == null || this.mSerialNumber.equals(device.getSerialNumber()));
                }
                return false;
            }
            return false;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return ((this.mVendorId << 16) | this.mProductId) ^ (((this.mClass << 16) | (this.mSubclass << 8)) | this.mProtocol);
    }

    public String toString() {
        return "DeviceFilter[mVendorId=" + this.mVendorId + ",mProductId=" + this.mProductId + ",mClass=" + this.mClass + ",mSubclass=" + this.mSubclass + ",mProtocol=" + this.mProtocol + ",mManufacturerName=" + this.mManufacturerName + ",mProductName=" + this.mProductName + ",mSerialNumber=" + this.mSerialNumber + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        long token = dump.start(idName, id);
        dump.write("vendor_id", 1120986464257L, this.mVendorId);
        dump.write("product_id", 1120986464258L, this.mProductId);
        dump.write("class", 1120986464259L, this.mClass);
        dump.write("subclass", 1120986464260L, this.mSubclass);
        dump.write("protocol", 1120986464261L, this.mProtocol);
        dump.write("manufacturer_name", 1138166333446L, this.mManufacturerName);
        dump.write("product_name", 1138166333447L, this.mProductName);
        dump.write("serial_number", 1138166333448L, this.mSerialNumber);
        dump.end(token);
    }
}
