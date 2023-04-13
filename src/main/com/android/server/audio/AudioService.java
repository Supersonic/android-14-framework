package com.android.server.audio;

import android.annotation.EnforcePermission;
import android.annotation.RequiresPermission;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.app.IUidObserver;
import android.app.NotificationManager;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorPrivacyManager;
import android.hardware.SensorPrivacyManagerInternal;
import android.hardware.hdmi.HdmiAudioSystemClient;
import android.hardware.hdmi.HdmiClient;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiPlaybackClient;
import android.hardware.hdmi.HdmiTvClient;
import android.hardware.input.InputManager;
import android.hidl.manager.V1_0.IServiceManager;
import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioDeviceVolumeManager;
import android.media.AudioFocusInfo;
import android.media.AudioFormat;
import android.media.AudioHalVersionInfo;
import android.media.AudioManager;
import android.media.AudioManagerInternal;
import android.media.AudioMixerAttributes;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioRecordingConfiguration;
import android.media.AudioRoutesInfo;
import android.media.AudioSystem;
import android.media.BluetoothProfileConnectionInfo;
import android.media.IAudioDeviceVolumeDispatcher;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioModeDispatcher;
import android.media.IAudioRoutesObserver;
import android.media.IAudioServerStateDispatcher;
import android.media.IAudioService;
import android.media.ICapturePresetDevicesRoleDispatcher;
import android.media.ICommunicationDeviceDispatcher;
import android.media.IDeviceVolumeBehaviorDispatcher;
import android.media.IDevicesForAttributesCallback;
import android.media.IMuteAwaitConnectionCallback;
import android.media.IPlaybackConfigDispatcher;
import android.media.IPreferredMixerAttributesDispatcher;
import android.media.IRecordingConfigDispatcher;
import android.media.IRingtonePlayer;
import android.media.ISpatializerCallback;
import android.media.ISpatializerHeadToSoundStagePoseCallback;
import android.media.ISpatializerHeadTrackerAvailableCallback;
import android.media.ISpatializerHeadTrackingModeCallback;
import android.media.ISpatializerOutputCallback;
import android.media.IStrategyNonDefaultDevicesDispatcher;
import android.media.IStrategyPreferredDevicesDispatcher;
import android.media.IStreamAliasingDispatcher;
import android.media.IVolumeController;
import android.media.MediaMetrics;
import android.media.PlayerBase;
import android.media.Spatializer;
import android.media.VolumeInfo;
import android.media.VolumePolicy;
import android.media.audiopolicy.AudioMix;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.AudioProductStrategy;
import android.media.audiopolicy.AudioVolumeGroup;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.media.permission.ClearCallingIdentityContext;
import android.media.permission.SafeCloseable;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionCallback;
import android.media.projection.IMediaProjectionManager;
import android.net.INetd;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HwBinder;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PermissionEnforcer;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.p005os.IInstalld;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.SomeArgs;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.UiModeManagerService$13$$ExternalSyntheticLambda1;
import com.android.server.audio.AudioDeviceBroker;
import com.android.server.audio.AudioService;
import com.android.server.audio.AudioSystemAdapter;
import com.android.server.audio.SoundEffectsHelper;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.utils.EventLogger;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class AudioService extends IAudioService.Stub implements AccessibilityManager.TouchExplorationStateChangeListener, AccessibilityManager.AccessibilityServicesStateChangeListener, AudioSystemAdapter.OnRoutingUpdatedListener, AudioSystemAdapter.OnVolRangeInitRequestListener {
    @VisibleForTesting
    public static final int BECOMING_NOISY_DELAY_MS = 1000;
    public static final Set<Integer> DEVICE_MEDIA_UNMUTED_ON_PLUG_SET;
    public static final String[] RINGER_MODE_NAMES;
    public static int[] mStreamVolumeAlias;
    public static VolumeInfo sDefaultVolumeInfo;
    public static final EventLogger sDeviceLogger;
    public static final EventLogger sForceUseLogger;
    public static boolean sIndependentA11yVolume;
    public static final EventLogger sLifecycleLogger;
    public static final EventLogger sSpatialLogger;
    public static int sStreamOverrideDelayMs;
    public static final SparseArray<VolumeGroupState> sVolumeGroupStates;
    public static final EventLogger sVolumeLogger;
    public final int[] STREAM_VOLUME_ALIAS_DEFAULT;
    public final int[] STREAM_VOLUME_ALIAS_NONE;
    public final int[] STREAM_VOLUME_ALIAS_TELEVISION;
    public final int[] STREAM_VOLUME_ALIAS_VOICE;
    public Set<Integer> mAbsVolumeMultiModeCaseDevices;
    public Map<Integer, AbsoluteVolumeDeviceInfo> mAbsoluteVolumeDeviceInfoMap;
    @GuardedBy({"mAccessibilityServiceUidsLock"})
    public int[] mAccessibilityServiceUids;
    public final Object mAccessibilityServiceUidsLock;
    @GuardedBy({"mSettingsLock"})
    public int[] mActiveAssistantServiceUids;
    public final ActivityManagerInternal mActivityManagerInternal;
    public final AppOpsManager mAppOps;
    @GuardedBy({"mSettingsLock"})
    public final ArraySet<Integer> mAssistantUids;
    public PowerManager.WakeLock mAudioEventWakeLock;
    public AudioHandler mAudioHandler;
    public final HashMap<IBinder, AudioPolicyProxy> mAudioPolicies;
    public final AudioPolicyFacade mAudioPolicy;
    @GuardedBy({"mAudioPolicies"})
    public int mAudioPolicyCounter;
    public final HashMap<IBinder, AsdProxy> mAudioServerStateListeners;
    public final AudioSystemAdapter mAudioSystem;
    public final AudioSystem.ErrorCallback mAudioSystemCallback;
    public AudioSystemThread mAudioSystemThread;
    public volatile boolean mAvrcpAbsVolSupported;
    public boolean mBtScoOnByApp;
    @GuardedBy({"mSettingsLock"})
    public boolean mCameraSoundForced;
    public final ContentResolver mContentResolver;
    public final Context mContext;
    public final AudioDeviceBroker mDeviceBroker;
    public final RemoteCallbackList<IDeviceVolumeBehaviorDispatcher> mDeviceVolumeBehaviorDispatchers;
    public boolean mDockAudioMediaEnabled;
    public int mDockState;
    public final AudioSystem.DynamicPolicyCallback mDynPolicyCallback;
    public final EventLogger mDynPolicyLogger;
    public String mEnabledSurroundFormats;
    public int mEncodedSurroundMode;
    public IAudioPolicyCallback mExtVolumeController;
    public final Object mExtVolumeControllerLock;
    public Set<Integer> mFixedVolumeDevices;
    public ForceControlStreamClient mForceControlStreamClient;
    public final Object mForceControlStreamLock;
    public Set<Integer> mFullVolumeDevices;
    public final boolean mHasSpatializerEffect;
    public final boolean mHasVibrator;
    @GuardedBy({"mHdmiClientLock"})
    public HdmiAudioSystemClient mHdmiAudioSystemClient;
    @GuardedBy({"mHdmiClientLock"})
    public boolean mHdmiCecVolumeControlEnabled;
    public final Object mHdmiClientLock;
    public MyHdmiControlStatusChangeListenerCallback mHdmiControlStatusChangeListenerCallback;
    @GuardedBy({"mHdmiClientLock"})
    public HdmiControlManager mHdmiManager;
    @GuardedBy({"mHdmiClientLock"})
    public HdmiPlaybackClient mHdmiPlaybackClient;
    public boolean mHdmiSystemAudioSupported;
    @GuardedBy({"mHdmiClientLock"})
    public HdmiTvClient mHdmiTvClient;
    public boolean mHomeSoundEffectEnabled;
    public int mInputMethodServiceUid;
    public final Object mInputMethodServiceUidLock;
    public boolean mIsCallScreeningModeSupported;
    public final boolean mIsSingleVolume;
    public long mLoweredFromNormalToVibrateTime;
    public final MediaFocusControl mMediaFocusControl;
    public AtomicBoolean mMediaPlaybackActive;
    public boolean mMicMuteFromApi;
    public boolean mMicMuteFromPrivacyToggle;
    public boolean mMicMuteFromRestrictions;
    public boolean mMicMuteFromSwitch;
    public boolean mMicMuteFromSystemCached;
    public AtomicInteger mMode;
    public final RemoteCallbackList<IAudioModeDispatcher> mModeDispatchers;
    public final EventLogger mModeLogger;
    public final boolean mMonitorRotation;
    public int mMuteAffectedStreams;
    public final RemoteCallbackList<IMuteAwaitConnectionCallback> mMuteAwaitConnectionDispatchers;
    public final Object mMuteAwaitConnectionLock;
    @GuardedBy({"mMuteAwaitConnectionLock"})
    public int[] mMutedUsagesAwaitingConnection;
    @GuardedBy({"mMuteAwaitConnectionLock"})
    public AudioDeviceAttributes mMutingExpectedDevice;
    public MyHdmiCecVolumeControlFeatureListener mMyHdmiCecVolumeControlFeatureListener;
    public boolean mNavigationRepeatSoundEffectsEnabled;
    public NotificationManager mNm;
    public boolean mNotifAliasRing;
    public final int mPlatformType;
    public final IPlaybackConfigDispatcher mPlaybackActivityMonitor;
    public final PlaybackActivityMonitor mPlaybackMonitor;
    public final RemoteCallbackList<IPreferredMixerAttributesDispatcher> mPrefMixerAttrDispatcher;
    public float[] mPrescaleAbsoluteVolume;
    public int mPrevVolDirection;
    @GuardedBy({"mSettingsLock"})
    public int mPrimaryAssistantUid;
    public IMediaProjectionManager mProjectionService;
    public final BroadcastReceiver mReceiver;
    public final RecordingActivityMonitor mRecordMonitor;
    public RestorableParameters mRestorableParameters;
    public int mRingerAndZenModeMutedStreams;
    @GuardedBy({"mSettingsLock"})
    public int mRingerMode;
    public int mRingerModeAffectedStreams;
    public AudioManagerInternal.RingerModeDelegate mRingerModeDelegate;
    @GuardedBy({"mSettingsLock"})
    public int mRingerModeExternal;
    public volatile IRingtonePlayer mRingtonePlayer;
    public final ArrayList<RmtSbmxFullVolDeathHandler> mRmtSbmxFullVolDeathHandlers;
    public int mRmtSbmxFullVolRefCount;
    public RoleObserver mRoleObserver;
    @GuardedBy({"mSettingsLock"})
    public boolean mRttEnabled;
    public final SensorPrivacyManagerInternal mSensorPrivacyManagerInternal;
    @GuardedBy({"mDeviceBroker.mSetModeLock"})
    public final ArrayList<SetModeDeathHandler> mSetModeDeathHandlers;
    public final SettingsAdapter mSettings;
    public final Object mSettingsLock;
    public SettingsObserver mSettingsObserver;
    public SoundEffectsHelper mSfxHelper;
    public final SoundDoseHelper mSoundDoseHelper;
    public final SpatializerHelper mSpatializerHelper;
    public final RemoteCallbackList<IStreamAliasingDispatcher> mStreamAliasingDispatchers;
    public VolumeStreamState[] mStreamStates;
    @GuardedBy({"mSupportedSystemUsagesLock"})
    public int[] mSupportedSystemUsages;
    public final Object mSupportedSystemUsagesLock;
    public boolean mSupportsMicPrivacyToggle;
    public boolean mSurroundModeChanged;
    public boolean mSystemReady;
    public final SystemServerAdapter mSystemServer;
    public final IUidObserver mUidObserver;
    public final boolean mUseFixedVolume;
    public final boolean mUseVolumeGroupAliases;
    public final UserManagerInternal mUserManagerInternal;
    public final UserManagerInternal.UserRestrictionsListener mUserRestrictionsListener;
    public boolean mUserSelectedVolumeControlStream;
    public boolean mUserSwitchedReceived;
    public int mVibrateSetting;
    public Vibrator mVibrator;
    public AtomicBoolean mVoicePlaybackActive;
    public final IRecordingConfigDispatcher mVoiceRecordingActivityMonitor;
    public int mVolumeControlStream;
    public final VolumeController mVolumeController;
    public VolumePolicy mVolumePolicy;
    public int mZenModeAffectedStreams;
    public static final int[] NO_ACTIVE_ASSISTANT_SERVICE_UIDS = new int[0];
    public static int[] MAX_STREAM_VOLUME = {5, 7, 7, 15, 7, 7, 15, 7, 15, 15, 15, 15};
    public static int[] MIN_STREAM_VOLUME = {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    public static final int[] STREAM_VOLUME_OPS = {34, 36, 35, 36, 37, 38, 39, 36, 36, 36, 64, 36};
    public static final VibrationAttributes TOUCH_VIBRATION_ATTRIBUTES = VibrationAttributes.createForUsage(18);

    /* loaded from: classes.dex */
    public interface ISafeHearingVolumeController {
        void postDisplayCsdWarning(int i, int i2);

        void postDisplaySafeVolumeWarning(int i);
    }

    public static boolean isCallStream(int i) {
        return i == 0 || i == 6;
    }

    public final boolean isAlarm(int i) {
        return i == 4;
    }

    public final boolean isMedia(int i) {
        return i == 3;
    }

    public final boolean isMuteAdjust(int i) {
        return i == -100 || i == 100 || i == 101;
    }

    public final boolean isNotificationOrRinger(int i) {
        return i == 5 || i == 2;
    }

    public final boolean isSystem(int i) {
        return i == 1;
    }

    public boolean isValidRingerMode(int i) {
        return i >= 0 && i <= 2;
    }

    public final int toEncodedSurroundOutputMode(int i, int i2) {
        if (i2 > 31 || i <= 3) {
            if (i != 0) {
                int i3 = 1;
                if (i != 1) {
                    i3 = 2;
                    if (i != 2) {
                        return i != 3 ? -1 : 3;
                    }
                }
                return i3;
            }
            return 0;
        }
        return -1;
    }

    public final int toEncodedSurroundSetting(int i) {
        if (i != 1) {
            if (i != 2) {
                return i != 3 ? 0 : 3;
            }
            return 2;
        }
        return 1;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public void setNotifAliasRingForTest(boolean z) {
        super.setNotifAliasRingForTest_enforcePermission();
        boolean z2 = this.mNotifAliasRing != z;
        this.mNotifAliasRing = z;
        if (z2) {
            updateStreamVolumeAlias(true, "AudioServiceTest");
        }
    }

    public boolean isPlatformVoice() {
        return this.mPlatformType == 1;
    }

    public boolean isPlatformTelevision() {
        return this.mPlatformType == 2;
    }

    public boolean isPlatformAutomotive() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }

    static {
        HashSet hashSet = new HashSet();
        DEVICE_MEDIA_UNMUTED_ON_PLUG_SET = hashSet;
        hashSet.add(4);
        hashSet.add(8);
        hashSet.add(Integer.valueOf((int) IInstalld.FLAG_CLEAR_APP_DATA_KEEP_ART_PROFILES));
        hashSet.addAll(AudioSystem.DEVICE_OUT_ALL_A2DP_SET);
        hashSet.addAll(AudioSystem.DEVICE_OUT_ALL_USB_SET);
        hashSet.add(1024);
        sVolumeGroupStates = new SparseArray<>();
        sIndependentA11yVolume = false;
        sLifecycleLogger = new EventLogger(20, "audio services lifecycle");
        sDeviceLogger = new EventLogger(50, "wired/A2DP/hearing aid device connection");
        sForceUseLogger = new EventLogger(20, "force use (logged before setForceUse() is executed)");
        sVolumeLogger = new EventLogger(40, "volume changes (logged when command received by AudioService)");
        sSpatialLogger = new EventLogger(30, "spatial audio");
        RINGER_MODE_NAMES = new String[]{"SILENT", "VIBRATE", "NORMAL"};
    }

    public int getVssVolumeForDevice(int i, int i2) {
        return this.mStreamStates[i].getIndex(i2);
    }

    public VolumeStreamState getVssVolumeForStream(int i) {
        return this.mStreamStates[i];
    }

    public int getMaxVssVolumeForStream(int i) {
        return this.mStreamStates[i].getMaxIndex();
    }

    /* loaded from: classes.dex */
    public static final class AbsoluteVolumeDeviceInfo {
        public final IAudioDeviceVolumeDispatcher mCallback;
        public final AudioDeviceAttributes mDevice;
        public int mDeviceVolumeBehavior;
        public final boolean mHandlesVolumeAdjustment;
        public final List<VolumeInfo> mVolumeInfos;

        public static /* synthetic */ boolean lambda$getMatchingVolumeInfoForStream$0(int i, int i2) {
            return i2 == i;
        }

        public AbsoluteVolumeDeviceInfo(AudioDeviceAttributes audioDeviceAttributes, List<VolumeInfo> list, IAudioDeviceVolumeDispatcher iAudioDeviceVolumeDispatcher, boolean z, int i) {
            this.mDevice = audioDeviceAttributes;
            this.mVolumeInfos = list;
            this.mCallback = iAudioDeviceVolumeDispatcher;
            this.mHandlesVolumeAdjustment = z;
            this.mDeviceVolumeBehavior = i;
        }

        public final VolumeInfo getMatchingVolumeInfoForStream(final int i) {
            for (VolumeInfo volumeInfo : this.mVolumeInfos) {
                boolean z = true;
                boolean z2 = volumeInfo.hasStreamType() && volumeInfo.getStreamType() == i;
                z = (volumeInfo.hasVolumeGroup() && Arrays.stream(volumeInfo.getVolumeGroup().getLegacyStreamTypes()).anyMatch(new IntPredicate() { // from class: com.android.server.audio.AudioService$AbsoluteVolumeDeviceInfo$$ExternalSyntheticLambda0
                    @Override // java.util.function.IntPredicate
                    public final boolean test(int i2) {
                        boolean lambda$getMatchingVolumeInfoForStream$0;
                        lambda$getMatchingVolumeInfoForStream$0 = AudioService.AbsoluteVolumeDeviceInfo.lambda$getMatchingVolumeInfoForStream$0(i, i2);
                        return lambda$getMatchingVolumeInfoForStream$0;
                    }
                })) ? false : false;
                if (!z2) {
                    if (z) {
                    }
                }
                return volumeInfo;
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class RestorableParameters {
        @GuardedBy({"mMap"})
        public Map<String, BooleanSupplier> mMap;

        public RestorableParameters() {
            this.mMap = new LinkedHashMap<String, BooleanSupplier>() { // from class: com.android.server.audio.AudioService.RestorableParameters.1
                @Override // java.util.LinkedHashMap
                public boolean removeEldestEntry(Map.Entry<String, BooleanSupplier> entry) {
                    if (size() <= 1000) {
                        return false;
                    }
                    Log.w("AS.AudioService", "Parameter map exceeds 1000 removing " + ((Object) entry.getKey()));
                    return true;
                }
            };
        }

        public int setParameters(String str, final String str2) {
            int parameters;
            Objects.requireNonNull(str, "id must not be null");
            Objects.requireNonNull(str2, "parameter must not be null");
            synchronized (this.mMap) {
                parameters = AudioSystem.setParameters(str2);
                if (parameters == 0) {
                    queueRestoreWithRemovalIfTrue(str, new BooleanSupplier() { // from class: com.android.server.audio.AudioService$RestorableParameters$$ExternalSyntheticLambda0
                        @Override // java.util.function.BooleanSupplier
                        public final boolean getAsBoolean() {
                            boolean lambda$setParameters$0;
                            lambda$setParameters$0 = AudioService.RestorableParameters.lambda$setParameters$0(str2);
                            return lambda$setParameters$0;
                        }
                    });
                }
            }
            return parameters;
        }

        public static /* synthetic */ boolean lambda$setParameters$0(String str) {
            return AudioSystem.setParameters(str) != 0;
        }

        public void queueRestoreWithRemovalIfTrue(String str, BooleanSupplier booleanSupplier) {
            Objects.requireNonNull(str, "id must not be null");
            synchronized (this.mMap) {
                if (booleanSupplier != null) {
                    this.mMap.put(str, booleanSupplier);
                } else {
                    this.mMap.remove(str);
                }
            }
        }

        public void restoreAll() {
            synchronized (this.mMap) {
                this.mMap.values().removeIf(new Predicate() { // from class: com.android.server.audio.AudioService$RestorableParameters$$ExternalSyntheticLambda1
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean asBoolean;
                        asBoolean = ((BooleanSupplier) obj).getAsBoolean();
                        return asBoolean;
                    }
                });
            }
        }
    }

    public static String makeAlsaAddressString(int i, int i2) {
        return "card=" + i + ";device=" + i2 + ";";
    }

    /* loaded from: classes.dex */
    public static final class Lifecycle extends SystemService {
        public AudioService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new AudioService(context, AudioSystemAdapter.getDefaultAdapter(), SystemServerAdapter.getDefaultAdapter(context), SettingsAdapter.getDefaultAdapter(), new DefaultAudioPolicyFacade(), null);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("audio", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            if (i == 550) {
                this.mService.systemReady();
            }
        }
    }

    public AudioService(Context context, AudioSystemAdapter audioSystemAdapter, SystemServerAdapter systemServerAdapter, SettingsAdapter settingsAdapter, AudioPolicyFacade audioPolicyFacade, Looper looper) {
        this(context, audioSystemAdapter, systemServerAdapter, settingsAdapter, audioPolicyFacade, looper, (AppOpsManager) context.getSystemService(AppOpsManager.class), PermissionEnforcer.fromContext(context));
    }

    @RequiresPermission("android.permission.READ_DEVICE_CONFIG")
    public AudioService(Context context, AudioSystemAdapter audioSystemAdapter, SystemServerAdapter systemServerAdapter, SettingsAdapter settingsAdapter, AudioPolicyFacade audioPolicyFacade, Looper looper, AppOpsManager appOpsManager, PermissionEnforcer permissionEnforcer) {
        super(permissionEnforcer);
        int integer;
        int[] iArr;
        int i;
        int integer2;
        this.mVolumeController = new VolumeController();
        this.mMode = new AtomicInteger(0);
        this.mSettingsLock = new Object();
        this.STREAM_VOLUME_ALIAS_VOICE = new int[]{0, 2, 2, 3, 4, 2, 6, 2, 2, 3, 3, 3};
        this.STREAM_VOLUME_ALIAS_TELEVISION = new int[]{3, 3, 3, 3, 3, 3, 6, 3, 3, 3, 3, 3};
        this.STREAM_VOLUME_ALIAS_NONE = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        this.STREAM_VOLUME_ALIAS_DEFAULT = new int[]{0, 2, 2, 3, 4, 2, 6, 2, 2, 3, 3, 3};
        this.mAvrcpAbsVolSupported = false;
        this.mAudioSystemCallback = new AudioSystem.ErrorCallback() { // from class: com.android.server.audio.AudioService.1
            public void onError(int i2) {
                if (i2 != 100) {
                    return;
                }
                if (AudioService.this.mRecordMonitor != null) {
                    AudioService.this.mRecordMonitor.onAudioServerDied();
                }
                if (AudioService.this.mPlaybackMonitor != null) {
                    AudioService.this.mPlaybackMonitor.onAudioServerDied();
                }
                AudioService.sendMsg(AudioService.this.mAudioHandler, 4, 1, 0, 0, null, 0);
                AudioService.sendMsg(AudioService.this.mAudioHandler, 23, 2, 0, 0, null, 0);
            }
        };
        this.mRingerModeExternal = -1;
        this.mRingerModeAffectedStreams = 0;
        this.mZenModeAffectedStreams = 0;
        this.mReceiver = new AudioServiceBroadcastReceiver();
        this.mUserRestrictionsListener = new AudioServiceUserRestrictionsListener();
        this.mSetModeDeathHandlers = new ArrayList<>();
        this.mPrevVolDirection = 0;
        this.mVolumeControlStream = -1;
        this.mUserSelectedVolumeControlStream = false;
        this.mForceControlStreamLock = new Object();
        this.mForceControlStreamClient = null;
        this.mFixedVolumeDevices = new HashSet(Arrays.asList(Integer.valueOf((int) IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES), 2097152));
        this.mFullVolumeDevices = new HashSet(Arrays.asList(262144, 262145));
        this.mAbsoluteVolumeDeviceInfoMap = new ArrayMap();
        this.mAbsVolumeMultiModeCaseDevices = new HashSet(Arrays.asList(134217728));
        this.mDockAudioMediaEnabled = true;
        this.mRestorableParameters = new RestorableParameters();
        this.mDockState = 0;
        this.mPrescaleAbsoluteVolume = new float[]{0.6f, 0.8f, 0.9f};
        this.mVolumePolicy = VolumePolicy.DEFAULT;
        this.mAssistantUids = new ArraySet<>();
        this.mPrimaryAssistantUid = -1;
        this.mActiveAssistantServiceUids = NO_ACTIVE_ASSISTANT_SERVICE_UIDS;
        this.mAccessibilityServiceUidsLock = new Object();
        this.mInputMethodServiceUid = -1;
        this.mInputMethodServiceUidLock = new Object();
        this.mSupportedSystemUsagesLock = new Object();
        this.mSupportedSystemUsages = new int[]{17};
        this.mUidObserver = new IUidObserver.Stub() { // from class: com.android.server.audio.AudioService.2
            public void onUidActive(int i2) throws RemoteException {
            }

            public void onUidIdle(int i2, boolean z) {
            }

            public void onUidProcAdjChanged(int i2) {
            }

            public void onUidStateChanged(int i2, int i3, long j, int i4) {
            }

            public void onUidGone(int i2, boolean z) {
                disableAudioForUid(false, i2);
            }

            public void onUidCachedChanged(int i2, boolean z) {
                disableAudioForUid(z, i2);
            }

            public final void disableAudioForUid(boolean z, int i2) {
                AudioService audioService = AudioService.this;
                audioService.queueMsgUnderWakeLock(audioService.mAudioHandler, 100, z ? 1 : 0, i2, null, 0);
            }
        };
        this.mRttEnabled = false;
        this.mVoicePlaybackActive = new AtomicBoolean(false);
        this.mMediaPlaybackActive = new AtomicBoolean(false);
        this.mPlaybackActivityMonitor = new IPlaybackConfigDispatcher.Stub() { // from class: com.android.server.audio.AudioService.3
            public void dispatchPlaybackConfigChange(List<AudioPlaybackConfiguration> list, boolean z) {
                AudioService.sendMsg(AudioService.this.mAudioHandler, 29, 0, 0, 0, list, 0);
            }
        };
        this.mVoiceRecordingActivityMonitor = new IRecordingConfigDispatcher.Stub() { // from class: com.android.server.audio.AudioService.4
            public void dispatchRecordingConfigChange(List<AudioRecordingConfiguration> list) {
                AudioService.sendMsg(AudioService.this.mAudioHandler, 37, 0, 0, 0, list, 0);
            }
        };
        this.mRmtSbmxFullVolRefCount = 0;
        this.mRmtSbmxFullVolDeathHandlers = new ArrayList<>();
        this.mStreamAliasingDispatchers = new RemoteCallbackList<>();
        this.mIsCallScreeningModeSupported = false;
        this.mModeDispatchers = new RemoteCallbackList<>();
        this.mMuteAwaitConnectionLock = new Object();
        this.mMuteAwaitConnectionDispatchers = new RemoteCallbackList<>();
        this.mDeviceVolumeBehaviorDispatchers = new RemoteCallbackList<>();
        this.mHdmiClientLock = new Object();
        this.mHdmiSystemAudioSupported = false;
        this.mHdmiControlStatusChangeListenerCallback = new MyHdmiControlStatusChangeListenerCallback();
        this.mMyHdmiCecVolumeControlFeatureListener = new MyHdmiCecVolumeControlFeatureListener();
        this.mModeLogger = new EventLogger(20, "phone state (logged after successful call to AudioSystem.setPhoneState(int, int))");
        this.mDynPolicyLogger = new EventLogger(10, "dynamic policy events (logged when command received by AudioService)");
        this.mPrefMixerAttrDispatcher = new RemoteCallbackList<>();
        this.mExtVolumeControllerLock = new Object();
        this.mDynPolicyCallback = new AudioSystem.DynamicPolicyCallback() { // from class: com.android.server.audio.AudioService.6
            public void onDynamicPolicyMixStateUpdate(String str, int i2) {
                if (TextUtils.isEmpty(str)) {
                    return;
                }
                AudioService.sendMsg(AudioService.this.mAudioHandler, 19, 2, i2, 0, str, 0);
            }
        };
        this.mAudioServerStateListeners = new HashMap<>();
        this.mAudioPolicies = new HashMap<>();
        this.mAudioPolicyCounter = 0;
        sLifecycleLogger.enqueue(new EventLogger.StringEvent("AudioService()"));
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mAppOps = appOpsManager;
        this.mAudioSystem = audioSystemAdapter;
        this.mSystemServer = systemServerAdapter;
        this.mSettings = settingsAdapter;
        this.mAudioPolicy = audioPolicyFacade;
        this.mPlatformType = AudioSystem.getPlatformType(context);
        this.mIsSingleVolume = AudioSystem.isSingleVolume(context);
        this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mSensorPrivacyManagerInternal = (SensorPrivacyManagerInternal) LocalServices.getService(SensorPrivacyManagerInternal.class);
        this.mAudioEventWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "handleAudioEvent");
        this.mSfxHelper = new SoundEffectsHelper(context, new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AudioService.this.lambda$new$0((PlayerBase) obj);
            }
        });
        this.mSpatializerHelper = new SpatializerHelper(this, audioSystemAdapter, context.getResources().getBoolean(17891808));
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        this.mVibrator = vibrator;
        this.mHasVibrator = vibrator == null ? false : vibrator.hasVibrator();
        this.mSupportsMicPrivacyToggle = ((SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class)).supportsSensorToggle(1);
        this.mUseVolumeGroupAliases = context.getResources().getBoolean(17891696);
        this.mNotifAliasRing = !DeviceConfig.getBoolean("systemui", "volume_separate_notification", false);
        DeviceConfig.addOnPropertiesChangedListener("systemui", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda7
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AudioService.this.onDeviceConfigChange(properties);
            }
        });
        if (AudioProductStrategy.getAudioProductStrategies().size() > 0) {
            for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                AudioAttributes audioAttributesForStrategyWithLegacyStreamType = AudioProductStrategy.getAudioAttributesForStrategyWithLegacyStreamType(numStreamTypes);
                int maxVolumeIndexForAttributes = AudioSystem.getMaxVolumeIndexForAttributes(audioAttributesForStrategyWithLegacyStreamType);
                if (maxVolumeIndexForAttributes != -1) {
                    MAX_STREAM_VOLUME[numStreamTypes] = maxVolumeIndexForAttributes;
                }
                int minVolumeIndexForAttributes = AudioSystem.getMinVolumeIndexForAttributes(audioAttributesForStrategyWithLegacyStreamType);
                if (minVolumeIndexForAttributes != -1) {
                    MIN_STREAM_VOLUME[numStreamTypes] = minVolumeIndexForAttributes;
                }
            }
            if (this.mUseVolumeGroupAliases) {
                int i2 = 0;
                while (true) {
                    int[] iArr2 = AudioSystem.DEFAULT_STREAM_VOLUME;
                    if (i2 >= iArr2.length) {
                        break;
                    }
                    iArr2[i2] = -1;
                    i2++;
                }
            }
        }
        int i3 = SystemProperties.getInt("ro.config.vc_call_vol_steps", -1);
        if (i3 != -1) {
            MAX_STREAM_VOLUME[0] = i3;
        }
        int i4 = SystemProperties.getInt("ro.config.vc_call_vol_default", -1);
        if (i4 != -1 && i4 <= MAX_STREAM_VOLUME[0] && i4 >= MIN_STREAM_VOLUME[0]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[0] = i4;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[0] = (MAX_STREAM_VOLUME[0] * 3) / 4;
        }
        int i5 = SystemProperties.getInt("ro.config.media_vol_steps", -1);
        if (i5 != -1) {
            MAX_STREAM_VOLUME[3] = i5;
        }
        int i6 = SystemProperties.getInt("ro.config.media_vol_default", -1);
        if (i6 != -1 && i6 <= MAX_STREAM_VOLUME[3] && i6 >= MIN_STREAM_VOLUME[3]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[3] = i6;
        } else if (isPlatformTelevision()) {
            AudioSystem.DEFAULT_STREAM_VOLUME[3] = MAX_STREAM_VOLUME[3] / 4;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[3] = MAX_STREAM_VOLUME[3] / 3;
        }
        int i7 = SystemProperties.getInt("ro.config.alarm_vol_steps", -1);
        if (i7 != -1) {
            MAX_STREAM_VOLUME[4] = i7;
        }
        int i8 = SystemProperties.getInt("ro.config.alarm_vol_default", -1);
        if (i8 != -1 && i8 <= MAX_STREAM_VOLUME[4]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[4] = i8;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[4] = (MAX_STREAM_VOLUME[4] * 6) / 7;
        }
        int i9 = SystemProperties.getInt("ro.config.system_vol_steps", -1);
        if (i9 != -1) {
            MAX_STREAM_VOLUME[1] = i9;
        }
        int i10 = SystemProperties.getInt("ro.config.system_vol_default", -1);
        if (i10 != -1 && i10 <= MAX_STREAM_VOLUME[1]) {
            AudioSystem.DEFAULT_STREAM_VOLUME[1] = i10;
        } else {
            AudioSystem.DEFAULT_STREAM_VOLUME[1] = MAX_STREAM_VOLUME[1];
        }
        int[] iArr3 = {5, 2};
        int[] iArr4 = {17694741, 17694743};
        int[] iArr5 = {17694740, 17694742};
        int i11 = 0;
        for (int i12 = 2; i11 < i12; i12 = 2) {
            try {
                integer2 = this.mContext.getResources().getInteger(iArr4[i11]);
            } catch (Resources.NotFoundException e) {
                Log.e("AS.AudioService", "Error querying max vol for stream type " + iArr3[i11], e);
            }
            if (integer2 <= 0) {
                throw new IllegalArgumentException("Invalid negative max volume for stream " + iArr3[i11]);
                break;
            }
            Log.i("AS.AudioService", "Stream " + iArr3[i11] + ": using max vol of " + integer2);
            MAX_STREAM_VOLUME[iArr3[i11]] = integer2;
            try {
                integer = this.mContext.getResources().getInteger(iArr5[i11]);
                iArr = MAX_STREAM_VOLUME;
                i = iArr3[i11];
            } catch (Resources.NotFoundException e2) {
                Log.e("AS.AudioService", "Error querying default vol for stream type " + iArr3[i11], e2);
            }
            if (integer > iArr[i]) {
                throw new IllegalArgumentException("Invalid default volume (" + integer + ") for stream " + iArr3[i11] + ", greater than max volume of " + MAX_STREAM_VOLUME[iArr3[i11]]);
            } else if (integer < MIN_STREAM_VOLUME[i]) {
                throw new IllegalArgumentException("Invalid default volume (" + integer + ") for stream " + iArr3[i11] + ", lower than min volume of " + MIN_STREAM_VOLUME[iArr3[i11]]);
            } else {
                Log.i("AS.AudioService", "Stream " + iArr3[i11] + ": using default vol of " + integer);
                AudioSystem.DEFAULT_STREAM_VOLUME[iArr3[i11]] = integer;
                i11++;
            }
        }
        if (looper == null) {
            createAudioSystemThread();
        } else {
            this.mAudioHandler = new AudioHandler(looper);
        }
        this.mSoundDoseHelper = new SoundDoseHelper(this, this.mContext, this.mAudioHandler, this.mSettings, this.mVolumeController);
        AudioSystem.setErrorCallback(this.mAudioSystemCallback);
        updateAudioHalPids();
        boolean readCameraSoundForced = readCameraSoundForced();
        this.mCameraSoundForced = new Boolean(readCameraSoundForced).booleanValue();
        sendMsg(this.mAudioHandler, 8, 2, 4, readCameraSoundForced ? 11 : 0, new String("AudioService ctor"), 0);
        this.mUseFixedVolume = this.mContext.getResources().getBoolean(17891860);
        this.mDeviceBroker = new AudioDeviceBroker(this.mContext, this);
        RecordingActivityMonitor recordingActivityMonitor = new RecordingActivityMonitor(this.mContext);
        this.mRecordMonitor = recordingActivityMonitor;
        recordingActivityMonitor.registerRecordingCallback(this.mVoiceRecordingActivityMonitor, true);
        updateStreamVolumeAlias(false, "AS.AudioService");
        readPersistedSettings();
        readUserRestrictions();
        PlaybackActivityMonitor playbackActivityMonitor = new PlaybackActivityMonitor(context, MAX_STREAM_VOLUME[4], new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AudioService.this.lambda$new$1((AudioDeviceAttributes) obj);
            }
        });
        this.mPlaybackMonitor = playbackActivityMonitor;
        playbackActivityMonitor.registerPlaybackCallback(this.mPlaybackActivityMonitor, true);
        this.mMediaFocusControl = new MediaFocusControl(this.mContext, playbackActivityMonitor);
        readAndSetLowRamDevice();
        this.mIsCallScreeningModeSupported = AudioSystem.isCallScreeningModeSupported();
        if (this.mSystemServer.isPrivileged()) {
            LocalServices.addService(AudioManagerInternal.class, new AudioServiceInternal());
            this.mUserManagerInternal.addUserRestrictionsListener(this.mUserRestrictionsListener);
            recordingActivityMonitor.initMonitor();
        }
        this.mMonitorRotation = SystemProperties.getBoolean("ro.audio.monitorRotation", false);
        this.mHasSpatializerEffect = SystemProperties.getBoolean("ro.audio.spatializer_enabled", false);
        AudioSystemAdapter.setRoutingListener(this);
        AudioSystemAdapter.setVolRangeInitReqListener(this);
        queueMsgUnderWakeLock(this.mAudioHandler, 101, 0, 0, null, 0);
        queueMsgUnderWakeLock(this.mAudioHandler, 102, 0, 0, null, 0);
    }

    public final void initVolumeStreamStates() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        synchronized (VolumeStreamState.class) {
            for (int i = numStreamTypes - 1; i >= 0; i--) {
                VolumeStreamState volumeStreamState = this.mStreamStates[i];
                int volumeGroupForStreamType = getVolumeGroupForStreamType(i);
                if (volumeGroupForStreamType != -1) {
                    SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
                    if (sparseArray.indexOfKey(volumeGroupForStreamType) >= 0) {
                        volumeStreamState.setVolumeGroupState(sparseArray.get(volumeGroupForStreamType));
                    }
                }
            }
        }
    }

    public final void onDeviceConfigChange(DeviceConfig.Properties properties) {
        boolean z;
        if (!properties.getKeyset().contains("volume_separate_notification") || this.mNotifAliasRing == (!properties.getBoolean("volume_separate_notification", false))) {
            return;
        }
        this.mNotifAliasRing = z;
        updateStreamVolumeAlias(true, "AS.AudioService");
    }

    public final void onInitStreamsAndVolumes() {
        createStreamStates();
        initVolumeGroupStates();
        this.mSoundDoseHelper.initSafeMediaVolumeIndex();
        initVolumeStreamStates();
        this.mRingerAndZenModeMutedStreams = 0;
        setRingerModeInt(getRingerModeInternal(), false);
        float[] fArr = {this.mContext.getResources().getFraction(18022403, 1, 1), this.mContext.getResources().getFraction(18022404, 1, 1), this.mContext.getResources().getFraction(18022405, 1, 1)};
        for (int i = 0; i < 3; i++) {
            float f = fArr[i];
            if (0.0f <= f && f <= 1.0f) {
                this.mPrescaleAbsoluteVolume[i] = f;
            }
        }
        initExternalEventReceivers();
        checkVolumeRangeInitialization("AudioService()");
    }

    public final void initExternalEventReceivers() {
        this.mSettingsObserver = new SettingsObserver();
        IntentFilter intentFilter = new IntentFilter("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED");
        intentFilter.addAction("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED");
        intentFilter.addAction("android.intent.action.DOCK_EVENT");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_BACKGROUND");
        intentFilter.addAction("android.intent.action.USER_FOREGROUND");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
        intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        if (this.mMonitorRotation) {
            RotationHelper.init(this.mContext, this.mAudioHandler, new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda16
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioService.this.lambda$initExternalEventReceivers$2((String) obj);
                }
            }, new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda17
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AudioService.this.lambda$initExternalEventReceivers$3((String) obj);
                }
            });
        }
        intentFilter.addAction("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION");
        intentFilter.addAction("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION");
        intentFilter.addAction("com.android.server.audio.action.CHECK_MUSIC_ACTIVE");
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, intentFilter, null, null, 2);
    }

    public void systemReady() {
        sendMsg(this.mAudioHandler, 16, 2, 0, 0, null, 0);
    }

    public final void updateVibratorInfos() {
        VibratorManager vibratorManager = (VibratorManager) this.mContext.getSystemService(VibratorManager.class);
        if (vibratorManager == null) {
            Slog.e("AS.AudioService", "Vibrator manager is not found");
            return;
        }
        int[] vibratorIds = vibratorManager.getVibratorIds();
        if (vibratorIds.length == 0) {
            Slog.d("AS.AudioService", "No vibrator found");
            return;
        }
        ArrayList arrayList = new ArrayList(vibratorIds.length);
        for (int i : vibratorIds) {
            Vibrator vibrator = vibratorManager.getVibrator(i);
            if (vibrator != null) {
                arrayList.add(vibrator);
            } else {
                Slog.w("AS.AudioService", "Vibrator(" + i + ") is not found");
            }
        }
        if (arrayList.isEmpty()) {
            Slog.w("AS.AudioService", "Cannot find any available vibrator");
        } else {
            AudioSystem.setVibratorInfos(arrayList);
        }
    }

    public void onSystemReady() {
        this.mSystemReady = true;
        scheduleLoadSoundEffects();
        this.mDeviceBroker.onSystemReady();
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.hdmi.cec")) {
            synchronized (this.mHdmiClientLock) {
                HdmiControlManager hdmiControlManager = (HdmiControlManager) this.mContext.getSystemService(HdmiControlManager.class);
                this.mHdmiManager = hdmiControlManager;
                if (hdmiControlManager != null) {
                    hdmiControlManager.addHdmiControlStatusChangeListener(this.mHdmiControlStatusChangeListenerCallback);
                    this.mHdmiManager.addHdmiCecVolumeControlFeatureListener(this.mContext.getMainExecutor(), this.mMyHdmiCecVolumeControlFeatureListener);
                }
                HdmiTvClient tvClient = this.mHdmiManager.getTvClient();
                this.mHdmiTvClient = tvClient;
                if (tvClient != null) {
                    this.mFixedVolumeDevices.removeAll(AudioSystem.DEVICE_ALL_HDMI_SYSTEM_AUDIO_AND_SPEAKER_SET);
                }
                this.mHdmiPlaybackClient = this.mHdmiManager.getPlaybackClient();
                this.mHdmiAudioSystemClient = this.mHdmiManager.getAudioSystemClient();
            }
        }
        if (this.mSupportsMicPrivacyToggle) {
            this.mSensorPrivacyManagerInternal.addSensorPrivacyListenerForAllUsers(1, new SensorPrivacyManagerInternal.OnUserSensorPrivacyChangedListener() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda20
                public final void onSensorPrivacyChanged(int i, boolean z) {
                    AudioService.this.lambda$onSystemReady$4(i, z);
                }
            });
        }
        this.mNm = (NotificationManager) this.mContext.getSystemService("notification");
        this.mSoundDoseHelper.configureSafeMedia(true, "AS.AudioService");
        initA11yMonitoring();
        RoleObserver roleObserver = new RoleObserver();
        this.mRoleObserver = roleObserver;
        roleObserver.register();
        onIndicateSystemReady();
        this.mMicMuteFromSystemCached = this.mAudioSystem.isMicrophoneMuted();
        setMicMuteFromSwitchInput();
        initMinStreamVolumeWithoutModifyAudioSettings();
        updateVibratorInfos();
        synchronized (this.mSupportedSystemUsagesLock) {
            AudioSystem.setSupportedSystemUsages(this.mSupportedSystemUsages);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$4(int i, boolean z) {
        if (i == getCurrentUserId()) {
            this.mMicMuteFromPrivacyToggle = z;
            setMicrophoneMuteNoCallerCheck(getCurrentUserId());
        }
    }

    @Override // com.android.server.audio.AudioSystemAdapter.OnRoutingUpdatedListener
    public void onRoutingUpdatedFromNative() {
        sendMsg(this.mAudioHandler, 41, 0, 0, 0, null, 0);
    }

    public void onRoutingUpdatedFromAudioThread() {
        if (this.mHasSpatializerEffect) {
            this.mSpatializerHelper.onRoutingUpdated();
        }
        checkMuteAwaitConnection();
    }

    /* renamed from: onRotationUpdate */
    public void lambda$initExternalEventReceivers$2(String str) {
        sendMsg(this.mAudioHandler, 48, 0, 0, 0, str, 0);
    }

    /* renamed from: onFoldUpdate */
    public void lambda$initExternalEventReceivers$3(String str) {
        sendMsg(this.mAudioHandler, 49, 0, 0, 0, str, 0);
    }

    /* renamed from: ignorePlayerLogs */
    public void lambda$new$0(PlayerBase playerBase) {
        sendMsg(this.mAudioHandler, 51, 0, playerBase.getPlayerIId(), 0, null, 0);
    }

    @Override // com.android.server.audio.AudioSystemAdapter.OnVolRangeInitRequestListener
    public void onVolumeRangeInitRequestFromNative() {
        sendMsg(this.mAudioHandler, 34, 0, 0, 0, "onVolumeRangeInitRequestFromNative", 0);
    }

    /* loaded from: classes.dex */
    public class RoleObserver implements OnRoleHoldersChangedListener {
        public final Executor mExecutor;
        public RoleManager mRm;

        public RoleObserver() {
            this.mExecutor = AudioService.this.mContext.getMainExecutor();
        }

        public void register() {
            RoleManager roleManager = (RoleManager) AudioService.this.mContext.getSystemService("role");
            this.mRm = roleManager;
            if (roleManager != null) {
                roleManager.addOnRoleHoldersChangedListenerAsUser(this.mExecutor, this, UserHandle.ALL);
                synchronized (AudioService.this.mSettingsLock) {
                    AudioService.this.updateAssistantUIdLocked(true);
                }
            }
        }

        public void onRoleHoldersChanged(String str, UserHandle userHandle) {
            if ("android.app.role.ASSISTANT".equals(str)) {
                synchronized (AudioService.this.mSettingsLock) {
                    AudioService.this.updateAssistantUIdLocked(false);
                }
            }
        }

        public String getAssistantRoleHolder() {
            RoleManager roleManager = this.mRm;
            if (roleManager != null) {
                List roleHolders = roleManager.getRoleHolders("android.app.role.ASSISTANT");
                return roleHolders.size() == 0 ? "" : (String) roleHolders.get(0);
            }
            return "";
        }
    }

    public void onIndicateSystemReady() {
        if (AudioSystem.systemReady() == 0) {
            return;
        }
        sendMsg(this.mAudioHandler, 20, 0, 0, 0, null, 1000);
    }

    public void onAudioServerDied() {
        int i;
        if (!this.mSystemReady || AudioSystem.checkAudioFlinger() != 0) {
            Log.e("AS.AudioService", "Audioserver died.");
            sLifecycleLogger.enqueue(new EventLogger.StringEvent("onAudioServerDied() audioserver died"));
            sendMsg(this.mAudioHandler, 4, 1, 0, 0, null, 500);
            return;
        }
        Log.i("AS.AudioService", "Audioserver started.");
        sLifecycleLogger.enqueue(new EventLogger.StringEvent("onAudioServerDied() audioserver started"));
        updateAudioHalPids();
        AudioSystem.setParameters("restarting=true");
        readAndSetLowRamDevice();
        this.mIsCallScreeningModeSupported = AudioSystem.isCallScreeningModeSupported();
        this.mDeviceBroker.onAudioServerDied();
        synchronized (this.mDeviceBroker.mSetModeLock) {
            onUpdateAudioMode(-1, Process.myPid(), this.mContext.getPackageName(), true);
        }
        synchronized (this.mSettingsLock) {
            i = this.mCameraSoundForced ? 11 : 0;
        }
        this.mDeviceBroker.setForceUse_Async(4, i, "onAudioServerDied");
        onReinitVolumes("after audioserver restart");
        restoreVolumeGroups();
        updateMasterMono(this.mContentResolver);
        updateMasterBalance(this.mContentResolver);
        setRingerModeInt(getRingerModeInternal(), false);
        if (this.mMonitorRotation) {
            RotationHelper.updateOrientation();
        }
        this.mRestorableParameters.restoreAll();
        synchronized (this.mSettingsLock) {
            this.mDeviceBroker.setForceUse_Async(3, this.mDockAudioMediaEnabled ? 8 : 0, "onAudioServerDied");
            sendEncodedSurroundMode(this.mContentResolver, "onAudioServerDied");
            sendEnabledSurroundFormats(this.mContentResolver, true);
            AudioSystem.setRttEnabled(this.mRttEnabled);
            resetAssistantServicesUidsLocked();
        }
        synchronized (this.mAccessibilityServiceUidsLock) {
            AudioSystem.setA11yServicesUids(this.mAccessibilityServiceUids);
        }
        synchronized (this.mInputMethodServiceUidLock) {
            this.mAudioSystem.setCurrentImeUid(this.mInputMethodServiceUid);
        }
        synchronized (this.mHdmiClientLock) {
            if (this.mHdmiManager != null && this.mHdmiTvClient != null) {
                setHdmiSystemAudioSupported(this.mHdmiSystemAudioSupported);
            }
        }
        synchronized (this.mSupportedSystemUsagesLock) {
            AudioSystem.setSupportedSystemUsages(this.mSupportedSystemUsages);
        }
        synchronized (this.mAudioPolicies) {
            ArrayList arrayList = new ArrayList();
            for (AudioPolicyProxy audioPolicyProxy : this.mAudioPolicies.values()) {
                int connectMixes = audioPolicyProxy.connectMixes();
                if (connectMixes != 0) {
                    Log.e("AS.AudioService", "onAudioServerDied: error " + AudioSystem.audioSystemErrorToString(connectMixes) + " when connecting mixes for policy " + audioPolicyProxy.toLogFriendlyString());
                    arrayList.add(audioPolicyProxy);
                } else {
                    int i2 = audioPolicyProxy.setupDeviceAffinities();
                    if (i2 != 0) {
                        Log.e("AS.AudioService", "onAudioServerDied: error " + AudioSystem.audioSystemErrorToString(i2) + " when connecting device affinities for policy " + audioPolicyProxy.toLogFriendlyString());
                        arrayList.add(audioPolicyProxy);
                    }
                }
            }
            arrayList.forEach(new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda19
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AudioService.AudioPolicyProxy) obj).release();
                }
            });
        }
        synchronized (this.mPlaybackMonitor) {
            for (Map.Entry<Integer, Integer> entry : this.mPlaybackMonitor.getAllAllowedCapturePolicies().entrySet()) {
                int allowedCapturePolicy = this.mAudioSystem.setAllowedCapturePolicy(entry.getKey().intValue(), AudioAttributes.capturePolicyToFlags(entry.getValue().intValue(), 0));
                if (allowedCapturePolicy != 0) {
                    Log.e("AS.AudioService", "Failed to restore capture policy, uid: " + entry.getKey() + ", capture policy: " + entry.getValue() + ", result: " + allowedCapturePolicy);
                    this.mPlaybackMonitor.setAllowedCapturePolicy(entry.getKey().intValue(), 1);
                }
            }
        }
        this.mSpatializerHelper.reset(this.mHasSpatializerEffect);
        this.mSoundDoseHelper.reset();
        onIndicateSystemReady();
        AudioSystem.setParameters("restarting=false");
        sendMsg(this.mAudioHandler, 23, 2, 1, 0, null, 0);
        setMicrophoneMuteNoCallerCheck(getCurrentUserId());
        setMicMuteFromSwitchInput();
        updateVibratorInfos();
    }

    public final void onRemoveAssistantServiceUids(int[] iArr) {
        synchronized (this.mSettingsLock) {
            removeAssistantServiceUidsLocked(iArr);
        }
    }

    @GuardedBy({"mSettingsLock"})
    public final void removeAssistantServiceUidsLocked(int[] iArr) {
        boolean z = false;
        for (int i = 0; i < iArr.length; i++) {
            if (this.mAssistantUids.remove(Integer.valueOf(iArr[i]))) {
                z = true;
            } else {
                Slog.e("AS.AudioService", TextUtils.formatSimple("Cannot remove assistant service, uid(%d) not present", new Object[]{Integer.valueOf(iArr[i])}));
            }
        }
        if (z) {
            updateAssistantServicesUidsLocked();
        }
    }

    public final void onAddAssistantServiceUids(int[] iArr) {
        synchronized (this.mSettingsLock) {
            addAssistantServiceUidsLocked(iArr);
        }
    }

    @GuardedBy({"mSettingsLock"})
    public final void addAssistantServiceUidsLocked(int[] iArr) {
        boolean z = false;
        for (int i = 0; i < iArr.length; i++) {
            int i2 = iArr[i];
            if (i2 != -1) {
                if (this.mAssistantUids.add(Integer.valueOf(i2))) {
                    z = true;
                } else {
                    Slog.e("AS.AudioService", TextUtils.formatSimple("Cannot add assistant service, uid(%d) already present", new Object[]{Integer.valueOf(iArr[i])}));
                }
            }
        }
        if (z) {
            updateAssistantServicesUidsLocked();
        }
    }

    @GuardedBy({"mSettingsLock"})
    public final void resetAssistantServicesUidsLocked() {
        this.mAssistantUids.clear();
        updateAssistantUIdLocked(true);
    }

    @GuardedBy({"mSettingsLock"})
    public final void updateAssistantServicesUidsLocked() {
        AudioSystem.setAssistantServicesUids(this.mAssistantUids.stream().mapToInt(new AudioService$$ExternalSyntheticLambda0()).toArray());
    }

    public final void updateActiveAssistantServiceUids() {
        int[] iArr;
        synchronized (this.mSettingsLock) {
            iArr = this.mActiveAssistantServiceUids;
        }
        AudioSystem.setActiveAssistantServicesUids(iArr);
    }

    public final void onReinitVolumes(String str) {
        int i;
        int numStreamTypes = AudioSystem.getNumStreamTypes() - 1;
        while (true) {
            if (numStreamTypes < 0) {
                i = 0;
                break;
            }
            VolumeStreamState volumeStreamState = this.mStreamStates[numStreamTypes];
            i = AudioSystem.initStreamVolume(numStreamTypes, volumeStreamState.mIndexMin / 10, volumeStreamState.mIndexMax / 10);
            if (i != 0) {
                Log.e("AS.AudioService", "Failed to initStreamVolume (" + i + ") for stream " + numStreamTypes);
                break;
            }
            volumeStreamState.applyAllVolumes();
            numStreamTypes--;
        }
        if (i != 0) {
            sLifecycleLogger.enqueue(new EventLogger.StringEvent(str + ": initStreamVolume failed with " + i + " will retry").printLog(1, "AS.AudioService"));
            sendMsg(this.mAudioHandler, 34, 1, 0, 0, str, 2000);
        } else if (checkVolumeRangeInitialization(str)) {
            sLifecycleLogger.enqueue(new EventLogger.StringEvent(str + ": initStreamVolume succeeded").printLog(0, "AS.AudioService"));
        }
    }

    public final boolean checkVolumeRangeInitialization(String str) {
        boolean z = false;
        int[] iArr = {4, 2, 3, 0, 10};
        int i = 0;
        while (true) {
            if (i >= 5) {
                z = true;
                break;
            }
            AudioAttributes build = new AudioAttributes.Builder().setInternalLegacyStreamType(iArr[i]).build();
            if (AudioSystem.getMaxVolumeIndexForAttributes(build) < 0 || AudioSystem.getMinVolumeIndexForAttributes(build) < 0) {
                break;
            }
            i++;
        }
        if (!z) {
            sLifecycleLogger.enqueue(new EventLogger.StringEvent(str + ": initStreamVolume succeeded but invalid mix/max levels, will retry").printLog(2, "AS.AudioService"));
            sendMsg(this.mAudioHandler, 34, 1, 0, 0, str, 2000);
        }
        return z;
    }

    public final void onDispatchAudioServerStateChange(boolean z) {
        synchronized (this.mAudioServerStateListeners) {
            for (AsdProxy asdProxy : this.mAudioServerStateListeners.values()) {
                try {
                    asdProxy.callback().dispatchAudioServerStateChange(z);
                } catch (RemoteException e) {
                    Log.w("AS.AudioService", "Could not call dispatchAudioServerStateChange()", e);
                }
            }
        }
    }

    public final void createAudioSystemThread() {
        AudioSystemThread audioSystemThread = new AudioSystemThread();
        this.mAudioSystemThread = audioSystemThread;
        audioSystemThread.start();
        waitForAudioHandlerCreation();
    }

    public final void waitForAudioHandlerCreation() {
        synchronized (this) {
            while (this.mAudioHandler == null) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                    Log.e("AS.AudioService", "Interrupted while waiting on volume handler.");
                }
            }
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void setSupportedSystemUsages(int[] iArr) {
        super.setSupportedSystemUsages_enforcePermission();
        verifySystemUsages(iArr);
        synchronized (this.mSupportedSystemUsagesLock) {
            AudioSystem.setSupportedSystemUsages(iArr);
            this.mSupportedSystemUsages = iArr;
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int[] getSupportedSystemUsages() {
        int[] copyOf;
        super.getSupportedSystemUsages_enforcePermission();
        synchronized (this.mSupportedSystemUsagesLock) {
            int[] iArr = this.mSupportedSystemUsages;
            copyOf = Arrays.copyOf(iArr, iArr.length);
        }
        return copyOf;
    }

    public final void verifySystemUsages(int[] iArr) {
        for (int i = 0; i < iArr.length; i++) {
            if (!AudioAttributes.isSystemUsage(iArr[i])) {
                throw new IllegalArgumentException("Non-system usage provided: " + iArr[i]);
            }
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public List<AudioProductStrategy> getAudioProductStrategies() {
        super.getAudioProductStrategies_enforcePermission();
        return AudioProductStrategy.getAudioProductStrategies();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public List<AudioVolumeGroup> getAudioVolumeGroups() {
        super.getAudioVolumeGroups_enforcePermission();
        return AudioVolumeGroup.getAudioVolumeGroups();
    }

    public final void checkAllAliasStreamVolumes() {
        synchronized (this.mSettingsLock) {
            synchronized (VolumeStreamState.class) {
                int numStreamTypes = AudioSystem.getNumStreamTypes();
                for (int i = 0; i < numStreamTypes; i++) {
                    VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                    volumeStreamStateArr[i].setAllIndexes(volumeStreamStateArr[mStreamVolumeAlias[i]], "AS.AudioService");
                    if (!this.mStreamStates[i].mIsMuted) {
                        this.mStreamStates[i].applyAllVolumes();
                    }
                }
            }
        }
    }

    public void postCheckVolumeCecOnHdmiConnection(int i, String str) {
        sendMsg(this.mAudioHandler, 28, 0, i, 0, str, 0);
    }

    public final void onCheckVolumeCecOnHdmiConnection(int i, String str) {
        if (i == 1) {
            if (this.mSoundDoseHelper.safeDevicesContains(1024)) {
                this.mSoundDoseHelper.scheduleMusicActiveCheck();
            }
            if (isPlatformTelevision()) {
                synchronized (this.mHdmiClientLock) {
                    if (this.mHdmiManager != null && this.mHdmiPlaybackClient != null) {
                        updateHdmiCecSinkLocked(this.mFullVolumeDevices.contains(1024));
                    }
                }
            }
            sendEnabledSurroundFormats(this.mContentResolver, true);
        } else if (isPlatformTelevision()) {
            synchronized (this.mHdmiClientLock) {
                if (this.mHdmiManager != null) {
                    updateHdmiCecSinkLocked(this.mFullVolumeDevices.contains(1024));
                }
            }
        }
    }

    public final void postUpdateVolumeStatesForAudioDevice(int i, String str) {
        sendMsg(this.mAudioHandler, 33, 2, i, 0, str, 0);
    }

    public final void onUpdateVolumeStatesForAudioDevice(int i, String str) {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        synchronized (this.mSettingsLock) {
            synchronized (VolumeStreamState.class) {
                for (int i2 = 0; i2 < numStreamTypes; i2++) {
                    updateVolumeStates(i, i2, str);
                }
            }
        }
    }

    public final void updateVolumeStates(int i, int i2, String str) {
        if (i == 4194304) {
            i = 2;
        }
        if (!this.mStreamStates[i2].hasIndexForDevice(i)) {
            VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
            volumeStreamStateArr[i2].setIndex(volumeStreamStateArr[mStreamVolumeAlias[i2]].getIndex(1073741824), i, str, true);
        }
        for (AudioDeviceAttributes audioDeviceAttributes : getDevicesForAttributesInt(new AudioAttributes.Builder().setInternalLegacyStreamType(i2).build(), true)) {
            if (audioDeviceAttributes.getType() == AudioDeviceInfo.convertInternalDeviceToDeviceType(i)) {
                this.mStreamStates[i2].checkFixedVolumeDevices();
                if (isStreamMute(i2) && this.mFullVolumeDevices.contains(Integer.valueOf(i))) {
                    this.mStreamStates[i2].mute(false);
                }
            }
        }
    }

    public final void checkAllFixedVolumeDevices() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            this.mStreamStates[i].checkFixedVolumeDevices();
        }
    }

    public final void checkAllFixedVolumeDevices(int i) {
        this.mStreamStates[i].checkFixedVolumeDevices();
    }

    public final void checkMuteAffectedStreams() {
        int i = 0;
        while (true) {
            VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
            if (i >= volumeStreamStateArr.length) {
                return;
            }
            VolumeStreamState volumeStreamState = volumeStreamStateArr[i];
            if (volumeStreamState.mIndexMin > 0 && volumeStreamState.mStreamType != 0 && volumeStreamState.mStreamType != 6) {
                this.mMuteAffectedStreams = (~(1 << volumeStreamState.mStreamType)) & this.mMuteAffectedStreams;
            }
            i++;
        }
    }

    public final void createStreamStates() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        VolumeStreamState[] volumeStreamStateArr = new VolumeStreamState[numStreamTypes];
        this.mStreamStates = volumeStreamStateArr;
        for (int i = 0; i < numStreamTypes; i++) {
            volumeStreamStateArr[i] = new VolumeStreamState(Settings.System.VOLUME_SETTINGS_INT[mStreamVolumeAlias[i]], i);
        }
        checkAllFixedVolumeDevices();
        checkAllAliasStreamVolumes();
        checkMuteAffectedStreams();
        updateDefaultVolumes();
    }

    public final void updateDefaultVolumes() {
        for (int i = 0; i < this.mStreamStates.length; i++) {
            int i2 = mStreamVolumeAlias[i];
            if (this.mUseVolumeGroupAliases) {
                if (AudioSystem.DEFAULT_STREAM_VOLUME[i] == -1) {
                    i2 = 3;
                    int uiDefaultRescaledIndex = getUiDefaultRescaledIndex(3, i);
                    if (uiDefaultRescaledIndex >= MIN_STREAM_VOLUME[i] && uiDefaultRescaledIndex <= MAX_STREAM_VOLUME[i]) {
                        AudioSystem.DEFAULT_STREAM_VOLUME[i] = uiDefaultRescaledIndex;
                    }
                }
            }
            if (i != i2) {
                AudioSystem.DEFAULT_STREAM_VOLUME[i] = getUiDefaultRescaledIndex(i2, i);
            }
        }
    }

    public final int getUiDefaultRescaledIndex(int i, int i2) {
        return (rescaleIndex(AudioSystem.DEFAULT_STREAM_VOLUME[i] * 10, i, i2) + 5) / 10;
    }

    public final void dumpStreamStates(PrintWriter printWriter) {
        printWriter.println("\nStream volumes (device: index)");
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            StringBuilder sb = new StringBuilder();
            if (mStreamVolumeAlias[i] != i) {
                sb.append(" (aliased to: ");
                sb.append(AudioSystem.STREAM_NAMES[mStreamVolumeAlias[i]]);
                sb.append(")");
            }
            printWriter.println("- " + AudioSystem.STREAM_NAMES[i] + ((Object) sb) + XmlUtils.STRING_ARRAY_SEPARATOR);
            this.mStreamStates[i].dump(printWriter);
            printWriter.println("");
        }
        printWriter.print("\n- mute affected streams = 0x");
        printWriter.println(Integer.toHexString(this.mMuteAffectedStreams));
    }

    public final void updateStreamVolumeAlias(boolean z, String str) {
        int i = 3;
        int i2 = sIndependentA11yVolume ? 10 : 3;
        int i3 = this.mContext.getResources().getBoolean(17891855) ? 11 : 3;
        if (this.mIsSingleVolume) {
            mStreamVolumeAlias = (int[]) this.STREAM_VOLUME_ALIAS_TELEVISION.clone();
        } else if (this.mUseVolumeGroupAliases) {
            mStreamVolumeAlias = (int[]) this.STREAM_VOLUME_ALIAS_NONE.clone();
            i = 8;
        } else {
            if (this.mPlatformType == 1) {
                mStreamVolumeAlias = (int[]) this.STREAM_VOLUME_ALIAS_VOICE.clone();
                i = 2;
            } else {
                mStreamVolumeAlias = (int[]) this.STREAM_VOLUME_ALIAS_DEFAULT.clone();
            }
            if (!this.mNotifAliasRing) {
                mStreamVolumeAlias[5] = 5;
            }
        }
        if (this.mIsSingleVolume) {
            this.mRingerModeAffectedStreams = 0;
        } else if (isInCommunication()) {
            this.mRingerModeAffectedStreams &= -257;
            i = 0;
        } else {
            this.mRingerModeAffectedStreams |= 256;
        }
        int[] iArr = mStreamVolumeAlias;
        iArr[8] = i;
        iArr[10] = i2;
        iArr[11] = i3;
        if (z && this.mStreamStates != null) {
            updateDefaultVolumes();
            synchronized (this.mSettingsLock) {
                synchronized (VolumeStreamState.class) {
                    VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                    volumeStreamStateArr[8].setAllIndexes(volumeStreamStateArr[i], str);
                    this.mStreamStates[10].mVolumeIndexSettingName = Settings.System.VOLUME_SETTINGS_INT[i2];
                    VolumeStreamState[] volumeStreamStateArr2 = this.mStreamStates;
                    volumeStreamStateArr2[10].setAllIndexes(volumeStreamStateArr2[i2], str);
                }
            }
            if (sIndependentA11yVolume) {
                this.mStreamStates[10].readSettings();
            }
            setRingerModeInt(getRingerModeInternal(), false);
            sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[8], 0);
            sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[10], 0);
        }
        dispatchStreamAliasingUpdate();
    }

    public final void readDockAudioSettings(ContentResolver contentResolver) {
        boolean z = this.mSettings.getGlobalInt(contentResolver, "dock_audio_media_enabled", 0) == 1;
        this.mDockAudioMediaEnabled = z;
        sendMsg(this.mAudioHandler, 8, 2, 3, z ? 8 : 0, new String("readDockAudioSettings"), 0);
    }

    public final void updateMasterMono(ContentResolver contentResolver) {
        AudioSystem.setMasterMono(this.mSettings.getSystemIntForUser(contentResolver, "master_mono", 0, -2) == 1);
    }

    public final void updateMasterBalance(ContentResolver contentResolver) {
        float floatForUser = Settings.System.getFloatForUser(contentResolver, "master_balance", 0.0f, -2);
        if (AudioSystem.setMasterBalance(floatForUser) != 0) {
            Log.e("AS.AudioService", String.format("setMasterBalance failed for %f", Float.valueOf(floatForUser)));
        }
    }

    public final void sendEncodedSurroundMode(ContentResolver contentResolver, String str) {
        sendEncodedSurroundMode(this.mSettings.getGlobalInt(contentResolver, "encoded_surround_output", 0), str);
    }

    public final void sendEncodedSurroundMode(int i, String str) {
        int i2;
        if (i == 0) {
            i2 = 0;
        } else if (i == 1) {
            i2 = 13;
        } else if (i == 2) {
            i2 = 14;
        } else if (i != 3) {
            Log.e("AS.AudioService", "updateSurroundSoundSettings: illegal value " + i);
            i2 = 16;
        } else {
            i2 = 15;
        }
        if (i2 != 16) {
            this.mDeviceBroker.setForceUse_Async(6, i2, str);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MANAGE_AUDIO_POLICY") != 0) {
            throw new SecurityException("Missing MANAGE_AUDIO_POLICY permission");
        }
        new AudioManagerShellCommand(this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public Map<Integer, Boolean> getSurroundFormats() {
        HashMap hashMap = new HashMap();
        int surroundFormats = AudioSystem.getSurroundFormats(hashMap);
        if (surroundFormats != 0) {
            Log.e("AS.AudioService", "getSurroundFormats failed:" + surroundFormats);
            return new HashMap();
        }
        return hashMap;
    }

    public List<Integer> getReportedSurroundFormats() {
        ArrayList arrayList = new ArrayList();
        int reportedSurroundFormats = AudioSystem.getReportedSurroundFormats(arrayList);
        if (reportedSurroundFormats != 0) {
            Log.e("AS.AudioService", "getReportedSurroundFormats failed:" + reportedSurroundFormats);
            return new ArrayList();
        }
        return arrayList;
    }

    public boolean isSurroundFormatEnabled(int i) {
        boolean contains;
        if (!isSurroundFormat(i)) {
            Log.w("AS.AudioService", "audioFormat to enable is not a surround format.");
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mSettingsLock) {
                contains = getEnabledFormats().contains(Integer.valueOf(i));
            }
            return contains;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public boolean setSurroundFormatEnabled(int i, boolean z) {
        if (!isSurroundFormat(i)) {
            Log.w("AS.AudioService", "audioFormat to enable is not a surround format.");
            return false;
        } else if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SETTINGS") != 0) {
            throw new SecurityException("Missing WRITE_SETTINGS permission");
        } else {
            HashSet<Integer> enabledFormats = getEnabledFormats();
            if (z) {
                enabledFormats.add(Integer.valueOf(i));
            } else {
                enabledFormats.remove(Integer.valueOf(i));
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (this.mSettingsLock) {
                    this.mSettings.putGlobalString(this.mContentResolver, "encoded_surround_output_enabled_formats", TextUtils.join(",", enabledFormats));
                }
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }
    }

    public boolean setEncodedSurroundMode(int i) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SETTINGS") != 0) {
            throw new SecurityException("Missing WRITE_SETTINGS permission");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mSettingsLock) {
                this.mSettings.putGlobalInt(this.mContentResolver, "encoded_surround_output", toEncodedSurroundSetting(i));
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return true;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public int getEncodedSurroundMode(int i) {
        int encodedSurroundOutputMode;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mSettingsLock) {
                encodedSurroundOutputMode = toEncodedSurroundOutputMode(this.mSettings.getGlobalInt(this.mContentResolver, "encoded_surround_output", 0), i);
            }
            return encodedSurroundOutputMode;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final HashSet<Integer> getEnabledFormats() {
        final HashSet<Integer> hashSet = new HashSet<>();
        String globalString = this.mSettings.getGlobalString(this.mContentResolver, "encoded_surround_output_enabled_formats");
        if (globalString != null) {
            try {
                Arrays.stream(TextUtils.split(globalString, ",")).mapToInt(new ToIntFunction() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda10
                    @Override // java.util.function.ToIntFunction
                    public final int applyAsInt(Object obj) {
                        return Integer.parseInt((String) obj);
                    }
                }).forEach(new IntConsumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda11
                    @Override // java.util.function.IntConsumer
                    public final void accept(int i) {
                        hashSet.add(Integer.valueOf(i));
                    }
                });
            } catch (NumberFormatException e) {
                Log.w("AS.AudioService", "ENCODED_SURROUND_OUTPUT_ENABLED_FORMATS misformatted.", e);
            }
        }
        return hashSet;
    }

    public final boolean isSurroundFormat(int i) {
        for (int i2 : AudioFormat.SURROUND_SOUND_ENCODING) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    public final void sendEnabledSurroundFormats(ContentResolver contentResolver, boolean z) {
        if (this.mEncodedSurroundMode != 3) {
            return;
        }
        String globalString = this.mSettings.getGlobalString(contentResolver, "encoded_surround_output_enabled_formats");
        if (globalString == null) {
            globalString = "";
        }
        if (z || !TextUtils.equals(globalString, this.mEnabledSurroundFormats)) {
            this.mEnabledSurroundFormats = globalString;
            String[] split = TextUtils.split(globalString, ",");
            ArrayList arrayList = new ArrayList();
            for (String str : split) {
                try {
                    int intValue = Integer.valueOf(str).intValue();
                    if (isSurroundFormat(intValue) && !arrayList.contains(Integer.valueOf(intValue))) {
                        arrayList.add(Integer.valueOf(intValue));
                    }
                } catch (Exception unused) {
                    Log.e("AS.AudioService", "Invalid enabled surround format:" + str);
                }
            }
            this.mSettings.putGlobalString(this.mContext.getContentResolver(), "encoded_surround_output_enabled_formats", TextUtils.join(",", arrayList));
            sendMsg(this.mAudioHandler, 24, 2, 0, 0, arrayList, 0);
        }
    }

    public final void onEnableSurroundFormats(ArrayList<Integer> arrayList) {
        int[] iArr;
        for (int i : AudioFormat.SURROUND_SOUND_ENCODING) {
            boolean contains = arrayList.contains(Integer.valueOf(i));
            Log.i("AS.AudioService", "enable surround format:" + i + " " + contains + " " + AudioSystem.setSurroundFormatEnabled(i, contains));
        }
    }

    @GuardedBy({"mSettingsLock"})
    public final void updateAssistantUIdLocked(boolean z) {
        int i;
        int i2;
        RoleObserver roleObserver = this.mRoleObserver;
        String assistantRoleHolder = roleObserver != null ? roleObserver.getAssistantRoleHolder() : "";
        if (TextUtils.isEmpty(assistantRoleHolder)) {
            String secureStringForUser = this.mSettings.getSecureStringForUser(this.mContentResolver, "voice_interaction_service", -2);
            if (TextUtils.isEmpty(secureStringForUser)) {
                secureStringForUser = this.mSettings.getSecureStringForUser(this.mContentResolver, "assistant", -2);
            }
            if (!TextUtils.isEmpty(secureStringForUser)) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(secureStringForUser);
                if (unflattenFromString == null) {
                    Slog.w("AS.AudioService", "Invalid service name for voice_interaction_service: " + secureStringForUser);
                    return;
                }
                assistantRoleHolder = unflattenFromString.getPackageName();
            }
        }
        if (!TextUtils.isEmpty(assistantRoleHolder)) {
            PackageManager packageManager = this.mContext.getPackageManager();
            if (packageManager.checkPermission("android.permission.CAPTURE_AUDIO_HOTWORD", assistantRoleHolder) == 0) {
                try {
                    i = packageManager.getPackageUidAsUser(assistantRoleHolder, getCurrentUserId());
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.e("AS.AudioService", "updateAssistantUId() could not find UID for package: " + assistantRoleHolder);
                }
                i2 = this.mPrimaryAssistantUid;
                if (i2 == i || z) {
                    this.mAssistantUids.remove(Integer.valueOf(i2));
                    this.mPrimaryAssistantUid = i;
                    addAssistantServiceUidsLocked(new int[]{i});
                }
                return;
            }
        }
        i = -1;
        i2 = this.mPrimaryAssistantUid;
        if (i2 == i) {
        }
        this.mAssistantUids.remove(Integer.valueOf(i2));
        this.mPrimaryAssistantUid = i;
        addAssistantServiceUidsLocked(new int[]{i});
    }

    public final void readPersistedSettings() {
        if (this.mSystemServer.isPrivileged()) {
            ContentResolver contentResolver = this.mContentResolver;
            int i = 2;
            int globalInt = this.mSettings.getGlobalInt(contentResolver, "mode_ringer", 2);
            int i2 = !isValidRingerMode(globalInt) ? 2 : globalInt;
            if (i2 == 1 && !this.mHasVibrator) {
                i2 = 0;
            }
            if (i2 != globalInt) {
                this.mSettings.putGlobalInt(contentResolver, "mode_ringer", i2);
            }
            if (this.mUseFixedVolume || this.mIsSingleVolume) {
                i2 = 2;
            }
            synchronized (this.mSettingsLock) {
                this.mRingerMode = i2;
                if (this.mRingerModeExternal == -1) {
                    this.mRingerModeExternal = i2;
                }
                int valueForVibrateSetting = AudioSystem.getValueForVibrateSetting(0, 1, this.mHasVibrator ? 2 : 0);
                this.mVibrateSetting = valueForVibrateSetting;
                if (!this.mHasVibrator) {
                    i = 0;
                }
                this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(valueForVibrateSetting, 0, i);
                updateRingerAndZenModeAffectedStreams();
                readDockAudioSettings(contentResolver);
                sendEncodedSurroundMode(contentResolver, "readPersistedSettings");
                sendEnabledSurroundFormats(contentResolver, true);
                updateAssistantUIdLocked(true);
                resetActiveAssistantUidsLocked();
                AudioSystem.setRttEnabled(this.mRttEnabled);
            }
            this.mMuteAffectedStreams = this.mSettings.getSystemIntForUser(contentResolver, "mute_streams_affected", 111, -2);
            updateMasterMono(contentResolver);
            updateMasterBalance(contentResolver);
            broadcastRingerMode("android.media.RINGER_MODE_CHANGED", this.mRingerModeExternal);
            broadcastRingerMode("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", this.mRingerMode);
            broadcastVibrateSetting(0);
            broadcastVibrateSetting(1);
            this.mVolumeController.loadSettings(contentResolver);
        }
    }

    @GuardedBy({"mSettingsLock"})
    public final void resetActiveAssistantUidsLocked() {
        this.mActiveAssistantServiceUids = NO_ACTIVE_ASSISTANT_SERVICE_UIDS;
        updateActiveAssistantServiceUids();
    }

    public final void readUserRestrictions() {
        if (this.mSystemServer.isPrivileged()) {
            int currentUserId = getCurrentUserId();
            boolean z = false;
            boolean z2 = this.mUserManagerInternal.getUserRestriction(currentUserId, "disallow_unmute_device") || this.mUserManagerInternal.getUserRestriction(currentUserId, "no_adjust_volume");
            if (this.mUseFixedVolume) {
                AudioSystem.setMasterVolume(1.0f);
            } else {
                z = z2;
            }
            AudioSystem.setMasterMute(z);
            broadcastMasterMuteStatus(z);
            this.mMicMuteFromRestrictions = this.mUserManagerInternal.getUserRestriction(currentUserId, "no_unmute_microphone");
            setMicrophoneMuteNoCallerCheck(currentUserId);
        }
    }

    public final int getIndexRange(int i) {
        return this.mStreamStates[i].getMaxIndex() - this.mStreamStates[i].getMinIndex();
    }

    public final int rescaleIndex(VolumeInfo volumeInfo, int i) {
        if (volumeInfo.getVolumeIndex() == -100 || volumeInfo.getMinVolumeIndex() == -100 || volumeInfo.getMaxVolumeIndex() == -100) {
            Log.e("AS.AudioService", "rescaleIndex: volumeInfo has invalid index or range");
            return this.mStreamStates[i].getMinIndex();
        }
        return rescaleIndex(volumeInfo.getVolumeIndex(), volumeInfo.getMinVolumeIndex(), volumeInfo.getMaxVolumeIndex(), this.mStreamStates[i].getMinIndex(), this.mStreamStates[i].getMaxIndex());
    }

    public final int rescaleIndex(int i, int i2, VolumeInfo volumeInfo) {
        int minVolumeIndex = volumeInfo.getMinVolumeIndex();
        int maxVolumeIndex = volumeInfo.getMaxVolumeIndex();
        return (minVolumeIndex == -100 || maxVolumeIndex == -100) ? i : rescaleIndex(i, this.mStreamStates[i2].getMinIndex(), this.mStreamStates[i2].getMaxIndex(), minVolumeIndex, maxVolumeIndex);
    }

    public final int rescaleIndex(int i, int i2, int i3) {
        return rescaleIndex(i, this.mStreamStates[i2].getMinIndex(), this.mStreamStates[i2].getMaxIndex(), this.mStreamStates[i3].getMinIndex(), this.mStreamStates[i3].getMaxIndex());
    }

    public final int rescaleIndex(int i, int i2, int i3, int i4, int i5) {
        int i6 = i3 - i2;
        int i7 = i5 - i4;
        if (i6 == 0) {
            Log.e("AS.AudioService", "rescaleIndex : index range should not be zero");
            return i4;
        }
        return i4 + ((((i - i2) * i7) + (i6 / 2)) / i6);
    }

    public final int rescaleStep(int i, int i2, int i3) {
        int indexRange = getIndexRange(i2);
        int indexRange2 = getIndexRange(i3);
        if (indexRange == 0) {
            Log.e("AS.AudioService", "rescaleStep : index range should not be zero");
            return 0;
        }
        return ((i * indexRange2) + (indexRange / 2)) / indexRange;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int setPreferredDevicesForStrategy(int i, List<AudioDeviceAttributes> list) {
        super.setPreferredDevicesForStrategy_enforcePermission();
        if (list == null) {
            return -1;
        }
        String format = String.format("setPreferredDeviceForStrategy u/pid:%d/%d strat:%d dev:%s", Integer.valueOf(Binder.getCallingUid()), Integer.valueOf(Binder.getCallingPid()), Integer.valueOf(i), list.stream().map(new Function() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String audioDeviceAttributes;
                audioDeviceAttributes = ((AudioDeviceAttributes) obj).toString();
                return audioDeviceAttributes;
            }
        }).collect(Collectors.joining(",")));
        sDeviceLogger.enqueue(new EventLogger.StringEvent(format).printLog("AS.AudioService"));
        if (list.stream().anyMatch(new Predicate() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda14
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setPreferredDevicesForStrategy$7;
                lambda$setPreferredDevicesForStrategy$7 = AudioService.lambda$setPreferredDevicesForStrategy$7((AudioDeviceAttributes) obj);
                return lambda$setPreferredDevicesForStrategy$7;
            }
        })) {
            Log.e("AS.AudioService", "Unsupported input routing in " + format);
            return -1;
        }
        int preferredDevicesForStrategySync = this.mDeviceBroker.setPreferredDevicesForStrategySync(i, list);
        if (preferredDevicesForStrategySync != 0) {
            Log.e("AS.AudioService", String.format("Error %d in %s)", Integer.valueOf(preferredDevicesForStrategySync), format));
        }
        return preferredDevicesForStrategySync;
    }

    public static /* synthetic */ boolean lambda$setPreferredDevicesForStrategy$7(AudioDeviceAttributes audioDeviceAttributes) {
        return audioDeviceAttributes.getRole() == 1;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int removePreferredDevicesForStrategy(int i) {
        super.removePreferredDevicesForStrategy_enforcePermission();
        String format = String.format("removePreferredDeviceForStrategy strat:%d", Integer.valueOf(i));
        sDeviceLogger.enqueue(new EventLogger.StringEvent(format).printLog("AS.AudioService"));
        int removePreferredDevicesForStrategySync = this.mDeviceBroker.removePreferredDevicesForStrategySync(i);
        if (removePreferredDevicesForStrategySync != 0) {
            Log.e("AS.AudioService", String.format("Error %d in %s)", Integer.valueOf(removePreferredDevicesForStrategySync), format));
        }
        return removePreferredDevicesForStrategySync;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public List<AudioDeviceAttributes> getPreferredDevicesForStrategy(int i) {
        super.getPreferredDevicesForStrategy_enforcePermission();
        ArrayList arrayList = new ArrayList();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int devicesForRoleAndStrategy = AudioSystem.getDevicesForRoleAndStrategy(i, 1, arrayList);
            if (devicesForRoleAndStrategy != 0) {
                Log.e("AS.AudioService", String.format("Error %d in getPreferredDeviceForStrategy(%d)", Integer.valueOf(devicesForRoleAndStrategy), Integer.valueOf(i)));
                return new ArrayList();
            }
            return arrayList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int setDeviceAsNonDefaultForStrategy(int i, AudioDeviceAttributes audioDeviceAttributes) {
        super.setDeviceAsNonDefaultForStrategy_enforcePermission();
        Objects.requireNonNull(audioDeviceAttributes);
        String format = String.format("setDeviceAsNonDefaultForStrategy u/pid:%d/%d strat:%d dev:%s", Integer.valueOf(Binder.getCallingUid()), Integer.valueOf(Binder.getCallingPid()), Integer.valueOf(i), audioDeviceAttributes.toString());
        sDeviceLogger.enqueue(new EventLogger.StringEvent(format).printLog("AS.AudioService"));
        if (audioDeviceAttributes.getRole() == 1) {
            Log.e("AS.AudioService", "Unsupported input routing in " + format);
            return -1;
        }
        int deviceAsNonDefaultForStrategySync = this.mDeviceBroker.setDeviceAsNonDefaultForStrategySync(i, audioDeviceAttributes);
        if (deviceAsNonDefaultForStrategySync != 0) {
            Log.e("AS.AudioService", String.format("Error %d in %s)", Integer.valueOf(deviceAsNonDefaultForStrategySync), format));
        }
        return deviceAsNonDefaultForStrategySync;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int removeDeviceAsNonDefaultForStrategy(int i, AudioDeviceAttributes audioDeviceAttributes) {
        super.removeDeviceAsNonDefaultForStrategy_enforcePermission();
        Objects.requireNonNull(audioDeviceAttributes);
        String format = String.format("removeDeviceAsNonDefaultForStrategy strat:%d dev:%s", Integer.valueOf(i), audioDeviceAttributes.toString());
        sDeviceLogger.enqueue(new EventLogger.StringEvent(format).printLog("AS.AudioService"));
        if (audioDeviceAttributes.getRole() == 1) {
            Log.e("AS.AudioService", "Unsupported input routing in " + format);
            return -1;
        }
        int removeDeviceAsNonDefaultForStrategySync = this.mDeviceBroker.removeDeviceAsNonDefaultForStrategySync(i, audioDeviceAttributes);
        if (removeDeviceAsNonDefaultForStrategySync != 0) {
            Log.e("AS.AudioService", String.format("Error %d in %s)", Integer.valueOf(removeDeviceAsNonDefaultForStrategySync), format));
        }
        return removeDeviceAsNonDefaultForStrategySync;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public List<AudioDeviceAttributes> getNonDefaultDevicesForStrategy(int i) {
        super.getNonDefaultDevicesForStrategy_enforcePermission();
        ArrayList arrayList = new ArrayList();
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            int devicesForRoleAndStrategy = AudioSystem.getDevicesForRoleAndStrategy(i, 2, arrayList);
            if (create != null) {
                create.close();
            }
            if (devicesForRoleAndStrategy != 0) {
                Log.e("AS.AudioService", String.format("Error %d in getNonDefaultDeviceForStrategy(%d)", Integer.valueOf(devicesForRoleAndStrategy), Integer.valueOf(i)));
                return new ArrayList();
            }
            return arrayList;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public void registerStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) {
        if (iStrategyPreferredDevicesDispatcher == null) {
            return;
        }
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.registerStrategyPreferredDevicesDispatcher(iStrategyPreferredDevicesDispatcher);
    }

    public void unregisterStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) {
        if (iStrategyPreferredDevicesDispatcher == null) {
            return;
        }
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.unregisterStrategyPreferredDevicesDispatcher(iStrategyPreferredDevicesDispatcher);
    }

    public void registerStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) {
        if (iStrategyNonDefaultDevicesDispatcher == null) {
            return;
        }
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.registerStrategyNonDefaultDevicesDispatcher(iStrategyNonDefaultDevicesDispatcher);
    }

    public void unregisterStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) {
        if (iStrategyNonDefaultDevicesDispatcher == null) {
            return;
        }
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.unregisterStrategyNonDefaultDevicesDispatcher(iStrategyNonDefaultDevicesDispatcher);
    }

    public int setPreferredDevicesForCapturePreset(int i, List<AudioDeviceAttributes> list) {
        if (list == null) {
            return -1;
        }
        enforceModifyAudioRoutingPermission();
        String format = String.format("setPreferredDevicesForCapturePreset u/pid:%d/%d source:%d dev:%s", Integer.valueOf(Binder.getCallingUid()), Integer.valueOf(Binder.getCallingPid()), Integer.valueOf(i), list.stream().map(new Function() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String audioDeviceAttributes;
                audioDeviceAttributes = ((AudioDeviceAttributes) obj).toString();
                return audioDeviceAttributes;
            }
        }).collect(Collectors.joining(",")));
        sDeviceLogger.enqueue(new EventLogger.StringEvent(format).printLog("AS.AudioService"));
        if (list.stream().anyMatch(new Predicate() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$setPreferredDevicesForCapturePreset$9;
                lambda$setPreferredDevicesForCapturePreset$9 = AudioService.lambda$setPreferredDevicesForCapturePreset$9((AudioDeviceAttributes) obj);
                return lambda$setPreferredDevicesForCapturePreset$9;
            }
        })) {
            Log.e("AS.AudioService", "Unsupported output routing in " + format);
            return -1;
        }
        int preferredDevicesForCapturePresetSync = this.mDeviceBroker.setPreferredDevicesForCapturePresetSync(i, list);
        if (preferredDevicesForCapturePresetSync != 0) {
            Log.e("AS.AudioService", String.format("Error %d in %s)", Integer.valueOf(preferredDevicesForCapturePresetSync), format));
        }
        return preferredDevicesForCapturePresetSync;
    }

    public static /* synthetic */ boolean lambda$setPreferredDevicesForCapturePreset$9(AudioDeviceAttributes audioDeviceAttributes) {
        return audioDeviceAttributes.getRole() == 2;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int clearPreferredDevicesForCapturePreset(int i) {
        super.clearPreferredDevicesForCapturePreset_enforcePermission();
        String format = String.format("removePreferredDeviceForCapturePreset source:%d", Integer.valueOf(i));
        sDeviceLogger.enqueue(new EventLogger.StringEvent(format).printLog("AS.AudioService"));
        int clearPreferredDevicesForCapturePresetSync = this.mDeviceBroker.clearPreferredDevicesForCapturePresetSync(i);
        if (clearPreferredDevicesForCapturePresetSync != 0) {
            Log.e("AS.AudioService", String.format("Error %d in %s", Integer.valueOf(clearPreferredDevicesForCapturePresetSync), format));
        }
        return clearPreferredDevicesForCapturePresetSync;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public List<AudioDeviceAttributes> getPreferredDevicesForCapturePreset(int i) {
        super.getPreferredDevicesForCapturePreset_enforcePermission();
        ArrayList arrayList = new ArrayList();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int devicesForRoleAndCapturePreset = AudioSystem.getDevicesForRoleAndCapturePreset(i, 1, arrayList);
            if (devicesForRoleAndCapturePreset != 0) {
                Log.e("AS.AudioService", String.format("Error %d in getPreferredDeviceForCapturePreset(%d)", Integer.valueOf(devicesForRoleAndCapturePreset), Integer.valueOf(i)));
                return new ArrayList();
            }
            return arrayList;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) {
        if (iCapturePresetDevicesRoleDispatcher == null) {
            return;
        }
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.registerCapturePresetDevicesRoleDispatcher(iCapturePresetDevicesRoleDispatcher);
    }

    public void unregisterCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) {
        if (iCapturePresetDevicesRoleDispatcher == null) {
            return;
        }
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.unregisterCapturePresetDevicesRoleDispatcher(iCapturePresetDevicesRoleDispatcher);
    }

    /* renamed from: getDevicesForAttributes */
    public ArrayList<AudioDeviceAttributes> m1893getDevicesForAttributes(AudioAttributes audioAttributes) {
        enforceQueryStateOrModifyRoutingPermission();
        return getDevicesForAttributesInt(audioAttributes, false);
    }

    /* renamed from: getDevicesForAttributesUnprotected */
    public ArrayList<AudioDeviceAttributes> m1894getDevicesForAttributesUnprotected(AudioAttributes audioAttributes) {
        return getDevicesForAttributesInt(audioAttributes, false);
    }

    public boolean isMusicActive(boolean z) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (z) {
                return AudioSystem.isStreamActiveRemotely(3, 0);
            }
            return AudioSystem.isStreamActive(3, 0);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public ArrayList<AudioDeviceAttributes> getDevicesForAttributesInt(AudioAttributes audioAttributes, boolean z) {
        Objects.requireNonNull(audioAttributes);
        return this.mAudioSystem.getDevicesForAttributes(audioAttributes, z);
    }

    public void addOnDevicesForAttributesChangedListener(AudioAttributes audioAttributes, IDevicesForAttributesCallback iDevicesForAttributesCallback) {
        this.mAudioSystem.addOnDevicesForAttributesChangedListener(audioAttributes, false, iDevicesForAttributesCallback);
    }

    public void removeOnDevicesForAttributesChangedListener(IDevicesForAttributesCallback iDevicesForAttributesCallback) {
        this.mAudioSystem.removeOnDevicesForAttributesChangedListener(iDevicesForAttributesCallback);
    }

    public void handleVolumeKey(KeyEvent keyEvent, boolean z, String str, String str2) {
        int i;
        if (z) {
            i = keyEvent.getAction() == 0 ? 1 : 2;
        } else if (keyEvent.getAction() != 0) {
            return;
        } else {
            i = 0;
        }
        int i2 = i;
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 24) {
            adjustSuggestedStreamVolume(1, Integer.MIN_VALUE, 4101, str, str2, Binder.getCallingUid(), Binder.getCallingPid(), true, i2);
        } else if (keyCode == 25) {
            adjustSuggestedStreamVolume(-1, Integer.MIN_VALUE, 4101, str, str2, Binder.getCallingUid(), Binder.getCallingPid(), true, i2);
        } else if (keyCode == 164) {
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                adjustSuggestedStreamVolume(101, Integer.MIN_VALUE, 4101, str, str2, Binder.getCallingUid(), Binder.getCallingPid(), true, 0);
            }
        } else {
            Log.e("AS.AudioService", "Invalid key code " + keyEvent.getKeyCode() + " sent by " + str);
        }
    }

    public void setNavigationRepeatSoundEffectsEnabled(boolean z) {
        this.mNavigationRepeatSoundEffectsEnabled = z;
    }

    public boolean areNavigationRepeatSoundEffectsEnabled() {
        return this.mNavigationRepeatSoundEffectsEnabled;
    }

    public void setHomeSoundEffectEnabled(boolean z) {
        this.mHomeSoundEffectEnabled = z;
    }

    public boolean isHomeSoundEffectEnabled() {
        return this.mHomeSoundEffectEnabled;
    }

    public final void adjustSuggestedStreamVolume(int i, int i2, int i3, String str, String str2, int i4, int i5, boolean z, int i6) {
        int activeStreamType;
        boolean wasStreamActiveRecently;
        int i7;
        int i8;
        int i9;
        if (i != 0) {
            sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(0, i2, i, i3, str + "/" + str2 + " uid:" + i4));
        }
        boolean notifyExternalVolumeController = notifyExternalVolumeController(i);
        new MediaMetrics.Item("audio.service.adjustSuggestedStreamVolume").setUid(Binder.getCallingUid()).set(MediaMetrics.Property.CALLING_PACKAGE, str).set(MediaMetrics.Property.CLIENT_NAME, str2).set(MediaMetrics.Property.DIRECTION, i > 0 ? INetd.IF_STATE_UP : INetd.IF_STATE_DOWN).set(MediaMetrics.Property.EXTERNAL, notifyExternalVolumeController ? "yes" : "no").set(MediaMetrics.Property.FLAGS, Integer.valueOf(i3)).record();
        if (notifyExternalVolumeController) {
            return;
        }
        synchronized (this.mForceControlStreamLock) {
            if (this.mUserSelectedVolumeControlStream) {
                activeStreamType = this.mVolumeControlStream;
            } else {
                activeStreamType = getActiveStreamType(i2);
                if (activeStreamType != 2 && activeStreamType != 5) {
                    wasStreamActiveRecently = this.mAudioSystem.isStreamActive(activeStreamType, 0);
                    if (!wasStreamActiveRecently && (i7 = this.mVolumeControlStream) != -1) {
                        activeStreamType = i7;
                    }
                }
                wasStreamActiveRecently = wasStreamActiveRecently(activeStreamType, 0);
                if (!wasStreamActiveRecently) {
                    activeStreamType = i7;
                }
            }
        }
        boolean isMuteAdjust = isMuteAdjust(i);
        ensureValidStreamType(activeStreamType);
        int i10 = mStreamVolumeAlias[activeStreamType];
        int i11 = ((i3 & 4) == 0 || i10 == 2) ? i3 : i3 & (-5);
        if (!this.mVolumeController.suppressAdjustment(i10, i11, isMuteAdjust) || this.mIsSingleVolume) {
            i8 = i11;
            i9 = i;
        } else {
            i9 = 0;
            i8 = i11 & (-5) & (-17);
        }
        adjustStreamVolume(activeStreamType, i9, i8, str, str2, i4, i5, null, z, i6);
    }

    public final boolean notifyExternalVolumeController(int i) {
        IAudioPolicyCallback iAudioPolicyCallback;
        synchronized (this.mExtVolumeControllerLock) {
            iAudioPolicyCallback = this.mExtVolumeController;
        }
        if (iAudioPolicyCallback == null) {
            return false;
        }
        sendMsg(this.mAudioHandler, 22, 2, i, 0, iAudioPolicyCallback, 0);
        return true;
    }

    public void adjustStreamVolume(int i, int i2, int i3, String str) {
        adjustStreamVolumeWithAttribution(i, i2, i3, str, null);
    }

    public void adjustStreamVolumeWithAttribution(int i, int i2, int i3, String str, String str2) {
        if (i == 10 && !canChangeAccessibilityVolume()) {
            Log.w("AS.AudioService", "Trying to call adjustStreamVolume() for a11y withoutCHANGE_ACCESSIBILITY_VOLUME / callingPackage=" + str);
            return;
        }
        sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(1, i, i2, i3, str));
        adjustStreamVolume(i, i2, i3, str, str, Binder.getCallingUid(), Binder.getCallingPid(), str2, callingHasAudioSettingsPermission(), 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:143:0x02c4  */
    /* JADX WARN: Removed duplicated region for block: B:170:0x02fc A[Catch: all -> 0x0343, TRY_LEAVE, TryCatch #1 {, blocks: (B:145:0x02c7, B:147:0x02cb, B:151:0x02d4, B:155:0x02db, B:170:0x02fc, B:179:0x032c, B:188:0x033e, B:189:0x0341, B:175:0x0307, B:176:0x031e, B:177:0x0322, B:178:0x0326), top: B:196:0x02c7 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0122  */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0142  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0144  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x014e  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0158  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x015f  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x0184  */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v17 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r4v9, types: [int] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void adjustStreamVolume(int i, int i2, int i3, String str, String str2, int i4, int i5, String str3, boolean z, int i6) {
        int rescaleStep;
        int i7;
        int i8;
        boolean z2;
        boolean z3;
        boolean z4;
        int i9;
        ?? r4;
        int index;
        boolean z5;
        AbsoluteVolumeDeviceInfo absoluteVolumeDeviceInfo;
        int i10 = i4;
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidDirection(i2);
        ensureValidStreamType(i);
        boolean isMuteAdjust = isMuteAdjust(i2);
        if (!isMuteAdjust || isStreamAffectedByMute(i)) {
            if (isMuteAdjust && ((i == 0 || i == 6) && this.mContext.checkPermission("android.permission.MODIFY_PHONE_STATE", i5, i10) != 0)) {
                Log.w("AS.AudioService", "MODIFY_PHONE_STATE Permission Denial: adjustStreamVolume from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            } else if (i == 11 && this.mContext.checkPermission("android.permission.MODIFY_AUDIO_ROUTING", i5, i10) != 0) {
                Log.w("AS.AudioService", "MODIFY_AUDIO_ROUTING Permission Denial: adjustStreamVolume from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            } else {
                int i11 = mStreamVolumeAlias[i];
                VolumeStreamState volumeStreamState = this.mStreamStates[i11];
                int deviceForStream = getDeviceForStream(i11);
                int index2 = volumeStreamState.getIndex(deviceForStream);
                if (AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(deviceForStream)) || AudioSystem.DEVICE_OUT_ALL_BLE_SET.contains(Integer.valueOf(deviceForStream)) || (i3 & 64) == 0) {
                    if (i10 == 1000) {
                        i10 = UserHandle.getUid(getCurrentUserId(), UserHandle.getAppId(i4));
                    }
                    if (checkNoteAppOp(STREAM_VOLUME_OPS[i11], i10, str, str3)) {
                        this.mSoundDoseHelper.invalidatPendingVolumeCommand();
                        int i12 = i3 & (-33);
                        if (i11 == 3 && isFixedVolumeDevice(deviceForStream)) {
                            i12 |= 32;
                            rescaleStep = this.mSoundDoseHelper.getSafeMediaVolumeIndex(deviceForStream);
                            if (rescaleStep < 0) {
                                rescaleStep = volumeStreamState.getMaxIndex();
                            }
                            if (index2 != 0) {
                                i7 = rescaleStep;
                                i8 = i7;
                                if ((i12 & 2) == 0 || isUiSoundsStreamType(i11)) {
                                    if (getRingerModeInternal() == 1) {
                                        i12 &= -17;
                                    }
                                    int i13 = i12;
                                    z2 = true;
                                    int checkForRingerModeChange = checkForRingerModeChange(i7, i2, i8, volumeStreamState.mIsMuted, str, i13);
                                    z3 = (checkForRingerModeChange & 1) == 0;
                                    int i14 = (checkForRingerModeChange & 128) == 0 ? i13 | 128 : i13;
                                    i12 = (checkForRingerModeChange & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0 ? i14 | IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES : i14;
                                } else {
                                    z3 = true;
                                    z2 = true;
                                }
                                if (!volumeAdjustmentAllowedByDnd(i11, i12)) {
                                    z3 = false;
                                }
                                int index3 = this.mStreamStates[i].getIndex(deviceForStream);
                                if (isAbsoluteVolumeDevice(deviceForStream) && (i12 & IInstalld.FLAG_FORCE) == 0) {
                                    absoluteVolumeDeviceInfo = this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(deviceForStream));
                                    if (absoluteVolumeDeviceInfo.mHandlesVolumeAdjustment) {
                                        dispatchAbsoluteVolumeAdjusted(i, absoluteVolumeDeviceInfo, index3, i2, i6);
                                        return;
                                    }
                                }
                                if (z3 || i2 == 0 || i6 == 2) {
                                    z4 = false;
                                } else {
                                    this.mAudioHandler.removeMessages(18);
                                    if (isMuteAdjust && !this.mFullVolumeDevices.contains(Integer.valueOf(deviceForStream))) {
                                        if (i2 == 101) {
                                            z5 = volumeStreamState.mIsMuted ^ z2;
                                        } else {
                                            z5 = i2 == -100 ? z2 : false;
                                        }
                                        muteAliasStreams(i11, z5);
                                    } else if (i2 == z2 && this.mSoundDoseHelper.raiseVolumeDisplaySafeMediaVolume(i11, i7 + i8, deviceForStream, i12)) {
                                        Log.e("AS.AudioService", "adjustStreamVolume() safe volume index = " + index3);
                                    } else if (!isFullVolumeDevice(deviceForStream) && (volumeStreamState.adjustIndex(i2 * i8, deviceForStream, str2, z) || volumeStreamState.mIsMuted)) {
                                        if (!volumeStreamState.mIsMuted) {
                                            z4 = false;
                                        } else if (i2 == z2) {
                                            z4 = false;
                                            muteAliasStreams(i11, false);
                                        } else {
                                            z4 = false;
                                            if (i2 == -1 && this.mIsSingleVolume) {
                                                sendMsg(this.mAudioHandler, 18, 2, i11, i12, null, 350);
                                            }
                                        }
                                        sendMsg(this.mAudioHandler, 0, 2, deviceForStream, 0, volumeStreamState, 0);
                                        index = this.mStreamStates[i].getIndex(deviceForStream);
                                        if (i11 != 3 && AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(deviceForStream)) && (i12 & 64) == 0) {
                                            this.mDeviceBroker.postSetAvrcpAbsoluteVolumeIndex(index / 10);
                                        } else if (isAbsoluteVolumeDevice(deviceForStream) && (i12 & IInstalld.FLAG_FORCE) == 0) {
                                            dispatchAbsoluteVolumeChanged(i, this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(deviceForStream)), index);
                                        }
                                        if (AudioSystem.isLeAudioDeviceType(deviceForStream) && i == getBluetoothContextualVolumeStream() && (i12 & 64) == 0) {
                                            this.mDeviceBroker.postSetLeAudioVolumeIndex(index, this.mStreamStates[i].getMaxIndex(), i);
                                        }
                                        if (deviceForStream == 134217728 && i == getBluetoothContextualVolumeStream()) {
                                            this.mDeviceBroker.postSetHearingAidVolumeIndex(index, i);
                                        }
                                    }
                                    z4 = false;
                                    index = this.mStreamStates[i].getIndex(deviceForStream);
                                    if (i11 != 3) {
                                    }
                                    if (isAbsoluteVolumeDevice(deviceForStream)) {
                                        dispatchAbsoluteVolumeChanged(i, this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(deviceForStream)), index);
                                    }
                                    if (AudioSystem.isLeAudioDeviceType(deviceForStream)) {
                                        this.mDeviceBroker.postSetLeAudioVolumeIndex(index, this.mStreamStates[i].getMaxIndex(), i);
                                    }
                                    if (deviceForStream == 134217728) {
                                        this.mDeviceBroker.postSetHearingAidVolumeIndex(index, i);
                                    }
                                }
                                int index4 = this.mStreamStates[i].getIndex(deviceForStream);
                                if (z3) {
                                    synchronized (this.mHdmiClientLock) {
                                        if (this.mHdmiManager != null) {
                                            HdmiClient hdmiClient = this.mHdmiPlaybackClient;
                                            HdmiClient hdmiClient2 = this.mHdmiTvClient;
                                            if (hdmiClient2 != null) {
                                                hdmiClient = hdmiClient2;
                                            }
                                            if (hdmiClient != null && this.mHdmiCecVolumeControlEnabled) {
                                                if (i11 != 3) {
                                                    i9 = 3;
                                                    if (i11 == i9 && (index3 != index4 || isMuteAdjust)) {
                                                        maybeSendSystemAudioStatusCommand(isMuteAdjust);
                                                    }
                                                } else if (isFullVolumeDevice(deviceForStream)) {
                                                    if (i2 != -100) {
                                                        if (i2 == -1) {
                                                            r4 = 25;
                                                        } else if (i2 == z2) {
                                                            r4 = 24;
                                                        } else if (i2 != 100 && i2 != 101) {
                                                            r4 = z4;
                                                        }
                                                        if (r4 != 0) {
                                                            long clearCallingIdentity = Binder.clearCallingIdentity();
                                                            if (i6 == 0) {
                                                                hdmiClient.sendVolumeKeyEvent((int) r4, z2);
                                                                hdmiClient.sendVolumeKeyEvent((int) r4, z4);
                                                            } else if (i6 == z2) {
                                                                hdmiClient.sendVolumeKeyEvent((int) r4, z2);
                                                            } else if (i6 == 2) {
                                                                hdmiClient.sendVolumeKeyEvent((int) r4, z4);
                                                            } else {
                                                                Log.e("AS.AudioService", "Invalid keyEventMode " + i6);
                                                            }
                                                            Binder.restoreCallingIdentity(clearCallingIdentity);
                                                        }
                                                    }
                                                    r4 = 164;
                                                    if (r4 != 0) {
                                                    }
                                                }
                                            }
                                            i9 = 3;
                                            if (i11 == i9) {
                                                maybeSendSystemAudioStatusCommand(isMuteAdjust);
                                            }
                                        }
                                    }
                                }
                                sendVolumeUpdate(i, index3, index4, i12, deviceForStream);
                            }
                        } else {
                            rescaleStep = rescaleStep(10, i, i11);
                        }
                        i7 = index2;
                        i8 = rescaleStep;
                        if ((i12 & 2) == 0) {
                        }
                        if (getRingerModeInternal() == 1) {
                        }
                        int i132 = i12;
                        z2 = true;
                        int checkForRingerModeChange2 = checkForRingerModeChange(i7, i2, i8, volumeStreamState.mIsMuted, str, i132);
                        if ((checkForRingerModeChange2 & 1) == 0) {
                        }
                        if ((checkForRingerModeChange2 & 128) == 0) {
                        }
                        if ((checkForRingerModeChange2 & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0) {
                        }
                        if (!volumeAdjustmentAllowedByDnd(i11, i12)) {
                        }
                        int index32 = this.mStreamStates[i].getIndex(deviceForStream);
                        if (isAbsoluteVolumeDevice(deviceForStream)) {
                            absoluteVolumeDeviceInfo = this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(deviceForStream));
                            if (absoluteVolumeDeviceInfo.mHandlesVolumeAdjustment) {
                            }
                        }
                        if (z3) {
                        }
                        z4 = false;
                        int index42 = this.mStreamStates[i].getIndex(deviceForStream);
                        if (z3) {
                        }
                        sendVolumeUpdate(i, index32, index42, i12, deviceForStream);
                    }
                }
            }
        }
    }

    public final void muteAliasStreams(int i, final boolean z) {
        synchronized (VolumeStreamState.class) {
            ArrayList arrayList = new ArrayList();
            int i2 = 0;
            while (true) {
                VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                if (i2 < volumeStreamStateArr.length) {
                    VolumeStreamState volumeStreamState = volumeStreamStateArr[i2];
                    if (i == mStreamVolumeAlias[i2] && volumeStreamState.isMutable() && ((!readCameraSoundForced() || volumeStreamState.getStreamType() != 7) && volumeStreamState.mute(z, false))) {
                        arrayList.add(Integer.valueOf(i2));
                    }
                    i2++;
                } else {
                    arrayList.forEach(new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda1
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            AudioService.this.lambda$muteAliasStreams$10(z, (Integer) obj);
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$muteAliasStreams$10(boolean z, Integer num) {
        this.mStreamStates[num.intValue()].doMute();
        broadcastMuteSetting(num.intValue(), z);
    }

    public final void broadcastMuteSetting(int i, boolean z) {
        Intent intent = new Intent("android.media.STREAM_MUTE_CHANGED_ACTION");
        intent.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", i);
        intent.putExtra("android.media.EXTRA_STREAM_VOLUME_MUTED", z);
        sendBroadcastToAll(intent, null);
    }

    public final void onUnmuteStream(int i, int i2) {
        boolean mute;
        synchronized (VolumeStreamState.class) {
            VolumeStreamState volumeStreamState = this.mStreamStates[i];
            mute = volumeStreamState.mute(false);
            int deviceForStream = getDeviceForStream(i);
            int index = volumeStreamState.getIndex(deviceForStream);
            sendVolumeUpdate(i, index, index, i2, deviceForStream);
        }
        if (i == 3 && mute) {
            synchronized (this.mHdmiClientLock) {
                maybeSendSystemAudioStatusCommand(true);
            }
        }
    }

    @GuardedBy({"mHdmiClientLock"})
    public final void maybeSendSystemAudioStatusCommand(boolean z) {
        if (this.mHdmiAudioSystemClient != null && this.mHdmiSystemAudioSupported && this.mHdmiCecVolumeControlEnabled) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mHdmiAudioSystemClient.sendReportAudioStatusCecCommand(z, getStreamVolume(3), getStreamMaxVolume(3), isStreamMute(3));
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final int getNewRingerMode(int i, int i2, int i3) {
        int i4;
        if (this.mIsSingleVolume) {
            return getRingerModeExternal();
        }
        if ((i3 & 2) != 0 || i == getUiSoundsStreamType()) {
            if (i2 == 0) {
                if (this.mHasVibrator) {
                    i4 = 1;
                } else if (!this.mVolumePolicy.volumeDownToEnterSilent) {
                    return 2;
                } else {
                    i4 = 0;
                }
                return i4;
            }
            return 2;
        }
        return getRingerModeExternal();
    }

    public final boolean isAndroidNPlus(String str) {
        try {
            return this.mContext.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserId(Binder.getCallingUid())).targetSdkVersion >= 24;
        } catch (PackageManager.NameNotFoundException unused) {
            return true;
        }
    }

    public final boolean wouldToggleZenMode(int i) {
        if (getRingerModeExternal() != 0 || i == 0) {
            return getRingerModeExternal() != 0 && i == 0;
        }
        return true;
    }

    public void onSetStreamVolume(int i, int i2, int i3, int i4, String str, boolean z, boolean z2) {
        int i5 = mStreamVolumeAlias[i];
        setStreamVolumeInt(i5, i2, i4, false, str, z);
        if ((i3 & 2) != 0 || i5 == getUiSoundsStreamType()) {
            setRingerMode(getNewRingerMode(i5, i2, i3), "AS.AudioService.onSetStreamVolume", false);
        }
        if (i == 6 || !z2) {
            return;
        }
        muteAliasStreams(i5, i2 == 0);
    }

    public final void enforceModifyAudioRoutingPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0) {
            throw new SecurityException("Missing MODIFY_AUDIO_ROUTING permission");
        }
    }

    public final void enforceQueryStateOrModifyRoutingPermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.QUERY_AUDIO_STATE") != 0) {
            throw new SecurityException("Missing MODIFY_AUDIO_ROUTING or QUERY_AUDIO_STATE permissions");
        }
    }

    @EnforcePermission(anyOf = {"android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED", "android.permission.MODIFY_AUDIO_ROUTING"})
    public void setVolumeGroupVolumeIndex(int i, int i2, int i3, String str, String str2) {
        int[] legacyStreamTypes;
        super.setVolumeGroupVolumeIndex_enforcePermission();
        SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
        if (sparseArray.indexOfKey(i) < 0) {
            Log.e("AS.AudioService", ": no volume group found for id " + i);
            return;
        }
        VolumeGroupState volumeGroupState = sparseArray.get(i);
        sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(8, volumeGroupState.name(), i2, i3, str + ", user " + getCurrentUserId()));
        volumeGroupState.setVolumeIndex(i2, i3);
        for (int i4 : volumeGroupState.getLegacyStreamTypes()) {
            try {
                ensureValidStreamType(i4);
                setStreamVolume(i4, i2, i3, null, str, str, str2, Binder.getCallingUid(), true);
            } catch (IllegalArgumentException unused) {
                Log.d("AS.AudioService", "volume group " + i + " has internal streams (" + i4 + "), do not change associated stream volume");
            }
        }
    }

    @EnforcePermission(anyOf = {"android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED", "android.permission.MODIFY_AUDIO_ROUTING"})
    public int getVolumeGroupVolumeIndex(int i) {
        int volumeIndex;
        super.getVolumeGroupVolumeIndex_enforcePermission();
        synchronized (VolumeStreamState.class) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (sparseArray.indexOfKey(i) < 0) {
                throw new IllegalArgumentException("No volume group for id " + i);
            }
            VolumeGroupState volumeGroupState = sparseArray.get(i);
            volumeIndex = volumeGroupState.isMuted() ? 0 : volumeGroupState.getVolumeIndex();
        }
        return volumeIndex;
    }

    @EnforcePermission(anyOf = {"android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED", "android.permission.MODIFY_AUDIO_ROUTING"})
    public int getVolumeGroupMaxVolumeIndex(int i) {
        int maxIndex;
        super.getVolumeGroupMaxVolumeIndex_enforcePermission();
        synchronized (VolumeStreamState.class) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (sparseArray.indexOfKey(i) < 0) {
                throw new IllegalArgumentException("No volume group for id " + i);
            }
            maxIndex = sparseArray.get(i).getMaxIndex();
        }
        return maxIndex;
    }

    @EnforcePermission(anyOf = {"android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED", "android.permission.MODIFY_AUDIO_ROUTING"})
    public int getVolumeGroupMinVolumeIndex(int i) {
        int minIndex;
        super.getVolumeGroupMinVolumeIndex_enforcePermission();
        synchronized (VolumeStreamState.class) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (sparseArray.indexOfKey(i) < 0) {
                throw new IllegalArgumentException("No volume group for id " + i);
            }
            minIndex = sparseArray.get(i).getMinIndex();
        }
        return minIndex;
    }

    @EnforcePermission(anyOf = {"MODIFY_AUDIO_ROUTING", "MODIFY_AUDIO_SETTINGS_PRIVILEGED"})
    public void setDeviceVolume(VolumeInfo volumeInfo, AudioDeviceAttributes audioDeviceAttributes, String str) {
        super.setDeviceVolume_enforcePermission();
        Objects.requireNonNull(volumeInfo);
        Objects.requireNonNull(audioDeviceAttributes);
        Objects.requireNonNull(str);
        if (!volumeInfo.hasStreamType()) {
            Log.e("AS.AudioService", "Unsupported non-stream type based VolumeInfo", new Exception());
            return;
        }
        int volumeIndex = volumeInfo.getVolumeIndex();
        if (volumeIndex == -100 && !volumeInfo.hasMuteCommand()) {
            throw new IllegalArgumentException("changing device volume requires a volume index or mute command");
        }
        if (volumeInfo.hasMuteCommand() && volumeInfo.isMuted() && !isStreamMute(volumeInfo.getStreamType())) {
            setStreamVolumeWithAttributionInt(volumeInfo.getStreamType(), this.mStreamStates[volumeInfo.getStreamType()].getMinIndex(), 0, audioDeviceAttributes, str, null);
            return;
        }
        EventLogger eventLogger = sVolumeLogger;
        eventLogger.enqueueAndLog("setDeviceVolume from:" + str + " " + volumeInfo + " " + audioDeviceAttributes, 0, "AS.AudioService");
        if (volumeInfo.getMinVolumeIndex() == -100 || volumeInfo.getMaxVolumeIndex() == -100) {
            int i = volumeIndex * 10;
            if (i < this.mStreamStates[volumeInfo.getStreamType()].getMinIndex() || i > this.mStreamStates[volumeInfo.getStreamType()].getMaxIndex()) {
                throw new IllegalArgumentException("invalid volume index " + volumeIndex + " not between min/max for stream " + volumeInfo.getStreamType());
            }
        } else {
            int minIndex = (this.mStreamStates[volumeInfo.getStreamType()].getMinIndex() + 5) / 10;
            int maxIndex = (this.mStreamStates[volumeInfo.getStreamType()].getMaxIndex() + 5) / 10;
            if (volumeInfo.getMinVolumeIndex() != minIndex || volumeInfo.getMaxVolumeIndex() != maxIndex) {
                volumeIndex = rescaleIndex(volumeIndex, volumeInfo.getMinVolumeIndex(), volumeInfo.getMaxVolumeIndex(), minIndex, maxIndex);
            }
        }
        setStreamVolumeWithAttributionInt(volumeInfo.getStreamType(), volumeIndex, 0, audioDeviceAttributes, str, null);
    }

    public void setStreamVolume(int i, int i2, int i3, String str) {
        setStreamVolumeWithAttribution(i, i2, i3, str, null);
    }

    public void adjustVolumeGroupVolume(int i, int i2, int i3, String str) {
        int[] legacyStreamTypes;
        ensureValidDirection(i2);
        SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
        if (sparseArray.indexOfKey(i) < 0) {
            Log.e("AS.AudioService", ": no volume group found for id " + i);
            return;
        }
        VolumeGroupState volumeGroupState = sparseArray.get(i);
        boolean z = false;
        for (int i4 : volumeGroupState.getLegacyStreamTypes()) {
            try {
                ensureValidStreamType(i4);
                if (volumeGroupState.isVssMuteBijective(i4)) {
                    adjustStreamVolume(i4, i2, i3, str);
                    if (isMuteAdjust(i2)) {
                        return;
                    }
                    z = true;
                } else {
                    continue;
                }
            } catch (IllegalArgumentException unused) {
                Log.d("AS.AudioService", "volume group " + i + " has internal streams (" + i4 + "), do not change associated stream volume");
            }
        }
        if (z) {
            return;
        }
        sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(11, volumeGroupState.name(), i2, i3, str));
        volumeGroupState.adjustVolume(i2, i3);
    }

    @EnforcePermission("android.permission.QUERY_AUDIO_STATE")
    public int getLastAudibleVolumeForVolumeGroup(int i) {
        super.getLastAudibleVolumeForVolumeGroup_enforcePermission();
        synchronized (VolumeStreamState.class) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (sparseArray.indexOfKey(i) < 0) {
                Log.e("AS.AudioService", ": no volume group found for id " + i);
                return 0;
            }
            return sparseArray.get(i).getVolumeIndex();
        }
    }

    public boolean isVolumeGroupMuted(int i) {
        synchronized (VolumeStreamState.class) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (sparseArray.indexOfKey(i) < 0) {
                Log.e("AS.AudioService", ": no volume group found for id " + i);
                return false;
            }
            return sparseArray.get(i).isMuted();
        }
    }

    public void setStreamVolumeWithAttribution(int i, int i2, int i3, String str, String str2) {
        setStreamVolumeWithAttributionInt(i, i2, i3, null, str, str2);
    }

    public void setStreamVolumeWithAttributionInt(final int i, final int i2, int i3, final AudioDeviceAttributes audioDeviceAttributes, final String str, String str2) {
        EventLogger.Event event;
        if (i == 10 && !canChangeAccessibilityVolume()) {
            Log.w("AS.AudioService", "Trying to call setStreamVolume() for a11y without CHANGE_ACCESSIBILITY_VOLUME  callingPackage=" + str);
        } else if (i == 0 && i2 == 0 && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            Log.w("AS.AudioService", "Trying to call setStreamVolume() for STREAM_VOICE_CALL and index 0 without MODIFY_PHONE_STATE  callingPackage=" + str);
        } else if (i == 11 && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0) {
            Log.w("AS.AudioService", "Trying to call setStreamVolume() for STREAM_ASSISTANT without MODIFY_AUDIO_ROUTING  callingPackage=" + str);
        } else {
            if (audioDeviceAttributes == null) {
                event = new AudioServiceEvents$VolumeEvent(2, i, i2, i3, str);
            } else {
                event = new EventLogger.Event(i, i2, audioDeviceAttributes, str) { // from class: com.android.server.audio.AudioServiceEvents$DeviceVolumeEvent
                    public final String mCaller;
                    public final String mDeviceAddress;
                    public final String mDeviceNativeType;
                    public final int mStream;
                    public final int mVolIndex;

                    {
                        this.mStream = i;
                        this.mVolIndex = i2;
                        String str3 = "0x" + Integer.toHexString(audioDeviceAttributes.getInternalType());
                        this.mDeviceNativeType = str3;
                        String address = audioDeviceAttributes.getAddress();
                        this.mDeviceAddress = address;
                        this.mCaller = str;
                        new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.EVENT, "setDeviceVolume").set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(i)).set(MediaMetrics.Property.INDEX, Integer.valueOf(i2)).set(MediaMetrics.Property.DEVICE, str3).set(MediaMetrics.Property.ADDRESS, address).set(MediaMetrics.Property.CALLING_PACKAGE, str).record();
                    }

                    @Override // com.android.server.utils.EventLogger.Event
                    public String eventToString() {
                        return "setDeviceVolume(stream:" + AudioSystem.streamToString(this.mStream) + " index:" + this.mVolIndex + " device:" + this.mDeviceNativeType + " addr:" + this.mDeviceAddress + ") from " + this.mCaller;
                    }
                };
            }
            sVolumeLogger.enqueue(event);
            setStreamVolume(i, i2, i3, audioDeviceAttributes, str, str, str2, Binder.getCallingUid(), callingOrSelfHasAudioSettingsPermission());
        }
    }

    @EnforcePermission("android.permission.ACCESS_ULTRASOUND")
    public boolean isUltrasoundSupported() {
        super.isUltrasoundSupported_enforcePermission();
        return AudioSystem.isUltrasoundSupported();
    }

    @EnforcePermission("android.permission.CAPTURE_AUDIO_HOTWORD")
    public boolean isHotwordStreamSupported(boolean z) {
        super.isHotwordStreamSupported_enforcePermission();
        try {
            return this.mAudioPolicy.isHotwordStreamSupported(z);
        } catch (IllegalStateException e) {
            Log.e("AS.AudioService", "Suppressing exception calling into AudioPolicy", e);
            return false;
        }
    }

    public final boolean canChangeAccessibilityVolume() {
        synchronized (this.mAccessibilityServiceUidsLock) {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.CHANGE_ACCESSIBILITY_VOLUME") == 0) {
                return true;
            }
            if (this.mAccessibilityServiceUids != null) {
                int callingUid = Binder.getCallingUid();
                int i = 0;
                while (true) {
                    int[] iArr = this.mAccessibilityServiceUids;
                    if (i >= iArr.length) {
                        break;
                    } else if (iArr[i] == callingUid) {
                        return true;
                    } else {
                        i++;
                    }
                }
            }
            return false;
        }
    }

    public int getBluetoothContextualVolumeStream() {
        return getBluetoothContextualVolumeStream(this.mMode.get());
    }

    public final int getBluetoothContextualVolumeStream(int i) {
        return (i == 2 || i == 3 || this.mVoicePlaybackActive.get()) ? 0 : 3;
    }

    public final void onPlaybackConfigChange(List<AudioPlaybackConfiguration> list) {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (AudioPlaybackConfiguration audioPlaybackConfiguration : list) {
            int usage = audioPlaybackConfiguration.getAudioAttributes().getUsage();
            if (audioPlaybackConfiguration.isActive()) {
                z2 = (usage == 2 || usage == 3) ? true : true;
                if (usage == 1 || usage == 14) {
                    z3 = true;
                }
            }
        }
        if (this.mVoicePlaybackActive.getAndSet(z2) != z2) {
            updateHearingAidVolumeOnVoiceActivityUpdate();
        }
        if (this.mMediaPlaybackActive.getAndSet(z3) != z3 && z3) {
            this.mSoundDoseHelper.scheduleMusicActiveCheck();
        }
        synchronized (this.mDeviceBroker.mSetModeLock) {
            Iterator<SetModeDeathHandler> it = this.mSetModeDeathHandlers.iterator();
            int i = 6000;
            int i2 = 2;
            boolean z4 = false;
            while (it.hasNext()) {
                SetModeDeathHandler next = it.next();
                boolean isActive = next.isActive();
                next.setPlaybackActive(z);
                Iterator<AudioPlaybackConfiguration> it2 = list.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    AudioPlaybackConfiguration next2 = it2.next();
                    int usage2 = next2.getAudioAttributes().getUsage();
                    if (next2.getClientUid() == next.getUid() && ((usage2 == 2 || usage2 == 3) && next2.getPlayerState() == 2)) {
                        next.setPlaybackActive(true);
                        break;
                    }
                }
                if (isActive != next.isActive()) {
                    if (next.isActive() && next == getAudioModeOwnerHandler()) {
                        z4 = true;
                        i2 = 0;
                        i = 0;
                    } else {
                        z4 = true;
                    }
                }
                z = false;
            }
            if (z4) {
                sendMsg(this.mAudioHandler, 36, i2, -1, Process.myPid(), this.mContext.getPackageName(), i);
            }
        }
    }

    public final void onRecordingConfigChange(List<AudioRecordingConfiguration> list) {
        synchronized (this.mDeviceBroker.mSetModeLock) {
            Iterator<SetModeDeathHandler> it = this.mSetModeDeathHandlers.iterator();
            int i = 2;
            int i2 = 6000;
            boolean z = false;
            while (it.hasNext()) {
                SetModeDeathHandler next = it.next();
                boolean isActive = next.isActive();
                next.setRecordingActive(false);
                Iterator<AudioRecordingConfiguration> it2 = list.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    AudioRecordingConfiguration next2 = it2.next();
                    if (next2.getClientUid() == next.getUid() && next2.getAudioSource() == 7) {
                        next.setRecordingActive(true);
                        break;
                    }
                }
                if (isActive != next.isActive()) {
                    if (next.isActive() && next == getAudioModeOwnerHandler()) {
                        i = 0;
                        i2 = 0;
                    }
                    z = true;
                }
            }
            if (z) {
                sendMsg(this.mAudioHandler, 36, i, -1, Process.myPid(), this.mContext.getPackageName(), i2);
            }
        }
    }

    public final void dumpAudioMode(PrintWriter printWriter) {
        printWriter.println("\nAudio mode: ");
        printWriter.println("- Requested mode = " + AudioSystem.modeToString(getMode()));
        printWriter.println("- Actual mode = " + AudioSystem.modeToString(this.mMode.get()));
        printWriter.println("- Mode owner: ");
        SetModeDeathHandler audioModeOwnerHandler = getAudioModeOwnerHandler();
        if (audioModeOwnerHandler != null) {
            audioModeOwnerHandler.dump(printWriter, -1);
        } else {
            printWriter.println("   None");
        }
        printWriter.println("- Mode owner stack: ");
        if (this.mSetModeDeathHandlers.isEmpty()) {
            printWriter.println("   Empty");
            return;
        }
        for (int i = 0; i < this.mSetModeDeathHandlers.size(); i++) {
            this.mSetModeDeathHandlers.get(i).dump(printWriter, i);
        }
    }

    public final void updateHearingAidVolumeOnVoiceActivityUpdate() {
        int bluetoothContextualVolumeStream = getBluetoothContextualVolumeStream();
        int streamVolume = getStreamVolume(bluetoothContextualVolumeStream);
        sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(6, this.mVoicePlaybackActive.get(), bluetoothContextualVolumeStream, streamVolume));
        this.mDeviceBroker.postSetHearingAidVolumeIndex(streamVolume * 10, bluetoothContextualVolumeStream);
    }

    public void updateAbsVolumeMultiModeDevices(int i, int i2) {
        if (i == i2) {
            return;
        }
        if (i2 == 0 || i2 == 2 || i2 == 3 || i2 == 4 || i2 == 5 || i2 == 6) {
            int bluetoothContextualVolumeStream = getBluetoothContextualVolumeStream(i2);
            Set intersectionAudioDeviceTypes = AudioSystem.intersectionAudioDeviceTypes(this.mAbsVolumeMultiModeCaseDevices, getDeviceSetForStreamDirect(bluetoothContextualVolumeStream));
            if (!intersectionAudioDeviceTypes.isEmpty() && AudioSystem.isSingleAudioDeviceType(intersectionAudioDeviceTypes, 134217728)) {
                int streamVolume = getStreamVolume(bluetoothContextualVolumeStream);
                sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(7, i2, bluetoothContextualVolumeStream, streamVolume));
                this.mDeviceBroker.postSetHearingAidVolumeIndex(streamVolume * 10, bluetoothContextualVolumeStream);
            }
        }
    }

    public final void setLeAudioVolumeOnModeUpdate(int i, int i2, int i3, int i4, int i5) {
        if (i == 0 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6) {
            if (!AudioSystem.isLeAudioDeviceType(i2)) {
                StringBuilder sb = new StringBuilder();
                sb.append("setLeAudioVolumeOnModeUpdate got unexpected device=");
                sb.append(i2);
                sb.append(", forcing to device=");
                i2 = 536870912;
                sb.append(536870912);
                Log.w("AS.AudioService", sb.toString());
            }
            this.mDeviceBroker.postSetLeAudioVolumeIndex(i4, i5, i3);
            this.mDeviceBroker.postApplyVolumeOnDevice(i3, i2, "setLeAudioVolumeOnModeUpdate");
        }
    }

    public final void setStreamVolume(int i, int i2, int i3, AudioDeviceAttributes audioDeviceAttributes, String str, String str2, String str3, int i4, boolean z) {
        int internalType;
        int i5;
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidStreamType(i);
        int i6 = mStreamVolumeAlias[i];
        VolumeStreamState volumeStreamState = this.mStreamStates[i6];
        if (audioDeviceAttributes == null) {
            internalType = getDeviceForStream(i);
        } else {
            internalType = audioDeviceAttributes.getInternalType();
        }
        int i7 = internalType;
        if (AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(i7)) || AudioSystem.DEVICE_OUT_ALL_BLE_SET.contains(Integer.valueOf(i7)) || (i3 & 64) == 0) {
            int i8 = i4;
            if (i8 == 1000) {
                i8 = UserHandle.getUid(getCurrentUserId(), UserHandle.getAppId(i4));
            }
            if (checkNoteAppOp(STREAM_VOLUME_OPS[i6], i8, str, str3)) {
                if (isAndroidNPlus(str) && wouldToggleZenMode(getNewRingerMode(i6, i2, i3)) && !this.mNm.isNotificationPolicyAccessGrantedForPackage(str)) {
                    throw new SecurityException("Not allowed to change Do Not Disturb state");
                }
                if (volumeAdjustmentAllowedByDnd(i6, i3)) {
                    this.mSoundDoseHelper.invalidatPendingVolumeCommand();
                    int index = volumeStreamState.getIndex(i7);
                    int rescaleIndex = rescaleIndex(i2 * 10, i, i6);
                    if (i6 == 3 && AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(i7)) && (i3 & 64) == 0) {
                        this.mDeviceBroker.postSetAvrcpAbsoluteVolumeIndex(rescaleIndex / 10);
                    } else if (isAbsoluteVolumeDevice(i7) && (i3 & IInstalld.FLAG_FORCE) == 0) {
                        dispatchAbsoluteVolumeChanged(i, this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(i7)), rescaleIndex);
                    }
                    if (AudioSystem.isLeAudioDeviceType(i7) && i == getBluetoothContextualVolumeStream() && (i3 & 64) == 0) {
                        this.mDeviceBroker.postSetLeAudioVolumeIndex(rescaleIndex, this.mStreamStates[i].getMaxIndex(), i);
                    }
                    if (i7 == 134217728 && i == getBluetoothContextualVolumeStream()) {
                        Log.i("AS.AudioService", "setStreamVolume postSetHearingAidVolumeIndex index=" + rescaleIndex + " stream=" + i);
                        this.mDeviceBroker.postSetHearingAidVolumeIndex(rescaleIndex, i);
                    }
                    int i9 = i3 & (-33);
                    if (i6 == 3 && isFixedVolumeDevice(i7)) {
                        i9 |= 32;
                        if (rescaleIndex != 0 && (rescaleIndex = this.mSoundDoseHelper.getSafeMediaVolumeIndex(i7)) < 0) {
                            rescaleIndex = volumeStreamState.getMaxIndex();
                        }
                    }
                    int i10 = rescaleIndex;
                    int i11 = i9;
                    if (this.mSoundDoseHelper.willDisplayWarningAfterCheckVolume(i, i10, i7, i11)) {
                        i5 = i10;
                    } else {
                        onSetStreamVolume(i, i10, i11, i7, str2, z, audioDeviceAttributes == null);
                        i5 = this.mStreamStates[i].getIndex(i7);
                    }
                    synchronized (this.mHdmiClientLock) {
                        if (i6 == 3 && index != i5) {
                            maybeSendSystemAudioStatusCommand(false);
                        }
                    }
                    sendVolumeUpdate(i, index, i5, i11, i7);
                }
            }
        }
    }

    public final void dispatchAbsoluteVolumeChanged(int i, AbsoluteVolumeDeviceInfo absoluteVolumeDeviceInfo, int i2) {
        VolumeInfo matchingVolumeInfoForStream = absoluteVolumeDeviceInfo.getMatchingVolumeInfoForStream(i);
        if (matchingVolumeInfoForStream != null) {
            try {
                absoluteVolumeDeviceInfo.mCallback.dispatchDeviceVolumeChanged(absoluteVolumeDeviceInfo.mDevice, new VolumeInfo.Builder(matchingVolumeInfoForStream).setVolumeIndex(rescaleIndex(i2, i, matchingVolumeInfoForStream)).build());
            } catch (RemoteException unused) {
                Log.w("AS.AudioService", "Couldn't dispatch absolute volume behavior volume change");
            }
        }
    }

    public final void dispatchAbsoluteVolumeAdjusted(int i, AbsoluteVolumeDeviceInfo absoluteVolumeDeviceInfo, int i2, int i3, int i4) {
        VolumeInfo matchingVolumeInfoForStream = absoluteVolumeDeviceInfo.getMatchingVolumeInfoForStream(i);
        if (matchingVolumeInfoForStream != null) {
            try {
                absoluteVolumeDeviceInfo.mCallback.dispatchDeviceVolumeAdjusted(absoluteVolumeDeviceInfo.mDevice, new VolumeInfo.Builder(matchingVolumeInfoForStream).setVolumeIndex(rescaleIndex(i2, i, matchingVolumeInfoForStream)).build(), i3, i4);
            } catch (RemoteException unused) {
                Log.w("AS.AudioService", "Couldn't dispatch absolute volume behavior volume adjustment");
            }
        }
    }

    public final boolean volumeAdjustmentAllowedByDnd(int i, int i2) {
        int zenMode = this.mNm.getZenMode();
        return ((zenMode == 1 || zenMode == 2 || zenMode == 3) && isStreamMutedByRingerOrZenMode(i) && !isUiSoundsStreamType(i) && (i2 & 2) == 0) ? false : true;
    }

    public void forceVolumeControlStream(int i, IBinder iBinder) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            return;
        }
        synchronized (this.mForceControlStreamLock) {
            if (this.mVolumeControlStream != -1 && i != -1) {
                this.mUserSelectedVolumeControlStream = true;
            }
            this.mVolumeControlStream = i;
            if (i == -1) {
                ForceControlStreamClient forceControlStreamClient = this.mForceControlStreamClient;
                if (forceControlStreamClient != null) {
                    forceControlStreamClient.release();
                    this.mForceControlStreamClient = null;
                }
                this.mUserSelectedVolumeControlStream = false;
            } else {
                ForceControlStreamClient forceControlStreamClient2 = this.mForceControlStreamClient;
                if (forceControlStreamClient2 == null) {
                    this.mForceControlStreamClient = new ForceControlStreamClient(iBinder);
                } else if (forceControlStreamClient2.getBinder() == iBinder) {
                    Log.d("AS.AudioService", "forceVolumeControlStream cb:" + iBinder + " is already linked.");
                } else {
                    this.mForceControlStreamClient.release();
                    this.mForceControlStreamClient = new ForceControlStreamClient(iBinder);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class ForceControlStreamClient implements IBinder.DeathRecipient {
        public IBinder mCb;

        public ForceControlStreamClient(IBinder iBinder) {
            if (iBinder != null) {
                try {
                    iBinder.linkToDeath(this, 0);
                } catch (RemoteException unused) {
                    Log.w("AS.AudioService", "ForceControlStreamClient() could not link to " + iBinder + " binder death");
                    iBinder = null;
                }
            }
            this.mCb = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mForceControlStreamLock) {
                Log.w("AS.AudioService", "SCO client died");
                if (AudioService.this.mForceControlStreamClient != this) {
                    Log.w("AS.AudioService", "unregistered control stream client died");
                } else {
                    AudioService.this.mForceControlStreamClient = null;
                    AudioService.this.mVolumeControlStream = -1;
                    AudioService.this.mUserSelectedVolumeControlStream = false;
                }
            }
        }

        public void release() {
            IBinder iBinder = this.mCb;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mCb = null;
            }
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    public final void sendBroadcastToAll(Intent intent, Bundle bundle) {
        if (this.mSystemServer.isPrivileged()) {
            intent.addFlags(67108864);
            intent.addFlags(268435456);
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, null, bundle);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public final void sendStickyBroadcastToAll(Intent intent) {
        intent.addFlags(268435456);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int getCurrentUserId() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int i = ActivityManager.getService().getCurrentUser().id;
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return i;
        } catch (RemoteException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void sendVolumeUpdate(int i, int i2, int i3, int i4, int i5) {
        int i6 = mStreamVolumeAlias[i];
        if (i6 == 3 && isFullVolumeDevice(i5)) {
            i4 &= -2;
        }
        this.mVolumeController.postVolumeChanged(i6, i4);
    }

    public final int updateFlagsForTvPlatform(int i) {
        synchronized (this.mHdmiClientLock) {
            if (this.mHdmiTvClient != null && this.mHdmiSystemAudioSupported && this.mHdmiCecVolumeControlEnabled) {
                i &= -2;
            }
        }
        return i;
    }

    public final void sendMasterMuteUpdate(boolean z, int i) {
        this.mVolumeController.postMasterMuteChanged(updateFlagsForTvPlatform(i));
        broadcastMasterMuteStatus(z);
    }

    public final void broadcastMasterMuteStatus(boolean z) {
        Intent intent = new Intent("android.media.MASTER_MUTE_CHANGED_ACTION");
        intent.putExtra("android.media.EXTRA_MASTER_VOLUME_MUTED", z);
        intent.addFlags(603979776);
        sendStickyBroadcastToAll(intent);
    }

    public final void setStreamVolumeInt(int i, int i2, int i3, boolean z, String str, boolean z2) {
        if (isFullVolumeDevice(i3)) {
            return;
        }
        VolumeStreamState volumeStreamState = this.mStreamStates[i];
        if (volumeStreamState.setIndex(i2, i3, str, z2) || z) {
            sendMsg(this.mAudioHandler, 0, 2, i3, 0, volumeStreamState, 0);
        }
    }

    public boolean isStreamMute(int i) {
        boolean z;
        if (i == Integer.MIN_VALUE) {
            i = getActiveStreamType(i);
        }
        synchronized (VolumeStreamState.class) {
            ensureValidStreamType(i);
            z = this.mStreamStates[i].mIsMuted;
        }
        return z;
    }

    /* loaded from: classes.dex */
    public class RmtSbmxFullVolDeathHandler implements IBinder.DeathRecipient {
        public IBinder mICallback;

        public RmtSbmxFullVolDeathHandler(IBinder iBinder) {
            this.mICallback = iBinder;
            try {
                iBinder.linkToDeath(this, 0);
            } catch (RemoteException e) {
                Log.e("AS.AudioService", "can't link to death", e);
            }
        }

        public boolean isHandlerFor(IBinder iBinder) {
            return this.mICallback.equals(iBinder);
        }

        public void forget() {
            try {
                this.mICallback.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
                Log.e("AS.AudioService", "error unlinking to death", e);
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w("AS.AudioService", "Recorder with remote submix at full volume died " + this.mICallback);
            AudioService.this.forceRemoteSubmixFullVolume(false, this.mICallback);
        }
    }

    public final boolean discardRmtSbmxFullVolDeathHandlerFor(IBinder iBinder) {
        Iterator<RmtSbmxFullVolDeathHandler> it = this.mRmtSbmxFullVolDeathHandlers.iterator();
        while (it.hasNext()) {
            RmtSbmxFullVolDeathHandler next = it.next();
            if (next.isHandlerFor(iBinder)) {
                next.forget();
                this.mRmtSbmxFullVolDeathHandlers.remove(next);
                return true;
            }
        }
        return false;
    }

    public final boolean hasRmtSbmxFullVolDeathHandlerFor(IBinder iBinder) {
        Iterator<RmtSbmxFullVolDeathHandler> it = this.mRmtSbmxFullVolDeathHandlers.iterator();
        while (it.hasNext()) {
            if (it.next().isHandlerFor(iBinder)) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0071 A[Catch: all -> 0x007e, TryCatch #0 {, blocks: (B:12:0x001f, B:14:0x0025, B:16:0x0033, B:17:0x0046, B:27:0x0071, B:28:0x007c, B:18:0x004c, B:20:0x0052, B:22:0x0056, B:24:0x005b), top: B:33:0x001d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void forceRemoteSubmixFullVolume(boolean z, IBinder iBinder) {
        int i;
        if (iBinder == null) {
            return;
        }
        if (this.mContext.checkCallingOrSelfPermission("android.permission.CAPTURE_AUDIO_OUTPUT") != 0) {
            Log.w("AS.AudioService", "Trying to call forceRemoteSubmixFullVolume() without CAPTURE_AUDIO_OUTPUT");
            return;
        }
        synchronized (this.mRmtSbmxFullVolDeathHandlers) {
            boolean z2 = true;
            boolean z3 = false;
            if (z) {
                if (!hasRmtSbmxFullVolDeathHandlerFor(iBinder)) {
                    this.mRmtSbmxFullVolDeathHandlers.add(new RmtSbmxFullVolDeathHandler(iBinder));
                    if (this.mRmtSbmxFullVolRefCount == 0) {
                        this.mFullVolumeDevices.add(32768);
                        this.mFixedVolumeDevices.add(32768);
                        z3 = true;
                    }
                    this.mRmtSbmxFullVolRefCount++;
                }
            } else if (discardRmtSbmxFullVolDeathHandlerFor(iBinder) && (i = this.mRmtSbmxFullVolRefCount) > 0) {
                int i2 = i - 1;
                this.mRmtSbmxFullVolRefCount = i2;
                if (i2 == 0) {
                    this.mFullVolumeDevices.remove(32768);
                    this.mFixedVolumeDevices.remove(32768);
                    if (z2) {
                        checkAllFixedVolumeDevices(3);
                        this.mStreamStates[3].applyAllVolumes();
                    }
                }
            }
            z2 = z3;
            if (z2) {
            }
        }
    }

    public final void setMasterMuteInternal(boolean z, int i, String str, int i2, int i3, int i4, String str2) {
        if (i2 == 1000) {
            i2 = UserHandle.getUid(i3, UserHandle.getAppId(i2));
        }
        if (z || checkNoteAppOp(33, i2, str, str2)) {
            if (i3 == UserHandle.getCallingUserId() || this.mContext.checkPermission("android.permission.INTERACT_ACROSS_USERS_FULL", i4, i2) == 0) {
                setMasterMuteInternalNoCallerCheck(z, i, i3);
            }
        }
    }

    public final void setMasterMuteInternalNoCallerCheck(boolean z, int i, int i2) {
        if (isPlatformAutomotive() || !this.mUseFixedVolume) {
            if (!((isPlatformAutomotive() && i2 == 0) || getCurrentUserId() == i2) || z == AudioSystem.getMasterMute()) {
                return;
            }
            AudioSystem.setMasterMute(z);
            sendMasterMuteUpdate(z, i);
        }
    }

    public boolean isMasterMute() {
        return AudioSystem.getMasterMute();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void setMasterMute(boolean z, int i, String str, int i2, String str2) {
        super.setMasterMute_enforcePermission();
        setMasterMuteInternal(z, i, str, Binder.getCallingUid(), i2, Binder.getCallingPid(), str2);
    }

    public int getStreamVolume(int i) {
        int i2;
        ensureValidStreamType(i);
        int deviceForStream = getDeviceForStream(i);
        synchronized (VolumeStreamState.class) {
            int index = this.mStreamStates[i].getIndex(deviceForStream);
            if (this.mStreamStates[i].mIsMuted) {
                index = 0;
            }
            if (index != 0 && mStreamVolumeAlias[i] == 3 && isFixedVolumeDevice(deviceForStream)) {
                index = this.mStreamStates[i].getMaxIndex();
            }
            i2 = (index + 5) / 10;
        }
        return i2;
    }

    @EnforcePermission(anyOf = {"MODIFY_AUDIO_ROUTING", "MODIFY_AUDIO_SETTINGS_PRIVILEGED"})
    public VolumeInfo getDeviceVolume(VolumeInfo volumeInfo, AudioDeviceAttributes audioDeviceAttributes, String str) {
        int index;
        VolumeInfo build;
        super.getDeviceVolume_enforcePermission();
        Objects.requireNonNull(volumeInfo);
        Objects.requireNonNull(audioDeviceAttributes);
        Objects.requireNonNull(str);
        if (!volumeInfo.hasStreamType()) {
            Log.e("AS.AudioService", "Unsupported non-stream type based VolumeInfo", new Exception());
            return getDefaultVolumeInfo();
        }
        int streamType = volumeInfo.getStreamType();
        VolumeInfo.Builder builder = new VolumeInfo.Builder(volumeInfo);
        builder.setMinVolumeIndex(this.mStreamStates[streamType].mIndexMin);
        builder.setMaxVolumeIndex(this.mStreamStates[streamType].mIndexMax);
        synchronized (VolumeStreamState.class) {
            if (isFixedVolumeDevice(audioDeviceAttributes.getInternalType())) {
                index = (this.mStreamStates[streamType].mIndexMax + 5) / 10;
            } else {
                index = (this.mStreamStates[streamType].getIndex(audioDeviceAttributes.getInternalType()) + 5) / 10;
            }
            builder.setVolumeIndex(index);
            build = builder.setMuted(this.mStreamStates[streamType].mIsMuted).build();
        }
        return build;
    }

    public int getStreamMaxVolume(int i) {
        ensureValidStreamType(i);
        return (this.mStreamStates[i].getMaxIndex() + 5) / 10;
    }

    public int getStreamMinVolume(int i) {
        ensureValidStreamType(i);
        return (this.mStreamStates[i].getMinIndex(Binder.getCallingUid() == 1000 || callingHasAudioSettingsPermission() || this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0) + 5) / 10;
    }

    @EnforcePermission("android.permission.QUERY_AUDIO_STATE")
    public int getLastAudibleStreamVolume(int i) {
        super.getLastAudibleStreamVolume_enforcePermission();
        ensureValidStreamType(i);
        return (this.mStreamStates[i].getIndex(getDeviceForStream(i)) + 5) / 10;
    }

    public VolumeInfo getDefaultVolumeInfo() {
        if (sDefaultVolumeInfo == null) {
            sDefaultVolumeInfo = new VolumeInfo.Builder(3).setMinVolumeIndex(getStreamMinVolume(3)).setMaxVolumeIndex(getStreamMaxVolume(3)).build();
        }
        return sDefaultVolumeInfo;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public void registerStreamAliasingDispatcher(IStreamAliasingDispatcher iStreamAliasingDispatcher, boolean z) {
        super.registerStreamAliasingDispatcher_enforcePermission();
        Objects.requireNonNull(iStreamAliasingDispatcher);
        if (z) {
            this.mStreamAliasingDispatchers.register(iStreamAliasingDispatcher);
        } else {
            this.mStreamAliasingDispatchers.unregister(iStreamAliasingDispatcher);
        }
    }

    public void dispatchStreamAliasingUpdate() {
        int beginBroadcast = this.mStreamAliasingDispatchers.beginBroadcast();
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                this.mStreamAliasingDispatchers.getBroadcastItem(i).dispatchStreamAliasingChanged();
            } catch (RemoteException e) {
                Log.e("AS.AudioService", "Error on stream alias update dispatch", e);
            }
        }
        this.mStreamAliasingDispatchers.finishBroadcast();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    /* renamed from: getIndependentStreamTypes */
    public ArrayList<Integer> m1895getIndependentStreamTypes() {
        int[] iArr;
        super.getIndependentStreamTypes_enforcePermission();
        if (this.mUseVolumeGroupAliases) {
            return new ArrayList<>(Arrays.stream(AudioManager.getPublicStreamTypes()).boxed().toList());
        }
        ArrayList<Integer> arrayList = new ArrayList<>(1);
        for (int i : mStreamVolumeAlias) {
            if (!arrayList.contains(Integer.valueOf(i))) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        return arrayList;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public int getStreamTypeAlias(int i) {
        super.getStreamTypeAlias_enforcePermission();
        ensureValidStreamType(i);
        return mStreamVolumeAlias[i];
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public boolean isVolumeControlUsingVolumeGroups() {
        super.isVolumeControlUsingVolumeGroups_enforcePermission();
        return this.mUseVolumeGroupAliases;
    }

    public int getUiSoundsStreamType() {
        return this.mUseVolumeGroupAliases ? this.STREAM_VOLUME_ALIAS_VOICE[1] : mStreamVolumeAlias[1];
    }

    public final boolean isUiSoundsStreamType(int i) {
        if (this.mUseVolumeGroupAliases) {
            int[] iArr = this.STREAM_VOLUME_ALIAS_VOICE;
            if (iArr[i] != iArr[1]) {
                return false;
            }
        } else if (i != mStreamVolumeAlias[1]) {
            return false;
        }
        return true;
    }

    public void setMicrophoneMute(boolean z, String str, int i, String str2) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000) {
            callingUid = UserHandle.getUid(i, UserHandle.getAppId(callingUid));
        }
        MediaMetrics.Item item = new MediaMetrics.Item("audio.mic").setUid(callingUid).set(MediaMetrics.Property.CALLING_PACKAGE, str).set(MediaMetrics.Property.EVENT, "setMicrophoneMute").set(MediaMetrics.Property.REQUEST, z ? "mute" : "unmute");
        if (!z && !checkNoteAppOp(44, callingUid, str, str2)) {
            item.set(MediaMetrics.Property.EARLY_RETURN, "disallow unmuting").record();
        } else if (!checkAudioSettingsPermission("setMicrophoneMute()")) {
            item.set(MediaMetrics.Property.EARLY_RETURN, "!checkAudioSettingsPermission").record();
        } else if (i != UserHandle.getCallingUserId() && this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") != 0) {
            item.set(MediaMetrics.Property.EARLY_RETURN, "permission").record();
        } else {
            this.mMicMuteFromApi = z;
            item.record();
            setMicrophoneMuteNoCallerCheck(i);
        }
    }

    public void setMicrophoneMuteFromSwitch(boolean z) {
        int callingUid = Binder.getCallingUid();
        if (callingUid != 1000) {
            Log.e("AS.AudioService", "setMicrophoneMuteFromSwitch() called from non system user!");
            return;
        }
        this.mMicMuteFromSwitch = z;
        new MediaMetrics.Item("audio.mic").setUid(callingUid).set(MediaMetrics.Property.EVENT, "setMicrophoneMuteFromSwitch").set(MediaMetrics.Property.REQUEST, z ? "mute" : "unmute").record();
        setMicrophoneMuteNoCallerCheck(callingUid);
    }

    public final void setMicMuteFromSwitchInput() {
        InputManager inputManager = (InputManager) this.mContext.getSystemService(InputManager.class);
        if (inputManager.isMicMuted() != -1) {
            setMicrophoneMuteFromSwitch(inputManager.isMicMuted() != 0);
        }
    }

    public boolean isMicrophoneMuted() {
        return this.mMicMuteFromSystemCached && (!this.mMicMuteFromPrivacyToggle || this.mMicMuteFromApi || this.mMicMuteFromRestrictions || this.mMicMuteFromSwitch);
    }

    public final boolean isMicrophoneSupposedToBeMuted() {
        return this.mMicMuteFromSwitch || this.mMicMuteFromRestrictions || this.mMicMuteFromApi || this.mMicMuteFromPrivacyToggle;
    }

    public final void setMicrophoneMuteNoCallerCheck(int i) {
        boolean isMicrophoneSupposedToBeMuted = isMicrophoneSupposedToBeMuted();
        if (getCurrentUserId() == i || i == 1000) {
            boolean isMicrophoneMuted = this.mAudioSystem.isMicrophoneMuted();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                int muteMicrophone = this.mAudioSystem.muteMicrophone(isMicrophoneSupposedToBeMuted);
                this.mMicMuteFromSystemCached = this.mAudioSystem.isMicrophoneMuted();
                if (muteMicrophone != 0) {
                    Log.e("AS.AudioService", "Error changing mic mute state to " + isMicrophoneSupposedToBeMuted + " current:" + this.mMicMuteFromSystemCached);
                }
                new MediaMetrics.Item("audio.mic").setUid(i).set(MediaMetrics.Property.EVENT, "setMicrophoneMuteNoCallerCheck").set(MediaMetrics.Property.MUTE, this.mMicMuteFromSystemCached ? "on" : "off").set(MediaMetrics.Property.REQUEST, isMicrophoneSupposedToBeMuted ? "mute" : "unmute").set(MediaMetrics.Property.STATUS, Integer.valueOf(muteMicrophone)).record();
                if (isMicrophoneSupposedToBeMuted != isMicrophoneMuted) {
                    sendMsg(this.mAudioHandler, 30, 1, 0, 0, null, 0);
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public int getRingerModeExternal() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerModeExternal;
        }
        return i;
    }

    public int getRingerModeInternal() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerMode;
        }
        return i;
    }

    public final void ensureValidRingerMode(int i) {
        if (isValidRingerMode(i)) {
            return;
        }
        throw new IllegalArgumentException("Bad ringer mode " + i);
    }

    public void setRingerModeExternal(int i, String str) {
        if (isAndroidNPlus(str) && wouldToggleZenMode(i) && !this.mNm.isNotificationPolicyAccessGrantedForPackage(str)) {
            throw new SecurityException("Not allowed to change Do Not Disturb state");
        }
        setRingerMode(i, str, true);
    }

    public void setRingerModeInternal(int i, String str) {
        enforceVolumeController("setRingerModeInternal");
        setRingerMode(i, str, false);
    }

    public void silenceRingerModeInternal(String str) {
        VibrationEffect vibrationEffect;
        int i;
        int secureIntForUser = this.mContext.getResources().getBoolean(17891879) ? this.mSettings.getSecureIntForUser(this.mContentResolver, "volume_hush_gesture", 0, -2) : 0;
        int i2 = 1;
        if (secureIntForUser == 1) {
            vibrationEffect = VibrationEffect.get(5);
            i = 17041718;
        } else if (secureIntForUser != 2) {
            vibrationEffect = null;
            i2 = 0;
            i = 0;
        } else {
            vibrationEffect = VibrationEffect.get(1);
            i = 17041717;
            i2 = 0;
        }
        maybeVibrate(vibrationEffect, str);
        setRingerModeInternal(i2, str);
        Toast.makeText(this.mContext, i, 0).show();
    }

    public final boolean maybeVibrate(VibrationEffect vibrationEffect, String str) {
        if (this.mHasVibrator && vibrationEffect != null) {
            this.mVibrator.vibrate(Binder.getCallingUid(), this.mContext.getOpPackageName(), vibrationEffect, str, TOUCH_VIBRATION_ATTRIBUTES);
            return true;
        }
        return false;
    }

    public final void setRingerMode(int i, String str, boolean z) {
        if (this.mUseFixedVolume || this.mIsSingleVolume || this.mUseVolumeGroupAliases) {
            return;
        }
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("Bad caller: " + str);
        }
        ensureValidRingerMode(i);
        if (i == 1 && !this.mHasVibrator) {
            i = 0;
        }
        int i2 = i;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mSettingsLock) {
                int ringerModeInternal = getRingerModeInternal();
                int ringerModeExternal = getRingerModeExternal();
                if (z) {
                    setRingerModeExt(i2);
                    AudioManagerInternal.RingerModeDelegate ringerModeDelegate = this.mRingerModeDelegate;
                    if (ringerModeDelegate != null) {
                        i2 = ringerModeDelegate.onSetRingerModeExternal(ringerModeExternal, i2, str, ringerModeInternal, this.mVolumePolicy);
                    }
                    if (i2 != ringerModeInternal) {
                        setRingerModeInt(i2, true);
                    }
                } else {
                    if (i2 != ringerModeInternal) {
                        setRingerModeInt(i2, true);
                    }
                    AudioManagerInternal.RingerModeDelegate ringerModeDelegate2 = this.mRingerModeDelegate;
                    if (ringerModeDelegate2 != null) {
                        i2 = ringerModeDelegate2.onSetRingerModeInternal(ringerModeInternal, i2, str, ringerModeExternal, this.mVolumePolicy);
                    }
                    setRingerModeExt(i2);
                }
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void setRingerModeExt(int i) {
        synchronized (this.mSettingsLock) {
            if (i == this.mRingerModeExternal) {
                return;
            }
            this.mRingerModeExternal = i;
            broadcastRingerMode("android.media.RINGER_MODE_CHANGED", i);
        }
    }

    @GuardedBy({"mSettingsLock"})
    public final void muteRingerModeStreams() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        if (this.mNm == null) {
            this.mNm = (NotificationManager) this.mContext.getSystemService("notification");
        }
        int i = this.mRingerMode;
        boolean z = i == 1 || i == 0;
        boolean z2 = i == 1 && this.mDeviceBroker.isBluetoothScoActive();
        sendMsg(this.mAudioHandler, 8, 2, 7, z2 ? 3 : 0, "muteRingerModeStreams() from u/pid:" + Binder.getCallingUid() + "/" + Binder.getCallingPid(), 0);
        int i2 = numStreamTypes - 1;
        while (i2 >= 0) {
            boolean isStreamMutedByRingerOrZenMode = isStreamMutedByRingerOrZenMode(i2);
            boolean z3 = shouldZenMuteStream(i2) || (z && isStreamAffectedByRingerMode(i2) && (!z2 || i2 != 2));
            if (isStreamMutedByRingerOrZenMode != z3) {
                if (!z3) {
                    int i3 = mStreamVolumeAlias[i2];
                    if (i3 == 2 || i3 == 5) {
                        synchronized (VolumeStreamState.class) {
                            VolumeStreamState volumeStreamState = this.mStreamStates[i2];
                            for (int i4 = 0; i4 < volumeStreamState.mIndexMap.size(); i4++) {
                                int keyAt = volumeStreamState.mIndexMap.keyAt(i4);
                                if (volumeStreamState.mIndexMap.valueAt(i4) == 0) {
                                    volumeStreamState.setIndex(10, keyAt, "AS.AudioService", true);
                                }
                            }
                            sendMsg(this.mAudioHandler, 1, 2, getDeviceForStream(i2), 0, this.mStreamStates[i2], 500);
                        }
                    }
                    this.mStreamStates[i2].mute(false);
                    this.mRingerAndZenModeMutedStreams &= ~(1 << i2);
                } else {
                    this.mStreamStates[i2].mute(true);
                    this.mRingerAndZenModeMutedStreams |= 1 << i2;
                }
            }
            i2--;
        }
    }

    public final void setRingerModeInt(int i, boolean z) {
        boolean z2;
        synchronized (this.mSettingsLock) {
            z2 = this.mRingerMode != i;
            this.mRingerMode = i;
            muteRingerModeStreams();
        }
        if (z) {
            sendMsg(this.mAudioHandler, 3, 0, 0, 0, null, 500);
        }
        if (z2) {
            broadcastRingerMode("android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION", i);
        }
    }

    public void postUpdateRingerModeServiceInt() {
        sendMsg(this.mAudioHandler, 25, 2, 0, 0, null, 0);
    }

    public final void onUpdateRingerModeServiceInt() {
        setRingerModeInt(getRingerModeInternal(), false);
    }

    public boolean shouldVibrate(int i) {
        if (this.mHasVibrator) {
            int vibrateSetting = getVibrateSetting(i);
            return vibrateSetting != 1 ? vibrateSetting == 2 && getRingerModeExternal() == 1 : getRingerModeExternal() != 0;
        }
        return false;
    }

    public int getVibrateSetting(int i) {
        if (this.mHasVibrator) {
            return (this.mVibrateSetting >> (i * 2)) & 3;
        }
        return 0;
    }

    public void setVibrateSetting(int i, int i2) {
        if (this.mHasVibrator) {
            this.mVibrateSetting = AudioSystem.getValueForVibrateSetting(this.mVibrateSetting, i, i2);
            broadcastVibrateSetting(i);
        }
    }

    /* loaded from: classes.dex */
    public class SetModeDeathHandler implements IBinder.DeathRecipient {
        public final IBinder mCb;
        public final boolean mIsPrivileged;
        public int mMode;
        public final String mPackage;
        public final int mPid;
        public final int mUid;
        public boolean mPlaybackActive = false;
        public boolean mRecordingActive = false;
        public long mUpdateTime = System.currentTimeMillis();

        public SetModeDeathHandler(IBinder iBinder, int i, int i2, boolean z, String str, int i3) {
            this.mMode = i3;
            this.mCb = iBinder;
            this.mPid = i;
            this.mUid = i2;
            this.mPackage = str;
            this.mIsPrivileged = z;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mDeviceBroker.mSetModeLock) {
                Log.w("AS.AudioService", "SetModeDeathHandler client died");
                int indexOf = AudioService.this.mSetModeDeathHandlers.indexOf(this);
                if (indexOf < 0) {
                    Log.w("AS.AudioService", "unregistered SetModeDeathHandler client died");
                } else {
                    AudioService.this.mSetModeDeathHandlers.get(indexOf);
                    AudioService.this.mSetModeDeathHandlers.remove(indexOf);
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 36, 2, -1, Process.myPid(), AudioService.this.mContext.getPackageName(), 0);
                }
            }
        }

        public int getPid() {
            return this.mPid;
        }

        public void setMode(int i) {
            this.mMode = i;
            this.mUpdateTime = System.currentTimeMillis();
        }

        public int getMode() {
            return this.mMode;
        }

        public IBinder getBinder() {
            return this.mCb;
        }

        public int getUid() {
            return this.mUid;
        }

        public boolean isPrivileged() {
            return this.mIsPrivileged;
        }

        public long getUpdateTime() {
            return this.mUpdateTime;
        }

        public void setPlaybackActive(boolean z) {
            this.mPlaybackActive = z;
        }

        public void setRecordingActive(boolean z) {
            this.mRecordingActive = z;
        }

        public boolean isActive() {
            if (this.mIsPrivileged) {
                return true;
            }
            int i = this.mMode;
            return (i == 3 && (this.mRecordingActive || this.mPlaybackActive)) || i == 1 || i == 4;
        }

        public void dump(PrintWriter printWriter, int i) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
            if (i >= 0) {
                printWriter.println("  Requester # " + (i + 1) + XmlUtils.STRING_ARRAY_SEPARATOR);
            }
            printWriter.println("  - Mode: " + AudioSystem.modeToString(this.mMode));
            printWriter.println("  - Binder: " + this.mCb);
            printWriter.println("  - Pid: " + this.mPid);
            printWriter.println("  - Uid: " + this.mUid);
            printWriter.println("  - Package: " + this.mPackage);
            printWriter.println("  - Privileged: " + this.mIsPrivileged);
            printWriter.println("  - Active: " + isActive());
            printWriter.println("    Playback active: " + this.mPlaybackActive);
            printWriter.println("    Recording active: " + this.mRecordingActive);
            printWriter.println("  - update time: " + simpleDateFormat.format(new Date(this.mUpdateTime)));
        }
    }

    @GuardedBy({"mDeviceBroker.mSetModeLock"})
    public final SetModeDeathHandler getAudioModeOwnerHandler() {
        Iterator<SetModeDeathHandler> it = this.mSetModeDeathHandlers.iterator();
        SetModeDeathHandler setModeDeathHandler = null;
        SetModeDeathHandler setModeDeathHandler2 = null;
        while (it.hasNext()) {
            SetModeDeathHandler next = it.next();
            if (next.isActive()) {
                if (next.isPrivileged()) {
                    if (setModeDeathHandler == null || next.getUpdateTime() > setModeDeathHandler.getUpdateTime()) {
                        setModeDeathHandler = next;
                    }
                } else if (setModeDeathHandler2 == null || next.getUpdateTime() > setModeDeathHandler2.getUpdateTime()) {
                    setModeDeathHandler2 = next;
                }
            }
        }
        return setModeDeathHandler != null ? setModeDeathHandler : setModeDeathHandler2;
    }

    @GuardedBy({"mDeviceBroker.mSetModeLock"})
    public AudioDeviceBroker.AudioModeInfo getAudioModeOwner() {
        SetModeDeathHandler audioModeOwnerHandler = getAudioModeOwnerHandler();
        if (audioModeOwnerHandler != null) {
            return new AudioDeviceBroker.AudioModeInfo(audioModeOwnerHandler.getMode(), audioModeOwnerHandler.getPid(), audioModeOwnerHandler.getUid());
        }
        return new AudioDeviceBroker.AudioModeInfo(0, 0, 0);
    }

    public void setMode(int i, IBinder iBinder, String str) {
        SetModeDeathHandler setModeDeathHandler;
        SetModeDeathHandler setModeDeathHandler2;
        int i2 = i;
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        if (checkAudioSettingsPermission("setMode()")) {
            if (iBinder == null) {
                Log.e("AS.AudioService", "setMode() called with null binder");
            } else if (i2 < -1 || i2 >= 7) {
                Log.w("AS.AudioService", "setMode() invalid mode: " + i2);
            } else {
                if (i2 == -1) {
                    i2 = getMode();
                }
                int i3 = i2;
                if (i3 == 4 && !this.mIsCallScreeningModeSupported) {
                    Log.w("AS.AudioService", "setMode(MODE_CALL_SCREENING) not permitted when call screening is not supported");
                    return;
                }
                boolean z = this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") == 0;
                if ((i3 == 2 || i3 == 5 || i3 == 6) && !z) {
                    Log.w("AS.AudioService", "MODIFY_PHONE_STATE Permission Denial: setMode(" + AudioSystem.modeToString(i3) + ") from pid=" + callingPid + ", uid=" + Binder.getCallingUid());
                    return;
                }
                synchronized (this.mDeviceBroker.mSetModeLock) {
                    Iterator<SetModeDeathHandler> it = this.mSetModeDeathHandlers.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            setModeDeathHandler = null;
                            break;
                        }
                        setModeDeathHandler = it.next();
                        if (setModeDeathHandler.getPid() == callingPid) {
                            break;
                        }
                    }
                    if (i3 != 0) {
                        if (setModeDeathHandler != null) {
                            setModeDeathHandler.setMode(i3);
                            setModeDeathHandler2 = setModeDeathHandler;
                        } else {
                            SetModeDeathHandler setModeDeathHandler3 = new SetModeDeathHandler(iBinder, callingPid, callingUid, z, str, i3);
                            try {
                                iBinder.linkToDeath(setModeDeathHandler3, 0);
                                this.mSetModeDeathHandlers.add(setModeDeathHandler3);
                                setModeDeathHandler2 = setModeDeathHandler3;
                            } catch (RemoteException unused) {
                                Log.w("AS.AudioService", "setMode() could not link to " + iBinder + " binder death");
                                return;
                            }
                        }
                        if (i3 == 3 && !setModeDeathHandler2.isPrivileged()) {
                            setModeDeathHandler2.setPlaybackActive(true);
                            setModeDeathHandler2.setRecordingActive(true);
                            sendMsg(this.mAudioHandler, 31, 2, 0, 0, setModeDeathHandler2, 6000);
                        }
                        sendMsg(this.mAudioHandler, 36, 0, i3, callingPid, str, 0);
                    } else {
                        if (setModeDeathHandler != null) {
                            if (!setModeDeathHandler.isPrivileged() && setModeDeathHandler.getMode() == 3) {
                                this.mAudioHandler.removeEqualMessages(31, setModeDeathHandler);
                            }
                            this.mSetModeDeathHandlers.remove(setModeDeathHandler);
                            try {
                                setModeDeathHandler.getBinder().unlinkToDeath(setModeDeathHandler, 0);
                            } catch (NoSuchElementException unused2) {
                                Log.w("AS.AudioService", "setMode link does not exist ...");
                            }
                        }
                        sendMsg(this.mAudioHandler, 36, 0, i3, callingPid, str, 0);
                    }
                }
            }
        }
    }

    @GuardedBy({"mDeviceBroker.mSetModeLock"})
    public void onUpdateAudioMode(int i, final int i2, final String str, boolean z) {
        int i3;
        int i4;
        int i5;
        final int mode = i == -1 ? getMode() : i;
        SetModeDeathHandler audioModeOwnerHandler = getAudioModeOwnerHandler();
        if (audioModeOwnerHandler != null) {
            int mode2 = audioModeOwnerHandler.getMode();
            int uid = audioModeOwnerHandler.getUid();
            i5 = audioModeOwnerHandler.getPid();
            i3 = mode2;
            i4 = uid;
        } else {
            i3 = 0;
            i4 = 0;
            i5 = 0;
        }
        if (i3 != this.mMode.get() || z) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                if (this.mAudioSystem.setPhoneState(i3, i4) == 0) {
                    sendMsg(this.mAudioHandler, 40, 0, i3, 0, null, 0);
                    int andSet = this.mMode.getAndSet(i3);
                    final int i6 = i5;
                    int i7 = i4;
                    final int i8 = i3;
                    this.mModeLogger.enqueue(new EventLogger.Event(str, i2, mode, i6, i8) { // from class: com.android.server.audio.AudioServiceEvents$PhoneStateEvent
                        public final int mActualMode;
                        public final int mOp = 0;
                        public final int mOwnerPid;
                        public final String mPackage;
                        public final int mRequestedMode;
                        public final int mRequesterPid;

                        {
                            this.mPackage = str;
                            this.mRequesterPid = i2;
                            this.mRequestedMode = mode;
                            this.mOwnerPid = i6;
                            this.mActualMode = i8;
                            logMetricEvent();
                        }

                        @Override // com.android.server.utils.EventLogger.Event
                        public String eventToString() {
                            int i9 = this.mOp;
                            if (i9 != 0) {
                                if (i9 == 1) {
                                    return "mode IN COMMUNICATION timeout for package=" + this.mPackage + " pid=" + this.mOwnerPid;
                                }
                                return "FIXME invalid op:" + this.mOp;
                            }
                            return "setMode(" + AudioSystem.modeToString(this.mRequestedMode) + ") from package=" + this.mPackage + " pid=" + this.mRequesterPid + " selected mode=" + AudioSystem.modeToString(this.mActualMode) + " by pid=" + this.mOwnerPid;
                        }

                        public final void logMetricEvent() {
                            int i9 = this.mOp;
                            if (i9 == 0) {
                                new MediaMetrics.Item("audio.mode").set(MediaMetrics.Property.EVENT, "set").set(MediaMetrics.Property.REQUESTED_MODE, AudioSystem.modeToString(this.mRequestedMode)).set(MediaMetrics.Property.MODE, AudioSystem.modeToString(this.mActualMode)).set(MediaMetrics.Property.CALLING_PACKAGE, this.mPackage).record();
                            } else if (i9 != 1) {
                            } else {
                                new MediaMetrics.Item("audio.mode").set(MediaMetrics.Property.EVENT, "inCommunicationTimeout").set(MediaMetrics.Property.CALLING_PACKAGE, this.mPackage).record();
                            }
                        }
                    });
                    int activeStreamType = getActiveStreamType(Integer.MIN_VALUE);
                    int deviceForStream = getDeviceForStream(activeStreamType);
                    int i9 = mStreamVolumeAlias[activeStreamType];
                    int index = this.mStreamStates[i9].getIndex(deviceForStream);
                    int maxIndex = this.mStreamStates[i9].getMaxIndex();
                    int i10 = i3;
                    setStreamVolumeInt(i9, index, deviceForStream, true, str, true);
                    updateStreamVolumeAlias(true, str);
                    updateAbsVolumeMultiModeDevices(andSet, i10);
                    setLeAudioVolumeOnModeUpdate(i10, deviceForStream, i9, index, maxIndex);
                    this.mDeviceBroker.postSetModeOwner(i10, i5, i7);
                    return;
                }
                Log.w("AS.AudioService", "onUpdateAudioMode: failed to set audio mode to: " + i3);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public int getMode() {
        synchronized (this.mDeviceBroker.mSetModeLock) {
            SetModeDeathHandler audioModeOwnerHandler = getAudioModeOwnerHandler();
            if (audioModeOwnerHandler != null) {
                return audioModeOwnerHandler.getMode();
            }
            return 0;
        }
    }

    public boolean isCallScreeningModeSupported() {
        return this.mIsCallScreeningModeSupported;
    }

    public void dispatchMode(int i) {
        int beginBroadcast = this.mModeDispatchers.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mModeDispatchers.getBroadcastItem(i2).dispatchAudioModeChanged(i);
            } catch (RemoteException unused) {
            }
        }
        this.mModeDispatchers.finishBroadcast();
    }

    public void registerModeDispatcher(IAudioModeDispatcher iAudioModeDispatcher) {
        this.mModeDispatchers.register(iAudioModeDispatcher);
    }

    public void unregisterModeDispatcher(IAudioModeDispatcher iAudioModeDispatcher) {
        this.mModeDispatchers.unregister(iAudioModeDispatcher);
    }

    @EnforcePermission("android.permission.CALL_AUDIO_INTERCEPTION")
    public boolean isPstnCallAudioInterceptable() {
        AudioDeviceInfo[] devicesStatic;
        super.isPstnCallAudioInterceptable_enforcePermission();
        boolean z = false;
        boolean z2 = false;
        for (AudioDeviceInfo audioDeviceInfo : AudioManager.getDevicesStatic(3)) {
            if (audioDeviceInfo.getInternalType() == 65536) {
                z = true;
            } else if (audioDeviceInfo.getInternalType() == -2147483584) {
                z2 = true;
            }
            if (z && z2) {
                return true;
            }
        }
        return false;
    }

    public void setRttEnabled(boolean z) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
            Log.w("AS.AudioService", "MODIFY_PHONE_STATE Permission Denial: setRttEnabled from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mSettingsLock) {
            this.mRttEnabled = z;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            AudioSystem.setRttEnabled(z);
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void adjustSuggestedStreamVolumeForUid(int i, int i2, int i3, String str, int i4, int i5, UserHandle userHandle, int i6) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Should only be called from system process");
        }
        adjustSuggestedStreamVolume(i2, i, i3, str, str, i4, i5, hasAudioSettingsPermission(i4, i5), 0);
    }

    public void adjustStreamVolumeForUid(int i, int i2, int i3, String str, int i4, int i5, UserHandle userHandle, int i6) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Should only be called from system process");
        }
        if (i2 != 0) {
            EventLogger eventLogger = sVolumeLogger;
            eventLogger.enqueue(new AudioServiceEvents$VolumeEvent(5, i, i2, i3, str + " uid:" + i4));
        }
        adjustStreamVolume(i, i2, i3, str, str, i4, i5, null, hasAudioSettingsPermission(i4, i5), 0);
    }

    public void setStreamVolumeForUid(int i, int i2, int i3, String str, int i4, int i5, UserHandle userHandle, int i6) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException("Should only be called from system process");
        }
        setStreamVolume(i, i2, i3, null, str, str, null, i4, hasAudioSettingsPermission(i4, i5));
    }

    /* loaded from: classes.dex */
    public static final class LoadSoundEffectReply implements SoundEffectsHelper.OnEffectsLoadCompleteHandler {
        public int mStatus;

        public LoadSoundEffectReply() {
            this.mStatus = 1;
        }

        @Override // com.android.server.audio.SoundEffectsHelper.OnEffectsLoadCompleteHandler
        public synchronized void run(boolean z) {
            this.mStatus = z ? 0 : -1;
            notify();
        }

        public synchronized boolean waitForLoaded(int i) {
            int i2;
            while (true) {
                i2 = this.mStatus;
                if (i2 != 1) {
                    break;
                }
                int i3 = i - 1;
                if (i <= 0) {
                    break;
                }
                try {
                    wait(5000L);
                } catch (InterruptedException unused) {
                    Log.w("AS.AudioService", "Interrupted while waiting sound pool loaded.");
                }
                i = i3;
            }
            return i2 == 0;
        }
    }

    public void playSoundEffect(int i, int i2) {
        if (querySoundEffectsEnabled(i2)) {
            playSoundEffectVolume(i, -1.0f);
        }
    }

    public final boolean querySoundEffectsEnabled(int i) {
        return this.mSettings.getSystemIntForUser(getContentResolver(), "sound_effects_enabled", 0, i) != 0;
    }

    public void playSoundEffectVolume(int i, float f) {
        if (isStreamMute(1)) {
            return;
        }
        if (i >= 16 || i < 0) {
            Log.w("AS.AudioService", "AudioService effectType value " + i + " out of range");
            return;
        }
        sendMsg(this.mAudioHandler, 5, 2, i, (int) (f * 1000.0f), null, 0);
    }

    public boolean loadSoundEffects() {
        LoadSoundEffectReply loadSoundEffectReply = new LoadSoundEffectReply();
        sendMsg(this.mAudioHandler, 7, 2, 0, 0, loadSoundEffectReply, 0);
        return loadSoundEffectReply.waitForLoaded(3);
    }

    public void scheduleLoadSoundEffects() {
        sendMsg(this.mAudioHandler, 7, 2, 0, 0, null, 0);
    }

    public void unloadSoundEffects() {
        sendMsg(this.mAudioHandler, 15, 2, 0, 0, null, 0);
    }

    public void reloadAudioSettings() {
        readAudioSettings(false);
    }

    public final void readAudioSettings(boolean z) {
        readPersistedSettings();
        readUserRestrictions();
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            VolumeStreamState volumeStreamState = this.mStreamStates[i];
            if (!z || mStreamVolumeAlias[i] != 3) {
                volumeStreamState.readSettings();
                synchronized (VolumeStreamState.class) {
                    if (volumeStreamState.mIsMuted && ((!isStreamAffectedByMute(i) && !isStreamMutedByRingerOrZenMode(i)) || this.mUseFixedVolume)) {
                        volumeStreamState.mIsMuted = false;
                    }
                }
                continue;
            }
        }
        readVolumeGroupsSettings(z);
        setRingerModeInt(getRingerModeInternal(), false);
        checkAllFixedVolumeDevices();
        checkAllAliasStreamVolumes();
        checkMuteAffectedStreams();
        this.mSoundDoseHelper.restoreMusicActiveMs();
        this.mSoundDoseHelper.enforceSafeMediaVolumeIfActive("AS.AudioService");
        restoreDeviceVolumeBehavior();
    }

    public int[] getAvailableCommunicationDeviceIds() {
        return AudioDeviceBroker.getAvailableCommunicationDevices().stream().mapToInt(new ToIntFunction() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda5
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((AudioDeviceInfo) obj).getId();
            }
        }).toArray();
    }

    public boolean setCommunicationDevice(IBinder iBinder, int i) {
        AudioDeviceInfo audioDeviceInfo;
        String str;
        int i2;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (i != 0) {
            audioDeviceInfo = AudioManager.getDeviceForPortId(i, 2);
            if (audioDeviceInfo == null) {
                Log.w("AS.AudioService", "setCommunicationDevice: invalid portID " + i);
                return false;
            } else if (!AudioDeviceBroker.isValidCommunicationDevice(audioDeviceInfo)) {
                if (!audioDeviceInfo.isSink()) {
                    throw new IllegalArgumentException("device must have sink role");
                }
                throw new IllegalArgumentException("invalid device type: " + audioDeviceInfo.getType());
            }
        } else {
            audioDeviceInfo = null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(audioDeviceInfo == null ? "clearCommunicationDevice(" : "setCommunicationDevice(");
        sb.append(") from u/pid:");
        sb.append(callingUid);
        sb.append("/");
        sb.append(callingPid);
        String sb2 = sb.toString();
        if (audioDeviceInfo != null) {
            i2 = audioDeviceInfo.getPort().type();
            str = audioDeviceInfo.getAddress();
        } else {
            AudioDeviceInfo communicationDevice = this.mDeviceBroker.getCommunicationDevice();
            if (communicationDevice != null) {
                i2 = communicationDevice.getPort().type();
                str = communicationDevice.getAddress();
            } else {
                str = null;
                i2 = 1073741824;
            }
        }
        if (i2 != 1073741824) {
            new MediaMetrics.Item("audio.device.setCommunicationDevice").set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(i2)).set(MediaMetrics.Property.ADDRESS, str).set(MediaMetrics.Property.STATE, audioDeviceInfo != null ? "connected" : "disconnected").record();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mDeviceBroker.setCommunicationDevice(iBinder, callingPid, audioDeviceInfo, sb2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getCommunicationDevice() {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            AudioDeviceInfo communicationDevice = this.mDeviceBroker.getCommunicationDevice();
            return communicationDevice != null ? communicationDevice.getId() : 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void registerCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher iCommunicationDeviceDispatcher) {
        if (iCommunicationDeviceDispatcher == null) {
            return;
        }
        this.mDeviceBroker.registerCommunicationDeviceDispatcher(iCommunicationDeviceDispatcher);
    }

    public void unregisterCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher iCommunicationDeviceDispatcher) {
        if (iCommunicationDeviceDispatcher == null) {
            return;
        }
        this.mDeviceBroker.unregisterCommunicationDeviceDispatcher(iCommunicationDeviceDispatcher);
    }

    public void setSpeakerphoneOn(IBinder iBinder, boolean z) {
        if (checkAudioSettingsPermission("setSpeakerphoneOn()")) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            String str = "setSpeakerphoneOn(" + z + ") from u/pid:" + callingUid + "/" + callingPid;
            new MediaMetrics.Item("audio.device.setSpeakerphoneOn").setUid(callingUid).setPid(callingPid).set(MediaMetrics.Property.STATE, z ? "on" : "off").record();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mDeviceBroker.setSpeakerphoneOn(iBinder, callingPid, z, str);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public boolean isSpeakerphoneOn() {
        return this.mDeviceBroker.isSpeakerphoneOn();
    }

    public void setBluetoothScoOn(boolean z) {
        if (checkAudioSettingsPermission("setBluetoothScoOn()")) {
            if (UserHandle.getCallingAppId() >= 10000) {
                this.mBtScoOnByApp = z;
                return;
            }
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            String str = "setBluetoothScoOn(" + z + ") from u/pid:" + callingUid + "/" + callingPid;
            new MediaMetrics.Item("audio.device.setBluetoothScoOn").setUid(callingUid).setPid(callingPid).set(MediaMetrics.Property.STATE, z ? "on" : "off").record();
            this.mDeviceBroker.setBluetoothScoOn(z, str);
        }
    }

    public boolean isBluetoothScoOn() {
        return this.mBtScoOnByApp || this.mDeviceBroker.isBluetoothScoOn();
    }

    public void setBluetoothA2dpOn(boolean z) {
        if (checkAudioSettingsPermission("setBluetoothA2dpOn()")) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            String str = "setBluetoothA2dpOn(" + z + ") from u/pid:" + callingUid + "/" + callingPid;
            new MediaMetrics.Item("audio.device.setBluetoothA2dpOn").setUid(callingUid).setPid(callingPid).set(MediaMetrics.Property.STATE, z ? "on" : "off").record();
            this.mDeviceBroker.setBluetoothA2dpOn_Async(z, str);
        }
    }

    public boolean isBluetoothA2dpOn() {
        return this.mDeviceBroker.isBluetoothA2dpOn();
    }

    public void startBluetoothSco(IBinder iBinder, int i) {
        if (checkAudioSettingsPermission("startBluetoothSco()")) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            int i2 = i < 18 ? 0 : -1;
            new MediaMetrics.Item("audio.bluetooth").setUid(callingUid).setPid(callingPid).set(MediaMetrics.Property.EVENT, "startBluetoothSco").set(MediaMetrics.Property.SCO_AUDIO_MODE, BtHelper.scoAudioModeToString(i2)).record();
            startBluetoothScoInt(iBinder, callingPid, i2, "startBluetoothSco()) from u/pid:" + callingUid + "/" + callingPid);
        }
    }

    public void startBluetoothScoVirtualCall(IBinder iBinder) {
        if (checkAudioSettingsPermission("startBluetoothScoVirtualCall()")) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            new MediaMetrics.Item("audio.bluetooth").setUid(callingUid).setPid(callingPid).set(MediaMetrics.Property.EVENT, "startBluetoothScoVirtualCall").set(MediaMetrics.Property.SCO_AUDIO_MODE, BtHelper.scoAudioModeToString(0)).record();
            startBluetoothScoInt(iBinder, callingPid, 0, "startBluetoothScoVirtualCall()) from u/pid:" + callingUid + "/" + callingPid);
        }
    }

    public void startBluetoothScoInt(IBinder iBinder, int i, int i2, String str) {
        MediaMetrics.Item item = new MediaMetrics.Item("audio.bluetooth").set(MediaMetrics.Property.EVENT, "startBluetoothScoInt").set(MediaMetrics.Property.SCO_AUDIO_MODE, BtHelper.scoAudioModeToString(i2));
        if (!checkAudioSettingsPermission("startBluetoothSco()") || !this.mSystemReady) {
            item.set(MediaMetrics.Property.EARLY_RETURN, "permission or systemReady").record();
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mDeviceBroker.startBluetoothScoForClient(iBinder, i, i2, str);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            item.record();
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void stopBluetoothSco(IBinder iBinder) {
        if (checkAudioSettingsPermission("stopBluetoothSco()") && this.mSystemReady) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            String str = "stopBluetoothSco()) from u/pid:" + callingUid + "/" + callingPid;
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mDeviceBroker.stopBluetoothScoForClient(iBinder, callingPid, str);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                new MediaMetrics.Item("audio.bluetooth").setUid(callingUid).setPid(callingPid).set(MediaMetrics.Property.EVENT, "stopBluetoothSco").set(MediaMetrics.Property.SCO_AUDIO_MODE, BtHelper.scoAudioModeToString(-1)).record();
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }
    }

    public ContentResolver getContentResolver() {
        return this.mContentResolver;
    }

    /* JADX WARN: Code restructure failed: missing block: B:53:0x00a8, code lost:
        if (r11 != 100) goto L52;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:76:0x00d8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int checkForRingerModeChange(int i, int i2, int i3, boolean z, String str, int i4) {
        int i5;
        int i6 = 1;
        r1 = 1;
        int i7 = 1;
        r1 = 1;
        r1 = 1;
        r1 = 1;
        r1 = 1;
        r1 = 1;
        r1 = 1;
        int i8 = 1;
        i6 = 1;
        i6 = 1;
        i6 = 1;
        i6 = 1;
        i6 = 1;
        i6 = 1;
        i6 = 1;
        if (isPlatformTelevision() || this.mIsSingleVolume) {
            return 1;
        }
        int ringerModeInternal = getRingerModeInternal();
        if (ringerModeInternal == 0) {
            if (!this.mIsSingleVolume || i2 != -1 || i < i3 * 2 || !z) {
                if (i2 == 1 || i2 == 101 || i2 == 100) {
                    if (!this.mVolumePolicy.volumeUpToExitSilent) {
                        i6 = 129;
                    } else if (this.mHasVibrator && i2 == 1) {
                        ringerModeInternal = 1;
                    }
                }
                i8 = i6 & (-2);
            }
            ringerModeInternal = 2;
            i8 = i6 & (-2);
        } else if (ringerModeInternal != 1) {
            if (ringerModeInternal != 2) {
                Log.e("AS.AudioService", "checkForRingerModeChange() wrong ringer mode: " + ringerModeInternal);
            } else if (i2 == -1) {
                if (this.mHasVibrator) {
                    if (i3 <= i && i < i3 * 2) {
                        this.mLoweredFromNormalToVibrateTime = SystemClock.uptimeMillis();
                        i5 = 1;
                        if (!isAndroidNPlus(str) && wouldToggleZenMode(i7) && !this.mNm.isNotificationPolicyAccessGrantedForPackage(str) && (i4 & IInstalld.FLAG_USE_QUOTA) == 0) {
                            throw new SecurityException("Not allowed to change Do Not Disturb state");
                        }
                        setRingerMode(i7, "AS.AudioService.checkForRingerModeChange", false);
                        this.mPrevVolDirection = i2;
                        return i5;
                    }
                } else if (i == i3 && this.mVolumePolicy.volumeDownToEnterSilent) {
                    i5 = 1;
                    i7 = 0;
                    if (!isAndroidNPlus(str)) {
                    }
                    setRingerMode(i7, "AS.AudioService.checkForRingerModeChange", false);
                    this.mPrevVolDirection = i2;
                    return i5;
                }
            } else if (this.mIsSingleVolume && (i2 == 101 || i2 == -100)) {
                i5 = 0;
                i7 = this.mHasVibrator;
                if (!isAndroidNPlus(str)) {
                }
                setRingerMode(i7, "AS.AudioService.checkForRingerModeChange", false);
                this.mPrevVolDirection = i2;
                return i5;
            }
        } else if (!this.mHasVibrator) {
            Log.e("AS.AudioService", "checkForRingerModeChange() current ringer mode is vibratebut no vibrator is present");
        } else if (i2 == -1) {
            if (!this.mIsSingleVolume || i < i3 * 2 || !z) {
                if (this.mPrevVolDirection != -1) {
                    if (!this.mVolumePolicy.volumeDownToEnterSilent) {
                        i6 = 2049;
                    } else if (SystemClock.uptimeMillis() - this.mLoweredFromNormalToVibrateTime > this.mVolumePolicy.vibrateToSilentDebounce && this.mRingerModeDelegate.canVolumeDownEnterSilent()) {
                        ringerModeInternal = 0;
                    }
                }
                i8 = i6 & (-2);
            }
            ringerModeInternal = 2;
            i8 = i6 & (-2);
        } else {
            if (i2 != 1) {
                if (i2 != 101) {
                }
            }
            ringerModeInternal = 2;
            i8 = i6 & (-2);
        }
        i5 = i8;
        i7 = ringerModeInternal;
        if (!isAndroidNPlus(str)) {
        }
        setRingerMode(i7, "AS.AudioService.checkForRingerModeChange", false);
        this.mPrevVolDirection = i2;
        return i5;
    }

    public boolean isStreamAffectedByRingerMode(int i) {
        return (this.mRingerModeAffectedStreams & (1 << i)) != 0;
    }

    public final boolean shouldZenMuteStream(int i) {
        if (this.mNm.getZenMode() != 1) {
            return false;
        }
        NotificationManager.Policy consolidatedNotificationPolicy = this.mNm.getConsolidatedNotificationPolicy();
        int i2 = consolidatedNotificationPolicy.priorityCategories;
        return (((i2 & 32) == 0) && isAlarm(i)) || (((i2 & 64) == 0) && isMedia(i)) || ((((i2 & 128) == 0) && isSystem(i)) || (ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(consolidatedNotificationPolicy) && isNotificationOrRinger(i)));
    }

    public final boolean isStreamMutedByRingerOrZenMode(int i) {
        return (this.mRingerAndZenModeMutedStreams & (1 << i)) != 0;
    }

    public final boolean updateZenModeAffectedStreams() {
        int i;
        if (this.mSystemReady) {
            int zenMode = this.mNm.getZenMode();
            if (zenMode == 2) {
                i = 24;
            } else if (zenMode == 1) {
                NotificationManager.Policy consolidatedNotificationPolicy = this.mNm.getConsolidatedNotificationPolicy();
                int i2 = consolidatedNotificationPolicy.priorityCategories;
                int i3 = (i2 & 32) == 0 ? 16 : 0;
                if ((i2 & 64) == 0) {
                    i3 |= 8;
                }
                int i4 = (i2 & 128) == 0 ? i3 | 2 : i3;
                i = ZenModeConfig.areAllPriorityOnlyRingerSoundsMuted(consolidatedNotificationPolicy) ? i4 | 32 | 4 : i4;
            } else {
                i = 0;
            }
            if (this.mZenModeAffectedStreams != i) {
                this.mZenModeAffectedStreams = i;
                return true;
            }
            return false;
        }
        return false;
    }

    @GuardedBy({"mSettingsLock"})
    public final boolean updateRingerAndZenModeAffectedStreams() {
        boolean updateZenModeAffectedStreams = updateZenModeAffectedStreams();
        int systemIntForUser = this.mSettings.getSystemIntForUser(this.mContentResolver, "mode_ringer_streams_affected", FrameworkStatsLog.f382x8c80549a, -2);
        if (this.mIsSingleVolume) {
            systemIntForUser = 0;
        } else {
            AudioManagerInternal.RingerModeDelegate ringerModeDelegate = this.mRingerModeDelegate;
            if (ringerModeDelegate != null) {
                systemIntForUser = ringerModeDelegate.getRingerModeAffectedStreams(systemIntForUser);
            }
        }
        int i = this.mCameraSoundForced ? systemIntForUser & (-129) : systemIntForUser | 128;
        int i2 = mStreamVolumeAlias[8] == 2 ? i | 256 : i & (-257);
        if (i2 != this.mRingerModeAffectedStreams) {
            this.mSettings.putSystemIntForUser(this.mContentResolver, "mode_ringer_streams_affected", i2, -2);
            this.mRingerModeAffectedStreams = i2;
            return true;
        }
        return updateZenModeAffectedStreams;
    }

    public boolean isStreamAffectedByMute(int i) {
        return (this.mMuteAffectedStreams & (1 << i)) != 0;
    }

    public final void ensureValidDirection(int i) {
        if (i == -100 || i == -1 || i == 0 || i == 1 || i == 100 || i == 101) {
            return;
        }
        throw new IllegalArgumentException("Bad direction " + i);
    }

    public final void ensureValidStreamType(int i) {
        if (i < 0 || i >= this.mStreamStates.length) {
            throw new IllegalArgumentException("Bad stream type " + i);
        }
    }

    @VisibleForTesting
    public boolean isInCommunication() {
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService("telecom");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            boolean isInCall = telecomManager.isInCall();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            int i = this.mMode.get();
            return isInCall || i == 3 || i == 2;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final boolean wasStreamActiveRecently(int i, int i2) {
        return this.mAudioSystem.isStreamActive(i, i2) || this.mAudioSystem.isStreamActiveRemotely(i, i2);
    }

    public final int getActiveStreamType(int i) {
        if (this.mIsSingleVolume && i == Integer.MIN_VALUE) {
            return 3;
        }
        if (this.mPlatformType == 1) {
            if (isInCommunication()) {
                return this.mDeviceBroker.isBluetoothScoActive() ? 6 : 0;
            } else if (i == Integer.MIN_VALUE) {
                if (wasStreamActiveRecently(2, sStreamOverrideDelayMs)) {
                    return 2;
                }
                return wasStreamActiveRecently(5, sStreamOverrideDelayMs) ? 5 : 3;
            } else if (wasStreamActiveRecently(5, sStreamOverrideDelayMs)) {
                return 5;
            } else {
                if (wasStreamActiveRecently(2, sStreamOverrideDelayMs)) {
                    return 2;
                }
            }
        }
        if (isInCommunication()) {
            return this.mDeviceBroker.isBluetoothScoActive() ? 6 : 0;
        } else if (this.mAudioSystem.isStreamActive(5, sStreamOverrideDelayMs)) {
            return 5;
        } else {
            if (this.mAudioSystem.isStreamActive(2, sStreamOverrideDelayMs)) {
                return 2;
            }
            if (i == Integer.MIN_VALUE) {
                if (this.mAudioSystem.isStreamActive(5, sStreamOverrideDelayMs)) {
                    return 5;
                }
                return this.mAudioSystem.isStreamActive(2, sStreamOverrideDelayMs) ? 2 : 3;
            }
            return i;
        }
    }

    public final void broadcastRingerMode(String str, int i) {
        if (this.mSystemServer.isPrivileged()) {
            Intent intent = new Intent(str);
            intent.putExtra("android.media.EXTRA_RINGER_MODE", i);
            intent.addFlags(603979776);
            sendStickyBroadcastToAll(intent);
        }
    }

    public final void broadcastVibrateSetting(int i) {
        if (this.mSystemServer.isPrivileged() && this.mActivityManagerInternal.isSystemReady()) {
            Intent intent = new Intent("android.media.VIBRATE_SETTING_CHANGED");
            intent.putExtra("android.media.EXTRA_VIBRATE_TYPE", i);
            intent.putExtra("android.media.EXTRA_VIBRATE_SETTING", getVibrateSetting(i));
            sendBroadcastToAll(intent, null);
        }
    }

    public final void queueMsgUnderWakeLock(Handler handler, int i, int i2, int i3, Object obj, int i4) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mAudioEventWakeLock.acquire();
            Binder.restoreCallingIdentity(clearCallingIdentity);
            sendMsg(handler, i, 2, i2, i3, obj, i4);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public static void sendMsg(Handler handler, int i, int i2, int i3, int i4, Object obj, int i5) {
        if (i2 == 0) {
            handler.removeMessages(i);
        } else if (i2 == 1 && handler.hasMessages(i)) {
            return;
        }
        handler.sendMessageAtTime(handler.obtainMessage(i, i3, i4, obj), SystemClock.uptimeMillis() + i5);
    }

    public static void sendBundleMsg(Handler handler, int i, int i2, int i3, int i4, Object obj, Bundle bundle, int i5) {
        if (i2 == 0) {
            handler.removeMessages(i);
        } else if (i2 == 1 && handler.hasMessages(i)) {
            return;
        }
        Message obtainMessage = handler.obtainMessage(i, i3, i4, obj);
        obtainMessage.setData(bundle);
        handler.sendMessageAtTime(obtainMessage, SystemClock.uptimeMillis() + i5);
    }

    public boolean checkAudioSettingsPermission(String str) {
        if (callingOrSelfHasAudioSettingsPermission()) {
            return true;
        }
        Log.w("AS.AudioService", "Audio Settings Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return false;
    }

    public final boolean callingOrSelfHasAudioSettingsPermission() {
        return this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_SETTINGS") == 0;
    }

    public final boolean callingHasAudioSettingsPermission() {
        return this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_SETTINGS") == 0;
    }

    public final boolean hasAudioSettingsPermission(int i, int i2) {
        return this.mContext.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", i2, i) == 0;
    }

    public void initMinStreamVolumeWithoutModifyAudioSettings() {
        int[] iArr;
        int i = Float.isNaN(AudioSystem.getStreamVolumeDB(4, MIN_STREAM_VOLUME[4], 4194304)) ? 2 : 4194304;
        int i2 = MAX_STREAM_VOLUME[4];
        while (i2 >= MIN_STREAM_VOLUME[4] && AudioSystem.getStreamVolumeDB(4, i2, i) >= -36.0f) {
            i2--;
        }
        int i3 = MIN_STREAM_VOLUME[4];
        if (i2 > i3) {
            i3 = Math.min(i2 + 1, MAX_STREAM_VOLUME[4]);
        }
        for (int i4 : mStreamVolumeAlias) {
            if (mStreamVolumeAlias[i4] == 4) {
                this.mStreamStates[i4].updateNoPermMinIndex(i3);
            }
        }
    }

    @VisibleForTesting
    public int getDeviceForStream(int i) {
        return selectOneAudioDevice(getDeviceSetForStream(i));
    }

    public final int selectOneAudioDevice(Set<Integer> set) {
        if (set.isEmpty()) {
            return 0;
        }
        if (set.size() == 1) {
            return set.iterator().next().intValue();
        }
        if (set.contains(2)) {
            return 2;
        }
        if (set.contains(4194304)) {
            return 4194304;
        }
        if (set.contains(262144)) {
            return 262144;
        }
        if (set.contains(262145)) {
            return 262145;
        }
        if (set.contains(2097152)) {
            return 2097152;
        }
        if (set.contains(524288)) {
            return 524288;
        }
        for (Integer num : set) {
            int intValue = num.intValue();
            if (AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(intValue))) {
                return intValue;
            }
        }
        Log.w("AS.AudioService", "selectOneAudioDevice returning DEVICE_NONE from invalid device combination " + AudioSystem.deviceSetToString(set));
        return 0;
    }

    @Deprecated
    public int getDeviceMaskForStream(int i) {
        ensureValidStreamType(i);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return AudioSystem.getDeviceMaskFromSet(getDeviceSetForStreamDirect(i));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final Set<Integer> getDeviceSetForStreamDirect(int i) {
        return AudioSystem.generateAudioDeviceTypesSet(getDevicesForAttributesInt(AudioProductStrategy.getAudioAttributesForStrategyWithLegacyStreamType(i), true));
    }

    public Set<Integer> getDeviceSetForStream(int i) {
        Set<Integer> observeDevicesForStream_syncVSS;
        ensureValidStreamType(i);
        synchronized (VolumeStreamState.class) {
            observeDevicesForStream_syncVSS = this.mStreamStates[i].observeDevicesForStream_syncVSS(true);
        }
        return observeDevicesForStream_syncVSS;
    }

    public final void onObserveDevicesForAllStreams(int i) {
        synchronized (this.mSettingsLock) {
            synchronized (VolumeStreamState.class) {
                int i2 = 0;
                while (true) {
                    VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                    if (i2 < volumeStreamStateArr.length) {
                        if (i2 != i) {
                            for (Integer num : volumeStreamStateArr[i2].observeDevicesForStream_syncVSS(false)) {
                                updateVolumeStates(num.intValue(), i2, "AudioService#onObserveDevicesForAllStreams");
                            }
                        }
                        i2++;
                    }
                }
            }
        }
    }

    @VisibleForTesting
    public void postObserveDevicesForAllStreams() {
        postObserveDevicesForAllStreams(-1);
    }

    @VisibleForTesting
    public void postObserveDevicesForAllStreams(int i) {
        sendMsg(this.mAudioHandler, 27, 2, i, 0, null, 0);
    }

    @RequiresPermission(anyOf = {"android.permission.MODIFY_AUDIO_ROUTING", "android.permission.BLUETOOTH_PRIVILEGED"})
    public void registerDeviceVolumeDispatcherForAbsoluteVolume(boolean z, IAudioDeviceVolumeDispatcher iAudioDeviceVolumeDispatcher, String str, AudioDeviceAttributes audioDeviceAttributes, List<VolumeInfo> list, boolean z2, int i) {
        int[] legacyStreamTypes;
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.BLUETOOTH_PRIVILEGED") != 0) {
            throw new SecurityException("Missing MODIFY_AUDIO_ROUTING or BLUETOOTH_PRIVILEGED permissions");
        }
        Objects.requireNonNull(audioDeviceAttributes);
        Objects.requireNonNull(list);
        int internalType = audioDeviceAttributes.getInternalType();
        if (z) {
            AbsoluteVolumeDeviceInfo absoluteVolumeDeviceInfo = new AbsoluteVolumeDeviceInfo(audioDeviceAttributes, list, iAudioDeviceVolumeDispatcher, z2, i);
            AbsoluteVolumeDeviceInfo absoluteVolumeDeviceInfo2 = this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(internalType));
            if (absoluteVolumeDeviceInfo2 != null && absoluteVolumeDeviceInfo2.mDeviceVolumeBehavior == i) {
                r11 = false;
            }
            if (r11) {
                removeAudioSystemDeviceOutFromFullVolumeDevices(internalType);
                removeAudioSystemDeviceOutFromFixedVolumeDevices(internalType);
                addAudioSystemDeviceOutToAbsVolumeDevices(internalType, absoluteVolumeDeviceInfo);
                dispatchDeviceVolumeBehavior(audioDeviceAttributes, i);
            }
            for (VolumeInfo volumeInfo : list) {
                if (volumeInfo.getVolumeIndex() != -100 && volumeInfo.getMinVolumeIndex() != -100 && volumeInfo.getMaxVolumeIndex() != -100) {
                    if (volumeInfo.hasStreamType()) {
                        setStreamVolumeInt(volumeInfo.getStreamType(), rescaleIndex(volumeInfo, volumeInfo.getStreamType()), internalType, false, str, true);
                    } else {
                        for (int i2 : volumeInfo.getVolumeGroup().getLegacyStreamTypes()) {
                            setStreamVolumeInt(i2, rescaleIndex(volumeInfo, i2), internalType, false, str, true);
                        }
                    }
                }
            }
            return;
        }
        if (removeAudioSystemDeviceOutFromAbsVolumeDevices(internalType) != null) {
            dispatchDeviceVolumeBehavior(audioDeviceAttributes, 0);
        }
    }

    @EnforcePermission(anyOf = {"MODIFY_AUDIO_ROUTING", "MODIFY_AUDIO_SETTINGS_PRIVILEGED"})
    public void setDeviceVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes, int i, String str) {
        super.setDeviceVolumeBehavior_enforcePermission();
        Objects.requireNonNull(audioDeviceAttributes);
        AudioManager.enforceValidVolumeBehavior(i);
        EventLogger eventLogger = sVolumeLogger;
        eventLogger.enqueue(new EventLogger.StringEvent("setDeviceVolumeBehavior: dev:" + AudioSystem.getOutputDeviceName(audioDeviceAttributes.getInternalType()) + " addr:" + audioDeviceAttributes.getAddress() + " behavior:" + AudioDeviceVolumeManager.volumeBehaviorName(i) + " pack:" + str).printLog("AS.AudioService"));
        if (str == null) {
            str = "";
        }
        if (audioDeviceAttributes.getType() == 8) {
            avrcpSupportsAbsoluteVolume(audioDeviceAttributes.getAddress(), i == 3);
            return;
        }
        setDeviceVolumeBehaviorInternal(audioDeviceAttributes, i, str);
        persistDeviceVolumeBehavior(audioDeviceAttributes.getInternalType(), i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x002c, code lost:
        if (removeAudioSystemDeviceOutFromAbsVolumeDevices(r0) != null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x003c, code lost:
        if (removeAudioSystemDeviceOutFromAbsVolumeDevices(r0) != null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x004c, code lost:
        if (removeAudioSystemDeviceOutFromAbsVolumeDevices(r0) != null) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x004f, code lost:
        r1 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0050, code lost:
        r2 = false | (r1 | r3);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setDeviceVolumeBehaviorInternal(AudioDeviceAttributes audioDeviceAttributes, int i, String str) {
        boolean removeAudioSystemDeviceOutFromFullVolumeDevices;
        int internalType = audioDeviceAttributes.getInternalType();
        boolean z = true;
        boolean z2 = false;
        if (i == 0) {
            removeAudioSystemDeviceOutFromFullVolumeDevices = removeAudioSystemDeviceOutFromFullVolumeDevices(internalType) | removeAudioSystemDeviceOutFromFixedVolumeDevices(internalType);
        } else if (i == 1) {
            removeAudioSystemDeviceOutFromFullVolumeDevices = addAudioSystemDeviceOutToFullVolumeDevices(internalType) | removeAudioSystemDeviceOutFromFixedVolumeDevices(internalType);
        } else if (i == 2) {
            removeAudioSystemDeviceOutFromFullVolumeDevices = removeAudioSystemDeviceOutFromFullVolumeDevices(internalType) | addAudioSystemDeviceOutToFixedVolumeDevices(internalType);
        } else if (i == 3 || i == 4 || i == 5) {
            throw new IllegalArgumentException("Absolute volume unsupported for now");
        }
        if (z2) {
            sendMsg(this.mAudioHandler, 47, 2, i, 0, audioDeviceAttributes, 0);
        }
        sDeviceLogger.enqueue(new EventLogger.StringEvent("Volume behavior " + i + " for dev=0x" + Integer.toHexString(internalType) + " from:" + str));
        StringBuilder sb = new StringBuilder();
        sb.append("setDeviceVolumeBehavior:");
        sb.append(str);
        postUpdateVolumeStatesForAudioDevice(internalType, sb.toString());
    }

    @EnforcePermission(anyOf = {"MODIFY_AUDIO_ROUTING", "QUERY_AUDIO_STATE", "MODIFY_AUDIO_SETTINGS_PRIVILEGED"})
    public int getDeviceVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes) {
        super.getDeviceVolumeBehavior_enforcePermission();
        Objects.requireNonNull(audioDeviceAttributes);
        return getDeviceVolumeBehaviorInt(audioDeviceAttributes);
    }

    public final int getDeviceVolumeBehaviorInt(AudioDeviceAttributes audioDeviceAttributes) {
        int internalType = audioDeviceAttributes.getInternalType();
        if (this.mFullVolumeDevices.contains(Integer.valueOf(internalType))) {
            return 1;
        }
        if (this.mFixedVolumeDevices.contains(Integer.valueOf(internalType))) {
            return 2;
        }
        if (this.mAbsVolumeMultiModeCaseDevices.contains(Integer.valueOf(internalType))) {
            return 4;
        }
        if (this.mAbsoluteVolumeDeviceInfoMap.containsKey(Integer.valueOf(internalType))) {
            return this.mAbsoluteVolumeDeviceInfoMap.get(Integer.valueOf(internalType)).mDeviceVolumeBehavior;
        }
        return (isA2dpAbsoluteVolumeDevice(internalType) || AudioSystem.isLeAudioDeviceType(internalType)) ? 3 : 0;
    }

    public boolean isVolumeFixed() {
        if (this.mUseFixedVolume) {
            return true;
        }
        for (AudioDeviceAttributes audioDeviceAttributes : getDevicesForAttributesInt(new AudioAttributes.Builder().setUsage(1).build(), true)) {
            if (getDeviceVolumeBehaviorInt(audioDeviceAttributes) == 2) {
                return true;
            }
        }
        return false;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void setWiredDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i, String str) {
        super.setWiredDeviceConnectionState_enforcePermission();
        if (i != 1 && i != 0) {
            throw new IllegalArgumentException("Invalid state " + i);
        }
        new MediaMetrics.Item("audio.service.setWiredDeviceConnectionState").set(MediaMetrics.Property.ADDRESS, audioDeviceAttributes.getAddress()).set(MediaMetrics.Property.CLIENT_NAME, str).set(MediaMetrics.Property.DEVICE, AudioSystem.getDeviceName(audioDeviceAttributes.getInternalType())).set(MediaMetrics.Property.NAME, audioDeviceAttributes.getName()).set(MediaMetrics.Property.STATE, i == 1 ? "connected" : "disconnected").record();
        this.mDeviceBroker.setWiredDeviceConnectionState(audioDeviceAttributes, i, str);
        if (audioDeviceAttributes.getInternalType() == -2013265920) {
            updateHdmiAudioSystemClient();
        }
    }

    public final void updateHdmiAudioSystemClient() {
        Slog.d("AS.AudioService", "Hdmi Audio System Client is updated");
        synchronized (this.mHdmiClientLock) {
            this.mHdmiAudioSystemClient = this.mHdmiManager.getAudioSystemClient();
        }
    }

    public void setTestDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, boolean z) {
        Objects.requireNonNull(audioDeviceAttributes);
        enforceModifyAudioRoutingPermission();
        this.mDeviceBroker.setTestDeviceConnectionState(audioDeviceAttributes, z ? 1 : 0);
        sendMsg(this.mAudioHandler, 41, 0, 0, 0, null, 0);
    }

    public void handleBluetoothActiveDeviceChanged(BluetoothDevice bluetoothDevice, BluetoothDevice bluetoothDevice2, BluetoothProfileConnectionInfo bluetoothProfileConnectionInfo) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.BLUETOOTH_STACK") != 0) {
            throw new SecurityException("Bluetooth is the only caller allowed");
        }
        if (bluetoothProfileConnectionInfo == null) {
            throw new IllegalArgumentException("Illegal null BluetoothProfileConnectionInfo for device " + bluetoothDevice2 + " -> " + bluetoothDevice);
        }
        int profile = bluetoothProfileConnectionInfo.getProfile();
        if (profile != 2 && profile != 11 && profile != 22 && profile != 26 && profile != 21) {
            throw new IllegalArgumentException("Illegal BluetoothProfile profile for device " + bluetoothDevice2 + " -> " + bluetoothDevice + ". Got: " + profile);
        }
        sendMsg(this.mAudioHandler, 38, 2, 0, 0, new AudioDeviceBroker.BtDeviceChangedData(bluetoothDevice, bluetoothDevice2, bluetoothProfileConnectionInfo, "AudioService"), 0);
    }

    @VisibleForTesting
    public void setMusicMute(boolean z) {
        this.mStreamStates[3].muteInternally(z);
    }

    @VisibleForTesting
    public void postAccessoryPlugMediaUnmute(int i) {
        sendMsg(this.mAudioHandler, 21, 2, i, 0, null, 0);
    }

    public final void onAccessoryPlugMediaUnmute(int i) {
        if (this.mNm.getZenMode() == 2 || isStreamMutedByRingerOrZenMode(3) || !DEVICE_MEDIA_UNMUTED_ON_PLUG_SET.contains(Integer.valueOf(i)) || !this.mStreamStates[3].mIsMuted || this.mStreamStates[3].getIndex(i) == 0 || !getDeviceSetForStreamDirect(3).contains(Integer.valueOf(i))) {
            return;
        }
        this.mStreamStates[3].mute(false);
    }

    public boolean hasHapticChannels(Uri uri) {
        return AudioManager.hasHapticChannelsImpl(this.mContext, uri);
    }

    public final void initVolumeGroupStates() {
        for (AudioVolumeGroup audioVolumeGroup : getAudioVolumeGroups()) {
            try {
                ensureValidAttributes(audioVolumeGroup);
                sVolumeGroupStates.append(audioVolumeGroup.getId(), new VolumeGroupState(audioVolumeGroup));
            } catch (IllegalArgumentException unused) {
            }
        }
        int i = 0;
        while (true) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (i >= sparseArray.size()) {
                return;
            }
            sparseArray.valueAt(i).applyAllVolumes(false);
            i++;
        }
    }

    public final void ensureValidAttributes(AudioVolumeGroup audioVolumeGroup) {
        if (audioVolumeGroup.getAudioAttributes().stream().anyMatch(new Predicate() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda21
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$ensureValidAttributes$11;
                lambda$ensureValidAttributes$11 = AudioService.lambda$ensureValidAttributes$11((AudioAttributes) obj);
                return lambda$ensureValidAttributes$11;
            }
        })) {
            return;
        }
        throw new IllegalArgumentException("Volume Group " + audioVolumeGroup.name() + " has no valid audio attributes");
    }

    public static /* synthetic */ boolean lambda$ensureValidAttributes$11(AudioAttributes audioAttributes) {
        return !audioAttributes.equals(AudioProductStrategy.getDefaultAttributes());
    }

    public final void readVolumeGroupsSettings(boolean z) {
        synchronized (this.mSettingsLock) {
            synchronized (VolumeStreamState.class) {
                int i = 0;
                while (true) {
                    SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
                    if (i < sparseArray.size()) {
                        VolumeGroupState valueAt = sparseArray.valueAt(i);
                        if (!z || !valueAt.isMusic()) {
                            valueAt.clearIndexCache();
                            valueAt.readSettings();
                        }
                        valueAt.applyAllVolumes(z);
                        i++;
                    }
                }
            }
        }
    }

    public final void restoreVolumeGroups() {
        int i = 0;
        while (true) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (i >= sparseArray.size()) {
                return;
            }
            sparseArray.valueAt(i).applyAllVolumes(false);
            i++;
        }
    }

    public final void dumpVolumeGroups(PrintWriter printWriter) {
        printWriter.println("\nVolume Groups (device: index)");
        int i = 0;
        while (true) {
            SparseArray<VolumeGroupState> sparseArray = sVolumeGroupStates;
            if (i >= sparseArray.size()) {
                return;
            }
            sparseArray.valueAt(i).dump(printWriter);
            printWriter.println("");
            i++;
        }
    }

    public static int getVolumeGroupForStreamType(int i) {
        AudioAttributes audioAttributesForStrategyWithLegacyStreamType = AudioProductStrategy.getAudioAttributesForStrategyWithLegacyStreamType(i);
        if (audioAttributesForStrategyWithLegacyStreamType.equals(new AudioAttributes.Builder().build())) {
            return -1;
        }
        return AudioProductStrategy.getVolumeGroupIdForAudioAttributes(audioAttributesForStrategyWithLegacyStreamType, false);
    }

    /* loaded from: classes.dex */
    public class VolumeGroupState {
        public AudioAttributes mAudioAttributes;
        public final AudioVolumeGroup mAudioVolumeGroup;
        public boolean mHasValidStreamType;
        public final SparseIntArray mIndexMap;
        public int mIndexMax;
        public int mIndexMin;
        public boolean mIsMuted;
        public int mPublicStreamType;
        public final String mSettingName;

        public final int getDeviceForVolume() {
            return AudioService.this.getDeviceForStream(this.mPublicStreamType);
        }

        public VolumeGroupState(AudioVolumeGroup audioVolumeGroup) {
            this.mIndexMap = new SparseIntArray(8);
            int i = 0;
            this.mHasValidStreamType = false;
            this.mPublicStreamType = 3;
            this.mAudioAttributes = AudioProductStrategy.getDefaultAttributes();
            this.mIsMuted = false;
            this.mAudioVolumeGroup = audioVolumeGroup;
            Iterator it = audioVolumeGroup.getAudioAttributes().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AudioAttributes audioAttributes = (AudioAttributes) it.next();
                if (!audioAttributes.equals(this.mAudioAttributes)) {
                    this.mAudioAttributes = audioAttributes;
                    break;
                }
            }
            int[] legacyStreamTypes = this.mAudioVolumeGroup.getLegacyStreamTypes();
            String str = "";
            if (legacyStreamTypes.length != 0) {
                int length = legacyStreamTypes.length;
                while (true) {
                    if (i < length) {
                        int i2 = legacyStreamTypes[i];
                        if (i2 != -1 && i2 < AudioSystem.getNumStreamTypes()) {
                            this.mPublicStreamType = i2;
                            this.mHasValidStreamType = true;
                            str = Settings.System.VOLUME_SETTINGS_INT[i2];
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                int[] iArr = AudioService.MIN_STREAM_VOLUME;
                int i3 = this.mPublicStreamType;
                this.mIndexMin = iArr[i3];
                this.mIndexMax = AudioService.MAX_STREAM_VOLUME[i3];
            } else if (!audioVolumeGroup.getAudioAttributes().isEmpty()) {
                this.mIndexMin = AudioSystem.getMinVolumeIndexForAttributes(this.mAudioAttributes);
                this.mIndexMax = AudioSystem.getMaxVolumeIndexForAttributes(this.mAudioAttributes);
            } else {
                throw new IllegalArgumentException("volume group: " + this.mAudioVolumeGroup.name() + " has neither valid attributes nor valid stream types assigned");
            }
            if (str.isEmpty()) {
                str = "volume_" + name();
            }
            this.mSettingName = str;
            readSettings();
        }

        public int[] getLegacyStreamTypes() {
            return this.mAudioVolumeGroup.getLegacyStreamTypes();
        }

        public String name() {
            return this.mAudioVolumeGroup.name();
        }

        public final boolean isVssMuteBijective(int i) {
            return AudioService.this.isStreamAffectedByMute(i) && getMinIndex() == (AudioService.this.mStreamStates[i].mIndexMin + 5) / 10 && (getMinIndex() == 0 || AudioService.isCallStream(i));
        }

        public final boolean isMutable() {
            return this.mIndexMin == 0 || (this.mHasValidStreamType && isVssMuteBijective(this.mPublicStreamType));
        }

        @GuardedBy({"AudioService.VolumeStreamState.class"})
        public boolean mute(boolean z) {
            if (isMutable()) {
                boolean z2 = this.mIsMuted != z;
                if (z2) {
                    this.mIsMuted = z;
                    applyAllVolumes(false);
                }
                return z2;
            }
            return false;
        }

        public boolean isMuted() {
            return this.mIsMuted;
        }

        public void adjustVolume(int i, int i2) {
            synchronized (VolumeStreamState.class) {
                int deviceForVolume = getDeviceForVolume();
                int index = getIndex(deviceForVolume);
                if (!AudioService.this.isMuteAdjust(i) || isMutable()) {
                    boolean z = true;
                    if (i == -100) {
                        if (index != 0) {
                            mute(true);
                        }
                        this.mIsMuted = true;
                    } else if (i != -1) {
                        if (i == 1) {
                            setVolumeIndex(Math.min(index + 1, this.mIndexMax), deviceForVolume, i2);
                        } else if (i == 100) {
                            mute(false);
                        } else if (i == 101) {
                            if (this.mIsMuted) {
                                z = false;
                            }
                            mute(z);
                        }
                    } else if (isMuted() && index != 0) {
                        mute(false);
                    } else {
                        setVolumeIndex(Math.max(index - 1, this.mIndexMin), deviceForVolume, i2);
                    }
                }
            }
        }

        public int getVolumeIndex() {
            int index;
            synchronized (VolumeStreamState.class) {
                index = getIndex(getDeviceForVolume());
            }
            return index;
        }

        public void setVolumeIndex(int i, int i2) {
            synchronized (VolumeStreamState.class) {
                if (AudioService.this.mUseFixedVolume) {
                    return;
                }
                setVolumeIndex(i, getDeviceForVolume(), i2);
            }
        }

        @GuardedBy({"AudioService.VolumeStreamState.class"})
        public final void setVolumeIndex(int i, int i2, int i3) {
            updateVolumeIndex(i, i2);
            if (mute(i == 0)) {
                return;
            }
            setVolumeIndexInt(getValidIndex(i), i2, i3);
        }

        @GuardedBy({"AudioService.VolumeStreamState.class"})
        public void updateVolumeIndex(int i, int i2) {
            if (this.mIndexMap.indexOfKey(i2) < 0 || this.mIndexMap.get(i2) != i) {
                this.mIndexMap.put(i2, getValidIndex(i));
                AudioService.sendMsg(AudioService.this.mAudioHandler, 2, 2, i2, 0, this, 500);
            }
        }

        @GuardedBy({"AudioService.VolumeStreamState.class"})
        public final void setVolumeIndexInt(int i, int i2, int i3) {
            if (this.mHasValidStreamType && isVssMuteBijective(this.mPublicStreamType) && AudioService.this.mStreamStates[this.mPublicStreamType].isFullyMuted()) {
                i = 0;
            } else if (this.mPublicStreamType == 6 && i == 0) {
                i = 1;
            }
            AudioSystem.setVolumeIndexForAttributes(this.mAudioAttributes, i, i2);
        }

        @GuardedBy({"AudioService.VolumeStreamState.class"})
        public final int getIndex(int i) {
            int i2 = this.mIndexMap.get(i, -1);
            return i2 != -1 ? i2 : this.mIndexMap.get(1073741824);
        }

        public int getMaxIndex() {
            return this.mIndexMax;
        }

        public int getMinIndex() {
            return this.mIndexMin;
        }

        public final boolean isValidStream(int i) {
            return i != -1 && i < AudioService.this.mStreamStates.length;
        }

        public boolean isMusic() {
            return this.mHasValidStreamType && this.mPublicStreamType == 3;
        }

        public void applyAllVolumes(boolean z) {
            int i;
            int i2;
            int[] legacyStreamTypes;
            synchronized (VolumeStreamState.class) {
                int i3 = 0;
                while (true) {
                    i = 1073741824;
                    if (i3 >= this.mIndexMap.size()) {
                        break;
                    }
                    int keyAt = this.mIndexMap.keyAt(i3);
                    int valueAt = this.mIndexMap.valueAt(i3);
                    if (keyAt != 1073741824) {
                        boolean z2 = false;
                        for (int i4 : getLegacyStreamTypes()) {
                            if (isValidStream(i4)) {
                                boolean z3 = AudioService.this.mStreamStates[i4].mIsMuted;
                                int deviceForStream = AudioService.this.getDeviceForStream(i4);
                                int index = (AudioService.this.mStreamStates[i4].getIndex(deviceForStream) + 5) / 10;
                                if (keyAt == deviceForStream) {
                                    if (index == valueAt && isMuted() == z3 && isVssMuteBijective(i4)) {
                                        z2 = true;
                                    } else {
                                        if (index != valueAt) {
                                            AudioService.this.mStreamStates[i4].setIndex(valueAt * 10, keyAt, "from vgs", true);
                                        }
                                        if (isMuted() != z3 && isVssMuteBijective(i4)) {
                                            AudioService.this.mStreamStates[i4].mute(isMuted());
                                        }
                                    }
                                }
                            }
                        }
                        if (!z2) {
                            if (isMuted()) {
                                valueAt = 0;
                            }
                            setVolumeIndexInt(valueAt, keyAt, 0);
                        }
                    }
                    i3++;
                }
                int index2 = getIndex(1073741824);
                int deviceForVolume = getDeviceForVolume();
                boolean z4 = z && this.mIndexMap.indexOfKey(deviceForVolume) < 0;
                int[] legacyStreamTypes2 = getLegacyStreamTypes();
                int length = legacyStreamTypes2.length;
                int i5 = 0;
                boolean z5 = false;
                while (i5 < length) {
                    int i6 = legacyStreamTypes2[i5];
                    if (isValidStream(i6)) {
                        boolean z6 = AudioService.this.mStreamStates[i6].mIsMuted;
                        int index3 = (AudioService.this.mStreamStates[i6].getIndex(i) + 5) / 10;
                        if (z4) {
                            AudioService.this.mStreamStates[i6].setIndex(index2 * 10, deviceForVolume, "from vgs", true);
                        }
                        if (index3 == index2 && isMuted() == z6 && isVssMuteBijective(i6)) {
                            z5 = true;
                        } else {
                            if (index3 != index2) {
                                AudioService.this.mStreamStates[i6].setIndex(index2 * 10, 1073741824, "from vgs", true);
                            }
                            if (isMuted() != z6 && isVssMuteBijective(i6)) {
                                AudioService.this.mStreamStates[i6].mute(isMuted());
                            }
                        }
                    }
                    i5++;
                    i = 1073741824;
                }
                if (!z5) {
                    setVolumeIndexInt(isMuted() ? 0 : index2, 1073741824, 0);
                }
                if (z4) {
                    if (isMuted()) {
                        i2 = 0;
                        index2 = 0;
                    } else {
                        i2 = 0;
                    }
                    setVolumeIndexInt(index2, deviceForVolume, i2);
                }
            }
        }

        public void clearIndexCache() {
            this.mIndexMap.clear();
        }

        public final void persistVolumeGroup(int i) {
            if (AudioService.this.mUseFixedVolume) {
                return;
            }
            if (AudioService.this.mSettings.putSystemIntForUser(AudioService.this.mContentResolver, getSettingNameForDevice(i), getIndex(i), isMusic() ? 0 : -2)) {
                return;
            }
            Log.e("AS.AudioService", "persistVolumeGroup failed for group " + this.mAudioVolumeGroup.name());
        }

        public void readSettings() {
            synchronized (VolumeStreamState.class) {
                if (AudioService.this.mUseFixedVolume) {
                    this.mIndexMap.put(1073741824, this.mIndexMax);
                    return;
                }
                for (Integer num : AudioSystem.DEVICE_OUT_ALL_SET) {
                    int intValue = num.intValue();
                    int i = intValue == 1073741824 ? AudioSystem.DEFAULT_STREAM_VOLUME[this.mPublicStreamType] : -1;
                    int systemIntForUser = AudioService.this.mSettings.getSystemIntForUser(AudioService.this.mContentResolver, getSettingNameForDevice(intValue), i, isMusic() ? 0 : -2);
                    if (systemIntForUser != -1) {
                        if (this.mPublicStreamType == 7 && AudioService.this.mCameraSoundForced) {
                            systemIntForUser = this.mIndexMax;
                        }
                        this.mIndexMap.put(intValue, getValidIndex(systemIntForUser));
                    }
                }
            }
        }

        @GuardedBy({"AudioService.VolumeStreamState.class"})
        public final int getValidIndex(int i) {
            int i2 = this.mIndexMin;
            return i < i2 ? i2 : (AudioService.this.mUseFixedVolume || i > this.mIndexMax) ? this.mIndexMax : i;
        }

        public String getSettingNameForDevice(int i) {
            if (AudioSystem.getOutputDeviceName(i).isEmpty()) {
                return this.mSettingName;
            }
            return this.mSettingName + "_" + AudioSystem.getOutputDeviceName(i);
        }

        public final void dump(final PrintWriter printWriter) {
            printWriter.println("- VOLUME GROUP " + this.mAudioVolumeGroup.name() + XmlUtils.STRING_ARRAY_SEPARATOR);
            printWriter.print("   Muted: ");
            printWriter.println(this.mIsMuted);
            printWriter.print("   Min: ");
            printWriter.println(this.mIndexMin);
            printWriter.print("   Max: ");
            printWriter.println(this.mIndexMax);
            printWriter.print("   Current: ");
            int i = 0;
            for (int i2 = 0; i2 < this.mIndexMap.size(); i2++) {
                if (i2 > 0) {
                    printWriter.print(", ");
                }
                int keyAt = this.mIndexMap.keyAt(i2);
                printWriter.print(Integer.toHexString(keyAt));
                String outputDeviceName = keyAt == 1073741824 ? "default" : AudioSystem.getOutputDeviceName(keyAt);
                if (!outputDeviceName.isEmpty()) {
                    printWriter.print(" (");
                    printWriter.print(outputDeviceName);
                    printWriter.print(")");
                }
                printWriter.print(": ");
                printWriter.print(this.mIndexMap.valueAt(i2));
            }
            printWriter.println();
            printWriter.print("   Devices: ");
            int deviceForVolume = getDeviceForVolume();
            for (Integer num : AudioSystem.DEVICE_OUT_ALL_SET) {
                int intValue = num.intValue();
                if ((deviceForVolume & intValue) == intValue) {
                    int i3 = i + 1;
                    if (i > 0) {
                        printWriter.print(", ");
                    }
                    printWriter.print(AudioSystem.getOutputDeviceName(intValue));
                    i = i3;
                }
            }
            printWriter.println();
            printWriter.print("   Streams: ");
            Arrays.stream(getLegacyStreamTypes()).forEach(new IntConsumer() { // from class: com.android.server.audio.AudioService$VolumeGroupState$$ExternalSyntheticLambda0
                @Override // java.util.function.IntConsumer
                public final void accept(int i4) {
                    AudioService.VolumeGroupState.lambda$dump$0(printWriter, i4);
                }
            });
        }

        public static /* synthetic */ void lambda$dump$0(PrintWriter printWriter, int i) {
            printWriter.print(AudioSystem.streamToString(i) + " ");
        }
    }

    /* loaded from: classes.dex */
    public class VolumeStreamState {
        public final SparseIntArray mIndexMap;
        public int mIndexMax;
        public int mIndexMin;
        public int mIndexMinNoPerm;
        public boolean mIsMuted;
        public boolean mIsMutedInternally;
        public Set<Integer> mObservedDeviceSet;
        public final Intent mStreamDevicesChanged;
        public final Bundle mStreamDevicesChangedOptions;
        public final int mStreamType;
        public final Intent mVolumeChanged;
        public final Bundle mVolumeChangedOptions;
        public VolumeGroupState mVolumeGroupState;
        public String mVolumeIndexSettingName;

        public VolumeStreamState(String str, int i) {
            this.mVolumeGroupState = null;
            this.mIsMuted = false;
            this.mIsMutedInternally = false;
            this.mObservedDeviceSet = new TreeSet();
            this.mIndexMap = new SparseIntArray(8) { // from class: com.android.server.audio.AudioService.VolumeStreamState.1
                @Override // android.util.SparseIntArray
                public void put(int i2, int i3) {
                    super.put(i2, i3);
                    record("put", i2, i3);
                }

                @Override // android.util.SparseIntArray
                public void setValueAt(int i2, int i3) {
                    super.setValueAt(i2, i3);
                    record("setValueAt", keyAt(i2), i3);
                }

                public final void record(String str2, int i2, int i3) {
                    String outputDeviceName = i2 == 1073741824 ? "default" : AudioSystem.getOutputDeviceName(i2);
                    new MediaMetrics.Item("audio.volume." + AudioSystem.streamToString(VolumeStreamState.this.mStreamType) + "." + outputDeviceName).set(MediaMetrics.Property.EVENT, str2).set(MediaMetrics.Property.INDEX, Integer.valueOf(i3)).set(MediaMetrics.Property.MIN_INDEX, Integer.valueOf(VolumeStreamState.this.mIndexMin)).set(MediaMetrics.Property.MAX_INDEX, Integer.valueOf(VolumeStreamState.this.mIndexMax)).record();
                }
            };
            this.mVolumeIndexSettingName = str;
            this.mStreamType = i;
            int i2 = AudioService.MIN_STREAM_VOLUME[i] * 10;
            this.mIndexMin = i2;
            this.mIndexMinNoPerm = i2;
            int i3 = AudioService.MAX_STREAM_VOLUME[i] * 10;
            this.mIndexMax = i3;
            int initStreamVolume = AudioSystem.initStreamVolume(i, i2 / 10, i3 / 10);
            if (initStreamVolume != 0) {
                EventLogger eventLogger = AudioService.sLifecycleLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("VSS() stream:" + i + " initStreamVolume=" + initStreamVolume).printLog(1, "AS.AudioService"));
                AudioService.sendMsg(AudioService.this.mAudioHandler, 34, 1, 0, 0, "VSS()", 2000);
            }
            readSettings();
            Intent intent = new Intent("android.media.VOLUME_CHANGED_ACTION");
            this.mVolumeChanged = intent;
            intent.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", i);
            BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
            makeBasic.setDeliveryGroupPolicy(1);
            makeBasic.setDeliveryGroupMatchingKey("android.media.VOLUME_CHANGED_ACTION", String.valueOf(i));
            makeBasic.setDeferUntilActive(true);
            this.mVolumeChangedOptions = makeBasic.toBundle();
            Intent intent2 = new Intent("android.media.STREAM_DEVICES_CHANGED_ACTION");
            this.mStreamDevicesChanged = intent2;
            intent2.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", i);
            BroadcastOptions makeBasic2 = BroadcastOptions.makeBasic();
            makeBasic2.setDeliveryGroupPolicy(1);
            makeBasic2.setDeliveryGroupMatchingKey("android.media.STREAM_DEVICES_CHANGED_ACTION", String.valueOf(i));
            makeBasic2.setDeferUntilActive(true);
            this.mStreamDevicesChangedOptions = makeBasic2.toBundle();
        }

        public void setVolumeGroupState(VolumeGroupState volumeGroupState) {
            this.mVolumeGroupState = volumeGroupState;
        }

        public void updateNoPermMinIndex(int i) {
            int i2 = i * 10;
            this.mIndexMinNoPerm = i2;
            if (i2 < this.mIndexMin) {
                Log.e("AS.AudioService", "Invalid mIndexMinNoPerm for stream " + this.mStreamType);
                this.mIndexMinNoPerm = this.mIndexMin;
            }
        }

        @GuardedBy({"VolumeStreamState.class"})
        public Set<Integer> observeDevicesForStream_syncVSS(boolean z) {
            if (!AudioService.this.mSystemServer.isPrivileged()) {
                return new TreeSet();
            }
            Set<Integer> deviceSetForStreamDirect = AudioService.this.getDeviceSetForStreamDirect(this.mStreamType);
            if (deviceSetForStreamDirect.equals(this.mObservedDeviceSet)) {
                return this.mObservedDeviceSet;
            }
            int deviceMaskFromSet = AudioSystem.getDeviceMaskFromSet(deviceSetForStreamDirect);
            int deviceMaskFromSet2 = AudioSystem.getDeviceMaskFromSet(this.mObservedDeviceSet);
            this.mObservedDeviceSet = deviceSetForStreamDirect;
            if (z) {
                AudioService.this.postObserveDevicesForAllStreams(this.mStreamType);
            }
            int[] iArr = AudioService.mStreamVolumeAlias;
            int i = this.mStreamType;
            if (iArr[i] == i) {
                EventLogTags.writeStreamDevicesChanged(i, deviceMaskFromSet2, deviceMaskFromSet);
            }
            SomeArgs obtain = SomeArgs.obtain();
            obtain.arg1 = this.mStreamDevicesChanged;
            obtain.arg2 = this.mStreamDevicesChangedOptions;
            AudioService.sendMsg(AudioService.this.mAudioHandler, 32, 2, deviceMaskFromSet2, deviceMaskFromSet, obtain, 0);
            return this.mObservedDeviceSet;
        }

        public String getSettingNameForDevice(int i) {
            if (hasValidSettingsName()) {
                String outputDeviceName = AudioSystem.getOutputDeviceName(i);
                if (outputDeviceName.isEmpty()) {
                    return this.mVolumeIndexSettingName;
                }
                return this.mVolumeIndexSettingName + "_" + outputDeviceName;
            }
            return null;
        }

        public final boolean hasValidSettingsName() {
            String str = this.mVolumeIndexSettingName;
            return (str == null || str.isEmpty()) ? false : true;
        }

        public void readSettings() {
            synchronized (AudioService.this.mSettingsLock) {
                synchronized (VolumeStreamState.class) {
                    if (AudioService.this.mUseFixedVolume) {
                        this.mIndexMap.put(1073741824, this.mIndexMax);
                        return;
                    }
                    int i = this.mStreamType;
                    if (i != 1 && i != 7) {
                        synchronized (VolumeStreamState.class) {
                            for (Integer num : AudioSystem.DEVICE_OUT_ALL_SET) {
                                int intValue = num.intValue();
                                int i2 = intValue == 1073741824 ? AudioSystem.DEFAULT_STREAM_VOLUME[this.mStreamType] : -1;
                                if (hasValidSettingsName()) {
                                    i2 = AudioService.this.mSettings.getSystemIntForUser(AudioService.this.mContentResolver, getSettingNameForDevice(intValue), i2, -2);
                                }
                                if (i2 != -1) {
                                    this.mIndexMap.put(intValue, getValidIndex(i2 * 10, true));
                                }
                            }
                        }
                        return;
                    }
                    int i3 = AudioSystem.DEFAULT_STREAM_VOLUME[i] * 10;
                    if (AudioService.this.mCameraSoundForced) {
                        i3 = this.mIndexMax;
                    }
                    this.mIndexMap.put(1073741824, i3);
                }
            }
        }

        public final int getAbsoluteVolumeIndex(int i) {
            if (i == 0) {
                return 0;
            }
            if (i > 0 && i <= 3) {
                return ((int) (this.mIndexMax * AudioService.this.mPrescaleAbsoluteVolume[i - 1])) / 10;
            }
            return (this.mIndexMax + 5) / 10;
        }

        public final void setStreamVolumeIndex(int i, int i2) {
            if (this.mStreamType == 6 && i == 0 && !isFullyMuted()) {
                i = 1;
            }
            AudioService.this.mAudioSystem.setStreamVolumeIndexAS(this.mStreamType, i, i2);
        }

        public void applyDeviceVolume_syncVSS(int i) {
            int absoluteVolumeIndex;
            if (isFullyMuted()) {
                absoluteVolumeIndex = 0;
            } else if (AudioService.this.isAbsoluteVolumeDevice(i) || AudioService.this.isA2dpAbsoluteVolumeDevice(i) || AudioSystem.isLeAudioDeviceType(i)) {
                absoluteVolumeIndex = getAbsoluteVolumeIndex((getIndex(i) + 5) / 10);
            } else if (AudioService.this.isFullVolumeDevice(i)) {
                absoluteVolumeIndex = (this.mIndexMax + 5) / 10;
            } else if (i == 134217728) {
                absoluteVolumeIndex = (this.mIndexMax + 5) / 10;
            } else {
                absoluteVolumeIndex = (getIndex(i) + 5) / 10;
            }
            setStreamVolumeIndex(absoluteVolumeIndex, i);
        }

        public void applyAllVolumes() {
            int absoluteVolumeIndex;
            synchronized (VolumeStreamState.class) {
                int i = 0;
                boolean z = false;
                for (int i2 = 0; i2 < this.mIndexMap.size(); i2++) {
                    int keyAt = this.mIndexMap.keyAt(i2);
                    if (keyAt != 1073741824) {
                        if (isFullyMuted()) {
                            absoluteVolumeIndex = 0;
                        } else {
                            if (!AudioService.this.isAbsoluteVolumeDevice(keyAt) && !AudioService.this.isA2dpAbsoluteVolumeDevice(keyAt) && !AudioSystem.isLeAudioDeviceType(keyAt)) {
                                if (AudioService.this.isFullVolumeDevice(keyAt)) {
                                    absoluteVolumeIndex = (this.mIndexMax + 5) / 10;
                                } else if (keyAt == 134217728) {
                                    absoluteVolumeIndex = (this.mIndexMax + 5) / 10;
                                } else {
                                    absoluteVolumeIndex = (this.mIndexMap.valueAt(i2) + 5) / 10;
                                }
                            }
                            absoluteVolumeIndex = getAbsoluteVolumeIndex((getIndex(keyAt) + 5) / 10);
                            z = true;
                        }
                        AudioService.sendMsg(AudioService.this.mAudioHandler, 1005, 0, keyAt, z ? 1 : 0, this, 0);
                        setStreamVolumeIndex(absoluteVolumeIndex, keyAt);
                    }
                }
                if (!isFullyMuted()) {
                    i = (getIndex(1073741824) + 5) / 10;
                }
                setStreamVolumeIndex(i, 1073741824);
            }
        }

        public boolean adjustIndex(int i, int i2, String str, boolean z) {
            return setIndex(getIndex(i2) + i, i2, str, z);
        }

        public boolean setIndex(int i, int i2, String str, boolean z) {
            int index;
            int validIndex;
            boolean z2;
            boolean z3;
            synchronized (AudioService.this.mSettingsLock) {
                synchronized (VolumeStreamState.class) {
                    index = getIndex(i2);
                    validIndex = getValidIndex(i, z);
                    if (this.mStreamType == 7 && AudioService.this.mCameraSoundForced) {
                        validIndex = this.mIndexMax;
                    }
                    this.mIndexMap.put(i2, validIndex);
                    z2 = index != validIndex;
                    z3 = i2 == AudioService.this.getDeviceForStream(this.mStreamType);
                    for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                        VolumeStreamState volumeStreamState = AudioService.this.mStreamStates[numStreamTypes];
                        int i3 = this.mStreamType;
                        if (numStreamTypes != i3 && AudioService.mStreamVolumeAlias[numStreamTypes] == i3 && (z2 || !volumeStreamState.hasIndexForDevice(i2))) {
                            int rescaleIndex = AudioService.this.rescaleIndex(validIndex, this.mStreamType, numStreamTypes);
                            volumeStreamState.setIndex(rescaleIndex, i2, str, z);
                            if (z3) {
                                volumeStreamState.setIndex(rescaleIndex, AudioService.this.getDeviceForStream(numStreamTypes), str, z);
                            }
                        }
                    }
                    if (z2 && this.mStreamType == 2 && i2 == 2) {
                        for (int i4 = 0; i4 < this.mIndexMap.size(); i4++) {
                            int keyAt = this.mIndexMap.keyAt(i4);
                            if (AudioSystem.DEVICE_OUT_ALL_SCO_SET.contains(Integer.valueOf(keyAt))) {
                                this.mIndexMap.put(keyAt, validIndex);
                            }
                        }
                    }
                }
            }
            if (z2) {
                updateVolumeGroupIndex(i2, false);
                int i5 = (index + 5) / 10;
                int i6 = (validIndex + 5) / 10;
                int[] iArr = AudioService.mStreamVolumeAlias;
                int i7 = this.mStreamType;
                if (iArr[i7] == i7) {
                    if (str == null) {
                        Log.w("AS.AudioService", "No caller for volume_changed event", new Throwable());
                    }
                    EventLogTags.writeVolumeChanged(this.mStreamType, i5, i6, this.mIndexMax / 10, str);
                }
                if (i6 != i5 && z3) {
                    this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", i6);
                    this.mVolumeChanged.putExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", i5);
                    this.mVolumeChanged.putExtra("android.media.EXTRA_VOLUME_STREAM_TYPE_ALIAS", AudioService.mStreamVolumeAlias[this.mStreamType]);
                    AudioService.this.sendBroadcastToAll(this.mVolumeChanged, this.mVolumeChangedOptions);
                }
            }
            return z2;
        }

        public int getIndex(int i) {
            int i2;
            synchronized (VolumeStreamState.class) {
                i2 = this.mIndexMap.get(i, -1);
                if (i2 == -1) {
                    i2 = this.mIndexMap.get(1073741824);
                }
            }
            return i2;
        }

        public boolean hasIndexForDevice(int i) {
            boolean z;
            synchronized (VolumeStreamState.class) {
                z = this.mIndexMap.get(i, -1) != -1;
            }
            return z;
        }

        public int getMaxIndex() {
            return this.mIndexMax;
        }

        public int getMinIndex() {
            return this.mIndexMin;
        }

        public int getMinIndex(boolean z) {
            return z ? this.mIndexMin : this.mIndexMinNoPerm;
        }

        @GuardedBy({"VolumeStreamState.class"})
        public void setAllIndexes(VolumeStreamState volumeStreamState, String str) {
            if (this.mStreamType == volumeStreamState.mStreamType) {
                return;
            }
            int streamType = volumeStreamState.getStreamType();
            int rescaleIndex = AudioService.this.rescaleIndex(volumeStreamState.getIndex(1073741824), streamType, this.mStreamType);
            for (int i = 0; i < this.mIndexMap.size(); i++) {
                SparseIntArray sparseIntArray = this.mIndexMap;
                sparseIntArray.put(sparseIntArray.keyAt(i), rescaleIndex);
            }
            SparseIntArray sparseIntArray2 = volumeStreamState.mIndexMap;
            for (int i2 = 0; i2 < sparseIntArray2.size(); i2++) {
                setIndex(AudioService.this.rescaleIndex(sparseIntArray2.valueAt(i2), streamType, this.mStreamType), sparseIntArray2.keyAt(i2), str, true);
            }
        }

        @GuardedBy({"VolumeStreamState.class"})
        public void setAllIndexesToMax() {
            for (int i = 0; i < this.mIndexMap.size(); i++) {
                SparseIntArray sparseIntArray = this.mIndexMap;
                sparseIntArray.put(sparseIntArray.keyAt(i), this.mIndexMax);
            }
        }

        public final void updateVolumeGroupIndex(int i, boolean z) {
            boolean z2;
            synchronized (VolumeStreamState.class) {
                if (this.mVolumeGroupState != null) {
                    int index = (getIndex(i) + 5) / 10;
                    this.mVolumeGroupState.updateVolumeIndex(index, i);
                    if (isMutable()) {
                        VolumeGroupState volumeGroupState = this.mVolumeGroupState;
                        if (z) {
                            z2 = this.mIsMuted;
                        } else {
                            z2 = (index == 0 && !AudioService.isCallStream(this.mStreamType)) || this.mIsMuted;
                        }
                        volumeGroupState.mute(z2);
                    }
                }
            }
        }

        public boolean mute(boolean z) {
            boolean mute;
            synchronized (VolumeStreamState.class) {
                mute = mute(z, true);
            }
            if (mute) {
                AudioService.this.broadcastMuteSetting(this.mStreamType, z);
            }
            return mute;
        }

        public boolean muteInternally(boolean z) {
            boolean z2;
            synchronized (VolumeStreamState.class) {
                if (z != this.mIsMutedInternally) {
                    this.mIsMutedInternally = z;
                    applyAllVolumes();
                    z2 = true;
                } else {
                    z2 = false;
                }
            }
            if (z2) {
                AudioService.sVolumeLogger.enqueue(new AudioServiceEvents$VolumeEvent(9, this.mStreamType, z));
            }
            return z2;
        }

        @GuardedBy({"VolumeStreamState.class"})
        public boolean isFullyMuted() {
            return this.mIsMuted || this.mIsMutedInternally;
        }

        public final boolean isMutable() {
            return AudioService.this.isStreamAffectedByMute(this.mStreamType) && (this.mIndexMin == 0 || AudioService.isCallStream(this.mStreamType));
        }

        public boolean mute(boolean z, boolean z2) {
            boolean z3;
            synchronized (VolumeStreamState.class) {
                z3 = z != this.mIsMuted;
                if (z3) {
                    this.mIsMuted = z;
                    if (z2) {
                        doMute();
                    }
                }
            }
            return z3;
        }

        public void doMute() {
            synchronized (VolumeStreamState.class) {
                updateVolumeGroupIndex(AudioService.this.getDeviceForStream(this.mStreamType), true);
                AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, this, 0);
            }
        }

        public int getStreamType() {
            return this.mStreamType;
        }

        public void checkFixedVolumeDevices() {
            synchronized (VolumeStreamState.class) {
                if (AudioService.mStreamVolumeAlias[this.mStreamType] == 3) {
                    for (int i = 0; i < this.mIndexMap.size(); i++) {
                        int keyAt = this.mIndexMap.keyAt(i);
                        int valueAt = this.mIndexMap.valueAt(i);
                        if (AudioService.this.isFullVolumeDevice(keyAt) || (AudioService.this.isFixedVolumeDevice(keyAt) && valueAt != 0)) {
                            this.mIndexMap.put(keyAt, this.mIndexMax);
                        }
                        applyDeviceVolume_syncVSS(keyAt);
                    }
                }
            }
        }

        public final int getValidIndex(int i, boolean z) {
            int i2 = z ? this.mIndexMin : this.mIndexMinNoPerm;
            return i < i2 ? i2 : (AudioService.this.mUseFixedVolume || i > this.mIndexMax) ? this.mIndexMax : i;
        }

        public final void dump(PrintWriter printWriter) {
            printWriter.print("   Muted: ");
            printWriter.println(this.mIsMuted);
            printWriter.print("   Muted Internally: ");
            printWriter.println(this.mIsMutedInternally);
            printWriter.print("   Min: ");
            printWriter.print((this.mIndexMin + 5) / 10);
            if (this.mIndexMin != this.mIndexMinNoPerm) {
                printWriter.print(" w/o perm:");
                printWriter.println((this.mIndexMinNoPerm + 5) / 10);
            } else {
                printWriter.println();
            }
            printWriter.print("   Max: ");
            printWriter.println((this.mIndexMax + 5) / 10);
            printWriter.print("   streamVolume:");
            printWriter.println(AudioService.this.getStreamVolume(this.mStreamType));
            printWriter.print("   Current: ");
            for (int i = 0; i < this.mIndexMap.size(); i++) {
                if (i > 0) {
                    printWriter.print(", ");
                }
                int keyAt = this.mIndexMap.keyAt(i);
                printWriter.print(Integer.toHexString(keyAt));
                String outputDeviceName = keyAt == 1073741824 ? "default" : AudioSystem.getOutputDeviceName(keyAt);
                if (!outputDeviceName.isEmpty()) {
                    printWriter.print(" (");
                    printWriter.print(outputDeviceName);
                    printWriter.print(")");
                }
                printWriter.print(": ");
                printWriter.print((this.mIndexMap.valueAt(i) + 5) / 10);
            }
            printWriter.println();
            printWriter.print("   Devices: ");
            printWriter.print(AudioSystem.deviceSetToString(AudioService.this.getDeviceSetForStream(this.mStreamType)));
            printWriter.println();
            printWriter.print("   Volume Group: ");
            VolumeGroupState volumeGroupState = this.mVolumeGroupState;
            printWriter.println(volumeGroupState != null ? volumeGroupState.name() : "n/a");
        }
    }

    /* loaded from: classes.dex */
    public class AudioSystemThread extends Thread {
        public AudioSystemThread() {
            super("AudioService");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (AudioService.this) {
                AudioService.this.mAudioHandler = new AudioHandler();
                AudioService.this.notify();
            }
            Looper.loop();
        }
    }

    /* loaded from: classes.dex */
    public static final class DeviceVolumeUpdate {
        public final String mCaller;
        public final int mDevice;
        public final int mStreamType;
        public final int mVssVolIndex;

        public DeviceVolumeUpdate(int i, int i2, int i3, String str) {
            this.mStreamType = i;
            this.mVssVolIndex = i2;
            this.mDevice = i3;
            this.mCaller = str;
        }

        public DeviceVolumeUpdate(int i, int i2, String str) {
            this.mStreamType = i;
            this.mVssVolIndex = -2049;
            this.mDevice = i2;
            this.mCaller = str;
        }

        public boolean hasVolumeIndex() {
            return this.mVssVolIndex != -2049;
        }

        public int getVolumeIndex() throws IllegalStateException {
            Preconditions.checkState(this.mVssVolIndex != -2049);
            return this.mVssVolIndex;
        }
    }

    @VisibleForTesting
    public void postSetVolumeIndexOnDevice(int i, int i2, int i3, String str) {
        sendMsg(this.mAudioHandler, 26, 2, 0, 0, new DeviceVolumeUpdate(i, i2, i3, str), 0);
    }

    public void postApplyVolumeOnDevice(int i, int i2, String str) {
        sendMsg(this.mAudioHandler, 26, 2, 0, 0, new DeviceVolumeUpdate(i, i2, str), 0);
    }

    public final void onSetVolumeIndexOnDevice(DeviceVolumeUpdate deviceVolumeUpdate) {
        VolumeStreamState volumeStreamState = this.mStreamStates[deviceVolumeUpdate.mStreamType];
        if (deviceVolumeUpdate.hasVolumeIndex()) {
            int volumeIndex = deviceVolumeUpdate.getVolumeIndex();
            if (this.mSoundDoseHelper.checkSafeMediaVolume(deviceVolumeUpdate.mStreamType, volumeIndex, deviceVolumeUpdate.mDevice)) {
                volumeIndex = this.mSoundDoseHelper.safeMediaVolumeIndex(deviceVolumeUpdate.mDevice);
            }
            volumeStreamState.setIndex(volumeIndex, deviceVolumeUpdate.mDevice, deviceVolumeUpdate.mCaller, true);
            EventLogger eventLogger = sVolumeLogger;
            eventLogger.enqueue(new EventLogger.StringEvent(deviceVolumeUpdate.mCaller + " dev:0x" + Integer.toHexString(deviceVolumeUpdate.mDevice) + " volIdx:" + volumeIndex));
        } else {
            EventLogger eventLogger2 = sVolumeLogger;
            eventLogger2.enqueue(new EventLogger.StringEvent(deviceVolumeUpdate.mCaller + " update vol on dev:0x" + Integer.toHexString(deviceVolumeUpdate.mDevice)));
        }
        setDeviceVolume(volumeStreamState, deviceVolumeUpdate.mDevice);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0030 A[Catch: all -> 0x0077, TryCatch #0 {, blocks: (B:4:0x0003, B:6:0x000f, B:8:0x0015, B:13:0x0020, B:15:0x0030, B:17:0x0036, B:19:0x0040, B:21:0x0046, B:23:0x004c, B:25:0x0052, B:27:0x0058, B:28:0x005f, B:29:0x0066, B:30:0x0069), top: B:36:0x0003 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void setDeviceVolume(VolumeStreamState volumeStreamState, int i) {
        int i2;
        int numStreamTypes;
        synchronized (VolumeStreamState.class) {
            AudioHandler audioHandler = this.mAudioHandler;
            if (!isAbsoluteVolumeDevice(i) && !isA2dpAbsoluteVolumeDevice(i) && !AudioSystem.isLeAudioDeviceType(i)) {
                i2 = 0;
                sendMsg(audioHandler, 1005, 0, i, i2, volumeStreamState, 0);
                volumeStreamState.applyDeviceVolume_syncVSS(i);
                for (numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                    if (numStreamTypes != volumeStreamState.mStreamType && mStreamVolumeAlias[numStreamTypes] == volumeStreamState.mStreamType) {
                        int deviceForStream = getDeviceForStream(numStreamTypes);
                        if (i != deviceForStream && (isAbsoluteVolumeDevice(i) || isA2dpAbsoluteVolumeDevice(i) || AudioSystem.isLeAudioDeviceType(i))) {
                            this.mStreamStates[numStreamTypes].applyDeviceVolume_syncVSS(i);
                        }
                        this.mStreamStates[numStreamTypes].applyDeviceVolume_syncVSS(deviceForStream);
                    }
                }
            }
            i2 = 1;
            sendMsg(audioHandler, 1005, 0, i, i2, volumeStreamState, 0);
            volumeStreamState.applyDeviceVolume_syncVSS(i);
            while (numStreamTypes >= 0) {
            }
        }
        sendMsg(this.mAudioHandler, 1, 2, i, 0, volumeStreamState, 500);
    }

    /* loaded from: classes.dex */
    public class AudioHandler extends Handler {
        public AudioHandler() {
        }

        public AudioHandler(Looper looper) {
            super(looper);
        }

        public final void setAllVolumes(VolumeStreamState volumeStreamState) {
            volumeStreamState.applyAllVolumes();
            for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                if (numStreamTypes != volumeStreamState.mStreamType && AudioService.mStreamVolumeAlias[numStreamTypes] == volumeStreamState.mStreamType) {
                    AudioService.this.mStreamStates[numStreamTypes].applyAllVolumes();
                }
            }
        }

        public final void persistVolume(VolumeStreamState volumeStreamState, int i) {
            if (AudioService.this.mUseFixedVolume) {
                return;
            }
            if ((!AudioService.this.mIsSingleVolume || volumeStreamState.mStreamType == 3) && volumeStreamState.hasValidSettingsName()) {
                AudioService.this.mSettings.putSystemIntForUser(AudioService.this.mContentResolver, volumeStreamState.getSettingNameForDevice(i), (volumeStreamState.getIndex(i) + 5) / 10, -2);
            }
        }

        public final void persistRingerMode(int i) {
            if (AudioService.this.mUseFixedVolume) {
                return;
            }
            AudioService.this.mSettings.putGlobalInt(AudioService.this.mContentResolver, "mode_ringer", i);
        }

        public final void onNotifyVolumeEvent(IAudioPolicyCallback iAudioPolicyCallback, int i) {
            try {
                iAudioPolicyCallback.notifyVolumeAdjust(i);
            } catch (Exception unused) {
            }
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                AudioService.this.setDeviceVolume((VolumeStreamState) message.obj, message.arg1);
                return;
            }
            if (i == 1) {
                persistVolume((VolumeStreamState) message.obj, message.arg1);
            } else if (i == 2) {
                ((VolumeGroupState) message.obj).persistVolumeGroup(message.arg1);
            } else if (i == 3) {
                persistRingerMode(AudioService.this.getRingerModeInternal());
            } else if (i == 4) {
                AudioService.this.onAudioServerDied();
            } else if (i == 5) {
                AudioService.this.mSfxHelper.playSoundEffect(message.arg1, message.arg2);
            } else if (i == 7) {
                LoadSoundEffectReply loadSoundEffectReply = (LoadSoundEffectReply) message.obj;
                if (AudioService.this.mSystemReady) {
                    AudioService.this.mSfxHelper.loadSoundEffects(loadSoundEffectReply);
                    return;
                }
                Log.w("AS.AudioService", "[schedule]loadSoundEffects() called before boot complete");
                if (loadSoundEffectReply != null) {
                    loadSoundEffectReply.run(false);
                }
            } else if (i == 8) {
                String str = (String) message.obj;
                int i2 = message.arg1;
                int i3 = message.arg2;
                if (i2 == 1) {
                    Log.wtf("AS.AudioService", "Invalid force use FOR_MEDIA in AudioService from " + str);
                    return;
                }
                new MediaMetrics.Item("audio.forceUse." + AudioSystem.forceUseUsageToString(i2)).set(MediaMetrics.Property.EVENT, "setForceUse").set(MediaMetrics.Property.FORCE_USE_DUE_TO, str).set(MediaMetrics.Property.FORCE_USE_MODE, AudioSystem.forceUseConfigToString(i3)).record();
                AudioService.sForceUseLogger.enqueue(new AudioServiceEvents$ForceUseEvent(i2, i3, str));
                AudioService.this.mAudioSystem.setForceUse(i2, i3);
            } else if (i == 10) {
                setAllVolumes((VolumeStreamState) message.obj);
            } else if (i == 15) {
                AudioService.this.mSfxHelper.unloadSoundEffects();
            } else if (i == 16) {
                AudioService.this.onSystemReady();
            } else {
                switch (i) {
                    case 18:
                        AudioService.this.onUnmuteStream(message.arg1, message.arg2);
                        return;
                    case 19:
                        AudioService.this.onDynPolicyMixStateUpdate((String) message.obj, message.arg1);
                        return;
                    case 20:
                        AudioService.this.onIndicateSystemReady();
                        return;
                    case 21:
                        AudioService.this.onAccessoryPlugMediaUnmute(message.arg1);
                        return;
                    case 22:
                        onNotifyVolumeEvent((IAudioPolicyCallback) message.obj, message.arg1);
                        return;
                    case 23:
                        AudioService.this.onDispatchAudioServerStateChange(message.arg1 == 1);
                        return;
                    case 24:
                        AudioService.this.onEnableSurroundFormats((ArrayList) message.obj);
                        return;
                    case 25:
                        AudioService.this.onUpdateRingerModeServiceInt();
                        return;
                    case 26:
                        AudioService.this.onSetVolumeIndexOnDevice((DeviceVolumeUpdate) message.obj);
                        return;
                    case 27:
                        AudioService.this.onObserveDevicesForAllStreams(message.arg1);
                        return;
                    case 28:
                        AudioService.this.onCheckVolumeCecOnHdmiConnection(message.arg1, (String) message.obj);
                        return;
                    case 29:
                        AudioService.this.onPlaybackConfigChange((List) message.obj);
                        return;
                    case 30:
                        AudioService.this.mSystemServer.sendMicrophoneMuteChangedIntent();
                        return;
                    case 31:
                        synchronized (AudioService.this.mDeviceBroker.mSetModeLock) {
                            Object obj = message.obj;
                            if (obj == null) {
                                return;
                            }
                            SetModeDeathHandler setModeDeathHandler = (SetModeDeathHandler) obj;
                            if (AudioService.this.mSetModeDeathHandlers.indexOf(setModeDeathHandler) < 0) {
                                return;
                            }
                            boolean isActive = setModeDeathHandler.isActive();
                            setModeDeathHandler.setPlaybackActive(AudioService.this.mPlaybackMonitor.isPlaybackActiveForUid(setModeDeathHandler.getUid()));
                            setModeDeathHandler.setRecordingActive(AudioService.this.mRecordMonitor.isRecordingActiveForUid(setModeDeathHandler.getUid()));
                            if (isActive != setModeDeathHandler.isActive()) {
                                AudioService.this.onUpdateAudioMode(-1, Process.myPid(), AudioService.this.mContext.getPackageName(), false);
                            }
                            return;
                        }
                    case 32:
                        SomeArgs someArgs = (SomeArgs) message.obj;
                        someArgs.recycle();
                        AudioService.this.sendBroadcastToAll(((Intent) someArgs.arg1).putExtra("android.media.EXTRA_PREV_VOLUME_STREAM_DEVICES", message.arg1).putExtra("android.media.EXTRA_VOLUME_STREAM_DEVICES", message.arg2), (Bundle) someArgs.arg2);
                        return;
                    case 33:
                        AudioService.this.onUpdateVolumeStatesForAudioDevice(message.arg1, (String) message.obj);
                        return;
                    case 34:
                        AudioService.this.onReinitVolumes((String) message.obj);
                        return;
                    case 35:
                        AudioService.this.onUpdateAccessibilityServiceUids();
                        return;
                    case 36:
                        synchronized (AudioService.this.mDeviceBroker.mSetModeLock) {
                            AudioService.this.onUpdateAudioMode(message.arg1, message.arg2, (String) message.obj, false);
                        }
                        return;
                    case 37:
                        AudioService.this.onRecordingConfigChange((List) message.obj);
                        return;
                    case 38:
                        AudioService.this.mDeviceBroker.queueOnBluetoothActiveDeviceChanged((AudioDeviceBroker.BtDeviceChangedData) message.obj);
                        return;
                    default:
                        switch (i) {
                            case 40:
                                AudioService.this.dispatchMode(message.arg1);
                                return;
                            case 41:
                                AudioService.this.onRoutingUpdatedFromAudioThread();
                                return;
                            case 42:
                                AudioService.this.mSpatializerHelper.onInitSensors();
                                return;
                            case 43:
                                AudioService.this.onPersistSpatialAudioDeviceSettings();
                                return;
                            case 44:
                                AudioService.this.onAddAssistantServiceUids(new int[]{message.arg1});
                                return;
                            case 45:
                                AudioService.this.onRemoveAssistantServiceUids(new int[]{message.arg1});
                                return;
                            case 46:
                                AudioService.this.updateActiveAssistantServiceUids();
                                return;
                            case 47:
                                AudioService.this.dispatchDeviceVolumeBehavior((AudioDeviceAttributes) message.obj, message.arg1);
                                return;
                            case 48:
                                AudioService.this.mAudioSystem.setParameters((String) message.obj);
                                return;
                            case 49:
                                AudioService.this.mAudioSystem.setParameters((String) message.obj);
                                return;
                            case 50:
                                AudioService.this.mSpatializerHelper.reset(AudioService.this.mHasSpatializerEffect);
                                return;
                            case 51:
                                AudioService.this.mPlaybackMonitor.ignorePlayerIId(message.arg1);
                                return;
                            case 52:
                                AudioService.this.onDispatchPreferredMixerAttributesChanged(message.getData(), message.arg1);
                                return;
                            case 53:
                                AudioService.this.onLowerVolumeToRs1();
                                return;
                            default:
                                switch (i) {
                                    case 100:
                                        AudioService.this.mPlaybackMonitor.disableAudioForUid(message.arg1 == 1, message.arg2);
                                        AudioService.this.mAudioEventWakeLock.release();
                                        return;
                                    case 101:
                                        AudioService.this.onInitStreamsAndVolumes();
                                        AudioService.this.mAudioEventWakeLock.release();
                                        return;
                                    case 102:
                                        AudioService.this.onInitSpatializer();
                                        AudioService.this.mAudioEventWakeLock.release();
                                        return;
                                    default:
                                        if (i >= 1000) {
                                            AudioService.this.mSoundDoseHelper.handleMessage(message);
                                            return;
                                        }
                                        return;
                                }
                        }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class SettingsObserver extends ContentObserver {
        public SettingsObserver() {
            super(new Handler());
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("zen_mode"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("zen_mode_config_etag"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("mode_ringer_streams_affected"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("dock_audio_media_enabled"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("master_mono"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor("master_balance"), false, this);
            AudioService.this.mEncodedSurroundMode = AudioService.this.mSettings.getGlobalInt(AudioService.this.mContentResolver, "encoded_surround_output", 0);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("encoded_surround_output"), false, this);
            AudioService.this.mEnabledSurroundFormats = AudioService.this.mSettings.getGlobalString(AudioService.this.mContentResolver, "encoded_surround_output_enabled_formats");
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("encoded_surround_output_enabled_formats"), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor("voice_interaction_service"), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            synchronized (AudioService.this.mSettingsLock) {
                if (AudioService.this.updateRingerAndZenModeAffectedStreams()) {
                    AudioService audioService = AudioService.this;
                    audioService.setRingerModeInt(audioService.getRingerModeInternal(), false);
                }
                AudioService audioService2 = AudioService.this;
                audioService2.readDockAudioSettings(audioService2.mContentResolver);
                AudioService audioService3 = AudioService.this;
                audioService3.updateMasterMono(audioService3.mContentResolver);
                AudioService audioService4 = AudioService.this;
                audioService4.updateMasterBalance(audioService4.mContentResolver);
                updateEncodedSurroundOutput();
                AudioService audioService5 = AudioService.this;
                audioService5.sendEnabledSurroundFormats(audioService5.mContentResolver, AudioService.this.mSurroundModeChanged);
                AudioService.this.updateAssistantUIdLocked(false);
            }
        }

        public final void updateEncodedSurroundOutput() {
            int globalInt = AudioService.this.mSettings.getGlobalInt(AudioService.this.mContentResolver, "encoded_surround_output", 0);
            if (AudioService.this.mEncodedSurroundMode != globalInt) {
                AudioService.this.sendEncodedSurroundMode(globalInt, "SettingsObserver");
                AudioService.this.mDeviceBroker.toggleHdmiIfConnected_Async();
                AudioService.this.mEncodedSurroundMode = globalInt;
                AudioService.this.mSurroundModeChanged = true;
                return;
            }
            AudioService.this.mSurroundModeChanged = false;
        }
    }

    public final void avrcpSupportsAbsoluteVolume(String str, boolean z) {
        EventLogger eventLogger = sVolumeLogger;
        eventLogger.enqueue(new EventLogger.StringEvent("avrcpSupportsAbsoluteVolume addr=" + str + " support=" + z).printLog("AS.AudioService"));
        this.mDeviceBroker.setAvrcpAbsoluteVolumeSupported(z);
        setAvrcpAbsoluteVolumeSupported(z);
    }

    public void setAvrcpAbsoluteVolumeSupported(boolean z) {
        this.mAvrcpAbsVolSupported = z;
        sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[3], 0);
    }

    @VisibleForTesting
    public boolean hasMediaDynamicPolicy() {
        synchronized (this.mAudioPolicies) {
            if (this.mAudioPolicies.isEmpty()) {
                return false;
            }
            for (AudioPolicyProxy audioPolicyProxy : this.mAudioPolicies.values()) {
                if (audioPolicyProxy.hasMixAffectingUsage(1, 3)) {
                    return true;
                }
            }
            return false;
        }
    }

    @VisibleForTesting
    public void checkMusicActive(int i, String str) {
        if (this.mSoundDoseHelper.safeDevicesContains(i)) {
            this.mSoundDoseHelper.scheduleMusicActiveCheck();
        }
    }

    /* loaded from: classes.dex */
    public class AudioServiceBroadcastReceiver extends BroadcastReceiver {
        public AudioServiceBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int i = 0;
            if (action.equals("android.intent.action.DOCK_EVENT")) {
                int intExtra = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
                if (intExtra == 1) {
                    i = 7;
                } else if (intExtra == 2) {
                    i = 6;
                } else if (intExtra == 3) {
                    i = 8;
                } else if (intExtra == 4) {
                    i = 9;
                }
                if (intExtra != 3 && (intExtra != 0 || AudioService.this.mDockState != 3)) {
                    AudioService.this.mDeviceBroker.setForceUse_Async(3, i, "ACTION_DOCK_EVENT intent");
                }
                AudioService.this.mDockState = intExtra;
            } else if (action.equals("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED") || action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
                AudioService.this.mDeviceBroker.receiveBtEvent(intent);
            } else if (action.equals("android.intent.action.SCREEN_ON")) {
                if (AudioService.this.mMonitorRotation) {
                    RotationHelper.enable();
                }
                AudioSystem.setParameters("screen_state=on");
            } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                if (AudioService.this.mMonitorRotation) {
                    RotationHelper.disable();
                }
                AudioSystem.setParameters("screen_state=off");
            } else if (action.equals("android.intent.action.CONFIGURATION_CHANGED")) {
                AudioService.this.handleConfigurationChanged(context);
            } else if (action.equals("android.intent.action.USER_SWITCHED")) {
                if (AudioService.this.mUserSwitchedReceived) {
                    AudioService.this.mDeviceBroker.postBroadcastBecomingNoisy();
                }
                AudioService.this.mUserSwitchedReceived = true;
                AudioService.this.mMediaFocusControl.discardAudioFocusOwner();
                if (AudioService.this.mSupportsMicPrivacyToggle) {
                    AudioService audioService = AudioService.this;
                    audioService.mMicMuteFromPrivacyToggle = audioService.mSensorPrivacyManagerInternal.isSensorPrivacyEnabled(AudioService.this.getCurrentUserId(), 1);
                    AudioService audioService2 = AudioService.this;
                    audioService2.setMicrophoneMuteNoCallerCheck(audioService2.getCurrentUserId());
                }
                AudioService.this.readAudioSettings(true);
                AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, AudioService.this.mStreamStates[3], 0);
            } else if (action.equals("android.intent.action.USER_BACKGROUND")) {
                int intExtra2 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                if (intExtra2 >= 0) {
                    AudioService.this.killBackgroundUserProcessesWithRecordAudioPermission(UserManagerService.getInstance().getUserInfo(intExtra2));
                }
                try {
                    UserManagerService.getInstance().setUserRestriction("no_record_audio", true, intExtra2);
                } catch (IllegalArgumentException e) {
                    Slog.w("AS.AudioService", "Failed to apply DISALLOW_RECORD_AUDIO restriction: " + e);
                }
            } else if (action.equals("android.intent.action.USER_FOREGROUND")) {
                try {
                    UserManagerService.getInstance().setUserRestriction("no_record_audio", false, intent.getIntExtra("android.intent.extra.user_handle", -1));
                } catch (IllegalArgumentException e2) {
                    Slog.w("AS.AudioService", "Failed to apply DISALLOW_RECORD_AUDIO restriction: " + e2);
                }
            } else if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                int intExtra3 = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1);
                if (intExtra3 == 10 || intExtra3 == 13) {
                    AudioService.this.mDeviceBroker.disconnectAllBluetoothProfiles();
                }
            } else if (action.equals("android.media.action.OPEN_AUDIO_EFFECT_CONTROL_SESSION") || action.equals("android.media.action.CLOSE_AUDIO_EFFECT_CONTROL_SESSION")) {
                AudioService.this.handleAudioEffectBroadcast(context, intent);
            } else if (action.equals("android.intent.action.PACKAGES_SUSPENDED")) {
                int[] intArrayExtra = intent.getIntArrayExtra("android.intent.extra.changed_uid_list");
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                if (stringArrayExtra == null || intArrayExtra == null || stringArrayExtra.length != intArrayExtra.length) {
                    return;
                }
                while (i < intArrayExtra.length) {
                    if (!TextUtils.isEmpty(stringArrayExtra[i])) {
                        AudioService.this.mMediaFocusControl.noFocusForSuspendedApp(stringArrayExtra[i], intArrayExtra[i]);
                    }
                    i++;
                }
            } else if (action.equals("com.android.server.audio.action.CHECK_MUSIC_ACTIVE")) {
                AudioService.this.mSoundDoseHelper.onCheckMusicActive("com.android.server.audio.action.CHECK_MUSIC_ACTIVE", AudioService.this.mAudioSystem.isStreamActive(3, 0));
            }
        }
    }

    /* loaded from: classes.dex */
    public class AudioServiceUserRestrictionsListener implements UserManagerInternal.UserRestrictionsListener {
        public AudioServiceUserRestrictionsListener() {
        }

        @Override // com.android.server.p011pm.UserManagerInternal.UserRestrictionsListener
        public void onUserRestrictionsChanged(int i, Bundle bundle, Bundle bundle2) {
            boolean z = bundle2.getBoolean("no_unmute_microphone");
            boolean z2 = bundle.getBoolean("no_unmute_microphone");
            if (z != z2) {
                AudioService.this.mMicMuteFromRestrictions = z2;
                AudioService.this.setMicrophoneMuteNoCallerCheck(i);
            }
            boolean z3 = true;
            boolean z4 = bundle2.getBoolean("no_adjust_volume") || bundle2.getBoolean("disallow_unmute_device");
            if (!bundle.getBoolean("no_adjust_volume") && !bundle.getBoolean("disallow_unmute_device")) {
                z3 = false;
            }
            if (z4 != z3) {
                AudioService.this.setMasterMuteInternalNoCallerCheck(z3, 0, i);
            }
        }
    }

    public final void handleAudioEffectBroadcast(Context context, Intent intent) {
        ResolveInfo resolveInfo;
        ActivityInfo activityInfo;
        String str;
        String str2 = intent.getPackage();
        if (str2 != null) {
            Log.w("AS.AudioService", "effect broadcast already targeted to " + str2);
            return;
        }
        intent.addFlags(32);
        List<ResolveInfo> queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        if (queryBroadcastReceivers != null && queryBroadcastReceivers.size() != 0 && (resolveInfo = queryBroadcastReceivers.get(0)) != null && (activityInfo = resolveInfo.activityInfo) != null && (str = activityInfo.packageName) != null) {
            intent.setPackage(str);
            context.sendBroadcastAsUser(intent, UserHandle.ALL);
            return;
        }
        Log.w("AS.AudioService", "couldn't find receiver package for effect intent");
    }

    public final void killBackgroundUserProcessesWithRecordAudioPermission(UserInfo userInfo) {
        PackageManager packageManager = this.mContext.getPackageManager();
        ComponentName homeActivityForUser = !userInfo.isManagedProfile() ? ((ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class)).getHomeActivityForUser(userInfo.id) : null;
        try {
            List list = AppGlobals.getPackageManager().getPackagesHoldingPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 0L, userInfo.id).getList();
            for (int size = list.size() - 1; size >= 0; size--) {
                PackageInfo packageInfo = (PackageInfo) list.get(size);
                if (UserHandle.getAppId(packageInfo.applicationInfo.uid) >= 10000 && packageManager.checkPermission("android.permission.INTERACT_ACROSS_USERS", packageInfo.packageName) != 0 && (homeActivityForUser == null || !packageInfo.packageName.equals(homeActivityForUser.getPackageName()) || !packageInfo.applicationInfo.isSystemApp())) {
                    try {
                        int i = packageInfo.applicationInfo.uid;
                        ActivityManager.getService().killUid(UserHandle.getAppId(i), UserHandle.getUserId(i), "killBackgroundUserProcessesWithAudioRecordPermission");
                    } catch (RemoteException e) {
                        Log.w("AS.AudioService", "Error calling killUid", e);
                    }
                }
            }
        } catch (RemoteException e2) {
            throw new AndroidRuntimeException(e2);
        }
    }

    public final boolean forceFocusDuckingForAccessibility(AudioAttributes audioAttributes, int i, int i2) {
        Bundle bundle;
        if (audioAttributes != null && audioAttributes.getUsage() == 11 && i == 3 && (bundle = audioAttributes.getBundle()) != null && bundle.getBoolean("a11y_force_ducking")) {
            if (i2 == 0) {
                return true;
            }
            synchronized (this.mAccessibilityServiceUidsLock) {
                if (this.mAccessibilityServiceUids != null) {
                    int callingUid = Binder.getCallingUid();
                    int i3 = 0;
                    while (true) {
                        int[] iArr = this.mAccessibilityServiceUids;
                        if (i3 >= iArr.length) {
                            break;
                        } else if (iArr[i3] == callingUid) {
                            return true;
                        } else {
                            i3++;
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    public final boolean isSupportedSystemUsage(int i) {
        synchronized (this.mSupportedSystemUsagesLock) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.mSupportedSystemUsages;
                if (i2 >= iArr.length) {
                    return false;
                }
                if (iArr[i2] == i) {
                    return true;
                }
                i2++;
            }
        }
    }

    public final void validateAudioAttributesUsage(AudioAttributes audioAttributes) {
        int systemUsage = audioAttributes.getSystemUsage();
        if (AudioAttributes.isSystemUsage(systemUsage)) {
            if ((systemUsage == 17 && (audioAttributes.getAllFlags() & 65536) != 0 && callerHasPermission("android.permission.CALL_AUDIO_INTERCEPTION")) || callerHasPermission("android.permission.MODIFY_AUDIO_ROUTING")) {
                if (isSupportedSystemUsage(systemUsage)) {
                    return;
                }
                throw new IllegalArgumentException("Unsupported usage " + AudioAttributes.usageToString(systemUsage));
            }
            throw new SecurityException("Missing MODIFY_AUDIO_ROUTING permission");
        }
    }

    public final boolean isValidAudioAttributesUsage(AudioAttributes audioAttributes) {
        int systemUsage = audioAttributes.getSystemUsage();
        if (AudioAttributes.isSystemUsage(systemUsage)) {
            return isSupportedSystemUsage(systemUsage) && ((systemUsage == 17 && (audioAttributes.getAllFlags() & 65536) != 0 && callerHasPermission("android.permission.CALL_AUDIO_INTERCEPTION")) || callerHasPermission("android.permission.MODIFY_AUDIO_ROUTING"));
        }
        return true;
    }

    public int requestAudioFocus(AudioAttributes audioAttributes, int i, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2, String str3, int i2, IAudioPolicyCallback iAudioPolicyCallback, int i3) {
        if ((i2 & 8) != 0) {
            throw new IllegalArgumentException("Invalid test flag");
        }
        int callingUid = Binder.getCallingUid();
        MediaMetrics.Item item = new MediaMetrics.Item("audio.service.focus").setUid(callingUid).set(MediaMetrics.Property.CALLING_PACKAGE, str2).set(MediaMetrics.Property.CLIENT_NAME, str).set(MediaMetrics.Property.EVENT, "requestAudioFocus").set(MediaMetrics.Property.FLAGS, Integer.valueOf(i2));
        if (audioAttributes != null && !isValidAudioAttributesUsage(audioAttributes)) {
            Log.w("AS.AudioService", "Request using unsupported usage");
            item.set(MediaMetrics.Property.EARLY_RETURN, "Request using unsupported usage").record();
            return 0;
        }
        if ((i2 & 4) == 4) {
            if ("AudioFocus_For_Phone_Ring_And_Calls".equals(str)) {
                if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0) {
                    Log.e("AS.AudioService", "Invalid permission to (un)lock audio focus", new Exception());
                    item.set(MediaMetrics.Property.EARLY_RETURN, "Invalid permission to (un)lock audio focus").record();
                    return 0;
                }
            } else {
                synchronized (this.mAudioPolicies) {
                    if (!this.mAudioPolicies.containsKey(iAudioPolicyCallback.asBinder())) {
                        Log.e("AS.AudioService", "Invalid unregistered AudioPolicy to (un)lock audio focus");
                        item.set(MediaMetrics.Property.EARLY_RETURN, "Invalid unregistered AudioPolicy to (un)lock audio focus").record();
                        return 0;
                    }
                }
            }
        }
        if (str2 == null || str == null || audioAttributes == null) {
            Log.e("AS.AudioService", "Invalid null parameter to request audio focus");
            item.set(MediaMetrics.Property.EARLY_RETURN, "Invalid null parameter to request audio focus").record();
            return 0;
        }
        item.record();
        return this.mMediaFocusControl.requestAudioFocus(audioAttributes, i, iBinder, iAudioFocusDispatcher, str, str2, str3, i2, i3, forceFocusDuckingForAccessibility(audioAttributes, i, callingUid), -1);
    }

    public int requestAudioFocusForTest(AudioAttributes audioAttributes, int i, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2, int i2, int i3, int i4) {
        if (enforceQueryAudioStateForTest("focus request")) {
            if (str2 == null || str == null || audioAttributes == null) {
                Log.e("AS.AudioService", "Invalid null parameter to request audio focus");
                return 0;
            }
            return this.mMediaFocusControl.requestAudioFocus(audioAttributes, i, iBinder, iAudioFocusDispatcher, str, str2, null, i2, i4, false, i3);
        }
        return 0;
    }

    public int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str, AudioAttributes audioAttributes, String str2) {
        MediaMetrics.Item item = new MediaMetrics.Item("audio.service.focus").set(MediaMetrics.Property.CALLING_PACKAGE, str2).set(MediaMetrics.Property.CLIENT_NAME, str).set(MediaMetrics.Property.EVENT, "abandonAudioFocus");
        if (audioAttributes != null && !isValidAudioAttributesUsage(audioAttributes)) {
            Log.w("AS.AudioService", "Request using unsupported usage.");
            item.set(MediaMetrics.Property.EARLY_RETURN, "unsupported usage").record();
            return 0;
        }
        item.record();
        return this.mMediaFocusControl.abandonAudioFocus(iAudioFocusDispatcher, str, audioAttributes, str2);
    }

    public int abandonAudioFocusForTest(IAudioFocusDispatcher iAudioFocusDispatcher, String str, AudioAttributes audioAttributes, String str2) {
        if (enforceQueryAudioStateForTest("focus abandon")) {
            return this.mMediaFocusControl.abandonAudioFocus(iAudioFocusDispatcher, str, audioAttributes, str2);
        }
        return 0;
    }

    public void unregisterAudioFocusClient(String str) {
        new MediaMetrics.Item("audio.service.focus").set(MediaMetrics.Property.CLIENT_NAME, str).set(MediaMetrics.Property.EVENT, "unregisterAudioFocusClient").record();
        this.mMediaFocusControl.unregisterAudioFocusClient(str);
    }

    public int getCurrentAudioFocus() {
        return this.mMediaFocusControl.getCurrentAudioFocus();
    }

    public int getFocusRampTimeMs(int i, AudioAttributes audioAttributes) {
        return MediaFocusControl.getFocusRampTimeMs(i, audioAttributes);
    }

    @VisibleForTesting
    public boolean hasAudioFocusUsers() {
        return this.mMediaFocusControl.hasAudioFocusUsers();
    }

    public long getFadeOutDurationOnFocusLossMillis(AudioAttributes audioAttributes) {
        if (enforceQueryAudioStateForTest("fade out duration")) {
            return this.mMediaFocusControl.getFadeOutDurationOnFocusLossMillis(audioAttributes);
        }
        return 0L;
    }

    public final boolean enforceQueryAudioStateForTest(String str) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.QUERY_AUDIO_STATE") != 0) {
            Log.e("AS.AudioService", "Doesn't have QUERY_AUDIO_STATE permission for " + str + " test API", new Exception());
            return false;
        }
        return true;
    }

    public int getSpatializerImmersiveAudioLevel() {
        return this.mSpatializerHelper.getCapableImmersiveAudioLevel();
    }

    public boolean isSpatializerEnabled() {
        return this.mSpatializerHelper.isEnabled();
    }

    public boolean isSpatializerAvailable() {
        return this.mSpatializerHelper.isAvailable();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public boolean isSpatializerAvailableForDevice(AudioDeviceAttributes audioDeviceAttributes) {
        super.isSpatializerAvailableForDevice_enforcePermission();
        SpatializerHelper spatializerHelper = this.mSpatializerHelper;
        Objects.requireNonNull(audioDeviceAttributes);
        return spatializerHelper.isAvailableForDevice(audioDeviceAttributes);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public boolean hasHeadTracker(AudioDeviceAttributes audioDeviceAttributes) {
        super.hasHeadTracker_enforcePermission();
        SpatializerHelper spatializerHelper = this.mSpatializerHelper;
        Objects.requireNonNull(audioDeviceAttributes);
        return spatializerHelper.hasHeadTracker(audioDeviceAttributes);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void setHeadTrackerEnabled(boolean z, AudioDeviceAttributes audioDeviceAttributes) {
        super.setHeadTrackerEnabled_enforcePermission();
        SpatializerHelper spatializerHelper = this.mSpatializerHelper;
        Objects.requireNonNull(audioDeviceAttributes);
        spatializerHelper.setHeadTrackerEnabled(z, audioDeviceAttributes);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public boolean isHeadTrackerEnabled(AudioDeviceAttributes audioDeviceAttributes) {
        super.isHeadTrackerEnabled_enforcePermission();
        SpatializerHelper spatializerHelper = this.mSpatializerHelper;
        Objects.requireNonNull(audioDeviceAttributes);
        return spatializerHelper.isHeadTrackerEnabled(audioDeviceAttributes);
    }

    public boolean isHeadTrackerAvailable() {
        return this.mSpatializerHelper.isHeadTrackerAvailable();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void setSpatializerEnabled(boolean z) {
        super.setSpatializerEnabled_enforcePermission();
        this.mSpatializerHelper.setFeatureEnabled(z);
    }

    public boolean canBeSpatialized(AudioAttributes audioAttributes, AudioFormat audioFormat) {
        Objects.requireNonNull(audioAttributes);
        Objects.requireNonNull(audioFormat);
        return this.mSpatializerHelper.canBeSpatialized(audioAttributes, audioFormat);
    }

    public void registerSpatializerCallback(ISpatializerCallback iSpatializerCallback) {
        Objects.requireNonNull(iSpatializerCallback);
        this.mSpatializerHelper.registerStateCallback(iSpatializerCallback);
    }

    public void unregisterSpatializerCallback(ISpatializerCallback iSpatializerCallback) {
        Objects.requireNonNull(iSpatializerCallback);
        this.mSpatializerHelper.unregisterStateCallback(iSpatializerCallback);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void registerSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback iSpatializerHeadTrackingModeCallback) {
        super.registerSpatializerHeadTrackingCallback_enforcePermission();
        Objects.requireNonNull(iSpatializerHeadTrackingModeCallback);
        this.mSpatializerHelper.registerHeadTrackingModeCallback(iSpatializerHeadTrackingModeCallback);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void unregisterSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback iSpatializerHeadTrackingModeCallback) {
        super.unregisterSpatializerHeadTrackingCallback_enforcePermission();
        Objects.requireNonNull(iSpatializerHeadTrackingModeCallback);
        this.mSpatializerHelper.unregisterHeadTrackingModeCallback(iSpatializerHeadTrackingModeCallback);
    }

    public void registerSpatializerHeadTrackerAvailableCallback(ISpatializerHeadTrackerAvailableCallback iSpatializerHeadTrackerAvailableCallback, boolean z) {
        Objects.requireNonNull(iSpatializerHeadTrackerAvailableCallback);
        this.mSpatializerHelper.registerHeadTrackerAvailableCallback(iSpatializerHeadTrackerAvailableCallback, z);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void registerHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback iSpatializerHeadToSoundStagePoseCallback) {
        super.registerHeadToSoundstagePoseCallback_enforcePermission();
        Objects.requireNonNull(iSpatializerHeadToSoundStagePoseCallback);
        this.mSpatializerHelper.registerHeadToSoundstagePoseCallback(iSpatializerHeadToSoundStagePoseCallback);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void unregisterHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback iSpatializerHeadToSoundStagePoseCallback) {
        super.unregisterHeadToSoundstagePoseCallback_enforcePermission();
        Objects.requireNonNull(iSpatializerHeadToSoundStagePoseCallback);
        this.mSpatializerHelper.unregisterHeadToSoundstagePoseCallback(iSpatializerHeadToSoundStagePoseCallback);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public List<AudioDeviceAttributes> getSpatializerCompatibleAudioDevices() {
        super.getSpatializerCompatibleAudioDevices_enforcePermission();
        return this.mSpatializerHelper.getCompatibleAudioDevices();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void addSpatializerCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes) {
        super.addSpatializerCompatibleAudioDevice_enforcePermission();
        Objects.requireNonNull(audioDeviceAttributes);
        this.mSpatializerHelper.addCompatibleAudioDevice(audioDeviceAttributes);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void removeSpatializerCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes) {
        super.removeSpatializerCompatibleAudioDevice_enforcePermission();
        Objects.requireNonNull(audioDeviceAttributes);
        this.mSpatializerHelper.removeCompatibleAudioDevice(audioDeviceAttributes);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public int[] getSupportedHeadTrackingModes() {
        super.getSupportedHeadTrackingModes_enforcePermission();
        return this.mSpatializerHelper.getSupportedHeadTrackingModes();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public int getActualHeadTrackingMode() {
        super.getActualHeadTrackingMode_enforcePermission();
        return this.mSpatializerHelper.getActualHeadTrackingMode();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public int getDesiredHeadTrackingMode() {
        super.getDesiredHeadTrackingMode_enforcePermission();
        return this.mSpatializerHelper.getDesiredHeadTrackingMode();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void setSpatializerGlobalTransform(float[] fArr) {
        super.setSpatializerGlobalTransform_enforcePermission();
        Objects.requireNonNull(fArr);
        this.mSpatializerHelper.setGlobalTransform(fArr);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void recenterHeadTracker() {
        super.recenterHeadTracker_enforcePermission();
        this.mSpatializerHelper.recenterHeadTracker();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void setDesiredHeadTrackingMode(@Spatializer.HeadTrackingModeSet int i) {
        super.setDesiredHeadTrackingMode_enforcePermission();
        if (i == -1 || i == 1 || i == 2) {
            this.mSpatializerHelper.setDesiredHeadTrackingMode(i);
        }
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void setSpatializerParameter(int i, byte[] bArr) {
        super.setSpatializerParameter_enforcePermission();
        Objects.requireNonNull(bArr);
        this.mSpatializerHelper.setEffectParameter(i, bArr);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void getSpatializerParameter(int i, byte[] bArr) {
        super.getSpatializerParameter_enforcePermission();
        Objects.requireNonNull(bArr);
        this.mSpatializerHelper.getEffectParameter(i, bArr);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public int getSpatializerOutput() {
        super.getSpatializerOutput_enforcePermission();
        return this.mSpatializerHelper.getOutput();
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void registerSpatializerOutputCallback(ISpatializerOutputCallback iSpatializerOutputCallback) {
        super.registerSpatializerOutputCallback_enforcePermission();
        Objects.requireNonNull(iSpatializerOutputCallback);
        this.mSpatializerHelper.registerSpatializerOutputCallback(iSpatializerOutputCallback);
    }

    @EnforcePermission("android.permission.MODIFY_DEFAULT_AUDIO_EFFECTS")
    public void unregisterSpatializerOutputCallback(ISpatializerOutputCallback iSpatializerOutputCallback) {
        super.unregisterSpatializerOutputCallback_enforcePermission();
        Objects.requireNonNull(iSpatializerOutputCallback);
        this.mSpatializerHelper.unregisterSpatializerOutputCallback(iSpatializerOutputCallback);
    }

    public void postInitSpatializerHeadTrackingSensors() {
        sendMsg(this.mAudioHandler, 42, 0, 0, 0, "AS.AudioService", 0);
    }

    public void postResetSpatializer() {
        sendMsg(this.mAudioHandler, 50, 0, 0, 0, "AS.AudioService", 0);
    }

    public void onInitSpatializer() {
        String secureStringForUser = this.mSettings.getSecureStringForUser(this.mContentResolver, "spatial_audio_enabled", -2);
        if (secureStringForUser == null) {
            Log.e("AS.AudioService", "error reading spatial audio device settings");
        }
        this.mSpatializerHelper.init(this.mHasSpatializerEffect, secureStringForUser);
        this.mSpatializerHelper.setFeatureEnabled(this.mHasSpatializerEffect);
    }

    public void persistSpatialAudioDeviceSettings() {
        sendMsg(this.mAudioHandler, 43, 0, 0, 0, "AS.AudioService", 1000);
    }

    public void onPersistSpatialAudioDeviceSettings() {
        String sADeviceSettings = this.mSpatializerHelper.getSADeviceSettings();
        Log.v("AS.AudioService", "saving spatial audio device settings: " + sADeviceSettings);
        if (this.mSettings.putSecureStringForUser(this.mContentResolver, "spatial_audio_enabled", sADeviceSettings, -2)) {
            return;
        }
        Log.e("AS.AudioService", "error saving spatial audio device settings: " + sADeviceSettings);
    }

    public final boolean readCameraSoundForced() {
        return SystemProperties.getBoolean("audio.camerasound.force", false) || this.mContext.getResources().getBoolean(17891402);
    }

    @SuppressLint({"EmptyCatch"})
    public void muteAwaitConnection(final int[] iArr, final AudioDeviceAttributes audioDeviceAttributes, long j) {
        Objects.requireNonNull(iArr);
        Objects.requireNonNull(audioDeviceAttributes);
        enforceModifyAudioRoutingPermission();
        if (j <= 0 || iArr.length == 0) {
            throw new IllegalArgumentException("Invalid timeOutMs/usagesToMute");
        }
        Log.i("AS.AudioService", "muteAwaitConnection dev:" + audioDeviceAttributes + " timeOutMs:" + j + " usages:" + Arrays.toString(iArr));
        if (this.mDeviceBroker.isDeviceConnected(audioDeviceAttributes)) {
            Log.i("AS.AudioService", "muteAwaitConnection ignored, device (" + audioDeviceAttributes + ") already connected");
            return;
        }
        synchronized (this.mMuteAwaitConnectionLock) {
            if (this.mMutingExpectedDevice != null) {
                Log.e("AS.AudioService", "muteAwaitConnection ignored, another in progress for device:" + this.mMutingExpectedDevice);
                throw new IllegalStateException("muteAwaitConnection already in progress");
            }
            this.mMutingExpectedDevice = audioDeviceAttributes;
            this.mMutedUsagesAwaitingConnection = iArr;
            this.mPlaybackMonitor.muteAwaitConnection(iArr, audioDeviceAttributes, j);
        }
        dispatchMuteAwaitConnection(new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AudioService.lambda$muteAwaitConnection$12(audioDeviceAttributes, iArr, (IMuteAwaitConnectionCallback) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$muteAwaitConnection$12(AudioDeviceAttributes audioDeviceAttributes, int[] iArr, IMuteAwaitConnectionCallback iMuteAwaitConnectionCallback) {
        try {
            iMuteAwaitConnectionCallback.dispatchOnMutedUntilConnection(audioDeviceAttributes, iArr);
        } catch (RemoteException unused) {
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public AudioDeviceAttributes getMutingExpectedDevice() {
        AudioDeviceAttributes audioDeviceAttributes;
        super.getMutingExpectedDevice_enforcePermission();
        synchronized (this.mMuteAwaitConnectionLock) {
            audioDeviceAttributes = this.mMutingExpectedDevice;
        }
        return audioDeviceAttributes;
    }

    @SuppressLint({"EmptyCatch"})
    public void cancelMuteAwaitConnection(final AudioDeviceAttributes audioDeviceAttributes) {
        Objects.requireNonNull(audioDeviceAttributes);
        enforceModifyAudioRoutingPermission();
        Log.i("AS.AudioService", "cancelMuteAwaitConnection for device:" + audioDeviceAttributes);
        synchronized (this.mMuteAwaitConnectionLock) {
            AudioDeviceAttributes audioDeviceAttributes2 = this.mMutingExpectedDevice;
            if (audioDeviceAttributes2 == null) {
                Log.i("AS.AudioService", "cancelMuteAwaitConnection ignored, no expected device");
            } else if (!audioDeviceAttributes.equalTypeAddress(audioDeviceAttributes2)) {
                Log.e("AS.AudioService", "cancelMuteAwaitConnection ignored, got " + audioDeviceAttributes + "] but expected device is" + this.mMutingExpectedDevice);
                throw new IllegalStateException("cancelMuteAwaitConnection for wrong device");
            } else {
                final int[] iArr = this.mMutedUsagesAwaitingConnection;
                this.mMutingExpectedDevice = null;
                this.mMutedUsagesAwaitingConnection = null;
                PlaybackActivityMonitor playbackActivityMonitor = this.mPlaybackMonitor;
                playbackActivityMonitor.cancelMuteAwaitConnection("cancelMuteAwaitConnection dev:" + audioDeviceAttributes);
                dispatchMuteAwaitConnection(new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda12
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AudioService.lambda$cancelMuteAwaitConnection$13(audioDeviceAttributes, iArr, (IMuteAwaitConnectionCallback) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$cancelMuteAwaitConnection$13(AudioDeviceAttributes audioDeviceAttributes, int[] iArr, IMuteAwaitConnectionCallback iMuteAwaitConnectionCallback) {
        try {
            iMuteAwaitConnectionCallback.dispatchOnUnmutedEvent(3, audioDeviceAttributes, iArr);
        } catch (RemoteException unused) {
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void registerMuteAwaitConnectionDispatcher(IMuteAwaitConnectionCallback iMuteAwaitConnectionCallback, boolean z) {
        super.registerMuteAwaitConnectionDispatcher_enforcePermission();
        if (z) {
            this.mMuteAwaitConnectionDispatchers.register(iMuteAwaitConnectionCallback);
        } else {
            this.mMuteAwaitConnectionDispatchers.unregister(iMuteAwaitConnectionCallback);
        }
    }

    @SuppressLint({"EmptyCatch"})
    public void checkMuteAwaitConnection() {
        synchronized (this.mMuteAwaitConnectionLock) {
            final AudioDeviceAttributes audioDeviceAttributes = this.mMutingExpectedDevice;
            if (audioDeviceAttributes == null) {
                return;
            }
            final int[] iArr = this.mMutedUsagesAwaitingConnection;
            if (this.mDeviceBroker.isDeviceConnected(audioDeviceAttributes)) {
                this.mMutingExpectedDevice = null;
                this.mMutedUsagesAwaitingConnection = null;
                PlaybackActivityMonitor playbackActivityMonitor = this.mPlaybackMonitor;
                playbackActivityMonitor.cancelMuteAwaitConnection("checkMuteAwaitConnection device " + audioDeviceAttributes + " connected, unmuting");
                dispatchMuteAwaitConnection(new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda18
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AudioService.lambda$checkMuteAwaitConnection$14(audioDeviceAttributes, iArr, (IMuteAwaitConnectionCallback) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$checkMuteAwaitConnection$14(AudioDeviceAttributes audioDeviceAttributes, int[] iArr, IMuteAwaitConnectionCallback iMuteAwaitConnectionCallback) {
        try {
            iMuteAwaitConnectionCallback.dispatchOnUnmutedEvent(1, audioDeviceAttributes, iArr);
        } catch (RemoteException unused) {
        }
    }

    @SuppressLint({"EmptyCatch"})
    /* renamed from: onMuteAwaitConnectionTimeout */
    public void lambda$new$1(final AudioDeviceAttributes audioDeviceAttributes) {
        synchronized (this.mMuteAwaitConnectionLock) {
            if (audioDeviceAttributes.equals(this.mMutingExpectedDevice)) {
                Log.i("AS.AudioService", "muteAwaitConnection timeout, clearing expected device " + this.mMutingExpectedDevice);
                final int[] iArr = this.mMutedUsagesAwaitingConnection;
                this.mMutingExpectedDevice = null;
                this.mMutedUsagesAwaitingConnection = null;
                dispatchMuteAwaitConnection(new Consumer() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda15
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AudioService.lambda$onMuteAwaitConnectionTimeout$15(audioDeviceAttributes, iArr, (IMuteAwaitConnectionCallback) obj);
                    }
                });
            }
        }
    }

    public static /* synthetic */ void lambda$onMuteAwaitConnectionTimeout$15(AudioDeviceAttributes audioDeviceAttributes, int[] iArr, IMuteAwaitConnectionCallback iMuteAwaitConnectionCallback) {
        try {
            iMuteAwaitConnectionCallback.dispatchOnUnmutedEvent(2, audioDeviceAttributes, iArr);
        } catch (RemoteException unused) {
        }
    }

    public final void dispatchMuteAwaitConnection(Consumer<IMuteAwaitConnectionCallback> consumer) {
        int beginBroadcast = this.mMuteAwaitConnectionDispatchers.beginBroadcast();
        ArrayList arrayList = null;
        for (int i = 0; i < beginBroadcast; i++) {
            try {
                consumer.accept(this.mMuteAwaitConnectionDispatchers.getBroadcastItem(i));
            } catch (Exception unused) {
                if (arrayList == null) {
                    arrayList = new ArrayList(1);
                }
                arrayList.add(this.mMuteAwaitConnectionDispatchers.getBroadcastItem(i));
            }
        }
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                this.mMuteAwaitConnectionDispatchers.unregister((IMuteAwaitConnectionCallback) it.next());
            }
        }
        this.mMuteAwaitConnectionDispatchers.finishBroadcast();
    }

    public void registerDeviceVolumeBehaviorDispatcher(boolean z, IDeviceVolumeBehaviorDispatcher iDeviceVolumeBehaviorDispatcher) {
        enforceQueryStateOrModifyRoutingPermission();
        Objects.requireNonNull(iDeviceVolumeBehaviorDispatcher);
        if (z) {
            this.mDeviceVolumeBehaviorDispatchers.register(iDeviceVolumeBehaviorDispatcher);
        } else {
            this.mDeviceVolumeBehaviorDispatchers.unregister(iDeviceVolumeBehaviorDispatcher);
        }
    }

    public final void dispatchDeviceVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes, int i) {
        int beginBroadcast = this.mDeviceVolumeBehaviorDispatchers.beginBroadcast();
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mDeviceVolumeBehaviorDispatchers.getBroadcastItem(i2).dispatchDeviceVolumeBehaviorChanged(audioDeviceAttributes, i);
            } catch (RemoteException unused) {
            }
        }
        this.mDeviceVolumeBehaviorDispatchers.finishBroadcast();
    }

    public final void handleConfigurationChanged(Context context) {
        try {
            Configuration configuration = context.getResources().getConfiguration();
            this.mSoundDoseHelper.configureSafeMedia(false, "AS.AudioService");
            boolean readCameraSoundForced = readCameraSoundForced();
            synchronized (this.mSettingsLock) {
                boolean z = readCameraSoundForced != this.mCameraSoundForced;
                this.mCameraSoundForced = readCameraSoundForced;
                if (z) {
                    if (!this.mIsSingleVolume) {
                        synchronized (VolumeStreamState.class) {
                            VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                            VolumeStreamState volumeStreamState = volumeStreamStateArr[7];
                            if (readCameraSoundForced) {
                                volumeStreamState.setAllIndexesToMax();
                                this.mRingerModeAffectedStreams &= -129;
                            } else {
                                volumeStreamState.setAllIndexes(volumeStreamStateArr[1], "AS.AudioService");
                                this.mRingerModeAffectedStreams |= 128;
                            }
                        }
                        setRingerModeInt(getRingerModeInternal(), false);
                    }
                    this.mDeviceBroker.setForceUse_Async(4, readCameraSoundForced ? 11 : 0, "handleConfigurationChanged");
                    sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[7], 0);
                }
            }
            this.mVolumeController.setLayoutDirection(configuration.getLayoutDirection());
        } catch (Exception e) {
            Log.e("AS.AudioService", "Error handling configuration change: ", e);
        }
    }

    public void setRingtonePlayer(IRingtonePlayer iRingtonePlayer) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.REMOTE_AUDIO_PLAYBACK", null);
        this.mRingtonePlayer = iRingtonePlayer;
    }

    public IRingtonePlayer getRingtonePlayer() {
        return this.mRingtonePlayer;
    }

    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) {
        return this.mDeviceBroker.startWatchingRoutes(iAudioRoutesObserver);
    }

    public void disableSafeMediaVolume(String str) {
        enforceVolumeController("disable the safe media volume");
        this.mSoundDoseHelper.disableSafeMediaVolume(str);
    }

    public void lowerVolumeToRs1(String str) {
        enforceVolumeController("lowerVolumeToRs1");
        postLowerVolumeToRs1();
    }

    public void postLowerVolumeToRs1() {
        sendMsg(this.mAudioHandler, 53, 2, 0, 0, null, 0);
    }

    public final void onLowerVolumeToRs1() {
        AudioDeviceAttributes audioDeviceAttributes;
        int i;
        ArrayList<AudioDeviceAttributes> devicesForAttributesInt = getDevicesForAttributesInt(new AudioAttributes.Builder().setUsage(1).build(), true);
        if (devicesForAttributesInt.isEmpty()) {
            AudioDeviceAttributes audioDeviceAttributes2 = devicesForAttributesInt.get(0);
            i = audioDeviceAttributes2.getInternalType();
            audioDeviceAttributes = audioDeviceAttributes2;
        } else {
            audioDeviceAttributes = new AudioDeviceAttributes(67108864, "");
            i = 67108864;
        }
        setStreamVolumeWithAttributionInt(3, this.mSoundDoseHelper.safeMediaVolumeIndex(i), 0, audioDeviceAttributes, "com.android.server.audio", "AudioService");
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public float getRs2Value() {
        super.getRs2Value_enforcePermission();
        return this.mSoundDoseHelper.getRs2Value();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public void setRs2Value(float f) {
        super.setRs2Value_enforcePermission();
        this.mSoundDoseHelper.setRs2Value(f);
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public float getCsd() {
        super.getCsd_enforcePermission();
        return this.mSoundDoseHelper.getCsd();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public void setCsd(float f) {
        super.setCsd_enforcePermission();
        this.mSoundDoseHelper.setCsd(f);
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public void forceUseFrameworkMel(boolean z) {
        super.forceUseFrameworkMel_enforcePermission();
        this.mSoundDoseHelper.forceUseFrameworkMel(z);
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public void forceComputeCsdOnAllDevices(boolean z) {
        super.forceComputeCsdOnAllDevices_enforcePermission();
        this.mSoundDoseHelper.forceComputeCsdOnAllDevices(z);
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED")
    public boolean isCsdEnabled() {
        super.isCsdEnabled_enforcePermission();
        return this.mSoundDoseHelper.isCsdEnabled();
    }

    @GuardedBy({"mHdmiClientLock"})
    public final void updateHdmiCecSinkLocked(boolean z) {
        if (hasDeviceVolumeBehavior(1024)) {
            return;
        }
        if (z) {
            setDeviceVolumeBehaviorInternal(new AudioDeviceAttributes(1024, ""), 1, "AudioService.updateHdmiCecSinkLocked()");
        } else {
            setDeviceVolumeBehaviorInternal(new AudioDeviceAttributes(1024, ""), 0, "AudioService.updateHdmiCecSinkLocked()");
        }
        postUpdateVolumeStatesForAudioDevice(1024, "HdmiPlaybackClient.DisplayStatusCallback");
    }

    /* loaded from: classes.dex */
    public class MyHdmiControlStatusChangeListenerCallback implements HdmiControlManager.HdmiControlStatusChangeListener {
        public MyHdmiControlStatusChangeListenerCallback() {
        }

        public void onStatusChange(int i, boolean z) {
            synchronized (AudioService.this.mHdmiClientLock) {
                if (AudioService.this.mHdmiManager == null) {
                    return;
                }
                boolean z2 = true;
                if (i != 1) {
                    z2 = false;
                }
                AudioService audioService = AudioService.this;
                if (!z2) {
                    z = false;
                }
                audioService.updateHdmiCecSinkLocked(z);
            }
        }
    }

    /* loaded from: classes.dex */
    public class MyHdmiCecVolumeControlFeatureListener implements HdmiControlManager.HdmiCecVolumeControlFeatureListener {
        public MyHdmiCecVolumeControlFeatureListener() {
        }

        public void onHdmiCecVolumeControlFeature(int i) {
            synchronized (AudioService.this.mHdmiClientLock) {
                if (AudioService.this.mHdmiManager == null) {
                    return;
                }
                AudioService audioService = AudioService.this;
                boolean z = true;
                if (i != 1) {
                    z = false;
                }
                audioService.mHdmiCecVolumeControlEnabled = z;
            }
        }
    }

    public int setHdmiSystemAudioSupported(boolean z) {
        synchronized (this.mHdmiClientLock) {
            if (this.mHdmiManager != null) {
                if (this.mHdmiTvClient == null && this.mHdmiAudioSystemClient == null) {
                    Log.w("AS.AudioService", "Only Hdmi-Cec enabled TV or audio system device supportssystem audio mode.");
                    return 0;
                }
                if (this.mHdmiSystemAudioSupported != z) {
                    this.mHdmiSystemAudioSupported = z;
                    this.mDeviceBroker.setForceUse_Async(5, z ? 12 : 0, "setHdmiSystemAudioSupported");
                }
                r2 = getDeviceMaskForStream(3);
            }
            return r2;
        }
    }

    public boolean isHdmiSystemAudioSupported() {
        return this.mHdmiSystemAudioSupported;
    }

    public final void initA11yMonitoring() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        updateDefaultStreamOverrideDelay(accessibilityManager.isTouchExplorationEnabled());
        updateA11yVolumeAlias(accessibilityManager.isAccessibilityVolumeStreamActive());
        accessibilityManager.addTouchExplorationStateChangeListener(this, null);
        accessibilityManager.addAccessibilityServicesStateChangeListener(this);
    }

    @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
    public void onTouchExplorationStateChanged(boolean z) {
        updateDefaultStreamOverrideDelay(z);
    }

    public final void updateDefaultStreamOverrideDelay(boolean z) {
        if (z) {
            sStreamOverrideDelayMs = 1000;
        } else {
            sStreamOverrideDelayMs = 0;
        }
    }

    public void onAccessibilityServicesStateChanged(AccessibilityManager accessibilityManager) {
        updateA11yVolumeAlias(accessibilityManager.isAccessibilityVolumeStreamActive());
    }

    public final void updateA11yVolumeAlias(boolean z) {
        if (sIndependentA11yVolume != z) {
            sIndependentA11yVolume = z;
            updateStreamVolumeAlias(true, "AS.AudioService");
            this.mVolumeController.setA11yMode(sIndependentA11yVolume ? 1 : 0);
            this.mVolumeController.postVolumeChanged(10, 0);
        }
    }

    public boolean isCameraSoundForced() {
        boolean z;
        synchronized (this.mSettingsLock) {
            z = this.mCameraSoundForced;
        }
        return z;
    }

    public final void dumpRingerMode(PrintWriter printWriter) {
        printWriter.println("\nRinger mode: ");
        StringBuilder sb = new StringBuilder();
        sb.append("- mode (internal) = ");
        String[] strArr = RINGER_MODE_NAMES;
        sb.append(strArr[this.mRingerMode]);
        printWriter.println(sb.toString());
        printWriter.println("- mode (external) = " + strArr[this.mRingerModeExternal]);
        printWriter.println("- zen mode:" + Settings.Global.zenModeToString(this.mNm.getZenMode()));
        dumpRingerModeStreams(printWriter, "affected", this.mRingerModeAffectedStreams);
        dumpRingerModeStreams(printWriter, "muted", this.mRingerAndZenModeMutedStreams);
        printWriter.print("- delegate = ");
        printWriter.println(this.mRingerModeDelegate);
    }

    public final void dumpRingerModeStreams(PrintWriter printWriter, String str, int i) {
        printWriter.print("- ringer mode ");
        printWriter.print(str);
        printWriter.print(" streams = 0x");
        printWriter.print(Integer.toHexString(i));
        if (i != 0) {
            printWriter.print(" (");
            boolean z = true;
            for (int i2 = 0; i2 < AudioSystem.STREAM_NAMES.length; i2++) {
                int i3 = 1 << i2;
                if ((i & i3) != 0) {
                    if (!z) {
                        printWriter.print(',');
                    }
                    printWriter.print(AudioSystem.STREAM_NAMES[i2]);
                    i &= ~i3;
                    z = false;
                }
            }
            if (i != 0) {
                if (!z) {
                    printWriter.print(',');
                }
                printWriter.print(i);
            }
            printWriter.print(')');
        }
        printWriter.println();
    }

    public final Set<Integer> getAbsoluteVolumeDevicesWithBehavior(final int i) {
        return (Set) this.mAbsoluteVolumeDeviceInfoMap.entrySet().stream().filter(new Predicate() { // from class: com.android.server.audio.AudioService$$ExternalSyntheticLambda9
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getAbsoluteVolumeDevicesWithBehavior$16;
                lambda$getAbsoluteVolumeDevicesWithBehavior$16 = AudioService.lambda$getAbsoluteVolumeDevicesWithBehavior$16(i, (Map.Entry) obj);
                return lambda$getAbsoluteVolumeDevicesWithBehavior$16;
            }
        }).map(new UiModeManagerService$13$$ExternalSyntheticLambda1()).collect(Collectors.toSet());
    }

    public static /* synthetic */ boolean lambda$getAbsoluteVolumeDevicesWithBehavior$16(int i, Map.Entry entry) {
        return ((AbsoluteVolumeDeviceInfo) entry.getValue()).mDeviceVolumeBehavior == i;
    }

    public final String dumpDeviceTypes(Set<Integer> set) {
        Iterator<Integer> it = set.iterator();
        if (it.hasNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append("0x" + Integer.toHexString(it.next().intValue()));
            while (it.hasNext()) {
                sb.append(",0x" + Integer.toHexString(it.next().intValue()));
            }
            return sb.toString();
        }
        return "";
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "AS.AudioService", printWriter)) {
            sLifecycleLogger.dump(printWriter);
            if (this.mAudioHandler != null) {
                printWriter.println("\nMessage handler (watch for unhandled messages):");
                this.mAudioHandler.dump(new PrintWriterPrinter(printWriter), "  ");
            } else {
                printWriter.println("\nMessage handler is null");
            }
            this.mMediaFocusControl.dump(printWriter);
            dumpStreamStates(printWriter);
            dumpVolumeGroups(printWriter);
            dumpRingerMode(printWriter);
            dumpAudioMode(printWriter);
            printWriter.println("\nAudio routes:");
            printWriter.print("  mMainType=0x");
            printWriter.println(Integer.toHexString(this.mDeviceBroker.getCurAudioRoutes().mainType));
            printWriter.print("  mBluetoothName=");
            printWriter.println(this.mDeviceBroker.getCurAudioRoutes().bluetoothName);
            printWriter.println("\nOther state:");
            printWriter.print("  mUseVolumeGroupAliases=");
            printWriter.println(this.mUseVolumeGroupAliases);
            printWriter.print("  mVolumeController=");
            printWriter.println(this.mVolumeController);
            this.mSoundDoseHelper.dump(printWriter);
            printWriter.print("  sIndependentA11yVolume=");
            printWriter.println(sIndependentA11yVolume);
            printWriter.print("  mCameraSoundForced=");
            printWriter.println(isCameraSoundForced());
            printWriter.print("  mHasVibrator=");
            printWriter.println(this.mHasVibrator);
            printWriter.print("  mVolumePolicy=");
            printWriter.println(this.mVolumePolicy);
            printWriter.print("  mAvrcpAbsVolSupported=");
            printWriter.println(this.mAvrcpAbsVolSupported);
            printWriter.print("  mBtScoOnByApp=");
            printWriter.println(this.mBtScoOnByApp);
            printWriter.print("  mIsSingleVolume=");
            printWriter.println(this.mIsSingleVolume);
            printWriter.print("  mUseFixedVolume=");
            printWriter.println(this.mUseFixedVolume);
            printWriter.print("  mNotifAliasRing=");
            printWriter.println(this.mNotifAliasRing);
            printWriter.print("  mFixedVolumeDevices=");
            printWriter.println(dumpDeviceTypes(this.mFixedVolumeDevices));
            printWriter.print("  mFullVolumeDevices=");
            printWriter.println(dumpDeviceTypes(this.mFullVolumeDevices));
            printWriter.print("  absolute volume devices=");
            printWriter.println(dumpDeviceTypes(getAbsoluteVolumeDevicesWithBehavior(3)));
            printWriter.print("  adjust-only absolute volume devices=");
            printWriter.println(dumpDeviceTypes(getAbsoluteVolumeDevicesWithBehavior(5)));
            printWriter.print("  mExtVolumeController=");
            printWriter.println(this.mExtVolumeController);
            printWriter.print("  mHdmiAudioSystemClient=");
            printWriter.println(this.mHdmiAudioSystemClient);
            printWriter.print("  mHdmiPlaybackClient=");
            printWriter.println(this.mHdmiPlaybackClient);
            printWriter.print("  mHdmiTvClient=");
            printWriter.println(this.mHdmiTvClient);
            printWriter.print("  mHdmiSystemAudioSupported=");
            printWriter.println(this.mHdmiSystemAudioSupported);
            synchronized (this.mHdmiClientLock) {
                printWriter.print("  mHdmiCecVolumeControlEnabled=");
                printWriter.println(this.mHdmiCecVolumeControlEnabled);
            }
            printWriter.print("  mIsCallScreeningModeSupported=");
            printWriter.println(this.mIsCallScreeningModeSupported);
            printWriter.print("  mic mute FromSwitch=" + this.mMicMuteFromSwitch + " FromRestrictions=" + this.mMicMuteFromRestrictions + " FromApi=" + this.mMicMuteFromApi + " from system=" + this.mMicMuteFromSystemCached);
            dumpAccessibilityServiceUids(printWriter);
            dumpAssistantServicesUids(printWriter);
            dumpAudioPolicies(printWriter);
            this.mDynPolicyLogger.dump(printWriter);
            this.mPlaybackMonitor.dump(printWriter);
            this.mRecordMonitor.dump(printWriter);
            printWriter.println("\nAudioDeviceBroker:");
            this.mDeviceBroker.dump(printWriter, "  ");
            printWriter.println("\nSoundEffects:");
            this.mSfxHelper.dump(printWriter, "  ");
            printWriter.println("\n");
            printWriter.println("\nEvent logs:");
            this.mModeLogger.dump(printWriter);
            printWriter.println("\n");
            sDeviceLogger.dump(printWriter);
            printWriter.println("\n");
            sForceUseLogger.dump(printWriter);
            printWriter.println("\n");
            sVolumeLogger.dump(printWriter);
            printWriter.println("\n");
            dumpSupportedSystemUsage(printWriter);
            printWriter.println("\n");
            printWriter.println("\nSpatial audio:");
            printWriter.println("mHasSpatializerEffect:" + this.mHasSpatializerEffect + " (effect present)");
            printWriter.println("isSpatializerEnabled:" + isSpatializerEnabled() + " (routing dependent)");
            this.mSpatializerHelper.dump(printWriter);
            sSpatialLogger.dump(printWriter);
            this.mAudioSystem.dump(printWriter);
        }
    }

    public final void dumpSupportedSystemUsage(PrintWriter printWriter) {
        printWriter.println("Supported System Usages:");
        synchronized (this.mSupportedSystemUsagesLock) {
            int i = 0;
            while (true) {
                int[] iArr = this.mSupportedSystemUsages;
                if (i < iArr.length) {
                    printWriter.printf("\t%s\n", AudioAttributes.usageToString(iArr[i]));
                    i++;
                }
            }
        }
    }

    public final void dumpAssistantServicesUids(PrintWriter printWriter) {
        synchronized (this.mSettingsLock) {
            if (this.mAssistantUids.size() > 0) {
                printWriter.println("  Assistant service UIDs:");
                Iterator<Integer> it = this.mAssistantUids.iterator();
                while (it.hasNext()) {
                    int intValue = it.next().intValue();
                    printWriter.println("  - " + intValue);
                }
            } else {
                printWriter.println("  No Assistant service Uids.");
            }
        }
    }

    public final void dumpAccessibilityServiceUids(PrintWriter printWriter) {
        int[] iArr;
        synchronized (this.mSupportedSystemUsagesLock) {
            int[] iArr2 = this.mAccessibilityServiceUids;
            if (iArr2 != null && iArr2.length > 0) {
                printWriter.println("  Accessibility service Uids:");
                for (int i : this.mAccessibilityServiceUids) {
                    printWriter.println("  - " + i);
                }
            } else {
                printWriter.println("  No accessibility service Uids.");
            }
        }
    }

    public static void readAndSetLowRamDevice() {
        long j;
        boolean isLowRamDeviceStatic = ActivityManager.isLowRamDeviceStatic();
        try {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ActivityManager.getService().getMemoryInfo(memoryInfo);
            j = memoryInfo.totalMem;
        } catch (RemoteException unused) {
            Log.w("AS.AudioService", "Cannot obtain MemoryInfo from ActivityManager, assume low memory device");
            j = 1073741824;
            isLowRamDeviceStatic = true;
        }
        int lowRamDevice = AudioSystem.setLowRamDevice(isLowRamDeviceStatic, j);
        if (lowRamDevice != 0) {
            Log.w("AS.AudioService", "AudioFlinger informed of device's low RAM attribute; status " + lowRamDevice);
        }
    }

    public final void enforceVolumeController(String str) {
        Context context = this.mContext;
        context.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "Only SystemUI can " + str);
    }

    public void setVolumeController(final IVolumeController iVolumeController) {
        enforceVolumeController("set the volume controller");
        if (this.mVolumeController.isSameBinder(iVolumeController)) {
            return;
        }
        this.mVolumeController.postDismiss();
        if (iVolumeController != null) {
            try {
                iVolumeController.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.audio.AudioService.5
                    @Override // android.os.IBinder.DeathRecipient
                    public void binderDied() {
                        if (AudioService.this.mVolumeController.isSameBinder(iVolumeController)) {
                            Log.w("AS.AudioService", "Current remote volume controller died, unregistering");
                            AudioService.this.setVolumeController(null);
                        }
                    }
                }, 0);
            } catch (RemoteException unused) {
            }
        }
        this.mVolumeController.setController(iVolumeController);
    }

    public IVolumeController getVolumeController() {
        enforceVolumeController("get the volume controller");
        return this.mVolumeController.getController();
    }

    public void notifyVolumeControllerVisible(IVolumeController iVolumeController, boolean z) {
        enforceVolumeController("notify about volume controller visibility");
        if (this.mVolumeController.isSameBinder(iVolumeController)) {
            this.mVolumeController.setVisible(z);
        }
    }

    public void setVolumePolicy(VolumePolicy volumePolicy) {
        enforceVolumeController("set volume policy");
        if (volumePolicy == null || volumePolicy.equals(this.mVolumePolicy)) {
            return;
        }
        this.mVolumePolicy = volumePolicy;
    }

    /* loaded from: classes.dex */
    public class VolumeController implements ISafeHearingVolumeController {
        public IVolumeController mController;
        public int mLongPressTimeout;
        public long mNextLongPress;
        public boolean mVisible;

        public VolumeController() {
        }

        public void setController(IVolumeController iVolumeController) {
            this.mController = iVolumeController;
            this.mVisible = false;
        }

        public IVolumeController getController() {
            return this.mController;
        }

        public void loadSettings(ContentResolver contentResolver) {
            this.mLongPressTimeout = AudioService.this.mSettings.getSecureIntForUser(contentResolver, "long_press_timeout", 500, -2);
        }

        public boolean suppressAdjustment(int i, int i2, boolean z) {
            if (z || i != 3 || this.mController == null) {
                return false;
            }
            if (i == 3 && AudioService.this.mAudioSystem.isStreamActive(3, this.mLongPressTimeout)) {
                return false;
            }
            long uptimeMillis = SystemClock.uptimeMillis();
            if ((i2 & 1) != 0 && !this.mVisible) {
                if (this.mNextLongPress < uptimeMillis) {
                    this.mNextLongPress = uptimeMillis + this.mLongPressTimeout;
                }
            } else {
                long j = this.mNextLongPress;
                if (j <= 0) {
                    return false;
                }
                if (uptimeMillis > j) {
                    this.mNextLongPress = 0L;
                    return false;
                }
            }
            return true;
        }

        public void setVisible(boolean z) {
            this.mVisible = z;
        }

        public boolean isSameBinder(IVolumeController iVolumeController) {
            return Objects.equals(asBinder(), binder(iVolumeController));
        }

        public IBinder asBinder() {
            return binder(this.mController);
        }

        public final IBinder binder(IVolumeController iVolumeController) {
            if (iVolumeController == null) {
                return null;
            }
            return iVolumeController.asBinder();
        }

        public String toString() {
            return "VolumeController(" + asBinder() + ",mVisible=" + this.mVisible + ")";
        }

        @Override // com.android.server.audio.AudioService.ISafeHearingVolumeController
        public void postDisplaySafeVolumeWarning(int i) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                return;
            }
            try {
                iVolumeController.displaySafeVolumeWarning(i | 1);
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling displaySafeVolumeWarning", e);
            }
        }

        @Override // com.android.server.audio.AudioService.ISafeHearingVolumeController
        public void postDisplayCsdWarning(int i, int i2) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                Log.e("VolumeController", "Unable to display CSD warning, no controller");
                return;
            }
            try {
                iVolumeController.displayCsdWarning(i, i2);
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling displayCsdWarning for warning " + i, e);
            }
        }

        public void postVolumeChanged(int i, int i2) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                return;
            }
            try {
                iVolumeController.volumeChanged(i, i2);
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling volumeChanged", e);
            }
        }

        public void postMasterMuteChanged(int i) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                return;
            }
            try {
                iVolumeController.masterMuteChanged(i);
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling masterMuteChanged", e);
            }
        }

        public void setLayoutDirection(int i) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                return;
            }
            try {
                iVolumeController.setLayoutDirection(i);
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling setLayoutDirection", e);
            }
        }

        public void postDismiss() {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                return;
            }
            try {
                iVolumeController.dismiss();
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling dismiss", e);
            }
        }

        public void setA11yMode(int i) {
            IVolumeController iVolumeController = this.mController;
            if (iVolumeController == null) {
                return;
            }
            try {
                iVolumeController.setA11yMode(i);
            } catch (RemoteException e) {
                Log.w("VolumeController", "Error calling setA11Mode", e);
            }
        }
    }

    /* loaded from: classes.dex */
    public final class AudioServiceInternal extends AudioManagerInternal {
        public AudioServiceInternal() {
        }

        public void setRingerModeDelegate(AudioManagerInternal.RingerModeDelegate ringerModeDelegate) {
            AudioService.this.mRingerModeDelegate = ringerModeDelegate;
            if (AudioService.this.mRingerModeDelegate != null) {
                synchronized (AudioService.this.mSettingsLock) {
                    AudioService.this.updateRingerAndZenModeAffectedStreams();
                }
                setRingerModeInternal(getRingerModeInternal(), "AS.AudioService.setRingerModeDelegate");
            }
        }

        public int getRingerModeInternal() {
            return AudioService.this.getRingerModeInternal();
        }

        public void setRingerModeInternal(int i, String str) {
            AudioService.this.setRingerModeInternal(i, str);
        }

        public void silenceRingerModeInternal(String str) {
            AudioService.this.silenceRingerModeInternal(str);
        }

        public void updateRingerModeAffectedStreamsInternal() {
            synchronized (AudioService.this.mSettingsLock) {
                if (AudioService.this.updateRingerAndZenModeAffectedStreams()) {
                    AudioService.this.setRingerModeInt(getRingerModeInternal(), false);
                }
            }
        }

        public void addAssistantServiceUid(int i) {
            AudioService.sendMsg(AudioService.this.mAudioHandler, 44, 2, i, 0, null, 0);
        }

        public void removeAssistantServiceUid(int i) {
            AudioService.sendMsg(AudioService.this.mAudioHandler, 45, 2, i, 0, null, 0);
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0034 A[Catch: all -> 0x006d, LOOP:0: B:15:0x0034->B:20:0x004c, LOOP_START, PHI: r2 
          PHI: (r2v1 int) = (r2v0 int), (r2v2 int) binds: [B:14:0x0032, B:20:0x004c] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x000d, B:24:0x005b, B:7:0x0017, B:9:0x0021, B:15:0x0034, B:17:0x003d, B:23:0x0052, B:20:0x004c), top: B:30:0x0007 }] */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0052 A[Catch: all -> 0x006d, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x000d, B:24:0x005b, B:7:0x0017, B:9:0x0021, B:15:0x0034, B:17:0x003d, B:23:0x0052, B:20:0x004c), top: B:30:0x0007 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void setActiveAssistantServicesUids(IntArray intArray) {
            boolean z;
            synchronized (AudioService.this.mSettingsLock) {
                if (intArray.size() == 0) {
                    AudioService.this.mActiveAssistantServiceUids = AudioService.NO_ACTIVE_ASSISTANT_SERVICE_UIDS;
                } else {
                    boolean z2 = true;
                    if (AudioService.this.mActiveAssistantServiceUids != null && AudioService.this.mActiveAssistantServiceUids.length == intArray.size()) {
                        z = false;
                        if (!z) {
                            for (int i = 0; i < AudioService.this.mActiveAssistantServiceUids.length; i++) {
                                if (intArray.get(i) != AudioService.this.mActiveAssistantServiceUids[i]) {
                                    break;
                                }
                            }
                        }
                        z2 = z;
                        if (z2) {
                            AudioService.this.mActiveAssistantServiceUids = intArray.toArray();
                        }
                    }
                    z = true;
                    if (!z) {
                    }
                    z2 = z;
                    if (z2) {
                    }
                }
            }
            AudioService.sendMsg(AudioService.this.mAudioHandler, 46, 0, 0, 0, null, 0);
        }

        /* JADX WARN: Removed duplicated region for block: B:18:0x003a A[Catch: all -> 0x0073, LOOP:0: B:18:0x003a->B:23:0x0052, LOOP_START, PHI: r2 
          PHI: (r2v1 int) = (r2v0 int), (r2v2 int) binds: [B:17:0x0038, B:23:0x0052] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {, blocks: (B:7:0x0010, B:9:0x0016, B:27:0x0061, B:28:0x0071, B:10:0x001d, B:12:0x0027, B:18:0x003a, B:20:0x0043, B:26:0x0058, B:23:0x0052), top: B:33:0x0010 }] */
        /* JADX WARN: Removed duplicated region for block: B:26:0x0058 A[Catch: all -> 0x0073, TryCatch #0 {, blocks: (B:7:0x0010, B:9:0x0016, B:27:0x0061, B:28:0x0071, B:10:0x001d, B:12:0x0027, B:18:0x003a, B:20:0x0043, B:26:0x0058, B:23:0x0052), top: B:33:0x0010 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void setAccessibilityServiceUids(IntArray intArray) {
            boolean z;
            if (AudioService.this.isPlatformAutomotive()) {
                return;
            }
            synchronized (AudioService.this.mAccessibilityServiceUidsLock) {
                if (intArray.size() == 0) {
                    AudioService.this.mAccessibilityServiceUids = null;
                } else {
                    boolean z2 = true;
                    if (AudioService.this.mAccessibilityServiceUids != null && AudioService.this.mAccessibilityServiceUids.length == intArray.size()) {
                        z = false;
                        if (!z) {
                            for (int i = 0; i < AudioService.this.mAccessibilityServiceUids.length; i++) {
                                if (intArray.get(i) != AudioService.this.mAccessibilityServiceUids[i]) {
                                    break;
                                }
                            }
                        }
                        z2 = z;
                        if (z2) {
                            AudioService.this.mAccessibilityServiceUids = intArray.toArray();
                        }
                    }
                    z = true;
                    if (!z) {
                    }
                    z2 = z;
                    if (z2) {
                    }
                }
                AudioService.sendMsg(AudioService.this.mAudioHandler, 35, 0, 0, 0, null, 0);
            }
        }

        public void setInputMethodServiceUid(int i) {
            synchronized (AudioService.this.mInputMethodServiceUidLock) {
                if (AudioService.this.mInputMethodServiceUid != i) {
                    AudioService.this.mAudioSystem.setCurrentImeUid(i);
                    AudioService.this.mInputMethodServiceUid = i;
                }
            }
        }
    }

    public final void onUpdateAccessibilityServiceUids() {
        int[] iArr;
        synchronized (this.mAccessibilityServiceUidsLock) {
            iArr = this.mAccessibilityServiceUids;
        }
        AudioSystem.setA11yServicesUids(iArr);
    }

    public String registerAudioPolicy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback, boolean z, boolean z2, boolean z3, boolean z4, IMediaProjection iMediaProjection) {
        boolean z5;
        AudioPolicyConfig audioPolicyConfig2;
        boolean z6;
        IMediaProjection iMediaProjection2;
        AudioSystem.setDynamicPolicyCallback(this.mDynPolicyCallback);
        if (z2 || z3 || z) {
            z5 = true;
            audioPolicyConfig2 = audioPolicyConfig;
            z6 = z4;
            iMediaProjection2 = iMediaProjection;
        } else {
            audioPolicyConfig2 = audioPolicyConfig;
            z6 = z4;
            iMediaProjection2 = iMediaProjection;
            z5 = false;
        }
        if (!isPolicyRegisterAllowed(audioPolicyConfig2, z5, z6, iMediaProjection2)) {
            Slog.w("AS.AudioService", "Permission denied to register audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need system permission or a MediaProjection that can project audio");
            return null;
        }
        synchronized (this.mAudioPolicies) {
            if (this.mAudioPolicies.containsKey(iAudioPolicyCallback.asBinder())) {
                Slog.e("AS.AudioService", "Cannot re-register policy");
                return null;
            }
            try {
                AudioPolicyProxy audioPolicyProxy = new AudioPolicyProxy(audioPolicyConfig, iAudioPolicyCallback, z, z2, z3, z4, iMediaProjection);
                iAudioPolicyCallback.asBinder().linkToDeath(audioPolicyProxy, 0);
                this.mDynPolicyLogger.enqueue(new EventLogger.StringEvent("registerAudioPolicy for " + iAudioPolicyCallback.asBinder() + " u/pid:" + Binder.getCallingUid() + "/" + Binder.getCallingPid() + " with config:" + audioPolicyProxy.toCompactLogString()).printLog("AS.AudioService"));
                String registrationId = audioPolicyProxy.getRegistrationId();
                this.mAudioPolicies.put(iAudioPolicyCallback.asBinder(), audioPolicyProxy);
                return registrationId;
            } catch (RemoteException e) {
                Slog.w("AS.AudioService", "Audio policy registration failed, could not link to " + iAudioPolicyCallback + " binder death", e);
                return null;
            } catch (IllegalStateException e2) {
                Slog.w("AS.AudioService", "Audio policy registration failed for binder " + iAudioPolicyCallback, e2);
                return null;
            }
        }
    }

    public final void onPolicyClientDeath(List<String> list) {
        for (String str : list) {
            if (this.mPlaybackMonitor.hasActiveMediaPlaybackOnSubmixWithAddress(str)) {
                this.mDeviceBroker.postBroadcastBecomingNoisy();
                return;
            }
        }
    }

    public final boolean isPolicyRegisterAllowed(AudioPolicyConfig audioPolicyConfig, boolean z, boolean z2, IMediaProjection iMediaProjection) {
        boolean z3 = z || z2 || audioPolicyConfig.getMixes().isEmpty();
        Iterator it = audioPolicyConfig.getMixes().iterator();
        ArrayList arrayList = null;
        boolean z4 = false;
        boolean z5 = false;
        boolean z6 = false;
        while (it.hasNext()) {
            AudioMix audioMix = (AudioMix) it.next();
            if (audioMix.getRule().allowPrivilegedMediaPlaybackCapture()) {
                String canBeUsedForPrivilegedMediaCapture = AudioMix.canBeUsedForPrivilegedMediaCapture(audioMix.getFormat());
                if (canBeUsedForPrivilegedMediaCapture != null) {
                    Log.e("AS.AudioService", canBeUsedForPrivilegedMediaCapture);
                    return false;
                }
                z4 |= true;
            }
            if (audioMix.containsMatchAttributeRuleForUsage(2) && audioMix.getRouteFlags() == 3) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(audioMix);
            }
            if (audioMix.getRouteFlags() == 3 && iMediaProjection != null) {
                z5 |= true;
            } else if (audioMix.isForCallRedirection()) {
                z6 |= true;
            } else if (audioMix.containsMatchAttributeRuleForUsage(2)) {
                z3 |= true;
            }
        }
        if (z4 && !callerHasPermission("android.permission.CAPTURE_MEDIA_OUTPUT") && !callerHasPermission("android.permission.CAPTURE_AUDIO_OUTPUT")) {
            Log.e("AS.AudioService", "Privileged audio capture requires CAPTURE_MEDIA_OUTPUT or CAPTURE_AUDIO_OUTPUT system permission");
            return false;
        }
        if (arrayList != null && arrayList.size() > 0) {
            if (!callerHasPermission("android.permission.CAPTURE_VOICE_COMMUNICATION_OUTPUT")) {
                Log.e("AS.AudioService", "Audio capture for voice communication requires CAPTURE_VOICE_COMMUNICATION_OUTPUT system permission");
                return false;
            }
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                ((AudioMix) it2.next()).getRule().setVoiceCommunicationCaptureAllowed(true);
            }
        }
        if (!z5 || canProjectAudio(iMediaProjection)) {
            if (z3 && !callerHasPermission("android.permission.MODIFY_AUDIO_ROUTING")) {
                Log.e("AS.AudioService", "Can not capture audio without MODIFY_AUDIO_ROUTING");
                return false;
            } else if (!z6 || callerHasPermission("android.permission.CALL_AUDIO_INTERCEPTION")) {
                return true;
            } else {
                Log.e("AS.AudioService", "Can not capture audio without CALL_AUDIO_INTERCEPTION");
                return false;
            }
        }
        return false;
    }

    public final boolean callerHasPermission(String str) {
        return this.mContext.checkCallingPermission(str) == 0;
    }

    public final boolean canProjectAudio(IMediaProjection iMediaProjection) {
        if (iMediaProjection == null) {
            Log.e("AS.AudioService", "MediaProjection is null");
            return false;
        }
        IMediaProjectionManager projectionService = getProjectionService();
        if (projectionService == null) {
            Log.e("AS.AudioService", "Can't get service IMediaProjectionManager");
            return false;
        }
        try {
            if (!projectionService.isCurrentProjection(iMediaProjection)) {
                Log.w("AS.AudioService", "App passed invalid MediaProjection token");
                return false;
            }
            try {
                if (iMediaProjection.canProjectAudio()) {
                    return true;
                }
                Log.w("AS.AudioService", "App passed MediaProjection that can not project audio");
                return false;
            } catch (RemoteException e) {
                Log.e("AS.AudioService", "Can't call .canProjectAudio() on valid IMediaProjection" + iMediaProjection.asBinder(), e);
                return false;
            }
        } catch (RemoteException e2) {
            Log.e("AS.AudioService", "Can't call .isCurrentProjection() on IMediaProjectionManager" + projectionService.asBinder(), e2);
            return false;
        }
    }

    public final IMediaProjectionManager getProjectionService() {
        if (this.mProjectionService == null) {
            this.mProjectionService = IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection"));
        }
        return this.mProjectionService;
    }

    public void unregisterAudioPolicyAsync(IAudioPolicyCallback iAudioPolicyCallback) {
        if (iAudioPolicyCallback == null) {
            return;
        }
        unregisterAudioPolicyInt(iAudioPolicyCallback, "unregisterAudioPolicyAsync");
    }

    public void unregisterAudioPolicy(IAudioPolicyCallback iAudioPolicyCallback) {
        if (iAudioPolicyCallback == null) {
            return;
        }
        unregisterAudioPolicyInt(iAudioPolicyCallback, "unregisterAudioPolicy");
    }

    public final void unregisterAudioPolicyInt(IAudioPolicyCallback iAudioPolicyCallback, String str) {
        EventLogger eventLogger = this.mDynPolicyLogger;
        eventLogger.enqueue(new EventLogger.StringEvent(str + " for " + iAudioPolicyCallback.asBinder()).printLog("AS.AudioService"));
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy remove = this.mAudioPolicies.remove(iAudioPolicyCallback.asBinder());
            if (remove == null) {
                Slog.w("AS.AudioService", "Trying to unregister unknown audio policy for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid());
                return;
            }
            iAudioPolicyCallback.asBinder().unlinkToDeath(remove, 0);
            remove.release();
        }
    }

    @GuardedBy({"mAudioPolicies"})
    public final AudioPolicyProxy checkUpdateForPolicy(IAudioPolicyCallback iAudioPolicyCallback, String str) {
        if (!(this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0)) {
            Slog.w("AS.AudioService", str + " for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", need MODIFY_AUDIO_ROUTING");
            return null;
        }
        AudioPolicyProxy audioPolicyProxy = this.mAudioPolicies.get(iAudioPolicyCallback.asBinder());
        if (audioPolicyProxy == null) {
            Slog.w("AS.AudioService", str + " for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid() + ", unregistered policy");
            return null;
        }
        return audioPolicyProxy;
    }

    public int addMixForPolicy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot add AudioMix in audio policy");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            return checkUpdateForPolicy.addMixes(audioPolicyConfig.getMixes()) == 0 ? 0 : -1;
        }
    }

    public int removeMixForPolicy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot add AudioMix in audio policy");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            return checkUpdateForPolicy.removeMixes(audioPolicyConfig.getMixes()) == 0 ? 0 : -1;
        }
    }

    public int setUidDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i, int[] iArr, String[] strArr) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot change device affinity in audio policy");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            if (checkUpdateForPolicy.hasMixRoutedToDevices(iArr, strArr)) {
                return checkUpdateForPolicy.setUidDeviceAffinities(i, iArr, strArr);
            }
            return -1;
        }
    }

    public int setUserIdDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i, int[] iArr, String[] strArr) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot change device affinity in audio policy");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            if (checkUpdateForPolicy.hasMixRoutedToDevices(iArr, strArr)) {
                return checkUpdateForPolicy.setUserIdDeviceAffinities(i, iArr, strArr);
            }
            return -1;
        }
    }

    public int removeUidDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot remove device affinity in audio policy");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            return checkUpdateForPolicy.removeUidDeviceAffinities(i);
        }
    }

    public int removeUserIdDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot remove device affinity in audio policy");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            return checkUpdateForPolicy.removeUserIdDeviceAffinities(i);
        }
    }

    public int setFocusPropertiesForPolicy(int i, IAudioPolicyCallback iAudioPolicyCallback) {
        synchronized (this.mAudioPolicies) {
            AudioPolicyProxy checkUpdateForPolicy = checkUpdateForPolicy(iAudioPolicyCallback, "Cannot change audio policy focus properties");
            if (checkUpdateForPolicy == null) {
                return -1;
            }
            if (!this.mAudioPolicies.containsKey(iAudioPolicyCallback.asBinder())) {
                Slog.e("AS.AudioService", "Cannot change audio policy focus properties, unregistered policy");
                return -1;
            }
            boolean z = true;
            if (i == 1) {
                for (AudioPolicyProxy audioPolicyProxy : this.mAudioPolicies.values()) {
                    if (audioPolicyProxy.mFocusDuckBehavior == 1) {
                        Slog.e("AS.AudioService", "Cannot change audio policy ducking behavior, already handled");
                        return -1;
                    }
                }
            }
            checkUpdateForPolicy.mFocusDuckBehavior = i;
            MediaFocusControl mediaFocusControl = this.mMediaFocusControl;
            if (i != 1) {
                z = false;
            }
            mediaFocusControl.setDuckingInExtPolicyAvailable(z);
            return 0;
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public List<AudioFocusInfo> getFocusStack() {
        super.getFocusStack_enforcePermission();
        return this.mMediaFocusControl.getFocusStack();
    }

    public boolean sendFocusLoss(AudioFocusInfo audioFocusInfo, IAudioPolicyCallback iAudioPolicyCallback) {
        Objects.requireNonNull(audioFocusInfo);
        Objects.requireNonNull(iAudioPolicyCallback);
        enforceModifyAudioRoutingPermission();
        if (!this.mAudioPolicies.containsKey(iAudioPolicyCallback.asBinder())) {
            throw new IllegalStateException("Only registered AudioPolicy can change focus");
        }
        if (!this.mAudioPolicies.get(iAudioPolicyCallback.asBinder()).mHasFocusListener) {
            throw new IllegalStateException("AudioPolicy must have focus listener to change focus");
        }
        return this.mMediaFocusControl.sendFocusLoss(audioFocusInfo);
    }

    public AudioHalVersionInfo getHalVersion() {
        for (AudioHalVersionInfo audioHalVersionInfo : AudioHalVersionInfo.VERSIONS) {
            try {
                HwBinder.getService(String.format("android.hardware.audio@%s::IDevicesFactory", audioHalVersionInfo.getMajorVersion() + "." + audioHalVersionInfo.getMinorVersion()), "default");
                return audioHalVersionInfo;
            } catch (RemoteException e) {
                Log.e("AS.AudioService", "Remote exception when getting hardware audio service:", e);
            } catch (NoSuchElementException unused) {
            }
        }
        return null;
    }

    public boolean hasRegisteredDynamicPolicy() {
        boolean z;
        synchronized (this.mAudioPolicies) {
            z = !this.mAudioPolicies.isEmpty();
        }
        return z;
    }

    public int setPreferredMixerAttributes(AudioAttributes audioAttributes, int i, AudioMixerAttributes audioMixerAttributes) {
        Objects.requireNonNull(audioAttributes);
        Objects.requireNonNull(audioMixerAttributes);
        if (checkAudioSettingsPermission("setPreferredMixerAttributes()")) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                String formatSimple = TextUtils.formatSimple("setPreferredMixerAttributes u/pid:%d/%d attr:%s mixerAttributes:%s portId:%d", new Object[]{Integer.valueOf(callingUid), Integer.valueOf(callingPid), audioAttributes.toString(), audioMixerAttributes.toString(), Integer.valueOf(i)});
                sDeviceLogger.enqueue(new EventLogger.StringEvent(formatSimple).printLog("AS.AudioService"));
                int preferredMixerAttributes = this.mAudioSystem.setPreferredMixerAttributes(audioAttributes, i, callingUid, audioMixerAttributes);
                if (preferredMixerAttributes == 0) {
                    dispatchPreferredMixerAttributesChanged(audioAttributes, i, audioMixerAttributes);
                } else {
                    Log.e("AS.AudioService", TextUtils.formatSimple("Error %d in %s)", new Object[]{Integer.valueOf(preferredMixerAttributes), formatSimple}));
                }
                return preferredMixerAttributes;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return -4;
    }

    public int clearPreferredMixerAttributes(AudioAttributes audioAttributes, int i) {
        Objects.requireNonNull(audioAttributes);
        if (checkAudioSettingsPermission("clearPreferredMixerAttributes()")) {
            int callingUid = Binder.getCallingUid();
            int callingPid = Binder.getCallingPid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                String formatSimple = TextUtils.formatSimple("clearPreferredMixerAttributes u/pid:%d/%d attr:%s", new Object[]{Integer.valueOf(callingUid), Integer.valueOf(callingPid), audioAttributes.toString()});
                sDeviceLogger.enqueue(new EventLogger.StringEvent(formatSimple).printLog("AS.AudioService"));
                int clearPreferredMixerAttributes = this.mAudioSystem.clearPreferredMixerAttributes(audioAttributes, i, callingUid);
                if (clearPreferredMixerAttributes == 0) {
                    dispatchPreferredMixerAttributesChanged(audioAttributes, i, null);
                } else {
                    Log.e("AS.AudioService", TextUtils.formatSimple("Error %d in %s)", new Object[]{Integer.valueOf(clearPreferredMixerAttributes), formatSimple}));
                }
                return clearPreferredMixerAttributes;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
        return -4;
    }

    public void dispatchPreferredMixerAttributesChanged(AudioAttributes audioAttributes, int i, AudioMixerAttributes audioMixerAttributes) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("audio_attributes", audioAttributes);
        bundle.putParcelable("audio_mixer_attributes", audioMixerAttributes);
        sendBundleMsg(this.mAudioHandler, 52, 2, i, 0, null, bundle, 0);
    }

    public void registerPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher iPreferredMixerAttributesDispatcher) {
        if (iPreferredMixerAttributesDispatcher == null) {
            return;
        }
        this.mPrefMixerAttrDispatcher.register(iPreferredMixerAttributesDispatcher);
    }

    public void unregisterPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher iPreferredMixerAttributesDispatcher) {
        if (iPreferredMixerAttributesDispatcher == null) {
            return;
        }
        this.mPrefMixerAttrDispatcher.unregister(iPreferredMixerAttributesDispatcher);
    }

    public void onDispatchPreferredMixerAttributesChanged(Bundle bundle, int i) {
        int beginBroadcast = this.mPrefMixerAttrDispatcher.beginBroadcast();
        AudioAttributes audioAttributes = (AudioAttributes) bundle.getParcelable("audio_attributes", AudioAttributes.class);
        AudioMixerAttributes audioMixerAttributes = (AudioMixerAttributes) bundle.getParcelable("audio_mixer_attributes", AudioMixerAttributes.class);
        for (int i2 = 0; i2 < beginBroadcast; i2++) {
            try {
                this.mPrefMixerAttrDispatcher.getBroadcastItem(i2).dispatchPrefMixerAttributesChanged(audioAttributes, i, audioMixerAttributes);
            } catch (RemoteException e) {
                Log.e("AS.AudioService", "Can't call dispatchPrefMixerAttributesChanged() IPreferredMixerAttributesDispatcher " + this.mPrefMixerAttrDispatcher.getBroadcastItem(i2).asBinder(), e);
            }
        }
        this.mPrefMixerAttrDispatcher.finishBroadcast();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public boolean supportsBluetoothVariableLatency() {
        super.supportsBluetoothVariableLatency_enforcePermission();
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            boolean supportsBluetoothVariableLatency = AudioSystem.supportsBluetoothVariableLatency();
            if (create != null) {
                create.close();
            }
            return supportsBluetoothVariableLatency;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void setBluetoothVariableLatencyEnabled(boolean z) {
        super.setBluetoothVariableLatencyEnabled_enforcePermission();
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            AudioSystem.setBluetoothVariableLatencyEnabled(z);
            if (create != null) {
                create.close();
            }
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public boolean isBluetoothVariableLatencyEnabled() {
        super.isBluetoothVariableLatencyEnabled_enforcePermission();
        SafeCloseable create = ClearCallingIdentityContext.create();
        try {
            boolean isBluetoothVariableLatencyEnabled = AudioSystem.isBluetoothVariableLatencyEnabled();
            if (create != null) {
                create.close();
            }
            return isBluetoothVariableLatencyEnabled;
        } catch (Throwable th) {
            if (create != null) {
                try {
                    create.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    public final void setExtVolumeController(IAudioPolicyCallback iAudioPolicyCallback) {
        if (!this.mContext.getResources().getBoolean(17891697)) {
            Log.e("AS.AudioService", "Cannot set external volume controller: device not set for volume keys handled in PhoneWindowManager");
            return;
        }
        synchronized (this.mExtVolumeControllerLock) {
            IAudioPolicyCallback iAudioPolicyCallback2 = this.mExtVolumeController;
            if (iAudioPolicyCallback2 != null && !iAudioPolicyCallback2.asBinder().pingBinder()) {
                Log.e("AS.AudioService", "Cannot set external volume controller: existing controller");
            }
            this.mExtVolumeController = iAudioPolicyCallback;
        }
    }

    public final void dumpAudioPolicies(PrintWriter printWriter) {
        printWriter.println("\nAudio policies:");
        synchronized (this.mAudioPolicies) {
            for (AudioPolicyProxy audioPolicyProxy : this.mAudioPolicies.values()) {
                printWriter.println(audioPolicyProxy.toLogFriendlyString());
            }
        }
    }

    public final void onDynPolicyMixStateUpdate(String str, int i) {
        synchronized (this.mAudioPolicies) {
            for (AudioPolicyProxy audioPolicyProxy : this.mAudioPolicies.values()) {
                Iterator it = audioPolicyProxy.getMixes().iterator();
                while (it.hasNext()) {
                    if (((AudioMix) it.next()).getRegistration().equals(str)) {
                        try {
                            audioPolicyProxy.mPolicyCallback.notifyMixStateUpdate(str, i);
                        } catch (RemoteException e) {
                            Log.e("AS.AudioService", "Can't call notifyMixStateUpdate() on IAudioPolicyCallback " + audioPolicyProxy.mPolicyCallback.asBinder(), e);
                        }
                        return;
                    }
                }
            }
        }
    }

    public void registerRecordingCallback(IRecordingConfigDispatcher iRecordingConfigDispatcher) {
        this.mRecordMonitor.registerRecordingCallback(iRecordingConfigDispatcher, this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public void unregisterRecordingCallback(IRecordingConfigDispatcher iRecordingConfigDispatcher) {
        this.mRecordMonitor.unregisterRecordingCallback(iRecordingConfigDispatcher);
    }

    public List<AudioRecordingConfiguration> getActiveRecordingConfigurations() {
        return this.mRecordMonitor.getActiveRecordingConfigurations(Binder.getCallingUid() == 1000 || this.mContext.checkCallingPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public int trackRecorder(IBinder iBinder) {
        return this.mRecordMonitor.trackRecorder(iBinder);
    }

    public void recorderEvent(int i, int i2) {
        this.mRecordMonitor.recorderEvent(i, i2);
    }

    public void releaseRecorder(int i) {
        this.mRecordMonitor.releaseRecorder(i);
    }

    public void registerPlaybackCallback(IPlaybackConfigDispatcher iPlaybackConfigDispatcher) {
        this.mPlaybackMonitor.registerPlaybackCallback(iPlaybackConfigDispatcher, this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public void unregisterPlaybackCallback(IPlaybackConfigDispatcher iPlaybackConfigDispatcher) {
        this.mPlaybackMonitor.unregisterPlaybackCallback(iPlaybackConfigDispatcher);
    }

    public List<AudioPlaybackConfiguration> getActivePlaybackConfigurations() {
        return this.mPlaybackMonitor.getActivePlaybackConfigurations(this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") == 0);
    }

    public int trackPlayer(PlayerBase.PlayerIdCard playerIdCard) {
        AudioAttributes audioAttributes;
        if (playerIdCard != null && (audioAttributes = playerIdCard.mAttributes) != null) {
            validateAudioAttributesUsage(audioAttributes);
        }
        return this.mPlaybackMonitor.trackPlayer(playerIdCard);
    }

    public void playerAttributes(int i, AudioAttributes audioAttributes) {
        if (audioAttributes != null) {
            validateAudioAttributesUsage(audioAttributes);
        }
        this.mPlaybackMonitor.playerAttributes(i, audioAttributes, Binder.getCallingUid());
    }

    public void playerSessionId(int i, int i2) {
        if (i2 <= 0) {
            throw new IllegalArgumentException("invalid session Id " + i2);
        }
        this.mPlaybackMonitor.playerSessionId(i, i2, Binder.getCallingUid());
    }

    public void playerEvent(int i, int i2, int i3) {
        this.mPlaybackMonitor.playerEvent(i, i2, i3, Binder.getCallingUid());
    }

    public void portEvent(int i, int i2, PersistableBundle persistableBundle) {
        this.mPlaybackMonitor.portEvent(i, i2, persistableBundle, Binder.getCallingUid());
    }

    public void playerHasOpPlayAudio(int i, boolean z) {
        this.mPlaybackMonitor.playerHasOpPlayAudio(i, z, Binder.getCallingUid());
    }

    public void releasePlayer(int i) {
        this.mPlaybackMonitor.releasePlayer(i, Binder.getCallingUid());
    }

    public int setAllowedCapturePolicy(int i) {
        int allowedCapturePolicy;
        int callingUid = Binder.getCallingUid();
        int capturePolicyToFlags = AudioAttributes.capturePolicyToFlags(i, 0);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            synchronized (this.mPlaybackMonitor) {
                allowedCapturePolicy = this.mAudioSystem.setAllowedCapturePolicy(callingUid, capturePolicyToFlags);
                if (allowedCapturePolicy == 0) {
                    this.mPlaybackMonitor.setAllowedCapturePolicy(callingUid, i);
                }
            }
            return allowedCapturePolicy;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getAllowedCapturePolicy() {
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mPlaybackMonitor.getAllowedCapturePolicy(callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* loaded from: classes.dex */
    public static final class AudioDeviceArray {
        public final String[] mDeviceAddresses;
        public final int[] mDeviceTypes;

        public AudioDeviceArray(int[] iArr, String[] strArr) {
            this.mDeviceTypes = iArr;
            this.mDeviceAddresses = strArr;
        }
    }

    /* loaded from: classes.dex */
    public class AudioPolicyProxy extends AudioPolicyConfig implements IBinder.DeathRecipient {
        public int mFocusDuckBehavior;
        public final boolean mHasFocusListener;
        public boolean mIsFocusPolicy;
        public boolean mIsTestFocusPolicy;
        public final boolean mIsVolumeController;
        public final IAudioPolicyCallback mPolicyCallback;
        public final IMediaProjection mProjection;
        public UnregisterOnStopCallback mProjectionCallback;
        public final HashMap<Integer, AudioDeviceArray> mUidDeviceAffinities;
        public final HashMap<Integer, AudioDeviceArray> mUserIdDeviceAffinities;

        /* loaded from: classes.dex */
        public final class UnregisterOnStopCallback extends IMediaProjectionCallback.Stub {
            public void onCapturedContentResize(int i, int i2) {
            }

            public void onCapturedContentVisibilityChanged(boolean z) {
            }

            public UnregisterOnStopCallback() {
            }

            public void onStop() {
                AudioPolicyProxy audioPolicyProxy = AudioPolicyProxy.this;
                AudioService.this.unregisterAudioPolicyAsync(audioPolicyProxy.mPolicyCallback);
            }
        }

        public AudioPolicyProxy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback, boolean z, boolean z2, boolean z3, boolean z4, IMediaProjection iMediaProjection) {
            super(audioPolicyConfig);
            this.mUidDeviceAffinities = new HashMap<>();
            this.mUserIdDeviceAffinities = new HashMap<>();
            this.mFocusDuckBehavior = 0;
            this.mIsFocusPolicy = false;
            this.mIsTestFocusPolicy = false;
            StringBuilder sb = new StringBuilder();
            sb.append(audioPolicyConfig.hashCode());
            sb.append(":ap:");
            int i = AudioService.this.mAudioPolicyCounter;
            AudioService.this.mAudioPolicyCounter = i + 1;
            sb.append(i);
            setRegistration(new String(sb.toString()));
            this.mPolicyCallback = iAudioPolicyCallback;
            this.mHasFocusListener = z;
            this.mIsVolumeController = z4;
            this.mProjection = iMediaProjection;
            if (z) {
                AudioService.this.mMediaFocusControl.addFocusFollower(iAudioPolicyCallback);
                if (z2) {
                    this.mIsFocusPolicy = true;
                    this.mIsTestFocusPolicy = z3;
                    AudioService.this.mMediaFocusControl.setFocusPolicy(iAudioPolicyCallback, this.mIsTestFocusPolicy);
                }
            }
            if (z4) {
                AudioService.this.setExtVolumeController(iAudioPolicyCallback);
            }
            if (iMediaProjection != null) {
                UnregisterOnStopCallback unregisterOnStopCallback = new UnregisterOnStopCallback();
                this.mProjectionCallback = unregisterOnStopCallback;
                try {
                    iMediaProjection.registerCallback(unregisterOnStopCallback);
                } catch (RemoteException e) {
                    release();
                    throw new IllegalStateException("MediaProjection callback registration failed, could not link to " + iMediaProjection + " binder death", e);
                }
            }
            int connectMixes = connectMixes();
            if (connectMixes == 0) {
                return;
            }
            release();
            throw new IllegalStateException("Could not connect mix, error: " + connectMixes);
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            EventLogger eventLogger = AudioService.this.mDynPolicyLogger;
            eventLogger.enqueue(new EventLogger.StringEvent("AudioPolicy " + this.mPolicyCallback.asBinder() + " died").printLog("AudioPolicyProxy"));
            ArrayList arrayList = new ArrayList();
            Iterator it = ((AudioPolicyConfig) this).mMixes.iterator();
            while (it.hasNext()) {
                arrayList.add(((AudioMix) it.next()).getRegistration());
            }
            AudioService.this.onPolicyClientDeath(arrayList);
            release();
        }

        public String getRegistrationId() {
            return getRegistration();
        }

        public void release() {
            if (this.mIsFocusPolicy) {
                AudioService.this.mMediaFocusControl.unsetFocusPolicy(this.mPolicyCallback, this.mIsTestFocusPolicy);
            }
            if (this.mFocusDuckBehavior == 1) {
                AudioService.this.mMediaFocusControl.setDuckingInExtPolicyAvailable(false);
            }
            if (this.mHasFocusListener) {
                AudioService.this.mMediaFocusControl.removeFocusFollower(this.mPolicyCallback);
            }
            UnregisterOnStopCallback unregisterOnStopCallback = this.mProjectionCallback;
            if (unregisterOnStopCallback != null) {
                try {
                    this.mProjection.unregisterCallback(unregisterOnStopCallback);
                } catch (RemoteException unused) {
                    Log.e("AudioPolicyProxy", "Fail to unregister Audiopolicy callback from MediaProjection");
                }
            }
            if (this.mIsVolumeController) {
                synchronized (AudioService.this.mExtVolumeControllerLock) {
                    AudioService.this.mExtVolumeController = null;
                }
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                AudioService.this.mAudioSystem.registerPolicyMixes(((AudioPolicyConfig) this).mMixes, false);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                synchronized (AudioService.this.mAudioPolicies) {
                    AudioService.this.mAudioPolicies.remove(this.mPolicyCallback.asBinder());
                }
                try {
                    this.mPolicyCallback.notifyUnregistration();
                } catch (RemoteException unused2) {
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                throw th;
            }
        }

        public boolean hasMixAffectingUsage(int i, int i2) {
            Iterator it = ((AudioPolicyConfig) this).mMixes.iterator();
            while (it.hasNext()) {
                AudioMix audioMix = (AudioMix) it.next();
                if (audioMix.isAffectingUsage(i) && (audioMix.getRouteFlags() & i2) != i2) {
                    return true;
                }
            }
            return false;
        }

        public boolean hasMixRoutedToDevices(int[] iArr, String[] strArr) {
            int i = 0;
            while (true) {
                boolean z = true;
                if (i >= iArr.length) {
                    return true;
                }
                Iterator it = ((AudioPolicyConfig) this).mMixes.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    } else if (((AudioMix) it.next()).isRoutedToDevice(iArr[i], strArr[i])) {
                        break;
                    }
                }
                if (!z) {
                    return false;
                }
                i++;
            }
        }

        public int addMixes(ArrayList<AudioMix> arrayList) {
            int registerPolicyMixes;
            synchronized (((AudioPolicyConfig) this).mMixes) {
                AudioService.this.mAudioSystem.registerPolicyMixes(((AudioPolicyConfig) this).mMixes, false);
                add(arrayList);
                registerPolicyMixes = AudioService.this.mAudioSystem.registerPolicyMixes(((AudioPolicyConfig) this).mMixes, true);
            }
            return registerPolicyMixes;
        }

        public int removeMixes(ArrayList<AudioMix> arrayList) {
            int registerPolicyMixes;
            synchronized (((AudioPolicyConfig) this).mMixes) {
                AudioService.this.mAudioSystem.registerPolicyMixes(((AudioPolicyConfig) this).mMixes, false);
                remove(arrayList);
                registerPolicyMixes = AudioService.this.mAudioSystem.registerPolicyMixes(((AudioPolicyConfig) this).mMixes, true);
            }
            return registerPolicyMixes;
        }

        public int connectMixes() {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AudioService.this.mAudioSystem.registerPolicyMixes(((AudioPolicyConfig) this).mMixes, true);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int setUidDeviceAffinities(int i, int[] iArr, String[] strArr) {
            Integer num = new Integer(i);
            if (this.mUidDeviceAffinities.remove(num) != null && removeUidDeviceAffinitiesFromSystem(i) != 0) {
                Log.e("AudioPolicyProxy", "AudioSystem. removeUidDeviceAffinities(" + i + ") failed,  cannot call AudioSystem.setUidDeviceAffinities");
                return -1;
            }
            AudioDeviceArray audioDeviceArray = new AudioDeviceArray(iArr, strArr);
            if (setUidDeviceAffinitiesOnSystem(i, audioDeviceArray) == 0) {
                this.mUidDeviceAffinities.put(num, audioDeviceArray);
                return 0;
            }
            Log.e("AudioPolicyProxy", "AudioSystem. setUidDeviceAffinities(" + i + ") failed");
            return -1;
        }

        public int removeUidDeviceAffinities(int i) {
            if (this.mUidDeviceAffinities.remove(new Integer(i)) == null || removeUidDeviceAffinitiesFromSystem(i) != 0) {
                Log.e("AudioPolicyProxy", "AudioSystem. removeUidDeviceAffinities failed");
                return -1;
            }
            return 0;
        }

        public final int removeUidDeviceAffinitiesFromSystem(int i) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AudioService.this.mAudioSystem.removeUidDeviceAffinities(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public final int setUidDeviceAffinitiesOnSystem(int i, AudioDeviceArray audioDeviceArray) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AudioService.this.mAudioSystem.setUidDeviceAffinities(i, audioDeviceArray.mDeviceTypes, audioDeviceArray.mDeviceAddresses);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int setUserIdDeviceAffinities(int i, int[] iArr, String[] strArr) {
            Integer num = new Integer(i);
            if (this.mUserIdDeviceAffinities.remove(num) != null && removeUserIdDeviceAffinitiesFromSystem(i) != 0) {
                Log.e("AudioPolicyProxy", "AudioSystem. removeUserIdDeviceAffinities(" + num + ") failed,  cannot call AudioSystem.setUserIdDeviceAffinities");
                return -1;
            }
            AudioDeviceArray audioDeviceArray = new AudioDeviceArray(iArr, strArr);
            if (setUserIdDeviceAffinitiesOnSystem(i, audioDeviceArray) == 0) {
                this.mUserIdDeviceAffinities.put(num, audioDeviceArray);
                return 0;
            }
            Log.e("AudioPolicyProxy", "AudioSystem.setUserIdDeviceAffinities(" + i + ") failed");
            return -1;
        }

        public int removeUserIdDeviceAffinities(int i) {
            if (this.mUserIdDeviceAffinities.remove(new Integer(i)) == null || removeUserIdDeviceAffinitiesFromSystem(i) != 0) {
                Log.e("AudioPolicyProxy", "AudioSystem.removeUserIdDeviceAffinities failed");
                return -1;
            }
            return 0;
        }

        public final int removeUserIdDeviceAffinitiesFromSystem(int i) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AudioService.this.mAudioSystem.removeUserIdDeviceAffinities(i);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public final int setUserIdDeviceAffinitiesOnSystem(int i, AudioDeviceArray audioDeviceArray) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return AudioService.this.mAudioSystem.setUserIdDeviceAffinities(i, audioDeviceArray.mDeviceTypes, audioDeviceArray.mDeviceAddresses);
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public int setupDeviceAffinities() {
            for (Map.Entry<Integer, AudioDeviceArray> entry : this.mUidDeviceAffinities.entrySet()) {
                int removeUidDeviceAffinitiesFromSystem = removeUidDeviceAffinitiesFromSystem(entry.getKey().intValue());
                if (removeUidDeviceAffinitiesFromSystem != 0) {
                    Log.e("AudioPolicyProxy", "setupDeviceAffinities failed to remove device affinity for uid " + entry.getKey());
                    return removeUidDeviceAffinitiesFromSystem;
                }
                int uidDeviceAffinitiesOnSystem = setUidDeviceAffinitiesOnSystem(entry.getKey().intValue(), entry.getValue());
                if (uidDeviceAffinitiesOnSystem != 0) {
                    Log.e("AudioPolicyProxy", "setupDeviceAffinities failed to set device affinity for uid " + entry.getKey());
                    return uidDeviceAffinitiesOnSystem;
                }
            }
            for (Map.Entry<Integer, AudioDeviceArray> entry2 : this.mUserIdDeviceAffinities.entrySet()) {
                int removeUserIdDeviceAffinitiesFromSystem = removeUserIdDeviceAffinitiesFromSystem(entry2.getKey().intValue());
                if (removeUserIdDeviceAffinitiesFromSystem != 0) {
                    Log.e("AudioPolicyProxy", "setupDeviceAffinities failed to remove device affinity for userId " + entry2.getKey());
                    return removeUserIdDeviceAffinitiesFromSystem;
                }
                int userIdDeviceAffinitiesOnSystem = setUserIdDeviceAffinitiesOnSystem(entry2.getKey().intValue(), entry2.getValue());
                if (userIdDeviceAffinitiesOnSystem != 0) {
                    Log.e("AudioPolicyProxy", "setupDeviceAffinities failed to set device affinity for userId " + entry2.getKey());
                    return userIdDeviceAffinitiesOnSystem;
                }
            }
            return 0;
        }

        public String toLogFriendlyString() {
            String str = (((((super.toLogFriendlyString() + " Uid Device Affinities:\n") + logFriendlyAttributeDeviceArrayMap("Uid", this.mUidDeviceAffinities, "     ")) + " UserId Device Affinities:\n") + logFriendlyAttributeDeviceArrayMap("UserId", this.mUserIdDeviceAffinities, "     ")) + " Proxy:\n") + "   is focus policy= " + this.mIsFocusPolicy + "\n";
            if (this.mIsFocusPolicy) {
                str = ((str + "     focus duck behaviour= " + this.mFocusDuckBehavior + "\n") + "     is test focus policy= " + this.mIsTestFocusPolicy + "\n") + "     has focus listener= " + this.mHasFocusListener + "\n";
            }
            return str + "   media projection= " + this.mProjection + "\n";
        }

        public final String logFriendlyAttributeDeviceArrayMap(String str, Map<Integer, AudioDeviceArray> map, String str2) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, AudioDeviceArray> entry : map.entrySet()) {
                sb.append(str2);
                sb.append(str);
                sb.append(": ");
                sb.append(entry.getKey());
                sb.append("\n");
                AudioDeviceArray value = entry.getValue();
                String str3 = str2 + "   ";
                for (int i = 0; i < value.mDeviceTypes.length; i++) {
                    sb.append(str3);
                    sb.append("Type: 0x");
                    sb.append(Integer.toHexString(value.mDeviceTypes[i]));
                    sb.append(" Address: ");
                    sb.append(value.mDeviceAddresses[i]);
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
    }

    public int dispatchFocusChange(AudioFocusInfo audioFocusInfo, int i, IAudioPolicyCallback iAudioPolicyCallback) {
        int dispatchFocusChange;
        if (audioFocusInfo != null) {
            if (iAudioPolicyCallback == null) {
                throw new IllegalArgumentException("Illegal null AudioPolicy callback");
            }
            synchronized (this.mAudioPolicies) {
                if (!this.mAudioPolicies.containsKey(iAudioPolicyCallback.asBinder())) {
                    throw new IllegalStateException("Unregistered AudioPolicy for focus dispatch");
                }
                dispatchFocusChange = this.mMediaFocusControl.dispatchFocusChange(audioFocusInfo, i);
            }
            return dispatchFocusChange;
        }
        throw new IllegalArgumentException("Illegal null AudioFocusInfo");
    }

    public void setFocusRequestResultFromExtPolicy(AudioFocusInfo audioFocusInfo, int i, IAudioPolicyCallback iAudioPolicyCallback) {
        if (audioFocusInfo == null) {
            throw new IllegalArgumentException("Illegal null AudioFocusInfo");
        }
        if (iAudioPolicyCallback == null) {
            throw new IllegalArgumentException("Illegal null AudioPolicy callback");
        }
        synchronized (this.mAudioPolicies) {
            if (!this.mAudioPolicies.containsKey(iAudioPolicyCallback.asBinder())) {
                throw new IllegalStateException("Unregistered AudioPolicy for external focus");
            }
            this.mMediaFocusControl.setFocusRequestResultFromExtPolicy(audioFocusInfo, i);
        }
    }

    /* loaded from: classes.dex */
    public class AsdProxy implements IBinder.DeathRecipient {
        public final IAudioServerStateDispatcher mAsd;

        public AsdProxy(IAudioServerStateDispatcher iAudioServerStateDispatcher) {
            this.mAsd = iAudioServerStateDispatcher;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mAudioServerStateListeners) {
                AudioService.this.mAudioServerStateListeners.remove(this.mAsd.asBinder());
            }
        }

        public IAudioServerStateDispatcher callback() {
            return this.mAsd;
        }
    }

    public final void checkMonitorAudioServerStatePermission() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_PHONE_STATE") != 0 && this.mContext.checkCallingOrSelfPermission("android.permission.MODIFY_AUDIO_ROUTING") != 0) {
            throw new SecurityException("Not allowed to monitor audioserver state");
        }
    }

    public void registerAudioServerStateDispatcher(IAudioServerStateDispatcher iAudioServerStateDispatcher) {
        checkMonitorAudioServerStatePermission();
        synchronized (this.mAudioServerStateListeners) {
            if (this.mAudioServerStateListeners.containsKey(iAudioServerStateDispatcher.asBinder())) {
                Slog.w("AS.AudioService", "Cannot re-register audio server state dispatcher");
                return;
            }
            AsdProxy asdProxy = new AsdProxy(iAudioServerStateDispatcher);
            try {
                iAudioServerStateDispatcher.asBinder().linkToDeath(asdProxy, 0);
            } catch (RemoteException unused) {
            }
            this.mAudioServerStateListeners.put(iAudioServerStateDispatcher.asBinder(), asdProxy);
        }
    }

    public void unregisterAudioServerStateDispatcher(IAudioServerStateDispatcher iAudioServerStateDispatcher) {
        checkMonitorAudioServerStatePermission();
        synchronized (this.mAudioServerStateListeners) {
            AsdProxy remove = this.mAudioServerStateListeners.remove(iAudioServerStateDispatcher.asBinder());
            if (remove == null) {
                Slog.w("AS.AudioService", "Trying to unregister unknown audioserver state dispatcher for pid " + Binder.getCallingPid() + " / uid " + Binder.getCallingUid());
                return;
            }
            iAudioServerStateDispatcher.asBinder().unlinkToDeath(remove, 0);
        }
    }

    public boolean isAudioServerRunning() {
        checkMonitorAudioServerStatePermission();
        return AudioSystem.checkAudioFlinger() == 0;
    }

    public final Set<Integer> getAudioHalPids() {
        String str;
        try {
            ArrayList<IServiceManager.InstanceDebugInfo> debugDump = IServiceManager.getService().debugDump();
            HashSet hashSet = new HashSet();
            Iterator<IServiceManager.InstanceDebugInfo> it = debugDump.iterator();
            while (it.hasNext()) {
                IServiceManager.InstanceDebugInfo next = it.next();
                if (next.pid != -1 && (str = next.interfaceName) != null && str.startsWith("android.hardware.audio")) {
                    hashSet.add(Integer.valueOf(next.pid));
                }
            }
            return hashSet;
        } catch (RemoteException | RuntimeException unused) {
            return new HashSet();
        }
    }

    public final void updateAudioHalPids() {
        Set<Integer> audioHalPids = getAudioHalPids();
        if (audioHalPids.isEmpty()) {
            Slog.w("AS.AudioService", "Could not retrieve audio HAL service pids");
        } else {
            AudioSystem.setAudioHalPids(audioHalPids.stream().mapToInt(new AudioService$$ExternalSyntheticLambda0()).toArray());
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void setMultiAudioFocusEnabled(boolean z) {
        super.setMultiAudioFocusEnabled_enforcePermission();
        MediaFocusControl mediaFocusControl = this.mMediaFocusControl;
        if (mediaFocusControl == null || mediaFocusControl.getMultiAudioFocusEnabled() == z) {
            return;
        }
        this.mMediaFocusControl.updateMultiAudioFocus(z);
        if (z) {
            return;
        }
        this.mDeviceBroker.postBroadcastBecomingNoisy();
    }

    public boolean setAdditionalOutputDeviceDelay(AudioDeviceAttributes audioDeviceAttributes, long j) {
        Objects.requireNonNull(audioDeviceAttributes, "device must not be null");
        enforceModifyAudioRoutingPermission();
        String str = "additional_output_device_delay=" + audioDeviceAttributes.getInternalType() + "," + audioDeviceAttributes.getAddress();
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(",");
        sb.append(j);
        return this.mRestorableParameters.setParameters(str, sb.toString()) == 0;
    }

    public long getAdditionalOutputDeviceDelay(AudioDeviceAttributes audioDeviceAttributes) {
        Objects.requireNonNull(audioDeviceAttributes, "device must not be null");
        try {
            return Long.parseLong(AudioSystem.getParameters("additional_output_device_delay=" + audioDeviceAttributes.getInternalType() + "," + audioDeviceAttributes.getAddress()).substring(31));
        } catch (NullPointerException unused) {
            return 0L;
        }
    }

    public long getMaxAdditionalOutputDeviceDelay(AudioDeviceAttributes audioDeviceAttributes) {
        Objects.requireNonNull(audioDeviceAttributes, "device must not be null");
        try {
            return Long.parseLong(AudioSystem.getParameters("max_additional_output_device_delay=" + audioDeviceAttributes.getInternalType() + "," + audioDeviceAttributes.getAddress()).substring(35));
        } catch (NullPointerException unused) {
            return 0L;
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void addAssistantServicesUids(int[] iArr) {
        super.addAssistantServicesUids_enforcePermission();
        Objects.requireNonNull(iArr);
        synchronized (this.mSettingsLock) {
            addAssistantServiceUidsLocked(iArr);
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void removeAssistantServicesUids(int[] iArr) {
        super.removeAssistantServicesUids_enforcePermission();
        Objects.requireNonNull(iArr);
        synchronized (this.mSettingsLock) {
            removeAssistantServiceUidsLocked(iArr);
        }
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int[] getAssistantServicesUids() {
        int[] array;
        super.getAssistantServicesUids_enforcePermission();
        synchronized (this.mSettingsLock) {
            array = this.mAssistantUids.stream().mapToInt(new AudioService$$ExternalSyntheticLambda0()).toArray();
        }
        return array;
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public void setActiveAssistantServiceUids(int[] iArr) {
        super.setActiveAssistantServiceUids_enforcePermission();
        Objects.requireNonNull(iArr);
        synchronized (this.mSettingsLock) {
            this.mActiveAssistantServiceUids = iArr;
        }
        updateActiveAssistantServiceUids();
    }

    @EnforcePermission("android.permission.MODIFY_AUDIO_ROUTING")
    public int[] getActiveAssistantServiceUids() {
        int[] iArr;
        super.getActiveAssistantServiceUids_enforcePermission();
        synchronized (this.mSettingsLock) {
            iArr = (int[]) this.mActiveAssistantServiceUids.clone();
        }
        return iArr;
    }

    public UUID getDeviceSensorUuid(AudioDeviceAttributes audioDeviceAttributes) {
        return this.mDeviceBroker.getDeviceSensorUuid(audioDeviceAttributes);
    }

    public final boolean isFixedVolumeDevice(int i) {
        if (i == 32768 && this.mRecordMonitor.isLegacyRemoteSubmixActive()) {
            return false;
        }
        return this.mFixedVolumeDevices.contains(Integer.valueOf(i));
    }

    public final boolean isFullVolumeDevice(int i) {
        if (i == 32768 && this.mRecordMonitor.isLegacyRemoteSubmixActive()) {
            return false;
        }
        return this.mFullVolumeDevices.contains(Integer.valueOf(i));
    }

    public final boolean isAbsoluteVolumeDevice(int i) {
        return this.mAbsoluteVolumeDeviceInfoMap.containsKey(Integer.valueOf(i));
    }

    public final boolean isA2dpAbsoluteVolumeDevice(int i) {
        return this.mAvrcpAbsVolSupported && AudioSystem.DEVICE_OUT_ALL_A2DP_SET.contains(Integer.valueOf(i));
    }

    public static String getSettingsNameForDeviceVolumeBehavior(int i) {
        return "AudioService_DeviceVolumeBehavior_" + AudioSystem.getOutputDeviceName(i);
    }

    public final void persistDeviceVolumeBehavior(int i, int i2) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mSettings.putSystemIntForUser(this.mContentResolver, getSettingsNameForDeviceVolumeBehavior(i), i2, -2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final int retrieveStoredDeviceVolumeBehavior(int i) {
        return this.mSettings.getSystemIntForUser(this.mContentResolver, getSettingsNameForDeviceVolumeBehavior(i), -1, -2);
    }

    public final void restoreDeviceVolumeBehavior() {
        for (Integer num : AudioSystem.DEVICE_OUT_ALL_SET) {
            int intValue = num.intValue();
            int retrieveStoredDeviceVolumeBehavior = retrieveStoredDeviceVolumeBehavior(intValue);
            if (retrieveStoredDeviceVolumeBehavior != -1) {
                setDeviceVolumeBehaviorInternal(new AudioDeviceAttributes(intValue, ""), retrieveStoredDeviceVolumeBehavior, "AudioService.restoreDeviceVolumeBehavior()");
            }
        }
    }

    public final boolean hasDeviceVolumeBehavior(int i) {
        return retrieveStoredDeviceVolumeBehavior(i) != -1;
    }

    public final boolean addAudioSystemDeviceOutToFixedVolumeDevices(int i) {
        return this.mFixedVolumeDevices.add(Integer.valueOf(i));
    }

    public final boolean removeAudioSystemDeviceOutFromFixedVolumeDevices(int i) {
        return this.mFixedVolumeDevices.remove(Integer.valueOf(i));
    }

    public final boolean addAudioSystemDeviceOutToFullVolumeDevices(int i) {
        return this.mFullVolumeDevices.add(Integer.valueOf(i));
    }

    public final boolean removeAudioSystemDeviceOutFromFullVolumeDevices(int i) {
        return this.mFullVolumeDevices.remove(Integer.valueOf(i));
    }

    public final void addAudioSystemDeviceOutToAbsVolumeDevices(int i, AbsoluteVolumeDeviceInfo absoluteVolumeDeviceInfo) {
        this.mAbsoluteVolumeDeviceInfoMap.put(Integer.valueOf(i), absoluteVolumeDeviceInfo);
    }

    public final AbsoluteVolumeDeviceInfo removeAudioSystemDeviceOutFromAbsVolumeDevices(int i) {
        return this.mAbsoluteVolumeDeviceInfoMap.remove(Integer.valueOf(i));
    }

    public final boolean checkNoteAppOp(int i, int i2, String str, String str2) {
        try {
            return this.mAppOps.noteOp(i, i2, str, str2, (String) null) == 0;
        } catch (Exception e) {
            Log.e("AS.AudioService", "Error noting op:" + i + " on uid:" + i2 + " for package:" + str, e);
            return false;
        }
    }
}
