package com.android.ims.rcs.uce.request;

import com.android.ims.rcs.uce.request.UceRequestManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public class UceRequestRepository {
    private final UceRequestDispatcher mDispatcher;
    private volatile boolean mDestroyed = false;
    private final Map<Long, UceRequestCoordinator> mRequestCoordinators = new HashMap();

    public UceRequestRepository(int subId, UceRequestManager.RequestManagerCallback callback) {
        this.mDispatcher = new UceRequestDispatcher(subId, callback);
    }

    public synchronized void onDestroy() {
        this.mDestroyed = true;
        this.mDispatcher.onDestroy();
        this.mRequestCoordinators.forEach(new BiConsumer() { // from class: com.android.ims.rcs.uce.request.UceRequestRepository$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                Long l = (Long) obj;
                ((UceRequestCoordinator) obj2).onFinish();
            }
        });
        this.mRequestCoordinators.clear();
    }

    public synchronized void addRequestCoordinator(UceRequestCoordinator coordinator) {
        if (this.mDestroyed) {
            return;
        }
        this.mRequestCoordinators.put(Long.valueOf(coordinator.getCoordinatorId()), coordinator);
        this.mDispatcher.addRequest(coordinator.getCoordinatorId(), coordinator.getActivatedRequestTaskIds());
    }

    public synchronized UceRequestCoordinator removeRequestCoordinator(Long coordinatorId) {
        return this.mRequestCoordinators.remove(coordinatorId);
    }

    public synchronized UceRequestCoordinator getRequestCoordinator(Long coordinatorId) {
        return this.mRequestCoordinators.get(coordinatorId);
    }

    public synchronized UceRequest getUceRequest(Long taskId) {
        for (UceRequestCoordinator coordinator : this.mRequestCoordinators.values()) {
            UceRequest request = coordinator.getUceRequest(taskId);
            if (request != null) {
                return request;
            }
        }
        return null;
    }

    public synchronized void notifyRequestFinished(Long taskId) {
        this.mDispatcher.onRequestFinished(taskId);
    }
}
