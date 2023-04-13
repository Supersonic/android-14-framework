package android.test;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.test.mock.MockApplication;
import android.test.mock.MockService;
import java.util.Random;
@Deprecated
/* loaded from: classes.dex */
public abstract class ServiceTestCase<T extends Service> extends AndroidTestCase {
    private Application mApplication;
    private T mService;
    Class<T> mServiceClass;
    private int mServiceId;
    private Context mSystemContext;
    private boolean mServiceAttached = false;
    private boolean mServiceCreated = false;
    private boolean mServiceStarted = false;
    private boolean mServiceBound = false;
    private Intent mServiceIntent = null;

    public ServiceTestCase(Class<T> serviceClass) {
        this.mServiceClass = serviceClass;
    }

    public T getService() {
        return this.mService;
    }

    protected void setUp() throws Exception {
        super.setUp();
        this.mSystemContext = getContext();
    }

    protected void setupService() {
        this.mService = null;
        try {
            this.mService = this.mServiceClass.newInstance();
        } catch (Exception e) {
            assertNotNull(this.mService);
        }
        if (getApplication() == null) {
            setApplication(new MockApplication());
        }
        MockService.attachForTesting(this.mService, getContext(), this.mServiceClass.getName(), getApplication());
        assertNotNull(this.mService);
        this.mServiceId = new Random().nextInt();
        this.mServiceAttached = true;
    }

    protected void startService(Intent intent) {
        if (!this.mServiceAttached) {
            setupService();
        }
        assertNotNull(this.mService);
        if (!this.mServiceCreated) {
            this.mService.onCreate();
            this.mServiceCreated = true;
        }
        this.mService.onStartCommand(intent, 0, this.mServiceId);
        this.mServiceStarted = true;
    }

    protected IBinder bindService(Intent intent) {
        if (!this.mServiceAttached) {
            setupService();
        }
        assertNotNull(this.mService);
        if (!this.mServiceCreated) {
            this.mService.onCreate();
            this.mServiceCreated = true;
        }
        this.mServiceIntent = intent.cloneFilter();
        IBinder result = this.mService.onBind(intent);
        this.mServiceBound = true;
        return result;
    }

    protected void shutdownService() {
        if (this.mServiceStarted) {
            this.mService.stopSelf();
            this.mServiceStarted = false;
        } else if (this.mServiceBound) {
            this.mService.onUnbind(this.mServiceIntent);
            this.mServiceBound = false;
        }
        if (this.mServiceCreated) {
            this.mService.onDestroy();
            this.mServiceCreated = false;
        }
    }

    protected void tearDown() throws Exception {
        shutdownService();
        this.mService = null;
        scrubClass(ServiceTestCase.class);
        super.tearDown();
    }

    public void setApplication(Application application) {
        this.mApplication = application;
    }

    public Application getApplication() {
        return this.mApplication;
    }

    public Context getSystemContext() {
        return this.mSystemContext;
    }

    public void testServiceTestCaseSetUpProperly() throws Exception {
        setupService();
        assertNotNull("service should be launched successfully", this.mService);
    }
}
