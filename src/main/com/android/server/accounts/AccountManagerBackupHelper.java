package com.android.server.accounts;

import android.accounts.Account;
import android.accounts.AccountManagerInternal;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.util.PackageUtils;
import android.util.Pair;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.accounts.AccountManagerService;
import com.android.server.clipboard.ClipboardService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class AccountManagerBackupHelper {
    public final AccountManagerInternal mAccountManagerInternal;
    public final AccountManagerService mAccountManagerService;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public Runnable mRestoreCancelCommand;
    @GuardedBy({"mLock"})
    public RestorePackageMonitor mRestorePackageMonitor;
    @GuardedBy({"mLock"})
    public List<PendingAppPermission> mRestorePendingAppPermissions;

    public AccountManagerBackupHelper(AccountManagerService accountManagerService, AccountManagerInternal accountManagerInternal) {
        this.mAccountManagerService = accountManagerService;
        this.mAccountManagerInternal = accountManagerInternal;
    }

    /* loaded from: classes.dex */
    public final class PendingAppPermission {
        public final String accountDigest;
        public final String certDigest;
        public final String packageName;
        public final int userId;

        public PendingAppPermission(String str, String str2, String str3, int i) {
            this.accountDigest = str;
            this.packageName = str2;
            this.certDigest = str3;
            this.userId = i;
        }

        public boolean apply(PackageManager packageManager) {
            Account account;
            AccountManagerService.UserAccounts userAccounts = AccountManagerBackupHelper.this.mAccountManagerService.getUserAccounts(this.userId);
            synchronized (userAccounts.dbLock) {
                synchronized (userAccounts.cacheLock) {
                    account = null;
                    for (Account[] accountArr : userAccounts.accountCache.values()) {
                        int length = accountArr.length;
                        int i = 0;
                        while (true) {
                            if (i < length) {
                                Account account2 = accountArr[i];
                                if (this.accountDigest.equals(PackageUtils.computeSha256Digest(account2.name.getBytes()))) {
                                    account = account2;
                                    continue;
                                    break;
                                }
                                i++;
                            }
                        }
                        if (account != null) {
                            break;
                        }
                    }
                }
            }
            if (account == null) {
                return false;
            }
            try {
                PackageInfo packageInfoAsUser = packageManager.getPackageInfoAsUser(this.packageName, 64, this.userId);
                String[] computeSignaturesSha256Digests = PackageUtils.computeSignaturesSha256Digests(packageInfoAsUser.signatures);
                if (this.certDigest.equals(PackageUtils.computeSignaturesSha256Digest(computeSignaturesSha256Digests)) || (packageInfoAsUser.signatures.length > 1 && this.certDigest.equals(computeSignaturesSha256Digests[0]))) {
                    int i2 = packageInfoAsUser.applicationInfo.uid;
                    if (!AccountManagerBackupHelper.this.mAccountManagerInternal.hasAccountAccess(account, i2)) {
                        AccountManagerBackupHelper.this.mAccountManagerService.grantAppPermission(account, "com.android.AccountManager.ACCOUNT_ACCESS_TOKEN_TYPE", i2);
                    }
                    return true;
                }
                return false;
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }
    }

    public byte[] backupAccountAccessPermissions(int i) {
        AccountManagerService.UserAccounts userAccounts = this.mAccountManagerService.getUserAccounts(i);
        synchronized (userAccounts.dbLock) {
            synchronized (userAccounts.cacheLock) {
                List<Pair<String, Integer>> findAllAccountGrants = userAccounts.accountsDb.findAllAccountGrants();
                if (findAllAccountGrants.isEmpty()) {
                    return null;
                }
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    TypedXmlSerializer newFastSerializer = Xml.newFastSerializer();
                    newFastSerializer.setOutput(byteArrayOutputStream, StandardCharsets.UTF_8.name());
                    newFastSerializer.startDocument((String) null, Boolean.TRUE);
                    newFastSerializer.startTag((String) null, "permissions");
                    PackageManager packageManager = this.mAccountManagerService.mContext.getPackageManager();
                    for (Pair<String, Integer> pair : findAllAccountGrants) {
                        String str = (String) pair.first;
                        String[] packagesForUid = packageManager.getPackagesForUid(((Integer) pair.second).intValue());
                        if (packagesForUid != null) {
                            for (String str2 : packagesForUid) {
                                try {
                                    String computeSignaturesSha256Digest = PackageUtils.computeSignaturesSha256Digest(packageManager.getPackageInfoAsUser(str2, 64, i).signatures);
                                    if (computeSignaturesSha256Digest != null) {
                                        newFastSerializer.startTag((String) null, "permission");
                                        newFastSerializer.attribute((String) null, "account-sha-256", PackageUtils.computeSha256Digest(str.getBytes()));
                                        newFastSerializer.attribute((String) null, "package", str2);
                                        newFastSerializer.attribute((String) null, "digest", computeSignaturesSha256Digest);
                                        newFastSerializer.endTag((String) null, "permission");
                                    }
                                } catch (PackageManager.NameNotFoundException unused) {
                                    Slog.i("AccountManagerBackupHelper", "Skipping backup of account access grant for non-existing package: " + str2);
                                }
                            }
                        }
                    }
                    newFastSerializer.endTag((String) null, "permissions");
                    newFastSerializer.endDocument();
                    newFastSerializer.flush();
                    return byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    Log.e("AccountManagerBackupHelper", "Error backing up account access grants", e);
                    return null;
                }
            }
        }
    }

    public void restoreAccountAccessPermissions(byte[] bArr, int i) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            TypedXmlPullParser newFastPullParser = Xml.newFastPullParser();
            newFastPullParser.setInput(byteArrayInputStream, StandardCharsets.UTF_8.name());
            PackageManager packageManager = this.mAccountManagerService.mContext.getPackageManager();
            int depth = newFastPullParser.getDepth();
            while (XmlUtils.nextElementWithin(newFastPullParser, depth)) {
                if ("permissions".equals(newFastPullParser.getName())) {
                    int depth2 = newFastPullParser.getDepth();
                    while (XmlUtils.nextElementWithin(newFastPullParser, depth2)) {
                        if ("permission".equals(newFastPullParser.getName())) {
                            String attributeValue = newFastPullParser.getAttributeValue((String) null, "account-sha-256");
                            if (TextUtils.isEmpty(attributeValue)) {
                                XmlUtils.skipCurrentTag(newFastPullParser);
                            }
                            String attributeValue2 = newFastPullParser.getAttributeValue((String) null, "package");
                            if (TextUtils.isEmpty(attributeValue2)) {
                                XmlUtils.skipCurrentTag(newFastPullParser);
                            }
                            String attributeValue3 = newFastPullParser.getAttributeValue((String) null, "digest");
                            if (TextUtils.isEmpty(attributeValue3)) {
                                XmlUtils.skipCurrentTag(newFastPullParser);
                            }
                            PendingAppPermission pendingAppPermission = new PendingAppPermission(attributeValue, attributeValue2, attributeValue3, i);
                            if (!pendingAppPermission.apply(packageManager)) {
                                synchronized (this.mLock) {
                                    if (this.mRestorePackageMonitor == null) {
                                        RestorePackageMonitor restorePackageMonitor = new RestorePackageMonitor();
                                        this.mRestorePackageMonitor = restorePackageMonitor;
                                        AccountManagerService accountManagerService = this.mAccountManagerService;
                                        restorePackageMonitor.register(accountManagerService.mContext, accountManagerService.mHandler.getLooper(), true);
                                    }
                                    if (this.mRestorePendingAppPermissions == null) {
                                        this.mRestorePendingAppPermissions = new ArrayList();
                                    }
                                    this.mRestorePendingAppPermissions.add(pendingAppPermission);
                                }
                            }
                        }
                    }
                }
            }
            CancelRestoreCommand cancelRestoreCommand = new CancelRestoreCommand();
            this.mRestoreCancelCommand = cancelRestoreCommand;
            this.mAccountManagerService.mHandler.postDelayed(cancelRestoreCommand, ClipboardService.DEFAULT_CLIPBOARD_TIMEOUT_MILLIS);
        } catch (IOException | XmlPullParserException e) {
            Log.e("AccountManagerBackupHelper", "Error restoring app permissions", e);
        }
    }

    /* loaded from: classes.dex */
    public final class RestorePackageMonitor extends PackageMonitor {
        public RestorePackageMonitor() {
        }

        public void onPackageAdded(String str, int i) {
            synchronized (AccountManagerBackupHelper.this.mLock) {
                if (AccountManagerBackupHelper.this.mRestorePendingAppPermissions == null) {
                    return;
                }
                if (UserHandle.getUserId(i) != 0) {
                    return;
                }
                for (int size = AccountManagerBackupHelper.this.mRestorePendingAppPermissions.size() - 1; size >= 0; size--) {
                    PendingAppPermission pendingAppPermission = (PendingAppPermission) AccountManagerBackupHelper.this.mRestorePendingAppPermissions.get(size);
                    if (pendingAppPermission.packageName.equals(str) && pendingAppPermission.apply(AccountManagerBackupHelper.this.mAccountManagerService.mContext.getPackageManager())) {
                        AccountManagerBackupHelper.this.mRestorePendingAppPermissions.remove(size);
                    }
                }
                if (AccountManagerBackupHelper.this.mRestorePendingAppPermissions.isEmpty() && AccountManagerBackupHelper.this.mRestoreCancelCommand != null) {
                    AccountManagerBackupHelper.this.mAccountManagerService.mHandler.removeCallbacks(AccountManagerBackupHelper.this.mRestoreCancelCommand);
                    AccountManagerBackupHelper.this.mRestoreCancelCommand.run();
                    AccountManagerBackupHelper.this.mRestoreCancelCommand = null;
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class CancelRestoreCommand implements Runnable {
        public CancelRestoreCommand() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (AccountManagerBackupHelper.this.mLock) {
                AccountManagerBackupHelper.this.mRestorePendingAppPermissions = null;
                if (AccountManagerBackupHelper.this.mRestorePackageMonitor != null) {
                    AccountManagerBackupHelper.this.mRestorePackageMonitor.unregister();
                    AccountManagerBackupHelper.this.mRestorePackageMonitor = null;
                }
            }
        }
    }
}
