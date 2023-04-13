package com.android.server.powerstats;

import android.content.Context;
import android.hardware.power.stats.Channel;
import android.hardware.power.stats.EnergyConsumer;
import android.hardware.power.stats.EnergyConsumerResult;
import android.hardware.power.stats.EnergyMeasurement;
import android.hardware.power.stats.PowerEntity;
import android.hardware.power.stats.StateResidencyResult;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.power.PowerStatsInternal;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.SystemService;
import com.android.server.powerstats.PowerStatsHALWrapper;
import com.android.server.powerstats.ProtoStreamUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
/* loaded from: classes2.dex */
public class PowerStatsService extends SystemService {
    public static final String TAG = "PowerStatsService";
    public BatteryTrigger mBatteryTrigger;
    public Context mContext;
    public File mDataStoragePath;
    @GuardedBy({"this"})
    public EnergyConsumer[] mEnergyConsumers;
    public final Injector mInjector;
    @GuardedBy({"this"})
    public Looper mLooper;
    public PowerStatsInternal mPowerStatsInternal;
    public PowerStatsLogger mPowerStatsLogger;
    public StatsPullAtomCallbackImpl mPullAtomCallback;
    public TimerTrigger mTimerTrigger;

    @VisibleForTesting
    /* loaded from: classes2.dex */
    public static class Injector {
        @GuardedBy({"this"})
        public PowerStatsHALWrapper.IPowerStatsHALWrapper mPowerStatsHALWrapper;

        public String createMeterCacheFilename() {
            return "meterCache";
        }

        public String createMeterFilename() {
            return "log.powerstats.meter.0";
        }

        public String createModelCacheFilename() {
            return "modelCache";
        }

        public String createModelFilename() {
            return "log.powerstats.model.0";
        }

        public String createResidencyCacheFilename() {
            return "residencyCache";
        }

        public String createResidencyFilename() {
            return "log.powerstats.residency.0";
        }

        public File createDataStoragePath() {
            return new File(Environment.getDataSystemDeDirectory(0), "powerstats");
        }

        public PowerStatsHALWrapper.IPowerStatsHALWrapper getPowerStatsHALWrapperImpl() {
            PowerStatsHALWrapper.IPowerStatsHALWrapper iPowerStatsHALWrapper;
            synchronized (this) {
                if (this.mPowerStatsHALWrapper == null) {
                    this.mPowerStatsHALWrapper = PowerStatsHALWrapper.getPowerStatsHalImpl();
                }
                iPowerStatsHALWrapper = this.mPowerStatsHALWrapper;
            }
            return iPowerStatsHALWrapper;
        }

        public PowerStatsLogger createPowerStatsLogger(Context context, Looper looper, File file, String str, String str2, String str3, String str4, String str5, String str6, PowerStatsHALWrapper.IPowerStatsHALWrapper iPowerStatsHALWrapper) {
            return new PowerStatsLogger(context, looper, file, str, str2, str3, str4, str5, str6, iPowerStatsHALWrapper);
        }

        public BatteryTrigger createBatteryTrigger(Context context, PowerStatsLogger powerStatsLogger) {
            return new BatteryTrigger(context, powerStatsLogger, true);
        }

        public TimerTrigger createTimerTrigger(Context context, PowerStatsLogger powerStatsLogger) {
            return new TimerTrigger(context, powerStatsLogger, true);
        }

        public StatsPullAtomCallbackImpl createStatsPullerImpl(Context context, PowerStatsInternal powerStatsInternal) {
            return new StatsPullAtomCallbackImpl(context, powerStatsInternal);
        }
    }

    /* loaded from: classes2.dex */
    public final class BinderService extends Binder {
        public BinderService() {
        }

        @Override // android.os.Binder
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(PowerStatsService.this.mContext, PowerStatsService.TAG, printWriter)) {
                if (PowerStatsService.this.mPowerStatsLogger == null) {
                    Slog.e(PowerStatsService.TAG, "PowerStats HAL is not initialized.  No data available.");
                } else if (strArr.length > 0 && "--proto".equals(strArr[0])) {
                    if ("model".equals(strArr[1])) {
                        PowerStatsService.this.mPowerStatsLogger.writeModelDataToFile(fileDescriptor);
                    } else if ("meter".equals(strArr[1])) {
                        PowerStatsService.this.mPowerStatsLogger.writeMeterDataToFile(fileDescriptor);
                    } else if ("residency".equals(strArr[1])) {
                        PowerStatsService.this.mPowerStatsLogger.writeResidencyDataToFile(fileDescriptor);
                    }
                } else if (strArr.length == 0) {
                    printWriter.println("PowerStatsService dumpsys: available PowerEntities");
                    ProtoStreamUtils.PowerEntityUtils.dumpsys(PowerStatsService.this.getPowerStatsHal().getPowerEntityInfo(), printWriter);
                    printWriter.println("PowerStatsService dumpsys: available Channels");
                    ProtoStreamUtils.ChannelUtils.dumpsys(PowerStatsService.this.getPowerStatsHal().getEnergyMeterInfo(), printWriter);
                    printWriter.println("PowerStatsService dumpsys: available EnergyConsumers");
                    ProtoStreamUtils.EnergyConsumerUtils.dumpsys(PowerStatsService.this.getPowerStatsHal().getEnergyConsumerInfo(), printWriter);
                }
            }
        }
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 500) {
            onSystemServicesReady();
        } else if (i == 1000) {
            onBootCompleted();
        }
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        if (getPowerStatsHal().isInitialized()) {
            LocalService localService = new LocalService();
            this.mPowerStatsInternal = localService;
            publishLocalService(PowerStatsInternal.class, localService);
        }
        publishBinderService("powerstats", new BinderService());
    }

    public final void onSystemServicesReady() {
        this.mPullAtomCallback = this.mInjector.createStatsPullerImpl(this.mContext, this.mPowerStatsInternal);
    }

    @VisibleForTesting
    public boolean getDeleteMeterDataOnBoot() {
        return this.mPowerStatsLogger.getDeleteMeterDataOnBoot();
    }

    @VisibleForTesting
    public boolean getDeleteModelDataOnBoot() {
        return this.mPowerStatsLogger.getDeleteModelDataOnBoot();
    }

    @VisibleForTesting
    public boolean getDeleteResidencyDataOnBoot() {
        return this.mPowerStatsLogger.getDeleteResidencyDataOnBoot();
    }

    public final void onBootCompleted() {
        if (getPowerStatsHal().isInitialized()) {
            this.mDataStoragePath = this.mInjector.createDataStoragePath();
            PowerStatsLogger createPowerStatsLogger = this.mInjector.createPowerStatsLogger(this.mContext, getLooper(), this.mDataStoragePath, this.mInjector.createMeterFilename(), this.mInjector.createMeterCacheFilename(), this.mInjector.createModelFilename(), this.mInjector.createModelCacheFilename(), this.mInjector.createResidencyFilename(), this.mInjector.createResidencyCacheFilename(), getPowerStatsHal());
            this.mPowerStatsLogger = createPowerStatsLogger;
            this.mBatteryTrigger = this.mInjector.createBatteryTrigger(this.mContext, createPowerStatsLogger);
            this.mTimerTrigger = this.mInjector.createTimerTrigger(this.mContext, this.mPowerStatsLogger);
            return;
        }
        Slog.e(TAG, "Failed to start PowerStatsService loggers");
    }

    public final PowerStatsHALWrapper.IPowerStatsHALWrapper getPowerStatsHal() {
        return this.mInjector.getPowerStatsHALWrapperImpl();
    }

    public final Looper getLooper() {
        synchronized (this) {
            Looper looper = this.mLooper;
            if (looper == null) {
                HandlerThread handlerThread = new HandlerThread(TAG);
                handlerThread.start();
                return handlerThread.getLooper();
            }
            return looper;
        }
    }

    public final EnergyConsumer[] getEnergyConsumerInfo() {
        EnergyConsumer[] energyConsumerArr;
        synchronized (this) {
            if (this.mEnergyConsumers == null) {
                this.mEnergyConsumers = getPowerStatsHal().getEnergyConsumerInfo();
            }
            energyConsumerArr = this.mEnergyConsumers;
        }
        return energyConsumerArr;
    }

    public PowerStatsService(Context context) {
        this(context, new Injector());
    }

    @VisibleForTesting
    public PowerStatsService(Context context, Injector injector) {
        super(context);
        this.mEnergyConsumers = null;
        this.mContext = context;
        this.mInjector = injector;
    }

    /* loaded from: classes2.dex */
    public final class LocalService extends PowerStatsInternal {
        public final Handler mHandler;

        public LocalService() {
            this.mHandler = new Handler(PowerStatsService.this.getLooper());
        }

        @Override // android.power.PowerStatsInternal
        public EnergyConsumer[] getEnergyConsumerInfo() {
            return PowerStatsService.this.getPowerStatsHal().getEnergyConsumerInfo();
        }

        @Override // android.power.PowerStatsInternal
        public CompletableFuture<EnergyConsumerResult[]> getEnergyConsumedAsync(int[] iArr) {
            CompletableFuture<EnergyConsumerResult[]> completableFuture = new CompletableFuture<>();
            Handler handler = this.mHandler;
            final PowerStatsService powerStatsService = PowerStatsService.this;
            handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.powerstats.PowerStatsService$LocalService$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    PowerStatsService.this.getEnergyConsumedAsync((CompletableFuture) obj, (int[]) obj2);
                }
            }, completableFuture, iArr));
            return completableFuture;
        }

        @Override // android.power.PowerStatsInternal
        public PowerEntity[] getPowerEntityInfo() {
            return PowerStatsService.this.getPowerStatsHal().getPowerEntityInfo();
        }

        @Override // android.power.PowerStatsInternal
        public CompletableFuture<StateResidencyResult[]> getStateResidencyAsync(int[] iArr) {
            CompletableFuture<StateResidencyResult[]> completableFuture = new CompletableFuture<>();
            Handler handler = this.mHandler;
            final PowerStatsService powerStatsService = PowerStatsService.this;
            handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.powerstats.PowerStatsService$LocalService$$ExternalSyntheticLambda1
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    PowerStatsService.this.getStateResidencyAsync((CompletableFuture) obj, (int[]) obj2);
                }
            }, completableFuture, iArr));
            return completableFuture;
        }

        @Override // android.power.PowerStatsInternal
        public Channel[] getEnergyMeterInfo() {
            return PowerStatsService.this.getPowerStatsHal().getEnergyMeterInfo();
        }

        @Override // android.power.PowerStatsInternal
        public CompletableFuture<EnergyMeasurement[]> readEnergyMeterAsync(int[] iArr) {
            CompletableFuture<EnergyMeasurement[]> completableFuture = new CompletableFuture<>();
            Handler handler = this.mHandler;
            final PowerStatsService powerStatsService = PowerStatsService.this;
            handler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.powerstats.PowerStatsService$LocalService$$ExternalSyntheticLambda2
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    PowerStatsService.this.readEnergyMeterAsync((CompletableFuture) obj, (int[]) obj2);
                }
            }, completableFuture, iArr));
            return completableFuture;
        }
    }

    public final void getEnergyConsumedAsync(CompletableFuture<EnergyConsumerResult[]> completableFuture, int[] iArr) {
        int length;
        EnergyConsumerResult[] energyConsumed = getPowerStatsHal().getEnergyConsumed(iArr);
        EnergyConsumer[] energyConsumerInfo = getEnergyConsumerInfo();
        if (energyConsumerInfo != null) {
            if (iArr.length == 0) {
                length = energyConsumerInfo.length;
            } else {
                length = iArr.length;
            }
            if (energyConsumed == null || length != energyConsumed.length) {
                StringBuilder sb = new StringBuilder();
                sb.append("Requested ids:");
                if (iArr.length == 0) {
                    sb.append("ALL");
                }
                sb.append("[");
                for (int i = 0; i < length; i++) {
                    int i2 = iArr[i];
                    sb.append(i2);
                    sb.append("(type:");
                    sb.append((int) energyConsumerInfo[i2].type);
                    sb.append(",ord:");
                    sb.append(energyConsumerInfo[i2].ordinal);
                    sb.append(",name:");
                    sb.append(energyConsumerInfo[i2].name);
                    sb.append(")");
                    if (i != length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
                sb.append(", Received result ids:");
                if (energyConsumed == null) {
                    sb.append("null");
                } else {
                    sb.append("[");
                    int length2 = energyConsumed.length;
                    for (int i3 = 0; i3 < length2; i3++) {
                        int i4 = energyConsumed[i3].f6id;
                        sb.append(i4);
                        sb.append("(type:");
                        sb.append((int) energyConsumerInfo[i4].type);
                        sb.append(",ord:");
                        sb.append(energyConsumerInfo[i4].ordinal);
                        sb.append(",name:");
                        sb.append(energyConsumerInfo[i4].name);
                        sb.append(")");
                        if (i3 != length2 - 1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("]");
                }
                Slog.wtf(TAG, "Missing result from getEnergyConsumedAsync call. " + ((Object) sb));
            }
        }
        completableFuture.complete(energyConsumed);
    }

    public final void getStateResidencyAsync(CompletableFuture<StateResidencyResult[]> completableFuture, int[] iArr) {
        completableFuture.complete(getPowerStatsHal().getStateResidency(iArr));
    }

    public final void readEnergyMeterAsync(CompletableFuture<EnergyMeasurement[]> completableFuture, int[] iArr) {
        completableFuture.complete(getPowerStatsHal().readEnergyMeter(iArr));
    }
}
