package android.content;

import android.content.res.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class ComponentCallbacksController {
    private List<ComponentCallbacks> mComponentCallbacks;
    private final Object mLock = new Object();

    public void registerCallbacks(ComponentCallbacks callbacks) {
        synchronized (this.mLock) {
            if (this.mComponentCallbacks == null) {
                this.mComponentCallbacks = new ArrayList();
            }
            this.mComponentCallbacks.add(callbacks);
        }
    }

    public void unregisterCallbacks(ComponentCallbacks callbacks) {
        synchronized (this.mLock) {
            List<ComponentCallbacks> list = this.mComponentCallbacks;
            if (list != null && !list.isEmpty()) {
                this.mComponentCallbacks.remove(callbacks);
            }
        }
    }

    public void clearCallbacks() {
        synchronized (this.mLock) {
            List<ComponentCallbacks> list = this.mComponentCallbacks;
            if (list != null) {
                list.clear();
            }
        }
    }

    public void dispatchConfigurationChanged(final Configuration newConfig) {
        forAllComponentCallbacks(new Consumer() { // from class: android.content.ComponentCallbacksController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ComponentCallbacks) obj).onConfigurationChanged(Configuration.this);
            }
        });
    }

    public void dispatchLowMemory() {
        forAllComponentCallbacks(new Consumer() { // from class: android.content.ComponentCallbacksController$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ComponentCallbacks) obj).onLowMemory();
            }
        });
    }

    public void dispatchTrimMemory(final int level) {
        forAllComponentCallbacks(new Consumer() { // from class: android.content.ComponentCallbacksController$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ComponentCallbacksController.lambda$dispatchTrimMemory$1(level, (ComponentCallbacks) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$dispatchTrimMemory$1(int level, ComponentCallbacks callbacks) {
        if (callbacks instanceof ComponentCallbacks2) {
            ((ComponentCallbacks2) callbacks).onTrimMemory(level);
        }
    }

    private void forAllComponentCallbacks(Consumer<ComponentCallbacks> callbacksConsumer) {
        synchronized (this.mLock) {
            List<ComponentCallbacks> list = this.mComponentCallbacks;
            if (list != null && !list.isEmpty()) {
                ComponentCallbacks[] callbacksArray = new ComponentCallbacks[this.mComponentCallbacks.size()];
                this.mComponentCallbacks.toArray(callbacksArray);
                for (ComponentCallbacks callbacks : callbacksArray) {
                    callbacksConsumer.accept(callbacks);
                }
            }
        }
    }
}
