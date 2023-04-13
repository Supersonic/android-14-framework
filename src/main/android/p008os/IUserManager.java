package android.p008os;

import android.content.IntentSender;
import android.content.p001pm.UserInfo;
import android.content.p001pm.UserProperties;
import android.graphics.Bitmap;
import android.p008os.IUserRestrictionsListener;
import android.p008os.UserManager;
import java.util.List;
/* renamed from: android.os.IUserManager */
/* loaded from: classes3.dex */
public interface IUserManager extends IInterface {
    void addUserRestrictionsListener(IUserRestrictionsListener iUserRestrictionsListener) throws RemoteException;

    boolean canAddMoreManagedProfiles(int i, boolean z) throws RemoteException;

    boolean canAddMoreProfilesToUser(String str, int i, boolean z) throws RemoteException;

    boolean canAddMoreUsersOfType(String str) throws RemoteException;

    boolean canHaveRestrictedProfile(int i) throws RemoteException;

    void clearSeedAccountData(int i) throws RemoteException;

    UserInfo createProfileForUserEvenWhenDisallowedWithThrow(String str, String str2, int i, int i2, String[] strArr) throws RemoteException;

    UserInfo createProfileForUserWithThrow(String str, String str2, int i, int i2, String[] strArr) throws RemoteException;

    UserInfo createRestrictedProfileWithThrow(String str, int i) throws RemoteException;

    UserHandle createUserWithAttributes(String str, String str2, int i, Bitmap bitmap, String str3, String str4, PersistableBundle persistableBundle) throws RemoteException;

    UserInfo createUserWithThrow(String str, String str2, int i) throws RemoteException;

    void evictCredentialEncryptionKey(int i) throws RemoteException;

    Bundle getApplicationRestrictions(String str) throws RemoteException;

    Bundle getApplicationRestrictionsForUser(String str, int i) throws RemoteException;

    int getBootUser() throws RemoteException;

    int getCredentialOwnerProfile(int i) throws RemoteException;

    Bundle getDefaultGuestRestrictions() throws RemoteException;

    int getDisplayIdAssignedToUser() throws RemoteException;

    List<UserInfo> getGuestUsers() throws RemoteException;

    int getMainUserId() throws RemoteException;

    String[] getPreInstallableSystemPackages(String str) throws RemoteException;

    int getPreviousFullUserToEnterForeground() throws RemoteException;

    UserInfo getPrimaryUser() throws RemoteException;

    int[] getProfileIds(int i, boolean z) throws RemoteException;

    UserInfo getProfileParent(int i) throws RemoteException;

    int getProfileParentId(int i) throws RemoteException;

    String getProfileType(int i) throws RemoteException;

    List<UserInfo> getProfiles(int i, boolean z) throws RemoteException;

    int getRemainingCreatableProfileCount(String str, int i) throws RemoteException;

    int getRemainingCreatableUserCount(String str) throws RemoteException;

    String getSeedAccountName(int i) throws RemoteException;

    PersistableBundle getSeedAccountOptions(int i) throws RemoteException;

    String getSeedAccountType(int i) throws RemoteException;

    String getUserAccount(int i) throws RemoteException;

    int getUserBadgeColorResId(int i) throws RemoteException;

    int getUserBadgeDarkColorResId(int i) throws RemoteException;

    int getUserBadgeLabelResId(int i) throws RemoteException;

    int getUserBadgeNoBackgroundResId(int i) throws RemoteException;

    int getUserBadgeResId(int i) throws RemoteException;

    long getUserCreationTime(int i) throws RemoteException;

    int getUserHandle(int i) throws RemoteException;

    ParcelFileDescriptor getUserIcon(int i) throws RemoteException;

    int getUserIconBadgeResId(int i) throws RemoteException;

    UserInfo getUserInfo(int i) throws RemoteException;

    String getUserName() throws RemoteException;

    UserProperties getUserPropertiesCopy(int i) throws RemoteException;

    int getUserRestrictionSource(String str, int i) throws RemoteException;

    List<UserManager.EnforcingUser> getUserRestrictionSources(String str, int i) throws RemoteException;

    Bundle getUserRestrictions(int i) throws RemoteException;

    int getUserSerialNumber(int i) throws RemoteException;

    long getUserStartRealtime() throws RemoteException;

    int getUserSwitchability(int i) throws RemoteException;

    long getUserUnlockRealtime() throws RemoteException;

    List<UserInfo> getUsers(boolean z, boolean z2, boolean z3) throws RemoteException;

    int[] getVisibleUsers() throws RemoteException;

    boolean hasBadge(int i) throws RemoteException;

    boolean hasBaseUserRestriction(String str, int i) throws RemoteException;

    boolean hasRestrictedProfiles(int i) throws RemoteException;

    boolean hasUserRestriction(String str, int i) throws RemoteException;

    boolean hasUserRestrictionOnAnyUser(String str) throws RemoteException;

    boolean isAdminUser(int i) throws RemoteException;

    boolean isDemoUser(int i) throws RemoteException;

    boolean isHeadlessSystemUserMode() throws RemoteException;

    boolean isPreCreated(int i) throws RemoteException;

    boolean isQuietModeEnabled(int i) throws RemoteException;

    boolean isRestricted(int i) throws RemoteException;

    boolean isSameProfileGroup(int i, int i2) throws RemoteException;

    boolean isSettingRestrictedForUser(String str, int i, String str2, int i2) throws RemoteException;

    boolean isUserForeground(int i) throws RemoteException;

    boolean isUserNameSet(int i) throws RemoteException;

    boolean isUserOfType(int i, String str) throws RemoteException;

    boolean isUserRunning(int i) throws RemoteException;

    boolean isUserSwitcherEnabled(boolean z, int i) throws RemoteException;

    boolean isUserTypeEnabled(String str) throws RemoteException;

    boolean isUserUnlocked(int i) throws RemoteException;

    boolean isUserUnlockingOrUnlocked(int i) throws RemoteException;

    boolean isUserVisible(int i) throws RemoteException;

    boolean markGuestForDeletion(int i) throws RemoteException;

    UserInfo preCreateUserWithThrow(String str) throws RemoteException;

    boolean removeUser(int i) throws RemoteException;

    boolean removeUserEvenWhenDisallowed(int i) throws RemoteException;

    int removeUserWhenPossible(int i, boolean z) throws RemoteException;

    boolean requestQuietModeEnabled(String str, boolean z, int i, IntentSender intentSender, int i2) throws RemoteException;

    void revokeUserAdmin(int i) throws RemoteException;

    void setApplicationRestrictions(String str, Bundle bundle, int i) throws RemoteException;

    void setBootUser(int i) throws RemoteException;

    void setDefaultGuestRestrictions(Bundle bundle) throws RemoteException;

    void setSeedAccountData(int i, String str, String str2, PersistableBundle persistableBundle, boolean z) throws RemoteException;

    void setUserAccount(int i, String str) throws RemoteException;

    void setUserAdmin(int i) throws RemoteException;

    void setUserEnabled(int i) throws RemoteException;

    boolean setUserEphemeral(int i, boolean z) throws RemoteException;

    void setUserIcon(int i, Bitmap bitmap) throws RemoteException;

    void setUserName(int i, String str) throws RemoteException;

    void setUserRestriction(String str, boolean z, int i) throws RemoteException;

    boolean someUserHasAccount(String str, String str2) throws RemoteException;

    boolean someUserHasSeedAccount(String str, String str2) throws RemoteException;

    /* renamed from: android.os.IUserManager$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IUserManager {
        @Override // android.p008os.IUserManager
        public int getCredentialOwnerProfile(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getProfileParentId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public UserInfo createUserWithThrow(String name, String userType, int flags) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public UserInfo preCreateUserWithThrow(String userType) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public UserInfo createProfileForUserWithThrow(String name, String userType, int flags, int userId, String[] disallowedPackages) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public UserInfo createRestrictedProfileWithThrow(String name, int parentUserHandle) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public String[] getPreInstallableSystemPackages(String userType) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public void setUserEnabled(int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public void setUserAdmin(int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public void revokeUserAdmin(int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public void evictCredentialEncryptionKey(int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public boolean removeUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean removeUserEvenWhenDisallowed(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public void setUserName(int userId, String name) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public void setUserIcon(int userId, Bitmap icon) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public ParcelFileDescriptor getUserIcon(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public UserInfo getPrimaryUser() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public int getMainUserId() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getPreviousFullUserToEnterForeground() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public List<UserInfo> getUsers(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public List<UserInfo> getProfiles(int userId, boolean enabledOnly) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public int[] getProfileIds(int userId, boolean enabledOnly) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserTypeEnabled(String userType) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean canAddMoreUsersOfType(String userType) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public int getRemainingCreatableUserCount(String userType) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getRemainingCreatableProfileCount(String userType, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public boolean canAddMoreProfilesToUser(String userType, int userId, boolean allowedToRemoveOne) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean canAddMoreManagedProfiles(int userId, boolean allowedToRemoveOne) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public UserInfo getProfileParent(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public boolean isSameProfileGroup(int userId, int otherUserHandle) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isHeadlessSystemUserMode() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserOfType(int userId, String userType) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public UserInfo getUserInfo(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public UserProperties getUserPropertiesCopy(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public String getUserAccount(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public void setUserAccount(int userId, String accountName) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public long getUserCreationTime(int userId) throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.IUserManager
        public int getUserSwitchability(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserSwitcherEnabled(boolean showEvenIfNotActionable, int mUserId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isRestricted(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean canHaveRestrictedProfile(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public int getUserSerialNumber(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserHandle(int userSerialNumber) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserRestrictionSource(String restrictionKey, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public List<UserManager.EnforcingUser> getUserRestrictionSources(String restrictionKey, int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public Bundle getUserRestrictions(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public boolean hasBaseUserRestriction(String restrictionKey, int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean hasUserRestriction(String restrictionKey, int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean hasUserRestrictionOnAnyUser(String restrictionKey) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isSettingRestrictedForUser(String setting, int userId, String value, int callingUid) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public void addUserRestrictionsListener(IUserRestrictionsListener listener) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public void setUserRestriction(String key, boolean value, int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public void setApplicationRestrictions(String packageName, Bundle restrictions, int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public Bundle getApplicationRestrictionsForUser(String packageName, int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public void setDefaultGuestRestrictions(Bundle restrictions) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public Bundle getDefaultGuestRestrictions() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public int removeUserWhenPossible(int userId, boolean overrideDevicePolicy) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public boolean markGuestForDeletion(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public List<UserInfo> getGuestUsers() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public boolean isQuietModeEnabled(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public UserHandle createUserWithAttributes(String userName, String userType, int flags, Bitmap userIcon, String accountName, String accountType, PersistableBundle accountOptions) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public void setSeedAccountData(int userId, String accountName, String accountType, PersistableBundle accountOptions, boolean persist) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public String getSeedAccountName(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public String getSeedAccountType(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public PersistableBundle getSeedAccountOptions(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public void clearSeedAccountData(int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public boolean someUserHasSeedAccount(String accountName, String accountType) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean someUserHasAccount(String accountName, String accountType) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public String getProfileType(int userId) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public boolean isDemoUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isAdminUser(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isPreCreated(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public UserInfo createProfileForUserEvenWhenDisallowedWithThrow(String name, String userType, int flags, int userId, String[] disallowedPackages) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserUnlockingOrUnlocked(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public int getUserIconBadgeResId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserBadgeResId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserBadgeNoBackgroundResId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserBadgeLabelResId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserBadgeColorResId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public int getUserBadgeDarkColorResId(int userId) throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public boolean hasBadge(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserUnlocked(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserRunning(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserForeground(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserVisible(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public int[] getVisibleUsers() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public int getDisplayIdAssignedToUser() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IUserManager
        public boolean isUserNameSet(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean hasRestrictedProfiles(int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public boolean requestQuietModeEnabled(String callingPackage, boolean enableQuietMode, int userId, IntentSender target, int flags) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public String getUserName() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IUserManager
        public long getUserStartRealtime() throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.IUserManager
        public long getUserUnlockRealtime() throws RemoteException {
            return 0L;
        }

        @Override // android.p008os.IUserManager
        public boolean setUserEphemeral(int userId, boolean enableEphemeral) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IUserManager
        public void setBootUser(int userId) throws RemoteException {
        }

        @Override // android.p008os.IUserManager
        public int getBootUser() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IUserManager$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IUserManager {
        public static final String DESCRIPTOR = "android.os.IUserManager";
        static final int TRANSACTION_addUserRestrictionsListener = 51;
        static final int TRANSACTION_canAddMoreManagedProfiles = 28;
        static final int TRANSACTION_canAddMoreProfilesToUser = 27;
        static final int TRANSACTION_canAddMoreUsersOfType = 24;
        static final int TRANSACTION_canHaveRestrictedProfile = 41;
        static final int TRANSACTION_clearSeedAccountData = 67;
        static final int TRANSACTION_createProfileForUserEvenWhenDisallowedWithThrow = 74;
        static final int TRANSACTION_createProfileForUserWithThrow = 5;
        static final int TRANSACTION_createRestrictedProfileWithThrow = 6;
        static final int TRANSACTION_createUserWithAttributes = 62;
        static final int TRANSACTION_createUserWithThrow = 3;
        static final int TRANSACTION_evictCredentialEncryptionKey = 11;
        static final int TRANSACTION_getApplicationRestrictions = 54;
        static final int TRANSACTION_getApplicationRestrictionsForUser = 55;
        static final int TRANSACTION_getBootUser = 97;
        static final int TRANSACTION_getCredentialOwnerProfile = 1;
        static final int TRANSACTION_getDefaultGuestRestrictions = 57;
        static final int TRANSACTION_getDisplayIdAssignedToUser = 88;
        static final int TRANSACTION_getGuestUsers = 60;
        static final int TRANSACTION_getMainUserId = 18;
        static final int TRANSACTION_getPreInstallableSystemPackages = 7;
        static final int TRANSACTION_getPreviousFullUserToEnterForeground = 19;
        static final int TRANSACTION_getPrimaryUser = 17;
        static final int TRANSACTION_getProfileIds = 22;
        static final int TRANSACTION_getProfileParent = 29;
        static final int TRANSACTION_getProfileParentId = 2;
        static final int TRANSACTION_getProfileType = 70;
        static final int TRANSACTION_getProfiles = 21;
        static final int TRANSACTION_getRemainingCreatableProfileCount = 26;
        static final int TRANSACTION_getRemainingCreatableUserCount = 25;
        static final int TRANSACTION_getSeedAccountName = 64;
        static final int TRANSACTION_getSeedAccountOptions = 66;
        static final int TRANSACTION_getSeedAccountType = 65;
        static final int TRANSACTION_getUserAccount = 35;
        static final int TRANSACTION_getUserBadgeColorResId = 80;
        static final int TRANSACTION_getUserBadgeDarkColorResId = 81;
        static final int TRANSACTION_getUserBadgeLabelResId = 79;
        static final int TRANSACTION_getUserBadgeNoBackgroundResId = 78;
        static final int TRANSACTION_getUserBadgeResId = 77;
        static final int TRANSACTION_getUserCreationTime = 37;
        static final int TRANSACTION_getUserHandle = 43;
        static final int TRANSACTION_getUserIcon = 16;
        static final int TRANSACTION_getUserIconBadgeResId = 76;
        static final int TRANSACTION_getUserInfo = 33;
        static final int TRANSACTION_getUserName = 92;
        static final int TRANSACTION_getUserPropertiesCopy = 34;
        static final int TRANSACTION_getUserRestrictionSource = 44;
        static final int TRANSACTION_getUserRestrictionSources = 45;
        static final int TRANSACTION_getUserRestrictions = 46;
        static final int TRANSACTION_getUserSerialNumber = 42;
        static final int TRANSACTION_getUserStartRealtime = 93;
        static final int TRANSACTION_getUserSwitchability = 38;
        static final int TRANSACTION_getUserUnlockRealtime = 94;
        static final int TRANSACTION_getUsers = 20;
        static final int TRANSACTION_getVisibleUsers = 87;
        static final int TRANSACTION_hasBadge = 82;
        static final int TRANSACTION_hasBaseUserRestriction = 47;
        static final int TRANSACTION_hasRestrictedProfiles = 90;
        static final int TRANSACTION_hasUserRestriction = 48;
        static final int TRANSACTION_hasUserRestrictionOnAnyUser = 49;
        static final int TRANSACTION_isAdminUser = 72;
        static final int TRANSACTION_isDemoUser = 71;
        static final int TRANSACTION_isHeadlessSystemUserMode = 31;
        static final int TRANSACTION_isPreCreated = 73;
        static final int TRANSACTION_isQuietModeEnabled = 61;
        static final int TRANSACTION_isRestricted = 40;
        static final int TRANSACTION_isSameProfileGroup = 30;
        static final int TRANSACTION_isSettingRestrictedForUser = 50;
        static final int TRANSACTION_isUserForeground = 85;
        static final int TRANSACTION_isUserNameSet = 89;
        static final int TRANSACTION_isUserOfType = 32;
        static final int TRANSACTION_isUserRunning = 84;
        static final int TRANSACTION_isUserSwitcherEnabled = 39;
        static final int TRANSACTION_isUserTypeEnabled = 23;
        static final int TRANSACTION_isUserUnlocked = 83;
        static final int TRANSACTION_isUserUnlockingOrUnlocked = 75;
        static final int TRANSACTION_isUserVisible = 86;
        static final int TRANSACTION_markGuestForDeletion = 59;
        static final int TRANSACTION_preCreateUserWithThrow = 4;
        static final int TRANSACTION_removeUser = 12;
        static final int TRANSACTION_removeUserEvenWhenDisallowed = 13;
        static final int TRANSACTION_removeUserWhenPossible = 58;
        static final int TRANSACTION_requestQuietModeEnabled = 91;
        static final int TRANSACTION_revokeUserAdmin = 10;
        static final int TRANSACTION_setApplicationRestrictions = 53;
        static final int TRANSACTION_setBootUser = 96;
        static final int TRANSACTION_setDefaultGuestRestrictions = 56;
        static final int TRANSACTION_setSeedAccountData = 63;
        static final int TRANSACTION_setUserAccount = 36;
        static final int TRANSACTION_setUserAdmin = 9;
        static final int TRANSACTION_setUserEnabled = 8;
        static final int TRANSACTION_setUserEphemeral = 95;
        static final int TRANSACTION_setUserIcon = 15;
        static final int TRANSACTION_setUserName = 14;
        static final int TRANSACTION_setUserRestriction = 52;
        static final int TRANSACTION_someUserHasAccount = 69;
        static final int TRANSACTION_someUserHasSeedAccount = 68;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IUserManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IUserManager)) {
                return (IUserManager) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getCredentialOwnerProfile";
                case 2:
                    return "getProfileParentId";
                case 3:
                    return "createUserWithThrow";
                case 4:
                    return "preCreateUserWithThrow";
                case 5:
                    return "createProfileForUserWithThrow";
                case 6:
                    return "createRestrictedProfileWithThrow";
                case 7:
                    return "getPreInstallableSystemPackages";
                case 8:
                    return "setUserEnabled";
                case 9:
                    return "setUserAdmin";
                case 10:
                    return "revokeUserAdmin";
                case 11:
                    return "evictCredentialEncryptionKey";
                case 12:
                    return "removeUser";
                case 13:
                    return "removeUserEvenWhenDisallowed";
                case 14:
                    return "setUserName";
                case 15:
                    return "setUserIcon";
                case 16:
                    return "getUserIcon";
                case 17:
                    return "getPrimaryUser";
                case 18:
                    return "getMainUserId";
                case 19:
                    return "getPreviousFullUserToEnterForeground";
                case 20:
                    return "getUsers";
                case 21:
                    return "getProfiles";
                case 22:
                    return "getProfileIds";
                case 23:
                    return "isUserTypeEnabled";
                case 24:
                    return "canAddMoreUsersOfType";
                case 25:
                    return "getRemainingCreatableUserCount";
                case 26:
                    return "getRemainingCreatableProfileCount";
                case 27:
                    return "canAddMoreProfilesToUser";
                case 28:
                    return "canAddMoreManagedProfiles";
                case 29:
                    return "getProfileParent";
                case 30:
                    return "isSameProfileGroup";
                case 31:
                    return "isHeadlessSystemUserMode";
                case 32:
                    return "isUserOfType";
                case 33:
                    return "getUserInfo";
                case 34:
                    return "getUserPropertiesCopy";
                case 35:
                    return "getUserAccount";
                case 36:
                    return "setUserAccount";
                case 37:
                    return "getUserCreationTime";
                case 38:
                    return "getUserSwitchability";
                case 39:
                    return "isUserSwitcherEnabled";
                case 40:
                    return "isRestricted";
                case 41:
                    return "canHaveRestrictedProfile";
                case 42:
                    return "getUserSerialNumber";
                case 43:
                    return "getUserHandle";
                case 44:
                    return "getUserRestrictionSource";
                case 45:
                    return "getUserRestrictionSources";
                case 46:
                    return "getUserRestrictions";
                case 47:
                    return "hasBaseUserRestriction";
                case 48:
                    return "hasUserRestriction";
                case 49:
                    return "hasUserRestrictionOnAnyUser";
                case 50:
                    return "isSettingRestrictedForUser";
                case 51:
                    return "addUserRestrictionsListener";
                case 52:
                    return "setUserRestriction";
                case 53:
                    return "setApplicationRestrictions";
                case 54:
                    return "getApplicationRestrictions";
                case 55:
                    return "getApplicationRestrictionsForUser";
                case 56:
                    return "setDefaultGuestRestrictions";
                case 57:
                    return "getDefaultGuestRestrictions";
                case 58:
                    return "removeUserWhenPossible";
                case 59:
                    return "markGuestForDeletion";
                case 60:
                    return "getGuestUsers";
                case 61:
                    return "isQuietModeEnabled";
                case 62:
                    return "createUserWithAttributes";
                case 63:
                    return "setSeedAccountData";
                case 64:
                    return "getSeedAccountName";
                case 65:
                    return "getSeedAccountType";
                case 66:
                    return "getSeedAccountOptions";
                case 67:
                    return "clearSeedAccountData";
                case 68:
                    return "someUserHasSeedAccount";
                case 69:
                    return "someUserHasAccount";
                case 70:
                    return "getProfileType";
                case 71:
                    return "isDemoUser";
                case 72:
                    return "isAdminUser";
                case 73:
                    return "isPreCreated";
                case 74:
                    return "createProfileForUserEvenWhenDisallowedWithThrow";
                case 75:
                    return "isUserUnlockingOrUnlocked";
                case 76:
                    return "getUserIconBadgeResId";
                case 77:
                    return "getUserBadgeResId";
                case 78:
                    return "getUserBadgeNoBackgroundResId";
                case 79:
                    return "getUserBadgeLabelResId";
                case 80:
                    return "getUserBadgeColorResId";
                case 81:
                    return "getUserBadgeDarkColorResId";
                case 82:
                    return "hasBadge";
                case 83:
                    return "isUserUnlocked";
                case 84:
                    return "isUserRunning";
                case 85:
                    return "isUserForeground";
                case 86:
                    return "isUserVisible";
                case 87:
                    return "getVisibleUsers";
                case 88:
                    return "getDisplayIdAssignedToUser";
                case 89:
                    return "isUserNameSet";
                case 90:
                    return "hasRestrictedProfiles";
                case 91:
                    return "requestQuietModeEnabled";
                case 92:
                    return "getUserName";
                case 93:
                    return "getUserStartRealtime";
                case 94:
                    return "getUserUnlockRealtime";
                case 95:
                    return "setUserEphemeral";
                case 96:
                    return "setBootUser";
                case 97:
                    return "getBootUser";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result = getCredentialOwnerProfile(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = getProfileParentId(_arg02);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            UserInfo _result3 = createUserWithThrow(_arg03, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            UserInfo _result4 = preCreateUserWithThrow(_arg04);
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            String _arg12 = data.readString();
                            int _arg22 = data.readInt();
                            int _arg3 = data.readInt();
                            String[] _arg4 = data.createStringArray();
                            data.enforceNoDataAvail();
                            UserInfo _result5 = createProfileForUserWithThrow(_arg05, _arg12, _arg22, _arg3, _arg4);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            UserInfo _result6 = createRestrictedProfileWithThrow(_arg06, _arg13);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            String[] _result7 = getPreInstallableSystemPackages(_arg07);
                            reply.writeNoException();
                            reply.writeStringArray(_result7);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            setUserEnabled(_arg08);
                            reply.writeNoException();
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            setUserAdmin(_arg09);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            revokeUserAdmin(_arg010);
                            reply.writeNoException();
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            evictCredentialEncryptionKey(_arg011);
                            reply.writeNoException();
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result8 = removeUser(_arg012);
                            reply.writeNoException();
                            reply.writeBoolean(_result8);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = removeUserEvenWhenDisallowed(_arg013);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            String _arg14 = data.readString();
                            data.enforceNoDataAvail();
                            setUserName(_arg014, _arg14);
                            reply.writeNoException();
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            Bitmap _arg15 = (Bitmap) data.readTypedObject(Bitmap.CREATOR);
                            data.enforceNoDataAvail();
                            setUserIcon(_arg015, _arg15);
                            reply.writeNoException();
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            ParcelFileDescriptor _result10 = getUserIcon(_arg016);
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            break;
                        case 17:
                            UserInfo _result11 = getPrimaryUser();
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            break;
                        case 18:
                            int _result12 = getMainUserId();
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 19:
                            int _result13 = getPreviousFullUserToEnterForeground();
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            break;
                        case 20:
                            boolean _arg017 = data.readBoolean();
                            boolean _arg16 = data.readBoolean();
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            List<UserInfo> _result14 = getUsers(_arg017, _arg16, _arg23);
                            reply.writeNoException();
                            reply.writeTypedList(_result14, 1);
                            break;
                        case 21:
                            int _arg018 = data.readInt();
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            List<UserInfo> _result15 = getProfiles(_arg018, _arg17);
                            reply.writeNoException();
                            reply.writeTypedList(_result15, 1);
                            break;
                        case 22:
                            int _arg019 = data.readInt();
                            boolean _arg18 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int[] _result16 = getProfileIds(_arg019, _arg18);
                            reply.writeNoException();
                            reply.writeIntArray(_result16);
                            break;
                        case 23:
                            String _arg020 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result17 = isUserTypeEnabled(_arg020);
                            reply.writeNoException();
                            reply.writeBoolean(_result17);
                            break;
                        case 24:
                            String _arg021 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result18 = canAddMoreUsersOfType(_arg021);
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            break;
                        case 25:
                            String _arg022 = data.readString();
                            data.enforceNoDataAvail();
                            int _result19 = getRemainingCreatableUserCount(_arg022);
                            reply.writeNoException();
                            reply.writeInt(_result19);
                            break;
                        case 26:
                            String _arg023 = data.readString();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result20 = getRemainingCreatableProfileCount(_arg023, _arg19);
                            reply.writeNoException();
                            reply.writeInt(_result20);
                            break;
                        case 27:
                            String _arg024 = data.readString();
                            int _arg110 = data.readInt();
                            boolean _arg24 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result21 = canAddMoreProfilesToUser(_arg024, _arg110, _arg24);
                            reply.writeNoException();
                            reply.writeBoolean(_result21);
                            break;
                        case 28:
                            int _arg025 = data.readInt();
                            boolean _arg111 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result22 = canAddMoreManagedProfiles(_arg025, _arg111);
                            reply.writeNoException();
                            reply.writeBoolean(_result22);
                            break;
                        case 29:
                            int _arg026 = data.readInt();
                            data.enforceNoDataAvail();
                            UserInfo _result23 = getProfileParent(_arg026);
                            reply.writeNoException();
                            reply.writeTypedObject(_result23, 1);
                            break;
                        case 30:
                            int _arg027 = data.readInt();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result24 = isSameProfileGroup(_arg027, _arg112);
                            reply.writeNoException();
                            reply.writeBoolean(_result24);
                            break;
                        case 31:
                            boolean _result25 = isHeadlessSystemUserMode();
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            break;
                        case 32:
                            int _arg028 = data.readInt();
                            String _arg113 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result26 = isUserOfType(_arg028, _arg113);
                            reply.writeNoException();
                            reply.writeBoolean(_result26);
                            break;
                        case 33:
                            int _arg029 = data.readInt();
                            data.enforceNoDataAvail();
                            UserInfo _result27 = getUserInfo(_arg029);
                            reply.writeNoException();
                            reply.writeTypedObject(_result27, 1);
                            break;
                        case 34:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            UserProperties _result28 = getUserPropertiesCopy(_arg030);
                            reply.writeNoException();
                            reply.writeTypedObject(_result28, 1);
                            break;
                        case 35:
                            int _arg031 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result29 = getUserAccount(_arg031);
                            reply.writeNoException();
                            reply.writeString(_result29);
                            break;
                        case 36:
                            int _arg032 = data.readInt();
                            String _arg114 = data.readString();
                            data.enforceNoDataAvail();
                            setUserAccount(_arg032, _arg114);
                            reply.writeNoException();
                            break;
                        case 37:
                            int _arg033 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result30 = getUserCreationTime(_arg033);
                            reply.writeNoException();
                            reply.writeLong(_result30);
                            break;
                        case 38:
                            int _arg034 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result31 = getUserSwitchability(_arg034);
                            reply.writeNoException();
                            reply.writeInt(_result31);
                            break;
                        case 39:
                            boolean _arg035 = data.readBoolean();
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = isUserSwitcherEnabled(_arg035, _arg115);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            break;
                        case 40:
                            int _arg036 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result33 = isRestricted(_arg036);
                            reply.writeNoException();
                            reply.writeBoolean(_result33);
                            break;
                        case 41:
                            int _arg037 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result34 = canHaveRestrictedProfile(_arg037);
                            reply.writeNoException();
                            reply.writeBoolean(_result34);
                            break;
                        case 42:
                            int _arg038 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result35 = getUserSerialNumber(_arg038);
                            reply.writeNoException();
                            reply.writeInt(_result35);
                            break;
                        case 43:
                            int _arg039 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result36 = getUserHandle(_arg039);
                            reply.writeNoException();
                            reply.writeInt(_result36);
                            break;
                        case 44:
                            String _arg040 = data.readString();
                            int _arg116 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result37 = getUserRestrictionSource(_arg040, _arg116);
                            reply.writeNoException();
                            reply.writeInt(_result37);
                            break;
                        case 45:
                            String _arg041 = data.readString();
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            List<UserManager.EnforcingUser> _result38 = getUserRestrictionSources(_arg041, _arg117);
                            reply.writeNoException();
                            reply.writeTypedList(_result38, 1);
                            break;
                        case 46:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result39 = getUserRestrictions(_arg042);
                            reply.writeNoException();
                            reply.writeTypedObject(_result39, 1);
                            break;
                        case 47:
                            String _arg043 = data.readString();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result40 = hasBaseUserRestriction(_arg043, _arg118);
                            reply.writeNoException();
                            reply.writeBoolean(_result40);
                            break;
                        case 48:
                            String _arg044 = data.readString();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result41 = hasUserRestriction(_arg044, _arg119);
                            reply.writeNoException();
                            reply.writeBoolean(_result41);
                            break;
                        case 49:
                            String _arg045 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result42 = hasUserRestrictionOnAnyUser(_arg045);
                            reply.writeNoException();
                            reply.writeBoolean(_result42);
                            break;
                        case 50:
                            String _arg046 = data.readString();
                            int _arg120 = data.readInt();
                            String _arg25 = data.readString();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result43 = isSettingRestrictedForUser(_arg046, _arg120, _arg25, _arg32);
                            reply.writeNoException();
                            reply.writeBoolean(_result43);
                            break;
                        case 51:
                            IUserRestrictionsListener _arg047 = IUserRestrictionsListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addUserRestrictionsListener(_arg047);
                            reply.writeNoException();
                            break;
                        case 52:
                            String _arg048 = data.readString();
                            boolean _arg121 = data.readBoolean();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            setUserRestriction(_arg048, _arg121, _arg26);
                            reply.writeNoException();
                            break;
                        case 53:
                            String _arg049 = data.readString();
                            Bundle _arg122 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            setApplicationRestrictions(_arg049, _arg122, _arg27);
                            reply.writeNoException();
                            break;
                        case 54:
                            String _arg050 = data.readString();
                            data.enforceNoDataAvail();
                            Bundle _result44 = getApplicationRestrictions(_arg050);
                            reply.writeNoException();
                            reply.writeTypedObject(_result44, 1);
                            break;
                        case 55:
                            String _arg051 = data.readString();
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result45 = getApplicationRestrictionsForUser(_arg051, _arg123);
                            reply.writeNoException();
                            reply.writeTypedObject(_result45, 1);
                            break;
                        case 56:
                            Bundle _arg052 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            setDefaultGuestRestrictions(_arg052);
                            reply.writeNoException();
                            break;
                        case 57:
                            Bundle _result46 = getDefaultGuestRestrictions();
                            reply.writeNoException();
                            reply.writeTypedObject(_result46, 1);
                            break;
                        case 58:
                            int _arg053 = data.readInt();
                            boolean _arg124 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result47 = removeUserWhenPossible(_arg053, _arg124);
                            reply.writeNoException();
                            reply.writeInt(_result47);
                            break;
                        case 59:
                            int _arg054 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result48 = markGuestForDeletion(_arg054);
                            reply.writeNoException();
                            reply.writeBoolean(_result48);
                            break;
                        case 60:
                            List<UserInfo> _result49 = getGuestUsers();
                            reply.writeNoException();
                            reply.writeTypedList(_result49, 1);
                            break;
                        case 61:
                            int _arg055 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result50 = isQuietModeEnabled(_arg055);
                            reply.writeNoException();
                            reply.writeBoolean(_result50);
                            break;
                        case 62:
                            String _arg056 = data.readString();
                            String _arg125 = data.readString();
                            int _arg28 = data.readInt();
                            Bitmap _arg33 = (Bitmap) data.readTypedObject(Bitmap.CREATOR);
                            String _arg42 = data.readString();
                            String _arg5 = data.readString();
                            PersistableBundle _arg6 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            data.enforceNoDataAvail();
                            UserHandle _result51 = createUserWithAttributes(_arg056, _arg125, _arg28, _arg33, _arg42, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeTypedObject(_result51, 1);
                            break;
                        case 63:
                            int _arg057 = data.readInt();
                            String _arg126 = data.readString();
                            String _arg29 = data.readString();
                            PersistableBundle _arg34 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            boolean _arg43 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSeedAccountData(_arg057, _arg126, _arg29, _arg34, _arg43);
                            reply.writeNoException();
                            break;
                        case 64:
                            int _arg058 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result52 = getSeedAccountName(_arg058);
                            reply.writeNoException();
                            reply.writeString(_result52);
                            break;
                        case 65:
                            int _arg059 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result53 = getSeedAccountType(_arg059);
                            reply.writeNoException();
                            reply.writeString(_result53);
                            break;
                        case 66:
                            int _arg060 = data.readInt();
                            data.enforceNoDataAvail();
                            PersistableBundle _result54 = getSeedAccountOptions(_arg060);
                            reply.writeNoException();
                            reply.writeTypedObject(_result54, 1);
                            break;
                        case 67:
                            int _arg061 = data.readInt();
                            data.enforceNoDataAvail();
                            clearSeedAccountData(_arg061);
                            reply.writeNoException();
                            break;
                        case 68:
                            String _arg062 = data.readString();
                            String _arg127 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result55 = someUserHasSeedAccount(_arg062, _arg127);
                            reply.writeNoException();
                            reply.writeBoolean(_result55);
                            break;
                        case 69:
                            String _arg063 = data.readString();
                            String _arg128 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result56 = someUserHasAccount(_arg063, _arg128);
                            reply.writeNoException();
                            reply.writeBoolean(_result56);
                            break;
                        case 70:
                            int _arg064 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result57 = getProfileType(_arg064);
                            reply.writeNoException();
                            reply.writeString(_result57);
                            break;
                        case 71:
                            int _arg065 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result58 = isDemoUser(_arg065);
                            reply.writeNoException();
                            reply.writeBoolean(_result58);
                            break;
                        case 72:
                            int _arg066 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result59 = isAdminUser(_arg066);
                            reply.writeNoException();
                            reply.writeBoolean(_result59);
                            break;
                        case 73:
                            int _arg067 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result60 = isPreCreated(_arg067);
                            reply.writeNoException();
                            reply.writeBoolean(_result60);
                            break;
                        case 74:
                            String _arg068 = data.readString();
                            String _arg129 = data.readString();
                            int _arg210 = data.readInt();
                            int _arg35 = data.readInt();
                            String[] _arg44 = data.createStringArray();
                            data.enforceNoDataAvail();
                            UserInfo _result61 = createProfileForUserEvenWhenDisallowedWithThrow(_arg068, _arg129, _arg210, _arg35, _arg44);
                            reply.writeNoException();
                            reply.writeTypedObject(_result61, 1);
                            break;
                        case 75:
                            int _arg069 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result62 = isUserUnlockingOrUnlocked(_arg069);
                            reply.writeNoException();
                            reply.writeBoolean(_result62);
                            break;
                        case 76:
                            int _arg070 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result63 = getUserIconBadgeResId(_arg070);
                            reply.writeNoException();
                            reply.writeInt(_result63);
                            break;
                        case 77:
                            int _arg071 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result64 = getUserBadgeResId(_arg071);
                            reply.writeNoException();
                            reply.writeInt(_result64);
                            break;
                        case 78:
                            int _arg072 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result65 = getUserBadgeNoBackgroundResId(_arg072);
                            reply.writeNoException();
                            reply.writeInt(_result65);
                            break;
                        case 79:
                            int _arg073 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result66 = getUserBadgeLabelResId(_arg073);
                            reply.writeNoException();
                            reply.writeInt(_result66);
                            break;
                        case 80:
                            int _arg074 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result67 = getUserBadgeColorResId(_arg074);
                            reply.writeNoException();
                            reply.writeInt(_result67);
                            break;
                        case 81:
                            int _arg075 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result68 = getUserBadgeDarkColorResId(_arg075);
                            reply.writeNoException();
                            reply.writeInt(_result68);
                            break;
                        case 82:
                            int _arg076 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result69 = hasBadge(_arg076);
                            reply.writeNoException();
                            reply.writeBoolean(_result69);
                            break;
                        case 83:
                            int _arg077 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result70 = isUserUnlocked(_arg077);
                            reply.writeNoException();
                            reply.writeBoolean(_result70);
                            break;
                        case 84:
                            int _arg078 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result71 = isUserRunning(_arg078);
                            reply.writeNoException();
                            reply.writeBoolean(_result71);
                            break;
                        case 85:
                            int _arg079 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result72 = isUserForeground(_arg079);
                            reply.writeNoException();
                            reply.writeBoolean(_result72);
                            break;
                        case 86:
                            int _arg080 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result73 = isUserVisible(_arg080);
                            reply.writeNoException();
                            reply.writeBoolean(_result73);
                            break;
                        case 87:
                            int[] _result74 = getVisibleUsers();
                            reply.writeNoException();
                            reply.writeIntArray(_result74);
                            break;
                        case 88:
                            int _result75 = getDisplayIdAssignedToUser();
                            reply.writeNoException();
                            reply.writeInt(_result75);
                            break;
                        case 89:
                            int _arg081 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result76 = isUserNameSet(_arg081);
                            reply.writeNoException();
                            reply.writeBoolean(_result76);
                            break;
                        case 90:
                            int _arg082 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result77 = hasRestrictedProfiles(_arg082);
                            reply.writeNoException();
                            reply.writeBoolean(_result77);
                            break;
                        case 91:
                            String _arg083 = data.readString();
                            boolean _arg130 = data.readBoolean();
                            int _arg211 = data.readInt();
                            IntentSender _arg36 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            int _arg45 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result78 = requestQuietModeEnabled(_arg083, _arg130, _arg211, _arg36, _arg45);
                            reply.writeNoException();
                            reply.writeBoolean(_result78);
                            break;
                        case 92:
                            String _result79 = getUserName();
                            reply.writeNoException();
                            reply.writeString(_result79);
                            break;
                        case 93:
                            long _result80 = getUserStartRealtime();
                            reply.writeNoException();
                            reply.writeLong(_result80);
                            break;
                        case 94:
                            long _result81 = getUserUnlockRealtime();
                            reply.writeNoException();
                            reply.writeLong(_result81);
                            break;
                        case 95:
                            int _arg084 = data.readInt();
                            boolean _arg131 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result82 = setUserEphemeral(_arg084, _arg131);
                            reply.writeNoException();
                            reply.writeBoolean(_result82);
                            break;
                        case 96:
                            int _arg085 = data.readInt();
                            data.enforceNoDataAvail();
                            setBootUser(_arg085);
                            reply.writeNoException();
                            break;
                        case 97:
                            int _result83 = getBootUser();
                            reply.writeNoException();
                            reply.writeInt(_result83);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.IUserManager$Stub$Proxy */
        /* loaded from: classes3.dex */
        public static class Proxy implements IUserManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.p008os.IUserManager
            public int getCredentialOwnerProfile(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getProfileParentId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo createUserWithThrow(String name, String userType, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(userType);
                    _data.writeInt(flags);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo preCreateUserWithThrow(String userType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo createProfileForUserWithThrow(String name, String userType, int flags, int userId, String[] disallowedPackages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(userType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    _data.writeStringArray(disallowedPackages);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo createRestrictedProfileWithThrow(String name, int parentUserHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeInt(parentUserHandle);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public String[] getPreInstallableSystemPackages(String userType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setUserEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setUserAdmin(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void revokeUserAdmin(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void evictCredentialEncryptionKey(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean removeUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean removeUserEvenWhenDisallowed(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setUserName(int userId, String name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(name);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setUserIcon(int userId, Bitmap icon) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeTypedObject(icon, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public ParcelFileDescriptor getUserIcon(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    ParcelFileDescriptor _result = (ParcelFileDescriptor) _reply.readTypedObject(ParcelFileDescriptor.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo getPrimaryUser() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getMainUserId() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getPreviousFullUserToEnterForeground() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public List<UserInfo> getUsers(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(excludePartial);
                    _data.writeBoolean(excludeDying);
                    _data.writeBoolean(excludePreCreated);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    List<UserInfo> _result = _reply.createTypedArrayList(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public List<UserInfo> getProfiles(int userId, boolean enabledOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(enabledOnly);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    List<UserInfo> _result = _reply.createTypedArrayList(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int[] getProfileIds(int userId, boolean enabledOnly) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(enabledOnly);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserTypeEnabled(String userType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean canAddMoreUsersOfType(String userType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getRemainingCreatableUserCount(String userType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getRemainingCreatableProfileCount(String userType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean canAddMoreProfilesToUser(String userType, int userId, boolean allowedToRemoveOne) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userType);
                    _data.writeInt(userId);
                    _data.writeBoolean(allowedToRemoveOne);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean canAddMoreManagedProfiles(int userId, boolean allowedToRemoveOne) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(allowedToRemoveOne);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo getProfileParent(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isSameProfileGroup(int userId, int otherUserHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeInt(otherUserHandle);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isHeadlessSystemUserMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserOfType(int userId, String userType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(userType);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo getUserInfo(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserProperties getUserPropertiesCopy(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    UserProperties _result = (UserProperties) _reply.readTypedObject(UserProperties.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public String getUserAccount(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setUserAccount(int userId, String accountName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(accountName);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public long getUserCreationTime(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserSwitchability(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserSwitcherEnabled(boolean showEvenIfNotActionable, int mUserId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(showEvenIfNotActionable);
                    _data.writeInt(mUserId);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isRestricted(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean canHaveRestrictedProfile(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserSerialNumber(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserHandle(int userSerialNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userSerialNumber);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserRestrictionSource(String restrictionKey, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restrictionKey);
                    _data.writeInt(userId);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public List<UserManager.EnforcingUser> getUserRestrictionSources(String restrictionKey, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restrictionKey);
                    _data.writeInt(userId);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    List<UserManager.EnforcingUser> _result = _reply.createTypedArrayList(UserManager.EnforcingUser.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public Bundle getUserRestrictions(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean hasBaseUserRestriction(String restrictionKey, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restrictionKey);
                    _data.writeInt(userId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean hasUserRestriction(String restrictionKey, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restrictionKey);
                    _data.writeInt(userId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean hasUserRestrictionOnAnyUser(String restrictionKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(restrictionKey);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isSettingRestrictedForUser(String setting, int userId, String value, int callingUid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(setting);
                    _data.writeInt(userId);
                    _data.writeString(value);
                    _data.writeInt(callingUid);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void addUserRestrictionsListener(IUserRestrictionsListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setUserRestriction(String key, boolean value, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeBoolean(value);
                    _data.writeInt(userId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setApplicationRestrictions(String packageName, Bundle restrictions, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(restrictions, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public Bundle getApplicationRestrictions(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public Bundle getApplicationRestrictionsForUser(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(55, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setDefaultGuestRestrictions(Bundle restrictions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(restrictions, 0);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public Bundle getDefaultGuestRestrictions() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int removeUserWhenPossible(int userId, boolean overrideDevicePolicy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(overrideDevicePolicy);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean markGuestForDeletion(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public List<UserInfo> getGuestUsers() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    List<UserInfo> _result = _reply.createTypedArrayList(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isQuietModeEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserHandle createUserWithAttributes(String userName, String userType, int flags, Bitmap userIcon, String accountName, String accountType, PersistableBundle accountOptions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(userName);
                    _data.writeString(userType);
                    _data.writeInt(flags);
                    _data.writeTypedObject(userIcon, 0);
                    _data.writeString(accountName);
                    _data.writeString(accountType);
                    _data.writeTypedObject(accountOptions, 0);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                    UserHandle _result = (UserHandle) _reply.readTypedObject(UserHandle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setSeedAccountData(int userId, String accountName, String accountType, PersistableBundle accountOptions, boolean persist) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeString(accountName);
                    _data.writeString(accountType);
                    _data.writeTypedObject(accountOptions, 0);
                    _data.writeBoolean(persist);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public String getSeedAccountName(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public String getSeedAccountType(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public PersistableBundle getSeedAccountOptions(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                    PersistableBundle _result = (PersistableBundle) _reply.readTypedObject(PersistableBundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void clearSeedAccountData(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean someUserHasSeedAccount(String accountName, String accountType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountName);
                    _data.writeString(accountType);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean someUserHasAccount(String accountName, String accountType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(accountName);
                    _data.writeString(accountType);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public String getProfileType(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isDemoUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isAdminUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isPreCreated(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public UserInfo createProfileForUserEvenWhenDisallowedWithThrow(String name, String userType, int flags, int userId, String[] disallowedPackages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(name);
                    _data.writeString(userType);
                    _data.writeInt(flags);
                    _data.writeInt(userId);
                    _data.writeStringArray(disallowedPackages);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                    UserInfo _result = (UserInfo) _reply.readTypedObject(UserInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserUnlockingOrUnlocked(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserIconBadgeResId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserBadgeResId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserBadgeNoBackgroundResId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserBadgeLabelResId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserBadgeColorResId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getUserBadgeDarkColorResId(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean hasBadge(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(82, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserUnlocked(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserRunning(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserForeground(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserVisible(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int[] getVisibleUsers() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getDisplayIdAssignedToUser() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean isUserNameSet(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean hasRestrictedProfiles(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean requestQuietModeEnabled(String callingPackage, boolean enableQuietMode, int userId, IntentSender target, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeBoolean(enableQuietMode);
                    _data.writeInt(userId);
                    _data.writeTypedObject(target, 0);
                    _data.writeInt(flags);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public String getUserName() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public long getUserStartRealtime() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public long getUserUnlockRealtime() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public boolean setUserEphemeral(int userId, boolean enableEphemeral) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(enableEphemeral);
                    this.mRemote.transact(95, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public void setBootUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IUserManager
            public int getBootUser() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(97, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 96;
        }
    }
}
