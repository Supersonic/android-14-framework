package com.android.server.tare;

import android.app.tare.EconomyManager;
import android.content.ContentResolver;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.IndentingPrintWriter;
import android.util.KeyValueListParser;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes2.dex */
public abstract class EconomicPolicy {
    public final InternalResourceService mIrs;
    public static final String TAG = "TARE-" + EconomicPolicy.class.getSimpleName();
    public static final Modifier[] COST_MODIFIER_BY_INDEX = new Modifier[4];

    public static int getEventType(int i) {
        return i & (-1073741824);
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
    }

    public abstract Action getAction(int i);

    public abstract int[] getCostModifiers();

    public abstract long getInitialSatiatedConsumptionLimit();

    public abstract long getMaxSatiatedBalance(int i, String str);

    public abstract long getMaxSatiatedConsumptionLimit();

    public abstract long getMinSatiatedBalance(int i, String str);

    public abstract long getMinSatiatedConsumptionLimit();

    public abstract Reward getReward(int i);

    /* loaded from: classes2.dex */
    public static class Action {
        public final long basePrice;
        public final long costToProduce;

        /* renamed from: id */
        public final int f1153id;
        public final boolean respectsStockLimit;

        public Action(int i, long j, long j2) {
            this(i, j, j2, true);
        }

        public Action(int i, long j, long j2, boolean z) {
            this.f1153id = i;
            this.costToProduce = j;
            this.basePrice = j2;
            this.respectsStockLimit = z;
        }
    }

    /* loaded from: classes2.dex */
    public static class Reward {

        /* renamed from: id */
        public final int f1154id;
        public final long instantReward;
        public final long maxDailyReward;
        public final long ongoingRewardPerSecond;

        public Reward(int i, long j, long j2, long j3) {
            this.f1154id = i;
            this.instantReward = j;
            this.ongoingRewardPerSecond = j2;
            this.maxDailyReward = j3;
        }
    }

    /* loaded from: classes2.dex */
    public static class Cost {
        public final long costToProduce;
        public final long price;

        public Cost(long j, long j2) {
            this.costToProduce = j;
            this.price = j2;
        }
    }

    public EconomicPolicy(InternalResourceService internalResourceService) {
        this.mIrs = internalResourceService;
        for (int i : getCostModifiers()) {
            initModifier(i, internalResourceService);
        }
    }

    public void setup(DeviceConfig.Properties properties) {
        for (int i = 0; i < 4; i++) {
            Modifier modifier = COST_MODIFIER_BY_INDEX[i];
            if (modifier != null) {
                modifier.setup();
            }
        }
    }

    public void tearDown() {
        for (int i = 0; i < 4; i++) {
            Modifier modifier = COST_MODIFIER_BY_INDEX[i];
            if (modifier != null) {
                modifier.tearDown();
            }
        }
    }

    public final Cost getCostOfAction(int i, int i2, String str) {
        int[] costModifiers;
        Action action = getAction(i);
        if (action == null || this.mIrs.isVip(i2, str)) {
            return new Cost(0L, 0L);
        }
        long j = action.costToProduce;
        long j2 = action.basePrice;
        long j3 = j2;
        boolean z = false;
        for (int i3 : getCostModifiers()) {
            if (i3 == 3) {
                z = true;
            } else {
                Modifier modifier = getModifier(i3);
                j = modifier.getModifiedCostToProduce(j);
                j3 = modifier.getModifiedPrice(j3);
            }
        }
        if (z) {
            j3 = ((ProcessStateModifier) getModifier(3)).getModifiedPrice(i2, str, j, j3);
        }
        return new Cost(j, j3);
    }

    public static void initModifier(int i, InternalResourceService internalResourceService) {
        Modifier chargingModifier;
        if (i >= 0) {
            Modifier[] modifierArr = COST_MODIFIER_BY_INDEX;
            if (i < modifierArr.length) {
                if (modifierArr[i] == null) {
                    if (i == 0) {
                        chargingModifier = new ChargingModifier(internalResourceService);
                    } else if (i == 1) {
                        chargingModifier = new DeviceIdleModifier(internalResourceService);
                    } else if (i == 2) {
                        chargingModifier = new PowerSaveModeModifier(internalResourceService);
                    } else if (i == 3) {
                        chargingModifier = new ProcessStateModifier(internalResourceService);
                    } else {
                        throw new IllegalArgumentException("Invalid modifier id " + i);
                    }
                    modifierArr[i] = chargingModifier;
                    return;
                }
                return;
            }
        }
        throw new IllegalArgumentException("Invalid modifier id " + i);
    }

    public static Modifier getModifier(int i) {
        if (i >= 0) {
            Modifier[] modifierArr = COST_MODIFIER_BY_INDEX;
            if (i < modifierArr.length) {
                Modifier modifier = modifierArr[i];
                if (modifier != null) {
                    return modifier;
                }
                throw new IllegalStateException("Modifier #" + i + " was never initialized");
            }
        }
        throw new IllegalArgumentException("Invalid modifier id " + i);
    }

    public static boolean isReward(int i) {
        return getEventType(i) == Integer.MIN_VALUE;
    }

    public static String eventToString(int i) {
        int i2 = (-1073741824) & i;
        if (i2 != Integer.MIN_VALUE) {
            if (i2 != 0) {
                if (i2 == 1073741824) {
                    return actionToString(i);
                }
                return "UNKNOWN_EVENT:" + Integer.toHexString(i);
            }
            return regulationToString(i);
        }
        return rewardToString(i);
    }

    public static String actionToString(int i) {
        int i2 = 805306368 & i;
        if (i2 == 268435456) {
            switch (i) {
                case 1342177280:
                    return "ALARM_WAKEUP_EXACT_ALLOW_WHILE_IDLE";
                case 1342177281:
                    return "ALARM_WAKEUP_EXACT";
                case 1342177282:
                    return "ALARM_WAKEUP_INEXACT_ALLOW_WHILE_IDLE";
                case 1342177283:
                    return "ALARM_WAKEUP_INEXACT";
                case 1342177284:
                    return "ALARM_NONWAKEUP_EXACT_ALLOW_WHILE_IDLE";
                case 1342177285:
                    return "ALARM_NONWAKEUP_EXACT";
                case 1342177286:
                    return "ALARM_NONWAKEUP_INEXACT_ALLOW_WHILE_IDLE";
                case 1342177287:
                    return "ALARM_NONWAKEUP_INEXACT";
                case 1342177288:
                    return "ALARM_CLOCK";
            }
        } else if (i2 == 536870912) {
            switch (i) {
                case 1610612736:
                    return "JOB_MAX_START";
                case 1610612737:
                    return "JOB_MAX_RUNNING";
                case 1610612738:
                    return "JOB_HIGH_START";
                case 1610612739:
                    return "JOB_HIGH_RUNNING";
                case 1610612740:
                    return "JOB_DEFAULT_START";
                case 1610612741:
                    return "JOB_DEFAULT_RUNNING";
                case 1610612742:
                    return "JOB_LOW_START";
                case 1610612743:
                    return "JOB_LOW_RUNNING";
                case 1610612744:
                    return "JOB_MIN_START";
                case 1610612745:
                    return "JOB_MIN_RUNNING";
                case 1610612746:
                    return "JOB_TIMEOUT";
            }
        }
        return "UNKNOWN_ACTION:" + Integer.toHexString(i);
    }

    public static String regulationToString(int i) {
        switch (i) {
            case 0:
                return "BASIC_INCOME";
            case 1:
                return "BIRTHRIGHT";
            case 2:
                return "WEALTH_RECLAMATION";
            case 3:
                return "PROMOTION";
            case 4:
                return "DEMOTION";
            case 5:
                return "BG_RESTRICTED";
            case 6:
                return "BG_UNRESTRICTED";
            case 7:
            default:
                return "UNKNOWN_REGULATION:" + Integer.toHexString(i);
            case 8:
                return "FORCE_STOP";
        }
    }

    public static String rewardToString(int i) {
        if (i != -1610612736) {
            switch (i) {
                case Integer.MIN_VALUE:
                    return "REWARD_NOTIFICATION_SEEN";
                case -2147483647:
                    return "REWARD_NOTIFICATION_INTERACTION";
                case -2147483646:
                    return "REWARD_TOP_ACTIVITY";
                case -2147483645:
                    return "REWARD_WIDGET_INTERACTION";
                case -2147483644:
                    return "REWARD_OTHER_USER_INTERACTION";
                default:
                    return "UNKNOWN_REWARD:" + Integer.toHexString(i);
            }
        }
        return "REWARD_JOB_APP_INSTALL";
    }

    public long getConstantAsCake(KeyValueListParser keyValueListParser, DeviceConfig.Properties properties, String str, long j) {
        return getConstantAsCake(keyValueListParser, properties, str, j, 0L);
    }

    public long getConstantAsCake(KeyValueListParser keyValueListParser, DeviceConfig.Properties properties, String str, long j, long j2) {
        if (keyValueListParser.size() > 0) {
            return Math.max(j2, EconomyManager.parseCreditValue(keyValueListParser.getString(str, (String) null), j));
        }
        if (properties != null) {
            return Math.max(j2, EconomyManager.parseCreditValue(properties.getString(str, (String) null), j));
        }
        return Math.max(j2, j);
    }

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        public String getSettingsGlobalString(ContentResolver contentResolver, String str) {
            return Settings.Global.getString(contentResolver, str);
        }
    }

    public static void dumpActiveModifiers(IndentingPrintWriter indentingPrintWriter) {
        for (int i = 0; i < 4; i++) {
            indentingPrintWriter.print("Modifier ");
            indentingPrintWriter.println(i);
            indentingPrintWriter.increaseIndent();
            Modifier modifier = COST_MODIFIER_BY_INDEX[i];
            if (modifier != null) {
                modifier.dump(indentingPrintWriter);
            } else {
                indentingPrintWriter.println("NOT ACTIVE");
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    public static void dumpAction(IndentingPrintWriter indentingPrintWriter, Action action) {
        indentingPrintWriter.print(actionToString(action.f1153id));
        indentingPrintWriter.print(": ");
        indentingPrintWriter.print("ctp=");
        indentingPrintWriter.print(TareUtils.cakeToString(action.costToProduce));
        indentingPrintWriter.print(", basePrice=");
        indentingPrintWriter.print(TareUtils.cakeToString(action.basePrice));
        indentingPrintWriter.println();
    }

    public static void dumpReward(IndentingPrintWriter indentingPrintWriter, Reward reward) {
        indentingPrintWriter.print(rewardToString(reward.f1154id));
        indentingPrintWriter.print(": ");
        indentingPrintWriter.print("instant=");
        indentingPrintWriter.print(TareUtils.cakeToString(reward.instantReward));
        indentingPrintWriter.print(", ongoing/sec=");
        indentingPrintWriter.print(TareUtils.cakeToString(reward.ongoingRewardPerSecond));
        indentingPrintWriter.print(", maxDaily=");
        indentingPrintWriter.print(TareUtils.cakeToString(reward.maxDailyReward));
        indentingPrintWriter.println();
    }
}
