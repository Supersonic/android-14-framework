package android.p008os;

import android.content.Context;
import android.p008os.BatteryConsumer;
import android.p008os.BatteryUsageStats;
import android.util.proto.ProtoOutputStream;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: android.os.AggregateBatteryConsumer */
/* loaded from: classes3.dex */
public final class AggregateBatteryConsumer extends BatteryConsumer {
    static final int COLUMN_COUNT = 3;
    static final int COLUMN_INDEX_CONSUMED_POWER = 2;
    static final int COLUMN_INDEX_SCOPE = 1;
    static final int CONSUMER_TYPE_AGGREGATE = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AggregateBatteryConsumer(BatteryConsumer.BatteryConsumerData data) {
        super(data);
    }

    private AggregateBatteryConsumer(Builder builder) {
        super(builder.mData, builder.mPowerComponentsBuilder.build());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getScope() {
        return this.mData.getInt(1);
    }

    @Override // android.p008os.BatteryConsumer
    public void dump(PrintWriter pw, boolean skipEmptyComponents) {
        this.mPowerComponents.dump(pw, skipEmptyComponents);
    }

    @Override // android.p008os.BatteryConsumer
    public double getConsumedPower() {
        return this.mData.getDouble(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToXml(TypedXmlSerializer serializer, int scope) throws IOException {
        serializer.startTag(null, "aggregate");
        serializer.attributeInt(null, "scope", scope);
        serializer.attributeDouble(null, Context.POWER_SERVICE, getConsumedPower());
        this.mPowerComponents.writeToXml(serializer);
        serializer.endTag(null, "aggregate");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void parseXml(TypedXmlPullParser parser, BatteryUsageStats.Builder builder) throws XmlPullParserException, IOException {
        int scope = parser.getAttributeInt(null, "scope");
        Builder consumerBuilder = builder.getAggregateBatteryConsumerBuilder(scope);
        int eventType = parser.getEventType();
        if (eventType != 2 || !parser.getName().equals("aggregate")) {
            throw new XmlPullParserException("Invalid XML parser state");
        }
        consumerBuilder.setConsumedPower(parser.getAttributeDouble(null, Context.POWER_SERVICE));
        while (true) {
            if ((eventType != 3 || !parser.getName().equals("aggregate")) && eventType != 1) {
                if (eventType == 2 && parser.getName().equals("power_components")) {
                    PowerComponents.parseXml(parser, consumerBuilder.mPowerComponentsBuilder);
                }
                eventType = parser.next();
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writePowerComponentModelProto(ProtoOutputStream proto) {
        for (int i = 0; i < 18; i++) {
            int powerModel = getPowerModel(i);
            if (powerModel != 0) {
                long token = proto.start(2246267895816L);
                proto.write(1120986464257L, i);
                proto.write(1159641169922L, powerModelToProtoEnum(powerModel));
                proto.end(token);
            }
        }
    }

    /* renamed from: android.os.AggregateBatteryConsumer$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder extends BatteryConsumer.BaseBuilder<Builder> {
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

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(int i, double d) {
            return super.setConsumedPower(i, d);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(int i, double d, int i2) {
            return super.setConsumedPower(i, d, i2);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(BatteryConsumer.Key key, double d, int i) {
            return super.setConsumedPower(key, d, i);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPowerForCustomComponent(int i, double d) {
            return super.setConsumedPowerForCustomComponent(i, d);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationForCustomComponentMillis(int i, long j) {
            return super.setUsageDurationForCustomComponentMillis(i, j);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationMillis(int i, long j) {
            return super.setUsageDurationMillis(i, j);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.AggregateBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationMillis(BatteryConsumer.Key key, long j) {
            return super.setUsageDurationMillis(key, j);
        }

        public Builder(BatteryConsumer.BatteryConsumerData data, int scope) {
            super(data, 0);
            data.putInt(1, scope);
        }

        public Builder setConsumedPower(double consumedPowerMah) {
            this.mData.putDouble(2, consumedPowerMah);
            return this;
        }

        public void add(AggregateBatteryConsumer aggregateBatteryConsumer) {
            setConsumedPower(this.mData.getDouble(2) + aggregateBatteryConsumer.getConsumedPower());
            this.mPowerComponentsBuilder.addPowerAndDuration(aggregateBatteryConsumer.mPowerComponents);
        }

        public AggregateBatteryConsumer build() {
            return new AggregateBatteryConsumer(this);
        }
    }
}
