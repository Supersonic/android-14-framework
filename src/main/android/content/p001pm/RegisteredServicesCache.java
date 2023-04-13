package android.content.p001pm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.p008os.Environment;
import android.p008os.Handler;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.p028os.BackgroundThread;
import com.android.internal.util.ArrayUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: android.content.pm.RegisteredServicesCache */
/* loaded from: classes.dex */
public abstract class RegisteredServicesCache<V> {
    private static final boolean DEBUG = false;
    protected static final String REGISTERED_SERVICES_DIR = "registered_services";
    private static final String TAG = "PackageManager";
    private final String mAttributesName;
    public final Context mContext;
    private final BroadcastReceiver mExternalReceiver;
    private Handler mHandler;
    private final String mInterfaceName;
    private RegisteredServicesCacheListener<V> mListener;
    private final String mMetaDataName;
    private final BroadcastReceiver mPackageReceiver;
    private final XmlSerializerAndParser<V> mSerializerAndParser;
    private final BroadcastReceiver mUserRemovedReceiver;
    protected final Object mServicesLock = new Object();
    private final SparseArray<UserServices<V>> mUserServices = new SparseArray<>(2);

    public abstract V parseServiceAttributes(Resources resources, String str, AttributeSet attributeSet);

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.content.pm.RegisteredServicesCache$UserServices */
    /* loaded from: classes.dex */
    public static class UserServices<V> {
        boolean mBindInstantServiceAllowed;
        boolean mPersistentServicesFileDidNotExist;
        final Map<V, Integer> persistentServices;
        Map<V, ServiceInfo<V>> services;

        private UserServices() {
            this.persistentServices = Maps.newHashMap();
            this.services = null;
            this.mPersistentServicesFileDidNotExist = true;
            this.mBindInstantServiceAllowed = false;
        }
    }

    private UserServices<V> findOrCreateUserLocked(int userId) {
        return findOrCreateUserLocked(userId, true);
    }

    private UserServices<V> findOrCreateUserLocked(int userId, boolean loadFromFileIfNew) {
        UserInfo user;
        UserServices<V> services = this.mUserServices.get(userId);
        if (services == null) {
            services = new UserServices<>();
            this.mUserServices.put(userId, services);
            if (loadFromFileIfNew && this.mSerializerAndParser != null && (user = getUser(userId)) != null) {
                AtomicFile file = createFileForUser(user.f48id);
                if (file.getBaseFile().exists()) {
                    InputStream is = null;
                    try {
                        try {
                            is = file.openRead();
                            readPersistentServicesLocked(is);
                        } catch (Exception e) {
                            Log.m103w(TAG, "Error reading persistent services for user " + user.f48id, e);
                        }
                    } finally {
                        IoUtils.closeQuietly(is);
                    }
                }
            }
        }
        return services;
    }

    public RegisteredServicesCache(Context context, String interfaceName, String metaDataName, String attributeName, XmlSerializerAndParser<V> serializerAndParser) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (uid != -1) {
                    RegisteredServicesCache.this.handlePackageEvent(intent, UserHandle.getUserId(uid));
                }
            }
        };
        this.mPackageReceiver = broadcastReceiver;
        BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                RegisteredServicesCache.this.handlePackageEvent(intent, 0);
            }
        };
        this.mExternalReceiver = broadcastReceiver2;
        BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() { // from class: android.content.pm.RegisteredServicesCache.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int userId = intent.getIntExtra(Intent.EXTRA_USER_HANDLE, -1);
                RegisteredServicesCache.this.onUserRemoved(userId);
            }
        };
        this.mUserRemovedReceiver = broadcastReceiver3;
        this.mContext = context;
        this.mInterfaceName = interfaceName;
        this.mMetaDataName = metaDataName;
        this.mAttributesName = attributeName;
        this.mSerializerAndParser = serializerAndParser;
        migrateIfNecessaryLocked();
        boolean isCore = UserHandle.isCore(Process.myUid());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        if (isCore) {
            intentFilter.setPriority(1000);
        }
        Handler handler = BackgroundThread.getHandler();
        context.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, handler);
        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        if (isCore) {
            sdFilter.setPriority(1000);
        }
        context.registerReceiver(broadcastReceiver2, sdFilter, null, handler);
        IntentFilter userFilter = new IntentFilter();
        userFilter.addAction(Intent.ACTION_USER_REMOVED);
        if (isCore) {
            userFilter.setPriority(1000);
        }
        context.registerReceiver(broadcastReceiver3, userFilter, null, handler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePackageEvent(Intent intent, int userId) {
        String action = intent.getAction();
        boolean isRemoval = Intent.ACTION_PACKAGE_REMOVED.equals(action) || Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action);
        boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        if (!isRemoval || !replacing) {
            int[] uids = null;
            if (Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE.equals(action) || Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE.equals(action)) {
                uids = intent.getIntArrayExtra(Intent.EXTRA_CHANGED_UID_LIST);
            } else {
                int uid = intent.getIntExtra(Intent.EXTRA_UID, -1);
                if (uid > 0) {
                    uids = new int[]{uid};
                }
            }
            generateServicesMap(uids, userId);
        }
    }

    public void invalidateCache(int userId) {
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            user.services = null;
            onServicesChangedLocked(userId);
        }
    }

    public void dump(FileDescriptor fd, PrintWriter fout, String[] args, int userId) {
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services != null) {
                fout.println("RegisteredServicesCache: " + user.services.size() + " services");
                Iterator<ServiceInfo<V>> it = user.services.values().iterator();
                while (it.hasNext()) {
                    fout.println("  " + it.next());
                }
            } else {
                fout.println("RegisteredServicesCache: services not loaded");
            }
        }
    }

    public RegisteredServicesCacheListener<V> getListener() {
        RegisteredServicesCacheListener<V> registeredServicesCacheListener;
        synchronized (this) {
            registeredServicesCacheListener = this.mListener;
        }
        return registeredServicesCacheListener;
    }

    public void setListener(RegisteredServicesCacheListener<V> listener, Handler handler) {
        if (handler == null) {
            handler = BackgroundThread.getHandler();
        }
        synchronized (this) {
            this.mHandler = handler;
            this.mListener = listener;
        }
    }

    private void notifyListener(final V type, final int userId, final boolean removed) {
        final RegisteredServicesCacheListener<V> listener;
        Handler handler;
        synchronized (this) {
            listener = this.mListener;
            handler = this.mHandler;
        }
        if (listener == null) {
            return;
        }
        handler.post(new Runnable() { // from class: android.content.pm.RegisteredServicesCache$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RegisteredServicesCache.lambda$notifyListener$0(RegisteredServicesCacheListener.this, type, userId, removed);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$notifyListener$0(RegisteredServicesCacheListener listener2, Object type, int userId, boolean removed) {
        try {
            listener2.onServiceChanged(type, userId, removed);
        } catch (Throwable th) {
            Slog.wtf(TAG, "Exception from onServiceChanged", th);
        }
    }

    /* renamed from: android.content.pm.RegisteredServicesCache$ServiceInfo */
    /* loaded from: classes.dex */
    public static class ServiceInfo<V> {
        public final ComponentInfo componentInfo;
        public final ComponentName componentName;
        public final V type;
        public final int uid;

        public ServiceInfo(V type, ComponentInfo componentInfo, ComponentName componentName) {
            this.type = type;
            this.componentInfo = componentInfo;
            this.componentName = componentName;
            this.uid = componentInfo != null ? componentInfo.applicationInfo.uid : -1;
        }

        public String toString() {
            return "ServiceInfo: " + this.type + ", " + this.componentName + ", uid " + this.uid;
        }
    }

    public ServiceInfo<V> getServiceInfo(V type, int userId) {
        ServiceInfo<V> serviceInfo;
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                generateServicesMap(null, userId);
            }
            serviceInfo = user.services.get(type);
        }
        return serviceInfo;
    }

    public Collection<ServiceInfo<V>> getAllServices(int userId) {
        Collection<ServiceInfo<V>> unmodifiableCollection;
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                generateServicesMap(null, userId);
            }
            unmodifiableCollection = Collections.unmodifiableCollection(new ArrayList(user.services.values()));
        }
        return unmodifiableCollection;
    }

    public void updateServices(int userId) {
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            if (user.services == null) {
                return;
            }
            List<ServiceInfo<V>> allServices = new ArrayList<>(user.services.values());
            IntArray updatedUids = null;
            for (ServiceInfo<V> service : allServices) {
                long versionCode = service.componentInfo.applicationInfo.versionCode;
                String pkg = service.componentInfo.packageName;
                ApplicationInfo newAppInfo = null;
                try {
                    newAppInfo = this.mContext.getPackageManager().getApplicationInfoAsUser(pkg, 0, userId);
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (newAppInfo == null || newAppInfo.versionCode != versionCode) {
                    if (updatedUids == null) {
                        updatedUids = new IntArray();
                    }
                    updatedUids.add(service.uid);
                }
            }
            if (updatedUids != null && updatedUids.size() > 0) {
                int[] updatedUidsArray = updatedUids.toArray();
                generateServicesMap(updatedUidsArray, userId);
            }
        }
    }

    public boolean getBindInstantServiceAllowed(int userId) {
        boolean z;
        this.mContext.enforceCallingOrSelfPermission(Manifest.C0000permission.MANAGE_BIND_INSTANT_SERVICE, "getBindInstantServiceAllowed");
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            z = user.mBindInstantServiceAllowed;
        }
        return z;
    }

    public void setBindInstantServiceAllowed(int userId, boolean allowed) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.C0000permission.MANAGE_BIND_INSTANT_SERVICE, "setBindInstantServiceAllowed");
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            user.mBindInstantServiceAllowed = allowed;
        }
    }

    protected boolean inSystemImage(int callerUid) {
        String[] packages = this.mContext.getPackageManager().getPackagesForUid(callerUid);
        if (packages != null) {
            for (String name : packages) {
                try {
                    PackageInfo packageInfo = this.mContext.getPackageManager().getPackageInfo(name, 0);
                    if ((packageInfo.applicationInfo.flags & 1) != 0) {
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return false;
                }
            }
        }
        return false;
    }

    protected List<ResolveInfo> queryIntentServices(int userId) {
        int flags;
        PackageManager pm = this.mContext.getPackageManager();
        synchronized (this.mServicesLock) {
            UserServices<V> user = findOrCreateUserLocked(userId);
            flags = user.mBindInstantServiceAllowed ? 786560 | 8388608 : 786560;
        }
        return pm.queryIntentServicesAsUser(new Intent(this.mInterfaceName), flags, userId);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void generateServicesMap(int[] changedUids, int userId) {
        ArrayList<ServiceInfo<V>> serviceInfos = new ArrayList<>();
        List<ResolveInfo> resolveInfos = queryIntentServices(userId);
        for (ResolveInfo resolveInfo : resolveInfos) {
            try {
                ServiceInfo<V> info = parseServiceInfo(resolveInfo);
                if (info == null) {
                    Log.m104w(TAG, "Unable to load service info " + resolveInfo.toString());
                } else {
                    serviceInfos.add(info);
                }
            } catch (IOException | XmlPullParserException e) {
                Log.m103w(TAG, "Unable to load service info " + resolveInfo.toString(), e);
            }
        }
        synchronized (this.mServicesLock) {
            try {
                UserServices<V> user = findOrCreateUserLocked(userId);
                boolean firstScan = user.services == null;
                if (firstScan) {
                    user.services = Maps.newHashMap();
                }
                new StringBuilder();
                boolean changed = false;
                Iterator<ServiceInfo<V>> it = serviceInfos.iterator();
                while (it.hasNext()) {
                    ServiceInfo<V> info2 = it.next();
                    Integer previousUid = user.persistentServices.get(info2.type);
                    if (previousUid == null) {
                        changed = true;
                        user.services.put(info2.type, info2);
                        user.persistentServices.put(info2.type, Integer.valueOf(info2.uid));
                        if (!user.mPersistentServicesFileDidNotExist || !firstScan) {
                            notifyListener(info2.type, userId, false);
                        }
                    } else if (previousUid.intValue() != info2.uid) {
                        if (inSystemImage(info2.uid) || !containsTypeAndUid(serviceInfos, info2.type, previousUid.intValue())) {
                            user.services.put(info2.type, info2);
                            user.persistentServices.put(info2.type, Integer.valueOf(info2.uid));
                            notifyListener(info2.type, userId, false);
                            changed = true;
                        }
                    } else {
                        user.services.put(info2.type, info2);
                    }
                }
                ArrayList<V> toBeRemoved = Lists.newArrayList();
                for (V v1 : user.persistentServices.keySet()) {
                    if (!containsType(serviceInfos, v1)) {
                        try {
                            if (containsUid(changedUids, user.persistentServices.get(v1).intValue())) {
                                toBeRemoved.add(v1);
                            }
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                }
                Iterator<V> it2 = toBeRemoved.iterator();
                while (it2.hasNext()) {
                    V v12 = it2.next();
                    changed = true;
                    user.persistentServices.remove(v12);
                    user.services.remove(v12);
                    notifyListener(v12, userId, true);
                }
                if (changed) {
                    onServicesChangedLocked(userId);
                    writePersistentServicesLocked(user, userId);
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onServicesChangedLocked(int userId) {
    }

    private boolean containsUid(int[] changedUids, int uid) {
        return changedUids == null || ArrayUtils.contains(changedUids, uid);
    }

    private boolean containsType(ArrayList<ServiceInfo<V>> serviceInfos, V type) {
        int N = serviceInfos.size();
        for (int i = 0; i < N; i++) {
            if (serviceInfos.get(i).type.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsTypeAndUid(ArrayList<ServiceInfo<V>> serviceInfos, V type, int uid) {
        int N = serviceInfos.size();
        for (int i = 0; i < N; i++) {
            ServiceInfo<V> serviceInfo = serviceInfos.get(i);
            if (serviceInfo.type.equals(type) && serviceInfo.uid == uid) {
                return true;
            }
        }
        return false;
    }

    protected ServiceInfo<V> parseServiceInfo(ResolveInfo service) throws XmlPullParserException, IOException {
        android.content.p001pm.ServiceInfo si = service.serviceInfo;
        ComponentName componentName = new ComponentName(si.packageName, si.name);
        PackageManager pm = this.mContext.getPackageManager();
        XmlResourceParser parser = null;
        try {
            try {
                parser = si.loadXmlMetaData(pm, this.mMetaDataName);
                if (parser != null) {
                    AttributeSet attrs = Xml.asAttributeSet(parser);
                    while (true) {
                        int type = parser.next();
                        if (type == 1 || type == 2) {
                            break;
                        }
                    }
                    String nodeName = parser.getName();
                    if (this.mAttributesName.equals(nodeName)) {
                        V v = parseServiceAttributes(pm.getResourcesForApplication(si.applicationInfo), si.packageName, attrs);
                        if (v == null) {
                        }
                        android.content.p001pm.ServiceInfo serviceInfo = service.serviceInfo;
                        ServiceInfo<V> serviceInfo2 = new ServiceInfo<>(v, serviceInfo, componentName);
                        if (parser != null) {
                            parser.close();
                        }
                        return serviceInfo2;
                    }
                    throw new XmlPullParserException("Meta-data does not start with " + this.mAttributesName + " tag");
                }
                throw new XmlPullParserException("No " + this.mMetaDataName + " meta-data");
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to load resources for pacakge " + si.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    private void readPersistentServicesLocked(InputStream is) throws XmlPullParserException, IOException {
        TypedXmlPullParser parser = Xml.resolvePullParser(is);
        for (int eventType = parser.getEventType(); eventType != 2 && eventType != 1; eventType = parser.next()) {
        }
        String tagName = parser.getName();
        if ("services".equals(tagName)) {
            int eventType2 = parser.next();
            do {
                if (eventType2 == 2 && parser.getDepth() == 2) {
                    String tagName2 = parser.getName();
                    if ("service".equals(tagName2)) {
                        V service = this.mSerializerAndParser.createFromXml(parser);
                        if (service != null) {
                            int uid = parser.getAttributeInt(null, "uid");
                            int userId = UserHandle.getUserId(uid);
                            UserServices<V> user = findOrCreateUserLocked(userId, false);
                            user.persistentServices.put(service, Integer.valueOf(uid));
                        } else {
                            return;
                        }
                    }
                }
                eventType2 = parser.next();
            } while (eventType2 != 1);
        }
    }

    private void migrateIfNecessaryLocked() {
        if (this.mSerializerAndParser == null) {
            return;
        }
        File systemDir = new File(getDataDirectory(), "system");
        File syncDir = new File(systemDir, REGISTERED_SERVICES_DIR);
        AtomicFile oldFile = new AtomicFile(new File(syncDir, this.mInterfaceName + ".xml"));
        boolean oldFileExists = oldFile.getBaseFile().exists();
        if (oldFileExists) {
            File marker = new File(syncDir, this.mInterfaceName + ".xml.migrated");
            if (!marker.exists()) {
                InputStream is = null;
                try {
                    try {
                        is = oldFile.openRead();
                        this.mUserServices.clear();
                        readPersistentServicesLocked(is);
                    } catch (Exception e) {
                        Log.m103w(TAG, "Error reading persistent services, starting from scratch", e);
                    }
                    try {
                        for (UserInfo user : getUsers()) {
                            UserServices<V> userServices = this.mUserServices.get(user.f48id);
                            if (userServices != null) {
                                writePersistentServicesLocked(userServices, user.f48id);
                            }
                        }
                        marker.createNewFile();
                    } catch (Exception e2) {
                        Log.m103w(TAG, "Migration failed", e2);
                    }
                    this.mUserServices.clear();
                } finally {
                    IoUtils.closeQuietly(is);
                }
            }
        }
    }

    private void writePersistentServicesLocked(UserServices<V> user, int userId) {
        if (this.mSerializerAndParser == null) {
            return;
        }
        AtomicFile atomicFile = createFileForUser(userId);
        FileOutputStream fos = null;
        try {
            fos = atomicFile.startWrite();
            TypedXmlSerializer out = Xml.resolveSerializer(fos);
            out.startDocument(null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag(null, "services");
            for (Map.Entry<V, Integer> service : user.persistentServices.entrySet()) {
                out.startTag(null, "service");
                out.attributeInt(null, "uid", service.getValue().intValue());
                this.mSerializerAndParser.writeAsXml((XmlSerializerAndParser<V>) service.getKey(), out);
                out.endTag(null, "service");
            }
            out.endTag(null, "services");
            out.endDocument();
            atomicFile.finishWrite(fos);
        } catch (IOException e1) {
            Log.m103w(TAG, "Error writing accounts", e1);
            if (fos != null) {
                atomicFile.failWrite(fos);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUserRemoved(int userId) {
        synchronized (this.mServicesLock) {
            this.mUserServices.remove(userId);
        }
    }

    protected List<UserInfo> getUsers() {
        return UserManager.get(this.mContext).getAliveUsers();
    }

    protected UserInfo getUser(int userId) {
        return UserManager.get(this.mContext).getUserInfo(userId);
    }

    private AtomicFile createFileForUser(int userId) {
        File userDir = getUserSystemDirectory(userId);
        File userFile = new File(userDir, "registered_services/" + this.mInterfaceName + ".xml");
        return new AtomicFile(userFile);
    }

    protected File getUserSystemDirectory(int userId) {
        return Environment.getUserSystemDirectory(userId);
    }

    protected File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    protected Map<V, Integer> getPersistentServices(int userId) {
        return findOrCreateUserLocked(userId).persistentServices;
    }
}
