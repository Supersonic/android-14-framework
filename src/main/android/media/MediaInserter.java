package android.media;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;
import android.p008os.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes2.dex */
public class MediaInserter {
    private final int mBufferSizePerUri;
    private final ContentProviderClient mProvider;
    private final HashMap<Uri, List<ContentValues>> mRowMap = new HashMap<>();
    private final HashMap<Uri, List<ContentValues>> mPriorityRowMap = new HashMap<>();

    public MediaInserter(ContentProviderClient provider, int bufferSizePerUri) {
        this.mProvider = provider;
        this.mBufferSizePerUri = bufferSizePerUri;
    }

    public void insert(Uri tableUri, ContentValues values) throws RemoteException {
        insert(tableUri, values, false);
    }

    public void insertwithPriority(Uri tableUri, ContentValues values) throws RemoteException {
        insert(tableUri, values, true);
    }

    private void insert(Uri tableUri, ContentValues values, boolean priority) throws RemoteException {
        HashMap<Uri, List<ContentValues>> rowmap = priority ? this.mPriorityRowMap : this.mRowMap;
        List<ContentValues> list = rowmap.get(tableUri);
        if (list == null) {
            list = new ArrayList();
            rowmap.put(tableUri, list);
        }
        list.add(new ContentValues(values));
        if (list.size() >= this.mBufferSizePerUri) {
            flushAllPriority();
            flush(tableUri, list);
        }
    }

    public void flushAll() throws RemoteException {
        flushAllPriority();
        for (Uri tableUri : this.mRowMap.keySet()) {
            List<ContentValues> list = this.mRowMap.get(tableUri);
            flush(tableUri, list);
        }
        this.mRowMap.clear();
    }

    private void flushAllPriority() throws RemoteException {
        for (Uri tableUri : this.mPriorityRowMap.keySet()) {
            List<ContentValues> list = this.mPriorityRowMap.get(tableUri);
            flush(tableUri, list);
        }
        this.mPriorityRowMap.clear();
    }

    private void flush(Uri tableUri, List<ContentValues> list) throws RemoteException {
        if (!list.isEmpty()) {
            ContentValues[] valuesArray = new ContentValues[list.size()];
            this.mProvider.bulkInsert(tableUri, (ContentValues[]) list.toArray(valuesArray));
            list.clear();
        }
    }
}
