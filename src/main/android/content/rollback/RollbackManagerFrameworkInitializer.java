package android.content.rollback;

import android.app.SystemServiceRegistry;
import android.content.Context;
import android.content.rollback.IRollbackManager;
import android.p008os.IBinder;
/* loaded from: classes.dex */
public class RollbackManagerFrameworkInitializer {
    private RollbackManagerFrameworkInitializer() {
    }

    public static void initialize() {
        SystemServiceRegistry.registerContextAwareService(Context.ROLLBACK_SERVICE, RollbackManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithBinder() { // from class: android.content.rollback.RollbackManagerFrameworkInitializer$$ExternalSyntheticLambda0
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithBinder
            public final Object createService(Context context, IBinder iBinder) {
                return RollbackManagerFrameworkInitializer.lambda$initialize$0(context, iBinder);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ RollbackManager lambda$initialize$0(Context context, IBinder b) {
        return new RollbackManager(context, IRollbackManager.Stub.asInterface(b));
    }
}
