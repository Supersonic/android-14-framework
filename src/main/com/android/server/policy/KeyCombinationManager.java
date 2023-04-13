package com.android.server.policy;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseLongArray;
import android.view.KeyEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ToBooleanFunction;
import com.android.server.policy.KeyCombinationManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class KeyCombinationManager {
    public final Handler mHandler;
    @GuardedBy({"mLock"})
    public TwoKeysCombinationRule mTriggeredRule;
    @GuardedBy({"mLock"})
    public final SparseLongArray mDownTimes = new SparseLongArray(2);
    public final ArrayList<TwoKeysCombinationRule> mRules = new ArrayList<>();
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final ArrayList<TwoKeysCombinationRule> mActiveRules = new ArrayList<>();

    /* loaded from: classes2.dex */
    public static abstract class TwoKeysCombinationRule {
        public int mKeyCode1;
        public int mKeyCode2;

        public abstract void cancel();

        public abstract void execute();

        public long getKeyInterceptDelayMs() {
            return 150L;
        }

        public boolean preCondition() {
            return true;
        }

        public TwoKeysCombinationRule(int i, int i2) {
            this.mKeyCode1 = i;
            this.mKeyCode2 = i2;
        }

        public boolean shouldInterceptKey(int i) {
            return preCondition() && (i == this.mKeyCode1 || i == this.mKeyCode2);
        }

        public boolean shouldInterceptKeys(SparseLongArray sparseLongArray) {
            long uptimeMillis = SystemClock.uptimeMillis();
            return sparseLongArray.get(this.mKeyCode1) > 0 && sparseLongArray.get(this.mKeyCode2) > 0 && uptimeMillis <= sparseLongArray.get(this.mKeyCode1) + 150 && uptimeMillis <= sparseLongArray.get(this.mKeyCode2) + 150;
        }

        public String toString() {
            return KeyEvent.keyCodeToString(this.mKeyCode1) + " + " + KeyEvent.keyCodeToString(this.mKeyCode2);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof TwoKeysCombinationRule) {
                TwoKeysCombinationRule twoKeysCombinationRule = (TwoKeysCombinationRule) obj;
                int i = this.mKeyCode1;
                int i2 = twoKeysCombinationRule.mKeyCode1;
                if (i == i2 && this.mKeyCode2 == twoKeysCombinationRule.mKeyCode2) {
                    return true;
                }
                return i == twoKeysCombinationRule.mKeyCode2 && this.mKeyCode2 == i2;
            }
            return false;
        }

        public int hashCode() {
            return (this.mKeyCode1 * 31) + this.mKeyCode2;
        }
    }

    public KeyCombinationManager(Handler handler) {
        this.mHandler = handler;
    }

    public void addRule(TwoKeysCombinationRule twoKeysCombinationRule) {
        if (this.mRules.contains(twoKeysCombinationRule)) {
            throw new IllegalArgumentException("Rule : " + twoKeysCombinationRule + " already exists.");
        }
        this.mRules.add(twoKeysCombinationRule);
    }

    public boolean interceptKey(KeyEvent keyEvent, boolean z) {
        boolean interceptKeyLocked;
        synchronized (this.mLock) {
            interceptKeyLocked = interceptKeyLocked(keyEvent, z);
        }
        return interceptKeyLocked;
    }

    public final boolean interceptKeyLocked(KeyEvent keyEvent, boolean z) {
        boolean z2 = keyEvent.getAction() == 0;
        final int keyCode = keyEvent.getKeyCode();
        int size = this.mActiveRules.size();
        long eventTime = keyEvent.getEventTime();
        if (z && z2) {
            if (this.mDownTimes.size() > 0) {
                if (size > 0 && eventTime > this.mDownTimes.valueAt(0) + 150) {
                    forAllRules(this.mActiveRules, new Consumer() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((KeyCombinationManager.TwoKeysCombinationRule) obj).cancel();
                        }
                    });
                    this.mActiveRules.clear();
                    return false;
                } else if (size == 0) {
                    return false;
                }
            }
            if (this.mDownTimes.get(keyCode) != 0) {
                return false;
            }
            this.mDownTimes.put(keyCode, eventTime);
            if (this.mDownTimes.size() == 1) {
                this.mTriggeredRule = null;
                forAllRules(this.mRules, new Consumer() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        KeyCombinationManager.this.lambda$interceptKeyLocked$1(keyCode, (KeyCombinationManager.TwoKeysCombinationRule) obj);
                    }
                });
            } else if (this.mTriggeredRule != null) {
                return true;
            } else {
                forAllActiveRules(new ToBooleanFunction() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda3
                    public final boolean apply(Object obj) {
                        boolean lambda$interceptKeyLocked$2;
                        lambda$interceptKeyLocked$2 = KeyCombinationManager.this.lambda$interceptKeyLocked$2((KeyCombinationManager.TwoKeysCombinationRule) obj);
                        return lambda$interceptKeyLocked$2;
                    }
                });
                this.mActiveRules.clear();
                TwoKeysCombinationRule twoKeysCombinationRule = this.mTriggeredRule;
                if (twoKeysCombinationRule != null) {
                    this.mActiveRules.add(twoKeysCombinationRule);
                    return true;
                }
            }
        } else {
            this.mDownTimes.delete(keyCode);
            for (int i = size - 1; i >= 0; i--) {
                final TwoKeysCombinationRule twoKeysCombinationRule2 = this.mActiveRules.get(i);
                if (twoKeysCombinationRule2.shouldInterceptKey(keyCode)) {
                    this.mHandler.post(new Runnable() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            KeyCombinationManager.TwoKeysCombinationRule.this.cancel();
                        }
                    });
                    this.mActiveRules.remove(i);
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$interceptKeyLocked$1(int i, TwoKeysCombinationRule twoKeysCombinationRule) {
        if (twoKeysCombinationRule.shouldInterceptKey(i)) {
            this.mActiveRules.add(twoKeysCombinationRule);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$interceptKeyLocked$2(final TwoKeysCombinationRule twoKeysCombinationRule) {
        if (twoKeysCombinationRule.shouldInterceptKeys(this.mDownTimes)) {
            Log.v("KeyCombinationManager", "Performing combination rule : " + twoKeysCombinationRule);
            this.mHandler.post(new Runnable() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    KeyCombinationManager.TwoKeysCombinationRule.this.execute();
                }
            });
            this.mTriggeredRule = twoKeysCombinationRule;
            return true;
        }
        return false;
    }

    public long getKeyInterceptTimeout(int i) {
        synchronized (this.mLock) {
            long j = 0;
            if (this.mDownTimes.get(i) == 0) {
                return 0L;
            }
            Iterator<TwoKeysCombinationRule> it = this.mActiveRules.iterator();
            while (it.hasNext()) {
                TwoKeysCombinationRule next = it.next();
                if (next.shouldInterceptKey(i)) {
                    j = Math.max(j, next.getKeyInterceptDelayMs());
                }
            }
            return this.mDownTimes.get(i) + Math.min(j, 150L);
        }
    }

    public boolean isKeyConsumed(KeyEvent keyEvent) {
        synchronized (this.mLock) {
            boolean z = false;
            if ((keyEvent.getFlags() & 1024) != 0) {
                return false;
            }
            TwoKeysCombinationRule twoKeysCombinationRule = this.mTriggeredRule;
            if (twoKeysCombinationRule != null && twoKeysCombinationRule.shouldInterceptKey(keyEvent.getKeyCode())) {
                z = true;
            }
            return z;
        }
    }

    public boolean isPowerKeyIntercepted() {
        synchronized (this.mLock) {
            boolean z = false;
            if (forAllActiveRules(new ToBooleanFunction() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda0
                public final boolean apply(Object obj) {
                    boolean shouldInterceptKey;
                    shouldInterceptKey = ((KeyCombinationManager.TwoKeysCombinationRule) obj).shouldInterceptKey(26);
                    return shouldInterceptKey;
                }
            })) {
                if (this.mDownTimes.size() > 1 || this.mDownTimes.get(26) == 0) {
                    z = true;
                }
                return z;
            }
            return false;
        }
    }

    public final void forAllRules(ArrayList<TwoKeysCombinationRule> arrayList, Consumer<TwoKeysCombinationRule> consumer) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            consumer.accept(arrayList.get(i));
        }
    }

    public final boolean forAllActiveRules(ToBooleanFunction<TwoKeysCombinationRule> toBooleanFunction) {
        int size = this.mActiveRules.size();
        for (int i = 0; i < size; i++) {
            if (toBooleanFunction.apply(this.mActiveRules.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void dump(final String str, final PrintWriter printWriter) {
        printWriter.println(str + "KeyCombination rules:");
        forAllRules(this.mRules, new Consumer() { // from class: com.android.server.policy.KeyCombinationManager$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KeyCombinationManager.lambda$dump$4(printWriter, str, (KeyCombinationManager.TwoKeysCombinationRule) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$dump$4(PrintWriter printWriter, String str, TwoKeysCombinationRule twoKeysCombinationRule) {
        printWriter.println(str + "  " + twoKeysCombinationRule);
    }
}
