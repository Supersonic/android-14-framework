package com.android.server.rollback;

import android.content.pm.VersionedPackage;
import android.content.rollback.PackageRollbackInfo;
import android.content.rollback.RollbackInfo;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.system.ErrnoException;
import android.system.Os;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class RollbackStore {
    public final File mRollbackDataDir;
    public final File mRollbackHistoryDir;

    public RollbackStore(File file, File file2) {
        this.mRollbackDataDir = file;
        this.mRollbackHistoryDir = file2;
    }

    public static List<Rollback> loadRollbacks(File file) {
        File[] listFiles;
        ArrayList arrayList = new ArrayList();
        file.mkdirs();
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                try {
                    arrayList.add(loadRollback(file2));
                } catch (IOException e) {
                    Slog.e("RollbackManager", "Unable to read rollback at " + file2, e);
                    removeFile(file2);
                }
            }
        }
        return arrayList;
    }

    public List<Rollback> loadRollbacks() {
        return loadRollbacks(this.mRollbackDataDir);
    }

    public List<Rollback> loadHistorialRollbacks() {
        return loadRollbacks(this.mRollbackHistoryDir);
    }

    public static List<Integer> toIntList(JSONArray jSONArray) throws JSONException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(Integer.valueOf(jSONArray.getInt(i)));
        }
        return arrayList;
    }

    public static JSONArray fromIntList(List<Integer> list) {
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            jSONArray.put(list.get(i));
        }
        return jSONArray;
    }

    public static JSONArray convertToJsonArray(List<PackageRollbackInfo.RestoreInfo> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        for (PackageRollbackInfo.RestoreInfo restoreInfo : list) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("userId", restoreInfo.userId);
            jSONObject.put("appId", restoreInfo.appId);
            jSONObject.put("seInfo", restoreInfo.seInfo);
            jSONArray.put(jSONObject);
        }
        return jSONArray;
    }

    public static ArrayList<PackageRollbackInfo.RestoreInfo> convertToRestoreInfoArray(JSONArray jSONArray) throws JSONException {
        ArrayList<PackageRollbackInfo.RestoreInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            arrayList.add(new PackageRollbackInfo.RestoreInfo(jSONObject.getInt("userId"), jSONObject.getInt("appId"), jSONObject.getString("seInfo")));
        }
        return arrayList;
    }

    public static JSONArray extensionVersionsToJson(SparseIntArray sparseIntArray) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        for (int i = 0; i < sparseIntArray.size(); i++) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("sdkVersion", sparseIntArray.keyAt(i));
            jSONObject.put("extensionVersion", sparseIntArray.valueAt(i));
            jSONArray.put(jSONObject);
        }
        return jSONArray;
    }

    public static SparseIntArray extensionVersionsFromJson(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null) {
            return new SparseIntArray(0);
        }
        SparseIntArray sparseIntArray = new SparseIntArray(jSONArray.length());
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            sparseIntArray.append(jSONObject.getInt("sdkVersion"), jSONObject.getInt("extensionVersion"));
        }
        return sparseIntArray;
    }

    public static JSONObject rollbackInfoToJson(RollbackInfo rollbackInfo) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("rollbackId", rollbackInfo.getRollbackId());
        jSONObject.put("packages", toJson(rollbackInfo.getPackages()));
        jSONObject.put("isStaged", rollbackInfo.isStaged());
        jSONObject.put("causePackages", versionedPackagesToJson(rollbackInfo.getCausePackages()));
        jSONObject.put("committedSessionId", rollbackInfo.getCommittedSessionId());
        return jSONObject;
    }

    public static RollbackInfo rollbackInfoFromJson(JSONObject jSONObject) throws JSONException {
        return new RollbackInfo(jSONObject.getInt("rollbackId"), packageRollbackInfosFromJson(jSONObject.getJSONArray("packages")), jSONObject.getBoolean("isStaged"), versionedPackagesFromJson(jSONObject.getJSONArray("causePackages")), jSONObject.getInt("committedSessionId"));
    }

    public Rollback createNonStagedRollback(int i, int i2, int i3, String str, int[] iArr, SparseIntArray sparseIntArray) {
        return new Rollback(i, new File(this.mRollbackDataDir, Integer.toString(i)), i2, false, i3, str, iArr, sparseIntArray);
    }

    public Rollback createStagedRollback(int i, int i2, int i3, String str, int[] iArr, SparseIntArray sparseIntArray) {
        return new Rollback(i, new File(this.mRollbackDataDir, Integer.toString(i)), i2, true, i3, str, iArr, sparseIntArray);
    }

    public static boolean isLinkPossible(File file, File file2) {
        try {
            return Os.stat(file.getAbsolutePath()).st_dev == Os.stat(file2.getAbsolutePath()).st_dev;
        } catch (ErrnoException unused) {
            return false;
        }
    }

    public static void backupPackageCodePath(Rollback rollback, String str, String str2) throws IOException {
        File file = new File(str2);
        File file2 = new File(rollback.getBackupDir(), str);
        file2.mkdirs();
        File file3 = new File(file2, file.getName());
        boolean isLinkPossible = isLinkPossible(file, file2);
        boolean z = true;
        boolean z2 = !isLinkPossible;
        if (!z2) {
            try {
                Os.link(file.getAbsolutePath(), file3.getAbsolutePath());
            } catch (ErrnoException e) {
                if (SystemProperties.getBoolean("persist.rollback.is_test", false)) {
                    throw new IOException(e);
                }
            }
        }
        z = z2;
        if (z) {
            Files.copy(file.toPath(), file3.toPath(), new CopyOption[0]);
        }
    }

    public static File[] getPackageCodePaths(Rollback rollback, String str) {
        File[] listFiles = new File(rollback.getBackupDir(), str).listFiles();
        if (listFiles == null || listFiles.length == 0) {
            return null;
        }
        return listFiles;
    }

    public static void deletePackageCodePaths(Rollback rollback) {
        for (PackageRollbackInfo packageRollbackInfo : rollback.info.getPackages()) {
            removeFile(new File(rollback.getBackupDir(), packageRollbackInfo.getPackageName()));
        }
    }

    public static void saveRollback(Rollback rollback, File file) {
        AtomicFile atomicFile = new AtomicFile(new File(file, "rollback.json"));
        FileOutputStream fileOutputStream = null;
        try {
            file.mkdirs();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("info", rollbackInfoToJson(rollback.info));
            jSONObject.put("timestamp", rollback.getTimestamp().toString());
            jSONObject.put("originalSessionId", rollback.getOriginalSessionId());
            jSONObject.put("state", rollback.getStateAsString());
            jSONObject.put("stateDescription", rollback.getStateDescription());
            jSONObject.put("restoreUserDataInProgress", rollback.isRestoreUserDataInProgress());
            jSONObject.put("userId", rollback.getUserId());
            jSONObject.putOpt("installerPackageName", rollback.getInstallerPackageName());
            jSONObject.putOpt("extensionVersions", extensionVersionsToJson(rollback.getExtensionVersions()));
            fileOutputStream = atomicFile.startWrite();
            fileOutputStream.write(jSONObject.toString().getBytes());
            fileOutputStream.flush();
            atomicFile.finishWrite(fileOutputStream);
        } catch (IOException | JSONException e) {
            Slog.e("RollbackManager", "Unable to save rollback for: " + rollback.info.getRollbackId(), e);
            if (fileOutputStream != null) {
                atomicFile.failWrite(fileOutputStream);
            }
        }
    }

    public static void saveRollback(Rollback rollback) {
        saveRollback(rollback, rollback.getBackupDir());
    }

    public void saveRollbackToHistory(Rollback rollback) {
        String hexString = Long.toHexString(rollback.getTimestamp().getEpochSecond());
        String num = Integer.toString(rollback.info.getRollbackId());
        File file = this.mRollbackHistoryDir;
        saveRollback(rollback, new File(file, num + PackageManagerShellCommandDataLoader.STDIN_PATH + hexString));
    }

    public static void deleteRollback(Rollback rollback) {
        removeFile(rollback.getBackupDir());
    }

    public static Rollback loadRollback(File file) throws IOException {
        try {
            return rollbackFromJson(new JSONObject(IoUtils.readFileAsString(new File(file, "rollback.json").getAbsolutePath())), file);
        } catch (ParseException | DateTimeParseException | JSONException e) {
            throw new IOException(e);
        }
    }

    @VisibleForTesting
    public static Rollback rollbackFromJson(JSONObject jSONObject, File file) throws JSONException, ParseException {
        return new Rollback(rollbackInfoFromJson(jSONObject.getJSONObject("info")), file, Instant.parse(jSONObject.getString("timestamp")), jSONObject.optInt("originalSessionId", jSONObject.optInt("stagedSessionId", -1)), Rollback.rollbackStateFromString(jSONObject.getString("state")), jSONObject.optString("stateDescription"), jSONObject.getBoolean("restoreUserDataInProgress"), jSONObject.optInt("userId", UserHandle.SYSTEM.getIdentifier()), jSONObject.optString("installerPackageName", ""), extensionVersionsFromJson(jSONObject.optJSONArray("extensionVersions")));
    }

    public static JSONObject toJson(VersionedPackage versionedPackage) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("packageName", versionedPackage.getPackageName());
        jSONObject.put("longVersionCode", versionedPackage.getLongVersionCode());
        return jSONObject;
    }

    public static VersionedPackage versionedPackageFromJson(JSONObject jSONObject) throws JSONException {
        return new VersionedPackage(jSONObject.getString("packageName"), jSONObject.getLong("longVersionCode"));
    }

    public static JSONObject toJson(PackageRollbackInfo packageRollbackInfo) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("versionRolledBackFrom", toJson(packageRollbackInfo.getVersionRolledBackFrom()));
        jSONObject.put("versionRolledBackTo", toJson(packageRollbackInfo.getVersionRolledBackTo()));
        List pendingBackups = packageRollbackInfo.getPendingBackups();
        ArrayList pendingRestores = packageRollbackInfo.getPendingRestores();
        List snapshottedUsers = packageRollbackInfo.getSnapshottedUsers();
        jSONObject.put("pendingBackups", fromIntList(pendingBackups));
        jSONObject.put("pendingRestores", convertToJsonArray(pendingRestores));
        jSONObject.put("isApex", packageRollbackInfo.isApex());
        jSONObject.put("isApkInApex", packageRollbackInfo.isApkInApex());
        jSONObject.put("installedUsers", fromIntList(snapshottedUsers));
        jSONObject.put("rollbackDataPolicy", packageRollbackInfo.getRollbackDataPolicy());
        return jSONObject;
    }

    public static PackageRollbackInfo packageRollbackInfoFromJson(JSONObject jSONObject) throws JSONException {
        return new PackageRollbackInfo(versionedPackageFromJson(jSONObject.getJSONObject("versionRolledBackFrom")), versionedPackageFromJson(jSONObject.getJSONObject("versionRolledBackTo")), toIntList(jSONObject.getJSONArray("pendingBackups")), convertToRestoreInfoArray(jSONObject.getJSONArray("pendingRestores")), jSONObject.getBoolean("isApex"), jSONObject.getBoolean("isApkInApex"), toIntList(jSONObject.getJSONArray("installedUsers")), jSONObject.optInt("rollbackDataPolicy", 0));
    }

    public static JSONArray versionedPackagesToJson(List<VersionedPackage> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        for (VersionedPackage versionedPackage : list) {
            jSONArray.put(toJson(versionedPackage));
        }
        return jSONArray;
    }

    public static List<VersionedPackage> versionedPackagesFromJson(JSONArray jSONArray) throws JSONException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(versionedPackageFromJson(jSONArray.getJSONObject(i)));
        }
        return arrayList;
    }

    public static JSONArray toJson(List<PackageRollbackInfo> list) throws JSONException {
        JSONArray jSONArray = new JSONArray();
        for (PackageRollbackInfo packageRollbackInfo : list) {
            jSONArray.put(toJson(packageRollbackInfo));
        }
        return jSONArray;
    }

    public static List<PackageRollbackInfo> packageRollbackInfosFromJson(JSONArray jSONArray) throws JSONException {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            arrayList.add(packageRollbackInfoFromJson(jSONArray.getJSONObject(i)));
        }
        return arrayList;
    }

    public static void removeFile(File file) {
        if (file.isDirectory()) {
            for (File file2 : file.listFiles()) {
                removeFile(file2);
            }
        }
        if (file.exists()) {
            file.delete();
        }
    }
}
