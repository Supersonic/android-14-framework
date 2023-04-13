package android.provider;

import android.annotation.SystemApi;
import android.content.ContentResolver;
import android.net.Uri;
import android.p008os.Bundle;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class SimPhonebookContract {
    public static final String AUTHORITY = "com.android.simphonebook";
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.android.simphonebook");
    public static final String SUBSCRIPTION_ID_PATH_SEGMENT = "subid";

    private SimPhonebookContract() {
    }

    public static String getEfUriPath(int efType) {
        switch (efType) {
            case 1:
                return ElementaryFiles.PATH_SEGMENT_EF_ADN;
            case 2:
                return ElementaryFiles.PATH_SEGMENT_EF_FDN;
            case 3:
                return ElementaryFiles.PATH_SEGMENT_EF_SDN;
            default:
                throw new IllegalArgumentException("Unsupported EfType " + efType);
        }
    }

    /* loaded from: classes3.dex */
    public static final class SimRecords {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/sim-contact_v2";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/sim-contact_v2";
        public static final String ELEMENTARY_FILE_TYPE = "elementary_file_type";
        public static final int ERROR_NAME_UNSUPPORTED = -1;
        public static final String EXTRA_ENCODED_NAME_LENGTH = "android.provider.extra.ENCODED_NAME_LENGTH";
        public static final String GET_ENCODED_NAME_LENGTH_METHOD_NAME = "get_encoded_name_length";
        public static final String NAME = "name";
        public static final String PHONE_NUMBER = "phone_number";
        @SystemApi
        public static final String QUERY_ARG_PIN2 = "android:query-arg-pin2";
        public static final String RECORD_NUMBER = "record_number";
        public static final String SUBSCRIPTION_ID = "subscription_id";

        private SimRecords() {
        }

        public static Uri getContentUri(int subscriptionId, int efType) {
            return buildContentUri(subscriptionId, efType).build();
        }

        public static Uri getItemUri(int subscriptionId, int efType, int recordNumber) {
            Preconditions.checkArgument(recordNumber > 0, "Invalid recordNumber");
            return buildContentUri(subscriptionId, efType).appendPath(String.valueOf(recordNumber)).build();
        }

        public static int getEncodedNameLength(ContentResolver resolver, String name) {
            Objects.requireNonNull(name);
            Bundle result = resolver.call(SimPhonebookContract.AUTHORITY, GET_ENCODED_NAME_LENGTH_METHOD_NAME, name, (Bundle) null);
            if (result == null || !result.containsKey(EXTRA_ENCODED_NAME_LENGTH)) {
                throw new IllegalStateException("Provider malfunction: no length was returned.");
            }
            int length = result.getInt(EXTRA_ENCODED_NAME_LENGTH, -1);
            if (length < 0 && length != -1) {
                throw new IllegalStateException("Provider malfunction: invalid length was returned.");
            }
            return length;
        }

        private static Uri.Builder buildContentUri(int subscriptionId, int efType) {
            return new Uri.Builder().scheme("content").authority(SimPhonebookContract.AUTHORITY).appendPath(SimPhonebookContract.SUBSCRIPTION_ID_PATH_SEGMENT).appendPath(String.valueOf(subscriptionId)).appendPath(SimPhonebookContract.getEfUriPath(efType));
        }
    }

    /* loaded from: classes3.dex */
    public static final class ElementaryFiles {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/sim-elementary-file";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/sim-elementary-file";
        public static final int EF_ADN = 1;
        public static final int EF_FDN = 2;
        public static final int EF_SDN = 3;
        public static final String EF_TYPE = "ef_type";
        public static final int EF_UNKNOWN = 0;
        public static final String MAX_RECORDS = "max_records";
        public static final String NAME_MAX_LENGTH = "name_max_length";
        public static final String PATH_SEGMENT_EF_ADN = "adn";
        public static final String PATH_SEGMENT_EF_FDN = "fdn";
        public static final String PATH_SEGMENT_EF_SDN = "sdn";
        public static final String PHONE_NUMBER_MAX_LENGTH = "phone_number_max_length";
        public static final String RECORD_COUNT = "record_count";
        public static final String SLOT_INDEX = "slot_index";
        public static final String SUBSCRIPTION_ID = "subscription_id";
        public static final String ELEMENTARY_FILES_PATH_SEGMENT = "elementary_files";
        public static final Uri CONTENT_URI = SimPhonebookContract.AUTHORITY_URI.buildUpon().appendPath(ELEMENTARY_FILES_PATH_SEGMENT).build();

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface EfType {
        }

        private ElementaryFiles() {
        }

        public static Uri getItemUri(int subscriptionId, int efType) {
            return CONTENT_URI.buildUpon().appendPath(SimPhonebookContract.SUBSCRIPTION_ID_PATH_SEGMENT).appendPath(String.valueOf(subscriptionId)).appendPath(SimPhonebookContract.getEfUriPath(efType)).build();
        }
    }
}
