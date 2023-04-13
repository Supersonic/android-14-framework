package android.p008os;

import android.p008os.BatteryConsumer;
import android.p008os.BatteryStats;
import android.p008os.BatteryUsageStats;
import android.text.TextUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: android.os.UidBatteryConsumer */
/* loaded from: classes3.dex */
public final class UidBatteryConsumer extends BatteryConsumer {
    static final int COLUMN_COUNT = 5;
    static final int COLUMN_INDEX_PACKAGE_WITH_HIGHEST_DRAIN = 2;
    static final int COLUMN_INDEX_TIME_IN_BACKGROUND = 4;
    static final int COLUMN_INDEX_TIME_IN_FOREGROUND = 3;
    static final int COLUMN_INDEX_UID = 1;
    static final int CONSUMER_TYPE_UID = 1;
    public static final int STATE_BACKGROUND = 1;
    public static final int STATE_FOREGROUND = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.UidBatteryConsumer$State */
    /* loaded from: classes3.dex */
    public @interface State {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public UidBatteryConsumer(BatteryConsumer.BatteryConsumerData data) {
        super(data);
    }

    private UidBatteryConsumer(Builder builder) {
        super(builder.mData, builder.mPowerComponentsBuilder.build());
    }

    public int getUid() {
        return this.mData.getInt(1);
    }

    public String getPackageWithHighestDrain() {
        return this.mData.getString(2);
    }

    public long getTimeInStateMs(int state) {
        switch (state) {
            case 0:
                return this.mData.getInt(3);
            case 1:
                return this.mData.getInt(4);
            default:
                return 0L;
        }
    }

    @Override // android.p008os.BatteryConsumer
    public void dump(PrintWriter pw, boolean skipEmptyComponents) {
        pw.print("UID ");
        UserHandle.formatUid(pw, getUid());
        pw.print(": ");
        pw.print(BatteryStats.formatCharge(getConsumedPower()));
        if (this.mData.layout.processStateDataIncluded) {
            StringBuilder sb = new StringBuilder();
            appendProcessStateData(sb, 1, skipEmptyComponents);
            appendProcessStateData(sb, 2, skipEmptyComponents);
            appendProcessStateData(sb, 3, skipEmptyComponents);
            appendProcessStateData(sb, 4, skipEmptyComponents);
            pw.print(sb);
        }
        pw.print(" ( ");
        this.mPowerComponents.dump(pw, skipEmptyComponents);
        pw.print(" ) ");
    }

    private void appendProcessStateData(StringBuilder sb, int processState, boolean skipEmptyComponents) {
        BatteryConsumer.Dimensions dimensions = new BatteryConsumer.Dimensions(-1, processState);
        double power = this.mPowerComponents.getConsumedPower(dimensions);
        if (power == 0.0d && skipEmptyComponents) {
            return;
        }
        sb.append(" ").append(processStateToString(processState)).append(": ").append(BatteryStats.formatCharge(power));
    }

    static UidBatteryConsumer create(BatteryConsumer.BatteryConsumerData data) {
        return new UidBatteryConsumer(data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToXml(TypedXmlSerializer serializer) throws IOException {
        if (getConsumedPower() == 0.0d) {
            return;
        }
        serializer.startTag(null, "uid");
        serializer.attributeInt(null, "uid", getUid());
        String packageWithHighestDrain = getPackageWithHighestDrain();
        if (!TextUtils.isEmpty(packageWithHighestDrain)) {
            serializer.attribute(null, "highest_drain_package", packageWithHighestDrain);
        }
        serializer.attributeLong(null, "time_in_foreground", getTimeInStateMs(0));
        serializer.attributeLong(null, "time_in_background", getTimeInStateMs(1));
        this.mPowerComponents.writeToXml(serializer);
        serializer.endTag(null, "uid");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void createFromXml(TypedXmlPullParser parser, BatteryUsageStats.Builder builder) throws XmlPullParserException, IOException {
        int uid = parser.getAttributeInt(null, "uid");
        Builder consumerBuilder = builder.getOrCreateUidBatteryConsumerBuilder(uid);
        int eventType = parser.getEventType();
        if (eventType != 2 || !parser.getName().equals("uid")) {
            throw new XmlPullParserException("Invalid XML parser state");
        }
        consumerBuilder.setPackageWithHighestDrain(parser.getAttributeValue(null, "highest_drain_package"));
        consumerBuilder.setTimeInStateMs(0, parser.getAttributeLong(null, "time_in_foreground"));
        consumerBuilder.setTimeInStateMs(1, parser.getAttributeLong(null, "time_in_background"));
        while (true) {
            if ((eventType != 3 || !parser.getName().equals("uid")) && eventType != 1) {
                if (eventType == 2 && parser.getName().equals("power_components")) {
                    PowerComponents.parseXml(parser, consumerBuilder.mPowerComponentsBuilder);
                }
                eventType = parser.next();
            } else {
                return;
            }
        }
    }

    /* renamed from: android.os.UidBatteryConsumer$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder extends BatteryConsumer.BaseBuilder<Builder> {
        private static final String PACKAGE_NAME_UNINITIALIZED = "";
        private final BatteryStats.Uid mBatteryStatsUid;
        private boolean mExcludeFromBatteryUsageStats;
        private final boolean mIsVirtualUid;
        private String mPackageWithHighestDrain;
        private final int mUid;

        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ BatteryConsumer.Key getKey(int i, int i2) {
            return super.getKey(i, i2);
        }

        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ BatteryConsumer.Key[] getKeys(int i) {
            return super.getKeys(i);
        }

        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ double getTotalPower() {
            return super.getTotalPower();
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(int i, double d) {
            return super.setConsumedPower(i, d);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(int i, double d, int i2) {
            return super.setConsumedPower(i, d, i2);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(BatteryConsumer.Key key, double d, int i) {
            return super.setConsumedPower(key, d, i);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPowerForCustomComponent(int i, double d) {
            return super.setConsumedPowerForCustomComponent(i, d);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationForCustomComponentMillis(int i, long j) {
            return super.setUsageDurationForCustomComponentMillis(i, j);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationMillis(int i, long j) {
            return super.setUsageDurationMillis(i, j);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UidBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationMillis(BatteryConsumer.Key key, long j) {
            return super.setUsageDurationMillis(key, j);
        }

        public Builder(BatteryConsumer.BatteryConsumerData data, BatteryStats.Uid batteryStatsUid) {
            this(data, batteryStatsUid, batteryStatsUid.getUid());
        }

        public Builder(BatteryConsumer.BatteryConsumerData data, int uid) {
            this(data, null, uid);
        }

        private Builder(BatteryConsumer.BatteryConsumerData data, BatteryStats.Uid batteryStatsUid, int uid) {
            super(data, 1);
            this.mPackageWithHighestDrain = "";
            this.mBatteryStatsUid = batteryStatsUid;
            this.mUid = uid;
            this.mIsVirtualUid = uid == 1090;
            data.putLong(1, uid);
        }

        public BatteryStats.Uid getBatteryStatsUid() {
            BatteryStats.Uid uid = this.mBatteryStatsUid;
            if (uid == null) {
                throw new IllegalStateException("UidBatteryConsumer.Builder was initialized without a BatteryStats.Uid");
            }
            return uid;
        }

        public int getUid() {
            return this.mUid;
        }

        public boolean isVirtualUid() {
            return this.mIsVirtualUid;
        }

        public Builder setPackageWithHighestDrain(String packageName) {
            this.mPackageWithHighestDrain = TextUtils.nullIfEmpty(packageName);
            return this;
        }

        public Builder setTimeInStateMs(int state, long timeInStateMs) {
            switch (state) {
                case 0:
                    this.mData.putLong(3, timeInStateMs);
                    break;
                case 1:
                    this.mData.putLong(4, timeInStateMs);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported state: " + state);
            }
            return this;
        }

        public Builder excludeFromBatteryUsageStats() {
            this.mExcludeFromBatteryUsageStats = true;
            return this;
        }

        public Builder add(UidBatteryConsumer consumer) {
            this.mPowerComponentsBuilder.addPowerAndDuration(consumer.mPowerComponents);
            setTimeInStateMs(0, this.mData.getLong(3) + consumer.getTimeInStateMs(0));
            setTimeInStateMs(1, this.mData.getLong(4) + consumer.getTimeInStateMs(1));
            String str = this.mPackageWithHighestDrain;
            if (str == "") {
                this.mPackageWithHighestDrain = consumer.getPackageWithHighestDrain();
            } else if (!TextUtils.equals(str, consumer.getPackageWithHighestDrain())) {
                this.mPackageWithHighestDrain = null;
            }
            return this;
        }

        public boolean isExcludedFromBatteryUsageStats() {
            return this.mExcludeFromBatteryUsageStats;
        }

        public UidBatteryConsumer build() {
            if (this.mPackageWithHighestDrain == "") {
                this.mPackageWithHighestDrain = null;
            }
            if (this.mPackageWithHighestDrain != null) {
                this.mData.putString(2, this.mPackageWithHighestDrain);
            }
            return new UidBatteryConsumer(this);
        }
    }
}
