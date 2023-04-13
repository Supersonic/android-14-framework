package android.view.translation;

import android.app.Activity;
import android.app.assist.ActivityId;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Dumpable;
import android.util.IntArray;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManagerGlobal;
import android.view.autofill.AutofillId;
import android.view.translation.TranslationContext;
import android.view.translation.TranslationRequest;
import android.widget.TextView;
import android.widget.TextViewTranslationCallback;
import com.android.internal.util.function.QuadConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class UiTranslationController implements Dumpable {
    public static final boolean DEBUG = Log.isLoggable(UiTranslationManager.LOG_TAG, 3);
    public static final String DUMPABLE_NAME = "UiTranslationController";
    private static final String TAG = "UiTranslationController";
    private final Activity mActivity;
    private final Context mContext;
    private int mCurrentState;
    private ArraySet<AutofillId> mLastRequestAutofillIds;
    private final Handler mWorkerHandler;
    private final HandlerThread mWorkerThread;
    private final Object mLock = new Object();
    private final ArrayMap<AutofillId, WeakReference<View>> mViews = new ArrayMap<>();
    private final ArrayMap<Pair<TranslationSpec, TranslationSpec>, Translator> mTranslators = new ArrayMap<>();
    private final ArraySet<AutofillId> mViewsToPadContent = new ArraySet<>();

    public UiTranslationController(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;
        HandlerThread handlerThread = new HandlerThread("UiTranslationController_" + activity.getComponentName(), -2);
        this.mWorkerThread = handlerThread;
        handlerThread.start();
        this.mWorkerHandler = handlerThread.getThreadHandler();
        activity.addDumpable(this);
    }

    public void updateUiTranslationState(int state, TranslationSpec sourceSpec, TranslationSpec targetSpec, List<AutofillId> views, UiTranslationSpec uiTranslationSpec) {
        if (this.mActivity.isDestroyed()) {
            Log.m108i("UiTranslationController", "Cannot update " + stateToString(state) + " for destroyed " + this.mActivity);
            return;
        }
        Log.m108i("UiTranslationController", "updateUiTranslationState state: " + stateToString(state) + (DEBUG ? ", views: " + views + ", spec: " + uiTranslationSpec : ""));
        synchronized (this.mLock) {
            this.mCurrentState = state;
            if (views != null) {
                setLastRequestAutofillIdsLocked(views);
            }
        }
        switch (state) {
            case 0:
                if (uiTranslationSpec != null && uiTranslationSpec.shouldPadContentForCompat()) {
                    synchronized (this.mLock) {
                        this.mViewsToPadContent.addAll(views);
                    }
                }
                Pair<TranslationSpec, TranslationSpec> specs = new Pair<>(sourceSpec, targetSpec);
                if (!this.mTranslators.containsKey(specs)) {
                    this.mWorkerHandler.sendMessage(PooledLambda.obtainMessage(new QuadConsumer() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda3
                        @Override // com.android.internal.util.function.QuadConsumer
                        public final void accept(Object obj, Object obj2, Object obj3, Object obj4) {
                            ((UiTranslationController) obj).createTranslatorAndStart((TranslationSpec) obj2, (TranslationSpec) obj3, (List) obj4);
                        }
                    }, this, sourceSpec, targetSpec, views));
                    return;
                } else {
                    onUiTranslationStarted(this.mTranslators.get(specs), views);
                    return;
                }
            case 1:
                runForEachView(new BiConsumer() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda4
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((ViewTranslationCallback) obj2).onHideTranslation((View) obj);
                    }
                });
                return;
            case 2:
                runForEachView(new BiConsumer() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda5
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((ViewTranslationCallback) obj2).onShowTranslation((View) obj);
                    }
                });
                return;
            case 3:
                destroyTranslators();
                runForEachView(new BiConsumer() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda6
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ViewTranslationCallback viewTranslationCallback = (ViewTranslationCallback) obj2;
                        ((View) obj).clearTranslationState();
                    }
                });
                notifyTranslationFinished(false);
                synchronized (this.mLock) {
                    this.mViews.clear();
                }
                return;
            default:
                Log.m104w("UiTranslationController", "onAutoTranslationStateChange(): unknown state: " + state);
                return;
        }
    }

    public void onActivityDestroyed() {
        synchronized (this.mLock) {
            Log.m108i("UiTranslationController", "onActivityDestroyed(): mCurrentState is " + stateToString(this.mCurrentState));
            if (this.mCurrentState != 3) {
                notifyTranslationFinished(true);
            }
            this.mViews.clear();
            destroyTranslators();
            this.mWorkerThread.quitSafely();
        }
    }

    private void notifyTranslationFinished(boolean activityDestroyed) {
        UiTranslationManager manager = (UiTranslationManager) this.mContext.getSystemService(UiTranslationManager.class);
        if (manager != null) {
            manager.onTranslationFinished(activityDestroyed, new ActivityId(this.mActivity.getTaskId(), this.mActivity.getShareableActivityToken()), this.mActivity.getComponentName());
        }
    }

    private void setLastRequestAutofillIdsLocked(List<AutofillId> views) {
        if (this.mLastRequestAutofillIds == null) {
            this.mLastRequestAutofillIds = new ArraySet<>();
        }
        if (this.mLastRequestAutofillIds.size() > 0) {
            this.mLastRequestAutofillIds.clear();
        }
        this.mLastRequestAutofillIds.addAll(views);
    }

    @Override // android.util.Dumpable
    public String getDumpableName() {
        return "UiTranslationController";
    }

    @Override // android.util.Dumpable
    public void dump(PrintWriter pw, String[] args) {
        pw.print("");
        pw.println("UiTranslationController:");
        String pfx = "  ";
        pw.print(pfx);
        pw.print("activity: ");
        pw.print(this.mActivity);
        pw.print(pfx);
        pw.print("resumed: ");
        pw.println(this.mActivity.isResumed());
        pw.print(pfx);
        pw.print("current state: ");
        pw.println(this.mCurrentState);
        int translatorSize = this.mTranslators.size();
        pw.print("");
        pw.print("number translator: ");
        pw.println(translatorSize);
        for (int i = 0; i < translatorSize; i++) {
            pw.print("");
            pw.print("#");
            pw.println(i);
            Translator translator = this.mTranslators.valueAt(i);
            translator.dump("", pw);
            pw.println();
        }
        synchronized (this.mLock) {
            int viewSize = this.mViews.size();
            pw.print("");
            pw.print("number views: ");
            pw.println(viewSize);
            for (int i2 = 0; i2 < viewSize; i2++) {
                pw.print("");
                pw.print("#");
                pw.println(i2);
                AutofillId autofillId = this.mViews.keyAt(i2);
                View view = this.mViews.valueAt(i2).get();
                pw.print(pfx);
                pw.print("autofillId: ");
                pw.println(autofillId);
                pw.print(pfx);
                pw.print("view:");
                pw.println(view);
            }
            pw.print("");
            pw.print("padded views: ");
            pw.println(this.mViewsToPadContent);
        }
        if (DEBUG) {
            dumpViewByTraversal("", pw);
        }
    }

    private void dumpViewByTraversal(String outerPrefix, PrintWriter pw) {
        ArrayList<ViewRootImpl> roots = WindowManagerGlobal.getInstance().getRootViews(this.mActivity.getActivityToken());
        pw.print(outerPrefix);
        pw.println("Dump views:");
        for (int rootNum = 0; rootNum < roots.size(); rootNum++) {
            View rootView = roots.get(rootNum).getView();
            if (rootView instanceof ViewGroup) {
                dumpChildren((ViewGroup) rootView, outerPrefix, pw);
            } else {
                dumpViewInfo(rootView, outerPrefix, pw);
            }
        }
    }

    private void dumpChildren(ViewGroup viewGroup, String outerPrefix, PrintWriter pw) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                pw.print(outerPrefix);
                pw.println("Children: ");
                pw.print(outerPrefix);
                pw.print(outerPrefix);
                pw.println(child);
                dumpChildren((ViewGroup) child, outerPrefix, pw);
            } else {
                pw.print(outerPrefix);
                pw.println("End Children: ");
                pw.print(outerPrefix);
                pw.print(outerPrefix);
                pw.print(child);
                dumpViewInfo(child, outerPrefix, pw);
            }
        }
    }

    private void dumpViewInfo(View view, String outerPrefix, PrintWriter pw) {
        AutofillId autofillId = view.getAutofillId();
        pw.print(outerPrefix);
        pw.print("autofillId: ");
        pw.print(autofillId);
        boolean isContainsView = false;
        boolean isRequestedView = false;
        synchronized (this.mLock) {
            if (this.mLastRequestAutofillIds.contains(autofillId)) {
                isRequestedView = true;
            }
            WeakReference<View> viewRef = this.mViews.get(autofillId);
            if (viewRef != null && viewRef.get() != null) {
                isContainsView = true;
            }
        }
        pw.print(outerPrefix);
        pw.print("isContainsView: ");
        pw.print(isContainsView);
        pw.print(outerPrefix);
        pw.print("isRequestedView: ");
        pw.println(isRequestedView);
    }

    public void onTranslationCompleted(TranslationResponse response) {
        Object valueOf;
        if (response == null || response.getTranslationStatus() != 0) {
            StringBuilder append = new StringBuilder().append("Fail result from TranslationService, status=");
            if (response == null) {
                valueOf = "null";
            } else {
                valueOf = Integer.valueOf(response.getTranslationStatus());
            }
            Log.m104w("UiTranslationController", append.append(valueOf).toString());
            return;
        }
        SparseArray<ViewTranslationResponse> translatedResult = response.getViewTranslationResponses();
        SparseArray<ViewTranslationResponse> viewsResult = new SparseArray<>();
        SparseArray<LongSparseArray<ViewTranslationResponse>> virtualViewsResult = new SparseArray<>();
        IntArray viewIds = new IntArray(1);
        for (int i = 0; i < translatedResult.size(); i++) {
            ViewTranslationResponse result = translatedResult.valueAt(i);
            AutofillId autofillId = result.getAutofillId();
            if (viewIds.indexOf(autofillId.getViewId()) < 0) {
                viewIds.add(autofillId.getViewId());
            }
            if (autofillId.isNonVirtual()) {
                viewsResult.put(translatedResult.keyAt(i), result);
            } else {
                boolean isVirtualViewAdded = virtualViewsResult.indexOfKey(autofillId.getViewId()) >= 0;
                LongSparseArray<ViewTranslationResponse> childIds = isVirtualViewAdded ? virtualViewsResult.get(autofillId.getViewId()) : new LongSparseArray<>();
                childIds.put(autofillId.getVirtualChildLongId(), result);
                if (!isVirtualViewAdded) {
                    virtualViewsResult.put(autofillId.getViewId(), childIds);
                }
            }
        }
        findViewsTraversalByAutofillIds(viewIds);
        if (viewsResult.size() > 0) {
            onTranslationCompleted(viewsResult);
        }
        if (virtualViewsResult.size() > 0) {
            onVirtualViewTranslationCompleted(virtualViewsResult);
        }
    }

    private void onVirtualViewTranslationCompleted(SparseArray<LongSparseArray<ViewTranslationResponse>> translatedResult) {
        if (this.mActivity.isDestroyed()) {
            Log.m106v("UiTranslationController", "onTranslationCompleted:" + this.mActivity + "is destroyed.");
            return;
        }
        synchronized (this.mLock) {
            if (this.mCurrentState == 3) {
                Log.m104w("UiTranslationController", "onTranslationCompleted: the translation state is finished now. Skip to show the translated text.");
                return;
            }
            for (int i = 0; i < translatedResult.size(); i++) {
                AutofillId autofillId = new AutofillId(translatedResult.keyAt(i));
                WeakReference<View> viewRef = this.mViews.get(autofillId);
                if (viewRef != null) {
                    final View view = viewRef.get();
                    if (view == null) {
                        Log.m104w("UiTranslationController", "onTranslationCompleted: the view for autofill id " + autofillId + " may be gone.");
                    } else {
                        LongSparseArray<ViewTranslationResponse> virtualChildResponse = translatedResult.valueAt(i);
                        if (DEBUG) {
                            Log.m106v("UiTranslationController", "onVirtualViewTranslationCompleted: received response for AutofillId " + autofillId);
                        }
                        view.onVirtualViewTranslationResponses(virtualChildResponse);
                        if (this.mCurrentState == 1) {
                            return;
                        }
                        this.mActivity.runOnUiThread(new Runnable() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda8
                            @Override // java.lang.Runnable
                            public final void run() {
                                UiTranslationController.lambda$onVirtualViewTranslationCompleted$3(View.this);
                            }
                        });
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onVirtualViewTranslationCompleted$3(View view) {
        if (view.getViewTranslationCallback() == null) {
            if (DEBUG) {
                Log.m112d("UiTranslationController", view + " doesn't support showing translation because of null ViewTranslationCallback.");
            }
        } else if (view.getViewTranslationCallback() != null) {
            view.getViewTranslationCallback().onShowTranslation(view);
        }
    }

    private void onTranslationCompleted(SparseArray<ViewTranslationResponse> translatedResult) {
        if (this.mActivity.isDestroyed()) {
            Log.m106v("UiTranslationController", "onTranslationCompleted:" + this.mActivity + "is destroyed.");
            return;
        }
        int resultCount = translatedResult.size();
        if (DEBUG) {
            Log.m106v("UiTranslationController", "onTranslationCompleted: receive " + resultCount + " responses.");
        }
        synchronized (this.mLock) {
            if (this.mCurrentState == 3) {
                Log.m104w("UiTranslationController", "onTranslationCompleted: the translation state is finished now. Skip to show the translated text.");
                return;
            }
            for (int i = 0; i < resultCount; i++) {
                final ViewTranslationResponse response = translatedResult.valueAt(i);
                if (DEBUG) {
                    Log.m106v("UiTranslationController", "onTranslationCompleted: " + sanitizedViewTranslationResponse(response));
                }
                final AutofillId autofillId = response.getAutofillId();
                if (autofillId == null) {
                    Log.m104w("UiTranslationController", "No AutofillId is set in ViewTranslationResponse");
                } else {
                    WeakReference<View> viewRef = this.mViews.get(autofillId);
                    if (viewRef != null) {
                        final View view = viewRef.get();
                        if (view == null) {
                            Log.m104w("UiTranslationController", "onTranslationCompleted: the view for autofill id " + autofillId + " may be gone.");
                        } else {
                            final int currentState = this.mCurrentState;
                            this.mActivity.runOnUiThread(new Runnable() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda2
                                @Override // java.lang.Runnable
                                public final void run() {
                                    UiTranslationController.this.lambda$onTranslationCompleted$4(view, response, autofillId, currentState);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTranslationCompleted$4(View view, ViewTranslationResponse response, AutofillId autofillId, int currentState) {
        ViewTranslationCallback callback = view.getViewTranslationCallback();
        if (view.getViewTranslationResponse() != null && view.getViewTranslationResponse().equals(response) && (callback instanceof TextViewTranslationCallback)) {
            TextViewTranslationCallback textViewCallback = (TextViewTranslationCallback) callback;
            if (textViewCallback.isShowingTranslation() || textViewCallback.isAnimationRunning()) {
                if (DEBUG) {
                    Log.m112d("UiTranslationController", "Duplicate ViewTranslationResponse for " + autofillId + ". Ignoring.");
                    return;
                }
                return;
            }
        }
        if (callback == null) {
            if (view instanceof TextView) {
                callback = new TextViewTranslationCallback();
                view.setViewTranslationCallback(callback);
            } else if (DEBUG) {
                Log.m112d("UiTranslationController", view + " doesn't support showing translation because of null ViewTranslationCallback.");
                return;
            } else {
                return;
            }
        }
        callback.setAnimationDurationMillis(250);
        if (this.mViewsToPadContent.contains(autofillId)) {
            callback.enableContentPadding();
        }
        view.onViewTranslationResponse(response);
        if (currentState == 1) {
            return;
        }
        callback.onShowTranslation(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createTranslatorAndStart(TranslationSpec sourceSpec, TranslationSpec targetSpec, List<AutofillId> views) {
        Translator translator = createTranslatorIfNeeded(sourceSpec, targetSpec);
        if (translator == null) {
            Log.m104w("UiTranslationController", "Can not create Translator for sourceSpec:" + sourceSpec + " targetSpec:" + targetSpec);
        } else {
            onUiTranslationStarted(translator, views);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTranslationRequest(Translator translator, List<ViewTranslationRequest> requests) {
        if (requests.size() == 0) {
            Log.m104w("UiTranslationController", "No ViewTranslationRequest was collected.");
            return;
        }
        TranslationRequest request = new TranslationRequest.Builder().setViewTranslationRequests(requests).build();
        if (DEBUG) {
            StringBuilder msg = new StringBuilder("sendTranslationRequest:{requests=[");
            for (ViewTranslationRequest viewRequest : requests) {
                msg.append("{request=").append(sanitizedViewTranslationRequest(viewRequest)).append("}, ");
            }
            Log.m112d("UiTranslationController", "sendTranslationRequest: " + msg.toString());
        }
        translator.requestUiTranslate(request, new Executor() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda9
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                runnable.run();
            }
        }, new Consumer() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                UiTranslationController.this.onTranslationCompleted((TranslationResponse) obj);
            }
        });
    }

    private void onUiTranslationStarted(final Translator translator, List<AutofillId> views) {
        long[] childs;
        synchronized (this.mLock) {
            SparseIntArray virtualViewChildCount = getRequestVirtualViewChildCount(views);
            final Map<AutofillId, long[]> viewIds = new ArrayMap<>();
            Map<AutofillId, Integer> unusedIndices = null;
            for (int i = 0; i < views.size(); i++) {
                AutofillId autofillId = views.get(i);
                if (autofillId.isNonVirtual()) {
                    viewIds.put(autofillId, null);
                } else {
                    if (unusedIndices == null) {
                        unusedIndices = new ArrayMap<>();
                    }
                    AutofillId virtualViewAutofillId = new AutofillId(autofillId.getViewId());
                    int end = 0;
                    if (viewIds.containsKey(virtualViewAutofillId)) {
                        childs = viewIds.get(virtualViewAutofillId);
                        end = unusedIndices.get(virtualViewAutofillId).intValue();
                    } else {
                        int childCount = virtualViewChildCount.get(autofillId.getViewId());
                        long[] childs2 = new long[childCount];
                        viewIds.put(virtualViewAutofillId, childs2);
                        childs = childs2;
                    }
                    unusedIndices.put(virtualViewAutofillId, Integer.valueOf(end + 1));
                    childs[end] = autofillId.getVirtualChildLongId();
                }
            }
            final ArrayList<ViewTranslationRequest> requests = new ArrayList<>();
            final int[] supportedFormats = getSupportedFormatsLocked();
            final ArrayList<ViewRootImpl> roots = WindowManagerGlobal.getInstance().getRootViews(this.mActivity.getActivityToken());
            final TranslationCapability capability = getTranslationCapability(translator.getTranslationContext());
            this.mActivity.runOnUiThread(new Runnable() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    UiTranslationController.this.lambda$onUiTranslationStarted$6(roots, viewIds, supportedFormats, capability, requests, translator);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUiTranslationStarted$6(ArrayList roots, Map viewIds, int[] supportedFormats, TranslationCapability capability, ArrayList requests, Translator translator) {
        for (int rootNum = 0; rootNum < roots.size(); rootNum++) {
            View rootView = ((ViewRootImpl) roots.get(rootNum)).getView();
            rootView.dispatchCreateViewTranslationRequest(viewIds, supportedFormats, capability, requests);
        }
        this.mWorkerHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda0
            @Override // com.android.internal.util.function.TriConsumer
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((UiTranslationController) obj).sendTranslationRequest((Translator) obj2, (ArrayList) obj3);
            }
        }, this, translator, requests));
    }

    private SparseIntArray getRequestVirtualViewChildCount(List<AutofillId> views) {
        SparseIntArray virtualViewCount = new SparseIntArray();
        for (int i = 0; i < views.size(); i++) {
            AutofillId autofillId = views.get(i);
            if (!autofillId.isNonVirtual()) {
                int virtualViewId = autofillId.getViewId();
                if (virtualViewCount.indexOfKey(virtualViewId) < 0) {
                    virtualViewCount.put(virtualViewId, 1);
                } else {
                    virtualViewCount.put(virtualViewId, virtualViewCount.get(virtualViewId) + 1);
                }
            }
        }
        return virtualViewCount;
    }

    private int[] getSupportedFormatsLocked() {
        return new int[]{1};
    }

    private TranslationCapability getTranslationCapability(TranslationContext translationContext) {
        return new TranslationCapability(3, translationContext.getSourceSpec(), translationContext.getTargetSpec(), true, 0);
    }

    private void findViewsTraversalByAutofillIds(IntArray sourceViewIds) {
        ArrayList<ViewRootImpl> roots = WindowManagerGlobal.getInstance().getRootViews(this.mActivity.getActivityToken());
        for (int rootNum = 0; rootNum < roots.size(); rootNum++) {
            View rootView = roots.get(rootNum).getView();
            if (rootView instanceof ViewGroup) {
                findViewsTraversalByAutofillIds((ViewGroup) rootView, sourceViewIds);
            }
            addViewIfNeeded(sourceViewIds, rootView);
        }
    }

    private void findViewsTraversalByAutofillIds(ViewGroup viewGroup, IntArray sourceViewIds) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                findViewsTraversalByAutofillIds((ViewGroup) child, sourceViewIds);
            }
            addViewIfNeeded(sourceViewIds, child);
        }
    }

    private void addViewIfNeeded(IntArray sourceViewIds, View view) {
        AutofillId autofillId = view.getAutofillId();
        if (autofillId != null && sourceViewIds.indexOf(autofillId.getViewId()) >= 0 && !this.mViews.containsKey(autofillId)) {
            this.mViews.put(autofillId, new WeakReference<>(view));
        }
    }

    private void runForEachView(final BiConsumer<View, ViewTranslationCallback> action) {
        synchronized (this.mLock) {
            final ArrayMap<AutofillId, WeakReference<View>> views = new ArrayMap<>(this.mViews);
            if (views.size() == 0) {
                Log.m104w("UiTranslationController", "No views can be excuted for runForEachView.");
            }
            this.mActivity.runOnUiThread(new Runnable() { // from class: android.view.translation.UiTranslationController$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    UiTranslationController.lambda$runForEachView$7(ArrayMap.this, action);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$runForEachView$7(ArrayMap views, BiConsumer action) {
        int viewCounts = views.size();
        for (int i = 0; i < viewCounts; i++) {
            View view = (View) ((WeakReference) views.valueAt(i)).get();
            boolean z = DEBUG;
            if (z) {
                Log.m112d("UiTranslationController", "runForEachView for autofillId = " + (view != null ? view.getAutofillId() : " null"));
            }
            if (view == null || view.getViewTranslationCallback() == null) {
                if (z) {
                    Log.m112d("UiTranslationController", "View was gone or ViewTranslationCallback for autofillId = " + views.keyAt(i));
                }
            } else {
                action.accept(view, view.getViewTranslationCallback());
            }
        }
    }

    private Translator createTranslatorIfNeeded(TranslationSpec sourceSpec, TranslationSpec targetSpec) {
        TranslationManager tm = (TranslationManager) this.mContext.getSystemService(TranslationManager.class);
        if (tm == null) {
            Log.m110e("UiTranslationController", "Can not find TranslationManager when trying to create translator.");
            return null;
        }
        TranslationContext translationContext = new TranslationContext.Builder(sourceSpec, targetSpec).setActivityId(new ActivityId(this.mActivity.getTaskId(), this.mActivity.getShareableActivityToken())).build();
        Translator translator = tm.createTranslator(translationContext);
        if (translator != null) {
            Pair<TranslationSpec, TranslationSpec> specs = new Pair<>(sourceSpec, targetSpec);
            this.mTranslators.put(specs, translator);
        }
        return translator;
    }

    private void destroyTranslators() {
        synchronized (this.mLock) {
            int count = this.mTranslators.size();
            for (int i = 0; i < count; i++) {
                Translator translator = this.mTranslators.valueAt(i);
                translator.destroy();
            }
            this.mTranslators.clear();
        }
    }

    public static String stateToString(int state) {
        switch (state) {
            case 0:
                return "UI_TRANSLATION_STARTED";
            case 1:
                return "UI_TRANSLATION_PAUSED";
            case 2:
                return "UI_TRANSLATION_RESUMED";
            case 3:
                return "UI_TRANSLATION_FINISHED";
            default:
                return "Unknown state (" + state + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    private static String sanitizedViewTranslationRequest(ViewTranslationRequest request) {
        String str;
        StringBuilder msg = new StringBuilder("ViewTranslationRequest:{values=[");
        for (String key : request.getKeys()) {
            TranslationRequestValue value = request.getValue(key);
            StringBuilder append = msg.append("{text=");
            if (value.getText() == null) {
                str = "null";
            } else {
                str = "string[" + value.getText().length() + "]}, ";
            }
            append.append(str);
        }
        return msg.toString();
    }

    private static String sanitizedViewTranslationResponse(ViewTranslationResponse response) {
        String str;
        Iterator<String> it;
        Iterator<String> it2;
        String str2;
        StringBuilder msg = new StringBuilder("ViewTranslationResponse:{values=[");
        Iterator<String> it3 = response.getKeys().iterator();
        while (it3.hasNext()) {
            String key = it3.next();
            TranslationResponseValue value = response.getValue(key);
            msg.append("{status=").append(value.getStatusCode()).append(", ");
            StringBuilder append = msg.append("text=");
            String str3 = "null";
            if (value.getText() == null) {
                str = "null";
            } else {
                str = "string[" + value.getText().length() + "], ";
            }
            append.append(str);
            Bundle definitions = (Bundle) value.getExtras().get(TranslationResponseValue.EXTRA_DEFINITIONS);
            if (definitions == null) {
                it = it3;
            } else {
                msg.append("definitions={");
                for (String partOfSpeech : definitions.keySet()) {
                    msg.append(partOfSpeech).append(":[");
                    CharSequence[] charSequenceArray = definitions.getCharSequenceArray(partOfSpeech);
                    int length = charSequenceArray.length;
                    int i = 0;
                    while (i < length) {
                        CharSequence definition = charSequenceArray[i];
                        if (definition == null) {
                            str2 = "null, ";
                            it2 = it3;
                        } else {
                            it2 = it3;
                            str2 = "string[" + definition.length() + "], ";
                        }
                        msg.append(str2);
                        i++;
                        it3 = it2;
                    }
                    msg.append("], ");
                }
                it = it3;
                msg.append("}");
            }
            StringBuilder append2 = msg.append("transliteration=");
            if (value.getTransliteration() != null) {
                str3 = "string[" + value.getTransliteration().length() + "]}, ";
            }
            append2.append(str3);
            it3 = it;
        }
        return msg.toString();
    }
}
