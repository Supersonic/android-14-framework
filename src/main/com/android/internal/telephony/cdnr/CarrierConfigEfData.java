package com.android.internal.telephony.cdnr;

import android.os.PersistableBundle;
import android.text.TextUtils;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public final class CarrierConfigEfData implements EfData {
    private final String[] mEhplmn;
    private final String[] mOpl;
    private final String[] mPnn;
    private final String[] mSpdi;
    private final String mSpn;
    private final int mSpnDisplayCondition;

    public CarrierConfigEfData(PersistableBundle persistableBundle) {
        this.mSpn = persistableBundle.getString("carrier_name_string");
        this.mSpnDisplayCondition = persistableBundle.getInt("spn_display_condition_override_int", -1);
        this.mSpdi = persistableBundle.getStringArray("spdi_override_string_array");
        this.mEhplmn = persistableBundle.getStringArray("ehplmn_override_string_array");
        this.mPnn = persistableBundle.getStringArray("pnn_override_string_array");
        this.mOpl = persistableBundle.getStringArray("opl_override_opl_string_array");
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public String getServiceProviderName() {
        if (TextUtils.isEmpty(this.mSpn)) {
            return null;
        }
        return this.mSpn;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public int getServiceProviderNameDisplayCondition(boolean z) {
        return this.mSpnDisplayCondition;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<String> getServiceProviderDisplayInformation() {
        String[] strArr = this.mSpdi;
        if (strArr != null) {
            return Arrays.asList(strArr);
        }
        return null;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<String> getEhplmnList() {
        String[] strArr = this.mEhplmn;
        if (strArr != null) {
            return Arrays.asList(strArr);
        }
        return null;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<IccRecords.PlmnNetworkName> getPlmnNetworkNameList() {
        String[] strArr;
        if (this.mPnn != null) {
            ArrayList arrayList = new ArrayList(this.mPnn.length);
            for (String str : this.mPnn) {
                try {
                    String[] split = str.split("\\s*,\\s*");
                    arrayList.add(new IccRecords.PlmnNetworkName(split[0], split.length > 1 ? split[1] : PhoneConfigurationManager.SSSS));
                } catch (Exception unused) {
                    Rlog.e("CarrierConfigEfData", "CarrierConfig wrong pnn format, pnnStr = " + str);
                }
            }
            return arrayList;
        }
        return null;
    }

    @Override // com.android.internal.telephony.cdnr.EfData
    public List<IccRecords.OperatorPlmnInfo> getOperatorPlmnList() {
        String[] strArr;
        if (this.mOpl != null) {
            ArrayList arrayList = new ArrayList(this.mOpl.length);
            for (String str : this.mOpl) {
                try {
                    String[] split = str.split("\\s*,\\s*");
                    arrayList.add(new IccRecords.OperatorPlmnInfo(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
                } catch (Exception unused) {
                    Rlog.e("CarrierConfigEfData", "CarrierConfig wrong opl format, oplStr = " + str);
                }
            }
            return arrayList;
        }
        return null;
    }
}
