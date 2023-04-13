package com.android.internal.telephony.subscription;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;
import android.telephony.SubscriptionManager;
import android.telephony.UiccAccessRule;
import android.text.TextUtils;
import android.util.Base64;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.AndroidUtilIndentingPrintWriter;
import com.android.internal.telephony.IccProvider;
import com.android.internal.telephony.InboundSmsHandler;
import com.android.internal.telephony.LocalLog;
import com.android.internal.telephony.subscription.SubscriptionInfoInternal;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.util.function.TriConsumer;
import com.android.telephony.Rlog;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SubscriptionDatabaseManager extends Handler {
    @GuardedBy({"mReadWriteLock"})
    private final Map<Integer, SubscriptionInfoInternal> mAllSubscriptionInfoInternalCache;
    private final boolean mAsyncMode;
    private final SubscriptionDatabaseManagerCallback mCallback;
    private final Context mContext;
    @GuardedBy({"this"})
    private boolean mDatabaseInitialized;
    private final LocalLog mLocalLog;
    private final ReadWriteLock mReadWriteLock;
    private final UiccController mUiccController;
    private static final Map<String, Function<SubscriptionInfoInternal, ?>> SUBSCRIPTION_GET_METHOD_MAP = Map.ofEntries(new AbstractMap.SimpleImmutableEntry("_id", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda55
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getSubscriptionId());
        }
    }), new AbstractMap.SimpleImmutableEntry("icc_id", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda66
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getIccId();
        }
    }), new AbstractMap.SimpleImmutableEntry("sim_id", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda77
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getSimSlotIndex());
        }
    }), new AbstractMap.SimpleImmutableEntry("display_name", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda88
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getDisplayName();
        }
    }), new AbstractMap.SimpleImmutableEntry("carrier_name", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda99
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getCarrierName();
        }
    }), new AbstractMap.SimpleImmutableEntry("name_source", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda110
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getDisplayNameSource());
        }
    }), new AbstractMap.SimpleImmutableEntry("color", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda121
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getIconTint());
        }
    }), new AbstractMap.SimpleImmutableEntry(IccProvider.STR_NUMBER, new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda132
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getNumber();
        }
    }), new AbstractMap.SimpleImmutableEntry("data_roaming", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda143
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getDataRoaming());
        }
    }), new AbstractMap.SimpleImmutableEntry("mcc_string", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda151
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getMcc();
        }
    }), new AbstractMap.SimpleImmutableEntry("mnc_string", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda56
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getMnc();
        }
    }), new AbstractMap.SimpleImmutableEntry("ehplmns", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda57
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getEhplmns();
        }
    }), new AbstractMap.SimpleImmutableEntry("hplmns", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda58
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getHplmns();
        }
    }), new AbstractMap.SimpleImmutableEntry("is_embedded", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda59
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getEmbedded());
        }
    }), new AbstractMap.SimpleImmutableEntry("card_id", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda60
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getCardString();
        }
    }), new AbstractMap.SimpleImmutableEntry("access_rules", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda61
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getNativeAccessRules();
        }
    }), new AbstractMap.SimpleImmutableEntry("access_rules_from_carrier_configs", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda62
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getCarrierConfigAccessRules();
        }
    }), new AbstractMap.SimpleImmutableEntry("is_removable", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda63
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getRemovableEmbedded());
        }
    }), new AbstractMap.SimpleImmutableEntry("volte_vt_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda64
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getEnhanced4GModeEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("vt_ims_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda65
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getVideoTelephonyEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda67
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getWifiCallingEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_mode", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda68
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getWifiCallingMode());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_roaming_mode", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda69
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getWifiCallingModeForRoaming());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_roaming_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda70
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getWifiCallingEnabledForRoaming());
        }
    }), new AbstractMap.SimpleImmutableEntry("is_opportunistic", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda71
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getOpportunistic());
        }
    }), new AbstractMap.SimpleImmutableEntry("group_uuid", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda72
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getGroupUuid();
        }
    }), new AbstractMap.SimpleImmutableEntry("iso_country_code", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda73
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getCountryIso();
        }
    }), new AbstractMap.SimpleImmutableEntry("carrier_id", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda74
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getCarrierId());
        }
    }), new AbstractMap.SimpleImmutableEntry("profile_class", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda75
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getProfileClass());
        }
    }), new AbstractMap.SimpleImmutableEntry("subscription_type", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda76
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getSubscriptionType());
        }
    }), new AbstractMap.SimpleImmutableEntry("group_owner", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda78
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getGroupOwner();
        }
    }), new AbstractMap.SimpleImmutableEntry("enabled_mobile_data_policies", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda79
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getEnabledMobileDataPolicies();
        }
    }), new AbstractMap.SimpleImmutableEntry("imsi", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda80
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getImsi();
        }
    }), new AbstractMap.SimpleImmutableEntry("uicc_applications_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda81
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getUiccApplicationsEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("ims_rcs_uce_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda82
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getRcsUceEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("cross_sim_calling_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda83
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getCrossSimCallingEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("rcs_config", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda84
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getRcsConfig();
        }
    }), new AbstractMap.SimpleImmutableEntry("allowed_network_types_for_reasons", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda85
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getAllowedNetworkTypesForReasons();
        }
    }), new AbstractMap.SimpleImmutableEntry("d2d_sharing_status", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda86
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getDeviceToDeviceStatusSharingPreference());
        }
    }), new AbstractMap.SimpleImmutableEntry("voims_opt_in_status", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda87
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getVoImsOptInEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("d2d_sharing_contacts", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda89
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getDeviceToDeviceStatusSharingContacts();
        }
    }), new AbstractMap.SimpleImmutableEntry("nr_advanced_calling_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda90
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getNrAdvancedCallingEnabled());
        }
    }), new AbstractMap.SimpleImmutableEntry("phone_number_source_carrier", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda91
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getNumberFromCarrier();
        }
    }), new AbstractMap.SimpleImmutableEntry("phone_number_source_ims", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda92
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return ((SubscriptionInfoInternal) obj).getNumberFromIms();
        }
    }), new AbstractMap.SimpleImmutableEntry("port_index", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda93
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getPortIndex());
        }
    }), new AbstractMap.SimpleImmutableEntry("usage_setting", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda94
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getUsageSetting());
        }
    }), new AbstractMap.SimpleImmutableEntry("tp_message_ref", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda95
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getLastUsedTPMessageReference());
        }
    }), new AbstractMap.SimpleImmutableEntry("user_handle", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda96
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getUserId());
        }
    }), new AbstractMap.SimpleImmutableEntry("satellite_enabled", new Function() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda97
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Integer.valueOf(((SubscriptionInfoInternal) obj).getSatelliteEnabled());
        }
    }));
    private static final Map<String, TriConsumer<SubscriptionDatabaseManager, Integer, Integer>> SUBSCRIPTION_SET_INTEGER_METHOD_MAP = Map.ofEntries(new AbstractMap.SimpleImmutableEntry("sim_id", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda98
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setSimSlotIndex(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("name_source", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda100
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setDisplayNameSource(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("color", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda101
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setIconTint(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("data_roaming", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda102
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setDataRoaming(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("is_embedded", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda103
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setEmbedded(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("is_removable", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda104
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setRemovableEmbedded(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("volte_vt_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda105
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setEnhanced4GModeEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("vt_ims_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda106
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setVideoTelephonyEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda107
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setWifiCallingEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_mode", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda108
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setWifiCallingMode(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_roaming_mode", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda109
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setWifiCallingModeForRoaming(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("wfc_ims_roaming_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda111
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setWifiCallingEnabledForRoaming(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("is_opportunistic", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda112
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setOpportunistic(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("carrier_id", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda113
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setCarrierId(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("profile_class", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda114
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setProfileClass(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("subscription_type", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda115
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setSubscriptionType(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("uicc_applications_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda116
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setUiccApplicationsEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("ims_rcs_uce_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda117
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setRcsUceEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("cross_sim_calling_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda118
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setCrossSimCallingEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("d2d_sharing_status", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda119
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setDeviceToDeviceStatusSharingPreference(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("voims_opt_in_status", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda120
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setVoImsOptInEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("nr_advanced_calling_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda122
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setNrAdvancedCallingEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("port_index", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda123
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setPortIndex(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("usage_setting", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda124
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setUsageSetting(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("tp_message_ref", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda125
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setLastUsedTPMessageReference(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("user_handle", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda126
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setUserId(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }), new AbstractMap.SimpleImmutableEntry("satellite_enabled", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda127
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setSatelliteEnabled(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
        }
    }));
    private static final Map<String, TriConsumer<SubscriptionDatabaseManager, Integer, String>> SUBSCRIPTION_SET_STRING_METHOD_MAP = Map.ofEntries(new AbstractMap.SimpleImmutableEntry("icc_id", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda128
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setIccId(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("display_name", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda129
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setDisplayName(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("carrier_name", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda130
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setCarrierName(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry(IccProvider.STR_NUMBER, new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda131
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setNumber(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("mcc_string", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda133
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setMcc(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("mnc_string", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda134
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setMnc(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("ehplmns", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda135
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setEhplmns(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("hplmns", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda136
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setHplmns(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("card_id", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda137
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setCardString(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("group_uuid", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda138
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setGroupUuid(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("iso_country_code", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda139
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setCountryIso(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("group_owner", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda140
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setGroupOwner(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("enabled_mobile_data_policies", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda141
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setEnabledMobileDataPolicies(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("imsi", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda142
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setImsi(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("allowed_network_types_for_reasons", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda144
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setAllowedNetworkTypesForReasons(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("d2d_sharing_contacts", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda145
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setDeviceToDeviceStatusSharingContacts(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("phone_number_source_carrier", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda146
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setNumberFromCarrier(((Integer) obj2).intValue(), (String) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("phone_number_source_ims", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda147
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setNumberFromIms(((Integer) obj2).intValue(), (String) obj3);
        }
    }));
    private static final Map<String, TriConsumer<SubscriptionDatabaseManager, Integer, byte[]>> SUBSCRIPTION_SET_BYTE_ARRAY_METHOD_MAP = Map.ofEntries(new AbstractMap.SimpleImmutableEntry("access_rules", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda148
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setNativeAccessRules(((Integer) obj2).intValue(), (byte[]) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("access_rules_from_carrier_configs", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda149
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setCarrierConfigAccessRules(((Integer) obj2).intValue(), (byte[]) obj3);
        }
    }), new AbstractMap.SimpleImmutableEntry("rcs_config", new TriConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda150
        public final void accept(Object obj, Object obj2, Object obj3) {
            ((SubscriptionDatabaseManager) obj).setRcsConfig(((Integer) obj2).intValue(), (byte[]) obj3);
        }
    }));
    private static final Set<String> GROUP_SHARING_COLUMNS = Set.of((Object[]) new String[]{"display_name", "name_source", "color", "data_roaming", "volte_vt_enabled", "vt_ims_enabled", "wfc_ims_enabled", "wfc_ims_mode", "wfc_ims_roaming_mode", "wfc_ims_roaming_enabled", "enabled_mobile_data_policies", "uicc_applications_enabled", "ims_rcs_uce_enabled", "cross_sim_calling_enabled", "rcs_config", "d2d_sharing_status", "voims_opt_in_status", "d2d_sharing_contacts", "nr_advanced_calling_enabled", "user_handle"});
    private static final Set<String> DEPRECATED_DATABASE_COLUMNS = Set.of((Object[]) new String[]{"display_number_format", "mcc", "mnc", "sim_provisioning_status", "enable_cmas_extreme_threat_alerts", "enable_cmas_severe_threat_alerts", "enable_cmas_amber_alerts", "enable_emergency_alerts", "alert_sound_duration", "alert_reminder_interval", "enable_alert_vibrate", "enable_alert_speech", "enable_etws_test_alerts", "enable_channel_50_alerts", "enable_cmas_test_alerts", "show_cmas_opt_out_dialog", "is_metered", "data_enabled_override_rules", "allowed_network_types"});

    private void logv(String str) {
    }

    /* loaded from: classes.dex */
    public static abstract class SubscriptionDatabaseManagerCallback {
        private final Executor mExecutor;

        public abstract void onInitialized();

        public abstract void onSubscriptionChanged(int i);

        public abstract void onUiccApplicationsEnabled(int i);

        public SubscriptionDatabaseManagerCallback(Executor executor) {
            this.mExecutor = executor;
        }

        @VisibleForTesting
        public Executor getExecutor() {
            return this.mExecutor;
        }

        public void invokeFromExecutor(Runnable runnable) {
            this.mExecutor.execute(runnable);
        }
    }

    public SubscriptionDatabaseManager(Context context, Looper looper, SubscriptionDatabaseManagerCallback subscriptionDatabaseManagerCallback) {
        super(looper);
        this.mReadWriteLock = new ReentrantReadWriteLock();
        this.mLocalLog = new LocalLog(128);
        this.mAllSubscriptionInfoInternalCache = new HashMap(16);
        this.mDatabaseInitialized = false;
        log("Created SubscriptionDatabaseManager.");
        this.mContext = context;
        this.mCallback = subscriptionDatabaseManagerCallback;
        this.mUiccController = UiccController.getInstance();
        this.mAsyncMode = context.getResources().getBoolean(17891815);
        initializeDatabase();
    }

    private static Object getSubscriptionInfoFieldByColumnName(SubscriptionInfoInternal subscriptionInfoInternal, String str) {
        Map<String, Function<SubscriptionInfoInternal, ?>> map = SUBSCRIPTION_GET_METHOD_MAP;
        if (map.containsKey(str)) {
            return map.get(str).apply(subscriptionInfoInternal);
        }
        throw new IllegalArgumentException("Invalid column name " + str);
    }

    public Object getSubscriptionProperty(int i, String str) {
        SubscriptionInfoInternal subscriptionInfoInternal = getSubscriptionInfoInternal(i);
        if (subscriptionInfoInternal == null) {
            throw new IllegalArgumentException("getSubscriptionProperty: Invalid subId " + i + ", columnName=" + str);
        }
        return getSubscriptionInfoFieldByColumnName(subscriptionInfoInternal, str);
    }

    public void setSubscriptionProperty(int i, String str, Object obj) {
        byte[] bArr;
        int intValue;
        Map<String, TriConsumer<SubscriptionDatabaseManager, Integer, Integer>> map = SUBSCRIPTION_SET_INTEGER_METHOD_MAP;
        if (map.containsKey(str)) {
            if (obj instanceof String) {
                intValue = Integer.parseInt((String) obj);
            } else if (obj instanceof Integer) {
                intValue = ((Integer) obj).intValue();
            } else {
                throw new ClassCastException("columnName=" + str + ", cannot cast " + obj.getClass() + " to integer.");
            }
            map.get(str).accept(this, Integer.valueOf(i), Integer.valueOf(intValue));
            return;
        }
        Map<String, TriConsumer<SubscriptionDatabaseManager, Integer, String>> map2 = SUBSCRIPTION_SET_STRING_METHOD_MAP;
        if (map2.containsKey(str)) {
            map2.get(str).accept(this, Integer.valueOf(i), (String) obj);
            return;
        }
        Map<String, TriConsumer<SubscriptionDatabaseManager, Integer, byte[]>> map3 = SUBSCRIPTION_SET_BYTE_ARRAY_METHOD_MAP;
        if (map3.containsKey(str)) {
            if (obj instanceof String) {
                bArr = Base64.decode((String) obj, 0);
            } else if (obj instanceof byte[]) {
                bArr = (byte[]) obj;
            } else {
                throw new ClassCastException("columnName=" + str + ", cannot cast " + obj.getClass() + " to byte[].");
            }
            map3.get(str).accept(this, Integer.valueOf(i), bArr);
            return;
        }
        throw new IllegalArgumentException("Does not support set " + str + ".");
    }

    private ContentValues createDeltaContentValues(SubscriptionInfoInternal subscriptionInfoInternal, SubscriptionInfoInternal subscriptionInfoInternal2) {
        Object subscriptionInfoFieldByColumnName;
        ContentValues contentValues = new ContentValues();
        for (String str : Telephony.SimInfo.getAllColumns()) {
            if (!DEPRECATED_DATABASE_COLUMNS.contains(str) && !str.equals("_id") && (subscriptionInfoFieldByColumnName = getSubscriptionInfoFieldByColumnName(subscriptionInfoInternal2, str)) != null) {
                if (!Objects.equals(subscriptionInfoInternal != null ? getSubscriptionInfoFieldByColumnName(subscriptionInfoInternal, str) : null, subscriptionInfoFieldByColumnName)) {
                    contentValues.putObject(str, subscriptionInfoFieldByColumnName);
                }
            }
        }
        return contentValues;
    }

    private int insertNewRecordIntoDatabaseSync(ContentValues contentValues) {
        Objects.requireNonNull(contentValues);
        Uri insert = this.mContext.getContentResolver().insert(Telephony.SimInfo.CONTENT_URI, contentValues);
        if (insert != null && insert.getLastPathSegment() != null) {
            int parseInt = Integer.parseInt(insert.getLastPathSegment());
            if (SubscriptionManager.isValidSubscriptionId(parseInt)) {
                logv("insertNewRecordIntoDatabaseSync: contentValues=" + contentValues);
                logl("insertNewRecordIntoDatabaseSync: Successfully added subscription. subId=" + insert.getLastPathSegment());
                return parseInt;
            }
        }
        logel("insertNewRecordIntoDatabaseSync: Failed to insert subscription into database. contentValues=" + contentValues);
        return -1;
    }

    public int insertSubscriptionInfo(SubscriptionInfoInternal subscriptionInfoInternal) {
        Objects.requireNonNull(subscriptionInfoInternal);
        if (SubscriptionManager.isValidSubscriptionId(subscriptionInfoInternal.getSubscriptionId())) {
            throw new RuntimeException("insertSubscriptionInfo: Not a new subscription to insert. subInfo=" + subscriptionInfoInternal);
        }
        synchronized (this) {
            if (!this.mDatabaseInitialized) {
                throw new IllegalStateException("Database has not been initialized. Can't insert new record at this point.");
            }
        }
        this.mReadWriteLock.writeLock().lock();
        try {
            final int insertNewRecordIntoDatabaseSync = insertNewRecordIntoDatabaseSync(createDeltaContentValues(null, subscriptionInfoInternal));
            if (insertNewRecordIntoDatabaseSync > 0) {
                this.mAllSubscriptionInfoInternalCache.put(Integer.valueOf(insertNewRecordIntoDatabaseSync), new SubscriptionInfoInternal.Builder(subscriptionInfoInternal).setId(insertNewRecordIntoDatabaseSync).build());
            } else {
                logel("insertSubscriptionInfo: Failed to insert a new subscription. subInfo=" + subscriptionInfoInternal);
            }
            this.mReadWriteLock.writeLock().unlock();
            this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionDatabaseManager.this.lambda$insertSubscriptionInfo$0(insertNewRecordIntoDatabaseSync);
                }
            });
            return insertNewRecordIntoDatabaseSync;
        } catch (Throwable th) {
            this.mReadWriteLock.writeLock().unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$insertSubscriptionInfo$0(int i) {
        this.mCallback.onSubscriptionChanged(i);
    }

    public void removeSubscriptionInfo(final int i) {
        if (!this.mAllSubscriptionInfoInternalCache.containsKey(Integer.valueOf(i))) {
            throw new IllegalArgumentException("subId " + i + " is invalid.");
        }
        this.mReadWriteLock.writeLock().lock();
        try {
            if (this.mContext.getContentResolver().delete(Telephony.SimInfo.CONTENT_URI, InboundSmsHandler.SELECT_BY_ID, new String[]{Integer.toString(i)}) > 0) {
                this.mAllSubscriptionInfoInternalCache.remove(Integer.valueOf(i));
            } else {
                logel("Failed to remove subscription with subId=" + i);
            }
            this.mReadWriteLock.writeLock().unlock();
            this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionDatabaseManager.this.lambda$removeSubscriptionInfo$1(i);
                }
            });
        } catch (Throwable th) {
            this.mReadWriteLock.writeLock().unlock();
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeSubscriptionInfo$1(int i) {
        this.mCallback.onSubscriptionChanged(i);
    }

    private int updateDatabase(final int i, final ContentValues contentValues) {
        logv("updateDatabase: prepare to update sub " + i);
        synchronized (this) {
            if (!this.mDatabaseInitialized) {
                logel("updateDatabase: Database has not been initialized. Can't update database at this point. contentValues=" + contentValues);
                return 0;
            } else if (this.mAsyncMode) {
                post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda157
                    @Override // java.lang.Runnable
                    public final void run() {
                        SubscriptionDatabaseManager.this.lambda$updateDatabase$2(i, contentValues);
                    }
                });
                return 1;
            } else {
                logv("updateDatabase: sync updated subscription in the database. subId=" + i + ", contentValues= " + contentValues.getValues());
                return this.mContext.getContentResolver().update(Uri.withAppendedPath(Telephony.SimInfo.CONTENT_URI, String.valueOf(i)), contentValues, null, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateDatabase$2(int i, ContentValues contentValues) {
        this.mContext.getContentResolver().update(Uri.withAppendedPath(Telephony.SimInfo.CONTENT_URI, String.valueOf(i)), contentValues, null, null);
        logv("updateDatabase: async updated subscription in the database. subId=" + i + ", contentValues= " + contentValues.getValues());
    }

    private <T> void writeDatabaseAndCacheHelper(final int i, final String str, final T t, final BiFunction<SubscriptionInfoInternal.Builder, T, SubscriptionInfoInternal.Builder> biFunction) {
        final ContentValues contentValues = new ContentValues();
        this.mReadWriteLock.writeLock().lock();
        try {
            final SubscriptionInfoInternal subscriptionInfoInternal = this.mAllSubscriptionInfoInternalCache.get(Integer.valueOf(i));
            if (subscriptionInfoInternal == null) {
                logel("Subscription doesn't exist. subId=" + i + ", columnName=" + str);
                throw new IllegalArgumentException("Subscription doesn't exist. subId=" + i + ", columnName=" + str);
            }
            final boolean contains = GROUP_SHARING_COLUMNS.contains(str);
            this.mAllSubscriptionInfoInternalCache.forEach(new BiConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda158
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    SubscriptionDatabaseManager.this.lambda$writeDatabaseAndCacheHelper$5(i, contains, subscriptionInfoInternal, str, t, biFunction, contentValues, (Integer) obj, (SubscriptionInfoInternal) obj2);
                }
            });
        } finally {
            this.mReadWriteLock.writeLock().unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$writeDatabaseAndCacheHelper$5(final int i, boolean z, SubscriptionInfoInternal subscriptionInfoInternal, String str, Object obj, BiFunction biFunction, ContentValues contentValues, Integer num, SubscriptionInfoInternal subscriptionInfoInternal2) {
        if ((num.intValue() == i || (z && !subscriptionInfoInternal.getGroupUuid().isEmpty() && subscriptionInfoInternal.getGroupUuid().equals(subscriptionInfoInternal2.getGroupUuid()))) && !Objects.equals(getSubscriptionInfoFieldByColumnName(subscriptionInfoInternal2, str), obj)) {
            logv("writeDatabaseAndCacheHelper: subId=" + i + ",columnName=" + str + ", newValue=" + obj);
            SubscriptionInfoInternal.Builder builder = (SubscriptionInfoInternal.Builder) biFunction.apply(new SubscriptionInfoInternal.Builder(subscriptionInfoInternal2), obj);
            contentValues.putObject(str, obj);
            if (updateDatabase(num.intValue(), contentValues) > 0) {
                this.mAllSubscriptionInfoInternalCache.put(num, builder.build());
                this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda159
                    @Override // java.lang.Runnable
                    public final void run() {
                        SubscriptionDatabaseManager.this.lambda$writeDatabaseAndCacheHelper$3(i);
                    }
                });
                if (str.equals("uicc_applications_enabled")) {
                    this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda160
                        @Override // java.lang.Runnable
                        public final void run() {
                            SubscriptionDatabaseManager.this.lambda$writeDatabaseAndCacheHelper$4(i);
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$writeDatabaseAndCacheHelper$3(int i) {
        this.mCallback.onSubscriptionChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$writeDatabaseAndCacheHelper$4(int i) {
        this.mCallback.onUiccApplicationsEnabled(i);
    }

    public void updateSubscription(SubscriptionInfoInternal subscriptionInfoInternal) {
        Objects.requireNonNull(subscriptionInfoInternal);
        this.mReadWriteLock.writeLock().lock();
        try {
            final int subscriptionId = subscriptionInfoInternal.getSubscriptionId();
            SubscriptionInfoInternal subscriptionInfoInternal2 = this.mAllSubscriptionInfoInternalCache.get(Integer.valueOf(subscriptionInfoInternal.getSubscriptionId()));
            if (subscriptionInfoInternal2 == null) {
                throw new IllegalArgumentException("updateSubscription: subscription does not exist. subId=" + subscriptionId);
            } else if (subscriptionInfoInternal2.equals(subscriptionInfoInternal)) {
            } else {
                if (updateDatabase(subscriptionId, createDeltaContentValues(subscriptionInfoInternal2, subscriptionInfoInternal)) > 0) {
                    this.mAllSubscriptionInfoInternalCache.put(Integer.valueOf(subscriptionId), subscriptionInfoInternal);
                    this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda49
                        @Override // java.lang.Runnable
                        public final void run() {
                            SubscriptionDatabaseManager.this.lambda$updateSubscription$6(subscriptionId);
                        }
                    });
                    if (subscriptionInfoInternal2.areUiccApplicationsEnabled() != subscriptionInfoInternal.areUiccApplicationsEnabled()) {
                        this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda50
                            @Override // java.lang.Runnable
                            public final void run() {
                                SubscriptionDatabaseManager.this.lambda$updateSubscription$7(subscriptionId);
                            }
                        });
                    }
                }
            }
        } finally {
            this.mReadWriteLock.writeLock().unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSubscription$6(int i) {
        this.mCallback.onSubscriptionChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSubscription$7(int i) {
        this.mCallback.onUiccApplicationsEnabled(i);
    }

    public void setIccId(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "icc_id", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda11
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setIccId((String) obj2);
            }
        });
    }

    public void setSimSlotIndex(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "sim_id", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda15
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setSimSlotIndex(((Integer) obj2).intValue());
            }
        });
    }

    public void setDisplayName(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "display_name", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda2
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setDisplayName((String) obj2);
            }
        });
    }

    public void setCarrierName(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "carrier_name", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda17
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setCarrierName((String) obj2);
            }
        });
    }

    public void setDisplayNameSource(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "name_source", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda28
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setDisplayNameSource(((Integer) obj2).intValue());
            }
        });
    }

    public void setIconTint(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "color", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda14
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setIconTint(((Integer) obj2).intValue());
            }
        });
    }

    public void setNumber(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, IccProvider.STR_NUMBER, str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda10
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setNumber((String) obj2);
            }
        });
    }

    public void setDataRoaming(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "data_roaming", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda42
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setDataRoaming(((Integer) obj2).intValue());
            }
        });
    }

    public void setMcc(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "mcc_string", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda45
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setMcc((String) obj2);
            }
        });
    }

    public void setMnc(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "mnc_string", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda43
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setMnc((String) obj2);
            }
        });
    }

    public void setEhplmns(int i, String[] strArr) {
        Objects.requireNonNull(strArr);
        setEhplmns(i, (String) Arrays.stream(strArr).filter(Predicate.not(new SubscriptionDatabaseManager$$ExternalSyntheticLambda1())).collect(Collectors.joining(",")));
    }

    public void setEhplmns(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "ehplmns", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda30
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setEhplmns((String) obj2);
            }
        });
    }

    public void setHplmns(int i, String[] strArr) {
        Objects.requireNonNull(strArr);
        setHplmns(i, (String) Arrays.stream(strArr).filter(Predicate.not(new SubscriptionDatabaseManager$$ExternalSyntheticLambda1())).collect(Collectors.joining(",")));
    }

    public void setHplmns(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "hplmns", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda22
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setHplmns((String) obj2);
            }
        });
    }

    public void setEmbedded(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "is_embedded", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda40
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setEmbedded(((Integer) obj2).intValue());
            }
        });
    }

    public void setEmbedded(int i, boolean z) {
        setEmbedded(i, z ? 1 : 0);
    }

    public void setCardString(int i, String str) {
        Objects.requireNonNull(str);
        setCardId(i, this.mUiccController.convertToPublicCardId(str));
        writeDatabaseAndCacheHelper(i, "card_id", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda48
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setCardString((String) obj2);
            }
        });
    }

    public void setCardId(int i, int i2) {
        this.mReadWriteLock.writeLock().lock();
        try {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mAllSubscriptionInfoInternalCache.get(Integer.valueOf(i));
            if (subscriptionInfoInternal == null) {
                throw new IllegalArgumentException("setCardId: Subscription doesn't exist. subId=" + i);
            }
            this.mAllSubscriptionInfoInternalCache.put(Integer.valueOf(i), new SubscriptionInfoInternal.Builder(subscriptionInfoInternal).setCardId(i2).build());
        } finally {
            this.mReadWriteLock.writeLock().unlock();
        }
    }

    public void setNativeAccessRules(int i, byte[] bArr) {
        Objects.requireNonNull(bArr);
        writeDatabaseAndCacheHelper(i, "access_rules", bArr, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda23
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setNativeAccessRules((byte[]) obj2);
            }
        });
    }

    public void setCarrierConfigAccessRules(int i, byte[] bArr) {
        Objects.requireNonNull(bArr);
        writeDatabaseAndCacheHelper(i, "access_rules_from_carrier_configs", bArr, new SubscriptionDatabaseManager$$ExternalSyntheticLambda6());
    }

    public void setCarrierConfigAccessRules(int i, UiccAccessRule[] uiccAccessRuleArr) {
        Objects.requireNonNull(uiccAccessRuleArr);
        writeDatabaseAndCacheHelper(i, "access_rules_from_carrier_configs", UiccAccessRule.encodeRules(uiccAccessRuleArr), new SubscriptionDatabaseManager$$ExternalSyntheticLambda6());
    }

    public void setRemovableEmbedded(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "is_removable", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda29
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setRemovableEmbedded(((Integer) obj2).intValue());
            }
        });
    }

    public void setEnhanced4GModeEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "volte_vt_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda153
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setEnhanced4GModeEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setVideoTelephonyEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "vt_ims_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda54
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setVideoTelephonyEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setWifiCallingEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "wfc_ims_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda12
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setWifiCallingEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setWifiCallingMode(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "wfc_ims_mode", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda32
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setWifiCallingMode(((Integer) obj2).intValue());
            }
        });
    }

    public void setWifiCallingModeForRoaming(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "wfc_ims_roaming_mode", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda152
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setWifiCallingModeForRoaming(((Integer) obj2).intValue());
            }
        });
    }

    public void setWifiCallingEnabledForRoaming(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "wfc_ims_roaming_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda37
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setWifiCallingEnabledForRoaming(((Integer) obj2).intValue());
            }
        });
    }

    public void setOpportunistic(int i, boolean z) {
        setOpportunistic(i, z ? 1 : 0);
    }

    public void setOpportunistic(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "is_opportunistic", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda19
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setOpportunistic(((Integer) obj2).intValue());
            }
        });
    }

    public void setGroupUuid(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "group_uuid", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda35
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setGroupUuid((String) obj2);
            }
        });
    }

    public void setCountryIso(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "iso_country_code", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda38
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setCountryIso((String) obj2);
            }
        });
    }

    public void setCarrierId(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "carrier_id", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda44
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setCarrierId(((Integer) obj2).intValue());
            }
        });
    }

    public void setProfileClass(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "profile_class", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda0
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setProfileClass(((Integer) obj2).intValue());
            }
        });
    }

    public void setSubscriptionType(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "subscription_type", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda8
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setType(((Integer) obj2).intValue());
            }
        });
    }

    public void setGroupOwner(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "group_owner", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda41
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setGroupOwner((String) obj2);
            }
        });
    }

    public void setEnabledMobileDataPolicies(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "enabled_mobile_data_policies", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda16
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setEnabledMobileDataPolicies((String) obj2);
            }
        });
    }

    public void setImsi(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "imsi", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda7
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setImsi((String) obj2);
            }
        });
    }

    public void setUiccApplicationsEnabled(int i, boolean z) {
        setUiccApplicationsEnabled(i, z ? 1 : 0);
    }

    public void setUiccApplicationsEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "uicc_applications_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda39
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setUiccApplicationsEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setRcsUceEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "ims_rcs_uce_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda27
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setRcsUceEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setCrossSimCallingEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "cross_sim_calling_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda46
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setCrossSimCallingEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setRcsConfig(int i, byte[] bArr) {
        Objects.requireNonNull(bArr);
        writeDatabaseAndCacheHelper(i, "rcs_config", bArr, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda51
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setRcsConfig((byte[]) obj2);
            }
        });
    }

    public void setAllowedNetworkTypesForReasons(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "allowed_network_types_for_reasons", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda13
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setAllowedNetworkTypesForReasons((String) obj2);
            }
        });
    }

    public void setDeviceToDeviceStatusSharingPreference(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "d2d_sharing_status", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda26
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setDeviceToDeviceStatusSharingPreference(((Integer) obj2).intValue());
            }
        });
    }

    public void setVoImsOptInEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "voims_opt_in_status", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda3
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setVoImsOptInEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setDeviceToDeviceStatusSharingContacts(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "d2d_sharing_contacts", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda4
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setDeviceToDeviceStatusSharingContacts((String) obj2);
            }
        });
    }

    public void setNrAdvancedCallingEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "nr_advanced_calling_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda5
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setNrAdvancedCallingEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setNumberFromCarrier(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "phone_number_source_carrier", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda20
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setNumberFromCarrier((String) obj2);
            }
        });
    }

    public void setNumberFromIms(int i, String str) {
        Objects.requireNonNull(str);
        writeDatabaseAndCacheHelper(i, "phone_number_source_ims", str, new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda52
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setNumberFromIms((String) obj2);
            }
        });
    }

    public void setPortIndex(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "port_index", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda47
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setPortIndex(((Integer) obj2).intValue());
            }
        });
    }

    public void setUsageSetting(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "usage_setting", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda154
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setUsageSetting(((Integer) obj2).intValue());
            }
        });
    }

    public void setLastUsedTPMessageReference(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "tp_message_ref", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda18
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setLastUsedTPMessageReference(((Integer) obj2).intValue());
            }
        });
    }

    public void setUserId(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "user_handle", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda9
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setUserId(((Integer) obj2).intValue());
            }
        });
    }

    public void setSatelliteEnabled(int i, int i2) {
        writeDatabaseAndCacheHelper(i, "satellite_enabled", Integer.valueOf(i2), new BiFunction() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda34
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return ((SubscriptionInfoInternal.Builder) obj).setSatelliteEnabled(((Integer) obj2).intValue());
            }
        });
    }

    public void setGroupDisabled(int i, boolean z) {
        this.mReadWriteLock.writeLock().lock();
        try {
            SubscriptionInfoInternal subscriptionInfoInternal = this.mAllSubscriptionInfoInternalCache.get(Integer.valueOf(i));
            if (subscriptionInfoInternal == null) {
                throw new IllegalArgumentException("setGroupDisabled: Subscription doesn't exist. subId=" + i);
            }
            this.mAllSubscriptionInfoInternalCache.put(Integer.valueOf(i), new SubscriptionInfoInternal.Builder(subscriptionInfoInternal).setGroupDisabled(z).build());
        } finally {
            this.mReadWriteLock.writeLock().unlock();
        }
    }

    public void reloadDatabase() {
        if (this.mAsyncMode) {
            post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionDatabaseManager.this.loadDatabaseInternal();
                }
            });
        } else {
            loadDatabaseInternal();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadDatabaseInternal() {
        log("loadDatabaseInternal");
        Cursor query = this.mContext.getContentResolver().query(Telephony.SimInfo.CONTENT_URI, null, null, null, null);
        try {
            this.mReadWriteLock.writeLock().lock();
            HashMap hashMap = new HashMap();
            boolean z = false;
            while (query != null && query.moveToNext()) {
                final SubscriptionInfoInternal createSubscriptionInfoFromCursor = createSubscriptionInfoFromCursor(query);
                hashMap.put(Integer.valueOf(createSubscriptionInfoFromCursor.getSubscriptionId()), createSubscriptionInfoFromCursor);
                if (!Objects.equals(this.mAllSubscriptionInfoInternalCache.get(Integer.valueOf(createSubscriptionInfoFromCursor.getSubscriptionId())), createSubscriptionInfoFromCursor)) {
                    this.mCallback.invokeFromExecutor(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda155
                        @Override // java.lang.Runnable
                        public final void run() {
                            SubscriptionDatabaseManager.this.lambda$loadDatabaseInternal$8(createSubscriptionInfoFromCursor);
                        }
                    });
                    z = true;
                }
            }
            if (z) {
                this.mAllSubscriptionInfoInternalCache.clear();
                this.mAllSubscriptionInfoInternalCache.putAll(hashMap);
                logl("Loaded " + this.mAllSubscriptionInfoInternalCache.size() + " records from the subscription database.");
                this.mAllSubscriptionInfoInternalCache.forEach(new BiConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda156
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        SubscriptionDatabaseManager.this.lambda$loadDatabaseInternal$9((Integer) obj, (SubscriptionInfoInternal) obj2);
                    }
                });
            }
            this.mReadWriteLock.writeLock().unlock();
            if (query != null) {
                query.close();
            }
        } catch (Throwable th) {
            if (query != null) {
                try {
                    query.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDatabaseInternal$8(SubscriptionInfoInternal subscriptionInfoInternal) {
        this.mCallback.onSubscriptionChanged(subscriptionInfoInternal.getSubscriptionId());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadDatabaseInternal$9(Integer num, SubscriptionInfoInternal subscriptionInfoInternal) {
        log("  " + subscriptionInfoInternal.toString());
    }

    private void initializeDatabase() {
        if (this.mAsyncMode) {
            post(new Runnable() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    SubscriptionDatabaseManager.this.lambda$initializeDatabase$10();
                }
            });
            return;
        }
        synchronized (this) {
            loadDatabaseInternal();
            this.mDatabaseInitialized = true;
            SubscriptionDatabaseManagerCallback subscriptionDatabaseManagerCallback = this.mCallback;
            Objects.requireNonNull(subscriptionDatabaseManagerCallback);
            subscriptionDatabaseManagerCallback.invokeFromExecutor(new SubscriptionDatabaseManager$$ExternalSyntheticLambda25(subscriptionDatabaseManagerCallback));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initializeDatabase$10() {
        synchronized (this) {
            loadDatabaseInternal();
            this.mDatabaseInitialized = true;
            SubscriptionDatabaseManagerCallback subscriptionDatabaseManagerCallback = this.mCallback;
            Objects.requireNonNull(subscriptionDatabaseManagerCallback);
            subscriptionDatabaseManagerCallback.invokeFromExecutor(new SubscriptionDatabaseManager$$ExternalSyntheticLambda25(subscriptionDatabaseManagerCallback));
        }
    }

    private SubscriptionInfoInternal createSubscriptionInfoFromCursor(Cursor cursor) {
        SubscriptionInfoInternal.Builder builder = new SubscriptionInfoInternal.Builder();
        builder.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id"))).setIccId(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("icc_id")))).setSimSlotIndex(cursor.getInt(cursor.getColumnIndexOrThrow("sim_id"))).setDisplayName(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("display_name")))).setCarrierName(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("carrier_name")))).setDisplayNameSource(cursor.getInt(cursor.getColumnIndexOrThrow("name_source"))).setIconTint(cursor.getInt(cursor.getColumnIndexOrThrow("color"))).setNumber(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow(IccProvider.STR_NUMBER)))).setDataRoaming(cursor.getInt(cursor.getColumnIndexOrThrow("data_roaming"))).setMcc(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("mcc_string")))).setMnc(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("mnc_string")))).setEhplmns(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("ehplmns")))).setHplmns(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("hplmns")))).setEmbedded(cursor.getInt(cursor.getColumnIndexOrThrow("is_embedded")));
        String emptyIfNull = TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("card_id")));
        builder.setCardString(emptyIfNull);
        int convertToPublicCardId = this.mUiccController.convertToPublicCardId(emptyIfNull);
        byte[] blob = cursor.getBlob(cursor.getColumnIndexOrThrow("access_rules"));
        if (blob != null) {
            builder.setNativeAccessRules(blob);
        }
        byte[] blob2 = cursor.getBlob(cursor.getColumnIndexOrThrow("access_rules_from_carrier_configs"));
        if (blob2 != null) {
            builder.setCarrierConfigAccessRules(blob2);
        }
        byte[] blob3 = cursor.getBlob(cursor.getColumnIndexOrThrow("rcs_config"));
        if (blob3 != null) {
            builder.setRcsConfig(blob3);
        }
        builder.setCardId(convertToPublicCardId).setRemovableEmbedded(cursor.getInt(cursor.getColumnIndexOrThrow("is_removable"))).setEnhanced4GModeEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("volte_vt_enabled"))).setVideoTelephonyEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("vt_ims_enabled"))).setWifiCallingEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("wfc_ims_enabled"))).setWifiCallingMode(cursor.getInt(cursor.getColumnIndexOrThrow("wfc_ims_mode"))).setWifiCallingModeForRoaming(cursor.getInt(cursor.getColumnIndexOrThrow("wfc_ims_roaming_mode"))).setWifiCallingEnabledForRoaming(cursor.getInt(cursor.getColumnIndexOrThrow("wfc_ims_roaming_enabled"))).setOpportunistic(cursor.getInt(cursor.getColumnIndexOrThrow("is_opportunistic"))).setGroupUuid(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("group_uuid")))).setCountryIso(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("iso_country_code")))).setCarrierId(cursor.getInt(cursor.getColumnIndexOrThrow("carrier_id"))).setProfileClass(cursor.getInt(cursor.getColumnIndexOrThrow("profile_class"))).setType(cursor.getInt(cursor.getColumnIndexOrThrow("subscription_type"))).setGroupOwner(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("group_owner")))).setEnabledMobileDataPolicies(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("enabled_mobile_data_policies")))).setImsi(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("imsi")))).setUiccApplicationsEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("uicc_applications_enabled"))).setRcsUceEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("ims_rcs_uce_enabled"))).setCrossSimCallingEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("cross_sim_calling_enabled"))).setAllowedNetworkTypesForReasons(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("allowed_network_types_for_reasons")))).setDeviceToDeviceStatusSharingPreference(cursor.getInt(cursor.getColumnIndexOrThrow("d2d_sharing_status"))).setVoImsOptInEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("voims_opt_in_status"))).setDeviceToDeviceStatusSharingContacts(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("d2d_sharing_contacts")))).setNrAdvancedCallingEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("nr_advanced_calling_enabled"))).setNumberFromCarrier(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("phone_number_source_carrier")))).setNumberFromIms(TextUtils.emptyIfNull(cursor.getString(cursor.getColumnIndexOrThrow("phone_number_source_ims")))).setPortIndex(cursor.getInt(cursor.getColumnIndexOrThrow("port_index"))).setUsageSetting(cursor.getInt(cursor.getColumnIndexOrThrow("usage_setting"))).setLastUsedTPMessageReference(cursor.getInt(cursor.getColumnIndexOrThrow("tp_message_ref"))).setUserId(cursor.getInt(cursor.getColumnIndexOrThrow("user_handle"))).setSatelliteEnabled(cursor.getInt(cursor.getColumnIndexOrThrow("satellite_enabled")));
        return builder.build();
    }

    public void syncToGroup(int i) {
        if (!this.mAllSubscriptionInfoInternalCache.containsKey(Integer.valueOf(i))) {
            throw new IllegalArgumentException("Invalid subId " + i);
        }
        for (String str : GROUP_SHARING_COLUMNS) {
            setSubscriptionProperty(i, str, getSubscriptionProperty(i, str));
        }
    }

    public SubscriptionInfoInternal getSubscriptionInfoInternal(int i) {
        this.mReadWriteLock.readLock().lock();
        try {
            return this.mAllSubscriptionInfoInternalCache.get(Integer.valueOf(i));
        } finally {
            this.mReadWriteLock.readLock().unlock();
        }
    }

    public List<SubscriptionInfoInternal> getAllSubscriptions() {
        this.mReadWriteLock.readLock().lock();
        try {
            return new ArrayList(this.mAllSubscriptionInfoInternalCache.values());
        } finally {
            this.mReadWriteLock.readLock().unlock();
        }
    }

    public SubscriptionInfoInternal getSubscriptionInfoInternalByIccId(final String str) {
        this.mReadWriteLock.readLock().lock();
        try {
            return this.mAllSubscriptionInfoInternalCache.values().stream().filter(new Predicate() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda21
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getSubscriptionInfoInternalByIccId$11;
                    lambda$getSubscriptionInfoInternalByIccId$11 = SubscriptionDatabaseManager.lambda$getSubscriptionInfoInternalByIccId$11(str, (SubscriptionInfoInternal) obj);
                    return lambda$getSubscriptionInfoInternalByIccId$11;
                }
            }).findFirst().orElse(null);
        } finally {
            this.mReadWriteLock.readLock().unlock();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getSubscriptionInfoInternalByIccId$11(String str, SubscriptionInfoInternal subscriptionInfoInternal) {
        return subscriptionInfoInternal.getIccId().equals(str);
    }

    private void log(String str) {
        Rlog.d("SDM", str);
    }

    private void loge(String str) {
        Rlog.e("SDM", str);
    }

    private void logel(String str) {
        loge(str);
        this.mLocalLog.log(str);
    }

    private void logl(String str) {
        log(str);
        this.mLocalLog.log(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        final AndroidUtilIndentingPrintWriter androidUtilIndentingPrintWriter = new AndroidUtilIndentingPrintWriter(printWriter, "  ");
        androidUtilIndentingPrintWriter.println(SubscriptionDatabaseManager.class.getSimpleName() + ":");
        androidUtilIndentingPrintWriter.increaseIndent();
        androidUtilIndentingPrintWriter.println("All subscriptions:");
        androidUtilIndentingPrintWriter.increaseIndent();
        this.mReadWriteLock.readLock().lock();
        try {
            this.mAllSubscriptionInfoInternalCache.forEach(new BiConsumer() { // from class: com.android.internal.telephony.subscription.SubscriptionDatabaseManager$$ExternalSyntheticLambda53
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    Integer num = (Integer) obj;
                    AndroidUtilIndentingPrintWriter.this.println((SubscriptionInfoInternal) obj2);
                }
            });
            this.mReadWriteLock.readLock().unlock();
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.println();
            androidUtilIndentingPrintWriter.println("mAsyncMode=" + this.mAsyncMode);
            synchronized (this) {
                androidUtilIndentingPrintWriter.println("mDatabaseInitialized=" + this.mDatabaseInitialized);
            }
            androidUtilIndentingPrintWriter.println("mReadWriteLock=" + this.mReadWriteLock);
            androidUtilIndentingPrintWriter.println();
            androidUtilIndentingPrintWriter.println("Local log:");
            androidUtilIndentingPrintWriter.increaseIndent();
            this.mLocalLog.dump(fileDescriptor, printWriter, strArr);
            androidUtilIndentingPrintWriter.decreaseIndent();
            androidUtilIndentingPrintWriter.decreaseIndent();
        } catch (Throwable th) {
            this.mReadWriteLock.readLock().unlock();
            throw th;
        }
    }
}
