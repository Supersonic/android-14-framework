package com.android.internal.telephony.metrics;

import com.android.internal.telephony.metrics.VoiceCallRatTracker;
import com.android.internal.telephony.nano.PersistAtomsProto$VoiceCallRatUsage;
import com.android.telephony.Rlog;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
/* loaded from: classes.dex */
public class VoiceCallRatTracker {
    private static final String TAG = "VoiceCallRatTracker";
    private Key mLastKey;
    private long mLastKeyTimestampMillis;
    private final Map<Key, Value> mRatUsageMap = new HashMap();

    /* JADX INFO: Access modifiers changed from: package-private */
    public VoiceCallRatTracker() {
        clear();
    }

    public static VoiceCallRatTracker fromProto(PersistAtomsProto$VoiceCallRatUsage[] persistAtomsProto$VoiceCallRatUsageArr) {
        final VoiceCallRatTracker voiceCallRatTracker = new VoiceCallRatTracker();
        if (persistAtomsProto$VoiceCallRatUsageArr == null) {
            Rlog.e(TAG, "fromProto: usages=null");
        } else {
            Arrays.stream(persistAtomsProto$VoiceCallRatUsageArr).forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.VoiceCallRatTracker$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VoiceCallRatTracker.this.addProto((PersistAtomsProto$VoiceCallRatUsage) obj);
                }
            });
        }
        return voiceCallRatTracker;
    }

    public PersistAtomsProto$VoiceCallRatUsage[] toProto() {
        return (PersistAtomsProto$VoiceCallRatUsage[]) this.mRatUsageMap.entrySet().stream().map(new Function() { // from class: com.android.internal.telephony.metrics.VoiceCallRatTracker$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                PersistAtomsProto$VoiceCallRatUsage entryToProto;
                entryToProto = VoiceCallRatTracker.entryToProto((Map.Entry) obj);
                return entryToProto;
            }
        }).toArray(new IntFunction() { // from class: com.android.internal.telephony.metrics.VoiceCallRatTracker$$ExternalSyntheticLambda1
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                PersistAtomsProto$VoiceCallRatUsage[] lambda$toProto$1;
                lambda$toProto$1 = VoiceCallRatTracker.lambda$toProto$1(i);
                return lambda$toProto$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ PersistAtomsProto$VoiceCallRatUsage[] lambda$toProto$1(int i) {
        return new PersistAtomsProto$VoiceCallRatUsage[i];
    }

    public void clear() {
        this.mRatUsageMap.clear();
        this.mLastKey = null;
        this.mLastKeyTimestampMillis = 0L;
    }

    public void add(int i, int i2, long j, Set<Integer> set) {
        if (this.mLastKey != null) {
            long j2 = j - this.mLastKeyTimestampMillis;
            if (j2 < 0) {
                Rlog.e(TAG, "add: durationMillis<0");
                j2 = 0;
            }
            addToKey(this.mLastKey, j2, set);
        }
        Key key = new Key(i, i2);
        addToKey(key, 0L, set);
        this.mLastKey = key;
        this.mLastKeyTimestampMillis = j;
    }

    public void conclude(long j) {
        if (this.mLastKey != null) {
            long j2 = j - this.mLastKeyTimestampMillis;
            if (j2 < 0) {
                Rlog.e(TAG, "conclude: durationMillis<0");
                j2 = 0;
            }
            Value value = this.mRatUsageMap.get(this.mLastKey);
            if (value == null) {
                Rlog.e(TAG, "conclude: value=null && mLastKey!=null");
            } else {
                value.durationMillis += j2;
            }
            this.mRatUsageMap.values().stream().forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.VoiceCallRatTracker$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((VoiceCallRatTracker.Value) obj).endSession();
                }
            });
            return;
        }
        Rlog.e(TAG, "conclude: mLastKey=null");
    }

    public VoiceCallRatTracker mergeWith(VoiceCallRatTracker voiceCallRatTracker) {
        if (voiceCallRatTracker == null) {
            Rlog.e(TAG, "mergeWith: attempting to merge with null", new Throwable());
        } else {
            voiceCallRatTracker.mRatUsageMap.entrySet().stream().forEach(new Consumer() { // from class: com.android.internal.telephony.metrics.VoiceCallRatTracker$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VoiceCallRatTracker.this.lambda$mergeWith$2((Map.Entry) obj);
                }
            });
        }
        return this;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$mergeWith$2(Map.Entry entry) {
        this.mRatUsageMap.merge((Key) entry.getKey(), (Value) entry.getValue(), new BiFunction() { // from class: com.android.internal.telephony.metrics.VoiceCallRatTracker$$ExternalSyntheticLambda5
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return VoiceCallRatTracker.Value.mergeInPlace((VoiceCallRatTracker.Value) obj, (VoiceCallRatTracker.Value) obj2);
            }
        });
    }

    private void addToKey(Key key, long j, Set<Integer> set) {
        Value value = this.mRatUsageMap.get(key);
        if (value == null) {
            this.mRatUsageMap.put(key, new Value(j, set));
        } else {
            value.add(j, set);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addProto(PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
        this.mRatUsageMap.put(Key.fromProto(persistAtomsProto$VoiceCallRatUsage), Value.fromProto(persistAtomsProto$VoiceCallRatUsage));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PersistAtomsProto$VoiceCallRatUsage entryToProto(Map.Entry<Key, Value> entry) {
        Key key = entry.getKey();
        Value value = entry.getValue();
        PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage = new PersistAtomsProto$VoiceCallRatUsage();
        persistAtomsProto$VoiceCallRatUsage.carrierId = key.carrierId;
        persistAtomsProto$VoiceCallRatUsage.rat = key.rat;
        if (value.mConnectionIds != null) {
            Rlog.e(TAG, "call not concluded when converting to proto");
        }
        persistAtomsProto$VoiceCallRatUsage.totalDurationMillis = value.durationMillis;
        persistAtomsProto$VoiceCallRatUsage.callCount = value.callCount;
        return persistAtomsProto$VoiceCallRatUsage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Key {
        public final int carrierId;
        public final int rat;

        Key(int i, int i2) {
            this.carrierId = i;
            this.rat = i2;
        }

        static Key fromProto(PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
            return new Key(persistAtomsProto$VoiceCallRatUsage.carrierId, persistAtomsProto$VoiceCallRatUsage.rat);
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.carrierId), Integer.valueOf(this.rat));
        }

        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            Key key = (Key) obj;
            return key.carrierId == this.carrierId && key.rat == this.rat;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Value {
        public long callCount;
        public long durationMillis;
        private Set<Integer> mConnectionIds;

        Value(long j, Set<Integer> set) {
            this.durationMillis = j;
            this.mConnectionIds = set;
            this.callCount = 0L;
        }

        private Value(long j, long j2) {
            this.durationMillis = j;
            this.mConnectionIds = null;
            this.callCount = j2;
        }

        void add(long j, Set<Integer> set) {
            this.durationMillis += j;
            Set<Integer> set2 = this.mConnectionIds;
            if (set2 != null) {
                set2.addAll(set);
            } else {
                Rlog.e(VoiceCallRatTracker.TAG, "Value: trying to add to concluded call");
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void endSession() {
            if (this.mConnectionIds != null) {
                if (this.callCount != 0) {
                    Rlog.e(VoiceCallRatTracker.TAG, "Value: mConnectionIds!=null && callCount!=0");
                }
                this.callCount = this.mConnectionIds.size();
                this.mConnectionIds = null;
            }
        }

        static Value fromProto(PersistAtomsProto$VoiceCallRatUsage persistAtomsProto$VoiceCallRatUsage) {
            return new Value(persistAtomsProto$VoiceCallRatUsage.totalDurationMillis, persistAtomsProto$VoiceCallRatUsage.callCount);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Value mergeInPlace(Value value, Value value2) {
            if (value2.mConnectionIds != null || value.mConnectionIds != null) {
                Rlog.e(VoiceCallRatTracker.TAG, "Value: call not concluded yet when merging");
            }
            value.durationMillis += value2.durationMillis;
            value.callCount += value2.callCount;
            return value;
        }
    }
}
