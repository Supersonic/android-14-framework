package android.media;

import android.graphics.Bitmap;
import android.media.MediaDescription;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class MediaMetadata implements Parcelable {
    public static final Parcelable.Creator<MediaMetadata> CREATOR;
    private static final SparseArray<String> EDITOR_KEY_MAPPING;
    private static final ArrayMap<String, Integer> METADATA_KEYS_TYPE;
    public static final String METADATA_KEY_BT_FOLDER_TYPE = "android.media.metadata.BT_FOLDER_TYPE";
    public static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
    public static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
    public static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
    public static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION";
    public static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE";
    public static final String METADATA_KEY_DISPLAY_TITLE = "android.media.metadata.DISPLAY_TITLE";
    public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
    public static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
    public static final String METADATA_KEY_MEDIA_ID = "android.media.metadata.MEDIA_ID";
    public static final String METADATA_KEY_MEDIA_URI = "android.media.metadata.MEDIA_URI";
    public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
    public static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
    public static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
    public static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
    public static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
    private static final int METADATA_TYPE_BITMAP = 2;
    private static final int METADATA_TYPE_INVALID = -1;
    private static final int METADATA_TYPE_LONG = 0;
    private static final int METADATA_TYPE_RATING = 3;
    private static final int METADATA_TYPE_TEXT = 1;
    private static final String TAG = "MediaMetadata";
    private final int mBitmapDimensionLimit;
    private final Bundle mBundle;
    private MediaDescription mDescription;
    public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
    public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
    public static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
    public static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
    public static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
    public static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
    public static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
    private static final String[] PREFERRED_DESCRIPTION_ORDER = {METADATA_KEY_TITLE, METADATA_KEY_ARTIST, METADATA_KEY_ALBUM, METADATA_KEY_ALBUM_ARTIST, METADATA_KEY_WRITER, METADATA_KEY_AUTHOR, METADATA_KEY_COMPOSER};
    public static final String METADATA_KEY_DISPLAY_ICON = "android.media.metadata.DISPLAY_ICON";
    public static final String METADATA_KEY_ART = "android.media.metadata.ART";
    public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
    private static final String[] PREFERRED_BITMAP_ORDER = {METADATA_KEY_DISPLAY_ICON, METADATA_KEY_ART, METADATA_KEY_ALBUM_ART};
    public static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI";
    public static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI";
    public static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI";
    private static final String[] PREFERRED_URI_ORDER = {METADATA_KEY_DISPLAY_ICON_URI, METADATA_KEY_ART_URI, METADATA_KEY_ALBUM_ART_URI};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface BitmapKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface LongKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface RatingKey {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface TextKey {
    }

    static {
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        METADATA_KEYS_TYPE = arrayMap;
        arrayMap.put(METADATA_KEY_TITLE, 1);
        arrayMap.put(METADATA_KEY_ARTIST, 1);
        arrayMap.put(METADATA_KEY_DURATION, 0);
        arrayMap.put(METADATA_KEY_ALBUM, 1);
        arrayMap.put(METADATA_KEY_AUTHOR, 1);
        arrayMap.put(METADATA_KEY_WRITER, 1);
        arrayMap.put(METADATA_KEY_COMPOSER, 1);
        arrayMap.put(METADATA_KEY_COMPILATION, 1);
        arrayMap.put(METADATA_KEY_DATE, 1);
        arrayMap.put(METADATA_KEY_YEAR, 0);
        arrayMap.put(METADATA_KEY_GENRE, 1);
        arrayMap.put(METADATA_KEY_TRACK_NUMBER, 0);
        arrayMap.put(METADATA_KEY_NUM_TRACKS, 0);
        arrayMap.put(METADATA_KEY_DISC_NUMBER, 0);
        arrayMap.put(METADATA_KEY_ALBUM_ARTIST, 1);
        arrayMap.put(METADATA_KEY_ART, 2);
        arrayMap.put(METADATA_KEY_ART_URI, 1);
        arrayMap.put(METADATA_KEY_ALBUM_ART, 2);
        arrayMap.put(METADATA_KEY_ALBUM_ART_URI, 1);
        arrayMap.put(METADATA_KEY_USER_RATING, 3);
        arrayMap.put(METADATA_KEY_RATING, 3);
        arrayMap.put(METADATA_KEY_DISPLAY_TITLE, 1);
        arrayMap.put(METADATA_KEY_DISPLAY_SUBTITLE, 1);
        arrayMap.put(METADATA_KEY_DISPLAY_DESCRIPTION, 1);
        arrayMap.put(METADATA_KEY_DISPLAY_ICON, 2);
        arrayMap.put(METADATA_KEY_DISPLAY_ICON_URI, 1);
        arrayMap.put(METADATA_KEY_BT_FOLDER_TYPE, 0);
        arrayMap.put(METADATA_KEY_MEDIA_ID, 1);
        arrayMap.put(METADATA_KEY_MEDIA_URI, 1);
        SparseArray<String> sparseArray = new SparseArray<>();
        EDITOR_KEY_MAPPING = sparseArray;
        sparseArray.put(100, METADATA_KEY_ART);
        sparseArray.put(101, METADATA_KEY_RATING);
        sparseArray.put(MediaMetadataEditor.RATING_KEY_BY_USER, METADATA_KEY_USER_RATING);
        sparseArray.put(1, METADATA_KEY_ALBUM);
        sparseArray.put(13, METADATA_KEY_ALBUM_ARTIST);
        sparseArray.put(2, METADATA_KEY_ARTIST);
        sparseArray.put(3, METADATA_KEY_AUTHOR);
        sparseArray.put(0, METADATA_KEY_TRACK_NUMBER);
        sparseArray.put(4, METADATA_KEY_COMPOSER);
        sparseArray.put(15, METADATA_KEY_COMPILATION);
        sparseArray.put(5, METADATA_KEY_DATE);
        sparseArray.put(14, METADATA_KEY_DISC_NUMBER);
        sparseArray.put(9, METADATA_KEY_DURATION);
        sparseArray.put(6, METADATA_KEY_GENRE);
        sparseArray.put(10, METADATA_KEY_NUM_TRACKS);
        sparseArray.put(7, METADATA_KEY_TITLE);
        sparseArray.put(11, METADATA_KEY_WRITER);
        sparseArray.put(8, METADATA_KEY_YEAR);
        CREATOR = new Parcelable.Creator<MediaMetadata>() { // from class: android.media.MediaMetadata.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public MediaMetadata createFromParcel(Parcel in) {
                return new MediaMetadata(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public MediaMetadata[] newArray(int size) {
                return new MediaMetadata[size];
            }
        };
    }

    private MediaMetadata(Bundle bundle, int bitmapDimensionLimit) {
        this.mBundle = new Bundle(bundle);
        this.mBitmapDimensionLimit = bitmapDimensionLimit;
    }

    private MediaMetadata(Parcel in) {
        this.mBundle = in.readBundle();
        this.mBitmapDimensionLimit = Math.max(in.readInt(), 1);
        getBitmap(METADATA_KEY_ART);
        getBitmap(METADATA_KEY_ALBUM_ART);
        getBitmap(METADATA_KEY_DISPLAY_ICON);
    }

    public boolean containsKey(String key) {
        return this.mBundle.containsKey(key);
    }

    public CharSequence getText(String key) {
        return this.mBundle.getCharSequence(key);
    }

    public String getString(String key) {
        CharSequence text = getText(key);
        if (text != null) {
            return text.toString();
        }
        return null;
    }

    public long getLong(String key) {
        return this.mBundle.getLong(key, 0L);
    }

    public Rating getRating(String key) {
        try {
            Rating rating = (Rating) this.mBundle.getParcelable(key, Rating.class);
            return rating;
        } catch (Exception e) {
            Log.m103w(TAG, "Failed to retrieve a key as Rating.", e);
            return null;
        }
    }

    public Bitmap getBitmap(String key) {
        try {
            Bitmap bmp = (Bitmap) this.mBundle.getParcelable(key, Bitmap.class);
            return bmp;
        } catch (Exception e) {
            Log.m103w(TAG, "Failed to retrieve a key as Bitmap.", e);
            return null;
        }
    }

    public int getBitmapDimensionLimit() {
        return this.mBitmapDimensionLimit;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.mBundle);
        dest.writeInt(this.mBitmapDimensionLimit);
    }

    public int size() {
        return this.mBundle.size();
    }

    public Set<String> keySet() {
        return this.mBundle.keySet();
    }

    public MediaDescription getDescription() {
        MediaDescription mediaDescription = this.mDescription;
        if (mediaDescription != null) {
            return mediaDescription;
        }
        String mediaId = getString(METADATA_KEY_MEDIA_ID);
        CharSequence[] text = new CharSequence[3];
        Bitmap icon = null;
        Uri iconUri = null;
        CharSequence displayText = getText(METADATA_KEY_DISPLAY_TITLE);
        if (!TextUtils.isEmpty(displayText)) {
            text[0] = displayText;
            text[1] = getText(METADATA_KEY_DISPLAY_SUBTITLE);
            text[2] = getText(METADATA_KEY_DISPLAY_DESCRIPTION);
        } else {
            int textIndex = 0;
            int keyIndex = 0;
            while (textIndex < text.length) {
                String[] strArr = PREFERRED_DESCRIPTION_ORDER;
                if (keyIndex >= strArr.length) {
                    break;
                }
                int keyIndex2 = keyIndex + 1;
                CharSequence next = getText(strArr[keyIndex]);
                if (!TextUtils.isEmpty(next)) {
                    text[textIndex] = next;
                    textIndex++;
                }
                keyIndex = keyIndex2;
            }
        }
        int i = 0;
        while (true) {
            String[] strArr2 = PREFERRED_BITMAP_ORDER;
            if (i >= strArr2.length) {
                break;
            }
            Bitmap next2 = getBitmap(strArr2[i]);
            if (next2 == null) {
                i++;
            } else {
                icon = next2;
                break;
            }
        }
        int i2 = 0;
        while (true) {
            String[] strArr3 = PREFERRED_URI_ORDER;
            if (i2 >= strArr3.length) {
                break;
            }
            String next3 = getString(strArr3[i2]);
            if (TextUtils.isEmpty(next3)) {
                i2++;
            } else {
                iconUri = Uri.parse(next3);
                break;
            }
        }
        Uri mediaUri = null;
        String mediaUriStr = getString(METADATA_KEY_MEDIA_URI);
        if (!TextUtils.isEmpty(mediaUriStr)) {
            mediaUri = Uri.parse(mediaUriStr);
        }
        MediaDescription.Builder bob = new MediaDescription.Builder();
        bob.setMediaId(mediaId);
        bob.setTitle(text[0]);
        bob.setSubtitle(text[1]);
        bob.setDescription(text[2]);
        bob.setIconBitmap(icon);
        bob.setIconUri(iconUri);
        bob.setMediaUri(mediaUri);
        if (this.mBundle.containsKey(METADATA_KEY_BT_FOLDER_TYPE)) {
            Bundle bundle = new Bundle();
            bundle.putLong(MediaDescription.EXTRA_BT_FOLDER_TYPE, getLong(METADATA_KEY_BT_FOLDER_TYPE));
            bob.setExtras(bundle);
        }
        MediaDescription build = bob.build();
        this.mDescription = build;
        return build;
    }

    public static String getKeyFromMetadataEditorKey(int editorKey) {
        return EDITOR_KEY_MAPPING.get(editorKey, null);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MediaMetadata)) {
            return false;
        }
        MediaMetadata m = (MediaMetadata) o;
        int i = 0;
        while (true) {
            ArrayMap<String, Integer> arrayMap = METADATA_KEYS_TYPE;
            if (i >= arrayMap.size()) {
                return true;
            }
            String key = arrayMap.keyAt(i);
            switch (arrayMap.valueAt(i).intValue()) {
                case 0:
                    if (getLong(key) == m.getLong(key)) {
                        break;
                    } else {
                        return false;
                    }
                case 1:
                    if (Objects.equals(getString(key), m.getString(key))) {
                        break;
                    } else {
                        return false;
                    }
            }
            i++;
        }
    }

    public int hashCode() {
        int hashCode = 17;
        int i = 0;
        while (true) {
            ArrayMap<String, Integer> arrayMap = METADATA_KEYS_TYPE;
            if (i < arrayMap.size()) {
                String key = arrayMap.keyAt(i);
                switch (arrayMap.valueAt(i).intValue()) {
                    case 0:
                        hashCode = (hashCode * 31) + Long.hashCode(getLong(key));
                        break;
                    case 1:
                        hashCode = (hashCode * 31) + Objects.hash(getString(key));
                        break;
                }
                i++;
            } else {
                return hashCode;
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mBitmapDimensionLimit;
        private final Bundle mBundle;

        public Builder() {
            this.mBitmapDimensionLimit = Integer.MAX_VALUE;
            this.mBundle = new Bundle();
        }

        public Builder(MediaMetadata source) {
            this.mBitmapDimensionLimit = Integer.MAX_VALUE;
            this.mBundle = new Bundle(source.mBundle);
            this.mBitmapDimensionLimit = source.mBitmapDimensionLimit;
        }

        public Builder putText(String key, CharSequence value) {
            if (MediaMetadata.METADATA_KEYS_TYPE.containsKey(key) && ((Integer) MediaMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 1) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a CharSequence");
            }
            this.mBundle.putCharSequence(key, value);
            return this;
        }

        public Builder putString(String key, String value) {
            if (MediaMetadata.METADATA_KEYS_TYPE.containsKey(key) && ((Integer) MediaMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 1) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a String");
            }
            this.mBundle.putCharSequence(key, value);
            return this;
        }

        public Builder putLong(String key, long value) {
            if (MediaMetadata.METADATA_KEYS_TYPE.containsKey(key) && ((Integer) MediaMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 0) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a long");
            }
            this.mBundle.putLong(key, value);
            return this;
        }

        public Builder putRating(String key, Rating value) {
            if (MediaMetadata.METADATA_KEYS_TYPE.containsKey(key) && ((Integer) MediaMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 3) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a Rating");
            }
            this.mBundle.putParcelable(key, value);
            return this;
        }

        public Builder putBitmap(String key, Bitmap value) {
            if (MediaMetadata.METADATA_KEYS_TYPE.containsKey(key) && ((Integer) MediaMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 2) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a Bitmap");
            }
            this.mBundle.putParcelable(key, value);
            return this;
        }

        public Builder setBitmapDimensionLimit(int bitmapDimensionLimit) {
            if (bitmapDimensionLimit > 0) {
                this.mBitmapDimensionLimit = bitmapDimensionLimit;
            } else {
                Log.m104w(MediaMetadata.TAG, "setBitmapDimensionLimit(): Ignoring non-positive bitmapDimensionLimit: " + bitmapDimensionLimit);
            }
            return this;
        }

        public MediaMetadata build() {
            if (this.mBitmapDimensionLimit != Integer.MAX_VALUE) {
                for (String key : this.mBundle.keySet()) {
                    Object value = this.mBundle.get(key);
                    if (value instanceof Bitmap) {
                        Bitmap bmp = (Bitmap) value;
                        if (bmp.getHeight() > this.mBitmapDimensionLimit || bmp.getWidth() > this.mBitmapDimensionLimit) {
                            putBitmap(key, scaleBitmap(bmp, this.mBitmapDimensionLimit));
                        }
                    }
                }
            }
            return new MediaMetadata(this.mBundle, this.mBitmapDimensionLimit);
        }

        private Bitmap scaleBitmap(Bitmap bmp, int maxDimension) {
            float maxDimensionF = maxDimension;
            float widthScale = maxDimensionF / bmp.getWidth();
            float heightScale = maxDimensionF / bmp.getHeight();
            float scale = Math.min(widthScale, heightScale);
            int height = (int) (bmp.getHeight() * scale);
            int width = (int) (bmp.getWidth() * scale);
            return Bitmap.createScaledBitmap(bmp, width, height, true);
        }
    }
}
