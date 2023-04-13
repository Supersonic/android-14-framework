package com.android.internal.telephony;

import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.ServiceState;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.telephony.Rlog;
import java.util.Arrays;
/* loaded from: classes.dex */
public class RatRatcheter {
    private final Phone mPhone;
    private final SparseArray<SparseIntArray> mRatFamilyMap = new SparseArray<>();

    public static boolean updateBandwidths(int[] iArr, ServiceState serviceState) {
        if (iArr == null) {
            return false;
        }
        if (Arrays.stream(iArr).sum() > Arrays.stream(serviceState.getCellBandwidths()).sum()) {
            serviceState.setCellBandwidths(iArr);
            return true;
        }
        return false;
    }

    public RatRatcheter(Phone phone) {
        this.mPhone = phone;
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) phone.getContext().getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null) {
            carrierConfigManager.registerCarrierConfigChangeListener(phone.getContext().getMainExecutor(), new CarrierConfigManager.CarrierConfigChangeListener() { // from class: com.android.internal.telephony.RatRatcheter$$ExternalSyntheticLambda0
                public final void onCarrierConfigChanged(int i, int i2, int i3, int i4) {
                    RatRatcheter.this.lambda$new$0(i, i2, i3, i4);
                }
            });
        }
        resetRatFamilyMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, int i2, int i3, int i4) {
        resetRatFamilyMap();
    }

    private int ratchetRat(int i, int i2) {
        int networkTypeToRilRadioTechnology = ServiceState.networkTypeToRilRadioTechnology(i);
        int networkTypeToRilRadioTechnology2 = ServiceState.networkTypeToRilRadioTechnology(i2);
        synchronized (this.mRatFamilyMap) {
            SparseIntArray sparseIntArray = this.mRatFamilyMap.get(networkTypeToRilRadioTechnology);
            if (sparseIntArray == null) {
                return i2;
            }
            SparseIntArray sparseIntArray2 = this.mRatFamilyMap.get(networkTypeToRilRadioTechnology2);
            if (sparseIntArray2 != sparseIntArray) {
                return i2;
            }
            if (sparseIntArray2.get(networkTypeToRilRadioTechnology, -1) <= sparseIntArray2.get(networkTypeToRilRadioTechnology2, -1)) {
                networkTypeToRilRadioTechnology = networkTypeToRilRadioTechnology2;
            }
            return ServiceState.rilRadioTechnologyToNetworkType(networkTypeToRilRadioTechnology);
        }
    }

    public void ratchet(ServiceState serviceState, ServiceState serviceState2) {
        if (!isSameRatFamily(serviceState, serviceState2)) {
            Rlog.e("RilRatcheter", "Same cell cannot have different RAT Families. Likely bug.");
            return;
        }
        int[] iArr = {1, 2};
        for (int i = 0; i < 2; i++) {
            int i2 = iArr[i];
            NetworkRegistrationInfo networkRegistrationInfo = serviceState.getNetworkRegistrationInfo(i2, 1);
            NetworkRegistrationInfo networkRegistrationInfo2 = serviceState2.getNetworkRegistrationInfo(i2, 1);
            networkRegistrationInfo2.setAccessNetworkTechnology(ratchetRat(networkRegistrationInfo.getAccessNetworkTechnology(), networkRegistrationInfo2.getAccessNetworkTechnology()));
            if (networkRegistrationInfo.isUsingCarrierAggregation()) {
                networkRegistrationInfo2.setIsUsingCarrierAggregation(true);
            }
            serviceState2.addNetworkRegistrationInfo(networkRegistrationInfo2);
        }
        updateBandwidths(serviceState.getCellBandwidths(), serviceState2);
    }

    private boolean isSameRatFamily(ServiceState serviceState, ServiceState serviceState2) {
        synchronized (this.mRatFamilyMap) {
            boolean z = true;
            int networkTypeToRilRadioTechnology = ServiceState.networkTypeToRilRadioTechnology(serviceState.getNetworkRegistrationInfo(2, 1).getAccessNetworkTechnology());
            int networkTypeToRilRadioTechnology2 = ServiceState.networkTypeToRilRadioTechnology(serviceState2.getNetworkRegistrationInfo(2, 1).getAccessNetworkTechnology());
            if (networkTypeToRilRadioTechnology == 14 && serviceState.isUsingCarrierAggregation()) {
                networkTypeToRilRadioTechnology = 19;
            }
            if (networkTypeToRilRadioTechnology2 == 14 && serviceState2.isUsingCarrierAggregation()) {
                networkTypeToRilRadioTechnology2 = 19;
            }
            if (networkTypeToRilRadioTechnology == networkTypeToRilRadioTechnology2) {
                return true;
            }
            if (this.mRatFamilyMap.get(networkTypeToRilRadioTechnology) == null) {
                return false;
            }
            if (this.mRatFamilyMap.get(networkTypeToRilRadioTechnology) != this.mRatFamilyMap.get(networkTypeToRilRadioTechnology2)) {
                z = false;
            }
            return z;
        }
    }

    private void resetRatFamilyMap() {
        synchronized (this.mRatFamilyMap) {
            this.mRatFamilyMap.clear();
            PersistableBundle carrierConfigSubset = CarrierConfigManager.getCarrierConfigSubset(this.mPhone.getContext(), this.mPhone.getSubId(), new String[]{"ratchet_rat_families"});
            if (carrierConfigSubset != null && !carrierConfigSubset.isEmpty()) {
                String[] stringArray = carrierConfigSubset.getStringArray("ratchet_rat_families");
                if (stringArray == null) {
                    return;
                }
                for (String str : stringArray) {
                    String[] split = str.split(",");
                    if (split.length >= 2) {
                        SparseIntArray sparseIntArray = new SparseIntArray(split.length);
                        int length = split.length;
                        int i = 0;
                        int i2 = 0;
                        while (true) {
                            if (i < length) {
                                String str2 = split[i];
                                try {
                                    int parseInt = Integer.parseInt(str2.trim());
                                    if (this.mRatFamilyMap.get(parseInt) != null) {
                                        Rlog.e("RilRatcheter", "RAT listed twice: " + str2);
                                        break;
                                    }
                                    sparseIntArray.put(parseInt, i2);
                                    this.mRatFamilyMap.put(parseInt, sparseIntArray);
                                    i++;
                                    i2++;
                                } catch (NumberFormatException unused) {
                                    Rlog.e("RilRatcheter", "NumberFormatException on " + str2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
