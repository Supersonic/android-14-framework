package com.android.internal.p028os;

import android.p008os.BatteryStats;
import android.p008os.Parcel;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.p028os.BatteryStatsHistory;
import java.util.Iterator;
/* renamed from: com.android.internal.os.BatteryStatsHistoryIterator */
/* loaded from: classes4.dex */
public class BatteryStatsHistoryIterator implements Iterator<BatteryStats.HistoryItem>, AutoCloseable {
    private static final boolean DEBUG = false;
    private static final int MAX_CPU_BRACKET_COUNT = 100;
    private static final int MAX_ENERGY_CONSUMER_COUNT = 100;
    private static final String TAG = "BatteryStatsHistoryItr";
    private final BatteryStatsHistory mBatteryStatsHistory;
    private BatteryStats.CpuUsageDetails mCpuUsageDetails;
    private BatteryStats.EnergyConsumerDetails mEnergyConsumerDetails;
    private final BatteryStats.HistoryItem mHistoryItem;
    private final BatteryStats.HistoryStepDetails mReadHistoryStepDetails = new BatteryStats.HistoryStepDetails();
    private final SparseArray<BatteryStats.HistoryTag> mHistoryTags = new SparseArray<>();
    private final BatteryStatsHistory.VarintParceler mVarintParceler = new BatteryStatsHistory.VarintParceler();

    public BatteryStatsHistoryIterator(BatteryStatsHistory history) {
        BatteryStats.HistoryItem historyItem = new BatteryStats.HistoryItem();
        this.mHistoryItem = historyItem;
        this.mBatteryStatsHistory = history;
        historyItem.clear();
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        Parcel p = this.mBatteryStatsHistory.getNextParcel();
        if (p == null) {
            close();
            return false;
        }
        return true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public BatteryStats.HistoryItem next() {
        Parcel p = this.mBatteryStatsHistory.getNextParcel();
        if (p == null) {
            close();
            return null;
        }
        long lastRealtimeMs = this.mHistoryItem.time;
        long lastWalltimeMs = this.mHistoryItem.currentTime;
        try {
            readHistoryDelta(p, this.mHistoryItem);
            if (this.mHistoryItem.cmd != 5 && this.mHistoryItem.cmd != 7 && lastWalltimeMs != 0) {
                BatteryStats.HistoryItem historyItem = this.mHistoryItem;
                historyItem.currentTime = (historyItem.time - lastRealtimeMs) + lastWalltimeMs;
            }
            return this.mHistoryItem;
        } catch (Throwable t) {
            Slog.wtf(TAG, "Corrupted battery history", t);
            return null;
        }
    }

    private void readHistoryDelta(Parcel src, BatteryStats.HistoryItem cur) {
        int batteryLevelInt;
        int firstToken = src.readInt();
        int deltaTimeToken = 524287 & firstToken;
        cur.cmd = (byte) 0;
        cur.numReadInts = 1;
        if (deltaTimeToken < 524285) {
            cur.time += deltaTimeToken;
        } else if (deltaTimeToken == 524285) {
            cur.readFromParcel(src);
            return;
        } else if (deltaTimeToken == 524286) {
            int delta = src.readInt();
            cur.time += delta;
            cur.numReadInts++;
        } else {
            long delta2 = src.readLong();
            cur.time += delta2;
            cur.numReadInts += 2;
        }
        if ((524288 & firstToken) != 0) {
            batteryLevelInt = src.readInt();
            readBatteryLevelInt(batteryLevelInt, cur);
            cur.numReadInts++;
        } else {
            batteryLevelInt = 0;
        }
        if ((1048576 & firstToken) != 0) {
            int stateInt = src.readInt();
            cur.states = (16777215 & stateInt) | ((-33554432) & firstToken);
            cur.batteryStatus = (byte) ((stateInt >> 29) & 7);
            cur.batteryHealth = (byte) ((stateInt >> 26) & 7);
            cur.batteryPlugType = (byte) ((stateInt >> 24) & 3);
            switch (cur.batteryPlugType) {
                case 1:
                    cur.batteryPlugType = (byte) 1;
                    break;
                case 2:
                    cur.batteryPlugType = (byte) 2;
                    break;
                case 3:
                    cur.batteryPlugType = (byte) 4;
                    break;
            }
            cur.numReadInts++;
        } else {
            cur.states = (firstToken & (-33554432)) | (cur.states & 16777215);
        }
        if ((2097152 & firstToken) != 0) {
            cur.states2 = src.readInt();
        }
        if ((4194304 & firstToken) != 0) {
            int indexes = src.readInt();
            int wakeLockIndex = indexes & 65535;
            int wakeReasonIndex = (indexes >> 16) & 65535;
            if (readHistoryTag(src, wakeLockIndex, cur.localWakelockTag)) {
                cur.wakelockTag = cur.localWakelockTag;
            } else {
                cur.wakelockTag = null;
            }
            if (readHistoryTag(src, wakeReasonIndex, cur.localWakeReasonTag)) {
                cur.wakeReasonTag = cur.localWakeReasonTag;
            } else {
                cur.wakeReasonTag = null;
            }
            cur.numReadInts++;
        } else {
            cur.wakelockTag = null;
            cur.wakeReasonTag = null;
        }
        if ((8388608 & firstToken) != 0) {
            cur.eventTag = cur.localEventTag;
            int codeAndIndex = src.readInt();
            cur.eventCode = codeAndIndex & 65535;
            int index = (codeAndIndex >> 16) & 65535;
            if (readHistoryTag(src, index, cur.localEventTag)) {
                cur.eventTag = cur.localEventTag;
            } else {
                cur.eventTag = null;
            }
            cur.numReadInts++;
        } else {
            cur.eventCode = 0;
        }
        if ((batteryLevelInt & 1) != 0) {
            cur.stepDetails = this.mReadHistoryStepDetails;
            cur.stepDetails.readFromParcel(src);
        } else {
            cur.stepDetails = null;
        }
        if ((16777216 & firstToken) != 0) {
            cur.batteryChargeUah = src.readInt();
        }
        cur.modemRailChargeMah = src.readDouble();
        cur.wifiRailChargeMah = src.readDouble();
        if ((cur.states2 & 131072) != 0) {
            int extensionFlags = src.readInt();
            if ((extensionFlags & 1) != 0) {
                if (this.mEnergyConsumerDetails == null) {
                    this.mEnergyConsumerDetails = new BatteryStats.EnergyConsumerDetails();
                }
                int consumerCount = src.readInt();
                if (consumerCount > 100) {
                    throw new IllegalStateException("EnergyConsumer count too high: " + consumerCount + ". Max = 100");
                }
                this.mEnergyConsumerDetails.consumers = new BatteryStats.EnergyConsumerDetails.EnergyConsumer[consumerCount];
                this.mEnergyConsumerDetails.chargeUC = new long[consumerCount];
                for (int i = 0; i < consumerCount; i++) {
                    BatteryStats.EnergyConsumerDetails.EnergyConsumer consumer = new BatteryStats.EnergyConsumerDetails.EnergyConsumer();
                    consumer.type = src.readInt();
                    consumer.ordinal = src.readInt();
                    consumer.name = src.readString();
                    this.mEnergyConsumerDetails.consumers[i] = consumer;
                }
            }
            if ((extensionFlags & 2) != 0) {
                BatteryStats.EnergyConsumerDetails energyConsumerDetails = this.mEnergyConsumerDetails;
                if (energyConsumerDetails == null) {
                    throw new IllegalStateException("MeasuredEnergyDetails without a header");
                }
                this.mVarintParceler.readLongArray(src, energyConsumerDetails.chargeUC);
                cur.energyConsumerDetails = this.mEnergyConsumerDetails;
            } else {
                cur.energyConsumerDetails = null;
            }
            if ((extensionFlags & 4) != 0) {
                this.mCpuUsageDetails = new BatteryStats.CpuUsageDetails();
                int cpuBracketCount = src.readInt();
                if (cpuBracketCount > 100) {
                    throw new IllegalStateException("Too many CPU brackets: " + cpuBracketCount + ". Max = 100");
                }
                this.mCpuUsageDetails.cpuBracketDescriptions = new String[cpuBracketCount];
                for (int i2 = 0; i2 < cpuBracketCount; i2++) {
                    this.mCpuUsageDetails.cpuBracketDescriptions[i2] = src.readString();
                }
                BatteryStats.CpuUsageDetails cpuUsageDetails = this.mCpuUsageDetails;
                cpuUsageDetails.cpuUsageMs = new long[cpuUsageDetails.cpuBracketDescriptions.length];
            } else {
                BatteryStats.CpuUsageDetails cpuUsageDetails2 = this.mCpuUsageDetails;
                if (cpuUsageDetails2 != null) {
                    cpuUsageDetails2.cpuBracketDescriptions = null;
                }
            }
            if ((extensionFlags & 8) != 0) {
                BatteryStats.CpuUsageDetails cpuUsageDetails3 = this.mCpuUsageDetails;
                if (cpuUsageDetails3 == null) {
                    throw new IllegalStateException("CpuUsageDetails without a header");
                }
                cpuUsageDetails3.uid = src.readInt();
                this.mVarintParceler.readLongArray(src, this.mCpuUsageDetails.cpuUsageMs);
                cur.cpuUsageDetails = this.mCpuUsageDetails;
                return;
            }
            cur.cpuUsageDetails = null;
            return;
        }
        cur.energyConsumerDetails = null;
        cur.cpuUsageDetails = null;
    }

    private boolean readHistoryTag(Parcel src, int index, BatteryStats.HistoryTag outTag) {
        if (index == 65535) {
            return false;
        }
        if ((32768 & index) != 0) {
            BatteryStats.HistoryTag tag = new BatteryStats.HistoryTag();
            tag.readFromParcel(src);
            tag.poolIdx = (-32769) & index;
            this.mHistoryTags.put(tag.poolIdx, tag);
            outTag.setTo(tag);
            return true;
        }
        BatteryStats.HistoryTag historyTag = this.mHistoryTags.get(index);
        if (historyTag != null) {
            outTag.setTo(historyTag);
        } else {
            outTag.string = null;
            outTag.uid = 0;
        }
        outTag.poolIdx = index;
        return true;
    }

    private static void readBatteryLevelInt(int batteryLevelInt, BatteryStats.HistoryItem out) {
        out.batteryLevel = (byte) (((-33554432) & batteryLevelInt) >>> 25);
        out.batteryTemperature = (short) ((33521664 & batteryLevelInt) >>> 15);
        out.batteryVoltage = (char) ((batteryLevelInt & 32766) >>> 1);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        this.mBatteryStatsHistory.iteratorFinished();
    }
}
