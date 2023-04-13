package com.android.ims;

import android.os.RemoteException;
import android.telephony.ims.ImsService;
import android.telephony.ims.feature.ImsFeature;
import android.util.LocalLog;
import android.util.Log;
import com.android.ims.ImsFeatureBinderRepository;
import com.android.ims.internal.IImsServiceFeatureCallback;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class ImsFeatureBinderRepository {
    private static final String TAG = "ImsFeatureBinderRepo";
    private final List<UpdateMapper> mFeatures = new ArrayList();
    private final LocalLog mLocalLog = new LocalLog(50);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ListenerContainer {
        private final IImsServiceFeatureCallback mCallback;
        private final Executor mExecutor;

        public ListenerContainer(IImsServiceFeatureCallback c, Executor e) {
            this.mCallback = c;
            this.mExecutor = e;
        }

        public void notifyFeatureCreatedOrRemoved(final ImsFeatureContainer connector, final int subId) {
            if (connector == null) {
                this.mExecutor.execute(new Runnable() { // from class: com.android.ims.ImsFeatureBinderRepository$ListenerContainer$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImsFeatureBinderRepository.ListenerContainer.this.lambda$notifyFeatureCreatedOrRemoved$0();
                    }
                });
            } else {
                this.mExecutor.execute(new Runnable() { // from class: com.android.ims.ImsFeatureBinderRepository$ListenerContainer$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImsFeatureBinderRepository.ListenerContainer.this.lambda$notifyFeatureCreatedOrRemoved$1(connector, subId);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyFeatureCreatedOrRemoved$0() {
            try {
                this.mCallback.imsFeatureRemoved(0);
            } catch (RemoteException e) {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyFeatureCreatedOrRemoved$1(ImsFeatureContainer connector, int subId) {
            try {
                this.mCallback.imsFeatureCreated(connector, subId);
            } catch (RemoteException e) {
            }
        }

        public void notifyStateChanged(final int state, final int subId) {
            this.mExecutor.execute(new Runnable() { // from class: com.android.ims.ImsFeatureBinderRepository$ListenerContainer$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsFeatureBinderRepository.ListenerContainer.this.lambda$notifyStateChanged$2(state, subId);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyStateChanged$2(int state, int subId) {
            try {
                this.mCallback.imsStatusChanged(state, subId);
            } catch (RemoteException e) {
            }
        }

        public void notifyUpdateCapabilties(final long caps) {
            this.mExecutor.execute(new Runnable() { // from class: com.android.ims.ImsFeatureBinderRepository$ListenerContainer$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImsFeatureBinderRepository.ListenerContainer.this.lambda$notifyUpdateCapabilties$3(caps);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyUpdateCapabilties$3(long caps) {
            try {
                this.mCallback.updateCapabilities(caps);
            } catch (RemoteException e) {
            }
        }

        public boolean isStale() {
            return !this.mCallback.asBinder().isBinderAlive();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ListenerContainer that = (ListenerContainer) o;
            return this.mCallback.equals(that.mCallback);
        }

        public int hashCode() {
            return Objects.hash(this.mCallback);
        }

        public String toString() {
            return "ListenerContainer{cb=" + this.mCallback + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class UpdateMapper {
        public final int imsFeatureType;
        private ImsFeatureContainer mFeatureContainer;
        private final List<ListenerContainer> mListeners = new ArrayList();
        private final Object mLock = new Object();
        public final int phoneId;
        public int subId;

        public UpdateMapper(int pId, int t) {
            this.phoneId = pId;
            this.imsFeatureType = t;
        }

        public void addFeatureContainer(ImsFeatureContainer c) {
            synchronized (this.mLock) {
                if (Objects.equals(c, this.mFeatureContainer)) {
                    return;
                }
                this.mFeatureContainer = c;
                List<ListenerContainer> listeners = copyListenerList(this.mListeners);
                listeners.forEach(new Consumer() { // from class: com.android.ims.ImsFeatureBinderRepository$UpdateMapper$$ExternalSyntheticLambda4
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ImsFeatureBinderRepository.UpdateMapper.this.lambda$addFeatureContainer$0((ImsFeatureBinderRepository.ListenerContainer) obj);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addFeatureContainer$0(ListenerContainer l) {
            l.notifyFeatureCreatedOrRemoved(this.mFeatureContainer, this.subId);
        }

        public ImsFeatureContainer removeFeatureContainer() {
            synchronized (this.mLock) {
                ImsFeatureContainer oldContainer = this.mFeatureContainer;
                if (oldContainer == null) {
                    return null;
                }
                this.mFeatureContainer = null;
                List<ListenerContainer> listeners = copyListenerList(this.mListeners);
                listeners.forEach(new Consumer() { // from class: com.android.ims.ImsFeatureBinderRepository$UpdateMapper$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ImsFeatureBinderRepository.UpdateMapper.this.lambda$removeFeatureContainer$1((ImsFeatureBinderRepository.ListenerContainer) obj);
                    }
                });
                return oldContainer;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeFeatureContainer$1(ListenerContainer l) {
            l.notifyFeatureCreatedOrRemoved(this.mFeatureContainer, this.subId);
        }

        public ImsFeatureContainer getFeatureContainer() {
            ImsFeatureContainer imsFeatureContainer;
            synchronized (this.mLock) {
                imsFeatureContainer = this.mFeatureContainer;
            }
            return imsFeatureContainer;
        }

        public void addListener(ListenerContainer c) {
            synchronized (this.mLock) {
                removeStaleListeners();
                if (this.mListeners.contains(c)) {
                    return;
                }
                ImsFeatureContainer featureContainer = this.mFeatureContainer;
                this.mListeners.add(c);
                if (featureContainer != null) {
                    c.notifyFeatureCreatedOrRemoved(featureContainer, this.subId);
                }
            }
        }

        public void removeListener(final IImsServiceFeatureCallback callback) {
            synchronized (this.mLock) {
                removeStaleListeners();
                List<ListenerContainer> oldListeners = (List) this.mListeners.stream().filter(new Predicate() { // from class: com.android.ims.ImsFeatureBinderRepository$UpdateMapper$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean equals;
                        equals = Objects.equals(((ImsFeatureBinderRepository.ListenerContainer) obj).mCallback, callback);
                        return equals;
                    }
                }).collect(Collectors.toList());
                this.mListeners.removeAll(oldListeners);
            }
        }

        public void notifyStateUpdated(final int newState) {
            ImsFeatureContainer featureContainer;
            List<ListenerContainer> listeners;
            synchronized (this.mLock) {
                removeStaleListeners();
                featureContainer = this.mFeatureContainer;
                listeners = copyListenerList(this.mListeners);
                ImsFeatureContainer imsFeatureContainer = this.mFeatureContainer;
                if (imsFeatureContainer != null && imsFeatureContainer.getState() != newState) {
                    this.mFeatureContainer.setState(newState);
                }
            }
            if (featureContainer != null) {
                listeners.forEach(new Consumer() { // from class: com.android.ims.ImsFeatureBinderRepository$UpdateMapper$$ExternalSyntheticLambda2
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ImsFeatureBinderRepository.UpdateMapper.this.lambda$notifyStateUpdated$3(newState, (ImsFeatureBinderRepository.ListenerContainer) obj);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyStateUpdated$3(int newState, ListenerContainer l) {
            l.notifyStateChanged(newState, this.subId);
        }

        public void notifyUpdateCapabilities(final long caps) {
            ImsFeatureContainer featureContainer;
            List<ListenerContainer> listeners;
            synchronized (this.mLock) {
                removeStaleListeners();
                featureContainer = this.mFeatureContainer;
                listeners = copyListenerList(this.mListeners);
                ImsFeatureContainer imsFeatureContainer = this.mFeatureContainer;
                if (imsFeatureContainer != null && imsFeatureContainer.getCapabilities() != caps) {
                    this.mFeatureContainer.setCapabilities(caps);
                }
            }
            if (featureContainer != null) {
                listeners.forEach(new Consumer() { // from class: com.android.ims.ImsFeatureBinderRepository$UpdateMapper$$ExternalSyntheticLambda3
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((ImsFeatureBinderRepository.ListenerContainer) obj).notifyUpdateCapabilties(caps);
                    }
                });
            }
        }

        public void updateSubId(int newSubId) {
            this.subId = newSubId;
        }

        private void removeStaleListeners() {
            List<ListenerContainer> staleListeners = (List) this.mListeners.stream().filter(new Predicate() { // from class: com.android.ims.ImsFeatureBinderRepository$UpdateMapper$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ((ImsFeatureBinderRepository.ListenerContainer) obj).isStale();
                }
            }).collect(Collectors.toList());
            this.mListeners.removeAll(staleListeners);
        }

        public String toString() {
            String str;
            synchronized (this.mLock) {
                str = "UpdateMapper{phoneId=" + this.phoneId + ", type=" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(this.imsFeatureType))) + ", container=" + this.mFeatureContainer + '}';
            }
            return str;
        }

        private List<ListenerContainer> copyListenerList(List<ListenerContainer> listeners) {
            return new ArrayList(listeners);
        }
    }

    public ImsFeatureBinderRepository() {
        logInfoLineLocked(-1, "FeatureConnectionRepository - created");
    }

    public Optional<ImsFeatureContainer> getIfExists(int phoneId, int type) {
        if (type < 0 || type >= 3) {
            throw new IllegalArgumentException("Incorrect feature type");
        }
        UpdateMapper m = getUpdateMapper(phoneId, type);
        ImsFeatureContainer c = m.getFeatureContainer();
        logVerboseLineLocked(phoneId, "getIfExists, type= " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(type))) + ", result= " + c);
        return Optional.ofNullable(c);
    }

    public void registerForConnectionUpdates(int phoneId, int type, IImsServiceFeatureCallback callback, Executor executor) {
        if (type < 0 || type >= 3 || callback == null || executor == null) {
            throw new IllegalArgumentException("One or more invalid arguments have been passed in");
        }
        ListenerContainer container = new ListenerContainer(callback, executor);
        logInfoLineLocked(phoneId, "registerForConnectionUpdates, type= " + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(type))) + ", conn= " + container);
        UpdateMapper m = getUpdateMapper(phoneId, type);
        m.addListener(container);
    }

    public void unregisterForConnectionUpdates(IImsServiceFeatureCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("this method does not accept null arguments");
        }
        logInfoLineLocked(-1, "unregisterForConnectionUpdates, callback= " + callback);
        synchronized (this.mFeatures) {
            for (UpdateMapper m : this.mFeatures) {
                m.removeListener(callback);
            }
        }
    }

    public void addConnection(int phoneId, int subId, int type, ImsFeatureContainer newConnection) {
        if (type < 0 || type >= 3) {
            throw new IllegalArgumentException("The type must valid");
        }
        logInfoLineLocked(phoneId, "addConnection, subId=" + subId + ", type=" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(type))) + ", conn=" + newConnection);
        UpdateMapper m = getUpdateMapper(phoneId, type);
        m.updateSubId(subId);
        m.addFeatureContainer(newConnection);
    }

    public ImsFeatureContainer removeConnection(int phoneId, int type) {
        if (type < 0 || type >= 3) {
            throw new IllegalArgumentException("The type must valid");
        }
        logInfoLineLocked(phoneId, "removeConnection, type=" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(type))));
        UpdateMapper m = getUpdateMapper(phoneId, type);
        return m.removeFeatureContainer();
    }

    public void notifyFeatureStateChanged(int phoneId, int type, int state) {
        logInfoLineLocked(phoneId, "notifyFeatureStateChanged, type=" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(type))) + ", state=" + ((String) ImsFeature.STATE_LOG_MAP.get(Integer.valueOf(state))));
        UpdateMapper m = getUpdateMapper(phoneId, type);
        m.notifyStateUpdated(state);
    }

    public void notifyFeatureCapabilitiesChanged(int phoneId, int type, long capabilities) {
        logInfoLineLocked(phoneId, "notifyFeatureCapabilitiesChanged, type=" + ((String) ImsFeature.FEATURE_LOG_MAP.get(Integer.valueOf(type))) + ", caps=" + ImsService.getCapabilitiesString(capabilities));
        UpdateMapper m = getUpdateMapper(phoneId, type);
        m.notifyUpdateCapabilities(capabilities);
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this.mLocalLog) {
            this.mLocalLog.dump(printWriter);
        }
    }

    private UpdateMapper getUpdateMapper(final int phoneId, final int type) {
        UpdateMapper mapper;
        synchronized (this.mFeatures) {
            mapper = this.mFeatures.stream().filter(new Predicate() { // from class: com.android.ims.ImsFeatureBinderRepository$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return ImsFeatureBinderRepository.lambda$getUpdateMapper$0(phoneId, type, (ImsFeatureBinderRepository.UpdateMapper) obj);
                }
            }).findFirst().orElse(null);
            if (mapper == null) {
                mapper = new UpdateMapper(phoneId, type);
                this.mFeatures.add(mapper);
            }
        }
        return mapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getUpdateMapper$0(int phoneId, int type, UpdateMapper c) {
        return c.phoneId == phoneId && c.imsFeatureType == type;
    }

    private void logVerboseLineLocked(int phoneId, String log) {
        if (Log.isLoggable(TAG, 2)) {
            String phoneIdPrefix = "[" + phoneId + "] ";
            Log.v(TAG, phoneIdPrefix + log);
            synchronized (this.mLocalLog) {
                this.mLocalLog.log(phoneIdPrefix + log);
            }
        }
    }

    private void logInfoLineLocked(int phoneId, String log) {
        String phoneIdPrefix = "[" + phoneId + "] ";
        Log.i(TAG, phoneIdPrefix + log);
        synchronized (this.mLocalLog) {
            this.mLocalLog.log(phoneIdPrefix + log);
        }
    }
}
