package android.hardware.gnss.V2_1;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes2.dex */
public interface IGnssAntennaInfoCallback extends IBase {
    public static final String kInterfaceName = "android.hardware.gnss@2.1::IGnssAntennaInfoCallback";

    @Override // android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
    IHwBinder asBinder();

    @Override // android.internal.hidl.base.V1_0.IBase
    void debug(NativeHandle nativeHandle, ArrayList<String> arrayList) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    DebugInfo getDebugInfo() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    ArrayList<byte[]> getHashChain() throws RemoteException;

    void gnssAntennaInfoCb(ArrayList<GnssAntennaInfo> arrayList) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    ArrayList<String> interfaceChain() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    String interfaceDescriptor() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    boolean linkToDeath(IHwBinder.DeathRecipient deathRecipient, long j) throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void notifySyspropsChanged() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void ping() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    void setHALInstrumentation() throws RemoteException;

    @Override // android.internal.hidl.base.V1_0.IBase
    boolean unlinkToDeath(IHwBinder.DeathRecipient deathRecipient) throws RemoteException;

    static IGnssAntennaInfoCallback asInterface(IHwBinder binder) {
        if (binder == null) {
            return null;
        }
        IHwInterface iface = binder.queryLocalInterface(kInterfaceName);
        if (iface != null && (iface instanceof IGnssAntennaInfoCallback)) {
            return (IGnssAntennaInfoCallback) iface;
        }
        IGnssAntennaInfoCallback proxy = new Proxy(binder);
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

    static IGnssAntennaInfoCallback castFrom(IHwInterface iface) {
        if (iface == null) {
            return null;
        }
        return asInterface(iface.asBinder());
    }

    static IGnssAntennaInfoCallback getService(String serviceName, boolean retry) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName, retry));
    }

    static IGnssAntennaInfoCallback getService(boolean retry) throws RemoteException {
        return getService("default", retry);
    }

    @Deprecated
    static IGnssAntennaInfoCallback getService(String serviceName) throws RemoteException {
        return asInterface(HwBinder.getService(kInterfaceName, serviceName));
    }

    @Deprecated
    static IGnssAntennaInfoCallback getService() throws RemoteException {
        return getService("default");
    }

    /* loaded from: classes2.dex */
    public static final class Row {
        public ArrayList<Double> row = new ArrayList<>();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != Row.class) {
                return false;
            }
            Row other = (Row) otherObject;
            if (HidlSupport.deepEquals(this.row, other.row)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(this.row)));
        }

        public final String toString() {
            return "{.row = " + this.row + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(16L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<Row> readVectorFromParcel(HwParcel parcel) {
            ArrayList<Row> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                Row _hidl_vec_element = new Row();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 16);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 0 + 8);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 8, _hidl_blob.handle(), _hidl_offset + 0 + 0, true);
            this.row.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                double _hidl_vec_element = childBlob.getDouble(_hidl_index_0 * 8);
                this.row.add(Double.valueOf(_hidl_vec_element));
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(16);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Row> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 16);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            int _hidl_vec_size = this.row.size();
            _hidl_blob.putInt32(_hidl_offset + 0 + 8, _hidl_vec_size);
            _hidl_blob.putBool(_hidl_offset + 0 + 12, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 8);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                childBlob.putDouble(_hidl_index_0 * 8, this.row.get(_hidl_index_0).doubleValue());
            }
            _hidl_blob.putBlob(_hidl_offset + 0 + 0, childBlob);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Coord {

        /* renamed from: x */
        public double f137x = 0.0d;
        public double xUncertainty = 0.0d;

        /* renamed from: y */
        public double f138y = 0.0d;
        public double yUncertainty = 0.0d;

        /* renamed from: z */
        public double f139z = 0.0d;
        public double zUncertainty = 0.0d;

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != Coord.class) {
                return false;
            }
            Coord other = (Coord) otherObject;
            if (this.f137x == other.f137x && this.xUncertainty == other.xUncertainty && this.f138y == other.f138y && this.yUncertainty == other.yUncertainty && this.f139z == other.f139z && this.zUncertainty == other.zUncertainty) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.f137x))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.xUncertainty))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.f138y))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.yUncertainty))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.f139z))), Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.zUncertainty))));
        }

        public final String toString() {
            return "{.x = " + this.f137x + ", .xUncertainty = " + this.xUncertainty + ", .y = " + this.f138y + ", .yUncertainty = " + this.yUncertainty + ", .z = " + this.f139z + ", .zUncertainty = " + this.zUncertainty + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(48L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<Coord> readVectorFromParcel(HwParcel parcel) {
            ArrayList<Coord> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 48, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                Coord _hidl_vec_element = new Coord();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 48);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.f137x = _hidl_blob.getDouble(0 + _hidl_offset);
            this.xUncertainty = _hidl_blob.getDouble(8 + _hidl_offset);
            this.f138y = _hidl_blob.getDouble(16 + _hidl_offset);
            this.yUncertainty = _hidl_blob.getDouble(24 + _hidl_offset);
            this.f139z = _hidl_blob.getDouble(32 + _hidl_offset);
            this.zUncertainty = _hidl_blob.getDouble(40 + _hidl_offset);
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(48);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<Coord> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 48);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 48);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putDouble(0 + _hidl_offset, this.f137x);
            _hidl_blob.putDouble(8 + _hidl_offset, this.xUncertainty);
            _hidl_blob.putDouble(16 + _hidl_offset, this.f138y);
            _hidl_blob.putDouble(24 + _hidl_offset, this.yUncertainty);
            _hidl_blob.putDouble(32 + _hidl_offset, this.f139z);
            _hidl_blob.putDouble(40 + _hidl_offset, this.zUncertainty);
        }
    }

    /* loaded from: classes2.dex */
    public static final class GnssAntennaInfo {
        public double carrierFrequencyMHz = 0.0d;
        public Coord phaseCenterOffsetCoordinateMillimeters = new Coord();
        public ArrayList<Row> phaseCenterVariationCorrectionMillimeters = new ArrayList<>();
        public ArrayList<Row> phaseCenterVariationCorrectionUncertaintyMillimeters = new ArrayList<>();
        public ArrayList<Row> signalGainCorrectionDbi = new ArrayList<>();
        public ArrayList<Row> signalGainCorrectionUncertaintyDbi = new ArrayList<>();

        public final boolean equals(Object otherObject) {
            if (this == otherObject) {
                return true;
            }
            if (otherObject == null || otherObject.getClass() != GnssAntennaInfo.class) {
                return false;
            }
            GnssAntennaInfo other = (GnssAntennaInfo) otherObject;
            if (this.carrierFrequencyMHz == other.carrierFrequencyMHz && HidlSupport.deepEquals(this.phaseCenterOffsetCoordinateMillimeters, other.phaseCenterOffsetCoordinateMillimeters) && HidlSupport.deepEquals(this.phaseCenterVariationCorrectionMillimeters, other.phaseCenterVariationCorrectionMillimeters) && HidlSupport.deepEquals(this.phaseCenterVariationCorrectionUncertaintyMillimeters, other.phaseCenterVariationCorrectionUncertaintyMillimeters) && HidlSupport.deepEquals(this.signalGainCorrectionDbi, other.signalGainCorrectionDbi) && HidlSupport.deepEquals(this.signalGainCorrectionUncertaintyDbi, other.signalGainCorrectionUncertaintyDbi)) {
                return true;
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hash(Integer.valueOf(HidlSupport.deepHashCode(Double.valueOf(this.carrierFrequencyMHz))), Integer.valueOf(HidlSupport.deepHashCode(this.phaseCenterOffsetCoordinateMillimeters)), Integer.valueOf(HidlSupport.deepHashCode(this.phaseCenterVariationCorrectionMillimeters)), Integer.valueOf(HidlSupport.deepHashCode(this.phaseCenterVariationCorrectionUncertaintyMillimeters)), Integer.valueOf(HidlSupport.deepHashCode(this.signalGainCorrectionDbi)), Integer.valueOf(HidlSupport.deepHashCode(this.signalGainCorrectionUncertaintyDbi)));
        }

        public final String toString() {
            return "{.carrierFrequencyMHz = " + this.carrierFrequencyMHz + ", .phaseCenterOffsetCoordinateMillimeters = " + this.phaseCenterOffsetCoordinateMillimeters + ", .phaseCenterVariationCorrectionMillimeters = " + this.phaseCenterVariationCorrectionMillimeters + ", .phaseCenterVariationCorrectionUncertaintyMillimeters = " + this.phaseCenterVariationCorrectionUncertaintyMillimeters + ", .signalGainCorrectionDbi = " + this.signalGainCorrectionDbi + ", .signalGainCorrectionUncertaintyDbi = " + this.signalGainCorrectionUncertaintyDbi + "}";
        }

        public final void readFromParcel(HwParcel parcel) {
            HwBlob blob = parcel.readBuffer(120L);
            readEmbeddedFromParcel(parcel, blob, 0L);
        }

        public static final ArrayList<GnssAntennaInfo> readVectorFromParcel(HwParcel parcel) {
            ArrayList<GnssAntennaInfo> _hidl_vec = new ArrayList<>();
            HwBlob _hidl_blob = parcel.readBuffer(16L);
            int _hidl_vec_size = _hidl_blob.getInt32(8L);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 120, _hidl_blob.handle(), 0L, true);
            _hidl_vec.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                GnssAntennaInfo _hidl_vec_element = new GnssAntennaInfo();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 120);
                _hidl_vec.add(_hidl_vec_element);
            }
            return _hidl_vec;
        }

        public final void readEmbeddedFromParcel(HwParcel parcel, HwBlob _hidl_blob, long _hidl_offset) {
            this.carrierFrequencyMHz = _hidl_blob.getDouble(_hidl_offset + 0);
            this.phaseCenterOffsetCoordinateMillimeters.readEmbeddedFromParcel(parcel, _hidl_blob, _hidl_offset + 8);
            int _hidl_vec_size = _hidl_blob.getInt32(_hidl_offset + 56 + 8);
            HwBlob childBlob = parcel.readEmbeddedBuffer(_hidl_vec_size * 16, _hidl_blob.handle(), _hidl_offset + 56 + 0, true);
            this.phaseCenterVariationCorrectionMillimeters.clear();
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                Row _hidl_vec_element = new Row();
                _hidl_vec_element.readEmbeddedFromParcel(parcel, childBlob, _hidl_index_0 * 16);
                this.phaseCenterVariationCorrectionMillimeters.add(_hidl_vec_element);
            }
            int _hidl_vec_size2 = _hidl_blob.getInt32(_hidl_offset + 72 + 8);
            HwBlob childBlob2 = parcel.readEmbeddedBuffer(_hidl_vec_size2 * 16, _hidl_blob.handle(), _hidl_offset + 72 + 0, true);
            this.phaseCenterVariationCorrectionUncertaintyMillimeters.clear();
            for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
                Row _hidl_vec_element2 = new Row();
                _hidl_vec_element2.readEmbeddedFromParcel(parcel, childBlob2, _hidl_index_02 * 16);
                this.phaseCenterVariationCorrectionUncertaintyMillimeters.add(_hidl_vec_element2);
            }
            int _hidl_vec_size3 = _hidl_blob.getInt32(_hidl_offset + 88 + 8);
            HwBlob childBlob3 = parcel.readEmbeddedBuffer(_hidl_vec_size3 * 16, _hidl_blob.handle(), _hidl_offset + 88 + 0, true);
            this.signalGainCorrectionDbi.clear();
            for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
                Row _hidl_vec_element3 = new Row();
                _hidl_vec_element3.readEmbeddedFromParcel(parcel, childBlob3, _hidl_index_03 * 16);
                this.signalGainCorrectionDbi.add(_hidl_vec_element3);
            }
            int _hidl_vec_size4 = _hidl_blob.getInt32(_hidl_offset + 104 + 8);
            HwBlob childBlob4 = parcel.readEmbeddedBuffer(_hidl_vec_size4 * 16, _hidl_blob.handle(), _hidl_offset + 104 + 0, true);
            this.signalGainCorrectionUncertaintyDbi.clear();
            for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
                Row _hidl_vec_element4 = new Row();
                _hidl_vec_element4.readEmbeddedFromParcel(parcel, childBlob4, _hidl_index_04 * 16);
                this.signalGainCorrectionUncertaintyDbi.add(_hidl_vec_element4);
            }
        }

        public final void writeToParcel(HwParcel parcel) {
            HwBlob _hidl_blob = new HwBlob(120);
            writeEmbeddedToBlob(_hidl_blob, 0L);
            parcel.writeBuffer(_hidl_blob);
        }

        public static final void writeVectorToParcel(HwParcel parcel, ArrayList<GnssAntennaInfo> _hidl_vec) {
            HwBlob _hidl_blob = new HwBlob(16);
            int _hidl_vec_size = _hidl_vec.size();
            _hidl_blob.putInt32(8L, _hidl_vec_size);
            _hidl_blob.putBool(12L, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 120);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                _hidl_vec.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 120);
            }
            _hidl_blob.putBlob(0L, childBlob);
            parcel.writeBuffer(_hidl_blob);
        }

        public final void writeEmbeddedToBlob(HwBlob _hidl_blob, long _hidl_offset) {
            _hidl_blob.putDouble(_hidl_offset + 0, this.carrierFrequencyMHz);
            this.phaseCenterOffsetCoordinateMillimeters.writeEmbeddedToBlob(_hidl_blob, _hidl_offset + 8);
            int _hidl_vec_size = this.phaseCenterVariationCorrectionMillimeters.size();
            _hidl_blob.putInt32(_hidl_offset + 56 + 8, _hidl_vec_size);
            _hidl_blob.putBool(_hidl_offset + 56 + 12, false);
            HwBlob childBlob = new HwBlob(_hidl_vec_size * 16);
            for (int _hidl_index_0 = 0; _hidl_index_0 < _hidl_vec_size; _hidl_index_0++) {
                this.phaseCenterVariationCorrectionMillimeters.get(_hidl_index_0).writeEmbeddedToBlob(childBlob, _hidl_index_0 * 16);
            }
            _hidl_blob.putBlob(_hidl_offset + 56 + 0, childBlob);
            int _hidl_vec_size2 = this.phaseCenterVariationCorrectionUncertaintyMillimeters.size();
            _hidl_blob.putInt32(_hidl_offset + 72 + 8, _hidl_vec_size2);
            _hidl_blob.putBool(_hidl_offset + 72 + 12, false);
            HwBlob childBlob2 = new HwBlob(_hidl_vec_size2 * 16);
            for (int _hidl_index_02 = 0; _hidl_index_02 < _hidl_vec_size2; _hidl_index_02++) {
                this.phaseCenterVariationCorrectionUncertaintyMillimeters.get(_hidl_index_02).writeEmbeddedToBlob(childBlob2, _hidl_index_02 * 16);
            }
            _hidl_blob.putBlob(_hidl_offset + 72 + 0, childBlob2);
            int _hidl_vec_size3 = this.signalGainCorrectionDbi.size();
            _hidl_blob.putInt32(_hidl_offset + 88 + 8, _hidl_vec_size3);
            _hidl_blob.putBool(_hidl_offset + 88 + 12, false);
            HwBlob childBlob3 = new HwBlob(_hidl_vec_size3 * 16);
            for (int _hidl_index_03 = 0; _hidl_index_03 < _hidl_vec_size3; _hidl_index_03++) {
                this.signalGainCorrectionDbi.get(_hidl_index_03).writeEmbeddedToBlob(childBlob3, _hidl_index_03 * 16);
            }
            _hidl_blob.putBlob(_hidl_offset + 88 + 0, childBlob3);
            int _hidl_vec_size4 = this.signalGainCorrectionUncertaintyDbi.size();
            _hidl_blob.putInt32(_hidl_offset + 104 + 8, _hidl_vec_size4);
            _hidl_blob.putBool(_hidl_offset + 104 + 12, false);
            HwBlob childBlob4 = new HwBlob(_hidl_vec_size4 * 16);
            for (int _hidl_index_04 = 0; _hidl_index_04 < _hidl_vec_size4; _hidl_index_04++) {
                this.signalGainCorrectionUncertaintyDbi.get(_hidl_index_04).writeEmbeddedToBlob(childBlob4, _hidl_index_04 * 16);
            }
            _hidl_blob.putBlob(_hidl_offset + 104 + 0, childBlob4);
        }
    }

    /* loaded from: classes2.dex */
    public static final class Proxy implements IGnssAntennaInfoCallback {
        private IHwBinder mRemote;

        public Proxy(IHwBinder remote) {
            this.mRemote = (IHwBinder) Objects.requireNonNull(remote);
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this.mRemote;
        }

        public String toString() {
            try {
                return interfaceDescriptor() + "@Proxy";
            } catch (RemoteException e) {
                return "[class or subclass of android.hardware.gnss@2.1::IGnssAntennaInfoCallback]@Proxy";
            }
        }

        public final boolean equals(Object other) {
            return HidlSupport.interfacesEqual(this, other);
        }

        public final int hashCode() {
            return asBinder().hashCode();
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback
        public void gnssAntennaInfoCb(ArrayList<GnssAntennaInfo> gnssAntennaInfos) throws RemoteException {
            HwParcel _hidl_request = new HwParcel();
            _hidl_request.writeInterfaceToken(IGnssAntennaInfoCallback.kInterfaceName);
            GnssAntennaInfo.writeVectorToParcel(_hidl_request, gnssAntennaInfos);
            HwParcel _hidl_reply = new HwParcel();
            try {
                this.mRemote.transact(1, _hidl_request, _hidl_reply, 0);
                _hidl_reply.verifySuccess();
                _hidl_request.releaseTemporaryStorage();
            } finally {
                _hidl_reply.release();
            }
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) throws RemoteException {
            return this.mRemote.linkToDeath(recipient, cookie);
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
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

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) throws RemoteException {
            return this.mRemote.unlinkToDeath(recipient);
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends HwBinder implements IGnssAntennaInfoCallback {
        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase, android.p008os.IHwInterface
        public IHwBinder asBinder() {
            return this;
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<String> interfaceChain() {
            return new ArrayList<>(Arrays.asList(IGnssAntennaInfoCallback.kInterfaceName, IBase.kInterfaceName));
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public void debug(NativeHandle fd, ArrayList<String> options) {
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final String interfaceDescriptor() {
            return IGnssAntennaInfoCallback.kInterfaceName;
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final ArrayList<byte[]> getHashChain() {
            return new ArrayList<>(Arrays.asList(new byte[]{11, -61, -19, -105, -53, -61, -10, -85, -56, -100, 104, -12, -23, -12, -47, 36, -7, -9, 35, 67, 25, -105, -36, -120, -62, 24, 108, -12, -46, -83, 71, -18}, new byte[]{-20, Byte.MAX_VALUE, -41, -98, MidiConstants.STATUS_CHANNEL_PRESSURE, 45, -6, -123, -68, 73, -108, 38, -83, -82, 62, -66, 35, -17, 5, 36, MidiConstants.STATUS_SONG_SELECT, -51, 105, 87, 19, -109, 36, -72, 59, 24, -54, 76}));
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final void setHALInstrumentation() {
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean linkToDeath(IHwBinder.DeathRecipient recipient, long cookie) {
            return true;
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final void ping() {
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final DebugInfo getDebugInfo() {
            DebugInfo info = new DebugInfo();
            info.pid = HidlSupport.getPidIfSharable();
            info.ptr = 0L;
            info.arch = 0;
            return info;
        }

        @Override // android.hardware.gnss.V2_1.IGnssAntennaInfoCallback, android.internal.hidl.base.V1_0.IBase
        public final void notifySyspropsChanged() {
            HwBinder.enableInstrumentation();
        }

        @Override // android.p008os.IHwBinder, android.hardware.cas.V1_0.ICas, android.internal.hidl.base.V1_0.IBase
        public final boolean unlinkToDeath(IHwBinder.DeathRecipient recipient) {
            return true;
        }

        @Override // android.p008os.IHwBinder
        public IHwInterface queryLocalInterface(String descriptor) {
            if (IGnssAntennaInfoCallback.kInterfaceName.equals(descriptor)) {
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
                    _hidl_request.enforceInterface(IGnssAntennaInfoCallback.kInterfaceName);
                    ArrayList<GnssAntennaInfo> gnssAntennaInfos = GnssAntennaInfo.readVectorFromParcel(_hidl_request);
                    gnssAntennaInfoCb(gnssAntennaInfos);
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
