package android.p008os;

import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.nfc.cardemulation.CardEmulation;
import android.p008os.BatteryConsumer;
import android.util.proto.ProtoOutputStream;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: android.os.PowerComponents */
/* loaded from: classes3.dex */
public class PowerComponents {
    private final BatteryConsumer.BatteryConsumerData mData;

    PowerComponents(Builder builder) {
        this.mData = builder.mData;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PowerComponents(BatteryConsumer.BatteryConsumerData data) {
        this.mData = data;
    }

    public double getConsumedPower(BatteryConsumer.Dimensions dimensions) {
        if (dimensions.powerComponent != -1) {
            BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
            return batteryConsumerData.getDouble(batteryConsumerData.getKeyOrThrow(dimensions.powerComponent, dimensions.processState).mPowerColumnIndex);
        } else if (dimensions.processState != 0) {
            if (!this.mData.layout.processStateDataIncluded) {
                throw new IllegalArgumentException("No data included in BatteryUsageStats for " + dimensions);
            }
            BatteryConsumer.Key[] keys = this.mData.layout.processStateKeys[dimensions.processState];
            double totalPowerMah = 0.0d;
            for (int i = keys.length - 1; i >= 0; i--) {
                totalPowerMah += this.mData.getDouble(keys[i].mPowerColumnIndex);
            }
            return totalPowerMah;
        } else {
            BatteryConsumer.BatteryConsumerData batteryConsumerData2 = this.mData;
            return batteryConsumerData2.getDouble(batteryConsumerData2.layout.totalConsumedPowerColumnIndex);
        }
    }

    public double getConsumedPower(BatteryConsumer.Key key) {
        return this.mData.getDouble(key.mPowerColumnIndex);
    }

    public double getConsumedPowerForCustomComponent(int componentId) {
        int index = componentId - 1000;
        if (index >= 0 && index < this.mData.layout.customPowerComponentCount) {
            BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
            return batteryConsumerData.getDouble(batteryConsumerData.layout.firstCustomConsumedPowerColumn + index);
        }
        throw new IllegalArgumentException("Unsupported custom power component ID: " + componentId);
    }

    public String getCustomPowerComponentName(int componentId) {
        int index = componentId - 1000;
        if (index >= 0 && index < this.mData.layout.customPowerComponentCount) {
            try {
                return this.mData.layout.customPowerComponentNames[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Unsupported custom power component ID: " + componentId);
            }
        }
        throw new IllegalArgumentException("Unsupported custom power component ID: " + componentId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPowerModel(BatteryConsumer.Key key) {
        if (key.mPowerModelColumnIndex == -1) {
            throw new IllegalStateException("Power model IDs were not requested in the BatteryUsageStatsQuery");
        }
        return this.mData.getInt(key.mPowerModelColumnIndex);
    }

    public long getUsageDurationMillis(BatteryConsumer.Key key) {
        return this.mData.getLong(key.mDurationColumnIndex);
    }

    public long getUsageDurationForCustomComponentMillis(int componentId) {
        int index = componentId - 1000;
        if (index >= 0 && index < this.mData.layout.customPowerComponentCount) {
            BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
            return batteryConsumerData.getLong(batteryConsumerData.layout.firstCustomUsageDurationColumn + index);
        }
        throw new IllegalArgumentException("Unsupported custom power component ID: " + componentId);
    }

    public void dump(PrintWriter pw, boolean skipEmptyComponents) {
        String separator = "";
        StringBuilder sb = new StringBuilder();
        int componentId = 0;
        while (true) {
            double d = 0.0d;
            if (componentId >= 18) {
                break;
            }
            BatteryConsumer.Key[] keys = this.mData.getKeys(componentId);
            int length = keys.length;
            int i = 0;
            while (i < length) {
                BatteryConsumer.Key key = keys[i];
                double componentPower = getConsumedPower(key);
                long durationMs = getUsageDurationMillis(key);
                if (!skipEmptyComponents || componentPower != d || durationMs != 0) {
                    sb.append(separator);
                    separator = " ";
                    sb.append(key.toShortString());
                    sb.append("=");
                    sb.append(BatteryStats.formatCharge(componentPower));
                    if (durationMs != 0) {
                        sb.append(" (");
                        BatteryStats.formatTimeMsNoSpace(sb, durationMs);
                        sb.append(NavigationBarInflaterView.KEY_CODE_END);
                    }
                }
                i++;
                d = 0.0d;
            }
            componentId++;
        }
        int customComponentCount = this.mData.layout.customPowerComponentCount;
        for (int customComponentId = 1000; customComponentId < customComponentCount + 1000; customComponentId++) {
            double customComponentPower = getConsumedPowerForCustomComponent(customComponentId);
            if (skipEmptyComponents && customComponentPower == 0.0d) {
            }
            sb.append(separator);
            separator = " ";
            sb.append(getCustomPowerComponentName(customComponentId));
            sb.append("=");
            sb.append(BatteryStats.formatCharge(customComponentPower));
        }
        pw.print(sb);
    }

    boolean hasStatsProtoData() {
        return writeStatsProtoImpl(null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeStatsProto(ProtoOutputStream proto) {
        writeStatsProtoImpl(proto);
    }

    private boolean writeStatsProtoImpl(ProtoOutputStream proto) {
        int i;
        boolean interestingData = false;
        int componentId = 0;
        while (true) {
            boolean z = true;
            if (componentId < 18) {
                BatteryConsumer.Key[] keys = this.mData.getKeys(componentId);
                int length = keys.length;
                int i2 = 0;
                while (i2 < length) {
                    BatteryConsumer.Key key = keys[i2];
                    long powerDeciCoulombs = BatteryConsumer.convertMahToDeciCoulombs(getConsumedPower(key));
                    long durationMs = getUsageDurationMillis(key);
                    if (powerDeciCoulombs == 0 && durationMs == 0) {
                        i = i2;
                    } else if (proto == null) {
                        return z;
                    } else {
                        if (key.processState == 0) {
                            i = i2;
                            writePowerComponentUsage(proto, 2246267895810L, componentId, powerDeciCoulombs, durationMs);
                        } else {
                            i = i2;
                            writePowerUsageSlice(proto, componentId, powerDeciCoulombs, durationMs, key.processState);
                        }
                        interestingData = true;
                    }
                    i2 = i + 1;
                    z = true;
                }
                componentId++;
            } else {
                for (int idx = 0; idx < this.mData.layout.customPowerComponentCount; idx++) {
                    int componentId2 = idx + 1000;
                    long powerDeciCoulombs2 = BatteryConsumer.convertMahToDeciCoulombs(getConsumedPowerForCustomComponent(componentId2));
                    long durationMs2 = getUsageDurationForCustomComponentMillis(componentId2);
                    if (powerDeciCoulombs2 != 0 || durationMs2 != 0) {
                        if (proto != null) {
                            writePowerComponentUsage(proto, 2246267895810L, componentId2, powerDeciCoulombs2, durationMs2);
                            interestingData = true;
                        } else {
                            return true;
                        }
                    }
                }
                return interestingData;
            }
        }
    }

    private void writePowerUsageSlice(ProtoOutputStream proto, int componentId, long powerDeciCoulombs, long durationMs, int processState) {
        int procState;
        long slicesToken = proto.start(2246267895811L);
        writePowerComponentUsage(proto, 1146756268033L, componentId, powerDeciCoulombs, durationMs);
        switch (processState) {
            case 1:
                procState = 1;
                break;
            case 2:
                procState = 2;
                break;
            case 3:
                procState = 3;
                break;
            case 4:
                procState = 4;
                break;
            default:
                throw new IllegalArgumentException("Unknown process state: " + processState);
        }
        proto.write(1159641169922L, procState);
        proto.end(slicesToken);
    }

    private void writePowerComponentUsage(ProtoOutputStream proto, long tag, int componentId, long powerDeciCoulombs, long durationMs) {
        long token = proto.start(tag);
        proto.write(1120986464257L, componentId);
        proto.write(1112396529666L, powerDeciCoulombs);
        proto.write(1112396529667L, durationMs);
        proto.end(token);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToXml(TypedXmlSerializer serializer) throws IOException {
        BatteryConsumer.Key[] keys;
        int i;
        String str;
        String str2;
        String str3 = "power_components";
        serializer.startTag(null, "power_components");
        int componentId = 0;
        while (true) {
            double d = 0.0d;
            if (componentId >= 18) {
                break;
            }
            BatteryConsumer.Key[] keys2 = this.mData.getKeys(componentId);
            int length = keys2.length;
            int i2 = 0;
            while (i2 < length) {
                BatteryConsumer.Key key = keys2[i2];
                String str4 = str3;
                double powerMah = getConsumedPower(key);
                long durationMs = getUsageDurationMillis(key);
                if (powerMah == d && durationMs == 0) {
                    keys = keys2;
                    i = length;
                } else {
                    serializer.startTag(null, CardEmulation.EXTRA_SERVICE_COMPONENT);
                    serializer.attributeInt(null, "id", componentId);
                    if (key.processState == 0) {
                        keys = keys2;
                        i = length;
                        str = null;
                    } else {
                        keys = keys2;
                        i = length;
                        str = null;
                        serializer.attributeInt(null, "process_state", key.processState);
                    }
                    if (powerMah != 0.0d) {
                        serializer.attributeDouble(str, Context.POWER_SERVICE, powerMah);
                    }
                    if (durationMs != 0) {
                        serializer.attributeLong(str, "duration", durationMs);
                    }
                    if (!this.mData.layout.powerModelsIncluded) {
                        str2 = null;
                    } else {
                        str2 = null;
                        serializer.attributeInt(null, "model", getPowerModel(key));
                    }
                    serializer.endTag(str2, CardEmulation.EXTRA_SERVICE_COMPONENT);
                }
                i2++;
                str3 = str4;
                keys2 = keys;
                length = i;
                d = 0.0d;
            }
            componentId++;
        }
        String str5 = str3;
        int customComponentEnd = this.mData.layout.customPowerComponentCount + 1000;
        for (int componentId2 = 1000; componentId2 < customComponentEnd; componentId2++) {
            double powerMah2 = getConsumedPowerForCustomComponent(componentId2);
            long durationMs2 = getUsageDurationForCustomComponentMillis(componentId2);
            if (powerMah2 != 0.0d || durationMs2 != 0) {
                serializer.startTag(null, "custom_component");
                serializer.attributeInt(null, "id", componentId2);
                if (powerMah2 != 0.0d) {
                    serializer.attributeDouble(null, Context.POWER_SERVICE, powerMah2);
                }
                if (durationMs2 != 0) {
                    serializer.attributeLong(null, "duration", durationMs2);
                }
                serializer.endTag(null, "custom_component");
            }
        }
        serializer.endTag(null, str5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static void parseXml(TypedXmlPullParser parser, Builder builder) throws XmlPullParserException, IOException {
        char c;
        int eventType;
        char c2;
        char c3;
        int eventType2 = parser.getEventType();
        int i = 2;
        if (eventType2 != 2 || !parser.getName().equals("power_components")) {
            throw new XmlPullParserException("Invalid XML parser state");
        }
        while (true) {
            if ((eventType2 != 3 || !parser.getName().equals("power_components")) && eventType2 != 1) {
                if (eventType2 == i) {
                    String name = parser.getName();
                    switch (name.hashCode()) {
                        case -1399907075:
                            if (name.equals(CardEmulation.EXTRA_SERVICE_COMPONENT)) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case -437794385:
                            if (name.equals("custom_component")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            int componentId = -1;
                            int processState = 0;
                            double powerMah = 0.0d;
                            long durationMs = 0;
                            int model = 0;
                            int i2 = 0;
                            while (i2 < parser.getAttributeCount()) {
                                String attributeName = parser.getAttributeName(i2);
                                switch (attributeName.hashCode()) {
                                    case -1992012396:
                                        eventType = eventType2;
                                        if (attributeName.equals("duration")) {
                                            c2 = 3;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    case 3355:
                                        eventType = eventType2;
                                        if (attributeName.equals("id")) {
                                            c2 = 0;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    case 104069929:
                                        eventType = eventType2;
                                        if (attributeName.equals("model")) {
                                            c2 = 4;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    case 106858757:
                                        eventType = eventType2;
                                        if (attributeName.equals(Context.POWER_SERVICE)) {
                                            c2 = 2;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    case 1664710337:
                                        eventType = eventType2;
                                        if (attributeName.equals("process_state")) {
                                            c2 = 1;
                                            break;
                                        }
                                        c2 = 65535;
                                        break;
                                    default:
                                        eventType = eventType2;
                                        c2 = 65535;
                                        break;
                                }
                                switch (c2) {
                                    case 0:
                                        int componentId2 = parser.getAttributeInt(i2);
                                        componentId = componentId2;
                                        break;
                                    case 1:
                                        int processState2 = parser.getAttributeInt(i2);
                                        processState = processState2;
                                        break;
                                    case 2:
                                        powerMah = parser.getAttributeDouble(i2);
                                        break;
                                    case 3:
                                        durationMs = parser.getAttributeLong(i2);
                                        break;
                                    case 4:
                                        model = parser.getAttributeInt(i2);
                                        break;
                                }
                                i2++;
                                eventType2 = eventType;
                            }
                            BatteryConsumer.Key key = builder.mData.getKey(componentId, processState);
                            builder.setConsumedPower(key, powerMah, model);
                            builder.setUsageDurationMillis(key, durationMs);
                            continue;
                        case 1:
                            int componentId3 = -1;
                            double powerMah2 = 0.0d;
                            long durationMs2 = 0;
                            for (int i3 = 0; i3 < parser.getAttributeCount(); i3++) {
                                String attributeName2 = parser.getAttributeName(i3);
                                switch (attributeName2.hashCode()) {
                                    case -1992012396:
                                        if (attributeName2.equals("duration")) {
                                            c3 = 2;
                                            break;
                                        }
                                        c3 = 65535;
                                        break;
                                    case 3355:
                                        if (attributeName2.equals("id")) {
                                            c3 = 0;
                                            break;
                                        }
                                        c3 = 65535;
                                        break;
                                    case 106858757:
                                        if (attributeName2.equals(Context.POWER_SERVICE)) {
                                            c3 = 1;
                                            break;
                                        }
                                        c3 = 65535;
                                        break;
                                    default:
                                        c3 = 65535;
                                        break;
                                }
                                switch (c3) {
                                    case 0:
                                        int componentId4 = parser.getAttributeInt(i3);
                                        componentId3 = componentId4;
                                        break;
                                    case 1:
                                        powerMah2 = parser.getAttributeDouble(i3);
                                        break;
                                    case 2:
                                        durationMs2 = parser.getAttributeLong(i3);
                                        break;
                                }
                            }
                            builder.setConsumedPowerForCustomComponent(componentId3, powerMah2);
                            builder.setUsageDurationForCustomComponentMillis(componentId3, durationMs2);
                            continue;
                        default:
                            continue;
                    }
                }
                eventType2 = parser.next();
                i = 2;
            }
            return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.os.PowerComponents$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private static final byte POWER_MODEL_UNINITIALIZED = -1;
        private final BatteryConsumer.BatteryConsumerData mData;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(BatteryConsumer.BatteryConsumerData data) {
            BatteryConsumer.Key[][] keyArr;
            this.mData = data;
            for (BatteryConsumer.Key[] keys : data.layout.keys) {
                for (BatteryConsumer.Key key : keys) {
                    if (key.mPowerModelColumnIndex != -1) {
                        this.mData.putInt(key.mPowerModelColumnIndex, -1);
                    }
                }
            }
        }

        public Builder setConsumedPower(BatteryConsumer.Key key, double componentPower, int powerModel) {
            this.mData.putDouble(key.mPowerColumnIndex, componentPower);
            if (key.mPowerModelColumnIndex != -1) {
                this.mData.putInt(key.mPowerModelColumnIndex, powerModel);
            }
            return this;
        }

        public Builder setConsumedPowerForCustomComponent(int componentId, double componentPower) {
            int index = componentId - 1000;
            if (index < 0 || index >= this.mData.layout.customPowerComponentCount) {
                throw new IllegalArgumentException("Unsupported custom power component ID: " + componentId);
            }
            BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
            batteryConsumerData.putDouble(batteryConsumerData.layout.firstCustomConsumedPowerColumn + index, componentPower);
            return this;
        }

        public Builder setUsageDurationMillis(BatteryConsumer.Key key, long componentUsageDurationMillis) {
            this.mData.putLong(key.mDurationColumnIndex, componentUsageDurationMillis);
            return this;
        }

        public Builder setUsageDurationForCustomComponentMillis(int componentId, long componentUsageDurationMillis) {
            int index = componentId - 1000;
            if (index < 0 || index >= this.mData.layout.customPowerComponentCount) {
                throw new IllegalArgumentException("Unsupported custom power component ID: " + componentId);
            }
            BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
            batteryConsumerData.putLong(batteryConsumerData.layout.firstCustomUsageDurationColumn + index, componentUsageDurationMillis);
            return this;
        }

        public void addPowerAndDuration(Builder other) {
            addPowerAndDuration(other.mData);
        }

        public void addPowerAndDuration(PowerComponents other) {
            addPowerAndDuration(other.mData);
        }

        private void addPowerAndDuration(BatteryConsumer.BatteryConsumerData otherData) {
            if (this.mData.layout.customPowerComponentCount != otherData.layout.customPowerComponentCount) {
                throw new IllegalArgumentException("Number of custom power components does not match: " + otherData.layout.customPowerComponentCount + ", expected: " + this.mData.layout.customPowerComponentCount);
            }
            for (int componentId = 17; componentId >= 0; componentId--) {
                BatteryConsumer.Key[] keys = this.mData.layout.keys[componentId];
                for (BatteryConsumer.Key key : keys) {
                    BatteryConsumer.Key otherKey = null;
                    BatteryConsumer.Key[] keyArr = otherData.layout.keys[componentId];
                    int length = keyArr.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        BatteryConsumer.Key aKey = keyArr[i];
                        if (!aKey.equals(key)) {
                            i++;
                        } else {
                            otherKey = aKey;
                            break;
                        }
                    }
                    if (otherKey != null) {
                        this.mData.putDouble(key.mPowerColumnIndex, this.mData.getDouble(key.mPowerColumnIndex) + otherData.getDouble(otherKey.mPowerColumnIndex));
                        this.mData.putLong(key.mDurationColumnIndex, this.mData.getLong(key.mDurationColumnIndex) + otherData.getLong(otherKey.mDurationColumnIndex));
                        if (key.mPowerModelColumnIndex != -1) {
                            boolean undefined = false;
                            if (otherKey.mPowerModelColumnIndex == -1) {
                                undefined = true;
                            } else {
                                int powerModel = this.mData.getInt(key.mPowerModelColumnIndex);
                                int otherPowerModel = otherData.getInt(otherKey.mPowerModelColumnIndex);
                                if (powerModel == -1) {
                                    this.mData.putInt(key.mPowerModelColumnIndex, otherPowerModel);
                                } else if (powerModel != otherPowerModel && otherPowerModel != -1) {
                                    undefined = true;
                                }
                            }
                            if (undefined) {
                                this.mData.putInt(key.mPowerModelColumnIndex, 0);
                            }
                        }
                    }
                }
            }
            for (int i2 = this.mData.layout.customPowerComponentCount - 1; i2 >= 0; i2--) {
                int powerColumnIndex = this.mData.layout.firstCustomConsumedPowerColumn + i2;
                int otherPowerColumnIndex = otherData.layout.firstCustomConsumedPowerColumn + i2;
                BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
                batteryConsumerData.putDouble(powerColumnIndex, batteryConsumerData.getDouble(powerColumnIndex) + otherData.getDouble(otherPowerColumnIndex));
                int usageColumnIndex = this.mData.layout.firstCustomUsageDurationColumn + i2;
                int otherDurationColumnIndex = otherData.layout.firstCustomUsageDurationColumn + i2;
                BatteryConsumer.BatteryConsumerData batteryConsumerData2 = this.mData;
                batteryConsumerData2.putLong(usageColumnIndex, batteryConsumerData2.getLong(usageColumnIndex) + otherData.getLong(otherDurationColumnIndex));
            }
        }

        public double getTotalPower() {
            double totalPowerMah = 0.0d;
            for (int componentId = 0; componentId < 18; componentId++) {
                BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
                totalPowerMah += batteryConsumerData.getDouble(batteryConsumerData.getKeyOrThrow(componentId, 0).mPowerColumnIndex);
            }
            for (int i = 0; i < this.mData.layout.customPowerComponentCount; i++) {
                BatteryConsumer.BatteryConsumerData batteryConsumerData2 = this.mData;
                totalPowerMah += batteryConsumerData2.getDouble(batteryConsumerData2.layout.firstCustomConsumedPowerColumn + i);
            }
            return totalPowerMah;
        }

        public PowerComponents build() {
            BatteryConsumer.Key[][] keyArr;
            BatteryConsumer.BatteryConsumerData batteryConsumerData = this.mData;
            batteryConsumerData.putDouble(batteryConsumerData.layout.totalConsumedPowerColumnIndex, getTotalPower());
            for (BatteryConsumer.Key[] keys : this.mData.layout.keys) {
                for (BatteryConsumer.Key key : keys) {
                    if (key.mPowerModelColumnIndex != -1 && this.mData.getInt(key.mPowerModelColumnIndex) == -1) {
                        this.mData.putInt(key.mPowerModelColumnIndex, 0);
                    }
                }
            }
            return new PowerComponents(this);
        }
    }
}
