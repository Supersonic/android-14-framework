package com.android.server.locksettings;

import android.hardware.weaver.IWeaver;
import android.hardware.weaver.V1_0.IWeaver;
import android.hardware.weaver.WeaverConfig;
import android.hardware.weaver.WeaverReadResponse;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.Slog;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class WeaverHidlAdapter implements IWeaver {
    public final android.hardware.weaver.V1_0.IWeaver mImpl;

    public WeaverHidlAdapter(android.hardware.weaver.V1_0.IWeaver iWeaver) {
        this.mImpl = iWeaver;
    }

    @Override // android.hardware.weaver.IWeaver
    public WeaverConfig getConfig() throws RemoteException {
        final WeaverConfig[] weaverConfigArr = new WeaverConfig[1];
        this.mImpl.getConfig(new IWeaver.getConfigCallback() { // from class: com.android.server.locksettings.WeaverHidlAdapter$$ExternalSyntheticLambda1
            @Override // android.hardware.weaver.V1_0.IWeaver.getConfigCallback
            public final void onValues(int i, android.hardware.weaver.V1_0.WeaverConfig weaverConfig) {
                WeaverHidlAdapter.lambda$getConfig$0(weaverConfigArr, i, weaverConfig);
            }
        });
        return weaverConfigArr[0];
    }

    public static /* synthetic */ void lambda$getConfig$0(WeaverConfig[] weaverConfigArr, int i, android.hardware.weaver.V1_0.WeaverConfig weaverConfig) {
        if (i == 0) {
            WeaverConfig weaverConfig2 = new WeaverConfig();
            weaverConfig2.slots = weaverConfig.slots;
            weaverConfig2.keySize = weaverConfig.keySize;
            weaverConfig2.valueSize = weaverConfig.valueSize;
            weaverConfigArr[0] = weaverConfig2;
            return;
        }
        Slog.e("WeaverHidlAdapter", "Failed to get HIDL weaver config. status: " + i + ", slots: " + weaverConfig.slots);
    }

    @Override // android.hardware.weaver.IWeaver
    public WeaverReadResponse read(int i, byte[] bArr) throws RemoteException {
        final WeaverReadResponse[] weaverReadResponseArr = new WeaverReadResponse[1];
        this.mImpl.read(i, toByteArrayList(bArr), new IWeaver.readCallback() { // from class: com.android.server.locksettings.WeaverHidlAdapter$$ExternalSyntheticLambda0
            @Override // android.hardware.weaver.V1_0.IWeaver.readCallback
            public final void onValues(int i2, android.hardware.weaver.V1_0.WeaverReadResponse weaverReadResponse) {
                WeaverHidlAdapter.lambda$read$1(weaverReadResponseArr, i2, weaverReadResponse);
            }
        });
        return weaverReadResponseArr[0];
    }

    public static /* synthetic */ void lambda$read$1(WeaverReadResponse[] weaverReadResponseArr, int i, android.hardware.weaver.V1_0.WeaverReadResponse weaverReadResponse) {
        WeaverReadResponse weaverReadResponse2 = new WeaverReadResponse();
        if (i == 0) {
            weaverReadResponse2.status = 0;
        } else if (i == 1) {
            weaverReadResponse2.status = 1;
        } else if (i == 2) {
            weaverReadResponse2.status = 2;
        } else if (i == 3) {
            weaverReadResponse2.status = 3;
        } else {
            Slog.e("WeaverHidlAdapter", "Unexpected status in read: " + i);
            weaverReadResponse2.status = 1;
        }
        weaverReadResponse2.timeout = weaverReadResponse.timeout;
        weaverReadResponse2.value = fromByteArrayList(weaverReadResponse.value);
        weaverReadResponseArr[0] = weaverReadResponse2;
    }

    @Override // android.hardware.weaver.IWeaver
    public void write(int i, byte[] bArr, byte[] bArr2) throws RemoteException {
        int write = this.mImpl.write(i, toByteArrayList(bArr), toByteArrayList(bArr2));
        if (write == 0) {
            return;
        }
        throw new ServiceSpecificException(1, "Failed IWeaver.write call, status: " + write);
    }

    @Override // android.os.IInterface
    public IBinder asBinder() {
        throw new UnsupportedOperationException("WeaverHidlAdapter does not support asBinder");
    }

    public static ArrayList<Byte> toByteArrayList(byte[] bArr) {
        ArrayList<Byte> arrayList = new ArrayList<>(bArr.length);
        for (byte b : bArr) {
            arrayList.add(Byte.valueOf(b));
        }
        return arrayList;
    }

    public static byte[] fromByteArrayList(ArrayList<Byte> arrayList) {
        byte[] bArr = new byte[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            bArr[i] = arrayList.get(i).byteValue();
        }
        return bArr;
    }
}
