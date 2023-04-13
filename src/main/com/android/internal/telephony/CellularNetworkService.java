package com.android.internal.telephony;

import android.hardware.radio.V1_0.DataRegStateResult;
import android.hardware.radio.V1_0.VoiceRegStateResult;
import android.hardware.radio.V1_4.LteVopsInfo;
import android.hardware.radio.V1_4.NrIndicators;
import android.hardware.radio.V1_5.RegStateResult;
import android.hardware.radio.V1_6.RegStateResult;
import android.hardware.radio.network.AccessTechnologySpecificInfo;
import android.hardware.radio.network.RegStateResult;
import android.os.AsyncResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.AnomalyReporter;
import android.telephony.CellIdentity;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityTdscdma;
import android.telephony.CellIdentityWcdma;
import android.telephony.DataSpecificRegistrationInfo;
import android.telephony.LteVopsSupportInfo;
import android.telephony.NetworkRegistrationInfo;
import android.telephony.NetworkService;
import android.telephony.NetworkServiceCallback;
import android.telephony.NrVopsSupportInfo;
import android.telephony.ServiceState;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.internal.annotations.VisibleForTesting;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/* loaded from: classes.dex */
public class CellularNetworkService extends NetworkService {
    private static final String TAG = CellularNetworkService.class.getSimpleName();
    private static final Map<Class<? extends CellIdentity>, List<Integer>> sNetworkTypes;

    static {
        ArrayMap arrayMap = new ArrayMap();
        sNetworkTypes = arrayMap;
        arrayMap.put(CellIdentityGsm.class, Arrays.asList(16, 1, 2));
        arrayMap.put(CellIdentityWcdma.class, Arrays.asList(3, 8, 9, 10, 15));
        arrayMap.put(CellIdentityCdma.class, Arrays.asList(4, 7, 5, 6, 12, 14));
        arrayMap.put(CellIdentityLte.class, Arrays.asList(13));
        arrayMap.put(CellIdentityNr.class, Arrays.asList(20));
        arrayMap.put(CellIdentityTdscdma.class, Arrays.asList(17));
    }

    /* loaded from: classes.dex */
    private class CellularNetworkServiceProvider extends NetworkService.NetworkServiceProvider {
        private final Map<Message, NetworkServiceCallback> mCallbackMap;
        private final Handler mHandler;
        private final Phone mPhone;

        private int getRegStateFromHalRegState(int i) {
            int i2 = 1;
            if (i != 1) {
                i2 = 2;
                if (i != 2) {
                    if (i != 3) {
                        if (i != 4) {
                            if (i != 5) {
                                if (i != 20) {
                                    switch (i) {
                                        case 12:
                                            break;
                                        case 13:
                                            break;
                                        case 14:
                                            break;
                                        default:
                                            return 0;
                                    }
                                } else {
                                    return 6;
                                }
                            } else {
                                return 5;
                            }
                        }
                        return 4;
                    }
                    return 3;
                }
            }
            return i2;
        }

        private boolean isEmergencyOnly(int i) {
            if (i == 10 || i == 20) {
                return true;
            }
            switch (i) {
                case 12:
                case 13:
                case 14:
                    return true;
                default:
                    return false;
            }
        }

        CellularNetworkServiceProvider(int i) {
            super(CellularNetworkService.this, i);
            this.mCallbackMap = new HashMap();
            Phone phone = PhoneFactory.getPhone(getSlotIndex());
            this.mPhone = phone;
            Handler handler = new Handler(Looper.myLooper()) { // from class: com.android.internal.telephony.CellularNetworkService.CellularNetworkServiceProvider.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    NetworkServiceCallback networkServiceCallback = (NetworkServiceCallback) CellularNetworkServiceProvider.this.mCallbackMap.remove(message);
                    int i2 = message.what;
                    if (i2 != 1 && i2 != 2) {
                        if (i2 != 3) {
                            return;
                        }
                        CellularNetworkServiceProvider.this.notifyNetworkRegistrationInfoChanged();
                    } else if (networkServiceCallback == null) {
                    } else {
                        AsyncResult asyncResult = (AsyncResult) message.obj;
                        NetworkRegistrationInfo registrationStateFromResult = CellularNetworkServiceProvider.this.getRegistrationStateFromResult(asyncResult.result, i2 == 1 ? 1 : 2);
                        try {
                            networkServiceCallback.onRequestNetworkRegistrationInfoComplete((asyncResult.exception != null || registrationStateFromResult == null) ? 5 : 0, registrationStateFromResult);
                        } catch (Exception e) {
                            CellularNetworkService.loge("Exception: " + e);
                        }
                    }
                }
            };
            this.mHandler = handler;
            phone.mCi.registerForNetworkStateChanged(handler, 3, null);
        }

        private List<Integer> getAvailableServices(int i, int i2, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (z) {
                arrayList.add(5);
            } else if (i == 5 || i == 1) {
                if (i2 == 2) {
                    arrayList.add(2);
                } else if (i2 == 1) {
                    arrayList.add(1);
                    arrayList.add(3);
                    arrayList.add(4);
                }
            }
            return arrayList;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public NetworkRegistrationInfo getRegistrationStateFromResult(Object obj, int i) {
            if (obj == null) {
                return null;
            }
            if (i == 1) {
                return createRegistrationStateFromVoiceRegState(obj);
            }
            if (i == 2) {
                return createRegistrationStateFromDataRegState(obj);
            }
            return null;
        }

        private String getPlmnFromCellIdentity(CellIdentity cellIdentity) {
            if (cellIdentity == null || (cellIdentity instanceof CellIdentityCdma)) {
                return PhoneConfigurationManager.SSSS;
            }
            String mccString = cellIdentity.getMccString();
            String mncString = cellIdentity.getMncString();
            if (TextUtils.isEmpty(mccString) || TextUtils.isEmpty(mncString)) {
                return PhoneConfigurationManager.SSSS;
            }
            return mccString + mncString;
        }

        private NetworkRegistrationInfo createRegistrationStateFromVoiceRegState(Object obj) {
            if (obj instanceof RegStateResult) {
                return getNetworkRegistrationInfoAidl(1, 1, (RegStateResult) obj);
            }
            if (obj instanceof android.hardware.radio.V1_6.RegStateResult) {
                return getNetworkRegistrationInfo1_6(1, 1, (android.hardware.radio.V1_6.RegStateResult) obj);
            }
            if (obj instanceof android.hardware.radio.V1_5.RegStateResult) {
                return getNetworkRegistrationInfo(1, 1, (android.hardware.radio.V1_5.RegStateResult) obj);
            }
            if (obj instanceof VoiceRegStateResult) {
                VoiceRegStateResult voiceRegStateResult = (VoiceRegStateResult) obj;
                int regStateFromHalRegState = getRegStateFromHalRegState(voiceRegStateResult.regState);
                int rilRadioTechnologyToNetworkType = ServiceState.rilRadioTechnologyToNetworkType(voiceRegStateResult.rat);
                int i = voiceRegStateResult.reasonForDenial;
                boolean isEmergencyOnly = isEmergencyOnly(voiceRegStateResult.regState);
                boolean z = voiceRegStateResult.cssSupported;
                int i2 = voiceRegStateResult.roamingIndicator;
                int i3 = voiceRegStateResult.systemIsInPrl;
                int i4 = voiceRegStateResult.defaultRoamingIndicator;
                List<Integer> availableServices = getAvailableServices(regStateFromHalRegState, 1, isEmergencyOnly);
                CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(voiceRegStateResult.cellIdentity);
                return new NetworkRegistrationInfo(1, 1, regStateFromHalRegState, rilRadioTechnologyToNetworkType, i, isEmergencyOnly, availableServices, convertHalCellIdentity, getPlmnFromCellIdentity(convertHalCellIdentity), z, i2, i3, i4);
            } else if (obj instanceof android.hardware.radio.V1_2.VoiceRegStateResult) {
                android.hardware.radio.V1_2.VoiceRegStateResult voiceRegStateResult2 = (android.hardware.radio.V1_2.VoiceRegStateResult) obj;
                int regStateFromHalRegState2 = getRegStateFromHalRegState(voiceRegStateResult2.regState);
                int rilRadioTechnologyToNetworkType2 = ServiceState.rilRadioTechnologyToNetworkType(voiceRegStateResult2.rat);
                int i5 = voiceRegStateResult2.reasonForDenial;
                boolean isEmergencyOnly2 = isEmergencyOnly(voiceRegStateResult2.regState);
                boolean z2 = voiceRegStateResult2.cssSupported;
                int i6 = voiceRegStateResult2.roamingIndicator;
                int i7 = voiceRegStateResult2.systemIsInPrl;
                int i8 = voiceRegStateResult2.defaultRoamingIndicator;
                List<Integer> availableServices2 = getAvailableServices(regStateFromHalRegState2, 1, isEmergencyOnly2);
                CellIdentity convertHalCellIdentity2 = RILUtils.convertHalCellIdentity(voiceRegStateResult2.cellIdentity);
                return new NetworkRegistrationInfo(1, 1, regStateFromHalRegState2, rilRadioTechnologyToNetworkType2, i5, isEmergencyOnly2, availableServices2, convertHalCellIdentity2, getPlmnFromCellIdentity(convertHalCellIdentity2), z2, i6, i7, i8);
            } else {
                return null;
            }
        }

        private NetworkRegistrationInfo createRegistrationStateFromDataRegState(Object obj) {
            boolean isEmergencyOnly;
            LteVopsSupportInfo lteVopsSupportInfo;
            LteVopsSupportInfo lteVopsSupportInfo2;
            int i;
            boolean z;
            int i2;
            CellIdentity cellIdentity;
            int i3;
            int i4;
            boolean z2;
            boolean z3;
            int regStateFromHalRegState;
            int rilRadioTechnologyToNetworkType;
            int i5;
            int i6;
            LteVopsSupportInfo lteVopsSupportInfo3 = new LteVopsSupportInfo(1, 1);
            if (obj instanceof RegStateResult) {
                return getNetworkRegistrationInfoAidl(2, 1, (RegStateResult) obj);
            }
            if (obj instanceof android.hardware.radio.V1_6.RegStateResult) {
                return getNetworkRegistrationInfo1_6(2, 1, (android.hardware.radio.V1_6.RegStateResult) obj);
            }
            if (obj instanceof android.hardware.radio.V1_5.RegStateResult) {
                return getNetworkRegistrationInfo(2, 1, (android.hardware.radio.V1_5.RegStateResult) obj);
            }
            if (obj instanceof DataRegStateResult) {
                DataRegStateResult dataRegStateResult = (DataRegStateResult) obj;
                regStateFromHalRegState = getRegStateFromHalRegState(dataRegStateResult.regState);
                rilRadioTechnologyToNetworkType = ServiceState.rilRadioTechnologyToNetworkType(dataRegStateResult.rat);
                i5 = dataRegStateResult.reasonDataDenied;
                isEmergencyOnly = isEmergencyOnly(dataRegStateResult.regState);
                i6 = dataRegStateResult.maxDataCalls;
                cellIdentity = RILUtils.convertHalCellIdentity(dataRegStateResult.cellIdentity);
            } else if (obj instanceof android.hardware.radio.V1_2.DataRegStateResult) {
                android.hardware.radio.V1_2.DataRegStateResult dataRegStateResult2 = (android.hardware.radio.V1_2.DataRegStateResult) obj;
                regStateFromHalRegState = getRegStateFromHalRegState(dataRegStateResult2.regState);
                rilRadioTechnologyToNetworkType = ServiceState.rilRadioTechnologyToNetworkType(dataRegStateResult2.rat);
                i5 = dataRegStateResult2.reasonDataDenied;
                isEmergencyOnly = isEmergencyOnly(dataRegStateResult2.regState);
                i6 = dataRegStateResult2.maxDataCalls;
                cellIdentity = RILUtils.convertHalCellIdentity(dataRegStateResult2.cellIdentity);
            } else if (obj instanceof android.hardware.radio.V1_4.DataRegStateResult) {
                android.hardware.radio.V1_4.DataRegStateResult dataRegStateResult3 = (android.hardware.radio.V1_4.DataRegStateResult) obj;
                int regStateFromHalRegState2 = getRegStateFromHalRegState(dataRegStateResult3.base.regState);
                int rilRadioTechnologyToNetworkType2 = ServiceState.rilRadioTechnologyToNetworkType(dataRegStateResult3.base.rat);
                android.hardware.radio.V1_2.DataRegStateResult dataRegStateResult4 = dataRegStateResult3.base;
                int i7 = dataRegStateResult4.reasonDataDenied;
                isEmergencyOnly = isEmergencyOnly(dataRegStateResult4.regState);
                android.hardware.radio.V1_2.DataRegStateResult dataRegStateResult5 = dataRegStateResult3.base;
                int i8 = dataRegStateResult5.maxDataCalls;
                CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(dataRegStateResult5.cellIdentity);
                NrIndicators nrIndicators = dataRegStateResult3.nrIndicators;
                if (dataRegStateResult3.vopsInfo.getDiscriminator() == 1 && ServiceState.rilRadioTechnologyToAccessNetworkType(dataRegStateResult3.base.rat) == 3) {
                    LteVopsInfo lteVopsInfo = dataRegStateResult3.vopsInfo.lteVopsInfo();
                    lteVopsSupportInfo = convertHalLteVopsSupportInfo(lteVopsInfo.isVopsSupported, lteVopsInfo.isEmcBearerSupported);
                } else {
                    lteVopsSupportInfo = new LteVopsSupportInfo(1, 1);
                }
                boolean z4 = nrIndicators.isEndcAvailable;
                boolean z5 = nrIndicators.isNrAvailable;
                lteVopsSupportInfo2 = lteVopsSupportInfo;
                i = regStateFromHalRegState2;
                z = z4;
                i2 = rilRadioTechnologyToNetworkType2;
                cellIdentity = convertHalCellIdentity;
                i3 = i7;
                i4 = i8;
                z2 = nrIndicators.isDcNrRestricted;
                z3 = z5;
                return new NetworkRegistrationInfo(2, 1, i, i2, i3, isEmergencyOnly, getAvailableServices(i, 2, isEmergencyOnly), cellIdentity, getPlmnFromCellIdentity(cellIdentity), i4, z2, z3, z, lteVopsSupportInfo2);
            } else {
                CellularNetworkService.loge("Unknown type of DataRegStateResult " + obj);
                return null;
            }
            lteVopsSupportInfo2 = lteVopsSupportInfo3;
            i = regStateFromHalRegState;
            i2 = rilRadioTechnologyToNetworkType;
            z2 = false;
            z3 = false;
            z = false;
            i3 = i5;
            i4 = i6;
            return new NetworkRegistrationInfo(2, 1, i, i2, i3, isEmergencyOnly, getAvailableServices(i, 2, isEmergencyOnly), cellIdentity, getPlmnFromCellIdentity(cellIdentity), i4, z2, z3, z, lteVopsSupportInfo2);
        }

        private NetworkRegistrationInfo getNetworkRegistrationInfo(int i, int i2, android.hardware.radio.V1_5.RegStateResult regStateResult) {
            int i3;
            int i4;
            LteVopsSupportInfo lteVopsSupportInfo;
            int i5;
            boolean z;
            boolean z2;
            boolean z3;
            boolean z4;
            int regStateFromHalRegState = getRegStateFromHalRegState(regStateResult.regState);
            boolean isEmergencyOnly = isEmergencyOnly(regStateResult.regState);
            List<Integer> availableServices = getAvailableServices(regStateFromHalRegState, i, isEmergencyOnly);
            CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(regStateResult.cellIdentity);
            String str = regStateResult.registeredPlmn;
            int i6 = regStateResult.reasonForDenial;
            int networkTypeForCellIdentity = CellularNetworkService.getNetworkTypeForCellIdentity(ServiceState.rilRadioTechnologyToNetworkType(regStateResult.rat), convertHalCellIdentity, this.mPhone.getCarrierId());
            LteVopsSupportInfo lteVopsSupportInfo2 = new LteVopsSupportInfo(1, 1);
            byte discriminator = regStateResult.accessTechnologySpecificInfo.getDiscriminator();
            if (discriminator == 1) {
                RegStateResult.AccessTechnologySpecificInfo.Cdma2000RegistrationInfo cdmaInfo = regStateResult.accessTechnologySpecificInfo.cdmaInfo();
                boolean z5 = cdmaInfo.cssSupported;
                i3 = cdmaInfo.roamingIndicator;
                int i7 = cdmaInfo.systemIsInPrl;
                i4 = cdmaInfo.defaultRoamingIndicator;
                lteVopsSupportInfo = lteVopsSupportInfo2;
                i5 = i7;
                z = false;
                z2 = false;
                z3 = false;
                z4 = z5;
            } else if (discriminator == 2) {
                RegStateResult.AccessTechnologySpecificInfo.EutranRegistrationInfo eutranInfo = regStateResult.accessTechnologySpecificInfo.eutranInfo();
                NrIndicators nrIndicators = eutranInfo.nrIndicators;
                boolean z6 = nrIndicators.isDcNrRestricted;
                z2 = nrIndicators.isNrAvailable;
                boolean z7 = nrIndicators.isEndcAvailable;
                LteVopsInfo lteVopsInfo = eutranInfo.lteVopsInfo;
                lteVopsSupportInfo = convertHalLteVopsSupportInfo(lteVopsInfo.isVopsSupported, lteVopsInfo.isEmcBearerSupported);
                z3 = z7;
                z = z6;
                z4 = false;
                i3 = 0;
                i5 = 0;
                i4 = 0;
            } else {
                CellularNetworkService.log("No access tech specific info passes for RegStateResult");
                lteVopsSupportInfo = lteVopsSupportInfo2;
                z4 = false;
                i3 = 0;
                z = false;
                z2 = false;
                i5 = 0;
                i4 = 0;
                z3 = false;
            }
            if (i == 1) {
                return new NetworkRegistrationInfo(i, i2, regStateFromHalRegState, networkTypeForCellIdentity, i6, isEmergencyOnly, availableServices, convertHalCellIdentity, str, z4, i3, i5, i4);
            }
            if (i != 2) {
                CellularNetworkService.loge("Unknown domain passed to CellularNetworkService= " + i);
            }
            return new NetworkRegistrationInfo(i, i2, regStateFromHalRegState, networkTypeForCellIdentity, i6, isEmergencyOnly, availableServices, convertHalCellIdentity, str, 16, z, z2, z3, lteVopsSupportInfo);
        }

        private NetworkRegistrationInfo getNetworkRegistrationInfoAidl(int i, int i2, android.hardware.radio.network.RegStateResult regStateResult) {
            boolean z;
            int i3;
            int i4;
            int i5;
            LteVopsSupportInfo lteVopsSupportInfo;
            boolean z2;
            boolean z3;
            boolean z4;
            int i6;
            byte b;
            int regStateFromHalRegState = getRegStateFromHalRegState(regStateResult.regState);
            boolean isEmergencyOnly = isEmergencyOnly(regStateResult.regState);
            List<Integer> availableServices = getAvailableServices(regStateFromHalRegState, i, isEmergencyOnly);
            CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(regStateResult.cellIdentity);
            String str = regStateResult.registeredPlmn;
            int i7 = regStateResult.reasonForDenial;
            if (regStateFromHalRegState == 3 && i7 == 0) {
                AnomalyReporter.reportAnomaly(UUID.fromString("62ed270f-e139-418a-a427-8bcc1bca8f21"), "RIL Missing Reg Fail Reason", this.mPhone.getCarrierId());
            }
            int rilRadioTechnologyToNetworkType = ServiceState.rilRadioTechnologyToNetworkType(regStateResult.rat);
            if (rilRadioTechnologyToNetworkType == 19) {
                rilRadioTechnologyToNetworkType = 13;
            }
            AccessTechnologySpecificInfo accessTechnologySpecificInfo = regStateResult.accessTechnologySpecificInfo;
            int tag = accessTechnologySpecificInfo.getTag();
            LteVopsSupportInfo lteVopsSupportInfo2 = null;
            if (tag == 1) {
                boolean z5 = accessTechnologySpecificInfo.getCdmaInfo().cssSupported;
                int i8 = accessTechnologySpecificInfo.getCdmaInfo().roamingIndicator;
                int i9 = accessTechnologySpecificInfo.getCdmaInfo().systemIsInPrl;
                z = z5;
                i3 = accessTechnologySpecificInfo.getCdmaInfo().defaultRoamingIndicator;
                i4 = i8;
                i5 = i9;
                lteVopsSupportInfo = null;
                z2 = false;
                z3 = false;
                z4 = false;
                i6 = 0;
                b = 0;
            } else if (tag != 2) {
                if (tag == 3) {
                    lteVopsSupportInfo2 = new NrVopsSupportInfo(accessTechnologySpecificInfo.getNgranNrVopsInfo().vopsSupported, accessTechnologySpecificInfo.getNgranNrVopsInfo().emcSupported, accessTechnologySpecificInfo.getNgranNrVopsInfo().emfSupported);
                } else if (tag == 4) {
                    z = accessTechnologySpecificInfo.getGeranDtmSupported();
                    lteVopsSupportInfo = null;
                    i3 = 0;
                    i5 = 0;
                    z2 = false;
                    z3 = false;
                    z4 = false;
                    i6 = 0;
                    b = 0;
                    i4 = 0;
                } else {
                    CellularNetworkService.log("No access tech specific info passes for RegStateResult");
                }
                lteVopsSupportInfo = lteVopsSupportInfo2;
                i3 = 0;
                i5 = 0;
                z2 = false;
                z3 = false;
                z4 = false;
                i6 = 0;
                z = false;
                b = 0;
                i4 = 0;
            } else {
                z4 = accessTechnologySpecificInfo.getEutranInfo().nrIndicators.isDcNrRestricted;
                boolean z6 = accessTechnologySpecificInfo.getEutranInfo().nrIndicators.isNrAvailable;
                boolean z7 = accessTechnologySpecificInfo.getEutranInfo().nrIndicators.isEndcAvailable;
                LteVopsSupportInfo convertHalLteVopsSupportInfo = convertHalLteVopsSupportInfo(accessTechnologySpecificInfo.getEutranInfo().lteVopsInfo.isVopsSupported, accessTechnologySpecificInfo.getEutranInfo().lteVopsInfo.isEmcBearerSupported);
                i3 = 0;
                i5 = 0;
                z = false;
                i4 = 0;
                lteVopsSupportInfo = convertHalLteVopsSupportInfo;
                z2 = z7;
                b = accessTechnologySpecificInfo.getEutranInfo().lteAttachResultType;
                i6 = accessTechnologySpecificInfo.getEutranInfo().extraInfo;
                z3 = z6;
            }
            if (i == 1) {
                return new NetworkRegistrationInfo(i, i2, regStateFromHalRegState, rilRadioTechnologyToNetworkType, i7, isEmergencyOnly, availableServices, convertHalCellIdentity, str, z, i4, i5, i3);
            }
            if (i != 2) {
                CellularNetworkService.loge("Unknown domain passed to CellularNetworkService= " + i);
            }
            return new NetworkRegistrationInfo.Builder().setDomain(i).setTransportType(i2).setRegistrationState(regStateFromHalRegState).setAccessNetworkTechnology(rilRadioTechnologyToNetworkType).setRejectCause(i7).setEmergencyOnly(isEmergencyOnly).setAvailableServices(availableServices).setCellIdentity(convertHalCellIdentity).setRegisteredPlmn(str).setDataSpecificInfo(new DataSpecificRegistrationInfo.Builder(16).setDcNrRestricted(z4).setNrAvailable(z3).setEnDcAvailable(z2).setVopsSupportInfo(lteVopsSupportInfo).setLteAttachResultType(b).setLteAttachExtraInfo(i6).build()).build();
        }

        private NetworkRegistrationInfo getNetworkRegistrationInfo1_6(int i, int i2, android.hardware.radio.V1_6.RegStateResult regStateResult) {
            int i3;
            LteVopsSupportInfo lteVopsSupportInfo;
            boolean z;
            boolean z2;
            boolean z3;
            int i4;
            int i5;
            boolean z4;
            int regStateFromHalRegState = getRegStateFromHalRegState(regStateResult.regState);
            boolean isEmergencyOnly = isEmergencyOnly(regStateResult.regState);
            List<Integer> availableServices = getAvailableServices(regStateFromHalRegState, i, isEmergencyOnly);
            CellIdentity convertHalCellIdentity = RILUtils.convertHalCellIdentity(regStateResult.cellIdentity);
            String str = regStateResult.registeredPlmn;
            int i6 = regStateResult.reasonForDenial;
            int networkTypeForCellIdentity = CellularNetworkService.getNetworkTypeForCellIdentity(ServiceState.rilRadioTechnologyToNetworkType(regStateResult.rat), convertHalCellIdentity, this.mPhone.getCarrierId());
            if (regStateFromHalRegState == 3 && i6 == 0) {
                AnomalyReporter.reportAnomaly(UUID.fromString("62ed270f-e139-418a-a427-8bcc1bca8f21"), "RIL Missing Reg Fail Reason", this.mPhone.getCarrierId());
            }
            RegStateResult.AccessTechnologySpecificInfo accessTechnologySpecificInfo = regStateResult.accessTechnologySpecificInfo;
            byte discriminator = accessTechnologySpecificInfo.getDiscriminator();
            if (discriminator == 1) {
                boolean z5 = accessTechnologySpecificInfo.cdmaInfo().cssSupported;
                int i7 = accessTechnologySpecificInfo.cdmaInfo().roamingIndicator;
                int i8 = accessTechnologySpecificInfo.cdmaInfo().systemIsInPrl;
                i3 = accessTechnologySpecificInfo.cdmaInfo().defaultRoamingIndicator;
                lteVopsSupportInfo = null;
                z = false;
                z2 = false;
                z3 = false;
                i4 = i8;
                i5 = i7;
                z4 = z5;
            } else if (discriminator != 2) {
                if (discriminator == 3) {
                    lteVopsSupportInfo = new NrVopsSupportInfo(accessTechnologySpecificInfo.ngranNrVopsInfo().vopsSupported, accessTechnologySpecificInfo.ngranNrVopsInfo().emcSupported, accessTechnologySpecificInfo.ngranNrVopsInfo().emfSupported);
                } else if (discriminator == 4) {
                    z4 = accessTechnologySpecificInfo.geranDtmSupported();
                    lteVopsSupportInfo = null;
                    i5 = 0;
                    z = false;
                    i4 = 0;
                    z2 = false;
                    i3 = 0;
                    z3 = false;
                } else {
                    CellularNetworkService.log("No access tech specific info passes for RegStateResult");
                    lteVopsSupportInfo = null;
                }
                z4 = false;
                i5 = 0;
                z = false;
                i4 = 0;
                z2 = false;
                i3 = 0;
                z3 = false;
            } else {
                boolean z6 = accessTechnologySpecificInfo.eutranInfo().nrIndicators.isDcNrRestricted;
                boolean z7 = accessTechnologySpecificInfo.eutranInfo().nrIndicators.isNrAvailable;
                boolean z8 = accessTechnologySpecificInfo.eutranInfo().nrIndicators.isEndcAvailable;
                lteVopsSupportInfo = convertHalLteVopsSupportInfo(accessTechnologySpecificInfo.eutranInfo().lteVopsInfo.isVopsSupported, accessTechnologySpecificInfo.eutranInfo().lteVopsInfo.isEmcBearerSupported);
                z = z6;
                z2 = z7;
                z3 = z8;
                z4 = false;
                i5 = 0;
                i4 = 0;
                i3 = 0;
            }
            if (i == 1) {
                return new NetworkRegistrationInfo(i, i2, regStateFromHalRegState, networkTypeForCellIdentity, i6, isEmergencyOnly, availableServices, convertHalCellIdentity, str, z4, i5, i4, i3);
            }
            if (i != 2) {
                CellularNetworkService.loge("Unknown domain passed to CellularNetworkService= " + i);
            }
            return new NetworkRegistrationInfo(i, i2, regStateFromHalRegState, networkTypeForCellIdentity, i6, isEmergencyOnly, availableServices, convertHalCellIdentity, str, 16, z, z2, z3, lteVopsSupportInfo);
        }

        private LteVopsSupportInfo convertHalLteVopsSupportInfo(boolean z, boolean z2) {
            return new LteVopsSupportInfo(z ? 2 : 3, z2 ? 2 : 3);
        }

        public void requestNetworkRegistrationInfo(int i, NetworkServiceCallback networkServiceCallback) {
            if (i == 1) {
                Message obtain = Message.obtain(this.mHandler, 1);
                this.mCallbackMap.put(obtain, networkServiceCallback);
                this.mPhone.mCi.getVoiceRegistrationState(obtain);
            } else if (i == 2) {
                Message obtain2 = Message.obtain(this.mHandler, 2);
                this.mCallbackMap.put(obtain2, networkServiceCallback);
                this.mPhone.mCi.getDataRegistrationState(obtain2);
            } else {
                CellularNetworkService.loge("requestNetworkRegistrationInfo invalid domain " + i);
                networkServiceCallback.onRequestNetworkRegistrationInfoComplete(2, (NetworkRegistrationInfo) null);
            }
        }

        public void close() {
            this.mCallbackMap.clear();
            this.mPhone.mCi.unregisterForNetworkStateChanged(this.mHandler);
        }
    }

    @VisibleForTesting
    public static int getNetworkTypeForCellIdentity(int i, CellIdentity cellIdentity, int i2) {
        if (cellIdentity == null) {
            if (i != 0) {
                AnomalyReporter.reportAnomaly(UUID.fromString("e67ea4ef-7251-4a69-a063-22c47fc58743"), "RIL Unexpected NetworkType", i2);
                if (Build.isDebuggable()) {
                    logw("Updating incorrect network type from " + TelephonyManager.getNetworkTypeName(i) + " to UNKNOWN");
                    return 0;
                }
                for (List<Integer> list : sNetworkTypes.values()) {
                    if (list.contains(Integer.valueOf(i))) {
                        return i;
                    }
                }
            }
            return 0;
        } else if (i == 18) {
            AnomalyReporter.reportAnomaly(UUID.fromString("07dfa183-b2e7-42b7-98a1-dd5ae2abdd4f"), "RIL Reported IWLAN", i2);
            if (Build.isDebuggable()) {
                Map<Class<? extends CellIdentity>, List<Integer>> map = sNetworkTypes;
                if (map.containsKey(cellIdentity.getClass())) {
                    int intValue = map.get(cellIdentity.getClass()).get(0).intValue();
                    logw("Updating incorrect network type from IWLAN to " + intValue);
                    return intValue;
                }
                logw("Updating incorrect network type from IWLAN to UNKNOWN");
                return 0;
            }
            return i;
        } else {
            Map<Class<? extends CellIdentity>, List<Integer>> map2 = sNetworkTypes;
            if (!map2.containsKey(cellIdentity.getClass())) {
                AnomalyReporter.reportAnomaly(UUID.fromString("469858cf-46e5-416e-bc11-5e7970917857"), "RIL Unknown CellIdentity", i2);
                return i;
            }
            List<Integer> list2 = map2.get(cellIdentity.getClass());
            if (list2.contains(Integer.valueOf(i))) {
                return i;
            }
            AnomalyReporter.reportAnomaly(UUID.fromString("2fb634fa-cab3-44d2-bbe8-c7689b0f3e31"), "RIL Mismatched NetworkType", i2);
            logw("Updating incorrect network type from " + TelephonyManager.getNetworkTypeName(i) + " to " + TelephonyManager.getNetworkTypeName(list2.get(0).intValue()));
            return list2.get(0).intValue();
        }
    }

    public NetworkService.NetworkServiceProvider onCreateNetworkServiceProvider(int i) {
        if (!SubscriptionManager.isValidSlotIndex(i)) {
            loge("Tried to Cellular network service with invalid slotId " + i);
            return null;
        }
        return new CellularNetworkServiceProvider(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
        Rlog.d(TAG, str);
    }

    private static void logw(String str) {
        Rlog.w(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Rlog.e(TAG, str);
    }
}
