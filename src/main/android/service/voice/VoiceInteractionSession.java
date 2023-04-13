package android.service.voice;

import android.C0001R;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.DirectAction;
import android.app.Instrumentation;
import android.app.VoiceInteractor;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ParceledListSlice;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.Message;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.service.voice.IVoiceInteractionSession;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionWindow;
import android.util.ArrayMap;
import android.util.DebugUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import com.android.internal.C4057R;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.IVoiceInteractorCallback;
import com.android.internal.app.IVoiceInteractorRequest;
import com.android.internal.p028os.HandlerCaller;
import com.android.internal.p028os.SomeArgs;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class VoiceInteractionSession implements KeyEvent.Callback, ComponentCallbacks2 {
    static final boolean DEBUG = false;
    public static final String KEY_SHOW_SESSION_ID = "android.service.voice.SHOW_SESSION_ID";
    static final int MSG_CANCEL = 7;
    static final int MSG_CLOSE_SYSTEM_DIALOGS = 102;
    static final int MSG_DESTROY = 103;
    static final int MSG_HANDLE_ASSIST = 104;
    static final int MSG_HANDLE_SCREENSHOT = 105;
    static final int MSG_HIDE = 107;
    static final int MSG_NOTIFY_VISIBLE_ACTIVITY_INFO_CHANGED = 109;
    static final int MSG_ON_LOCKSCREEN_SHOWN = 108;
    static final int MSG_REGISTER_VISIBLE_ACTIVITY_CALLBACK = 110;
    static final int MSG_SHOW = 106;
    static final int MSG_START_ABORT_VOICE = 4;
    static final int MSG_START_COMMAND = 5;
    static final int MSG_START_COMPLETE_VOICE = 3;
    static final int MSG_START_CONFIRMATION = 1;
    static final int MSG_START_PICK_OPTION = 2;
    static final int MSG_SUPPORTS_COMMANDS = 6;
    static final int MSG_TASK_FINISHED = 101;
    static final int MSG_TASK_STARTED = 100;
    static final int MSG_UNREGISTER_VISIBLE_ACTIVITY_CALLBACK = 111;
    public static final int SHOW_SOURCE_ACTIVITY = 16;
    public static final int SHOW_SOURCE_APPLICATION = 8;
    public static final int SHOW_SOURCE_ASSIST_GESTURE = 4;
    public static final int SHOW_SOURCE_AUTOMOTIVE_SYSTEM_UI = 128;
    public static final int SHOW_SOURCE_NOTIFICATION = 64;
    public static final int SHOW_SOURCE_PUSH_TO_TALK = 32;
    public static final int SHOW_WITH_ASSIST = 1;
    public static final int SHOW_WITH_SCREENSHOT = 2;
    static final String TAG = "VoiceInteractionSession";
    public static final int VOICE_INTERACTION_ACTIVITY_EVENT_PAUSE = 3;
    public static final int VOICE_INTERACTION_ACTIVITY_EVENT_RESUME = 2;
    public static final int VOICE_INTERACTION_ACTIVITY_EVENT_START = 1;
    public static final int VOICE_INTERACTION_ACTIVITY_EVENT_STOP = 4;
    final ArrayMap<IBinder, Request> mActiveRequests;
    final MyCallbacks mCallbacks;
    FrameLayout mContentFrame;
    final Context mContext;
    final KeyEvent.DispatcherState mDispatcherState;
    final HandlerCaller mHandlerCaller;
    boolean mInShowWindow;
    LayoutInflater mInflater;
    boolean mInitialized;
    final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsComputer;
    final IVoiceInteractor mInteractor;
    ICancellationSignal mKillCallback;
    final Map<SafeResultListener, Consumer<Bundle>> mRemoteCallbacks;
    View mRootView;
    final IVoiceInteractionSession mSession;
    IVoiceInteractionManagerService mSystemService;
    int mTheme;
    TypedArray mThemeAttrs;
    final Insets mTmpInsets;
    IBinder mToken;
    boolean mUiEnabled;
    private final Map<VisibleActivityCallback, Executor> mVisibleActivityCallbacks;
    private final List<VisibleActivityInfo> mVisibleActivityInfos;
    final WeakReference<VoiceInteractionSession> mWeakRef;
    VoiceInteractionWindow mWindow;
    boolean mWindowAdded;
    boolean mWindowVisible;
    boolean mWindowWasVisible;

    /* loaded from: classes3.dex */
    public static final class Insets {
        public static final int TOUCHABLE_INSETS_CONTENT = 1;
        public static final int TOUCHABLE_INSETS_FRAME = 0;
        public static final int TOUCHABLE_INSETS_REGION = 3;
        public int touchableInsets;
        public final Rect contentInsets = new Rect();
        public final Region touchableRegion = new Region();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VoiceInteractionActivityEventType {
    }

    /* loaded from: classes3.dex */
    public static class Request {
        final IVoiceInteractorCallback mCallback;
        final String mCallingPackage;
        final int mCallingUid;
        final Bundle mExtras;
        final IVoiceInteractorRequest mInterface = new IVoiceInteractorRequest.Stub() { // from class: android.service.voice.VoiceInteractionSession.Request.1
            @Override // com.android.internal.app.IVoiceInteractorRequest
            public void cancel() throws RemoteException {
                VoiceInteractionSession session = Request.this.mSession.get();
                if (session != null) {
                    session.mHandlerCaller.sendMessage(session.mHandlerCaller.obtainMessageO(7, Request.this));
                }
            }
        };
        final WeakReference<VoiceInteractionSession> mSession;

        Request(String packageName, int uid, IVoiceInteractorCallback callback, VoiceInteractionSession session, Bundle extras) {
            this.mCallingPackage = packageName;
            this.mCallingUid = uid;
            this.mCallback = callback;
            this.mSession = session.mWeakRef;
            this.mExtras = extras;
        }

        public int getCallingUid() {
            return this.mCallingUid;
        }

        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public boolean isActive() {
            VoiceInteractionSession session = this.mSession.get();
            if (session == null) {
                return false;
            }
            return session.isRequestActive(this.mInterface.asBinder());
        }

        void finishRequest() {
            VoiceInteractionSession session = this.mSession.get();
            if (session == null) {
                throw new IllegalStateException("VoiceInteractionSession has been destroyed");
            }
            Request req = session.removeRequest(this.mInterface.asBinder());
            if (req == null) {
                throw new IllegalStateException("Request not active: " + this);
            }
            if (req != this) {
                throw new IllegalStateException("Current active request " + req + " not same as calling request " + this);
            }
        }

        public void cancel() {
            try {
                finishRequest();
                this.mCallback.deliverCancel(this.mInterface);
            } catch (RemoteException e) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            DebugUtils.buildShortClassTag(this, sb);
            sb.append(" ");
            sb.append(this.mInterface.asBinder());
            sb.append(" pkg=");
            sb.append(this.mCallingPackage);
            sb.append(" uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append('}');
            return sb.toString();
        }

        void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            writer.print(prefix);
            writer.print("mInterface=");
            writer.println(this.mInterface.asBinder());
            writer.print(prefix);
            writer.print("mCallingPackage=");
            writer.print(this.mCallingPackage);
            writer.print(" mCallingUid=");
            UserHandle.formatUid(writer, this.mCallingUid);
            writer.println();
            writer.print(prefix);
            writer.print("mCallback=");
            writer.println(this.mCallback.asBinder());
            if (this.mExtras != null) {
                writer.print(prefix);
                writer.print("mExtras=");
                writer.println(this.mExtras);
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class ConfirmationRequest extends Request {
        final VoiceInteractor.Prompt mPrompt;

        ConfirmationRequest(String packageName, int uid, IVoiceInteractorCallback callback, VoiceInteractionSession session, VoiceInteractor.Prompt prompt, Bundle extras) {
            super(packageName, uid, callback, session, extras);
            this.mPrompt = prompt;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getPrompt() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public void sendConfirmationResult(boolean confirmed, Bundle result) {
            try {
                finishRequest();
                this.mCallback.deliverConfirmationResult(this.mInterface, confirmed, result);
            } catch (RemoteException e) {
            }
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            super.dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.print("mPrompt=");
            writer.println(this.mPrompt);
        }
    }

    /* loaded from: classes3.dex */
    public static final class PickOptionRequest extends Request {
        final VoiceInteractor.PickOptionRequest.Option[] mOptions;
        final VoiceInteractor.Prompt mPrompt;

        PickOptionRequest(String packageName, int uid, IVoiceInteractorCallback callback, VoiceInteractionSession session, VoiceInteractor.Prompt prompt, VoiceInteractor.PickOptionRequest.Option[] options, Bundle extras) {
            super(packageName, uid, callback, session, extras);
            this.mPrompt = prompt;
            this.mOptions = options;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getPrompt() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public VoiceInteractor.PickOptionRequest.Option[] getOptions() {
            return this.mOptions;
        }

        void sendPickOptionResult(boolean finished, VoiceInteractor.PickOptionRequest.Option[] selections, Bundle result) {
            if (finished) {
                try {
                    finishRequest();
                } catch (RemoteException e) {
                    return;
                }
            }
            this.mCallback.deliverPickOptionResult(this.mInterface, finished, selections, result);
        }

        public void sendIntermediatePickOptionResult(VoiceInteractor.PickOptionRequest.Option[] selections, Bundle result) {
            sendPickOptionResult(false, selections, result);
        }

        public void sendPickOptionResult(VoiceInteractor.PickOptionRequest.Option[] selections, Bundle result) {
            sendPickOptionResult(true, selections, result);
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            super.dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.print("mPrompt=");
            writer.println(this.mPrompt);
            if (this.mOptions != null) {
                writer.print(prefix);
                writer.println("Options:");
                int i = 0;
                while (true) {
                    VoiceInteractor.PickOptionRequest.Option[] optionArr = this.mOptions;
                    if (i < optionArr.length) {
                        VoiceInteractor.PickOptionRequest.Option op = optionArr[i];
                        writer.print(prefix);
                        writer.print("  #");
                        writer.print(i);
                        writer.println(":");
                        writer.print(prefix);
                        writer.print("    mLabel=");
                        writer.println(op.getLabel());
                        writer.print(prefix);
                        writer.print("    mIndex=");
                        writer.println(op.getIndex());
                        if (op.countSynonyms() > 0) {
                            writer.print(prefix);
                            writer.println("    Synonyms:");
                            for (int j = 0; j < op.countSynonyms(); j++) {
                                writer.print(prefix);
                                writer.print("      #");
                                writer.print(j);
                                writer.print(": ");
                                writer.println(op.getSynonymAt(j));
                            }
                        }
                        if (op.getExtras() != null) {
                            writer.print(prefix);
                            writer.print("    mExtras=");
                            writer.println(op.getExtras());
                        }
                        i++;
                    } else {
                        return;
                    }
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public static final class CompleteVoiceRequest extends Request {
        final VoiceInteractor.Prompt mPrompt;

        CompleteVoiceRequest(String packageName, int uid, IVoiceInteractorCallback callback, VoiceInteractionSession session, VoiceInteractor.Prompt prompt, Bundle extras) {
            super(packageName, uid, callback, session, extras);
            this.mPrompt = prompt;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getMessage() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public void sendCompleteResult(Bundle result) {
            try {
                finishRequest();
                this.mCallback.deliverCompleteVoiceResult(this.mInterface, result);
            } catch (RemoteException e) {
            }
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            super.dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.print("mPrompt=");
            writer.println(this.mPrompt);
        }
    }

    /* loaded from: classes3.dex */
    public static final class AbortVoiceRequest extends Request {
        final VoiceInteractor.Prompt mPrompt;

        AbortVoiceRequest(String packageName, int uid, IVoiceInteractorCallback callback, VoiceInteractionSession session, VoiceInteractor.Prompt prompt, Bundle extras) {
            super(packageName, uid, callback, session, extras);
            this.mPrompt = prompt;
        }

        public VoiceInteractor.Prompt getVoicePrompt() {
            return this.mPrompt;
        }

        @Deprecated
        public CharSequence getMessage() {
            VoiceInteractor.Prompt prompt = this.mPrompt;
            if (prompt != null) {
                return prompt.getVoicePromptAt(0);
            }
            return null;
        }

        public void sendAbortResult(Bundle result) {
            try {
                finishRequest();
                this.mCallback.deliverAbortVoiceResult(this.mInterface, result);
            } catch (RemoteException e) {
            }
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            super.dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.print("mPrompt=");
            writer.println(this.mPrompt);
        }
    }

    /* loaded from: classes3.dex */
    public static final class CommandRequest extends Request {
        final String mCommand;

        CommandRequest(String packageName, int uid, IVoiceInteractorCallback callback, VoiceInteractionSession session, String command, Bundle extras) {
            super(packageName, uid, callback, session, extras);
            this.mCommand = command;
        }

        public String getCommand() {
            return this.mCommand;
        }

        void sendCommandResult(boolean finished, Bundle result) {
            if (finished) {
                try {
                    finishRequest();
                } catch (RemoteException e) {
                    return;
                }
            }
            this.mCallback.deliverCommandResult(this.mInterface, finished, result);
        }

        public void sendIntermediateResult(Bundle result) {
            sendCommandResult(false, result);
        }

        public void sendResult(Bundle result) {
            sendCommandResult(true, result);
        }

        @Override // android.service.voice.VoiceInteractionSession.Request
        void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            super.dump(prefix, fd, writer, args);
            writer.print(prefix);
            writer.print("mCommand=");
            writer.println(this.mCommand);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class MyCallbacks implements HandlerCaller.Callback, VoiceInteractionWindow.Callback {
        MyCallbacks() {
        }

        @Override // com.android.internal.p028os.HandlerCaller.Callback
        public void executeMessage(Message msg) {
            SomeArgs args = null;
            switch (msg.what) {
                case 1:
                    VoiceInteractionSession.this.onRequestConfirmation((ConfirmationRequest) msg.obj);
                    break;
                case 2:
                    VoiceInteractionSession.this.onRequestPickOption((PickOptionRequest) msg.obj);
                    break;
                case 3:
                    VoiceInteractionSession.this.onRequestCompleteVoice((CompleteVoiceRequest) msg.obj);
                    break;
                case 4:
                    VoiceInteractionSession.this.onRequestAbortVoice((AbortVoiceRequest) msg.obj);
                    break;
                case 5:
                    VoiceInteractionSession.this.onRequestCommand((CommandRequest) msg.obj);
                    break;
                case 6:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    args2.arg1 = VoiceInteractionSession.this.onGetSupportedCommands((String[]) args2.arg1);
                    args2.complete();
                    args = null;
                    break;
                case 7:
                    VoiceInteractionSession.this.onCancelRequest((Request) msg.obj);
                    break;
                case 100:
                    VoiceInteractionSession.this.onTaskStarted((Intent) msg.obj, msg.arg1);
                    break;
                case 101:
                    VoiceInteractionSession.this.onTaskFinished((Intent) msg.obj, msg.arg1);
                    break;
                case 102:
                    VoiceInteractionSession.this.onCloseSystemDialogs();
                    break;
                case 103:
                    VoiceInteractionSession.this.doDestroy();
                    break;
                case 104:
                    args = (SomeArgs) msg.obj;
                    VoiceInteractionSession.this.doOnHandleAssist(args.argi1, (IBinder) args.arg5, (Bundle) args.arg1, (AssistStructure) args.arg2, (Throwable) args.arg3, (AssistContent) args.arg4, args.argi5, args.argi6);
                    break;
                case 105:
                    VoiceInteractionSession.this.onHandleScreenshot((Bitmap) msg.obj);
                    break;
                case 106:
                    args = (SomeArgs) msg.obj;
                    VoiceInteractionSession.this.doShow((Bundle) args.arg1, msg.arg1, (IVoiceInteractionSessionShowCallback) args.arg2);
                    break;
                case 107:
                    VoiceInteractionSession.this.doHide();
                    break;
                case 108:
                    VoiceInteractionSession.this.onLockscreenShown();
                    break;
                case 109:
                    VoiceInteractionSession.this.doNotifyVisibleActivityInfoChanged((VisibleActivityInfo) msg.obj, msg.arg1);
                    break;
                case 110:
                    args = (SomeArgs) msg.obj;
                    VoiceInteractionSession.this.doRegisterVisibleActivityCallback((Executor) args.arg1, (VisibleActivityCallback) args.arg2);
                    break;
                case 111:
                    VoiceInteractionSession.this.doUnregisterVisibleActivityCallback((VisibleActivityCallback) msg.obj);
                    break;
            }
            if (args != null) {
                args.recycle();
            }
        }

        @Override // android.service.voice.VoiceInteractionWindow.Callback
        public void onBackPressed() {
            VoiceInteractionSession.this.onBackPressed();
        }
    }

    public VoiceInteractionSession(Context context) {
        this(context, new Handler());
    }

    public VoiceInteractionSession(Context context, Handler handler) {
        this.mDispatcherState = new KeyEvent.DispatcherState();
        this.mTheme = 0;
        this.mUiEnabled = true;
        this.mActiveRequests = new ArrayMap<>();
        this.mTmpInsets = new Insets();
        this.mWeakRef = new WeakReference<>(this);
        this.mRemoteCallbacks = new ArrayMap();
        this.mVisibleActivityCallbacks = new ArrayMap();
        this.mVisibleActivityInfos = new ArrayList();
        this.mInteractor = new IVoiceInteractor.Stub() { // from class: android.service.voice.VoiceInteractionSession.1
            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startConfirmation(String callingPackage, IVoiceInteractorCallback callback, VoiceInteractor.Prompt prompt, Bundle extras) {
                ConfirmationRequest request = new ConfirmationRequest(callingPackage, Binder.getCallingUid(), callback, VoiceInteractionSession.this, prompt, extras);
                VoiceInteractionSession.this.addRequest(request);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(1, request));
                return request.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startPickOption(String callingPackage, IVoiceInteractorCallback callback, VoiceInteractor.Prompt prompt, VoiceInteractor.PickOptionRequest.Option[] options, Bundle extras) {
                PickOptionRequest request = new PickOptionRequest(callingPackage, Binder.getCallingUid(), callback, VoiceInteractionSession.this, prompt, options, extras);
                VoiceInteractionSession.this.addRequest(request);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(2, request));
                return request.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startCompleteVoice(String callingPackage, IVoiceInteractorCallback callback, VoiceInteractor.Prompt message, Bundle extras) {
                CompleteVoiceRequest request = new CompleteVoiceRequest(callingPackage, Binder.getCallingUid(), callback, VoiceInteractionSession.this, message, extras);
                VoiceInteractionSession.this.addRequest(request);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(3, request));
                return request.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startAbortVoice(String callingPackage, IVoiceInteractorCallback callback, VoiceInteractor.Prompt message, Bundle extras) {
                AbortVoiceRequest request = new AbortVoiceRequest(callingPackage, Binder.getCallingUid(), callback, VoiceInteractionSession.this, message, extras);
                VoiceInteractionSession.this.addRequest(request);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(4, request));
                return request.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public IVoiceInteractorRequest startCommand(String callingPackage, IVoiceInteractorCallback callback, String command, Bundle extras) {
                CommandRequest request = new CommandRequest(callingPackage, Binder.getCallingUid(), callback, VoiceInteractionSession.this, command, extras);
                VoiceInteractionSession.this.addRequest(request);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(5, request));
                return request.mInterface;
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public boolean[] supportsCommands(String callingPackage, String[] commands) {
                Message msg = VoiceInteractionSession.this.mHandlerCaller.obtainMessageIOO(6, 0, commands, null);
                SomeArgs args = VoiceInteractionSession.this.mHandlerCaller.sendMessageAndWait(msg);
                if (args != null) {
                    boolean[] res = (boolean[]) args.arg1;
                    args.recycle();
                    return res;
                }
                return new boolean[commands.length];
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public void notifyDirectActionsChanged(int taskId, IBinder assistToken) {
                VoiceInteractionSession.this.mHandlerCaller.getHandler().sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.voice.VoiceInteractionSession$1$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((VoiceInteractionSession) obj).onDirectActionsInvalidated((VoiceInteractionSession.ActivityId) obj2);
                    }
                }, VoiceInteractionSession.this, new ActivityId(taskId, assistToken)));
            }

            @Override // com.android.internal.app.IVoiceInteractor
            public void setKillCallback(ICancellationSignal callback) {
                VoiceInteractionSession.this.mKillCallback = callback;
            }
        };
        this.mSession = new IVoiceInteractionSession.Stub() { // from class: android.service.voice.VoiceInteractionSession.2
            @Override // android.service.voice.IVoiceInteractionSession
            public void show(Bundle sessionArgs, int flags, IVoiceInteractionSessionShowCallback showCallback) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIOO(106, flags, sessionArgs, showCallback));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void hide() {
                VoiceInteractionSession.this.mHandlerCaller.removeMessages(106);
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(107));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void handleAssist(final int taskId, final IBinder assistToken, final Bundle data, final AssistStructure structure, final AssistContent content, final int index, final int count) {
                Thread retriever = new Thread("AssistStructure retriever") { // from class: android.service.voice.VoiceInteractionSession.2.1
                    @Override // java.lang.Thread, java.lang.Runnable
                    public void run() {
                        Throwable failure = null;
                        AssistStructure assistStructure = structure;
                        if (assistStructure != null) {
                            try {
                                assistStructure.ensureData();
                            } catch (Throwable e) {
                                Log.m103w(VoiceInteractionSession.TAG, "Failure retrieving AssistStructure", e);
                                failure = e;
                            }
                        }
                        SomeArgs args = SomeArgs.obtain();
                        args.argi1 = taskId;
                        args.arg1 = data;
                        args.arg2 = failure == null ? structure : null;
                        args.arg3 = failure;
                        args.arg4 = content;
                        args.arg5 = assistToken;
                        args.argi5 = index;
                        args.argi6 = count;
                        VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(104, args));
                    }
                };
                retriever.start();
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void handleScreenshot(Bitmap screenshot) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageO(105, screenshot));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void taskStarted(Intent intent, int taskId) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(100, taskId, intent));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void taskFinished(Intent intent, int taskId) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(101, taskId, intent));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void closeSystemDialogs() {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(102));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void onLockscreenShown() {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(108));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void destroy() {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessage(103));
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void notifyVisibleActivityInfoChanged(VisibleActivityInfo visibleActivityInfo, int type) {
                VoiceInteractionSession.this.mHandlerCaller.sendMessage(VoiceInteractionSession.this.mHandlerCaller.obtainMessageIO(109, type, visibleActivityInfo));
            }
        };
        MyCallbacks myCallbacks = new MyCallbacks();
        this.mCallbacks = myCallbacks;
        this.mInsetsComputer = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: android.service.voice.VoiceInteractionSession.3
            @Override // android.view.ViewTreeObserver.OnComputeInternalInsetsListener
            public void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo info) {
                VoiceInteractionSession voiceInteractionSession = VoiceInteractionSession.this;
                voiceInteractionSession.onComputeInsets(voiceInteractionSession.mTmpInsets);
                info.contentInsets.set(VoiceInteractionSession.this.mTmpInsets.contentInsets);
                info.visibleInsets.set(VoiceInteractionSession.this.mTmpInsets.contentInsets);
                info.touchableRegion.set(VoiceInteractionSession.this.mTmpInsets.touchableRegion);
                info.setTouchableInsets(VoiceInteractionSession.this.mTmpInsets.touchableInsets);
            }
        };
        this.mHandlerCaller = new HandlerCaller(context, handler.getLooper(), myCallbacks, true);
        this.mContext = createWindowContextIfNeeded(context);
    }

    public Context getContext() {
        return this.mContext;
    }

    private Context createWindowContextIfNeeded(Context context) {
        DisplayManager displayManager;
        try {
            if (!context.isUiContext() && (displayManager = (DisplayManager) context.getSystemService(DisplayManager.class)) != null) {
                return context.createWindowContext(displayManager.getDisplay(0), 2031, null);
            }
            return context;
        } catch (RuntimeException e) {
            Log.m104w(TAG, "Fail to createWindowContext, Exception = " + e);
            return context;
        }
    }

    void addRequest(Request req) {
        synchronized (this) {
            this.mActiveRequests.put(req.mInterface.asBinder(), req);
        }
    }

    boolean isRequestActive(IBinder reqInterface) {
        boolean containsKey;
        synchronized (this) {
            containsKey = this.mActiveRequests.containsKey(reqInterface);
        }
        return containsKey;
    }

    Request removeRequest(IBinder reqInterface) {
        Request remove;
        synchronized (this) {
            remove = this.mActiveRequests.remove(reqInterface);
        }
        return remove;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doCreate(IVoiceInteractionManagerService service, IBinder token) {
        this.mSystemService = service;
        this.mToken = token;
        onCreate();
    }

    void doShow(Bundle args, int flags, final IVoiceInteractionSessionShowCallback showCallback) {
        if (this.mInShowWindow) {
            Log.m104w(TAG, "Re-entrance in to showWindow");
            return;
        }
        try {
            this.mInShowWindow = true;
            onPrepareShow(args, flags);
            if (!this.mWindowVisible) {
                ensureWindowAdded();
            }
            onShow(args, flags);
            if (!this.mWindowVisible) {
                this.mWindowVisible = true;
                if (this.mUiEnabled) {
                    showWindow();
                }
            }
            if (showCallback != null) {
                if (this.mUiEnabled) {
                    this.mRootView.invalidate();
                    this.mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.service.voice.VoiceInteractionSession.4
                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            VoiceInteractionSession.this.mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                            try {
                                showCallback.onShown();
                                return true;
                            } catch (RemoteException e) {
                                Log.m103w(VoiceInteractionSession.TAG, "Error calling onShown", e);
                                return true;
                            }
                        }
                    });
                } else {
                    try {
                        showCallback.onShown();
                    } catch (RemoteException e) {
                        Log.m103w(TAG, "Error calling onShown", e);
                    }
                }
            }
        } finally {
            this.mWindowWasVisible = true;
            this.mInShowWindow = false;
        }
    }

    void doHide() {
        if (this.mWindowVisible) {
            ensureWindowHidden();
            this.mWindowVisible = false;
            onHide();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doDestroy() {
        onDestroy();
        ICancellationSignal iCancellationSignal = this.mKillCallback;
        if (iCancellationSignal != null) {
            try {
                iCancellationSignal.cancel();
            } catch (RemoteException e) {
            }
            this.mKillCallback = null;
        }
        if (this.mInitialized) {
            this.mRootView.getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsComputer);
            if (this.mWindowAdded) {
                this.mWindow.dismiss();
                this.mWindowAdded = false;
            }
            this.mInitialized = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doNotifyVisibleActivityInfoChanged(VisibleActivityInfo visibleActivityInfo, int type) {
        if (this.mVisibleActivityCallbacks.isEmpty()) {
            return;
        }
        switch (type) {
            case 1:
                notifyVisibleActivityChanged(visibleActivityInfo, type);
                this.mVisibleActivityInfos.add(visibleActivityInfo);
                return;
            case 2:
                notifyVisibleActivityChanged(visibleActivityInfo, type);
                this.mVisibleActivityInfos.remove(visibleActivityInfo);
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doRegisterVisibleActivityCallback(Executor executor, final VisibleActivityCallback callback) {
        if (this.mVisibleActivityCallbacks.containsKey(callback)) {
            return;
        }
        int preCallbackCount = this.mVisibleActivityCallbacks.size();
        this.mVisibleActivityCallbacks.put(callback, executor);
        if (preCallbackCount == 0) {
            try {
                this.mSystemService.startListeningVisibleActivityChanged(this.mToken);
                return;
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return;
            }
        }
        for (int i = 0; i < this.mVisibleActivityInfos.size(); i++) {
            final VisibleActivityInfo visibleActivityInfo = this.mVisibleActivityInfos.get(i);
            executor.execute(new Runnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceInteractionSession.VisibleActivityCallback.this.onVisible(visibleActivityInfo);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doUnregisterVisibleActivityCallback(VisibleActivityCallback callback) {
        this.mVisibleActivityCallbacks.remove(callback);
        if (this.mVisibleActivityCallbacks.size() == 0) {
            this.mVisibleActivityInfos.clear();
            try {
                this.mSystemService.stopListeningVisibleActivityChanged(this.mToken);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        }
    }

    private void notifyVisibleActivityChanged(final VisibleActivityInfo visibleActivityInfo, int type) {
        for (Map.Entry<VisibleActivityCallback, Executor> e : this.mVisibleActivityCallbacks.entrySet()) {
            final Executor executor = e.getValue();
            final VisibleActivityCallback visibleActivityCallback = e.getKey();
            switch (type) {
                case 1:
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda9
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            executor.execute(new Runnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda4
                                @Override // java.lang.Runnable
                                public final void run() {
                                    VoiceInteractionSession.VisibleActivityCallback.this.onVisible(r2);
                                }
                            });
                        }
                    });
                    break;
                case 2:
                    Binder.withCleanCallingIdentity(new FunctionalUtils.ThrowingRunnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda10
                        @Override // com.android.internal.util.FunctionalUtils.ThrowingRunnable
                        public final void runOrThrow() {
                            executor.execute(new Runnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda11
                                @Override // java.lang.Runnable
                                public final void run() {
                                    VoiceInteractionSession.VisibleActivityCallback.this.onInvisible(r2.getActivityId());
                                }
                            });
                        }
                    });
                    break;
            }
        }
    }

    void ensureWindowCreated() {
        if (this.mInitialized) {
            return;
        }
        if (!this.mUiEnabled) {
            throw new IllegalStateException("setUiEnabled is false");
        }
        this.mInitialized = true;
        this.mInflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        VoiceInteractionWindow voiceInteractionWindow = new VoiceInteractionWindow(this.mContext, TAG, this.mTheme, this.mCallbacks, this, this.mDispatcherState, 2031, 80, true);
        this.mWindow = voiceInteractionWindow;
        voiceInteractionWindow.getWindow().getAttributes().setFitInsetsTypes(0);
        this.mWindow.getWindow().addFlags(16843008);
        this.mThemeAttrs = this.mContext.obtainStyledAttributes(C0001R.styleable.VoiceInteractionSession);
        View inflate = this.mInflater.inflate(C4057R.layout.voice_interaction_session, (ViewGroup) null);
        this.mRootView = inflate;
        inflate.setSystemUiVisibility(1792);
        this.mWindow.setContentView(this.mRootView);
        this.mRootView.getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsComputer);
        this.mContentFrame = (FrameLayout) this.mRootView.findViewById(16908290);
        this.mWindow.getWindow().setLayout(-1, -1);
        this.mWindow.setToken(this.mToken);
    }

    void ensureWindowAdded() {
        if (this.mUiEnabled && !this.mWindowAdded) {
            this.mWindowAdded = true;
            ensureWindowCreated();
            View v = onCreateContentView();
            if (v != null) {
                setContentView(v);
            }
        }
    }

    void showWindow() {
        VoiceInteractionWindow voiceInteractionWindow = this.mWindow;
        if (voiceInteractionWindow != null) {
            voiceInteractionWindow.show();
            try {
                this.mSystemService.setSessionWindowVisible(this.mToken, true);
            } catch (RemoteException e) {
                Log.m103w(TAG, "Failed to notify session window shown", e);
            }
        }
    }

    void ensureWindowHidden() {
        VoiceInteractionWindow voiceInteractionWindow = this.mWindow;
        if (voiceInteractionWindow != null) {
            voiceInteractionWindow.hide();
            try {
                this.mSystemService.setSessionWindowVisible(this.mToken, false);
            } catch (RemoteException e) {
                Log.m103w(TAG, "Failed to notify session window hidden", e);
            }
        }
    }

    public void setDisabledShowContext(int flags) {
        try {
            this.mSystemService.setDisabledShowContext(flags);
        } catch (RemoteException e) {
        }
    }

    public int getDisabledShowContext() {
        try {
            return this.mSystemService.getDisabledShowContext();
        } catch (RemoteException e) {
            return 0;
        }
    }

    public int getUserDisabledShowContext() {
        try {
            return this.mSystemService.getUserDisabledShowContext();
        } catch (RemoteException e) {
            return 0;
        }
    }

    public void show(Bundle args, int flags) {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.showSessionFromSession(iBinder, args, flags, this.mContext.getAttributionTag());
        } catch (RemoteException e) {
        }
    }

    public void hide() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.hideSessionFromSession(iBinder);
        } catch (RemoteException e) {
        }
    }

    public void setUiEnabled(boolean enabled) {
        if (this.mUiEnabled != enabled) {
            this.mUiEnabled = enabled;
            if (this.mWindowVisible) {
                if (enabled) {
                    ensureWindowAdded();
                    showWindow();
                    return;
                }
                ensureWindowHidden();
            }
        }
    }

    public void setTheme(int theme) {
        if (this.mWindow != null) {
            throw new IllegalStateException("Must be called before onCreate()");
        }
        this.mTheme = theme;
    }

    public void startVoiceActivity(Intent intent) {
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            intent.migrateExtraStreamToClipData(this.mContext);
            intent.prepareToLeaveProcess(this.mContext);
            int res = this.mSystemService.startVoiceActivity(this.mToken, intent, intent.resolveType(this.mContext.getContentResolver()), this.mContext.getAttributionTag());
            Instrumentation.checkStartActivityResult(res, intent);
        } catch (RemoteException e) {
        }
    }

    public void startAssistantActivity(Intent intent) {
        startAssistantActivity(intent, ActivityOptions.makeBasic().toBundle());
    }

    public void startAssistantActivity(Intent intent, Bundle bundle) {
        Objects.requireNonNull(bundle);
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            intent.migrateExtraStreamToClipData(this.mContext);
            intent.prepareToLeaveProcess(this.mContext);
            int res = this.mSystemService.startAssistantActivity(this.mToken, intent, intent.resolveType(this.mContext.getContentResolver()), this.mContext.getAttributionTag(), bundle);
            Instrumentation.checkStartActivityResult(res, intent);
        } catch (RemoteException e) {
        }
    }

    public final void requestDirectActions(ActivityId activityId, final CancellationSignal cancellationSignal, final Executor resultExecutor, final Consumer<List<DirectAction>> callback) {
        RemoteCallback cancellationCallback;
        Objects.requireNonNull(activityId);
        Objects.requireNonNull(resultExecutor);
        Objects.requireNonNull(callback);
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        if (cancellationSignal != null) {
            cancellationCallback = new RemoteCallback(new RemoteCallback.OnResultListener() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda0
                @Override // android.p008os.RemoteCallback.OnResultListener
                public final void onResult(Bundle bundle) {
                    VoiceInteractionSession.lambda$requestDirectActions$5(CancellationSignal.this, bundle);
                }
            });
        } else {
            cancellationCallback = null;
        }
        try {
            this.mSystemService.requestDirectActions(this.mToken, activityId.getTaskId(), activityId.getAssistToken(), cancellationCallback, new RemoteCallback(createSafeResultListener(new Consumer() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VoiceInteractionSession.lambda$requestDirectActions$7(resultExecutor, callback, (Bundle) obj);
                }
            })));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$requestDirectActions$5(CancellationSignal cancellationSignal, Bundle b) {
        IBinder cancellation;
        if (b != null && (cancellation = b.getBinder(VoiceInteractor.KEY_CANCELLATION_SIGNAL)) != null) {
            cancellationSignal.setRemote(ICancellationSignal.Stub.asInterface(cancellation));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$requestDirectActions$7(Executor resultExecutor, final Consumer callback, Bundle result) {
        final List<DirectAction> list;
        if (result == null) {
            list = Collections.emptyList();
        } else {
            ParceledListSlice<DirectAction> pls = (ParceledListSlice) result.getParcelable(DirectAction.KEY_ACTIONS_LIST, ParceledListSlice.class);
            if (pls != null) {
                List<DirectAction> receivedList = pls.getList();
                list = receivedList != null ? receivedList : Collections.emptyList();
            } else {
                List<DirectAction> list2 = Collections.emptyList();
                list = list2;
            }
        }
        resultExecutor.execute(new Runnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                callback.accept(list);
            }
        });
    }

    public void onDirectActionsInvalidated(ActivityId activityId) {
    }

    public final void performDirectAction(DirectAction action, Bundle extras, final CancellationSignal cancellationSignal, final Executor resultExecutor, final Consumer<Bundle> resultListener) {
        RemoteCallback cancellationCallback;
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        Objects.requireNonNull(resultExecutor);
        Objects.requireNonNull(resultListener);
        if (cancellationSignal != null) {
            cancellationSignal.throwIfCanceled();
        }
        if (cancellationSignal != null) {
            cancellationCallback = new RemoteCallback(createSafeResultListener(new Consumer() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda7
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    VoiceInteractionSession.lambda$performDirectAction$8(CancellationSignal.this, (Bundle) obj);
                }
            }));
        } else {
            cancellationCallback = null;
        }
        RemoteCallback resultCallback = new RemoteCallback(createSafeResultListener(new Consumer() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                VoiceInteractionSession.lambda$performDirectAction$11(resultExecutor, resultListener, (Bundle) obj);
            }
        }));
        try {
            this.mSystemService.performDirectAction(this.mToken, action.getId(), extras, action.getTaskId(), action.getActivityId(), cancellationCallback, resultCallback);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$performDirectAction$8(CancellationSignal cancellationSignal, Bundle b) {
        IBinder cancellation;
        if (b != null && (cancellation = b.getBinder(VoiceInteractor.KEY_CANCELLATION_SIGNAL)) != null) {
            cancellationSignal.setRemote(ICancellationSignal.Stub.asInterface(cancellation));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$performDirectAction$11(Executor resultExecutor, final Consumer resultListener, final Bundle b) {
        if (b != null) {
            resultExecutor.execute(new Runnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    resultListener.accept(b);
                }
            });
        } else {
            resultExecutor.execute(new Runnable() { // from class: android.service.voice.VoiceInteractionSession$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    resultListener.accept(Bundle.EMPTY);
                }
            });
        }
    }

    public void setKeepAwake(boolean keepAwake) {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.setKeepAwake(iBinder, keepAwake);
        } catch (RemoteException e) {
        }
    }

    public void closeSystemDialogs() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.closeSystemDialogs(iBinder);
        } catch (RemoteException e) {
        }
    }

    public LayoutInflater getLayoutInflater() {
        ensureWindowCreated();
        return this.mInflater;
    }

    public Dialog getWindow() {
        ensureWindowCreated();
        return this.mWindow;
    }

    public void finish() {
        IBinder iBinder = this.mToken;
        if (iBinder == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        try {
            this.mSystemService.finish(iBinder);
        } catch (RemoteException e) {
        }
    }

    public void onCreate() {
        doOnCreate();
    }

    private void doOnCreate() {
        int i = this.mTheme;
        if (i == 0) {
            i = C4057R.C4062style.Theme_DeviceDefault_VoiceInteractionSession;
        }
        this.mTheme = i;
    }

    public void onPrepareShow(Bundle args, int showFlags) {
    }

    public void onShow(Bundle args, int showFlags) {
    }

    public void onHide() {
    }

    public void onDestroy() {
    }

    public View onCreateContentView() {
        return null;
    }

    public void setContentView(View view) {
        ensureWindowCreated();
        this.mContentFrame.removeAllViews();
        this.mContentFrame.addView(view, new FrameLayout.LayoutParams(-1, -1));
        this.mContentFrame.requestApplyInsets();
    }

    void doOnHandleAssist(int taskId, IBinder assistToken, Bundle data, AssistStructure structure, Throwable failure, AssistContent content, int index, int count) {
        if (failure != null) {
            onAssistStructureFailure(failure);
        }
        AssistState assistState = new AssistState(new ActivityId(taskId, assistToken), data, structure, content, index, count);
        onHandleAssist(assistState);
    }

    public void onAssistStructureFailure(Throwable failure) {
    }

    @Deprecated
    public void onHandleAssist(Bundle data, AssistStructure structure, AssistContent content) {
    }

    public void onHandleAssist(AssistState state) {
        if (state.getAssistData() == null && state.getAssistStructure() == null && state.getAssistContent() == null) {
            return;
        }
        if (state.getIndex() == 0) {
            onHandleAssist(state.getAssistData(), state.getAssistStructure(), state.getAssistContent());
        } else {
            onHandleAssistSecondary(state.getAssistData(), state.getAssistStructure(), state.getAssistContent(), state.getIndex(), state.getCount());
        }
    }

    @Deprecated
    public void onHandleAssistSecondary(Bundle data, AssistStructure structure, AssistContent content, int index, int count) {
    }

    public void onHandleScreenshot(Bitmap screenshot) {
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {
        return false;
    }

    public void onBackPressed() {
        hide();
    }

    public void onCloseSystemDialogs() {
        hide();
    }

    public void onLockscreenShown() {
        hide();
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
    }

    public void onComputeInsets(Insets outInsets) {
        outInsets.contentInsets.left = 0;
        outInsets.contentInsets.bottom = 0;
        outInsets.contentInsets.right = 0;
        View decor = getWindow().getWindow().getDecorView();
        outInsets.contentInsets.top = decor.getHeight();
        outInsets.touchableInsets = 0;
        outInsets.touchableRegion.setEmpty();
    }

    public void onTaskStarted(Intent intent, int taskId) {
    }

    public void onTaskFinished(Intent intent, int taskId) {
        hide();
    }

    public boolean[] onGetSupportedCommands(String[] commands) {
        return new boolean[commands.length];
    }

    public void onRequestConfirmation(ConfirmationRequest request) {
    }

    public void onRequestPickOption(PickOptionRequest request) {
    }

    public void onRequestCompleteVoice(CompleteVoiceRequest request) {
    }

    public void onRequestAbortVoice(AbortVoiceRequest request) {
    }

    public void onRequestCommand(CommandRequest request) {
    }

    public void onCancelRequest(Request request) {
    }

    public final void registerVisibleActivityCallback(Executor executor, VisibleActivityCallback callback) {
        if (this.mToken == null) {
            throw new IllegalStateException("Can't call before onCreate()");
        }
        Objects.requireNonNull(executor);
        Objects.requireNonNull(callback);
        HandlerCaller handlerCaller = this.mHandlerCaller;
        handlerCaller.sendMessage(handlerCaller.obtainMessageOO(110, executor, callback));
    }

    public final void unregisterVisibleActivityCallback(VisibleActivityCallback callback) {
        Objects.requireNonNull(callback);
        HandlerCaller handlerCaller = this.mHandlerCaller;
        handlerCaller.sendMessage(handlerCaller.obtainMessageO(111, callback));
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.print("mToken=");
        writer.println(this.mToken);
        writer.print(prefix);
        writer.print("mTheme=#");
        writer.println(Integer.toHexString(this.mTheme));
        writer.print(prefix);
        writer.print("mUiEnabled=");
        writer.println(this.mUiEnabled);
        writer.print(" mInitialized=");
        writer.println(this.mInitialized);
        writer.print(prefix);
        writer.print("mWindowAdded=");
        writer.print(this.mWindowAdded);
        writer.print(" mWindowVisible=");
        writer.println(this.mWindowVisible);
        writer.print(prefix);
        writer.print("mWindowWasVisible=");
        writer.print(this.mWindowWasVisible);
        writer.print(" mInShowWindow=");
        writer.println(this.mInShowWindow);
        if (this.mActiveRequests.size() > 0) {
            writer.print(prefix);
            writer.println("Active requests:");
            String innerPrefix = prefix + "    ";
            for (int i = 0; i < this.mActiveRequests.size(); i++) {
                Request req = this.mActiveRequests.valueAt(i);
                writer.print(prefix);
                writer.print("  #");
                writer.print(i);
                writer.print(": ");
                writer.println(req);
                req.dump(innerPrefix, fd, writer, args);
            }
        }
    }

    private SafeResultListener createSafeResultListener(Consumer<Bundle> consumer) {
        SafeResultListener listener;
        synchronized (this) {
            listener = new SafeResultListener(consumer, this);
            this.mRemoteCallbacks.put(listener, consumer);
        }
        return listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Consumer<Bundle> removeSafeResultListener(SafeResultListener listener) {
        Consumer<Bundle> remove;
        synchronized (this) {
            remove = this.mRemoteCallbacks.remove(listener);
        }
        return remove;
    }

    /* loaded from: classes3.dex */
    public interface VisibleActivityCallback {
        default void onVisible(VisibleActivityInfo activityInfo) {
        }

        default void onInvisible(ActivityId activityId) {
        }
    }

    /* loaded from: classes3.dex */
    public static final class AssistState {
        private final ActivityId mActivityId;
        private final AssistContent mContent;
        private final int mCount;
        private final Bundle mData;
        private final int mIndex;
        private final AssistStructure mStructure;

        AssistState(ActivityId activityId, Bundle data, AssistStructure structure, AssistContent content, int index, int count) {
            this.mActivityId = activityId;
            this.mIndex = index;
            this.mCount = count;
            this.mData = data;
            this.mStructure = structure;
            this.mContent = content;
        }

        public boolean isFocused() {
            return this.mIndex == 0;
        }

        public int getIndex() {
            return this.mIndex;
        }

        public int getCount() {
            return this.mCount;
        }

        public ActivityId getActivityId() {
            return this.mActivityId;
        }

        public Bundle getAssistData() {
            return this.mData;
        }

        public AssistStructure getAssistStructure() {
            return this.mStructure;
        }

        public AssistContent getAssistContent() {
            return this.mContent;
        }
    }

    /* loaded from: classes3.dex */
    public static class ActivityId {
        private final IBinder mAssistToken;
        private final int mTaskId;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ActivityId(int taskId, IBinder assistToken) {
            this.mTaskId = taskId;
            this.mAssistToken = assistToken;
        }

        public int getTaskId() {
            return this.mTaskId;
        }

        public IBinder getAssistToken() {
            return this.mAssistToken;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ActivityId that = (ActivityId) o;
            if (this.mTaskId != that.mTaskId) {
                return false;
            }
            IBinder iBinder = this.mAssistToken;
            if (iBinder != null) {
                return iBinder.equals(that.mAssistToken);
            }
            if (that.mAssistToken == null) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.mTaskId;
            int i = result * 31;
            IBinder iBinder = this.mAssistToken;
            int result2 = i + (iBinder != null ? iBinder.hashCode() : 0);
            return result2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class SafeResultListener implements RemoteCallback.OnResultListener {
        private final WeakReference<VoiceInteractionSession> mWeakSession;

        SafeResultListener(Consumer<Bundle> action, VoiceInteractionSession session) {
            this.mWeakSession = new WeakReference<>(session);
        }

        @Override // android.p008os.RemoteCallback.OnResultListener
        public void onResult(Bundle result) {
            Consumer<Bundle> consumer;
            VoiceInteractionSession session = this.mWeakSession.get();
            if (session != null && (consumer = session.removeSafeResultListener(this)) != null) {
                consumer.accept(result);
            }
        }
    }
}
