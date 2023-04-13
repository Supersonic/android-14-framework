package android.p008os;

import android.p008os.BatteryConsumer;
import android.p008os.BatteryUsageStats;
import android.p008os.UidBatteryConsumer;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: android.os.UserBatteryConsumer */
/* loaded from: classes3.dex */
public class UserBatteryConsumer extends BatteryConsumer {
    static final int COLUMN_COUNT = 2;
    private static final int COLUMN_INDEX_USER_ID = 1;
    static final int CONSUMER_TYPE_USER = 2;

    /* JADX INFO: Access modifiers changed from: package-private */
    public UserBatteryConsumer(BatteryConsumer.BatteryConsumerData data) {
        super(data);
    }

    private UserBatteryConsumer(Builder builder) {
        super(builder.mData, builder.mPowerComponentsBuilder.build());
    }

    public int getUserId() {
        return this.mData.getInt(1);
    }

    @Override // android.p008os.BatteryConsumer
    public void dump(PrintWriter pw, boolean skipEmptyComponents) {
        double consumedPower = getConsumedPower();
        pw.print("User ");
        pw.print(getUserId());
        pw.print(": ");
        pw.print(BatteryStats.formatCharge(consumedPower));
        pw.print(" ( ");
        this.mPowerComponents.dump(pw, skipEmptyComponents);
        pw.print(" ) ");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void writeToXml(TypedXmlSerializer serializer) throws IOException {
        if (getConsumedPower() == 0.0d) {
            return;
        }
        serializer.startTag(null, "user");
        serializer.attributeInt(null, "user_id", getUserId());
        this.mPowerComponents.writeToXml(serializer);
        serializer.endTag(null, "user");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void createFromXml(TypedXmlPullParser parser, BatteryUsageStats.Builder builder) throws XmlPullParserException, IOException {
        int userId = parser.getAttributeInt(null, "user_id");
        Builder consumerBuilder = builder.getOrCreateUserBatteryConsumerBuilder(userId);
        int eventType = parser.getEventType();
        if (eventType != 2 || !parser.getName().equals("user")) {
            throw new XmlPullParserException("Invalid XML parser state");
        }
        while (true) {
            if ((eventType != 3 || !parser.getName().equals("user")) && eventType != 1) {
                if (eventType == 2 && parser.getName().equals("power_components")) {
                    PowerComponents.parseXml(parser, consumerBuilder.mPowerComponentsBuilder);
                }
                eventType = parser.next();
            } else {
                return;
            }
        }
    }

    /* renamed from: android.os.UserBatteryConsumer$Builder */
    /* loaded from: classes3.dex */
    public static final class Builder extends BatteryConsumer.BaseBuilder<Builder> {
        private List<UidBatteryConsumer.Builder> mUidBatteryConsumers;

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

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(int i, double d) {
            return super.setConsumedPower(i, d);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(int i, double d, int i2) {
            return super.setConsumedPower(i, d, i2);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPower(BatteryConsumer.Key key, double d, int i) {
            return super.setConsumedPower(key, d, i);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setConsumedPowerForCustomComponent(int i, double d) {
            return super.setConsumedPowerForCustomComponent(i, d);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationForCustomComponentMillis(int i, long j) {
            return super.setUsageDurationForCustomComponentMillis(i, j);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationMillis(int i, long j) {
            return super.setUsageDurationMillis(i, j);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [android.os.UserBatteryConsumer$Builder, android.os.BatteryConsumer$BaseBuilder] */
        @Override // android.p008os.BatteryConsumer.BaseBuilder
        public /* bridge */ /* synthetic */ Builder setUsageDurationMillis(BatteryConsumer.Key key, long j) {
            return super.setUsageDurationMillis(key, j);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(BatteryConsumer.BatteryConsumerData data, int userId) {
            super(data, 2);
            data.putLong(1, userId);
        }

        public void addUidBatteryConsumer(UidBatteryConsumer.Builder uidBatteryConsumerBuilder) {
            if (this.mUidBatteryConsumers == null) {
                this.mUidBatteryConsumers = new ArrayList();
            }
            this.mUidBatteryConsumers.add(uidBatteryConsumerBuilder);
        }

        public UserBatteryConsumer build() {
            List<UidBatteryConsumer.Builder> list = this.mUidBatteryConsumers;
            if (list != null) {
                for (int i = list.size() - 1; i >= 0; i--) {
                    UidBatteryConsumer.Builder uidBatteryConsumer = this.mUidBatteryConsumers.get(i);
                    this.mPowerComponentsBuilder.addPowerAndDuration(uidBatteryConsumer.mPowerComponentsBuilder);
                }
            }
            return new UserBatteryConsumer(this);
        }
    }
}
