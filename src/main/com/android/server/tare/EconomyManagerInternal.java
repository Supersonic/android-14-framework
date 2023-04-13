package com.android.server.tare;

import com.android.server.tare.EconomyManagerInternal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;
/* loaded from: classes2.dex */
public interface EconomyManagerInternal {

    /* loaded from: classes2.dex */
    public interface AffordabilityChangeListener {
        void onAffordabilityChanged(int i, String str, ActionBill actionBill, boolean z);
    }

    /* loaded from: classes2.dex */
    public interface TareStateChangeListener {
        void onTareEnabledModeChanged(int i);
    }

    boolean canPayFor(int i, String str, ActionBill actionBill);

    int getEnabledMode(int i);

    long getMaxDurationMs(int i, String str, ActionBill actionBill);

    void noteInstantaneousEvent(int i, String str, int i2, String str2);

    void noteOngoingEventStarted(int i, String str, int i2, String str2);

    void noteOngoingEventStopped(int i, String str, int i2, String str2);

    void registerAffordabilityChangeListener(int i, String str, AffordabilityChangeListener affordabilityChangeListener, ActionBill actionBill);

    void registerTareStateChangeListener(TareStateChangeListener tareStateChangeListener, int i);

    void unregisterAffordabilityChangeListener(int i, String str, AffordabilityChangeListener affordabilityChangeListener, ActionBill actionBill);

    /* loaded from: classes2.dex */
    public static final class AnticipatedAction {
        public final int actionId;
        public final int mHashCode;
        public final int numInstantaneousCalls;
        public final long ongoingDurationMs;

        public AnticipatedAction(int i, int i2, long j) {
            this.actionId = i;
            this.numInstantaneousCalls = i2;
            this.ongoingDurationMs = j;
            this.mHashCode = ((((0 + i) * 31) + i2) * 31) + ((int) ((j >>> 32) ^ j));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || AnticipatedAction.class != obj.getClass()) {
                return false;
            }
            AnticipatedAction anticipatedAction = (AnticipatedAction) obj;
            return this.actionId == anticipatedAction.actionId && this.numInstantaneousCalls == anticipatedAction.numInstantaneousCalls && this.ongoingDurationMs == anticipatedAction.ongoingDurationMs;
        }

        public int hashCode() {
            return this.mHashCode;
        }
    }

    /* loaded from: classes2.dex */
    public static final class ActionBill {
        public static final Comparator<AnticipatedAction> sAnticipatedActionComparator = Comparator.comparingInt(new ToIntFunction() { // from class: com.android.server.tare.EconomyManagerInternal$ActionBill$$ExternalSyntheticLambda0
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int i;
                i = ((EconomyManagerInternal.AnticipatedAction) obj).actionId;
                return i;
            }
        });
        public final List<AnticipatedAction> mAnticipatedActions;
        public final int mHashCode;

        public ActionBill(List<AnticipatedAction> list) {
            ArrayList arrayList = new ArrayList(list);
            arrayList.sort(sAnticipatedActionComparator);
            this.mAnticipatedActions = Collections.unmodifiableList(arrayList);
            int i = 0;
            for (int i2 = 0; i2 < this.mAnticipatedActions.size(); i2++) {
                i = (i * 31) + this.mAnticipatedActions.get(i2).hashCode();
            }
            this.mHashCode = i;
        }

        public List<AnticipatedAction> getAnticipatedActions() {
            return this.mAnticipatedActions;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || ActionBill.class != obj.getClass()) {
                return false;
            }
            return this.mAnticipatedActions.equals(((ActionBill) obj).mAnticipatedActions);
        }

        public int hashCode() {
            return this.mHashCode;
        }
    }
}
