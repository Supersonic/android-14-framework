package com.android.server.blob;

import android.app.blob.BlobHandle;
import android.app.blob.IBlobCommitCallback;
import android.app.blob.IBlobStoreSession;
import android.content.Context;
import android.os.Binder;
import android.os.FileUtils;
import android.os.LimitExceededException;
import android.os.ParcelFileDescriptor;
import android.os.ParcelableException;
import android.os.RemoteException;
import android.os.RevocableFileDescriptor;
import android.os.Trace;
import android.os.storage.StorageManager;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.text.format.Formatter;
import android.util.ExceptionUtils;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.server.blob.BlobStoreManagerService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* JADX INFO: Access modifiers changed from: package-private */
@VisibleForTesting
/* loaded from: classes.dex */
public class BlobStoreSession extends IBlobStoreSession.Stub {
    @GuardedBy({"mSessionLock"})
    public final BlobAccessMode mBlobAccessMode;
    @GuardedBy({"mSessionLock"})
    public IBlobCommitCallback mBlobCommitCallback;
    public final BlobHandle mBlobHandle;
    public final Context mContext;
    public final long mCreationTimeMs;
    public byte[] mDataDigest;
    public final BlobStoreManagerService.SessionStateChangeListener mListener;
    public final String mOwnerPackageName;
    public final int mOwnerUid;
    @GuardedBy({"mRevocableFds"})
    public final ArrayList<RevocableFileDescriptor> mRevocableFds;
    public File mSessionFile;
    public final long mSessionId;
    public final Object mSessionLock;
    @GuardedBy({"mSessionLock"})
    public int mState;

    public BlobStoreSession(Context context, long j, BlobHandle blobHandle, int i, String str, long j2, BlobStoreManagerService.SessionStateChangeListener sessionStateChangeListener) {
        this.mSessionLock = new Object();
        this.mRevocableFds = new ArrayList<>();
        this.mState = 0;
        this.mBlobAccessMode = new BlobAccessMode();
        this.mContext = context;
        this.mBlobHandle = blobHandle;
        this.mSessionId = j;
        this.mOwnerUid = i;
        this.mOwnerPackageName = str;
        this.mCreationTimeMs = j2;
        this.mListener = sessionStateChangeListener;
    }

    public BlobStoreSession(Context context, long j, BlobHandle blobHandle, int i, String str, BlobStoreManagerService.SessionStateChangeListener sessionStateChangeListener) {
        this(context, j, blobHandle, i, str, System.currentTimeMillis(), sessionStateChangeListener);
    }

    public BlobHandle getBlobHandle() {
        return this.mBlobHandle;
    }

    public long getSessionId() {
        return this.mSessionId;
    }

    public int getOwnerUid() {
        return this.mOwnerUid;
    }

    public String getOwnerPackageName() {
        return this.mOwnerPackageName;
    }

    public boolean hasAccess(int i, String str) {
        return this.mOwnerUid == i && this.mOwnerPackageName.equals(str);
    }

    public void open() {
        synchronized (this.mSessionLock) {
            if (isFinalized()) {
                throw new IllegalStateException("Not allowed to open session with state: " + stateToString(this.mState));
            }
            this.mState = 1;
        }
    }

    public int getState() {
        int i;
        synchronized (this.mSessionLock) {
            i = this.mState;
        }
        return i;
    }

    public void sendCommitCallbackResult(int i) {
        synchronized (this.mSessionLock) {
            try {
                this.mBlobCommitCallback.onResult(i);
            } catch (RemoteException e) {
                Slog.d("BlobStore", "Error sending the callback result", e);
            }
            this.mBlobCommitCallback = null;
        }
    }

    public BlobAccessMode getBlobAccessMode() {
        BlobAccessMode blobAccessMode;
        synchronized (this.mSessionLock) {
            blobAccessMode = this.mBlobAccessMode;
        }
        return blobAccessMode;
    }

    public boolean isFinalized() {
        boolean z;
        synchronized (this.mSessionLock) {
            int i = this.mState;
            z = i == 3 || i == 2;
        }
        return z;
    }

    public boolean isExpired() {
        long lastModified = getSessionFile().lastModified();
        if (lastModified == 0) {
            lastModified = this.mCreationTimeMs;
        }
        return BlobStoreConfig.hasSessionExpired(lastModified);
    }

    public ParcelFileDescriptor openWrite(long j, long j2) {
        FileDescriptor fileDescriptor;
        ParcelFileDescriptor revocableFileDescriptor;
        Preconditions.checkArgumentNonnegative(j, "offsetBytes must not be negative");
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to write in state: " + stateToString(this.mState));
            }
        }
        try {
            fileDescriptor = openWriteInternal(j, j2);
            try {
                RevocableFileDescriptor revocableFileDescriptor2 = new RevocableFileDescriptor(this.mContext, fileDescriptor);
                synchronized (this.mSessionLock) {
                    if (this.mState != 1) {
                        IoUtils.closeQuietly(fileDescriptor);
                        throw new IllegalStateException("Not allowed to write in state: " + stateToString(this.mState));
                    }
                    trackRevocableFdLocked(revocableFileDescriptor2);
                    revocableFileDescriptor = revocableFileDescriptor2.getRevocableFileDescriptor();
                }
                return revocableFileDescriptor;
            } catch (IOException e) {
                e = e;
                IoUtils.closeQuietly(fileDescriptor);
                throw ExceptionUtils.wrap(e);
            }
        } catch (IOException e2) {
            e = e2;
            fileDescriptor = null;
        }
    }

    public final FileDescriptor openWriteInternal(long j, long j2) throws IOException {
        try {
            File sessionFile = getSessionFile();
            if (sessionFile == null) {
                throw new IllegalStateException("Couldn't get the file for this session");
            }
            FileDescriptor open = Os.open(sessionFile.getPath(), OsConstants.O_CREAT | OsConstants.O_RDWR, FrameworkStatsLog.NON_A11Y_TOOL_SERVICE_WARNING_REPORT);
            if (j > 0 && Os.lseek(open, j, OsConstants.SEEK_SET) != j) {
                throw new IllegalStateException("Failed to seek " + j + "; curOffset=" + j);
            }
            if (j2 > 0) {
                ((StorageManager) this.mContext.getSystemService(StorageManager.class)).allocateBytes(open, j2);
            }
            return open;
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public ParcelFileDescriptor openRead() {
        FileDescriptor fileDescriptor;
        ParcelFileDescriptor revocableFileDescriptor;
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to read in state: " + stateToString(this.mState));
            } else if (!BlobStoreConfig.shouldUseRevocableFdForReads()) {
                try {
                    return new ParcelFileDescriptor(openReadInternal());
                } catch (IOException e) {
                    throw ExceptionUtils.wrap(e);
                }
            } else {
                try {
                    fileDescriptor = openReadInternal();
                    try {
                        RevocableFileDescriptor revocableFileDescriptor2 = new RevocableFileDescriptor(this.mContext, fileDescriptor);
                        synchronized (this.mSessionLock) {
                            if (this.mState != 1) {
                                IoUtils.closeQuietly(fileDescriptor);
                                throw new IllegalStateException("Not allowed to read in state: " + stateToString(this.mState));
                            }
                            trackRevocableFdLocked(revocableFileDescriptor2);
                            revocableFileDescriptor = revocableFileDescriptor2.getRevocableFileDescriptor();
                        }
                        return revocableFileDescriptor;
                    } catch (IOException e2) {
                        e = e2;
                        IoUtils.closeQuietly(fileDescriptor);
                        throw ExceptionUtils.wrap(e);
                    }
                } catch (IOException e3) {
                    e = e3;
                    fileDescriptor = null;
                }
            }
        }
    }

    public final FileDescriptor openReadInternal() throws IOException {
        try {
            File sessionFile = getSessionFile();
            if (sessionFile == null) {
                throw new IllegalStateException("Couldn't get the file for this session");
            }
            return Os.open(sessionFile.getPath(), OsConstants.O_RDONLY, 0);
        } catch (ErrnoException e) {
            throw e.rethrowAsIOException();
        }
    }

    public long getSize() {
        return getSessionFile().length();
    }

    public void allowPackageAccess(String str, byte[] bArr) {
        assertCallerIsOwner();
        Objects.requireNonNull(str, "packageName must not be null");
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to change access type in state: " + stateToString(this.mState));
            } else if (this.mBlobAccessMode.getAllowedPackagesCount() >= BlobStoreConfig.getMaxPermittedPackages()) {
                throw new ParcelableException(new LimitExceededException("Too many packages permitted to access the blob: " + this.mBlobAccessMode.getAllowedPackagesCount()));
            } else {
                this.mBlobAccessMode.allowPackageAccess(str, bArr);
            }
        }
    }

    public void allowSameSignatureAccess() {
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to change access type in state: " + stateToString(this.mState));
            }
            this.mBlobAccessMode.allowSameSignatureAccess();
        }
    }

    public void allowPublicAccess() {
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to change access type in state: " + stateToString(this.mState));
            }
            this.mBlobAccessMode.allowPublicAccess();
        }
    }

    public boolean isPackageAccessAllowed(String str, byte[] bArr) {
        boolean isPackageAccessAllowed;
        assertCallerIsOwner();
        Objects.requireNonNull(str, "packageName must not be null");
        Preconditions.checkByteArrayNotEmpty(bArr, "certificate");
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to get access type in state: " + stateToString(this.mState));
            }
            isPackageAccessAllowed = this.mBlobAccessMode.isPackageAccessAllowed(str, bArr);
        }
        return isPackageAccessAllowed;
    }

    public boolean isSameSignatureAccessAllowed() {
        boolean isSameSignatureAccessAllowed;
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to get access type in state: " + stateToString(this.mState));
            }
            isSameSignatureAccessAllowed = this.mBlobAccessMode.isSameSignatureAccessAllowed();
        }
        return isSameSignatureAccessAllowed;
    }

    public boolean isPublicAccessAllowed() {
        boolean isPublicAccessAllowed;
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState != 1) {
                throw new IllegalStateException("Not allowed to get access type in state: " + stateToString(this.mState));
            }
            isPublicAccessAllowed = this.mBlobAccessMode.isPublicAccessAllowed();
        }
        return isPublicAccessAllowed;
    }

    public void close() {
        closeSession(0, false);
    }

    public void abandon() {
        closeSession(2, true);
    }

    public void commit(IBlobCommitCallback iBlobCommitCallback) {
        synchronized (this.mSessionLock) {
            this.mBlobCommitCallback = iBlobCommitCallback;
            closeSession(3, true);
        }
    }

    public final void closeSession(int i, boolean z) {
        assertCallerIsOwner();
        synchronized (this.mSessionLock) {
            if (this.mState == 1) {
                this.mState = i;
                revokeAllFds();
                if (z) {
                    this.mListener.onStateChanged(this);
                }
            } else if (i == 0) {
            } else {
                throw new IllegalStateException("Not allowed to delete or abandon a session with state: " + stateToString(this.mState));
            }
        }
    }

    public void computeDigest() {
        try {
            try {
                Trace.traceBegin(524288L, "computeBlobDigest-i" + this.mSessionId + "-l" + getSessionFile().length());
                this.mDataDigest = FileUtils.digest(getSessionFile(), this.mBlobHandle.algorithm);
            } catch (IOException | NoSuchAlgorithmException e) {
                Slog.e("BlobStore", "Error computing the digest", e);
            }
        } finally {
            Trace.traceEnd(524288L);
        }
    }

    public void verifyBlobData() {
        synchronized (this.mSessionLock) {
            byte[] bArr = this.mDataDigest;
            if (bArr != null && Arrays.equals(bArr, this.mBlobHandle.digest)) {
                this.mState = 4;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Digest of the data (");
                byte[] bArr2 = this.mDataDigest;
                sb.append(bArr2 == null ? "null" : BlobHandle.safeDigest(bArr2));
                sb.append(") didn't match the given BlobHandle.digest (");
                sb.append(BlobHandle.safeDigest(this.mBlobHandle.digest));
                sb.append(")");
                Slog.d("BlobStore", sb.toString());
                this.mState = 5;
                FrameworkStatsLog.write((int) FrameworkStatsLog.BLOB_COMMITTED, getOwnerUid(), this.mSessionId, getSize(), 3);
                sendCommitCallbackResult(1);
            }
            this.mListener.onStateChanged(this);
        }
    }

    public void destroy() {
        revokeAllFds();
        getSessionFile().delete();
    }

    public final void revokeAllFds() {
        synchronized (this.mRevocableFds) {
            for (int size = this.mRevocableFds.size() - 1; size >= 0; size--) {
                this.mRevocableFds.get(size).revoke();
            }
            this.mRevocableFds.clear();
        }
    }

    @GuardedBy({"mSessionLock"})
    public final void trackRevocableFdLocked(final RevocableFileDescriptor revocableFileDescriptor) {
        synchronized (this.mRevocableFds) {
            this.mRevocableFds.add(revocableFileDescriptor);
        }
        revocableFileDescriptor.addOnCloseListener(new ParcelFileDescriptor.OnCloseListener() { // from class: com.android.server.blob.BlobStoreSession$$ExternalSyntheticLambda0
            @Override // android.os.ParcelFileDescriptor.OnCloseListener
            public final void onClose(IOException iOException) {
                BlobStoreSession.this.lambda$trackRevocableFdLocked$0(revocableFileDescriptor, iOException);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$trackRevocableFdLocked$0(RevocableFileDescriptor revocableFileDescriptor, IOException iOException) {
        synchronized (this.mRevocableFds) {
            this.mRevocableFds.remove(revocableFileDescriptor);
        }
    }

    public File getSessionFile() {
        if (this.mSessionFile == null) {
            this.mSessionFile = BlobStoreConfig.prepareBlobFile(this.mSessionId);
        }
        return this.mSessionFile;
    }

    public static String stateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                Slog.wtf("BlobStore", "Unknown state: " + i);
                                return "<unknown>";
                            }
                            return "<verified_invalid>";
                        }
                        return "<verified_valid>";
                    }
                    return "<committed>";
                }
                return "<abandoned>";
            }
            return "<opened>";
        }
        return "<closed>";
    }

    public String toString() {
        return "BlobStoreSession {id:" + this.mSessionId + ",handle:" + this.mBlobHandle + ",uid:" + this.mOwnerUid + ",pkg:" + this.mOwnerPackageName + "}";
    }

    public final void assertCallerIsOwner() {
        if (Binder.getCallingUid() == this.mOwnerUid) {
            return;
        }
        throw new SecurityException(this.mOwnerUid + " is not the session owner");
    }

    public void dump(IndentingPrintWriter indentingPrintWriter, BlobStoreManagerService.DumpArgs dumpArgs) {
        synchronized (this.mSessionLock) {
            indentingPrintWriter.println("state: " + stateToString(this.mState));
            indentingPrintWriter.println("ownerUid: " + this.mOwnerUid);
            indentingPrintWriter.println("ownerPkg: " + this.mOwnerPackageName);
            indentingPrintWriter.println("creation time: " + BlobStoreUtils.formatTime(this.mCreationTimeMs));
            indentingPrintWriter.println("size: " + Formatter.formatFileSize(this.mContext, getSize(), 8));
            indentingPrintWriter.println("blobHandle:");
            indentingPrintWriter.increaseIndent();
            this.mBlobHandle.dump(indentingPrintWriter, dumpArgs.shouldDumpFull());
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("accessMode:");
            indentingPrintWriter.increaseIndent();
            this.mBlobAccessMode.dump(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Open fds: #" + this.mRevocableFds.size());
        }
    }

    public void writeToXml(XmlSerializer xmlSerializer) throws IOException {
        synchronized (this.mSessionLock) {
            XmlUtils.writeLongAttribute(xmlSerializer, "id", this.mSessionId);
            XmlUtils.writeStringAttribute(xmlSerializer, "p", this.mOwnerPackageName);
            XmlUtils.writeIntAttribute(xmlSerializer, "u", this.mOwnerUid);
            XmlUtils.writeLongAttribute(xmlSerializer, "crt", this.mCreationTimeMs);
            xmlSerializer.startTag(null, "bh");
            this.mBlobHandle.writeToXml(xmlSerializer);
            xmlSerializer.endTag(null, "bh");
            xmlSerializer.startTag(null, "am");
            this.mBlobAccessMode.writeToXml(xmlSerializer);
            xmlSerializer.endTag(null, "am");
        }
    }

    public static BlobStoreSession createFromXml(XmlPullParser xmlPullParser, int i, Context context, BlobStoreManagerService.SessionStateChangeListener sessionStateChangeListener) throws IOException, XmlPullParserException {
        long currentTimeMillis;
        long readLongAttribute = XmlUtils.readLongAttribute(xmlPullParser, "id");
        String readStringAttribute = XmlUtils.readStringAttribute(xmlPullParser, "p");
        int readIntAttribute = XmlUtils.readIntAttribute(xmlPullParser, "u");
        if (i >= 5) {
            currentTimeMillis = XmlUtils.readLongAttribute(xmlPullParser, "crt");
        } else {
            currentTimeMillis = System.currentTimeMillis();
        }
        long j = currentTimeMillis;
        int depth = xmlPullParser.getDepth();
        BlobHandle blobHandle = null;
        BlobAccessMode blobAccessMode = null;
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            if ("bh".equals(xmlPullParser.getName())) {
                blobHandle = BlobHandle.createFromXml(xmlPullParser);
            } else if ("am".equals(xmlPullParser.getName())) {
                blobAccessMode = BlobAccessMode.createFromXml(xmlPullParser);
            }
        }
        if (blobHandle == null) {
            Slog.wtf("BlobStore", "blobHandle should be available");
            return null;
        } else if (blobAccessMode == null) {
            Slog.wtf("BlobStore", "blobAccessMode should be available");
            return null;
        } else {
            BlobStoreSession blobStoreSession = new BlobStoreSession(context, readLongAttribute, blobHandle, readIntAttribute, readStringAttribute, j, sessionStateChangeListener);
            blobStoreSession.mBlobAccessMode.allow(blobAccessMode);
            return blobStoreSession;
        }
    }
}
