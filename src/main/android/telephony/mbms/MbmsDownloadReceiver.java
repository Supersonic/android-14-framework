package android.telephony.mbms;

import android.annotation.SystemApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.net.Uri;
import android.p008os.Bundle;
import android.telephony.MbmsDownloadSession;
import android.telephony.mbms.vendor.VendorUtils;
import android.util.Log;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
/* loaded from: classes3.dex */
public class MbmsDownloadReceiver extends BroadcastReceiver {
    public static final String DOWNLOAD_TOKEN_SUFFIX = ".download_token";
    private static final String EMBMS_INTENT_PERMISSION = "android.permission.SEND_EMBMS_INTENTS";
    private static final String LOG_TAG = "MbmsDownloadReceiver";
    private static final int MAX_TEMP_FILE_RETRIES = 5;
    public static final String MBMS_FILE_PROVIDER_META_DATA_KEY = "mbms-file-provider-authority";
    @SystemApi
    public static final int RESULT_APP_NOTIFICATION_ERROR = 6;
    @SystemApi
    public static final int RESULT_BAD_TEMP_FILE_ROOT = 3;
    @SystemApi
    public static final int RESULT_DOWNLOAD_FINALIZATION_ERROR = 4;
    @SystemApi
    public static final int RESULT_INVALID_ACTION = 1;
    @SystemApi
    public static final int RESULT_MALFORMED_INTENT = 2;
    @SystemApi
    public static final int RESULT_OK = 0;
    @SystemApi
    public static final int RESULT_TEMP_FILE_GENERATION_ERROR = 5;
    private static final String TEMP_FILE_STAGING_LOCATION = "staged_completed_files";
    private static final String TEMP_FILE_SUFFIX = ".embms.temp";
    private String mFileProviderAuthorityCache = null;
    private String mMiddlewarePackageNameCache = null;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        verifyPermissionIntegrity(context);
        if (!verifyIntentContents(context, intent)) {
            setResultCode(2);
        } else if (!Objects.equals(intent.getStringExtra(VendorUtils.EXTRA_TEMP_FILE_ROOT), MbmsTempFileProvider.getEmbmsTempFileDir(context).getPath())) {
            setResultCode(3);
        } else if (VendorUtils.ACTION_DOWNLOAD_RESULT_INTERNAL.equals(intent.getAction())) {
            moveDownloadedFile(context, intent);
            cleanupPostMove(context, intent);
        } else if (VendorUtils.ACTION_FILE_DESCRIPTOR_REQUEST.equals(intent.getAction())) {
            generateTempFiles(context, intent);
        } else if (VendorUtils.ACTION_CLEANUP.equals(intent.getAction())) {
            cleanupTempFiles(context, intent);
        } else {
            setResultCode(1);
        }
    }

    private boolean verifyIntentContents(Context context, Intent intent) {
        if (VendorUtils.ACTION_DOWNLOAD_RESULT_INTERNAL.equals(intent.getAction())) {
            if (!intent.hasExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_RESULT)) {
                Log.m104w(LOG_TAG, "Download result did not include a result code. Ignoring.");
                return false;
            } else if (intent.hasExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST)) {
                if (1 != intent.getIntExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_RESULT, 2)) {
                    return true;
                }
                if (!intent.hasExtra(VendorUtils.EXTRA_TEMP_FILE_ROOT)) {
                    Log.m104w(LOG_TAG, "Download result did not include the temp file root. Ignoring.");
                    return false;
                } else if (!intent.hasExtra(MbmsDownloadSession.EXTRA_MBMS_FILE_INFO)) {
                    Log.m104w(LOG_TAG, "Download result did not include the associated file info. Ignoring.");
                    return false;
                } else if (intent.hasExtra(VendorUtils.EXTRA_FINAL_URI)) {
                    DownloadRequest request = (DownloadRequest) intent.getParcelableExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST, DownloadRequest.class);
                    String expectedTokenFileName = request.getHash() + DOWNLOAD_TOKEN_SUFFIX;
                    File expectedTokenFile = new File(MbmsUtils.getEmbmsTempFileDirForService(context, request.getFileServiceId()), expectedTokenFileName);
                    if (!expectedTokenFile.exists()) {
                        Log.m104w(LOG_TAG, "Supplied download request does not match a token that we have. Expected " + expectedTokenFile);
                        return false;
                    }
                } else {
                    Log.m104w(LOG_TAG, "Download result did not include the path to the final temp file. Ignoring.");
                    return false;
                }
            } else {
                Log.m104w(LOG_TAG, "Download result did not include the associated request. Ignoring.");
                return false;
            }
        } else if (VendorUtils.ACTION_FILE_DESCRIPTOR_REQUEST.equals(intent.getAction())) {
            if (!intent.hasExtra(VendorUtils.EXTRA_SERVICE_ID)) {
                Log.m104w(LOG_TAG, "Temp file request did not include the associated service id. Ignoring.");
                return false;
            } else if (!intent.hasExtra(VendorUtils.EXTRA_TEMP_FILE_ROOT)) {
                Log.m104w(LOG_TAG, "Download result did not include the temp file root. Ignoring.");
                return false;
            }
        } else if (VendorUtils.ACTION_CLEANUP.equals(intent.getAction())) {
            if (!intent.hasExtra(VendorUtils.EXTRA_SERVICE_ID)) {
                Log.m104w(LOG_TAG, "Cleanup request did not include the associated service id. Ignoring.");
                return false;
            } else if (!intent.hasExtra(VendorUtils.EXTRA_TEMP_FILE_ROOT)) {
                Log.m104w(LOG_TAG, "Cleanup request did not include the temp file root. Ignoring.");
                return false;
            } else if (!intent.hasExtra(VendorUtils.EXTRA_TEMP_FILES_IN_USE)) {
                Log.m104w(LOG_TAG, "Cleanup request did not include the list of temp files in use. Ignoring.");
                return false;
            }
        }
        return true;
    }

    private void moveDownloadedFile(Context context, Intent intent) {
        DownloadRequest request = (DownloadRequest) intent.getParcelableExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST, DownloadRequest.class);
        Intent intentForApp = request.getIntentForApp();
        if (intentForApp == null) {
            Log.m108i(LOG_TAG, "Malformed app notification intent");
            setResultCode(6);
            return;
        }
        int result = intent.getIntExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_RESULT, 2);
        intentForApp.putExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_RESULT, result);
        intentForApp.putExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST, request);
        if (result != 1) {
            Log.m108i(LOG_TAG, "Download request indicated a failed download. Aborting.");
            context.sendBroadcast(intentForApp);
            setResultCode(0);
            return;
        }
        Uri finalTempFile = (Uri) intent.getParcelableExtra(VendorUtils.EXTRA_FINAL_URI, Uri.class);
        if (!verifyTempFilePath(context, request.getFileServiceId(), finalTempFile)) {
            Log.m104w(LOG_TAG, "Download result specified an invalid temp file " + finalTempFile);
            setResultCode(4);
            return;
        }
        FileInfo completedFileInfo = (FileInfo) intent.getParcelableExtra(MbmsDownloadSession.EXTRA_MBMS_FILE_INFO, FileInfo.class);
        Path appSpecifiedDestination = FileSystems.getDefault().getPath(request.getDestinationUri().getPath(), new String[0]);
        try {
            String relativeLocation = getFileRelativePath(request.getSourceUri().getPath(), completedFileInfo.getUri().getPath());
            Uri finalLocation = moveToFinalLocation(finalTempFile, appSpecifiedDestination, relativeLocation);
            intentForApp.putExtra(MbmsDownloadSession.EXTRA_MBMS_COMPLETED_FILE_URI, finalLocation);
            intentForApp.putExtra(MbmsDownloadSession.EXTRA_MBMS_FILE_INFO, completedFileInfo);
            context.sendBroadcast(intentForApp);
            setResultCode(0);
        } catch (IOException e) {
            Log.m104w(LOG_TAG, "Failed to move temp file to final destination");
            setResultCode(4);
        }
    }

    private void cleanupPostMove(Context context, Intent intent) {
        DownloadRequest request = (DownloadRequest) intent.getParcelableExtra(MbmsDownloadSession.EXTRA_MBMS_DOWNLOAD_REQUEST, DownloadRequest.class);
        if (request == null) {
            Log.m104w(LOG_TAG, "Intent does not include a DownloadRequest. Ignoring.");
            return;
        }
        List<Uri> tempFiles = intent.getParcelableArrayListExtra(VendorUtils.EXTRA_TEMP_LIST, Uri.class);
        if (tempFiles == null) {
            return;
        }
        for (Uri tempFileUri : tempFiles) {
            if (verifyTempFilePath(context, request.getFileServiceId(), tempFileUri)) {
                File tempFile = new File(tempFileUri.getSchemeSpecificPart());
                if (!tempFile.delete()) {
                    Log.m104w(LOG_TAG, "Failed to delete temp file at " + tempFile.getPath());
                }
            }
        }
    }

    private void generateTempFiles(Context context, Intent intent) {
        String serviceId = intent.getStringExtra(VendorUtils.EXTRA_SERVICE_ID);
        if (serviceId == null) {
            Log.m104w(LOG_TAG, "Temp file request did not include the associated service id. Ignoring.");
            setResultCode(2);
            return;
        }
        int fdCount = intent.getIntExtra(VendorUtils.EXTRA_FD_COUNT, 0);
        List<Uri> pausedList = intent.getParcelableArrayListExtra(VendorUtils.EXTRA_PAUSED_LIST, Uri.class);
        if (fdCount == 0 && (pausedList == null || pausedList.size() == 0)) {
            Log.m108i(LOG_TAG, "No temp files actually requested. Ending.");
            setResultCode(0);
            setResultExtras(Bundle.EMPTY);
            return;
        }
        ArrayList<UriPathPair> freshTempFiles = generateFreshTempFiles(context, serviceId, fdCount);
        ArrayList<UriPathPair> pausedFiles = generateUrisForPausedFiles(context, serviceId, pausedList);
        Bundle result = new Bundle();
        result.putParcelableArrayList(VendorUtils.EXTRA_FREE_URI_LIST, freshTempFiles);
        result.putParcelableArrayList(VendorUtils.EXTRA_PAUSED_URI_LIST, pausedFiles);
        setResultCode(0);
        setResultExtras(result);
    }

    private ArrayList<UriPathPair> generateFreshTempFiles(Context context, String serviceId, int freshFdCount) {
        File tempFileDir = MbmsUtils.getEmbmsTempFileDirForService(context, serviceId);
        if (!tempFileDir.exists()) {
            tempFileDir.mkdirs();
        }
        ArrayList<UriPathPair> result = new ArrayList<>(freshFdCount);
        for (int i = 0; i < freshFdCount; i++) {
            File tempFile = generateSingleTempFile(tempFileDir);
            if (tempFile == null) {
                setResultCode(5);
                Log.m104w(LOG_TAG, "Failed to generate a temp file. Moving on.");
            } else {
                Uri fileUri = Uri.fromFile(tempFile);
                Uri contentUri = MbmsTempFileProvider.getUriForFile(context, getFileProviderAuthorityCached(context), tempFile);
                context.grantUriPermission(getMiddlewarePackageCached(context), contentUri, 3);
                result.add(new UriPathPair(fileUri, contentUri));
            }
        }
        return result;
    }

    private static File generateSingleTempFile(File tempFileDir) {
        int numTries = 0;
        while (numTries < 5) {
            numTries++;
            String fileName = UUID.randomUUID() + TEMP_FILE_SUFFIX;
            File tempFile = new File(tempFileDir, fileName);
            if (tempFile.createNewFile()) {
                return tempFile.getCanonicalFile();
            }
            continue;
        }
        return null;
    }

    private ArrayList<UriPathPair> generateUrisForPausedFiles(Context context, String serviceId, List<Uri> pausedFiles) {
        if (pausedFiles == null) {
            return new ArrayList<>(0);
        }
        ArrayList<UriPathPair> result = new ArrayList<>(pausedFiles.size());
        for (Uri fileUri : pausedFiles) {
            if (!verifyTempFilePath(context, serviceId, fileUri)) {
                Log.m104w(LOG_TAG, "Supplied file " + fileUri + " is not a valid temp file to resume");
                setResultCode(5);
            } else {
                File tempFile = new File(fileUri.getSchemeSpecificPart());
                if (!tempFile.exists()) {
                    Log.m104w(LOG_TAG, "Supplied file " + fileUri + " does not exist.");
                    setResultCode(5);
                } else {
                    Uri contentUri = MbmsTempFileProvider.getUriForFile(context, getFileProviderAuthorityCached(context), tempFile);
                    context.grantUriPermission(getMiddlewarePackageCached(context), contentUri, 3);
                    result.add(new UriPathPair(fileUri, contentUri));
                }
            }
        }
        return result;
    }

    private void cleanupTempFiles(Context context, Intent intent) {
        String serviceId = intent.getStringExtra(VendorUtils.EXTRA_SERVICE_ID);
        File tempFileDir = MbmsUtils.getEmbmsTempFileDirForService(context, serviceId);
        final List<Uri> filesInUse = intent.getParcelableArrayListExtra(VendorUtils.EXTRA_TEMP_FILES_IN_USE, Uri.class);
        File[] filesToDelete = tempFileDir.listFiles(new FileFilter() { // from class: android.telephony.mbms.MbmsDownloadReceiver.1
            @Override // java.io.FileFilter
            public boolean accept(File file) {
                try {
                    File canonicalFile = file.getCanonicalFile();
                    if (!canonicalFile.getName().endsWith(MbmsDownloadReceiver.TEMP_FILE_SUFFIX)) {
                        return false;
                    }
                    Uri fileInUseUri = Uri.fromFile(canonicalFile);
                    return !filesInUse.contains(fileInUseUri);
                } catch (IOException e) {
                    Log.m104w(MbmsDownloadReceiver.LOG_TAG, "Got IOException canonicalizing " + file + ", not deleting.");
                    return false;
                }
            }
        });
        for (File fileToDelete : filesToDelete) {
            fileToDelete.delete();
        }
    }

    private static Uri moveToFinalLocation(Uri fromPath, Path appSpecifiedPath, String relativeLocation) throws IOException {
        if (!"file".equals(fromPath.getScheme())) {
            Log.m104w(LOG_TAG, "Downloaded file location uri " + fromPath + " does not have a file scheme");
            return null;
        }
        Path fromFile = FileSystems.getDefault().getPath(fromPath.getPath(), new String[0]);
        Path toFile = appSpecifiedPath.resolve(relativeLocation);
        if (!Files.isDirectory(toFile.getParent(), new LinkOption[0])) {
            Files.createDirectories(toFile.getParent(), new FileAttribute[0]);
        }
        Path result = Files.move(fromFile, toFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return Uri.fromFile(result.toFile());
    }

    public static String getFileRelativePath(String sourceUriPath, String fileInfoPath) {
        if (sourceUriPath.endsWith("*")) {
            int lastSlash = sourceUriPath.lastIndexOf(47);
            sourceUriPath = sourceUriPath.substring(0, lastSlash);
        }
        if (!fileInfoPath.startsWith(sourceUriPath)) {
            Log.m110e(LOG_TAG, "File location specified in FileInfo does not match the source URI. source: " + sourceUriPath + " fileinfo path: " + fileInfoPath);
            return null;
        } else if (fileInfoPath.length() == sourceUriPath.length()) {
            return sourceUriPath.substring(sourceUriPath.lastIndexOf(47) + 1);
        } else {
            String prefixOmittedPath = fileInfoPath.substring(sourceUriPath.length());
            if (prefixOmittedPath.startsWith("/")) {
                return prefixOmittedPath.substring(1);
            }
            return prefixOmittedPath;
        }
    }

    private static boolean verifyTempFilePath(Context context, String serviceId, Uri filePath) {
        if (!"file".equals(filePath.getScheme())) {
            Log.m104w(LOG_TAG, "Uri " + filePath + " does not have a file scheme");
            return false;
        }
        String path = filePath.getSchemeSpecificPart();
        File tempFile = new File(path);
        if (!tempFile.exists()) {
            Log.m104w(LOG_TAG, "File at " + path + " does not exist.");
            return false;
        } else if (!MbmsUtils.isContainedIn(MbmsUtils.getEmbmsTempFileDirForService(context, serviceId), tempFile)) {
            Log.m104w(LOG_TAG, "File at " + path + " is not contained in the temp file root, which is " + MbmsUtils.getEmbmsTempFileDirForService(context, serviceId));
            return false;
        } else {
            return true;
        }
    }

    private String getFileProviderAuthorityCached(Context context) {
        String str = this.mFileProviderAuthorityCache;
        if (str != null) {
            return str;
        }
        String fileProviderAuthority = getFileProviderAuthority(context);
        this.mFileProviderAuthorityCache = fileProviderAuthority;
        return fileProviderAuthority;
    }

    private static String getFileProviderAuthority(Context context) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128);
            if (appInfo.metaData == null) {
                throw new RuntimeException("App must declare the file provider authority as metadata in the manifest.");
            }
            String authority = appInfo.metaData.getString(MBMS_FILE_PROVIDER_META_DATA_KEY);
            if (authority == null) {
                throw new RuntimeException("App must declare the file provider authority as metadata in the manifest.");
            }
            return authority;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Package manager couldn't find " + context.getPackageName());
        }
    }

    private String getMiddlewarePackageCached(Context context) {
        if (this.mMiddlewarePackageNameCache == null) {
            this.mMiddlewarePackageNameCache = MbmsUtils.getMiddlewareServiceInfo(context, MbmsDownloadSession.MBMS_DOWNLOAD_SERVICE_ACTION).packageName;
        }
        return this.mMiddlewarePackageNameCache;
    }

    private void verifyPermissionIntegrity(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent queryIntent = new Intent(context, MbmsDownloadReceiver.class);
        List<ResolveInfo> infos = pm.queryBroadcastReceivers(queryIntent, 0);
        if (infos.size() != 1) {
            throw new IllegalStateException("Non-unique download receiver in your app");
        }
        ActivityInfo selfInfo = infos.get(0).activityInfo;
        if (selfInfo == null) {
            throw new IllegalStateException("Queried ResolveInfo does not contain a receiver");
        }
        if (MbmsUtils.getOverrideServiceName(context, MbmsDownloadSession.MBMS_DOWNLOAD_SERVICE_ACTION) != null) {
            if (selfInfo.permission == null) {
                throw new IllegalStateException("MbmsDownloadReceiver must require some permission");
            }
        } else if (!Objects.equals("android.permission.SEND_EMBMS_INTENTS", selfInfo.permission)) {
            throw new IllegalStateException("MbmsDownloadReceiver must require the SEND_EMBMS_INTENTS permission.");
        }
    }
}
