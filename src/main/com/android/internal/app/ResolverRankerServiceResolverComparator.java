package com.android.internal.app;

import android.app.usage.UsageStats;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.metrics.LogMaker;
import android.p008os.IBinder;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.service.resolver.IResolverRankerResult;
import android.service.resolver.IResolverRankerService;
import android.service.resolver.ResolverRankerService;
import android.service.resolver.ResolverTarget;
import android.util.Log;
import com.android.internal.app.AbstractResolverComparator;
import com.android.internal.app.ResolverActivity;
import com.android.internal.app.ResolverRankerServiceResolverComparator;
import com.android.internal.app.chooser.TargetInfo;
import com.android.internal.logging.MetricsLogger;
import com.google.android.collect.Lists;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes4.dex */
public class ResolverRankerServiceResolverComparator extends AbstractResolverComparator {
    private static final int CONNECTION_COST_TIMEOUT_MILLIS = 200;
    private static final boolean DEBUG = false;
    private static final float RECENCY_MULTIPLIER = 2.0f;
    private static final long RECENCY_TIME_PERIOD = 43200000;
    private static final String TAG = "RRSResolverComparator";
    private static final long USAGE_STATS_PERIOD = 604800000;
    private String mAction;
    private final Collator mCollator;
    private ResolverRankerServiceComparatorModel mComparatorModel;
    private CountDownLatch mConnectSignal;
    private ResolverRankerServiceConnection mConnection;
    private Context mContext;
    private final long mCurrentTime;
    private final Object mLock;
    private IResolverRankerService mRanker;
    private ComponentName mRankerServiceName;
    private final String mReferrerPackage;
    private ComponentName mResolvedRankerName;
    private final long mSinceTime;
    private final Map<UserHandle, Map<String, UsageStats>> mStatsPerUser;
    private ArrayList<ResolverTarget> mTargets;
    private final Map<UserHandle, LinkedHashMap<ComponentName, ResolverTarget>> mTargetsDictPerUser;

    public ResolverRankerServiceResolverComparator(Context launchedFromContext, Intent intent, String referrerPackage, AbstractResolverComparator.AfterCompute afterCompute, ChooserActivityLogger chooserActivityLogger, UserHandle targetUserSpace) {
        this(launchedFromContext, intent, referrerPackage, afterCompute, chooserActivityLogger, Lists.newArrayList(targetUserSpace));
    }

    public ResolverRankerServiceResolverComparator(Context launchedFromContext, Intent intent, String referrerPackage, AbstractResolverComparator.AfterCompute afterCompute, ChooserActivityLogger chooserActivityLogger, List<UserHandle> targetUserSpaceList) {
        super(launchedFromContext, intent, targetUserSpaceList);
        this.mLock = new Object();
        this.mCollator = Collator.getInstance(launchedFromContext.getResources().getConfiguration().locale);
        this.mReferrerPackage = referrerPackage;
        this.mContext = launchedFromContext;
        long currentTimeMillis = System.currentTimeMillis();
        this.mCurrentTime = currentTimeMillis;
        this.mSinceTime = currentTimeMillis - 604800000;
        this.mStatsPerUser = new HashMap();
        this.mTargetsDictPerUser = new HashMap();
        for (UserHandle user : targetUserSpaceList) {
            this.mStatsPerUser.put(user, this.mUsmMap.get(user).queryAndAggregateUsageStats(this.mSinceTime, this.mCurrentTime));
            this.mTargetsDictPerUser.put(user, new LinkedHashMap<>());
        }
        this.mAction = intent.getAction();
        this.mRankerServiceName = new ComponentName(this.mContext, getClass());
        setCallBack(afterCompute);
        setChooserActivityLogger(chooserActivityLogger);
        this.mComparatorModel = buildUpdatedModel();
    }

    @Override // com.android.internal.app.AbstractResolverComparator
    public void handleResultMessage(Message msg) {
        if (msg.what != 0) {
            return;
        }
        if (msg.obj == null) {
            Log.m110e(TAG, "Receiving null prediction results.");
            return;
        }
        List<ResolverTarget> receivedTargets = (List) msg.obj;
        if (receivedTargets != null && this.mTargets != null && receivedTargets.size() == this.mTargets.size()) {
            int size = this.mTargets.size();
            boolean isUpdated = false;
            for (int i = 0; i < size; i++) {
                float predictedProb = receivedTargets.get(i).getSelectProbability();
                if (predictedProb != this.mTargets.get(i).getSelectProbability()) {
                    this.mTargets.get(i).setSelectProbability(predictedProb);
                    isUpdated = true;
                }
            }
            if (isUpdated) {
                this.mRankerServiceName = this.mResolvedRankerName;
                this.mComparatorModel = buildUpdatedModel();
                return;
            }
            return;
        }
        Log.m110e(TAG, "Sizes of sent and received ResolverTargets diff.");
    }

    @Override // com.android.internal.app.AbstractResolverComparator
    public void doCompute(List<ResolverActivity.ResolvedComponentInfo> targets) {
        Iterator<ResolverActivity.ResolvedComponentInfo> it;
        long recentSinceTime = this.mCurrentTime - 43200000;
        Iterator<ResolverActivity.ResolvedComponentInfo> it2 = targets.iterator();
        float mostRecencyScore = 1.0f;
        float mostTimeSpentScore = 1.0f;
        float mostLaunchScore = 1.0f;
        float mostChooserScore = 1.0f;
        while (it2.hasNext()) {
            ResolverActivity.ResolvedComponentInfo target = it2.next();
            ResolverTarget resolverTarget = new ResolverTarget();
            LinkedHashMap<ComponentName, ResolverTarget> targetsDict = this.mTargetsDictPerUser.get(target.getResolveInfoAt(0).userHandle);
            Map<String, UsageStats> stats = this.mStatsPerUser.get(target.getResolveInfoAt(0).userHandle);
            if (targetsDict == null || stats == null) {
                it = it2;
            } else {
                targetsDict.put(target.name, resolverTarget);
                UsageStats pkStats = stats.get(target.name.getPackageName());
                if (pkStats != null) {
                    if (target.name.getPackageName().equals(this.mReferrerPackage)) {
                        it = it2;
                    } else if (isPersistentProcess(target)) {
                        it = it2;
                    } else {
                        it = it2;
                        float recencyScore = (float) Math.max(pkStats.getLastTimeUsed() - recentSinceTime, 0L);
                        resolverTarget.setRecencyScore(recencyScore);
                        if (recencyScore > mostRecencyScore) {
                            mostRecencyScore = recencyScore;
                        }
                    }
                    float timeSpentScore = (float) pkStats.getTotalTimeInForeground();
                    resolverTarget.setTimeSpentScore(timeSpentScore);
                    if (timeSpentScore > mostTimeSpentScore) {
                        mostTimeSpentScore = timeSpentScore;
                    }
                    float launchScore = pkStats.mLaunchCount;
                    resolverTarget.setLaunchScore(launchScore);
                    if (launchScore > mostLaunchScore) {
                        mostLaunchScore = launchScore;
                    }
                    float chooserScore = 0.0f;
                    if (pkStats.mChooserCounts != null && this.mAction != null) {
                        if (pkStats.mChooserCounts.get(this.mAction) != null) {
                            chooserScore = pkStats.mChooserCounts.get(this.mAction).getOrDefault(this.mContentType, 0).intValue();
                            if (this.mAnnotations != null) {
                                int size = this.mAnnotations.length;
                                int i = 0;
                                while (i < size) {
                                    chooserScore += pkStats.mChooserCounts.get(this.mAction).getOrDefault(this.mAnnotations[i], 0).intValue();
                                    i++;
                                    size = size;
                                    timeSpentScore = timeSpentScore;
                                }
                            }
                        }
                    }
                    resolverTarget.setChooserScore(chooserScore);
                    if (chooserScore > mostChooserScore) {
                        mostChooserScore = chooserScore;
                    }
                } else {
                    it = it2;
                }
            }
            it2 = it;
        }
        this.mTargets = new ArrayList<>();
        for (UserHandle u : this.mTargetsDictPerUser.keySet()) {
            this.mTargets.addAll(this.mTargetsDictPerUser.get(u).values());
        }
        Iterator<ResolverTarget> it3 = this.mTargets.iterator();
        while (it3.hasNext()) {
            ResolverTarget target2 = it3.next();
            float recency = target2.getRecencyScore() / mostRecencyScore;
            setFeatures(target2, recency * recency * RECENCY_MULTIPLIER, target2.getLaunchScore() / mostLaunchScore, target2.getTimeSpentScore() / mostTimeSpentScore, target2.getChooserScore() / mostChooserScore);
            addDefaultSelectProbability(target2);
        }
        predictSelectProbabilities(this.mTargets);
        this.mComparatorModel = buildUpdatedModel();
    }

    @Override // com.android.internal.app.AbstractResolverComparator
    public int compare(ResolveInfo lhs, ResolveInfo rhs) {
        return this.mComparatorModel.getComparator().compare(lhs, rhs);
    }

    @Override // com.android.internal.app.AbstractResolverComparator
    public float getScore(TargetInfo targetInfo) {
        return this.mComparatorModel.getScore(targetInfo);
    }

    @Override // com.android.internal.app.AbstractResolverComparator
    public void updateModel(TargetInfo targetInfo) {
        synchronized (this.mLock) {
            this.mComparatorModel.notifyOnTargetSelected(targetInfo);
        }
    }

    @Override // com.android.internal.app.AbstractResolverComparator
    public void destroy() {
        this.mHandler.removeMessages(0);
        this.mHandler.removeMessages(1);
        ResolverRankerServiceConnection resolverRankerServiceConnection = this.mConnection;
        if (resolverRankerServiceConnection != null) {
            this.mContext.unbindService(resolverRankerServiceConnection);
            this.mConnection.destroy();
        }
        afterCompute();
    }

    private void initRanker(Context context) {
        synchronized (this.mLock) {
            if (this.mConnection == null || this.mRanker == null) {
                Intent intent = resolveRankerService();
                if (intent == null) {
                    return;
                }
                CountDownLatch countDownLatch = new CountDownLatch(1);
                this.mConnectSignal = countDownLatch;
                ResolverRankerServiceConnection resolverRankerServiceConnection = new ResolverRankerServiceConnection(countDownLatch);
                this.mConnection = resolverRankerServiceConnection;
                context.bindServiceAsUser(intent, resolverRankerServiceConnection, 1, UserHandle.SYSTEM);
            }
        }
    }

    private Intent resolveRankerService() {
        Intent intent = new Intent(ResolverRankerService.SERVICE_INTERFACE);
        List<ResolveInfo> resolveInfos = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (resolveInfo != null && resolveInfo.serviceInfo != null && resolveInfo.serviceInfo.applicationInfo != null) {
                ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.applicationInfo.packageName, resolveInfo.serviceInfo.name);
                try {
                    String perm = this.mContext.getPackageManager().getServiceInfo(componentName, 0).permission;
                    if (!"android.permission.BIND_RESOLVER_RANKER_SERVICE".equals(perm)) {
                        Log.m104w(TAG, "ResolverRankerService " + componentName + " does not require permission android.permission.BIND_RESOLVER_RANKER_SERVICE - this service will not be queried for ResolverRankerServiceResolverComparator. add android:permission=\"android.permission.BIND_RESOLVER_RANKER_SERVICE\" to the <service> tag for " + componentName + " in the manifest.");
                    } else if (this.mContext.getPackageManager().checkPermission("android.permission.PROVIDE_RESOLVER_RANKER_SERVICE", resolveInfo.serviceInfo.packageName) != 0) {
                        Log.m104w(TAG, "ResolverRankerService " + componentName + " does not hold permission android.permission.PROVIDE_RESOLVER_RANKER_SERVICE - this service will not be queried for ResolverRankerServiceResolverComparator.");
                    } else {
                        this.mResolvedRankerName = componentName;
                        intent.setComponent(componentName);
                        return intent;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.m110e(TAG, "Could not look up service " + componentName + "; component name not found");
                }
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ResolverRankerServiceConnection implements ServiceConnection {
        private final CountDownLatch mConnectSignal;
        public final IResolverRankerResult resolverRankerResult = new IResolverRankerResult.Stub() { // from class: com.android.internal.app.ResolverRankerServiceResolverComparator.ResolverRankerServiceConnection.1
            @Override // android.service.resolver.IResolverRankerResult
            public void sendResult(List<ResolverTarget> targets) throws RemoteException {
                synchronized (ResolverRankerServiceResolverComparator.this.mLock) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = targets;
                    ResolverRankerServiceResolverComparator.this.mHandler.sendMessage(msg);
                }
            }
        };

        public ResolverRankerServiceConnection(CountDownLatch connectSignal) {
            this.mConnectSignal = connectSignal;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            synchronized (ResolverRankerServiceResolverComparator.this.mLock) {
                ResolverRankerServiceResolverComparator.this.mRanker = IResolverRankerService.Stub.asInterface(service);
                ResolverRankerServiceResolverComparator resolverRankerServiceResolverComparator = ResolverRankerServiceResolverComparator.this;
                resolverRankerServiceResolverComparator.mComparatorModel = resolverRankerServiceResolverComparator.buildUpdatedModel();
                this.mConnectSignal.countDown();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            synchronized (ResolverRankerServiceResolverComparator.this.mLock) {
                destroy();
            }
        }

        public void destroy() {
            synchronized (ResolverRankerServiceResolverComparator.this.mLock) {
                ResolverRankerServiceResolverComparator.this.mRanker = null;
                ResolverRankerServiceResolverComparator resolverRankerServiceResolverComparator = ResolverRankerServiceResolverComparator.this;
                resolverRankerServiceResolverComparator.mComparatorModel = resolverRankerServiceResolverComparator.buildUpdatedModel();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.app.AbstractResolverComparator
    public void beforeCompute() {
        super.beforeCompute();
        for (UserHandle userHandle : this.mTargetsDictPerUser.keySet()) {
            this.mTargetsDictPerUser.get(userHandle).clear();
        }
        this.mTargets = null;
        this.mRankerServiceName = new ComponentName(this.mContext, getClass());
        this.mComparatorModel = buildUpdatedModel();
        this.mResolvedRankerName = null;
        initRanker(this.mContext);
    }

    private void predictSelectProbabilities(List<ResolverTarget> targets) {
        if (this.mConnection != null) {
            try {
                this.mConnectSignal.await(200L, TimeUnit.MILLISECONDS);
                synchronized (this.mLock) {
                    IResolverRankerService iResolverRankerService = this.mRanker;
                    if (iResolverRankerService != null) {
                        iResolverRankerService.predict(targets, this.mConnection.resolverRankerResult);
                        return;
                    }
                }
            } catch (RemoteException e) {
                Log.m110e(TAG, "Error in Predict: " + e);
            } catch (InterruptedException e2) {
                Log.m110e(TAG, "Error in Wait for Service Connection.");
            }
        }
        afterCompute();
    }

    private void addDefaultSelectProbability(ResolverTarget target) {
        float sum = (target.getLaunchScore() * 2.5543f) + (target.getTimeSpentScore() * 2.8412f) + (target.getRecencyScore() * 0.269f) + (target.getChooserScore() * 4.2222f);
        target.setSelectProbability((float) (1.0d / (Math.exp(1.6568f - sum) + 1.0d)));
    }

    private void setFeatures(ResolverTarget target, float recencyScore, float launchScore, float timeSpentScore, float chooserScore) {
        target.setRecencyScore(recencyScore);
        target.setLaunchScore(launchScore);
        target.setTimeSpentScore(timeSpentScore);
        target.setChooserScore(chooserScore);
    }

    static boolean isPersistentProcess(ResolverActivity.ResolvedComponentInfo rci) {
        return (rci == null || rci.getCount() <= 0 || (rci.getResolveInfoAt(0).activityInfo.applicationInfo.flags & 8) == 0) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ResolverRankerServiceComparatorModel buildUpdatedModel() {
        return new ResolverRankerServiceComparatorModel(this.mStatsPerUser, this.mTargetsDictPerUser, this.mTargets, this.mCollator, this.mRanker, this.mRankerServiceName, this.mAnnotations != null, this.mPmMap);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class ResolverRankerServiceComparatorModel implements ResolverComparatorModel {
        private final boolean mAnnotationsUsed;
        private final Collator mCollator;
        private final Map<UserHandle, PackageManager> mPmMap;
        private final IResolverRankerService mRanker;
        private final ComponentName mRankerServiceName;
        private final Map<UserHandle, Map<String, UsageStats>> mStatsPerUser;
        private final List<ResolverTarget> mTargets;
        private final Map<UserHandle, LinkedHashMap<ComponentName, ResolverTarget>> mTargetsDictPerUser;

        ResolverRankerServiceComparatorModel(Map<UserHandle, Map<String, UsageStats>> statsPerUser, Map<UserHandle, LinkedHashMap<ComponentName, ResolverTarget>> targetsDictPerUser, List<ResolverTarget> targets, Collator collator, IResolverRankerService ranker, ComponentName rankerServiceName, boolean annotationsUsed, Map<UserHandle, PackageManager> pmMap) {
            this.mStatsPerUser = statsPerUser;
            this.mTargetsDictPerUser = targetsDictPerUser;
            this.mTargets = targets;
            this.mCollator = collator;
            this.mRanker = ranker;
            this.mRankerServiceName = rankerServiceName;
            this.mAnnotationsUsed = annotationsUsed;
            this.mPmMap = pmMap;
        }

        @Override // com.android.internal.app.ResolverComparatorModel
        public Comparator<ResolveInfo> getComparator() {
            return new Comparator() { // from class: com.android.internal.app.ResolverRankerServiceResolverComparator$ResolverRankerServiceComparatorModel$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$getComparator$0;
                    lambda$getComparator$0 = ResolverRankerServiceResolverComparator.ResolverRankerServiceComparatorModel.this.lambda$getComparator$0((ResolveInfo) obj, (ResolveInfo) obj2);
                    return lambda$getComparator$0;
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ int lambda$getComparator$0(ResolveInfo lhs, ResolveInfo rhs) {
            int selectProbabilityDiff;
            ResolverTarget lhsTarget = getActivityResolverTargetForUser(lhs.activityInfo, lhs.userHandle);
            ResolverTarget rhsTarget = getActivityResolverTargetForUser(rhs.activityInfo, rhs.userHandle);
            if (lhsTarget != null && rhsTarget != null && (selectProbabilityDiff = Float.compare(rhsTarget.getSelectProbability(), lhsTarget.getSelectProbability())) != 0) {
                return selectProbabilityDiff > 0 ? 1 : -1;
            }
            CharSequence sa = null;
            if (this.mPmMap.containsKey(lhs.userHandle)) {
                sa = lhs.loadLabel(this.mPmMap.get(lhs.userHandle));
            }
            if (sa == null) {
                sa = lhs.activityInfo.name;
            }
            CharSequence sb = null;
            if (this.mPmMap.containsKey(rhs.userHandle)) {
                sb = rhs.loadLabel(this.mPmMap.get(rhs.userHandle));
            }
            if (sb == null) {
                sb = rhs.activityInfo.name;
            }
            return this.mCollator.compare(sa.toString().trim(), sb.toString().trim());
        }

        @Override // com.android.internal.app.ResolverComparatorModel
        public float getScore(TargetInfo targetInfo) {
            if (this.mTargetsDictPerUser.containsKey(targetInfo.getResolveInfo().userHandle) && this.mTargetsDictPerUser.get(targetInfo.getResolveInfo().userHandle).get(targetInfo.getResolvedComponentName()) != null) {
                return this.mTargetsDictPerUser.get(targetInfo.getResolveInfo().userHandle).get(targetInfo.getResolvedComponentName()).getSelectProbability();
            }
            return 0.0f;
        }

        @Override // com.android.internal.app.ResolverComparatorModel
        public void notifyOnTargetSelected(TargetInfo targetInfo) {
            if (this.mRanker != null) {
                int selectedPos = -1;
                try {
                    if (this.mTargetsDictPerUser.containsKey(targetInfo.getResolveInfo().userHandle)) {
                        selectedPos = new ArrayList(this.mTargetsDictPerUser.get(targetInfo.getResolveInfo().userHandle).keySet()).indexOf(targetInfo.getResolvedComponentName());
                    }
                    if (selectedPos >= 0 && this.mTargets != null) {
                        float selectedProbability = getScore(targetInfo);
                        int order = 0;
                        for (ResolverTarget target : this.mTargets) {
                            if (target.getSelectProbability() > selectedProbability) {
                                order++;
                            }
                        }
                        logMetrics(order);
                        this.mRanker.train(this.mTargets, selectedPos);
                    }
                } catch (RemoteException e) {
                    Log.m110e(ResolverRankerServiceResolverComparator.TAG, "Error in Train: " + e);
                }
            }
        }

        private void logMetrics(int selectedPos) {
            if (this.mRankerServiceName != null) {
                MetricsLogger metricsLogger = new MetricsLogger();
                LogMaker log = new LogMaker(1085);
                log.setComponentName(this.mRankerServiceName);
                log.addTaggedData(1086, Integer.valueOf(this.mAnnotationsUsed ? 1 : 0));
                log.addTaggedData(1087, Integer.valueOf(selectedPos));
                metricsLogger.write(log);
            }
        }

        private ResolverTarget getActivityResolverTargetForUser(ActivityInfo activity, UserHandle user) {
            if (this.mStatsPerUser == null || !this.mTargetsDictPerUser.containsKey(user)) {
                return null;
            }
            return this.mTargetsDictPerUser.get(user).get(new ComponentName(activity.packageName, activity.name));
        }
    }
}
