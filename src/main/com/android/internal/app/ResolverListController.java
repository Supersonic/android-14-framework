package com.android.internal.app;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.IPackageManager;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.Log;
import com.android.internal.app.AbstractResolverComparator;
import com.android.internal.app.ResolverActivity;
import com.android.internal.app.chooser.DisplayResolveInfo;
import com.android.internal.app.chooser.TargetInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CountDownLatch;
/* loaded from: classes4.dex */
public class ResolverListController {
    private static final boolean DEBUG = false;
    private static final String TAG = "ResolverListController";
    private boolean isComputed;
    private final Context mContext;
    private final int mLaunchedFromUid;
    private final UserHandle mQueryIntentsAsUser;
    private final String mReferrerPackage;
    private AbstractResolverComparator mResolverComparator;
    private final Intent mTargetIntent;
    private final UserHandle mUserHandle;
    private final PackageManager mpm;

    public ResolverListController(Context context, PackageManager pm, Intent targetIntent, String referrerPackage, int launchedFromUid, UserHandle userHandle, UserHandle queryIntentsAsUser) {
        this(context, pm, targetIntent, referrerPackage, launchedFromUid, userHandle, new ResolverRankerServiceResolverComparator(context, targetIntent, referrerPackage, (AbstractResolverComparator.AfterCompute) null, (ChooserActivityLogger) null, userHandle), queryIntentsAsUser);
    }

    public ResolverListController(Context context, PackageManager pm, Intent targetIntent, String referrerPackage, int launchedFromUid, UserHandle userHandle, AbstractResolverComparator resolverComparator, UserHandle queryIntentsAsUser) {
        this.isComputed = false;
        this.mContext = context;
        this.mpm = pm;
        this.mLaunchedFromUid = launchedFromUid;
        this.mTargetIntent = targetIntent;
        this.mReferrerPackage = referrerPackage;
        this.mUserHandle = userHandle;
        this.mResolverComparator = resolverComparator;
        this.mQueryIntentsAsUser = queryIntentsAsUser;
    }

    public ResolveInfo getLastChosen() throws RemoteException {
        IPackageManager packageManager = AppGlobals.getPackageManager();
        Intent intent = this.mTargetIntent;
        return packageManager.getLastChosenActivity(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 65536);
    }

    public void setLastChosen(Intent intent, IntentFilter filter, int match) throws RemoteException {
        AppGlobals.getPackageManager().setLastChosenActivity(intent, intent.resolveType(this.mContext.getContentResolver()), 65536, filter, match, intent.getComponent());
    }

    public List<ResolverActivity.ResolvedComponentInfo> getResolversForIntent(boolean shouldGetResolvedFilter, boolean shouldGetActivityMetadata, boolean shouldGetOnlyDefaultActivities, List<Intent> intents) {
        return getResolversForIntentAsUser(shouldGetResolvedFilter, shouldGetActivityMetadata, shouldGetOnlyDefaultActivities, intents, this.mQueryIntentsAsUser);
    }

    public List<ResolverActivity.ResolvedComponentInfo> getResolversForIntentAsUser(boolean shouldGetResolvedFilter, boolean shouldGetActivityMetadata, boolean shouldGetOnlyDefaultActivities, List<Intent> intents, UserHandle userHandle) {
        int baseFlags = (shouldGetActivityMetadata ? 128 : 0) | (shouldGetOnlyDefaultActivities ? 65536 : 0) | 524288 | 262144 | (shouldGetResolvedFilter ? 64 : 0) | 536870912;
        return getResolversForIntentAsUserInternal(intents, userHandle, baseFlags);
    }

    private List<ResolverActivity.ResolvedComponentInfo> getResolversForIntentAsUserInternal(List<Intent> intents, UserHandle userHandle, int baseFlags) {
        List<ResolverActivity.ResolvedComponentInfo> resolvedComponents = null;
        int N = intents.size();
        for (int i = 0; i < N; i++) {
            Intent intent = intents.get(i);
            int flags = baseFlags;
            if (intent.isWebIntent() || (intent.getFlags() & 2048) != 0) {
                flags |= 8388608;
            }
            Intent intent2 = intent.getClass() == Intent.class ? intent : new Intent(intent);
            List<ResolveInfo> infos = this.mpm.queryIntentActivitiesAsUser(intent2, flags, userHandle);
            if (infos != null) {
                if (resolvedComponents == null) {
                    resolvedComponents = new ArrayList<>();
                }
                addResolveListDedupe(resolvedComponents, intent2, infos);
            }
        }
        return resolvedComponents;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public void addResolveListDedupe(List<ResolverActivity.ResolvedComponentInfo> into, Intent intent, List<ResolveInfo> from) {
        int fromCount = from.size();
        int intoCount = into.size();
        for (int i = 0; i < fromCount; i++) {
            ResolveInfo newInfo = from.get(i);
            if (newInfo.userHandle == null) {
                Log.m104w(TAG, "Skipping ResolveInfo with no userHandle: " + newInfo);
            } else {
                boolean found = false;
                int j = 0;
                while (true) {
                    if (j >= intoCount) {
                        break;
                    }
                    ResolverActivity.ResolvedComponentInfo rci = into.get(j);
                    if (!isSameResolvedComponent(newInfo, rci)) {
                        j++;
                    } else {
                        found = true;
                        rci.add(intent, newInfo);
                        break;
                    }
                }
                if (!found) {
                    ComponentName name = new ComponentName(newInfo.activityInfo.packageName, newInfo.activityInfo.name);
                    ResolverActivity.ResolvedComponentInfo rci2 = new ResolverActivity.ResolvedComponentInfo(name, intent, newInfo);
                    rci2.setPinned(isComponentPinned(name));
                    rci2.setFixedAtTop(isFixedAtTop(name));
                    into.add(rci2);
                }
            }
        }
    }

    public boolean isComponentPinned(ComponentName name) {
        return false;
    }

    public boolean isFixedAtTop(ComponentName name) {
        return false;
    }

    public ArrayList<ResolverActivity.ResolvedComponentInfo> filterIneligibleActivities(List<ResolverActivity.ResolvedComponentInfo> inputList, boolean returnCopyOfOriginalListIfModified) {
        ArrayList<ResolverActivity.ResolvedComponentInfo> listToReturn = null;
        for (int i = inputList.size() - 1; i >= 0; i--) {
            ActivityInfo ai = inputList.get(i).getResolveInfoAt(0).activityInfo;
            int granted = ActivityManager.checkComponentPermission(ai.permission, this.mLaunchedFromUid, ai.applicationInfo.uid, ai.exported);
            if (granted != 0 || isComponentFiltered(ai.getComponentName())) {
                if (returnCopyOfOriginalListIfModified && listToReturn == null) {
                    listToReturn = new ArrayList<>(inputList);
                }
                inputList.remove(i);
            }
        }
        return listToReturn;
    }

    public ArrayList<ResolverActivity.ResolvedComponentInfo> filterLowPriority(List<ResolverActivity.ResolvedComponentInfo> inputList, boolean returnCopyOfOriginalListIfModified) {
        ArrayList<ResolverActivity.ResolvedComponentInfo> listToReturn = null;
        ResolverActivity.ResolvedComponentInfo rci0 = inputList.get(0);
        ResolveInfo r0 = rci0.getResolveInfoAt(0);
        int N = inputList.size();
        for (int i = 1; i < N; i++) {
            ResolveInfo ri = inputList.get(i).getResolveInfoAt(0);
            if (r0.priority != ri.priority || r0.isDefault != ri.isDefault) {
                while (i < N) {
                    if (returnCopyOfOriginalListIfModified && listToReturn == null) {
                        listToReturn = new ArrayList<>(inputList);
                    }
                    inputList.remove(i);
                    N--;
                }
            }
        }
        return listToReturn;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ComputeCallback implements AbstractResolverComparator.AfterCompute {
        private CountDownLatch mFinishComputeSignal;

        public ComputeCallback(CountDownLatch finishComputeSignal) {
            this.mFinishComputeSignal = finishComputeSignal;
        }

        @Override // com.android.internal.app.AbstractResolverComparator.AfterCompute
        public void afterCompute() {
            this.mFinishComputeSignal.countDown();
        }
    }

    private void compute(List<ResolverActivity.ResolvedComponentInfo> inputList) throws InterruptedException {
        if (this.mResolverComparator == null) {
            Log.m112d(TAG, "Comparator has already been destroyed; skipped.");
            return;
        }
        CountDownLatch finishComputeSignal = new CountDownLatch(1);
        ComputeCallback callback = new ComputeCallback(finishComputeSignal);
        this.mResolverComparator.setCallBack(callback);
        this.mResolverComparator.compute(inputList);
        finishComputeSignal.await();
        this.isComputed = true;
    }

    public void sort(List<ResolverActivity.ResolvedComponentInfo> inputList) {
        try {
            System.currentTimeMillis();
            if (!this.isComputed) {
                compute(inputList);
            }
            Collections.sort(inputList, this.mResolverComparator);
            System.currentTimeMillis();
        } catch (InterruptedException e) {
            Log.m110e(TAG, "Compute & Sort was interrupted: " + e);
        }
    }

    public void topK(List<ResolverActivity.ResolvedComponentInfo> inputList, int k) {
        if (inputList == null || inputList.isEmpty() || k <= 0) {
            return;
        }
        if (inputList.size() <= k) {
            sort(inputList);
            return;
        }
        try {
            System.currentTimeMillis();
            if (!this.isComputed) {
                compute(inputList);
            }
            PriorityQueue<ResolverActivity.ResolvedComponentInfo> minHeap = new PriorityQueue<>(k, new Comparator() { // from class: com.android.internal.app.ResolverListController$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$topK$0;
                    lambda$topK$0 = ResolverListController.this.lambda$topK$0((ResolverActivity.ResolvedComponentInfo) obj, (ResolverActivity.ResolvedComponentInfo) obj2);
                    return lambda$topK$0;
                }
            });
            int size = inputList.size();
            int pointer = size - 1;
            minHeap.addAll(inputList.subList(size - k, size));
            for (int i = (size - k) - 1; i >= 0; i--) {
                ResolverActivity.ResolvedComponentInfo ci = inputList.get(i);
                if ((-this.mResolverComparator.compare(ci, minHeap.peek())) > 0) {
                    inputList.set(pointer, minHeap.poll());
                    minHeap.add(ci);
                    pointer--;
                } else {
                    inputList.set(pointer, ci);
                    pointer--;
                }
            }
            while (!minHeap.isEmpty()) {
                inputList.set(pointer, minHeap.poll());
                pointer--;
            }
            System.currentTimeMillis();
        } catch (InterruptedException e) {
            Log.m110e(TAG, "Compute & greatestOf was interrupted: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$topK$0(ResolverActivity.ResolvedComponentInfo o1, ResolverActivity.ResolvedComponentInfo o2) {
        return -this.mResolverComparator.compare(o1, o2);
    }

    private static boolean isSameResolvedComponent(ResolveInfo a, ResolverActivity.ResolvedComponentInfo b) {
        ActivityInfo ai = a.activityInfo;
        return ai.packageName.equals(b.name.getPackageName()) && ai.name.equals(b.name.getClassName());
    }

    boolean isComponentFiltered(ComponentName componentName) {
        return false;
    }

    public float getScore(DisplayResolveInfo target) {
        return this.mResolverComparator.getScore(target);
    }

    public float getScore(TargetInfo targetInfo) {
        return this.mResolverComparator.getScore(targetInfo);
    }

    public void updateModel(TargetInfo targetInfo) {
        this.mResolverComparator.updateModel(targetInfo);
    }

    public void updateChooserCounts(String packageName, UserHandle user, String action) {
        this.mResolverComparator.updateChooserCounts(packageName, user, action);
    }

    public void destroy() {
        this.mResolverComparator.destroy();
    }
}
