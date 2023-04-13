package android.view.inputmethod;

import android.annotation.NonNull;
import android.content.Context;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.util.Size;
import android.util.Slog;
import android.view.SurfaceControlViewHost;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InlineSuggestion;
import android.widget.inline.InlineContentView;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Parcelling;
import com.android.internal.view.inline.IInlineContentCallback;
import com.android.internal.view.inline.IInlineContentProvider;
import com.android.internal.view.inline.InlineTooltipUi;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public final class InlineSuggestion implements Parcelable {
    public static final Parcelable.Creator<InlineSuggestion> CREATOR;
    private static final String TAG = "InlineSuggestion";
    static Parcelling<InlineContentCallbackImpl> sParcellingForInlineContentCallback;
    static Parcelling<InlineTooltipUi> sParcellingForInlineTooltipUi;
    private final IInlineContentProvider mContentProvider;
    private final InlineSuggestionInfo mInfo;
    private InlineContentCallbackImpl mInlineContentCallback;
    private InlineTooltipUi mInlineTooltipUi;

    public static InlineSuggestion newInlineSuggestion(InlineSuggestionInfo info) {
        return new InlineSuggestion(info, null, null, null);
    }

    public InlineSuggestion(InlineSuggestionInfo info, IInlineContentProvider contentProvider) {
        this(info, contentProvider, null, null);
    }

    public void inflate(Context context, Size size, Executor callbackExecutor, final Consumer<InlineContentView> callback) {
        Size minSize = this.mInfo.getInlinePresentationSpec().getMinSize();
        Size maxSize = this.mInfo.getInlinePresentationSpec().getMaxSize();
        if (!isValid(size.getWidth(), minSize.getWidth(), maxSize.getWidth()) || !isValid(size.getHeight(), minSize.getHeight(), maxSize.getHeight())) {
            throw new IllegalArgumentException("size is neither between min:" + minSize + " and max:" + maxSize + ", nor wrap_content");
        }
        InlineSuggestion toolTip = this.mInfo.getTooltip();
        if (toolTip != null) {
            if (this.mInlineTooltipUi == null) {
                this.mInlineTooltipUi = new InlineTooltipUi(context);
            }
        } else {
            this.mInlineTooltipUi = null;
        }
        this.mInlineContentCallback = getInlineContentCallback(context, callbackExecutor, callback, this.mInlineTooltipUi);
        IInlineContentProvider iInlineContentProvider = this.mContentProvider;
        if (iInlineContentProvider == null) {
            callbackExecutor.execute(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(null);
                }
            });
            this.mInlineTooltipUi = null;
            return;
        }
        try {
            iInlineContentProvider.provideContent(size.getWidth(), size.getHeight(), new InlineContentCallbackWrapper(this.mInlineContentCallback));
        } catch (RemoteException e) {
            Slog.m90w(TAG, "Error creating suggestion content surface: " + e);
            callbackExecutor.execute(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    callback.accept(null);
                }
            });
        }
        if (toolTip == null) {
            return;
        }
        Size tooltipSize = new Size(-2, -2);
        this.mInfo.getTooltip().inflate(context, tooltipSize, callbackExecutor, new Consumer() { // from class: android.view.inputmethod.InlineSuggestion$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                InlineSuggestion.this.lambda$inflate$3((InlineContentView) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$inflate$2(InlineContentView view) {
        this.mInlineTooltipUi.setTooltipView(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$inflate$3(final InlineContentView view) {
        Handler.getMain().post(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                InlineSuggestion.this.lambda$inflate$2(view);
            }
        });
    }

    private static boolean isValid(int actual, int min, int max) {
        if (actual == -2) {
            return true;
        }
        return actual >= min && actual <= max;
    }

    private synchronized InlineContentCallbackImpl getInlineContentCallback(Context context, Executor callbackExecutor, Consumer<InlineContentView> callback, InlineTooltipUi inlineTooltipUi) {
        if (this.mInlineContentCallback != null) {
            throw new IllegalStateException("Already called #inflate()");
        }
        return new InlineContentCallbackImpl(context, this.mContentProvider, callbackExecutor, callback, inlineTooltipUi);
    }

    /* loaded from: classes4.dex */
    private static final class InlineContentCallbackWrapper extends IInlineContentCallback.Stub {
        private final WeakReference<InlineContentCallbackImpl> mCallbackImpl;

        InlineContentCallbackWrapper(InlineContentCallbackImpl callbackImpl) {
            this.mCallbackImpl = new WeakReference<>(callbackImpl);
        }

        @Override // com.android.internal.view.inline.IInlineContentCallback
        public void onContent(SurfaceControlViewHost.SurfacePackage content, int width, int height) {
            InlineContentCallbackImpl callbackImpl = this.mCallbackImpl.get();
            if (callbackImpl != null) {
                callbackImpl.onContent(content, width, height);
            }
        }

        @Override // com.android.internal.view.inline.IInlineContentCallback
        public void onClick() {
            InlineContentCallbackImpl callbackImpl = this.mCallbackImpl.get();
            if (callbackImpl != null) {
                callbackImpl.onClick();
            }
        }

        @Override // com.android.internal.view.inline.IInlineContentCallback
        public void onLongClick() {
            InlineContentCallbackImpl callbackImpl = this.mCallbackImpl.get();
            if (callbackImpl != null) {
                callbackImpl.onLongClick();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class InlineContentCallbackImpl {
        private final Consumer<InlineContentView> mCallback;
        private final Executor mCallbackExecutor;
        private final Context mContext;
        private final IInlineContentProvider mInlineContentProvider;
        private InlineTooltipUi mInlineTooltipUi;
        private SurfaceControlViewHost.SurfacePackage mSurfacePackage;
        private Consumer<SurfaceControlViewHost.SurfacePackage> mSurfacePackageConsumer;
        private InlineContentView mView;
        private final Handler mMainHandler = new Handler(Looper.getMainLooper());
        private boolean mFirstContentReceived = false;

        InlineContentCallbackImpl(Context context, IInlineContentProvider inlineContentProvider, Executor callbackExecutor, Consumer<InlineContentView> callback, InlineTooltipUi inlineTooltipUi) {
            this.mContext = context;
            this.mInlineContentProvider = inlineContentProvider;
            this.mCallbackExecutor = callbackExecutor;
            this.mCallback = callback;
            this.mInlineTooltipUi = inlineTooltipUi;
        }

        public void onContent(final SurfaceControlViewHost.SurfacePackage content, final int width, final int height) {
            this.mMainHandler.post(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    InlineSuggestion.InlineContentCallbackImpl.this.lambda$onContent$0(content, width, height);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: handleOnContent */
        public void lambda$onContent$0(SurfaceControlViewHost.SurfacePackage content, int width, int height) {
            if (!this.mFirstContentReceived) {
                handleOnFirstContentReceived(content, width, height);
                this.mFirstContentReceived = true;
                return;
            }
            handleOnSurfacePackage(content);
        }

        private void handleOnFirstContentReceived(SurfaceControlViewHost.SurfacePackage content, int width, int height) {
            this.mSurfacePackage = content;
            if (content == null) {
                this.mCallbackExecutor.execute(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        InlineSuggestion.InlineContentCallbackImpl.this.lambda$handleOnFirstContentReceived$1();
                    }
                });
                return;
            }
            InlineContentView inlineContentView = new InlineContentView(this.mContext);
            this.mView = inlineContentView;
            if (this.mInlineTooltipUi != null) {
                inlineContentView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: android.view.inputmethod.InlineSuggestion.InlineContentCallbackImpl.1
                    @Override // android.view.View.OnLayoutChangeListener
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if (InlineContentCallbackImpl.this.mInlineTooltipUi != null) {
                            InlineContentCallbackImpl.this.mInlineTooltipUi.update(InlineContentCallbackImpl.this.mView);
                        }
                    }
                });
            }
            this.mView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            this.mView.setChildSurfacePackageUpdater(getSurfacePackageUpdater());
            this.mCallbackExecutor.execute(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    InlineSuggestion.InlineContentCallbackImpl.this.lambda$handleOnFirstContentReceived$2();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleOnFirstContentReceived$1() {
            this.mCallback.accept(null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$handleOnFirstContentReceived$2() {
            this.mCallback.accept(this.mView);
        }

        private void handleOnSurfacePackage(SurfaceControlViewHost.SurfacePackage surfacePackage) {
            Consumer<SurfaceControlViewHost.SurfacePackage> consumer;
            if (surfacePackage == null) {
                return;
            }
            if (this.mSurfacePackage != null || (consumer = this.mSurfacePackageConsumer) == null) {
                surfacePackage.release();
                try {
                    this.mInlineContentProvider.onSurfacePackageReleased();
                    return;
                } catch (RemoteException e) {
                    Slog.m90w(InlineSuggestion.TAG, "Error calling onSurfacePackageReleased(): " + e);
                    return;
                }
            }
            this.mSurfacePackage = surfacePackage;
            if (surfacePackage != null && consumer != null) {
                consumer.accept(surfacePackage);
                this.mSurfacePackageConsumer = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleOnSurfacePackageReleased() {
            if (this.mSurfacePackage != null) {
                try {
                    this.mInlineContentProvider.onSurfacePackageReleased();
                } catch (RemoteException e) {
                    Slog.m90w(InlineSuggestion.TAG, "Error calling onSurfacePackageReleased(): " + e);
                }
                this.mSurfacePackage = null;
            }
            this.mSurfacePackageConsumer = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleGetSurfacePackage(Consumer<SurfaceControlViewHost.SurfacePackage> consumer) {
            SurfaceControlViewHost.SurfacePackage surfacePackage = this.mSurfacePackage;
            if (surfacePackage != null) {
                consumer.accept(surfacePackage);
                return;
            }
            this.mSurfacePackageConsumer = consumer;
            try {
                this.mInlineContentProvider.requestSurfacePackage();
            } catch (RemoteException e) {
                Slog.m90w(InlineSuggestion.TAG, "Error calling getSurfacePackage(): " + e);
                consumer.accept(null);
                this.mSurfacePackageConsumer = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$2 */
        /* loaded from: classes4.dex */
        public class C36412 implements InlineContentView.SurfacePackageUpdater {
            C36412() {
            }

            @Override // android.widget.inline.InlineContentView.SurfacePackageUpdater
            public void onSurfacePackageReleased() {
                InlineContentCallbackImpl.this.mMainHandler.post(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$2$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        InlineSuggestion.InlineContentCallbackImpl.C36412.this.lambda$onSurfacePackageReleased$0();
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onSurfacePackageReleased$0() {
                InlineContentCallbackImpl.this.handleOnSurfacePackageReleased();
            }

            @Override // android.widget.inline.InlineContentView.SurfacePackageUpdater
            public void getSurfacePackage(final Consumer<SurfaceControlViewHost.SurfacePackage> consumer) {
                InlineContentCallbackImpl.this.mMainHandler.post(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        InlineSuggestion.InlineContentCallbackImpl.C36412.this.lambda$getSurfacePackage$1(consumer);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$getSurfacePackage$1(Consumer consumer) {
                InlineContentCallbackImpl.this.handleGetSurfacePackage(consumer);
            }
        }

        private InlineContentView.SurfacePackageUpdater getSurfacePackageUpdater() {
            return new C36412();
        }

        public void onClick() {
            this.mMainHandler.post(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    InlineSuggestion.InlineContentCallbackImpl.this.lambda$onClick$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onClick$3() {
            InlineContentView inlineContentView = this.mView;
            if (inlineContentView != null && inlineContentView.hasOnClickListeners()) {
                this.mView.callOnClick();
            }
        }

        public void onLongClick() {
            this.mMainHandler.post(new Runnable() { // from class: android.view.inputmethod.InlineSuggestion$InlineContentCallbackImpl$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    InlineSuggestion.InlineContentCallbackImpl.this.lambda$onLongClick$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLongClick$4() {
            InlineContentView inlineContentView = this.mView;
            if (inlineContentView != null && inlineContentView.hasOnLongClickListeners()) {
                this.mView.performLongClick();
            }
        }
    }

    /* loaded from: classes4.dex */
    private static class InlineContentCallbackImplParceling implements Parcelling<InlineContentCallbackImpl> {
        private InlineContentCallbackImplParceling() {
        }

        @Override // com.android.internal.util.Parcelling
        public void parcel(InlineContentCallbackImpl item, Parcel dest, int parcelFlags) {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.util.Parcelling
        public InlineContentCallbackImpl unparcel(Parcel source) {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    private static class InlineTooltipUiParceling implements Parcelling<InlineTooltipUi> {
        private InlineTooltipUiParceling() {
        }

        @Override // com.android.internal.util.Parcelling
        public void parcel(InlineTooltipUi item, Parcel dest, int parcelFlags) {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.internal.util.Parcelling
        public InlineTooltipUi unparcel(Parcel source) {
            return null;
        }
    }

    public InlineSuggestion(InlineSuggestionInfo info, IInlineContentProvider contentProvider, InlineContentCallbackImpl inlineContentCallback, InlineTooltipUi inlineTooltipUi) {
        this.mInfo = info;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) info);
        this.mContentProvider = contentProvider;
        this.mInlineContentCallback = inlineContentCallback;
        this.mInlineTooltipUi = inlineTooltipUi;
    }

    public InlineSuggestionInfo getInfo() {
        return this.mInfo;
    }

    public IInlineContentProvider getContentProvider() {
        return this.mContentProvider;
    }

    public InlineContentCallbackImpl getInlineContentCallback() {
        return this.mInlineContentCallback;
    }

    public InlineTooltipUi getInlineTooltipUi() {
        return this.mInlineTooltipUi;
    }

    public String toString() {
        return "InlineSuggestion { info = " + this.mInfo + ", contentProvider = " + this.mContentProvider + ", inlineContentCallback = " + this.mInlineContentCallback + ", inlineTooltipUi = " + this.mInlineTooltipUi + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineSuggestion that = (InlineSuggestion) o;
        if (Objects.equals(this.mInfo, that.mInfo) && Objects.equals(this.mContentProvider, that.mContentProvider) && Objects.equals(this.mInlineContentCallback, that.mInlineContentCallback) && Objects.equals(this.mInlineTooltipUi, that.mInlineTooltipUi)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + Objects.hashCode(this.mInfo);
        return (((((_hash * 31) + Objects.hashCode(this.mContentProvider)) * 31) + Objects.hashCode(this.mInlineContentCallback)) * 31) + Objects.hashCode(this.mInlineTooltipUi);
    }

    static {
        Parcelling<InlineContentCallbackImpl> parcelling = Parcelling.Cache.get(InlineContentCallbackImplParceling.class);
        sParcellingForInlineContentCallback = parcelling;
        if (parcelling == null) {
            sParcellingForInlineContentCallback = Parcelling.Cache.put(new InlineContentCallbackImplParceling());
        }
        Parcelling<InlineTooltipUi> parcelling2 = Parcelling.Cache.get(InlineTooltipUiParceling.class);
        sParcellingForInlineTooltipUi = parcelling2;
        if (parcelling2 == null) {
            sParcellingForInlineTooltipUi = Parcelling.Cache.put(new InlineTooltipUiParceling());
        }
        CREATOR = new Parcelable.Creator<InlineSuggestion>() { // from class: android.view.inputmethod.InlineSuggestion.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public InlineSuggestion[] newArray(int size) {
                return new InlineSuggestion[size];
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public InlineSuggestion createFromParcel(Parcel in) {
                return new InlineSuggestion(in);
            }
        };
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mContentProvider != null ? (byte) (0 | 2) : (byte) 0;
        if (this.mInlineContentCallback != null) {
            flg = (byte) (flg | 4);
        }
        if (this.mInlineTooltipUi != null) {
            flg = (byte) (flg | 8);
        }
        dest.writeByte(flg);
        dest.writeTypedObject(this.mInfo, flags);
        IInlineContentProvider iInlineContentProvider = this.mContentProvider;
        if (iInlineContentProvider != null) {
            dest.writeStrongInterface(iInlineContentProvider);
        }
        sParcellingForInlineContentCallback.parcel(this.mInlineContentCallback, dest, flags);
        sParcellingForInlineTooltipUi.parcel(this.mInlineTooltipUi, dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    InlineSuggestion(Parcel in) {
        byte flg = in.readByte();
        InlineSuggestionInfo info = (InlineSuggestionInfo) in.readTypedObject(InlineSuggestionInfo.CREATOR);
        IInlineContentProvider contentProvider = (flg & 2) == 0 ? null : IInlineContentProvider.Stub.asInterface(in.readStrongBinder());
        InlineContentCallbackImpl inlineContentCallback = sParcellingForInlineContentCallback.unparcel(in);
        InlineTooltipUi inlineTooltipUi = sParcellingForInlineTooltipUi.unparcel(in);
        this.mInfo = info;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) info);
        this.mContentProvider = contentProvider;
        this.mInlineContentCallback = inlineContentCallback;
        this.mInlineTooltipUi = inlineTooltipUi;
    }

    @Deprecated
    private void __metadata() {
    }
}
