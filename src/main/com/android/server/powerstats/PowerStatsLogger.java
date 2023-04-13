package com.android.server.powerstats;

import android.content.Context;
import android.hardware.power.stats.EnergyConsumerResult;
import android.hardware.power.stats.EnergyMeasurement;
import android.hardware.power.stats.StateResidencyResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.powerstats.PowerStatsDataStorage;
import com.android.server.powerstats.PowerStatsHALWrapper;
import com.android.server.powerstats.ProtoStreamUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
/* loaded from: classes2.dex */
public final class PowerStatsLogger extends Handler {
    public static final String TAG = PowerStatsLogger.class.getSimpleName();
    public File mDataStoragePath;
    public boolean mDeleteMeterDataOnBoot;
    public boolean mDeleteModelDataOnBoot;
    public boolean mDeleteResidencyDataOnBoot;
    public final PowerStatsHALWrapper.IPowerStatsHALWrapper mPowerStatsHALWrapper;
    public final PowerStatsDataStorage mPowerStatsMeterStorage;
    public final PowerStatsDataStorage mPowerStatsModelStorage;
    public final PowerStatsDataStorage mPowerStatsResidencyStorage;
    public final long mStartWallTime;

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            StateResidencyResult[] stateResidency = this.mPowerStatsHALWrapper.getStateResidency(new int[0]);
            ProtoStreamUtils.StateResidencyResultUtils.adjustTimeSinceBootToEpoch(stateResidency, this.mStartWallTime);
            this.mPowerStatsResidencyStorage.write(ProtoStreamUtils.StateResidencyResultUtils.getProtoBytes(stateResidency));
        } else if (i == 1) {
            EnergyConsumerResult[] energyConsumed = this.mPowerStatsHALWrapper.getEnergyConsumed(new int[0]);
            ProtoStreamUtils.EnergyConsumerResultUtils.adjustTimeSinceBootToEpoch(energyConsumed, this.mStartWallTime);
            this.mPowerStatsModelStorage.write(ProtoStreamUtils.EnergyConsumerResultUtils.getProtoBytes(energyConsumed, true));
        } else if (i != 2) {
        } else {
            EnergyMeasurement[] readEnergyMeter = this.mPowerStatsHALWrapper.readEnergyMeter(new int[0]);
            ProtoStreamUtils.EnergyMeasurementUtils.adjustTimeSinceBootToEpoch(readEnergyMeter, this.mStartWallTime);
            this.mPowerStatsMeterStorage.write(ProtoStreamUtils.EnergyMeasurementUtils.getProtoBytes(readEnergyMeter));
            EnergyConsumerResult[] energyConsumed2 = this.mPowerStatsHALWrapper.getEnergyConsumed(new int[0]);
            ProtoStreamUtils.EnergyConsumerResultUtils.adjustTimeSinceBootToEpoch(energyConsumed2, this.mStartWallTime);
            this.mPowerStatsModelStorage.write(ProtoStreamUtils.EnergyConsumerResultUtils.getProtoBytes(energyConsumed2, false));
        }
    }

    public void writeMeterDataToFile(FileDescriptor fileDescriptor) {
        final ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        try {
            ProtoStreamUtils.ChannelUtils.packProtoMessage(this.mPowerStatsHALWrapper.getEnergyMeterInfo(), protoOutputStream);
            this.mPowerStatsMeterStorage.read(new PowerStatsDataStorage.DataElementReadCallback() { // from class: com.android.server.powerstats.PowerStatsLogger.1
                @Override // com.android.server.powerstats.PowerStatsDataStorage.DataElementReadCallback
                public void onReadDataElement(byte[] bArr) {
                    try {
                        new ProtoInputStream(new ByteArrayInputStream(bArr));
                        ProtoStreamUtils.EnergyMeasurementUtils.packProtoMessage(ProtoStreamUtils.EnergyMeasurementUtils.unpackProtoMessage(bArr), protoOutputStream);
                    } catch (IOException e) {
                        Slog.e(PowerStatsLogger.TAG, "Failed to write energy meter data to incident report.", e);
                    }
                }
            });
        } catch (IOException e) {
            Slog.e(TAG, "Failed to write energy meter info to incident report.", e);
        }
        protoOutputStream.flush();
    }

    public void writeModelDataToFile(FileDescriptor fileDescriptor) {
        final ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        try {
            ProtoStreamUtils.EnergyConsumerUtils.packProtoMessage(this.mPowerStatsHALWrapper.getEnergyConsumerInfo(), protoOutputStream);
            this.mPowerStatsModelStorage.read(new PowerStatsDataStorage.DataElementReadCallback() { // from class: com.android.server.powerstats.PowerStatsLogger.2
                @Override // com.android.server.powerstats.PowerStatsDataStorage.DataElementReadCallback
                public void onReadDataElement(byte[] bArr) {
                    try {
                        new ProtoInputStream(new ByteArrayInputStream(bArr));
                        ProtoStreamUtils.EnergyConsumerResultUtils.packProtoMessage(ProtoStreamUtils.EnergyConsumerResultUtils.unpackProtoMessage(bArr), protoOutputStream, true);
                    } catch (IOException e) {
                        Slog.e(PowerStatsLogger.TAG, "Failed to write energy model data to incident report.", e);
                    }
                }
            });
        } catch (IOException e) {
            Slog.e(TAG, "Failed to write energy model info to incident report.", e);
        }
        protoOutputStream.flush();
    }

    public void writeResidencyDataToFile(FileDescriptor fileDescriptor) {
        final ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        try {
            ProtoStreamUtils.PowerEntityUtils.packProtoMessage(this.mPowerStatsHALWrapper.getPowerEntityInfo(), protoOutputStream);
            this.mPowerStatsResidencyStorage.read(new PowerStatsDataStorage.DataElementReadCallback() { // from class: com.android.server.powerstats.PowerStatsLogger.3
                @Override // com.android.server.powerstats.PowerStatsDataStorage.DataElementReadCallback
                public void onReadDataElement(byte[] bArr) {
                    try {
                        new ProtoInputStream(new ByteArrayInputStream(bArr));
                        ProtoStreamUtils.StateResidencyResultUtils.packProtoMessage(ProtoStreamUtils.StateResidencyResultUtils.unpackProtoMessage(bArr), protoOutputStream);
                    } catch (IOException e) {
                        Slog.e(PowerStatsLogger.TAG, "Failed to write residency data to incident report.", e);
                    }
                }
            });
        } catch (IOException e) {
            Slog.e(TAG, "Failed to write residency data to incident report.", e);
        }
        protoOutputStream.flush();
    }

    public final boolean dataChanged(String str, byte[] bArr) {
        if (this.mDataStoragePath.exists() || this.mDataStoragePath.mkdirs()) {
            File file = new File(this.mDataStoragePath, str);
            if (file.exists()) {
                byte[] bArr2 = new byte[(int) file.length()];
                try {
                    new FileInputStream(file.getPath()).read(bArr2);
                } catch (IOException e) {
                    Slog.e(TAG, "Failed to read cached data from file", e);
                }
                return !Arrays.equals(bArr2, bArr);
            }
            return true;
        }
        return false;
    }

    public final void updateCacheFile(String str, byte[] bArr) {
        try {
            AtomicFile atomicFile = new AtomicFile(new File(this.mDataStoragePath, str));
            FileOutputStream startWrite = atomicFile.startWrite();
            startWrite.write(bArr);
            atomicFile.finishWrite(startWrite);
        } catch (IOException e) {
            Slog.e(TAG, "Failed to write current data to cached file", e);
        }
    }

    public boolean getDeleteMeterDataOnBoot() {
        return this.mDeleteMeterDataOnBoot;
    }

    public boolean getDeleteModelDataOnBoot() {
        return this.mDeleteModelDataOnBoot;
    }

    public boolean getDeleteResidencyDataOnBoot() {
        return this.mDeleteResidencyDataOnBoot;
    }

    @VisibleForTesting
    public long getStartWallTime() {
        return this.mStartWallTime;
    }

    public PowerStatsLogger(Context context, Looper looper, File file, String str, String str2, String str3, String str4, String str5, String str6, PowerStatsHALWrapper.IPowerStatsHALWrapper iPowerStatsHALWrapper) {
        super(looper);
        this.mStartWallTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        this.mPowerStatsHALWrapper = iPowerStatsHALWrapper;
        this.mDataStoragePath = file;
        PowerStatsDataStorage powerStatsDataStorage = new PowerStatsDataStorage(context, this.mDataStoragePath, str);
        this.mPowerStatsMeterStorage = powerStatsDataStorage;
        PowerStatsDataStorage powerStatsDataStorage2 = new PowerStatsDataStorage(context, this.mDataStoragePath, str3);
        this.mPowerStatsModelStorage = powerStatsDataStorage2;
        PowerStatsDataStorage powerStatsDataStorage3 = new PowerStatsDataStorage(context, this.mDataStoragePath, str5);
        this.mPowerStatsResidencyStorage = powerStatsDataStorage3;
        byte[] protoBytes = ProtoStreamUtils.ChannelUtils.getProtoBytes(iPowerStatsHALWrapper.getEnergyMeterInfo());
        boolean dataChanged = dataChanged(str2, protoBytes);
        this.mDeleteMeterDataOnBoot = dataChanged;
        if (dataChanged) {
            powerStatsDataStorage.deleteLogs();
            updateCacheFile(str2, protoBytes);
        }
        byte[] protoBytes2 = ProtoStreamUtils.EnergyConsumerUtils.getProtoBytes(iPowerStatsHALWrapper.getEnergyConsumerInfo());
        boolean dataChanged2 = dataChanged(str4, protoBytes2);
        this.mDeleteModelDataOnBoot = dataChanged2;
        if (dataChanged2) {
            powerStatsDataStorage2.deleteLogs();
            updateCacheFile(str4, protoBytes2);
        }
        byte[] protoBytes3 = ProtoStreamUtils.PowerEntityUtils.getProtoBytes(iPowerStatsHALWrapper.getPowerEntityInfo());
        boolean dataChanged3 = dataChanged(str6, protoBytes3);
        this.mDeleteResidencyDataOnBoot = dataChanged3;
        if (dataChanged3) {
            powerStatsDataStorage3.deleteLogs();
            updateCacheFile(str6, protoBytes3);
        }
    }
}
