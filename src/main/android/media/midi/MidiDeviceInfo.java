package android.media.midi;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public final class MidiDeviceInfo implements Parcelable {
    public static final Parcelable.Creator<MidiDeviceInfo> CREATOR = new Parcelable.Creator<MidiDeviceInfo>() { // from class: android.media.midi.MidiDeviceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MidiDeviceInfo createFromParcel(Parcel in) {
            int type = in.readInt();
            int id = in.readInt();
            int inputPortCount = in.readInt();
            int outputPortCount = in.readInt();
            String[] inputPortNames = in.createStringArray();
            String[] outputPortNames = in.createStringArray();
            boolean isPrivate = in.readInt() == 1;
            int defaultProtocol = in.readInt();
            in.readBundle();
            Bundle properties = in.readBundle();
            return new MidiDeviceInfo(type, id, inputPortCount, outputPortCount, inputPortNames, outputPortNames, properties, isPrivate, defaultProtocol);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MidiDeviceInfo[] newArray(int size) {
            return new MidiDeviceInfo[size];
        }
    };
    public static final String PROPERTY_ALSA_CARD = "alsa_card";
    public static final String PROPERTY_ALSA_DEVICE = "alsa_device";
    public static final String PROPERTY_BLUETOOTH_DEVICE = "bluetooth_device";
    public static final String PROPERTY_MANUFACTURER = "manufacturer";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_PRODUCT = "product";
    public static final String PROPERTY_SERIAL_NUMBER = "serial_number";
    public static final String PROPERTY_SERVICE_INFO = "service_info";
    public static final String PROPERTY_USB_DEVICE = "usb_device";
    public static final String PROPERTY_VERSION = "version";
    public static final int PROTOCOL_UMP_MIDI_1_0_UP_TO_128_BITS = 3;
    public static final int PROTOCOL_UMP_MIDI_1_0_UP_TO_128_BITS_AND_JRTS = 4;
    public static final int PROTOCOL_UMP_MIDI_1_0_UP_TO_64_BITS = 1;
    public static final int PROTOCOL_UMP_MIDI_1_0_UP_TO_64_BITS_AND_JRTS = 2;
    public static final int PROTOCOL_UMP_MIDI_2_0 = 17;
    public static final int PROTOCOL_UMP_MIDI_2_0_AND_JRTS = 18;
    public static final int PROTOCOL_UMP_USE_MIDI_CI = 0;
    public static final int PROTOCOL_UNKNOWN = -1;
    private static final String TAG = "MidiDeviceInfo";
    public static final int TYPE_BLUETOOTH = 3;
    public static final int TYPE_USB = 1;
    public static final int TYPE_VIRTUAL = 2;
    private final int mDefaultProtocol;
    private final int mId;
    private final int mInputPortCount;
    private final String[] mInputPortNames;
    private final boolean mIsPrivate;
    private final int mOutputPortCount;
    private final String[] mOutputPortNames;
    private final Bundle mProperties;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Protocol {
    }

    /* loaded from: classes2.dex */
    public static final class PortInfo {
        public static final int TYPE_INPUT = 1;
        public static final int TYPE_OUTPUT = 2;
        private final String mName;
        private final int mPortNumber;
        private final int mPortType;

        PortInfo(int type, int portNumber, String name) {
            this.mPortType = type;
            this.mPortNumber = portNumber;
            this.mName = name == null ? "" : name;
        }

        public int getType() {
            return this.mPortType;
        }

        public int getPortNumber() {
            return this.mPortNumber;
        }

        public String getName() {
            return this.mName;
        }
    }

    public MidiDeviceInfo(int type, int id, int numInputPorts, int numOutputPorts, String[] inputPortNames, String[] outputPortNames, Bundle properties, boolean isPrivate, int defaultProtocol) {
        if (numInputPorts < 0 || numInputPorts > 256) {
            throw new IllegalArgumentException("numInputPorts out of range = " + numInputPorts);
        }
        if (numOutputPorts < 0 || numOutputPorts > 256) {
            throw new IllegalArgumentException("numOutputPorts out of range = " + numOutputPorts);
        }
        this.mType = type;
        this.mId = id;
        this.mInputPortCount = numInputPorts;
        this.mOutputPortCount = numOutputPorts;
        if (inputPortNames == null) {
            this.mInputPortNames = new String[numInputPorts];
        } else {
            this.mInputPortNames = inputPortNames;
        }
        if (outputPortNames == null) {
            this.mOutputPortNames = new String[numOutputPorts];
        } else {
            this.mOutputPortNames = outputPortNames;
        }
        this.mProperties = properties;
        this.mIsPrivate = isPrivate;
        this.mDefaultProtocol = defaultProtocol;
    }

    public int getType() {
        return this.mType;
    }

    public int getId() {
        return this.mId;
    }

    public int getInputPortCount() {
        return this.mInputPortCount;
    }

    public int getOutputPortCount() {
        return this.mOutputPortCount;
    }

    public PortInfo[] getPorts() {
        PortInfo[] ports = new PortInfo[this.mInputPortCount + this.mOutputPortCount];
        int index = 0;
        int i = 0;
        while (i < this.mInputPortCount) {
            ports[index] = new PortInfo(1, i, this.mInputPortNames[i]);
            i++;
            index++;
        }
        int i2 = 0;
        while (i2 < this.mOutputPortCount) {
            ports[index] = new PortInfo(2, i2, this.mOutputPortNames[i2]);
            i2++;
            index++;
        }
        return ports;
    }

    public Bundle getProperties() {
        return this.mProperties;
    }

    public boolean isPrivate() {
        return this.mIsPrivate;
    }

    public int getDefaultProtocol() {
        return this.mDefaultProtocol;
    }

    public boolean equals(Object o) {
        return (o instanceof MidiDeviceInfo) && ((MidiDeviceInfo) o).mId == this.mId;
    }

    public int hashCode() {
        return this.mId;
    }

    public String toString() {
        this.mProperties.getString("name");
        return "MidiDeviceInfo[mType=" + this.mType + ",mInputPortCount=" + this.mInputPortCount + ",mOutputPortCount=" + this.mOutputPortCount + ",mProperties=" + this.mProperties + ",mIsPrivate=" + this.mIsPrivate + ",mDefaultProtocol=" + this.mDefaultProtocol;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private Bundle getBasicProperties(String[] keys) {
        Bundle basicProperties = new Bundle();
        for (String key : keys) {
            Object val = this.mProperties.get(key);
            if (val != null) {
                if (val instanceof String) {
                    basicProperties.putString(key, (String) val);
                } else if (val instanceof Integer) {
                    basicProperties.putInt(key, ((Integer) val).intValue());
                } else {
                    Log.m104w(TAG, "Unsupported property type: " + val.getClass().getName());
                }
            }
        }
        return basicProperties;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mType);
        parcel.writeInt(this.mId);
        parcel.writeInt(this.mInputPortCount);
        parcel.writeInt(this.mOutputPortCount);
        parcel.writeStringArray(this.mInputPortNames);
        parcel.writeStringArray(this.mOutputPortNames);
        parcel.writeInt(this.mIsPrivate ? 1 : 0);
        parcel.writeInt(this.mDefaultProtocol);
        parcel.writeBundle(getBasicProperties(new String[]{"name", PROPERTY_MANUFACTURER, "product", "version", "serial_number", PROPERTY_ALSA_CARD, PROPERTY_ALSA_DEVICE}));
        parcel.writeBundle(this.mProperties);
    }
}
