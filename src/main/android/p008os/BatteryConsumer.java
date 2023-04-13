package android.p008os;

import android.content.Context;
import android.database.CursorWindow;
import android.p008os.PowerComponents;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.accessibility.common.ShortcutConstants;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
/* renamed from: android.os.BatteryConsumer */
/* loaded from: classes3.dex */
public abstract class BatteryConsumer {
    static final int COLUMN_COUNT = 1;
    static final int COLUMN_INDEX_BATTERY_CONSUMER_TYPE = 0;
    public static final int FIRST_CUSTOM_POWER_COMPONENT_ID = 1000;
    public static final int LAST_CUSTOM_POWER_COMPONENT_ID = 9999;
    public static final int POWER_COMPONENT_AMBIENT_DISPLAY = 15;
    public static final int POWER_COMPONENT_ANY = -1;
    public static final int POWER_COMPONENT_AUDIO = 4;
    public static final int POWER_COMPONENT_BLUETOOTH = 2;
    public static final int POWER_COMPONENT_CAMERA = 3;
    public static final int POWER_COMPONENT_COUNT = 18;
    public static final int POWER_COMPONENT_CPU = 1;
    public static final int POWER_COMPONENT_FLASHLIGHT = 6;
    public static final int POWER_COMPONENT_GNSS = 10;
    public static final int POWER_COMPONENT_IDLE = 16;
    public static final int POWER_COMPONENT_MEMORY = 13;
    public static final int POWER_COMPONENT_MOBILE_RADIO = 8;
    public static final int POWER_COMPONENT_PHONE = 14;
    public static final int POWER_COMPONENT_REATTRIBUTED_TO_OTHER_CONSUMERS = 17;
    public static final int POWER_COMPONENT_SCREEN = 0;
    public static final int POWER_COMPONENT_SENSORS = 9;
    public static final int POWER_COMPONENT_SYSTEM_SERVICES = 7;
    public static final int POWER_COMPONENT_VIDEO = 5;
    public static final int POWER_COMPONENT_WAKELOCK = 12;
    public static final int POWER_COMPONENT_WIFI = 11;
    public static final int POWER_MODEL_ENERGY_CONSUMPTION = 2;
    public static final int POWER_MODEL_POWER_PROFILE = 1;
    public static final int POWER_MODEL_UNDEFINED = 0;
    public static final int PROCESS_STATE_ANY = 0;
    public static final int PROCESS_STATE_BACKGROUND = 2;
    public static final int PROCESS_STATE_CACHED = 4;
    public static final int PROCESS_STATE_COUNT = 5;
    public static final int PROCESS_STATE_FOREGROUND = 1;
    public static final int PROCESS_STATE_FOREGROUND_SERVICE = 3;
    public static final int PROCESS_STATE_UNSPECIFIED = 0;
    private static final int[] SUPPORTED_POWER_COMPONENTS_PER_PROCESS_STATE;
    private static final String TAG = "BatteryConsumer";
    public static final Dimensions UNSPECIFIED_DIMENSIONS;
    private static final String[] sPowerComponentNames;
    private static final String[] sProcessStateNames;
    protected final BatteryConsumerData mData;
    protected final PowerComponents mPowerComponents;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryConsumer$PowerComponent */
    /* loaded from: classes3.dex */
    public @interface PowerComponent {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryConsumer$PowerModel */
    /* loaded from: classes3.dex */
    public @interface PowerModel {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BatteryConsumer$ProcessState */
    /* loaded from: classes3.dex */
    public @interface ProcessState {
    }

    public abstract void dump(PrintWriter printWriter, boolean z);

    static {
        sPowerComponentNames = r0;
        String[] strArr = {"screen", "cpu", "bluetooth", Context.CAMERA_SERVICE, "audio", "video", "flashlight", "system_services", "mobile_radio", "sensors", "gnss", "wifi", "wakelock", "memory", "phone", "ambient_display", "idle", "reattributed"};
        sProcessStateNames = r0;
        String[] strArr2 = {"unspecified", "fg", "bg", "fgs", "cached"};
        SUPPORTED_POWER_COMPONENTS_PER_PROCESS_STATE = new int[]{1, 8, 11, 2};
        UNSPECIFIED_DIMENSIONS = new Dimensions(-1, 0);
    }

    /* renamed from: android.os.BatteryConsumer$Dimensions */
    /* loaded from: classes3.dex */
    public static final class Dimensions {
        public final int powerComponent;
        public final int processState;

        public Dimensions(int powerComponent, int processState) {
            this.powerComponent = powerComponent;
            this.processState = processState;
        }

        public String toString() {
            boolean dimensionSpecified = false;
            StringBuilder sb = new StringBuilder();
            if (this.powerComponent != -1) {
                sb.append("powerComponent=").append(BatteryConsumer.sPowerComponentNames[this.powerComponent]);
                dimensionSpecified = true;
            }
            if (this.processState != 0) {
                if (dimensionSpecified) {
                    sb.append(", ");
                }
                sb.append("processState=").append(BatteryConsumer.sProcessStateNames[this.processState]);
                dimensionSpecified = true;
            }
            if (!dimensionSpecified) {
                sb.append("any components and process states");
            }
            return sb.toString();
        }
    }

    /* renamed from: android.os.BatteryConsumer$Key */
    /* loaded from: classes3.dex */
    public static final class Key {
        final int mDurationColumnIndex;
        final int mPowerColumnIndex;
        final int mPowerModelColumnIndex;
        private String mShortString;
        public final int powerComponent;
        public final int processState;

        private Key(int powerComponent, int processState, int powerModelColumnIndex, int powerColumnIndex, int durationColumnIndex) {
            this.powerComponent = powerComponent;
            this.processState = processState;
            this.mPowerModelColumnIndex = powerModelColumnIndex;
            this.mPowerColumnIndex = powerColumnIndex;
            this.mDurationColumnIndex = durationColumnIndex;
        }

        public boolean equals(Object o) {
            Key key = (Key) o;
            return this.powerComponent == key.powerComponent && this.processState == key.processState;
        }

        public int hashCode() {
            int result = this.powerComponent;
            return (result * 31) + this.processState;
        }

        public String toShortString() {
            if (this.mShortString == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(BatteryConsumer.powerComponentIdToString(this.powerComponent));
                if (this.processState != 0) {
                    sb.append(ShortcutConstants.SERVICES_SEPARATOR);
                    sb.append(BatteryConsumer.processStateToString(this.processState));
                }
                this.mShortString = sb.toString();
            }
            return this.mShortString;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BatteryConsumer(BatteryConsumerData data, PowerComponents powerComponents) {
        this.mData = data;
        this.mPowerComponents = powerComponents;
    }

    public BatteryConsumer(BatteryConsumerData data) {
        this.mData = data;
        this.mPowerComponents = new PowerComponents(data);
    }

    public double getConsumedPower() {
        return this.mPowerComponents.getConsumedPower(UNSPECIFIED_DIMENSIONS);
    }

    public double getConsumedPower(Dimensions dimensions) {
        return this.mPowerComponents.getConsumedPower(dimensions);
    }

    public Key[] getKeys(int componentId) {
        return this.mData.getKeys(componentId);
    }

    public Key getKey(int componentId) {
        return this.mData.getKey(componentId, 0);
    }

    public Key getKey(int componentId, int processState) {
        return this.mData.getKey(componentId, processState);
    }

    public double getConsumedPower(int componentId) {
        return this.mPowerComponents.getConsumedPower(this.mData.getKeyOrThrow(componentId, 0));
    }

    public double getConsumedPower(Key key) {
        return this.mPowerComponents.getConsumedPower(key);
    }

    public int getPowerModel(int componentId) {
        return this.mPowerComponents.getPowerModel(this.mData.getKeyOrThrow(componentId, 0));
    }

    public int getPowerModel(Key key) {
        return this.mPowerComponents.getPowerModel(key);
    }

    public double getConsumedPowerForCustomComponent(int componentId) {
        return this.mPowerComponents.getConsumedPowerForCustomComponent(componentId);
    }

    public int getCustomPowerComponentCount() {
        return this.mData.layout.customPowerComponentCount;
    }

    public String getCustomPowerComponentName(int componentId) {
        return this.mPowerComponents.getCustomPowerComponentName(componentId);
    }

    public long getUsageDurationMillis(int componentId) {
        return this.mPowerComponents.getUsageDurationMillis(getKey(componentId));
    }

    public long getUsageDurationMillis(Key key) {
        return this.mPowerComponents.getUsageDurationMillis(key);
    }

    public long getUsageDurationForCustomComponentMillis(int componentId) {
        return this.mPowerComponents.getUsageDurationForCustomComponentMillis(componentId);
    }

    public static String powerComponentIdToString(int componentId) {
        if (componentId == -1) {
            return "all";
        }
        return sPowerComponentNames[componentId];
    }

    public static String powerModelToString(int powerModel) {
        switch (powerModel) {
            case 1:
                return "power profile";
            case 2:
                return "energy consumption";
            default:
                return "";
        }
    }

    public static int powerModelToProtoEnum(int powerModel) {
        switch (powerModel) {
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    public static String processStateToString(int processState) {
        return sProcessStateNames[processState];
    }

    public void dump(PrintWriter pw) {
        dump(pw, true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasStatsProtoData() {
        return writeStatsProtoImpl(null, 0L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeStatsProto(ProtoOutputStream proto, long fieldId) {
        writeStatsProtoImpl(proto, fieldId);
    }

    private boolean writeStatsProtoImpl(ProtoOutputStream proto, long fieldId) {
        long totalConsumedPowerDeciCoulombs = convertMahToDeciCoulombs(getConsumedPower());
        if (totalConsumedPowerDeciCoulombs == 0) {
            return false;
        }
        if (proto == null) {
            return true;
        }
        long token = proto.start(fieldId);
        proto.write(1112396529665L, totalConsumedPowerDeciCoulombs);
        this.mPowerComponents.writeStatsProto(proto);
        proto.end(token);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long convertMahToDeciCoulombs(double powerMah) {
        return (long) ((36.0d * powerMah) + 0.5d);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.BatteryConsumer$BatteryConsumerData */
    /* loaded from: classes3.dex */
    public static class BatteryConsumerData {
        public final BatteryConsumerDataLayout layout;
        private final int mCursorRow;
        private final CursorWindow mCursorWindow;

        /* JADX INFO: Access modifiers changed from: package-private */
        public BatteryConsumerData(CursorWindow cursorWindow, int cursorRow, BatteryConsumerDataLayout layout) {
            this.mCursorWindow = cursorWindow;
            this.mCursorRow = cursorRow;
            this.layout = layout;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static BatteryConsumerData create(CursorWindow cursorWindow, BatteryConsumerDataLayout layout) {
            int cursorRow = cursorWindow.getNumRows();
            if (!cursorWindow.allocRow()) {
                Slog.m96e(BatteryConsumer.TAG, "Cannot allocate BatteryConsumerData: too many UIDs: " + cursorRow);
                cursorRow = -1;
            }
            return new BatteryConsumerData(cursorWindow, cursorRow, layout);
        }

        public Key[] getKeys(int componentId) {
            return this.layout.keys[componentId];
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Key getKeyOrThrow(int componentId, int processState) {
            Key key = getKey(componentId, processState);
            if (key == null) {
                if (processState == 0) {
                    throw new IllegalArgumentException("Unsupported power component ID: " + componentId);
                }
                throw new IllegalArgumentException("Unsupported power component ID: " + componentId + " process state: " + processState);
            }
            return key;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Key getKey(int componentId, int processState) {
            Key[] keyArr;
            if (componentId >= 18) {
                return null;
            }
            if (processState == 0) {
                return this.layout.keys[componentId][0];
            }
            for (Key key : this.layout.keys[componentId]) {
                if (key.processState == processState) {
                    return key;
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void putInt(int columnIndex, int value) {
            int i = this.mCursorRow;
            if (i == -1) {
                return;
            }
            this.mCursorWindow.putLong(value, i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getInt(int columnIndex) {
            int i = this.mCursorRow;
            if (i == -1) {
                return 0;
            }
            return this.mCursorWindow.getInt(i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void putDouble(int columnIndex, double value) {
            int i = this.mCursorRow;
            if (i == -1) {
                return;
            }
            this.mCursorWindow.putDouble(value, i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public double getDouble(int columnIndex) {
            int i = this.mCursorRow;
            if (i == -1) {
                return 0.0d;
            }
            return this.mCursorWindow.getDouble(i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void putLong(int columnIndex, long value) {
            int i = this.mCursorRow;
            if (i == -1) {
                return;
            }
            this.mCursorWindow.putLong(value, i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public long getLong(int columnIndex) {
            int i = this.mCursorRow;
            if (i == -1) {
                return 0L;
            }
            return this.mCursorWindow.getLong(i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void putString(int columnIndex, String value) {
            int i = this.mCursorRow;
            if (i == -1) {
                return;
            }
            this.mCursorWindow.putString(value, i, columnIndex);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getString(int columnIndex) {
            int i = this.mCursorRow;
            if (i == -1) {
                return null;
            }
            return this.mCursorWindow.getString(i, columnIndex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.BatteryConsumer$BatteryConsumerDataLayout */
    /* loaded from: classes3.dex */
    public static class BatteryConsumerDataLayout {
        private static final Key[] KEY_ARRAY = new Key[0];
        public final int columnCount;
        public final int customPowerComponentCount;
        public final String[] customPowerComponentNames;
        public final int firstCustomConsumedPowerColumn;
        public final int firstCustomUsageDurationColumn;
        public final Key[][] keys;
        public final boolean powerModelsIncluded;
        public final boolean processStateDataIncluded;
        public final Key[][] processStateKeys;
        public final int totalConsumedPowerColumnIndex;

        private BatteryConsumerDataLayout(int firstColumn, String[] customPowerComponentNames, boolean powerModelsIncluded, boolean includeProcessStateData) {
            int columnIndex;
            int i;
            int columnIndex2;
            int i2;
            int processState;
            this.customPowerComponentNames = customPowerComponentNames;
            this.customPowerComponentCount = customPowerComponentNames.length;
            this.powerModelsIncluded = powerModelsIncluded;
            this.processStateDataIncluded = includeProcessStateData;
            int columnIndex3 = firstColumn + 1;
            this.totalConsumedPowerColumnIndex = firstColumn;
            int i3 = 18;
            this.keys = new Key[18];
            ArrayList<Key> perComponentKeys = new ArrayList<>();
            int componentId = 0;
            while (true) {
                if (componentId >= i3) {
                    break;
                }
                perComponentKeys.clear();
                if (powerModelsIncluded) {
                    i = columnIndex3;
                    columnIndex = columnIndex3 + 1;
                } else {
                    columnIndex = columnIndex3;
                    i = -1;
                }
                int columnIndex4 = columnIndex + 1;
                columnIndex3 = columnIndex4 + 1;
                perComponentKeys.add(new Key(componentId, 0, i, columnIndex, columnIndex4));
                if (includeProcessStateData) {
                    boolean isSupported = false;
                    int[] iArr = BatteryConsumer.SUPPORTED_POWER_COMPONENTS_PER_PROCESS_STATE;
                    int length = iArr.length;
                    int i4 = 0;
                    while (true) {
                        if (i4 >= length) {
                            break;
                        }
                        int id = iArr[i4];
                        if (id != componentId) {
                            i4++;
                        } else {
                            isSupported = true;
                            break;
                        }
                    }
                    if (isSupported) {
                        int processState2 = 0;
                        for (int i5 = 5; processState2 < i5; i5 = 5) {
                            if (processState2 == 0) {
                                processState = processState2;
                            } else {
                                if (powerModelsIncluded) {
                                    i2 = columnIndex3;
                                    columnIndex2 = columnIndex3 + 1;
                                } else {
                                    columnIndex2 = columnIndex3;
                                    i2 = -1;
                                }
                                int columnIndex5 = columnIndex2 + 1;
                                processState = processState2;
                                perComponentKeys.add(new Key(componentId, processState2, i2, columnIndex2, columnIndex5));
                                columnIndex3 = columnIndex5 + 1;
                            }
                            processState2 = processState + 1;
                        }
                    }
                }
                this.keys[componentId] = (Key[]) perComponentKeys.toArray(KEY_ARRAY);
                componentId++;
                i3 = 18;
            }
            if (includeProcessStateData) {
                this.processStateKeys = new Key[5];
                ArrayList<Key> perProcStateKeys = new ArrayList<>();
                for (int processState3 = 0; processState3 < 5; processState3++) {
                    if (processState3 != 0) {
                        perProcStateKeys.clear();
                        for (int i6 = 0; i6 < this.keys.length; i6++) {
                            int j = 0;
                            while (true) {
                                Key[] keyArr = this.keys[i6];
                                if (j < keyArr.length) {
                                    if (keyArr[j].processState == processState3) {
                                        perProcStateKeys.add(this.keys[i6][j]);
                                    }
                                    j++;
                                }
                            }
                        }
                        this.processStateKeys[processState3] = (Key[]) perProcStateKeys.toArray(KEY_ARRAY);
                    }
                }
            } else {
                this.processStateKeys = null;
            }
            this.firstCustomConsumedPowerColumn = columnIndex3;
            int i7 = this.customPowerComponentCount;
            int columnIndex6 = columnIndex3 + i7;
            this.firstCustomUsageDurationColumn = columnIndex6;
            this.columnCount = columnIndex6 + i7;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static BatteryConsumerDataLayout createBatteryConsumerDataLayout(String[] customPowerComponentNames, boolean includePowerModels, boolean includeProcessStateData) {
        int columnCount = Math.max(1, 3);
        return new BatteryConsumerDataLayout(Math.max(Math.max(columnCount, 5), 2), customPowerComponentNames, includePowerModels, includeProcessStateData);
    }

    /* renamed from: android.os.BatteryConsumer$BaseBuilder */
    /* loaded from: classes3.dex */
    protected static abstract class BaseBuilder<T extends BaseBuilder<?>> {
        protected final BatteryConsumerData mData;
        protected final PowerComponents.Builder mPowerComponentsBuilder;

        public BaseBuilder(BatteryConsumerData data, int consumerType) {
            this.mData = data;
            data.putLong(0, consumerType);
            this.mPowerComponentsBuilder = new PowerComponents.Builder(data);
        }

        public Key[] getKeys(int componentId) {
            return this.mData.getKeys(componentId);
        }

        public Key getKey(int componentId, int processState) {
            return this.mData.getKey(componentId, processState);
        }

        public T setConsumedPower(int componentId, double componentPower) {
            return setConsumedPower(componentId, componentPower, 1);
        }

        /* JADX WARN: Multi-variable type inference failed */
        public T setConsumedPower(int componentId, double componentPower, int powerModel) {
            this.mPowerComponentsBuilder.setConsumedPower(getKey(componentId, 0), componentPower, powerModel);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public T setConsumedPower(Key key, double componentPower, int powerModel) {
            this.mPowerComponentsBuilder.setConsumedPower(key, componentPower, powerModel);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public T setConsumedPowerForCustomComponent(int componentId, double componentPower) {
            this.mPowerComponentsBuilder.setConsumedPowerForCustomComponent(componentId, componentPower);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public T setUsageDurationMillis(int componentId, long componentUsageTimeMillis) {
            this.mPowerComponentsBuilder.setUsageDurationMillis(getKey(componentId, 0), componentUsageTimeMillis);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public T setUsageDurationMillis(Key key, long componentUsageTimeMillis) {
            this.mPowerComponentsBuilder.setUsageDurationMillis(key, componentUsageTimeMillis);
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public T setUsageDurationForCustomComponentMillis(int componentId, long componentUsageTimeMillis) {
            this.mPowerComponentsBuilder.setUsageDurationForCustomComponentMillis(componentId, componentUsageTimeMillis);
            return this;
        }

        public double getTotalPower() {
            return this.mPowerComponentsBuilder.getTotalPower();
        }
    }
}
