package android.service.autofill;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.p008os.BaseBundle;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.service.autofill.IInlineSuggestionRenderService;
import android.service.autofill.IInlineSuggestionUi;
import android.service.autofill.InlineSuggestionRenderService;
import android.util.Log;
import android.util.LruCache;
import android.util.Size;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.WindowManager;
import com.android.internal.util.function.NonaConsumer;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.function.BiConsumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class InlineSuggestionRenderService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.autofill.InlineSuggestionRenderService";
    private static final String TAG = "InlineSuggestionRenderService";
    private IInlineSuggestionUiCallback mCallback;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper(), null, true);
    private final LruCache<InlineSuggestionUiImpl, Boolean> mActiveInlineSuggestions = new LruCache<InlineSuggestionUiImpl, Boolean>(30) { // from class: android.service.autofill.InlineSuggestionRenderService.1
        @Override // android.util.LruCache
        public void entryRemoved(boolean evicted, InlineSuggestionUiImpl key, Boolean oldValue, Boolean newValue) {
            if (evicted) {
                Log.m104w(InlineSuggestionRenderService.TAG, "Hit max=30 entries in the cache. Releasing oldest one to make space.");
                key.releaseSurfaceControlViewHost();
            }
        }
    };

    private Size measuredSize(View view, int width, int height, Size minSize, Size maxSize) {
        int widthMeasureSpec;
        int heightMeasureSpec;
        if (width == -2 || height == -2) {
            if (width == -2) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxSize.getWidth(), Integer.MIN_VALUE);
            } else {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
            }
            if (height == -2) {
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxSize.getHeight(), Integer.MIN_VALUE);
            } else {
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
            }
            view.measure(widthMeasureSpec, heightMeasureSpec);
            return new Size(Math.max(view.getMeasuredWidth(), minSize.getWidth()), Math.max(view.getMeasuredHeight(), minSize.getHeight()));
        }
        return new Size(width, height);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRenderSuggestion(final IInlineSuggestionUiCallback callback, InlinePresentation presentation, int width, int height, IBinder hostInputToken, int displayId, int userId, int sessionId) {
        if (hostInputToken != null) {
            updateDisplay(displayId);
            try {
                View suggestionView = onRenderSuggestion(presentation, width, height);
                if (suggestionView == null) {
                    Log.m104w(TAG, "ExtServices failed to render the inline suggestion view.");
                    try {
                        callback.onError();
                    } catch (RemoteException e) {
                        Log.m104w(TAG, "Null suggestion view returned by renderer");
                    }
                    updateDisplay(0);
                    return;
                }
                this.mCallback = callback;
                final Size measuredSize = measuredSize(suggestionView, width, height, presentation.getInlinePresentationSpec().getMinSize(), presentation.getInlinePresentationSpec().getMaxSize());
                Log.m106v(TAG, "width=" + width + ", height=" + height + ", measuredSize=" + measuredSize);
                InlineSuggestionRoot suggestionRoot = new InlineSuggestionRoot(this, callback);
                suggestionRoot.addView(suggestionView);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(measuredSize.getWidth(), measuredSize.getHeight(), 2, 0, -2);
                final SurfaceControlViewHost host = new SurfaceControlViewHost(this, getDisplay(), hostInputToken, TAG);
                host.setView(suggestionRoot, lp);
                suggestionView.setFocusable(false);
                suggestionView.setOnClickListener(new View.OnClickListener() { // from class: android.service.autofill.InlineSuggestionRenderService$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        InlineSuggestionRenderService.lambda$handleRenderSuggestion$0(IInlineSuggestionUiCallback.this, view);
                    }
                });
                final View.OnLongClickListener onLongClickListener = suggestionView.getOnLongClickListener();
                suggestionView.setOnLongClickListener(new View.OnLongClickListener() { // from class: android.service.autofill.InlineSuggestionRenderService$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        return InlineSuggestionRenderService.lambda$handleRenderSuggestion$1(View.OnLongClickListener.this, callback, view);
                    }
                });
                final InlineSuggestionUiImpl uiImpl = new InlineSuggestionUiImpl(host, this.mMainHandler, userId, sessionId);
                this.mActiveInlineSuggestions.put(uiImpl, true);
                this.mMainHandler.post(new Runnable() { // from class: android.service.autofill.InlineSuggestionRenderService$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        InlineSuggestionRenderService.lambda$handleRenderSuggestion$2(IInlineSuggestionUiCallback.this, uiImpl, host, measuredSize);
                    }
                });
                return;
            } finally {
                updateDisplay(0);
            }
        }
        try {
            callback.onError();
        } catch (RemoteException e2) {
            Log.m104w(TAG, "RemoteException calling onError()");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$handleRenderSuggestion$0(IInlineSuggestionUiCallback callback, View v) {
        try {
            callback.onClick();
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling onClick()");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$handleRenderSuggestion$1(View.OnLongClickListener onLongClickListener, IInlineSuggestionUiCallback callback, View v) {
        if (onLongClickListener != null) {
            onLongClickListener.onLongClick(v);
        }
        try {
            callback.onLongClick();
            return true;
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling onLongClick()");
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$handleRenderSuggestion$2(IInlineSuggestionUiCallback callback, InlineSuggestionUiImpl uiImpl, SurfaceControlViewHost host, Size measuredSize) {
        try {
            callback.onContent(new InlineSuggestionUiWrapper(uiImpl), host.getSurfacePackage(), measuredSize.getWidth(), measuredSize.getHeight());
        } catch (RemoteException e) {
            Log.m104w(TAG, "RemoteException calling onContent()");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleGetInlineSuggestionsRendererInfo(RemoteCallback callback) {
        Bundle rendererInfo = onGetInlineSuggestionsRendererInfo();
        callback.sendResult(rendererInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDestroySuggestionViews(int userId, int sessionId) {
        Log.m106v(TAG, "handleDestroySuggestionViews called for " + userId + ":" + sessionId);
        for (InlineSuggestionUiImpl inlineSuggestionUi : this.mActiveInlineSuggestions.snapshot().keySet()) {
            if (inlineSuggestionUi.mUserId == userId && inlineSuggestionUi.mSessionId == sessionId) {
                Log.m106v(TAG, "Destroy " + inlineSuggestionUi);
                inlineSuggestionUi.releaseSurfaceControlViewHost();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class InlineSuggestionUiWrapper extends IInlineSuggestionUi.Stub {
        private final WeakReference<InlineSuggestionUiImpl> mUiImpl;

        InlineSuggestionUiWrapper(InlineSuggestionUiImpl uiImpl) {
            this.mUiImpl = new WeakReference<>(uiImpl);
        }

        @Override // android.service.autofill.IInlineSuggestionUi
        public void releaseSurfaceControlViewHost() {
            InlineSuggestionUiImpl uiImpl = this.mUiImpl.get();
            if (uiImpl != null) {
                uiImpl.releaseSurfaceControlViewHost();
            }
        }

        @Override // android.service.autofill.IInlineSuggestionUi
        public void getSurfacePackage(ISurfacePackageResultCallback callback) {
            InlineSuggestionUiImpl uiImpl = this.mUiImpl.get();
            if (uiImpl != null) {
                uiImpl.getSurfacePackage(callback);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public final class InlineSuggestionUiImpl {
        private final Handler mHandler;
        private final int mSessionId;
        private final int mUserId;
        private SurfaceControlViewHost mViewHost;

        InlineSuggestionUiImpl(SurfaceControlViewHost viewHost, Handler handler, int userId, int sessionId) {
            this.mViewHost = viewHost;
            this.mHandler = handler;
            this.mUserId = userId;
            this.mSessionId = sessionId;
        }

        public void releaseSurfaceControlViewHost() {
            this.mHandler.post(new Runnable() { // from class: android.service.autofill.InlineSuggestionRenderService$InlineSuggestionUiImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InlineSuggestionRenderService.InlineSuggestionUiImpl.this.lambda$releaseSurfaceControlViewHost$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$releaseSurfaceControlViewHost$0() {
            if (this.mViewHost == null) {
                return;
            }
            Log.m106v(InlineSuggestionRenderService.TAG, "Releasing inline suggestion view host");
            this.mViewHost.release();
            this.mViewHost = null;
            InlineSuggestionRenderService.this.mActiveInlineSuggestions.remove(this);
            Log.m106v(InlineSuggestionRenderService.TAG, "Removed the inline suggestion from the cache, current size=" + InlineSuggestionRenderService.this.mActiveInlineSuggestions.size());
        }

        public void getSurfacePackage(final ISurfacePackageResultCallback callback) {
            Log.m112d(InlineSuggestionRenderService.TAG, "getSurfacePackage");
            this.mHandler.post(new Runnable() { // from class: android.service.autofill.InlineSuggestionRenderService$InlineSuggestionUiImpl$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    InlineSuggestionRenderService.InlineSuggestionUiImpl.this.lambda$getSurfacePackage$1(callback);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getSurfacePackage$1(ISurfacePackageResultCallback callback) {
            try {
                SurfaceControlViewHost surfaceControlViewHost = this.mViewHost;
                callback.onResult(surfaceControlViewHost == null ? null : surfaceControlViewHost.getSurfacePackage());
            } catch (RemoteException e) {
                Log.m104w(InlineSuggestionRenderService.TAG, "RemoteException calling onSurfacePackage");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public final void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("mActiveInlineSuggestions: " + this.mActiveInlineSuggestions.size());
        for (InlineSuggestionUiImpl impl : this.mActiveInlineSuggestions.snapshot().keySet()) {
            pw.printf("ui: [%s] - [%d]  [%d]\n", impl, Integer.valueOf(impl.mUserId), Integer.valueOf(impl.mSessionId));
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        BaseBundle.setShouldDefuse(true);
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return new BinderC24572().asBinder();
        }
        Log.m104w(TAG, "Tried to bind to wrong intent (should be android.service.autofill.InlineSuggestionRenderService: " + intent);
        return null;
    }

    /* renamed from: android.service.autofill.InlineSuggestionRenderService$2 */
    /* loaded from: classes3.dex */
    class BinderC24572 extends IInlineSuggestionRenderService.Stub {
        BinderC24572() {
        }

        @Override // android.service.autofill.IInlineSuggestionRenderService
        public void renderSuggestion(IInlineSuggestionUiCallback callback, InlinePresentation presentation, int width, int height, IBinder hostInputToken, int displayId, int userId, int sessionId) {
            InlineSuggestionRenderService.this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new NonaConsumer() { // from class: android.service.autofill.InlineSuggestionRenderService$2$$ExternalSyntheticLambda1
                @Override // com.android.internal.util.function.NonaConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
                    ((InlineSuggestionRenderService) obj).handleRenderSuggestion((IInlineSuggestionUiCallback) obj2, (InlinePresentation) obj3, ((Integer) obj4).intValue(), ((Integer) obj5).intValue(), (IBinder) obj6, ((Integer) obj7).intValue(), ((Integer) obj8).intValue(), ((Integer) obj9).intValue());
                }
            }, InlineSuggestionRenderService.this, callback, presentation, Integer.valueOf(width), Integer.valueOf(height), hostInputToken, Integer.valueOf(displayId), Integer.valueOf(userId), Integer.valueOf(sessionId)));
        }

        @Override // android.service.autofill.IInlineSuggestionRenderService
        public void getInlineSuggestionsRendererInfo(RemoteCallback callback) {
            InlineSuggestionRenderService.this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.autofill.InlineSuggestionRenderService$2$$ExternalSyntheticLambda2
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    ((InlineSuggestionRenderService) obj).handleGetInlineSuggestionsRendererInfo((RemoteCallback) obj2);
                }
            }, InlineSuggestionRenderService.this, callback));
        }

        @Override // android.service.autofill.IInlineSuggestionRenderService
        public void destroySuggestionViews(int userId, int sessionId) {
            InlineSuggestionRenderService.this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: android.service.autofill.InlineSuggestionRenderService$2$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.TriConsumer
                public final void accept(Object obj, Object obj2, Object obj3) {
                    ((InlineSuggestionRenderService) obj).handleDestroySuggestionViews(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
                }
            }, InlineSuggestionRenderService.this, Integer.valueOf(userId), Integer.valueOf(sessionId)));
        }
    }

    public final void startIntentSender(IntentSender intentSender) {
        IInlineSuggestionUiCallback iInlineSuggestionUiCallback = this.mCallback;
        if (iInlineSuggestionUiCallback == null) {
            return;
        }
        try {
            iInlineSuggestionUiCallback.onStartIntentSender(intentSender);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public Bundle onGetInlineSuggestionsRendererInfo() {
        return Bundle.EMPTY;
    }

    public View onRenderSuggestion(InlinePresentation presentation, int width, int height) {
        Log.m110e(TAG, "service implementation (" + getClass() + " does not implement onRenderSuggestion()");
        return null;
    }
}
