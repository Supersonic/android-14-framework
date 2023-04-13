package com.android.internal.telephony.nano;

import com.android.internal.telephony.RadioNVItems;
import com.android.internal.telephony.protobuf.nano.CodedInputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.CodedOutputByteBufferNano;
import com.android.internal.telephony.protobuf.nano.ExtendableMessageNano;
import com.android.internal.telephony.protobuf.nano.InternalNano;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.io.IOException;
/* loaded from: classes.dex */
public final class TelephonyProto$TelephonySettings extends ExtendableMessageNano<TelephonyProto$TelephonySettings> {
    private static volatile TelephonyProto$TelephonySettings[] _emptyArray;
    public boolean isAirplaneMode;
    public boolean isCellularDataEnabled;
    public boolean isDataRoamingEnabled;
    public boolean isEnhanced4GLteModeEnabled;
    public boolean isVtOverLteEnabled;
    public boolean isVtOverWifiEnabled;
    public boolean isWifiCallingEnabled;
    public boolean isWifiEnabled;
    public int preferredNetworkMode;
    public int wifiCallingMode;

    /* loaded from: classes.dex */
    public interface RilNetworkMode {
        public static final int NETWORK_MODE_CDMA = 5;
        public static final int NETWORK_MODE_CDMA_NO_EVDO = 6;
        public static final int NETWORK_MODE_EVDO_NO_CDMA = 7;
        public static final int NETWORK_MODE_GLOBAL = 8;
        public static final int NETWORK_MODE_GSM_ONLY = 2;
        public static final int NETWORK_MODE_GSM_UMTS = 4;
        public static final int NETWORK_MODE_LTE_CDMA_EVDO = 9;
        public static final int NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA = 11;
        public static final int NETWORK_MODE_LTE_GSM_WCDMA = 10;
        public static final int NETWORK_MODE_LTE_ONLY = 12;
        public static final int NETWORK_MODE_LTE_TDSCDMA = 16;
        public static final int NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 23;
        public static final int NETWORK_MODE_LTE_TDSCDMA_GSM = 18;
        public static final int NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA = 21;
        public static final int NETWORK_MODE_LTE_TDSCDMA_WCDMA = 20;
        public static final int NETWORK_MODE_LTE_WCDMA = 13;
        public static final int NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 22;
        public static final int NETWORK_MODE_TDSCDMA_GSM = 17;
        public static final int NETWORK_MODE_TDSCDMA_GSM_WCDMA = 19;
        public static final int NETWORK_MODE_TDSCDMA_ONLY = 14;
        public static final int NETWORK_MODE_TDSCDMA_WCDMA = 15;
        public static final int NETWORK_MODE_UNKNOWN = 0;
        public static final int NETWORK_MODE_WCDMA_ONLY = 3;
        public static final int NETWORK_MODE_WCDMA_PREF = 1;
    }

    /* loaded from: classes.dex */
    public interface WiFiCallingMode {
        public static final int WFC_MODE_CELLULAR_PREFERRED = 2;
        public static final int WFC_MODE_UNKNOWN = 0;
        public static final int WFC_MODE_WIFI_ONLY = 1;
        public static final int WFC_MODE_WIFI_PREFERRED = 3;
    }

    public static TelephonyProto$TelephonySettings[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TelephonyProto$TelephonySettings[0];
                }
            }
        }
        return _emptyArray;
    }

    public TelephonyProto$TelephonySettings() {
        clear();
    }

    public TelephonyProto$TelephonySettings clear() {
        this.isAirplaneMode = false;
        this.isCellularDataEnabled = false;
        this.isDataRoamingEnabled = false;
        this.preferredNetworkMode = 0;
        this.isEnhanced4GLteModeEnabled = false;
        this.isWifiEnabled = false;
        this.isWifiCallingEnabled = false;
        this.wifiCallingMode = 0;
        this.isVtOverLteEnabled = false;
        this.isVtOverWifiEnabled = false;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        boolean z = this.isAirplaneMode;
        if (z) {
            codedOutputByteBufferNano.writeBool(1, z);
        }
        boolean z2 = this.isCellularDataEnabled;
        if (z2) {
            codedOutputByteBufferNano.writeBool(2, z2);
        }
        boolean z3 = this.isDataRoamingEnabled;
        if (z3) {
            codedOutputByteBufferNano.writeBool(3, z3);
        }
        int i = this.preferredNetworkMode;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(4, i);
        }
        boolean z4 = this.isEnhanced4GLteModeEnabled;
        if (z4) {
            codedOutputByteBufferNano.writeBool(5, z4);
        }
        boolean z5 = this.isWifiEnabled;
        if (z5) {
            codedOutputByteBufferNano.writeBool(6, z5);
        }
        boolean z6 = this.isWifiCallingEnabled;
        if (z6) {
            codedOutputByteBufferNano.writeBool(7, z6);
        }
        int i2 = this.wifiCallingMode;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i2);
        }
        boolean z7 = this.isVtOverLteEnabled;
        if (z7) {
            codedOutputByteBufferNano.writeBool(9, z7);
        }
        boolean z8 = this.isVtOverWifiEnabled;
        if (z8) {
            codedOutputByteBufferNano.writeBool(10, z8);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.telephony.protobuf.nano.ExtendableMessageNano, com.android.internal.telephony.protobuf.nano.MessageNano
    public int computeSerializedSize() {
        int computeSerializedSize = super.computeSerializedSize();
        boolean z = this.isAirplaneMode;
        if (z) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(1, z);
        }
        boolean z2 = this.isCellularDataEnabled;
        if (z2) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(2, z2);
        }
        boolean z3 = this.isDataRoamingEnabled;
        if (z3) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, z3);
        }
        int i = this.preferredNetworkMode;
        if (i != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i);
        }
        boolean z4 = this.isEnhanced4GLteModeEnabled;
        if (z4) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(5, z4);
        }
        boolean z5 = this.isWifiEnabled;
        if (z5) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(6, z5);
        }
        boolean z6 = this.isWifiCallingEnabled;
        if (z6) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(7, z6);
        }
        int i2 = this.wifiCallingMode;
        if (i2 != 0) {
            computeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i2);
        }
        boolean z7 = this.isVtOverLteEnabled;
        if (z7) {
            computeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(9, z7);
        }
        boolean z8 = this.isVtOverWifiEnabled;
        return z8 ? computeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(10, z8) : computeSerializedSize;
    }

    @Override // com.android.internal.telephony.protobuf.nano.MessageNano
    public TelephonyProto$TelephonySettings mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int readTag = codedInputByteBufferNano.readTag();
            switch (readTag) {
                case 0:
                    return this;
                case 8:
                    this.isAirplaneMode = codedInputByteBufferNano.readBool();
                    break;
                case 16:
                    this.isCellularDataEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 24:
                    this.isDataRoamingEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 32:
                    int readInt32 = codedInputByteBufferNano.readInt32();
                    switch (readInt32) {
                        case 0:
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
                        case 15:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                        case 20:
                        case 21:
                        case 22:
                        case 23:
                            this.preferredNetworkMode = readInt32;
                            continue;
                    }
                case 40:
                    this.isEnhanced4GLteModeEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 48:
                    this.isWifiEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 56:
                    this.isWifiCallingEnabled = codedInputByteBufferNano.readBool();
                    break;
                case 64:
                    int readInt322 = codedInputByteBufferNano.readInt32();
                    if (readInt322 != 0 && readInt322 != 1 && readInt322 != 2 && readInt322 != 3) {
                        break;
                    } else {
                        this.wifiCallingMode = readInt322;
                        break;
                    }
                    break;
                case RadioNVItems.RIL_NV_LTE_BAND_ENABLE_26 /* 72 */:
                    this.isVtOverLteEnabled = codedInputByteBufferNano.readBool();
                    break;
                case RadioNVItems.RIL_NV_LTE_NEXT_SCAN /* 80 */:
                    this.isVtOverWifiEnabled = codedInputByteBufferNano.readBool();
                    break;
                default:
                    if (storeUnknownField(codedInputByteBufferNano, readTag)) {
                        break;
                    } else {
                        return this;
                    }
            }
        }
    }

    public static TelephonyProto$TelephonySettings parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (TelephonyProto$TelephonySettings) MessageNano.mergeFrom(new TelephonyProto$TelephonySettings(), bArr);
    }

    public static TelephonyProto$TelephonySettings parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new TelephonyProto$TelephonySettings().mergeFrom(codedInputByteBufferNano);
    }
}
