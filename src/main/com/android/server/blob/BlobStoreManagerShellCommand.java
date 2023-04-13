package com.android.server.blob;

import android.app.ActivityManager;
import android.app.blob.BlobHandle;
import android.os.ShellCommand;
import java.io.PrintWriter;
import java.util.Base64;
/* loaded from: classes.dex */
public class BlobStoreManagerShellCommand extends ShellCommand {
    public final BlobStoreManagerService mService;

    public BlobStoreManagerShellCommand(BlobStoreManagerService blobStoreManagerService) {
        this.mService = blobStoreManagerService;
    }

    public int onCommand(String str) {
        if (str == null) {
            return handleDefaultCommands((String) null);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        char c = 65535;
        switch (str.hashCode()) {
            case -1168531841:
                if (str.equals("delete-blob")) {
                    c = 0;
                    break;
                }
                break;
            case -971115831:
                if (str.equals("clear-all-sessions")) {
                    c = 1;
                    break;
                }
                break;
            case -258166326:
                if (str.equals("clear-all-blobs")) {
                    c = 2;
                    break;
                }
                break;
            case 712607671:
                if (str.equals("query-blob-existence")) {
                    c = 3;
                    break;
                }
                break;
            case 1861559962:
                if (str.equals("idle-maintenance")) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return runDeleteBlob(outPrintWriter);
            case 1:
                return runClearAllSessions(outPrintWriter);
            case 2:
                return runClearAllBlobs(outPrintWriter);
            case 3:
                return runQueryBlobExistence(outPrintWriter);
            case 4:
                return runIdleMaintenance(outPrintWriter);
            default:
                return handleDefaultCommands(str);
        }
    }

    public final int runClearAllSessions(PrintWriter printWriter) {
        ParsedArgs parsedArgs = new ParsedArgs();
        parsedArgs.userId = -1;
        if (parseOptions(printWriter, parsedArgs) < 0) {
            return -1;
        }
        this.mService.runClearAllSessions(parsedArgs.userId);
        return 0;
    }

    public final int runClearAllBlobs(PrintWriter printWriter) {
        ParsedArgs parsedArgs = new ParsedArgs();
        parsedArgs.userId = -1;
        if (parseOptions(printWriter, parsedArgs) < 0) {
            return -1;
        }
        this.mService.runClearAllBlobs(parsedArgs.userId);
        return 0;
    }

    public final int runDeleteBlob(PrintWriter printWriter) {
        ParsedArgs parsedArgs = new ParsedArgs();
        if (parseOptions(printWriter, parsedArgs) < 0) {
            return -1;
        }
        this.mService.deleteBlob(parsedArgs.getBlobHandle(), parsedArgs.userId);
        return 0;
    }

    public final int runIdleMaintenance(PrintWriter printWriter) {
        this.mService.runIdleMaintenance();
        return 0;
    }

    public final int runQueryBlobExistence(PrintWriter printWriter) {
        ParsedArgs parsedArgs = new ParsedArgs();
        if (parseOptions(printWriter, parsedArgs) < 0) {
            return -1;
        }
        printWriter.println(this.mService.isBlobAvailable(parsedArgs.blobId, parsedArgs.userId) ? 1 : 0);
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("BlobStore service (blob_store) commands:");
        outPrintWriter.println("help");
        outPrintWriter.println("    Print this help text.");
        outPrintWriter.println();
        outPrintWriter.println("clear-all-sessions [-u | --user USER_ID]");
        outPrintWriter.println("    Remove all sessions.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's sessions to be removed.");
        outPrintWriter.println("                    If not specified, sessions in all users are removed.");
        outPrintWriter.println();
        outPrintWriter.println("clear-all-blobs [-u | --user USER_ID]");
        outPrintWriter.println("    Remove all blobs.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's blobs to be removed.");
        outPrintWriter.println("                    If not specified, blobs in all users are removed.");
        outPrintWriter.println("delete-blob [-u | --user USER_ID] [--digest DIGEST] [--expiry EXPIRY_TIME] [--label LABEL] [--tag TAG]");
        outPrintWriter.println("    Delete a blob.");
        outPrintWriter.println("    Options:");
        outPrintWriter.println("      -u or --user: specify which user's blobs to be removed;");
        outPrintWriter.println("                    If not specified, blobs in all users are removed.");
        outPrintWriter.println("      --digest: Base64 encoded digest of the blob to delete.");
        outPrintWriter.println("      --expiry: Expiry time of the blob to delete, in milliseconds.");
        outPrintWriter.println("      --label: Label of the blob to delete.");
        outPrintWriter.println("      --tag: Tag of the blob to delete.");
        outPrintWriter.println("idle-maintenance");
        outPrintWriter.println("    Run idle maintenance which takes care of removing stale data.");
        outPrintWriter.println("query-blob-existence [-b BLOB_ID]");
        outPrintWriter.println("    Prints 1 if blob exists, otherwise 0.");
        outPrintWriter.println();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0064, code lost:
        if (r0.equals("--label") == false) goto L5;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int parseOptions(PrintWriter printWriter, ParsedArgs parsedArgs) {
        while (true) {
            String nextOption = getNextOption();
            char c = 0;
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case -1620968108:
                        break;
                    case 1493:
                        if (nextOption.equals("-b")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1512:
                        if (nextOption.equals("-u")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case 43013626:
                        if (nextOption.equals("--tag")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1068100452:
                        if (nextOption.equals("--digest")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1110854355:
                        if (nextOption.equals("--expiry")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1332867059:
                        if (nextOption.equals("--algo")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1333469547:
                        if (nextOption.equals("--user")) {
                            c = 7;
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
                        parsedArgs.label = getNextArgRequired();
                        break;
                    case 1:
                        parsedArgs.blobId = Long.parseLong(getNextArgRequired());
                        break;
                    case 2:
                    case 7:
                        parsedArgs.userId = Integer.parseInt(getNextArgRequired());
                        break;
                    case 3:
                        parsedArgs.tag = getNextArgRequired();
                        break;
                    case 4:
                        parsedArgs.digest = Base64.getDecoder().decode(getNextArgRequired());
                        break;
                    case 5:
                        parsedArgs.expiryTimeMillis = Long.parseLong(getNextArgRequired());
                        break;
                    case 6:
                        parsedArgs.algorithm = getNextArgRequired();
                        break;
                    default:
                        printWriter.println("Error: unknown option '" + nextOption + "'");
                        return -1;
                }
            } else {
                if (parsedArgs.userId == -2) {
                    parsedArgs.userId = ActivityManager.getCurrentUser();
                }
                return 0;
            }
        }
    }

    /* loaded from: classes.dex */
    public static class ParsedArgs {
        public String algorithm;
        public long blobId;
        public byte[] digest;
        public long expiryTimeMillis;
        public CharSequence label;
        public String tag;
        public int userId;

        public ParsedArgs() {
            this.userId = -2;
            this.algorithm = "SHA-256";
        }

        public BlobHandle getBlobHandle() {
            return BlobHandle.create(this.algorithm, this.digest, this.label, this.expiryTimeMillis, this.tag);
        }
    }
}
