package com.android.server.p012tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiHotplugEvent;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.media.AudioDevicePort;
import android.media.AudioDevicePortConfig;
import android.media.AudioFormat;
import android.media.AudioGain;
import android.media.AudioGainConfig;
import android.media.AudioManager;
import android.media.AudioPatch;
import android.media.AudioPort;
import android.media.AudioPortConfig;
import android.media.tv.ITvInputHardware;
import android.media.tv.ITvInputHardwareCallback;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvStreamConfig;
import android.media.tv.tunerresourcemanager.ResourceClientProfile;
import android.media.tv.tunerresourcemanager.TunerResourceManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Surface;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.p012tv.TvInputHal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/* renamed from: com.android.server.tv.TvInputHardwareManager */
/* loaded from: classes2.dex */
public class TvInputHardwareManager implements TvInputHal.Callback {
    public static final String TAG = "TvInputHardwareManager";
    public final AudioManager mAudioManager;
    public final SparseArray<Connection> mConnections;
    public final Context mContext;
    public int mCurrentIndex;
    public int mCurrentMaxIndex;
    public final TvInputHal mHal;
    public final Handler mHandler;
    public final SparseArray<String> mHardwareInputIdMap;
    public final List<TvInputHardwareInfo> mHardwareList;
    public final IHdmiDeviceEventListener mHdmiDeviceEventListener;
    public final List<HdmiDeviceInfo> mHdmiDeviceList;
    public final IHdmiHotplugEventListener mHdmiHotplugEventListener;
    public final SparseArray<String> mHdmiInputIdMap;
    public final SparseBooleanArray mHdmiStateMap;
    public final IHdmiSystemAudioModeChangeListener mHdmiSystemAudioModeChangeListener;
    public final Map<String, TvInputInfo> mInputMap;
    public final Listener mListener;
    public final Object mLock;
    public final List<Message> mPendingHdmiDeviceEvents;
    public final List<Message> mPendingTvinputInfoEvents;
    public final BroadcastReceiver mVolumeReceiver;

    /* renamed from: com.android.server.tv.TvInputHardwareManager$Listener */
    /* loaded from: classes2.dex */
    public interface Listener {
        void onHardwareDeviceAdded(TvInputHardwareInfo tvInputHardwareInfo);

        void onHardwareDeviceRemoved(TvInputHardwareInfo tvInputHardwareInfo);

        void onHdmiDeviceAdded(HdmiDeviceInfo hdmiDeviceInfo);

        void onHdmiDeviceRemoved(HdmiDeviceInfo hdmiDeviceInfo);

        void onHdmiDeviceUpdated(String str, HdmiDeviceInfo hdmiDeviceInfo);

        void onStateChanged(String str, int i);
    }

    public TvInputHardwareManager(Context context, Listener listener) {
        TvInputHal tvInputHal = new TvInputHal(this);
        this.mHal = tvInputHal;
        this.mConnections = new SparseArray<>();
        this.mHardwareList = new ArrayList();
        this.mHdmiDeviceList = new ArrayList();
        this.mHardwareInputIdMap = new SparseArray<>();
        this.mHdmiInputIdMap = new SparseArray<>();
        this.mInputMap = new ArrayMap();
        this.mHdmiHotplugEventListener = new HdmiHotplugEventListener();
        this.mHdmiDeviceEventListener = new HdmiDeviceEventListener();
        this.mHdmiSystemAudioModeChangeListener = new HdmiSystemAudioModeChangeListener();
        this.mVolumeReceiver = new BroadcastReceiver() { // from class: com.android.server.tv.TvInputHardwareManager.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                TvInputHardwareManager.this.handleVolumeChange(context2, intent);
            }
        };
        this.mCurrentIndex = 0;
        this.mCurrentMaxIndex = 0;
        this.mHdmiStateMap = new SparseBooleanArray();
        this.mPendingHdmiDeviceEvents = new ArrayList();
        this.mPendingTvinputInfoEvents = new ArrayList();
        this.mHandler = new ListenerHandler();
        this.mLock = new Object();
        this.mContext = context;
        this.mListener = listener;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        tvInputHal.init();
    }

    public void onBootPhase(int i) {
        if (i == 500) {
            IHdmiControlService asInterface = IHdmiControlService.Stub.asInterface(ServiceManager.getService("hdmi_control"));
            if (asInterface != null) {
                try {
                    asInterface.addHotplugEventListener(this.mHdmiHotplugEventListener);
                    asInterface.addDeviceEventListener(this.mHdmiDeviceEventListener);
                    asInterface.addSystemAudioModeChangeListener(this.mHdmiSystemAudioModeChangeListener);
                    this.mHdmiDeviceList.addAll(asInterface.getInputDevices());
                } catch (RemoteException e) {
                    Slog.w(TAG, "Error registering listeners to HdmiControlService:", e);
                }
            } else {
                Slog.w(TAG, "HdmiControlService is not available");
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
            intentFilter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
            this.mContext.registerReceiver(this.mVolumeReceiver, intentFilter);
            updateVolume();
        }
    }

    @Override // com.android.server.p012tv.TvInputHal.Callback
    public void onDeviceAvailable(TvInputHardwareInfo tvInputHardwareInfo, TvStreamConfig[] tvStreamConfigArr) {
        synchronized (this.mLock) {
            Connection connection = new Connection(tvInputHardwareInfo);
            connection.updateConfigsLocked(tvStreamConfigArr);
            connection.updateCableConnectionStatusLocked(tvInputHardwareInfo.getCableConnectionStatus());
            this.mConnections.put(tvInputHardwareInfo.getDeviceId(), connection);
            buildHardwareListLocked();
            this.mHandler.obtainMessage(2, 0, 0, tvInputHardwareInfo).sendToTarget();
            if (tvInputHardwareInfo.getType() == 9) {
                processPendingHdmiDeviceEventsLocked();
            }
        }
    }

    public final void buildHardwareListLocked() {
        this.mHardwareList.clear();
        for (int i = 0; i < this.mConnections.size(); i++) {
            this.mHardwareList.add(this.mConnections.valueAt(i).getHardwareInfoLocked());
        }
    }

    @Override // com.android.server.p012tv.TvInputHal.Callback
    public void onDeviceUnavailable(int i) {
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(i);
            if (connection == null) {
                String str = TAG;
                Slog.e(str, "onDeviceUnavailable: Cannot find a connection with " + i);
                return;
            }
            connection.resetLocked(null, null, null, null, null, null);
            this.mConnections.remove(i);
            buildHardwareListLocked();
            TvInputHardwareInfo hardwareInfoLocked = connection.getHardwareInfoLocked();
            if (hardwareInfoLocked.getType() == 9) {
                Iterator<HdmiDeviceInfo> it = this.mHdmiDeviceList.iterator();
                while (it.hasNext()) {
                    HdmiDeviceInfo next = it.next();
                    if (next.getPortId() == hardwareInfoLocked.getHdmiPortId()) {
                        this.mHandler.obtainMessage(5, 0, 0, next).sendToTarget();
                        it.remove();
                    }
                }
            }
            this.mHandler.obtainMessage(3, 0, 0, hardwareInfoLocked).sendToTarget();
        }
    }

    @Override // com.android.server.p012tv.TvInputHal.Callback
    public void onStreamConfigurationChanged(final int i, TvStreamConfig[] tvStreamConfigArr, int i2) {
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(i);
            if (connection == null) {
                String str = TAG;
                Slog.e(str, "StreamConfigurationChanged: Cannot find a connection with " + i);
                return;
            }
            int configsLengthLocked = connection.getConfigsLengthLocked();
            int inputStateLocked = connection.getInputStateLocked();
            connection.updateConfigsLocked(tvStreamConfigArr);
            String str2 = this.mHardwareInputIdMap.get(i);
            if (str2 != null) {
                if (connection.updateCableConnectionStatusLocked(i2)) {
                    if (inputStateLocked != connection.getInputStateLocked()) {
                        this.mHandler.obtainMessage(1, connection.getInputStateLocked(), 0, str2).sendToTarget();
                    }
                } else {
                    if ((configsLengthLocked == 0) != (connection.getConfigsLengthLocked() == 0)) {
                        this.mHandler.obtainMessage(1, connection.getInputStateLocked(), 0, str2).sendToTarget();
                    }
                }
            } else {
                Message obtainMessage = this.mHandler.obtainMessage(7, i, i2, connection);
                this.mPendingTvinputInfoEvents.removeIf(new Predicate() { // from class: com.android.server.tv.TvInputHardwareManager$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$onStreamConfigurationChanged$0;
                        lambda$onStreamConfigurationChanged$0 = TvInputHardwareManager.lambda$onStreamConfigurationChanged$0(i, (Message) obj);
                        return lambda$onStreamConfigurationChanged$0;
                    }
                });
                this.mPendingTvinputInfoEvents.add(obtainMessage);
            }
            ITvInputHardwareCallback callbackLocked = connection.getCallbackLocked();
            if (callbackLocked != null) {
                try {
                    callbackLocked.onStreamConfigChanged(tvStreamConfigArr);
                } catch (RemoteException e) {
                    Slog.e(TAG, "error in onStreamConfigurationChanged", e);
                }
            }
        }
    }

    public static /* synthetic */ boolean lambda$onStreamConfigurationChanged$0(int i, Message message) {
        return message.arg1 == i;
    }

    @Override // com.android.server.p012tv.TvInputHal.Callback
    public void onFirstFrameCaptured(int i, int i2) {
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(i);
            if (connection == null) {
                String str = TAG;
                Slog.e(str, "FirstFrameCaptured: Cannot find a connection with " + i);
                return;
            }
            Runnable onFirstFrameCapturedLocked = connection.getOnFirstFrameCapturedLocked();
            if (onFirstFrameCapturedLocked != null) {
                onFirstFrameCapturedLocked.run();
                connection.setOnFirstFrameCapturedLocked(null);
            }
        }
    }

    public List<TvInputHardwareInfo> getHardwareList() {
        List<TvInputHardwareInfo> unmodifiableList;
        synchronized (this.mLock) {
            unmodifiableList = Collections.unmodifiableList(this.mHardwareList);
        }
        return unmodifiableList;
    }

    public List<HdmiDeviceInfo> getHdmiDeviceList() {
        List<HdmiDeviceInfo> unmodifiableList;
        synchronized (this.mLock) {
            unmodifiableList = Collections.unmodifiableList(this.mHdmiDeviceList);
        }
        return unmodifiableList;
    }

    public final boolean checkUidChangedLocked(Connection connection, int i, int i2) {
        Integer callingUidLocked = connection.getCallingUidLocked();
        Integer resolvedUserIdLocked = connection.getResolvedUserIdLocked();
        return callingUidLocked == null || resolvedUserIdLocked == null || callingUidLocked.intValue() != i || resolvedUserIdLocked.intValue() != i2;
    }

    public void addHardwareInput(int i, TvInputInfo tvInputInfo) {
        String str;
        synchronized (this.mLock) {
            String str2 = this.mHardwareInputIdMap.get(i);
            if (str2 != null) {
                String str3 = TAG;
                Slog.w(str3, "Trying to override previous registration: old = " + this.mInputMap.get(str2) + XmlUtils.STRING_ARRAY_SEPARATOR + i + ", new = " + tvInputInfo + XmlUtils.STRING_ARRAY_SEPARATOR + i);
            }
            this.mHardwareInputIdMap.put(i, tvInputInfo.getId());
            this.mInputMap.put(tvInputInfo.getId(), tvInputInfo);
            processPendingTvInputInfoEventsLocked();
            String str4 = TAG;
            Slog.d(str4, "deviceId =" + i + ", tvinputinfo = " + tvInputInfo);
            for (int i2 = 0; i2 < this.mHdmiStateMap.size(); i2++) {
                TvInputHardwareInfo findHardwareInfoForHdmiPortLocked = findHardwareInfoForHdmiPortLocked(this.mHdmiStateMap.keyAt(i2));
                if (findHardwareInfoForHdmiPortLocked != null && (str = this.mHardwareInputIdMap.get(findHardwareInfoForHdmiPortLocked.getDeviceId())) != null && str.equals(tvInputInfo.getId())) {
                    this.mHandler.obtainMessage(1, this.mHdmiStateMap.valueAt(i2) ? 0 : 1, 0, str).sendToTarget();
                    return;
                }
            }
            Connection connection = this.mConnections.get(i);
            if (connection != null) {
                this.mHandler.obtainMessage(1, connection.getInputStateLocked(), 0, tvInputInfo.getId()).sendToTarget();
            }
        }
    }

    public static <T> int indexOfEqualValue(SparseArray<T> sparseArray, T t) {
        for (int i = 0; i < sparseArray.size(); i++) {
            if (sparseArray.valueAt(i).equals(t)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean intArrayContains(int[] iArr, int i) {
        for (int i2 : iArr) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public void addHdmiInput(int i, TvInputInfo tvInputInfo) {
        if (tvInputInfo.getType() != 1007) {
            throw new IllegalArgumentException("info (" + tvInputInfo + ") has non-HDMI type.");
        }
        synchronized (this.mLock) {
            if (indexOfEqualValue(this.mHardwareInputIdMap, tvInputInfo.getParentId()) < 0) {
                throw new IllegalArgumentException("info (" + tvInputInfo + ") has invalid parentId.");
            }
            String str = this.mHdmiInputIdMap.get(i);
            if (str != null) {
                String str2 = TAG;
                Slog.w(str2, "Trying to override previous registration: old = " + this.mInputMap.get(str) + XmlUtils.STRING_ARRAY_SEPARATOR + i + ", new = " + tvInputInfo + XmlUtils.STRING_ARRAY_SEPARATOR + i);
            }
            this.mHdmiInputIdMap.put(i, tvInputInfo.getId());
            this.mInputMap.put(tvInputInfo.getId(), tvInputInfo);
        }
    }

    public void removeHardwareInput(String str) {
        synchronized (this.mLock) {
            this.mInputMap.remove(str);
            int indexOfEqualValue = indexOfEqualValue(this.mHardwareInputIdMap, str);
            if (indexOfEqualValue >= 0) {
                this.mHardwareInputIdMap.removeAt(indexOfEqualValue);
            }
            int indexOfEqualValue2 = indexOfEqualValue(this.mHdmiInputIdMap, str);
            if (indexOfEqualValue2 >= 0) {
                this.mHdmiInputIdMap.removeAt(indexOfEqualValue2);
            }
        }
    }

    public ITvInputHardware acquireHardware(int i, ITvInputHardwareCallback iTvInputHardwareCallback, TvInputInfo tvInputInfo, int i2, int i3, String str, int i4) {
        iTvInputHardwareCallback.getClass();
        TunerResourceManager tunerResourceManager = (TunerResourceManager) this.mContext.getSystemService("tv_tuner_resource_mgr");
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(i);
            if (connection == null) {
                String str2 = TAG;
                Slog.e(str2, "Invalid deviceId : " + i);
                return null;
            }
            ResourceClientProfile resourceClientProfile = new ResourceClientProfile();
            resourceClientProfile.tvInputSessionId = str;
            resourceClientProfile.useCase = i4;
            ResourceClientProfile resourceClientProfileLocked = connection.getResourceClientProfileLocked();
            if (resourceClientProfileLocked != null && tunerResourceManager != null && !tunerResourceManager.isHigherPriority(resourceClientProfile, resourceClientProfileLocked)) {
                String str3 = TAG;
                Slog.d(str3, "Acquiring does not show higher priority than the current holder. Device id:" + i);
                return null;
            }
            TvInputHardwareImpl tvInputHardwareImpl = new TvInputHardwareImpl(connection.getHardwareInfoLocked());
            try {
                iTvInputHardwareCallback.asBinder().linkToDeath(connection, 0);
                connection.resetLocked(tvInputHardwareImpl, iTvInputHardwareCallback, tvInputInfo, Integer.valueOf(i2), Integer.valueOf(i3), resourceClientProfile);
                return connection.getHardwareLocked();
            } catch (RemoteException unused) {
                tvInputHardwareImpl.release();
                return null;
            }
        }
    }

    public void releaseHardware(int i, ITvInputHardware iTvInputHardware, int i2, int i3) {
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(i);
            if (connection == null) {
                String str = TAG;
                Slog.e(str, "Invalid deviceId : " + i);
                return;
            }
            if (connection.getHardwareLocked() == iTvInputHardware && !checkUidChangedLocked(connection, i2, i3)) {
                ITvInputHardwareCallback callbackLocked = connection.getCallbackLocked();
                if (callbackLocked != null) {
                    callbackLocked.asBinder().unlinkToDeath(connection, 0);
                }
                connection.resetLocked(null, null, null, null, null, null);
            }
        }
    }

    public final TvInputHardwareInfo findHardwareInfoForHdmiPortLocked(int i) {
        for (TvInputHardwareInfo tvInputHardwareInfo : this.mHardwareList) {
            if (tvInputHardwareInfo.getType() == 9 && tvInputHardwareInfo.getHdmiPortId() == i) {
                return tvInputHardwareInfo;
            }
        }
        return null;
    }

    public final int findDeviceIdForInputIdLocked(String str) {
        for (int i = 0; i < this.mConnections.size(); i++) {
            if (this.mConnections.get(i).getInfoLocked().getId().equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public List<TvStreamConfig> getAvailableTvStreamConfigList(String str, int i, int i2) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            int findDeviceIdForInputIdLocked = findDeviceIdForInputIdLocked(str);
            if (findDeviceIdForInputIdLocked < 0) {
                Slog.e(TAG, "Invalid inputId : " + str);
                return arrayList;
            }
            TvStreamConfig[] configsLocked = this.mConnections.get(findDeviceIdForInputIdLocked).getConfigsLocked();
            for (TvStreamConfig tvStreamConfig : configsLocked) {
                if (tvStreamConfig.getType() == 2) {
                    arrayList.add(tvStreamConfig);
                }
            }
            return arrayList;
        }
    }

    public boolean captureFrame(String str, Surface surface, final TvStreamConfig tvStreamConfig, int i, int i2) {
        synchronized (this.mLock) {
            int findDeviceIdForInputIdLocked = findDeviceIdForInputIdLocked(str);
            if (findDeviceIdForInputIdLocked < 0) {
                String str2 = TAG;
                Slog.e(str2, "Invalid inputId : " + str);
                return false;
            }
            Connection connection = this.mConnections.get(findDeviceIdForInputIdLocked);
            final TvInputHardwareImpl hardwareImplLocked = connection.getHardwareImplLocked();
            if (hardwareImplLocked != null) {
                Runnable onFirstFrameCapturedLocked = connection.getOnFirstFrameCapturedLocked();
                if (onFirstFrameCapturedLocked != null) {
                    onFirstFrameCapturedLocked.run();
                    connection.setOnFirstFrameCapturedLocked(null);
                }
                boolean startCapture = hardwareImplLocked.startCapture(surface, tvStreamConfig);
                if (startCapture) {
                    connection.setOnFirstFrameCapturedLocked(new Runnable() { // from class: com.android.server.tv.TvInputHardwareManager.2
                        @Override // java.lang.Runnable
                        public void run() {
                            hardwareImplLocked.stopCapture(tvStreamConfig);
                        }
                    });
                }
                return startCapture;
            }
            return false;
        }
    }

    public final void processPendingHdmiDeviceEventsLocked() {
        Iterator<Message> it = this.mPendingHdmiDeviceEvents.iterator();
        while (it.hasNext()) {
            Message next = it.next();
            if (findHardwareInfoForHdmiPortLocked(((HdmiDeviceInfo) next.obj).getPortId()) != null) {
                next.sendToTarget();
                it.remove();
            }
        }
    }

    public final void processPendingTvInputInfoEventsLocked() {
        Iterator<Message> it = this.mPendingTvinputInfoEvents.iterator();
        while (it.hasNext()) {
            Message next = it.next();
            if (this.mHardwareInputIdMap.get(next.arg1) != null) {
                next.sendToTarget();
                it.remove();
            }
        }
    }

    public final void updateVolume() {
        this.mCurrentMaxIndex = this.mAudioManager.getStreamMaxVolume(3);
        this.mCurrentIndex = this.mAudioManager.getStreamVolume(3);
    }

    public final void handleVolumeChange(Context context, Intent intent) {
        int intExtra;
        String action = intent.getAction();
        action.hashCode();
        if (action.equals("android.media.VOLUME_CHANGED_ACTION")) {
            if (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) != 3 || (intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0)) == this.mCurrentIndex) {
                return;
            }
            this.mCurrentIndex = intExtra;
        } else if (action.equals("android.media.STREAM_MUTE_CHANGED_ACTION")) {
            if (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) != 3) {
                return;
            }
        } else {
            String str = TAG;
            Slog.w(str, "Unrecognized intent: " + intent);
            return;
        }
        synchronized (this.mLock) {
            for (int i = 0; i < this.mConnections.size(); i++) {
                TvInputHardwareImpl hardwareImplLocked = this.mConnections.valueAt(i).getHardwareImplLocked();
                if (hardwareImplLocked != null) {
                    hardwareImplLocked.onMediaStreamVolumeChanged();
                }
            }
        }
    }

    public final float getMediaStreamVolume() {
        return this.mCurrentIndex / this.mCurrentMaxIndex;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, indentingPrintWriter)) {
            synchronized (this.mLock) {
                indentingPrintWriter.println("TvInputHardwareManager Info:");
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("mConnections: deviceId -> Connection");
                indentingPrintWriter.increaseIndent();
                for (int i = 0; i < this.mConnections.size(); i++) {
                    indentingPrintWriter.println(this.mConnections.keyAt(i) + ": " + this.mConnections.valueAt(i));
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("mHardwareList:");
                indentingPrintWriter.increaseIndent();
                for (TvInputHardwareInfo tvInputHardwareInfo : this.mHardwareList) {
                    indentingPrintWriter.println(tvInputHardwareInfo);
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("mHdmiDeviceList:");
                indentingPrintWriter.increaseIndent();
                for (HdmiDeviceInfo hdmiDeviceInfo : this.mHdmiDeviceList) {
                    indentingPrintWriter.println(hdmiDeviceInfo);
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("mHardwareInputIdMap: deviceId -> inputId");
                indentingPrintWriter.increaseIndent();
                for (int i2 = 0; i2 < this.mHardwareInputIdMap.size(); i2++) {
                    indentingPrintWriter.println(this.mHardwareInputIdMap.keyAt(i2) + ": " + this.mHardwareInputIdMap.valueAt(i2));
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("mHdmiInputIdMap: id -> inputId");
                indentingPrintWriter.increaseIndent();
                for (int i3 = 0; i3 < this.mHdmiInputIdMap.size(); i3++) {
                    indentingPrintWriter.println(this.mHdmiInputIdMap.keyAt(i3) + ": " + this.mHdmiInputIdMap.valueAt(i3));
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("mInputMap: inputId -> inputInfo");
                indentingPrintWriter.increaseIndent();
                for (Map.Entry<String, TvInputInfo> entry : this.mInputMap.entrySet()) {
                    indentingPrintWriter.println(entry.getKey() + ": " + entry.getValue());
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.decreaseIndent();
            }
        }
    }

    /* renamed from: com.android.server.tv.TvInputHardwareManager$Connection */
    /* loaded from: classes2.dex */
    public class Connection implements IBinder.DeathRecipient {
        public ITvInputHardwareCallback mCallback;
        public TvInputHardwareInfo mHardwareInfo;
        public TvInputInfo mInfo;
        public Runnable mOnFirstFrameCaptured;
        public TvInputHardwareImpl mHardware = null;
        public TvStreamConfig[] mConfigs = null;
        public Integer mCallingUid = null;
        public Integer mResolvedUserId = null;
        public ResourceClientProfile mResourceClientProfile = null;
        public boolean mIsCableConnectionStatusUpdated = false;

        public Connection(TvInputHardwareInfo tvInputHardwareInfo) {
            this.mHardwareInfo = tvInputHardwareInfo;
        }

        public void resetLocked(TvInputHardwareImpl tvInputHardwareImpl, ITvInputHardwareCallback iTvInputHardwareCallback, TvInputInfo tvInputInfo, Integer num, Integer num2, ResourceClientProfile resourceClientProfile) {
            if (this.mHardware != null) {
                try {
                    this.mCallback.onReleased();
                } catch (RemoteException e) {
                    Slog.e(TvInputHardwareManager.TAG, "error in Connection::resetLocked", e);
                }
                this.mHardware.release();
            }
            this.mHardware = tvInputHardwareImpl;
            this.mCallback = iTvInputHardwareCallback;
            this.mInfo = tvInputInfo;
            this.mCallingUid = num;
            this.mResolvedUserId = num2;
            this.mOnFirstFrameCaptured = null;
            this.mResourceClientProfile = resourceClientProfile;
            if (tvInputHardwareImpl == null || iTvInputHardwareCallback == null) {
                return;
            }
            try {
                iTvInputHardwareCallback.onStreamConfigChanged(getConfigsLocked());
            } catch (RemoteException e2) {
                Slog.e(TvInputHardwareManager.TAG, "error in Connection::resetLocked", e2);
            }
        }

        public void updateConfigsLocked(TvStreamConfig[] tvStreamConfigArr) {
            this.mConfigs = tvStreamConfigArr;
        }

        public TvInputHardwareInfo getHardwareInfoLocked() {
            return this.mHardwareInfo;
        }

        public TvInputInfo getInfoLocked() {
            return this.mInfo;
        }

        public ITvInputHardware getHardwareLocked() {
            return this.mHardware;
        }

        public TvInputHardwareImpl getHardwareImplLocked() {
            return this.mHardware;
        }

        public ITvInputHardwareCallback getCallbackLocked() {
            return this.mCallback;
        }

        public TvStreamConfig[] getConfigsLocked() {
            return this.mConfigs;
        }

        public Integer getCallingUidLocked() {
            return this.mCallingUid;
        }

        public Integer getResolvedUserIdLocked() {
            return this.mResolvedUserId;
        }

        public void setOnFirstFrameCapturedLocked(Runnable runnable) {
            this.mOnFirstFrameCaptured = runnable;
        }

        public Runnable getOnFirstFrameCapturedLocked() {
            return this.mOnFirstFrameCaptured;
        }

        public ResourceClientProfile getResourceClientProfileLocked() {
            return this.mResourceClientProfile;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (TvInputHardwareManager.this.mLock) {
                resetLocked(null, null, null, null, null, null);
            }
        }

        public String toString() {
            return "Connection{ mHardwareInfo: " + this.mHardwareInfo + ", mInfo: " + this.mInfo + ", mCallback: " + this.mCallback + ", mConfigs: " + Arrays.toString(this.mConfigs) + ", mCallingUid: " + this.mCallingUid + ", mResolvedUserId: " + this.mResolvedUserId + ", mResourceClientProfile: " + this.mResourceClientProfile + " }";
        }

        public boolean updateCableConnectionStatusLocked(int i) {
            if (i != 0 || this.mIsCableConnectionStatusUpdated) {
                this.mIsCableConnectionStatusUpdated = true;
                this.mHardwareInfo = this.mHardwareInfo.toBuilder().cableConnectionStatus(i).build();
            }
            return this.mIsCableConnectionStatusUpdated;
        }

        public final int getConfigsLengthLocked() {
            TvStreamConfig[] tvStreamConfigArr = this.mConfigs;
            if (tvStreamConfigArr == null) {
                return 0;
            }
            return tvStreamConfigArr.length;
        }

        public final int getInputStateLocked() {
            int i = 0;
            if (getConfigsLengthLocked() <= 0 || this.mIsCableConnectionStatusUpdated) {
                int cableConnectionStatus = this.mHardwareInfo.getCableConnectionStatus();
                if (cableConnectionStatus != 1) {
                    i = 2;
                    if (cableConnectionStatus != 2) {
                        return 1;
                    }
                }
                return i;
            }
            return 0;
        }
    }

    /* renamed from: com.android.server.tv.TvInputHardwareManager$TvInputHardwareImpl */
    /* loaded from: classes2.dex */
    public class TvInputHardwareImpl extends ITvInputHardware.Stub {
        public TvStreamConfig mActiveConfig;
        public final AudioManager.OnAudioPortUpdateListener mAudioListener;
        public AudioPatch mAudioPatch;
        public List<AudioDevicePort> mAudioSink;
        public AudioDevicePort mAudioSource;
        public float mCommittedVolume;
        public int mDesiredChannelMask;
        public int mDesiredFormat;
        public int mDesiredSamplingRate;
        public final TvInputHardwareInfo mInfo;
        public String mOverrideAudioAddress;
        public int mOverrideAudioType;
        public float mSourceVolume;
        public boolean mReleased = false;
        public final Object mImplLock = new Object();

        public TvInputHardwareImpl(TvInputHardwareInfo tvInputHardwareInfo) {
            AudioManager.OnAudioPortUpdateListener onAudioPortUpdateListener = new AudioManager.OnAudioPortUpdateListener() { // from class: com.android.server.tv.TvInputHardwareManager.TvInputHardwareImpl.1
                public void onAudioPatchListUpdate(AudioPatch[] audioPatchArr) {
                }

                public void onAudioPortListUpdate(AudioPort[] audioPortArr) {
                    synchronized (TvInputHardwareImpl.this.mImplLock) {
                        TvInputHardwareImpl.this.updateAudioConfigLocked();
                    }
                }

                public void onServiceDied() {
                    synchronized (TvInputHardwareImpl.this.mImplLock) {
                        TvInputHardwareImpl.this.mAudioSource = null;
                        TvInputHardwareImpl.this.mAudioSink.clear();
                        if (TvInputHardwareImpl.this.mAudioPatch != null) {
                            AudioManager unused = TvInputHardwareManager.this.mAudioManager;
                            AudioManager.releaseAudioPatch(TvInputHardwareImpl.this.mAudioPatch);
                            TvInputHardwareImpl.this.mAudioPatch = null;
                        }
                    }
                }
            };
            this.mAudioListener = onAudioPortUpdateListener;
            this.mOverrideAudioType = 0;
            this.mOverrideAudioAddress = "";
            this.mAudioSink = new ArrayList();
            this.mAudioPatch = null;
            this.mCommittedVolume = -1.0f;
            this.mSourceVolume = 0.0f;
            this.mActiveConfig = null;
            this.mDesiredSamplingRate = 0;
            this.mDesiredChannelMask = 1;
            this.mDesiredFormat = 1;
            this.mInfo = tvInputHardwareInfo;
            TvInputHardwareManager.this.mAudioManager.registerAudioPortUpdateListener(onAudioPortUpdateListener);
            if (tvInputHardwareInfo.getAudioType() != 0) {
                this.mAudioSource = findAudioDevicePort(tvInputHardwareInfo.getAudioType(), tvInputHardwareInfo.getAudioAddress());
                findAudioSinkFromAudioPolicy(this.mAudioSink);
            }
        }

        public final void findAudioSinkFromAudioPolicy(List<AudioDevicePort> list) {
            list.clear();
            ArrayList arrayList = new ArrayList();
            AudioManager unused = TvInputHardwareManager.this.mAudioManager;
            if (AudioManager.listAudioDevicePorts(arrayList) != 0) {
                return;
            }
            int devicesForStream = TvInputHardwareManager.this.mAudioManager.getDevicesForStream(3);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                AudioDevicePort audioDevicePort = (AudioDevicePort) it.next();
                if ((audioDevicePort.type() & devicesForStream) != 0 && (audioDevicePort.type() & Integer.MIN_VALUE) == 0) {
                    list.add(audioDevicePort);
                }
            }
        }

        public final AudioDevicePort findAudioDevicePort(int i, String str) {
            if (i == 0) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            AudioManager unused = TvInputHardwareManager.this.mAudioManager;
            if (AudioManager.listAudioDevicePorts(arrayList) != 0) {
                return null;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                AudioDevicePort audioDevicePort = (AudioDevicePort) it.next();
                if (audioDevicePort.type() == i && audioDevicePort.address().equals(str)) {
                    return audioDevicePort;
                }
            }
            return null;
        }

        public void release() {
            synchronized (this.mImplLock) {
                TvInputHardwareManager.this.mAudioManager.unregisterAudioPortUpdateListener(this.mAudioListener);
                if (this.mAudioPatch != null) {
                    AudioManager unused = TvInputHardwareManager.this.mAudioManager;
                    AudioManager.releaseAudioPatch(this.mAudioPatch);
                    this.mAudioPatch = null;
                }
                this.mReleased = true;
            }
        }

        public boolean setSurface(Surface surface, TvStreamConfig tvStreamConfig) throws RemoteException {
            int i;
            int i2;
            synchronized (this.mImplLock) {
                if (this.mReleased) {
                    throw new IllegalStateException("Device already released.");
                }
                boolean z = true;
                if (surface == null) {
                    if (this.mActiveConfig == null) {
                        return true;
                    }
                    i2 = TvInputHardwareManager.this.mHal.removeStream(this.mInfo.getDeviceId(), this.mActiveConfig);
                    this.mActiveConfig = null;
                } else if (tvStreamConfig == null) {
                    return false;
                } else {
                    TvStreamConfig tvStreamConfig2 = this.mActiveConfig;
                    if (tvStreamConfig2 == null || tvStreamConfig.equals(tvStreamConfig2)) {
                        i = 0;
                    } else {
                        i = TvInputHardwareManager.this.mHal.removeStream(this.mInfo.getDeviceId(), this.mActiveConfig);
                        if (i != 0) {
                            this.mActiveConfig = null;
                        }
                    }
                    if (i == 0) {
                        i2 = TvInputHardwareManager.this.mHal.addOrUpdateStream(this.mInfo.getDeviceId(), surface, tvStreamConfig);
                        if (i2 == 0) {
                            this.mActiveConfig = tvStreamConfig;
                        }
                    } else {
                        i2 = i;
                    }
                }
                updateAudioConfigLocked();
                if (i2 != 0) {
                    z = false;
                }
                return z;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:100:0x01df  */
        /* JADX WARN: Removed duplicated region for block: B:111:0x0185 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:113:? A[RETURN, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:38:0x00bd  */
        /* JADX WARN: Removed duplicated region for block: B:73:0x014d  */
        /* JADX WARN: Removed duplicated region for block: B:74:0x0153  */
        /* JADX WARN: Removed duplicated region for block: B:80:0x0171  */
        /* JADX WARN: Removed duplicated region for block: B:87:0x0196  */
        /* JADX WARN: Removed duplicated region for block: B:88:0x019b  */
        /* JADX WARN: Removed duplicated region for block: B:91:0x01a5  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final void updateAudioConfigLocked() {
            AudioGainConfig audioGainConfig;
            AudioPortConfig activeConfig;
            AudioPortConfig audioPortConfig;
            int i;
            int length;
            int i2;
            int i3;
            boolean z;
            char c;
            AudioGain audioGain;
            int maxValue;
            boolean updateAudioSinkLocked = updateAudioSinkLocked();
            boolean updateAudioSourceLocked = updateAudioSourceLocked();
            if (this.mAudioSource == null || this.mAudioSink.isEmpty() || this.mActiveConfig == null) {
                if (this.mAudioPatch != null) {
                    AudioManager unused = TvInputHardwareManager.this.mAudioManager;
                    AudioManager.releaseAudioPatch(this.mAudioPatch);
                    this.mAudioPatch = null;
                    return;
                }
                return;
            }
            TvInputHardwareManager.this.updateVolume();
            float mediaStreamVolume = this.mSourceVolume * TvInputHardwareManager.this.getMediaStreamVolume();
            int i4 = 0;
            int i5 = 1;
            if (this.mAudioSource.gains().length > 0 && mediaStreamVolume != this.mCommittedVolume) {
                AudioGain[] gains = this.mAudioSource.gains();
                int length2 = gains.length;
                int i6 = 0;
                while (true) {
                    if (i6 >= length2) {
                        audioGain = null;
                        break;
                    }
                    audioGain = gains[i6];
                    if ((audioGain.mode() & 1) != 0) {
                        break;
                    }
                    i6++;
                }
                if (audioGain != null) {
                    int maxValue2 = (audioGain.maxValue() - audioGain.minValue()) / audioGain.stepValue();
                    int minValue = audioGain.minValue();
                    if (mediaStreamVolume < 1.0f) {
                        maxValue = minValue + (audioGain.stepValue() * ((int) ((maxValue2 * mediaStreamVolume) + 0.5d)));
                    } else {
                        maxValue = audioGain.maxValue();
                    }
                    audioGainConfig = audioGain.buildConfig(1, audioGain.channelMask(), new int[]{maxValue}, 0);
                    activeConfig = this.mAudioSource.activeConfig();
                    ArrayList arrayList = new ArrayList();
                    AudioPatch audioPatch = this.mAudioPatch;
                    AudioPatch[] audioPatchArr = {audioPatch};
                    boolean z2 = !updateAudioSourceLocked || updateAudioSinkLocked || audioPatch == null;
                    for (AudioDevicePort audioDevicePort : this.mAudioSink) {
                        AudioDevicePortConfig activeConfig2 = audioDevicePort.activeConfig();
                        int i7 = this.mDesiredSamplingRate;
                        int i8 = this.mDesiredChannelMask;
                        int i9 = this.mDesiredFormat;
                        if (activeConfig2 != null) {
                            if (i7 == 0) {
                                i7 = activeConfig2.samplingRate();
                            }
                            if (i8 == i5) {
                                i8 = activeConfig2.channelMask();
                            }
                            if (i9 == i5) {
                                i9 = activeConfig2.format();
                            }
                        }
                        if (activeConfig2 == null || activeConfig2.samplingRate() != i7 || activeConfig2.channelMask() != i8 || activeConfig2.format() != i9) {
                            if (!TvInputHardwareManager.intArrayContains(audioDevicePort.samplingRates(), i7) && audioDevicePort.samplingRates().length > 0) {
                                i7 = audioDevicePort.samplingRates()[0];
                            }
                            if (!TvInputHardwareManager.intArrayContains(audioDevicePort.channelMasks(), i8)) {
                                i8 = 1;
                            }
                            if (!TvInputHardwareManager.intArrayContains(audioDevicePort.formats(), i9)) {
                                i9 = 1;
                            }
                            activeConfig2 = audioDevicePort.buildConfig(i7, i8, i9, (AudioGainConfig) null);
                            z2 = true;
                        }
                        arrayList.add(activeConfig2);
                        i4 = 0;
                        i5 = 1;
                    }
                    audioPortConfig = (AudioPortConfig) arrayList.get(i4);
                    if (activeConfig == null && audioGainConfig == null) {
                        z = z2;
                    } else {
                        if (!TvInputHardwareManager.intArrayContains(this.mAudioSource.samplingRates(), audioPortConfig.samplingRate())) {
                            i = audioPortConfig.samplingRate();
                        } else {
                            i = this.mAudioSource.samplingRates().length > 0 ? this.mAudioSource.samplingRates()[0] : 0;
                        }
                        int[] channelMasks = this.mAudioSource.channelMasks();
                        length = channelMasks.length;
                        i2 = 0;
                        while (true) {
                            if (i2 < length) {
                                i3 = 1;
                                break;
                            }
                            i3 = channelMasks[i2];
                            if (AudioFormat.channelCountFromOutChannelMask(audioPortConfig.channelMask()) == AudioFormat.channelCountFromInChannelMask(i3)) {
                                break;
                            }
                            i2++;
                        }
                        activeConfig = this.mAudioSource.buildConfig(i, i3, !TvInputHardwareManager.intArrayContains(this.mAudioSource.formats(), audioPortConfig.format()) ? audioPortConfig.format() : 1, audioGainConfig);
                        z = true;
                    }
                    if (z) {
                        this.mCommittedVolume = mediaStreamVolume;
                        AudioPatch audioPatch2 = this.mAudioPatch;
                        if (audioPatch2 == null || updateAudioSinkLocked || updateAudioSourceLocked) {
                            if (audioPatch2 != null) {
                                AudioManager unused2 = TvInputHardwareManager.this.mAudioManager;
                                AudioManager.releaseAudioPatch(this.mAudioPatch);
                                c = 0;
                                audioPatchArr[0] = null;
                            } else {
                                c = 0;
                            }
                            AudioManager unused3 = TvInputHardwareManager.this.mAudioManager;
                            AudioManager.createAudioPatch(audioPatchArr, new AudioPortConfig[]{activeConfig}, (AudioPortConfig[]) arrayList.toArray(new AudioPortConfig[arrayList.size()]));
                            this.mAudioPatch = audioPatchArr[c];
                        }
                    }
                    if (audioGainConfig == null) {
                        AudioManager unused4 = TvInputHardwareManager.this.mAudioManager;
                        AudioManager.setAudioPortGain(this.mAudioSource, audioGainConfig);
                        return;
                    }
                    return;
                }
                Slog.w(TvInputHardwareManager.TAG, "No audio source gain with MODE_JOINT support exists.");
            }
            audioGainConfig = null;
            activeConfig = this.mAudioSource.activeConfig();
            ArrayList arrayList2 = new ArrayList();
            AudioPatch audioPatch3 = this.mAudioPatch;
            AudioPatch[] audioPatchArr2 = {audioPatch3};
            if (updateAudioSourceLocked) {
            }
            while (r12.hasNext()) {
            }
            audioPortConfig = (AudioPortConfig) arrayList2.get(i4);
            if (activeConfig == null) {
            }
            if (!TvInputHardwareManager.intArrayContains(this.mAudioSource.samplingRates(), audioPortConfig.samplingRate())) {
            }
            int[] channelMasks2 = this.mAudioSource.channelMasks();
            length = channelMasks2.length;
            i2 = 0;
            while (true) {
                if (i2 < length) {
                }
                i2++;
            }
            activeConfig = this.mAudioSource.buildConfig(i, i3, !TvInputHardwareManager.intArrayContains(this.mAudioSource.formats(), audioPortConfig.format()) ? audioPortConfig.format() : 1, audioGainConfig);
            z = true;
            if (z) {
            }
            if (audioGainConfig == null) {
            }
        }

        public void setStreamVolume(float f) throws RemoteException {
            synchronized (this.mImplLock) {
                if (this.mReleased) {
                    throw new IllegalStateException("Device already released.");
                }
                this.mSourceVolume = f;
                updateAudioConfigLocked();
            }
        }

        public final boolean startCapture(Surface surface, TvStreamConfig tvStreamConfig) {
            synchronized (this.mImplLock) {
                if (this.mReleased) {
                    return false;
                }
                if (surface != null && tvStreamConfig != null) {
                    if (tvStreamConfig.getType() != 2) {
                        return false;
                    }
                    return TvInputHardwareManager.this.mHal.addOrUpdateStream(this.mInfo.getDeviceId(), surface, tvStreamConfig) == 0;
                }
                return false;
            }
        }

        public final boolean stopCapture(TvStreamConfig tvStreamConfig) {
            synchronized (this.mImplLock) {
                if (this.mReleased) {
                    return false;
                }
                if (tvStreamConfig == null) {
                    return false;
                }
                return TvInputHardwareManager.this.mHal.removeStream(this.mInfo.getDeviceId(), tvStreamConfig) == 0;
            }
        }

        public final boolean updateAudioSourceLocked() {
            if (this.mInfo.getAudioType() == 0) {
                return false;
            }
            AudioDevicePort audioDevicePort = this.mAudioSource;
            AudioDevicePort findAudioDevicePort = findAudioDevicePort(this.mInfo.getAudioType(), this.mInfo.getAudioAddress());
            this.mAudioSource = findAudioDevicePort;
            if (findAudioDevicePort == null) {
                if (audioDevicePort == null) {
                    return false;
                }
            } else if (findAudioDevicePort.equals(audioDevicePort)) {
                return false;
            }
            return true;
        }

        public final boolean updateAudioSinkLocked() {
            if (this.mInfo.getAudioType() == 0) {
                return false;
            }
            List<AudioDevicePort> list = this.mAudioSink;
            ArrayList arrayList = new ArrayList();
            this.mAudioSink = arrayList;
            int i = this.mOverrideAudioType;
            if (i == 0) {
                findAudioSinkFromAudioPolicy(arrayList);
            } else {
                AudioDevicePort findAudioDevicePort = findAudioDevicePort(i, this.mOverrideAudioAddress);
                if (findAudioDevicePort != null) {
                    this.mAudioSink.add(findAudioDevicePort);
                }
            }
            if (this.mAudioSink.size() != list.size()) {
                return true;
            }
            list.removeAll(this.mAudioSink);
            return !list.isEmpty();
        }

        public final void handleAudioSinkUpdated() {
            synchronized (this.mImplLock) {
                updateAudioConfigLocked();
            }
        }

        public void overrideAudioSink(int i, String str, int i2, int i3, int i4) {
            synchronized (this.mImplLock) {
                this.mOverrideAudioType = i;
                this.mOverrideAudioAddress = str;
                this.mDesiredSamplingRate = i2;
                this.mDesiredChannelMask = i3;
                this.mDesiredFormat = i4;
                updateAudioConfigLocked();
            }
        }

        public void onMediaStreamVolumeChanged() {
            synchronized (this.mImplLock) {
                updateAudioConfigLocked();
            }
        }
    }

    /* renamed from: com.android.server.tv.TvInputHardwareManager$ListenerHandler */
    /* loaded from: classes2.dex */
    public class ListenerHandler extends Handler {
        public ListenerHandler() {
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            String str;
            switch (message.what) {
                case 1:
                    TvInputHardwareManager.this.mListener.onStateChanged((String) message.obj, message.arg1);
                    return;
                case 2:
                    TvInputHardwareManager.this.mListener.onHardwareDeviceAdded((TvInputHardwareInfo) message.obj);
                    return;
                case 3:
                    TvInputHardwareManager.this.mListener.onHardwareDeviceRemoved((TvInputHardwareInfo) message.obj);
                    return;
                case 4:
                    TvInputHardwareManager.this.mListener.onHdmiDeviceAdded((HdmiDeviceInfo) message.obj);
                    return;
                case 5:
                    TvInputHardwareManager.this.mListener.onHdmiDeviceRemoved((HdmiDeviceInfo) message.obj);
                    return;
                case 6:
                    HdmiDeviceInfo hdmiDeviceInfo = (HdmiDeviceInfo) message.obj;
                    synchronized (TvInputHardwareManager.this.mLock) {
                        str = (String) TvInputHardwareManager.this.mHdmiInputIdMap.get(hdmiDeviceInfo.getId());
                    }
                    if (str != null) {
                        TvInputHardwareManager.this.mListener.onHdmiDeviceUpdated(str, hdmiDeviceInfo);
                        return;
                    } else {
                        Slog.w(TvInputHardwareManager.TAG, "Could not resolve input ID matching the device info; ignoring.");
                        return;
                    }
                case 7:
                    int i = message.arg1;
                    int i2 = message.arg2;
                    Connection connection = (Connection) message.obj;
                    int configsLengthLocked = connection.getConfigsLengthLocked();
                    int inputStateLocked = connection.getInputStateLocked();
                    String str2 = (String) TvInputHardwareManager.this.mHardwareInputIdMap.get(i);
                    if (str2 != null) {
                        if (connection.updateCableConnectionStatusLocked(i2)) {
                            if (inputStateLocked != connection.getInputStateLocked()) {
                                TvInputHardwareManager.this.mHandler.obtainMessage(1, connection.getInputStateLocked(), 0, str2).sendToTarget();
                                return;
                            }
                            return;
                        }
                        if ((configsLengthLocked == 0) != (connection.getConfigsLengthLocked() == 0)) {
                            TvInputHardwareManager.this.mHandler.obtainMessage(1, connection.getInputStateLocked(), 0, str2).sendToTarget();
                            return;
                        }
                        return;
                    }
                    return;
                default:
                    String str3 = TvInputHardwareManager.TAG;
                    Slog.w(str3, "Unhandled message: " + message);
                    return;
            }
        }
    }

    /* renamed from: com.android.server.tv.TvInputHardwareManager$HdmiHotplugEventListener */
    /* loaded from: classes2.dex */
    public final class HdmiHotplugEventListener extends IHdmiHotplugEventListener.Stub {
        public HdmiHotplugEventListener() {
        }

        public void onReceived(HdmiHotplugEvent hdmiHotplugEvent) {
            synchronized (TvInputHardwareManager.this.mLock) {
                TvInputHardwareManager.this.mHdmiStateMap.put(hdmiHotplugEvent.getPort(), hdmiHotplugEvent.isConnected());
                TvInputHardwareInfo findHardwareInfoForHdmiPortLocked = TvInputHardwareManager.this.findHardwareInfoForHdmiPortLocked(hdmiHotplugEvent.getPort());
                if (findHardwareInfoForHdmiPortLocked == null) {
                    return;
                }
                String str = (String) TvInputHardwareManager.this.mHardwareInputIdMap.get(findHardwareInfoForHdmiPortLocked.getDeviceId());
                if (str == null) {
                    return;
                }
                TvInputHardwareManager.this.mHandler.obtainMessage(1, hdmiHotplugEvent.isConnected() ? 0 : 1, 0, str).sendToTarget();
            }
        }
    }

    /* renamed from: com.android.server.tv.TvInputHardwareManager$HdmiDeviceEventListener */
    /* loaded from: classes2.dex */
    public final class HdmiDeviceEventListener extends IHdmiDeviceEventListener.Stub {
        public HdmiDeviceEventListener() {
        }

        /* JADX WARN: Removed duplicated region for block: B:32:0x00bc A[Catch: all -> 0x00cb, TryCatch #0 {all -> 0x00cb, blocks: (B:30:0x00a6, B:32:0x00bc, B:34:0x00c9, B:33:0x00c0, B:14:0x001d, B:16:0x0031, B:17:0x004e, B:19:0x0050, B:20:0x005b, B:22:0x006f, B:23:0x008c, B:27:0x0091, B:29:0x009b, B:38:0x00cd, B:39:0x00ea), top: B:43:0x0010 }] */
        /* JADX WARN: Removed duplicated region for block: B:33:0x00c0 A[Catch: all -> 0x00cb, TryCatch #0 {all -> 0x00cb, blocks: (B:30:0x00a6, B:32:0x00bc, B:34:0x00c9, B:33:0x00c0, B:14:0x001d, B:16:0x0031, B:17:0x004e, B:19:0x0050, B:20:0x005b, B:22:0x006f, B:23:0x008c, B:27:0x0091, B:29:0x009b, B:38:0x00cd, B:39:0x00ea), top: B:43:0x0010 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onStatusChanged(HdmiDeviceInfo hdmiDeviceInfo, int i) {
            int i2;
            HdmiDeviceInfo hdmiDeviceInfo2;
            if (hdmiDeviceInfo.isSourceType()) {
                synchronized (TvInputHardwareManager.this.mLock) {
                    try {
                        if (i != 1) {
                            if (i == 2) {
                                if (!TvInputHardwareManager.this.mHdmiDeviceList.remove(findHdmiDeviceInfo(hdmiDeviceInfo.getId()))) {
                                    Slog.w(TvInputHardwareManager.TAG, "The list doesn't contain " + hdmiDeviceInfo + "; ignoring.");
                                    return;
                                }
                                i2 = 5;
                            } else if (i == 3) {
                                if (!TvInputHardwareManager.this.mHdmiDeviceList.remove(findHdmiDeviceInfo(hdmiDeviceInfo.getId()))) {
                                    Slog.w(TvInputHardwareManager.TAG, "The list doesn't contain " + hdmiDeviceInfo + "; ignoring.");
                                    return;
                                }
                                TvInputHardwareManager.this.mHdmiDeviceList.add(hdmiDeviceInfo);
                                i2 = 6;
                            } else {
                                hdmiDeviceInfo2 = null;
                                i2 = 0;
                                Message obtainMessage = TvInputHardwareManager.this.mHandler.obtainMessage(i2, 0, 0, hdmiDeviceInfo2);
                                if (TvInputHardwareManager.this.findHardwareInfoForHdmiPortLocked(hdmiDeviceInfo.getPortId()) == null) {
                                    obtainMessage.sendToTarget();
                                } else {
                                    TvInputHardwareManager.this.mPendingHdmiDeviceEvents.add(obtainMessage);
                                }
                            }
                        } else if (findHdmiDeviceInfo(hdmiDeviceInfo.getId()) == null) {
                            TvInputHardwareManager.this.mHdmiDeviceList.add(hdmiDeviceInfo);
                            i2 = 4;
                        } else {
                            Slog.w(TvInputHardwareManager.TAG, "The list already contains " + hdmiDeviceInfo + "; ignoring.");
                            return;
                        }
                        hdmiDeviceInfo2 = hdmiDeviceInfo;
                        Message obtainMessage2 = TvInputHardwareManager.this.mHandler.obtainMessage(i2, 0, 0, hdmiDeviceInfo2);
                        if (TvInputHardwareManager.this.findHardwareInfoForHdmiPortLocked(hdmiDeviceInfo.getPortId()) == null) {
                        }
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
        }

        public final HdmiDeviceInfo findHdmiDeviceInfo(int i) {
            for (HdmiDeviceInfo hdmiDeviceInfo : TvInputHardwareManager.this.mHdmiDeviceList) {
                if (hdmiDeviceInfo.getId() == i) {
                    return hdmiDeviceInfo;
                }
            }
            return null;
        }
    }

    /* renamed from: com.android.server.tv.TvInputHardwareManager$HdmiSystemAudioModeChangeListener */
    /* loaded from: classes2.dex */
    public final class HdmiSystemAudioModeChangeListener extends IHdmiSystemAudioModeChangeListener.Stub {
        public HdmiSystemAudioModeChangeListener() {
        }

        public void onStatusChanged(boolean z) throws RemoteException {
            synchronized (TvInputHardwareManager.this.mLock) {
                for (int i = 0; i < TvInputHardwareManager.this.mConnections.size(); i++) {
                    TvInputHardwareImpl hardwareImplLocked = ((Connection) TvInputHardwareManager.this.mConnections.valueAt(i)).getHardwareImplLocked();
                    if (hardwareImplLocked != null) {
                        hardwareImplLocked.handleAudioSinkUpdated();
                    }
                }
            }
        }
    }
}
