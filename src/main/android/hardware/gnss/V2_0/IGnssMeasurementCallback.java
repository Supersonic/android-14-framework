package android.hardware.gnss.V2_0;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import android.hardware.gnss.V1_0.IGnssMeasurementCallback;
import android.hardware.gnss.V1_1.IGnssMeasurementCallback;
import android.internal.hidl.base.V1_0.DebugInfo;
import android.internal.hidl.base.V1_0.IBase;
import android.p008os.HidlSupport;
import android.p008os.HwBinder;
import android.p008os.HwBlob;
import android.p008os.HwParcel;
import android.p008os.IHwBinder;
import android.p008os.IHwInterface;
import android.p008os.NativeHandle;
import android.p008os.RemoteException;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.GsmAlphabet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IGnssMeasurementCallback extends android.hardware.gnss.V1_1.IGnssMeasurementCallback {
    public static final String kInterfaceName = "android.hardware.gnss@2.0::IGnssMeasurementCallback";

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void gnssMeasurementCb_2_0(GnssData gnssData) throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IGnssMeasurementCallback asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnssMeasurementCallback)) {
            return (IGnssMeasurementCallback) iface;
        }
        IGnssMeasurementCallback proxy = new Proxy(binder);
        try {
            Iterator<String> it = proxy.interfaceChain().iterator();
            while (it.hasNext()) {
                String descriptor = it.next();
                if (descriptor.equals(kInterfaceName)) {
                    return proxy;
                }
            }
        } catch (RemoteException e) {
        }
        return null;
    }

    static IGnssMeasurementCallback castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnssMeasurementCallback getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnssMeasurementCallback getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnssMeasurementCallback getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnssMeasurementCallback getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class GnssMeasurementState {
        public static final int STATE_2ND_CODE_LOCK = 65536;
        public static final int STATE_BDS_D2_BIT_SYNC = 256;
        public static final int STATE_BDS_D2_SUBFRAME_SYNC = 512;
        public static final int STATE_BIT_SYNC = 2;
        public static final int STATE_CODE_LOCK = 1;
        public static final int STATE_GAL_E1BC_CODE_LOCK = 1024;
        public static final int STATE_GAL_E1B_PAGE_SYNC = 4096;
        public static final int STATE_GAL_E1C_2ND_CODE_LOCK = 2048;
        public static final int STATE_GLO_STRING_SYNC = 64;
        public static final int STATE_GLO_TOD_DECODED = 128;
        public static final int STATE_GLO_TOD_KNOWN = 32768;
        public static final int STATE_MSEC_AMBIGUOUS = 16;
        public static final int STATE_SBAS_SYNC = 8192;
        public static final int STATE_SUBFRAME_SYNC = 4;
        public static final int STATE_SYMBOL_SYNC = 32;
        public static final int STATE_TOW_DECODED = 8;
        public static final int STATE_TOW_KNOWN = 16384;
        public static final int STATE_UNKNOWN = 0;

        public static final String toString(int o) {
            if (o == 0) {
                return "STATE_UNKNOWN";
            }
            if (o == 1) {
                return "STATE_CODE_LOCK";
            }
            if (o == 2) {
                return "STATE_BIT_SYNC";
            }
            if (o == 4) {
                return "STATE_SUBFRAME_SYNC";
            }
            if (o == 8) {
                return "STATE_TOW_DECODED";
            }
            if (o == 16) {
                return "STATE_MSEC_AMBIGUOUS";
            }
            if (o == 32) {
                return "STATE_SYMBOL_SYNC";
            }
            if (o == 64) {
                return "STATE_GLO_STRING_SYNC";
            }
            if (o == 128) {
                return "STATE_GLO_TOD_DECODED";
            }
            if (o == 256) {
                return "STATE_BDS_D2_BIT_SYNC";
            }
            if (o == 512) {
                return "STATE_BDS_D2_SUBFRAME_SYNC";
            }
            if (o == 1024) {
                return "STATE_GAL_E1BC_CODE_LOCK";
            }
            if (o == 2048) {
                return "STATE_GAL_E1C_2ND_CODE_LOCK";
            }
            if (o == 4096) {
                return "STATE_GAL_E1B_PAGE_SYNC";
            }
            if (o == 8192) {
                return "STATE_SBAS_SYNC";
            }
            if (o == 16384) {
                return "STATE_TOW_KNOWN";
            }
            if (o == 32768) {
                return "STATE_GLO_TOD_KNOWN";
            }
            if (o == 65536) {
                return "STATE_2ND_CODE_LOCK";
            }
            return "0x" + Integer.toHexString(o);
        }

        public static final String dumpBitfield(int o) {
            ArrayList<String> list = new ArrayList<>();
            int flipped = 0;
            list.add("STATE_UNKNOWN");
            if ((o & 1) == 1) {
                list.add("STATE_CODE_LOCK");
                flipped = 0 | 1;
            }
            if ((o & 2) == 2) {
                list.add("STATE_BIT_SYNC");
                flipped |= 2;
            }
            if ((o & 4) == 4) {
                list.add("STATE_SUBFRAME_SYNC");
                flipped |= 4;
            }
            if ((o & 8) == 8) {
                list.add("STATE_TOW_DECODED");
                flipped |= 8;
            }
            if ((o & 16) == 16) {
                list.add("STATE_MSEC_AMBIGUOUS");
                flipped |= 16;
            }
            if ((o & 32) == 32) {
                list.add("STATE_SYMBOL_SYNC");
                flipped |= 32;
            }
            if ((o & 64) == 64) {
                list.add("STATE_GLO_STRING_SYNC");
                flipped |= 64;
            }
            if ((o & 128) == 128) {
                list.add("STATE_GLO_TOD_DECODED");
                flipped |= 128;
            }
            if ((o & 256) == 256) {
                list.add("STATE_BDS_D2_BIT_SYNC");
                flipped |= 256;
            }
            if ((o & 512) == 512) {
                list.add("STATE_BDS_D2_SUBFRAME_SYNC");
                flipped |= 512;
            }
            if ((o & 1024) == 1024) {
                list.add("STATE_GAL_E1BC_CODE_LOCK");
                flipped |= 1024;
            }
            if ((o & 2048) == 2048) {
                list.add("STATE_GAL_E1C_2ND_CODE_LOCK");
                flipped |= 2048;
            }
            if ((o & 4096) == 4096) {
                list.add("STATE_GAL_E1B_PAGE_SYNC");
                flipped |= 4096;
            }
            if ((o & 8192) == 8192) {
                list.add("STATE_SBAS_SYNC");
                flipped |= 8192;
            }
            if ((o & 16384) == 16384) {
                list.add("STATE_TOW_KNOWN");
                flipped |= 16384;
            }
            if ((o & 32768) == 32768) {
                list.add("STATE_GLO_TOD_KNOWN");
                flipped |= 32768;
            }
            if ((o & 65536) == 65536) {
                list.add("STATE_2ND_CODE_LOCK");
                flipped |= 65536;
            }
            if (o != flipped) {
                list.add("0x" + Integer.toHexString((~flipped) & o));
            }
            return String.join(" | ", list);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssMeasurement {
        public int state;
        public IGnssMeasurementCallback.GnssMeasurement v1_1 = new IGnssMeasurementCallback.GnssMeasurement();
        public String codeType = new String();
        public byte constellation = 0;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssMeasurement.class) {
                return false;
            }
            GnssMeasurement other = (GnssMeasurement) otherObject;
            if (HidlSupport.deepEquals(this.v1_1, other.v1_1) && HidlSupport.deepEquals(this.codeType, other.codeType) && HidlSupport.deepEquals(Integer.valueOf(this.state), Integer.valueOf(other.state)) && this.constellation == other.constellation) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.v1_1)), Integer.valueOf(HidlSupport.deepHashCode(this.codeType)), Integer.valueOf(HidlSupport.deepHashCode(Integer.valueOf(this.state))), Integer.valueOf(HidlSupport.deepHashCode(Byte.valueOf(this.constellation))));
        }

        public final String toString() {
            return "{.v1_1 = " + this.v1_1 + ", .codeType = " + this.codeType + ", .state = " + GnssMeasurementState.dumpBitfield(this.state) + ", .constellation = " + GnssConstellationType.toString(this.constellation) + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(176L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssMeasurement> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssMeasurement> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 176, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssMeasurement _hidl_vec_element = new GnssMeasurement();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 176);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.v1_1.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 0);
            String string = _hidl_blob.getString(_hidl_offset + 152);
            this.codeType = string;
            parcel.readEmbeddedBuffer(string.getBytes().length + 1, _hidl_blob.handle(), _hidl_offset + 152 + 0, false);
            this.state = _hidl_blob.getInt32(_hidl_offset + 168);
            this.constellation = _hidl_blob.getInt8(_hidl_offset + 172);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(176);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssMeasurement> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 176);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 176);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            this.v1_1.writeEmbeddedToBlob(_hidl_blob, 0 + _hidl_offset);
            _hidl_blob.putString(152 + _hidl_offset, this.codeType);
            _hidl_blob.putInt32(168 + _hidl_offset, this.state);
            _hidl_blob.putInt8(172 + _hidl_offset, this.constellation);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssData {
        public ArrayList<GnssMeasurement> measurements = new ArrayList<>();
        public IGnssMeasurementCallback.GnssClock clock = new IGnssMeasurementCallback.GnssClock();
        public ElapsedRealtime elapsedRealtime = new ElapsedRealtime();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssData.class) {
                return false;
            }
            GnssData other = (GnssData) otherObject;
            if (HidlSupport.deepEquals(this.measurements, other.measurements) && HidlSupport.deepEquals(this.clock, other.clock) && HidlSupport.deepEquals(this.elapsedRealtime, other.elapsedRealtime)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.measurements)), Integer.valueOf(HidlSupport.deepHashCode(this.clock)), Integer.valueOf(HidlSupport.deepHashCode(this.elapsedRealtime)));
        }

        public final String toString() {
            return "{.measurements = " + this.measurements + ", .clock = " + this.clock + ", .elapsedRealtime = " + this.elapsedRealtime + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(112L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssData> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssData> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 112, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssData _hidl_vec_element = new GnssData();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 112);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 0 + 8);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 176, _hidl_blob.handle(), _hidl_offset + 0 + 0, true);
            this.measurements.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssMeasurement _hidl_vec_element = new GnssMeasurement();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 176);
                this.measurements.add(_hidl_vec_element);
            }
            this.clock.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 16);
            this.elapsedRealtime.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 88);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(112);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssData> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 112);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 112);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            int _hidl_vec_size = this.measurements.size();
            _hidl_blob.putInt32(_hidl_offset + 0 + 8, _hidl_vec_size);
            _hidl_blob.putBool(_hidl_offset + 0 + 12, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 176);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                this.measurements.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 176);
            }
            _hidl_blob.putBlob(_hidl_offset + 0 + 0, childBlob);
            this.clock.writeEmbeddedToBlob(_hidl_blob, 16 + _hidl_offset);
            this.elapsedRealtime.writeEmbeddedToBlob(_hidl_blob, 88 + _hidl_offset);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IGnssMeasurementCallback {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@2.0::IGnssMeasurementCallback]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V1_0.IGnssMeasurementCallback
        public void GnssMeasurementCb(IGnssMeasurementCallback.GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_0.IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V1_1.IGnssMeasurementCallback
        public void gnssMeasurementCb(IGnssMeasurementCallback.GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(android.hardware.gnss.V1_1.IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(2, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback
        public void gnssMeasurementCb_2_0(GnssData data) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssMeasurementCallback.kInterfaceName);
            data.writeToParcel(_hidl_request);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(3, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public ArrayList<String> interfaceChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256067662, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<String> _hidl_out_descriptors = _hidl_reply.readStringVector();
                return _hidl_out_descriptors;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            _hidl_request.writeNativeHandle(fd);
            _hidl_request.writeStringVector(options);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256131655, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public String interfaceDescriptor() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256136003, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                String _hidl_out_descriptor = _hidl_reply.readString();
                return _hidl_out_descriptor;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public ArrayList<byte[]> getHashChain() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256398152, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                ArrayList<byte[]> _hidl_out_hashchain = new ArrayList<>();
                HwBlob _hidl_blob = _hidl_reply.readBuffer(16L);
                int _hidl_vec_size = _hidl_blob.getInt32(8L);
                HwBlob childBlob = _hidl_reply.readEmbeddedBuffer(_hidl_vec_size * 32, _hidl_blob.handle(), 0L, true);
                _hidl_out_hashchain.clear();
                for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                    byte[] _hidl_vec_element = new byte[32];
                    long _hidl_array_offset_1 = _hidl_index_0 * 32;
                    childBlob.copyToInt8Array(_hidl_array_offset_1, _hidl_vec_element, 32);
                    _hidl_out_hashchain.add(_hidl_vec_element);
                }
                return _hidl_out_hashchain;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void setHALInstrumentation() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256462420, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void ping() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(256921159, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public DebugInfo getDebugInfo() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257049926, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
                DebugInfo _hidl_out_info = new DebugInfo();
                _hidl_out_info.readFromParcel(_hidl_reply);
                return _hidl_out_info;
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void notifySyspropsChanged() throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IBase.kInterfaceName);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(257120595, _hidl_request, _hidl_reply, 1);
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IGnssMeasurementCallback {
        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssMeasurementCallback.kInterfaceName, android.hardware.gnss.V1_1.IGnssMeasurementCallback.kInterfaceName, android.hardware.gnss.V1_0.IGnssMeasurementCallback.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssMeasurementCallback.kInterfaceName;
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{-35, 108, -39, -37, -92, -3, -23, -102, GsmAlphabet.GSM_EXTENDED_ESCAPE, -61, -53, 23, 40, -40, 35, 9, -11, 9, -90, -26, -31, -103, 62, 80, 66, -33, -91, -1, -28, -81, 84, 66}, new byte[]{122, -30, 2, 86, 98, -29, AcquiredInfo.POWER_PRESS, 105, 10, 63, -6, 28, 101, -52, -105, 44, 98, -105, -90, -122, 56, 23, 64, 85, -61, 60, -65, 61, 46, 75, -67, -36}, new byte[]{-67, -92, -110, -20, 64, 33, -47, 56, 105, -34, 114, -67, 111, -116, 21, -59, -125, 123, 120, -42, 19, 107, -115, 83, -114, -2, -59, 50, 5, 115, -91, -20}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V2_0.IGnssMeasurementCallback, android.hardware.gnss.V1_1.IGnssMeasurementCallback, android.hardware.gnss.V1_0.IGnssMeasurementCallback, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnssMeasurementCallback.kInterfaceName.equals(descriptor)) {
                return this;
            }
            return null;
        }

        public void registerAsService(String serviceName) throws RemoteException {
            registerService(serviceName);
        }

        public String toString() {
            return interfaceDescriptor() + "@Stub";
        }

        @Override // android.p008os.HwBinder
        public void onTransact(int _hidl_code, HwParcel _hidl_request, HwParcel _hidl_reply, int _hidl_flags) throws RemoteException {
            switch (_hidl_code) {
                case 1:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_0.IGnssMeasurementCallback.kInterfaceName);
                    IGnssMeasurementCallback.GnssData data = new IGnssMeasurementCallback.GnssData();
                    data.readFromParcel(_hidl_request);
                    GnssMeasurementCb(data);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 2:
                    _hidl_request.enforceInterface(android.hardware.gnss.V1_1.IGnssMeasurementCallback.kInterfaceName);
                    IGnssMeasurementCallback.GnssData data2 = new IGnssMeasurementCallback.GnssData();
                    data2.readFromParcel(_hidl_request);
                    gnssMeasurementCb(data2);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 3:
                    _hidl_request.enforceInterface(IGnssMeasurementCallback.kInterfaceName);
                    GnssData data3 = new GnssData();
                    data3.readFromParcel(_hidl_request);
                    gnssMeasurementCb_2_0(data3);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 256067662:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<String> _hidl_out_descriptors = interfaceChain();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeStringVector(_hidl_out_descriptors);
                    _hidl_reply.send();
                    return;
                case 256131655:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    NativeHandle fd = _hidl_request.readNativeHandle();
                    ArrayList<String> options = _hidl_request.readStringVector();
                    debug(fd, options);
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 256136003:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    String _hidl_out_descriptor = interfaceDescriptor();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.writeString(_hidl_out_descriptor);
                    _hidl_reply.send();
                    return;
                case 256398152:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ArrayList<byte[]> _hidl_out_hashchain = getHashChain();
                    _hidl_reply.writeStatus(0);
                    HwBlob _hidl_blob = new HwBlob(16);
                    int _hidl_vec_size = _hidl_out_hashchain.size();
                    _hidl_blob.putInt32(8L, _hidl_vec_size);
                    _hidl_blob.putBool(12L, false);
                    HwBlob childBlob = new HwBlob(_hidl_vec_size * 32);
                    for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                        long _hidl_array_offset_1 = _hidl_index_0 * 32;
                        byte[] _hidl_array_item_1 = _hidl_out_hashchain.get(_hidl_index_0);
                        if (_hidl_array_item_1 == null || _hidl_array_item_1.length != 32) {
                            throw new IllegalArgumentException("Array element is not of the expected length");
                        }
                        childBlob.putInt8Array(_hidl_array_offset_1, _hidl_array_item_1);
                    }
                    _hidl_blob.putBlob(0L, childBlob);
                    _hidl_reply.writeBuffer(_hidl_blob);
                    _hidl_reply.send();
                    return;
                case 256462420:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    setHALInstrumentation();
                    return;
                case 256660548:
                default:
                    return;
                case 256921159:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    ping();
                    _hidl_reply.writeStatus(0);
                    _hidl_reply.send();
                    return;
                case 257049926:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    DebugInfo _hidl_out_info = getDebugInfo();
                    _hidl_reply.writeStatus(0);
                    _hidl_out_info.writeToParcel(_hidl_reply);
                    _hidl_reply.send();
                    return;
                case 257120595:
                    _hidl_request.enforceInterface(IBase.kInterfaceName);
                    notifySyspropsChanged();
                    return;
            }
        }
    }
}
