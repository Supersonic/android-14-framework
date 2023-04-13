package android.appwidget;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.LoadedApk;
import android.app.PendingIntent;
import android.appwidget.AppWidgetHost;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.LauncherActivityInfo;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.SizeF;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class AppWidgetHostView extends FrameLayout implements AppWidgetHost.AppWidgetHostListener {
    private static final int FIRST_RESOURCE_COLOR_ID = 17170461;
    private static final LayoutInflater.Filter INFLATER_FILTER = new LayoutInflater.Filter() { // from class: android.appwidget.AppWidgetHostView$$ExternalSyntheticLambda2
        @Override // android.view.LayoutInflater.Filter
        public final boolean onLoadClass(Class cls) {
            boolean isAnnotationPresent;
            isAnnotationPresent = cls.isAnnotationPresent(RemoteViews.RemoteView.class);
            return isAnnotationPresent;
        }
    };
    private static final String KEY_INFLATION_ID = "inflation_id";
    private static final String KEY_JAILED_ARRAY = "jail";
    private static final int LAST_RESOURCE_COLOR_ID = 17170525;
    static final boolean LOGD = false;
    static final String TAG = "AppWidgetHostView";
    static final int VIEW_MODE_CONTENT = 1;
    static final int VIEW_MODE_DEFAULT = 3;
    static final int VIEW_MODE_ERROR = 2;
    static final int VIEW_MODE_NOINIT = 0;
    int mAppWidgetId;
    private Executor mAsyncExecutor;
    boolean mColorMappingChanged;
    private RemoteViews.ColorResources mColorResources;
    Context mContext;
    private SizeF mCurrentSize;
    private long mDelayedRestoredInflationId;
    private SparseArray<Parcelable> mDelayedRestoredState;
    AppWidgetProviderInfo mInfo;
    private RemoteViews.InteractionHandler mInteractionHandler;
    private CancellationSignal mLastExecutionSignal;
    private RemoteViews mLastInflatedRemoteViews;
    private long mLastInflatedRemoteViewsId;
    private boolean mOnLightBackground;
    Context mRemoteContext;
    View mView;
    int mViewMode;

    public AppWidgetHostView(Context context) {
        this(context, 17432576, 17432577);
    }

    public AppWidgetHostView(Context context, RemoteViews.InteractionHandler handler) {
        this(context, 17432576, 17432577);
        this.mInteractionHandler = getHandler(handler);
    }

    public AppWidgetHostView(Context context, int animationIn, int animationOut) {
        super(context);
        this.mViewMode = 0;
        this.mColorMappingChanged = false;
        this.mCurrentSize = null;
        this.mColorResources = null;
        this.mLastInflatedRemoteViews = null;
        this.mLastInflatedRemoteViewsId = -1L;
        this.mContext = context;
        setIsRootNamespace(true);
    }

    public void setInteractionHandler(RemoteViews.InteractionHandler handler) {
        this.mInteractionHandler = getHandler(handler);
    }

    public void setAppWidget(int appWidgetId, AppWidgetProviderInfo info) {
        this.mAppWidgetId = appWidgetId;
        this.mInfo = info;
        Rect padding = getDefaultPadding();
        setPadding(padding.left, padding.top, padding.right, padding.bottom);
        if (info != null) {
            String description = info.loadLabel(getContext().getPackageManager());
            if ((info.providerInfo.applicationInfo.flags & 1073741824) != 0) {
                description = Resources.getSystem().getString(C4057R.string.suspended_widget_accessibility, description);
            }
            setContentDescription(description);
        }
    }

    public static Rect getDefaultPaddingForWidget(Context context, ComponentName component, Rect padding) {
        return getDefaultPaddingForWidget(context, padding);
    }

    private static Rect getDefaultPaddingForWidget(Context context, Rect padding) {
        if (padding == null) {
            padding = new Rect(0, 0, 0, 0);
        } else {
            padding.set(0, 0, 0, 0);
        }
        Resources r = context.getResources();
        padding.left = r.getDimensionPixelSize(C4057R.dimen.default_app_widget_padding_left);
        padding.right = r.getDimensionPixelSize(C4057R.dimen.default_app_widget_padding_right);
        padding.top = r.getDimensionPixelSize(C4057R.dimen.default_app_widget_padding_top);
        padding.bottom = r.getDimensionPixelSize(C4057R.dimen.default_app_widget_padding_bottom);
        return padding;
    }

    private Rect getDefaultPadding() {
        return getDefaultPaddingForWidget(this.mContext, null);
    }

    public int getAppWidgetId() {
        return this.mAppWidgetId;
    }

    public AppWidgetProviderInfo getAppWidgetInfo() {
        return this.mInfo;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        SparseArray<Parcelable> jail = new SparseArray<>();
        super.dispatchSaveInstanceState(jail);
        Bundle bundle = new Bundle();
        bundle.putSparseParcelableArray(KEY_JAILED_ARRAY, jail);
        bundle.putLong(KEY_INFLATION_ID, this.mLastInflatedRemoteViewsId);
        container.put(generateId(), bundle);
        container.put(generateId(), bundle);
    }

    private int generateId() {
        int id = getId();
        return id == -1 ? this.mAppWidgetId : id;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        Parcelable parcelable = container.get(generateId());
        SparseArray<Parcelable> jail = null;
        long inflationId = -1;
        if (parcelable instanceof Bundle) {
            Bundle bundle = (Bundle) parcelable;
            jail = bundle.getSparseParcelableArray(KEY_JAILED_ARRAY);
            inflationId = bundle.getLong(KEY_INFLATION_ID, -1L);
        }
        if (jail == null) {
            jail = new SparseArray<>();
        }
        this.mDelayedRestoredState = jail;
        this.mDelayedRestoredInflationId = inflationId;
        restoreInstanceState();
    }

    void restoreInstanceState() {
        long inflationId = this.mDelayedRestoredInflationId;
        SparseArray<Parcelable> state = this.mDelayedRestoredState;
        if (inflationId == -1 || inflationId != this.mLastInflatedRemoteViewsId) {
            return;
        }
        this.mDelayedRestoredInflationId = -1L;
        this.mDelayedRestoredState = null;
        try {
            super.dispatchRestoreInstanceState(state);
        } catch (Exception e) {
            Log.m109e(TAG, "failed to restoreInstanceState for widget id: " + this.mAppWidgetId + ", " + (this.mInfo == null ? "null" : this.mInfo.provider), e);
        }
    }

    private SizeF computeSizeFromLayout(int left, int top, int right, int bottom) {
        float density = getResources().getDisplayMetrics().density;
        return new SizeF((((right - left) - getPaddingLeft()) - getPaddingRight()) / density, (((bottom - top) - getPaddingTop()) - getPaddingBottom()) / density);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        RemoteViews toApply;
        try {
            SizeF oldSize = this.mCurrentSize;
            try {
                SizeF newSize = computeSizeFromLayout(left, top, right, bottom);
                this.mCurrentSize = newSize;
                RemoteViews remoteViews = this.mLastInflatedRemoteViews;
                if (remoteViews != null && (toApply = remoteViews.getRemoteViewsToApplyIfDifferent(oldSize, newSize)) != null) {
                    applyRemoteViews(toApply, false);
                    measureChildWithMargins(this.mView, View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824), 0);
                }
                super.onLayout(changed, left, top, right, bottom);
            } catch (RuntimeException e) {
                e = e;
                Log.m109e(TAG, "Remote provider threw runtime exception, using error view instead.", e);
                removeViewInLayout(this.mView);
                View child = getErrorView();
                prepareView(child);
                addViewInLayout(child, 0, child.getLayoutParams());
                measureChild(child, View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
                child.layout(0, 0, child.getMeasuredWidth() + this.mPaddingLeft + this.mPaddingRight, child.getMeasuredHeight() + this.mPaddingTop + this.mPaddingBottom);
                this.mView = child;
                this.mViewMode = 2;
            }
        } catch (RuntimeException e2) {
            e = e2;
        }
    }

    @Deprecated
    public void updateAppWidgetSize(Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight) {
        updateAppWidgetSize(newOptions, minWidth, minHeight, maxWidth, maxHeight, false);
    }

    public void updateAppWidgetSize(Bundle newOptions, List<SizeF> sizes) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this.mContext);
        Rect padding = getDefaultPadding();
        float density = getResources().getDisplayMetrics().density;
        float xPaddingDips = (padding.left + padding.right) / density;
        float yPaddingDips = (padding.top + padding.bottom) / density;
        ArrayList<SizeF> paddedSizes = new ArrayList<>(sizes.size());
        float minWidth = Float.MAX_VALUE;
        float maxWidth = 0.0f;
        float minHeight = Float.MAX_VALUE;
        float maxHeight = 0.0f;
        int i = 0;
        while (i < sizes.size()) {
            SizeF size = sizes.get(i);
            SizeF paddedSize = new SizeF(Math.max(0.0f, size.getWidth() - xPaddingDips), Math.max(0.0f, size.getHeight() - yPaddingDips));
            paddedSizes.add(paddedSize);
            minWidth = Math.min(minWidth, paddedSize.getWidth());
            maxWidth = Math.max(maxWidth, paddedSize.getWidth());
            minHeight = Math.min(minHeight, paddedSize.getHeight());
            maxHeight = Math.max(maxHeight, paddedSize.getHeight());
            i++;
            padding = padding;
            density = density;
        }
        if (paddedSizes.equals(widgetManager.getAppWidgetOptions(this.mAppWidgetId).getParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES))) {
            return;
        }
        Bundle options = newOptions.deepCopy();
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, (int) minWidth);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, (int) minHeight);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, (int) maxWidth);
        options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, (int) maxHeight);
        options.putParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES, paddedSizes);
        updateAppWidgetOptions(options);
    }

    public void updateAppWidgetSize(Bundle newOptions, int minWidth, int minHeight, int maxWidth, int maxHeight, boolean ignorePadding) {
        Bundle newOptions2;
        if (newOptions != null) {
            newOptions2 = newOptions;
        } else {
            newOptions2 = new Bundle();
        }
        Rect padding = getDefaultPadding();
        float density = getResources().getDisplayMetrics().density;
        int xPaddingDips = (int) ((padding.left + padding.right) / density);
        int yPaddingDips = (int) ((padding.top + padding.bottom) / density);
        int newMinWidth = minWidth - (ignorePadding ? 0 : xPaddingDips);
        int newMinHeight = minHeight - (ignorePadding ? 0 : yPaddingDips);
        int newMaxWidth = maxWidth - (ignorePadding ? 0 : xPaddingDips);
        int newMaxHeight = maxHeight - (ignorePadding ? 0 : yPaddingDips);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this.mContext);
        Bundle oldOptions = widgetManager.getAppWidgetOptions(this.mAppWidgetId);
        boolean needsUpdate = false;
        needsUpdate = (newMinWidth == oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) && newMinHeight == oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT) && newMaxWidth == oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH) && newMaxHeight == oldOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)) ? true : true;
        if (needsUpdate) {
            newOptions2.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, newMinWidth);
            newOptions2.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, newMinHeight);
            newOptions2.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, newMaxWidth);
            newOptions2.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, newMaxHeight);
            newOptions2.putParcelableArrayList(AppWidgetManager.OPTION_APPWIDGET_SIZES, new ArrayList<>());
            updateAppWidgetOptions(newOptions2);
        }
    }

    public void updateAppWidgetOptions(Bundle options) {
        AppWidgetManager.getInstance(this.mContext).updateAppWidgetOptions(this.mAppWidgetId, options);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Context context = this.mRemoteContext;
        if (context == null) {
            context = this.mContext;
        }
        return new FrameLayout.LayoutParams(context, attrs);
    }

    public void setExecutor(Executor executor) {
        CancellationSignal cancellationSignal = this.mLastExecutionSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            this.mLastExecutionSignal = null;
        }
        this.mAsyncExecutor = executor;
    }

    public void setOnLightBackground(boolean onLightBackground) {
        this.mOnLightBackground = onLightBackground;
    }

    @Override // android.appwidget.AppWidgetHost.AppWidgetHostListener
    public void onUpdateProviderInfo(AppWidgetProviderInfo info) {
        setAppWidget(this.mAppWidgetId, info);
        this.mViewMode = 0;
        updateAppWidget(null);
    }

    @Override // android.appwidget.AppWidgetHost.AppWidgetHostListener
    public void updateAppWidget(RemoteViews remoteViews) {
        this.mLastInflatedRemoteViews = remoteViews;
        applyRemoteViews(remoteViews, true);
    }

    private void reapplyLastRemoteViews() {
        SparseArray<Parcelable> savedState = new SparseArray<>();
        saveHierarchyState(savedState);
        applyRemoteViews(this.mLastInflatedRemoteViews, true);
        restoreHierarchyState(savedState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyRemoteViews(RemoteViews remoteViews, boolean useAsyncIfPossible) {
        boolean recycled = false;
        View content = null;
        Exception exception = null;
        this.mLastInflatedRemoteViewsId = -1L;
        CancellationSignal cancellationSignal = this.mLastExecutionSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            this.mLastExecutionSignal = null;
        }
        if (remoteViews == null) {
            if (this.mViewMode == 3) {
                return;
            }
            content = getDefaultView();
            this.mViewMode = 3;
        } else {
            RemoteViews rvToApply = remoteViews.getRemoteViewsToApply(this.mContext, this.mCurrentSize);
            if (this.mOnLightBackground) {
                rvToApply = rvToApply.getDarkTextViews();
            }
            if (this.mAsyncExecutor != null && useAsyncIfPossible) {
                inflateAsync(rvToApply);
                return;
            }
            this.mRemoteContext = getRemoteContextEnsuringCorrectCachedApkPath();
            if (!this.mColorMappingChanged && rvToApply.canRecycleView(this.mView)) {
                try {
                    rvToApply.reapply(this.mContext, this.mView, this.mInteractionHandler, this.mCurrentSize, this.mColorResources);
                    content = this.mView;
                    this.mLastInflatedRemoteViewsId = rvToApply.computeUniqueId(remoteViews);
                    recycled = true;
                } catch (RuntimeException e) {
                    exception = e;
                }
            }
            if (content == null) {
                try {
                    content = rvToApply.apply(this.mContext, this, this.mInteractionHandler, this.mCurrentSize, this.mColorResources);
                    this.mLastInflatedRemoteViewsId = rvToApply.computeUniqueId(remoteViews);
                } catch (RuntimeException e2) {
                    exception = e2;
                }
            }
            this.mViewMode = 1;
        }
        applyContent(content, recycled, exception);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyContent(View content, boolean recycled, Exception exception) {
        this.mColorMappingChanged = false;
        if (content == null) {
            if (this.mViewMode == 2) {
                return;
            }
            if (exception != null) {
                Log.m103w(TAG, "Error inflating RemoteViews", exception);
            }
            content = getErrorView();
            this.mViewMode = 2;
        }
        if (!recycled) {
            prepareView(content);
            addView(content);
        }
        View view = this.mView;
        if (view != content) {
            removeView(view);
            this.mView = content;
        }
    }

    private void inflateAsync(RemoteViews remoteViews) {
        this.mRemoteContext = getRemoteContextEnsuringCorrectCachedApkPath();
        int layoutId = remoteViews.getLayoutId();
        CancellationSignal cancellationSignal = this.mLastExecutionSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
        if (!this.mColorMappingChanged && remoteViews.canRecycleView(this.mView)) {
            try {
                this.mLastExecutionSignal = remoteViews.reapplyAsync(this.mContext, this.mView, this.mAsyncExecutor, new ViewApplyListener(remoteViews, layoutId, true), this.mInteractionHandler, this.mCurrentSize, this.mColorResources);
            } catch (Exception e) {
            }
        }
        if (this.mLastExecutionSignal == null) {
            this.mLastExecutionSignal = remoteViews.applyAsync(this.mContext, this, this.mAsyncExecutor, new ViewApplyListener(remoteViews, layoutId, false), this.mInteractionHandler, this.mCurrentSize, this.mColorResources);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ViewApplyListener implements RemoteViews.OnViewAppliedListener {
        private final boolean mIsReapply;
        private final int mLayoutId;
        private final RemoteViews mViews;

        ViewApplyListener(RemoteViews views, int layoutId, boolean isReapply) {
            this.mViews = views;
            this.mLayoutId = layoutId;
            this.mIsReapply = isReapply;
        }

        @Override // android.widget.RemoteViews.OnViewAppliedListener
        public void onViewApplied(View v) {
            AppWidgetHostView.this.mViewMode = 1;
            AppWidgetHostView.this.applyContent(v, this.mIsReapply, null);
            AppWidgetHostView appWidgetHostView = AppWidgetHostView.this;
            appWidgetHostView.mLastInflatedRemoteViewsId = this.mViews.computeUniqueId(appWidgetHostView.mLastInflatedRemoteViews);
            AppWidgetHostView.this.restoreInstanceState();
            AppWidgetHostView.this.mLastExecutionSignal = null;
        }

        @Override // android.widget.RemoteViews.OnViewAppliedListener
        public void onError(Exception e) {
            if (this.mIsReapply) {
                AppWidgetHostView appWidgetHostView = AppWidgetHostView.this;
                RemoteViews remoteViews = this.mViews;
                Context context = appWidgetHostView.mContext;
                AppWidgetHostView appWidgetHostView2 = AppWidgetHostView.this;
                appWidgetHostView.mLastExecutionSignal = remoteViews.applyAsync(context, appWidgetHostView2, appWidgetHostView2.mAsyncExecutor, new ViewApplyListener(this.mViews, this.mLayoutId, false), AppWidgetHostView.this.mInteractionHandler, AppWidgetHostView.this.mCurrentSize);
            } else {
                AppWidgetHostView.this.applyContent(null, false, e);
            }
            AppWidgetHostView.this.mLastExecutionSignal = null;
        }
    }

    @Override // android.appwidget.AppWidgetHost.AppWidgetHostListener
    public void onViewDataChanged(int viewId) {
        View v = findViewById(viewId);
        if (v != null && (v instanceof AdapterView)) {
            AdapterView<?> adapterView = (AdapterView) v;
            Adapter adapter = adapterView.getAdapter();
            if (adapter instanceof BaseAdapter) {
                BaseAdapter baseAdapter = (BaseAdapter) adapter;
                baseAdapter.notifyDataSetChanged();
            } else if (adapter == null && (adapterView instanceof RemoteViewsAdapter.RemoteAdapterConnectionCallback)) {
                ((RemoteViewsAdapter.RemoteAdapterConnectionCallback) adapterView).deferNotifyDataSetChanged();
            }
        }
    }

    protected Context getRemoteContextEnsuringCorrectCachedApkPath() {
        try {
            ApplicationInfo expectedAppInfo = this.mInfo.providerInfo.applicationInfo;
            LoadedApk.checkAndUpdateApkPaths(expectedAppInfo);
            Context newContext = this.mContext.createApplicationContext(this.mInfo.providerInfo.applicationInfo, 4);
            RemoteViews.ColorResources colorResources = this.mColorResources;
            if (colorResources != null) {
                colorResources.apply(newContext);
            }
            return newContext;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(TAG, "Package name " + this.mInfo.providerInfo.packageName + " not found");
            return this.mContext;
        } catch (NullPointerException e2) {
            Log.m109e(TAG, "Error trying to create the remote context.", e2);
            return this.mContext;
        }
    }

    protected void prepareView(View view) {
        FrameLayout.LayoutParams requested = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (requested == null) {
            requested = new FrameLayout.LayoutParams(-1, -1);
        }
        requested.gravity = 17;
        view.setLayoutParams(requested);
    }

    protected View getDefaultView() {
        View defaultView = null;
        Exception exception = null;
        try {
            if (this.mInfo != null) {
                Context theirContext = getRemoteContextEnsuringCorrectCachedApkPath();
                this.mRemoteContext = theirContext;
                LayoutInflater inflater = ((LayoutInflater) theirContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).cloneInContext(theirContext);
                inflater.setFilter(INFLATER_FILTER);
                AppWidgetManager manager = AppWidgetManager.getInstance(this.mContext);
                Bundle options = manager.getAppWidgetOptions(this.mAppWidgetId);
                int layoutId = this.mInfo.initialLayout;
                if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY)) {
                    int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY);
                    if (category == 2) {
                        int kgLayoutId = this.mInfo.initialKeyguardLayout;
                        layoutId = kgLayoutId == 0 ? layoutId : kgLayoutId;
                    }
                }
                defaultView = inflater.inflate(layoutId, (ViewGroup) this, false);
                if (!(defaultView instanceof AdapterView)) {
                    defaultView.setOnClickListener(new View.OnClickListener() { // from class: android.appwidget.AppWidgetHostView$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            AppWidgetHostView.this.onDefaultViewClicked(view);
                        }
                    });
                }
            } else {
                Log.m104w(TAG, "can't inflate defaultView because mInfo is missing");
            }
        } catch (RuntimeException e) {
            exception = e;
        }
        if (exception != null) {
            Log.m103w(TAG, "Error inflating AppWidget " + this.mInfo, exception);
        }
        if (defaultView == null) {
            return getErrorView();
        }
        return defaultView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDefaultViewClicked(View view) {
        if (this.mInfo != null) {
            LauncherApps launcherApps = (LauncherApps) getContext().getSystemService(LauncherApps.class);
            List<LauncherActivityInfo> activities = launcherApps.getActivityList(this.mInfo.provider.getPackageName(), this.mInfo.getProfile());
            if (!activities.isEmpty()) {
                LauncherActivityInfo ai = activities.get(0);
                launcherApps.startMainActivity(ai.getComponentName(), ai.getUser(), RemoteViews.getSourceBounds(view), null);
            }
        }
    }

    protected View getErrorView() {
        TextView tv = new TextView(this.mContext);
        tv.setText(C4057R.string.gadget_host_error_inflating);
        tv.setBackgroundColor(Color.argb(127, 0, 0, 0));
        return tv;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        info.setClassName(AppWidgetHostView.class.getName());
    }

    public ActivityOptions createSharedElementActivityOptions(int[] sharedViewIds, String[] sharedViewNames, Intent fillInIntent) {
        Context parentContext = getContext();
        while ((parentContext instanceof ContextWrapper) && !(parentContext instanceof Activity)) {
            parentContext = ((ContextWrapper) parentContext).getBaseContext();
        }
        if (parentContext instanceof Activity) {
            List<Pair<View, String>> sharedElements = new ArrayList<>();
            Bundle extras = new Bundle();
            for (int i = 0; i < sharedViewIds.length; i++) {
                View view = findViewById(sharedViewIds[i]);
                if (view != null) {
                    sharedElements.add(Pair.create(view, sharedViewNames[i]));
                    extras.putParcelable(sharedViewNames[i], RemoteViews.getSourceBounds(view));
                }
            }
            if (sharedElements.isEmpty()) {
                return null;
            }
            fillInIntent.putExtra(RemoteViews.EXTRA_SHARED_ELEMENT_BOUNDS, extras);
            ActivityOptions opts = ActivityOptions.makeSceneTransitionAnimation((Activity) parentContext, (Pair[]) sharedElements.toArray(new Pair[sharedElements.size()]));
            opts.setPendingIntentLaunchFlags(268435456);
            return opts;
        }
        return null;
    }

    private RemoteViews.InteractionHandler getHandler(final RemoteViews.InteractionHandler handler) {
        return new RemoteViews.InteractionHandler() { // from class: android.appwidget.AppWidgetHostView$$ExternalSyntheticLambda0
            @Override // android.widget.RemoteViews.InteractionHandler
            public final boolean onInteraction(View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse remoteResponse) {
                boolean lambda$getHandler$1;
                lambda$getHandler$1 = AppWidgetHostView.this.lambda$getHandler$1(handler, view, pendingIntent, remoteResponse);
                return lambda$getHandler$1;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$getHandler$1(RemoteViews.InteractionHandler handler, View view, PendingIntent pendingIntent, RemoteViews.RemoteResponse response) {
        AppWidgetManager.getInstance(this.mContext).noteAppWidgetTapped(this.mAppWidgetId);
        if (handler != null) {
            return handler.onInteraction(view, pendingIntent, response);
        }
        return RemoteViews.startPendingIntent(view, pendingIntent, response.getLaunchOptions(view));
    }

    public void setColorResources(SparseIntArray colorMapping) {
        RemoteViews.ColorResources colorResources = this.mColorResources;
        if (colorResources != null && isSameColorMapping(colorResources.getColorMapping(), colorMapping)) {
            return;
        }
        setColorResources(RemoteViews.ColorResources.create(this.mContext, colorMapping));
    }

    public void setColorResources(RemoteViews.ColorResources colorResources) {
        if (colorResources == this.mColorResources) {
            return;
        }
        this.mColorResources = colorResources;
        this.mColorMappingChanged = true;
        this.mViewMode = 0;
        reapplyLastRemoteViews();
    }

    private boolean isSameColorMapping(SparseIntArray oldColors, SparseIntArray newColors) {
        if (oldColors.size() != newColors.size()) {
            return false;
        }
        for (int i = 0; i < oldColors.size(); i++) {
            if (oldColors.keyAt(i) != newColors.keyAt(i) || oldColors.valueAt(i) != newColors.valueAt(i)) {
                return false;
            }
        }
        return true;
    }

    public void resetColorResources() {
        if (this.mColorResources != null) {
            this.mColorResources = null;
            this.mColorMappingChanged = true;
            this.mViewMode = 0;
            reapplyLastRemoteViews();
        }
    }
}
