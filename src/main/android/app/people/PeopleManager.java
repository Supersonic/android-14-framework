package android.app.people;

import android.annotation.SystemApi;
import android.app.people.IConversationListener;
import android.app.people.IPeopleManager;
import android.app.people.PeopleManager;
import android.content.Context;
import android.content.p001pm.ParceledListSlice;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public final class PeopleManager {
    private static final String LOG_TAG = PeopleManager.class.getSimpleName();
    private Context mContext;
    public Map<ConversationListener, Pair<Executor, IConversationListener>> mConversationListeners;
    private IPeopleManager mService;

    public PeopleManager(Context context) throws ServiceManager.ServiceNotFoundException {
        this.mConversationListeners = new HashMap();
        this.mContext = context;
        this.mService = IPeopleManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.PEOPLE_SERVICE));
    }

    public PeopleManager(Context context, IPeopleManager service) {
        this.mConversationListeners = new HashMap();
        this.mContext = context;
        this.mService = service;
    }

    @SystemApi
    public boolean isConversation(String packageName, String shortcutId) {
        Preconditions.checkStringNotEmpty(packageName);
        Preconditions.checkStringNotEmpty(shortcutId);
        try {
            return this.mService.isConversation(packageName, this.mContext.getUserId(), shortcutId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addOrUpdateStatus(String conversationId, ConversationStatus status) {
        Preconditions.checkStringNotEmpty(conversationId);
        Objects.requireNonNull(status);
        try {
            this.mService.addOrUpdateStatus(this.mContext.getPackageName(), this.mContext.getUserId(), conversationId, status);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearStatus(String conversationId, String statusId) {
        Preconditions.checkStringNotEmpty(conversationId);
        Preconditions.checkStringNotEmpty(statusId);
        try {
            this.mService.clearStatus(this.mContext.getPackageName(), this.mContext.getUserId(), conversationId, statusId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearStatuses(String conversationId) {
        Preconditions.checkStringNotEmpty(conversationId);
        try {
            this.mService.clearStatuses(this.mContext.getPackageName(), this.mContext.getUserId(), conversationId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ConversationStatus> getStatuses(String conversationId) {
        try {
            ParceledListSlice<ConversationStatus> parceledList = this.mService.getStatuses(this.mContext.getPackageName(), this.mContext.getUserId(), conversationId);
            if (parceledList != null) {
                return parceledList.getList();
            }
            return new ArrayList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* loaded from: classes.dex */
    public interface ConversationListener {
        default void onConversationUpdate(ConversationChannel conversation) {
        }
    }

    public void registerConversationListener(String packageName, int userId, String shortcutId, ConversationListener listener, Executor executor) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        Objects.requireNonNull(packageName, "Package name cannot be null");
        Objects.requireNonNull(shortcutId, "Shortcut ID cannot be null");
        synchronized (this.mConversationListeners) {
            IConversationListener proxy = new ConversationListenerProxy(executor, listener);
            try {
                this.mService.registerConversationListener(packageName, userId, shortcutId, proxy);
                this.mConversationListeners.put(listener, new Pair<>(executor, proxy));
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void unregisterConversationListener(ConversationListener listener) {
        Objects.requireNonNull(listener, "Listener cannot be null");
        synchronized (this.mConversationListeners) {
            if (this.mConversationListeners.containsKey(listener)) {
                IConversationListener proxy = this.mConversationListeners.remove(listener).second;
                try {
                    this.mService.unregisterConversationListener(proxy);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ConversationListenerProxy extends IConversationListener.Stub {
        private final Executor mExecutor;
        private final ConversationListener mListener;

        ConversationListenerProxy(Executor executor, ConversationListener listener) {
            this.mExecutor = executor;
            this.mListener = listener;
        }

        @Override // android.app.people.IConversationListener
        public void onConversationUpdate(final ConversationChannel conversation) {
            Executor executor;
            if (this.mListener == null || (executor = this.mExecutor) == null) {
                Slog.m96e(PeopleManager.LOG_TAG, "Binder is dead");
            } else {
                executor.execute(new Runnable() { // from class: android.app.people.PeopleManager$ConversationListenerProxy$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PeopleManager.ConversationListenerProxy.this.lambda$onConversationUpdate$0(conversation);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onConversationUpdate$0(ConversationChannel conversation) {
            this.mListener.onConversationUpdate(conversation);
        }
    }
}
