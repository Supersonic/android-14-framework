package android.service.autofill.augmented;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.assist.AssistStructure;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.p008os.BaseBundle;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.service.autofill.Dataset;
import android.service.autofill.FillEventHistory;
import android.service.autofill.augmented.IAugmentedAutofillService;
import android.service.autofill.augmented.PresentationParams;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAugmentedAutofillManagerClient;
import android.view.autofill.IAutofillWindowPresenter;
import android.view.inputmethod.InlineSuggestionsRequest;
import com.android.internal.util.function.DecConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class AugmentedAutofillService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.autofill.augmented.AugmentedAutofillService";
    private static final String TAG = AugmentedAutofillService.class.getSimpleName();
    static boolean sDebug = !Build.IS_USER;
    static boolean sVerbose = false;
    private SparseArray<AutofillProxy> mAutofillProxies;
    private AutofillProxy mAutofillProxyForLastRequest;
    private Handler mHandler;
    private ComponentName mServiceComponentName;

    /* loaded from: classes3.dex */
    private final class AugmentedAutofillServiceImpl extends IAugmentedAutofillService.Stub {
        private AugmentedAutofillServiceImpl() {
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onConnected(boolean debug, boolean verbose) {
            AugmentedAutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.autofill.augmented.AugmentedAutofillService$AugmentedAutofillServiceImpl$$ExternalSyntheticLambda2
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((AugmentedAutofillService) obj).handleOnConnected(((Boolean) obj2).booleanValue(), ((Boolean) obj3).booleanValue());
                }
            }, AugmentedAutofillService.this, Boolean.valueOf(debug), Boolean.valueOf(verbose)));
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onDisconnected() {
            AugmentedAutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.autofill.augmented.AugmentedAutofillService$AugmentedAutofillServiceImpl$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AugmentedAutofillService) obj).handleOnDisconnected();
                }
            }, AugmentedAutofillService.this));
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onFillRequest(int sessionId, IBinder client, int taskId, ComponentName componentName, AutofillId focusedId, AutofillValue focusedValue, long requestTime, InlineSuggestionsRequest inlineSuggestionsRequest, IFillCallback callback) {
            AugmentedAutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new DecConsumer() { // from class: android.service.autofill.augmented.AugmentedAutofillService$AugmentedAutofillServiceImpl$$ExternalSyntheticLambda3
                @Override // com.android.internal.util.function.DecConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9, Object obj10) {
                    ((AugmentedAutofillService) obj).handleOnFillRequest(((Integer) obj2).intValue(), (IBinder) obj3, ((Integer) obj4).intValue(), (ComponentName) obj5, (AutofillId) obj6, (AutofillValue) obj7, ((Long) obj8).longValue(), (InlineSuggestionsRequest) obj9, (IFillCallback) obj10);
                }
            }, AugmentedAutofillService.this, Integer.valueOf(sessionId), client, Integer.valueOf(taskId), componentName, focusedId, focusedValue, Long.valueOf(requestTime), inlineSuggestionsRequest, callback));
        }

        @Override // android.service.autofill.augmented.IAugmentedAutofillService
        public void onDestroyAllFillWindowsRequest() {
            AugmentedAutofillService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.autofill.augmented.AugmentedAutofillService$AugmentedAutofillServiceImpl$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AugmentedAutofillService) obj).handleOnDestroyAllFillWindowsRequest();
                }
            }, AugmentedAutofillService.this));
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler(Looper.getMainLooper(), null, true);
        BaseBundle.setShouldDefuse(true);
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        this.mServiceComponentName = intent.getComponent();
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return new AugmentedAutofillServiceImpl();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.autofill.augmented.AugmentedAutofillService: " + intent);
        return null;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.autofill.augmented.AugmentedAutofillService$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((AugmentedAutofillService) obj).handleOnUnbind();
            }
        }, this));
        return false;
    }

    public void onConnected() {
    }

    public final boolean requestAutofill(ComponentName activityComponent, AutofillId autofillId) {
        AutofillProxy proxy = this.mAutofillProxyForLastRequest;
        if (proxy == null || !proxy.mComponentName.equals(activityComponent) || !proxy.mFocusedId.equals(autofillId)) {
            return false;
        }
        try {
            return proxy.requestAutofill();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return false;
        }
    }

    public void onFillRequest(FillRequest request, CancellationSignal cancellationSignal, FillController controller, FillCallback callback) {
    }

    public void onDisconnected() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnConnected(boolean debug, boolean verbose) {
        if (sDebug || debug) {
            Log.m112d(TAG, "handleOnConnected(): debug=" + debug + ", verbose=" + verbose);
        }
        sDebug = debug;
        sVerbose = verbose;
        onConnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnDisconnected() {
        onDisconnected();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnFillRequest(int sessionId, IBinder client, int taskId, ComponentName componentName, AutofillId focusedId, AutofillValue focusedValue, long requestTime, InlineSuggestionsRequest inlineSuggestionsRequest, IFillCallback callback) {
        ICancellationSignal transport;
        IFillCallback iFillCallback;
        CancellationSignal cancellationSignal;
        AutofillProxy proxy;
        if (this.mAutofillProxies == null) {
            this.mAutofillProxies = new SparseArray<>();
        }
        ICancellationSignal transport2 = CancellationSignal.createTransport();
        CancellationSignal cancellationSignal2 = CancellationSignal.fromTransport(transport2);
        AutofillProxy proxy2 = this.mAutofillProxies.get(sessionId);
        if (proxy2 == null) {
            transport = transport2;
            AutofillProxy proxy3 = new AutofillProxy(sessionId, client, taskId, this.mServiceComponentName, componentName, focusedId, focusedValue, requestTime, callback, cancellationSignal2);
            this.mAutofillProxies.put(sessionId, proxy3);
            iFillCallback = callback;
            proxy = proxy3;
            cancellationSignal = cancellationSignal2;
        } else {
            transport = transport2;
            if (sDebug) {
                Log.m112d(TAG, "Reusing proxy for session " + sessionId);
            }
            iFillCallback = callback;
            cancellationSignal = cancellationSignal2;
            proxy2.update(focusedId, focusedValue, iFillCallback, cancellationSignal);
            proxy = proxy2;
        }
        try {
            iFillCallback.onCancellable(transport);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
        this.mAutofillProxyForLastRequest = proxy;
        onFillRequest(new FillRequest(proxy, inlineSuggestionsRequest), cancellationSignal, new FillController(proxy), new FillCallback(proxy));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnDestroyAllFillWindowsRequest() {
        SparseArray<AutofillProxy> sparseArray = this.mAutofillProxies;
        if (sparseArray != null) {
            int size = sparseArray.size();
            for (int i = 0; i < size; i++) {
                int sessionId = this.mAutofillProxies.keyAt(i);
                AutofillProxy proxy = this.mAutofillProxies.valueAt(i);
                if (proxy == null) {
                    Log.m104w(TAG, "No proxy for session " + sessionId);
                    return;
                }
                if (proxy.mCallback != null) {
                    try {
                        if (!proxy.mCallback.isCompleted()) {
                            proxy.mCallback.cancel();
                        }
                    } catch (Exception e) {
                        Log.m109e(TAG, "failed to check current pending request status", e);
                    }
                }
                proxy.destroy();
            }
            this.mAutofillProxies.clear();
            this.mAutofillProxyForLastRequest = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnUnbind() {
        SparseArray<AutofillProxy> sparseArray = this.mAutofillProxies;
        if (sparseArray == null) {
            if (sDebug) {
                Log.m112d(TAG, "onUnbind(): no proxy to destroy");
                return;
            }
            return;
        }
        int size = sparseArray.size();
        if (sDebug) {
            Log.m112d(TAG, "onUnbind(): destroying " + size + " proxies");
        }
        for (int i = 0; i < size; i++) {
            AutofillProxy proxy = this.mAutofillProxies.valueAt(i);
            try {
                proxy.destroy();
            } catch (Exception e) {
                Log.m104w(TAG, "error destroying " + proxy);
            }
        }
        this.mAutofillProxies = null;
        this.mAutofillProxyForLastRequest = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public final void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("Service component: ");
        pw.println(ComponentName.flattenToShortString(this.mServiceComponentName));
        SparseArray<AutofillProxy> sparseArray = this.mAutofillProxies;
        if (sparseArray != null) {
            int size = sparseArray.size();
            pw.print("Number proxies: ");
            pw.println(size);
            for (int i = 0; i < size; i++) {
                int sessionId = this.mAutofillProxies.keyAt(i);
                AutofillProxy proxy = this.mAutofillProxies.valueAt(i);
                pw.print(i);
                pw.print(") SessionId=");
                pw.print(sessionId);
                pw.println(":");
                proxy.dump("  ", pw);
            }
        }
        dump(pw, args);
    }

    protected void dump(PrintWriter pw, String[] args) {
        pw.print(getClass().getName());
        pw.println(": nothing to dump");
    }

    public final FillEventHistory getFillEventHistory() {
        AutofillManager afm = (AutofillManager) getSystemService(AutofillManager.class);
        if (afm == null) {
            return null;
        }
        return afm.getFillEventHistory();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class AutofillProxy {
        static final int REPORT_EVENT_INLINE_RESPONSE = 4;
        static final int REPORT_EVENT_NO_RESPONSE = 1;
        static final int REPORT_EVENT_UI_DESTROYED = 3;
        static final int REPORT_EVENT_UI_SHOWN = 2;
        private IFillCallback mCallback;
        private CancellationSignal mCancellationSignal;
        private final IAugmentedAutofillManagerClient mClient;
        public final ComponentName mComponentName;
        private FillWindow mFillWindow;
        private long mFirstOnSuccessTime;
        private final long mFirstRequestTime;
        private AutofillId mFocusedId;
        private AutofillValue mFocusedValue;
        private AssistStructure.ViewNode mFocusedViewNode;
        private AutofillId mLastShownId;
        private final Object mLock;
        private String mServicePackageName;
        private final int mSessionId;
        private PresentationParams.SystemPopupPresentationParams mSmartSuggestion;
        public final int mTaskId;
        private long mUiFirstDestroyedTime;
        private long mUiFirstShownTime;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        @interface ReportEvent {
        }

        private AutofillProxy(int sessionId, IBinder client, int taskId, ComponentName serviceComponentName, ComponentName componentName, AutofillId focusedId, AutofillValue focusedValue, long requestTime, IFillCallback callback, CancellationSignal cancellationSignal) {
            this.mLock = new Object();
            this.mSessionId = sessionId;
            this.mClient = IAugmentedAutofillManagerClient.Stub.asInterface(client);
            this.mCallback = callback;
            this.mTaskId = taskId;
            this.mComponentName = componentName;
            this.mServicePackageName = serviceComponentName.getPackageName();
            this.mFocusedId = focusedId;
            this.mFocusedValue = focusedValue;
            this.mFirstRequestTime = requestTime;
            this.mCancellationSignal = cancellationSignal;
        }

        public PresentationParams.SystemPopupPresentationParams getSmartSuggestionParams() {
            synchronized (this.mLock) {
                if (this.mSmartSuggestion != null && this.mFocusedId.equals(this.mLastShownId)) {
                    return this.mSmartSuggestion;
                }
                try {
                    Rect rect = this.mClient.getViewCoordinates(this.mFocusedId);
                    if (rect == null) {
                        if (AugmentedAutofillService.sDebug) {
                            Log.m112d(AugmentedAutofillService.TAG, "getViewCoordinates(" + this.mFocusedId + ") returned null");
                        }
                        return null;
                    }
                    PresentationParams.SystemPopupPresentationParams systemPopupPresentationParams = new PresentationParams.SystemPopupPresentationParams(this, rect);
                    this.mSmartSuggestion = systemPopupPresentationParams;
                    this.mLastShownId = this.mFocusedId;
                    return systemPopupPresentationParams;
                } catch (RemoteException e) {
                    Log.m104w(AugmentedAutofillService.TAG, "Could not get coordinates for " + this.mFocusedId);
                    return null;
                }
            }
        }

        public void autofill(List<Pair<AutofillId, AutofillValue>> pairs) throws RemoteException {
            int size = pairs.size();
            List<AutofillId> ids = new ArrayList<>(size);
            List<AutofillValue> values = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Pair<AutofillId, AutofillValue> pair = pairs.get(i);
                ids.add(pair.first);
                values.add(pair.second);
            }
            boolean hideHighlight = false;
            if (size == 1 && ids.get(0).equals(this.mFocusedId)) {
                hideHighlight = true;
            }
            this.mClient.autofill(this.mSessionId, ids, values, hideHighlight);
        }

        public void setFillWindow(FillWindow fillWindow) {
            synchronized (this.mLock) {
                this.mFillWindow = fillWindow;
            }
        }

        public FillWindow getFillWindow() {
            FillWindow fillWindow;
            synchronized (this.mLock) {
                fillWindow = this.mFillWindow;
            }
            return fillWindow;
        }

        public void requestShowFillUi(int width, int height, Rect anchorBounds, IAutofillWindowPresenter presenter) throws RemoteException {
            if (this.mCancellationSignal.isCanceled()) {
                if (AugmentedAutofillService.sVerbose) {
                    Log.m106v(AugmentedAutofillService.TAG, "requestShowFillUi() not showing because request is cancelled");
                    return;
                }
                return;
            }
            this.mClient.requestShowFillUi(this.mSessionId, this.mFocusedId, width, height, anchorBounds, presenter);
        }

        public void requestHideFillUi() throws RemoteException {
            this.mClient.requestHideFillUi(this.mSessionId, this.mFocusedId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean requestAutofill() throws RemoteException {
            return this.mClient.requestAutofill(this.mSessionId, this.mFocusedId);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void update(AutofillId focusedId, AutofillValue focusedValue, IFillCallback callback, CancellationSignal cancellationSignal) {
            synchronized (this.mLock) {
                this.mFocusedId = focusedId;
                this.mFocusedValue = focusedValue;
                this.mFocusedViewNode = null;
                IFillCallback iFillCallback = this.mCallback;
                if (iFillCallback != null) {
                    try {
                        if (!iFillCallback.isCompleted()) {
                            this.mCallback.cancel();
                        }
                    } catch (RemoteException e) {
                        Log.m109e(AugmentedAutofillService.TAG, "failed to check current pending request status", e);
                    }
                    Log.m112d(AugmentedAutofillService.TAG, "mCallback is updated.");
                }
                this.mCallback = callback;
                this.mCancellationSignal = cancellationSignal;
            }
        }

        public AutofillId getFocusedId() {
            AutofillId autofillId;
            synchronized (this.mLock) {
                autofillId = this.mFocusedId;
            }
            return autofillId;
        }

        public AutofillValue getFocusedValue() {
            AutofillValue autofillValue;
            synchronized (this.mLock) {
                autofillValue = this.mFocusedValue;
            }
            return autofillValue;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void reportResult(List<Dataset> inlineSuggestionsData, Bundle clientState, boolean showingFillWindow) {
            try {
                this.mCallback.onSuccess(inlineSuggestionsData, clientState, showingFillWindow);
            } catch (RemoteException e) {
                Log.m110e(AugmentedAutofillService.TAG, "Error calling back with the inline suggestions data: " + e);
            }
        }

        public AssistStructure.ViewNode getFocusedViewNode() {
            AssistStructure.ViewNode viewNode;
            synchronized (this.mLock) {
                if (this.mFocusedViewNode == null) {
                    try {
                        AssistStructure.ViewNodeParcelable viewNodeParcelable = this.mClient.getViewNodeParcelable(this.mFocusedId);
                        if (viewNodeParcelable != null) {
                            this.mFocusedViewNode = viewNodeParcelable.getViewNode();
                        }
                    } catch (RemoteException e) {
                        Log.m110e(AugmentedAutofillService.TAG, "Error getting the ViewNode of the focused view: " + e);
                        return null;
                    }
                }
                viewNode = this.mFocusedViewNode;
            }
            return viewNode;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void logEvent(int event) {
            if (AugmentedAutofillService.sVerbose) {
                Log.m106v(AugmentedAutofillService.TAG, "returnAndLogResult(): " + event);
            }
            long duration = -1;
            int type = 0;
            switch (event) {
                case 1:
                    type = 10;
                    if (this.mFirstOnSuccessTime == 0) {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        this.mFirstOnSuccessTime = elapsedRealtime;
                        duration = elapsedRealtime - this.mFirstRequestTime;
                        if (AugmentedAutofillService.sDebug) {
                            Log.m112d(AugmentedAutofillService.TAG, "Service responded nothing in " + TimeUtils.formatDuration(duration));
                            break;
                        }
                    }
                    break;
                case 2:
                    type = 1;
                    if (this.mUiFirstShownTime == 0) {
                        long elapsedRealtime2 = SystemClock.elapsedRealtime();
                        this.mUiFirstShownTime = elapsedRealtime2;
                        duration = elapsedRealtime2 - this.mFirstRequestTime;
                        if (AugmentedAutofillService.sDebug) {
                            Log.m112d(AugmentedAutofillService.TAG, "UI shown in " + TimeUtils.formatDuration(duration));
                            break;
                        }
                    }
                    break;
                case 3:
                    type = 2;
                    if (this.mUiFirstDestroyedTime == 0) {
                        long elapsedRealtime3 = SystemClock.elapsedRealtime();
                        this.mUiFirstDestroyedTime = elapsedRealtime3;
                        duration = elapsedRealtime3 - this.mFirstRequestTime;
                        if (AugmentedAutofillService.sDebug) {
                            Log.m112d(AugmentedAutofillService.TAG, "UI destroyed in " + TimeUtils.formatDuration(duration));
                            break;
                        }
                    }
                    break;
                case 4:
                    if (this.mFirstOnSuccessTime == 0) {
                        long elapsedRealtime4 = SystemClock.elapsedRealtime();
                        this.mFirstOnSuccessTime = elapsedRealtime4;
                        duration = elapsedRealtime4 - this.mFirstRequestTime;
                        if (AugmentedAutofillService.sDebug) {
                            Log.m112d(AugmentedAutofillService.TAG, "Inline response in " + TimeUtils.formatDuration(duration));
                            break;
                        }
                    }
                    break;
                default:
                    Log.m104w(AugmentedAutofillService.TAG, "invalid event reported: " + event);
                    break;
            }
            Helper.logResponse(type, this.mServicePackageName, this.mComponentName, this.mSessionId, duration);
        }

        public void dump(String prefix, PrintWriter pw) {
            pw.print(prefix);
            pw.print("sessionId: ");
            pw.println(this.mSessionId);
            pw.print(prefix);
            pw.print("taskId: ");
            pw.println(this.mTaskId);
            pw.print(prefix);
            pw.print("component: ");
            pw.println(this.mComponentName.flattenToShortString());
            pw.print(prefix);
            pw.print("focusedId: ");
            pw.println(this.mFocusedId);
            if (this.mFocusedValue != null) {
                pw.print(prefix);
                pw.print("focusedValue: ");
                pw.println(this.mFocusedValue);
            }
            if (this.mLastShownId != null) {
                pw.print(prefix);
                pw.print("lastShownId: ");
                pw.println(this.mLastShownId);
            }
            pw.print(prefix);
            pw.print("client: ");
            pw.println(this.mClient);
            String prefix2 = prefix + "  ";
            if (this.mFillWindow != null) {
                pw.print(prefix);
                pw.println("window:");
                this.mFillWindow.dump(prefix2, pw);
            }
            if (this.mSmartSuggestion != null) {
                pw.print(prefix);
                pw.println("smartSuggestion:");
                this.mSmartSuggestion.dump(prefix2, pw);
            }
            long j = this.mFirstOnSuccessTime;
            if (j > 0) {
                pw.print(prefix);
                pw.print("response time: ");
                TimeUtils.formatDuration(j - this.mFirstRequestTime, pw);
                pw.println();
            }
            long responseTime = this.mUiFirstShownTime;
            if (responseTime > 0) {
                pw.print(prefix);
                pw.print("UI rendering time: ");
                TimeUtils.formatDuration(responseTime - this.mFirstRequestTime, pw);
                pw.println();
            }
            long uiRenderingTime = this.mUiFirstDestroyedTime;
            if (uiRenderingTime > 0) {
                long uiTotalTime = uiRenderingTime - this.mFirstRequestTime;
                pw.print(prefix);
                pw.print("UI life time: ");
                TimeUtils.formatDuration(uiTotalTime, pw);
                pw.println();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void destroy() {
            synchronized (this.mLock) {
                if (this.mFillWindow != null) {
                    if (AugmentedAutofillService.sDebug) {
                        Log.m112d(AugmentedAutofillService.TAG, "destroying window");
                    }
                    this.mFillWindow.destroy();
                    this.mFillWindow = null;
                }
            }
        }
    }
}
