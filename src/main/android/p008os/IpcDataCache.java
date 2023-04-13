package android.p008os;

import android.annotation.SystemApi;
import android.app.PropertyInvalidatedCache;
import android.util.ArraySet;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Iterator;
@SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
/* renamed from: android.os.IpcDataCache */
/* loaded from: classes3.dex */
public class IpcDataCache<Query, Result> extends PropertyInvalidatedCache<Query, Result> {
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final String MODULE_BLUETOOTH = "bluetooth";
    public static final String MODULE_SYSTEM = "system_server";
    public static final String MODULE_TEST = "test";

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.IpcDataCache$IpcDataCacheModule */
    /* loaded from: classes3.dex */
    public @interface IpcDataCacheModule {
    }

    /* renamed from: android.os.IpcDataCache$RemoteCall */
    /* loaded from: classes3.dex */
    public interface RemoteCall<Query, Result> {
        Result apply(Query query) throws RemoteException;
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    /* renamed from: android.os.IpcDataCache$QueryHandler */
    /* loaded from: classes3.dex */
    public static abstract class QueryHandler<Q, R> extends PropertyInvalidatedCache.QueryHandler<Q, R> {
        @Override // android.app.PropertyInvalidatedCache.QueryHandler
        public abstract R apply(Q q);

        @Override // android.app.PropertyInvalidatedCache.QueryHandler
        public boolean shouldBypassCache(Q query) {
            return false;
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public IpcDataCache(int maxEntries, String module, String api, String cacheName, QueryHandler<Query, Result> computer) {
        super(maxEntries, module, api, cacheName, computer);
    }

    @Override // android.app.PropertyInvalidatedCache
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void disableForCurrentProcess() {
        super.disableForCurrentProcess();
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static void disableForCurrentProcess(String cacheName) {
        PropertyInvalidatedCache.disableForCurrentProcess(cacheName);
    }

    @Override // android.app.PropertyInvalidatedCache
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public Result query(Query query) {
        return (Result) super.query(query);
    }

    @Override // android.app.PropertyInvalidatedCache
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void invalidateCache() {
        super.invalidateCache();
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static void invalidateCache(String module, String api) {
        PropertyInvalidatedCache.invalidateCache(module, api);
    }

    /* renamed from: android.os.IpcDataCache$Config */
    /* loaded from: classes3.dex */
    public static class Config {
        private final String mApi;
        private ArraySet<String> mChildren;
        private boolean mDisabled;
        private final int mMaxEntries;
        private final String mModule;
        private final String mName;

        public Config(int maxEntries, String module, String api, String name) {
            this.mDisabled = false;
            this.mMaxEntries = maxEntries;
            this.mModule = module;
            this.mApi = api;
            this.mName = name;
        }

        public Config(int maxEntries, String module, String api) {
            this(maxEntries, module, api, api);
        }

        public Config(Config root, String api, String name) {
            this(root.maxEntries(), root.module(), api, name);
        }

        public Config(Config root, String api) {
            this(root.maxEntries(), root.module(), api, api);
        }

        public Config child(String name) {
            Config result = new Config(this, api(), name);
            registerChild(name);
            return result;
        }

        public final int maxEntries() {
            return this.mMaxEntries;
        }

        public final String module() {
            return this.mModule;
        }

        public final String api() {
            return this.mApi;
        }

        public final String name() {
            return this.mName;
        }

        private final void registerChild(String name) {
            synchronized (this) {
                if (this.mChildren == null) {
                    this.mChildren = new ArraySet<>();
                }
                this.mChildren.add(name);
                if (this.mDisabled) {
                    IpcDataCache.disableForCurrentProcess(name);
                }
            }
        }

        public void invalidateCache() {
            IpcDataCache.invalidateCache(this.mModule, this.mApi);
        }

        public void disableForCurrentProcess() {
            IpcDataCache.disableForCurrentProcess(this.mName);
        }

        public void disableAllForCurrentProcess() {
            synchronized (this) {
                this.mDisabled = true;
                disableForCurrentProcess();
                ArraySet<String> arraySet = this.mChildren;
                if (arraySet != null) {
                    Iterator<String> it = arraySet.iterator();
                    while (it.hasNext()) {
                        String c = it.next();
                        IpcDataCache.disableForCurrentProcess(c);
                    }
                }
            }
        }
    }

    public IpcDataCache(Config config, QueryHandler<Query, Result> computer) {
        super(config.maxEntries(), config.module(), config.api(), config.name(), computer);
    }

    /* renamed from: android.os.IpcDataCache$SystemServerCallHandler */
    /* loaded from: classes3.dex */
    private static class SystemServerCallHandler<Query, Result> extends QueryHandler<Query, Result> {
        private final RemoteCall<Query, Result> mHandler;

        public SystemServerCallHandler(RemoteCall handler) {
            this.mHandler = handler;
        }

        @Override // android.p008os.IpcDataCache.QueryHandler, android.app.PropertyInvalidatedCache.QueryHandler
        public Result apply(Query query) {
            try {
                return this.mHandler.apply(query);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public IpcDataCache(Config config, RemoteCall<Query, Result> computer) {
        this(config, new SystemServerCallHandler(computer));
    }
}
