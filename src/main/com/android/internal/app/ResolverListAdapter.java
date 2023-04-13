package com.android.internal.app;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.PermissionChecker;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.LabeledIntent;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.p008os.AsyncTask;
import android.p008os.RemoteException;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.app.ResolverActivity;
import com.android.internal.app.chooser.DisplayResolveInfo;
import com.android.internal.app.chooser.TargetInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes4.dex */
public class ResolverListAdapter extends BaseAdapter {
    private static final String TAG = "ResolverListAdapter";
    private static ColorMatrixColorFilter sSuspendedMatrixColorFilter;
    private final List<ResolveInfo> mBaseResolveList;
    protected final Context mContext;
    private boolean mFilterLastUsed;
    private final int mIconDpi;
    protected final LayoutInflater mInflater;
    private final Intent[] mInitialIntents;
    private final UserHandle mInitialIntentsUserSpace;
    private final List<Intent> mIntents;
    private final boolean mIsAudioCaptureDevice;
    private boolean mIsTabLoaded;
    protected ResolveInfo mLastChosen;
    private DisplayResolveInfo mOtherProfile;
    private int mPlaceholderCount;
    private final PackageManager mPm;
    private Runnable mPostListReadyRunnable;
    final ResolverListCommunicator mResolverListCommunicator;
    ResolverListController mResolverListController;
    private List<ResolverActivity.ResolvedComponentInfo> mUnfilteredResolveList;
    private int mLastChosenPosition = -1;
    private final Map<DisplayResolveInfo, LoadIconTask> mIconLoaders = new HashMap();
    private final Map<DisplayResolveInfo, LoadLabelTask> mLabelLoaders = new HashMap();
    List<DisplayResolveInfo> mDisplayList = new ArrayList();

    public ResolverListAdapter(Context context, List<Intent> payloadIntents, Intent[] initialIntents, List<ResolveInfo> rList, boolean filterLastUsed, ResolverListController resolverListController, ResolverListCommunicator resolverListCommunicator, boolean isAudioCaptureDevice, UserHandle initialIntentsUserSpace) {
        this.mContext = context;
        this.mIntents = payloadIntents;
        this.mInitialIntents = initialIntents;
        this.mBaseResolveList = rList;
        this.mInflater = LayoutInflater.from(context);
        this.mPm = context.getPackageManager();
        this.mFilterLastUsed = filterLastUsed;
        this.mResolverListController = resolverListController;
        this.mResolverListCommunicator = resolverListCommunicator;
        this.mIsAudioCaptureDevice = isAudioCaptureDevice;
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        this.mIconDpi = am.getLauncherLargeIconDensity();
        this.mInitialIntentsUserSpace = initialIntentsUserSpace;
    }

    public ResolverListController getResolverListController() {
        return this.mResolverListController;
    }

    public void handlePackagesChanged() {
        this.mResolverListCommunicator.onHandlePackagesChanged(this);
    }

    public void setPlaceholderCount(int count) {
        this.mPlaceholderCount = count;
    }

    public int getPlaceholderCount() {
        return this.mPlaceholderCount;
    }

    public DisplayResolveInfo getFilteredItem() {
        int i;
        if (this.mFilterLastUsed && (i = this.mLastChosenPosition) >= 0) {
            return this.mDisplayList.get(i);
        }
        return null;
    }

    public DisplayResolveInfo getOtherProfile() {
        return this.mOtherProfile;
    }

    public int getFilteredPosition() {
        int i;
        if (this.mFilterLastUsed && (i = this.mLastChosenPosition) >= 0) {
            return i;
        }
        return -1;
    }

    public boolean hasFilteredItem() {
        return this.mFilterLastUsed && this.mLastChosen != null;
    }

    public float getScore(DisplayResolveInfo target) {
        return this.mResolverListController.getScore(target);
    }

    public float getScore(TargetInfo targetInfo) {
        return this.mResolverListController.getScore(targetInfo);
    }

    public void updateModel(TargetInfo targetInfo) {
        this.mResolverListController.updateModel(targetInfo);
    }

    public void updateChooserCounts(String packageName, String action, UserHandle userHandle) {
        this.mResolverListController.updateChooserCounts(packageName, userHandle, action);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ResolverActivity.ResolvedComponentInfo> getUnfilteredResolveList() {
        return this.mUnfilteredResolveList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean rebuildList(boolean doPostProcessing) {
        Trace.beginSection("ResolverListAdapter#rebuildList");
        this.mDisplayList.clear();
        this.mIsTabLoaded = false;
        this.mLastChosenPosition = -1;
        List<ResolverActivity.ResolvedComponentInfo> currentResolveList = getInitialRebuiltResolveList();
        this.mUnfilteredResolveList = performPrimaryResolveListFiltering(currentResolveList);
        ResolverActivity.ResolvedComponentInfo otherProfileInfo = getFirstNonCurrentUserResolvedComponentInfo(currentResolveList);
        updateOtherProfileTreatment(otherProfileInfo);
        if (otherProfileInfo != null) {
            currentResolveList.remove(otherProfileInfo);
        }
        boolean needsCopyOfUnfiltered = this.mUnfilteredResolveList == currentResolveList;
        List<ResolverActivity.ResolvedComponentInfo> originalList = performSecondaryResolveListFiltering(currentResolveList, needsCopyOfUnfiltered);
        if (originalList != null) {
            this.mUnfilteredResolveList = originalList;
        }
        boolean result = finishRebuildingListWithFilteredResults(currentResolveList, doPostProcessing);
        Trace.endSection();
        return result;
    }

    List<ResolverActivity.ResolvedComponentInfo> getInitialRebuiltResolveList() {
        if (this.mBaseResolveList != null) {
            List<ResolverActivity.ResolvedComponentInfo> currentResolveList = new ArrayList<>();
            this.mResolverListController.addResolveListDedupe(currentResolveList, this.mResolverListCommunicator.getTargetIntent(), this.mBaseResolveList);
            return currentResolveList;
        }
        return this.mResolverListController.getResolversForIntent(true, this.mResolverListCommunicator.shouldGetActivityMetadata(), this.mResolverListCommunicator.shouldGetOnlyDefaultActivities(), this.mIntents);
    }

    List<ResolverActivity.ResolvedComponentInfo> performPrimaryResolveListFiltering(List<ResolverActivity.ResolvedComponentInfo> currentResolveList) {
        if (this.mBaseResolveList != null || currentResolveList == null) {
            return currentResolveList;
        }
        List<ResolverActivity.ResolvedComponentInfo> originalList = this.mResolverListController.filterIneligibleActivities(currentResolveList, true);
        return originalList == null ? currentResolveList : originalList;
    }

    List<ResolverActivity.ResolvedComponentInfo> performSecondaryResolveListFiltering(List<ResolverActivity.ResolvedComponentInfo> currentResolveList, boolean returnCopyOfOriginalListIfModified) {
        if (currentResolveList == null || currentResolveList.isEmpty()) {
            return currentResolveList;
        }
        return this.mResolverListController.filterLowPriority(currentResolveList, returnCopyOfOriginalListIfModified);
    }

    void updateOtherProfileTreatment(ResolverActivity.ResolvedComponentInfo otherProfileInfo) {
        this.mLastChosen = null;
        if (otherProfileInfo != null) {
            this.mOtherProfile = makeOtherProfileDisplayResolveInfo(this.mContext, otherProfileInfo, this.mPm, this.mResolverListCommunicator, this.mIconDpi);
            return;
        }
        this.mOtherProfile = null;
        try {
            this.mLastChosen = this.mResolverListController.getLastChosen();
        } catch (RemoteException re) {
            Log.m112d(TAG, "Error calling getLastChosenActivity\n" + re);
        }
    }

    boolean finishRebuildingListWithFilteredResults(List<ResolverActivity.ResolvedComponentInfo> filteredResolveList, boolean doPostProcessing) {
        if (filteredResolveList == null || filteredResolveList.size() < 2) {
            setPlaceholderCount(0);
            processSortedList(filteredResolveList, doPostProcessing);
            return true;
        }
        int placeholderCount = filteredResolveList.size();
        if (this.mResolverListCommunicator.useLayoutWithDefault()) {
            placeholderCount--;
        }
        setPlaceholderCount(placeholderCount);
        postListReadyRunnable(doPostProcessing, false);
        createSortingTask(doPostProcessing).execute(filteredResolveList);
        return false;
    }

    AsyncTask<List<ResolverActivity.ResolvedComponentInfo>, Void, List<ResolverActivity.ResolvedComponentInfo>> createSortingTask(final boolean doPostProcessing) {
        return new AsyncTask<List<ResolverActivity.ResolvedComponentInfo>, Void, List<ResolverActivity.ResolvedComponentInfo>>() { // from class: com.android.internal.app.ResolverListAdapter.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public List<ResolverActivity.ResolvedComponentInfo> doInBackground(List<ResolverActivity.ResolvedComponentInfo>... params) {
                ResolverListAdapter.this.mResolverListController.sort(params[0]);
                return params[0];
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.AsyncTask
            public void onPostExecute(List<ResolverActivity.ResolvedComponentInfo> sortedComponents) {
                ResolverListAdapter.this.processSortedList(sortedComponents, doPostProcessing);
                ResolverListAdapter.this.notifyDataSetChanged();
                if (doPostProcessing) {
                    ResolverListAdapter.this.mResolverListCommunicator.updateProfileViewButton();
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void processSortedList(List<ResolverActivity.ResolvedComponentInfo> sortedComponents, boolean doPostProcessing) {
        int n = sortedComponents != null ? sortedComponents.size() : 0;
        Trace.beginSection("ResolverListAdapter#processSortedList:" + n);
        boolean z = true;
        if (n != 0) {
            if (this.mInitialIntents != null) {
                int i = 0;
                while (true) {
                    Intent[] intentArr = this.mInitialIntents;
                    if (i >= intentArr.length) {
                        break;
                    }
                    Intent ii = intentArr[i];
                    if (ii != null) {
                        Intent rii = ii.getClass() == Intent.class ? ii : new Intent(ii);
                        ActivityInfo ai = rii.resolveActivityInfo(this.mPm, 0);
                        if (ai == null) {
                            Log.m104w(TAG, "No activity found for " + ii);
                        } else {
                            ResolveInfo ri = new ResolveInfo();
                            ri.activityInfo = ai;
                            UserManager userManager = (UserManager) this.mContext.getSystemService("user");
                            if (ii instanceof LabeledIntent) {
                                LabeledIntent li = (LabeledIntent) ii;
                                ri.resolvePackageName = li.getSourcePackage();
                                ri.labelRes = li.getLabelResource();
                                ri.nonLocalizedLabel = li.getNonLocalizedLabel();
                                ri.icon = li.getIconResource();
                                ri.iconResourceId = ri.icon;
                            }
                            if (userManager.isManagedProfile()) {
                                ri.noResourceId = z;
                                ri.icon = 0;
                            }
                            ri.userHandle = this.mInitialIntentsUserSpace;
                            addResolveInfo(new DisplayResolveInfo(ii, ri, ri.loadLabel(this.mPm), null, ii, makePresentationGetter(ri)));
                        }
                    }
                    i++;
                    z = true;
                }
            }
            for (ResolverActivity.ResolvedComponentInfo rci : sortedComponents) {
                if (rci.getResolveInfoAt(0) != null) {
                    addResolveInfoWithAlternates(rci);
                }
            }
        }
        this.mResolverListCommunicator.sendVoiceChoicesIfNeeded();
        postListReadyRunnable(doPostProcessing, true);
        this.mIsTabLoaded = true;
        Trace.endSection();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void postListReadyRunnable(final boolean doPostProcessing, final boolean rebuildCompleted) {
        if (this.mPostListReadyRunnable == null) {
            this.mPostListReadyRunnable = new Runnable() { // from class: com.android.internal.app.ResolverListAdapter.2
                @Override // java.lang.Runnable
                public void run() {
                    ResolverListAdapter.this.mResolverListCommunicator.onPostListReady(ResolverListAdapter.this, doPostProcessing, rebuildCompleted);
                    ResolverListAdapter.this.mPostListReadyRunnable = null;
                }
            };
            this.mContext.getMainThreadHandler().post(this.mPostListReadyRunnable);
        }
    }

    private void addResolveInfoWithAlternates(ResolverActivity.ResolvedComponentInfo rci) {
        int count = rci.getCount();
        Intent intent = rci.getIntentAt(0);
        ResolveInfo add = rci.getResolveInfoAt(0);
        Intent replaceIntent = this.mResolverListCommunicator.getReplacementIntent(add.activityInfo, intent);
        Intent defaultIntent = this.mResolverListCommunicator.getReplacementIntent(add.activityInfo, this.mResolverListCommunicator.getTargetIntent());
        DisplayResolveInfo dri = new DisplayResolveInfo(intent, add, replaceIntent != null ? replaceIntent : defaultIntent, makePresentationGetter(add));
        dri.setPinned(rci.isPinned());
        if (rci.isPinned()) {
            Log.m108i(TAG, "Pinned item: " + rci.name);
        }
        addResolveInfo(dri);
        if (replaceIntent == intent) {
            for (int i = 1; i < count; i++) {
                Intent altIntent = rci.getIntentAt(i);
                dri.addAlternateSourceIntent(altIntent);
            }
        }
        updateLastChosenPosition(add);
    }

    private void updateLastChosenPosition(ResolveInfo info) {
        if (this.mOtherProfile != null) {
            this.mLastChosenPosition = -1;
            return;
        }
        ResolveInfo resolveInfo = this.mLastChosen;
        if (resolveInfo != null && resolveInfo.activityInfo.packageName.equals(info.activityInfo.packageName) && this.mLastChosen.activityInfo.name.equals(info.activityInfo.name)) {
            this.mLastChosenPosition = this.mDisplayList.size() - 1;
        }
    }

    private void addResolveInfo(DisplayResolveInfo dri) {
        if (dri != null && dri.getResolveInfo() != null && dri.getResolveInfo().targetUserId == -2 && shouldAddResolveInfo(dri)) {
            this.mDisplayList.add(dri);
            Log.m108i(TAG, "Add DisplayResolveInfo component: " + dri.getResolvedComponentName() + ", intent component: " + dri.getResolvedIntent().getComponent());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldAddResolveInfo(DisplayResolveInfo dri) {
        for (DisplayResolveInfo existingInfo : this.mDisplayList) {
            if (this.mResolverListCommunicator.resolveInfoMatch(dri.getResolveInfo(), existingInfo.getResolveInfo())) {
                return false;
            }
        }
        return true;
    }

    public ResolveInfo resolveInfoForPosition(int position, boolean filtered) {
        TargetInfo target = targetInfoForPosition(position, filtered);
        if (target != null) {
            return target.getResolveInfo();
        }
        return null;
    }

    public TargetInfo targetInfoForPosition(int position, boolean filtered) {
        if (filtered) {
            return getItem(position);
        }
        if (this.mDisplayList.size() > position) {
            return this.mDisplayList.get(position);
        }
        return null;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int totalSize;
        List<DisplayResolveInfo> list = this.mDisplayList;
        if (list == null || list.isEmpty()) {
            totalSize = this.mPlaceholderCount;
        } else {
            totalSize = this.mDisplayList.size();
        }
        if (this.mFilterLastUsed && this.mLastChosenPosition >= 0) {
            return totalSize - 1;
        }
        return totalSize;
    }

    public int getUnfilteredCount() {
        return this.mDisplayList.size();
    }

    @Override // android.widget.Adapter
    public TargetInfo getItem(int position) {
        int i;
        if (this.mFilterLastUsed && (i = this.mLastChosenPosition) >= 0 && position >= i) {
            position++;
        }
        if (this.mDisplayList.size() > position) {
            return this.mDisplayList.get(position);
        }
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    public int getDisplayResolveInfoCount() {
        return this.mDisplayList.size();
    }

    public DisplayResolveInfo getDisplayResolveInfo(int index) {
        return this.mDisplayList.get(index);
    }

    @Override // android.widget.Adapter
    public final View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = createView(parent);
        }
        onBindView(view, getItem(position), position);
        return view;
    }

    public final View createView(ViewGroup parent) {
        View view = onCreateView(parent);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    View onCreateView(ViewGroup parent) {
        return this.mInflater.inflate(C4057R.layout.resolve_list_item, parent, false);
    }

    public final void bindView(int position, View view) {
        onBindView(view, getItem(position), position);
    }

    protected void onBindView(View view, TargetInfo info, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (info == null) {
            holder.icon.setImageDrawable(this.mContext.getDrawable(C4057R.C4058drawable.resolver_icon_placeholder));
            holder.bindLabel("", "", false);
        } else if (info instanceof DisplayResolveInfo) {
            DisplayResolveInfo dri = (DisplayResolveInfo) info;
            if (dri.hasDisplayLabel()) {
                holder.bindLabel(dri.getDisplayLabel(), dri.getExtendedInfo(), alwaysShowSubLabel());
            } else {
                holder.bindLabel("", "", false);
                loadLabel(dri);
            }
            holder.bindIcon(info);
            if (!dri.hasDisplayIcon()) {
                loadIcon(dri);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void loadIcon(DisplayResolveInfo info) {
        if (this.mIconLoaders.get(info) == null) {
            LoadIconTask task = new LoadIconTask(info);
            this.mIconLoaders.put(info, task);
            task.execute(new Void[0]);
        }
    }

    private void loadLabel(DisplayResolveInfo info) {
        if (this.mLabelLoaders.get(info) == null) {
            LoadLabelTask task = createLoadLabelTask(info);
            this.mLabelLoaders.put(info, task);
            task.execute(new Void[0]);
        }
    }

    protected LoadLabelTask createLoadLabelTask(DisplayResolveInfo info) {
        return new LoadLabelTask(info);
    }

    public void onDestroy() {
        if (this.mPostListReadyRunnable != null) {
            this.mContext.getMainThreadHandler().removeCallbacks(this.mPostListReadyRunnable);
            this.mPostListReadyRunnable = null;
        }
        ResolverListController resolverListController = this.mResolverListController;
        if (resolverListController != null) {
            resolverListController.destroy();
        }
        cancelTasks(this.mIconLoaders.values());
        cancelTasks(this.mLabelLoaders.values());
        this.mIconLoaders.clear();
        this.mLabelLoaders.clear();
    }

    private <T extends AsyncTask> void cancelTasks(Collection<T> tasks) {
        for (T task : tasks) {
            task.cancel(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ColorMatrixColorFilter getSuspendedColorMatrix() {
        if (sSuspendedMatrixColorFilter == null) {
            ColorMatrix tempBrightnessMatrix = new ColorMatrix();
            float[] mat = tempBrightnessMatrix.getArray();
            mat[0] = 0.5f;
            mat[6] = 0.5f;
            mat[12] = 0.5f;
            mat[4] = 127;
            mat[9] = 127;
            mat[14] = 127;
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0.0f);
            matrix.preConcat(tempBrightnessMatrix);
            sSuspendedMatrixColorFilter = new ColorMatrixColorFilter(matrix);
        }
        return sSuspendedMatrixColorFilter;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActivityInfoPresentationGetter makePresentationGetter(ActivityInfo ai) {
        return new ActivityInfoPresentationGetter(this.mContext, this.mIconDpi, ai);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ResolveInfoPresentationGetter makePresentationGetter(ResolveInfo ri) {
        return new ResolveInfoPresentationGetter(this.mContext, this.mIconDpi, ri);
    }

    Drawable loadIconForResolveInfo(ResolveInfo ri) {
        return makePresentationGetter(ri).getIcon(ResolverActivity.getResolveInfoUserHandle(ri, getUserHandle()));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadFilteredItemIconTaskAsync(final ImageView iconView) {
        final DisplayResolveInfo iconInfo = getFilteredItem();
        if (iconView != null && iconInfo != null) {
            new AsyncTask<Void, Void, Drawable>() { // from class: com.android.internal.app.ResolverListAdapter.3
                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.p008os.AsyncTask
                public Drawable doInBackground(Void... params) {
                    return ResolverListAdapter.this.loadIconForResolveInfo(iconInfo.getResolveInfo());
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.p008os.AsyncTask
                public void onPostExecute(Drawable d) {
                    iconView.setImageDrawable(d);
                }
            }.execute(new Void[0]);
        }
    }

    public UserHandle getUserHandle() {
        return this.mResolverListController.getUserHandle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<ResolverActivity.ResolvedComponentInfo> getResolversForUser(UserHandle userHandle) {
        return this.mResolverListController.getResolversForIntentAsUser(true, this.mResolverListCommunicator.shouldGetActivityMetadata(), this.mResolverListCommunicator.shouldGetOnlyDefaultActivities(), this.mIntents, userHandle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<Intent> getIntents() {
        return this.mIntents;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isTabLoaded() {
        return this.mIsTabLoaded;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void markTabLoaded() {
        this.mIsTabLoaded = true;
    }

    protected boolean alwaysShowSubLabel() {
        return false;
    }

    private static ResolverActivity.ResolvedComponentInfo getFirstNonCurrentUserResolvedComponentInfo(List<ResolverActivity.ResolvedComponentInfo> resolveList) {
        if (resolveList == null) {
            return null;
        }
        for (ResolverActivity.ResolvedComponentInfo info : resolveList) {
            ResolveInfo resolveInfo = info.getResolveInfoAt(0);
            if (resolveInfo.targetUserId != -2) {
                return info;
            }
        }
        return null;
    }

    private static DisplayResolveInfo makeOtherProfileDisplayResolveInfo(Context context, ResolverActivity.ResolvedComponentInfo resolvedComponentInfo, PackageManager pm, ResolverListCommunicator resolverListCommunicator, int iconDpi) {
        ResolveInfo resolveInfo = resolvedComponentInfo.getResolveInfoAt(0);
        Intent pOrigIntent = resolverListCommunicator.getReplacementIntent(resolveInfo.activityInfo, resolvedComponentInfo.getIntentAt(0));
        Intent replacementIntent = resolverListCommunicator.getReplacementIntent(resolveInfo.activityInfo, resolverListCommunicator.getTargetIntent());
        ResolveInfoPresentationGetter presentationGetter = new ResolveInfoPresentationGetter(context, iconDpi, resolveInfo);
        return new DisplayResolveInfo(resolvedComponentInfo.getIntentAt(0), resolveInfo, resolveInfo.loadLabel(pm), resolveInfo.loadLabel(pm), pOrigIntent != null ? pOrigIntent : replacementIntent, presentationGetter);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public interface ResolverListCommunicator {
        Intent getReplacementIntent(ActivityInfo activityInfo, Intent intent);

        Intent getTargetIntent();

        void onHandlePackagesChanged(ResolverListAdapter resolverListAdapter);

        void onPostListReady(ResolverListAdapter resolverListAdapter, boolean z, boolean z2);

        boolean resolveInfoMatch(ResolveInfo resolveInfo, ResolveInfo resolveInfo2);

        void sendVoiceChoicesIfNeeded();

        boolean shouldGetActivityMetadata();

        void updateProfileViewButton();

        boolean useLayoutWithDefault();

        default boolean shouldGetOnlyDefaultActivities() {
            return true;
        }
    }

    /* loaded from: classes4.dex */
    public static class ViewHolder {
        public Drawable defaultItemViewBackground;
        public ImageView icon;
        public View itemView;
        public TextView text;
        public TextView text2;

        public ViewHolder(View view) {
            this.itemView = view;
            this.defaultItemViewBackground = view.getBackground();
            this.text = (TextView) view.findViewById(16908308);
            this.text2 = (TextView) view.findViewById(16908309);
            this.icon = (ImageView) view.findViewById(16908294);
        }

        public void bindLabel(CharSequence label, CharSequence subLabel, boolean showSubLabel) {
            this.text.setText(label);
            if (TextUtils.equals(label, subLabel)) {
                subLabel = null;
            }
            this.text2.setText(subLabel);
            if (showSubLabel || subLabel != null) {
                this.text2.setVisibility(0);
            } else {
                this.text2.setVisibility(8);
            }
            this.itemView.setContentDescription(null);
        }

        public void updateContentDescription(String description) {
            this.itemView.setContentDescription(description);
        }

        public void bindIcon(TargetInfo info) {
            this.icon.setImageDrawable(info.getDisplayIcon(this.itemView.getContext()));
            if (info.isSuspended()) {
                this.icon.setColorFilter(ResolverListAdapter.getSuspendedColorMatrix());
            } else {
                this.icon.setColorFilter((ColorFilter) null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes4.dex */
    public class LoadLabelTask extends AsyncTask<Void, Void, CharSequence[]> {
        private final DisplayResolveInfo mDisplayResolveInfo;

        protected LoadLabelTask(DisplayResolveInfo dri) {
            this.mDisplayResolveInfo = dri;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public CharSequence[] doInBackground(Void... voids) {
            ResolveInfoPresentationGetter pg = ResolverListAdapter.this.makePresentationGetter(this.mDisplayResolveInfo.getResolveInfo());
            if (ResolverListAdapter.this.mIsAudioCaptureDevice) {
                ActivityInfo activityInfo = this.mDisplayResolveInfo.getResolveInfo().activityInfo;
                String packageName = activityInfo.packageName;
                int uid = activityInfo.applicationInfo.uid;
                boolean hasRecordPermission = PermissionChecker.checkPermissionForPreflight(ResolverListAdapter.this.mContext, Manifest.C0000permission.RECORD_AUDIO, -1, uid, packageName) == 0;
                if (!hasRecordPermission) {
                    return new CharSequence[]{pg.getLabel(), ResolverListAdapter.this.mContext.getString(C4057R.string.usb_device_resolve_prompt_warn)};
                }
            }
            return new CharSequence[]{pg.getLabel(), pg.getSubLabel()};
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public void onPostExecute(CharSequence[] result) {
            if (this.mDisplayResolveInfo.hasDisplayLabel()) {
                return;
            }
            this.mDisplayResolveInfo.setDisplayLabel(result[0]);
            this.mDisplayResolveInfo.setExtendedInfo(result[1]);
            ResolverListAdapter.this.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class LoadIconTask extends AsyncTask<Void, Void, Drawable> {
        protected final DisplayResolveInfo mDisplayResolveInfo;
        private final ResolveInfo mResolveInfo;

        /* JADX INFO: Access modifiers changed from: package-private */
        public LoadIconTask(DisplayResolveInfo dri) {
            this.mDisplayResolveInfo = dri;
            this.mResolveInfo = dri.getResolveInfo();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.AsyncTask
        public Drawable doInBackground(Void... params) {
            return ResolverListAdapter.this.loadIconForResolveInfo(this.mResolveInfo);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.AsyncTask
        public void onPostExecute(Drawable d) {
            DisplayResolveInfo otherProfile = ResolverListAdapter.this.getOtherProfile();
            DisplayResolveInfo displayResolveInfo = this.mDisplayResolveInfo;
            if (otherProfile == displayResolveInfo) {
                ResolverListAdapter.this.mResolverListCommunicator.updateProfileViewButton();
            } else if (!displayResolveInfo.hasDisplayIcon()) {
                this.mDisplayResolveInfo.setDisplayIcon(d);
                ResolverListAdapter.this.notifyDataSetChanged();
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class ResolveInfoPresentationGetter extends ActivityInfoPresentationGetter {
        private final ResolveInfo mRi;

        public ResolveInfoPresentationGetter(Context ctx, int iconDpi, ResolveInfo ri) {
            super(ctx, iconDpi, ri.activityInfo);
            this.mRi = ri;
        }

        @Override // com.android.internal.app.ResolverListAdapter.ActivityInfoPresentationGetter, com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        Drawable getIconSubstituteInternal() {
            Drawable dr = null;
            try {
                if (this.mRi.resolvePackageName != null && this.mRi.icon != 0) {
                    dr = loadIconFromResource(this.mPm.getResourcesForApplication(this.mRi.resolvePackageName), this.mRi.icon);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.m109e(ResolverListAdapter.TAG, "SUBSTITUTE_SHARE_TARGET_APP_NAME_AND_ICON permission granted but couldn't find resources for package", e);
            }
            return dr == null ? super.getIconSubstituteInternal() : dr;
        }

        @Override // com.android.internal.app.ResolverListAdapter.ActivityInfoPresentationGetter, com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        String getAppSubLabelInternal() {
            return this.mRi.loadLabel(this.mPm).toString();
        }

        @Override // com.android.internal.app.ResolverListAdapter.ActivityInfoPresentationGetter, com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        String getAppLabelForSubstitutePermission() {
            return this.mRi.getComponentInfo().loadLabel(this.mPm).toString();
        }
    }

    /* loaded from: classes4.dex */
    public static class ActivityInfoPresentationGetter extends TargetPresentationGetter {
        private final ActivityInfo mActivityInfo;

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        public /* bridge */ /* synthetic */ Drawable getIcon(UserHandle userHandle) {
            return super.getIcon(userHandle);
        }

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        public /* bridge */ /* synthetic */ Bitmap getIconBitmap(UserHandle userHandle) {
            return super.getIconBitmap(userHandle);
        }

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        public /* bridge */ /* synthetic */ String getLabel() {
            return super.getLabel();
        }

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        public /* bridge */ /* synthetic */ String getSubLabel() {
            return super.getSubLabel();
        }

        public ActivityInfoPresentationGetter(Context ctx, int iconDpi, ActivityInfo activityInfo) {
            super(ctx, iconDpi, activityInfo.applicationInfo);
            this.mActivityInfo = activityInfo;
        }

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        Drawable getIconSubstituteInternal() {
            try {
                if (this.mActivityInfo.icon == 0) {
                    return null;
                }
                Drawable dr = loadIconFromResource(this.mPm.getResourcesForApplication(this.mActivityInfo.applicationInfo), this.mActivityInfo.icon);
                return dr;
            } catch (PackageManager.NameNotFoundException e) {
                Log.m109e(ResolverListAdapter.TAG, "SUBSTITUTE_SHARE_TARGET_APP_NAME_AND_ICON permission granted but couldn't find resources for package", e);
                return null;
            }
        }

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        String getAppSubLabelInternal() {
            return (String) this.mActivityInfo.loadLabel(this.mPm);
        }

        @Override // com.android.internal.app.ResolverListAdapter.TargetPresentationGetter
        String getAppLabelForSubstitutePermission() {
            return getAppSubLabelInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static abstract class TargetPresentationGetter {
        private final ApplicationInfo mAi;
        private Context mCtx;
        private final boolean mHasSubstitutePermission;
        private final int mIconDpi;
        protected PackageManager mPm;

        abstract String getAppLabelForSubstitutePermission();

        abstract String getAppSubLabelInternal();

        abstract Drawable getIconSubstituteInternal();

        TargetPresentationGetter(Context ctx, int iconDpi, ApplicationInfo ai) {
            this.mCtx = ctx;
            PackageManager packageManager = ctx.getPackageManager();
            this.mPm = packageManager;
            this.mAi = ai;
            this.mIconDpi = iconDpi;
            this.mHasSubstitutePermission = packageManager.checkPermission(Manifest.C0000permission.SUBSTITUTE_SHARE_TARGET_APP_NAME_AND_ICON, ai.packageName) == 0;
        }

        public Drawable getIcon(UserHandle userHandle) {
            return new BitmapDrawable(this.mCtx.getResources(), getIconBitmap(userHandle));
        }

        public Bitmap getIconBitmap(UserHandle userHandle) {
            Drawable dr = null;
            if (this.mHasSubstitutePermission) {
                dr = getIconSubstituteInternal();
            }
            if (dr == null) {
                try {
                    if (this.mAi.icon != 0) {
                        dr = loadIconFromResource(this.mPm.getResourcesForApplication(this.mAi), this.mAi.icon);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            if (dr == null) {
                dr = this.mAi.loadIcon(this.mPm);
            }
            SimpleIconFactory sif = SimpleIconFactory.obtain(this.mCtx);
            Bitmap icon = sif.createUserBadgedIconBitmap(dr, userHandle);
            sif.recycle();
            return icon;
        }

        public String getLabel() {
            String label = null;
            if (this.mHasSubstitutePermission) {
                label = getAppLabelForSubstitutePermission();
            }
            if (label == null) {
                String label2 = (String) this.mAi.loadLabel(this.mPm);
                return label2;
            }
            return label;
        }

        public String getSubLabel() {
            if (this.mHasSubstitutePermission) {
                String appSubLabel = getAppSubLabelInternal();
                if (!TextUtils.isEmpty(appSubLabel) && !TextUtils.equals(appSubLabel, getLabel())) {
                    return appSubLabel;
                }
                return null;
            }
            return getAppSubLabelInternal();
        }

        protected String loadLabelFromResource(Resources res, int resId) {
            return res.getString(resId);
        }

        protected Drawable loadIconFromResource(Resources res, int resId) {
            return res.getDrawableForDensity(resId, this.mIconDpi);
        }
    }
}
