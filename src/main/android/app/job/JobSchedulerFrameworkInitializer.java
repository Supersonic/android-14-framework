package android.app.job;

import android.annotation.SystemApi;
import android.app.JobSchedulerImpl;
import android.app.SystemServiceRegistry;
import android.app.job.IJobScheduler;
import android.app.tare.EconomyManager;
import android.content.Context;
import android.p008os.DeviceIdleManager;
import android.p008os.IBinder;
import android.p008os.IDeviceIdleController;
import android.p008os.PowerExemptionManager;
import android.p008os.PowerWhitelistManager;
@SystemApi
/* loaded from: classes.dex */
public class JobSchedulerFrameworkInitializer {
    private JobSchedulerFrameworkInitializer() {
    }

    public static void registerServiceWrappers() {
        SystemServiceRegistry.registerContextAwareService(Context.JOB_SCHEDULER_SERVICE, JobScheduler.class, new SystemServiceRegistry.ContextAwareServiceProducerWithBinder() { // from class: android.app.job.JobSchedulerFrameworkInitializer$$ExternalSyntheticLambda0
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithBinder
            public final Object createService(Context context, IBinder iBinder) {
                return JobSchedulerFrameworkInitializer.lambda$registerServiceWrappers$0(context, iBinder);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.DEVICE_IDLE_CONTROLLER, DeviceIdleManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithBinder() { // from class: android.app.job.JobSchedulerFrameworkInitializer$$ExternalSyntheticLambda1
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithBinder
            public final Object createService(Context context, IBinder iBinder) {
                return JobSchedulerFrameworkInitializer.lambda$registerServiceWrappers$1(context, iBinder);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.POWER_WHITELIST_MANAGER, PowerWhitelistManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.app.job.JobSchedulerFrameworkInitializer$$ExternalSyntheticLambda2
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return new PowerWhitelistManager(context);
            }
        });
        SystemServiceRegistry.registerContextAwareService(Context.POWER_EXEMPTION_SERVICE, PowerExemptionManager.class, new SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder() { // from class: android.app.job.JobSchedulerFrameworkInitializer$$ExternalSyntheticLambda3
            @Override // android.app.SystemServiceRegistry.ContextAwareServiceProducerWithoutBinder
            public final Object createService(Context context) {
                return new PowerExemptionManager(context);
            }
        });
        SystemServiceRegistry.registerStaticService(Context.RESOURCE_ECONOMY_SERVICE, EconomyManager.class, new SystemServiceRegistry.StaticServiceProducerWithoutBinder() { // from class: android.app.job.JobSchedulerFrameworkInitializer$$ExternalSyntheticLambda4
            @Override // android.app.SystemServiceRegistry.StaticServiceProducerWithoutBinder
            public final Object createService() {
                return new EconomyManager();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ JobScheduler lambda$registerServiceWrappers$0(Context context, IBinder b) {
        return new JobSchedulerImpl(context, IJobScheduler.Stub.asInterface(b));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ DeviceIdleManager lambda$registerServiceWrappers$1(Context context, IBinder b) {
        return new DeviceIdleManager(context, IDeviceIdleController.Stub.asInterface(b));
    }
}
