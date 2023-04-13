package com.android.server.power.stats;

import android.hardware.power.stats.EnergyConsumer;
import android.hardware.power.stats.EnergyConsumerAttribution;
import android.hardware.power.stats.EnergyConsumerResult;
import android.os.BatteryStats;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
/* loaded from: classes2.dex */
public class EnergyConsumerSnapshot {
    public final SparseArray<SparseLongArray> mAttributionSnapshots;
    public BatteryStats.EnergyConsumerDetails mEnergyConsumerDetails;
    public final SparseLongArray mEnergyConsumerSnapshots;
    public final SparseArray<EnergyConsumer> mEnergyConsumers;
    public final int mNumCpuClusterOrdinals;
    public final int mNumDisplayOrdinals;
    public final int mNumOtherOrdinals;
    public final SparseIntArray mVoltageSnapshots;

    public EnergyConsumerSnapshot(SparseArray<EnergyConsumer> sparseArray) {
        this.mEnergyConsumers = sparseArray;
        this.mEnergyConsumerSnapshots = new SparseLongArray(sparseArray.size());
        this.mVoltageSnapshots = new SparseIntArray(sparseArray.size());
        this.mNumCpuClusterOrdinals = calculateNumOrdinals(2, sparseArray);
        this.mNumDisplayOrdinals = calculateNumOrdinals(3, sparseArray);
        int calculateNumOrdinals = calculateNumOrdinals(0, sparseArray);
        this.mNumOtherOrdinals = calculateNumOrdinals;
        this.mAttributionSnapshots = new SparseArray<>(calculateNumOrdinals);
    }

    /* loaded from: classes2.dex */
    public static class EnergyConsumerDeltaData {
        public long bluetoothChargeUC = -1;
        public long[] cpuClusterChargeUC = null;
        public long[] displayChargeUC = null;
        public long gnssChargeUC = -1;
        public long mobileRadioChargeUC = -1;
        public long wifiChargeUC = -1;
        public long cameraChargeUC = -1;
        public long[] otherTotalChargeUC = null;
        public SparseLongArray[] otherUidChargesUC = null;

        public boolean isEmpty() {
            return this.bluetoothChargeUC <= 0 && isEmpty(this.cpuClusterChargeUC) && isEmpty(this.displayChargeUC) && this.gnssChargeUC <= 0 && this.mobileRadioChargeUC <= 0 && this.wifiChargeUC <= 0 && isEmpty(this.otherTotalChargeUC);
        }

        public final boolean isEmpty(long[] jArr) {
            if (jArr == null) {
                return true;
            }
            for (long j : jArr) {
                if (j > 0) {
                    return false;
                }
            }
            return true;
        }
    }

    public EnergyConsumerDeltaData updateAndGetDelta(EnergyConsumerResult[] energyConsumerResultArr, int i) {
        int i2;
        int i3;
        String str;
        EnergyConsumerResult[] energyConsumerResultArr2 = energyConsumerResultArr;
        EnergyConsumer energyConsumer = null;
        if (energyConsumerResultArr2 == null || energyConsumerResultArr2.length == 0) {
            return null;
        }
        String str2 = "EnergyConsumerSnapshot";
        if (i <= 0) {
            Slog.wtf("EnergyConsumerSnapshot", "Unexpected battery voltage (" + i + " mV) when taking energy consumer snapshot");
            return null;
        }
        EnergyConsumerDeltaData energyConsumerDeltaData = new EnergyConsumerDeltaData();
        int length = energyConsumerResultArr2.length;
        int i4 = 0;
        while (i4 < length) {
            EnergyConsumerResult energyConsumerResult = energyConsumerResultArr2[i4];
            int i5 = energyConsumerResult.f6id;
            long j = energyConsumerResult.energyUWs;
            EnergyConsumerAttribution[] energyConsumerAttributionArr = energyConsumerResult.attribution;
            EnergyConsumer energyConsumer2 = this.mEnergyConsumers.get(i5, energyConsumer);
            if (energyConsumer2 == null) {
                Slog.e(str2, "updateAndGetDelta given invalid consumerId " + i5);
                i2 = length;
                i3 = i4;
                str = str2;
            } else {
                byte b = energyConsumer2.type;
                int i6 = energyConsumer2.ordinal;
                String str3 = str2;
                long j2 = this.mEnergyConsumerSnapshots.get(i5, -1L);
                int i7 = this.mVoltageSnapshots.get(i5);
                this.mEnergyConsumerSnapshots.put(i5, j);
                this.mVoltageSnapshots.put(i5, i);
                int i8 = ((i7 + i) + 1) / 2;
                SparseLongArray updateAndGetDeltaForTypeOther = updateAndGetDeltaForTypeOther(energyConsumer2, energyConsumerAttributionArr, i8);
                if (j2 >= 0 && j != j2) {
                    i2 = length;
                    i3 = i4;
                    long j3 = j - j2;
                    if (j3 < 0 || i7 <= 0) {
                        str = str3;
                        Slog.e(str, "Bad data! EnergyConsumer " + energyConsumer2.name + ": new energy (" + j + ") < old energy (" + j2 + "), new voltage (" + i + "), old voltage (" + i7 + "). Skipping. ");
                    } else {
                        long calculateChargeConsumedUC = calculateChargeConsumedUC(j3, i8);
                        switch (b) {
                            case 0:
                                if (energyConsumerDeltaData.otherTotalChargeUC == null) {
                                    int i9 = this.mNumOtherOrdinals;
                                    energyConsumerDeltaData.otherTotalChargeUC = new long[i9];
                                    energyConsumerDeltaData.otherUidChargesUC = new SparseLongArray[i9];
                                }
                                energyConsumerDeltaData.otherTotalChargeUC[i6] = calculateChargeConsumedUC;
                                energyConsumerDeltaData.otherUidChargesUC[i6] = updateAndGetDeltaForTypeOther;
                                break;
                            case 1:
                                energyConsumerDeltaData.bluetoothChargeUC = calculateChargeConsumedUC;
                                break;
                            case 2:
                                if (energyConsumerDeltaData.cpuClusterChargeUC == null) {
                                    energyConsumerDeltaData.cpuClusterChargeUC = new long[this.mNumCpuClusterOrdinals];
                                }
                                energyConsumerDeltaData.cpuClusterChargeUC[i6] = calculateChargeConsumedUC;
                                break;
                            case 3:
                                if (energyConsumerDeltaData.displayChargeUC == null) {
                                    energyConsumerDeltaData.displayChargeUC = new long[this.mNumDisplayOrdinals];
                                }
                                energyConsumerDeltaData.displayChargeUC[i6] = calculateChargeConsumedUC;
                                break;
                            case 4:
                                energyConsumerDeltaData.gnssChargeUC = calculateChargeConsumedUC;
                                break;
                            case 5:
                                energyConsumerDeltaData.mobileRadioChargeUC = calculateChargeConsumedUC;
                                break;
                            case 6:
                                energyConsumerDeltaData.wifiChargeUC = calculateChargeConsumedUC;
                                break;
                            case 7:
                                energyConsumerDeltaData.cameraChargeUC = calculateChargeConsumedUC;
                                break;
                            default:
                                str = str3;
                                Slog.w(str, "Ignoring consumer " + energyConsumer2.name + " of unknown type " + ((int) b));
                                break;
                        }
                    }
                } else {
                    i2 = length;
                    i3 = i4;
                }
                str = str3;
            }
            i4 = i3 + 1;
            energyConsumerResultArr2 = energyConsumerResultArr;
            str2 = str;
            length = i2;
            energyConsumer = null;
        }
        return energyConsumerDeltaData;
    }

    public final SparseLongArray updateAndGetDeltaForTypeOther(EnergyConsumer energyConsumer, EnergyConsumerAttribution[] energyConsumerAttributionArr, int i) {
        EnergyConsumerAttribution[] energyConsumerAttributionArr2;
        SparseLongArray sparseLongArray;
        if (energyConsumer.type != 0) {
            return null;
        }
        int i2 = 0;
        EnergyConsumerAttribution[] energyConsumerAttributionArr3 = energyConsumerAttributionArr == null ? new EnergyConsumerAttribution[0] : energyConsumerAttributionArr;
        SparseLongArray sparseLongArray2 = this.mAttributionSnapshots.get(energyConsumer.f5id, null);
        if (sparseLongArray2 == null) {
            SparseLongArray sparseLongArray3 = new SparseLongArray(energyConsumerAttributionArr3.length);
            this.mAttributionSnapshots.put(energyConsumer.f5id, sparseLongArray3);
            int length = energyConsumerAttributionArr3.length;
            while (i2 < length) {
                EnergyConsumerAttribution energyConsumerAttribution = energyConsumerAttributionArr3[i2];
                sparseLongArray3.put(energyConsumerAttribution.uid, energyConsumerAttribution.energyUWs);
                i2++;
            }
            return null;
        }
        SparseLongArray sparseLongArray4 = new SparseLongArray();
        int length2 = energyConsumerAttributionArr3.length;
        while (i2 < length2) {
            EnergyConsumerAttribution energyConsumerAttribution2 = energyConsumerAttributionArr3[i2];
            int i3 = energyConsumerAttribution2.uid;
            long j = energyConsumerAttribution2.energyUWs;
            long j2 = sparseLongArray2.get(i3, 0L);
            sparseLongArray2.put(i3, j);
            if (j2 >= 0 && j != j2) {
                energyConsumerAttributionArr2 = energyConsumerAttributionArr3;
                sparseLongArray = sparseLongArray2;
                long j3 = j - j2;
                if (j3 < 0 || i <= 0) {
                    Slog.e("EnergyConsumerSnapshot", "EnergyConsumer " + energyConsumer.name + ": new energy (" + j + ") but old energy (" + j2 + "). Average voltage (" + i + ")Skipping. ");
                } else {
                    sparseLongArray4.put(i3, calculateChargeConsumedUC(j3, i));
                }
            } else {
                energyConsumerAttributionArr2 = energyConsumerAttributionArr3;
                sparseLongArray = sparseLongArray2;
            }
            i2++;
            sparseLongArray2 = sparseLongArray;
            energyConsumerAttributionArr3 = energyConsumerAttributionArr2;
        }
        return sparseLongArray4;
    }

    public String[] getOtherOrdinalNames() {
        String[] strArr = new String[this.mNumOtherOrdinals];
        int size = this.mEnergyConsumers.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            EnergyConsumer valueAt = this.mEnergyConsumers.valueAt(i2);
            if (valueAt.type == 0) {
                strArr[i] = sanitizeCustomBucketName(valueAt.name);
                i++;
            }
        }
        return strArr;
    }

    public final String sanitizeCustomBucketName(String str) {
        char[] charArray;
        if (str == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length());
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                sb.append(' ');
            } else if (Character.isISOControl(c)) {
                sb.append('_');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static int calculateNumOrdinals(int i, SparseArray<EnergyConsumer> sparseArray) {
        if (sparseArray == null) {
            return 0;
        }
        int size = sparseArray.size();
        int i2 = 0;
        for (int i3 = 0; i3 < size; i3++) {
            if (sparseArray.valueAt(i3).type == i) {
                i2++;
            }
        }
        return i2;
    }

    public final long calculateChargeConsumedUC(long j, int i) {
        return ((j * 1000) + (i / 2)) / i;
    }

    public BatteryStats.EnergyConsumerDetails getEnergyConsumerDetails(EnergyConsumerDeltaData energyConsumerDeltaData) {
        if (this.mEnergyConsumerDetails == null) {
            this.mEnergyConsumerDetails = createEnergyConsumerDetails();
        }
        long[] jArr = this.mEnergyConsumerDetails.chargeUC;
        int i = 0;
        while (true) {
            BatteryStats.EnergyConsumerDetails energyConsumerDetails = this.mEnergyConsumerDetails;
            BatteryStats.EnergyConsumerDetails.EnergyConsumer[] energyConsumerArr = energyConsumerDetails.consumers;
            if (i >= energyConsumerArr.length) {
                return energyConsumerDetails;
            }
            BatteryStats.EnergyConsumerDetails.EnergyConsumer energyConsumer = energyConsumerArr[i];
            switch (energyConsumer.type) {
                case 0:
                    long[] jArr2 = energyConsumerDeltaData.otherTotalChargeUC;
                    if (jArr2 != null) {
                        jArr[i] = jArr2[energyConsumer.ordinal];
                        break;
                    } else {
                        jArr[i] = -1;
                        break;
                    }
                case 1:
                    jArr[i] = energyConsumerDeltaData.bluetoothChargeUC;
                    break;
                case 2:
                    long[] jArr3 = energyConsumerDeltaData.cpuClusterChargeUC;
                    if (jArr3 != null) {
                        jArr[i] = jArr3[energyConsumer.ordinal];
                        break;
                    } else {
                        jArr[i] = -1;
                        break;
                    }
                case 3:
                    long[] jArr4 = energyConsumerDeltaData.displayChargeUC;
                    if (jArr4 != null) {
                        jArr[i] = jArr4[energyConsumer.ordinal];
                        break;
                    } else {
                        jArr[i] = -1;
                        break;
                    }
                case 4:
                    jArr[i] = energyConsumerDeltaData.gnssChargeUC;
                    break;
                case 5:
                    jArr[i] = energyConsumerDeltaData.mobileRadioChargeUC;
                    break;
                case 6:
                    jArr[i] = energyConsumerDeltaData.wifiChargeUC;
                    break;
                case 7:
                    jArr[i] = energyConsumerDeltaData.cameraChargeUC;
                    break;
                default:
                    jArr[i] = -1;
                    break;
            }
            i++;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00a6 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final BatteryStats.EnergyConsumerDetails createEnergyConsumerDetails() {
        BatteryStats.EnergyConsumerDetails energyConsumerDetails = new BatteryStats.EnergyConsumerDetails();
        energyConsumerDetails.consumers = new BatteryStats.EnergyConsumerDetails.EnergyConsumer[this.mEnergyConsumers.size()];
        for (int i = 0; i < this.mEnergyConsumers.size(); i++) {
            EnergyConsumer valueAt = this.mEnergyConsumers.valueAt(i);
            BatteryStats.EnergyConsumerDetails.EnergyConsumer energyConsumer = new BatteryStats.EnergyConsumerDetails.EnergyConsumer();
            byte b = valueAt.type;
            energyConsumer.type = b;
            energyConsumer.ordinal = valueAt.ordinal;
            switch (b) {
                case 0:
                    energyConsumer.name = sanitizeCustomBucketName(valueAt.name);
                    break;
                case 1:
                    energyConsumer.name = "BLUETOOTH";
                    break;
                case 2:
                    energyConsumer.name = "CPU";
                    break;
                case 3:
                    energyConsumer.name = "DISPLAY";
                    break;
                case 4:
                    energyConsumer.name = "GNSS";
                    break;
                case 5:
                    energyConsumer.name = "MOBILE_RADIO";
                    break;
                case 6:
                    energyConsumer.name = "WIFI";
                    break;
                default:
                    energyConsumer.name = "UNKNOWN";
                    break;
            }
            if (energyConsumer.type != 0) {
                boolean z = true;
                boolean z2 = energyConsumer.ordinal != 0;
                if (!z2) {
                    for (int i2 = 0; i2 < this.mEnergyConsumers.size(); i2++) {
                        EnergyConsumer valueAt2 = this.mEnergyConsumers.valueAt(i2);
                        if (valueAt2.type == energyConsumer.type && valueAt2.ordinal != 0) {
                            if (!z) {
                                energyConsumer.name += "/" + valueAt.ordinal;
                            }
                        }
                    }
                }
                z = z2;
                if (!z) {
                }
            }
            energyConsumerDetails.consumers[i] = energyConsumer;
        }
        energyConsumerDetails.chargeUC = new long[energyConsumerDetails.consumers.length];
        return energyConsumerDetails;
    }
}
