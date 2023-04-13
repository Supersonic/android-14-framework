package android.media;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Build;
import android.util.SparseIntArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
/* loaded from: classes2.dex */
public final class AudioDeviceInfo {
    private static final SparseIntArray EXT_TO_INT_DEVICE_MAPPING;
    private static final SparseIntArray EXT_TO_INT_INPUT_DEVICE_MAPPING;
    private static final SparseIntArray INT_TO_EXT_DEVICE_MAPPING;
    public static final int TYPE_AUX_LINE = 19;
    public static final int TYPE_BLE_BROADCAST = 30;
    public static final int TYPE_BLE_HEADSET = 26;
    public static final int TYPE_BLE_SPEAKER = 27;
    public static final int TYPE_BLUETOOTH_A2DP = 8;
    public static final int TYPE_BLUETOOTH_SCO = 7;
    public static final int TYPE_BUILTIN_EARPIECE = 1;
    public static final int TYPE_BUILTIN_MIC = 15;
    public static final int TYPE_BUILTIN_SPEAKER = 2;
    public static final int TYPE_BUILTIN_SPEAKER_SAFE = 24;
    public static final int TYPE_BUS = 21;
    public static final int TYPE_DOCK = 13;
    public static final int TYPE_DOCK_ANALOG = 31;
    public static final int TYPE_ECHO_REFERENCE = 28;
    public static final int TYPE_FM = 14;
    public static final int TYPE_FM_TUNER = 16;
    public static final int TYPE_HDMI = 9;
    public static final int TYPE_HDMI_ARC = 10;
    public static final int TYPE_HDMI_EARC = 29;
    public static final int TYPE_HEARING_AID = 23;
    public static final int TYPE_IP = 20;
    public static final int TYPE_LINE_ANALOG = 5;
    public static final int TYPE_LINE_DIGITAL = 6;
    public static final int TYPE_REMOTE_SUBMIX = 25;
    public static final int TYPE_TELEPHONY = 18;
    public static final int TYPE_TV_TUNER = 17;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_USB_ACCESSORY = 12;
    public static final int TYPE_USB_DEVICE = 11;
    public static final int TYPE_USB_HEADSET = 22;
    public static final int TYPE_WIRED_HEADPHONES = 4;
    public static final int TYPE_WIRED_HEADSET = 3;
    private final AudioDevicePort mPort;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AudioDeviceType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AudioDeviceTypeIn {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface AudioDeviceTypeOut {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isValidAudioDeviceTypeOut(int type) {
        switch (type) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 26:
            case 27:
            case 29:
            case 30:
            case 31:
                return true;
            case 15:
            case 16:
            case 17:
            case 25:
            case 28:
            default:
                return false;
        }
    }

    static boolean isValidAudioDeviceTypeIn(int type) {
        switch (type) {
            case 3:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 17:
            case 18:
            case 20:
            case 21:
            case 22:
            case 25:
            case 26:
            case 28:
            case 29:
            case 31:
                return true;
            case 4:
            case 14:
            case 19:
            case 23:
            case 24:
            case 27:
            case 30:
            default:
                return false;
        }
    }

    public static void enforceValidAudioDeviceTypeOut(int type) {
        if (!isValidAudioDeviceTypeOut(type)) {
            throw new IllegalArgumentException("Illegal output device type " + type);
        }
    }

    public static void enforceValidAudioDeviceTypeIn(int type) {
        if (!isValidAudioDeviceTypeIn(type)) {
            throw new IllegalArgumentException("Illegal input device type " + type);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AudioDeviceInfo that = (AudioDeviceInfo) o;
        return Objects.equals(getPort(), that.getPort());
    }

    public int hashCode() {
        return Objects.hash(getPort());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioDeviceInfo(AudioDevicePort port) {
        this.mPort = port;
    }

    public AudioDevicePort getPort() {
        return this.mPort;
    }

    public int getInternalType() {
        return this.mPort.type();
    }

    public int getId() {
        return this.mPort.handle().m152id();
    }

    public CharSequence getProductName() {
        String portName = this.mPort.name();
        return (portName == null || portName.length() == 0) ? Build.MODEL : portName;
    }

    public String getAddress() {
        return this.mPort.address();
    }

    public boolean isSource() {
        return this.mPort.role() == 1;
    }

    public boolean isSink() {
        return this.mPort.role() == 2;
    }

    public int[] getSampleRates() {
        return this.mPort.samplingRates();
    }

    public int[] getChannelMasks() {
        return this.mPort.channelMasks();
    }

    public int[] getChannelIndexMasks() {
        return this.mPort.channelIndexMasks();
    }

    public int[] getChannelCounts() {
        int[] channelMasks;
        int[] channelIndexMasks;
        int channelCountFromInChannelMask;
        TreeSet<Integer> countSet = new TreeSet<>();
        for (int mask : getChannelMasks()) {
            if (isSink()) {
                channelCountFromInChannelMask = AudioFormat.channelCountFromOutChannelMask(mask);
            } else {
                channelCountFromInChannelMask = AudioFormat.channelCountFromInChannelMask(mask);
            }
            countSet.add(Integer.valueOf(channelCountFromInChannelMask));
        }
        for (int index_mask : getChannelIndexMasks()) {
            countSet.add(Integer.valueOf(Integer.bitCount(index_mask)));
        }
        int[] counts = new int[countSet.size()];
        int index = 0;
        Iterator<Integer> it = countSet.iterator();
        while (it.hasNext()) {
            int count = it.next().intValue();
            counts[index] = count;
            index++;
        }
        return counts;
    }

    public int[] getEncodings() {
        return AudioFormat.filterPublicFormats(this.mPort.formats());
    }

    public List<AudioProfile> getAudioProfiles() {
        return this.mPort.profiles();
    }

    public List<AudioDescriptor> getAudioDescriptors() {
        return this.mPort.audioDescriptors();
    }

    public int[] getEncapsulationModes() {
        return this.mPort.encapsulationModes();
    }

    public int[] getEncapsulationMetadataTypes() {
        return this.mPort.encapsulationMetadataTypes();
    }

    public int getType() {
        return INT_TO_EXT_DEVICE_MAPPING.get(this.mPort.type(), 0);
    }

    public static int convertDeviceTypeToInternalDevice(int deviceType) {
        return EXT_TO_INT_DEVICE_MAPPING.get(deviceType, 0);
    }

    public static int convertInternalDeviceToDeviceType(int intDevice) {
        return INT_TO_EXT_DEVICE_MAPPING.get(intDevice, 0);
    }

    public static int convertDeviceTypeToInternalInputDevice(int deviceType) {
        return convertDeviceTypeToInternalInputDevice(deviceType, "");
    }

    public static int convertDeviceTypeToInternalInputDevice(int deviceType, String address) {
        int internalType = EXT_TO_INT_INPUT_DEVICE_MAPPING.get(deviceType, 0);
        if (internalType == -2147483644 && NavigationBarInflaterView.BACK.equals(address)) {
            return -2147483520;
        }
        return internalType;
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        INT_TO_EXT_DEVICE_MAPPING = sparseIntArray;
        sparseIntArray.put(1, 1);
        sparseIntArray.put(2, 2);
        sparseIntArray.put(4, 3);
        sparseIntArray.put(8, 4);
        sparseIntArray.put(16, 7);
        sparseIntArray.put(32, 7);
        sparseIntArray.put(64, 7);
        sparseIntArray.put(128, 8);
        sparseIntArray.put(256, 8);
        sparseIntArray.put(512, 8);
        sparseIntArray.put(1024, 9);
        sparseIntArray.put(2048, 31);
        sparseIntArray.put(4096, 13);
        sparseIntArray.put(8192, 12);
        sparseIntArray.put(16384, 11);
        sparseIntArray.put(67108864, 22);
        sparseIntArray.put(65536, 18);
        sparseIntArray.put(131072, 5);
        sparseIntArray.put(262144, 10);
        sparseIntArray.put(262145, 29);
        sparseIntArray.put(524288, 6);
        sparseIntArray.put(1048576, 14);
        sparseIntArray.put(2097152, 19);
        sparseIntArray.put(8388608, 20);
        sparseIntArray.put(16777216, 21);
        sparseIntArray.put(134217728, 23);
        sparseIntArray.put(4194304, 24);
        sparseIntArray.put(32768, 25);
        sparseIntArray.put(536870912, 26);
        sparseIntArray.put(536870913, 27);
        sparseIntArray.put(536870914, 30);
        sparseIntArray.put(-2147483644, 15);
        sparseIntArray.put(-2147483640, 7);
        sparseIntArray.put(-2147483632, 3);
        sparseIntArray.put(-2147483616, 9);
        sparseIntArray.put(-2147483584, 18);
        sparseIntArray.put(-2147483520, 15);
        sparseIntArray.put(-2147483136, 31);
        sparseIntArray.put(-2147482624, 13);
        sparseIntArray.put(-2147481600, 12);
        sparseIntArray.put(-2147479552, 11);
        sparseIntArray.put(AudioSystem.DEVICE_IN_USB_HEADSET, 22);
        sparseIntArray.put(-2147475456, 16);
        sparseIntArray.put(-2147467264, 17);
        sparseIntArray.put(-2147450880, 5);
        sparseIntArray.put(-2147418112, 6);
        sparseIntArray.put(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP, 8);
        sparseIntArray.put(AudioSystem.DEVICE_IN_IP, 20);
        sparseIntArray.put(AudioSystem.DEVICE_IN_BUS, 21);
        sparseIntArray.put(AudioSystem.DEVICE_IN_REMOTE_SUBMIX, 25);
        sparseIntArray.put(-1610612736, 26);
        sparseIntArray.put(-2013265920, 10);
        sparseIntArray.put(-2013265919, 29);
        sparseIntArray.put(-1879048192, 28);
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        EXT_TO_INT_DEVICE_MAPPING = sparseIntArray2;
        sparseIntArray2.put(1, 1);
        sparseIntArray2.put(2, 2);
        sparseIntArray2.put(3, 4);
        sparseIntArray2.put(4, 8);
        sparseIntArray2.put(5, 131072);
        sparseIntArray2.put(6, 524288);
        sparseIntArray2.put(7, 16);
        sparseIntArray2.put(8, 128);
        sparseIntArray2.put(9, 1024);
        sparseIntArray2.put(10, 262144);
        sparseIntArray2.put(29, 262145);
        sparseIntArray2.put(11, 16384);
        sparseIntArray2.put(22, 67108864);
        sparseIntArray2.put(12, 8192);
        sparseIntArray2.put(13, 4096);
        sparseIntArray2.put(31, 2048);
        sparseIntArray2.put(14, 1048576);
        sparseIntArray2.put(18, 65536);
        sparseIntArray2.put(19, 2097152);
        sparseIntArray2.put(20, 8388608);
        sparseIntArray2.put(21, 16777216);
        sparseIntArray2.put(23, 134217728);
        sparseIntArray2.put(24, 4194304);
        sparseIntArray2.put(25, 32768);
        sparseIntArray2.put(26, 536870912);
        sparseIntArray2.put(27, 536870913);
        sparseIntArray2.put(30, 536870914);
        SparseIntArray sparseIntArray3 = new SparseIntArray();
        EXT_TO_INT_INPUT_DEVICE_MAPPING = sparseIntArray3;
        sparseIntArray3.put(15, -2147483644);
        sparseIntArray3.put(7, -2147483640);
        sparseIntArray3.put(3, -2147483632);
        sparseIntArray3.put(9, -2147483616);
        sparseIntArray3.put(18, -2147483584);
        sparseIntArray3.put(13, -2147482624);
        sparseIntArray3.put(31, -2147483136);
        sparseIntArray3.put(12, -2147481600);
        sparseIntArray3.put(11, -2147479552);
        sparseIntArray3.put(22, AudioSystem.DEVICE_IN_USB_HEADSET);
        sparseIntArray3.put(16, -2147475456);
        sparseIntArray3.put(17, -2147467264);
        sparseIntArray3.put(5, -2147450880);
        sparseIntArray3.put(6, -2147418112);
        sparseIntArray3.put(8, AudioSystem.DEVICE_IN_BLUETOOTH_A2DP);
        sparseIntArray3.put(20, AudioSystem.DEVICE_IN_IP);
        sparseIntArray3.put(21, AudioSystem.DEVICE_IN_BUS);
        sparseIntArray3.put(25, AudioSystem.DEVICE_IN_REMOTE_SUBMIX);
        sparseIntArray3.put(26, -1610612736);
        sparseIntArray3.put(10, -2013265920);
        sparseIntArray3.put(29, -2013265919);
        sparseIntArray3.put(28, -1879048192);
    }
}
