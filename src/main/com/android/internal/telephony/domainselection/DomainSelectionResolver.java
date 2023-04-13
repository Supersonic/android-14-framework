package com.android.internal.telephony.domainselection;

import android.content.Context;
import android.telephony.DomainSelectionService;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.RIL;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class DomainSelectionResolver {
    private static final String TAG = "DomainSelectionResolver";
    private static DomainSelectionResolver sInstance;
    private final Context mContext;
    private DomainSelectionController mController;
    private final boolean mDeviceConfigEnabled;
    private DomainSelectionControllerFactory mDomainSelectionControllerFactory = new DomainSelectionControllerFactory() { // from class: com.android.internal.telephony.domainselection.DomainSelectionResolver.1
        @Override // com.android.internal.telephony.domainselection.DomainSelectionResolver.DomainSelectionControllerFactory
        public DomainSelectionController create(Context context, DomainSelectionService domainSelectionService) {
            return new DomainSelectionController(context, domainSelectionService);
        }
    };
    private final LocalLog mEventLog = new LocalLog(10);

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface DomainSelectionControllerFactory {
        DomainSelectionController create(Context context, DomainSelectionService domainSelectionService);
    }

    public static void make(Context context, boolean z) {
        if (sInstance == null) {
            sInstance = new DomainSelectionResolver(context, z);
        }
    }

    public static DomainSelectionResolver getInstance() {
        DomainSelectionResolver domainSelectionResolver = sInstance;
        if (domainSelectionResolver != null) {
            return domainSelectionResolver;
        }
        throw new IllegalStateException("DomainSelectionResolver is not ready!");
    }

    @VisibleForTesting
    public static void setDomainSelectionResolver(DomainSelectionResolver domainSelectionResolver) {
        sInstance = domainSelectionResolver;
    }

    public DomainSelectionResolver(Context context, boolean z) {
        this.mContext = context;
        this.mDeviceConfigEnabled = z;
        logi("DomainSelectionResolver created: device-config=" + z);
    }

    public boolean isDomainSelectionSupported() {
        return this.mDeviceConfigEnabled && PhoneFactory.getDefaultPhone().getHalVersion(4).greaterOrEqual(RIL.RADIO_HAL_VERSION_2_1);
    }

    public DomainSelectionConnection getDomainSelectionConnection(Phone phone, int i, boolean z) {
        if (this.mController == null) {
            throw new IllegalStateException("DomainSelection is not supported!");
        }
        if (phone == null || !phone.isImsAvailable()) {
            return null;
        }
        return this.mController.getDomainSelectionConnection(phone, i, z);
    }

    @VisibleForTesting
    public void setDomainSelectionControllerFactory(DomainSelectionControllerFactory domainSelectionControllerFactory) {
        this.mDomainSelectionControllerFactory = domainSelectionControllerFactory;
    }

    public void initialize(DomainSelectionService domainSelectionService) {
        logi("Initialize.");
        this.mController = this.mDomainSelectionControllerFactory.create(this.mContext, domainSelectionService);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println("Resolver:");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("Event Log:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mEventLog.dump(androidUtilIndentingPrintWriter);
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.decreaseIndent();
        androidUtilIndentingPrintWriter.println("Controller:");
        androidUtilIndentingPrintWriter.increaseIndent();
        DomainSelectionController domainSelectionController = this.mController;
        if (domainSelectionController == null) {
            androidUtilIndentingPrintWriter.println("no active controller");
        } else {
            domainSelectionController.dump(androidUtilIndentingPrintWriter);
        }
        androidUtilIndentingPrintWriter.decreaseIndent();
    }

    private void logi(String str) {
        Log.i(TAG, str);
        this.mEventLog.log(str);
    }
}
