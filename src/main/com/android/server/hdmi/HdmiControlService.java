package com.android.server.hdmi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.DisplayManager;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiHotplugEvent;
import android.hardware.hdmi.HdmiPortInfo;
import android.hardware.hdmi.IHdmiCecSettingChangeListener;
import android.hardware.hdmi.IHdmiCecVolumeControlFeatureListener;
import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.hdmi.IHdmiControlStatusChangeListener;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiInputChangeListener;
import android.hardware.hdmi.IHdmiMhlVendorCommandListener;
import android.hardware.hdmi.IHdmiRecordListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.hardware.hdmi.IHdmiVendorCommandListener;
import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceVolumeManager;
import android.media.AudioManager;
import android.media.VolumeInfo;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.sysprop.HdmiProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import com.android.server.hdmi.HdmiCecConfig;
import com.android.server.hdmi.HdmiCecController;
import com.android.server.hdmi.HdmiCecLocalDevice;
import com.android.server.hdmi.HdmiControlService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import libcore.util.EmptyArray;
/* loaded from: classes.dex */
public class HdmiControlService extends SystemService {
    @VisibleForTesting
    static final AudioDeviceAttributes AUDIO_OUTPUT_DEVICE_HDMI;
    @VisibleForTesting
    static final AudioDeviceAttributes AUDIO_OUTPUT_DEVICE_HDMI_ARC;
    public static final AudioDeviceAttributes AUDIO_OUTPUT_DEVICE_HDMI_EARC;
    public static final List<AudioDeviceAttributes> AVC_AUDIO_OUTPUT_DEVICES;
    @VisibleForTesting
    static final AudioAttributes STREAM_MUSIC_ATTRIBUTES;
    public AbsoluteVolumeChangedListener mAbsoluteVolumeChangedListener;
    public int mActivePortId;
    @GuardedBy({"mLock"})
    public final HdmiCecLocalDevice.ActiveSource mActiveSource;
    public boolean mAddressAllocated;
    public HdmiCecAtomWriter mAtomWriter;
    @GuardedBy({"mLock"})
    public Map<AudioDeviceAttributes, Integer> mAudioDeviceVolumeBehaviors;
    public AudioDeviceVolumeManagerWrapperInterface mAudioDeviceVolumeManager;
    public AudioManager mAudioManager;
    public HdmiCecController mCecController;
    public final List<Integer> mCecLocalDevices;
    public CecMessageBuffer mCecMessageBuffer;
    public int mCecVersion;
    public DeviceConfigWrapper mDeviceConfig;
    @GuardedBy({"mLock"})
    public final ArrayList<DeviceEventListenerRecord> mDeviceEventListenerRecords;
    public DisplayManager mDisplayManager;
    public IHdmiControlCallback mDisplayStatusCallback;
    public HdmiEarcController mEarcController;
    @GuardedBy({"mLock"})
    public boolean mEarcEnabled;
    public HdmiEarcLocalDevice mEarcLocalDevice;
    public int mEarcPortId;
    @GuardedBy({"mLock"})
    @VisibleForTesting
    private boolean mEarcSupported;
    public boolean mEarcTxFeatureFlagEnabled;
    public final Handler mHandler;
    public HdmiCecConfig mHdmiCecConfig;
    public HdmiCecNetwork mHdmiCecNetwork;
    @GuardedBy({"mLock"})
    public final ArrayMap<String, RemoteCallbackList<IHdmiCecSettingChangeListener>> mHdmiCecSettingChangeListenerRecords;
    @GuardedBy({"mLock"})
    public int mHdmiCecVolumeControl;
    @GuardedBy({"mLock"})
    public final RemoteCallbackList<IHdmiCecVolumeControlFeatureListener> mHdmiCecVolumeControlFeatureListenerRecords;
    public final HdmiControlBroadcastReceiver mHdmiControlBroadcastReceiver;
    @GuardedBy({"mLock"})
    public int mHdmiControlEnabled;
    @GuardedBy({"mLock"})
    public final ArrayList<HdmiControlStatusChangeListenerRecord> mHdmiControlStatusChangeListenerRecords;
    @GuardedBy({"mLock"})
    public final ArrayList<HotplugEventListenerRecord> mHotplugEventListenerRecords;
    @GuardedBy({"mLock"})
    public InputChangeListenerRecord mInputChangeListenerRecord;
    public Looper mIoLooper;
    public final HandlerThread mIoThread;
    public boolean mIsCecAvailable;
    public int mLastInputMhl;
    public final Object mLock;
    public String mMenuLanguage;
    public HdmiMhlControllerStub mMhlController;
    @GuardedBy({"mLock"})
    public List<HdmiDeviceInfo> mMhlDevices;
    @GuardedBy({"mLock"})
    public boolean mMhlInputChangeEnabled;
    @GuardedBy({"mLock"})
    public final ArrayList<HdmiMhlVendorCommandListenerRecord> mMhlVendorCommandListenerRecords;
    public IHdmiControlCallback mOtpCallbackPendingAddressAllocation;
    public PowerManagerWrapper mPowerManager;
    public PowerManagerInternalWrapper mPowerManagerInternal;
    public HdmiCecPowerStatusController mPowerStatusController;
    @GuardedBy({"mLock"})
    public boolean mProhibitMode;
    @GuardedBy({"mLock"})
    public HdmiRecordListenerRecord mRecordListenerRecord;
    public final SelectRequestBuffer mSelectRequestBuffer;
    public final Executor mServiceThreadExecutor;
    public HdmiCecConfig.SettingChangeListener mSettingChangeListener;
    public final SettingsObserver mSettingsObserver;
    public boolean mSoundbarModeFeatureFlagEnabled;
    public boolean mStandbyMessageReceived;
    public int mStreamMusicMaxVolume;
    @GuardedBy({"mLock"})
    public boolean mSystemAudioActivated;
    public final ArrayList<SystemAudioModeChangeListenerRecord> mSystemAudioModeChangeListenerRecords;
    public boolean mTransitionFromArcToEarcTxEnabled;
    public TvInputManager mTvInputManager;
    @GuardedBy({"mLock"})
    public final ArrayList<VendorCommandListenerRecord> mVendorCommandListenerRecords;
    public boolean mWakeUpMessageReceived;
    public static final Locale HONG_KONG = new Locale("zh", "HK");
    public static final Locale MACAU = new Locale("zh", "MO");
    public static final Map<String, String> sTerminologyToBibliographicMap = createsTerminologyToBibliographicMap();

    /* loaded from: classes.dex */
    public interface DevicePollingCallback {
        void onPollingFinished(List<Integer> list);
    }

    /* loaded from: classes.dex */
    public interface SendMessageCallback {
        void onSendCompleted(int i);
    }

    public static int toInt(boolean z) {
        return z ? 1 : 0;
    }

    @VisibleForTesting
    public int getInitialPowerStatus() {
        return 3;
    }

    static {
        AudioDeviceAttributes audioDeviceAttributes = new AudioDeviceAttributes(2, 9, "");
        AUDIO_OUTPUT_DEVICE_HDMI = audioDeviceAttributes;
        AudioDeviceAttributes audioDeviceAttributes2 = new AudioDeviceAttributes(2, 10, "");
        AUDIO_OUTPUT_DEVICE_HDMI_ARC = audioDeviceAttributes2;
        AudioDeviceAttributes audioDeviceAttributes3 = new AudioDeviceAttributes(2, 29, "");
        AUDIO_OUTPUT_DEVICE_HDMI_EARC = audioDeviceAttributes3;
        AVC_AUDIO_OUTPUT_DEVICES = Collections.unmodifiableList(Arrays.asList(audioDeviceAttributes, audioDeviceAttributes2, audioDeviceAttributes3));
        STREAM_MUSIC_ATTRIBUTES = new AudioAttributes.Builder().setLegacyStreamType(3).build();
    }

    public static Map<String, String> createsTerminologyToBibliographicMap() {
        HashMap hashMap = new HashMap();
        hashMap.put("sqi", "alb");
        hashMap.put("hye", "arm");
        hashMap.put("eus", "baq");
        hashMap.put("mya", "bur");
        hashMap.put("ces", "cze");
        hashMap.put("nld", "dut");
        hashMap.put("kat", "geo");
        hashMap.put("deu", "ger");
        hashMap.put("ell", "gre");
        hashMap.put("fra", "fre");
        hashMap.put("isl", "ice");
        hashMap.put("mkd", "mac");
        hashMap.put("mri", "mao");
        hashMap.put("msa", "may");
        hashMap.put("fas", "per");
        hashMap.put("ron", "rum");
        hashMap.put("slk", "slo");
        hashMap.put("bod", "tib");
        hashMap.put("cym", "wel");
        return Collections.unmodifiableMap(hashMap);
    }

    @VisibleForTesting
    public static String localeToMenuLanguage(Locale locale) {
        if (locale.equals(Locale.TAIWAN) || locale.equals(HONG_KONG) || locale.equals(MACAU)) {
            return "chi";
        }
        String iSO3Language = locale.getISO3Language();
        Map<String, String> map = sTerminologyToBibliographicMap;
        return map.containsKey(iSO3Language) ? map.get(iSO3Language) : iSO3Language;
    }

    /* loaded from: classes.dex */
    public class HdmiControlBroadcastReceiver extends BroadcastReceiver {
        public HdmiControlBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            HdmiControlService.this.assertRunOnServiceThread();
            boolean contains = SystemProperties.get("sys.shutdown.requested").contains("1");
            String action = intent.getAction();
            action.hashCode();
            char c = 65535;
            switch (action.hashCode()) {
                case -2128145023:
                    if (action.equals("android.intent.action.SCREEN_OFF")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1454123155:
                    if (action.equals("android.intent.action.SCREEN_ON")) {
                        c = 1;
                        break;
                    }
                    break;
                case 158859398:
                    if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1947666138:
                    if (action.equals("android.intent.action.ACTION_SHUTDOWN")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    if (!HdmiControlService.this.isPowerOnOrTransient() || contains) {
                        return;
                    }
                    HdmiControlService.this.onStandby(0);
                    return;
                case 1:
                    if (HdmiControlService.this.isPowerStandbyOrTransient()) {
                        HdmiControlService.this.onWakeUp(0);
                        return;
                    }
                    return;
                case 2:
                    String localeToMenuLanguage = HdmiControlService.localeToMenuLanguage(Locale.getDefault());
                    if (HdmiControlService.this.mMenuLanguage.equals(localeToMenuLanguage)) {
                        return;
                    }
                    HdmiControlService.this.onLanguageChanged(localeToMenuLanguage);
                    return;
                case 3:
                    if (!HdmiControlService.this.isPowerOnOrTransient() || contains) {
                        return;
                    }
                    HdmiControlService.this.onStandby(1);
                    return;
                default:
                    return;
            }
        }
    }

    @VisibleForTesting
    public HdmiControlService(Context context, List<Integer> list, AudioDeviceVolumeManagerWrapperInterface audioDeviceVolumeManagerWrapperInterface) {
        super(context);
        this.mServiceThreadExecutor = new Executor() { // from class: com.android.server.hdmi.HdmiControlService.1
            @Override // java.util.concurrent.Executor
            public void execute(Runnable runnable) {
                HdmiControlService.this.runOnServiceThread(runnable);
            }
        };
        this.mActiveSource = new HdmiCecLocalDevice.ActiveSource();
        this.mSystemAudioActivated = false;
        this.mAudioDeviceVolumeBehaviors = new HashMap();
        this.mIoThread = new HandlerThread("Hdmi Control Io Thread");
        this.mLock = new Object();
        this.mHdmiControlStatusChangeListenerRecords = new ArrayList<>();
        this.mHdmiCecVolumeControlFeatureListenerRecords = new RemoteCallbackList<>();
        this.mHotplugEventListenerRecords = new ArrayList<>();
        this.mDeviceEventListenerRecords = new ArrayList<>();
        this.mVendorCommandListenerRecords = new ArrayList<>();
        this.mHdmiCecSettingChangeListenerRecords = new ArrayMap<>();
        this.mEarcPortId = -1;
        this.mSystemAudioModeChangeListenerRecords = new ArrayList<>();
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mHdmiControlBroadcastReceiver = new HdmiControlBroadcastReceiver();
        this.mDisplayStatusCallback = null;
        this.mOtpCallbackPendingAddressAllocation = null;
        this.mMenuLanguage = localeToMenuLanguage(Locale.getDefault());
        this.mStandbyMessageReceived = false;
        this.mWakeUpMessageReceived = false;
        this.mSoundbarModeFeatureFlagEnabled = false;
        this.mEarcTxFeatureFlagEnabled = false;
        this.mTransitionFromArcToEarcTxEnabled = false;
        this.mActivePortId = -1;
        this.mMhlVendorCommandListenerRecords = new ArrayList<>();
        this.mLastInputMhl = -1;
        this.mAddressAllocated = false;
        this.mIsCecAvailable = false;
        this.mAtomWriter = new HdmiCecAtomWriter();
        this.mSelectRequestBuffer = new SelectRequestBuffer();
        this.mSettingChangeListener = new C092924();
        this.mCecLocalDevices = list;
        this.mSettingsObserver = new SettingsObserver(handler);
        this.mHdmiCecConfig = new HdmiCecConfig(context);
        this.mDeviceConfig = new DeviceConfigWrapper();
        this.mAudioDeviceVolumeManager = audioDeviceVolumeManagerWrapperInterface;
    }

    public HdmiControlService(Context context) {
        super(context);
        this.mServiceThreadExecutor = new Executor() { // from class: com.android.server.hdmi.HdmiControlService.1
            @Override // java.util.concurrent.Executor
            public void execute(Runnable runnable) {
                HdmiControlService.this.runOnServiceThread(runnable);
            }
        };
        this.mActiveSource = new HdmiCecLocalDevice.ActiveSource();
        this.mSystemAudioActivated = false;
        this.mAudioDeviceVolumeBehaviors = new HashMap();
        this.mIoThread = new HandlerThread("Hdmi Control Io Thread");
        this.mLock = new Object();
        this.mHdmiControlStatusChangeListenerRecords = new ArrayList<>();
        this.mHdmiCecVolumeControlFeatureListenerRecords = new RemoteCallbackList<>();
        this.mHotplugEventListenerRecords = new ArrayList<>();
        this.mDeviceEventListenerRecords = new ArrayList<>();
        this.mVendorCommandListenerRecords = new ArrayList<>();
        this.mHdmiCecSettingChangeListenerRecords = new ArrayMap<>();
        this.mEarcPortId = -1;
        this.mSystemAudioModeChangeListenerRecords = new ArrayList<>();
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mHdmiControlBroadcastReceiver = new HdmiControlBroadcastReceiver();
        this.mDisplayStatusCallback = null;
        this.mOtpCallbackPendingAddressAllocation = null;
        this.mMenuLanguage = localeToMenuLanguage(Locale.getDefault());
        this.mStandbyMessageReceived = false;
        this.mWakeUpMessageReceived = false;
        this.mSoundbarModeFeatureFlagEnabled = false;
        this.mEarcTxFeatureFlagEnabled = false;
        this.mTransitionFromArcToEarcTxEnabled = false;
        this.mActivePortId = -1;
        this.mMhlVendorCommandListenerRecords = new ArrayList<>();
        this.mLastInputMhl = -1;
        this.mAddressAllocated = false;
        this.mIsCecAvailable = false;
        this.mAtomWriter = new HdmiCecAtomWriter();
        this.mSelectRequestBuffer = new SelectRequestBuffer();
        this.mSettingChangeListener = new C092924();
        this.mCecLocalDevices = readDeviceTypes();
        this.mSettingsObserver = new SettingsObserver(handler);
        this.mHdmiCecConfig = new HdmiCecConfig(context);
        this.mDeviceConfig = new DeviceConfigWrapper();
    }

    @VisibleForTesting
    public List<HdmiProperties.cec_device_types_values> getCecDeviceTypes() {
        return HdmiProperties.cec_device_types();
    }

    @VisibleForTesting
    public List<Integer> getDeviceTypes() {
        return HdmiProperties.device_type();
    }

    @VisibleForTesting
    public List<Integer> readDeviceTypes() {
        List<HdmiProperties.cec_device_types_values> cecDeviceTypes = getCecDeviceTypes();
        if (!cecDeviceTypes.isEmpty()) {
            if (cecDeviceTypes.contains(null)) {
                Slog.w("HdmiControlService", "Error parsing ro.hdmi.cec_device_types: " + SystemProperties.get("ro.hdmi.cec_device_types"));
            }
            return (List) cecDeviceTypes.stream().map(new Function() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Integer enumToIntDeviceType;
                    enumToIntDeviceType = HdmiControlService.enumToIntDeviceType((HdmiProperties.cec_device_types_values) obj);
                    return enumToIntDeviceType;
                }
            }).filter(new Predicate() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((Integer) obj);
                }
            }).collect(Collectors.toList());
        }
        List<Integer> deviceTypes = getDeviceTypes();
        if (deviceTypes.contains(null)) {
            Slog.w("HdmiControlService", "Error parsing ro.hdmi.device_type: " + SystemProperties.get("ro.hdmi.device_type"));
        }
        return (List) deviceTypes.stream().filter(new Predicate() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((Integer) obj);
            }
        }).collect(Collectors.toList());
    }

    /* renamed from: com.android.server.hdmi.HdmiControlService$27 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C093227 {

        /* renamed from: $SwitchMap$android$sysprop$HdmiProperties$cec_device_types_values */
        public static final /* synthetic */ int[] f1143x776a6083;

        static {
            int[] iArr = new int[HdmiProperties.cec_device_types_values.values().length];
            f1143x776a6083 = iArr;
            try {
                iArr[HdmiProperties.cec_device_types_values.TV.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.RECORDING_DEVICE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.RESERVED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.TUNER.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.PLAYBACK_DEVICE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.AUDIO_SYSTEM.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.PURE_CEC_SWITCH.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                f1143x776a6083[HdmiProperties.cec_device_types_values.VIDEO_PROCESSOR.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    public static Integer enumToIntDeviceType(HdmiProperties.cec_device_types_values cec_device_types_valuesVar) {
        if (cec_device_types_valuesVar == null) {
            return null;
        }
        switch (C093227.f1143x776a6083[cec_device_types_valuesVar.ordinal()]) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            case 8:
                return 7;
            default:
                Slog.w("HdmiControlService", "Unrecognized device type in ro.hdmi.cec_device_types: " + cec_device_types_valuesVar.getPropValue());
                return null;
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        initService();
        publishBinderService("hdmi_control", new BinderService());
        if (this.mCecController != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
            getContext().registerReceiver(this.mHdmiControlBroadcastReceiver, intentFilter);
            registerContentObserver();
        }
        this.mMhlController.setOption(104, 1);
    }

    @VisibleForTesting
    public void initService() {
        if (this.mIoLooper == null) {
            this.mIoThread.start();
            this.mIoLooper = this.mIoThread.getLooper();
        }
        if (this.mPowerStatusController == null) {
            this.mPowerStatusController = new HdmiCecPowerStatusController(this);
        }
        this.mPowerStatusController.setPowerStatus(getInitialPowerStatus());
        setProhibitMode(false);
        this.mHdmiControlEnabled = this.mHdmiCecConfig.getIntValue("hdmi_cec_enabled");
        this.mSoundbarModeFeatureFlagEnabled = this.mDeviceConfig.getBoolean("enable_soundbar_mode", false);
        this.mEarcTxFeatureFlagEnabled = this.mDeviceConfig.getBoolean("enable_earc_tx", false);
        this.mTransitionFromArcToEarcTxEnabled = this.mDeviceConfig.getBoolean("transition_arc_to_earc_tx", false);
        synchronized (this.mLock) {
            this.mEarcEnabled = this.mHdmiCecConfig.getIntValue("earc_enabled") == 1;
            if (isTvDevice()) {
                this.mEarcEnabled &= this.mEarcTxFeatureFlagEnabled;
            }
        }
        setHdmiCecVolumeControlEnabledInternal(getHdmiCecConfig().getIntValue("volume_control_enabled"));
        this.mMhlInputChangeEnabled = readBooleanSetting("mhl_input_switching_enabled", true);
        if (this.mCecMessageBuffer == null) {
            this.mCecMessageBuffer = new CecMessageBuffer(this);
        }
        if (this.mCecController == null) {
            this.mCecController = HdmiCecController.create(this, getAtomWriter());
        }
        if (this.mCecController == null) {
            Slog.i("HdmiControlService", "Device does not support HDMI-CEC.");
            return;
        }
        if (this.mMhlController == null) {
            this.mMhlController = HdmiMhlControllerStub.create(this);
        }
        if (!this.mMhlController.isReady()) {
            Slog.i("HdmiControlService", "Device does not support MHL-control.");
        }
        if (this.mEarcController == null) {
            this.mEarcController = HdmiEarcController.create(this);
        }
        if (this.mEarcController == null) {
            Slog.i("HdmiControlService", "Device does not support eARC.");
        }
        this.mHdmiCecNetwork = new HdmiCecNetwork(this, this.mCecController, this.mMhlController);
        if (isCecControlEnabled()) {
            initializeCec(1);
        } else {
            this.mCecController.enableCec(false);
        }
        synchronized (this.mLock) {
            this.mMhlDevices = Collections.emptyList();
        }
        this.mHdmiCecNetwork.initPortInfo();
        List<HdmiPortInfo> portInfo = getPortInfo();
        synchronized (this.mLock) {
            this.mEarcSupported = false;
            Iterator<HdmiPortInfo> it = portInfo.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                HdmiPortInfo next = it.next();
                boolean isEarcSupported = next.isEarcSupported();
                if (isEarcSupported && this.mEarcSupported) {
                    Slog.e("HdmiControlService", "HDMI eARC supported on more than 1 port.");
                    this.mEarcSupported = false;
                    this.mEarcPortId = -1;
                    break;
                } else if (isEarcSupported) {
                    this.mEarcPortId = next.getId();
                    this.mEarcSupported = isEarcSupported;
                }
            }
            this.mEarcSupported &= this.mEarcController != null;
        }
        if (isEarcSupported()) {
            if (isEarcEnabled()) {
                initializeEarc(1);
            } else {
                setEarcEnabledInHal(false, false);
            }
        }
        this.mHdmiCecConfig.registerChangeListener("hdmi_cec_enabled", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.2
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                HdmiControlService.this.setCecEnabled(HdmiControlService.this.mHdmiCecConfig.getIntValue("hdmi_cec_enabled"));
            }
        }, this.mServiceThreadExecutor);
        this.mHdmiCecConfig.registerChangeListener("hdmi_cec_version", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.3
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                HdmiControlService.this.initializeCec(0);
            }
        }, this.mServiceThreadExecutor);
        this.mHdmiCecConfig.registerChangeListener("routing_control", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.4
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                boolean z = HdmiControlService.this.mHdmiCecConfig.getIntValue("routing_control") == 1;
                if (HdmiControlService.this.isAudioSystemDevice()) {
                    if (HdmiControlService.this.audioSystem() == null) {
                        Slog.w("HdmiControlService", "Switch device has not registered yet. Can't turn routing on.");
                    } else {
                        HdmiControlService.this.audioSystem().setRoutingControlFeatureEnabled(z);
                    }
                }
            }
        }, this.mServiceThreadExecutor);
        this.mHdmiCecConfig.registerChangeListener("system_audio_control", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.5
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                boolean z = HdmiControlService.this.mHdmiCecConfig.getIntValue("system_audio_control") == 1;
                if (HdmiControlService.this.isTvDeviceEnabled()) {
                    HdmiControlService.this.m49tv().setSystemAudioControlFeatureEnabled(z);
                }
                if (HdmiControlService.this.isAudioSystemDevice()) {
                    if (HdmiControlService.this.audioSystem() == null) {
                        Slog.e("HdmiControlService", "Audio System device has not registered yet. Can't turn system audio mode on.");
                    } else {
                        HdmiControlService.this.audioSystem().onSystemAudioControlFeatureSupportChanged(z);
                    }
                }
            }
        }, this.mServiceThreadExecutor);
        this.mHdmiCecConfig.registerChangeListener("volume_control_enabled", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.6
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                HdmiControlService hdmiControlService = HdmiControlService.this;
                hdmiControlService.setHdmiCecVolumeControlEnabledInternal(hdmiControlService.getHdmiCecConfig().getIntValue("volume_control_enabled"));
            }
        }, this.mServiceThreadExecutor);
        this.mHdmiCecConfig.registerChangeListener("tv_wake_on_one_touch_play", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.7
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                if (HdmiControlService.this.isTvDeviceEnabled()) {
                    HdmiControlService.this.mCecController.enableWakeupByOtp(HdmiControlService.this.m49tv().getAutoWakeup());
                }
            }
        }, this.mServiceThreadExecutor);
        if (isTvDevice()) {
            this.mDeviceConfig.addOnPropertiesChangedListener(getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.hdmi.HdmiControlService.8
                public void onPropertiesChanged(DeviceConfig.Properties properties) {
                    int i = 0;
                    HdmiControlService.this.mEarcTxFeatureFlagEnabled = properties.getBoolean("enable_earc_tx", false);
                    boolean z = HdmiControlService.this.mHdmiCecConfig.getIntValue("earc_enabled") == 1;
                    HdmiControlService hdmiControlService = HdmiControlService.this;
                    if (z && hdmiControlService.mEarcTxFeatureFlagEnabled) {
                        i = 1;
                    }
                    hdmiControlService.setEarcEnabled(i);
                }
            });
        }
        this.mHdmiCecConfig.registerChangeListener("earc_enabled", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.9
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                if (HdmiControlService.this.isTvDevice()) {
                    int i = 0;
                    boolean z = HdmiControlService.this.mHdmiCecConfig.getIntValue("earc_enabled") == 1;
                    HdmiControlService hdmiControlService = HdmiControlService.this;
                    if (z && hdmiControlService.mEarcTxFeatureFlagEnabled) {
                        i = 1;
                    }
                    hdmiControlService.setEarcEnabled(i);
                    return;
                }
                HdmiControlService hdmiControlService2 = HdmiControlService.this;
                hdmiControlService2.setEarcEnabled(hdmiControlService2.mHdmiCecConfig.getIntValue("earc_enabled"));
            }
        }, this.mServiceThreadExecutor);
        this.mDeviceConfig.addOnPropertiesChangedListener(getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.hdmi.HdmiControlService.10
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                int i = 0;
                HdmiControlService.this.mSoundbarModeFeatureFlagEnabled = properties.getBoolean("enable_soundbar_mode", false);
                boolean z = HdmiControlService.this.mHdmiCecConfig.getIntValue("soundbar_mode") == 1;
                HdmiControlService hdmiControlService = HdmiControlService.this;
                if (z && hdmiControlService.mSoundbarModeFeatureFlagEnabled) {
                    i = 1;
                }
                hdmiControlService.setSoundbarMode(i);
            }
        });
        this.mHdmiCecConfig.registerChangeListener("soundbar_mode", new HdmiCecConfig.SettingChangeListener() { // from class: com.android.server.hdmi.HdmiControlService.11
            @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
            public void onChange(String str) {
                int i = 0;
                boolean z = HdmiControlService.this.mHdmiCecConfig.getIntValue("soundbar_mode") == 1;
                HdmiControlService hdmiControlService = HdmiControlService.this;
                if (z && hdmiControlService.mSoundbarModeFeatureFlagEnabled) {
                    i = 1;
                }
                hdmiControlService.setSoundbarMode(i);
            }
        }, this.mServiceThreadExecutor);
        this.mDeviceConfig.addOnPropertiesChangedListener(getContext().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.hdmi.HdmiControlService.12
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                HdmiControlService.this.mTransitionFromArcToEarcTxEnabled = properties.getBoolean("transition_arc_to_earc_tx", false);
            }
        });
    }

    public boolean isScreenOff() {
        return this.mDisplayManager.getDisplay(0).getState() == 1;
    }

    public final void bootCompleted() {
        if (this.mPowerManager.isInteractive() && isPowerStandbyOrTransient()) {
            this.mPowerStatusController.setPowerStatus(0);
            if (this.mAddressAllocated) {
                for (HdmiCecLocalDevice hdmiCecLocalDevice : getAllCecLocalDevices()) {
                    hdmiCecLocalDevice.startQueuedActions();
                }
            }
        }
    }

    @VisibleForTesting
    public void setAudioManager(AudioManager audioManager) {
        this.mAudioManager = audioManager;
    }

    @VisibleForTesting
    public void setCecController(HdmiCecController hdmiCecController) {
        this.mCecController = hdmiCecController;
    }

    @VisibleForTesting
    public void setEarcController(HdmiEarcController hdmiEarcController) {
        this.mEarcController = hdmiEarcController;
    }

    @VisibleForTesting
    public void setHdmiCecNetwork(HdmiCecNetwork hdmiCecNetwork) {
        this.mHdmiCecNetwork = hdmiCecNetwork;
    }

    @VisibleForTesting
    public void setHdmiCecConfig(HdmiCecConfig hdmiCecConfig) {
        this.mHdmiCecConfig = hdmiCecConfig;
    }

    public HdmiCecNetwork getHdmiCecNetwork() {
        return this.mHdmiCecNetwork;
    }

    @VisibleForTesting
    public void setHdmiMhlController(HdmiMhlControllerStub hdmiMhlControllerStub) {
        this.mMhlController = hdmiMhlControllerStub;
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i != 500) {
            if (i == 1000) {
                runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        HdmiControlService.this.bootCompleted();
                    }
                });
                return;
            }
            return;
        }
        this.mDisplayManager = (DisplayManager) getContext().getSystemService(DisplayManager.class);
        this.mTvInputManager = (TvInputManager) getContext().getSystemService("tv_input");
        this.mPowerManager = new PowerManagerWrapper(getContext());
        this.mPowerManagerInternal = new PowerManagerInternalWrapper();
        this.mAudioManager = (AudioManager) getContext().getSystemService("audio");
        this.mStreamMusicMaxVolume = getAudioManager().getStreamMaxVolume(3);
        if (this.mAudioDeviceVolumeManager == null) {
            this.mAudioDeviceVolumeManager = new AudioDeviceVolumeManagerWrapper(getContext());
        }
        getAudioDeviceVolumeManager().addOnDeviceVolumeBehaviorChangedListener(this.mServiceThreadExecutor, new AudioDeviceVolumeManager.OnDeviceVolumeBehaviorChangedListener() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda3
            public final void onDeviceVolumeBehaviorChanged(AudioDeviceAttributes audioDeviceAttributes, int i2) {
                HdmiControlService.this.onDeviceVolumeBehaviorChanged(audioDeviceAttributes, i2);
            }
        });
    }

    public TvInputManager getTvInputManager() {
        return this.mTvInputManager;
    }

    public void registerTvInputCallback(TvInputManager.TvInputCallback tvInputCallback) {
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager == null) {
            return;
        }
        tvInputManager.registerCallback(tvInputCallback, this.mHandler);
    }

    public void unregisterTvInputCallback(TvInputManager.TvInputCallback tvInputCallback) {
        TvInputManager tvInputManager = this.mTvInputManager;
        if (tvInputManager == null) {
            return;
        }
        tvInputManager.unregisterCallback(tvInputCallback);
    }

    @VisibleForTesting
    public void setDeviceConfig(DeviceConfigWrapper deviceConfigWrapper) {
        this.mDeviceConfig = deviceConfigWrapper;
    }

    @VisibleForTesting
    public void setPowerManager(PowerManagerWrapper powerManagerWrapper) {
        this.mPowerManager = powerManagerWrapper;
    }

    @VisibleForTesting
    public void setPowerManagerInternal(PowerManagerInternalWrapper powerManagerInternalWrapper) {
        this.mPowerManagerInternal = powerManagerInternalWrapper;
    }

    public PowerManagerWrapper getPowerManager() {
        return this.mPowerManager;
    }

    public PowerManagerInternalWrapper getPowerManagerInternal() {
        return this.mPowerManagerInternal;
    }

    @VisibleForTesting
    public void setSoundbarMode(int i) {
        boolean z;
        HdmiCecLocalDevicePlayback playback = playback();
        HdmiCecLocalDeviceAudioSystem audioSystem = audioSystem();
        if (playback == null) {
            Slog.w("HdmiControlService", "Device type not compatible to change soundbar mode.");
        } else if (!SystemProperties.getBoolean("persist.sys.hdmi.property_arc_support", true)) {
            Slog.w("HdmiControlService", "Device type doesn't support ARC.");
        } else {
            if (i != 0 || audioSystem == null) {
                z = false;
            } else {
                z = audioSystem.isArcEnabled();
                if (isSystemAudioActivated()) {
                    audioSystem.terminateSystemAudioMode();
                }
                if (z) {
                    if (audioSystem.hasAction(ArcTerminationActionFromAvr.class)) {
                        audioSystem.removeAction(ArcTerminationActionFromAvr.class);
                    }
                    audioSystem.addAndStartAction(new ArcTerminationActionFromAvr(audioSystem, new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiControlService.13
                        public void onComplete(int i2) {
                            HdmiControlService.this.mAddressAllocated = false;
                            HdmiControlService.this.initializeCecLocalDevices(5);
                        }
                    }));
                }
            }
            if (z) {
                return;
            }
            this.mAddressAllocated = false;
            initializeCecLocalDevices(5);
        }
    }

    public boolean isDeviceDiscoveryHandledByPlayback() {
        HdmiCecLocalDevicePlayback playback = playback();
        if (playback != null) {
            return playback.hasAction(DeviceDiscoveryAction.class) || playback.hasAction(HotplugDetectionAction.class);
        }
        return false;
    }

    public final void onInitializeCecComplete(int i) {
        updatePowerStatusOnInitializeCecComplete();
        int i2 = 0;
        this.mWakeUpMessageReceived = false;
        if (isTvDeviceEnabled()) {
            this.mCecController.enableWakeupByOtp(m49tv().getAutoWakeup());
        }
        if (i == 0) {
            i2 = 1;
        } else if (i != 1) {
            i2 = 2;
            if (i == 2) {
                for (HdmiCecLocalDevice hdmiCecLocalDevice : getAllCecLocalDevices()) {
                    hdmiCecLocalDevice.onInitializeCecComplete(i);
                }
            } else if (i != 3) {
                i2 = -1;
            }
        }
        if (i2 != -1) {
            invokeVendorCommandListenersOnControlStateChanged(true, i2);
            announceHdmiControlStatusChange(1);
        }
    }

    public final void updatePowerStatusOnInitializeCecComplete() {
        if (this.mPowerStatusController.isPowerStatusTransientToOn()) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    HdmiControlService.this.lambda$updatePowerStatusOnInitializeCecComplete$0();
                }
            });
        } else if (this.mPowerStatusController.isPowerStatusTransientToStandby()) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    HdmiControlService.this.lambda$updatePowerStatusOnInitializeCecComplete$1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePowerStatusOnInitializeCecComplete$0() {
        this.mPowerStatusController.setPowerStatus(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updatePowerStatusOnInitializeCecComplete$1() {
        this.mPowerStatusController.setPowerStatus(1);
    }

    public final void registerContentObserver() {
        ContentResolver contentResolver = getContext().getContentResolver();
        String[] strArr = {"mhl_input_switching_enabled", "mhl_power_charge_enabled", "device_name"};
        for (int i = 0; i < 3; i++) {
            contentResolver.registerContentObserver(Settings.Global.getUriFor(strArr[i]), false, this.mSettingsObserver, -1);
        }
    }

    /* loaded from: classes.dex */
    public class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:10:0x002b, code lost:
            if (r4.equals("mhl_input_switching_enabled") == false) goto L3;
         */
        @Override // android.database.ContentObserver
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onChange(boolean z, Uri uri) {
            String lastPathSegment = uri.getLastPathSegment();
            char c = 1;
            boolean readBooleanSetting = HdmiControlService.this.readBooleanSetting(lastPathSegment, true);
            lastPathSegment.hashCode();
            switch (lastPathSegment.hashCode()) {
                case -1543071020:
                    if (lastPathSegment.equals("device_name")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -1262529811:
                    break;
                case -885757826:
                    if (lastPathSegment.equals("mhl_power_charge_enabled")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    HdmiControlService.this.setDisplayName(HdmiControlService.this.readStringSetting(lastPathSegment, Build.MODEL));
                    return;
                case 1:
                    HdmiControlService.this.setMhlInputChangeEnabled(readBooleanSetting);
                    return;
                case 2:
                    HdmiControlService.this.mMhlController.setOption(102, HdmiControlService.toInt(readBooleanSetting));
                    return;
                default:
                    return;
            }
        }
    }

    @VisibleForTesting
    public boolean readBooleanSetting(String str, boolean z) {
        return Settings.Global.getInt(getContext().getContentResolver(), str, toInt(z)) == 1;
    }

    @VisibleForTesting
    public int readIntSetting(String str, int i) {
        return Settings.Global.getInt(getContext().getContentResolver(), str, i);
    }

    @VisibleForTesting
    public void writeStringSystemProperty(String str, String str2) {
        SystemProperties.set(str, str2);
    }

    @VisibleForTesting
    public boolean readBooleanSystemProperty(String str, boolean z) {
        return SystemProperties.getBoolean(str, z);
    }

    public String readStringSetting(String str, String str2) {
        String string = Settings.Global.getString(getContext().getContentResolver(), str);
        return TextUtils.isEmpty(string) ? str2 : string;
    }

    public final void initializeCec(int i) {
        this.mAddressAllocated = false;
        this.mCecVersion = Math.max(5, Math.min(getHdmiCecConfig().getIntValue("hdmi_cec_version"), this.mCecController.getVersion()));
        this.mCecController.enableSystemCecControl(true);
        this.mCecController.setLanguage(this.mMenuLanguage);
        initializeCecLocalDevices(i);
    }

    public final List<Integer> getCecLocalDeviceTypes() {
        ArrayList arrayList = new ArrayList(this.mCecLocalDevices);
        if (this.mHdmiCecConfig.getIntValue("soundbar_mode") == 1 && !arrayList.contains(5) && SystemProperties.getBoolean("persist.sys.hdmi.property_arc_support", true) && this.mSoundbarModeFeatureFlagEnabled) {
            arrayList.add(5);
        }
        return arrayList;
    }

    @VisibleForTesting
    public void initializeCecLocalDevices(int i) {
        assertRunOnServiceThread();
        ArrayList<HdmiCecLocalDevice> arrayList = new ArrayList<>();
        for (Integer num : getCecLocalDeviceTypes()) {
            int intValue = num.intValue();
            HdmiCecLocalDevice localDevice = this.mHdmiCecNetwork.getLocalDevice(intValue);
            if (localDevice == null) {
                localDevice = HdmiCecLocalDevice.create(this, intValue);
            }
            localDevice.init();
            arrayList.add(localDevice);
        }
        clearCecLocalDevices();
        allocateLogicalAddress(arrayList, i);
    }

    @VisibleForTesting
    public void allocateLogicalAddress(final ArrayList<HdmiCecLocalDevice> arrayList, final int i) {
        assertRunOnServiceThread();
        this.mCecController.clearLogicalAddress();
        final ArrayList arrayList2 = new ArrayList();
        final int[] iArr = new int[1];
        this.mAddressAllocated = arrayList.isEmpty();
        this.mSelectRequestBuffer.clear();
        Iterator<HdmiCecLocalDevice> it = arrayList.iterator();
        while (it.hasNext()) {
            final HdmiCecLocalDevice next = it.next();
            this.mCecController.allocateLogicalAddress(next.getType(), next.getPreferredAddress(), new HdmiCecController.AllocateAddressCallback() { // from class: com.android.server.hdmi.HdmiControlService.14
                @Override // com.android.server.hdmi.HdmiCecController.AllocateAddressCallback
                public void onAllocated(int i2, int i3) {
                    if (i3 == 15) {
                        Slog.e("HdmiControlService", "Failed to allocate address:[device_type:" + i2 + "]");
                    } else {
                        HdmiControlService hdmiControlService = HdmiControlService.this;
                        next.setDeviceInfo(hdmiControlService.createDeviceInfo(i3, i2, 0, hdmiControlService.getCecVersion()));
                        HdmiControlService.this.mHdmiCecNetwork.addLocalDevice(i2, next);
                        HdmiControlService.this.mCecController.addLogicalAddress(i3);
                        arrayList2.add(next);
                    }
                    int size = arrayList.size();
                    int[] iArr2 = iArr;
                    int i4 = iArr2[0] + 1;
                    iArr2[0] = i4;
                    if (size == i4) {
                        int i5 = i;
                        if (i5 != 4 && i5 != 5) {
                            HdmiControlService.this.onInitializeCecComplete(i5);
                        }
                        HdmiControlService.this.mAddressAllocated = true;
                        HdmiControlService.this.notifyAddressAllocated(arrayList2, i);
                        if (HdmiControlService.this.mDisplayStatusCallback != null) {
                            HdmiControlService hdmiControlService2 = HdmiControlService.this;
                            hdmiControlService2.queryDisplayStatus(hdmiControlService2.mDisplayStatusCallback);
                            HdmiControlService.this.mDisplayStatusCallback = null;
                        }
                        if (HdmiControlService.this.mOtpCallbackPendingAddressAllocation != null) {
                            HdmiControlService hdmiControlService3 = HdmiControlService.this;
                            hdmiControlService3.oneTouchPlay(hdmiControlService3.mOtpCallbackPendingAddressAllocation);
                            HdmiControlService.this.mOtpCallbackPendingAddressAllocation = null;
                        }
                        HdmiControlService.this.mCecMessageBuffer.processMessages();
                    }
                }
            });
        }
    }

    public final void notifyAddressAllocated(ArrayList<HdmiCecLocalDevice> arrayList, int i) {
        assertRunOnServiceThread();
        List<HdmiCecMessage> buffer = this.mCecMessageBuffer.getBuffer();
        Iterator<HdmiCecLocalDevice> it = arrayList.iterator();
        while (it.hasNext()) {
            HdmiCecLocalDevice next = it.next();
            next.handleAddressAllocated(next.getDeviceInfo().getLogicalAddress(), buffer, i);
        }
        if (isTvDeviceEnabled()) {
            m49tv().setSelectRequestBuffer(this.mSelectRequestBuffer);
        }
    }

    public boolean isAddressAllocated() {
        return this.mAddressAllocated;
    }

    public List<HdmiPortInfo> getPortInfo() {
        List<HdmiPortInfo> portInfo;
        synchronized (this.mLock) {
            portInfo = this.mHdmiCecNetwork.getPortInfo();
        }
        return portInfo;
    }

    public HdmiPortInfo getPortInfo(int i) {
        return this.mHdmiCecNetwork.getPortInfo(i);
    }

    public int portIdToPath(int i) {
        return this.mHdmiCecNetwork.portIdToPath(i);
    }

    public int pathToPortId(int i) {
        return this.mHdmiCecNetwork.physicalAddressToPortId(i);
    }

    public boolean isValidPortId(int i) {
        return this.mHdmiCecNetwork.getPortInfo(i) != null;
    }

    @VisibleForTesting
    public Looper getIoLooper() {
        return this.mIoLooper;
    }

    @VisibleForTesting
    public void setIoLooper(Looper looper) {
        this.mIoLooper = looper;
    }

    @VisibleForTesting
    public void setCecMessageBuffer(CecMessageBuffer cecMessageBuffer) {
        this.mCecMessageBuffer = cecMessageBuffer;
    }

    public Looper getServiceLooper() {
        return this.mHandler.getLooper();
    }

    public int getPhysicalAddress() {
        return this.mCecController.getPhysicalAddress();
    }

    public int getVendorId() {
        return this.mCecController.getVendorId();
    }

    public HdmiDeviceInfo getDeviceInfo(int i) {
        assertRunOnServiceThread();
        return this.mHdmiCecNetwork.getCecDeviceInfo(i);
    }

    public HdmiDeviceInfo getDeviceInfoByPort(int i) {
        assertRunOnServiceThread();
        this.mMhlController.getLocalDevice(i);
        return null;
    }

    @VisibleForTesting
    public int getCecVersion() {
        return this.mCecVersion;
    }

    public boolean isConnectedToArcPort(int i) {
        return this.mHdmiCecNetwork.isConnectedToArcPort(i);
    }

    public boolean isConnected(int i) {
        assertRunOnServiceThread();
        return this.mCecController.isConnected(i);
    }

    public void runOnServiceThread(Runnable runnable) {
        this.mHandler.post(new WorkSourceUidPreservingRunnable(runnable));
    }

    public final void assertRunOnServiceThread() {
        if (Looper.myLooper() != this.mHandler.getLooper()) {
            throw new IllegalStateException("Should run on service thread.");
        }
    }

    public void sendCecCommand(HdmiCecMessage hdmiCecMessage, SendMessageCallback sendMessageCallback) {
        assertRunOnServiceThread();
        if (hdmiCecMessage.getValidationResult() == 0 && verifyPhysicalAddresses(hdmiCecMessage)) {
            this.mCecController.sendCommand(hdmiCecMessage, sendMessageCallback);
            return;
        }
        HdmiLogger.error("Invalid message type:" + hdmiCecMessage, new Object[0]);
        if (sendMessageCallback != null) {
            sendMessageCallback.onSendCompleted(3);
        }
    }

    public void sendCecCommand(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        sendCecCommand(hdmiCecMessage, null);
    }

    public void maySendFeatureAbortCommand(HdmiCecMessage hdmiCecMessage, int i) {
        assertRunOnServiceThread();
        this.mCecController.maySendFeatureAbortCommand(hdmiCecMessage, i);
    }

    public boolean verifyPhysicalAddresses(HdmiCecMessage hdmiCecMessage) {
        byte[] params = hdmiCecMessage.getParams();
        int opcode = hdmiCecMessage.getOpcode();
        if (opcode == 112) {
            return params.length == 0 || verifyPhysicalAddress(params, 0);
        }
        if (opcode != 132 && opcode != 134 && opcode != 157) {
            if (opcode == 161 || opcode == 162) {
                return verifyExternalSourcePhysicalAddress(params, 7);
            }
            switch (opcode) {
                case 128:
                    return verifyPhysicalAddress(params, 0) && verifyPhysicalAddress(params, 2);
                case 129:
                case 130:
                    break;
                default:
                    return true;
            }
        }
        return verifyPhysicalAddress(params, 0);
    }

    public final boolean verifyPhysicalAddress(byte[] bArr, int i) {
        if (isTvDevice()) {
            int twoBytesToInt = HdmiUtils.twoBytesToInt(bArr, i);
            return (twoBytesToInt != 65535 && twoBytesToInt == getPhysicalAddress()) || pathToPortId(twoBytesToInt) != -1;
        }
        return true;
    }

    public final boolean verifyExternalSourcePhysicalAddress(byte[] bArr, int i) {
        byte b = bArr[i];
        int i2 = i + 1;
        if (b != 5 || bArr.length - i2 < 2) {
            return true;
        }
        return verifyPhysicalAddress(bArr, i2);
    }

    public final boolean sourceAddressIsLocal(HdmiCecMessage hdmiCecMessage) {
        for (HdmiCecLocalDevice hdmiCecLocalDevice : getAllCecLocalDevices()) {
            if (hdmiCecMessage.getSource() == hdmiCecLocalDevice.getDeviceInfo().getLogicalAddress() && hdmiCecMessage.getSource() != 15) {
                HdmiLogger.warning("Unexpected source: message sent from device itself, " + hdmiCecMessage, new Object[0]);
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    public int handleCecCommand(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        int validationResult = hdmiCecMessage.getValidationResult();
        int i = 3;
        if (validationResult != 3 && verifyPhysicalAddresses(hdmiCecMessage)) {
            i = -1;
            if (validationResult == 0 && !sourceAddressIsLocal(hdmiCecMessage)) {
                getHdmiCecNetwork().handleCecMessage(hdmiCecMessage);
                int dispatchMessageToLocalDevice = dispatchMessageToLocalDevice(hdmiCecMessage);
                if (this.mAddressAllocated || !this.mCecMessageBuffer.bufferMessage(hdmiCecMessage)) {
                    return dispatchMessageToLocalDevice;
                }
                return -1;
            }
        }
        return i;
    }

    public void enableAudioReturnChannel(int i, boolean z) {
        if (!this.mTransitionFromArcToEarcTxEnabled && z && this.mEarcController != null) {
            setEarcEnabledInHal(false, false);
        }
        this.mCecController.enableAudioReturnChannel(i, z);
    }

    @VisibleForTesting
    public int dispatchMessageToLocalDevice(HdmiCecMessage hdmiCecMessage) {
        assertRunOnServiceThread();
        for (HdmiCecLocalDevice hdmiCecLocalDevice : this.mHdmiCecNetwork.getLocalDeviceList()) {
            int dispatchMessage = hdmiCecLocalDevice.dispatchMessage(hdmiCecMessage);
            if (dispatchMessage != -2 && hdmiCecMessage.getDestination() != 15) {
                return dispatchMessage;
            }
        }
        if (hdmiCecMessage.getDestination() == 15) {
            return -1;
        }
        HdmiLogger.warning("Unhandled cec command:" + hdmiCecMessage, new Object[0]);
        return -2;
    }

    public void onHotplug(int i, boolean z) {
        assertRunOnServiceThread();
        this.mHdmiCecNetwork.initPortInfo();
        if (z && !isTvDevice() && getPortInfo(i).getType() == 1) {
            ArrayList<HdmiCecLocalDevice> arrayList = new ArrayList<>();
            for (Integer num : getCecLocalDeviceTypes()) {
                int intValue = num.intValue();
                HdmiCecLocalDevice localDevice = this.mHdmiCecNetwork.getLocalDevice(intValue);
                if (localDevice == null) {
                    localDevice = HdmiCecLocalDevice.create(this, intValue);
                    localDevice.init();
                }
                arrayList.add(localDevice);
            }
            allocateLogicalAddress(arrayList, 4);
        }
        for (HdmiCecLocalDevice hdmiCecLocalDevice : this.mHdmiCecNetwork.getLocalDeviceList()) {
            hdmiCecLocalDevice.onHotplug(i, z);
        }
        announceHotplugEvent(i, z);
    }

    public void pollDevices(DevicePollingCallback devicePollingCallback, int i, int i2, int i3) {
        assertRunOnServiceThread();
        this.mCecController.pollDevices(devicePollingCallback, i, checkPollStrategy(i2), i3);
    }

    public final int checkPollStrategy(int i) {
        int i2 = i & 3;
        if (i2 == 0) {
            throw new IllegalArgumentException("Invalid poll strategy:" + i);
        }
        int i3 = 196608 & i;
        if (i3 != 0) {
            return i2 | i3;
        }
        throw new IllegalArgumentException("Invalid iteration strategy:" + i);
    }

    public List<HdmiCecLocalDevice> getAllCecLocalDevices() {
        assertRunOnServiceThread();
        return this.mHdmiCecNetwork.getLocalDeviceList();
    }

    public void checkLogicalAddressConflictAndReallocate(int i, int i2) {
        if (i2 == getPhysicalAddress()) {
            return;
        }
        for (HdmiCecLocalDevice hdmiCecLocalDevice : getAllCecLocalDevices()) {
            if (hdmiCecLocalDevice.getDeviceInfo().getLogicalAddress() == i) {
                HdmiLogger.debug("allocate logical address for " + hdmiCecLocalDevice.getDeviceInfo(), new Object[0]);
                ArrayList<HdmiCecLocalDevice> arrayList = new ArrayList<>();
                arrayList.add(hdmiCecLocalDevice);
                allocateLogicalAddress(arrayList, 4);
                return;
            }
        }
    }

    public Object getServiceLock() {
        return this.mLock;
    }

    public void setAudioStatus(boolean z, int i) {
        if (isTvDeviceEnabled() && m49tv().isSystemAudioActivated() && m49tv().isArcEstablished() && getHdmiCecVolumeControl() != 0) {
            AudioManager audioManager = getAudioManager();
            boolean isStreamMute = audioManager.isStreamMute(3);
            if (z) {
                if (isStreamMute) {
                    return;
                }
                audioManager.setStreamMute(3, true);
                return;
            }
            if (isStreamMute) {
                audioManager.setStreamMute(3, false);
            }
            if (i < 0 || i > 100) {
                return;
            }
            Slog.i("HdmiControlService", "volume: " + i);
            audioManager.setStreamVolume(3, i, FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP);
        }
    }

    public void announceSystemAudioModeChange(boolean z) {
        synchronized (this.mLock) {
            Iterator<SystemAudioModeChangeListenerRecord> it = this.mSystemAudioModeChangeListenerRecords.iterator();
            while (it.hasNext()) {
                invokeSystemAudioModeChangeLocked(it.next().mListener, z);
            }
        }
    }

    public final HdmiDeviceInfo createDeviceInfo(int i, int i2, int i3, int i4) {
        return HdmiDeviceInfo.cecDeviceBuilder().setLogicalAddress(i).setPhysicalAddress(getPhysicalAddress()).setPortId(pathToPortId(getPhysicalAddress())).setDeviceType(i2).setVendorId(getVendorId()).setDisplayName(readStringSetting("device_name", Build.MODEL)).setDevicePowerStatus(i3).setCecVersion(i4).build();
    }

    public final void setDisplayName(String str) {
        for (HdmiCecLocalDevice hdmiCecLocalDevice : getAllCecLocalDevices()) {
            HdmiDeviceInfo deviceInfo = hdmiCecLocalDevice.getDeviceInfo();
            if (!deviceInfo.getDisplayName().equals(str)) {
                hdmiCecLocalDevice.setDeviceInfo(deviceInfo.toBuilder().setDisplayName(str).build());
                sendCecCommand(HdmiCecMessageBuilder.buildSetOsdNameCommand(deviceInfo.getLogicalAddress(), 0, str));
            }
        }
    }

    @GuardedBy({"mLock"})
    public final List<HdmiDeviceInfo> getMhlDevicesLocked() {
        return this.mMhlDevices;
    }

    /* loaded from: classes.dex */
    public class HdmiMhlVendorCommandListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiMhlVendorCommandListener mListener;

        public HdmiMhlVendorCommandListenerRecord(IHdmiMhlVendorCommandListener iHdmiMhlVendorCommandListener) {
            this.mListener = iHdmiMhlVendorCommandListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            HdmiControlService.this.mMhlVendorCommandListenerRecords.remove(this);
        }
    }

    /* loaded from: classes.dex */
    public final class HdmiControlStatusChangeListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiControlStatusChangeListener mListener;

        public HdmiControlStatusChangeListenerRecord(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) {
            this.mListener = iHdmiControlStatusChangeListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mHdmiControlStatusChangeListenerRecords.remove(this);
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof HdmiControlStatusChangeListenerRecord) {
                return obj == this || ((HdmiControlStatusChangeListenerRecord) obj).mListener == this.mListener;
            }
            return false;
        }

        public int hashCode() {
            return this.mListener.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public final class HotplugEventListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiHotplugEventListener mListener;

        public HotplugEventListenerRecord(IHdmiHotplugEventListener iHdmiHotplugEventListener) {
            this.mListener = iHdmiHotplugEventListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mHotplugEventListenerRecords.remove(this);
            }
        }

        public boolean equals(Object obj) {
            if (obj instanceof HotplugEventListenerRecord) {
                return obj == this || ((HotplugEventListenerRecord) obj).mListener == this.mListener;
            }
            return false;
        }

        public int hashCode() {
            return this.mListener.hashCode();
        }
    }

    /* loaded from: classes.dex */
    public final class DeviceEventListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiDeviceEventListener mListener;

        public DeviceEventListenerRecord(IHdmiDeviceEventListener iHdmiDeviceEventListener) {
            this.mListener = iHdmiDeviceEventListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mDeviceEventListenerRecords.remove(this);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class SystemAudioModeChangeListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiSystemAudioModeChangeListener mListener;

        public SystemAudioModeChangeListenerRecord(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) {
            this.mListener = iHdmiSystemAudioModeChangeListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mSystemAudioModeChangeListenerRecords.remove(this);
            }
        }
    }

    /* loaded from: classes.dex */
    public class VendorCommandListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiVendorCommandListener mListener;
        public final int mVendorId;

        public VendorCommandListenerRecord(IHdmiVendorCommandListener iHdmiVendorCommandListener, int i) {
            this.mListener = iHdmiVendorCommandListener;
            this.mVendorId = i;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                HdmiControlService.this.mVendorCommandListenerRecords.remove(this);
            }
        }
    }

    /* loaded from: classes.dex */
    public class HdmiRecordListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiRecordListener mListener;

        public HdmiRecordListenerRecord(IHdmiRecordListener iHdmiRecordListener) {
            this.mListener = iHdmiRecordListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                if (HdmiControlService.this.mRecordListenerRecord == this) {
                    HdmiControlService.this.mRecordListenerRecord = null;
                }
            }
        }
    }

    public final void setWorkSourceUidToCallingUid() {
        Binder.setCallingWorkSourceUid(Binder.getCallingUid());
    }

    public final void enforceAccessPermission() {
        getContext().enforceCallingOrSelfPermission("android.permission.HDMI_CEC", "HdmiControlService");
    }

    public final void initBinderCall() {
        enforceAccessPermission();
        setWorkSourceUidToCallingUid();
    }

    /* loaded from: classes.dex */
    public final class BinderService extends IHdmiControlService.Stub {
        public BinderService() {
        }

        public int[] getSupportedTypes() {
            HdmiControlService.this.initBinderCall();
            int size = HdmiControlService.this.mCecLocalDevices.size();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = ((Integer) HdmiControlService.this.mCecLocalDevices.get(i)).intValue();
            }
            return iArr;
        }

        public HdmiDeviceInfo getActiveSource() {
            HdmiControlService.this.initBinderCall();
            return HdmiControlService.this.getActiveSource();
        }

        public void deviceSelect(final int i, final IHdmiControlCallback iHdmiControlCallback) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.1
                @Override // java.lang.Runnable
                public void run() {
                    if (iHdmiControlCallback == null) {
                        Slog.e("HdmiControlService", "Callback cannot be null");
                        return;
                    }
                    HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
                    HdmiCecLocalDevicePlayback playback = HdmiControlService.this.playback();
                    if (m49tv != null || playback != null) {
                        if (m49tv != null) {
                            HdmiControlService.this.mMhlController.getLocalDeviceById(i);
                            m49tv.deviceSelect(i, iHdmiControlCallback);
                            return;
                        }
                        playback.deviceSelect(i, iHdmiControlCallback);
                    } else if (!HdmiControlService.this.mAddressAllocated) {
                        HdmiControlService.this.mSelectRequestBuffer.set(SelectRequestBuffer.newDeviceSelect(HdmiControlService.this, i, iHdmiControlCallback));
                    } else if (HdmiControlService.this.isTvDevice()) {
                        Slog.e("HdmiControlService", "Local tv device not available");
                    } else {
                        HdmiControlService.this.invokeCallback(iHdmiControlCallback, 2);
                    }
                }
            });
        }

        public void portSelect(final int i, final IHdmiControlCallback iHdmiControlCallback) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.2
                @Override // java.lang.Runnable
                public void run() {
                    if (iHdmiControlCallback == null) {
                        Slog.e("HdmiControlService", "Callback cannot be null");
                        return;
                    }
                    HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
                    if (m49tv != null) {
                        m49tv.doManualPortSwitching(i, iHdmiControlCallback);
                        return;
                    }
                    HdmiCecLocalDeviceAudioSystem audioSystem = HdmiControlService.this.audioSystem();
                    if (audioSystem != null) {
                        audioSystem.doManualPortSwitching(i, iHdmiControlCallback);
                    } else if (!HdmiControlService.this.mAddressAllocated) {
                        HdmiControlService.this.mSelectRequestBuffer.set(SelectRequestBuffer.newPortSelect(HdmiControlService.this, i, iHdmiControlCallback));
                    } else {
                        Slog.w("HdmiControlService", "Local device not available");
                        HdmiControlService.this.invokeCallback(iHdmiControlCallback, 2);
                    }
                }
            });
        }

        public void sendKeyEvent(final int i, final int i2, final boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.3
                @Override // java.lang.Runnable
                public void run() {
                    HdmiControlService.this.mMhlController.getLocalDevice(HdmiControlService.this.mActivePortId);
                    if (HdmiControlService.this.mCecController != null) {
                        HdmiCecLocalDevice localDevice = HdmiControlService.this.mHdmiCecNetwork.getLocalDevice(i);
                        if (localDevice == null) {
                            Slog.w("HdmiControlService", "Local device not available to send key event.");
                        } else {
                            localDevice.sendKeyEvent(i2, z);
                        }
                    }
                }
            });
        }

        public void sendVolumeKeyEvent(final int i, final int i2, final boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.4
                @Override // java.lang.Runnable
                public void run() {
                    if (HdmiControlService.this.mCecController == null) {
                        Slog.w("HdmiControlService", "CEC controller not available to send volume key event.");
                        return;
                    }
                    HdmiCecLocalDevice localDevice = HdmiControlService.this.mHdmiCecNetwork.getLocalDevice(i);
                    if (localDevice == null) {
                        Slog.w("HdmiControlService", "Local device " + i + " not available to send volume key event.");
                        return;
                    }
                    localDevice.sendVolumeKeyEvent(i2, z);
                }
            });
        }

        public void oneTouchPlay(final IHdmiControlCallback iHdmiControlCallback) {
            HdmiControlService.this.initBinderCall();
            int callingPid = Binder.getCallingPid();
            Slog.d("HdmiControlService", "Process pid: " + callingPid + " is calling oneTouchPlay.");
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.5
                @Override // java.lang.Runnable
                public void run() {
                    HdmiControlService.this.oneTouchPlay(iHdmiControlCallback);
                }
            });
        }

        public void toggleAndFollowTvPower() {
            HdmiControlService.this.initBinderCall();
            int callingPid = Binder.getCallingPid();
            Slog.d("HdmiControlService", "Process pid: " + callingPid + " is calling toggleAndFollowTvPower.");
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.6
                @Override // java.lang.Runnable
                public void run() {
                    HdmiControlService.this.toggleAndFollowTvPower();
                }
            });
        }

        public boolean shouldHandleTvPowerKey() {
            HdmiControlService.this.initBinderCall();
            return HdmiControlService.this.shouldHandleTvPowerKey();
        }

        public void queryDisplayStatus(final IHdmiControlCallback iHdmiControlCallback) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.7
                @Override // java.lang.Runnable
                public void run() {
                    HdmiControlService.this.queryDisplayStatus(iHdmiControlCallback);
                }
            });
        }

        public void addHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addHdmiControlStatusChangeListener(iHdmiControlStatusChangeListener);
        }

        public void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.removeHdmiControlStatusChangeListener(iHdmiControlStatusChangeListener);
        }

        public void addHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addHdmiCecVolumeControlFeatureListener(iHdmiCecVolumeControlFeatureListener);
        }

        public void removeHdmiCecVolumeControlFeatureListener(IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.removeHdmiControlVolumeControlStatusChangeListener(iHdmiCecVolumeControlFeatureListener);
        }

        public void addHotplugEventListener(IHdmiHotplugEventListener iHdmiHotplugEventListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addHotplugEventListener(iHdmiHotplugEventListener);
        }

        public void removeHotplugEventListener(IHdmiHotplugEventListener iHdmiHotplugEventListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.removeHotplugEventListener(iHdmiHotplugEventListener);
        }

        public void addDeviceEventListener(IHdmiDeviceEventListener iHdmiDeviceEventListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addDeviceEventListener(iHdmiDeviceEventListener);
        }

        public List<HdmiPortInfo> getPortInfo() {
            HdmiControlService.this.initBinderCall();
            if (HdmiControlService.this.getPortInfo() == null) {
                return Collections.emptyList();
            }
            return HdmiControlService.this.getPortInfo();
        }

        public boolean canChangeSystemAudioMode() {
            HdmiControlService.this.initBinderCall();
            HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
            if (m49tv == null) {
                return false;
            }
            return m49tv.hasSystemAudioDevice();
        }

        public boolean getSystemAudioMode() {
            HdmiControlService.this.initBinderCall();
            HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
            HdmiCecLocalDeviceAudioSystem audioSystem = HdmiControlService.this.audioSystem();
            return (m49tv != null && m49tv.isSystemAudioActivated()) || (audioSystem != null && audioSystem.isSystemAudioActivated());
        }

        public int getPhysicalAddress() {
            int physicalAddress;
            HdmiControlService.this.initBinderCall();
            synchronized (HdmiControlService.this.mLock) {
                physicalAddress = HdmiControlService.this.mHdmiCecNetwork.getPhysicalAddress();
            }
            return physicalAddress;
        }

        public void setSystemAudioMode(final boolean z, final IHdmiControlCallback iHdmiControlCallback) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.8
                @Override // java.lang.Runnable
                public void run() {
                    HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
                    if (m49tv == null) {
                        Slog.w("HdmiControlService", "Local tv device not available");
                        HdmiControlService.this.invokeCallback(iHdmiControlCallback, 2);
                        return;
                    }
                    m49tv.changeSystemAudioMode(z, iHdmiControlCallback);
                }
            });
        }

        public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addSystemAudioModeChangeListner(iHdmiSystemAudioModeChangeListener);
        }

        public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.removeSystemAudioModeChangeListener(iHdmiSystemAudioModeChangeListener);
        }

        public void setInputChangeListener(IHdmiInputChangeListener iHdmiInputChangeListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.setInputChangeListener(iHdmiInputChangeListener);
        }

        public List<HdmiDeviceInfo> getInputDevices() {
            HdmiControlService.this.initBinderCall();
            return HdmiUtils.mergeToUnmodifiableList(HdmiControlService.this.mHdmiCecNetwork.getSafeExternalInputsLocked(), HdmiControlService.this.getMhlDevicesLocked());
        }

        public List<HdmiDeviceInfo> getDeviceList() {
            HdmiControlService.this.initBinderCall();
            return HdmiControlService.this.mHdmiCecNetwork.getSafeCecDevicesLocked();
        }

        public void powerOffRemoteDevice(final int i, final int i2) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.9
                @Override // java.lang.Runnable
                public void run() {
                    Slog.w("HdmiControlService", "Device " + i + " power status is " + i2 + " before standby command sent out");
                    HdmiControlService hdmiControlService = HdmiControlService.this;
                    hdmiControlService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(hdmiControlService.getRemoteControlSourceAddress(), i));
                }
            });
        }

        public void powerOnRemoteDevice(final int i, final int i2) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.10
                @Override // java.lang.Runnable
                public void run() {
                    Slog.i("HdmiControlService", "Device " + i + " power status is " + i2 + " before power on command sent out");
                    if (HdmiControlService.this.getSwitchDevice() != null) {
                        HdmiControlService.this.getSwitchDevice().sendUserControlPressedAndReleased(i, 109);
                    } else {
                        Slog.e("HdmiControlService", "Can't get the correct local device to handle routing.");
                    }
                }
            });
        }

        public void askRemoteDeviceToBecomeActiveSource(final int i) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.11
                @Override // java.lang.Runnable
                public void run() {
                    HdmiCecMessage buildSetStreamPath = HdmiCecMessageBuilder.buildSetStreamPath(HdmiControlService.this.getRemoteControlSourceAddress(), i);
                    if (HdmiControlService.this.pathToPortId(i) != -1) {
                        if (HdmiControlService.this.getSwitchDevice() != null) {
                            HdmiControlService.this.getSwitchDevice().handleSetStreamPath(buildSetStreamPath);
                        } else {
                            Slog.e("HdmiControlService", "Can't get the correct local device to handle routing.");
                        }
                    }
                    HdmiControlService.this.sendCecCommand(buildSetStreamPath);
                }
            });
        }

        public void setSystemAudioVolume(final int i, final int i2, final int i3) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.12
                @Override // java.lang.Runnable
                public void run() {
                    HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
                    if (m49tv == null) {
                        Slog.w("HdmiControlService", "Local tv device not available");
                        return;
                    }
                    int i4 = i;
                    m49tv.changeVolume(i4, i2 - i4, i3);
                }
            });
        }

        public void setSystemAudioMute(final boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.13
                @Override // java.lang.Runnable
                public void run() {
                    HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
                    if (m49tv == null) {
                        Slog.w("HdmiControlService", "Local tv device not available");
                    } else {
                        m49tv.changeMute(z);
                    }
                }
            });
        }

        public void setArcMode(final boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.14
                @Override // java.lang.Runnable
                public void run() {
                    HdmiCecLocalDeviceTv m49tv = HdmiControlService.this.m49tv();
                    if (m49tv == null) {
                        Slog.w("HdmiControlService", "Local tv device not available to change arc mode.");
                    } else {
                        m49tv.startArcAction(z);
                    }
                }
            });
        }

        public void setProhibitMode(boolean z) {
            HdmiControlService.this.initBinderCall();
            if (HdmiControlService.this.isTvDevice()) {
                HdmiControlService.this.setProhibitMode(z);
            }
        }

        public void addVendorCommandListener(IHdmiVendorCommandListener iHdmiVendorCommandListener, int i) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addVendorCommandListener(iHdmiVendorCommandListener, i);
        }

        public void sendVendorCommand(final int i, final int i2, final byte[] bArr, final boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.15
                @Override // java.lang.Runnable
                public void run() {
                    HdmiCecLocalDevice localDevice = HdmiControlService.this.mHdmiCecNetwork.getLocalDevice(i);
                    if (localDevice == null) {
                        Slog.w("HdmiControlService", "Local device not available");
                    } else if (z) {
                        HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildVendorCommandWithId(localDevice.getDeviceInfo().getLogicalAddress(), i2, HdmiControlService.this.getVendorId(), bArr));
                    } else {
                        HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildVendorCommand(localDevice.getDeviceInfo().getLogicalAddress(), i2, bArr));
                    }
                }
            });
        }

        public void sendStandby(final int i, final int i2) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.16
                @Override // java.lang.Runnable
                public void run() {
                    HdmiControlService.this.mMhlController.getLocalDeviceById(i2);
                    HdmiCecLocalDevice localDevice = HdmiControlService.this.mHdmiCecNetwork.getLocalDevice(i);
                    if (localDevice == null) {
                        localDevice = HdmiControlService.this.audioSystem();
                    }
                    if (localDevice == null) {
                        Slog.w("HdmiControlService", "Local device not available");
                    } else {
                        localDevice.sendStandby(i2);
                    }
                }
            });
        }

        public void setHdmiRecordListener(IHdmiRecordListener iHdmiRecordListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.setHdmiRecordListener(iHdmiRecordListener);
        }

        public void startOneTouchRecord(final int i, final byte[] bArr) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.17
                @Override // java.lang.Runnable
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w("HdmiControlService", "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.m49tv().startOneTouchRecord(i, bArr);
                    }
                }
            });
        }

        public void stopOneTouchRecord(final int i) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.18
                @Override // java.lang.Runnable
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w("HdmiControlService", "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.m49tv().stopOneTouchRecord(i);
                    }
                }
            });
        }

        public void startTimerRecording(final int i, final int i2, final byte[] bArr) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.19
                @Override // java.lang.Runnable
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w("HdmiControlService", "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.m49tv().startTimerRecording(i, i2, bArr);
                    }
                }
            });
        }

        public void clearTimerRecording(final int i, final int i2, final byte[] bArr) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.20
                @Override // java.lang.Runnable
                public void run() {
                    if (!HdmiControlService.this.isTvDeviceEnabled()) {
                        Slog.w("HdmiControlService", "TV device is not enabled.");
                    } else {
                        HdmiControlService.this.m49tv().clearTimerRecording(i, i2, bArr);
                    }
                }
            });
        }

        public void sendMhlVendorCommand(final int i, final int i2, final int i3, final byte[] bArr) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.21
                @Override // java.lang.Runnable
                public void run() {
                    if (!HdmiControlService.this.isCecControlEnabled()) {
                        Slog.w("HdmiControlService", "Hdmi control is disabled.");
                        return;
                    }
                    HdmiControlService.this.mMhlController.getLocalDevice(i);
                    Slog.w("HdmiControlService", "Invalid port id:" + i);
                }
            });
        }

        public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener iHdmiMhlVendorCommandListener) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.addHdmiMhlVendorCommandListener(iHdmiMhlVendorCommandListener);
        }

        public void setStandbyMode(final boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.22
                @Override // java.lang.Runnable
                public void run() {
                    HdmiControlService.this.setStandbyMode(z);
                }
            });
        }

        public void reportAudioStatus(final int i, int i2, int i3, boolean z) {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.23
                @Override // java.lang.Runnable
                public void run() {
                    if (HdmiControlService.this.mHdmiCecNetwork.getLocalDevice(i) == null) {
                        Slog.w("HdmiControlService", "Local device not available");
                    } else if (HdmiControlService.this.audioSystem() == null) {
                        Slog.w("HdmiControlService", "audio system is not available");
                    } else if (!HdmiControlService.this.audioSystem().isSystemAudioActivated()) {
                        Slog.w("HdmiControlService", "audio system is not in system audio mode");
                    } else {
                        HdmiControlService.this.audioSystem().reportAudioStatus(0);
                    }
                }
            });
        }

        public void setSystemAudioModeOnForAudioOnlySource() {
            HdmiControlService.this.initBinderCall();
            HdmiControlService.this.runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.BinderService.24
                @Override // java.lang.Runnable
                public void run() {
                    if (!HdmiControlService.this.isAudioSystemDevice()) {
                        Slog.e("HdmiControlService", "Not an audio system device. Won't set system audio mode on");
                    } else if (HdmiControlService.this.audioSystem() == null) {
                        Slog.e("HdmiControlService", "Audio System local device is not registered");
                    } else if (!HdmiControlService.this.audioSystem().checkSupportAndSetSystemAudioMode(true)) {
                        Slog.e("HdmiControlService", "System Audio Mode is not supported.");
                    } else {
                        HdmiControlService hdmiControlService = HdmiControlService.this;
                        hdmiControlService.sendCecCommand(HdmiCecMessageBuilder.buildSetSystemAudioMode(hdmiControlService.audioSystem().getDeviceInfo().getLogicalAddress(), 15, true));
                    }
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            HdmiControlService.this.initBinderCall();
            new HdmiControlShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(HdmiControlService.this.getContext(), "HdmiControlService", printWriter)) {
                IndentingPrintWriter indentingPrintWriter = new IndentingPrintWriter(printWriter, "  ");
                synchronized (HdmiControlService.this.mLock) {
                    indentingPrintWriter.println("mProhibitMode: " + HdmiControlService.this.mProhibitMode);
                }
                indentingPrintWriter.println("mPowerStatus: " + HdmiControlService.this.mPowerStatusController.getPowerStatus());
                indentingPrintWriter.println("mIsCecAvailable: " + HdmiControlService.this.mIsCecAvailable);
                indentingPrintWriter.println("mCecVersion: " + HdmiControlService.this.mCecVersion);
                indentingPrintWriter.println("mIsAbsoluteVolumeControlEnabled: " + HdmiControlService.this.isAbsoluteVolumeControlEnabled());
                indentingPrintWriter.println("System_settings:");
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.println("mMhlInputChangeEnabled: " + HdmiControlService.this.isMhlInputChangeEnabled());
                indentingPrintWriter.println("mSystemAudioActivated: " + HdmiControlService.this.isSystemAudioActivated());
                indentingPrintWriter.println("mHdmiCecVolumeControlEnabled: " + HdmiControlService.this.getHdmiCecVolumeControl());
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("CEC settings:");
                indentingPrintWriter.increaseIndent();
                HdmiCecConfig hdmiCecConfig = HdmiControlService.this.getHdmiCecConfig();
                List<String> allSettings = hdmiCecConfig.getAllSettings();
                HashSet hashSet = new HashSet(hdmiCecConfig.getUserSettings());
                for (String str : allSettings) {
                    if (hdmiCecConfig.isStringValueType(str)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(str);
                        sb.append(" (string): ");
                        sb.append(hdmiCecConfig.getStringValue(str));
                        sb.append(" (default: ");
                        sb.append(hdmiCecConfig.getDefaultStringValue(str));
                        sb.append(")");
                        sb.append(hashSet.contains(str) ? " [modifiable]" : "");
                        indentingPrintWriter.println(sb.toString());
                    } else if (hdmiCecConfig.isIntValueType(str)) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(str);
                        sb2.append(" (int): ");
                        sb2.append(hdmiCecConfig.getIntValue(str));
                        sb2.append(" (default: ");
                        sb2.append(hdmiCecConfig.getDefaultIntValue(str));
                        sb2.append(")");
                        sb2.append(hashSet.contains(str) ? " [modifiable]" : "");
                        indentingPrintWriter.println(sb2.toString());
                    }
                }
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.println("mMhlController: ");
                indentingPrintWriter.increaseIndent();
                HdmiControlService.this.mMhlController.dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.print("eARC local device: ");
                indentingPrintWriter.increaseIndent();
                if (HdmiControlService.this.mEarcLocalDevice == null) {
                    indentingPrintWriter.println("None. eARC is either disabled or not available.");
                } else {
                    HdmiControlService.this.mEarcLocalDevice.dump(indentingPrintWriter);
                }
                indentingPrintWriter.decreaseIndent();
                HdmiControlService.this.mHdmiCecNetwork.dump(indentingPrintWriter);
                if (HdmiControlService.this.mCecController != null) {
                    indentingPrintWriter.println("mCecController: ");
                    indentingPrintWriter.increaseIndent();
                    HdmiControlService.this.mCecController.dump(indentingPrintWriter);
                    indentingPrintWriter.decreaseIndent();
                }
            }
        }

        public boolean setMessageHistorySize(int i) {
            HdmiControlService.this.enforceAccessPermission();
            if (HdmiControlService.this.mCecController == null) {
                return false;
            }
            return HdmiControlService.this.mCecController.setMessageHistorySize(i);
        }

        public int getMessageHistorySize() {
            HdmiControlService.this.enforceAccessPermission();
            if (HdmiControlService.this.mCecController != null) {
                return HdmiControlService.this.mCecController.getMessageHistorySize();
            }
            return 0;
        }

        public void addCecSettingChangeListener(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.addCecSettingChangeListener(str, iHdmiCecSettingChangeListener);
        }

        public void removeCecSettingChangeListener(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) {
            HdmiControlService.this.enforceAccessPermission();
            HdmiControlService.this.removeCecSettingChangeListener(str, iHdmiCecSettingChangeListener);
        }

        public List<String> getUserCecSettings() {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return HdmiControlService.this.getHdmiCecConfig().getUserSettings();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public List<String> getAllowedCecSettingStringValues(String str) {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return HdmiControlService.this.getHdmiCecConfig().getAllowedStringValues(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int[] getAllowedCecSettingIntValues(String str) {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return HdmiControlService.this.getHdmiCecConfig().getAllowedIntValues(str).stream().mapToInt(new ToIntFunction() { // from class: com.android.server.hdmi.HdmiControlService$BinderService$$ExternalSyntheticLambda0
                    @Override // java.util.function.ToIntFunction
                    public final int applyAsInt(Object obj) {
                        int intValue;
                        intValue = ((Integer) obj).intValue();
                        return intValue;
                    }
                }).toArray();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public String getCecSettingStringValue(String str) {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return HdmiControlService.this.getHdmiCecConfig().getStringValue(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setCecSettingStringValue(String str, String str2) {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                HdmiControlService.this.getHdmiCecConfig().setStringValue(str, str2);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int getCecSettingIntValue(String str) {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return HdmiControlService.this.getHdmiCecConfig().getIntValue(str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public void setCecSettingIntValue(String str, int i) {
            HdmiControlService.this.initBinderCall();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                HdmiControlService.this.getHdmiCecConfig().setIntValue(str, i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    @VisibleForTesting
    public void setHdmiCecVolumeControlEnabledInternal(int i) {
        this.mHdmiCecVolumeControl = i;
        announceHdmiCecVolumeControlFeatureChange(i);
        runOnServiceThread(new HdmiControlService$$ExternalSyntheticLambda2(this));
    }

    public final int getRemoteControlSourceAddress() {
        if (isAudioSystemDevice()) {
            return audioSystem().getDeviceInfo().getLogicalAddress();
        }
        if (isPlaybackDevice()) {
            return playback().getDeviceInfo().getLogicalAddress();
        }
        return 15;
    }

    public final HdmiCecLocalDeviceSource getSwitchDevice() {
        if (isAudioSystemDevice()) {
            return audioSystem();
        }
        if (isPlaybackDevice()) {
            return playback();
        }
        return null;
    }

    @VisibleForTesting
    public void oneTouchPlay(IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        if (!this.mAddressAllocated) {
            this.mOtpCallbackPendingAddressAllocation = iHdmiControlCallback;
            Slog.d("HdmiControlService", "Local device is under address allocation. Save OTP callback for later process.");
            return;
        }
        HdmiCecLocalDeviceSource playback = playback();
        if (playback == null) {
            playback = audioSystem();
        }
        if (playback == null) {
            Slog.w("HdmiControlService", "Local source device not available");
            invokeCallback(iHdmiControlCallback, 2);
            return;
        }
        playback.oneTouchPlay(iHdmiControlCallback);
    }

    @VisibleForTesting
    public void toggleAndFollowTvPower() {
        assertRunOnServiceThread();
        HdmiCecLocalDeviceSource playback = playback();
        if (playback == null) {
            playback = audioSystem();
        }
        if (playback == null) {
            Slog.w("HdmiControlService", "Local source device not available");
        } else {
            playback.toggleAndFollowTvPower();
        }
    }

    @VisibleForTesting
    public boolean shouldHandleTvPowerKey() {
        if (isTvDevice() || getHdmiCecConfig().getStringValue("power_control_mode").equals("none") || getHdmiCecConfig().getIntValue("hdmi_cec_enabled") != 1) {
            return false;
        }
        return this.mIsCecAvailable;
    }

    public void queryDisplayStatus(IHdmiControlCallback iHdmiControlCallback) {
        assertRunOnServiceThread();
        if (!this.mAddressAllocated) {
            this.mDisplayStatusCallback = iHdmiControlCallback;
            Slog.d("HdmiControlService", "Local device is under address allocation. Queue display callback for later process.");
            return;
        }
        HdmiCecLocalDeviceSource playback = playback();
        if (playback == null) {
            playback = audioSystem();
        }
        if (playback == null) {
            Slog.w("HdmiControlService", "Local source device not available");
            invokeCallback(iHdmiControlCallback, -1);
            return;
        }
        playback.queryDisplayStatus(iHdmiControlCallback);
    }

    public HdmiDeviceInfo getActiveSource() {
        int activePath;
        if (playback() != null && playback().isActiveSource()) {
            return playback().getDeviceInfo();
        }
        HdmiCecLocalDevice.ActiveSource localActiveSource = getLocalActiveSource();
        if (localActiveSource.isValid()) {
            HdmiDeviceInfo safeCecDeviceInfo = this.mHdmiCecNetwork.getSafeCecDeviceInfo(localActiveSource.logicalAddress);
            if (safeCecDeviceInfo != null) {
                return safeCecDeviceInfo;
            }
            int i = localActiveSource.physicalAddress;
            return HdmiDeviceInfo.hardwarePort(i, pathToPortId(i));
        } else if (m49tv() == null || (activePath = m49tv().getActivePath()) == 65535) {
            return null;
        } else {
            HdmiDeviceInfo safeDeviceInfoByPath = this.mHdmiCecNetwork.getSafeDeviceInfoByPath(activePath);
            return safeDeviceInfoByPath != null ? safeDeviceInfoByPath : HdmiDeviceInfo.hardwarePort(activePath, m49tv().getActivePortId());
        }
    }

    @VisibleForTesting
    public void addHdmiControlStatusChangeListener(final IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) {
        final HdmiControlStatusChangeListenerRecord hdmiControlStatusChangeListenerRecord = new HdmiControlStatusChangeListenerRecord(iHdmiControlStatusChangeListener);
        try {
            iHdmiControlStatusChangeListener.asBinder().linkToDeath(hdmiControlStatusChangeListenerRecord, 0);
            synchronized (this.mLock) {
                this.mHdmiControlStatusChangeListenerRecords.add(hdmiControlStatusChangeListenerRecord);
            }
            runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.15
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (HdmiControlService.this.mLock) {
                        if (HdmiControlService.this.mHdmiControlStatusChangeListenerRecords.contains(hdmiControlStatusChangeListenerRecord)) {
                            synchronized (HdmiControlService.this.mLock) {
                                HdmiControlService hdmiControlService = HdmiControlService.this;
                                hdmiControlService.invokeHdmiControlStatusChangeListenerLocked(iHdmiControlStatusChangeListener, hdmiControlService.mHdmiControlEnabled);
                            }
                        }
                    }
                }
            });
        } catch (RemoteException unused) {
            Slog.w("HdmiControlService", "Listener already died");
        }
    }

    public final void removeHdmiControlStatusChangeListener(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener) {
        synchronized (this.mLock) {
            Iterator<HdmiControlStatusChangeListenerRecord> it = this.mHdmiControlStatusChangeListenerRecords.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                HdmiControlStatusChangeListenerRecord next = it.next();
                if (next.mListener.asBinder() == iHdmiControlStatusChangeListener.asBinder()) {
                    iHdmiControlStatusChangeListener.asBinder().unlinkToDeath(next, 0);
                    this.mHdmiControlStatusChangeListenerRecords.remove(next);
                    break;
                }
            }
        }
    }

    @VisibleForTesting
    public void addHdmiCecVolumeControlFeatureListener(final IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) {
        this.mHdmiCecVolumeControlFeatureListenerRecords.register(iHdmiCecVolumeControlFeatureListener);
        runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.16
            @Override // java.lang.Runnable
            public void run() {
                synchronized (HdmiControlService.this.mLock) {
                    try {
                        iHdmiCecVolumeControlFeatureListener.onHdmiCecVolumeControlFeature(HdmiControlService.this.mHdmiCecVolumeControl);
                    } catch (RemoteException e) {
                        Slog.e("HdmiControlService", "Failed to report HdmiControlVolumeControlStatusChange: " + HdmiControlService.this.mHdmiCecVolumeControl, e);
                    }
                }
            }
        });
    }

    @VisibleForTesting
    public void removeHdmiControlVolumeControlStatusChangeListener(IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) {
        this.mHdmiCecVolumeControlFeatureListenerRecords.unregister(iHdmiCecVolumeControlFeatureListener);
    }

    public final void addHotplugEventListener(final IHdmiHotplugEventListener iHdmiHotplugEventListener) {
        final HotplugEventListenerRecord hotplugEventListenerRecord = new HotplugEventListenerRecord(iHdmiHotplugEventListener);
        try {
            iHdmiHotplugEventListener.asBinder().linkToDeath(hotplugEventListenerRecord, 0);
            synchronized (this.mLock) {
                this.mHotplugEventListenerRecords.add(hotplugEventListenerRecord);
            }
            runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.17
                @Override // java.lang.Runnable
                public void run() {
                    synchronized (HdmiControlService.this.mLock) {
                        if (HdmiControlService.this.mHotplugEventListenerRecords.contains(hotplugEventListenerRecord)) {
                            for (HdmiPortInfo hdmiPortInfo : HdmiControlService.this.getPortInfo()) {
                                HdmiHotplugEvent hdmiHotplugEvent = new HdmiHotplugEvent(hdmiPortInfo.getId(), HdmiControlService.this.mCecController.isConnected(hdmiPortInfo.getId()));
                                synchronized (HdmiControlService.this.mLock) {
                                    HdmiControlService.this.invokeHotplugEventListenerLocked(iHdmiHotplugEventListener, hdmiHotplugEvent);
                                }
                            }
                        }
                    }
                }
            });
        } catch (RemoteException unused) {
            Slog.w("HdmiControlService", "Listener already died");
        }
    }

    public final void removeHotplugEventListener(IHdmiHotplugEventListener iHdmiHotplugEventListener) {
        synchronized (this.mLock) {
            Iterator<HotplugEventListenerRecord> it = this.mHotplugEventListenerRecords.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                HotplugEventListenerRecord next = it.next();
                if (next.mListener.asBinder() == iHdmiHotplugEventListener.asBinder()) {
                    iHdmiHotplugEventListener.asBinder().unlinkToDeath(next, 0);
                    this.mHotplugEventListenerRecords.remove(next);
                    break;
                }
            }
        }
    }

    public final void addDeviceEventListener(IHdmiDeviceEventListener iHdmiDeviceEventListener) {
        DeviceEventListenerRecord deviceEventListenerRecord = new DeviceEventListenerRecord(iHdmiDeviceEventListener);
        try {
            iHdmiDeviceEventListener.asBinder().linkToDeath(deviceEventListenerRecord, 0);
            synchronized (this.mLock) {
                this.mDeviceEventListenerRecords.add(deviceEventListenerRecord);
            }
        } catch (RemoteException unused) {
            Slog.w("HdmiControlService", "Listener already died");
        }
    }

    public void invokeDeviceEventListeners(HdmiDeviceInfo hdmiDeviceInfo, int i) {
        synchronized (this.mLock) {
            Iterator<DeviceEventListenerRecord> it = this.mDeviceEventListenerRecords.iterator();
            while (it.hasNext()) {
                try {
                    it.next().mListener.onStatusChanged(hdmiDeviceInfo, i);
                } catch (RemoteException e) {
                    Slog.e("HdmiControlService", "Failed to report device event:" + e);
                }
            }
        }
    }

    public final void addSystemAudioModeChangeListner(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) {
        SystemAudioModeChangeListenerRecord systemAudioModeChangeListenerRecord = new SystemAudioModeChangeListenerRecord(iHdmiSystemAudioModeChangeListener);
        try {
            iHdmiSystemAudioModeChangeListener.asBinder().linkToDeath(systemAudioModeChangeListenerRecord, 0);
            synchronized (this.mLock) {
                this.mSystemAudioModeChangeListenerRecords.add(systemAudioModeChangeListenerRecord);
            }
        } catch (RemoteException unused) {
            Slog.w("HdmiControlService", "Listener already died");
        }
    }

    public final void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener) {
        synchronized (this.mLock) {
            Iterator<SystemAudioModeChangeListenerRecord> it = this.mSystemAudioModeChangeListenerRecords.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SystemAudioModeChangeListenerRecord next = it.next();
                if (next.mListener.asBinder() == iHdmiSystemAudioModeChangeListener) {
                    iHdmiSystemAudioModeChangeListener.asBinder().unlinkToDeath(next, 0);
                    this.mSystemAudioModeChangeListenerRecords.remove(next);
                    break;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class InputChangeListenerRecord implements IBinder.DeathRecipient {
        public final IHdmiInputChangeListener mListener;

        public InputChangeListenerRecord(IHdmiInputChangeListener iHdmiInputChangeListener) {
            this.mListener = iHdmiInputChangeListener;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (HdmiControlService.this.mLock) {
                if (HdmiControlService.this.mInputChangeListenerRecord == this) {
                    HdmiControlService.this.mInputChangeListenerRecord = null;
                }
            }
        }
    }

    public final void setInputChangeListener(IHdmiInputChangeListener iHdmiInputChangeListener) {
        synchronized (this.mLock) {
            this.mInputChangeListenerRecord = new InputChangeListenerRecord(iHdmiInputChangeListener);
            try {
                iHdmiInputChangeListener.asBinder().linkToDeath(this.mInputChangeListenerRecord, 0);
            } catch (RemoteException unused) {
                Slog.w("HdmiControlService", "Listener already died");
            }
        }
    }

    public void invokeInputChangeListener(HdmiDeviceInfo hdmiDeviceInfo) {
        synchronized (this.mLock) {
            InputChangeListenerRecord inputChangeListenerRecord = this.mInputChangeListenerRecord;
            if (inputChangeListenerRecord != null) {
                try {
                    inputChangeListenerRecord.mListener.onChanged(hdmiDeviceInfo);
                } catch (RemoteException e) {
                    Slog.w("HdmiControlService", "Exception thrown by IHdmiInputChangeListener: " + e);
                }
            }
        }
    }

    public final void setHdmiRecordListener(IHdmiRecordListener iHdmiRecordListener) {
        synchronized (this.mLock) {
            this.mRecordListenerRecord = new HdmiRecordListenerRecord(iHdmiRecordListener);
            try {
                iHdmiRecordListener.asBinder().linkToDeath(this.mRecordListenerRecord, 0);
            } catch (RemoteException e) {
                Slog.w("HdmiControlService", "Listener already died.", e);
            }
        }
    }

    public byte[] invokeRecordRequestListener(int i) {
        synchronized (this.mLock) {
            HdmiRecordListenerRecord hdmiRecordListenerRecord = this.mRecordListenerRecord;
            if (hdmiRecordListenerRecord != null) {
                try {
                    return hdmiRecordListenerRecord.mListener.getOneTouchRecordSource(i);
                } catch (RemoteException e) {
                    Slog.w("HdmiControlService", "Failed to start record.", e);
                }
            }
            return EmptyArray.BYTE;
        }
    }

    public void invokeOneTouchRecordResult(int i, int i2) {
        synchronized (this.mLock) {
            HdmiRecordListenerRecord hdmiRecordListenerRecord = this.mRecordListenerRecord;
            if (hdmiRecordListenerRecord != null) {
                try {
                    hdmiRecordListenerRecord.mListener.onOneTouchRecordResult(i, i2);
                } catch (RemoteException e) {
                    Slog.w("HdmiControlService", "Failed to call onOneTouchRecordResult.", e);
                }
            }
        }
    }

    public void invokeTimerRecordingResult(int i, int i2) {
        synchronized (this.mLock) {
            HdmiRecordListenerRecord hdmiRecordListenerRecord = this.mRecordListenerRecord;
            if (hdmiRecordListenerRecord != null) {
                try {
                    hdmiRecordListenerRecord.mListener.onTimerRecordingResult(i, i2);
                } catch (RemoteException e) {
                    Slog.w("HdmiControlService", "Failed to call onTimerRecordingResult.", e);
                }
            }
        }
    }

    public void invokeClearTimerRecordingResult(int i, int i2) {
        synchronized (this.mLock) {
            HdmiRecordListenerRecord hdmiRecordListenerRecord = this.mRecordListenerRecord;
            if (hdmiRecordListenerRecord != null) {
                try {
                    hdmiRecordListenerRecord.mListener.onClearTimerRecordingResult(i, i2);
                } catch (RemoteException e) {
                    Slog.w("HdmiControlService", "Failed to call onClearTimerRecordingResult.", e);
                }
            }
        }
    }

    public final void invokeCallback(IHdmiControlCallback iHdmiControlCallback, int i) {
        if (iHdmiControlCallback == null) {
            return;
        }
        try {
            iHdmiControlCallback.onComplete(i);
        } catch (RemoteException e) {
            Slog.e("HdmiControlService", "Invoking callback failed:" + e);
        }
    }

    public final void invokeSystemAudioModeChangeLocked(IHdmiSystemAudioModeChangeListener iHdmiSystemAudioModeChangeListener, boolean z) {
        try {
            iHdmiSystemAudioModeChangeListener.onStatusChanged(z);
        } catch (RemoteException e) {
            Slog.e("HdmiControlService", "Invoking callback failed:" + e);
        }
    }

    public final void announceHotplugEvent(int i, boolean z) {
        HdmiHotplugEvent hdmiHotplugEvent = new HdmiHotplugEvent(i, z);
        synchronized (this.mLock) {
            Iterator<HotplugEventListenerRecord> it = this.mHotplugEventListenerRecords.iterator();
            while (it.hasNext()) {
                invokeHotplugEventListenerLocked(it.next().mListener, hdmiHotplugEvent);
            }
        }
    }

    public final void invokeHotplugEventListenerLocked(IHdmiHotplugEventListener iHdmiHotplugEventListener, HdmiHotplugEvent hdmiHotplugEvent) {
        try {
            iHdmiHotplugEventListener.onReceived(hdmiHotplugEvent);
        } catch (RemoteException e) {
            Slog.e("HdmiControlService", "Failed to report hotplug event:" + hdmiHotplugEvent.toString(), e);
        }
    }

    public final void announceHdmiControlStatusChange(int i) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            ArrayList arrayList = new ArrayList(this.mHdmiControlStatusChangeListenerRecords.size());
            Iterator<HdmiControlStatusChangeListenerRecord> it = this.mHdmiControlStatusChangeListenerRecords.iterator();
            while (it.hasNext()) {
                arrayList.add(it.next().mListener);
            }
            invokeHdmiControlStatusChangeListenerLocked(arrayList, i);
        }
    }

    public final void invokeHdmiControlStatusChangeListenerLocked(IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener, int i) {
        invokeHdmiControlStatusChangeListenerLocked(Collections.singletonList(iHdmiControlStatusChangeListener), i);
    }

    public final void invokeHdmiControlStatusChangeListenerLocked(final Collection<IHdmiControlStatusChangeListener> collection, final int i) {
        if (i == 1) {
            queryDisplayStatus(new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiControlService.18
                public void onComplete(int i2) {
                    HdmiControlService.this.mIsCecAvailable = i2 != -1;
                    if (collection.isEmpty()) {
                        return;
                    }
                    HdmiControlService hdmiControlService = HdmiControlService.this;
                    hdmiControlService.invokeHdmiControlStatusChangeListenerLocked(collection, i, hdmiControlService.mIsCecAvailable);
                }
            });
            return;
        }
        this.mIsCecAvailable = false;
        if (collection.isEmpty()) {
            return;
        }
        invokeHdmiControlStatusChangeListenerLocked(collection, i, this.mIsCecAvailable);
    }

    public final void invokeHdmiControlStatusChangeListenerLocked(Collection<IHdmiControlStatusChangeListener> collection, int i, boolean z) {
        for (IHdmiControlStatusChangeListener iHdmiControlStatusChangeListener : collection) {
            try {
                iHdmiControlStatusChangeListener.onStatusChange(i, z);
            } catch (RemoteException e) {
                Slog.e("HdmiControlService", "Failed to report HdmiControlStatusChange: " + i + " isAvailable: " + z, e);
            }
        }
    }

    public final void announceHdmiCecVolumeControlFeatureChange(final int i) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mHdmiCecVolumeControlFeatureListenerRecords.broadcast(new Consumer() { // from class: com.android.server.hdmi.HdmiControlService$$ExternalSyntheticLambda5
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    HdmiControlService.lambda$announceHdmiCecVolumeControlFeatureChange$2(i, (IHdmiCecVolumeControlFeatureListener) obj);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$announceHdmiCecVolumeControlFeatureChange$2(int i, IHdmiCecVolumeControlFeatureListener iHdmiCecVolumeControlFeatureListener) {
        try {
            iHdmiCecVolumeControlFeatureListener.onHdmiCecVolumeControlFeature(i);
        } catch (RemoteException unused) {
            Slog.e("HdmiControlService", "Failed to report HdmiControlVolumeControlStatusChange: " + i);
        }
    }

    /* renamed from: tv */
    public HdmiCecLocalDeviceTv m49tv() {
        return (HdmiCecLocalDeviceTv) this.mHdmiCecNetwork.getLocalDevice(0);
    }

    public boolean isTvDevice() {
        return this.mCecLocalDevices.contains(0);
    }

    public boolean isAudioSystemDevice() {
        return this.mCecLocalDevices.contains(5);
    }

    public boolean isPlaybackDevice() {
        return this.mCecLocalDevices.contains(4);
    }

    public boolean isTvDeviceEnabled() {
        return isTvDevice() && m49tv() != null;
    }

    public HdmiCecLocalDevicePlayback playback() {
        return (HdmiCecLocalDevicePlayback) this.mHdmiCecNetwork.getLocalDevice(4);
    }

    public HdmiCecLocalDeviceAudioSystem audioSystem() {
        return (HdmiCecLocalDeviceAudioSystem) this.mHdmiCecNetwork.getLocalDevice(5);
    }

    public AudioManager getAudioManager() {
        return this.mAudioManager;
    }

    public final AudioDeviceVolumeManagerWrapperInterface getAudioDeviceVolumeManager() {
        return this.mAudioDeviceVolumeManager;
    }

    public boolean isCecControlEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = true;
            if (this.mHdmiControlEnabled != 1) {
                z = false;
            }
        }
        return z;
    }

    public final boolean isEarcEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mEarcEnabled;
        }
        return z;
    }

    public final boolean isEarcSupported() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mEarcSupported;
        }
        return z;
    }

    public int getPowerStatus() {
        assertRunOnServiceThread();
        return this.mPowerStatusController.getPowerStatus();
    }

    @VisibleForTesting
    public void setPowerStatus(int i) {
        assertRunOnServiceThread();
        this.mPowerStatusController.setPowerStatus(i);
    }

    public boolean isPowerOnOrTransient() {
        assertRunOnServiceThread();
        return this.mPowerStatusController.isPowerStatusOn() || this.mPowerStatusController.isPowerStatusTransientToOn();
    }

    public boolean isPowerStandbyOrTransient() {
        assertRunOnServiceThread();
        return this.mPowerStatusController.isPowerStatusStandby() || this.mPowerStatusController.isPowerStatusTransientToStandby();
    }

    public boolean isPowerStandby() {
        assertRunOnServiceThread();
        return this.mPowerStatusController.isPowerStatusStandby();
    }

    public void wakeUp() {
        assertRunOnServiceThread();
        this.mWakeUpMessageReceived = true;
        this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 8, "android.server.hdmi:WAKE");
    }

    public void standby() {
        assertRunOnServiceThread();
        if (canGoToStandby()) {
            this.mStandbyMessageReceived = true;
            this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 5, 0);
        }
    }

    public boolean isWakeUpMessageReceived() {
        return this.mWakeUpMessageReceived;
    }

    @VisibleForTesting
    public boolean isStandbyMessageReceived() {
        return this.mStandbyMessageReceived;
    }

    @VisibleForTesting
    public void onWakeUp(int i) {
        int i2;
        assertRunOnServiceThread();
        int i3 = 2;
        this.mPowerStatusController.setPowerStatus(2, false);
        if (this.mCecController != null) {
            if (isCecControlEnabled()) {
                if (i == 0) {
                    i2 = this.mWakeUpMessageReceived ? 3 : 2;
                } else if (i != 1) {
                    Slog.e("HdmiControlService", "wakeUpAction " + i + " not defined.");
                    return;
                } else {
                    i2 = 1;
                }
                initializeCec(i2);
            }
        } else {
            Slog.i("HdmiControlService", "Device does not support HDMI-CEC.");
        }
        if (isEarcSupported()) {
            if (isEarcEnabled()) {
                if (i != 0) {
                    if (i != 1) {
                        Slog.e("HdmiControlService", "wakeUpAction " + i + " not defined.");
                        return;
                    }
                    i3 = 1;
                }
                initializeEarc(i3);
                return;
            }
            setEarcEnabledInHal(false, false);
        }
    }

    @VisibleForTesting
    public void onStandby(final int i) {
        this.mWakeUpMessageReceived = false;
        assertRunOnServiceThread();
        this.mPowerStatusController.setPowerStatus(3, false);
        invokeVendorCommandListenersOnControlStateChanged(false, 3);
        final List<HdmiCecLocalDevice> allCecLocalDevices = getAllCecLocalDevices();
        if (!isStandbyMessageReceived() && !canGoToStandby()) {
            this.mPowerStatusController.setPowerStatus(1);
            for (HdmiCecLocalDevice hdmiCecLocalDevice : allCecLocalDevices) {
                hdmiCecLocalDevice.onStandby(this.mStandbyMessageReceived, i);
            }
            return;
        }
        disableCecLocalDevices(new HdmiCecLocalDevice.PendingActionClearedCallback() { // from class: com.android.server.hdmi.HdmiControlService.19
            @Override // com.android.server.hdmi.HdmiCecLocalDevice.PendingActionClearedCallback
            public void onCleared(HdmiCecLocalDevice hdmiCecLocalDevice2) {
                Slog.v("HdmiControlService", "On standby-action cleared:" + hdmiCecLocalDevice2.mDeviceType);
                allCecLocalDevices.remove(hdmiCecLocalDevice2);
                if (allCecLocalDevices.isEmpty()) {
                    HdmiControlService.this.onPendingActionsCleared(i);
                }
            }
        });
    }

    public boolean canGoToStandby() {
        for (HdmiCecLocalDevice hdmiCecLocalDevice : this.mHdmiCecNetwork.getLocalDeviceList()) {
            if (!hdmiCecLocalDevice.canGoToStandby()) {
                return false;
            }
        }
        return true;
    }

    public final void onLanguageChanged(String str) {
        assertRunOnServiceThread();
        this.mMenuLanguage = str;
        if (isTvDeviceEnabled()) {
            m49tv().broadcastMenuLanguage(str);
            this.mCecController.setLanguage(str);
        }
    }

    public String getLanguage() {
        assertRunOnServiceThread();
        return this.mMenuLanguage;
    }

    public final void disableCecLocalDevices(HdmiCecLocalDevice.PendingActionClearedCallback pendingActionClearedCallback) {
        if (this.mCecController != null) {
            for (HdmiCecLocalDevice hdmiCecLocalDevice : this.mHdmiCecNetwork.getLocalDeviceList()) {
                hdmiCecLocalDevice.disableDevice(this.mStandbyMessageReceived, pendingActionClearedCallback);
            }
        }
        this.mMhlController.clearAllLocalDevices();
    }

    @VisibleForTesting
    public void clearCecLocalDevices() {
        assertRunOnServiceThread();
        HdmiCecController hdmiCecController = this.mCecController;
        if (hdmiCecController == null) {
            return;
        }
        hdmiCecController.clearLogicalAddress();
        this.mHdmiCecNetwork.clearLocalDevices();
    }

    public final void onPendingActionsCleared(int i) {
        assertRunOnServiceThread();
        Slog.v("HdmiControlService", "onPendingActionsCleared");
        if (this.mPowerStatusController.isPowerStatusTransientToStandby()) {
            this.mPowerStatusController.setPowerStatus(1);
            for (HdmiCecLocalDevice hdmiCecLocalDevice : this.mHdmiCecNetwork.getLocalDeviceList()) {
                hdmiCecLocalDevice.onStandby(this.mStandbyMessageReceived, i);
            }
            if (!isAudioSystemDevice()) {
                this.mCecController.enableSystemCecControl(false);
                this.mMhlController.setOption(104, 0);
            }
        }
        this.mStandbyMessageReceived = false;
    }

    @VisibleForTesting
    public void addVendorCommandListener(IHdmiVendorCommandListener iHdmiVendorCommandListener, int i) {
        VendorCommandListenerRecord vendorCommandListenerRecord = new VendorCommandListenerRecord(iHdmiVendorCommandListener, i);
        try {
            iHdmiVendorCommandListener.asBinder().linkToDeath(vendorCommandListenerRecord, 0);
            synchronized (this.mLock) {
                this.mVendorCommandListenerRecords.add(vendorCommandListenerRecord);
            }
        } catch (RemoteException unused) {
            Slog.w("HdmiControlService", "Listener already died");
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(7:12|(2:14|(2:16|17))|18|19|21|17|10) */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0045, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0046, code lost:
        android.util.Slog.e("HdmiControlService", "Failed to notify vendor command reception", r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean invokeVendorCommandListenersOnReceived(int i, int i2, int i3, byte[] bArr, boolean z) {
        synchronized (this.mLock) {
            if (this.mVendorCommandListenerRecords.isEmpty()) {
                return false;
            }
            Iterator<VendorCommandListenerRecord> it = this.mVendorCommandListenerRecords.iterator();
            while (it.hasNext()) {
                VendorCommandListenerRecord next = it.next();
                if (z) {
                    if (next.mVendorId != ((bArr[0] & 255) << 16) + ((bArr[1] & 255) << 8) + (bArr[2] & 255)) {
                    }
                }
                next.mListener.onReceived(i2, i3, bArr, z);
            }
            return true;
        }
    }

    public boolean invokeVendorCommandListenersOnControlStateChanged(boolean z, int i) {
        synchronized (this.mLock) {
            if (this.mVendorCommandListenerRecords.isEmpty()) {
                return false;
            }
            Iterator<VendorCommandListenerRecord> it = this.mVendorCommandListenerRecords.iterator();
            while (it.hasNext()) {
                try {
                    it.next().mListener.onControlStateChanged(z, i);
                } catch (RemoteException e) {
                    Slog.e("HdmiControlService", "Failed to notify control-state-changed to vendor handler", e);
                }
            }
            return true;
        }
    }

    public final void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener iHdmiMhlVendorCommandListener) {
        HdmiMhlVendorCommandListenerRecord hdmiMhlVendorCommandListenerRecord = new HdmiMhlVendorCommandListenerRecord(iHdmiMhlVendorCommandListener);
        try {
            iHdmiMhlVendorCommandListener.asBinder().linkToDeath(hdmiMhlVendorCommandListenerRecord, 0);
            synchronized (this.mLock) {
                this.mMhlVendorCommandListenerRecords.add(hdmiMhlVendorCommandListenerRecord);
            }
        } catch (RemoteException unused) {
            Slog.w("HdmiControlService", "Listener already died.");
        }
    }

    public void setStandbyMode(boolean z) {
        assertRunOnServiceThread();
        if (isPowerOnOrTransient() && z) {
            this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 5, 0);
            if (playback() != null) {
                playback().sendStandby(0);
            }
        } else if (!isPowerStandbyOrTransient() || z) {
        } else {
            this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), 8, "android.server.hdmi:WAKE");
            if (playback() != null) {
                oneTouchPlay(new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiControlService.20
                    public void onComplete(int i) {
                        if (i != 0) {
                            Slog.w("HdmiControlService", "Failed to complete 'one touch play'. result=" + i);
                        }
                    }
                });
            }
        }
    }

    public int getHdmiCecVolumeControl() {
        int i;
        synchronized (this.mLock) {
            i = this.mHdmiCecVolumeControl;
        }
        return i;
    }

    public boolean isProhibitMode() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mProhibitMode;
        }
        return z;
    }

    public void setProhibitMode(boolean z) {
        synchronized (this.mLock) {
            this.mProhibitMode = z;
        }
    }

    public boolean isSystemAudioActivated() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mSystemAudioActivated;
        }
        return z;
    }

    public void setSystemAudioActivated(boolean z) {
        synchronized (this.mLock) {
            this.mSystemAudioActivated = z;
        }
        runOnServiceThread(new HdmiControlService$$ExternalSyntheticLambda2(this));
    }

    public void setCecEnabled(int i) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mHdmiControlEnabled = i;
        }
        if (i == 1) {
            onEnableCec();
            setHdmiCecVolumeControlEnabledInternal(getHdmiCecConfig().getIntValue("volume_control_enabled"));
            return;
        }
        setHdmiCecVolumeControlEnabledInternal(0);
        invokeVendorCommandListenersOnControlStateChanged(false, 1);
        runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.21
            @Override // java.lang.Runnable
            public void run() {
                HdmiControlService.this.onDisableCec();
            }
        });
        announceHdmiControlStatusChange(i);
    }

    public final void onEnableCec() {
        this.mCecController.enableCec(true);
        this.mCecController.enableSystemCecControl(true);
        this.mMhlController.setOption(103, 1);
        initializeCec(0);
    }

    public final void onDisableCec() {
        disableCecLocalDevices(new HdmiCecLocalDevice.PendingActionClearedCallback() { // from class: com.android.server.hdmi.HdmiControlService.22
            @Override // com.android.server.hdmi.HdmiCecLocalDevice.PendingActionClearedCallback
            public void onCleared(HdmiCecLocalDevice hdmiCecLocalDevice) {
                HdmiControlService.this.assertRunOnServiceThread();
                HdmiControlService.this.mCecController.flush(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.22.1
                    @Override // java.lang.Runnable
                    public void run() {
                        HdmiControlService.this.mCecController.enableCec(false);
                        HdmiControlService.this.mCecController.enableSystemCecControl(false);
                        HdmiControlService.this.mMhlController.setOption(103, 0);
                        HdmiControlService.this.clearCecLocalDevices();
                    }
                });
            }
        });
    }

    public void setActivePortId(int i) {
        assertRunOnServiceThread();
        this.mActivePortId = i;
        setLastInputForMhl(-1);
    }

    public HdmiCecLocalDevice.ActiveSource getLocalActiveSource() {
        HdmiCecLocalDevice.ActiveSource activeSource;
        synchronized (this.mLock) {
            activeSource = this.mActiveSource;
        }
        return activeSource;
    }

    @VisibleForTesting
    public void pauseActiveMediaSessions() {
        for (MediaController mediaController : ((MediaSessionManager) getContext().getSystemService(MediaSessionManager.class)).getActiveSessions(null)) {
            mediaController.getTransportControls().pause();
        }
    }

    public void setActiveSource(int i, int i2, String str) {
        synchronized (this.mLock) {
            HdmiCecLocalDevice.ActiveSource activeSource = this.mActiveSource;
            activeSource.logicalAddress = i;
            activeSource.physicalAddress = i2;
        }
        getAtomWriter().activeSourceChanged(i, i2, HdmiUtils.pathRelationship(getPhysicalAddress(), i2));
        for (HdmiCecLocalDevice hdmiCecLocalDevice : getAllCecLocalDevices()) {
            hdmiCecLocalDevice.addActiveSourceHistoryItem(new HdmiCecLocalDevice.ActiveSource(i, i2), i == hdmiCecLocalDevice.getDeviceInfo().getLogicalAddress() && i2 == getPhysicalAddress(), str);
        }
        runOnServiceThread(new HdmiControlService$$ExternalSyntheticLambda2(this));
    }

    public void setAndBroadcastActiveSource(int i, int i2, int i3, String str) {
        if (i2 == 4) {
            HdmiCecLocalDevicePlayback playback = playback();
            playback.setActiveSource(playback.getDeviceInfo().getLogicalAddress(), i, str);
            playback.wakeUpIfActiveSource();
            playback.maySendActiveSource(i3);
        }
        if (i2 == 5) {
            HdmiCecLocalDeviceAudioSystem audioSystem = audioSystem();
            if (playback() == null) {
                audioSystem.setActiveSource(audioSystem.getDeviceInfo().getLogicalAddress(), i, str);
                audioSystem.wakeUpIfActiveSource();
                audioSystem.maySendActiveSource(i3);
            }
        }
    }

    public void setAndBroadcastActiveSourceFromOneDeviceType(int i, int i2, String str) {
        HdmiCecLocalDevicePlayback playback = playback();
        HdmiCecLocalDeviceAudioSystem audioSystem = audioSystem();
        if (playback != null) {
            playback.setActiveSource(playback.getDeviceInfo().getLogicalAddress(), i2, str);
            playback.wakeUpIfActiveSource();
            playback.maySendActiveSource(i);
        } else if (audioSystem != null) {
            audioSystem.setActiveSource(audioSystem.getDeviceInfo().getLogicalAddress(), i2, str);
            audioSystem.wakeUpIfActiveSource();
            audioSystem.maySendActiveSource(i);
        }
    }

    public void setLastInputForMhl(int i) {
        assertRunOnServiceThread();
        this.mLastInputMhl = i;
    }

    /* renamed from: com.android.server.hdmi.HdmiControlService$23 */
    /* loaded from: classes.dex */
    public class C092823 extends IHdmiControlCallback.Stub {
        public final /* synthetic */ HdmiControlService this$0;
        public final /* synthetic */ int val$lastInput;

        public void onComplete(int i) throws RemoteException {
            this.this$0.setLastInputForMhl(this.val$lastInput);
        }
    }

    public void setMhlInputChangeEnabled(boolean z) {
        this.mMhlController.setOption(101, toInt(z));
        synchronized (this.mLock) {
            this.mMhlInputChangeEnabled = z;
        }
    }

    @VisibleForTesting
    public HdmiCecAtomWriter getAtomWriter() {
        return this.mAtomWriter;
    }

    public boolean isMhlInputChangeEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mMhlInputChangeEnabled;
        }
        return z;
    }

    public void displayOsd(int i) {
        assertRunOnServiceThread();
        Intent intent = new Intent("android.hardware.hdmi.action.OSD_MESSAGE");
        intent.putExtra("android.hardware.hdmi.extra.MESSAGE_ID", i);
        getContext().sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.HDMI_CEC");
    }

    public void displayOsd(int i, int i2) {
        assertRunOnServiceThread();
        Intent intent = new Intent("android.hardware.hdmi.action.OSD_MESSAGE");
        intent.putExtra("android.hardware.hdmi.extra.MESSAGE_ID", i);
        intent.putExtra("android.hardware.hdmi.extra.MESSAGE_EXTRA_PARAM1", i2);
        getContext().sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.HDMI_CEC");
    }

    @VisibleForTesting
    public HdmiCecConfig getHdmiCecConfig() {
        return this.mHdmiCecConfig;
    }

    /* renamed from: com.android.server.hdmi.HdmiControlService$24 */
    /* loaded from: classes.dex */
    public class C092924 implements HdmiCecConfig.SettingChangeListener {
        public C092924() {
        }

        @Override // com.android.server.hdmi.HdmiCecConfig.SettingChangeListener
        public void onChange(final String str) {
            synchronized (HdmiControlService.this.mLock) {
                if (HdmiControlService.this.mHdmiCecSettingChangeListenerRecords.containsKey(str)) {
                    ((RemoteCallbackList) HdmiControlService.this.mHdmiCecSettingChangeListenerRecords.get(str)).broadcast(new Consumer() { // from class: com.android.server.hdmi.HdmiControlService$24$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            HdmiControlService.C092924.this.lambda$onChange$0(str, (IHdmiCecSettingChangeListener) obj);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onChange$0(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) {
            HdmiControlService.this.invokeCecSettingChangeListenerLocked(str, iHdmiCecSettingChangeListener);
        }
    }

    public final void addCecSettingChangeListener(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) {
        synchronized (this.mLock) {
            if (!this.mHdmiCecSettingChangeListenerRecords.containsKey(str)) {
                this.mHdmiCecSettingChangeListenerRecords.put(str, new RemoteCallbackList<>());
                this.mHdmiCecConfig.registerChangeListener(str, this.mSettingChangeListener);
            }
            this.mHdmiCecSettingChangeListenerRecords.get(str).register(iHdmiCecSettingChangeListener);
        }
    }

    public final void removeCecSettingChangeListener(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) {
        synchronized (this.mLock) {
            if (this.mHdmiCecSettingChangeListenerRecords.containsKey(str)) {
                this.mHdmiCecSettingChangeListenerRecords.get(str).unregister(iHdmiCecSettingChangeListener);
                if (this.mHdmiCecSettingChangeListenerRecords.get(str).getRegisteredCallbackCount() == 0) {
                    this.mHdmiCecSettingChangeListenerRecords.remove(str);
                    this.mHdmiCecConfig.removeChangeListener(str, this.mSettingChangeListener);
                }
            }
        }
    }

    public final void invokeCecSettingChangeListenerLocked(String str, IHdmiCecSettingChangeListener iHdmiCecSettingChangeListener) {
        try {
            iHdmiCecSettingChangeListener.onChange(str);
        } catch (RemoteException e) {
            Slog.e("HdmiControlService", "Failed to report setting change", e);
        }
    }

    @VisibleForTesting
    public void onDeviceVolumeBehaviorChanged(AudioDeviceAttributes audioDeviceAttributes, int i) {
        assertRunOnServiceThread();
        if (AVC_AUDIO_OUTPUT_DEVICES.contains(audioDeviceAttributes)) {
            synchronized (this.mLock) {
                this.mAudioDeviceVolumeBehaviors.put(audioDeviceAttributes, Integer.valueOf(i));
            }
            checkAndUpdateAbsoluteVolumeControlState();
        }
    }

    public final int getDeviceVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes) {
        if (AVC_AUDIO_OUTPUT_DEVICES.contains(audioDeviceAttributes)) {
            synchronized (this.mLock) {
                if (this.mAudioDeviceVolumeBehaviors.containsKey(audioDeviceAttributes)) {
                    return this.mAudioDeviceVolumeBehaviors.get(audioDeviceAttributes).intValue();
                }
            }
        }
        return getAudioManager().getDeviceVolumeBehavior(audioDeviceAttributes);
    }

    public boolean isAbsoluteVolumeControlEnabled() {
        AudioDeviceAttributes avcAudioOutputDevice;
        return (isTvDevice() || isPlaybackDevice()) && (avcAudioOutputDevice = getAvcAudioOutputDevice()) != null && getDeviceVolumeBehavior(avcAudioOutputDevice) == 3;
    }

    public final AudioDeviceAttributes getAvcAudioOutputDevice() {
        if (isTvDevice()) {
            return m49tv().getSystemAudioOutputDevice();
        }
        if (isPlaybackDevice()) {
            return AUDIO_OUTPUT_DEVICE_HDMI;
        }
        return null;
    }

    public void checkAndUpdateAbsoluteVolumeControlState() {
        HdmiCecLocalDevice playback;
        assertRunOnServiceThread();
        if (getAudioManager() == null) {
            return;
        }
        if (isTvDevice() && m49tv() != null) {
            playback = m49tv();
            if (!isSystemAudioActivated()) {
                disableAbsoluteVolumeControl();
                return;
            }
        } else if (!isPlaybackDevice() || playback() == null) {
            return;
        } else {
            playback = playback();
        }
        HdmiDeviceInfo safeCecDeviceInfo = getHdmiCecNetwork().getSafeCecDeviceInfo(playback.findAudioReceiverAddress());
        int deviceVolumeBehavior = getDeviceVolumeBehavior(getAvcAudioOutputDevice());
        boolean z = deviceVolumeBehavior == 1 || deviceVolumeBehavior == 3;
        if (!(getHdmiCecVolumeControl() == 1) || !z) {
            disableAbsoluteVolumeControl();
        } else if (safeCecDeviceInfo == null) {
            disableAbsoluteVolumeControl();
        } else {
            int setAudioVolumeLevelSupport = safeCecDeviceInfo.getDeviceFeatures().getSetAudioVolumeLevelSupport();
            if (setAudioVolumeLevelSupport == 0) {
                disableAbsoluteVolumeControl();
            } else if (setAudioVolumeLevelSupport == 1) {
                if (isAbsoluteVolumeControlEnabled()) {
                    return;
                }
                playback.addAvcAudioStatusAction(safeCecDeviceInfo.getLogicalAddress());
            } else if (setAudioVolumeLevelSupport != 2) {
            } else {
                disableAbsoluteVolumeControl();
                playback.queryAvcSupport(safeCecDeviceInfo.getLogicalAddress());
            }
        }
    }

    public final void disableAbsoluteVolumeControl() {
        if (isPlaybackDevice()) {
            playback().removeAvcAudioStatusAction();
        } else if (isTvDevice()) {
            m49tv().removeAvcAudioStatusAction();
        }
        AudioDeviceAttributes avcAudioOutputDevice = getAvcAudioOutputDevice();
        if (getDeviceVolumeBehavior(avcAudioOutputDevice) == 3) {
            getAudioManager().setDeviceVolumeBehavior(avcAudioOutputDevice, 1);
        }
    }

    public void enableAbsoluteVolumeControl(AudioStatus audioStatus) {
        HdmiCecLocalDevice playback = isPlaybackDevice() ? playback() : m49tv();
        HdmiDeviceInfo deviceInfo = getHdmiCecNetwork().getDeviceInfo(playback.findAudioReceiverAddress());
        VolumeInfo build = new VolumeInfo.Builder(3).setMuted(audioStatus.getMute()).setVolumeIndex(audioStatus.getVolume()).setMaxVolumeIndex(100).setMinVolumeIndex(0).build();
        this.mAbsoluteVolumeChangedListener = new AbsoluteVolumeChangedListener(playback, deviceInfo);
        notifyAvcMuteChange(audioStatus.getMute());
        getAudioDeviceVolumeManager().setDeviceAbsoluteVolumeBehavior(getAvcAudioOutputDevice(), build, this.mServiceThreadExecutor, this.mAbsoluteVolumeChangedListener, true);
    }

    @VisibleForTesting
    public AbsoluteVolumeChangedListener getAbsoluteVolumeChangedListener() {
        return this.mAbsoluteVolumeChangedListener;
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public class AbsoluteVolumeChangedListener implements AudioDeviceVolumeManager.OnAudioDeviceVolumeChangedListener {
        public HdmiCecLocalDevice mLocalDevice;
        public HdmiDeviceInfo mSystemAudioDevice;

        public AbsoluteVolumeChangedListener(HdmiCecLocalDevice hdmiCecLocalDevice, HdmiDeviceInfo hdmiDeviceInfo) {
            this.mLocalDevice = hdmiCecLocalDevice;
            this.mSystemAudioDevice = hdmiDeviceInfo;
        }

        public void onAudioDeviceVolumeChanged(AudioDeviceAttributes audioDeviceAttributes, final VolumeInfo volumeInfo) {
            final int logicalAddress = this.mLocalDevice.getDeviceInfo().getLogicalAddress();
            HdmiControlService.this.sendCecCommand(SetAudioVolumeLevelMessage.build(logicalAddress, this.mSystemAudioDevice.getLogicalAddress(), volumeInfo.getVolumeIndex()), new SendMessageCallback() { // from class: com.android.server.hdmi.HdmiControlService$AbsoluteVolumeChangedListener$$ExternalSyntheticLambda0
                @Override // com.android.server.hdmi.HdmiControlService.SendMessageCallback
                public final void onSendCompleted(int i) {
                    HdmiControlService.AbsoluteVolumeChangedListener.this.lambda$onAudioDeviceVolumeChanged$0(volumeInfo, logicalAddress, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAudioDeviceVolumeChanged$0(VolumeInfo volumeInfo, int i, int i2) {
            if (i2 == 0) {
                boolean isTvDevice = HdmiControlService.this.isTvDevice();
                HdmiControlService hdmiControlService = HdmiControlService.this;
                (isTvDevice ? hdmiControlService.m49tv() : hdmiControlService.playback()).updateAvcVolume(volumeInfo.getVolumeIndex());
                return;
            }
            HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildGiveAudioStatus(i, this.mSystemAudioDevice.getLogicalAddress()));
        }

        /* JADX WARN: Removed duplicated region for block: B:17:0x001e  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0030  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onAudioDeviceVolumeAdjusted(AudioDeviceAttributes audioDeviceAttributes, VolumeInfo volumeInfo, int i, int i2) {
            int i3;
            if (i != -100) {
                if (i == -1) {
                    i3 = 25;
                } else if (i == 1) {
                    i3 = 24;
                } else if (i != 100 && i != 101) {
                    return;
                }
                if (i2 != 0) {
                    this.mLocalDevice.sendVolumeKeyEvent(i3, true);
                    this.mLocalDevice.sendVolumeKeyEvent(i3, false);
                    return;
                } else if (i2 == 1) {
                    this.mLocalDevice.sendVolumeKeyEvent(i3, true);
                    return;
                } else if (i2 != 2) {
                    return;
                } else {
                    this.mLocalDevice.sendVolumeKeyEvent(i3, false);
                    return;
                }
            }
            i3 = FrameworkStatsLog.f376xd07885aa;
            if (i2 != 0) {
            }
        }
    }

    public void notifyAvcVolumeChange(int i) {
        if (isAbsoluteVolumeControlEnabled() && getAudioManager().getDevicesForAttributes(STREAM_MUSIC_ATTRIBUTES).contains(getAvcAudioOutputDevice())) {
            setStreamMusicVolume(i, isTvDevice() ? 8193 : IInstalld.FLAG_FORCE);
        }
    }

    public void notifyAvcMuteChange(boolean z) {
        if (isAbsoluteVolumeControlEnabled() && getAudioManager().getDevicesForAttributes(STREAM_MUSIC_ATTRIBUTES).contains(getAvcAudioOutputDevice())) {
            getAudioManager().adjustStreamVolume(3, z ? -100 : 100, isTvDevice() ? 8193 : IInstalld.FLAG_FORCE);
        }
    }

    public void setStreamMusicVolume(int i, int i2) {
        getAudioManager().setStreamVolume(3, (i * this.mStreamMusicMaxVolume) / 100, i2);
    }

    public final void initializeEarc(int i) {
        Slog.i("HdmiControlService", "eARC initialized, reason = " + i);
        initializeEarcLocalDevice(i);
        if (i == 6) {
            setEarcEnabledInHal(true, true);
        } else {
            setEarcEnabledInHal(true, false);
        }
    }

    @VisibleForTesting
    public void initializeEarcLocalDevice(int i) {
        assertRunOnServiceThread();
        if (this.mEarcLocalDevice == null) {
            this.mEarcLocalDevice = HdmiEarcLocalDevice.create(this, 0);
        }
    }

    @VisibleForTesting
    public void setEarcEnabled(int i) {
        assertRunOnServiceThread();
        synchronized (this.mLock) {
            this.mEarcEnabled = i == 1;
            if (!isEarcSupported()) {
                Slog.i("HdmiControlService", "Enabled/disabled eARC setting, but the hardware doesn´t support eARC. This settings change doesn´t have an effect.");
            } else if (this.mEarcEnabled) {
                onEnableEarc();
            } else {
                runOnServiceThread(new Runnable() { // from class: com.android.server.hdmi.HdmiControlService.25
                    @Override // java.lang.Runnable
                    public void run() {
                        HdmiControlService.this.onDisableEarc();
                    }
                });
            }
        }
    }

    @VisibleForTesting
    public void setEarcSupported(boolean z) {
        synchronized (this.mLock) {
            this.mEarcSupported = z;
        }
    }

    public final void onEnableEarc() {
        initializeEarc(6);
    }

    public final void onDisableEarc() {
        disableEarcLocalDevice();
        setEarcEnabledInHal(false, false);
        clearEarcLocalDevice();
    }

    @VisibleForTesting
    public void clearEarcLocalDevice() {
        assertRunOnServiceThread();
        this.mEarcLocalDevice = null;
    }

    @VisibleForTesting
    public void addEarcLocalDevice(HdmiEarcLocalDevice hdmiEarcLocalDevice) {
        assertRunOnServiceThread();
        this.mEarcLocalDevice = hdmiEarcLocalDevice;
    }

    @VisibleForTesting
    public HdmiEarcLocalDevice getEarcLocalDevice() {
        assertRunOnServiceThread();
        return this.mEarcLocalDevice;
    }

    public final void disableEarcLocalDevice() {
        HdmiEarcLocalDevice hdmiEarcLocalDevice = this.mEarcLocalDevice;
        if (hdmiEarcLocalDevice == null) {
            return;
        }
        hdmiEarcLocalDevice.disableDevice();
    }

    @VisibleForTesting
    public void setEarcEnabledInHal(final boolean z, boolean z2) {
        assertRunOnServiceThread();
        if (z2) {
            startArcAction(false, new IHdmiControlCallback.Stub() { // from class: com.android.server.hdmi.HdmiControlService.26
                public void onComplete(int i) throws RemoteException {
                    if (i != 0) {
                        Slog.w("HdmiControlService", "ARC termination before enabling eARC in the HAL failed with result: " + i);
                    }
                    HdmiControlService.this.mEarcController.setEarcEnabled(z);
                    HdmiCecController hdmiCecController = HdmiControlService.this.mCecController;
                    boolean z3 = z;
                    hdmiCecController.setHpdSignalType(z3 ? 1 : 0, HdmiControlService.this.mEarcPortId);
                }
            });
            return;
        }
        this.mEarcController.setEarcEnabled(z);
        this.mCecController.setHpdSignalType(z ? 1 : 0, this.mEarcPortId);
    }

    public void handleEarcStateChange(int i, int i2) {
        assertRunOnServiceThread();
        if (!getPortInfo(i2).isEarcSupported()) {
            Slog.w("HdmiControlService", "Tried to update eARC status on a port that doesn't support eARC.");
            return;
        }
        HdmiEarcLocalDevice hdmiEarcLocalDevice = this.mEarcLocalDevice;
        if (hdmiEarcLocalDevice != null) {
            hdmiEarcLocalDevice.handleEarcStateChange(i);
        }
    }

    public void handleEarcCapabilitiesReported(byte[] bArr, int i) {
        assertRunOnServiceThread();
        if (!getPortInfo(i).isEarcSupported()) {
            Slog.w("HdmiControlService", "Tried to process eARC capabilities from a port that doesn't support eARC.");
            return;
        }
        HdmiEarcLocalDevice hdmiEarcLocalDevice = this.mEarcLocalDevice;
        if (hdmiEarcLocalDevice != null) {
            hdmiEarcLocalDevice.handleEarcCapabilitiesReported(bArr);
        }
    }

    public boolean earcBlocksArcConnection() {
        boolean z;
        if (this.mEarcLocalDevice == null) {
            return false;
        }
        synchronized (this.mLock) {
            z = this.mEarcLocalDevice.mEarcStatus != 2;
        }
        return z;
    }

    public void startArcAction(boolean z, IHdmiControlCallback iHdmiControlCallback) {
        if (!isTvDeviceEnabled()) {
            invokeCallback(iHdmiControlCallback, 6);
        } else {
            m49tv().startArcAction(z, iHdmiControlCallback);
        }
    }
}
