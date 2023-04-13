package android.widget;

import android.app.IServiceConnection;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.p001pm.ApplicationInfo;
import android.content.res.Configuration;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.RemoteViewsAdapter;
import com.android.internal.C4057R;
import com.android.internal.widget.IRemoteViewsFactory;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executor;
/* loaded from: classes4.dex */
public class RemoteViewsAdapter extends BaseAdapter implements Handler.Callback {
    private static final int CACHE_RESET_CONFIG_FLAGS = -1073737216;
    private static final int DEFAULT_CACHE_SIZE = 40;
    private static final int DEFAULT_LOADING_VIEW_HEIGHT = 50;
    static final int MSG_LOAD_NEXT_ITEM = 3;
    private static final int MSG_MAIN_HANDLER_COMMIT_METADATA = 1;
    private static final int MSG_MAIN_HANDLER_REMOTE_ADAPTER_CONNECTED = 3;
    private static final int MSG_MAIN_HANDLER_REMOTE_ADAPTER_DISCONNECTED = 4;
    private static final int MSG_MAIN_HANDLER_REMOTE_VIEWS_LOADED = 5;
    private static final int MSG_MAIN_HANDLER_SUPER_NOTIFY_DATA_SET_CHANGED = 2;
    static final int MSG_NOTIFY_DATA_SET_CHANGED = 2;
    static final int MSG_REQUEST_BIND = 1;
    static final int MSG_UNBIND_SERVICE = 4;
    private static final int REMOTE_VIEWS_CACHE_DURATION = 5000;
    private static final String TAG = "RemoteViewsAdapter";
    private static final int UNBIND_SERVICE_DELAY = 5000;
    private static Handler sCacheRemovalQueue;
    private static HandlerThread sCacheRemovalThread;
    private static final HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> sCachedRemoteViewsCaches = new HashMap<>();
    private static final HashMap<RemoteViewsCacheKey, Runnable> sRemoteViewsCacheRemoveRunnables = new HashMap<>();
    private final int mAppWidgetId;
    private final Executor mAsyncViewLoadExecutor;
    private final FixedSizeRemoteViewsCache mCache;
    private final RemoteAdapterConnectionCallback mCallback;
    private final Context mContext;
    private boolean mDataReady;
    private final Intent mIntent;
    private ApplicationInfo mLastRemoteViewAppInfo;
    private final Handler mMainHandler;
    private final boolean mOnLightBackground;
    private RemoteViews.InteractionHandler mRemoteViewsInteractionHandler;
    private RemoteViewsFrameLayoutRefSet mRequestedViews;
    private final RemoteServiceHandler mServiceHandler;
    private int mVisibleWindowLowerBound;
    private int mVisibleWindowUpperBound;
    private final HandlerThread mWorkerThread;

    /* loaded from: classes4.dex */
    public interface RemoteAdapterConnectionCallback {
        void deferNotifyDataSetChanged();

        boolean onRemoteAdapterConnected();

        void onRemoteAdapterDisconnected();

        void setRemoteViewsAdapter(Intent intent, boolean z);
    }

    /* loaded from: classes4.dex */
    public static class AsyncRemoteAdapterAction implements Runnable {
        private final RemoteAdapterConnectionCallback mCallback;
        private final Intent mIntent;

        public AsyncRemoteAdapterAction(RemoteAdapterConnectionCallback callback, Intent intent) {
            this.mCallback = callback;
            this.mIntent = intent;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mCallback.setRemoteViewsAdapter(this.mIntent, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class RemoteServiceHandler extends Handler implements ServiceConnection {
        private final WeakReference<RemoteViewsAdapter> mAdapter;
        private boolean mBindRequested;
        private final Context mContext;
        private boolean mNotifyDataSetChangedPending;
        private IRemoteViewsFactory mRemoteViewsFactory;

        RemoteServiceHandler(Looper workerLooper, RemoteViewsAdapter adapter, Context context) {
            super(workerLooper);
            this.mNotifyDataSetChangedPending = false;
            this.mBindRequested = false;
            this.mAdapter = new WeakReference<>(adapter);
            this.mContext = context;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mRemoteViewsFactory = IRemoteViewsFactory.Stub.asInterface(service);
            enqueueDeferredUnbindServiceMessage();
            RemoteViewsAdapter adapter = this.mAdapter.get();
            if (adapter == null) {
                return;
            }
            if (this.mNotifyDataSetChangedPending) {
                this.mNotifyDataSetChangedPending = false;
                Message msg = Message.obtain(this, 2);
                handleMessage(msg);
                msg.recycle();
            } else if (!sendNotifyDataSetChange(false)) {
            } else {
                adapter.updateTemporaryMetaData(this.mRemoteViewsFactory);
                adapter.mMainHandler.sendEmptyMessage(1);
                adapter.mMainHandler.sendEmptyMessage(3);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            this.mRemoteViewsFactory = null;
            RemoteViewsAdapter adapter = this.mAdapter.get();
            if (adapter != null) {
                adapter.mMainHandler.sendEmptyMessage(4);
            }
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            int newCount;
            int[] visibleWindow;
            RemoteViewsAdapter adapter = this.mAdapter.get();
            switch (msg.what) {
                case 1:
                    if (adapter == null || this.mRemoteViewsFactory != null) {
                        enqueueDeferredUnbindServiceMessage();
                    }
                    if (this.mBindRequested) {
                        return;
                    }
                    IServiceConnection sd = this.mContext.getServiceDispatcher(this, this, (long) InputDevice.SOURCE_HDMI);
                    Intent intent = (Intent) msg.obj;
                    int appWidgetId = msg.arg1;
                    try {
                        this.mBindRequested = AppWidgetManager.getInstance(this.mContext).bindRemoteViewsService(this.mContext, appWidgetId, intent, sd, InputDevice.SOURCE_HDMI);
                        return;
                    } catch (Exception e) {
                        Log.m110e(RemoteViewsAdapter.TAG, "Failed to bind remoteViewsService: " + e.getMessage());
                        return;
                    }
                case 2:
                    enqueueDeferredUnbindServiceMessage();
                    if (adapter == null) {
                        return;
                    }
                    if (this.mRemoteViewsFactory == null) {
                        this.mNotifyDataSetChangedPending = true;
                        adapter.requestBindService();
                        return;
                    } else if (!sendNotifyDataSetChange(true)) {
                        return;
                    } else {
                        synchronized (adapter.mCache) {
                            adapter.mCache.reset();
                        }
                        adapter.updateTemporaryMetaData(this.mRemoteViewsFactory);
                        synchronized (adapter.mCache.getTemporaryMetaData()) {
                            newCount = adapter.mCache.getTemporaryMetaData().count;
                            visibleWindow = adapter.getVisibleWindow(newCount);
                        }
                        for (int position : visibleWindow) {
                            if (position < newCount) {
                                adapter.updateRemoteViews(this.mRemoteViewsFactory, position, false);
                            }
                        }
                        adapter.mMainHandler.sendEmptyMessage(1);
                        adapter.mMainHandler.sendEmptyMessage(2);
                        return;
                    }
                case 3:
                    if (adapter == null || this.mRemoteViewsFactory == null) {
                        return;
                    }
                    removeMessages(4);
                    int position2 = adapter.mCache.getNextIndexToLoad();
                    if (position2 > -1) {
                        adapter.updateRemoteViews(this.mRemoteViewsFactory, position2, true);
                        sendEmptyMessage(3);
                        return;
                    }
                    enqueueDeferredUnbindServiceMessage();
                    return;
                case 4:
                    unbindNow();
                    return;
                default:
                    return;
            }
        }

        protected void unbindNow() {
            if (this.mBindRequested) {
                this.mBindRequested = false;
                this.mContext.unbindService(this);
            }
            this.mRemoteViewsFactory = null;
        }

        private boolean sendNotifyDataSetChange(boolean always) {
            if (!always) {
                try {
                    if (this.mRemoteViewsFactory.isCreated()) {
                        return true;
                    }
                } catch (RemoteException | RuntimeException e) {
                    Log.m110e(RemoteViewsAdapter.TAG, "Error in updateNotifyDataSetChanged(): " + e.getMessage());
                    return false;
                }
            }
            this.mRemoteViewsFactory.onDataSetChanged();
            return true;
        }

        private void enqueueDeferredUnbindServiceMessage() {
            removeMessages(4);
            sendEmptyMessageDelayed(4, 5000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class RemoteViewsFrameLayout extends AppWidgetHostView {
        public int cacheIndex;
        private final FixedSizeRemoteViewsCache mCache;

        public RemoteViewsFrameLayout(Context context, FixedSizeRemoteViewsCache cache) {
            super(context);
            this.cacheIndex = -1;
            this.mCache = cache;
        }

        public void onRemoteViewsLoaded(RemoteViews view, RemoteViews.InteractionHandler handler, boolean forceApplyAsync) {
            setInteractionHandler(handler);
            applyRemoteViews(view, forceApplyAsync || (view != null && view.prefersAsyncApply()));
        }

        @Override // android.appwidget.AppWidgetHostView
        protected View getDefaultView() {
            int viewHeight = this.mCache.getMetaData().getLoadingTemplate(getContext()).defaultHeight;
            TextView loadingTextView = (TextView) LayoutInflater.from(getContext()).inflate(C4057R.layout.remote_views_adapter_default_loading_view, (ViewGroup) this, false);
            loadingTextView.setHeight(viewHeight);
            return loadingTextView;
        }

        @Override // android.appwidget.AppWidgetHostView
        protected Context getRemoteContextEnsuringCorrectCachedApkPath() {
            return null;
        }

        @Override // android.appwidget.AppWidgetHostView
        protected View getErrorView() {
            return getDefaultView();
        }
    }

    /* loaded from: classes4.dex */
    private class RemoteViewsFrameLayoutRefSet extends SparseArray<ArrayList<RemoteViewsFrameLayout>> {
        private RemoteViewsFrameLayoutRefSet() {
        }

        public void add(int position, RemoteViewsFrameLayout layout) {
            ArrayList<RemoteViewsFrameLayout> refs = get(position);
            if (refs == null) {
                refs = new ArrayList<>();
                put(position, refs);
            }
            layout.cacheIndex = position;
            refs.add(layout);
        }

        public void notifyOnRemoteViewsLoaded(int position, RemoteViews view) {
            ArrayList<RemoteViewsFrameLayout> refs;
            if (view != null && (refs = removeReturnOld(position)) != null) {
                Iterator<RemoteViewsFrameLayout> it = refs.iterator();
                while (it.hasNext()) {
                    RemoteViewsFrameLayout ref = it.next();
                    ref.onRemoteViewsLoaded(view, RemoteViewsAdapter.this.mRemoteViewsInteractionHandler, true);
                }
            }
        }

        public void removeView(RemoteViewsFrameLayout rvfl) {
            if (rvfl.cacheIndex < 0) {
                return;
            }
            ArrayList<RemoteViewsFrameLayout> refs = get(rvfl.cacheIndex);
            if (refs != null) {
                refs.remove(rvfl);
            }
            rvfl.cacheIndex = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class RemoteViewsMetaData {
        int count;
        boolean hasStableIds;
        LoadingViewTemplate loadingTemplate;
        private final SparseIntArray mTypeIdIndexMap = new SparseIntArray();
        int viewTypeCount;

        public RemoteViewsMetaData() {
            reset();
        }

        public void set(RemoteViewsMetaData d) {
            synchronized (d) {
                this.count = d.count;
                this.viewTypeCount = d.viewTypeCount;
                this.hasStableIds = d.hasStableIds;
                this.loadingTemplate = d.loadingTemplate;
            }
        }

        public void reset() {
            this.count = 0;
            this.viewTypeCount = 1;
            this.hasStableIds = true;
            this.loadingTemplate = null;
            this.mTypeIdIndexMap.clear();
        }

        public int getMappedViewType(int typeId) {
            int mappedTypeId = this.mTypeIdIndexMap.get(typeId, -1);
            if (mappedTypeId == -1) {
                int mappedTypeId2 = this.mTypeIdIndexMap.size() + 1;
                this.mTypeIdIndexMap.put(typeId, mappedTypeId2);
                return mappedTypeId2;
            }
            return mappedTypeId;
        }

        public boolean isViewTypeInRange(int typeId) {
            int mappedType = getMappedViewType(typeId);
            return mappedType < this.viewTypeCount;
        }

        public synchronized LoadingViewTemplate getLoadingTemplate(Context context) {
            if (this.loadingTemplate == null) {
                this.loadingTemplate = new LoadingViewTemplate(null, context);
            }
            return this.loadingTemplate;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class RemoteViewsIndexMetaData {
        long itemId;
        int typeId;

        public RemoteViewsIndexMetaData(RemoteViews v, long itemId) {
            set(v, itemId);
        }

        public void set(RemoteViews v, long id) {
            this.itemId = id;
            if (v != null) {
                this.typeId = v.getLayoutId();
            } else {
                this.typeId = 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class FixedSizeRemoteViewsCache {
        private static final float sMaxCountSlackPercent = 0.75f;
        private static final int sMaxMemoryLimitInBytes = 2097152;
        private final Configuration mConfiguration;
        private final int mMaxCount;
        private final int mMaxCountSlack;
        private final RemoteViewsMetaData mMetaData = new RemoteViewsMetaData();
        private final RemoteViewsMetaData mTemporaryMetaData = new RemoteViewsMetaData();
        private final SparseArray<RemoteViewsIndexMetaData> mIndexMetaData = new SparseArray<>();
        private final SparseArray<RemoteViews> mIndexRemoteViews = new SparseArray<>();
        private final SparseBooleanArray mIndicesToLoad = new SparseBooleanArray();
        private int mPreloadLowerBound = 0;
        private int mPreloadUpperBound = -1;
        private int mLastRequestedIndex = -1;

        FixedSizeRemoteViewsCache(int maxCacheSize, Configuration configuration) {
            this.mMaxCount = maxCacheSize;
            this.mMaxCountSlack = Math.round((maxCacheSize / 2) * 0.75f);
            this.mConfiguration = new Configuration(configuration);
        }

        public void insert(int position, RemoteViews v, long itemId, int[] visibleWindow) {
            int trimIndex;
            if (this.mIndexRemoteViews.size() >= this.mMaxCount) {
                this.mIndexRemoteViews.remove(getFarthestPositionFrom(position, visibleWindow));
            }
            int pruneFromPosition = this.mLastRequestedIndex;
            if (pruneFromPosition <= -1) {
                pruneFromPosition = position;
            }
            while (getRemoteViewsBitmapMemoryUsage() >= 2097152 && (trimIndex = getFarthestPositionFrom(pruneFromPosition, visibleWindow)) >= 0) {
                this.mIndexRemoteViews.remove(trimIndex);
            }
            RemoteViewsIndexMetaData metaData = this.mIndexMetaData.get(position);
            if (metaData != null) {
                metaData.set(v, itemId);
            } else {
                this.mIndexMetaData.put(position, new RemoteViewsIndexMetaData(v, itemId));
            }
            this.mIndexRemoteViews.put(position, v);
        }

        public RemoteViewsMetaData getMetaData() {
            return this.mMetaData;
        }

        public RemoteViewsMetaData getTemporaryMetaData() {
            return this.mTemporaryMetaData;
        }

        public RemoteViews getRemoteViewsAt(int position) {
            return this.mIndexRemoteViews.get(position);
        }

        public RemoteViewsIndexMetaData getMetaDataAt(int position) {
            return this.mIndexMetaData.get(position);
        }

        public void commitTemporaryMetaData() {
            synchronized (this.mTemporaryMetaData) {
                synchronized (this.mMetaData) {
                    this.mMetaData.set(this.mTemporaryMetaData);
                }
            }
        }

        private int getRemoteViewsBitmapMemoryUsage() {
            int mem = 0;
            for (int i = this.mIndexRemoteViews.size() - 1; i >= 0; i--) {
                RemoteViews v = this.mIndexRemoteViews.valueAt(i);
                if (v != null) {
                    mem += v.estimateMemoryUsage();
                }
            }
            return mem;
        }

        private int getFarthestPositionFrom(int pos, int[] visibleWindow) {
            int maxDist = 0;
            int maxDistIndex = -1;
            int maxDistNotVisible = 0;
            int maxDistIndexNotVisible = -1;
            for (int i = this.mIndexRemoteViews.size() - 1; i >= 0; i--) {
                int index = this.mIndexRemoteViews.keyAt(i);
                int dist = Math.abs(index - pos);
                if (dist > maxDistNotVisible && Arrays.binarySearch(visibleWindow, index) < 0) {
                    maxDistIndexNotVisible = index;
                    maxDistNotVisible = dist;
                }
                if (dist >= maxDist) {
                    maxDistIndex = index;
                    maxDist = dist;
                }
            }
            if (maxDistIndexNotVisible > -1) {
                return maxDistIndexNotVisible;
            }
            return maxDistIndex;
        }

        public void queueRequestedPositionToLoad(int position) {
            this.mLastRequestedIndex = position;
            synchronized (this.mIndicesToLoad) {
                this.mIndicesToLoad.put(position, true);
            }
        }

        public boolean queuePositionsToBePreloadedFromRequestedPosition(int position) {
            int count;
            int i;
            int i2 = this.mPreloadLowerBound;
            if (i2 <= position && position <= (i = this.mPreloadUpperBound)) {
                int center = (i + i2) / 2;
                if (Math.abs(position - center) < this.mMaxCountSlack) {
                    return false;
                }
            }
            synchronized (this.mMetaData) {
                count = this.mMetaData.count;
            }
            synchronized (this.mIndicesToLoad) {
                for (int i3 = this.mIndicesToLoad.size() - 1; i3 >= 0; i3--) {
                    if (!this.mIndicesToLoad.valueAt(i3)) {
                        this.mIndicesToLoad.removeAt(i3);
                    }
                }
                int i4 = this.mMaxCount;
                int halfMaxCount = i4 / 2;
                int i5 = position - halfMaxCount;
                this.mPreloadLowerBound = i5;
                this.mPreloadUpperBound = position + halfMaxCount;
                int effectiveLowerBound = Math.max(0, i5);
                int effectiveUpperBound = Math.min(this.mPreloadUpperBound, count - 1);
                for (int i6 = effectiveLowerBound; i6 <= effectiveUpperBound; i6++) {
                    if (this.mIndexRemoteViews.indexOfKey(i6) < 0 && !this.mIndicesToLoad.get(i6)) {
                        this.mIndicesToLoad.put(i6, false);
                    }
                }
            }
            return true;
        }

        public int getNextIndexToLoad() {
            synchronized (this.mIndicesToLoad) {
                int index = this.mIndicesToLoad.indexOfValue(true);
                if (index < 0) {
                    index = this.mIndicesToLoad.indexOfValue(false);
                }
                if (index < 0) {
                    return -1;
                }
                int key = this.mIndicesToLoad.keyAt(index);
                this.mIndicesToLoad.removeAt(index);
                return key;
            }
        }

        public boolean containsRemoteViewAt(int position) {
            return this.mIndexRemoteViews.indexOfKey(position) >= 0;
        }

        public boolean containsMetaDataAt(int position) {
            return this.mIndexMetaData.indexOfKey(position) >= 0;
        }

        public void reset() {
            this.mPreloadLowerBound = 0;
            this.mPreloadUpperBound = -1;
            this.mLastRequestedIndex = -1;
            this.mIndexRemoteViews.clear();
            this.mIndexMetaData.clear();
            synchronized (this.mIndicesToLoad) {
                this.mIndicesToLoad.clear();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class RemoteViewsCacheKey {
        final Intent.FilterComparison filter;
        final int widgetId;

        RemoteViewsCacheKey(Intent.FilterComparison filter, int widgetId) {
            this.filter = filter;
            this.widgetId = widgetId;
        }

        public boolean equals(Object o) {
            if (o instanceof RemoteViewsCacheKey) {
                RemoteViewsCacheKey other = (RemoteViewsCacheKey) o;
                return other.filter.equals(this.filter) && other.widgetId == this.widgetId;
            }
            return false;
        }

        public int hashCode() {
            Intent.FilterComparison filterComparison = this.filter;
            return (filterComparison == null ? 0 : filterComparison.hashCode()) ^ (this.widgetId << 2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x00d7 A[Catch: all -> 0x00dc, TryCatch #1 {, blocks: (B:12:0x008e, B:14:0x009e, B:17:0x00ad, B:18:0x00b9, B:28:0x00d3, B:30:0x00d7, B:31:0x00da, B:27:0x00ca, B:19:0x00ba, B:21:0x00c2, B:22:0x00c5), top: B:38:0x008e }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public RemoteViewsAdapter(Context context, Intent intent, RemoteAdapterConnectionCallback callback, boolean useAsyncLoader) {
        this.mDataReady = false;
        this.mContext = context;
        this.mIntent = intent;
        if (intent == null) {
            throw new IllegalArgumentException("Non-null Intent must be specified.");
        }
        int intExtra = intent.getIntExtra("remoteAdapterAppWidgetId", -1);
        this.mAppWidgetId = intExtra;
        this.mRequestedViews = new RemoteViewsFrameLayoutRefSet();
        this.mOnLightBackground = intent.getBooleanExtra("remoteAdapterOnLightBackground", false);
        intent.removeExtra("remoteAdapterAppWidgetId");
        intent.removeExtra("remoteAdapterOnLightBackground");
        HandlerThread handlerThread = new HandlerThread("RemoteViewsCache-loader");
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mMainHandler = new Handler(Looper.myLooper(), this);
        this.mServiceHandler = new RemoteServiceHandler(handlerThread.getLooper(), this, context.getApplicationContext());
        this.mAsyncViewLoadExecutor = useAsyncLoader ? new HandlerThreadExecutor(handlerThread) : null;
        this.mCallback = callback;
        if (sCacheRemovalThread == null) {
            HandlerThread handlerThread2 = new HandlerThread("RemoteViewsAdapter-cachePruner");
            sCacheRemovalThread = handlerThread2;
            handlerThread2.start();
            sCacheRemovalQueue = new Handler(sCacheRemovalThread.getLooper());
        }
        RemoteViewsCacheKey key = new RemoteViewsCacheKey(new Intent.FilterComparison(intent), intExtra);
        HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> hashMap = sCachedRemoteViewsCaches;
        synchronized (hashMap) {
            FixedSizeRemoteViewsCache cache = hashMap.get(key);
            Configuration config = context.getResources().getConfiguration();
            if (cache != null && (cache.mConfiguration.diff(config) & CACHE_RESET_CONFIG_FLAGS) == 0) {
                FixedSizeRemoteViewsCache fixedSizeRemoteViewsCache = hashMap.get(key);
                this.mCache = fixedSizeRemoteViewsCache;
                synchronized (fixedSizeRemoteViewsCache.mMetaData) {
                    if (fixedSizeRemoteViewsCache.mMetaData.count > 0) {
                        this.mDataReady = true;
                    }
                }
                if (!this.mDataReady) {
                    requestBindService();
                }
            }
            this.mCache = new FixedSizeRemoteViewsCache(40, config);
            if (!this.mDataReady) {
            }
        }
    }

    protected void finalize() throws Throwable {
        try {
            this.mServiceHandler.unbindNow();
            this.mWorkerThread.quit();
        } finally {
            super.finalize();
        }
    }

    public boolean isDataReady() {
        return this.mDataReady;
    }

    public void setRemoteViewsInteractionHandler(RemoteViews.InteractionHandler handler) {
        this.mRemoteViewsInteractionHandler = handler;
    }

    public void saveRemoteViewsCache() {
        int metaDataCount;
        int numRemoteViewsCached;
        final RemoteViewsCacheKey key = new RemoteViewsCacheKey(new Intent.FilterComparison(this.mIntent), this.mAppWidgetId);
        HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> hashMap = sCachedRemoteViewsCaches;
        synchronized (hashMap) {
            HashMap<RemoteViewsCacheKey, Runnable> hashMap2 = sRemoteViewsCacheRemoveRunnables;
            if (hashMap2.containsKey(key)) {
                sCacheRemovalQueue.removeCallbacks(hashMap2.get(key));
                hashMap2.remove(key);
            }
            synchronized (this.mCache.mMetaData) {
                metaDataCount = this.mCache.mMetaData.count;
            }
            synchronized (this.mCache) {
                numRemoteViewsCached = this.mCache.mIndexRemoteViews.size();
            }
            if (metaDataCount > 0 && numRemoteViewsCached > 0) {
                hashMap.put(key, this.mCache);
            }
            Runnable r = new Runnable() { // from class: android.widget.RemoteViewsAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RemoteViewsAdapter.lambda$saveRemoteViewsCache$0(RemoteViewsAdapter.RemoteViewsCacheKey.this);
                }
            };
            hashMap2.put(key, r);
            sCacheRemovalQueue.postDelayed(r, 5000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$saveRemoteViewsCache$0(RemoteViewsCacheKey key) {
        HashMap<RemoteViewsCacheKey, FixedSizeRemoteViewsCache> hashMap = sCachedRemoteViewsCaches;
        synchronized (hashMap) {
            hashMap.remove(key);
            sRemoteViewsCacheRemoveRunnables.remove(key);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTemporaryMetaData(IRemoteViewsFactory factory) {
        RemoteViews firstView;
        try {
            boolean hasStableIds = factory.hasStableIds();
            int viewTypeCount = factory.getViewTypeCount();
            int count = factory.getCount();
            LoadingViewTemplate loadingTemplate = new LoadingViewTemplate(factory.getLoadingView(), this.mContext);
            if (count > 0 && loadingTemplate.remoteViews == null && (firstView = factory.getViewAt(0)) != null) {
                loadingTemplate.loadFirstViewHeight(firstView, this.mContext, new HandlerThreadExecutor(this.mWorkerThread));
            }
            RemoteViewsMetaData tmpMetaData = this.mCache.getTemporaryMetaData();
            synchronized (tmpMetaData) {
                tmpMetaData.hasStableIds = hasStableIds;
                tmpMetaData.viewTypeCount = viewTypeCount + 1;
                tmpMetaData.count = count;
                tmpMetaData.loadingTemplate = loadingTemplate;
            }
        } catch (RemoteException | RuntimeException e) {
            Log.m110e(TAG, "Error in updateMetaData: " + e.getMessage());
            synchronized (this.mCache.getMetaData()) {
                this.mCache.getMetaData().reset();
                synchronized (this.mCache) {
                    this.mCache.reset();
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRemoteViews(IRemoteViewsFactory factory, int position, boolean notifyWhenLoaded) {
        boolean viewTypeInRange;
        int cacheCount;
        try {
            RemoteViews remoteViews = factory.getViewAt(position);
            long itemId = factory.getItemId(position);
            if (remoteViews == null) {
                throw new RuntimeException("Null remoteViews");
            }
            if (remoteViews.mApplication != null) {
                ApplicationInfo applicationInfo = this.mLastRemoteViewAppInfo;
                if (applicationInfo != null && remoteViews.hasSameAppInfo(applicationInfo)) {
                    remoteViews.mApplication = this.mLastRemoteViewAppInfo;
                } else {
                    this.mLastRemoteViewAppInfo = remoteViews.mApplication;
                }
            }
            int layoutId = remoteViews.getLayoutId();
            RemoteViewsMetaData metaData = this.mCache.getMetaData();
            synchronized (metaData) {
                viewTypeInRange = metaData.isViewTypeInRange(layoutId);
                cacheCount = this.mCache.mMetaData.count;
            }
            synchronized (this.mCache) {
                if (viewTypeInRange) {
                    int[] visibleWindow = getVisibleWindow(cacheCount);
                    this.mCache.insert(position, remoteViews, itemId, visibleWindow);
                    if (notifyWhenLoaded) {
                        Message.obtain(this.mMainHandler, 5, position, 0, remoteViews).sendToTarget();
                    }
                } else {
                    Log.m110e(TAG, "Error: widget's RemoteViewsFactory returns more view types than  indicated by getViewTypeCount() ");
                }
            }
        } catch (RemoteException | RuntimeException e) {
            Log.m110e(TAG, "Error in updateRemoteViews(" + position + "): " + e.getMessage());
        }
    }

    public Intent getRemoteViewsServiceIntent() {
        return this.mIntent;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        int i;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            i = metaData.count;
        }
        return i;
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return null;
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        synchronized (this.mCache) {
            if (this.mCache.containsMetaDataAt(position)) {
                return this.mCache.getMetaDataAt(position).itemId;
            }
            return 0L;
        }
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int position) {
        int mappedViewType;
        synchronized (this.mCache) {
            if (this.mCache.containsMetaDataAt(position)) {
                int typeId = this.mCache.getMetaDataAt(position).typeId;
                RemoteViewsMetaData metaData = this.mCache.getMetaData();
                synchronized (metaData) {
                    mappedViewType = metaData.getMappedViewType(typeId);
                }
                return mappedViewType;
            }
            return 0;
        }
    }

    public void setVisibleRangeHint(int lowerBound, int upperBound) {
        this.mVisibleWindowLowerBound = lowerBound;
        this.mVisibleWindowUpperBound = upperBound;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        RemoteViewsFrameLayout layout;
        synchronized (this.mCache) {
            RemoteViews rv = this.mCache.getRemoteViewsAt(position);
            boolean isInCache = rv != null;
            boolean hasNewItems = false;
            if (convertView != null && (convertView instanceof RemoteViewsFrameLayout)) {
                this.mRequestedViews.removeView((RemoteViewsFrameLayout) convertView);
            }
            if (!isInCache) {
                requestBindService();
            } else {
                hasNewItems = this.mCache.queuePositionsToBePreloadedFromRequestedPosition(position);
            }
            if (convertView instanceof RemoteViewsFrameLayout) {
                layout = (RemoteViewsFrameLayout) convertView;
            } else {
                layout = new RemoteViewsFrameLayout(parent.getContext(), this.mCache);
                layout.setExecutor(this.mAsyncViewLoadExecutor);
                layout.setOnLightBackground(this.mOnLightBackground);
            }
            if (isInCache) {
                layout.onRemoteViewsLoaded(rv, this.mRemoteViewsInteractionHandler, false);
                if (hasNewItems) {
                    this.mServiceHandler.sendEmptyMessage(3);
                }
            } else {
                layout.onRemoteViewsLoaded(this.mCache.getMetaData().getLoadingTemplate(this.mContext).remoteViews, this.mRemoteViewsInteractionHandler, false);
                this.mRequestedViews.add(position, layout);
                this.mCache.queueRequestedPositionToLoad(position);
                this.mServiceHandler.sendEmptyMessage(3);
            }
        }
        return layout;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        int i;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            i = metaData.viewTypeCount;
        }
        return i;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean hasStableIds() {
        boolean z;
        RemoteViewsMetaData metaData = this.mCache.getMetaData();
        synchronized (metaData) {
            z = metaData.hasStableIds;
        }
        return z;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public boolean isEmpty() {
        return getCount() <= 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int[] getVisibleWindow(int count) {
        int[] window;
        int lower = this.mVisibleWindowLowerBound;
        int upper = this.mVisibleWindowUpperBound;
        if ((lower == 0 && upper == 0) || lower < 0 || upper < 0) {
            return new int[0];
        }
        if (lower <= upper) {
            window = new int[(upper + 1) - lower];
            int i = lower;
            int j = 0;
            while (i <= upper) {
                window[j] = i;
                i++;
                j++;
            }
        } else {
            int count2 = Math.max(count, lower);
            window = new int[(count2 - lower) + upper + 1];
            int j2 = 0;
            int i2 = 0;
            while (i2 <= upper) {
                window[j2] = i2;
                i2++;
                j2++;
            }
            int i3 = lower;
            while (i3 < count2) {
                window[j2] = i3;
                i3++;
                j2++;
            }
        }
        return window;
    }

    @Override // android.widget.BaseAdapter
    public void notifyDataSetChanged() {
        this.mServiceHandler.removeMessages(4);
        this.mServiceHandler.sendEmptyMessage(2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override // android.p008os.Handler.Callback
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                this.mCache.commitTemporaryMetaData();
                return true;
            case 2:
                superNotifyDataSetChanged();
                return true;
            case 3:
                RemoteAdapterConnectionCallback remoteAdapterConnectionCallback = this.mCallback;
                if (remoteAdapterConnectionCallback != null) {
                    remoteAdapterConnectionCallback.onRemoteAdapterConnected();
                }
                return true;
            case 4:
                RemoteAdapterConnectionCallback remoteAdapterConnectionCallback2 = this.mCallback;
                if (remoteAdapterConnectionCallback2 != null) {
                    remoteAdapterConnectionCallback2.onRemoteAdapterDisconnected();
                }
                return true;
            case 5:
                this.mRequestedViews.notifyOnRemoteViewsLoaded(msg.arg1, (RemoteViews) msg.obj);
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestBindService() {
        this.mServiceHandler.removeMessages(4);
        Message.obtain(this.mServiceHandler, 1, this.mAppWidgetId, 0, this.mIntent).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class HandlerThreadExecutor implements Executor {
        private final HandlerThread mThread;

        HandlerThreadExecutor(HandlerThread thread) {
            this.mThread = thread;
        }

        @Override // java.util.concurrent.Executor
        public void execute(Runnable runnable) {
            if (Thread.currentThread().getId() == this.mThread.getId()) {
                runnable.run();
            } else {
                new Handler(this.mThread.getLooper()).post(runnable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class LoadingViewTemplate {
        public int defaultHeight;
        public final RemoteViews remoteViews;

        LoadingViewTemplate(RemoteViews views, Context context) {
            this.remoteViews = views;
            float density = context.getResources().getDisplayMetrics().density;
            this.defaultHeight = Math.round(50.0f * density);
        }

        public void loadFirstViewHeight(RemoteViews firstView, Context context, Executor executor) {
            firstView.applyAsync(context, new RemoteViewsFrameLayout(context, null), executor, new RemoteViews.OnViewAppliedListener() { // from class: android.widget.RemoteViewsAdapter.LoadingViewTemplate.1
                @Override // android.widget.RemoteViews.OnViewAppliedListener
                public void onViewApplied(View v) {
                    try {
                        v.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
                        LoadingViewTemplate.this.defaultHeight = v.getMeasuredHeight();
                    } catch (Exception e) {
                        onError(e);
                    }
                }

                @Override // android.widget.RemoteViews.OnViewAppliedListener
                public void onError(Exception e) {
                    Log.m103w(RemoteViewsAdapter.TAG, "Error inflating first RemoteViews", e);
                }
            });
        }
    }
}
