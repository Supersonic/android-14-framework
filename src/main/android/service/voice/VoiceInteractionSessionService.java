package android.service.voice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.p008os.Bundle;
import android.p008os.DeadObjectException;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.service.voice.IVoiceInteractionSessionService;
import android.util.Log;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.p028os.HandlerCaller;
import com.android.internal.p028os.SomeArgs;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes3.dex */
public abstract class VoiceInteractionSessionService extends Service {
    static final int MSG_NEW_SESSION = 1;
    private static final String TAG = "VoiceInteractionSession";
    HandlerCaller mHandlerCaller;
    VoiceInteractionSession mSession;
    IVoiceInteractionManagerService mSystemService;
    IVoiceInteractionSessionService mInterface = new IVoiceInteractionSessionService.Stub() { // from class: android.service.voice.VoiceInteractionSessionService.1
        @Override // android.service.voice.IVoiceInteractionSessionService
        public void newSession(IBinder token, Bundle args, int startFlags) {
            VoiceInteractionSessionService.this.mHandlerCaller.sendMessage(VoiceInteractionSessionService.this.mHandlerCaller.obtainMessageIOO(1, startFlags, token, args));
        }
    };
    final HandlerCaller.Callback mHandlerCallerCallback = new HandlerCaller.Callback() { // from class: android.service.voice.VoiceInteractionSessionService.2
        @Override // com.android.internal.p028os.HandlerCaller.Callback
        public void executeMessage(Message msg) {
            SomeArgs args = (SomeArgs) msg.obj;
            switch (msg.what) {
                case 1:
                    VoiceInteractionSessionService.this.doNewSession((IBinder) args.arg1, (Bundle) args.arg2, args.argi1);
                    return;
                default:
                    return;
            }
        }
    };

    public abstract VoiceInteractionSession onNewSession(Bundle bundle);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mSystemService = IVoiceInteractionManagerService.Stub.asInterface(ServiceManager.getService(Context.VOICE_INTERACTION_MANAGER_SERVICE));
        this.mHandlerCaller = new HandlerCaller(this, Looper.myLooper(), this.mHandlerCallerCallback, true);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mInterface.asBinder();
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        VoiceInteractionSession voiceInteractionSession = this.mSession;
        if (voiceInteractionSession != null) {
            voiceInteractionSession.onConfigurationChanged(newConfig);
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        VoiceInteractionSession voiceInteractionSession = this.mSession;
        if (voiceInteractionSession != null) {
            voiceInteractionSession.onLowMemory();
        }
    }

    @Override // android.app.Service, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        VoiceInteractionSession voiceInteractionSession = this.mSession;
        if (voiceInteractionSession != null) {
            voiceInteractionSession.onTrimMemory(level);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        if (this.mSession == null) {
            writer.println("(no active session)");
            return;
        }
        writer.println("VoiceInteractionSession:");
        this.mSession.dump("  ", fd, writer, args);
    }

    void doNewSession(IBinder token, Bundle args, int startFlags) {
        VoiceInteractionSession voiceInteractionSession = this.mSession;
        if (voiceInteractionSession != null) {
            voiceInteractionSession.doDestroy();
            this.mSession = null;
        }
        this.mSession = onNewSession(args);
        if (deliverSession(token)) {
            this.mSession.doCreate(this.mSystemService, token);
            return;
        }
        this.mSession.doDestroy();
        this.mSession = null;
    }

    private boolean deliverSession(IBinder token) {
        try {
            return this.mSystemService.deliverNewSession(token, this.mSession.mSession, this.mSession.mInteractor);
        } catch (DeadObjectException e) {
            return false;
        } catch (RemoteException e2) {
            Log.m110e(TAG, "Failed to deliver session: " + e2);
            return false;
        }
    }
}
