package android.media.browse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.p001pm.BaseParceledListSlice;
import android.content.p001pm.ParceledListSlice;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaDescription;
import android.media.session.MediaSession;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.service.media.IMediaBrowserService;
import android.service.media.IMediaBrowserServiceCallbacks;
import android.service.media.MediaBrowserService;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public final class MediaBrowser {
    private static final int CONNECT_STATE_CONNECTED = 3;
    private static final int CONNECT_STATE_CONNECTING = 2;
    private static final int CONNECT_STATE_DISCONNECTED = 1;
    private static final int CONNECT_STATE_DISCONNECTING = 0;
    private static final int CONNECT_STATE_SUSPENDED = 4;
    private static final boolean DBG = false;
    public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
    public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
    private static final String TAG = "MediaBrowser";
    private final ConnectionCallback mCallback;
    private final Context mContext;
    private volatile Bundle mExtras;
    private volatile MediaSession.Token mMediaSessionToken;
    private final Bundle mRootHints;
    private volatile String mRootId;
    private IMediaBrowserService mServiceBinder;
    private IMediaBrowserServiceCallbacks mServiceCallbacks;
    private final ComponentName mServiceComponent;
    private MediaServiceConnection mServiceConnection;
    private final Handler mHandler = new Handler();
    private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap<>();
    private volatile int mState = 1;

    public MediaBrowser(Context context, ComponentName serviceComponent, ConnectionCallback callback, Bundle rootHints) {
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        if (serviceComponent == null) {
            throw new IllegalArgumentException("service component must not be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("connection callback must not be null");
        }
        this.mContext = context;
        this.mServiceComponent = serviceComponent;
        this.mCallback = callback;
        this.mRootHints = rootHints == null ? null : new Bundle(rootHints);
    }

    public void connect() {
        if (this.mState != 0 && this.mState != 1) {
            throw new IllegalStateException("connect() called while neither disconnecting nor disconnected (state=" + getStateLabel(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mState = 2;
        this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.1
            @Override // java.lang.Runnable
            public void run() {
                if (MediaBrowser.this.mState == 0) {
                    return;
                }
                MediaBrowser.this.mState = 2;
                if (MediaBrowser.this.mServiceBinder != null) {
                    throw new RuntimeException("mServiceBinder should be null. Instead it is " + MediaBrowser.this.mServiceBinder);
                }
                if (MediaBrowser.this.mServiceCallbacks != null) {
                    throw new RuntimeException("mServiceCallbacks should be null. Instead it is " + MediaBrowser.this.mServiceCallbacks);
                }
                Intent intent = new Intent(MediaBrowserService.SERVICE_INTERFACE);
                intent.setComponent(MediaBrowser.this.mServiceComponent);
                MediaBrowser mediaBrowser = MediaBrowser.this;
                mediaBrowser.mServiceConnection = new MediaServiceConnection();
                boolean bound = false;
                try {
                    bound = MediaBrowser.this.mContext.bindService(intent, MediaBrowser.this.mServiceConnection, 4097);
                } catch (Exception e) {
                    Log.m110e(MediaBrowser.TAG, "Failed binding to service " + MediaBrowser.this.mServiceComponent);
                }
                if (!bound) {
                    MediaBrowser.this.forceCloseConnection();
                    MediaBrowser.this.mCallback.onConnectionFailed();
                }
            }
        });
    }

    public void disconnect() {
        this.mState = 0;
        this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.2
            @Override // java.lang.Runnable
            public void run() {
                if (MediaBrowser.this.mServiceCallbacks != null) {
                    try {
                        MediaBrowser.this.mServiceBinder.disconnect(MediaBrowser.this.mServiceCallbacks);
                    } catch (RemoteException e) {
                        Log.m104w(MediaBrowser.TAG, "RemoteException during connect for " + MediaBrowser.this.mServiceComponent);
                    }
                }
                int state = MediaBrowser.this.mState;
                MediaBrowser.this.forceCloseConnection();
                if (state != 0) {
                    MediaBrowser.this.mState = state;
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceCloseConnection() {
        MediaServiceConnection mediaServiceConnection = this.mServiceConnection;
        if (mediaServiceConnection != null) {
            try {
                this.mContext.unbindService(mediaServiceConnection);
            } catch (IllegalArgumentException e) {
            }
        }
        this.mState = 1;
        this.mServiceConnection = null;
        this.mServiceBinder = null;
        this.mServiceCallbacks = null;
        this.mRootId = null;
        this.mMediaSessionToken = null;
    }

    public boolean isConnected() {
        return this.mState == 3;
    }

    public ComponentName getServiceComponent() {
        if (!isConnected()) {
            throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + NavigationBarInflaterView.KEY_CODE_END);
        }
        return this.mServiceComponent;
    }

    public String getRoot() {
        if (!isConnected()) {
            throw new IllegalStateException("getRoot() called while not connected (state=" + getStateLabel(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
        }
        return this.mRootId;
    }

    public Bundle getExtras() {
        if (!isConnected()) {
            throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + NavigationBarInflaterView.KEY_CODE_END);
        }
        return this.mExtras;
    }

    public MediaSession.Token getSessionToken() {
        if (!isConnected()) {
            throw new IllegalStateException("getSessionToken() called while not connected (state=" + this.mState + NavigationBarInflaterView.KEY_CODE_END);
        }
        return this.mMediaSessionToken;
    }

    public void subscribe(String parentId, SubscriptionCallback callback) {
        subscribeInternal(parentId, null, callback);
    }

    public void subscribe(String parentId, Bundle options, SubscriptionCallback callback) {
        if (options == null) {
            throw new IllegalArgumentException("options cannot be null");
        }
        subscribeInternal(parentId, new Bundle(options), callback);
    }

    public void unsubscribe(String parentId) {
        unsubscribeInternal(parentId, null);
    }

    public void unsubscribe(String parentId, SubscriptionCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        unsubscribeInternal(parentId, callback);
    }

    public void getItem(final String mediaId, final ItemCallback cb) {
        if (TextUtils.isEmpty(mediaId)) {
            throw new IllegalArgumentException("mediaId cannot be empty.");
        }
        if (cb == null) {
            throw new IllegalArgumentException("cb cannot be null.");
        }
        if (this.mState != 3) {
            Log.m108i(TAG, "Not connected, unable to retrieve the MediaItem.");
            this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.3
                @Override // java.lang.Runnable
                public void run() {
                    cb.onError(mediaId);
                }
            });
            return;
        }
        ResultReceiver receiver = new ResultReceiver(this.mHandler) { // from class: android.media.browse.MediaBrowser.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.p008os.ResultReceiver
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (!MediaBrowser.this.isConnected()) {
                    return;
                }
                if (resultCode != 0 || resultData == null || !resultData.containsKey(MediaBrowserService.KEY_MEDIA_ITEM)) {
                    cb.onError(mediaId);
                    return;
                }
                Parcelable item = resultData.getParcelable(MediaBrowserService.KEY_MEDIA_ITEM);
                if (item != null && !(item instanceof MediaItem)) {
                    cb.onError(mediaId);
                } else {
                    cb.onItemLoaded((MediaItem) item);
                }
            }
        };
        try {
            this.mServiceBinder.getMediaItem(mediaId, receiver, this.mServiceCallbacks);
        } catch (RemoteException e) {
            Log.m108i(TAG, "Remote error getting media item.");
            this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.5
                @Override // java.lang.Runnable
                public void run() {
                    cb.onError(mediaId);
                }
            });
        }
    }

    private void subscribeInternal(String parentId, Bundle options, SubscriptionCallback callback) {
        if (TextUtils.isEmpty(parentId)) {
            throw new IllegalArgumentException("parentId cannot be empty.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("callback cannot be null");
        }
        Subscription sub = this.mSubscriptions.get(parentId);
        if (sub == null) {
            sub = new Subscription();
            this.mSubscriptions.put(parentId, sub);
        }
        sub.putCallback(this.mContext, options, callback);
        if (isConnected()) {
            if (options == null) {
                try {
                    this.mServiceBinder.addSubscriptionDeprecated(parentId, this.mServiceCallbacks);
                } catch (RemoteException e) {
                    Log.m112d(TAG, "addSubscription failed with RemoteException parentId=" + parentId);
                    return;
                }
            }
            this.mServiceBinder.addSubscription(parentId, callback.mToken, options, this.mServiceCallbacks);
        }
    }

    private void unsubscribeInternal(String parentId, SubscriptionCallback callback) {
        if (TextUtils.isEmpty(parentId)) {
            throw new IllegalArgumentException("parentId cannot be empty.");
        }
        Subscription sub = this.mSubscriptions.get(parentId);
        if (sub == null) {
            return;
        }
        try {
            if (callback == null) {
                if (isConnected()) {
                    this.mServiceBinder.removeSubscriptionDeprecated(parentId, this.mServiceCallbacks);
                    this.mServiceBinder.removeSubscription(parentId, null, this.mServiceCallbacks);
                }
            } else {
                List<SubscriptionCallback> callbacks = sub.getCallbacks();
                List<Bundle> optionsList = sub.getOptionsList();
                for (int i = callbacks.size() - 1; i >= 0; i--) {
                    if (callbacks.get(i) == callback) {
                        if (isConnected()) {
                            this.mServiceBinder.removeSubscription(parentId, callback.mToken, this.mServiceCallbacks);
                        }
                        callbacks.remove(i);
                        optionsList.remove(i);
                    }
                }
            }
        } catch (RemoteException e) {
            Log.m112d(TAG, "removeSubscription failed with RemoteException parentId=" + parentId);
        }
        if (sub.isEmpty() || callback == null) {
            this.mSubscriptions.remove(parentId);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStateLabel(int state) {
        switch (state) {
            case 0:
                return "CONNECT_STATE_DISCONNECTING";
            case 1:
                return "CONNECT_STATE_DISCONNECTED";
            case 2:
                return "CONNECT_STATE_CONNECTING";
            case 3:
                return "CONNECT_STATE_CONNECTED";
            case 4:
                return "CONNECT_STATE_SUSPENDED";
            default:
                return "UNKNOWN/" + state;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onServiceConnected(final IMediaBrowserServiceCallbacks callback, final String root, final MediaSession.Token session, final Bundle extra) {
        this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.6
            @Override // java.lang.Runnable
            public void run() {
                if (!MediaBrowser.this.isCurrent(callback, "onConnect")) {
                    return;
                }
                if (MediaBrowser.this.mState != 2) {
                    Log.m104w(MediaBrowser.TAG, "onConnect from service while mState=" + MediaBrowser.getStateLabel(MediaBrowser.this.mState) + "... ignoring");
                    return;
                }
                MediaBrowser.this.mRootId = root;
                MediaBrowser.this.mMediaSessionToken = session;
                MediaBrowser.this.mExtras = extra;
                MediaBrowser.this.mState = 3;
                MediaBrowser.this.mCallback.onConnected();
                for (Map.Entry<String, Subscription> subscriptionEntry : MediaBrowser.this.mSubscriptions.entrySet()) {
                    String id = subscriptionEntry.getKey();
                    Subscription sub = subscriptionEntry.getValue();
                    List<SubscriptionCallback> callbackList = sub.getCallbacks();
                    List<Bundle> optionsList = sub.getOptionsList();
                    for (int i = 0; i < callbackList.size(); i++) {
                        try {
                            MediaBrowser.this.mServiceBinder.addSubscription(id, callbackList.get(i).mToken, optionsList.get(i), MediaBrowser.this.mServiceCallbacks);
                        } catch (RemoteException e) {
                            Log.m112d(MediaBrowser.TAG, "addSubscription failed with RemoteException parentId=" + id);
                        }
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConnectionFailed(final IMediaBrowserServiceCallbacks callback) {
        this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.7
            @Override // java.lang.Runnable
            public void run() {
                Log.m110e(MediaBrowser.TAG, "onConnectFailed for " + MediaBrowser.this.mServiceComponent);
                if (!MediaBrowser.this.isCurrent(callback, "onConnectFailed")) {
                    return;
                }
                if (MediaBrowser.this.mState != 2) {
                    Log.m104w(MediaBrowser.TAG, "onConnect from service while mState=" + MediaBrowser.getStateLabel(MediaBrowser.this.mState) + "... ignoring");
                    return;
                }
                MediaBrowser.this.forceCloseConnection();
                MediaBrowser.this.mCallback.onConnectionFailed();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onLoadChildren(final IMediaBrowserServiceCallbacks callback, final String parentId, final ParceledListSlice list, final Bundle options) {
        this.mHandler.post(new Runnable() { // from class: android.media.browse.MediaBrowser.8
            @Override // java.lang.Runnable
            public void run() {
                Subscription subscription;
                SubscriptionCallback subscriptionCallback;
                if (MediaBrowser.this.isCurrent(callback, "onLoadChildren") && (subscription = (Subscription) MediaBrowser.this.mSubscriptions.get(parentId)) != null && (subscriptionCallback = subscription.getCallback(MediaBrowser.this.mContext, options)) != null) {
                    BaseParceledListSlice baseParceledListSlice = list;
                    List<MediaItem> data = baseParceledListSlice == null ? null : baseParceledListSlice.getList();
                    Bundle bundle = options;
                    if (bundle == null) {
                        if (data == null) {
                            subscriptionCallback.onError(parentId);
                        } else {
                            subscriptionCallback.onChildrenLoaded(parentId, data);
                        }
                    } else if (data == null) {
                        subscriptionCallback.onError(parentId, bundle);
                    } else {
                        subscriptionCallback.onChildrenLoaded(parentId, data, bundle);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isCurrent(IMediaBrowserServiceCallbacks callback, String funcName) {
        if (this.mServiceCallbacks != callback || this.mState == 0 || this.mState == 1) {
            if (this.mState != 0 && this.mState != 1) {
                Log.m108i(TAG, funcName + " for " + this.mServiceComponent + " with mServiceConnection=" + this.mServiceCallbacks + " this=" + this);
                return false;
            }
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ServiceCallbacks getNewServiceCallbacks() {
        return new ServiceCallbacks(this);
    }

    void dump() {
        Log.m112d(TAG, "MediaBrowser...");
        Log.m112d(TAG, "  mServiceComponent=" + this.mServiceComponent);
        Log.m112d(TAG, "  mCallback=" + this.mCallback);
        Log.m112d(TAG, "  mRootHints=" + this.mRootHints);
        Log.m112d(TAG, "  mState=" + getStateLabel(this.mState));
        Log.m112d(TAG, "  mServiceConnection=" + this.mServiceConnection);
        Log.m112d(TAG, "  mServiceBinder=" + this.mServiceBinder);
        Log.m112d(TAG, "  mServiceCallbacks=" + this.mServiceCallbacks);
        Log.m112d(TAG, "  mRootId=" + this.mRootId);
        Log.m112d(TAG, "  mMediaSessionToken=" + this.mMediaSessionToken);
    }

    /* loaded from: classes2.dex */
    public static class MediaItem implements Parcelable {
        public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator<MediaItem>() { // from class: android.media.browse.MediaBrowser.MediaItem.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public MediaItem createFromParcel(Parcel in) {
                return new MediaItem(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public MediaItem[] newArray(int size) {
                return new MediaItem[size];
            }
        };
        public static final int FLAG_BROWSABLE = 1;
        public static final int FLAG_PLAYABLE = 2;
        private final MediaDescription mDescription;
        private final int mFlags;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes2.dex */
        public @interface Flags {
        }

        public MediaItem(MediaDescription description, int flags) {
            if (description == null) {
                throw new IllegalArgumentException("description cannot be null");
            }
            if (TextUtils.isEmpty(description.getMediaId())) {
                throw new IllegalArgumentException("description must have a non-empty media id");
            }
            this.mFlags = flags;
            this.mDescription = description;
        }

        private MediaItem(Parcel in) {
            this.mFlags = in.readInt();
            this.mDescription = MediaDescription.CREATOR.createFromParcel(in);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.mFlags);
            this.mDescription.writeToParcel(out, flags);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("MediaItem{");
            sb.append("mFlags=").append(this.mFlags);
            sb.append(", mDescription=").append(this.mDescription);
            sb.append('}');
            return sb.toString();
        }

        public int getFlags() {
            return this.mFlags;
        }

        public boolean isBrowsable() {
            return (this.mFlags & 1) != 0;
        }

        public boolean isPlayable() {
            return (this.mFlags & 2) != 0;
        }

        public MediaDescription getDescription() {
            return this.mDescription;
        }

        public String getMediaId() {
            return this.mDescription.getMediaId();
        }
    }

    /* loaded from: classes2.dex */
    public static class ConnectionCallback {
        public void onConnected() {
        }

        public void onConnectionSuspended() {
        }

        public void onConnectionFailed() {
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class SubscriptionCallback {
        Binder mToken = new Binder();

        public void onChildrenLoaded(String parentId, List<MediaItem> children) {
        }

        public void onChildrenLoaded(String parentId, List<MediaItem> children, Bundle options) {
        }

        public void onError(String parentId) {
        }

        public void onError(String parentId, Bundle options) {
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class ItemCallback {
        public void onItemLoaded(MediaItem item) {
        }

        public void onError(String mediaId) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class MediaServiceConnection implements ServiceConnection {
        private MediaServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(final ComponentName name, final IBinder binder) {
            postOrRun(new Runnable() { // from class: android.media.browse.MediaBrowser.MediaServiceConnection.1
                @Override // java.lang.Runnable
                public void run() {
                    if (!MediaServiceConnection.this.isCurrent("onServiceConnected")) {
                        return;
                    }
                    MediaBrowser.this.mServiceBinder = IMediaBrowserService.Stub.asInterface(binder);
                    MediaBrowser.this.mServiceCallbacks = MediaBrowser.this.getNewServiceCallbacks();
                    MediaBrowser.this.mState = 2;
                    try {
                        MediaBrowser.this.mServiceBinder.connect(MediaBrowser.this.mContext.getPackageName(), MediaBrowser.this.mRootHints, MediaBrowser.this.mServiceCallbacks);
                    } catch (RemoteException e) {
                        Log.m104w(MediaBrowser.TAG, "RemoteException during connect for " + MediaBrowser.this.mServiceComponent);
                    }
                }
            });
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(final ComponentName name) {
            postOrRun(new Runnable() { // from class: android.media.browse.MediaBrowser.MediaServiceConnection.2
                @Override // java.lang.Runnable
                public void run() {
                    if (!MediaServiceConnection.this.isCurrent("onServiceDisconnected")) {
                        return;
                    }
                    MediaBrowser.this.mServiceBinder = null;
                    MediaBrowser.this.mServiceCallbacks = null;
                    MediaBrowser.this.mState = 4;
                    MediaBrowser.this.mCallback.onConnectionSuspended();
                }
            });
        }

        private void postOrRun(Runnable r) {
            if (Thread.currentThread() == MediaBrowser.this.mHandler.getLooper().getThread()) {
                r.run();
            } else {
                MediaBrowser.this.mHandler.post(r);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isCurrent(String funcName) {
            if (MediaBrowser.this.mServiceConnection != this || MediaBrowser.this.mState == 0 || MediaBrowser.this.mState == 1) {
                if (MediaBrowser.this.mState != 0 && MediaBrowser.this.mState != 1) {
                    Log.m108i(MediaBrowser.TAG, funcName + " for " + MediaBrowser.this.mServiceComponent + " with mServiceConnection=" + MediaBrowser.this.mServiceConnection + " this=" + this);
                    return false;
                }
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ServiceCallbacks extends IMediaBrowserServiceCallbacks.Stub {
        private WeakReference<MediaBrowser> mMediaBrowser;

        ServiceCallbacks(MediaBrowser mediaBrowser) {
            this.mMediaBrowser = new WeakReference<>(mediaBrowser);
        }

        @Override // android.service.media.IMediaBrowserServiceCallbacks
        public void onConnect(String root, MediaSession.Token session, Bundle extras) {
            MediaBrowser mediaBrowser = this.mMediaBrowser.get();
            if (mediaBrowser != null) {
                mediaBrowser.onServiceConnected(this, root, session, extras);
            }
        }

        @Override // android.service.media.IMediaBrowserServiceCallbacks
        public void onConnectFailed() {
            MediaBrowser mediaBrowser = this.mMediaBrowser.get();
            if (mediaBrowser != null) {
                mediaBrowser.onConnectionFailed(this);
            }
        }

        @Override // android.service.media.IMediaBrowserServiceCallbacks
        public void onLoadChildren(String parentId, ParceledListSlice list, Bundle options) {
            MediaBrowser mediaBrowser = this.mMediaBrowser.get();
            if (mediaBrowser != null) {
                mediaBrowser.onLoadChildren(this, parentId, list, options);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Subscription {
        private final List<SubscriptionCallback> mCallbacks = new ArrayList();
        private final List<Bundle> mOptionsList = new ArrayList();

        Subscription() {
        }

        public boolean isEmpty() {
            return this.mCallbacks.isEmpty();
        }

        public List<Bundle> getOptionsList() {
            return this.mOptionsList;
        }

        public List<SubscriptionCallback> getCallbacks() {
            return this.mCallbacks;
        }

        public SubscriptionCallback getCallback(Context context, Bundle options) {
            if (options != null) {
                options.setClassLoader(context.getClassLoader());
            }
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserUtils.areSameOptions(this.mOptionsList.get(i), options)) {
                    return this.mCallbacks.get(i);
                }
            }
            return null;
        }

        public void putCallback(Context context, Bundle options, SubscriptionCallback callback) {
            if (options != null) {
                options.setClassLoader(context.getClassLoader());
            }
            for (int i = 0; i < this.mOptionsList.size(); i++) {
                if (MediaBrowserUtils.areSameOptions(this.mOptionsList.get(i), options)) {
                    this.mCallbacks.set(i, callback);
                    return;
                }
            }
            this.mCallbacks.add(callback);
            this.mOptionsList.add(options);
        }
    }
}
