package com.android.ims.rcs.uce.eab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ProvisioningManager;
import android.telephony.ims.RcsContactPresenceTuple;
import android.telephony.ims.RcsContactUceCapability;
import android.text.TextUtils;
import android.util.Log;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.ims.RcsFeatureManager;
import com.android.ims.rcs.uce.UceController;
import com.android.ims.rcs.uce.eab.EabProvider;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class EabControllerImpl implements EabController {
    private static final int CLEAN_UP_LEGACY_CAPABILITY_DELAY_MILLI_SEC = 30000;
    private static final int CLEAN_UP_LEGACY_CAPABILITY_SEC = 604800;
    private static final int DEFAULT_AVAILABILITY_CACHE_EXPIRATION_SEC = 60;
    private static final int DEFAULT_CAPABILITY_CACHE_EXPIRATION_SEC = 86400;
    private static final int DEFAULT_NON_RCS_CAPABILITY_CACHE_EXPIRATION_SEC = 604800;
    private static final String TAG = "EabControllerImpl";
    private final Context mContext;
    private final EabBulkCapabilityUpdater mEabBulkCapabilityUpdater;
    private final Handler mHandler;
    private final int mSubId;
    private UceController.UceControllerCallback mUceControllerCallback;
    private volatile boolean mIsSetDestroyedFlag = false;
    private ExpirationTimeFactory mExpirationTimeFactory = new ExpirationTimeFactory() { // from class: com.android.ims.rcs.uce.eab.EabControllerImpl$$ExternalSyntheticLambda0
        @Override // com.android.ims.rcs.uce.eab.EabControllerImpl.ExpirationTimeFactory
        public final long getExpirationTime() {
            long epochSecond;
            epochSecond = Instant.now().getEpochSecond();
            return epochSecond;
        }
    };
    public final Runnable mCapabilityCleanupRunnable = new Runnable() { // from class: com.android.ims.rcs.uce.eab.EabControllerImpl$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            EabControllerImpl.this.lambda$new$1();
        }
    };

    /* loaded from: classes.dex */
    public interface ExpirationTimeFactory {
        long getExpirationTime();
    }

    public /* synthetic */ void lambda$new$1() {
        Log.d(TAG, "Cleanup Capabilities");
        cleanupExpiredCapabilities();
    }

    public EabControllerImpl(Context context, int subId, UceController.UceControllerCallback c, Looper looper) {
        this.mContext = context;
        this.mSubId = subId;
        this.mUceControllerCallback = c;
        Handler handler = new Handler(looper);
        this.mHandler = handler;
        this.mEabBulkCapabilityUpdater = new EabBulkCapabilityUpdater(context, subId, this, new EabContactSyncController(), this.mUceControllerCallback, handler);
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onRcsConnected(RcsFeatureManager manager) {
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onRcsDisconnected() {
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        this.mIsSetDestroyedFlag = true;
        this.mEabBulkCapabilityUpdater.onDestroy();
    }

    @Override // com.android.ims.rcs.uce.ControllerBase
    public void onCarrierConfigChanged() {
        this.mCapabilityCleanupRunnable.run();
        cleanupOrphanedRows();
        if (!this.mIsSetDestroyedFlag) {
            this.mEabBulkCapabilityUpdater.onCarrierConfigChanged();
        }
    }

    @Override // com.android.ims.rcs.uce.eab.EabController
    public void setUceRequestCallback(UceController.UceControllerCallback c) {
        Objects.requireNonNull(c);
        if (this.mIsSetDestroyedFlag) {
            Log.d(TAG, "EabController destroyed.");
            return;
        }
        this.mUceControllerCallback = c;
        this.mEabBulkCapabilityUpdater.setUceRequestCallback(c);
    }

    @Override // com.android.ims.rcs.uce.eab.EabController
    public List<EabCapabilityResult> getCapabilities(List<Uri> uris) {
        Objects.requireNonNull(uris);
        if (this.mIsSetDestroyedFlag) {
            Log.d(TAG, "EabController destroyed.");
            return generateDestroyedResult(uris);
        }
        Log.d(TAG, "getCapabilities uri size=" + uris.size());
        List<EabCapabilityResult> capabilityResultList = new ArrayList<>();
        for (Uri uri : uris) {
            EabCapabilityResult result = generateEabResult(uri, new EabControllerImpl$$ExternalSyntheticLambda3(this));
            capabilityResultList.add(result);
        }
        return capabilityResultList;
    }

    @Override // com.android.ims.rcs.uce.eab.EabController
    public List<EabCapabilityResult> getCapabilitiesIncludingExpired(List<Uri> uris) {
        Objects.requireNonNull(uris);
        if (this.mIsSetDestroyedFlag) {
            Log.d(TAG, "EabController destroyed.");
            return generateDestroyedResult(uris);
        }
        Log.d(TAG, "getCapabilitiesIncludingExpired uri size=" + uris.size());
        List<EabCapabilityResult> capabilityResultList = new ArrayList<>();
        for (Uri uri : uris) {
            EabCapabilityResult result = generateEabResultIncludingExpired(uri, new EabControllerImpl$$ExternalSyntheticLambda3(this));
            capabilityResultList.add(result);
        }
        return capabilityResultList;
    }

    @Override // com.android.ims.rcs.uce.eab.EabController
    public EabCapabilityResult getAvailability(Uri contactUri) {
        Objects.requireNonNull(contactUri);
        if (this.mIsSetDestroyedFlag) {
            Log.d(TAG, "EabController destroyed.");
            return new EabCapabilityResult(contactUri, 1, null);
        }
        return generateEabResult(contactUri, new EabControllerImpl$$ExternalSyntheticLambda2(this));
    }

    @Override // com.android.ims.rcs.uce.eab.EabController
    public EabCapabilityResult getAvailabilityIncludingExpired(Uri contactUri) {
        Objects.requireNonNull(contactUri);
        if (this.mIsSetDestroyedFlag) {
            Log.d(TAG, "EabController destroyed.");
            return new EabCapabilityResult(contactUri, 1, null);
        }
        return generateEabResultIncludingExpired(contactUri, new EabControllerImpl$$ExternalSyntheticLambda2(this));
    }

    @Override // com.android.ims.rcs.uce.eab.EabController
    public void saveCapabilities(List<RcsContactUceCapability> contactCapabilities) {
        Objects.requireNonNull(contactCapabilities);
        if (this.mIsSetDestroyedFlag) {
            Log.d(TAG, "EabController destroyed.");
            return;
        }
        Log.d(TAG, "Save capabilities: " + contactCapabilities.size());
        for (RcsContactUceCapability capability : contactCapabilities) {
            String phoneNumber = getNumberFromUri(this.mContext, capability.getContactUri());
            Cursor c = this.mContext.getContentResolver().query(EabProvider.CONTACT_URI, null, "phone_number=?", new String[]{phoneNumber}, null);
            if (c != null && c.moveToNext()) {
                int contactId = getIntValue(c, "_id");
                if (capability.getCapabilityMechanism() != 1) {
                    if (capability.getCapabilityMechanism() == 2) {
                        Log.d(TAG, "Insert options capability");
                        deleteOldOptionCapability(contactId);
                        insertNewOptionCapability(contactId, capability);
                    }
                } else {
                    Log.d(TAG, "Insert presence capability");
                    deleteOldPresenceCapability(contactId);
                    insertNewPresenceCapability(contactId, capability);
                }
            } else {
                Log.e(TAG, "The phone number can't find in contact table. ");
                int contactId2 = insertNewContact(phoneNumber);
                if (capability.getCapabilityMechanism() != 1) {
                    if (capability.getCapabilityMechanism() == 2) {
                        insertNewOptionCapability(contactId2, capability);
                    }
                } else {
                    insertNewPresenceCapability(contactId2, capability);
                }
            }
            if (c != null) {
                c.close();
            }
        }
        cleanupOrphanedRows();
        this.mEabBulkCapabilityUpdater.updateExpiredTimeAlert();
        if (this.mHandler.hasCallbacks(this.mCapabilityCleanupRunnable)) {
            this.mHandler.removeCallbacks(this.mCapabilityCleanupRunnable);
        }
        this.mHandler.postDelayed(this.mCapabilityCleanupRunnable, 30000L);
    }

    public void cleanupOrphanedRows() {
        this.mContext.getContentResolver().delete(EabProvider.COMMON_URI, "_id NOT IN  (SELECT eab_common_id FROM eab_presence)  AND _id NOT IN  (SELECT eab_common_id FROM eab_options) ", null);
    }

    private List<EabCapabilityResult> generateDestroyedResult(List<Uri> contactUri) {
        List<EabCapabilityResult> destroyedResult = new ArrayList<>();
        for (Uri uri : contactUri) {
            destroyedResult.add(new EabCapabilityResult(uri, 1, null));
        }
        return destroyedResult;
    }

    private EabCapabilityResult generateEabResult(Uri contactUri, Predicate<Cursor> isExpiredMethod) {
        RcsUceCapabilityBuilderWrapper builder = null;
        Uri queryUri = Uri.withAppendedPath(Uri.withAppendedPath(EabProvider.ALL_DATA_URI, String.valueOf(this.mSubId)), getNumberFromUri(this.mContext, contactUri));
        Cursor cursor = this.mContext.getContentResolver().query(queryUri, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (!isExpiredMethod.test(cursor)) {
                    if (builder == null) {
                        builder = createNewBuilder(contactUri, cursor);
                    } else {
                        updateCapability(contactUri, cursor, builder);
                    }
                }
            }
            cursor.close();
            if (builder == null) {
                EabCapabilityResult result = new EabCapabilityResult(contactUri, 2, null);
                return result;
            } else if (builder.getMechanism() == 1) {
                RcsContactUceCapability.PresenceBuilder presenceBuilder = builder.getPresenceBuilder();
                EabCapabilityResult result2 = new EabCapabilityResult(contactUri, 0, presenceBuilder.build());
                return result2;
            } else {
                RcsContactUceCapability.OptionsBuilder optionsBuilder = builder.getOptionsBuilder();
                EabCapabilityResult result3 = new EabCapabilityResult(contactUri, 0, optionsBuilder.build());
                return result3;
            }
        }
        EabCapabilityResult result4 = new EabCapabilityResult(contactUri, 3, null);
        return result4;
    }

    private EabCapabilityResult generateEabResultIncludingExpired(Uri contactUri, Predicate<Cursor> isExpiredMethod) {
        RcsUceCapabilityBuilderWrapper builder = null;
        Optional<Boolean> isExpired = Optional.empty();
        Uri queryUri = Uri.withAppendedPath(Uri.withAppendedPath(EabProvider.ALL_DATA_URI, String.valueOf(this.mSubId)), getNumberFromUri(this.mContext, contactUri));
        Cursor cursor = this.mContext.getContentResolver().query(queryUri, null, null, null, null);
        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                if (!isExpired.isPresent()) {
                    isExpired = Optional.of(Boolean.valueOf(isExpiredMethod.test(cursor)));
                }
                if (builder == null) {
                    builder = createNewBuilder(contactUri, cursor);
                } else {
                    updateCapability(contactUri, cursor, builder);
                }
            }
            cursor.close();
            int eabResult = 0;
            if (isExpired.orElse(false).booleanValue()) {
                eabResult = 2;
            }
            if (builder.getMechanism() == 1) {
                RcsContactUceCapability.PresenceBuilder presenceBuilder = builder.getPresenceBuilder();
                EabCapabilityResult result = new EabCapabilityResult(contactUri, eabResult, presenceBuilder.build());
                return result;
            }
            RcsContactUceCapability.OptionsBuilder optionsBuilder = builder.getOptionsBuilder();
            EabCapabilityResult result2 = new EabCapabilityResult(contactUri, eabResult, optionsBuilder.build());
            return result2;
        }
        EabCapabilityResult result3 = new EabCapabilityResult(contactUri, 3, null);
        return result3;
    }

    private void updateCapability(Uri contactUri, Cursor cursor, RcsUceCapabilityBuilderWrapper builderWrapper) {
        RcsContactPresenceTuple presenceTuple;
        if (builderWrapper.getMechanism() == 1) {
            RcsContactUceCapability.PresenceBuilder builder = builderWrapper.getPresenceBuilder();
            if (builder != null && (presenceTuple = createPresenceTuple(contactUri, cursor)) != null) {
                builder.addCapabilityTuple(presenceTuple);
                return;
            }
            return;
        }
        RcsContactUceCapability.OptionsBuilder builder2 = builderWrapper.getOptionsBuilder();
        if (builder2 != null) {
            builder2.addFeatureTag(createOptionTuple(cursor));
        }
    }

    private RcsUceCapabilityBuilderWrapper createNewBuilder(Uri contactUri, Cursor cursor) {
        int mechanism = getIntValue(cursor, EabProvider.EabCommonColumns.MECHANISM);
        int result = getIntValue(cursor, EabProvider.EabCommonColumns.REQUEST_RESULT);
        RcsUceCapabilityBuilderWrapper builderWrapper = new RcsUceCapabilityBuilderWrapper(mechanism);
        if (mechanism == 1) {
            RcsContactUceCapability.PresenceBuilder builder = new RcsContactUceCapability.PresenceBuilder(contactUri, 1, result);
            RcsContactPresenceTuple tuple = createPresenceTuple(contactUri, cursor);
            if (tuple != null) {
                builder.addCapabilityTuple(tuple);
            }
            String entityUri = getStringValue(cursor, EabProvider.EabCommonColumns.ENTITY_URI);
            if (!TextUtils.isEmpty(entityUri)) {
                builder.setEntityUri(Uri.parse(entityUri));
            }
            builderWrapper.setPresenceBuilder(builder);
        } else {
            RcsContactUceCapability.OptionsBuilder builder2 = new RcsContactUceCapability.OptionsBuilder(contactUri, 1);
            builder2.setRequestResult(result);
            builder2.addFeatureTag(createOptionTuple(cursor));
            builderWrapper.setOptionsBuilder(builder2);
        }
        return builderWrapper;
    }

    private String createOptionTuple(Cursor cursor) {
        return getStringValue(cursor, EabProvider.OptionsColumns.FEATURE_TAG);
    }

    private RcsContactPresenceTuple createPresenceTuple(Uri contactUri, Cursor cursor) {
        String[] duplexModeList;
        String[] unsupportedDuplexModeList;
        RcsContactPresenceTuple.ServiceCapabilities.Builder serviceCapabilitiesBuilder;
        RcsContactPresenceTuple.Builder rcsContactPresenceTupleBuilder;
        String status = getStringValue(cursor, EabProvider.PresenceTupleColumns.BASIC_STATUS);
        String serviceId = getStringValue(cursor, EabProvider.PresenceTupleColumns.SERVICE_ID);
        String version = getStringValue(cursor, EabProvider.PresenceTupleColumns.SERVICE_VERSION);
        String description = getStringValue(cursor, "description");
        String timeStamp = getStringValue(cursor, EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP);
        boolean audioCapable = getIntValue(cursor, EabProvider.PresenceTupleColumns.AUDIO_CAPABLE) == 1;
        boolean videoCapable = getIntValue(cursor, EabProvider.PresenceTupleColumns.VIDEO_CAPABLE) == 1;
        String duplexModes = getStringValue(cursor, EabProvider.PresenceTupleColumns.DUPLEX_MODE);
        String unsupportedDuplexModes = getStringValue(cursor, EabProvider.PresenceTupleColumns.UNSUPPORTED_DUPLEX_MODE);
        if (!TextUtils.isEmpty(duplexModes)) {
            duplexModeList = duplexModes.split(",");
        } else {
            String[] duplexModeList2 = new String[0];
            duplexModeList = duplexModeList2;
        }
        if (!TextUtils.isEmpty(unsupportedDuplexModes)) {
            unsupportedDuplexModeList = unsupportedDuplexModes.split(",");
        } else {
            String[] unsupportedDuplexModeList2 = new String[0];
            unsupportedDuplexModeList = unsupportedDuplexModeList2;
        }
        RcsContactPresenceTuple.ServiceCapabilities.Builder serviceCapabilitiesBuilder2 = new RcsContactPresenceTuple.ServiceCapabilities.Builder(audioCapable, videoCapable);
        if (TextUtils.isEmpty(duplexModes) && TextUtils.isEmpty(unsupportedDuplexModes)) {
            serviceCapabilitiesBuilder = serviceCapabilitiesBuilder2;
        } else {
            String[] duplexModeList3 = duplexModeList;
            int length = duplexModeList3.length;
            int i = 0;
            while (i < length) {
                int i2 = length;
                String duplexMode = duplexModeList3[i];
                serviceCapabilitiesBuilder2.addSupportedDuplexMode(duplexMode);
                i++;
                length = i2;
            }
            serviceCapabilitiesBuilder = serviceCapabilitiesBuilder2;
            int length2 = unsupportedDuplexModeList.length;
            int i3 = 0;
            while (i3 < length2) {
                int i4 = length2;
                String unsupportedDuplex = unsupportedDuplexModeList[i3];
                serviceCapabilitiesBuilder.addUnsupportedDuplexMode(unsupportedDuplex);
                i3++;
                length2 = i4;
            }
        }
        RcsContactPresenceTuple.ServiceCapabilities serviceCapabilities = serviceCapabilitiesBuilder.build();
        boolean isTupleEmpty = TextUtils.isEmpty(status) && TextUtils.isEmpty(serviceId) && TextUtils.isEmpty(version);
        if (!isTupleEmpty) {
            RcsContactPresenceTuple.Builder rcsContactPresenceTupleBuilder2 = new RcsContactPresenceTuple.Builder(status, serviceId, version);
            if (description != null) {
                rcsContactPresenceTupleBuilder = rcsContactPresenceTupleBuilder2;
                rcsContactPresenceTupleBuilder.setServiceDescription(description);
            } else {
                rcsContactPresenceTupleBuilder = rcsContactPresenceTupleBuilder2;
            }
            if (contactUri != null) {
                rcsContactPresenceTupleBuilder.setContactUri(contactUri);
            }
            if (serviceCapabilities != null) {
                rcsContactPresenceTupleBuilder.setServiceCapabilities(serviceCapabilities);
            }
            if (timeStamp != null) {
                try {
                    Instant instant = Instant.ofEpochSecond(Long.parseLong(timeStamp));
                    rcsContactPresenceTupleBuilder.setTime(instant);
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Create presence tuple: NumberFormatException");
                } catch (DateTimeParseException e2) {
                    Log.w(TAG, "Create presence tuple: parse timestamp failed");
                }
            }
            return rcsContactPresenceTupleBuilder.build();
        }
        return null;
    }

    public boolean isCapabilityExpired(Cursor cursor) {
        int capabilityCacheExpiration;
        String requestTimeStamp = getRequestTimestamp(cursor);
        if (isNonRcsCapability(cursor)) {
            capabilityCacheExpiration = getNonRcsCapabilityCacheExpiration(this.mSubId);
        } else {
            int capabilityCacheExpiration2 = this.mSubId;
            capabilityCacheExpiration = getCapabilityCacheExpiration(capabilityCacheExpiration2);
        }
        if (requestTimeStamp == null) {
            Log.d(TAG, "Capability requestTimeStamp is null");
            return false;
        }
        Instant expiredTimestamp = Instant.ofEpochSecond(Long.parseLong(requestTimeStamp)).plus(capabilityCacheExpiration, (TemporalUnit) ChronoUnit.SECONDS);
        boolean expired = expiredTimestamp.isBefore(Instant.now());
        Log.d(TAG, "Capability expiredTimestamp: " + expiredTimestamp.getEpochSecond() + ", isNonRcsCapability: " + isNonRcsCapability(cursor) + ", capabilityCacheExpiration: " + capabilityCacheExpiration + ", expired:" + expired);
        return expired;
    }

    private boolean isNonRcsCapability(Cursor cursor) {
        int result = getIntValue(cursor, EabProvider.EabCommonColumns.REQUEST_RESULT);
        return result == 2;
    }

    public boolean isAvailabilityExpired(Cursor cursor) {
        String requestTimeStamp = getRequestTimestamp(cursor);
        if (requestTimeStamp == null) {
            Log.d(TAG, "Capability requestTimeStamp is null");
            return false;
        }
        Instant expiredTimestamp = Instant.ofEpochSecond(Long.parseLong(requestTimeStamp)).plus(getAvailabilityCacheExpiration(this.mSubId), (TemporalUnit) ChronoUnit.SECONDS);
        boolean expired = expiredTimestamp.isBefore(Instant.now());
        Log.d(TAG, "Availability insertedTimestamp: " + expiredTimestamp.getEpochSecond() + ", expired:" + expired);
        return expired;
    }

    private String getRequestTimestamp(Cursor cursor) {
        int mechanism = getIntValue(cursor, EabProvider.EabCommonColumns.MECHANISM);
        if (mechanism == 1) {
            String expiredTimestamp = getStringValue(cursor, EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP);
            return expiredTimestamp;
        } else if (mechanism != 2) {
            return null;
        } else {
            String expiredTimestamp2 = getStringValue(cursor, EabProvider.OptionsColumns.REQUEST_TIMESTAMP);
            return expiredTimestamp2;
        }
    }

    private int getNonRcsCapabilityCacheExpiration(int subId) {
        PersistableBundle carrierConfig = ((CarrierConfigManager) this.mContext.getSystemService(CarrierConfigManager.class)).getConfigForSubId(subId);
        if (carrierConfig != null) {
            int value = carrierConfig.getInt("ims.non_rcs_capabilities_cache_expiration_sec_int");
            return value;
        }
        Log.e(TAG, "getNonRcsCapabilityCacheExpiration: CarrierConfig is null, returning default");
        return 604800;
    }

    public int getCapabilityCacheExpiration(int subId) {
        int value = -1;
        try {
            ProvisioningManager pm = ProvisioningManager.createForSubscriptionId(subId);
            value = pm.getProvisioningIntValue(18);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in getCapabilityCacheExpiration(): " + ex);
        }
        if (value <= 0) {
            Log.e(TAG, "The capability expiration cannot be less than 0.");
            return DEFAULT_CAPABILITY_CACHE_EXPIRATION_SEC;
        }
        return value;
    }

    protected long getAvailabilityCacheExpiration(int subId) {
        long value = -1;
        try {
            ProvisioningManager pm = ProvisioningManager.createForSubscriptionId(subId);
            value = pm.getProvisioningIntValue(19);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in getAvailabilityCacheExpiration(): " + ex);
        }
        if (value <= 0) {
            Log.e(TAG, "The Availability expiration cannot be less than 0.");
            return 60L;
        }
        return value;
    }

    private int insertNewContact(String phoneNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EabProvider.ContactColumns.PHONE_NUMBER, phoneNumber);
        Uri result = this.mContext.getContentResolver().insert(EabProvider.CONTACT_URI, contentValues);
        return Integer.parseInt(result.getLastPathSegment());
    }

    private void deleteOldPresenceCapability(int id) {
        Cursor c = this.mContext.getContentResolver().query(EabProvider.COMMON_URI, new String[]{"_id"}, "eab_contact_id=?", new String[]{String.valueOf(id)}, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int commonId = c.getInt(c.getColumnIndex("_id"));
                this.mContext.getContentResolver().delete(EabProvider.PRESENCE_URI, "eab_common_id=?", new String[]{String.valueOf(commonId)});
            }
        }
        if (c != null) {
            c.close();
        }
    }

    private void insertNewPresenceCapability(int contactId, RcsContactUceCapability capability) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EabProvider.EabCommonColumns.EAB_CONTACT_ID, Integer.valueOf(contactId));
        contentValues.put(EabProvider.EabCommonColumns.MECHANISM, (Integer) 1);
        contentValues.put(EabProvider.EabCommonColumns.SUBSCRIPTION_ID, Integer.valueOf(this.mSubId));
        contentValues.put(EabProvider.EabCommonColumns.REQUEST_RESULT, Integer.valueOf(capability.getRequestResult()));
        if (capability.getEntityUri() != null) {
            contentValues.put(EabProvider.EabCommonColumns.ENTITY_URI, capability.getEntityUri().toString());
        }
        Uri result = this.mContext.getContentResolver().insert(EabProvider.COMMON_URI, contentValues);
        int commonId = Integer.parseInt(result.getLastPathSegment());
        Log.d(TAG, "Insert into common table. Id: " + commonId);
        if (capability.getCapabilityTuples().size() == 0) {
            insertEmptyTuple(commonId);
        } else {
            insertAllTuples(commonId, capability);
        }
    }

    private void insertEmptyTuple(int commonId) {
        Log.d(TAG, "Insert empty tuple into presence table.");
        ContentValues contentValues = new ContentValues();
        contentValues.put("eab_common_id", Integer.valueOf(commonId));
        contentValues.put(EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP, Long.valueOf(this.mExpirationTimeFactory.getExpirationTime()));
        this.mContext.getContentResolver().insert(EabProvider.PRESENCE_URI, contentValues);
    }

    private void insertAllTuples(int commonId, RcsContactUceCapability capability) {
        ContentValues[] presenceContent = new ContentValues[capability.getCapabilityTuples().size()];
        for (int i = 0; i < presenceContent.length; i++) {
            RcsContactPresenceTuple tuple = (RcsContactPresenceTuple) capability.getCapabilityTuples().get(i);
            RcsContactPresenceTuple.ServiceCapabilities serviceCapabilities = tuple.getServiceCapabilities();
            String duplexMode = null;
            String unsupportedDuplexMode = null;
            if (serviceCapabilities != null) {
                List<String> duplexModes = serviceCapabilities.getSupportedDuplexModes();
                if (duplexModes.size() != 0) {
                    duplexMode = TextUtils.join(",", duplexModes);
                }
                List<String> unsupportedDuplexModes = serviceCapabilities.getUnsupportedDuplexModes();
                if (unsupportedDuplexModes.size() != 0) {
                    unsupportedDuplexMode = TextUtils.join(",", unsupportedDuplexModes);
                }
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("eab_common_id", Integer.valueOf(commonId));
            contentValues.put(EabProvider.PresenceTupleColumns.BASIC_STATUS, tuple.getStatus());
            contentValues.put(EabProvider.PresenceTupleColumns.SERVICE_ID, tuple.getServiceId());
            contentValues.put(EabProvider.PresenceTupleColumns.SERVICE_VERSION, tuple.getServiceVersion());
            contentValues.put("description", tuple.getServiceDescription());
            contentValues.put(EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP, Long.valueOf(this.mExpirationTimeFactory.getExpirationTime()));
            contentValues.put(EabProvider.PresenceTupleColumns.CONTACT_URI, tuple.getContactUri().toString());
            if (serviceCapabilities != null) {
                contentValues.put(EabProvider.PresenceTupleColumns.DUPLEX_MODE, duplexMode);
                contentValues.put(EabProvider.PresenceTupleColumns.UNSUPPORTED_DUPLEX_MODE, unsupportedDuplexMode);
                contentValues.put(EabProvider.PresenceTupleColumns.AUDIO_CAPABLE, Boolean.valueOf(serviceCapabilities.isAudioCapable()));
                contentValues.put(EabProvider.PresenceTupleColumns.VIDEO_CAPABLE, Boolean.valueOf(serviceCapabilities.isVideoCapable()));
            }
            presenceContent[i] = contentValues;
        }
        Log.d(TAG, "Insert into presence table. count: " + presenceContent.length);
        this.mContext.getContentResolver().bulkInsert(EabProvider.PRESENCE_URI, presenceContent);
    }

    private void deleteOldOptionCapability(int contactId) {
        Cursor c = this.mContext.getContentResolver().query(EabProvider.COMMON_URI, new String[]{"_id"}, "eab_contact_id=?", new String[]{String.valueOf(contactId)}, null);
        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                int commonId = c.getInt(c.getColumnIndex("_id"));
                this.mContext.getContentResolver().delete(EabProvider.OPTIONS_URI, "eab_common_id=?", new String[]{String.valueOf(commonId)});
            }
        }
        if (c != null) {
            c.close();
        }
    }

    private void insertNewOptionCapability(int contactId, RcsContactUceCapability capability) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EabProvider.EabCommonColumns.EAB_CONTACT_ID, Integer.valueOf(contactId));
        contentValues.put(EabProvider.EabCommonColumns.MECHANISM, (Integer) 2);
        contentValues.put(EabProvider.EabCommonColumns.SUBSCRIPTION_ID, Integer.valueOf(this.mSubId));
        contentValues.put(EabProvider.EabCommonColumns.REQUEST_RESULT, Integer.valueOf(capability.getRequestResult()));
        Uri result = this.mContext.getContentResolver().insert(EabProvider.COMMON_URI, contentValues);
        int commonId = Integer.valueOf(result.getLastPathSegment()).intValue();
        List<ContentValues> optionContentList = new ArrayList<>();
        for (String feature : capability.getFeatureTags()) {
            ContentValues contentValues2 = new ContentValues();
            contentValues2.put("eab_common_id", Integer.valueOf(commonId));
            contentValues2.put(EabProvider.OptionsColumns.FEATURE_TAG, feature);
            contentValues2.put(EabProvider.OptionsColumns.REQUEST_TIMESTAMP, Long.valueOf(Instant.now().getEpochSecond()));
            optionContentList.add(contentValues2);
        }
        ContentValues[] optionContent = new ContentValues[optionContentList.size()];
        this.mContext.getContentResolver().bulkInsert(EabProvider.OPTIONS_URI, (ContentValues[]) optionContentList.toArray(optionContent));
    }

    private void cleanupExpiredCapabilities() {
        long rcsCapabilitiesExpiredTime = (Instant.now().getEpochSecond() - getCapabilityCacheExpiration(this.mSubId)) - 604800;
        long nonRcsCapabilitiesExpiredTime = (Instant.now().getEpochSecond() - getNonRcsCapabilityCacheExpiration(this.mSubId)) - 604800;
        cleanupCapabilities(rcsCapabilitiesExpiredTime, getRcsCommonIdList());
        cleanupCapabilities(nonRcsCapabilitiesExpiredTime, getNonRcsCommonIdList());
    }

    private void cleanupCapabilities(long rcsCapabilitiesExpiredTime, List<Integer> commonIdList) {
        if (commonIdList.size() > 0) {
            String presenceClause = "eab_common_id IN (" + TextUtils.join(",", commonIdList) + ")  AND " + EabProvider.PresenceTupleColumns.REQUEST_TIMESTAMP + "<?";
            String optionClause = "eab_common_id IN (" + TextUtils.join(",", commonIdList) + ")  AND " + EabProvider.OptionsColumns.REQUEST_TIMESTAMP + "<?";
            int deletePresenceCount = this.mContext.getContentResolver().delete(EabProvider.PRESENCE_URI, presenceClause, new String[]{String.valueOf(rcsCapabilitiesExpiredTime)});
            int deleteOptionsCount = this.mContext.getContentResolver().delete(EabProvider.OPTIONS_URI, optionClause, new String[]{String.valueOf(rcsCapabilitiesExpiredTime)});
            Log.d(TAG, "Cleanup capabilities. deletePresenceCount: " + deletePresenceCount + ",deleteOptionsCount: " + deleteOptionsCount);
        }
    }

    private List<Integer> getRcsCommonIdList() {
        ArrayList<Integer> list = new ArrayList<>();
        Cursor cursor = this.mContext.getContentResolver().query(EabProvider.COMMON_URI, null, "request_result<>?", new String[]{String.valueOf(2)}, null);
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            list.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
        }
        cursor.close();
        return list;
    }

    private List<Integer> getNonRcsCommonIdList() {
        ArrayList<Integer> list = new ArrayList<>();
        Cursor cursor = this.mContext.getContentResolver().query(EabProvider.COMMON_URI, null, "request_result=?", new String[]{String.valueOf(2)}, null);
        if (cursor == null) {
            return list;
        }
        while (cursor.moveToNext()) {
            list.add(Integer.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
        }
        cursor.close();
        return list;
    }

    private String getStringValue(Cursor cursor, String column) {
        return cursor.getString(cursor.getColumnIndex(column));
    }

    private int getIntValue(Cursor cursor, String column) {
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    private static String getNumberFromUri(Context context, Uri uri) {
        String number = uri.getSchemeSpecificPart();
        String[] numberParts = number.split("[@;:]");
        if (numberParts.length == 0) {
            return null;
        }
        return formatNumber(context, numberParts[0]);
    }

    public static String formatNumber(Context context, String number) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        String simCountryIso = manager.getSimCountryIso();
        if (simCountryIso != null) {
            String simCountryIso2 = simCountryIso.toUpperCase();
            PhoneNumberUtil util = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber phoneNumber = util.parse(number, simCountryIso2);
                return util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            } catch (NumberParseException e) {
                Log.w(TAG, "formatNumber: could not format " + number + ", error: " + e);
            }
        }
        return number;
    }

    public void setExpirationTimeFactory(ExpirationTimeFactory factory) {
        this.mExpirationTimeFactory = factory;
    }
}
