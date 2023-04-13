package android.app.cloudsearch;

import android.annotation.SystemApi;
import android.app.cloudsearch.CloudSearchManager;
import android.app.cloudsearch.SearchResponse;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes.dex */
public class CloudSearchManager {

    /* loaded from: classes.dex */
    public interface CallBack {
        void onSearchFailed(SearchRequest searchRequest, SearchResponse searchResponse);

        void onSearchSucceeded(SearchRequest searchRequest, SearchResponse searchResponse);
    }

    @SystemApi
    public void search(final SearchRequest request, Executor callbackExecutor, final CallBack callback) {
        callbackExecutor.execute(new Runnable() { // from class: android.app.cloudsearch.CloudSearchManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CloudSearchManager.CallBack.this.onSearchFailed(request, new SearchResponse.Builder(-1).build());
            }
        });
    }
}
