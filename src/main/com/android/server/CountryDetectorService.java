package com.android.server;

import android.content.Context;
import android.location.Country;
import android.location.CountryListener;
import android.location.ICountryDetector;
import android.location.ICountryListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.server.location.countrydetector.ComprehensiveCountryDetector;
import com.android.server.location.countrydetector.CountryDetectorBase;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
/* loaded from: classes.dex */
public class CountryDetectorService extends ICountryDetector.Stub {
    public final Context mContext;
    public CountryDetectorBase mCountryDetector;
    public Handler mHandler;
    public CountryListener mLocationBasedDetectorListener;
    public final HashMap<IBinder, Receiver> mReceivers;
    public boolean mSystemReady;

    /* loaded from: classes.dex */
    public final class Receiver implements IBinder.DeathRecipient {
        public final IBinder mKey;
        public final ICountryListener mListener;

        public Receiver(ICountryListener iCountryListener) {
            this.mListener = iCountryListener;
            this.mKey = iCountryListener.asBinder();
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            CountryDetectorService.this.removeListener(this.mKey);
        }

        public boolean equals(Object obj) {
            if (obj instanceof Receiver) {
                return this.mKey.equals(((Receiver) obj).mKey);
            }
            return false;
        }

        public int hashCode() {
            return this.mKey.hashCode();
        }

        public ICountryListener getListener() {
            return this.mListener;
        }
    }

    public CountryDetectorService(Context context) {
        this(context, BackgroundThread.getHandler());
    }

    @VisibleForTesting
    public CountryDetectorService(Context context, Handler handler) {
        this.mReceivers = new HashMap<>();
        this.mContext = context;
        this.mHandler = handler;
    }

    public Country detectCountry() {
        if (this.mSystemReady) {
            return this.mCountryDetector.detectCountry();
        }
        return null;
    }

    public void addCountryListener(ICountryListener iCountryListener) throws RemoteException {
        if (!this.mSystemReady) {
            throw new RemoteException();
        }
        addListener(iCountryListener);
    }

    public void removeCountryListener(ICountryListener iCountryListener) throws RemoteException {
        if (!this.mSystemReady) {
            throw new RemoteException();
        }
        removeListener(iCountryListener.asBinder());
    }

    public final void addListener(ICountryListener iCountryListener) {
        synchronized (this.mReceivers) {
            Receiver receiver = new Receiver(iCountryListener);
            try {
                iCountryListener.asBinder().linkToDeath(receiver, 0);
                Country detectCountry = detectCountry();
                if (detectCountry != null) {
                    iCountryListener.onCountryDetected(detectCountry);
                }
                this.mReceivers.put(iCountryListener.asBinder(), receiver);
                if (this.mReceivers.size() == 1) {
                    Slog.d("CountryDetector", "The first listener is added");
                    setCountryListener(this.mLocationBasedDetectorListener);
                }
            } catch (RemoteException e) {
                Slog.e("CountryDetector", "linkToDeath failed:", e);
            }
        }
    }

    public final void removeListener(IBinder iBinder) {
        synchronized (this.mReceivers) {
            this.mReceivers.remove(iBinder);
            if (this.mReceivers.isEmpty()) {
                setCountryListener(null);
                Slog.d("CountryDetector", "No listener is left");
            }
        }
    }

    /* renamed from: notifyReceivers */
    public void lambda$initialize$1(Country country) {
        synchronized (this.mReceivers) {
            for (Receiver receiver : this.mReceivers.values()) {
                try {
                    receiver.getListener().onCountryDetected(country);
                } catch (RemoteException e) {
                    Slog.e("CountryDetector", "notifyReceivers failed:", e);
                }
            }
        }
    }

    public void systemRunning() {
        this.mHandler.post(new Runnable() { // from class: com.android.server.CountryDetectorService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                CountryDetectorService.this.lambda$systemRunning$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$systemRunning$0() {
        initialize();
        this.mSystemReady = true;
    }

    @VisibleForTesting
    public void initialize() {
        String string = this.mContext.getString(17039861);
        if (!TextUtils.isEmpty(string)) {
            this.mCountryDetector = loadCustomCountryDetectorIfAvailable(string);
        }
        if (this.mCountryDetector == null) {
            Slog.d("CountryDetector", "Using default country detector");
            this.mCountryDetector = new ComprehensiveCountryDetector(this.mContext);
        }
        this.mLocationBasedDetectorListener = new CountryListener() { // from class: com.android.server.CountryDetectorService$$ExternalSyntheticLambda0
            public final void onCountryDetected(Country country) {
                CountryDetectorService.this.lambda$initialize$2(country);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initialize$2(final Country country) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.CountryDetectorService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                CountryDetectorService.this.lambda$initialize$1(country);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setCountryListener$3(CountryListener countryListener) {
        this.mCountryDetector.setCountryListener(countryListener);
    }

    public void setCountryListener(final CountryListener countryListener) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.CountryDetectorService$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CountryDetectorService.this.lambda$setCountryListener$3(countryListener);
            }
        });
    }

    @VisibleForTesting
    public CountryDetectorBase getCountryDetector() {
        return this.mCountryDetector;
    }

    @VisibleForTesting
    public boolean isSystemReady() {
        return this.mSystemReady;
    }

    public final CountryDetectorBase loadCustomCountryDetectorIfAvailable(String str) {
        Slog.d("CountryDetector", "Using custom country detector class: " + str);
        try {
            return (CountryDetectorBase) Class.forName(str).asSubclass(CountryDetectorBase.class).getConstructor(Context.class).newInstance(this.mContext);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException unused) {
            Slog.e("CountryDetector", "Could not instantiate the custom country detector class");
            return null;
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        DumpUtils.checkDumpPermission(this.mContext, "CountryDetector", printWriter);
    }
}
