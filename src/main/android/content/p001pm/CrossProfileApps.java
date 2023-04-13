package android.content.p001pm;

import android.annotation.SystemApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.admin.DevicePolicyResources;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.provider.Settings;
import com.android.internal.C4057R;
import com.android.internal.util.UserIcons;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
/* renamed from: android.content.pm.CrossProfileApps */
/* loaded from: classes.dex */
public class CrossProfileApps {
    public static final String ACTION_CAN_INTERACT_ACROSS_PROFILES_CHANGED = "android.content.pm.action.CAN_INTERACT_ACROSS_PROFILES_CHANGED";
    private final Context mContext;
    private final Resources mResources;
    private final ICrossProfileApps mService;
    private final UserManager mUserManager;

    public CrossProfileApps(Context context, ICrossProfileApps service) {
        this.mContext = context;
        this.mService = service;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mResources = context.getResources();
    }

    public void startMainActivity(ComponentName component, UserHandle targetUser) {
        try {
            this.mService.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getPackageName(), this.mContext.getAttributionTag(), component, targetUser.getIdentifier(), true, null, null);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void startMainActivity(ComponentName component, UserHandle targetUser, Activity callingActivity, Bundle options) {
        try {
            this.mService.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getPackageName(), this.mContext.getAttributionTag(), component, targetUser.getIdentifier(), true, callingActivity != null ? callingActivity.getActivityToken() : null, options);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void startActivity(Intent intent, UserHandle targetUser, Activity callingActivity) {
        startActivity(intent, targetUser, callingActivity, (Bundle) null);
    }

    public void startActivity(Intent intent, UserHandle targetUser, Activity callingActivity, Bundle options) {
        try {
            this.mService.startActivityAsUserByIntent(this.mContext.getIApplicationThread(), this.mContext.getPackageName(), this.mContext.getAttributionTag(), intent, targetUser.getIdentifier(), callingActivity != null ? callingActivity.getActivityToken() : null, options);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void startActivity(ComponentName component, UserHandle targetUser, Activity callingActivity, Bundle options) {
        try {
            this.mService.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getPackageName(), this.mContext.getAttributionTag(), component, targetUser.getIdentifier(), false, callingActivity != null ? callingActivity.getActivityToken() : null, options);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void startActivity(ComponentName component, UserHandle targetUser) {
        try {
            this.mService.startActivityAsUser(this.mContext.getIApplicationThread(), this.mContext.getPackageName(), this.mContext.getAttributionTag(), component, targetUser.getIdentifier(), false, null, null);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public List<UserHandle> getTargetUserProfiles() {
        try {
            return this.mService.getTargetUserProfiles(this.mContext.getPackageName());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public CharSequence getProfileSwitchingLabel(UserHandle userHandle) {
        verifyCanAccessUser(userHandle);
        final boolean isManagedProfile = this.mUserManager.isManagedProfile(userHandle.getIdentifier());
        DevicePolicyManager dpm = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class);
        return dpm.getResources().getString(getUpdatableProfileSwitchingLabelId(isManagedProfile), new Supplier() { // from class: android.content.pm.CrossProfileApps$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$getProfileSwitchingLabel$0;
                lambda$getProfileSwitchingLabel$0 = CrossProfileApps.this.lambda$getProfileSwitchingLabel$0(isManagedProfile);
                return lambda$getProfileSwitchingLabel$0;
            }
        });
    }

    private String getUpdatableProfileSwitchingLabelId(boolean isManagedProfile) {
        return isManagedProfile ? DevicePolicyResources.Strings.Core.SWITCH_TO_WORK_LABEL : DevicePolicyResources.Strings.Core.SWITCH_TO_PERSONAL_LABEL;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getDefaultProfileSwitchingLabel */
    public String lambda$getProfileSwitchingLabel$0(boolean isManagedProfile) {
        int stringRes = isManagedProfile ? C4057R.string.managed_profile_label : C4057R.string.user_owner_label;
        return this.mResources.getString(stringRes);
    }

    public Drawable getProfileSwitchingIconDrawable(UserHandle userHandle) {
        verifyCanAccessUser(userHandle);
        boolean isManagedProfile = this.mUserManager.isManagedProfile(userHandle.getIdentifier());
        return isManagedProfile ? this.mContext.getPackageManager().getUserBadgeForDensityNoBackground(userHandle, 0) : UserIcons.getDefaultUserIcon(this.mResources, 0, true);
    }

    public boolean canRequestInteractAcrossProfiles() {
        try {
            return this.mService.canRequestInteractAcrossProfiles(this.mContext.getPackageName());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean canInteractAcrossProfiles() {
        try {
            return this.mService.canInteractAcrossProfiles(this.mContext.getPackageName());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Intent createRequestInteractAcrossProfilesIntent() {
        if (!canRequestInteractAcrossProfiles()) {
            throw new SecurityException("The calling package can not request to interact across profiles.");
        }
        Intent settingsIntent = new Intent();
        settingsIntent.setAction(Settings.ACTION_MANAGE_CROSS_PROFILE_ACCESS);
        Uri packageUri = Uri.parse("package:" + this.mContext.getPackageName());
        settingsIntent.setData(packageUri);
        return settingsIntent;
    }

    public void setInteractAcrossProfilesAppOp(String packageName, int newMode) {
        try {
            this.mService.setInteractAcrossProfilesAppOp(this.mContext.getUserId(), packageName, newMode);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean canConfigureInteractAcrossProfiles(String packageName) {
        try {
            return this.mService.canConfigureInteractAcrossProfiles(this.mContext.getUserId(), packageName);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public boolean canUserAttemptToConfigureInteractAcrossProfiles(String packageName) {
        try {
            return this.mService.canUserAttemptToConfigureInteractAcrossProfiles(this.mContext.getUserId(), packageName);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void resetInteractAcrossProfilesAppOps(Collection<String> previousCrossProfilePackages, final Set<String> newCrossProfilePackages) {
        if (previousCrossProfilePackages.isEmpty()) {
            return;
        }
        List<String> unsetCrossProfilePackages = (List) previousCrossProfilePackages.stream().filter(new Predicate() { // from class: android.content.pm.CrossProfileApps$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CrossProfileApps.lambda$resetInteractAcrossProfilesAppOps$1(newCrossProfilePackages, (String) obj);
            }
        }).collect(Collectors.toList());
        if (unsetCrossProfilePackages.isEmpty()) {
            return;
        }
        try {
            this.mService.resetInteractAcrossProfilesAppOps(this.mContext.getUserId(), unsetCrossProfilePackages);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$resetInteractAcrossProfilesAppOps$1(Set newCrossProfilePackages, String packageName) {
        return !newCrossProfilePackages.contains(packageName);
    }

    public void clearInteractAcrossProfilesAppOps() {
        try {
            this.mService.clearInteractAcrossProfilesAppOps(this.mContext.getUserId());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    private void verifyCanAccessUser(UserHandle userHandle) {
        if (!getTargetUserProfiles().contains(userHandle)) {
            throw new SecurityException("Not allowed to access " + userHandle);
        }
    }
}
