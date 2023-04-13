package com.android.server.soundtrigger_middleware;

import android.content.Context;
import android.media.permission.Identity;
import android.media.permission.IdentityContext;
import android.media.soundtrigger.ModelParameterRange;
import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.PhraseSoundModel;
import android.media.soundtrigger.RecognitionConfig;
import android.media.soundtrigger.RecognitionEvent;
import android.media.soundtrigger.SoundModel;
import android.media.soundtrigger_middleware.ISoundTriggerCallback;
import android.media.soundtrigger_middleware.ISoundTriggerModule;
import android.media.soundtrigger_middleware.SoundTriggerModuleDescriptor;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.p005os.BatteryStatsInternal;
import android.util.Log;
import com.android.internal.util.LatencyTracker;
import com.android.server.LocalServices;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
/* loaded from: classes2.dex */
public class SoundTriggerMiddlewareLogging implements ISoundTriggerMiddlewareInternal, Dumpable {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
    public final Context mContext;
    public final ISoundTriggerMiddlewareInternal mDelegate;
    public final LinkedList<Event> mLastEvents = new LinkedList<>();

    /* loaded from: classes2.dex */
    public static class BatteryStatsHolder {
        public static final BatteryStatsInternal INSTANCE = (BatteryStatsInternal) LocalServices.getService(BatteryStatsInternal.class);
    }

    public SoundTriggerMiddlewareLogging(Context context, ISoundTriggerMiddlewareInternal iSoundTriggerMiddlewareInternal) {
        this.mDelegate = iSoundTriggerMiddlewareInternal;
        this.mContext = context;
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerMiddlewareInternal
    public SoundTriggerModuleDescriptor[] listModules() {
        try {
            SoundTriggerModuleDescriptor[] listModules = this.mDelegate.listModules();
            logReturn("listModules", listModules, new Object[0]);
            return listModules;
        } catch (Exception e) {
            logException("listModules", e, new Object[0]);
            throw e;
        }
    }

    @Override // com.android.server.soundtrigger_middleware.ISoundTriggerMiddlewareInternal
    public ISoundTriggerModule attach(int i, ISoundTriggerCallback iSoundTriggerCallback) {
        try {
            ModuleLogging moduleLogging = new ModuleLogging(iSoundTriggerCallback);
            moduleLogging.attach(this.mDelegate.attach(i, moduleLogging.getCallbackWrapper()));
            logReturn("attach", moduleLogging, Integer.valueOf(i), iSoundTriggerCallback);
            return moduleLogging;
        } catch (Exception e) {
            logException("attach", e, Integer.valueOf(i), iSoundTriggerCallback);
            throw e;
        }
    }

    public String toString() {
        return this.mDelegate.toString();
    }

    public final void logException(String str, Exception exc, Object... objArr) {
        logExceptionWithObject(this, IdentityContext.get(), str, exc, objArr);
    }

    public final void logReturn(String str, Object obj, Object... objArr) {
        logReturnWithObject(this, IdentityContext.get(), str, obj, objArr);
    }

    /* loaded from: classes2.dex */
    public class ModuleLogging implements ISoundTriggerModule {
        public final CallbackLogging mCallbackWrapper;
        public ISoundTriggerModule mDelegate;
        public final Identity mOriginatorIdentity = IdentityContext.getNonNull();

        public ModuleLogging(ISoundTriggerCallback iSoundTriggerCallback) {
            this.mCallbackWrapper = new CallbackLogging(iSoundTriggerCallback);
        }

        public void attach(ISoundTriggerModule iSoundTriggerModule) {
            this.mDelegate = iSoundTriggerModule;
        }

        public ISoundTriggerCallback getCallbackWrapper() {
            return this.mCallbackWrapper;
        }

        public int loadModel(SoundModel soundModel) throws RemoteException {
            try {
                int loadModel = this.mDelegate.loadModel(soundModel);
                logReturn("loadModel", Integer.valueOf(loadModel), soundModel);
                return loadModel;
            } catch (Exception e) {
                logException("loadModel", e, soundModel);
                throw e;
            }
        }

        public int loadPhraseModel(PhraseSoundModel phraseSoundModel) throws RemoteException {
            try {
                int loadPhraseModel = this.mDelegate.loadPhraseModel(phraseSoundModel);
                logReturn("loadPhraseModel", Integer.valueOf(loadPhraseModel), phraseSoundModel);
                return loadPhraseModel;
            } catch (Exception e) {
                logException("loadPhraseModel", e, phraseSoundModel);
                throw e;
            }
        }

        public void unloadModel(int i) throws RemoteException {
            try {
                this.mDelegate.unloadModel(i);
                logVoidReturn("unloadModel", Integer.valueOf(i));
            } catch (Exception e) {
                logException("unloadModel", e, Integer.valueOf(i));
                throw e;
            }
        }

        public void startRecognition(int i, RecognitionConfig recognitionConfig) throws RemoteException {
            try {
                this.mDelegate.startRecognition(i, recognitionConfig);
                logVoidReturn("startRecognition", Integer.valueOf(i), recognitionConfig);
            } catch (Exception e) {
                logException("startRecognition", e, Integer.valueOf(i), recognitionConfig);
                throw e;
            }
        }

        public void stopRecognition(int i) throws RemoteException {
            try {
                this.mDelegate.stopRecognition(i);
                logVoidReturn("stopRecognition", Integer.valueOf(i));
            } catch (Exception e) {
                logException("stopRecognition", e, Integer.valueOf(i));
                throw e;
            }
        }

        public void forceRecognitionEvent(int i) throws RemoteException {
            try {
                this.mDelegate.forceRecognitionEvent(i);
                logVoidReturn("forceRecognitionEvent", Integer.valueOf(i));
            } catch (Exception e) {
                logException("forceRecognitionEvent", e, Integer.valueOf(i));
                throw e;
            }
        }

        public void setModelParameter(int i, int i2, int i3) throws RemoteException {
            try {
                this.mDelegate.setModelParameter(i, i2, i3);
                logVoidReturn("setModelParameter", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
            } catch (Exception e) {
                logException("setModelParameter", e, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3));
                throw e;
            }
        }

        public int getModelParameter(int i, int i2) throws RemoteException {
            try {
                int modelParameter = this.mDelegate.getModelParameter(i, i2);
                logReturn("getModelParameter", Integer.valueOf(modelParameter), Integer.valueOf(i), Integer.valueOf(i2));
                return modelParameter;
            } catch (Exception e) {
                logException("getModelParameter", e, Integer.valueOf(i), Integer.valueOf(i2));
                throw e;
            }
        }

        public ModelParameterRange queryModelParameterSupport(int i, int i2) throws RemoteException {
            try {
                ModelParameterRange queryModelParameterSupport = this.mDelegate.queryModelParameterSupport(i, i2);
                logReturn("queryModelParameterSupport", queryModelParameterSupport, Integer.valueOf(i), Integer.valueOf(i2));
                return queryModelParameterSupport;
            } catch (Exception e) {
                logException("queryModelParameterSupport", e, Integer.valueOf(i), Integer.valueOf(i2));
                throw e;
            }
        }

        public void detach() throws RemoteException {
            try {
                this.mDelegate.detach();
                logVoidReturn("detach", new Object[0]);
            } catch (Exception e) {
                logException("detach", e, new Object[0]);
                throw e;
            }
        }

        public IBinder asBinder() {
            return this.mDelegate.asBinder();
        }

        public String toString() {
            return Objects.toString(this.mDelegate);
        }

        public final void logException(String str, Exception exc, Object... objArr) {
            SoundTriggerMiddlewareLogging.this.logExceptionWithObject(this, this.mOriginatorIdentity, str, exc, objArr);
        }

        public final void logReturn(String str, Object obj, Object... objArr) {
            SoundTriggerMiddlewareLogging.this.logReturnWithObject(this, this.mOriginatorIdentity, str, obj, objArr);
        }

        public final void logVoidReturn(String str, Object... objArr) {
            SoundTriggerMiddlewareLogging.this.logVoidReturnWithObject(this, this.mOriginatorIdentity, str, objArr);
        }

        /* loaded from: classes2.dex */
        public class CallbackLogging implements ISoundTriggerCallback {
            public final ISoundTriggerCallback mCallbackDelegate;

            public CallbackLogging(ISoundTriggerCallback iSoundTriggerCallback) {
                this.mCallbackDelegate = iSoundTriggerCallback;
            }

            public void onRecognition(int i, RecognitionEvent recognitionEvent, int i2) throws RemoteException {
                try {
                    BatteryStatsHolder.INSTANCE.noteWakingSoundTrigger(SystemClock.elapsedRealtime(), ModuleLogging.this.mOriginatorIdentity.uid);
                    this.mCallbackDelegate.onRecognition(i, recognitionEvent, i2);
                    logVoidReturn("onRecognition", Integer.valueOf(i), recognitionEvent);
                } catch (Exception e) {
                    logException("onRecognition", e, Integer.valueOf(i), recognitionEvent);
                    throw e;
                }
            }

            public void onPhraseRecognition(int i, PhraseRecognitionEvent phraseRecognitionEvent, int i2) throws RemoteException {
                try {
                    BatteryStatsHolder.INSTANCE.noteWakingSoundTrigger(SystemClock.elapsedRealtime(), ModuleLogging.this.mOriginatorIdentity.uid);
                    startKeyphraseEventLatencyTracking(phraseRecognitionEvent);
                    this.mCallbackDelegate.onPhraseRecognition(i, phraseRecognitionEvent, i2);
                    logVoidReturn("onPhraseRecognition", Integer.valueOf(i), phraseRecognitionEvent);
                } catch (Exception e) {
                    logException("onPhraseRecognition", e, Integer.valueOf(i), phraseRecognitionEvent);
                    throw e;
                }
            }

            public void onModelUnloaded(int i) throws RemoteException {
                try {
                    this.mCallbackDelegate.onModelUnloaded(i);
                    logVoidReturn("onModelUnloaded", Integer.valueOf(i));
                } catch (Exception e) {
                    logException("onModelUnloaded", e, Integer.valueOf(i));
                    throw e;
                }
            }

            public void onResourcesAvailable() throws RemoteException {
                try {
                    this.mCallbackDelegate.onResourcesAvailable();
                    logVoidReturn("onResourcesAvailable", new Object[0]);
                } catch (Exception e) {
                    logException("onResourcesAvailable", e, new Object[0]);
                    throw e;
                }
            }

            public void onModuleDied() throws RemoteException {
                try {
                    this.mCallbackDelegate.onModuleDied();
                    logVoidReturn("onModuleDied", new Object[0]);
                } catch (Exception e) {
                    logException("onModuleDied", e, new Object[0]);
                    throw e;
                }
            }

            public final void logException(String str, Exception exc, Object... objArr) {
                ModuleLogging moduleLogging = ModuleLogging.this;
                SoundTriggerMiddlewareLogging.this.logExceptionWithObject(this, moduleLogging.mOriginatorIdentity, str, exc, objArr);
            }

            public final void logVoidReturn(String str, Object... objArr) {
                ModuleLogging moduleLogging = ModuleLogging.this;
                SoundTriggerMiddlewareLogging.this.logVoidReturnWithObject(this, moduleLogging.mOriginatorIdentity, str, objArr);
            }

            public final void startKeyphraseEventLatencyTracking(PhraseRecognitionEvent phraseRecognitionEvent) {
                String str;
                if (phraseRecognitionEvent.phraseExtras.length > 0) {
                    str = "KeyphraseId=" + phraseRecognitionEvent.phraseExtras[0].id;
                } else {
                    str = null;
                }
                LatencyTracker latencyTracker = LatencyTracker.getInstance(SoundTriggerMiddlewareLogging.this.mContext);
                latencyTracker.onActionCancel(19);
                latencyTracker.onActionStart(19, str);
            }

            public IBinder asBinder() {
                return this.mCallbackDelegate.asBinder();
            }

            public String toString() {
                return Objects.toString(this.mCallbackDelegate);
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class Event {
        public final String message;
        public final long timestamp;

        public Event(String str) {
            this.timestamp = System.currentTimeMillis();
            this.message = str;
        }
    }

    public static String printArgs(Object[] objArr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < objArr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            printObject(sb, objArr[i]);
        }
        return sb.toString();
    }

    public static void printObject(StringBuilder sb, Object obj) {
        ObjectPrinter.print(sb, obj, 16);
    }

    public static String printObject(Object obj) {
        StringBuilder sb = new StringBuilder();
        printObject(sb, obj);
        return sb.toString();
    }

    public final void logReturnWithObject(Object obj, Identity identity, String str, Object obj2, Object[] objArr) {
        String format = String.format("%s[this=%s, client=%s](%s) -> %s", str, obj, printObject(identity), printArgs(objArr), printObject(obj2));
        Log.i("SoundTriggerMiddlewareLogging", format);
        appendMessage(format);
    }

    public final void logVoidReturnWithObject(Object obj, Identity identity, String str, Object[] objArr) {
        String format = String.format("%s[this=%s, client=%s](%s)", str, obj, printObject(identity), printArgs(objArr));
        Log.i("SoundTriggerMiddlewareLogging", format);
        appendMessage(format);
    }

    public final void logExceptionWithObject(Object obj, Identity identity, String str, Exception exc, Object[] objArr) {
        String format = String.format("%s[this=%s, client=%s](%s) threw", str, obj, printObject(identity), printArgs(objArr));
        Log.e("SoundTriggerMiddlewareLogging", format, exc);
        appendMessage(format + " " + exc.toString());
    }

    public final void appendMessage(String str) {
        Event event = new Event(str);
        synchronized (this.mLastEvents) {
            if (this.mLastEvents.size() > 64) {
                this.mLastEvents.remove();
            }
            this.mLastEvents.add(event);
        }
    }

    @Override // com.android.server.soundtrigger_middleware.Dumpable
    public void dump(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("=========================================");
        printWriter.println("Last events");
        printWriter.println("=========================================");
        synchronized (this.mLastEvents) {
            Iterator<Event> it = this.mLastEvents.iterator();
            while (it.hasNext()) {
                Event next = it.next();
                printWriter.print(DATE_FORMAT.format(new Date(next.timestamp)));
                printWriter.print('\t');
                printWriter.println(next.message);
            }
        }
        printWriter.println();
        ISoundTriggerMiddlewareInternal iSoundTriggerMiddlewareInternal = this.mDelegate;
        if (iSoundTriggerMiddlewareInternal instanceof Dumpable) {
            ((Dumpable) iSoundTriggerMiddlewareInternal).dump(printWriter);
        }
    }
}
