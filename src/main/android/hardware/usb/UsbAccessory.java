package android.hardware.usb;

import android.app.ActivityThread;
import android.hardware.usb.IUsbSerialReader;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes2.dex */
public class UsbAccessory implements Parcelable {
    public static final Parcelable.Creator<UsbAccessory> CREATOR = new Parcelable.Creator<UsbAccessory>() { // from class: android.hardware.usb.UsbAccessory.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UsbAccessory createFromParcel(Parcel in) {
            String manufacturer = in.readString();
            String model = in.readString();
            String description = in.readString();
            String version = in.readString();
            String uri = in.readString();
            IUsbSerialReader serialNumberReader = IUsbSerialReader.Stub.asInterface(in.readStrongBinder());
            return new UsbAccessory(manufacturer, model, description, version, uri, serialNumberReader);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UsbAccessory[] newArray(int size) {
            return new UsbAccessory[size];
        }
    };
    public static final int DESCRIPTION_STRING = 2;
    public static final int MANUFACTURER_STRING = 0;
    public static final int MODEL_STRING = 1;
    public static final int SERIAL_STRING = 5;
    private static final String TAG = "UsbAccessory";
    public static final int URI_STRING = 4;
    public static final int VERSION_STRING = 3;
    private final String mDescription;
    private final String mManufacturer;
    private final String mModel;
    private final IUsbSerialReader mSerialNumberReader;
    private final String mUri;
    private final String mVersion;

    public UsbAccessory(String manufacturer, String model, String description, String version, String uri, IUsbSerialReader serialNumberReader) {
        this.mManufacturer = (String) Objects.requireNonNull(manufacturer);
        this.mModel = (String) Objects.requireNonNull(model);
        this.mDescription = description;
        this.mVersion = version;
        this.mUri = uri;
        this.mSerialNumberReader = serialNumberReader;
        if (ActivityThread.isSystem()) {
            Preconditions.checkArgument(serialNumberReader instanceof IUsbSerialReader.Stub);
        }
    }

    @Deprecated
    public UsbAccessory(String manufacturer, String model, String description, String version, String uri, final String serialNumber) {
        this(manufacturer, model, description, version, uri, new IUsbSerialReader.Stub() { // from class: android.hardware.usb.UsbAccessory.1
            @Override // android.hardware.usb.IUsbSerialReader
            public String getSerial(String packageName) {
                return serialNumber;
            }
        });
    }

    public String getManufacturer() {
        return this.mManufacturer;
    }

    public String getModel() {
        return this.mModel;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getVersion() {
        return this.mVersion;
    }

    public String getUri() {
        return this.mUri;
    }

    public String getSerial() {
        try {
            return this.mSerialNumberReader.getSerial(ActivityThread.currentPackageName());
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    private static boolean compare(String s1, String s2) {
        if (s1 == null) {
            return s2 == null;
        }
        return s1.equals(s2);
    }

    public boolean equals(Object obj) {
        if (obj instanceof UsbAccessory) {
            UsbAccessory accessory = (UsbAccessory) obj;
            return compare(this.mManufacturer, accessory.getManufacturer()) && compare(this.mModel, accessory.getModel()) && compare(this.mDescription, accessory.getDescription()) && compare(this.mVersion, accessory.getVersion()) && compare(this.mUri, accessory.getUri()) && compare(getSerial(), accessory.getSerial());
        }
        return false;
    }

    public int hashCode() {
        int hashCode = this.mManufacturer.hashCode() ^ this.mModel.hashCode();
        String str = this.mDescription;
        int hashCode2 = hashCode ^ (str == null ? 0 : str.hashCode());
        String str2 = this.mVersion;
        int hashCode3 = hashCode2 ^ (str2 == null ? 0 : str2.hashCode());
        String str3 = this.mUri;
        return hashCode3 ^ (str3 != null ? str3.hashCode() : 0);
    }

    public String toString() {
        return "UsbAccessory[mManufacturer=" + this.mManufacturer + ", mModel=" + this.mModel + ", mDescription=" + this.mDescription + ", mVersion=" + this.mVersion + ", mUri=" + this.mUri + ", mSerialNumberReader=" + this.mSerialNumberReader + NavigationBarInflaterView.SIZE_MOD_END;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mManufacturer);
        parcel.writeString(this.mModel);
        parcel.writeString(this.mDescription);
        parcel.writeString(this.mVersion);
        parcel.writeString(this.mUri);
        parcel.writeStrongBinder(this.mSerialNumberReader.asBinder());
    }
}
