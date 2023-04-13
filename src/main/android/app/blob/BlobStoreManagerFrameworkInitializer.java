package android.app.blob;

import android.app.SystemServiceRegistry;
import android.app.blob.IBlobStoreManager;
import android.content.Context;
import android.p008os.IBinder;
/* loaded from: classes.dex */
public class BlobStoreManagerFrameworkInitializer {
    public static void initialize() {
        SystemServiceRegistry.registerContextAwareService(Context.BLOB_STORE_SERVICE, BlobStoreManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithBinder() { // from class: android.app.blob.BlobStoreManagerFrameworkInitializer$$ExternalSyntheticLambda0
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithBinder
            public final Object createService(Context context, IBinder iBinder) {
                return BlobStoreManagerFrameworkInitializer.lambda$initialize$0(context, iBinder);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ BlobStoreManager lambda$initialize$0(Context context, IBinder service) {
        return new BlobStoreManager(context, IBlobStoreManager.Stub.asInterface(service));
    }
}
