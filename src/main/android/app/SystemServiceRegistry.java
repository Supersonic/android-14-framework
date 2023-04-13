package android.app;

import android.accounts.AccountManager;
import android.accounts.IAccountManager;
import android.adservices.AdServicesFrameworkInitializer;
import android.annotation.SystemApi;
import android.app.IAlarmManager;
import android.app.IGrammaticalInflectionManager;
import android.app.ILocaleManager;
import android.app.IWallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.app.admin.IDevicePolicyManager;
import android.app.ambientcontext.AmbientContextManager;
import android.app.ambientcontext.IAmbientContextManager;
import android.app.appsearch.AppSearchManagerFrameworkInitializer;
import android.app.blob.BlobStoreManagerFrameworkInitializer;
import android.app.contentsuggestions.ContentSuggestionsManager;
import android.app.contentsuggestions.IContentSuggestionsManager;
import android.app.job.JobSchedulerFrameworkInitializer;
import android.app.people.PeopleManager;
import android.app.prediction.AppPredictionManager;
import android.app.role.RoleFrameworkInitializer;
import android.app.sdksandbox.SdkSandboxManagerFrameworkInitializer;
import android.app.search.SearchUiManager;
import android.app.slice.SliceManager;
import android.app.smartspace.SmartspaceManager;
import android.app.time.TimeManager;
import android.app.timedetector.TimeDetector;
import android.app.timedetector.TimeDetectorImpl;
import android.app.timezonedetector.TimeZoneDetector;
import android.app.timezonedetector.TimeZoneDetectorImpl;
import android.app.trust.TrustManager;
import android.app.usage.IStorageStatsManager;
import android.app.usage.IUsageStatsManager;
import android.app.usage.StorageStatsManager;
import android.app.usage.UsageStatsManager;
import android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager;
import android.app.wallpapereffectsgeneration.WallpaperEffectsGenerationManager;
import android.app.wearable.IWearableSensingManager;
import android.app.wearable.WearableSensingManager;
import android.apphibernation.AppHibernationManager;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothFrameworkInitializer;
import android.companion.CompanionDeviceManager;
import android.companion.ICompanionDeviceManager;
import android.companion.virtual.IVirtualDeviceManager;
import android.companion.virtual.VirtualDeviceManager;
import android.compat.Compatibility;
import android.content.ClipboardManager;
import android.content.ContentCaptureOptions;
import android.content.Context;
import android.content.IRestrictionsManager;
import android.content.RestrictionsManager;
import android.content.integrity.AppIntegrityManager;
import android.content.integrity.IAppIntegrityManager;
import android.content.p000om.IOverlayManager;
import android.content.p000om.OverlayManager;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.CrossProfileApps;
import android.content.p001pm.DataLoaderManager;
import android.content.p001pm.ICrossProfileApps;
import android.content.p001pm.IDataLoaderManager;
import android.content.p001pm.IShortcutService;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ShortcutManager;
import android.content.p001pm.verify.domain.DomainVerificationManager;
import android.content.p001pm.verify.domain.IDomainVerificationManager;
import android.content.res.Resources;
import android.content.rollback.RollbackManagerFrameworkInitializer;
import android.credentials.CredentialManager;
import android.credentials.ICredentialManager;
import android.debug.AdbManager;
import android.debug.IAdbManager;
import android.devicelock.DeviceLockFrameworkInitializer;
import android.graphics.fonts.FontManager;
import android.hardware.ConsumerIrManager;
import android.hardware.ISerialManager;
import android.hardware.SensorManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.SerialManager;
import android.hardware.SystemSensorManager;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.IAuthService;
import android.hardware.camera2.CameraManager;
import android.hardware.devicestate.DeviceStateManager;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManager;
import android.hardware.face.FaceManager;
import android.hardware.face.IFaceService;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.input.InputManager;
import android.hardware.iris.IIrisService;
import android.hardware.iris.IrisManager;
import android.hardware.lights.LightsManager;
import android.hardware.lights.SystemLightsManager;
import android.hardware.location.ContextHubManager;
import android.hardware.radio.RadioManager;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbManager;
import android.health.connect.HealthServicesInitializer;
import android.location.CountryDetector;
import android.location.ICountryDetector;
import android.location.ILocationManager;
import android.location.LocationManager;
import android.media.AudioDeviceVolumeManager;
import android.media.AudioManager;
import android.media.MediaFrameworkInitializer;
import android.media.MediaFrameworkPlatformInitializer;
import android.media.MediaRouter;
import android.media.metrics.IMediaMetricsManager;
import android.media.metrics.MediaMetricsManager;
import android.media.midi.IMidiManager;
import android.media.midi.MidiManager;
import android.media.musicrecognition.IMusicRecognitionManager;
import android.media.musicrecognition.MusicRecognitionManager;
import android.media.p007tv.ITvInputManager;
import android.media.p007tv.TvInputManager;
import android.media.p007tv.interactive.ITvInteractiveAppManager;
import android.media.p007tv.interactive.TvInteractiveAppManager;
import android.media.p007tv.tunerresourcemanager.ITunerResourceManager;
import android.media.p007tv.tunerresourcemanager.TunerResourceManager;
import android.media.projection.MediaProjectionManager;
import android.media.soundtrigger.SoundTriggerManager;
import android.nearby.NearbyFrameworkInitializer;
import android.net.ConnectivityFrameworkInitializer;
import android.net.ConnectivityFrameworkInitializerTiramisu;
import android.net.INetworkPolicyManager;
import android.net.IPacProxyManager;
import android.net.IVpnManager;
import android.net.NetworkPolicyManager;
import android.net.NetworkScoreManager;
import android.net.NetworkWatchlistManager;
import android.net.PacProxyManager;
import android.net.TetheringManager;
import android.net.VpnManager;
import android.net.vcn.IVcnManagementService;
import android.net.vcn.VcnManager;
import android.net.wifi.WifiFrameworkInitializer;
import android.net.wifi.nl80211.WifiNl80211Manager;
import android.net.wifi.sharedconnectivity.app.SharedConnectivityManager;
import android.nfc.NfcFrameworkInitializer;
import android.ondevicepersonalization.OnDevicePersonalizationFrameworkInitializer;
import android.os.StatsFrameworkInitializer;
import android.p008os.BatteryManager;
import android.p008os.BatteryStatsManager;
import android.p008os.BugreportManager;
import android.p008os.DropBoxManager;
import android.p008os.HardwarePropertiesManager;
import android.p008os.IBatteryPropertiesRegistrar;
import android.p008os.IBinder;
import android.p008os.IDumpstate;
import android.p008os.IHardwarePropertiesManager;
import android.p008os.IPowerManager;
import android.p008os.IRecoverySystem;
import android.p008os.ISystemUpdateManager;
import android.p008os.IThermalService;
import android.p008os.IUserManager;
import android.p008os.IncidentManager;
import android.p008os.PerformanceHintManager;
import android.p008os.PermissionEnforcer;
import android.p008os.PowerManager;
import android.p008os.Process;
import android.p008os.RecoverySystem;
import android.p008os.ServiceManager;
import android.p008os.SystemConfigManager;
import android.p008os.SystemUpdateManager;
import android.p008os.SystemVibrator;
import android.p008os.SystemVibratorManager;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.p008os.Vibrator;
import android.p008os.VibratorManager;
import android.p008os.health.SystemHealthManager;
import android.p008os.image.DynamicSystemManager;
import android.p008os.image.IDynamicSystemService;
import android.p008os.incremental.IIncrementalService;
import android.p008os.incremental.IncrementalManager;
import android.p008os.storage.StorageManager;
import android.permission.LegacyPermissionManager;
import android.permission.PermissionCheckerManager;
import android.permission.PermissionControllerManager;
import android.permission.PermissionManager;
import android.print.IPrintManager;
import android.print.PrintManager;
import android.safetycenter.SafetyCenterFrameworkInitializer;
import android.scheduling.SchedulingFrameworkInitializer;
import android.security.FileIntegrityManager;
import android.security.IFileIntegrityService;
import android.security.attestationverification.AttestationVerificationManager;
import android.security.attestationverification.IAttestationVerificationManagerService;
import android.service.oemlock.IOemLockService;
import android.service.oemlock.OemLockManager;
import android.service.p011vr.IVrManager;
import android.service.persistentdata.IPersistentDataBlockService;
import android.service.persistentdata.PersistentDataBlockManager;
import android.system.virtualmachine.VirtualizationFrameworkInitializer;
import android.telecom.TelecomManager;
import android.telephony.MmsManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.telephony.TelephonyRegistryManager;
import android.transparency.BinaryTransparencyManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.uwb.UwbFrameworkInitializer;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.CaptioningManager;
import android.view.autofill.AutofillManager;
import android.view.autofill.IAutoFillManager;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.IContentCaptureManager;
import android.view.displayhash.DisplayHashManager;
import android.view.inputmethod.InputMethodManager;
import android.view.selectiontoolbar.ISelectionToolbarManager;
import android.view.selectiontoolbar.SelectionToolbarManager;
import android.view.textclassifier.TextClassificationManager;
import android.view.textservice.TextServicesManager;
import android.view.translation.ITranslationManager;
import android.view.translation.TranslationManager;
import android.view.translation.UiTranslationManager;
import com.android.internal.C4057R;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.ISoundTriggerService;
import com.android.internal.appwidget.IAppWidgetService;
import com.android.internal.graphics.fonts.IFontManager;
import com.android.internal.net.INetworkWatchlistManager;
import com.android.internal.p028os.IBinaryTransparencyService;
import com.android.internal.p028os.IDropBoxManagerService;
import com.android.internal.policy.PhoneLayoutInflater;
import com.android.internal.util.Preconditions;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
@SystemApi
/* loaded from: classes.dex */
public final class SystemServiceRegistry {
    private static final Map<String, String> SYSTEM_SERVICE_CLASS_NAMES;
    private static final Map<String, ServiceFetcher<?>> SYSTEM_SERVICE_FETCHERS;
    private static final Map<Class<?>, String> SYSTEM_SERVICE_NAMES;
    private static final String TAG = "SystemServiceRegistry";
    public static boolean sEnableServiceNotFoundWtf = false;
    private static volatile boolean sInitializing;
    private static int sServiceCacheSize;

    @SystemApi
    /* loaded from: classes.dex */
    public interface ContextAwareServiceProducerWithBinder<TServiceClass> {
        TServiceClass createService(Context context, IBinder iBinder);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface ContextAwareServiceProducerWithoutBinder<TServiceClass> {
        TServiceClass createService(Context context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ServiceFetcher<T> {
        T getService(ContextImpl contextImpl);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface StaticServiceProducerWithBinder<TServiceClass> {
        TServiceClass createService(IBinder iBinder);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface StaticServiceProducerWithoutBinder<TServiceClass> {
        TServiceClass createService();
    }

    static {
        ArrayMap arrayMap = new ArrayMap();
        SYSTEM_SERVICE_NAMES = arrayMap;
        SYSTEM_SERVICE_FETCHERS = new ArrayMap();
        SYSTEM_SERVICE_CLASS_NAMES = new ArrayMap();
        registerService(Context.ACCESSIBILITY_SERVICE, AccessibilityManager.class, new CachedServiceFetcher<AccessibilityManager>() { // from class: android.app.SystemServiceRegistry.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AccessibilityManager createService(ContextImpl ctx) {
                return AccessibilityManager.getInstance(ctx);
            }
        });
        registerService(Context.CAPTIONING_SERVICE, CaptioningManager.class, new CachedServiceFetcher<CaptioningManager>() { // from class: android.app.SystemServiceRegistry.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public CaptioningManager createService(ContextImpl ctx) {
                return new CaptioningManager(ctx);
            }
        });
        registerService("account", AccountManager.class, new CachedServiceFetcher<AccountManager>() { // from class: android.app.SystemServiceRegistry.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AccountManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("account");
                IAccountManager service = IAccountManager.Stub.asInterface(b);
                return new AccountManager(ctx, service);
            }
        });
        registerService("activity", ActivityManager.class, new CachedServiceFetcher<ActivityManager>() { // from class: android.app.SystemServiceRegistry.4
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ActivityManager createService(ContextImpl ctx) {
                return new ActivityManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.ACTIVITY_TASK_SERVICE, ActivityTaskManager.class, new CachedServiceFetcher<ActivityTaskManager>() { // from class: android.app.SystemServiceRegistry.5
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ActivityTaskManager createService(ContextImpl ctx) {
                return ActivityTaskManager.getInstance();
            }
        });
        registerService(Context.URI_GRANTS_SERVICE, UriGrantsManager.class, new CachedServiceFetcher<UriGrantsManager>() { // from class: android.app.SystemServiceRegistry.6
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public UriGrantsManager createService(ContextImpl ctx) {
                return new UriGrantsManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService("alarm", AlarmManager.class, new CachedServiceFetcher<AlarmManager>() { // from class: android.app.SystemServiceRegistry.7
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AlarmManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("alarm");
                IAlarmManager service = IAlarmManager.Stub.asInterface(b);
                return new AlarmManager(service, ctx);
            }
        });
        registerService("audio", AudioManager.class, new CachedServiceFetcher<AudioManager>() { // from class: android.app.SystemServiceRegistry.8
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AudioManager createService(ContextImpl ctx) {
                return new AudioManager(ctx);
            }
        });
        registerService(Context.AUDIO_DEVICE_VOLUME_SERVICE, AudioDeviceVolumeManager.class, new CachedServiceFetcher<AudioDeviceVolumeManager>() { // from class: android.app.SystemServiceRegistry.9
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AudioDeviceVolumeManager createService(ContextImpl ctx) {
                return new AudioDeviceVolumeManager(ctx);
            }
        });
        registerService(Context.MEDIA_ROUTER_SERVICE, MediaRouter.class, new CachedServiceFetcher<MediaRouter>() { // from class: android.app.SystemServiceRegistry.10
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public MediaRouter createService(ContextImpl ctx) {
                return new MediaRouter(ctx);
            }
        });
        registerService(Context.HDMI_CONTROL_SERVICE, HdmiControlManager.class, new StaticServiceFetcher<HdmiControlManager>() { // from class: android.app.SystemServiceRegistry.11
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public HdmiControlManager createService() throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.HDMI_CONTROL_SERVICE);
                return new HdmiControlManager(IHdmiControlService.Stub.asInterface(b));
            }
        });
        registerService(Context.TEXT_CLASSIFICATION_SERVICE, TextClassificationManager.class, new CachedServiceFetcher<TextClassificationManager>() { // from class: android.app.SystemServiceRegistry.12
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TextClassificationManager createService(ContextImpl ctx) {
                return new TextClassificationManager(ctx);
            }
        });
        registerService(Context.SELECTION_TOOLBAR_SERVICE, SelectionToolbarManager.class, new CachedServiceFetcher<SelectionToolbarManager>() { // from class: android.app.SystemServiceRegistry.13
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SelectionToolbarManager createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.SELECTION_TOOLBAR_SERVICE);
                return new SelectionToolbarManager(ctx.getOuterContext(), ISelectionToolbarManager.Stub.asInterface(b));
            }
        });
        registerService(Context.FONT_SERVICE, FontManager.class, new CachedServiceFetcher<FontManager>() { // from class: android.app.SystemServiceRegistry.14
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public FontManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.FONT_SERVICE);
                return FontManager.create(IFontManager.Stub.asInterface(b));
            }
        });
        registerService("clipboard", ClipboardManager.class, new CachedServiceFetcher<ClipboardManager>() { // from class: android.app.SystemServiceRegistry.15
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ClipboardManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new ClipboardManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        arrayMap.put(android.text.ClipboardManager.class, "clipboard");
        registerService(Context.PAC_PROXY_SERVICE, PacProxyManager.class, new CachedServiceFetcher<PacProxyManager>() { // from class: android.app.SystemServiceRegistry.16
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PacProxyManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.PAC_PROXY_SERVICE);
                IPacProxyManager service = IPacProxyManager.Stub.asInterface(b);
                return new PacProxyManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.NETD_SERVICE, IBinder.class, new StaticServiceFetcher<IBinder>() { // from class: android.app.SystemServiceRegistry.17
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public IBinder createService() throws ServiceManager.ServiceNotFoundException {
                return ServiceManager.getServiceOrThrow(Context.NETD_SERVICE);
            }
        });
        registerService(Context.TETHERING_SERVICE, TetheringManager.class, new SystemServiceRegistry$CachedServiceFetcherC026718());
        registerService(Context.VPN_MANAGEMENT_SERVICE, VpnManager.class, new CachedServiceFetcher<VpnManager>() { // from class: android.app.SystemServiceRegistry.19
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public VpnManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.VPN_MANAGEMENT_SERVICE);
                IVpnManager service = IVpnManager.Stub.asInterface(b);
                return new VpnManager(ctx, service);
            }
        });
        registerService(Context.VCN_MANAGEMENT_SERVICE, VcnManager.class, new CachedServiceFetcher<VcnManager>() { // from class: android.app.SystemServiceRegistry.20
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public VcnManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.VCN_MANAGEMENT_SERVICE);
                IVcnManagementService service = IVcnManagementService.Stub.asInterface(b);
                return new VcnManager(ctx, service);
            }
        });
        registerService(Context.COUNTRY_DETECTOR, CountryDetector.class, new StaticServiceFetcher<CountryDetector>() { // from class: android.app.SystemServiceRegistry.21
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public CountryDetector createService() throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.COUNTRY_DETECTOR);
                return new CountryDetector(ICountryDetector.Stub.asInterface(b));
            }
        });
        registerService(Context.DEVICE_POLICY_SERVICE, DevicePolicyManager.class, new CachedServiceFetcher<DevicePolicyManager>() { // from class: android.app.SystemServiceRegistry.22
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DevicePolicyManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.DEVICE_POLICY_SERVICE);
                return new DevicePolicyManager(ctx, IDevicePolicyManager.Stub.asInterface(b));
            }
        });
        registerService(Context.DOWNLOAD_SERVICE, DownloadManager.class, new CachedServiceFetcher<DownloadManager>() { // from class: android.app.SystemServiceRegistry.23
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DownloadManager createService(ContextImpl ctx) {
                return new DownloadManager(ctx);
            }
        });
        registerService(Context.BATTERY_SERVICE, BatteryManager.class, new CachedServiceFetcher<BatteryManager>() { // from class: android.app.SystemServiceRegistry.24
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public BatteryManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBatteryStats stats = IBatteryStats.Stub.asInterface(ServiceManager.getServiceOrThrow("batterystats"));
                IBatteryPropertiesRegistrar registrar = IBatteryPropertiesRegistrar.Stub.asInterface(ServiceManager.getServiceOrThrow("batteryproperties"));
                return new BatteryManager(ctx, stats, registrar);
            }
        });
        registerService(Context.DROPBOX_SERVICE, DropBoxManager.class, new CachedServiceFetcher<DropBoxManager>() { // from class: android.app.SystemServiceRegistry.25
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DropBoxManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.DROPBOX_SERVICE);
                IDropBoxManagerService service = IDropBoxManagerService.Stub.asInterface(b);
                return new DropBoxManager(ctx, service);
            }
        });
        registerService(Context.BINARY_TRANSPARENCY_SERVICE, BinaryTransparencyManager.class, new CachedServiceFetcher<BinaryTransparencyManager>() { // from class: android.app.SystemServiceRegistry.26
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public BinaryTransparencyManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.BINARY_TRANSPARENCY_SERVICE);
                IBinaryTransparencyService service = IBinaryTransparencyService.Stub.asInterface(b);
                return new BinaryTransparencyManager(ctx, service);
            }
        });
        registerService("input", InputManager.class, new ServiceFetcher<InputManager>() { // from class: android.app.SystemServiceRegistry.27
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.ServiceFetcher
            public InputManager getService(ContextImpl ctx) {
                return InputManager.getInstance(ctx.getOuterContext());
            }
        });
        registerService(Context.DISPLAY_SERVICE, DisplayManager.class, new CachedServiceFetcher<DisplayManager>() { // from class: android.app.SystemServiceRegistry.28
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DisplayManager createService(ContextImpl ctx) {
                return new DisplayManager(ctx.getOuterContext());
            }
        });
        registerService(Context.COLOR_DISPLAY_SERVICE, ColorDisplayManager.class, new CachedServiceFetcher<ColorDisplayManager>() { // from class: android.app.SystemServiceRegistry.29
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ColorDisplayManager createService(ContextImpl ctx) {
                return new ColorDisplayManager();
            }
        });
        registerService(Context.INPUT_METHOD_SERVICE, InputMethodManager.class, new ServiceFetcher<InputMethodManager>() { // from class: android.app.SystemServiceRegistry.30
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.ServiceFetcher
            public InputMethodManager getService(ContextImpl ctx) {
                return InputMethodManager.forContext(ctx.getOuterContext());
            }
        });
        registerService(Context.TEXT_SERVICES_MANAGER_SERVICE, TextServicesManager.class, new CachedServiceFetcher<TextServicesManager>() { // from class: android.app.SystemServiceRegistry.31
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TextServicesManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return TextServicesManager.createInstance(ctx);
            }
        });
        registerService(Context.KEYGUARD_SERVICE, KeyguardManager.class, new CachedServiceFetcher<KeyguardManager>() { // from class: android.app.SystemServiceRegistry.32
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public KeyguardManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new KeyguardManager(ctx);
            }
        });
        registerService(Context.LAYOUT_INFLATER_SERVICE, LayoutInflater.class, new CachedServiceFetcher<LayoutInflater>() { // from class: android.app.SystemServiceRegistry.33
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public LayoutInflater createService(ContextImpl ctx) {
                return new PhoneLayoutInflater(ctx.getOuterContext());
            }
        });
        registerService("location", LocationManager.class, new CachedServiceFetcher<LocationManager>() { // from class: android.app.SystemServiceRegistry.34
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public LocationManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("location");
                return new LocationManager(ctx, ILocationManager.Stub.asInterface(b));
            }
        });
        registerService(Context.NETWORK_POLICY_SERVICE, NetworkPolicyManager.class, new CachedServiceFetcher<NetworkPolicyManager>() { // from class: android.app.SystemServiceRegistry.35
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public NetworkPolicyManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new NetworkPolicyManager(ctx, INetworkPolicyManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.NETWORK_POLICY_SERVICE)));
            }
        });
        registerService("notification", NotificationManager.class, new CachedServiceFetcher<NotificationManager>() { // from class: android.app.SystemServiceRegistry.36
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public NotificationManager createService(ContextImpl ctx) {
                Context outerContext = ctx.getOuterContext();
                return new NotificationManager(new ContextThemeWrapper(outerContext, Resources.selectSystemTheme(0, outerContext.getApplicationInfo().targetSdkVersion, 16973835, 16973935, 16974126, 16974130)), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.PEOPLE_SERVICE, PeopleManager.class, new CachedServiceFetcher<PeopleManager>() { // from class: android.app.SystemServiceRegistry.37
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PeopleManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new PeopleManager(ctx);
            }
        });
        registerService(Context.POWER_SERVICE, PowerManager.class, new CachedServiceFetcher<PowerManager>() { // from class: android.app.SystemServiceRegistry.38
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PowerManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder powerBinder = ServiceManager.getServiceOrThrow(Context.POWER_SERVICE);
                IPowerManager powerService = IPowerManager.Stub.asInterface(powerBinder);
                IBinder thermalBinder = ServiceManager.getServiceOrThrow(Context.THERMAL_SERVICE);
                IThermalService thermalService = IThermalService.Stub.asInterface(thermalBinder);
                return new PowerManager(ctx.getOuterContext(), powerService, thermalService, ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.PERFORMANCE_HINT_SERVICE, PerformanceHintManager.class, new CachedServiceFetcher<PerformanceHintManager>() { // from class: android.app.SystemServiceRegistry.39
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PerformanceHintManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return PerformanceHintManager.create();
            }
        });
        registerService("recovery", RecoverySystem.class, new CachedServiceFetcher<RecoverySystem>() { // from class: android.app.SystemServiceRegistry.40
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public RecoverySystem createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("recovery");
                IRecoverySystem service = IRecoverySystem.Stub.asInterface(b);
                return new RecoverySystem(service);
            }
        });
        registerService("search", SearchManager.class, new CachedServiceFetcher<SearchManager>() { // from class: android.app.SystemServiceRegistry.41
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SearchManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new SearchManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.SENSOR_SERVICE, SensorManager.class, new CachedServiceFetcher<SensorManager>() { // from class: android.app.SystemServiceRegistry.42
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SensorManager createService(ContextImpl ctx) {
                return new SystemSensorManager(ctx.getOuterContext(), ctx.mMainThread.getHandler().getLooper());
            }
        });
        registerService(Context.SENSOR_PRIVACY_SERVICE, SensorPrivacyManager.class, new CachedServiceFetcher<SensorPrivacyManager>() { // from class: android.app.SystemServiceRegistry.43
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SensorPrivacyManager createService(ContextImpl ctx) {
                return SensorPrivacyManager.getInstance(ctx);
            }
        });
        registerService(Context.STATUS_BAR_SERVICE, StatusBarManager.class, new CachedServiceFetcher<StatusBarManager>() { // from class: android.app.SystemServiceRegistry.44
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public StatusBarManager createService(ContextImpl ctx) {
                return new StatusBarManager(ctx.getOuterContext());
            }
        });
        registerService(Context.STORAGE_SERVICE, StorageManager.class, new CachedServiceFetcher<StorageManager>() { // from class: android.app.SystemServiceRegistry.45
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public StorageManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new StorageManager(ctx, ctx.mMainThread.getHandler().getLooper());
            }
        });
        registerService(Context.STORAGE_STATS_SERVICE, StorageStatsManager.class, new CachedServiceFetcher<StorageStatsManager>() { // from class: android.app.SystemServiceRegistry.46
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public StorageStatsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IStorageStatsManager service = IStorageStatsManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.STORAGE_STATS_SERVICE));
                return new StorageStatsManager(ctx, service);
            }
        });
        registerService(Context.SYSTEM_UPDATE_SERVICE, SystemUpdateManager.class, new CachedServiceFetcher<SystemUpdateManager>() { // from class: android.app.SystemServiceRegistry.47
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SystemUpdateManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.SYSTEM_UPDATE_SERVICE);
                ISystemUpdateManager service = ISystemUpdateManager.Stub.asInterface(b);
                return new SystemUpdateManager(service);
            }
        });
        registerService(Context.SYSTEM_CONFIG_SERVICE, SystemConfigManager.class, new CachedServiceFetcher<SystemConfigManager>() { // from class: android.app.SystemServiceRegistry.48
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SystemConfigManager createService(ContextImpl ctx) {
                return new SystemConfigManager();
            }
        });
        registerService(Context.TELEPHONY_REGISTRY_SERVICE, TelephonyRegistryManager.class, new CachedServiceFetcher<TelephonyRegistryManager>() { // from class: android.app.SystemServiceRegistry.49
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TelephonyRegistryManager createService(ContextImpl ctx) {
                return new TelephonyRegistryManager(ctx);
            }
        });
        registerService(Context.TELECOM_SERVICE, TelecomManager.class, new CachedServiceFetcher<TelecomManager>() { // from class: android.app.SystemServiceRegistry.50
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TelecomManager createService(ContextImpl ctx) {
                return new TelecomManager(ctx.getOuterContext());
            }
        });
        registerService("mms", MmsManager.class, new CachedServiceFetcher<MmsManager>() { // from class: android.app.SystemServiceRegistry.51
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public MmsManager createService(ContextImpl ctx) {
                return new MmsManager(ctx.getOuterContext());
            }
        });
        registerService(Context.UI_MODE_SERVICE, UiModeManager.class, new CachedServiceFetcher<UiModeManager>() { // from class: android.app.SystemServiceRegistry.52
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public UiModeManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new UiModeManager(ctx.getOuterContext());
            }
        });
        registerService("usb", UsbManager.class, new CachedServiceFetcher<UsbManager>() { // from class: android.app.SystemServiceRegistry.53
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public UsbManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("usb");
                return new UsbManager(ctx, IUsbManager.Stub.asInterface(b));
            }
        });
        registerService("adb", AdbManager.class, new CachedServiceFetcher<AdbManager>() { // from class: android.app.SystemServiceRegistry.54
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AdbManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("adb");
                return new AdbManager(ctx, IAdbManager.Stub.asInterface(b));
            }
        });
        registerService(Context.SERIAL_SERVICE, SerialManager.class, new CachedServiceFetcher<SerialManager>() { // from class: android.app.SystemServiceRegistry.55
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SerialManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.SERIAL_SERVICE);
                return new SerialManager(ctx, ISerialManager.Stub.asInterface(b));
            }
        });
        registerService(Context.VIBRATOR_MANAGER_SERVICE, VibratorManager.class, new CachedServiceFetcher<VibratorManager>() { // from class: android.app.SystemServiceRegistry.56
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public VibratorManager createService(ContextImpl ctx) {
                return new SystemVibratorManager(ctx);
            }
        });
        registerService(Context.VIBRATOR_SERVICE, Vibrator.class, new CachedServiceFetcher<Vibrator>() { // from class: android.app.SystemServiceRegistry.57
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public Vibrator createService(ContextImpl ctx) {
                return new SystemVibrator(ctx);
            }
        });
        registerService(Context.WALLPAPER_SERVICE, WallpaperManager.class, new CachedServiceFetcher<WallpaperManager>() { // from class: android.app.SystemServiceRegistry.58
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public WallpaperManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.WALLPAPER_SERVICE);
                if (b == null) {
                    ApplicationInfo appInfo = ctx.getApplicationInfo();
                    if (appInfo.targetSdkVersion >= 28 && appInfo.isInstantApp()) {
                        throw new ServiceManager.ServiceNotFoundException(Context.WALLPAPER_SERVICE);
                    }
                    boolean enabled = Resources.getSystem().getBoolean(C4057R.bool.config_enableWallpaperService);
                    if (!enabled) {
                        return DisabledWallpaperManager.getInstance();
                    }
                    Log.m110e(SystemServiceRegistry.TAG, "No wallpaper service");
                }
                IWallpaperManager service = IWallpaperManager.Stub.asInterface(b);
                return new WallpaperManager(service, ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.WIFI_NL80211_SERVICE, WifiNl80211Manager.class, new CachedServiceFetcher<WifiNl80211Manager>() { // from class: android.app.SystemServiceRegistry.59
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public WifiNl80211Manager createService(ContextImpl ctx) {
                return new WifiNl80211Manager(ctx.getOuterContext());
            }
        });
        registerService(Context.WINDOW_SERVICE, WindowManager.class, new CachedServiceFetcher<WindowManager>() { // from class: android.app.SystemServiceRegistry.60
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public WindowManager createService(ContextImpl ctx) {
                return new WindowManagerImpl(ctx);
            }
        });
        registerService("user", UserManager.class, new CachedServiceFetcher<UserManager>() { // from class: android.app.SystemServiceRegistry.61
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public UserManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("user");
                IUserManager service = IUserManager.Stub.asInterface(b);
                return new UserManager(ctx, service);
            }
        });
        registerService(Context.APP_OPS_SERVICE, AppOpsManager.class, new CachedServiceFetcher<AppOpsManager>() { // from class: android.app.SystemServiceRegistry.62
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AppOpsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.APP_OPS_SERVICE);
                IAppOpsService service = IAppOpsService.Stub.asInterface(b);
                return new AppOpsManager(ctx, service);
            }
        });
        registerService(Context.CAMERA_SERVICE, CameraManager.class, new CachedServiceFetcher<CameraManager>() { // from class: android.app.SystemServiceRegistry.63
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public CameraManager createService(ContextImpl ctx) {
                return new CameraManager(ctx);
            }
        });
        registerService(Context.LAUNCHER_APPS_SERVICE, LauncherApps.class, new CachedServiceFetcher<LauncherApps>() { // from class: android.app.SystemServiceRegistry.64
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public LauncherApps createService(ContextImpl ctx) {
                return new LauncherApps(ctx);
            }
        });
        registerService(Context.RESTRICTIONS_SERVICE, RestrictionsManager.class, new CachedServiceFetcher<RestrictionsManager>() { // from class: android.app.SystemServiceRegistry.65
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public RestrictionsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.RESTRICTIONS_SERVICE);
                IRestrictionsManager service = IRestrictionsManager.Stub.asInterface(b);
                return new RestrictionsManager(ctx, service);
            }
        });
        registerService(Context.PRINT_SERVICE, PrintManager.class, new CachedServiceFetcher<PrintManager>() { // from class: android.app.SystemServiceRegistry.66
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PrintManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IPrintManager service = null;
                if (ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_PRINTING)) {
                    service = IPrintManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.PRINT_SERVICE));
                }
                int userId = ctx.getUserId();
                int appId = UserHandle.getAppId(ctx.getApplicationInfo().uid);
                return new PrintManager(ctx.getOuterContext(), service, userId, appId);
            }
        });
        registerService(Context.COMPANION_DEVICE_SERVICE, CompanionDeviceManager.class, new CachedServiceFetcher<CompanionDeviceManager>() { // from class: android.app.SystemServiceRegistry.67
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public CompanionDeviceManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                ICompanionDeviceManager service = null;
                if (ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_COMPANION_DEVICE_SETUP)) {
                    service = ICompanionDeviceManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.COMPANION_DEVICE_SERVICE));
                }
                return new CompanionDeviceManager(service, ctx.getOuterContext());
            }
        });
        registerService(Context.VIRTUAL_DEVICE_SERVICE, VirtualDeviceManager.class, new CachedServiceFetcher<VirtualDeviceManager>() { // from class: android.app.SystemServiceRegistry.68
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public VirtualDeviceManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                if (!ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_COMPANION_DEVICE_SETUP)) {
                    return null;
                }
                IVirtualDeviceManager service = IVirtualDeviceManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.VIRTUAL_DEVICE_SERVICE));
                return new VirtualDeviceManager(service, ctx.getOuterContext());
            }
        });
        registerService(Context.CONSUMER_IR_SERVICE, ConsumerIrManager.class, new CachedServiceFetcher<ConsumerIrManager>() { // from class: android.app.SystemServiceRegistry.69
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ConsumerIrManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new ConsumerIrManager(ctx);
            }
        });
        registerService(Context.TRUST_SERVICE, TrustManager.class, new StaticServiceFetcher<TrustManager>() { // from class: android.app.SystemServiceRegistry.70
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public TrustManager createService() throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.TRUST_SERVICE);
                return new TrustManager(b);
            }
        });
        registerService(Context.FINGERPRINT_SERVICE, FingerprintManager.class, new CachedServiceFetcher<FingerprintManager>() { // from class: android.app.SystemServiceRegistry.71
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public FingerprintManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder binder;
                if (ctx.getApplicationInfo().targetSdkVersion >= 26) {
                    binder = ServiceManager.getServiceOrThrow(Context.FINGERPRINT_SERVICE);
                } else {
                    binder = ServiceManager.getService(Context.FINGERPRINT_SERVICE);
                }
                IFingerprintService service = IFingerprintService.Stub.asInterface(binder);
                return new FingerprintManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.FACE_SERVICE, FaceManager.class, new CachedServiceFetcher<FaceManager>() { // from class: android.app.SystemServiceRegistry.72
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public FaceManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder binder;
                if (ctx.getApplicationInfo().targetSdkVersion >= 26) {
                    binder = ServiceManager.getServiceOrThrow(Context.FACE_SERVICE);
                } else {
                    binder = ServiceManager.getService(Context.FACE_SERVICE);
                }
                IFaceService service = IFaceService.Stub.asInterface(binder);
                return new FaceManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.IRIS_SERVICE, IrisManager.class, new CachedServiceFetcher<IrisManager>() { // from class: android.app.SystemServiceRegistry.73
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public IrisManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder binder = ServiceManager.getServiceOrThrow(Context.IRIS_SERVICE);
                IIrisService service = IIrisService.Stub.asInterface(binder);
                return new IrisManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.BIOMETRIC_SERVICE, BiometricManager.class, new CachedServiceFetcher<BiometricManager>() { // from class: android.app.SystemServiceRegistry.74
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public BiometricManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder binder = ServiceManager.getServiceOrThrow(Context.AUTH_SERVICE);
                IAuthService service = IAuthService.Stub.asInterface(binder);
                return new BiometricManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.TV_INTERACTIVE_APP_SERVICE, TvInteractiveAppManager.class, new CachedServiceFetcher<TvInteractiveAppManager>() { // from class: android.app.SystemServiceRegistry.75
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TvInteractiveAppManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.TV_INTERACTIVE_APP_SERVICE);
                ITvInteractiveAppManager service = ITvInteractiveAppManager.Stub.asInterface(iBinder);
                return new TvInteractiveAppManager(service, ctx.getUserId());
            }
        });
        registerService(Context.TV_INPUT_SERVICE, TvInputManager.class, new CachedServiceFetcher<TvInputManager>() { // from class: android.app.SystemServiceRegistry.76
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TvInputManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.TV_INPUT_SERVICE);
                ITvInputManager service = ITvInputManager.Stub.asInterface(iBinder);
                return new TvInputManager(service, ctx.getUserId());
            }
        });
        registerService(Context.TV_TUNER_RESOURCE_MGR_SERVICE, TunerResourceManager.class, new CachedServiceFetcher<TunerResourceManager>() { // from class: android.app.SystemServiceRegistry.77
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TunerResourceManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.TV_TUNER_RESOURCE_MGR_SERVICE);
                ITunerResourceManager service = ITunerResourceManager.Stub.asInterface(iBinder);
                return new TunerResourceManager(service, ctx.getUserId());
            }
        });
        registerService(Context.NETWORK_SCORE_SERVICE, NetworkScoreManager.class, new CachedServiceFetcher<NetworkScoreManager>() { // from class: android.app.SystemServiceRegistry.78
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public NetworkScoreManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new NetworkScoreManager(ctx);
            }
        });
        registerService(Context.USAGE_STATS_SERVICE, UsageStatsManager.class, new CachedServiceFetcher<UsageStatsManager>() { // from class: android.app.SystemServiceRegistry.79
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public UsageStatsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.USAGE_STATS_SERVICE);
                IUsageStatsManager service = IUsageStatsManager.Stub.asInterface(iBinder);
                return new UsageStatsManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.PERSISTENT_DATA_BLOCK_SERVICE, PersistentDataBlockManager.class, new StaticServiceFetcher<PersistentDataBlockManager>() { // from class: android.app.SystemServiceRegistry.80
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public PersistentDataBlockManager createService() throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.PERSISTENT_DATA_BLOCK_SERVICE);
                IPersistentDataBlockService persistentDataBlockService = IPersistentDataBlockService.Stub.asInterface(b);
                if (persistentDataBlockService != null) {
                    return new PersistentDataBlockManager(persistentDataBlockService);
                }
                return null;
            }
        });
        registerService(Context.OEM_LOCK_SERVICE, OemLockManager.class, new StaticServiceFetcher<OemLockManager>() { // from class: android.app.SystemServiceRegistry.81
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public OemLockManager createService() throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.OEM_LOCK_SERVICE);
                IOemLockService oemLockService = IOemLockService.Stub.asInterface(b);
                if (oemLockService != null) {
                    return new OemLockManager(oemLockService);
                }
                return null;
            }
        });
        registerService(Context.MEDIA_PROJECTION_SERVICE, MediaProjectionManager.class, new CachedServiceFetcher<MediaProjectionManager>() { // from class: android.app.SystemServiceRegistry.82
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public MediaProjectionManager createService(ContextImpl ctx) {
                return new MediaProjectionManager(ctx);
            }
        });
        registerService(Context.APPWIDGET_SERVICE, AppWidgetManager.class, new CachedServiceFetcher<AppWidgetManager>() { // from class: android.app.SystemServiceRegistry.83
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AppWidgetManager createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.APPWIDGET_SERVICE);
                if (b == null) {
                    return null;
                }
                return new AppWidgetManager(ctx, IAppWidgetService.Stub.asInterface(b));
            }
        });
        registerService("midi", MidiManager.class, new CachedServiceFetcher<MidiManager>() { // from class: android.app.SystemServiceRegistry.84
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public MidiManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("midi");
                return new MidiManager(IMidiManager.Stub.asInterface(b));
            }
        });
        registerService(Context.RADIO_SERVICE, RadioManager.class, new CachedServiceFetcher<RadioManager>() { // from class: android.app.SystemServiceRegistry.85
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public RadioManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new RadioManager(ctx);
            }
        });
        registerService(Context.HARDWARE_PROPERTIES_SERVICE, HardwarePropertiesManager.class, new CachedServiceFetcher<HardwarePropertiesManager>() { // from class: android.app.SystemServiceRegistry.86
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public HardwarePropertiesManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.HARDWARE_PROPERTIES_SERVICE);
                IHardwarePropertiesManager service = IHardwarePropertiesManager.Stub.asInterface(b);
                return new HardwarePropertiesManager(ctx, service);
            }
        });
        registerService(Context.SOUND_TRIGGER_SERVICE, SoundTriggerManager.class, new CachedServiceFetcher<SoundTriggerManager>() { // from class: android.app.SystemServiceRegistry.87
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SoundTriggerManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.SOUND_TRIGGER_SERVICE);
                return new SoundTriggerManager(ctx, ISoundTriggerService.Stub.asInterface(b));
            }
        });
        registerService("shortcut", ShortcutManager.class, new CachedServiceFetcher<ShortcutManager>() { // from class: android.app.SystemServiceRegistry.88
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ShortcutManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("shortcut");
                return new ShortcutManager(ctx, IShortcutService.Stub.asInterface(b));
            }
        });
        registerService("overlay", OverlayManager.class, new CachedServiceFetcher<OverlayManager>() { // from class: android.app.SystemServiceRegistry.89
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public OverlayManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b;
                if (Compatibility.isChangeEnabled((long) OverlayManager.SELF_TARGETING_OVERLAY)) {
                    b = ServiceManager.getService("overlay");
                } else {
                    b = ServiceManager.getServiceOrThrow("overlay");
                }
                return new OverlayManager(ctx, IOverlayManager.Stub.asInterface(b));
            }
        });
        registerService(Context.NETWORK_WATCHLIST_SERVICE, NetworkWatchlistManager.class, new CachedServiceFetcher<NetworkWatchlistManager>() { // from class: android.app.SystemServiceRegistry.90
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public NetworkWatchlistManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.NETWORK_WATCHLIST_SERVICE);
                return new NetworkWatchlistManager(ctx, INetworkWatchlistManager.Stub.asInterface(b));
            }
        });
        registerService(Context.SYSTEM_HEALTH_SERVICE, SystemHealthManager.class, new CachedServiceFetcher<SystemHealthManager>() { // from class: android.app.SystemServiceRegistry.91
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SystemHealthManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("batterystats");
                return new SystemHealthManager(IBatteryStats.Stub.asInterface(b));
            }
        });
        registerService(Context.CONTEXTHUB_SERVICE, ContextHubManager.class, new CachedServiceFetcher<ContextHubManager>() { // from class: android.app.SystemServiceRegistry.92
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ContextHubManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new ContextHubManager(ctx.getOuterContext(), ctx.mMainThread.getHandler().getLooper());
            }
        });
        registerService(Context.INCIDENT_SERVICE, IncidentManager.class, new CachedServiceFetcher<IncidentManager>() { // from class: android.app.SystemServiceRegistry.93
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public IncidentManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new IncidentManager(ctx);
            }
        });
        registerService(Context.BUGREPORT_SERVICE, BugreportManager.class, new CachedServiceFetcher<BugreportManager>() { // from class: android.app.SystemServiceRegistry.94
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public BugreportManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.BUGREPORT_SERVICE);
                return new BugreportManager(ctx.getOuterContext(), IDumpstate.Stub.asInterface(b));
            }
        });
        registerService(Context.AUTOFILL_MANAGER_SERVICE, AutofillManager.class, new CachedServiceFetcher<AutofillManager>() { // from class: android.app.SystemServiceRegistry.95
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AutofillManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.AUTOFILL_MANAGER_SERVICE);
                IAutoFillManager service = IAutoFillManager.Stub.asInterface(b);
                return new AutofillManager(ctx.getOuterContext(), service);
            }
        });
        registerService(Context.CREDENTIAL_SERVICE, CredentialManager.class, new CachedServiceFetcher<CredentialManager>() { // from class: android.app.SystemServiceRegistry.96
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public CredentialManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.CREDENTIAL_SERVICE);
                ICredentialManager service = ICredentialManager.Stub.asInterface(b);
                if (service != null) {
                    return new CredentialManager(ctx.getOuterContext(), service);
                }
                return null;
            }
        });
        registerService(Context.MUSIC_RECOGNITION_SERVICE, MusicRecognitionManager.class, new CachedServiceFetcher<MusicRecognitionManager>() { // from class: android.app.SystemServiceRegistry.97
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public MusicRecognitionManager createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.MUSIC_RECOGNITION_SERVICE);
                return new MusicRecognitionManager(IMusicRecognitionManager.Stub.asInterface(b));
            }
        });
        registerService(Context.CONTENT_CAPTURE_MANAGER_SERVICE, ContentCaptureManager.class, new CachedServiceFetcher<ContentCaptureManager>() { // from class: android.app.SystemServiceRegistry.98
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ContentCaptureManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                Context outerContext = ctx.getOuterContext();
                ContentCaptureOptions options = outerContext.getContentCaptureOptions();
                if (options != null) {
                    if (options.lite || options.isWhitelisted(outerContext)) {
                        IBinder b = ServiceManager.getService(Context.CONTENT_CAPTURE_MANAGER_SERVICE);
                        IContentCaptureManager service = IContentCaptureManager.Stub.asInterface(b);
                        if (service != null) {
                            return new ContentCaptureManager(outerContext, service, options);
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            }
        });
        registerService(Context.TRANSLATION_MANAGER_SERVICE, TranslationManager.class, new CachedServiceFetcher<TranslationManager>() { // from class: android.app.SystemServiceRegistry.99
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TranslationManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.TRANSLATION_MANAGER_SERVICE);
                ITranslationManager service = ITranslationManager.Stub.asInterface(b);
                if (service != null) {
                    return new TranslationManager(ctx.getOuterContext(), service);
                }
                return null;
            }
        });
        registerService(Context.UI_TRANSLATION_SERVICE, UiTranslationManager.class, new CachedServiceFetcher<UiTranslationManager>() { // from class: android.app.SystemServiceRegistry.100
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public UiTranslationManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.TRANSLATION_MANAGER_SERVICE);
                ITranslationManager service = ITranslationManager.Stub.asInterface(b);
                if (service != null) {
                    return new UiTranslationManager(ctx.getOuterContext(), service);
                }
                return null;
            }
        });
        registerService(Context.SEARCH_UI_SERVICE, SearchUiManager.class, new CachedServiceFetcher<SearchUiManager>() { // from class: android.app.SystemServiceRegistry.101
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SearchUiManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.SEARCH_UI_SERVICE);
                if (b == null) {
                    return null;
                }
                return new SearchUiManager(ctx);
            }
        });
        registerService(Context.SMARTSPACE_SERVICE, SmartspaceManager.class, new CachedServiceFetcher<SmartspaceManager>() { // from class: android.app.SystemServiceRegistry.102
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SmartspaceManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.SMARTSPACE_SERVICE);
                if (b == null) {
                    return null;
                }
                return new SmartspaceManager(ctx);
            }
        });
        registerService(Context.APP_PREDICTION_SERVICE, AppPredictionManager.class, new CachedServiceFetcher<AppPredictionManager>() { // from class: android.app.SystemServiceRegistry.103
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AppPredictionManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.APP_PREDICTION_SERVICE);
                if (b == null) {
                    return null;
                }
                return new AppPredictionManager(ctx);
            }
        });
        registerService(Context.CONTENT_SUGGESTIONS_SERVICE, ContentSuggestionsManager.class, new CachedServiceFetcher<ContentSuggestionsManager>() { // from class: android.app.SystemServiceRegistry.104
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public ContentSuggestionsManager createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.CONTENT_SUGGESTIONS_SERVICE);
                IContentSuggestionsManager service = IContentSuggestionsManager.Stub.asInterface(b);
                return new ContentSuggestionsManager(ctx.getUserId(), service);
            }
        });
        registerService(Context.WALLPAPER_EFFECTS_GENERATION_SERVICE, WallpaperEffectsGenerationManager.class, new CachedServiceFetcher<WallpaperEffectsGenerationManager>() { // from class: android.app.SystemServiceRegistry.105
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public WallpaperEffectsGenerationManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getService(Context.WALLPAPER_EFFECTS_GENERATION_SERVICE);
                if (b == null) {
                    return null;
                }
                return new WallpaperEffectsGenerationManager(IWallpaperEffectsGenerationManager.Stub.asInterface(b));
            }
        });
        registerService(Context.VR_SERVICE, VrManager.class, new CachedServiceFetcher<VrManager>() { // from class: android.app.SystemServiceRegistry.106
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public VrManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.VR_SERVICE);
                return new VrManager(IVrManager.Stub.asInterface(b));
            }
        });
        registerService(Context.CROSS_PROFILE_APPS_SERVICE, CrossProfileApps.class, new CachedServiceFetcher<CrossProfileApps>() { // from class: android.app.SystemServiceRegistry.107
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public CrossProfileApps createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.CROSS_PROFILE_APPS_SERVICE);
                return new CrossProfileApps(ctx.getOuterContext(), ICrossProfileApps.Stub.asInterface(b));
            }
        });
        registerService("slice", SliceManager.class, new CachedServiceFetcher<SliceManager>() { // from class: android.app.SystemServiceRegistry.108
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SliceManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new SliceManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService("time_detector", TimeDetector.class, new CachedServiceFetcher<TimeDetector>() { // from class: android.app.SystemServiceRegistry.109
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TimeDetector createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new TimeDetectorImpl();
            }
        });
        registerService("time_zone_detector", TimeZoneDetector.class, new CachedServiceFetcher<TimeZoneDetector>() { // from class: android.app.SystemServiceRegistry.110
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TimeZoneDetector createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new TimeZoneDetectorImpl();
            }
        });
        registerService(Context.TIME_MANAGER_SERVICE, TimeManager.class, new CachedServiceFetcher<TimeManager>() { // from class: android.app.SystemServiceRegistry.111
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TimeManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new TimeManager();
            }
        });
        registerService("permission", PermissionManager.class, new CachedServiceFetcher<PermissionManager>() { // from class: android.app.SystemServiceRegistry.112
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PermissionManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new PermissionManager(ctx.getOuterContext());
            }
        });
        registerService(Context.LEGACY_PERMISSION_SERVICE, LegacyPermissionManager.class, new CachedServiceFetcher<LegacyPermissionManager>() { // from class: android.app.SystemServiceRegistry.113
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public LegacyPermissionManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new LegacyPermissionManager();
            }
        });
        registerService(Context.PERMISSION_CONTROLLER_SERVICE, PermissionControllerManager.class, new CachedServiceFetcher<PermissionControllerManager>() { // from class: android.app.SystemServiceRegistry.114
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PermissionControllerManager createService(ContextImpl ctx) {
                return new PermissionControllerManager(ctx.getOuterContext(), ctx.getMainThreadHandler());
            }
        });
        registerService(Context.PERMISSION_CHECKER_SERVICE, PermissionCheckerManager.class, new CachedServiceFetcher<PermissionCheckerManager>() { // from class: android.app.SystemServiceRegistry.115
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PermissionCheckerManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new PermissionCheckerManager(ctx.getOuterContext());
            }
        });
        registerService(Context.PERMISSION_ENFORCER_SERVICE, PermissionEnforcer.class, new CachedServiceFetcher<PermissionEnforcer>() { // from class: android.app.SystemServiceRegistry.116
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public PermissionEnforcer createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new PermissionEnforcer(ctx.getOuterContext());
            }
        });
        registerService(Context.DYNAMIC_SYSTEM_SERVICE, DynamicSystemManager.class, new CachedServiceFetcher<DynamicSystemManager>() { // from class: android.app.SystemServiceRegistry.117
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DynamicSystemManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.DYNAMIC_SYSTEM_SERVICE);
                return new DynamicSystemManager(IDynamicSystemService.Stub.asInterface(b));
            }
        });
        registerService("batterystats", BatteryStatsManager.class, new CachedServiceFetcher<BatteryStatsManager>() { // from class: android.app.SystemServiceRegistry.118
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public BatteryStatsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow("batterystats");
                return new BatteryStatsManager(IBatteryStats.Stub.asInterface(b));
            }
        });
        registerService(Context.DATA_LOADER_MANAGER_SERVICE, DataLoaderManager.class, new CachedServiceFetcher<DataLoaderManager>() { // from class: android.app.SystemServiceRegistry.119
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DataLoaderManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.DATA_LOADER_MANAGER_SERVICE);
                return new DataLoaderManager(IDataLoaderManager.Stub.asInterface(b));
            }
        });
        registerService(Context.LIGHTS_SERVICE, LightsManager.class, new CachedServiceFetcher<LightsManager>() { // from class: android.app.SystemServiceRegistry.120
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public LightsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new SystemLightsManager(ctx);
            }
        });
        registerService("locale", LocaleManager.class, new CachedServiceFetcher<LocaleManager>() { // from class: android.app.SystemServiceRegistry.121
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public LocaleManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new LocaleManager(ctx, ILocaleManager.Stub.asInterface(ServiceManager.getServiceOrThrow("locale")));
            }
        });
        registerService(Context.INCREMENTAL_SERVICE, IncrementalManager.class, new CachedServiceFetcher<IncrementalManager>() { // from class: android.app.SystemServiceRegistry.122
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public IncrementalManager createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.INCREMENTAL_SERVICE);
                if (b == null) {
                    return null;
                }
                return new IncrementalManager(IIncrementalService.Stub.asInterface(b));
            }
        });
        registerService(Context.FILE_INTEGRITY_SERVICE, FileIntegrityManager.class, new CachedServiceFetcher<FileIntegrityManager>() { // from class: android.app.SystemServiceRegistry.123
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public FileIntegrityManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.FILE_INTEGRITY_SERVICE);
                return new FileIntegrityManager(ctx.getOuterContext(), IFileIntegrityService.Stub.asInterface(b));
            }
        });
        registerService(Context.ATTESTATION_VERIFICATION_SERVICE, AttestationVerificationManager.class, new CachedServiceFetcher<AttestationVerificationManager>() { // from class: android.app.SystemServiceRegistry.124
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AttestationVerificationManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.ATTESTATION_VERIFICATION_SERVICE);
                return new AttestationVerificationManager(ctx.getOuterContext(), IAttestationVerificationManagerService.Stub.asInterface(b));
            }
        });
        registerService(Context.APP_INTEGRITY_SERVICE, AppIntegrityManager.class, new CachedServiceFetcher<AppIntegrityManager>() { // from class: android.app.SystemServiceRegistry.125
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AppIntegrityManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder b = ServiceManager.getServiceOrThrow(Context.APP_INTEGRITY_SERVICE);
                return new AppIntegrityManager(IAppIntegrityManager.Stub.asInterface(b));
            }
        });
        registerService(Context.APP_HIBERNATION_SERVICE, AppHibernationManager.class, new CachedServiceFetcher<AppHibernationManager>() { // from class: android.app.SystemServiceRegistry.126
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AppHibernationManager createService(ContextImpl ctx) {
                IBinder b = ServiceManager.getService(Context.APP_HIBERNATION_SERVICE);
                if (b == null) {
                    return null;
                }
                return new AppHibernationManager(ctx);
            }
        });
        registerService(Context.DREAM_SERVICE, DreamManager.class, new CachedServiceFetcher<DreamManager>() { // from class: android.app.SystemServiceRegistry.127
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DreamManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new DreamManager(ctx);
            }
        });
        registerService(Context.DEVICE_STATE_SERVICE, DeviceStateManager.class, new CachedServiceFetcher<DeviceStateManager>() { // from class: android.app.SystemServiceRegistry.128
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DeviceStateManager createService(ContextImpl ctx) {
                return new DeviceStateManager();
            }
        });
        registerService(Context.MEDIA_METRICS_SERVICE, MediaMetricsManager.class, new CachedServiceFetcher<MediaMetricsManager>() { // from class: android.app.SystemServiceRegistry.129
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public MediaMetricsManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.MEDIA_METRICS_SERVICE);
                IMediaMetricsManager service = IMediaMetricsManager.Stub.asInterface(iBinder);
                return new MediaMetricsManager(service, ctx.getUserId());
            }
        });
        registerService(Context.GAME_SERVICE, GameManager.class, new CachedServiceFetcher<GameManager>() { // from class: android.app.SystemServiceRegistry.130
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public GameManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new GameManager(ctx.getOuterContext(), ctx.mMainThread.getHandler());
            }
        });
        registerService(Context.DOMAIN_VERIFICATION_SERVICE, DomainVerificationManager.class, new CachedServiceFetcher<DomainVerificationManager>() { // from class: android.app.SystemServiceRegistry.131
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DomainVerificationManager createService(ContextImpl context) throws ServiceManager.ServiceNotFoundException {
                IBinder binder = ServiceManager.getServiceOrThrow(Context.DOMAIN_VERIFICATION_SERVICE);
                IDomainVerificationManager service = IDomainVerificationManager.Stub.asInterface(binder);
                return new DomainVerificationManager(context, service);
            }
        });
        registerService(Context.DISPLAY_HASH_SERVICE, DisplayHashManager.class, new CachedServiceFetcher<DisplayHashManager>() { // from class: android.app.SystemServiceRegistry.132
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public DisplayHashManager createService(ContextImpl ctx) {
                return new DisplayHashManager();
            }
        });
        registerService(Context.AMBIENT_CONTEXT_SERVICE, AmbientContextManager.class, new CachedServiceFetcher<AmbientContextManager>() { // from class: android.app.SystemServiceRegistry.133
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public AmbientContextManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.AMBIENT_CONTEXT_SERVICE);
                IAmbientContextManager manager = IAmbientContextManager.Stub.asInterface(iBinder);
                return new AmbientContextManager(ctx.getOuterContext(), manager);
            }
        });
        registerService(Context.WEARABLE_SENSING_SERVICE, WearableSensingManager.class, new CachedServiceFetcher<WearableSensingManager>() { // from class: android.app.SystemServiceRegistry.134
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public WearableSensingManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                IBinder iBinder = ServiceManager.getServiceOrThrow(Context.WEARABLE_SENSING_SERVICE);
                IWearableSensingManager manager = IWearableSensingManager.Stub.asInterface(iBinder);
                return new WearableSensingManager(ctx.getOuterContext(), manager);
            }
        });
        registerService(Context.GRAMMATICAL_INFLECTION_SERVICE, GrammaticalInflectionManager.class, new CachedServiceFetcher<GrammaticalInflectionManager>() { // from class: android.app.SystemServiceRegistry.135
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public GrammaticalInflectionManager createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return new GrammaticalInflectionManager(ctx, IGrammaticalInflectionManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.GRAMMATICAL_INFLECTION_SERVICE)));
            }
        });
        registerService(Context.SHARED_CONNECTIVITY_SERVICE, SharedConnectivityManager.class, new CachedServiceFetcher<SharedConnectivityManager>() { // from class: android.app.SystemServiceRegistry.136
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public SharedConnectivityManager createService(ContextImpl ctx) {
                return SharedConnectivityManager.create(ctx);
            }
        });
        sInitializing = true;
        try {
            ConnectivityFrameworkInitializer.registerServiceWrappers();
            JobSchedulerFrameworkInitializer.registerServiceWrappers();
            BlobStoreManagerFrameworkInitializer.initialize();
            BluetoothFrameworkInitializer.registerServiceWrappers();
            NfcFrameworkInitializer.registerServiceWrappers();
            TelephonyFrameworkInitializer.registerServiceWrappers();
            AppSearchManagerFrameworkInitializer.initialize();
            HealthServicesInitializer.registerServiceWrappers();
            WifiFrameworkInitializer.registerServiceWrappers();
            StatsFrameworkInitializer.registerServiceWrappers();
            RollbackManagerFrameworkInitializer.initialize();
            MediaFrameworkPlatformInitializer.registerServiceWrappers();
            MediaFrameworkInitializer.registerServiceWrappers();
            RoleFrameworkInitializer.registerServiceWrappers();
            SchedulingFrameworkInitializer.registerServiceWrappers();
            SdkSandboxManagerFrameworkInitializer.registerServiceWrappers();
            AdServicesFrameworkInitializer.registerServiceWrappers();
            UwbFrameworkInitializer.registerServiceWrappers();
            SafetyCenterFrameworkInitializer.registerServiceWrappers();
            ConnectivityFrameworkInitializerTiramisu.registerServiceWrappers();
            NearbyFrameworkInitializer.registerServiceWrappers();
            OnDevicePersonalizationFrameworkInitializer.registerServiceWrappers();
            DeviceLockFrameworkInitializer.registerServiceWrappers();
            VirtualizationFrameworkInitializer.registerServiceWrappers();
        } finally {
            sInitializing = false;
        }
    }

    private SystemServiceRegistry() {
    }

    /* renamed from: android.app.SystemServiceRegistry$18 */
    /* loaded from: classes.dex */
    class SystemServiceRegistry$CachedServiceFetcherC026718 extends CachedServiceFetcher<TetheringManager> {
        SystemServiceRegistry$CachedServiceFetcherC026718() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
        public TetheringManager createService(ContextImpl ctx) {
            return new TetheringManager(ctx, new Supplier() { // from class: android.app.SystemServiceRegistry$18$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    IBinder service;
                    service = ServiceManager.getService(Context.TETHERING_SERVICE);
                    return service;
                }
            });
        }
    }

    private static void ensureInitializing(String methodName) {
        Preconditions.checkState(sInitializing, "Internal error: %s can only be called during class initialization.", methodName);
    }

    public static Object[] createServiceCache() {
        return new Object[sServiceCacheSize];
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static Object getSystemService(ContextImpl ctx, String name) {
        char c;
        if (name == null) {
            return null;
        }
        ServiceFetcher<?> fetcher = SYSTEM_SERVICE_FETCHERS.get(name);
        if (fetcher == null) {
            if (sEnableServiceNotFoundWtf) {
                Slog.wtf(TAG, "Unknown manager requested: " + name);
            }
            return null;
        }
        Object ret = fetcher.getService(ctx);
        if (sEnableServiceNotFoundWtf && ret == null) {
            switch (name.hashCode()) {
                case -1419358249:
                    if (name.equals(Context.ETHERNET_SERVICE)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -769002131:
                    if (name.equals(Context.APP_PREDICTION_SERVICE)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 56472313:
                    if (name.equals(Context.VIRTUALIZATION_SERVICE)) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 273796326:
                    if (name.equals(Context.CONTEXTHUB_SERVICE)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 948275489:
                    if (name.equals(Context.VIRTUAL_DEVICE_SERVICE)) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 974854528:
                    if (name.equals(Context.CONTENT_CAPTURE_MANAGER_SERVICE)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 1085372378:
                    if (name.equals(Context.INCREMENTAL_SERVICE)) {
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
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return null;
                default:
                    Slog.wtf(TAG, "Manager wrapper not available: " + name);
                    return null;
            }
        }
        return ret;
    }

    public static String getSystemServiceName(Class<?> serviceClass) {
        if (serviceClass == null) {
            return null;
        }
        String serviceName = SYSTEM_SERVICE_NAMES.get(serviceClass);
        if (sEnableServiceNotFoundWtf && serviceName == null) {
            Slog.wtf(TAG, "Unknown manager requested: " + serviceClass.getCanonicalName());
        }
        return serviceName;
    }

    private static <T> void registerService(String serviceName, Class<T> serviceClass, ServiceFetcher<T> serviceFetcher) {
        SYSTEM_SERVICE_NAMES.put(serviceClass, serviceName);
        SYSTEM_SERVICE_FETCHERS.put(serviceName, serviceFetcher);
        SYSTEM_SERVICE_CLASS_NAMES.put(serviceName, serviceClass.getSimpleName());
    }

    public static String getSystemServiceClassName(String name) {
        return SYSTEM_SERVICE_CLASS_NAMES.get(name);
    }

    @SystemApi
    public static <TServiceClass> void registerStaticService(final String serviceName, Class<TServiceClass> serviceWrapperClass, final StaticServiceProducerWithBinder<TServiceClass> serviceProducer) {
        ensureInitializing("registerStaticService");
        Preconditions.checkStringNotEmpty(serviceName);
        Objects.requireNonNull(serviceWrapperClass);
        Objects.requireNonNull(serviceProducer);
        registerService(serviceName, serviceWrapperClass, new StaticServiceFetcher<TServiceClass>() { // from class: android.app.SystemServiceRegistry.137
            /* JADX WARN: Type inference failed for: r0v1, types: [TServiceClass, java.lang.Object] */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public TServiceClass createService() throws ServiceManager.ServiceNotFoundException {
                return StaticServiceProducerWithBinder.this.createService(ServiceManager.getServiceOrThrow(serviceName));
            }
        });
    }

    @SystemApi
    public static <TServiceClass> void registerStaticService(String serviceName, Class<TServiceClass> serviceWrapperClass, final StaticServiceProducerWithoutBinder<TServiceClass> serviceProducer) {
        ensureInitializing("registerStaticService");
        Preconditions.checkStringNotEmpty(serviceName);
        Objects.requireNonNull(serviceWrapperClass);
        Objects.requireNonNull(serviceProducer);
        registerService(serviceName, serviceWrapperClass, new StaticServiceFetcher<TServiceClass>() { // from class: android.app.SystemServiceRegistry.138
            /* JADX WARN: Type inference failed for: r0v1, types: [TServiceClass, java.lang.Object] */
            @Override // android.app.SystemServiceRegistry.StaticServiceFetcher
            public TServiceClass createService() {
                return StaticServiceProducerWithoutBinder.this.createService();
            }
        });
    }

    @SystemApi
    public static <TServiceClass> void registerContextAwareService(final String serviceName, Class<TServiceClass> serviceWrapperClass, final ContextAwareServiceProducerWithBinder<TServiceClass> serviceProducer) {
        ensureInitializing("registerContextAwareService");
        Preconditions.checkStringNotEmpty(serviceName);
        Objects.requireNonNull(serviceWrapperClass);
        Objects.requireNonNull(serviceProducer);
        registerService(serviceName, serviceWrapperClass, new CachedServiceFetcher<TServiceClass>() { // from class: android.app.SystemServiceRegistry.139
            /* JADX WARN: Type inference failed for: r0v1, types: [TServiceClass, java.lang.Object] */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TServiceClass createService(ContextImpl ctx) throws ServiceManager.ServiceNotFoundException {
                return ContextAwareServiceProducerWithBinder.this.createService(ctx.getOuterContext(), ServiceManager.getServiceOrThrow(serviceName));
            }
        });
    }

    @SystemApi
    public static <TServiceClass> void registerContextAwareService(String serviceName, Class<TServiceClass> serviceWrapperClass, final ContextAwareServiceProducerWithoutBinder<TServiceClass> serviceProducer) {
        ensureInitializing("registerContextAwareService");
        Preconditions.checkStringNotEmpty(serviceName);
        Objects.requireNonNull(serviceWrapperClass);
        Objects.requireNonNull(serviceProducer);
        registerService(serviceName, serviceWrapperClass, new CachedServiceFetcher<TServiceClass>() { // from class: android.app.SystemServiceRegistry.140
            /* JADX WARN: Type inference failed for: r0v1, types: [TServiceClass, java.lang.Object] */
            @Override // android.app.SystemServiceRegistry.CachedServiceFetcher
            public TServiceClass createService(ContextImpl ctx) {
                return ContextAwareServiceProducerWithoutBinder.this.createService(ctx.getOuterContext());
            }
        });
    }

    /* loaded from: classes.dex */
    static abstract class CachedServiceFetcher<T> implements ServiceFetcher<T> {
        private final int mCacheIndex;

        public abstract T createService(ContextImpl contextImpl) throws ServiceManager.ServiceNotFoundException;

        CachedServiceFetcher() {
            int i = SystemServiceRegistry.sServiceCacheSize;
            SystemServiceRegistry.sServiceCacheSize = i + 1;
            this.mCacheIndex = i;
        }

        /* JADX WARN: Code restructure failed: missing block: B:7:0x000e, code lost:
            r3 = r6;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Object[], java.lang.Object] */
        /* JADX WARN: Type inference failed for: r6v0 */
        @Override // android.app.SystemServiceRegistry.ServiceFetcher
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final T getService(ContextImpl ctx) {
            T ret;
            ?? r0 = ctx.mServiceCache;
            int[] gates = ctx.mServiceInitializationStateArray;
            boolean interrupted = false;
            while (true) {
                boolean doInitialize = false;
                synchronized (r0) {
                    int i = this.mCacheIndex;
                    ?? r6 = r0[i];
                    if (r6 != 0) {
                        break;
                    }
                    if (gates[i] == 2 || gates[i] == 3) {
                        gates[i] = 0;
                    }
                    if (gates[i] == 0) {
                        doInitialize = true;
                        gates[i] = 1;
                    }
                    if (doInitialize) {
                        T service = null;
                        int newState = 3;
                        try {
                            try {
                                T service2 = createService(ctx);
                                synchronized (r0) {
                                    int i2 = this.mCacheIndex;
                                    r0[i2] = service2;
                                    gates[i2] = 2;
                                    r0.notifyAll();
                                }
                                service = service2;
                                newState = 2;
                            } catch (Throwable th) {
                                synchronized (r0) {
                                    int i3 = this.mCacheIndex;
                                    r0[i3] = service;
                                    gates[i3] = newState;
                                    r0.notifyAll();
                                    throw th;
                                }
                            }
                        } catch (ServiceManager.ServiceNotFoundException e) {
                            SystemServiceRegistry.onServiceNotFound(e);
                            synchronized (r0) {
                                r0[this.mCacheIndex] = 0;
                                gates[this.mCacheIndex] = 3;
                                r0.notifyAll();
                            }
                        }
                        ret = service;
                        break;
                    }
                    synchronized (r0) {
                        while (gates[this.mCacheIndex] < 2) {
                            try {
                                interrupted |= Thread.interrupted();
                                r0.wait();
                            } catch (InterruptedException e2) {
                                Slog.m90w(SystemServiceRegistry.TAG, "getService() interrupted");
                                interrupted = true;
                            }
                        }
                    }
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
            return ret;
        }
    }

    /* loaded from: classes.dex */
    static abstract class StaticServiceFetcher<T> implements ServiceFetcher<T> {
        private T mCachedInstance;

        public abstract T createService() throws ServiceManager.ServiceNotFoundException;

        StaticServiceFetcher() {
        }

        @Override // android.app.SystemServiceRegistry.ServiceFetcher
        public final T getService(ContextImpl ctx) {
            T t;
            synchronized (this) {
                if (this.mCachedInstance == null) {
                    try {
                        this.mCachedInstance = createService();
                    } catch (ServiceManager.ServiceNotFoundException e) {
                        SystemServiceRegistry.onServiceNotFound(e);
                    }
                }
                t = this.mCachedInstance;
            }
            return t;
        }
    }

    public static void onServiceNotFound(ServiceManager.ServiceNotFoundException e) {
        if (Process.myUid() < 10000) {
            Log.wtf(TAG, e.getMessage(), e);
        } else {
            Log.m104w(TAG, e.getMessage());
        }
    }
}
