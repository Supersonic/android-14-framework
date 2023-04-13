package android.media;

import android.app.SystemServiceRegistry;
import android.content.Context;
import android.media.session.MediaSessionManager;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes2.dex */
public class MediaFrameworkPlatformInitializer {
    private static volatile MediaServiceManager sMediaServiceManager;

    private MediaFrameworkPlatformInitializer() {
    }

    public static void setMediaServiceManager(MediaServiceManager mediaServiceManager) {
        Preconditions.checkState(sMediaServiceManager == null, "setMediaServiceManager called twice!");
        sMediaServiceManager = (MediaServiceManager) Objects.requireNonNull(mediaServiceManager);
    }

    public static MediaServiceManager getMediaServiceManager() {
        return sMediaServiceManager;
    }

    public static void registerServiceWrappers() {
        SystemServiceRegistry.registerContextAwareService(Context.MEDIA_SESSION_SERVICE, MediaSessionManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.media.MediaFrameworkPlatformInitializer$$ExternalSyntheticLambda0
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return MediaFrameworkPlatformInitializer.lambda$registerServiceWrappers$0(context);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ MediaSessionManager lambda$registerServiceWrappers$0(Context context) {
        return new MediaSessionManager(context);
    }
}
