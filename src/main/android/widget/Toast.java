package android.widget;

import android.app.INotificationManager;
import android.app.ITransientNotification;
import android.app.ITransientNotificationCallback;
import android.compat.Compatibility;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.IAccessibilityManager;
import android.widget.Toast;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes4.dex */
public class Toast {
    private static final long CHANGE_TEXT_TOASTS_IN_THE_SYSTEM = 147798919;
    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;
    static final String TAG = "Toast";
    static final boolean localLOGV = false;
    private static INotificationManager sService;
    private final List<Callback> mCallbacks;
    private final Context mContext;
    int mDuration;
    private final Handler mHandler;
    private View mNextView;
    final ITransientNotification$StubC3903TN mTN;
    private CharSequence mText;
    private final Binder mToken;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface Duration {
    }

    public Toast(Context context) {
        this(context, null);
    }

    public Toast(Context context, Looper looper) {
        this.mContext = context;
        Binder binder = new Binder();
        this.mToken = binder;
        Looper looper2 = getLooper(looper);
        this.mHandler = new Handler(looper2);
        ArrayList arrayList = new ArrayList();
        this.mCallbacks = arrayList;
        ITransientNotification$StubC3903TN iTransientNotification$StubC3903TN = new ITransientNotification$StubC3903TN(context, context.getPackageName(), binder, arrayList, looper2);
        this.mTN = iTransientNotification$StubC3903TN;
        iTransientNotification$StubC3903TN.f545mY = context.getResources().getDimensionPixelSize(C4057R.dimen.toast_y_offset);
        iTransientNotification$StubC3903TN.mGravity = context.getResources().getInteger(C4057R.integer.config_toastDefaultGravity);
    }

    private Looper getLooper(Looper looper) {
        if (looper != null) {
            return looper;
        }
        return (Looper) Preconditions.checkNotNull(Looper.myLooper(), "Can't toast on a thread that has not called Looper.prepare()");
    }

    public void show() {
        if (Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM)) {
            Preconditions.checkState((this.mNextView == null && this.mText == null) ? false : true, "You must either set a text or a view");
        } else if (this.mNextView == null) {
            throw new RuntimeException("setView must have been called");
        }
        INotificationManager service = getService();
        String pkg = this.mContext.getOpPackageName();
        ITransientNotification$StubC3903TN tn = this.mTN;
        tn.mNextView = this.mNextView;
        int displayId = this.mContext.getDisplayId();
        try {
            if (Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM)) {
                if (this.mNextView != null) {
                    service.enqueueToast(pkg, this.mToken, tn, this.mDuration, displayId);
                } else {
                    ITransientNotificationCallback callback = new CallbackBinder(this.mCallbacks, this.mHandler);
                    service.enqueueTextToast(pkg, this.mToken, this.mText, this.mDuration, displayId, callback);
                }
            } else {
                service.enqueueToast(pkg, this.mToken, tn, this.mDuration, displayId);
            }
        } catch (RemoteException e) {
        }
    }

    public void cancel() {
        if (Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM) && this.mNextView == null) {
            try {
                getService().cancelToast(this.mContext.getOpPackageName(), this.mToken);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        this.mTN.cancel();
    }

    @Deprecated
    public void setView(View view) {
        this.mNextView = view;
    }

    @Deprecated
    public View getView() {
        return this.mNextView;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
        this.mTN.mDuration = duration;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "setMargin() shouldn't be called on text toasts, the values won't be used");
        }
        this.mTN.mHorizontalMargin = horizontalMargin;
        this.mTN.mVerticalMargin = verticalMargin;
    }

    public float getHorizontalMargin() {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "getHorizontalMargin() shouldn't be called on text toasts, the result may not reflect actual values.");
        }
        return this.mTN.mHorizontalMargin;
    }

    public float getVerticalMargin() {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "getVerticalMargin() shouldn't be called on text toasts, the result may not reflect actual values.");
        }
        return this.mTN.mVerticalMargin;
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "setGravity() shouldn't be called on text toasts, the values won't be used");
        }
        this.mTN.mGravity = gravity;
        this.mTN.f544mX = xOffset;
        this.mTN.f545mY = yOffset;
    }

    public int getGravity() {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "getGravity() shouldn't be called on text toasts, the result may not reflect actual values.");
        }
        return this.mTN.mGravity;
    }

    public int getXOffset() {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "getXOffset() shouldn't be called on text toasts, the result may not reflect actual values.");
        }
        return this.mTN.f544mX;
    }

    public int getYOffset() {
        if (isSystemRenderedTextToast()) {
            Log.m110e(TAG, "getYOffset() shouldn't be called on text toasts, the result may not reflect actual values.");
        }
        return this.mTN.f545mY;
    }

    private boolean isSystemRenderedTextToast() {
        return Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM) && this.mNextView == null;
    }

    public void addCallback(Callback callback) {
        Preconditions.checkNotNull(callback);
        synchronized (this.mCallbacks) {
            this.mCallbacks.add(callback);
        }
    }

    public void removeCallback(Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
        }
    }

    public WindowManager.LayoutParams getWindowParams() {
        if (Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM)) {
            if (this.mNextView != null) {
                return this.mTN.mParams;
            }
            return null;
        }
        return this.mTN.mParams;
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        return makeText(context, null, text, duration);
    }

    public static Toast makeText(Context context, Looper looper, CharSequence text, int duration) {
        Toast result = new Toast(context, looper);
        if (Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM)) {
            result.mText = text;
        } else {
            result.mNextView = ToastPresenter.getTextToastView(context, text);
        }
        result.mDuration = duration;
        return result;
    }

    public static Toast makeCustomToastWithIcon(Context context, Looper looper, CharSequence text, int duration, Drawable icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Drawable icon should not be null for makeCustomToastWithIcon");
        }
        Toast result = new Toast(context, looper);
        result.mNextView = ToastPresenter.getTextToastViewWithIcon(context, text, icon);
        result.mDuration = duration;
        return result;
    }

    public static Toast makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void setText(int resId) {
        setText(this.mContext.getText(resId));
    }

    public void setText(CharSequence s) {
        if (Compatibility.isChangeEnabled((long) CHANGE_TEXT_TOASTS_IN_THE_SYSTEM)) {
            if (this.mNextView != null) {
                throw new IllegalStateException("Text provided for custom toast, remove previous setView() calls if you want a text toast instead.");
            }
            this.mText = s;
            return;
        }
        View view = this.mNextView;
        if (view == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        TextView tv = (TextView) view.findViewById(16908299);
        if (tv == null) {
            throw new RuntimeException("This Toast was not created with Toast.makeText()");
        }
        tv.setText(s);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static INotificationManager getService() {
        INotificationManager iNotificationManager = sService;
        if (iNotificationManager != null) {
            return iNotificationManager;
        }
        INotificationManager asInterface = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
        sService = asInterface;
        return asInterface;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.widget.Toast$TN */
    /* loaded from: classes4.dex */
    public static class ITransientNotification$StubC3903TN extends ITransientNotification.Stub {
        private static final int CANCEL = 2;
        private static final int HIDE = 1;
        private static final int SHOW = 0;
        private final List<Callback> mCallbacks;
        int mDuration;
        int mGravity;
        final Handler mHandler;
        float mHorizontalMargin;
        View mNextView;
        final String mPackageName;
        private final WindowManager.LayoutParams mParams;
        private final ToastPresenter mPresenter;
        final Binder mToken;
        float mVerticalMargin;
        View mView;
        WindowManager mWM;

        /* renamed from: mX */
        int f544mX;

        /* renamed from: mY */
        int f545mY;

        ITransientNotification$StubC3903TN(Context context, String packageName, Binder token, List<Callback> callbacks, Looper looper) {
            IAccessibilityManager accessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
            ToastPresenter toastPresenter = new ToastPresenter(context, accessibilityManager, Toast.getService(), packageName);
            this.mPresenter = toastPresenter;
            this.mParams = toastPresenter.getLayoutParams();
            this.mPackageName = packageName;
            this.mToken = token;
            this.mCallbacks = callbacks;
            this.mHandler = new Handler(looper, null) { // from class: android.widget.Toast.TN.1
                @Override // android.p008os.Handler
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            IBinder token2 = (IBinder) msg.obj;
                            ITransientNotification$StubC3903TN.this.handleShow(token2);
                            return;
                        case 1:
                            ITransientNotification$StubC3903TN.this.handleHide();
                            ITransientNotification$StubC3903TN.this.mNextView = null;
                            return;
                        case 2:
                            ITransientNotification$StubC3903TN.this.handleHide();
                            ITransientNotification$StubC3903TN.this.mNextView = null;
                            try {
                                Toast.getService().cancelToast(ITransientNotification$StubC3903TN.this.mPackageName, ITransientNotification$StubC3903TN.this.mToken);
                                return;
                            } catch (RemoteException e) {
                                return;
                            }
                        default:
                            return;
                    }
                }
            };
        }

        private List<Callback> getCallbacks() {
            ArrayList arrayList;
            synchronized (this.mCallbacks) {
                arrayList = new ArrayList(this.mCallbacks);
            }
            return arrayList;
        }

        @Override // android.app.ITransientNotification
        public void show(IBinder windowToken) {
            this.mHandler.obtainMessage(0, windowToken).sendToTarget();
        }

        @Override // android.app.ITransientNotification
        public void hide() {
            this.mHandler.obtainMessage(1).sendToTarget();
        }

        public void cancel() {
            this.mHandler.obtainMessage(2).sendToTarget();
        }

        public void handleShow(IBinder windowToken) {
            if (!this.mHandler.hasMessages(2) && !this.mHandler.hasMessages(1) && this.mView != this.mNextView) {
                handleHide();
                View view = this.mNextView;
                this.mView = view;
                this.mPresenter.show(view, this.mToken, windowToken, this.mDuration, this.mGravity, this.f544mX, this.f545mY, this.mHorizontalMargin, this.mVerticalMargin, new CallbackBinder(getCallbacks(), this.mHandler));
            }
        }

        public void handleHide() {
            View view = this.mView;
            if (view != null) {
                Preconditions.checkState(view == this.mPresenter.getView(), "Trying to hide toast view different than the last one displayed");
                this.mPresenter.hide(new CallbackBinder(getCallbacks(), this.mHandler));
                this.mView = null;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Callback {
        public void onToastShown() {
        }

        public void onToastHidden() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class CallbackBinder extends ITransientNotificationCallback.Stub {
        private final List<Callback> mCallbacks;
        private final Handler mHandler;

        private CallbackBinder(List<Callback> callbacks, Handler handler) {
            this.mCallbacks = callbacks;
            this.mHandler = handler;
        }

        @Override // android.app.ITransientNotificationCallback
        public void onToastShown() {
            this.mHandler.post(new Runnable() { // from class: android.widget.Toast$CallbackBinder$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Toast.CallbackBinder.this.lambda$onToastShown$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onToastShown$0() {
            for (Callback callback : getCallbacks()) {
                callback.onToastShown();
            }
        }

        @Override // android.app.ITransientNotificationCallback
        public void onToastHidden() {
            this.mHandler.post(new Runnable() { // from class: android.widget.Toast$CallbackBinder$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Toast.CallbackBinder.this.lambda$onToastHidden$1();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onToastHidden$1() {
            for (Callback callback : getCallbacks()) {
                callback.onToastHidden();
            }
        }

        private List<Callback> getCallbacks() {
            ArrayList arrayList;
            synchronized (this.mCallbacks) {
                arrayList = new ArrayList(this.mCallbacks);
            }
            return arrayList;
        }
    }
}
