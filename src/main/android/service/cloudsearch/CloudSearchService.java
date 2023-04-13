package android.service.cloudsearch;

import android.annotation.SystemApi;
import android.app.Service;
import android.app.cloudsearch.SearchRequest;
import android.app.cloudsearch.SearchResponse;
import android.content.Intent;
import android.p008os.IBinder;
@SystemApi
/* loaded from: classes3.dex */
public abstract class CloudSearchService extends Service {
    public static final String SERVICE_INTERFACE = "android.service.cloudsearch.CloudSearchService";

    public abstract void onSearch(SearchRequest searchRequest);

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
    }

    public final void returnResults(String requestId, SearchResponse response) {
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return null;
    }
}
