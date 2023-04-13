package android.app;

import android.annotation.SystemApi;
import android.app.ActivityManager;
import android.compat.Compatibility;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p001pm.ParceledListSlice;
import android.content.p001pm.ResolveInfo;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.AndroidException;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.proto.ProtoOutputStream;
import com.android.internal.p028os.IResultReceiver;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class PendingIntent implements Parcelable {
    public static final long BLOCK_MUTABLE_IMPLICIT_PENDING_INTENT = 236704164;
    public static final int FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT = 16777216;
    public static final int FLAG_CANCEL_CURRENT = 268435456;
    public static final int FLAG_IMMUTABLE = 67108864;
    public static final int FLAG_MUTABLE = 33554432;
    @Deprecated
    public static final int FLAG_MUTABLE_UNAUDITED = 33554432;
    public static final int FLAG_NO_CREATE = 536870912;
    public static final int FLAG_ONE_SHOT = 1073741824;
    public static final int FLAG_UPDATE_CURRENT = 134217728;
    static final long PENDING_INTENT_EXPLICIT_MUTABILITY_REQUIRED = 160794467;
    private static final String TAG = "PendingIntent";
    private ActivityManager.PendingIntentInfo mCachedInfo;
    private CancelListerInfo mCancelListerInfo;
    private final IIntentSender mTarget;
    private IBinder mWhitelistToken;
    private static final ThreadLocal<OnMarshaledListener> sOnMarshaledListener = new ThreadLocal<>();
    public static final Parcelable.Creator<PendingIntent> CREATOR = new Parcelable.Creator<PendingIntent>() { // from class: android.app.PendingIntent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PendingIntent createFromParcel(Parcel in) {
            IBinder target = in.readStrongBinder();
            if (target != null) {
                return new PendingIntent(target, in.getClassCookie(PendingIntent.class));
            }
            return null;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PendingIntent[] newArray(int size) {
            return new PendingIntent[size];
        }
    };

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    /* loaded from: classes.dex */
    public interface CancelListener {
        void onCanceled(PendingIntent pendingIntent);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Flags {
    }

    /* loaded from: classes.dex */
    public interface OnFinished {
        void onSendFinished(PendingIntent pendingIntent, Intent intent, int i, String str, Bundle bundle);
    }

    /* loaded from: classes.dex */
    public interface OnMarshaledListener {
        void onMarshaled(PendingIntent pendingIntent, Parcel parcel, int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class CancelListerInfo extends IResultReceiver.Stub {
        private final ArraySet<Pair<Executor, CancelListener>> mCancelListeners;
        private boolean mCanceled;

        private CancelListerInfo() {
            this.mCancelListeners = new ArraySet<>();
        }

        @Override // com.android.internal.p028os.IResultReceiver
        public void send(int resultCode, Bundle resultData) throws RemoteException {
            PendingIntent.this.notifyCancelListeners();
        }
    }

    /* loaded from: classes.dex */
    public static class CanceledException extends AndroidException {
        public CanceledException() {
        }

        public CanceledException(String name) {
            super(name);
        }

        public CanceledException(Exception cause) {
            super(cause);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class FinishedDispatcher extends IIntentReceiver.Stub implements Runnable {
        private static Handler sDefaultSystemHandler;
        private final Handler mHandler;
        private Intent mIntent;
        private final PendingIntent mPendingIntent;
        private int mResultCode;
        private String mResultData;
        private Bundle mResultExtras;
        private final OnFinished mWho;

        FinishedDispatcher(PendingIntent pi, OnFinished who, Handler handler) {
            this.mPendingIntent = pi;
            this.mWho = who;
            if (handler == null && ActivityThread.isSystem()) {
                if (sDefaultSystemHandler == null) {
                    sDefaultSystemHandler = new Handler(Looper.getMainLooper());
                }
                this.mHandler = sDefaultSystemHandler;
                return;
            }
            this.mHandler = handler;
        }

        @Override // android.content.IIntentReceiver
        public void performReceive(Intent intent, int resultCode, String data, Bundle extras, boolean serialized, boolean sticky, int sendingUser) {
            this.mIntent = intent;
            this.mResultCode = resultCode;
            this.mResultData = data;
            this.mResultExtras = extras;
            Handler handler = this.mHandler;
            if (handler == null) {
                run();
            } else {
                handler.post(this);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mWho.onSendFinished(this.mPendingIntent, this.mIntent, this.mResultCode, this.mResultData, this.mResultExtras);
        }
    }

    public static void setOnMarshaledListener(OnMarshaledListener listener) {
        sOnMarshaledListener.set(listener);
    }

    private static void checkPendingIntent(int flags, Intent intent, Context context) {
        boolean isFlagImmutableSet = (67108864 & flags) != 0;
        boolean isFlagMutableSet = (33554432 & flags) != 0;
        String packageName = context.getPackageName();
        if (isFlagImmutableSet && isFlagMutableSet) {
            throw new IllegalArgumentException("Cannot set both FLAG_IMMUTABLE and FLAG_MUTABLE for PendingIntent");
        }
        if (Compatibility.isChangeEnabled((long) PENDING_INTENT_EXPLICIT_MUTABILITY_REQUIRED) && !isFlagImmutableSet && !isFlagMutableSet) {
            String msg = packageName + ": Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.\nStrongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable, e.g. if it needs to be used with inline replies or bubbles.";
            throw new IllegalArgumentException(msg);
        } else if (isNewMutableDisallowedImplicitPendingIntent(flags, intent) && !Compatibility.isChangeEnabled((long) BLOCK_MUTABLE_IMPLICIT_PENDING_INTENT)) {
            String msg2 = "New mutable implicit PendingIntent: pkg=" + packageName + ", action=" + intent.getAction() + ", featureId=" + context.getAttributionTag() + ". This will be blocked once the app targets U+ for security reasons.";
            Log.m102w(TAG, new StackTrace(msg2));
        }
    }

    public static boolean isNewMutableDisallowedImplicitPendingIntent(int flags, Intent intent) {
        boolean isFlagNoCreateSet = (536870912 & flags) != 0;
        boolean isFlagMutableSet = (33554432 & flags) != 0;
        boolean isImplicit = intent.getComponent() == null && intent.getPackage() == null;
        boolean isFlagAllowUnsafeImplicitIntentSet = (16777216 & flags) != 0;
        return !isFlagNoCreateSet && isFlagMutableSet && isImplicit && !isFlagAllowUnsafeImplicitIntentSet;
    }

    public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags) {
        return getActivity(context, requestCode, intent, flags, null);
    }

    public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags, Bundle options) {
        UserHandle user = context.getUser();
        return getActivityAsUser(context, requestCode, intent, flags, options, user != null ? user : UserHandle.m145of(context.getUserId()));
    }

    public static PendingIntent getActivityAsUser(Context context, int requestCode, Intent intent, int flags, Bundle options, UserHandle user) {
        String packageName = context.getPackageName();
        String resolvedType = intent.resolveTypeIfNeeded(context.getContentResolver());
        checkPendingIntent(flags, intent, context);
        try {
            intent.migrateExtraStreamToClipData(context);
            intent.prepareToLeaveProcess(context);
            IIntentSender target = ActivityManager.getService().getIntentSenderWithFeature(2, packageName, context.getAttributionTag(), null, null, requestCode, new Intent[]{intent}, resolvedType != null ? new String[]{resolvedType} : null, flags, options, user.getIdentifier());
            if (target != null) {
                return new PendingIntent(target);
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static PendingIntent getActivities(Context context, int requestCode, Intent[] intents, int flags) {
        return getActivities(context, requestCode, intents, flags, null);
    }

    public static PendingIntent getActivities(Context context, int requestCode, Intent[] intents, int flags, Bundle options) {
        UserHandle user = context.getUser();
        return getActivitiesAsUser(context, requestCode, intents, flags, options, user != null ? user : UserHandle.m145of(context.getUserId()));
    }

    public static PendingIntent getActivitiesAsUser(Context context, int requestCode, Intent[] intents, int flags, Bundle options, UserHandle user) {
        String packageName = context.getPackageName();
        String[] resolvedTypes = new String[intents.length];
        for (int i = 0; i < intents.length; i++) {
            intents[i].migrateExtraStreamToClipData(context);
            intents[i].prepareToLeaveProcess(context);
            resolvedTypes[i] = intents[i].resolveTypeIfNeeded(context.getContentResolver());
            checkPendingIntent(flags, intents[i], context);
        }
        try {
        } catch (RemoteException e) {
            e = e;
        }
        try {
            IIntentSender target = ActivityManager.getService().getIntentSenderWithFeature(2, packageName, context.getAttributionTag(), null, null, requestCode, intents, resolvedTypes, flags, options, user.getIdentifier());
            if (target != null) {
                return new PendingIntent(target);
            }
            return null;
        } catch (RemoteException e2) {
            e = e2;
            throw e.rethrowFromSystemServer();
        }
    }

    public static PendingIntent getBroadcast(Context context, int requestCode, Intent intent, int flags) {
        return getBroadcastAsUser(context, requestCode, intent, flags, context.getUser());
    }

    public static PendingIntent getBroadcastAsUser(Context context, int requestCode, Intent intent, int flags, UserHandle userHandle) {
        String packageName = context.getPackageName();
        String resolvedType = intent.resolveTypeIfNeeded(context.getContentResolver());
        checkPendingIntent(flags, intent, context);
        try {
            intent.prepareToLeaveProcess(context);
            IIntentSender target = ActivityManager.getService().getIntentSenderWithFeature(1, packageName, context.getAttributionTag(), null, null, requestCode, new Intent[]{intent}, resolvedType != null ? new String[]{resolvedType} : null, flags, null, userHandle.getIdentifier());
            if (target != null) {
                return new PendingIntent(target);
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static PendingIntent getService(Context context, int requestCode, Intent intent, int flags) {
        return buildServicePendingIntent(context, requestCode, intent, flags, 4);
    }

    public static PendingIntent getForegroundService(Context context, int requestCode, Intent intent, int flags) {
        return buildServicePendingIntent(context, requestCode, intent, flags, 5);
    }

    private static PendingIntent buildServicePendingIntent(Context context, int requestCode, Intent intent, int flags, int serviceKind) {
        String packageName = context.getPackageName();
        String resolvedType = intent.resolveTypeIfNeeded(context.getContentResolver());
        checkPendingIntent(flags, intent, context);
        try {
            intent.prepareToLeaveProcess(context);
            IIntentSender target = ActivityManager.getService().getIntentSenderWithFeature(serviceKind, packageName, context.getAttributionTag(), null, null, requestCode, new Intent[]{intent}, resolvedType != null ? new String[]{resolvedType} : null, flags, null, context.getUserId());
            if (target != null) {
                return new PendingIntent(target);
            }
            return null;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IntentSender getIntentSender() {
        return new IntentSender(this.mTarget, this.mWhitelistToken);
    }

    public void cancel() {
        try {
            ActivityManager.getService().cancelIntentSender(this.mTarget);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void send() throws CanceledException {
        send(null, 0, null, null, null, null, null);
    }

    public void send(int code) throws CanceledException {
        send(null, code, null, null, null, null, null);
    }

    public void send(Context context, int code, Intent intent) throws CanceledException {
        send(context, code, intent, null, null, null, null);
    }

    public void send(Bundle options) throws CanceledException {
        send(null, 0, null, null, null, null, options);
    }

    public void send(int code, OnFinished onFinished, Handler handler) throws CanceledException {
        send(null, code, null, onFinished, handler, null, null);
    }

    public void send(Context context, int code, Intent intent, OnFinished onFinished, Handler handler) throws CanceledException {
        send(context, code, intent, onFinished, handler, null, null);
    }

    public void send(Context context, int code, Intent intent, OnFinished onFinished, Handler handler, String requiredPermission) throws CanceledException {
        send(context, code, intent, onFinished, handler, requiredPermission, null);
    }

    public void send(Context context, int code, Intent intent, OnFinished onFinished, Handler handler, String requiredPermission, Bundle options) throws CanceledException {
        if (sendAndReturnResult(context, code, intent, onFinished, handler, requiredPermission, options) < 0) {
            throw new CanceledException();
        }
    }

    public int sendAndReturnResult(Context context, int code, Intent intent, OnFinished onFinished, Handler handler, String requiredPermission, Bundle options) throws CanceledException {
        String resolvedType;
        Bundle options2;
        FinishedDispatcher finishedDispatcher;
        if (intent != null) {
            try {
                resolvedType = intent.resolveTypeIfNeeded(context.getContentResolver());
            } catch (RemoteException e) {
                e = e;
                throw new CanceledException(e);
            }
        } else {
            resolvedType = null;
        }
        if (context != null && isActivity()) {
            ActivityOptions activityOptions = options != null ? new ActivityOptions(options) : ActivityOptions.makeBasic();
            activityOptions.setCallerDisplayId(context.getDisplayId());
            options2 = activityOptions.toBundle();
        } else {
            options2 = options;
        }
        try {
            IApplicationThread app = ActivityThread.currentActivityThread().getApplicationThread();
            IActivityManager service = ActivityManager.getService();
            IIntentSender iIntentSender = this.mTarget;
            IBinder iBinder = this.mWhitelistToken;
            if (onFinished != null) {
                try {
                    finishedDispatcher = new FinishedDispatcher(this, onFinished, handler);
                } catch (RemoteException e2) {
                    e = e2;
                    throw new CanceledException(e);
                }
            } else {
                finishedDispatcher = null;
            }
            return service.sendIntentSender(app, iIntentSender, iBinder, code, intent, resolvedType, finishedDispatcher, requiredPermission, options2);
        } catch (RemoteException e3) {
            e = e3;
        }
    }

    @Deprecated
    public String getTargetPackage() {
        return getCreatorPackage();
    }

    public String getCreatorPackage() {
        return getCachedInfo().getCreatorPackage();
    }

    public int getCreatorUid() {
        return getCachedInfo().getCreatorUid();
    }

    @Deprecated
    public void registerCancelListener(CancelListener cancelListener) {
        if (!addCancelListener(new PendingIntent$$ExternalSyntheticLambda1(), cancelListener)) {
            cancelListener.onCanceled(this);
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean addCancelListener(Executor executor, CancelListener cancelListener) {
        synchronized (this.mTarget) {
            CancelListerInfo cancelListerInfo = this.mCancelListerInfo;
            if (cancelListerInfo == null || !cancelListerInfo.mCanceled) {
                if (this.mCancelListerInfo == null) {
                    this.mCancelListerInfo = new CancelListerInfo();
                }
                CancelListerInfo cli = this.mCancelListerInfo;
                boolean wasEmpty = cli.mCancelListeners.isEmpty();
                cli.mCancelListeners.add(Pair.create(executor, cancelListener));
                if (wasEmpty) {
                    try {
                        boolean success = ActivityManager.getService().registerIntentSenderCancelListenerEx(this.mTarget, cli);
                        if (!success) {
                            cli.mCanceled = true;
                        }
                        return success;
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
                return cli.mCanceled ? false : true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCancelListeners() {
        ArraySet<Pair<Executor, CancelListener>> cancelListeners;
        synchronized (this.mTarget) {
            CancelListerInfo cli = this.mCancelListerInfo;
            cli.mCanceled = true;
            cancelListeners = new ArraySet<>((ArraySet<Pair<Executor, CancelListener>>) cli.mCancelListeners);
            cli.mCancelListeners.clear();
        }
        int size = cancelListeners.size();
        for (int i = 0; i < size; i++) {
            final Pair<Executor, CancelListener> pair = cancelListeners.valueAt(i);
            ((Executor) pair.first).execute(new Runnable() { // from class: android.app.PendingIntent$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    PendingIntent.this.lambda$notifyCancelListeners$0(pair);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyCancelListeners$0(Pair pair) {
        ((CancelListener) pair.second).onCanceled(this);
    }

    @Deprecated
    public void unregisterCancelListener(CancelListener cancelListener) {
        removeCancelListener(cancelListener);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void removeCancelListener(CancelListener cancelListener) {
        synchronized (this.mTarget) {
            CancelListerInfo cli = this.mCancelListerInfo;
            if (cli != null && cli.mCancelListeners.size() != 0) {
                for (int i = cli.mCancelListeners.size() - 1; i >= 0; i--) {
                    if (((Pair) cli.mCancelListeners.valueAt(i)).second == cancelListener) {
                        cli.mCancelListeners.removeAt(i);
                    }
                }
                if (cli.mCancelListeners.isEmpty()) {
                    try {
                        ActivityManager.getService().unregisterIntentSenderCancelListener(this.mTarget, cli);
                    } catch (RemoteException e) {
                        throw e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }

    public UserHandle getCreatorUserHandle() {
        int uid = getCachedInfo().getCreatorUid();
        return UserHandle.getUserHandleForUid(uid);
    }

    public boolean isTargetedToPackage() {
        try {
            return ActivityManager.getService().isIntentSenderTargetedToPackage(this.mTarget);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isImmutable() {
        return getCachedInfo().isImmutable();
    }

    public boolean isActivity() {
        return getCachedInfo().getIntentSenderType() == 2;
    }

    public boolean isForegroundService() {
        return getCachedInfo().getIntentSenderType() == 5;
    }

    public boolean isService() {
        return getCachedInfo().getIntentSenderType() == 4;
    }

    public boolean isBroadcast() {
        return getCachedInfo().getIntentSenderType() == 1;
    }

    public Intent getIntent() {
        try {
            return ActivityManager.getService().getIntentForIntentSender(this.mTarget);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getTag(String prefix) {
        try {
            return ActivityManager.getService().getTagForIntentSender(this.mTarget, prefix);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public List<ResolveInfo> queryIntentComponents(int flags) {
        try {
            ParceledListSlice<ResolveInfo> parceledList = ActivityManager.getService().queryIntentComponentsForIntentSender(this.mTarget, flags);
            if (parceledList == null) {
                return Collections.emptyList();
            }
            return parceledList.getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public boolean intentFilterEquals(PendingIntent other) {
        if (other == null) {
            return false;
        }
        try {
            return ActivityManager.getService().getIntentForIntentSender(other.mTarget).filterEquals(getIntent());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean equals(Object otherObj) {
        if (otherObj instanceof PendingIntent) {
            return this.mTarget.asBinder().equals(((PendingIntent) otherObj).mTarget.asBinder());
        }
        return false;
    }

    public int hashCode() {
        return this.mTarget.asBinder().hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("PendingIntent{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(": ");
        sb.append(this.mTarget.asBinder());
        sb.append('}');
        return sb.toString();
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, this.mTarget.asBinder().toString());
        proto.end(token);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeStrongBinder(this.mTarget.asBinder());
        OnMarshaledListener listener = sOnMarshaledListener.get();
        if (listener != null) {
            listener.onMarshaled(this, out, flags);
        }
    }

    public static void writePendingIntentOrNullToParcel(PendingIntent sender, Parcel out) {
        OnMarshaledListener listener;
        out.writeStrongBinder(sender != null ? sender.mTarget.asBinder() : null);
        if (sender != null && (listener = sOnMarshaledListener.get()) != null) {
            listener.onMarshaled(sender, out, 0);
        }
    }

    public static PendingIntent readPendingIntentOrNullFromParcel(Parcel in) {
        IBinder b = in.readStrongBinder();
        if (b != null) {
            return new PendingIntent(b, in.getClassCookie(PendingIntent.class));
        }
        return null;
    }

    public PendingIntent(IIntentSender target) {
        this.mTarget = (IIntentSender) Objects.requireNonNull(target);
    }

    PendingIntent(IBinder target, Object cookie) {
        this.mTarget = (IIntentSender) Objects.requireNonNull(IIntentSender.Stub.asInterface(target));
        if (cookie != null) {
            this.mWhitelistToken = (IBinder) cookie;
        }
    }

    public IIntentSender getTarget() {
        return this.mTarget;
    }

    public IBinder getWhitelistToken() {
        return this.mWhitelistToken;
    }

    private ActivityManager.PendingIntentInfo getCachedInfo() {
        if (this.mCachedInfo == null) {
            try {
                this.mCachedInfo = ActivityManager.getService().getInfoForIntentSender(this.mTarget);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
        return this.mCachedInfo;
    }
}
