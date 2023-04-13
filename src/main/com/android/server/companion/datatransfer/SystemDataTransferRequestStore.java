package com.android.server.companion.datatransfer;

import android.companion.datatransfer.PermissionSyncRequest;
import android.companion.datatransfer.SystemDataTransferRequest;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.FunctionalUtils;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.companion.DataStoreUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SystemDataTransferRequestStore {
    public final ConcurrentMap<Integer, AtomicFile> mUserIdToStorageFile = new ConcurrentHashMap();
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final SparseArray<ArrayList<SystemDataTransferRequest>> mCachedPerUser = new SparseArray<>();
    public final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    public List<SystemDataTransferRequest> readRequestsByAssociationId(int i, int i2) {
        ArrayList<SystemDataTransferRequest> readRequestsFromCache;
        synchronized (this.mLock) {
            readRequestsFromCache = readRequestsFromCache(i);
        }
        ArrayList arrayList = new ArrayList();
        for (SystemDataTransferRequest systemDataTransferRequest : readRequestsFromCache) {
            if (systemDataTransferRequest.getAssociationId() == i2) {
                arrayList.add(systemDataTransferRequest);
            }
        }
        return arrayList;
    }

    public void writeRequest(final int i, SystemDataTransferRequest systemDataTransferRequest) {
        final ArrayList<SystemDataTransferRequest> readRequestsFromCache;
        Slog.i("CDM_SystemDataTransferRequestStore", "Writing request=" + systemDataTransferRequest + " to store.");
        synchronized (this.mLock) {
            readRequestsFromCache = readRequestsFromCache(i);
            readRequestsFromCache.add(systemDataTransferRequest);
            this.mCachedPerUser.set(i, readRequestsFromCache);
        }
        this.mExecutor.execute(new Runnable() { // from class: com.android.server.companion.datatransfer.SystemDataTransferRequestStore$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SystemDataTransferRequestStore.this.lambda$writeRequest$0(i, readRequestsFromCache);
            }
        });
    }

    public void removeRequestsByAssociationId(final int i, final int i2) {
        final ArrayList<SystemDataTransferRequest> readRequestsFromCache;
        Slog.i("CDM_SystemDataTransferRequestStore", "Removing system data transfer requests for userId=" + i + ", associationId=" + i2);
        synchronized (this.mLock) {
            readRequestsFromCache = readRequestsFromCache(i);
            readRequestsFromCache.removeIf(new Predicate() { // from class: com.android.server.companion.datatransfer.SystemDataTransferRequestStore$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$removeRequestsByAssociationId$1;
                    lambda$removeRequestsByAssociationId$1 = SystemDataTransferRequestStore.lambda$removeRequestsByAssociationId$1(i2, (SystemDataTransferRequest) obj);
                    return lambda$removeRequestsByAssociationId$1;
                }
            });
            this.mCachedPerUser.set(i, readRequestsFromCache);
        }
        this.mExecutor.execute(new Runnable() { // from class: com.android.server.companion.datatransfer.SystemDataTransferRequestStore$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SystemDataTransferRequestStore.this.lambda$removeRequestsByAssociationId$2(i, readRequestsFromCache);
            }
        });
    }

    public static /* synthetic */ boolean lambda$removeRequestsByAssociationId$1(int i, SystemDataTransferRequest systemDataTransferRequest) {
        return systemDataTransferRequest.getAssociationId() == i;
    }

    @GuardedBy({"mLock"})
    public final ArrayList<SystemDataTransferRequest> readRequestsFromCache(final int i) {
        ArrayList<SystemDataTransferRequest> arrayList = this.mCachedPerUser.get(i);
        if (arrayList == null) {
            try {
                arrayList = (ArrayList) this.mExecutor.submit(new Callable() { // from class: com.android.server.companion.datatransfer.SystemDataTransferRequestStore$$ExternalSyntheticLambda2
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        ArrayList lambda$readRequestsFromCache$3;
                        lambda$readRequestsFromCache$3 = SystemDataTransferRequestStore.this.lambda$readRequestsFromCache$3(i);
                        return lambda$readRequestsFromCache$3;
                    }
                }).get(5L, TimeUnit.SECONDS);
            } catch (InterruptedException unused) {
                Slog.e("CDM_SystemDataTransferRequestStore", "Thread reading SystemDataTransferRequest from disk is interrupted.");
            } catch (ExecutionException unused2) {
                Slog.e("CDM_SystemDataTransferRequestStore", "Error occurred while reading SystemDataTransferRequest from disk.");
            } catch (TimeoutException unused3) {
                Slog.e("CDM_SystemDataTransferRequestStore", "Reading SystemDataTransferRequest from disk timed out.");
            }
            this.mCachedPerUser.set(i, arrayList);
        }
        return arrayList;
    }

    /* renamed from: readRequestsFromStore */
    public final ArrayList<SystemDataTransferRequest> lambda$readRequestsFromCache$3(int i) {
        AtomicFile storageFileForUser = getStorageFileForUser(i);
        Slog.i("CDM_SystemDataTransferRequestStore", "Reading SystemDataTransferRequests for user " + i + " from file=" + storageFileForUser.getBaseFile().getPath());
        synchronized (storageFileForUser) {
            if (!storageFileForUser.getBaseFile().exists()) {
                Slog.d("CDM_SystemDataTransferRequestStore", "File does not exist -> Abort");
                return new ArrayList<>();
            }
            try {
                FileInputStream openRead = storageFileForUser.openRead();
                try {
                    TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                    XmlUtils.beginDocument(resolvePullParser, "requests");
                    ArrayList<SystemDataTransferRequest> readRequestsFromXml = readRequestsFromXml(resolvePullParser);
                    if (openRead != null) {
                        openRead.close();
                    }
                    return readRequestsFromXml;
                } catch (Throwable th) {
                    if (openRead != null) {
                        try {
                            openRead.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException | XmlPullParserException e) {
                Slog.e("CDM_SystemDataTransferRequestStore", "Error while reading requests file", e);
                return new ArrayList<>();
            }
        }
    }

    public final ArrayList<SystemDataTransferRequest> readRequestsFromXml(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        if (!DataStoreUtils.isStartOfTag(typedXmlPullParser, "requests")) {
            throw new XmlPullParserException("The XML doesn't have start tag: requests");
        }
        ArrayList<SystemDataTransferRequest> arrayList = new ArrayList<>();
        while (true) {
            typedXmlPullParser.nextTag();
            if (DataStoreUtils.isEndOfTag(typedXmlPullParser, "requests")) {
                return arrayList;
            }
            if (DataStoreUtils.isStartOfTag(typedXmlPullParser, "request")) {
                arrayList.add(readRequestFromXml(typedXmlPullParser));
            }
        }
    }

    public final SystemDataTransferRequest readRequestFromXml(TypedXmlPullParser typedXmlPullParser) throws XmlPullParserException, IOException {
        if (!DataStoreUtils.isStartOfTag(typedXmlPullParser, "request")) {
            throw new XmlPullParserException("XML doesn't have start tag: request");
        }
        int readIntAttribute = XmlUtils.readIntAttribute(typedXmlPullParser, "association_id");
        int readIntAttribute2 = XmlUtils.readIntAttribute(typedXmlPullParser, "data_type");
        int readIntAttribute3 = XmlUtils.readIntAttribute(typedXmlPullParser, "user_id");
        boolean readBooleanAttribute = XmlUtils.readBooleanAttribute(typedXmlPullParser, "is_user_consented");
        if (readIntAttribute2 != 1) {
            return null;
        }
        PermissionSyncRequest permissionSyncRequest = new PermissionSyncRequest(readIntAttribute);
        permissionSyncRequest.setUserId(readIntAttribute3);
        permissionSyncRequest.setUserConsented(readBooleanAttribute);
        return permissionSyncRequest;
    }

    /* renamed from: writeRequestsToStore */
    public void lambda$writeRequest$0(int i, final List<SystemDataTransferRequest> list) {
        AtomicFile storageFileForUser = getStorageFileForUser(i);
        Slog.i("CDM_SystemDataTransferRequestStore", "Writing SystemDataTransferRequests for user " + i + " to file=" + storageFileForUser.getBaseFile().getPath());
        synchronized (storageFileForUser) {
            DataStoreUtils.writeToFileSafely(storageFileForUser, new FunctionalUtils.ThrowingConsumer() { // from class: com.android.server.companion.datatransfer.SystemDataTransferRequestStore$$ExternalSyntheticLambda5
                public final void acceptOrThrow(Object obj) {
                    SystemDataTransferRequestStore.this.lambda$writeRequestsToStore$4(list, (FileOutputStream) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$writeRequestsToStore$4(List list, FileOutputStream fileOutputStream) throws Exception {
        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(fileOutputStream);
        resolveSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        resolveSerializer.startDocument((String) null, Boolean.TRUE);
        writeRequestsToXml(resolveSerializer, list);
        resolveSerializer.endDocument();
    }

    public final void writeRequestsToXml(TypedXmlSerializer typedXmlSerializer, Collection<SystemDataTransferRequest> collection) throws IOException {
        typedXmlSerializer.startTag((String) null, "requests");
        for (SystemDataTransferRequest systemDataTransferRequest : collection) {
            writeRequestToXml(typedXmlSerializer, systemDataTransferRequest);
        }
        typedXmlSerializer.endTag((String) null, "requests");
    }

    public final void writeRequestToXml(TypedXmlSerializer typedXmlSerializer, SystemDataTransferRequest systemDataTransferRequest) throws IOException {
        typedXmlSerializer.startTag((String) null, "request");
        XmlUtils.writeIntAttribute(typedXmlSerializer, "association_id", systemDataTransferRequest.getAssociationId());
        XmlUtils.writeIntAttribute(typedXmlSerializer, "data_type", systemDataTransferRequest.getDataType());
        XmlUtils.writeIntAttribute(typedXmlSerializer, "user_id", systemDataTransferRequest.getUserId());
        XmlUtils.writeBooleanAttribute(typedXmlSerializer, "is_user_consented", systemDataTransferRequest.isUserConsented());
        typedXmlSerializer.endTag((String) null, "request");
    }

    public final AtomicFile getStorageFileForUser(final int i) {
        return this.mUserIdToStorageFile.computeIfAbsent(Integer.valueOf(i), new Function() { // from class: com.android.server.companion.datatransfer.SystemDataTransferRequestStore$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                AtomicFile createStorageFileForUser;
                Integer num = (Integer) obj;
                createStorageFileForUser = DataStoreUtils.createStorageFileForUser(i, "companion_device_system_data_transfer_requests.xml");
                return createStorageFileForUser;
            }
        });
    }
}
