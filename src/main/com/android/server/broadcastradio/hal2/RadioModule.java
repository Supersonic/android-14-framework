package com.android.server.broadcastradio.hal2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.broadcastradio.V2_0.AmFmRegionConfig;
import android.hardware.broadcastradio.V2_0.Announcement;
import android.hardware.broadcastradio.V2_0.IAnnouncementListener;
import android.hardware.broadcastradio.V2_0.IBroadcastRadio;
import android.hardware.broadcastradio.V2_0.ITunerCallback;
import android.hardware.broadcastradio.V2_0.ITunerSession;
import android.hardware.broadcastradio.V2_0.ProgramInfo;
import android.hardware.broadcastradio.V2_0.ProgramListChunk;
import android.hardware.broadcastradio.V2_0.ProgramSelector;
import android.hardware.broadcastradio.V2_0.VendorKeyValue;
import android.hardware.radio.IAnnouncementListener;
import android.hardware.radio.ICloseHandle;
import android.hardware.radio.ProgramList;
import android.hardware.radio.RadioManager;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.IndentingPrintWriter;
import android.util.MutableInt;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.broadcastradio.hal2.RadioModule;
import com.android.server.broadcastradio.hal2.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class RadioModule {
    public final RadioEventLogger mEventLogger;
    @GuardedBy({"mLock"})
    public ITunerSession mHalTunerSession;
    public final Handler mHandler;
    public final RadioManager.ModuleProperties mProperties;
    public final IBroadcastRadio mService;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public Boolean mAntennaConnected = null;
    @GuardedBy({"mLock"})
    public RadioManager.ProgramInfo mCurrentProgramInfo = null;
    @GuardedBy({"mLock"})
    public final ProgramInfoCache mProgramInfoCache = new ProgramInfoCache(null);
    @GuardedBy({"mLock"})
    public ProgramList.Filter mUnionOfAidlProgramFilters = null;
    public final ITunerCallback mHalTunerCallback = new HwBinderC06451();
    @GuardedBy({"mLock"})
    public final Set<TunerSession> mAidlTunerSessions = new HashSet();

    /* loaded from: classes.dex */
    public interface AidlCallbackRunnable {
        void run(android.hardware.radio.ITunerCallback iTunerCallback) throws RemoteException;
    }

    /* renamed from: com.android.server.broadcastradio.hal2.RadioModule$1 */
    /* loaded from: classes.dex */
    public class HwBinderC06451 extends ITunerCallback.Stub {
        public HwBinderC06451() {
        }

        @Override // android.hardware.broadcastradio.V2_0.ITunerCallback
        public void onTuneFailed(final int i, final ProgramSelector programSelector) {
            RadioModule.this.fireLater(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RadioModule.HwBinderC06451.this.lambda$onTuneFailed$1(programSelector, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTuneFailed$1(ProgramSelector programSelector, int i) {
            final android.hardware.radio.ProgramSelector programSelectorFromHal = Convert.programSelectorFromHal(programSelector);
            final int halResultToTunerResult = Convert.halResultToTunerResult(i);
            synchronized (RadioModule.this.mLock) {
                RadioModule.this.fanoutAidlCallbackLocked(new AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda5
                    @Override // com.android.server.broadcastradio.hal2.RadioModule.AidlCallbackRunnable
                    public final void run(android.hardware.radio.ITunerCallback iTunerCallback) {
                        iTunerCallback.onTuneFailed(halResultToTunerResult, programSelectorFromHal);
                    }
                });
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.ITunerCallback
        public void onCurrentProgramInfoChanged(final ProgramInfo programInfo) {
            RadioModule.this.fireLater(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    RadioModule.HwBinderC06451.this.lambda$onCurrentProgramInfoChanged$3(programInfo);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCurrentProgramInfoChanged$3(ProgramInfo programInfo) {
            synchronized (RadioModule.this.mLock) {
                RadioModule.this.mCurrentProgramInfo = Convert.programInfoFromHal(programInfo);
                final RadioManager.ProgramInfo programInfo2 = RadioModule.this.mCurrentProgramInfo;
                RadioModule.this.fanoutAidlCallbackLocked(new AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda8
                    @Override // com.android.server.broadcastradio.hal2.RadioModule.AidlCallbackRunnable
                    public final void run(android.hardware.radio.ITunerCallback iTunerCallback) {
                        iTunerCallback.onCurrentProgramInfoChanged(programInfo2);
                    }
                });
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.ITunerCallback
        public void onProgramListUpdated(final ProgramListChunk programListChunk) {
            RadioModule.this.fireLater(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    RadioModule.HwBinderC06451.this.lambda$onProgramListUpdated$4(programListChunk);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onProgramListUpdated$4(ProgramListChunk programListChunk) {
            ProgramList.Chunk programListChunkFromHal = Convert.programListChunkFromHal(programListChunk);
            synchronized (RadioModule.this.mLock) {
                RadioModule.this.mProgramInfoCache.filterAndApplyChunk(programListChunkFromHal);
                for (TunerSession tunerSession : RadioModule.this.mAidlTunerSessions) {
                    tunerSession.onMergedProgramListUpdateFromHal(programListChunkFromHal);
                }
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.ITunerCallback
        public void onAntennaStateChange(final boolean z) {
            RadioModule.this.fireLater(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    RadioModule.HwBinderC06451.this.lambda$onAntennaStateChange$6(z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAntennaStateChange$6(final boolean z) {
            synchronized (RadioModule.this.mLock) {
                RadioModule.this.mAntennaConnected = Boolean.valueOf(z);
                RadioModule.this.fanoutAidlCallbackLocked(new AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda6
                    @Override // com.android.server.broadcastradio.hal2.RadioModule.AidlCallbackRunnable
                    public final void run(android.hardware.radio.ITunerCallback iTunerCallback) {
                        iTunerCallback.onAntennaState(z);
                    }
                });
            }
        }

        @Override // android.hardware.broadcastradio.V2_0.ITunerCallback
        public void onParametersUpdated(final ArrayList<VendorKeyValue> arrayList) {
            RadioModule.this.fireLater(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    RadioModule.HwBinderC06451.this.lambda$onParametersUpdated$8(arrayList);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onParametersUpdated$8(ArrayList arrayList) {
            final Map<String, String> vendorInfoFromHal = Convert.vendorInfoFromHal(arrayList);
            synchronized (RadioModule.this.mLock) {
                RadioModule.this.fanoutAidlCallbackLocked(new AidlCallbackRunnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$1$$ExternalSyntheticLambda7
                    @Override // com.android.server.broadcastradio.hal2.RadioModule.AidlCallbackRunnable
                    public final void run(android.hardware.radio.ITunerCallback iTunerCallback) {
                        iTunerCallback.onParametersUpdated(vendorInfoFromHal);
                    }
                });
            }
        }
    }

    @VisibleForTesting
    public RadioModule(IBroadcastRadio iBroadcastRadio, RadioManager.ModuleProperties moduleProperties) {
        Objects.requireNonNull(moduleProperties);
        this.mProperties = moduleProperties;
        Objects.requireNonNull(iBroadcastRadio);
        this.mService = iBroadcastRadio;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mEventLogger = new RadioEventLogger("BcRadio2Srv.module", 25);
    }

    public static RadioModule tryLoadingModule(int i, String str) {
        try {
            Slog.i("BcRadio2Srv.module", "Try loading module for idx " + i + ", fqName " + str);
            IBroadcastRadio service = IBroadcastRadio.getService(str);
            if (service == null) {
                Slog.w("BcRadio2Srv.module", "No service found for fqName " + str);
                return null;
            }
            final Mutable mutable = new Mutable();
            service.getAmFmRegionConfig(false, new IBroadcastRadio.getAmFmRegionConfigCallback() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda3
                @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.getAmFmRegionConfigCallback
                public final void onValues(int i2, AmFmRegionConfig amFmRegionConfig) {
                    RadioModule.lambda$tryLoadingModule$0(Mutable.this, i2, amFmRegionConfig);
                }
            });
            final Mutable mutable2 = new Mutable();
            service.getDabRegionConfig(new IBroadcastRadio.getDabRegionConfigCallback() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda4
                @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.getDabRegionConfigCallback
                public final void onValues(int i2, ArrayList arrayList) {
                    RadioModule.lambda$tryLoadingModule$1(Mutable.this, i2, arrayList);
                }
            });
            return new RadioModule(service, Convert.propertiesFromHal(i, str, service.getProperties(), (AmFmRegionConfig) mutable.value, (List) mutable2.value));
        } catch (RemoteException e) {
            Slog.e("BcRadio2Srv.module", "Failed to load module " + str, e);
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$tryLoadingModule$0(Mutable mutable, int i, AmFmRegionConfig amFmRegionConfig) {
        if (i == 0) {
            mutable.value = amFmRegionConfig;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$tryLoadingModule$1(Mutable mutable, int i, ArrayList arrayList) {
        if (i == 0) {
            mutable.value = arrayList;
        }
    }

    public IBroadcastRadio getService() {
        return this.mService;
    }

    public RadioManager.ModuleProperties getProperties() {
        return this.mProperties;
    }

    public TunerSession openSession(android.hardware.radio.ITunerCallback iTunerCallback) throws RemoteException {
        TunerSession tunerSession;
        this.mEventLogger.logRadioEvent("Open TunerSession", new Object[0]);
        synchronized (this.mLock) {
            if (this.mHalTunerSession == null) {
                final Mutable mutable = new Mutable();
                this.mService.openSession(this.mHalTunerCallback, new IBroadcastRadio.openSessionCallback() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda1
                    @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.openSessionCallback
                    public final void onValues(int i, ITunerSession iTunerSession) {
                        RadioModule.this.lambda$openSession$2(mutable, i, iTunerSession);
                    }
                });
                ITunerSession iTunerSession = (ITunerSession) mutable.value;
                Objects.requireNonNull(iTunerSession);
                this.mHalTunerSession = iTunerSession;
            }
            tunerSession = new TunerSession(this, this.mHalTunerSession, iTunerCallback);
            this.mAidlTunerSessions.add(tunerSession);
            Boolean bool = this.mAntennaConnected;
            if (bool != null) {
                iTunerCallback.onAntennaState(bool.booleanValue());
            }
            RadioManager.ProgramInfo programInfo = this.mCurrentProgramInfo;
            if (programInfo != null) {
                iTunerCallback.onCurrentProgramInfoChanged(programInfo);
            }
        }
        return tunerSession;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$openSession$2(Mutable mutable, int i, ITunerSession iTunerSession) {
        Convert.throwOnError("openSession", i);
        mutable.value = iTunerSession;
        this.mEventLogger.logRadioEvent("New HIDL 2.0 tuner session is opened", new Object[0]);
    }

    public void closeSessions(Integer num) {
        int size;
        TunerSession[] tunerSessionArr;
        this.mEventLogger.logRadioEvent("Close TunerSessions", new Object[0]);
        synchronized (this.mLock) {
            size = this.mAidlTunerSessions.size();
            tunerSessionArr = new TunerSession[size];
            this.mAidlTunerSessions.toArray(tunerSessionArr);
            this.mAidlTunerSessions.clear();
        }
        for (int i = 0; i < size; i++) {
            tunerSessionArr[i].close(num);
        }
    }

    @GuardedBy({"mLock"})
    public final ProgramList.Filter buildUnionOfTunerSessionFiltersLocked() {
        boolean z = false;
        boolean z2 = true;
        HashSet hashSet = null;
        HashSet hashSet2 = null;
        for (TunerSession tunerSession : this.mAidlTunerSessions) {
            ProgramList.Filter programListFilter = tunerSession.getProgramListFilter();
            if (programListFilter != null) {
                if (hashSet == null) {
                    hashSet = new HashSet(programListFilter.getIdentifierTypes());
                    hashSet2 = new HashSet(programListFilter.getIdentifiers());
                    z = programListFilter.areCategoriesIncluded();
                    z2 = programListFilter.areModificationsExcluded();
                } else {
                    if (!hashSet.isEmpty()) {
                        if (programListFilter.getIdentifierTypes().isEmpty()) {
                            hashSet.clear();
                        } else {
                            hashSet.addAll(programListFilter.getIdentifierTypes());
                        }
                    }
                    if (!hashSet2.isEmpty()) {
                        if (programListFilter.getIdentifiers().isEmpty()) {
                            hashSet2.clear();
                        } else {
                            hashSet2.addAll(programListFilter.getIdentifiers());
                        }
                    }
                    z |= programListFilter.areCategoriesIncluded();
                    z2 &= programListFilter.areModificationsExcluded();
                }
            }
        }
        if (hashSet == null) {
            return null;
        }
        return new ProgramList.Filter(hashSet, hashSet2, z, z2);
    }

    public void onTunerSessionProgramListFilterChanged(TunerSession tunerSession) {
        synchronized (this.mLock) {
            onTunerSessionProgramListFilterChangedLocked(tunerSession);
        }
    }

    @GuardedBy({"mLock"})
    public final void onTunerSessionProgramListFilterChangedLocked(TunerSession tunerSession) {
        ProgramList.Filter buildUnionOfTunerSessionFiltersLocked = buildUnionOfTunerSessionFiltersLocked();
        if (buildUnionOfTunerSessionFiltersLocked == null) {
            if (this.mUnionOfAidlProgramFilters == null) {
                return;
            }
            this.mUnionOfAidlProgramFilters = null;
            try {
                this.mHalTunerSession.stopProgramListUpdates();
            } catch (RemoteException e) {
                Slog.e("BcRadio2Srv.module", "mHalTunerSession.stopProgramListUpdates() failed: ", e);
            }
        } else if (buildUnionOfTunerSessionFiltersLocked.equals(this.mUnionOfAidlProgramFilters)) {
            if (tunerSession != null) {
                tunerSession.updateProgramInfoFromHalCache(this.mProgramInfoCache);
            }
        } else {
            this.mUnionOfAidlProgramFilters = buildUnionOfTunerSessionFiltersLocked;
            try {
                Convert.throwOnError("startProgramListUpdates", this.mHalTunerSession.startProgramListUpdates(Convert.programFilterToHal(buildUnionOfTunerSessionFiltersLocked)));
            } catch (RemoteException e2) {
                Slog.e("BcRadio2Srv.module", "mHalTunerSession.startProgramListUpdates() failed: ", e2);
            }
        }
    }

    public void onTunerSessionClosed(TunerSession tunerSession) {
        synchronized (this.mLock) {
            onTunerSessionsClosedLocked(tunerSession);
        }
    }

    @GuardedBy({"mLock"})
    public final void onTunerSessionsClosedLocked(TunerSession... tunerSessionArr) {
        for (TunerSession tunerSession : tunerSessionArr) {
            this.mAidlTunerSessions.remove(tunerSession);
        }
        onTunerSessionProgramListFilterChanged(null);
        if (!this.mAidlTunerSessions.isEmpty() || this.mHalTunerSession == null) {
            return;
        }
        this.mEventLogger.logRadioEvent("Closing HAL tuner session", new Object[0]);
        try {
            this.mHalTunerSession.close();
        } catch (RemoteException e) {
            Slog.e("BcRadio2Srv.module", "mHalTunerSession.close() failed: ", e);
        }
        this.mHalTunerSession = null;
    }

    public final void fireLater(final Runnable runnable) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                runnable.run();
            }
        });
    }

    public void fanoutAidlCallback(final AidlCallbackRunnable aidlCallbackRunnable) {
        fireLater(new Runnable() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                RadioModule.this.lambda$fanoutAidlCallback$4(aidlCallbackRunnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fanoutAidlCallback$4(AidlCallbackRunnable aidlCallbackRunnable) {
        synchronized (this.mLock) {
            fanoutAidlCallbackLocked(aidlCallbackRunnable);
        }
    }

    @GuardedBy({"mLock"})
    public final void fanoutAidlCallbackLocked(AidlCallbackRunnable aidlCallbackRunnable) {
        ArrayList arrayList = null;
        for (TunerSession tunerSession : this.mAidlTunerSessions) {
            try {
                aidlCallbackRunnable.run(tunerSession.mCallback);
            } catch (DeadObjectException unused) {
                Slog.e("BcRadio2Srv.module", "Removing dead TunerSession");
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(tunerSession);
            } catch (RemoteException e) {
                Slog.e("BcRadio2Srv.module", "Failed to invoke ITunerCallback: ", e);
            }
        }
        if (arrayList != null) {
            onTunerSessionsClosedLocked((TunerSession[]) arrayList.toArray(new TunerSession[arrayList.size()]));
        }
    }

    public ICloseHandle addAnnouncementListener(int[] iArr, IAnnouncementListener iAnnouncementListener) throws RemoteException {
        this.mEventLogger.logRadioEvent("Add AnnouncementListener", new Object[0]);
        ArrayList<Byte> arrayList = new ArrayList<>();
        for (int i : iArr) {
            arrayList.add(Byte.valueOf((byte) i));
        }
        final MutableInt mutableInt = new MutableInt(1);
        final Mutable mutable = new Mutable();
        this.mService.registerAnnouncementListener(arrayList, new HwBinderC06462(iAnnouncementListener), new IBroadcastRadio.registerAnnouncementListenerCallback() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda2
            @Override // android.hardware.broadcastradio.V2_0.IBroadcastRadio.registerAnnouncementListenerCallback
            public final void onValues(int i2, android.hardware.broadcastradio.V2_0.ICloseHandle iCloseHandle) {
                RadioModule.lambda$addAnnouncementListener$5(mutableInt, mutable, i2, iCloseHandle);
            }
        });
        Convert.throwOnError("addAnnouncementListener", mutableInt.value);
        return new ICloseHandle.Stub() { // from class: com.android.server.broadcastradio.hal2.RadioModule.3
            public void close() {
                try {
                    ((android.hardware.broadcastradio.V2_0.ICloseHandle) mutable.value).close();
                } catch (RemoteException e) {
                    Slog.e("BcRadio2Srv.module", "Failed closing announcement listener", e);
                }
                mutable.value = null;
            }
        };
    }

    /* renamed from: com.android.server.broadcastradio.hal2.RadioModule$2 */
    /* loaded from: classes.dex */
    public class HwBinderC06462 extends IAnnouncementListener.Stub {
        public final /* synthetic */ android.hardware.radio.IAnnouncementListener val$listener;

        public HwBinderC06462(android.hardware.radio.IAnnouncementListener iAnnouncementListener) {
            this.val$listener = iAnnouncementListener;
        }

        @Override // android.hardware.broadcastradio.V2_0.IAnnouncementListener
        public void onListUpdated(ArrayList<Announcement> arrayList) throws RemoteException {
            this.val$listener.onListUpdated((List) arrayList.stream().map(new Function() { // from class: com.android.server.broadcastradio.hal2.RadioModule$2$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    android.hardware.radio.Announcement announcementFromHal;
                    announcementFromHal = Convert.announcementFromHal((Announcement) obj);
                    return announcementFromHal;
                }
            }).collect(Collectors.toList()));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ void lambda$addAnnouncementListener$5(MutableInt mutableInt, Mutable mutable, int i, android.hardware.broadcastradio.V2_0.ICloseHandle iCloseHandle) {
        mutableInt.value = i;
        mutable.value = iCloseHandle;
    }

    public Bitmap getImage(final int i) {
        this.mEventLogger.logRadioEvent("Get image for id %d", Integer.valueOf(i));
        if (i == 0) {
            throw new IllegalArgumentException("Image ID is missing");
        }
        List list = (List) Utils.maybeRethrow(new Utils.FuncThrowingRemoteException() { // from class: com.android.server.broadcastradio.hal2.RadioModule$$ExternalSyntheticLambda0
            @Override // com.android.server.broadcastradio.hal2.Utils.FuncThrowingRemoteException
            public final Object exec() {
                ArrayList lambda$getImage$6;
                lambda$getImage$6 = RadioModule.this.lambda$getImage$6(i);
                return lambda$getImage$6;
            }
        });
        int size = list.size();
        byte[] bArr = new byte[size];
        for (int i2 = 0; i2 < list.size(); i2++) {
            bArr[i2] = ((Byte) list.get(i2)).byteValue();
        }
        if (size == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bArr, 0, size);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ ArrayList lambda$getImage$6(int i) throws RemoteException {
        return this.mService.getImage(i);
    }

    public void dumpInfo(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.printf("RadioModule\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.printf("BroadcastRadioService: %s\n", new Object[]{this.mService});
        indentingPrintWriter.printf("Properties: %s\n", new Object[]{this.mProperties});
        synchronized (this.mLock) {
            indentingPrintWriter.printf("HIDL 2.0 HAL TunerSession: %s\n", new Object[]{this.mHalTunerSession});
            indentingPrintWriter.printf("Is antenna connected? ", new Object[0]);
            Boolean bool = this.mAntennaConnected;
            if (bool == null) {
                indentingPrintWriter.printf("null\n", new Object[0]);
            } else {
                Object[] objArr = new Object[1];
                objArr[0] = bool.booleanValue() ? "Yes" : "No";
                indentingPrintWriter.printf("%s\n", objArr);
            }
            indentingPrintWriter.printf("Current ProgramInfo: %s\n", new Object[]{this.mCurrentProgramInfo});
            indentingPrintWriter.printf("ProgramInfoCache: %s\n", new Object[]{this.mProgramInfoCache});
            indentingPrintWriter.printf("Union of AIDL ProgramFilters: %s\n", new Object[]{this.mUnionOfAidlProgramFilters});
            indentingPrintWriter.printf("AIDL TunerSessions:\n", new Object[0]);
            indentingPrintWriter.increaseIndent();
            for (TunerSession tunerSession : this.mAidlTunerSessions) {
                tunerSession.dumpInfo(indentingPrintWriter);
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.printf("Radio module events:\n", new Object[0]);
        indentingPrintWriter.increaseIndent();
        this.mEventLogger.dump(indentingPrintWriter);
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
    }
}
