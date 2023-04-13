package com.android.internal.app;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.VoiceInteractor;
import android.app.admin.DevicePolicyEventLogger;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PermissionChecker;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.UserInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Bundle;
import android.p008os.PatternMatcher;
import android.p008os.RemoteException;
import android.p008os.StrictMode;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.C4057R;
import com.android.internal.app.AbstractMultiProfilePagerAdapter;
import com.android.internal.app.AbstractResolverComparator;
import com.android.internal.app.NoCrossProfileEmptyStateProvider;
import com.android.internal.app.ResolverListAdapter;
import com.android.internal.app.chooser.ChooserTargetInfo;
import com.android.internal.app.chooser.DisplayResolveInfo;
import com.android.internal.app.chooser.TargetInfo;
import com.android.internal.content.PackageMonitor;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.ResolverDrawerLayout;
import com.android.internal.widget.ViewPager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class ResolverActivity extends Activity implements ResolverListAdapter.ResolverListCommunicator {
    private static final boolean DEBUG = false;
    public static boolean ENABLE_TABBED_VIEW = true;
    static final String EXTRA_CALLING_USER = "com.android.internal.app.ResolverActivity.EXTRA_CALLING_USER";
    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    public static final String EXTRA_IS_AUDIO_CAPTURE_DEVICE = "is_audio_capture_device";
    protected static final String EXTRA_SELECTED_PROFILE = "com.android.internal.app.ResolverActivity.EXTRA_SELECTED_PROFILE";
    private static final String EXTRA_SHOW_FRAGMENT_ARGS = ":settings:show_fragment_args";
    private static final String LAST_SHOWN_TAB_KEY = "last_shown_tab_key";
    protected static final String METRICS_CATEGORY_CHOOSER = "intent_chooser";
    protected static final String METRICS_CATEGORY_RESOLVER = "intent_resolver";
    private static final String OPEN_LINKS_COMPONENT_KEY = "app_link_state";
    protected static final int PROFILE_PERSONAL = 0;
    protected static final int PROFILE_WORK = 1;
    private static final String TAB_TAG_PERSONAL = "personal";
    private static final String TAB_TAG_WORK = "work";
    private static final String TAG = "ResolverActivity";
    private Button mAlwaysButton;
    private UserHandle mCloneProfileUserHandle;
    private int mDefaultTitleResId;
    private Space mFooterSpacer;
    private UserHandle mHeaderCreatorUser;
    protected final ArrayList<Intent> mIntents;
    private final boolean mIsIntentPicker;
    private int mLastSelected;
    protected final LatencyTracker mLatencyTracker;
    protected int mLaunchedFromUid;
    private UserHandle mLaunchedFromUserHandle;
    private int mLayoutId;
    protected AbstractMultiProfilePagerAdapter mMultiProfilePagerAdapter;
    private AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener mOnSwitchOnWorkSelectedListener;
    private Button mOnceButton;
    private PackageMonitor mPersonalPackageMonitor;
    private UserHandle mPersonalProfileUserHandle;
    private PickTargetOptionRequest mPickOptionRequest;
    protected PackageManager mPm;
    private String mProfileSwitchMessage;
    protected View mProfileView;
    protected AbstractMultiProfilePagerAdapter.QuietModeManager mQuietModeManager;
    private String mReferrerPackage;
    private boolean mRegistered;
    protected ResolverDrawerLayout mResolverDrawerLayout;
    private boolean mResolvingHome;
    private boolean mRetainInOnStop;
    private boolean mSafeForwardingMode;
    protected boolean mSupportsAlwaysUseOption;
    protected Insets mSystemWindowInsets;
    private UserHandle mTabOwnerUserHandleForLaunch;
    private CharSequence mTitle;
    private PackageMonitor mWorkPackageMonitor;
    private boolean mWorkProfileHasBeenEnabled;
    private BroadcastReceiver mWorkProfileStateReceiver;
    private UserHandle mWorkProfileUserHandle;

    public ResolverActivity() {
        this.mLastSelected = -1;
        this.mResolvingHome = false;
        this.mIntents = new ArrayList<>();
        this.mSystemWindowInsets = null;
        this.mFooterSpacer = null;
        this.mWorkProfileHasBeenEnabled = false;
        this.mLatencyTracker = getLatencyTracker();
        this.mIsIntentPicker = getClass().equals(ResolverActivity.class);
    }

    protected ResolverActivity(boolean isIntentPicker) {
        this.mLastSelected = -1;
        this.mResolvingHome = false;
        this.mIntents = new ArrayList<>();
        this.mSystemWindowInsets = null;
        this.mFooterSpacer = null;
        this.mWorkProfileHasBeenEnabled = false;
        this.mLatencyTracker = getLatencyTracker();
        this.mIsIntentPicker = isIntentPicker;
    }

    private LatencyTracker getLatencyTracker() {
        return LatencyTracker.getInstance(this);
    }

    public static int getLabelRes(String action) {
        return ActionTitle.forAction(action).labelRes;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public enum ActionTitle {
        VIEW("android.intent.action.VIEW", C4057R.string.whichViewApplication, C4057R.string.whichViewApplicationNamed, C4057R.string.whichViewApplicationLabel),
        EDIT(Intent.ACTION_EDIT, C4057R.string.whichEditApplication, C4057R.string.whichEditApplicationNamed, C4057R.string.whichEditApplicationLabel),
        SEND(Intent.ACTION_SEND, C4057R.string.whichSendApplication, C4057R.string.whichSendApplicationNamed, C4057R.string.whichSendApplicationLabel),
        SENDTO(Intent.ACTION_SENDTO, C4057R.string.whichSendToApplication, C4057R.string.whichSendToApplicationNamed, C4057R.string.whichSendToApplicationLabel),
        SEND_MULTIPLE(Intent.ACTION_SEND_MULTIPLE, C4057R.string.whichSendApplication, C4057R.string.whichSendApplicationNamed, C4057R.string.whichSendApplicationLabel),
        CAPTURE_IMAGE("android.media.action.IMAGE_CAPTURE", C4057R.string.whichImageCaptureApplication, C4057R.string.whichImageCaptureApplicationNamed, C4057R.string.whichImageCaptureApplicationLabel),
        DEFAULT(null, C4057R.string.whichApplication, C4057R.string.whichApplicationNamed, C4057R.string.whichApplicationLabel),
        HOME(Intent.ACTION_MAIN, C4057R.string.whichHomeApplication, C4057R.string.whichHomeApplicationNamed, C4057R.string.whichHomeApplicationLabel);
        
        public static final int BROWSABLE_APP_TITLE_RES = 17041779;
        public static final int BROWSABLE_HOST_APP_TITLE_RES = 17041777;
        public static final int BROWSABLE_HOST_TITLE_RES = 17041776;
        public static final int BROWSABLE_TITLE_RES = 17041778;
        public final String action;
        public final int labelRes;
        public final int namedTitleRes;
        public final int titleRes;

        ActionTitle(String action, int titleRes, int namedTitleRes, int labelRes) {
            this.action = action;
            this.titleRes = titleRes;
            this.namedTitleRes = namedTitleRes;
            this.labelRes = labelRes;
        }

        public static ActionTitle forAction(String action) {
            ActionTitle[] values;
            for (ActionTitle title : values()) {
                if (title != HOME && action != null && action.equals(title.action)) {
                    return title;
                }
            }
            return DEFAULT;
        }
    }

    protected PackageMonitor createPackageMonitor(final ResolverListAdapter listAdapter) {
        return new PackageMonitor() { // from class: com.android.internal.app.ResolverActivity.1
            @Override // com.android.internal.content.PackageMonitor
            public void onSomePackagesChanged() {
                listAdapter.handlePackagesChanged();
                ResolverActivity.this.updateProfileViewButton();
            }

            @Override // com.android.internal.content.PackageMonitor
            public boolean onPackageChanged(String packageName, int uid, String[] components) {
                return true;
            }
        };
    }

    private Intent makeMyIntent() {
        Intent intent = new Intent(getIntent());
        intent.setComponent(null);
        intent.setFlags(intent.getFlags() & (-8388609));
        return intent;
    }

    protected void super_onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = makeMyIntent();
        Set<String> categories = intent.getCategories();
        if (Intent.ACTION_MAIN.equals(intent.getAction()) && categories != null && categories.size() == 1 && categories.contains(Intent.CATEGORY_HOME)) {
            this.mResolvingHome = true;
        }
        setSafeForwardingMode(true);
        onCreate(savedInstanceState, intent, null, 0, null, null, true);
    }

    protected void onCreate(Bundle savedInstanceState, Intent intent, CharSequence title, Intent[] initialIntents, List<ResolveInfo> rList, boolean supportsAlwaysUseOption) {
        onCreate(savedInstanceState, intent, title, 0, initialIntents, rList, supportsAlwaysUseOption);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState, Intent intent, CharSequence title, int defaultTitleRes, Intent[] initialIntents, List<ResolveInfo> rList, boolean supportsAlwaysUseOption) {
        int i;
        setTheme(appliedThemeResId());
        super.onCreate(savedInstanceState);
        this.mQuietModeManager = createQuietModeManager();
        setProfileSwitchMessage(intent.getContentUserHint());
        int launchedFromUid = getLaunchedFromUid();
        this.mLaunchedFromUid = launchedFromUid;
        this.mLaunchedFromUserHandle = UserHandle.getUserHandleForUid(launchedFromUid);
        int i2 = this.mLaunchedFromUid;
        if (i2 < 0 || UserHandle.isIsolated(i2)) {
            finish();
            return;
        }
        this.mPm = getPackageManager();
        this.mReferrerPackage = getReferrerPackageName();
        this.mIntents.add(0, new Intent(intent));
        this.mTitle = title;
        this.mDefaultTitleResId = defaultTitleRes;
        this.mSupportsAlwaysUseOption = supportsAlwaysUseOption;
        this.mPersonalProfileUserHandle = fetchPersonalProfileUserHandle();
        this.mWorkProfileUserHandle = fetchWorkProfileUserProfile();
        this.mCloneProfileUserHandle = fetchCloneProfileUserHandle();
        this.mTabOwnerUserHandleForLaunch = fetchTabOwnerUserHandleForLaunch();
        boolean filterLastUsed = (!this.mSupportsAlwaysUseOption || isVoiceInteraction() || shouldShowTabs() || hasCloneProfile()) ? false : true;
        this.mMultiProfilePagerAdapter = createMultiProfilePagerAdapter(initialIntents, rList, filterLastUsed);
        if (configureContentView()) {
            return;
        }
        PackageMonitor createPackageMonitor = createPackageMonitor(this.mMultiProfilePagerAdapter.getPersonalListAdapter());
        this.mPersonalPackageMonitor = createPackageMonitor;
        createPackageMonitor.register((Context) this, getMainLooper(), getPersonalProfileUserHandle(), false);
        if (shouldShowTabs()) {
            PackageMonitor createPackageMonitor2 = createPackageMonitor(this.mMultiProfilePagerAdapter.getWorkListAdapter());
            this.mWorkPackageMonitor = createPackageMonitor2;
            createPackageMonitor2.register((Context) this, getMainLooper(), getWorkProfileUserHandle(), false);
        }
        this.mRegistered = true;
        ResolverDrawerLayout rdl = (ResolverDrawerLayout) findViewById(C4057R.C4059id.contentPanel);
        if (rdl != null) {
            rdl.setOnDismissedListener(new ResolverDrawerLayout.OnDismissedListener() { // from class: com.android.internal.app.ResolverActivity.2
                @Override // com.android.internal.widget.ResolverDrawerLayout.OnDismissedListener
                public void onDismissed() {
                    ResolverActivity.this.finish();
                }
            });
            boolean hasTouchScreen = getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
            if (isVoiceInteraction() || !hasTouchScreen) {
                rdl.setCollapsed(false);
            }
            rdl.setSystemUiVisibility(768);
            rdl.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda3
                @Override // android.view.View.OnApplyWindowInsetsListener
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return ResolverActivity.this.onApplyWindowInsets(view, windowInsets);
                }
            });
            this.mResolverDrawerLayout = rdl;
        }
        View findViewById = findViewById(C4057R.C4059id.profile_button);
        this.mProfileView = findViewById;
        if (findViewById != null) {
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ResolverActivity.this.onProfileClick(view);
                }
            });
            updateProfileViewButton();
        }
        Set<String> categories = intent.getCategories();
        if (this.mMultiProfilePagerAdapter.getActiveListAdapter().hasFilteredItem()) {
            i = 451;
        } else {
            i = 453;
        }
        MetricsLogger.action(this, i, intent.getAction() + ":" + intent.getType() + ":" + (categories != null ? Arrays.toString(categories.toArray()) : ""));
    }

    protected AbstractMultiProfilePagerAdapter createMultiProfilePagerAdapter(Intent[] initialIntents, List<ResolveInfo> rList, boolean filterLastUsed) {
        if (shouldShowTabs()) {
            AbstractMultiProfilePagerAdapter resolverMultiProfilePagerAdapter = createResolverMultiProfilePagerAdapterForTwoProfiles(initialIntents, rList, filterLastUsed);
            return resolverMultiProfilePagerAdapter;
        }
        AbstractMultiProfilePagerAdapter resolverMultiProfilePagerAdapter2 = createResolverMultiProfilePagerAdapterForOneProfile(initialIntents, rList, filterLastUsed);
        return resolverMultiProfilePagerAdapter2;
    }

    protected AbstractMultiProfilePagerAdapter.MyUserIdProvider createMyUserIdProvider() {
        return new AbstractMultiProfilePagerAdapter.MyUserIdProvider();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMultiProfilePagerAdapter.CrossProfileIntentsChecker createCrossProfileIntentsChecker() {
        return new AbstractMultiProfilePagerAdapter.CrossProfileIntentsChecker(getContentResolver());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.app.ResolverActivity$3 */
    /* loaded from: classes4.dex */
    public class C41083 implements AbstractMultiProfilePagerAdapter.QuietModeManager {
        private boolean mIsWaitingToEnableWorkProfile = false;
        final /* synthetic */ UserManager val$userManager;

        C41083(UserManager userManager) {
            this.val$userManager = userManager;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.QuietModeManager
        public boolean isQuietModeEnabled(UserHandle workProfileUserHandle) {
            return this.val$userManager.isQuietModeEnabled(workProfileUserHandle);
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.QuietModeManager
        public void requestQuietModeEnabled(final boolean enabled, final UserHandle workProfileUserHandle) {
            Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;
            final UserManager userManager = this.val$userManager;
            executor.execute(new Runnable() { // from class: com.android.internal.app.ResolverActivity$3$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UserManager.this.requestQuietModeEnabled(enabled, workProfileUserHandle);
                }
            });
            this.mIsWaitingToEnableWorkProfile = true;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.QuietModeManager
        public void markWorkProfileEnabledBroadcastReceived() {
            this.mIsWaitingToEnableWorkProfile = false;
        }

        @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.QuietModeManager
        public boolean isWaitingToEnableWorkProfile() {
            return this.mIsWaitingToEnableWorkProfile;
        }
    }

    protected AbstractMultiProfilePagerAdapter.QuietModeManager createQuietModeManager() {
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        return new C41083(userManager);
    }

    protected AbstractMultiProfilePagerAdapter.EmptyStateProvider createBlockerEmptyStateProvider() {
        boolean shouldShowNoCrossProfileIntentsEmptyState = getUser().equals(getIntentUser());
        if (!shouldShowNoCrossProfileIntentsEmptyState) {
            return new AbstractMultiProfilePagerAdapter.EmptyStateProvider() { // from class: com.android.internal.app.ResolverActivity.4
            };
        }
        AbstractMultiProfilePagerAdapter.EmptyState noWorkToPersonalEmptyState = new NoCrossProfileEmptyStateProvider.DevicePolicyBlockerEmptyState(this, DevicePolicyResources.Strings.Core.RESOLVER_CROSS_PROFILE_BLOCKED_TITLE, C4057R.string.resolver_cross_profile_blocked, DevicePolicyResources.Strings.Core.RESOLVER_CANT_ACCESS_PERSONAL, C4057R.string.resolver_cant_access_personal_apps_explanation, 158, METRICS_CATEGORY_RESOLVER);
        AbstractMultiProfilePagerAdapter.EmptyState noPersonalToWorkEmptyState = new NoCrossProfileEmptyStateProvider.DevicePolicyBlockerEmptyState(this, DevicePolicyResources.Strings.Core.RESOLVER_CROSS_PROFILE_BLOCKED_TITLE, C4057R.string.resolver_cross_profile_blocked, DevicePolicyResources.Strings.Core.RESOLVER_CANT_ACCESS_WORK, C4057R.string.resolver_cant_access_work_apps_explanation, 159, METRICS_CATEGORY_RESOLVER);
        return new NoCrossProfileEmptyStateProvider(getPersonalProfileUserHandle(), noWorkToPersonalEmptyState, noPersonalToWorkEmptyState, createCrossProfileIntentsChecker(), getTabOwnerUserHandleForLaunch());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMultiProfilePagerAdapter.EmptyStateProvider createEmptyStateProvider(UserHandle workProfileUserHandle) {
        AbstractMultiProfilePagerAdapter.EmptyStateProvider blockerEmptyStateProvider = createBlockerEmptyStateProvider();
        AbstractMultiProfilePagerAdapter.EmptyStateProvider workProfileOffEmptyStateProvider = new WorkProfilePausedEmptyStateProvider(this, workProfileUserHandle, this.mQuietModeManager, new AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda2
            @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener
            public final void onSwitchOnWorkSelected() {
                ResolverActivity.this.lambda$createEmptyStateProvider$0();
            }
        }, getMetricsCategory());
        AbstractMultiProfilePagerAdapter.EmptyStateProvider noAppsEmptyStateProvider = new NoAppsAvailableEmptyStateProvider(this, workProfileUserHandle, getPersonalProfileUserHandle(), getMetricsCategory(), getTabOwnerUserHandleForLaunch());
        return new AbstractMultiProfilePagerAdapter.CompositeEmptyStateProvider(blockerEmptyStateProvider, workProfileOffEmptyStateProvider, noAppsEmptyStateProvider);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createEmptyStateProvider$0() {
        AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener onSwitchOnWorkSelectedListener = this.mOnSwitchOnWorkSelectedListener;
        if (onSwitchOnWorkSelectedListener != null) {
            onSwitchOnWorkSelectedListener.onSwitchOnWorkSelected();
        }
    }

    private ResolverMultiProfilePagerAdapter createResolverMultiProfilePagerAdapterForOneProfile(Intent[] initialIntents, List<ResolveInfo> rList, boolean filterLastUsed) {
        ResolverListAdapter adapter = createResolverListAdapter(this, this.mIntents, initialIntents, rList, filterLastUsed, getPersonalProfileUserHandle());
        AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager = createQuietModeManager();
        return new ResolverMultiProfilePagerAdapter(this, adapter, createEmptyStateProvider(null), quietModeManager, null, getCloneProfileUserHandle());
    }

    private UserHandle getIntentUser() {
        if (getIntent().hasExtra(EXTRA_CALLING_USER)) {
            return (UserHandle) getIntent().getParcelableExtra(EXTRA_CALLING_USER, UserHandle.class);
        }
        return getUser();
    }

    private ResolverMultiProfilePagerAdapter createResolverMultiProfilePagerAdapterForTwoProfiles(Intent[] initialIntents, List<ResolveInfo> rList, boolean filterLastUsed) {
        int selectedProfile;
        int selectedProfile2 = getCurrentProfile();
        UserHandle intentUser = getIntentUser();
        if (!getTabOwnerUserHandleForLaunch().equals(intentUser)) {
            if (getPersonalProfileUserHandle().equals(intentUser)) {
                selectedProfile = 0;
            } else {
                if (getWorkProfileUserHandle().equals(intentUser)) {
                    selectedProfile = 1;
                }
                selectedProfile = selectedProfile2;
            }
        } else {
            int selectedProfileExtra = getSelectedProfileExtra();
            if (selectedProfileExtra != -1) {
                selectedProfile = selectedProfileExtra;
            }
            selectedProfile = selectedProfile2;
        }
        ResolverListAdapter personalAdapter = createResolverListAdapter(this, this.mIntents, selectedProfile == 0 ? initialIntents : null, rList, filterLastUsed && UserHandle.myUserId() == getPersonalProfileUserHandle().getIdentifier(), getPersonalProfileUserHandle());
        UserHandle workProfileUserHandle = getWorkProfileUserHandle();
        ResolverListAdapter workAdapter = createResolverListAdapter(this, this.mIntents, selectedProfile == 1 ? initialIntents : null, rList, filterLastUsed && UserHandle.myUserId() == workProfileUserHandle.getIdentifier(), workProfileUserHandle);
        AbstractMultiProfilePagerAdapter.QuietModeManager quietModeManager = createQuietModeManager();
        return new ResolverMultiProfilePagerAdapter(this, personalAdapter, workAdapter, createEmptyStateProvider(getWorkProfileUserHandle()), quietModeManager, selectedProfile, getWorkProfileUserHandle(), getCloneProfileUserHandle());
    }

    protected int appliedThemeResId() {
        return C4057R.C4062style.Theme_DeviceDefault_Resolver;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSelectedProfileExtra() {
        int selectedProfile = -1;
        if (getIntent().hasExtra(EXTRA_SELECTED_PROFILE) && (selectedProfile = getIntent().getIntExtra(EXTRA_SELECTED_PROFILE, -1)) != 0 && selectedProfile != 1) {
            throw new IllegalArgumentException("com.android.internal.app.ResolverActivity.EXTRA_SELECTED_PROFILE has invalid value " + selectedProfile + ". Must be either ResolverActivity.PROFILE_PERSONAL or ResolverActivity.PROFILE_WORK.");
        }
        return selectedProfile;
    }

    protected int getCurrentProfile() {
        return UserHandle.myUserId() == getPersonalProfileUserHandle().getIdentifier() ? 0 : 1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UserHandle getPersonalProfileUserHandle() {
        return this.mPersonalProfileUserHandle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UserHandle getWorkProfileUserHandle() {
        return this.mWorkProfileUserHandle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UserHandle getCloneProfileUserHandle() {
        return this.mCloneProfileUserHandle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public UserHandle getTabOwnerUserHandleForLaunch() {
        return this.mTabOwnerUserHandleForLaunch;
    }

    protected UserHandle fetchPersonalProfileUserHandle() {
        UserHandle m145of = UserHandle.m145of(ActivityManager.getCurrentUser());
        this.mPersonalProfileUserHandle = m145of;
        return m145of;
    }

    protected UserHandle fetchWorkProfileUserProfile() {
        this.mWorkProfileUserHandle = null;
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        for (UserInfo userInfo : userManager.getProfiles(this.mPersonalProfileUserHandle.getIdentifier())) {
            if (userInfo.isManagedProfile()) {
                this.mWorkProfileUserHandle = userInfo.getUserHandle();
            }
        }
        return this.mWorkProfileUserHandle;
    }

    protected UserHandle fetchCloneProfileUserHandle() {
        this.mCloneProfileUserHandle = null;
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        for (UserInfo userInfo : userManager.getProfiles(this.mPersonalProfileUserHandle.getIdentifier())) {
            if (userInfo.isCloneProfile()) {
                this.mCloneProfileUserHandle = userInfo.getUserHandle();
            }
        }
        return this.mCloneProfileUserHandle;
    }

    private UserHandle fetchTabOwnerUserHandleForLaunch() {
        if (UserHandle.m145of(UserHandle.myUserId()).equals(getWorkProfileUserHandle())) {
            return getWorkProfileUserHandle();
        }
        return getPersonalProfileUserHandle();
    }

    private boolean hasWorkProfile() {
        return getWorkProfileUserHandle() != null;
    }

    private boolean hasCloneProfile() {
        return getCloneProfileUserHandle() != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final boolean isLaunchedAsCloneProfile() {
        return hasCloneProfile() && UserHandle.myUserId() == getCloneProfileUserHandle().getIdentifier();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldShowTabs() {
        return hasWorkProfile() && ENABLE_TABBED_VIEW;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onProfileClick(View v) {
        DisplayResolveInfo dri = this.mMultiProfilePagerAdapter.getActiveListAdapter().getOtherProfile();
        if (dri == null) {
            return;
        }
        this.mProfileSwitchMessage = null;
        onTargetSelected(dri, false);
        finish();
    }

    protected boolean shouldAddFooterView() {
        View buttonBar;
        return useLayoutWithDefault() || (buttonBar = findViewById(C4057R.C4059id.button_bar)) == null || buttonBar.getVisibility() == 8;
    }

    protected void applyFooterView(int height) {
        if (this.mFooterSpacer == null) {
            this.mFooterSpacer = new Space(getApplicationContext());
        } else {
            ((ResolverMultiProfilePagerAdapter) this.mMultiProfilePagerAdapter).getActiveAdapterView().removeFooterView(this.mFooterSpacer);
        }
        this.mFooterSpacer.setLayoutParams(new AbsListView.LayoutParams(-1, this.mSystemWindowInsets.bottom));
        ((ResolverMultiProfilePagerAdapter) this.mMultiProfilePagerAdapter).getActiveAdapterView().addFooterView(this.mFooterSpacer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
        Insets systemWindowInsets = insets.getSystemWindowInsets();
        this.mSystemWindowInsets = systemWindowInsets;
        this.mResolverDrawerLayout.setPadding(systemWindowInsets.left, this.mSystemWindowInsets.top, this.mSystemWindowInsets.right, 0);
        resetButtonBar();
        if (shouldUseMiniResolver()) {
            View buttonContainer = findViewById(C4057R.C4059id.button_bar_container);
            buttonContainer.setPadding(0, 0, 0, this.mSystemWindowInsets.bottom + getResources().getDimensionPixelOffset(C4057R.dimen.resolver_button_bar_spacing));
        }
        if (shouldAddFooterView()) {
            applyFooterView(this.mSystemWindowInsets.bottom);
        }
        return insets.consumeSystemWindowInsets();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mMultiProfilePagerAdapter.getActiveListAdapter().handlePackagesChanged();
        if (this.mIsIntentPicker && shouldShowTabs() && !useLayoutWithDefault() && !shouldUseMiniResolver()) {
            updateIntentPickerPaddings();
        }
        Insets insets = this.mSystemWindowInsets;
        if (insets != null) {
            this.mResolverDrawerLayout.setPadding(insets.left, this.mSystemWindowInsets.top, this.mSystemWindowInsets.right, 0);
        }
    }

    private void updateIntentPickerPaddings() {
        View titleCont = findViewById(C4057R.C4059id.title_container);
        titleCont.setPadding(titleCont.getPaddingLeft(), titleCont.getPaddingTop(), titleCont.getPaddingRight(), getResources().getDimensionPixelSize(C4057R.dimen.resolver_title_padding_bottom));
        View buttonBar = findViewById(C4057R.C4059id.button_bar);
        buttonBar.setPadding(buttonBar.getPaddingLeft(), getResources().getDimensionPixelSize(C4057R.dimen.resolver_button_bar_spacing), buttonBar.getPaddingRight(), getResources().getDimensionPixelSize(C4057R.dimen.resolver_button_bar_spacing));
    }

    @Override // com.android.internal.app.ResolverListAdapter.ResolverListCommunicator
    public void sendVoiceChoicesIfNeeded() {
        if (!isVoiceInteraction()) {
            return;
        }
        int count = this.mMultiProfilePagerAdapter.getActiveListAdapter().getCount();
        VoiceInteractor.PickOptionRequest.Option[] options = new VoiceInteractor.PickOptionRequest.Option[count];
        int N = options.length;
        for (int i = 0; i < N; i++) {
            TargetInfo target = this.mMultiProfilePagerAdapter.getActiveListAdapter().getItem(i);
            if (target == null) {
                return;
            }
            options[i] = optionForChooserTarget(target, i);
        }
        this.mPickOptionRequest = new PickTargetOptionRequest(new VoiceInteractor.Prompt(getTitle()), options, null);
        getVoiceInteractor().submitRequest(this.mPickOptionRequest);
    }

    VoiceInteractor.PickOptionRequest.Option optionForChooserTarget(TargetInfo target, int index) {
        return new VoiceInteractor.PickOptionRequest.Option(target.getDisplayLabel(), index);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setAdditionalTargets(Intent[] intents) {
        if (intents != null) {
            for (Intent intent : intents) {
                this.mIntents.add(intent);
            }
        }
    }

    @Override // com.android.internal.app.ResolverListAdapter.ResolverListCommunicator
    public Intent getTargetIntent() {
        if (this.mIntents.isEmpty()) {
            return null;
        }
        return this.mIntents.get(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getReferrerPackageName() {
        Uri referrer = getReferrer();
        if (referrer != null && "android-app".equals(referrer.getScheme())) {
            return referrer.getHost();
        }
        return null;
    }

    public int getLayoutResource() {
        return C4057R.layout.resolver_list;
    }

    @Override // com.android.internal.app.ResolverListAdapter.ResolverListCommunicator
    public void updateProfileViewButton() {
        if (this.mProfileView == null) {
            return;
        }
        DisplayResolveInfo dri = this.mMultiProfilePagerAdapter.getActiveListAdapter().getOtherProfile();
        if (dri != null && !shouldShowTabs()) {
            this.mProfileView.setVisibility(0);
            View text = this.mProfileView.findViewById(C4057R.C4059id.profile_button);
            if (!(text instanceof TextView)) {
                text = this.mProfileView.findViewById(16908308);
            }
            ((TextView) text).setText(dri.getDisplayLabel());
            return;
        }
        this.mProfileView.setVisibility(8);
    }

    private void setProfileSwitchMessage(int contentUserHint) {
        if (contentUserHint != -2 && contentUserHint != UserHandle.myUserId()) {
            UserManager userManager = (UserManager) getSystemService("user");
            UserInfo originUserInfo = userManager.getUserInfo(contentUserHint);
            boolean originIsManaged = originUserInfo != null ? originUserInfo.isManagedProfile() : false;
            boolean targetIsManaged = userManager.isManagedProfile();
            if (originIsManaged && !targetIsManaged) {
                this.mProfileSwitchMessage = getForwardToPersonalMsg();
            } else if (!originIsManaged && targetIsManaged) {
                this.mProfileSwitchMessage = getForwardToWorkMsg();
            }
        }
    }

    private String getForwardToPersonalMsg() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.FORWARD_INTENT_TO_PERSONAL, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda6
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getForwardToPersonalMsg$1;
                lambda$getForwardToPersonalMsg$1 = ResolverActivity.this.lambda$getForwardToPersonalMsg$1();
                return lambda$getForwardToPersonalMsg$1;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getForwardToPersonalMsg$1() {
        return getString(C4057R.string.forward_intent_to_owner);
    }

    private String getForwardToWorkMsg() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.FORWARD_INTENT_TO_WORK, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda8
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getForwardToWorkMsg$2;
                lambda$getForwardToWorkMsg$2 = ResolverActivity.this.lambda$getForwardToWorkMsg$2();
                return lambda$getForwardToWorkMsg$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getForwardToWorkMsg$2() {
        return getString(C4057R.string.forward_intent_to_work);
    }

    public void setSafeForwardingMode(boolean safeForwarding) {
        this.mSafeForwardingMode = safeForwarding;
    }

    protected CharSequence getTitleForAction(Intent intent, int defaultTitleRes) {
        ActionTitle title;
        if (this.mResolvingHome) {
            title = ActionTitle.HOME;
        } else {
            title = ActionTitle.forAction(intent.getAction());
        }
        boolean named = this.mMultiProfilePagerAdapter.getActiveListAdapter().getFilteredPosition() >= 0;
        if (title == ActionTitle.DEFAULT && defaultTitleRes != 0) {
            return getString(defaultTitleRes);
        }
        if (named) {
            return getString(title.namedTitleRes, this.mMultiProfilePagerAdapter.getActiveListAdapter().getFilteredItem().getDisplayLabel());
        }
        return getString(title.titleRes);
    }

    void dismiss() {
        if (!isFinishing()) {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestart() {
        super.onRestart();
        if (!this.mRegistered) {
            this.mPersonalPackageMonitor.register((Context) this, getMainLooper(), getPersonalProfileUserHandle(), false);
            if (shouldShowTabs()) {
                if (this.mWorkPackageMonitor == null) {
                    this.mWorkPackageMonitor = createPackageMonitor(this.mMultiProfilePagerAdapter.getWorkListAdapter());
                }
                this.mWorkPackageMonitor.register((Context) this, getMainLooper(), getWorkProfileUserHandle(), false);
            }
            this.mRegistered = true;
        }
        if (shouldShowTabs() && this.mQuietModeManager.isWaitingToEnableWorkProfile() && this.mQuietModeManager.isQuietModeEnabled(getWorkProfileUserHandle())) {
            this.mQuietModeManager.markWorkProfileEnabledBroadcastReceived();
        }
        this.mMultiProfilePagerAdapter.getActiveListAdapter().handlePackagesChanged();
        updateProfileViewButton();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        getWindow().addSystemFlags(524288);
        if (shouldShowTabs()) {
            this.mWorkProfileStateReceiver = createWorkProfileStateReceiver();
            registerWorkProfileStateReceiver();
            this.mWorkProfileHasBeenEnabled = isWorkProfileEnabled();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isWorkProfileEnabled() {
        UserHandle workUserHandle = getWorkProfileUserHandle();
        UserManager userManager = (UserManager) getSystemService(UserManager.class);
        return !userManager.isQuietModeEnabled(workUserHandle) && userManager.isUserUnlocked(workUserHandle);
    }

    private void registerWorkProfileStateReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_UNLOCKED);
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_AVAILABLE);
        filter.addAction(Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE);
        registerReceiverAsUser(this.mWorkProfileStateReceiver, UserHandle.ALL, filter, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        Window window = getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.privateFlags &= -524289;
        window.setAttributes(attrs);
        if (this.mRegistered) {
            this.mPersonalPackageMonitor.unregister();
            PackageMonitor packageMonitor = this.mWorkPackageMonitor;
            if (packageMonitor != null) {
                packageMonitor.unregister();
            }
            this.mRegistered = false;
        }
        Intent intent = getIntent();
        if ((intent.getFlags() & 268435456) != 0 && !isVoiceInteraction() && !this.mResolvingHome && !this.mRetainInOnStop && !isChangingConfigurations()) {
            finish();
        }
        if (this.mWorkPackageMonitor != null) {
            unregisterReceiver(this.mWorkProfileStateReceiver);
            this.mWorkPackageMonitor = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onDestroy() {
        PickTargetOptionRequest pickTargetOptionRequest;
        super.onDestroy();
        if (!isChangingConfigurations() && (pickTargetOptionRequest = this.mPickOptionRequest) != null) {
            pickTargetOptionRequest.cancel();
        }
        AbstractMultiProfilePagerAdapter abstractMultiProfilePagerAdapter = this.mMultiProfilePagerAdapter;
        if (abstractMultiProfilePagerAdapter != null && abstractMultiProfilePagerAdapter.getActiveListAdapter() != null) {
            this.mMultiProfilePagerAdapter.getActiveListAdapter().onDestroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ViewPager viewPager = (ViewPager) findViewById(C4057R.C4059id.profile_pager);
        if (viewPager != null) {
            outState.putInt(LAST_SHOWN_TAB_KEY, viewPager.getCurrentItem());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resetButtonBar();
        ViewPager viewPager = (ViewPager) findViewById(C4057R.C4059id.profile_pager);
        if (viewPager != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(LAST_SHOWN_TAB_KEY));
        }
        this.mMultiProfilePagerAdapter.clearInactiveProfileCache();
    }

    private boolean hasManagedProfile() {
        UserManager userManager = (UserManager) getSystemService("user");
        if (userManager == null) {
            return false;
        }
        try {
            List<UserInfo> profiles = userManager.getProfiles(getUserId());
            for (UserInfo userInfo : profiles) {
                if (userInfo != null && userInfo.isManagedProfile()) {
                    return true;
                }
            }
            return false;
        } catch (SecurityException e) {
            return false;
        }
    }

    private boolean supportsManagedProfiles(ResolveInfo resolveInfo) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(resolveInfo.activityInfo.packageName, 0);
            return appInfo.targetSdkVersion >= 21;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlwaysButtonEnabled(boolean hasValidSelection, int checkedPos, boolean filtered) {
        if (!this.mMultiProfilePagerAdapter.getCurrentUserHandle().equals(getUser())) {
            this.mAlwaysButton.setEnabled(false);
        } else if (hasCloneProfile() && !this.mMultiProfilePagerAdapter.getCurrentUserHandle().equals(this.mWorkProfileUserHandle)) {
            this.mAlwaysButton.setEnabled(false);
        } else {
            boolean enabled = false;
            ResolveInfo ri = null;
            if (hasValidSelection) {
                ri = this.mMultiProfilePagerAdapter.getActiveListAdapter().resolveInfoForPosition(checkedPos, filtered);
                if (ri == null) {
                    Log.m110e(TAG, "Invalid position supplied to setAlwaysButtonEnabled");
                    return;
                } else if (ri.targetUserId != -2) {
                    Log.m110e(TAG, "Attempted to set selection to resolve info for another user");
                    return;
                } else {
                    enabled = true;
                    this.mAlwaysButton.setText(getResources().getString(C4057R.string.activity_resolver_use_always));
                }
            }
            if (ri != null) {
                ActivityInfo activityInfo = ri.activityInfo;
                boolean hasRecordPermission = this.mPm.checkPermission(Manifest.C0000permission.RECORD_AUDIO, activityInfo.packageName) == 0;
                if (!hasRecordPermission) {
                    boolean hasAudioCapture = getIntent().getBooleanExtra(EXTRA_IS_AUDIO_CAPTURE_DEVICE, false);
                    enabled = !hasAudioCapture;
                }
            }
            this.mAlwaysButton.setEnabled(enabled);
        }
    }

    public void onButtonClick(View v) {
        int which;
        int id = v.getId();
        ListView listView = (ListView) this.mMultiProfilePagerAdapter.getActiveAdapterView();
        ResolverListAdapter currentListAdapter = this.mMultiProfilePagerAdapter.getActiveListAdapter();
        if (currentListAdapter.hasFilteredItem()) {
            which = currentListAdapter.getFilteredPosition();
        } else {
            which = listView.getCheckedItemPosition();
        }
        boolean hasIndexBeenFiltered = !currentListAdapter.hasFilteredItem();
        startSelected(which, id == 16908842, hasIndexBeenFiltered);
    }

    public void startSelected(int which, boolean always, boolean hasIndexBeenFiltered) {
        int i;
        if (isFinishing()) {
            return;
        }
        ResolveInfo ri = this.mMultiProfilePagerAdapter.getActiveListAdapter().resolveInfoForPosition(which, hasIndexBeenFiltered);
        if (this.mResolvingHome && hasManagedProfile() && !supportsManagedProfiles(ri)) {
            Toast.makeText(this, getWorkProfileNotSupportedMsg(ri.activityInfo.loadLabel(getPackageManager()).toString()), 1).show();
            return;
        }
        TargetInfo target = this.mMultiProfilePagerAdapter.getActiveListAdapter().targetInfoForPosition(which, hasIndexBeenFiltered);
        if (target != null && onTargetSelected(target, always)) {
            if (always && this.mSupportsAlwaysUseOption) {
                MetricsLogger.action(this, (int) MetricsProto.MetricsEvent.ACTION_APP_DISAMBIG_ALWAYS);
            } else if (this.mSupportsAlwaysUseOption) {
                MetricsLogger.action(this, (int) MetricsProto.MetricsEvent.ACTION_APP_DISAMBIG_JUST_ONCE);
            } else {
                MetricsLogger.action(this, (int) MetricsProto.MetricsEvent.ACTION_APP_DISAMBIG_TAP);
            }
            if (this.mMultiProfilePagerAdapter.getActiveListAdapter().hasFilteredItem()) {
                i = 452;
            } else {
                i = MetricsProto.MetricsEvent.ACTION_HIDE_APP_DISAMBIG_NONE_FEATURED;
            }
            MetricsLogger.action(this, i);
            finish();
        }
    }

    private String getWorkProfileNotSupportedMsg(final String launcherName) {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.RESOLVER_WORK_PROFILE_NOT_SUPPORTED, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda9
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getWorkProfileNotSupportedMsg$3;
                lambda$getWorkProfileNotSupportedMsg$3 = ResolverActivity.this.lambda$getWorkProfileNotSupportedMsg$3(launcherName);
                return lambda$getWorkProfileNotSupportedMsg$3;
            }
        }, launcherName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getWorkProfileNotSupportedMsg$3(String launcherName) {
        return getString(C4057R.string.activity_resolver_work_profiles_support, launcherName);
    }

    public Intent getReplacementIntent(ActivityInfo aInfo, Intent defIntent) {
        return defIntent;
    }

    @Override // com.android.internal.app.ResolverListAdapter.ResolverListCommunicator
    public final void onPostListReady(ResolverListAdapter listAdapter, boolean doPostProcessing, boolean rebuildCompleted) {
        if (isDestroyed() || isAutolaunching()) {
            return;
        }
        if (this.mIsIntentPicker) {
            ((ResolverMultiProfilePagerAdapter) this.mMultiProfilePagerAdapter).setUseLayoutWithDefault(useLayoutWithDefault());
        }
        if (this.mMultiProfilePagerAdapter.shouldShowEmptyStateScreen(listAdapter)) {
            this.mMultiProfilePagerAdapter.showEmptyResolverListEmptyState(listAdapter);
        } else {
            this.mMultiProfilePagerAdapter.showListView(listAdapter);
        }
        if ((!rebuildCompleted || !maybeAutolaunchActivity()) && doPostProcessing) {
            maybeCreateHeader(listAdapter);
            resetButtonBar();
            onListRebuilt(listAdapter, rebuildCompleted);
        }
    }

    protected void onListRebuilt(ResolverListAdapter listAdapter, boolean rebuildCompleted) {
        ResolverDrawerLayout rdl;
        int i;
        ItemClickListener listener = new ItemClickListener();
        setupAdapterListView((ListView) this.mMultiProfilePagerAdapter.getActiveAdapterView(), listener);
        if (shouldShowTabs() && this.mIsIntentPicker && (rdl = (ResolverDrawerLayout) findViewById(C4057R.C4059id.contentPanel)) != null) {
            Resources resources = getResources();
            if (useLayoutWithDefault()) {
                i = C4057R.dimen.resolver_max_collapsed_height_with_default_with_tabs;
            } else {
                i = C4057R.dimen.resolver_max_collapsed_height_with_tabs;
            }
            rdl.setMaxCollapsedHeight(resources.getDimensionPixelSize(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:117:0x01fb A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01d6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean onTargetSelected(TargetInfo target, boolean always) {
        Intent filterIntent;
        ComponentName[] set;
        int otherProfileMatch;
        String mimeType;
        ResolveInfo ri = target.getResolveInfo();
        Intent intent = target != null ? target.getResolvedIntent() : null;
        if (intent != null && ((this.mSupportsAlwaysUseOption || this.mMultiProfilePagerAdapter.getActiveListAdapter().hasFilteredItem()) && this.mMultiProfilePagerAdapter.getActiveListAdapter().getUnfilteredResolveList() != null)) {
            IntentFilter filter = new IntentFilter();
            if (intent.getSelector() != null) {
                filterIntent = intent.getSelector();
            } else {
                filterIntent = intent;
            }
            String action = filterIntent.getAction();
            if (action != null) {
                filter.addAction(action);
            }
            Set<String> categories = filterIntent.getCategories();
            if (categories != null) {
                for (String cat : categories) {
                    filter.addCategory(cat);
                }
            }
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            int cat2 = 268369920 & ri.match;
            Uri data = filterIntent.getData();
            if (cat2 == 6291456 && (mimeType = filterIntent.resolveType(this)) != null) {
                try {
                    filter.addDataType(mimeType);
                } catch (IntentFilter.MalformedMimeTypeException e) {
                    Log.m102w(TAG, e);
                    filter = null;
                }
            }
            if (data != null && data.getScheme() != null && (cat2 != 6291456 || (!"file".equals(data.getScheme()) && !"content".equals(data.getScheme())))) {
                filter.addDataScheme(data.getScheme());
                Iterator<PatternMatcher> pIt = ri.filter.schemeSpecificPartsIterator();
                if (pIt != null) {
                    String ssp = data.getSchemeSpecificPart();
                    while (true) {
                        if (ssp == null || !pIt.hasNext()) {
                            break;
                        }
                        PatternMatcher p = pIt.next();
                        if (p.match(ssp)) {
                            filter.addDataSchemeSpecificPart(p.getPath(), p.getType());
                            break;
                        }
                    }
                }
                Iterator<IntentFilter.AuthorityEntry> aIt = ri.filter.authoritiesIterator();
                if (aIt != null) {
                    while (true) {
                        if (!aIt.hasNext()) {
                            break;
                        }
                        IntentFilter.AuthorityEntry a = aIt.next();
                        if (a.match(data) >= 0) {
                            int port = a.getPort();
                            filter.addDataAuthority(a.getHost(), port >= 0 ? Integer.toString(port) : null);
                        }
                    }
                }
                Iterator<PatternMatcher> pIt2 = ri.filter.pathsIterator();
                if (pIt2 != null) {
                    String path = data.getPath();
                    while (true) {
                        if (path == null || !pIt2.hasNext()) {
                            break;
                        }
                        PatternMatcher p2 = pIt2.next();
                        if (p2.match(path)) {
                            filter.addDataPath(p2.getPath(), p2.getType());
                            break;
                        }
                    }
                }
            }
            if (filter != null) {
                int N = this.mMultiProfilePagerAdapter.getActiveListAdapter().getUnfilteredResolveList().size();
                boolean needToAddBackProfileForwardingComponent = this.mMultiProfilePagerAdapter.getActiveListAdapter().getOtherProfile() != null;
                if (!needToAddBackProfileForwardingComponent) {
                    set = new ComponentName[N];
                } else {
                    set = new ComponentName[N + 1];
                }
                int bestMatch = 0;
                int i = 0;
                while (i < N) {
                    Intent filterIntent2 = filterIntent;
                    ResolveInfo r = this.mMultiProfilePagerAdapter.getActiveListAdapter().getUnfilteredResolveList().get(i).getResolveInfoAt(0);
                    String action2 = action;
                    Set<String> categories2 = categories;
                    set[i] = new ComponentName(r.activityInfo.packageName, r.activityInfo.name);
                    if (r.match > bestMatch) {
                        bestMatch = r.match;
                    }
                    i++;
                    filterIntent = filterIntent2;
                    action = action2;
                    categories = categories2;
                }
                if (needToAddBackProfileForwardingComponent) {
                    set[N] = this.mMultiProfilePagerAdapter.getActiveListAdapter().getOtherProfile().getResolvedComponentName();
                    otherProfileMatch = this.mMultiProfilePagerAdapter.getActiveListAdapter().getOtherProfile().getResolveInfo().match;
                    if (otherProfileMatch > bestMatch) {
                        if (!always) {
                            int userId = getUserId();
                            PackageManager pm = getPackageManager();
                            pm.addUniquePreferredActivity(filter, otherProfileMatch, set, intent.getComponent());
                            if (ri.handleAllWebDataURI) {
                                String packageName = pm.getDefaultBrowserPackageNameAsUser(userId);
                                if (TextUtils.isEmpty(packageName)) {
                                    pm.setDefaultBrowserPackageNameAsUser(ri.activityInfo.packageName, userId);
                                }
                            }
                        } else {
                            try {
                                this.mMultiProfilePagerAdapter.getActiveListAdapter().mResolverListController.setLastChosen(intent, filter, otherProfileMatch);
                            } catch (RemoteException re) {
                                Log.m112d(TAG, "Error calling setLastChosenActivity\n" + re);
                            }
                        }
                    }
                }
                otherProfileMatch = bestMatch;
                if (!always) {
                }
            }
        }
        if (target != null) {
            safelyStartActivity(target);
            if (target.isSuspended()) {
                return false;
            }
            return true;
        }
        return true;
    }

    public final void safelyStartActivity(TargetInfo cti) {
        UserHandle activityUserHandle = getResolveInfoUserHandle(cti.getResolveInfo(), this.mMultiProfilePagerAdapter.getCurrentUserHandle());
        safelyStartActivityAsUser(cti, activityUserHandle, null);
    }

    public final void safelyStartActivityAsUser(TargetInfo cti, UserHandle user) {
        safelyStartActivityAsUser(cti, user, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void safelyStartActivityAsUser(TargetInfo cti, UserHandle user, Bundle options) {
        StrictMode.disableDeathOnFileUriExposure();
        try {
            safelyStartActivityInternal(cti, user, options);
        } finally {
            StrictMode.enableDeathOnFileUriExposure();
        }
    }

    protected void safelyStartActivityInternal(TargetInfo cti, UserHandle user, Bundle options) {
        if (!cti.isSuspended() && this.mRegistered) {
            PackageMonitor packageMonitor = this.mPersonalPackageMonitor;
            if (packageMonitor != null) {
                packageMonitor.unregister();
            }
            PackageMonitor packageMonitor2 = this.mWorkPackageMonitor;
            if (packageMonitor2 != null) {
                packageMonitor2.unregister();
            }
            this.mRegistered = false;
        }
        String str = this.mProfileSwitchMessage;
        if (str != null) {
            Toast.makeText(this, str, 1).show();
        }
        if (!this.mSafeForwardingMode) {
            if (cti.startAsUser(this, options, user)) {
                onActivityStarted(cti);
                maybeLogCrossProfileTargetLaunch(cti, user);
                return;
            }
            return;
        }
        try {
            if (cti.startAsCaller(this, options, user.getIdentifier())) {
                onActivityStarted(cti);
                maybeLogCrossProfileTargetLaunch(cti, user);
            }
        } catch (RuntimeException e) {
            Slog.wtf(TAG, "Unable to launch as uid " + this.mLaunchedFromUid + " package " + getLaunchedFromPackage() + ", while running in " + ActivityThread.currentProcessName(), e);
        }
    }

    private void maybeLogCrossProfileTargetLaunch(TargetInfo cti, UserHandle currentUserHandle) {
        if (!hasWorkProfile() || currentUserHandle.equals(getUser())) {
            return;
        }
        DevicePolicyEventLogger devicePolicyEventLogger = DevicePolicyEventLogger.createEvent(155).setBoolean(currentUserHandle.equals(getPersonalProfileUserHandle()));
        String[] strArr = new String[2];
        strArr[0] = getMetricsCategory();
        strArr[1] = cti instanceof ChooserTargetInfo ? ChooserActivity.LAUNCH_LOCATION_DIRECT_SHARE : "other_target";
        devicePolicyEventLogger.setStrings(strArr).write();
    }

    public void onActivityStarted(TargetInfo cti) {
    }

    public boolean shouldGetActivityMetadata() {
        return false;
    }

    public boolean shouldAutoLaunchSingleChoice(TargetInfo target) {
        return !target.isSuspended();
    }

    void showTargetDetails(ResolveInfo ri) {
        Intent in = new Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", ri.activityInfo.packageName, null)).addFlags(524288);
        startActivityAsUser(in, this.mMultiProfilePagerAdapter.getCurrentUserHandle());
    }

    protected ResolverListAdapter createResolverListAdapter(Context context, List<Intent> payloadIntents, Intent[] initialIntents, List<ResolveInfo> rList, boolean filterLastUsed, UserHandle userHandle) {
        UserHandle initialIntentsUserSpace;
        Intent startIntent = getIntent();
        boolean isAudioCaptureDevice = startIntent.getBooleanExtra(EXTRA_IS_AUDIO_CAPTURE_DEVICE, false);
        if (!isLaunchedAsCloneProfile() || !userHandle.equals(getPersonalProfileUserHandle())) {
            initialIntentsUserSpace = userHandle;
        } else {
            initialIntentsUserSpace = getCloneProfileUserHandle();
        }
        return new ResolverListAdapter(context, payloadIntents, initialIntents, rList, filterLastUsed, createListController(userHandle), this, isAudioCaptureDevice, initialIntentsUserSpace);
    }

    protected ResolverListController createListController(UserHandle userHandle) {
        UserHandle queryIntentsUser = getQueryIntentsUser(userHandle);
        ResolverRankerServiceResolverComparator resolverComparator = new ResolverRankerServiceResolverComparator(this, getTargetIntent(), getReferrerPackageName(), (AbstractResolverComparator.AfterCompute) null, (ChooserActivityLogger) null, getResolverRankerServiceUserHandleList(userHandle));
        return new ResolverListController(this, this.mPm, getTargetIntent(), getReferrerPackageName(), this.mLaunchedFromUid, userHandle, resolverComparator, queryIntentsUser);
    }

    private boolean configureContentView() {
        if (this.mMultiProfilePagerAdapter.getActiveListAdapter() == null) {
            throw new IllegalStateException("mMultiProfilePagerAdapter.getCurrentListAdapter() cannot be null.");
        }
        Trace.beginSection("configureContentView");
        boolean z = true;
        boolean rebuildCompleted = this.mMultiProfilePagerAdapter.rebuildActiveTab(true) || this.mMultiProfilePagerAdapter.getActiveListAdapter().isTabLoaded();
        if (shouldShowTabs()) {
            boolean rebuildInactiveCompleted = this.mMultiProfilePagerAdapter.rebuildInactiveTab(false) || this.mMultiProfilePagerAdapter.getInactiveListAdapter().isTabLoaded();
            if (!rebuildCompleted || !rebuildInactiveCompleted) {
                z = false;
            }
            rebuildCompleted = z;
        }
        if (shouldUseMiniResolver()) {
            configureMiniResolverContent();
            Trace.endSection();
            return false;
        }
        if (useLayoutWithDefault()) {
            this.mLayoutId = C4057R.layout.resolver_list_with_default;
        } else {
            this.mLayoutId = getLayoutResource();
        }
        setContentView(this.mLayoutId);
        this.mMultiProfilePagerAdapter.setupViewPager((ViewPager) findViewById(C4057R.C4059id.profile_pager));
        boolean result = postRebuildList(rebuildCompleted);
        Trace.endSection();
        return result;
    }

    private void configureMiniResolverContent() {
        this.mLayoutId = C4057R.layout.miniresolver;
        setContentView(C4057R.layout.miniresolver);
        final DisplayResolveInfo sameProfileResolveInfo = this.mMultiProfilePagerAdapter.getActiveListAdapter().mDisplayList.get(0);
        boolean inWorkProfile = getCurrentProfile() == 1;
        final ResolverListAdapter inactiveAdapter = this.mMultiProfilePagerAdapter.getInactiveListAdapter();
        final DisplayResolveInfo otherProfileResolveInfo = inactiveAdapter.mDisplayList.get(0);
        ImageView icon = (ImageView) findViewById(16908294);
        Objects.requireNonNull(inactiveAdapter);
        new ResolverListAdapter.LoadIconTask(inactiveAdapter, otherProfileResolveInfo, otherProfileResolveInfo, icon) { // from class: com.android.internal.app.ResolverActivity.5
            final /* synthetic */ ImageView val$icon;
            final /* synthetic */ DisplayResolveInfo val$otherProfileResolveInfo;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(otherProfileResolveInfo);
                this.val$otherProfileResolveInfo = otherProfileResolveInfo;
                this.val$icon = icon;
                Objects.requireNonNull(inactiveAdapter);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.android.internal.app.ResolverListAdapter.LoadIconTask, android.p008os.AsyncTask
            public void onPostExecute(Drawable drawable) {
                if (!ResolverActivity.this.isDestroyed()) {
                    this.val$otherProfileResolveInfo.setDisplayIcon(drawable);
                    new ResolverListAdapter.ViewHolder(this.val$icon).bindIcon(this.val$otherProfileResolveInfo);
                }
            }
        }.execute(new Void[0]);
        ((TextView) findViewById(C4057R.C4059id.open_cross_profile)).setText(getResources().getString(inWorkProfile ? C4057R.string.miniresolver_open_in_personal : C4057R.string.miniresolver_open_in_work, otherProfileResolveInfo.getDisplayLabel()));
        ((Button) findViewById(C4057R.C4059id.use_same_profile_browser)).setText(inWorkProfile ? C4057R.string.miniresolver_use_work_browser : C4057R.string.miniresolver_use_personal_browser);
        findViewById(C4057R.C4059id.use_same_profile_browser).setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ResolverActivity.this.lambda$configureMiniResolverContent$4(sameProfileResolveInfo, view);
            }
        });
        findViewById(C4057R.C4059id.button_open).setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda13
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ResolverActivity.this.lambda$configureMiniResolverContent$5(otherProfileResolveInfo, inactiveAdapter, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$configureMiniResolverContent$4(DisplayResolveInfo sameProfileResolveInfo, View v) {
        safelyStartActivity(sameProfileResolveInfo);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$configureMiniResolverContent$5(DisplayResolveInfo otherProfileResolveInfo, ResolverListAdapter inactiveAdapter, View v) {
        otherProfileResolveInfo.getResolvedIntent();
        safelyStartActivityAsUser(otherProfileResolveInfo, inactiveAdapter.mResolverListController.getUserHandle());
        finish();
    }

    private boolean shouldUseMiniResolver() {
        if (!this.mIsIntentPicker || this.mMultiProfilePagerAdapter.getActiveListAdapter() == null || this.mMultiProfilePagerAdapter.getInactiveListAdapter() == null) {
            return false;
        }
        List<DisplayResolveInfo> sameProfileList = this.mMultiProfilePagerAdapter.getActiveListAdapter().mDisplayList;
        List<DisplayResolveInfo> otherProfileList = this.mMultiProfilePagerAdapter.getInactiveListAdapter().mDisplayList;
        if (sameProfileList.isEmpty()) {
            Log.m112d(TAG, "No targets in the current profile");
            return false;
        } else if (otherProfileList.size() != 1) {
            Log.m112d(TAG, "Found " + otherProfileList.size() + " resolvers in the other profile");
            return false;
        } else if (otherProfileList.get(0).getResolveInfo().handleAllWebDataURI) {
            Log.m112d(TAG, "Other profile is a web browser");
            return false;
        } else {
            for (DisplayResolveInfo info : sameProfileList) {
                if (!info.getResolveInfo().handleAllWebDataURI) {
                    Log.m112d(TAG, "Non-browser found in this profile");
                    return false;
                }
            }
            return true;
        }
    }

    protected boolean postRebuildList(boolean rebuildCompleted) {
        return postRebuildListInternal(rebuildCompleted);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean postRebuildListInternal(boolean rebuildCompleted) {
        this.mMultiProfilePagerAdapter.getActiveListAdapter().getUnfilteredCount();
        if (rebuildCompleted && maybeAutolaunchActivity()) {
            return true;
        }
        setupViewVisibilities();
        if (shouldShowTabs()) {
            setupProfileTabs();
            return false;
        }
        return false;
    }

    private int isPermissionGranted(String permission, int uid) {
        return ActivityManager.checkComponentPermission(permission, uid, -1, true);
    }

    private boolean maybeAutolaunchActivity() {
        int numberOfProfiles = this.mMultiProfilePagerAdapter.getItemCount();
        if (numberOfProfiles == 1 && maybeAutolaunchIfSingleTarget()) {
            return true;
        }
        if (numberOfProfiles == 2 && this.mMultiProfilePagerAdapter.getActiveListAdapter().isTabLoaded() && this.mMultiProfilePagerAdapter.getInactiveListAdapter().isTabLoaded()) {
            if (maybeAutolaunchIfNoAppsOnInactiveTab() || maybeAutolaunchIfCrossProfileSupported()) {
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean maybeAutolaunchIfSingleTarget() {
        int count = this.mMultiProfilePagerAdapter.getActiveListAdapter().getUnfilteredCount();
        if (count != 1 || this.mMultiProfilePagerAdapter.getActiveListAdapter().getOtherProfile() != null) {
            return false;
        }
        TargetInfo target = this.mMultiProfilePagerAdapter.getActiveListAdapter().targetInfoForPosition(0, false);
        if (!shouldAutoLaunchSingleChoice(target)) {
            return false;
        }
        safelyStartActivity(target);
        finish();
        return true;
    }

    private boolean maybeAutolaunchIfNoAppsOnInactiveTab() {
        int count = this.mMultiProfilePagerAdapter.getActiveListAdapter().getUnfilteredCount();
        if (count != 1) {
            return false;
        }
        ResolverListAdapter inactiveListAdapter = this.mMultiProfilePagerAdapter.getInactiveListAdapter();
        if (inactiveListAdapter.getUnfilteredCount() != 0) {
            return false;
        }
        TargetInfo target = this.mMultiProfilePagerAdapter.getActiveListAdapter().targetInfoForPosition(0, false);
        safelyStartActivity(target);
        finish();
        return true;
    }

    private boolean maybeAutolaunchIfCrossProfileSupported() {
        ResolverListAdapter activeListAdapter = this.mMultiProfilePagerAdapter.getActiveListAdapter();
        int count = activeListAdapter.getUnfilteredCount();
        if (count != 1) {
            return false;
        }
        ResolverListAdapter inactiveListAdapter = this.mMultiProfilePagerAdapter.getInactiveListAdapter();
        if (inactiveListAdapter.getUnfilteredCount() != 1) {
            return false;
        }
        TargetInfo activeProfileTarget = activeListAdapter.targetInfoForPosition(0, false);
        TargetInfo inactiveProfileTarget = inactiveListAdapter.targetInfoForPosition(0, false);
        if (!Objects.equals(activeProfileTarget.getResolvedComponentName(), inactiveProfileTarget.getResolvedComponentName()) || !shouldAutoLaunchSingleChoice(activeProfileTarget)) {
            return false;
        }
        String packageName = activeProfileTarget.getResolvedComponentName().getPackageName();
        if (!canAppInteractCrossProfiles(packageName)) {
            return false;
        }
        DevicePolicyEventLogger.createEvent(161).setBoolean(activeListAdapter.getUserHandle().equals(getPersonalProfileUserHandle())).setStrings(getMetricsCategory()).write();
        safelyStartActivity(activeProfileTarget);
        finish();
        return true;
    }

    private boolean canAppInteractCrossProfiles(String packageName) {
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(packageName, 0);
            if (!applicationInfo.crossProfile) {
                return false;
            }
            int packageUid = applicationInfo.uid;
            return isPermissionGranted(Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL, packageUid) == 0 || isPermissionGranted(Manifest.C0000permission.INTERACT_ACROSS_USERS, packageUid) == 0 || PermissionChecker.checkPermissionForPreflight(this, Manifest.C0000permission.INTERACT_ACROSS_PROFILES, -1, packageUid, packageName) == 0;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(TAG, "Package " + packageName + " does not exist on current user.");
            return false;
        }
    }

    private boolean isAutolaunching() {
        return !this.mRegistered && isFinishing();
    }

    private void setupProfileTabs() {
        maybeHideDivider();
        final TabHost tabHost = (TabHost) findViewById(C4057R.C4059id.profile_tabhost);
        tabHost.setup();
        final ViewPager viewPager = (ViewPager) findViewById(C4057R.C4059id.profile_pager);
        viewPager.setSaveEnabled(false);
        Button personalButton = (Button) getLayoutInflater().inflate(C4057R.layout.resolver_profile_tab_button, (ViewGroup) tabHost.getTabWidget(), false);
        personalButton.setText(getPersonalTabLabel());
        personalButton.setContentDescription(getPersonalTabAccessibilityLabel());
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("personal").setContent(C4057R.C4059id.profile_pager).setIndicator(personalButton);
        tabHost.addTab(tabSpec);
        Button workButton = (Button) getLayoutInflater().inflate(C4057R.layout.resolver_profile_tab_button, (ViewGroup) tabHost.getTabWidget(), false);
        workButton.setText(getWorkTabLabel());
        workButton.setContentDescription(getWorkTabAccessibilityLabel());
        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec(TAB_TAG_WORK).setContent(C4057R.C4059id.profile_pager).setIndicator(workButton);
        tabHost.addTab(tabSpec2);
        TabWidget tabWidget = tabHost.getTabWidget();
        tabWidget.setVisibility(0);
        updateActiveTabStyle(tabHost);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda10
            @Override // android.widget.TabHost.OnTabChangeListener
            public final void onTabChanged(String str) {
                ResolverActivity.this.lambda$setupProfileTabs$6(tabHost, viewPager, str);
            }
        });
        viewPager.setVisibility(0);
        tabHost.setCurrentTab(this.mMultiProfilePagerAdapter.getCurrentPage());
        this.mMultiProfilePagerAdapter.setOnProfileSelectedListener(new AbstractMultiProfilePagerAdapter.OnProfileSelectedListener() { // from class: com.android.internal.app.ResolverActivity.6
            @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.OnProfileSelectedListener
            public void onProfileSelected(int index) {
                tabHost.setCurrentTab(index);
                ResolverActivity.this.resetButtonBar();
                ResolverActivity.this.resetCheckedItem();
            }

            @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.OnProfileSelectedListener
            public void onProfilePageStateChanged(int state) {
                ResolverActivity.this.onHorizontalSwipeStateChanged(state);
            }
        });
        this.mOnSwitchOnWorkSelectedListener = new AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda11
            @Override // com.android.internal.app.AbstractMultiProfilePagerAdapter.OnSwitchOnWorkSelectedListener
            public final void onSwitchOnWorkSelected() {
                ResolverActivity.lambda$setupProfileTabs$7(TabHost.this);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setupProfileTabs$6(TabHost tabHost, ViewPager viewPager, String tabId) {
        updateActiveTabStyle(tabHost);
        if ("personal".equals(tabId)) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }
        setupViewVisibilities();
        maybeLogProfileChange();
        onProfileTabSelected();
        DevicePolicyEventLogger.createEvent(156).setInt(viewPager.getCurrentItem()).setStrings(getMetricsCategory()).write();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setupProfileTabs$7(TabHost tabHost) {
        View workTab = tabHost.getTabWidget().getChildAt(1);
        workTab.setFocusable(true);
        workTab.setFocusableInTouchMode(true);
        workTab.requestFocus();
    }

    private String getPersonalTabLabel() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.RESOLVER_PERSONAL_TAB, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getPersonalTabLabel$8;
                lambda$getPersonalTabLabel$8 = ResolverActivity.this.lambda$getPersonalTabLabel$8();
                return lambda$getPersonalTabLabel$8;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getPersonalTabLabel$8() {
        return getString(C4057R.string.resolver_personal_tab);
    }

    private String getWorkTabLabel() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.RESOLVER_WORK_TAB, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda7
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getWorkTabLabel$9;
                lambda$getWorkTabLabel$9 = ResolverActivity.this.lambda$getWorkTabLabel$9();
                return lambda$getWorkTabLabel$9;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getWorkTabLabel$9() {
        return getString(C4057R.string.resolver_work_tab);
    }

    void onHorizontalSwipeStateChanged(int state) {
    }

    private void maybeHideDivider() {
        View divider;
        if (!this.mIsIntentPicker || (divider = findViewById(C4057R.C4059id.divider)) == null) {
            return;
        }
        divider.setVisibility(8);
    }

    protected void onProfileTabSelected() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetCheckedItem() {
        if (!this.mIsIntentPicker) {
            return;
        }
        this.mLastSelected = -1;
        ListView inactiveListView = (ListView) this.mMultiProfilePagerAdapter.getInactiveAdapterView();
        if (inactiveListView.getCheckedItemCount() > 0) {
            inactiveListView.setItemChecked(inactiveListView.getCheckedItemPosition(), false);
        }
    }

    private String getPersonalTabAccessibilityLabel() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.RESOLVER_PERSONAL_TAB_ACCESSIBILITY, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getPersonalTabAccessibilityLabel$10;
                lambda$getPersonalTabAccessibilityLabel$10 = ResolverActivity.this.lambda$getPersonalTabAccessibilityLabel$10();
                return lambda$getPersonalTabAccessibilityLabel$10;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getPersonalTabAccessibilityLabel$10() {
        return getString(C4057R.string.resolver_personal_tab_accessibility);
    }

    private String getWorkTabAccessibilityLabel() {
        return ((DevicePolicyManager) getSystemService(DevicePolicyManager.class)).getResources().getString(DevicePolicyResources.Strings.Core.RESOLVER_WORK_TAB_ACCESSIBILITY, new Supplier() { // from class: com.android.internal.app.ResolverActivity$$ExternalSyntheticLambda5
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getWorkTabAccessibilityLabel$11;
                lambda$getWorkTabAccessibilityLabel$11 = ResolverActivity.this.lambda$getWorkTabAccessibilityLabel$11();
                return lambda$getWorkTabAccessibilityLabel$11;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$getWorkTabAccessibilityLabel$11() {
        return getString(C4057R.string.resolver_work_tab_accessibility);
    }

    private static int getAttrColor(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int colorAccent = ta.getColor(0, 0);
        ta.recycle();
        return colorAccent;
    }

    private void updateActiveTabStyle(TabHost tabHost) {
        int currentTab = tabHost.getCurrentTab();
        TextView selected = (TextView) tabHost.getTabWidget().getChildAt(currentTab);
        TextView unselected = (TextView) tabHost.getTabWidget().getChildAt(1 - currentTab);
        selected.setSelected(true);
        unselected.setSelected(false);
    }

    private void setupViewVisibilities() {
        ResolverListAdapter activeListAdapter = this.mMultiProfilePagerAdapter.getActiveListAdapter();
        if (!this.mMultiProfilePagerAdapter.shouldShowEmptyStateScreen(activeListAdapter)) {
            addUseDifferentAppLabelIfNecessary(activeListAdapter);
        }
    }

    public void addUseDifferentAppLabelIfNecessary(ResolverListAdapter adapter) {
        boolean useHeader = adapter.hasFilteredItem();
        if (useHeader) {
            FrameLayout stub = (FrameLayout) findViewById(C4057R.C4059id.stub);
            stub.setVisibility(0);
            TextView textView = (TextView) LayoutInflater.from(this).inflate(C4057R.layout.resolver_different_item_header, (ViewGroup) null, false);
            if (shouldShowTabs()) {
                textView.setGravity(17);
            }
            stub.addView(textView);
        }
    }

    private void setupAdapterListView(ListView listView, ItemClickListener listener) {
        listView.setOnItemClickListener(listener);
        listView.setOnItemLongClickListener(listener);
        if (this.mSupportsAlwaysUseOption) {
            listView.setChoiceMode(1);
        }
    }

    private void maybeCreateHeader(ResolverListAdapter listAdapter) {
        TextView titleView;
        if (this.mHeaderCreatorUser != null && !listAdapter.getUserHandle().equals(this.mHeaderCreatorUser)) {
            return;
        }
        if (!shouldShowTabs() && listAdapter.getCount() == 0 && listAdapter.getPlaceholderCount() == 0 && (titleView = (TextView) findViewById(16908310)) != null) {
            titleView.setVisibility(8);
        }
        CharSequence title = this.mTitle;
        if (title == null) {
            title = getTitleForAction(getTargetIntent(), this.mDefaultTitleResId);
        }
        if (!TextUtils.isEmpty(title)) {
            TextView titleView2 = (TextView) findViewById(16908310);
            if (titleView2 != null) {
                titleView2.setText(title);
            }
            setTitle(title);
        }
        ImageView iconView = (ImageView) findViewById(16908294);
        if (iconView != null) {
            listAdapter.loadFilteredItemIconTaskAsync(iconView);
        }
        this.mHeaderCreatorUser = listAdapter.getUserHandle();
    }

    protected void resetButtonBar() {
        if (!this.mSupportsAlwaysUseOption) {
            return;
        }
        ViewGroup buttonLayout = (ViewGroup) findViewById(C4057R.C4059id.button_bar);
        if (buttonLayout == null) {
            Log.m110e(TAG, "Layout unexpectedly does not have a button bar");
            return;
        }
        ResolverListAdapter activeListAdapter = this.mMultiProfilePagerAdapter.getActiveListAdapter();
        View buttonBarDivider = findViewById(C4057R.C4059id.resolver_button_bar_divider);
        if (!useLayoutWithDefault()) {
            Insets insets = this.mSystemWindowInsets;
            int inset = insets != null ? insets.bottom : 0;
            buttonLayout.setPadding(buttonLayout.getPaddingLeft(), buttonLayout.getPaddingTop(), buttonLayout.getPaddingRight(), getResources().getDimensionPixelSize(C4057R.dimen.resolver_button_bar_spacing) + inset);
        }
        if (activeListAdapter.isTabLoaded() && this.mMultiProfilePagerAdapter.shouldShowEmptyStateScreen(activeListAdapter) && !useLayoutWithDefault()) {
            buttonLayout.setVisibility(4);
            if (buttonBarDivider != null) {
                buttonBarDivider.setVisibility(4);
            }
            setButtonBarIgnoreOffset(false);
            return;
        }
        if (buttonBarDivider != null) {
            buttonBarDivider.setVisibility(0);
        }
        buttonLayout.setVisibility(0);
        setButtonBarIgnoreOffset(true);
        this.mOnceButton = (Button) buttonLayout.findViewById(C4057R.C4059id.button_once);
        this.mAlwaysButton = (Button) buttonLayout.findViewById(C4057R.C4059id.button_always);
        resetAlwaysOrOnceButtonBar();
    }

    private void setButtonBarIgnoreOffset(boolean ignoreOffset) {
        View buttonBarContainer = findViewById(C4057R.C4059id.button_bar_container);
        if (buttonBarContainer != null) {
            ResolverDrawerLayout.LayoutParams layoutParams = (ResolverDrawerLayout.LayoutParams) buttonBarContainer.getLayoutParams();
            layoutParams.ignoreOffset = ignoreOffset;
            buttonBarContainer.setLayoutParams(layoutParams);
        }
    }

    private void resetAlwaysOrOnceButtonBar() {
        setAlwaysButtonEnabled(false, -1, false);
        this.mOnceButton.setEnabled(false);
        int filteredPosition = this.mMultiProfilePagerAdapter.getActiveListAdapter().getFilteredPosition();
        if (useLayoutWithDefault() && filteredPosition != -1) {
            setAlwaysButtonEnabled(true, filteredPosition, false);
            this.mOnceButton.setEnabled(true);
            this.mOnceButton.requestFocus();
            return;
        }
        ListView currentAdapterView = (ListView) this.mMultiProfilePagerAdapter.getActiveAdapterView();
        if (currentAdapterView != null && currentAdapterView.getCheckedItemPosition() != -1) {
            setAlwaysButtonEnabled(true, currentAdapterView.getCheckedItemPosition(), true);
            this.mOnceButton.setEnabled(true);
        }
    }

    @Override // com.android.internal.app.ResolverListAdapter.ResolverListCommunicator
    public boolean useLayoutWithDefault() {
        boolean adapterForCurrentUserHasFilteredItem = this.mMultiProfilePagerAdapter.getListAdapterForUserHandle(getTabOwnerUserHandleForLaunch()).hasFilteredItem();
        return this.mSupportsAlwaysUseOption && adapterForCurrentUserHasFilteredItem;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRetainInOnStop(boolean retainInOnStop) {
        this.mRetainInOnStop = retainInOnStop;
    }

    @Override // com.android.internal.app.ResolverListAdapter.ResolverListCommunicator
    public boolean resolveInfoMatch(ResolveInfo lhs, ResolveInfo rhs) {
        return lhs == null ? rhs == null : lhs.activityInfo == null ? rhs.activityInfo == null : Objects.equals(lhs.activityInfo.name, rhs.activityInfo.name) && Objects.equals(lhs.activityInfo.packageName, rhs.activityInfo.packageName) && Objects.equals(getResolveInfoUserHandle(lhs, this.mMultiProfilePagerAdapter.getActiveListAdapter().getUserHandle()), getResolveInfoUserHandle(rhs, this.mMultiProfilePagerAdapter.getActiveListAdapter().getUserHandle()));
    }

    protected String getMetricsCategory() {
        return METRICS_CATEGORY_RESOLVER;
    }

    public void onHandlePackagesChanged(ResolverListAdapter listAdapter) {
        if (listAdapter == this.mMultiProfilePagerAdapter.getActiveListAdapter()) {
            if (listAdapter.getUserHandle().equals(getWorkProfileUserHandle()) && this.mQuietModeManager.isWaitingToEnableWorkProfile()) {
                return;
            }
            boolean listRebuilt = this.mMultiProfilePagerAdapter.rebuildActiveTab(true);
            if (listRebuilt) {
                ResolverListAdapter activeListAdapter = this.mMultiProfilePagerAdapter.getActiveListAdapter();
                activeListAdapter.notifyDataSetChanged();
                if (activeListAdapter.getCount() == 0 && !inactiveListAdapterHasItems()) {
                    finish();
                    return;
                }
                return;
            }
            return;
        }
        this.mMultiProfilePagerAdapter.clearInactiveProfileCache();
    }

    private boolean inactiveListAdapterHasItems() {
        return shouldShowTabs() && this.mMultiProfilePagerAdapter.getInactiveListAdapter().getCount() > 0;
    }

    private BroadcastReceiver createWorkProfileStateReceiver() {
        return new BroadcastReceiver() { // from class: com.android.internal.app.ResolverActivity.7
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!TextUtils.equals(action, Intent.ACTION_USER_UNLOCKED) && !TextUtils.equals(action, Intent.ACTION_MANAGED_PROFILE_UNAVAILABLE) && !TextUtils.equals(action, Intent.ACTION_MANAGED_PROFILE_AVAILABLE)) {
                    return;
                }
                int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
                if (userId != ResolverActivity.this.getWorkProfileUserHandle().getIdentifier()) {
                    return;
                }
                if (ResolverActivity.this.isWorkProfileEnabled()) {
                    if (ResolverActivity.this.mWorkProfileHasBeenEnabled) {
                        return;
                    }
                    ResolverActivity.this.mWorkProfileHasBeenEnabled = true;
                    ResolverActivity.this.mQuietModeManager.markWorkProfileEnabledBroadcastReceived();
                } else {
                    ResolverActivity.this.mWorkProfileHasBeenEnabled = false;
                }
                if (ResolverActivity.this.mMultiProfilePagerAdapter.getCurrentUserHandle().equals(ResolverActivity.this.getWorkProfileUserHandle())) {
                    ResolverActivity.this.mMultiProfilePagerAdapter.rebuildActiveTab(true);
                } else {
                    ResolverActivity.this.mMultiProfilePagerAdapter.clearInactiveProfileCache();
                }
            }
        };
    }

    /* loaded from: classes4.dex */
    public static final class ResolvedComponentInfo {
        private boolean mFixedAtTop;
        private boolean mPinned;
        public final ComponentName name;
        private final List<Intent> mIntents = new ArrayList();
        private final List<ResolveInfo> mResolveInfos = new ArrayList();

        public ResolvedComponentInfo(ComponentName name, Intent intent, ResolveInfo info) {
            this.name = name;
            add(intent, info);
        }

        public void add(Intent intent, ResolveInfo info) {
            this.mIntents.add(intent);
            this.mResolveInfos.add(info);
        }

        public int getCount() {
            return this.mIntents.size();
        }

        public Intent getIntentAt(int index) {
            if (index >= 0) {
                return this.mIntents.get(index);
            }
            return null;
        }

        public ResolveInfo getResolveInfoAt(int index) {
            if (index >= 0) {
                return this.mResolveInfos.get(index);
            }
            return null;
        }

        public int findIntent(Intent intent) {
            int N = this.mIntents.size();
            for (int i = 0; i < N; i++) {
                if (intent.equals(this.mIntents.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        public int findResolveInfo(ResolveInfo info) {
            int N = this.mResolveInfos.size();
            for (int i = 0; i < N; i++) {
                if (info.equals(this.mResolveInfos.get(i))) {
                    return i;
                }
            }
            return -1;
        }

        public boolean isPinned() {
            return this.mPinned;
        }

        public void setPinned(boolean pinned) {
            this.mPinned = pinned;
        }

        public boolean isFixedAtTop() {
            return this.mFixedAtTop;
        }

        public void setFixedAtTop(boolean isFixedAtTop) {
            this.mFixedAtTop = isFixedAtTop;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class ItemClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
        ItemClickListener() {
        }

        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = parent instanceof ListView ? (ListView) parent : null;
            if (listView != null) {
                position -= listView.getHeaderViewsCount();
            }
            if (position < 0 || ResolverActivity.this.mMultiProfilePagerAdapter.getActiveListAdapter().resolveInfoForPosition(position, true) == null) {
                return;
            }
            ListView currentAdapterView = (ListView) ResolverActivity.this.mMultiProfilePagerAdapter.getActiveAdapterView();
            int checkedPos = currentAdapterView.getCheckedItemPosition();
            boolean hasValidSelection = checkedPos != -1;
            if (ResolverActivity.this.useLayoutWithDefault() || ((hasValidSelection && ResolverActivity.this.mLastSelected == checkedPos) || ResolverActivity.this.mAlwaysButton == null)) {
                ResolverActivity.this.startSelected(position, false, true);
                return;
            }
            ResolverActivity.this.setAlwaysButtonEnabled(hasValidSelection, checkedPos, true);
            ResolverActivity.this.mOnceButton.setEnabled(hasValidSelection);
            if (hasValidSelection) {
                currentAdapterView.smoothScrollToPosition(checkedPos);
                ResolverActivity.this.mOnceButton.requestFocus();
            }
            ResolverActivity.this.mLastSelected = checkedPos;
        }

        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ListView listView = parent instanceof ListView ? (ListView) parent : null;
            if (listView != null) {
                position -= listView.getHeaderViewsCount();
            }
            if (position < 0) {
                return false;
            }
            ResolveInfo ri = ResolverActivity.this.mMultiProfilePagerAdapter.getActiveListAdapter().resolveInfoForPosition(position, true);
            ResolverActivity.this.showTargetDetails(ri);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static final boolean isSpecificUriMatch(int match) {
        int match2 = match & IntentFilter.MATCH_CATEGORY_MASK;
        return match2 >= 3145728 && match2 <= 5242880;
    }

    /* loaded from: classes4.dex */
    static class PickTargetOptionRequest extends VoiceInteractor.PickOptionRequest {
        public PickTargetOptionRequest(VoiceInteractor.Prompt prompt, VoiceInteractor.PickOptionRequest.Option[] options, Bundle extras) {
            super(prompt, options, extras);
        }

        @Override // android.app.VoiceInteractor.Request
        public void onCancel() {
            super.onCancel();
            ResolverActivity ra = (ResolverActivity) getActivity();
            if (ra != null) {
                ra.mPickOptionRequest = null;
                ra.finish();
            }
        }

        @Override // android.app.VoiceInteractor.PickOptionRequest
        public void onPickOptionResult(boolean finished, VoiceInteractor.PickOptionRequest.Option[] selections, Bundle result) {
            ResolverActivity ra;
            super.onPickOptionResult(finished, selections, result);
            if (selections.length == 1 && (ra = (ResolverActivity) getActivity()) != null) {
                TargetInfo ti = ra.mMultiProfilePagerAdapter.getActiveListAdapter().getItem(selections[0].getIndex());
                if (ra.onTargetSelected(ti, false)) {
                    ra.mPickOptionRequest = null;
                    ra.finish();
                }
            }
        }
    }

    protected void maybeLogProfileChange() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final UserHandle getQueryIntentsUser(UserHandle userHandle) {
        if (!isLaunchedAsCloneProfile() || !userHandle.equals(getPersonalProfileUserHandle())) {
            return userHandle;
        }
        UserHandle queryIntentsUser = getCloneProfileUserHandle();
        return queryIntentsUser;
    }

    public final List<UserHandle> getResolverRankerServiceUserHandleList(UserHandle userHandle) {
        return getResolverRankerServiceUserHandleListInternal(userHandle);
    }

    protected List<UserHandle> getResolverRankerServiceUserHandleListInternal(UserHandle userHandle) {
        List<UserHandle> userList = new ArrayList<>();
        userList.add(userHandle);
        if (userHandle.equals(getPersonalProfileUserHandle()) && getCloneProfileUserHandle() != null) {
            userList.add(getCloneProfileUserHandle());
        }
        return userList;
    }

    public static UserHandle getResolveInfoUserHandle(ResolveInfo resolveInfo, UserHandle predictedHandle) {
        if (resolveInfo.userHandle == null) {
            Log.m110e(TAG, "ResolveInfo with null UserHandle found: " + resolveInfo);
        }
        return resolveInfo.userHandle;
    }
}
