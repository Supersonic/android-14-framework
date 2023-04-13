package com.android.server.powerstats;

import android.hardware.power.stats.Channel;
import android.hardware.power.stats.EnergyConsumer;
import android.hardware.power.stats.EnergyConsumerAttribution;
import android.hardware.power.stats.EnergyConsumerResult;
import android.hardware.power.stats.EnergyMeasurement;
import android.hardware.power.stats.PowerEntity;
import android.hardware.power.stats.State;
import android.hardware.power.stats.StateResidency;
import android.hardware.power.stats.StateResidencyResult;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import android.util.proto.ProtoUtils;
import android.util.proto.WireTypeMismatchException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class ProtoStreamUtils {
    public static final String TAG = "ProtoStreamUtils";

    /* loaded from: classes2.dex */
    public static class PowerEntityUtils {
        public static byte[] getProtoBytes(PowerEntity[] powerEntityArr) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            packProtoMessage(powerEntityArr, protoOutputStream);
            return protoOutputStream.getBytes();
        }

        public static void packProtoMessage(PowerEntity[] powerEntityArr, ProtoOutputStream protoOutputStream) {
            if (powerEntityArr == null) {
                return;
            }
            for (int i = 0; i < powerEntityArr.length; i++) {
                long start = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, powerEntityArr[i].f8id);
                protoOutputStream.write(1138166333442L, powerEntityArr[i].name);
                State[] stateArr = powerEntityArr[i].states;
                if (stateArr != null) {
                    int length = stateArr.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        State state = powerEntityArr[i].states[i2];
                        long start2 = protoOutputStream.start(2246267895811L);
                        protoOutputStream.write(1120986464257L, state.f9id);
                        protoOutputStream.write(1138166333442L, state.name);
                        protoOutputStream.end(start2);
                    }
                }
                protoOutputStream.end(start);
            }
        }

        public static void dumpsys(PowerEntity[] powerEntityArr, PrintWriter printWriter) {
            if (powerEntityArr == null) {
                return;
            }
            for (int i = 0; i < powerEntityArr.length; i++) {
                printWriter.println("PowerEntityId: " + powerEntityArr[i].f8id + ", PowerEntityName: " + powerEntityArr[i].name);
                if (powerEntityArr[i].states != null) {
                    for (int i2 = 0; i2 < powerEntityArr[i].states.length; i2++) {
                        printWriter.println("  StateId: " + powerEntityArr[i].states[i2].f9id + ", StateName: " + powerEntityArr[i].states[i2].name);
                    }
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class StateResidencyResultUtils {
        public static void adjustTimeSinceBootToEpoch(StateResidencyResult[] stateResidencyResultArr, long j) {
            if (stateResidencyResultArr == null) {
                return;
            }
            for (int i = 0; i < stateResidencyResultArr.length; i++) {
                int length = stateResidencyResultArr[i].stateResidencyData.length;
                for (int i2 = 0; i2 < length; i2++) {
                    stateResidencyResultArr[i].stateResidencyData[i2].lastEntryTimestampMs += j;
                }
            }
        }

        public static byte[] getProtoBytes(StateResidencyResult[] stateResidencyResultArr) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            packProtoMessage(stateResidencyResultArr, protoOutputStream);
            return protoOutputStream.getBytes();
        }

        public static void packProtoMessage(StateResidencyResult[] stateResidencyResultArr, ProtoOutputStream protoOutputStream) {
            if (stateResidencyResultArr == null) {
                return;
            }
            for (int i = 0; i < stateResidencyResultArr.length; i++) {
                int length = stateResidencyResultArr[i].stateResidencyData.length;
                long j = 2246267895810L;
                long start = protoOutputStream.start(2246267895810L);
                long j2 = 1120986464257L;
                protoOutputStream.write(1120986464257L, stateResidencyResultArr[i].f11id);
                int i2 = 0;
                while (i2 < length) {
                    StateResidency stateResidency = stateResidencyResultArr[i].stateResidencyData[i2];
                    long start2 = protoOutputStream.start(j);
                    protoOutputStream.write(j2, stateResidency.f10id);
                    protoOutputStream.write(1112396529666L, stateResidency.totalTimeInStateMs);
                    protoOutputStream.write(1112396529667L, stateResidency.totalStateEntryCount);
                    protoOutputStream.write(1112396529668L, stateResidency.lastEntryTimestampMs);
                    protoOutputStream.end(start2);
                    i2++;
                    j = 2246267895810L;
                    j2 = 1120986464257L;
                }
                protoOutputStream.end(start);
            }
        }

        public static StateResidencyResult[] unpackProtoMessage(byte[] bArr) throws IOException {
            ProtoInputStream protoInputStream = new ProtoInputStream(new ByteArrayInputStream(bArr));
            ArrayList arrayList = new ArrayList();
            while (true) {
                try {
                    int nextField = protoInputStream.nextField();
                    new StateResidencyResult();
                    if (nextField == 2) {
                        long start = protoInputStream.start(2246267895810L);
                        arrayList.add(unpackStateResidencyResultProto(protoInputStream));
                        protoInputStream.end(start);
                    } else if (nextField == -1) {
                        return (StateResidencyResult[]) arrayList.toArray(new StateResidencyResult[arrayList.size()]);
                    } else {
                        String str = ProtoStreamUtils.TAG;
                        Slog.e(str, "Unhandled field in PowerStatsServiceResidencyProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                    }
                } catch (WireTypeMismatchException unused) {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Wire Type mismatch in PowerStatsServiceResidencyProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }

        public static StateResidencyResult unpackStateResidencyResultProto(ProtoInputStream protoInputStream) throws IOException {
            int nextField;
            StateResidencyResult stateResidencyResult = new StateResidencyResult();
            ArrayList arrayList = new ArrayList();
            while (true) {
                try {
                    nextField = protoInputStream.nextField();
                } catch (WireTypeMismatchException unused) {
                    String str = ProtoStreamUtils.TAG;
                    Slog.e(str, "Wire Type mismatch in StateResidencyResultProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
                if (nextField == -1) {
                    stateResidencyResult.stateResidencyData = (StateResidency[]) arrayList.toArray(new StateResidency[arrayList.size()]);
                    return stateResidencyResult;
                } else if (nextField == 1) {
                    stateResidencyResult.f11id = protoInputStream.readInt(1120986464257L);
                } else if (nextField == 2) {
                    long start = protoInputStream.start(2246267895810L);
                    arrayList.add(unpackStateResidencyProto(protoInputStream));
                    protoInputStream.end(start);
                } else {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Unhandled field in StateResidencyResultProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }

        public static StateResidency unpackStateResidencyProto(ProtoInputStream protoInputStream) throws IOException {
            int nextField;
            StateResidency stateResidency = new StateResidency();
            while (true) {
                try {
                    nextField = protoInputStream.nextField();
                } catch (WireTypeMismatchException unused) {
                    String str = ProtoStreamUtils.TAG;
                    Slog.e(str, "Wire Type mismatch in StateResidencyProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
                if (nextField == -1) {
                    return stateResidency;
                }
                if (nextField == 1) {
                    stateResidency.f10id = protoInputStream.readInt(1120986464257L);
                } else if (nextField == 2) {
                    stateResidency.totalTimeInStateMs = protoInputStream.readLong(1112396529666L);
                } else if (nextField == 3) {
                    stateResidency.totalStateEntryCount = protoInputStream.readLong(1112396529667L);
                } else if (nextField == 4) {
                    stateResidency.lastEntryTimestampMs = protoInputStream.readLong(1112396529668L);
                } else {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Unhandled field in StateResidencyProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class ChannelUtils {
        public static byte[] getProtoBytes(Channel[] channelArr) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            packProtoMessage(channelArr, protoOutputStream);
            return protoOutputStream.getBytes();
        }

        public static void packProtoMessage(Channel[] channelArr, ProtoOutputStream protoOutputStream) {
            if (channelArr == null) {
                return;
            }
            for (int i = 0; i < channelArr.length; i++) {
                long start = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, channelArr[i].f4id);
                protoOutputStream.write(1138166333442L, channelArr[i].name);
                protoOutputStream.write(1138166333443L, channelArr[i].subsystem);
                protoOutputStream.end(start);
            }
        }

        public static void dumpsys(Channel[] channelArr, PrintWriter printWriter) {
            if (channelArr == null) {
                return;
            }
            for (int i = 0; i < channelArr.length; i++) {
                printWriter.println("ChannelId: " + channelArr[i].f4id + ", ChannelName: " + channelArr[i].name + ", ChannelSubsystem: " + channelArr[i].subsystem);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class EnergyMeasurementUtils {
        public static void adjustTimeSinceBootToEpoch(EnergyMeasurement[] energyMeasurementArr, long j) {
            if (energyMeasurementArr == null) {
                return;
            }
            for (EnergyMeasurement energyMeasurement : energyMeasurementArr) {
                energyMeasurement.timestampMs += j;
            }
        }

        public static byte[] getProtoBytes(EnergyMeasurement[] energyMeasurementArr) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            packProtoMessage(energyMeasurementArr, protoOutputStream);
            return protoOutputStream.getBytes();
        }

        public static void packProtoMessage(EnergyMeasurement[] energyMeasurementArr, ProtoOutputStream protoOutputStream) {
            if (energyMeasurementArr == null) {
                return;
            }
            for (int i = 0; i < energyMeasurementArr.length; i++) {
                long start = protoOutputStream.start(2246267895810L);
                protoOutputStream.write(1120986464257L, energyMeasurementArr[i].f7id);
                protoOutputStream.write(1112396529666L, energyMeasurementArr[i].timestampMs);
                protoOutputStream.write(1112396529668L, energyMeasurementArr[i].durationMs);
                protoOutputStream.write(1112396529667L, energyMeasurementArr[i].energyUWs);
                protoOutputStream.end(start);
            }
        }

        public static EnergyMeasurement[] unpackProtoMessage(byte[] bArr) throws IOException {
            ProtoInputStream protoInputStream = new ProtoInputStream(new ByteArrayInputStream(bArr));
            ArrayList arrayList = new ArrayList();
            while (true) {
                try {
                    int nextField = protoInputStream.nextField();
                    new EnergyMeasurement();
                    if (nextField == 2) {
                        long start = protoInputStream.start(2246267895810L);
                        arrayList.add(unpackEnergyMeasurementProto(protoInputStream));
                        protoInputStream.end(start);
                    } else if (nextField == -1) {
                        return (EnergyMeasurement[]) arrayList.toArray(new EnergyMeasurement[arrayList.size()]);
                    } else {
                        String str = ProtoStreamUtils.TAG;
                        Slog.e(str, "Unhandled field in proto: " + ProtoUtils.currentFieldToString(protoInputStream));
                    }
                } catch (WireTypeMismatchException unused) {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Wire Type mismatch in proto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }

        public static EnergyMeasurement unpackEnergyMeasurementProto(ProtoInputStream protoInputStream) throws IOException {
            int nextField;
            EnergyMeasurement energyMeasurement = new EnergyMeasurement();
            while (true) {
                try {
                    nextField = protoInputStream.nextField();
                } catch (WireTypeMismatchException unused) {
                    String str = ProtoStreamUtils.TAG;
                    Slog.e(str, "Wire Type mismatch in EnergyMeasurementProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
                if (nextField == -1) {
                    return energyMeasurement;
                }
                if (nextField == 1) {
                    energyMeasurement.f7id = protoInputStream.readInt(1120986464257L);
                } else if (nextField == 2) {
                    energyMeasurement.timestampMs = protoInputStream.readLong(1112396529666L);
                } else if (nextField == 3) {
                    energyMeasurement.energyUWs = protoInputStream.readLong(1112396529667L);
                } else if (nextField == 4) {
                    energyMeasurement.durationMs = protoInputStream.readLong(1112396529668L);
                } else {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Unhandled field in EnergyMeasurementProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class EnergyConsumerUtils {
        public static byte[] getProtoBytes(EnergyConsumer[] energyConsumerArr) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            packProtoMessage(energyConsumerArr, protoOutputStream);
            return protoOutputStream.getBytes();
        }

        public static void packProtoMessage(EnergyConsumer[] energyConsumerArr, ProtoOutputStream protoOutputStream) {
            if (energyConsumerArr == null) {
                return;
            }
            for (int i = 0; i < energyConsumerArr.length; i++) {
                long start = protoOutputStream.start(2246267895809L);
                protoOutputStream.write(1120986464257L, energyConsumerArr[i].f5id);
                protoOutputStream.write(1120986464258L, energyConsumerArr[i].ordinal);
                protoOutputStream.write(1120986464259L, (int) energyConsumerArr[i].type);
                protoOutputStream.write(1138166333444L, energyConsumerArr[i].name);
                protoOutputStream.end(start);
            }
        }

        public static void dumpsys(EnergyConsumer[] energyConsumerArr, PrintWriter printWriter) {
            if (energyConsumerArr == null) {
                return;
            }
            for (int i = 0; i < energyConsumerArr.length; i++) {
                printWriter.println("EnergyConsumerId: " + energyConsumerArr[i].f5id + ", Ordinal: " + energyConsumerArr[i].ordinal + ", Type: " + ((int) energyConsumerArr[i].type) + ", Name: " + energyConsumerArr[i].name);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class EnergyConsumerResultUtils {
        public static void adjustTimeSinceBootToEpoch(EnergyConsumerResult[] energyConsumerResultArr, long j) {
            if (energyConsumerResultArr == null) {
                return;
            }
            for (EnergyConsumerResult energyConsumerResult : energyConsumerResultArr) {
                energyConsumerResult.timestampMs += j;
            }
        }

        public static byte[] getProtoBytes(EnergyConsumerResult[] energyConsumerResultArr, boolean z) {
            ProtoOutputStream protoOutputStream = new ProtoOutputStream();
            packProtoMessage(energyConsumerResultArr, protoOutputStream, z);
            return protoOutputStream.getBytes();
        }

        public static void packProtoMessage(EnergyConsumerResult[] energyConsumerResultArr, ProtoOutputStream protoOutputStream, boolean z) {
            if (energyConsumerResultArr == null) {
                return;
            }
            for (int i = 0; i < energyConsumerResultArr.length; i++) {
                long start = protoOutputStream.start(2246267895810L);
                long j = 1120986464257L;
                protoOutputStream.write(1120986464257L, energyConsumerResultArr[i].f6id);
                protoOutputStream.write(1112396529666L, energyConsumerResultArr[i].timestampMs);
                protoOutputStream.write(1112396529667L, energyConsumerResultArr[i].energyUWs);
                if (z) {
                    int length = energyConsumerResultArr[i].attribution.length;
                    int i2 = 0;
                    while (i2 < length) {
                        EnergyConsumerAttribution energyConsumerAttribution = energyConsumerResultArr[i].attribution[i2];
                        long start2 = protoOutputStream.start(2246267895812L);
                        protoOutputStream.write(j, energyConsumerAttribution.uid);
                        protoOutputStream.write(1112396529666L, energyConsumerAttribution.energyUWs);
                        protoOutputStream.end(start2);
                        i2++;
                        j = 1120986464257L;
                    }
                }
                protoOutputStream.end(start);
            }
        }

        public static EnergyConsumerResult[] unpackProtoMessage(byte[] bArr) throws IOException {
            ProtoInputStream protoInputStream = new ProtoInputStream(new ByteArrayInputStream(bArr));
            ArrayList arrayList = new ArrayList();
            while (true) {
                try {
                    int nextField = protoInputStream.nextField();
                    new EnergyConsumerResult();
                    if (nextField == 2) {
                        long start = protoInputStream.start(2246267895810L);
                        arrayList.add(unpackEnergyConsumerResultProto(protoInputStream));
                        protoInputStream.end(start);
                    } else if (nextField == -1) {
                        return (EnergyConsumerResult[]) arrayList.toArray(new EnergyConsumerResult[arrayList.size()]);
                    } else {
                        String str = ProtoStreamUtils.TAG;
                        Slog.e(str, "Unhandled field in proto: " + ProtoUtils.currentFieldToString(protoInputStream));
                    }
                } catch (WireTypeMismatchException unused) {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Wire Type mismatch in proto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }

        public static EnergyConsumerAttribution unpackEnergyConsumerAttributionProto(ProtoInputStream protoInputStream) throws IOException {
            int nextField;
            EnergyConsumerAttribution energyConsumerAttribution = new EnergyConsumerAttribution();
            while (true) {
                try {
                    nextField = protoInputStream.nextField();
                } catch (WireTypeMismatchException unused) {
                    String str = ProtoStreamUtils.TAG;
                    Slog.e(str, "Wire Type mismatch in EnergyConsumerAttributionProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
                if (nextField == -1) {
                    return energyConsumerAttribution;
                }
                if (nextField == 1) {
                    energyConsumerAttribution.uid = protoInputStream.readInt(1120986464257L);
                } else if (nextField == 2) {
                    energyConsumerAttribution.energyUWs = protoInputStream.readLong(1112396529666L);
                } else {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Unhandled field in EnergyConsumerAttributionProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }

        public static EnergyConsumerResult unpackEnergyConsumerResultProto(ProtoInputStream protoInputStream) throws IOException {
            int nextField;
            EnergyConsumerResult energyConsumerResult = new EnergyConsumerResult();
            ArrayList arrayList = new ArrayList();
            while (true) {
                try {
                    nextField = protoInputStream.nextField();
                } catch (WireTypeMismatchException unused) {
                    String str = ProtoStreamUtils.TAG;
                    Slog.e(str, "Wire Type mismatch in EnergyConsumerResultProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
                if (nextField == -1) {
                    energyConsumerResult.attribution = (EnergyConsumerAttribution[]) arrayList.toArray(new EnergyConsumerAttribution[arrayList.size()]);
                    return energyConsumerResult;
                } else if (nextField == 1) {
                    energyConsumerResult.f6id = protoInputStream.readInt(1120986464257L);
                } else if (nextField == 2) {
                    energyConsumerResult.timestampMs = protoInputStream.readLong(1112396529666L);
                } else if (nextField == 3) {
                    energyConsumerResult.energyUWs = protoInputStream.readLong(1112396529667L);
                } else if (nextField == 4) {
                    long start = protoInputStream.start(2246267895812L);
                    arrayList.add(unpackEnergyConsumerAttributionProto(protoInputStream));
                    protoInputStream.end(start);
                } else {
                    String str2 = ProtoStreamUtils.TAG;
                    Slog.e(str2, "Unhandled field in EnergyConsumerResultProto: " + ProtoUtils.currentFieldToString(protoInputStream));
                }
            }
        }
    }
}
