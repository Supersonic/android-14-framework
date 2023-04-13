package android.media.p007tv;

import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* renamed from: android.media.tv.TvContentRating */
/* loaded from: classes2.dex */
public final class TvContentRating {
    private static final String DELIMITER = "/";
    public static final TvContentRating UNRATED = new TvContentRating("null", "null", "null", null);
    private final String mDomain;
    private final int mHashCode;
    private final String mRating;
    private final String mRatingSystem;
    private final String[] mSubRatings;

    public static TvContentRating createRating(String domain, String ratingSystem, String rating, String... subRatings) {
        if (TextUtils.isEmpty(domain)) {
            throw new IllegalArgumentException("domain cannot be empty");
        }
        if (TextUtils.isEmpty(ratingSystem)) {
            throw new IllegalArgumentException("ratingSystem cannot be empty");
        }
        if (TextUtils.isEmpty(rating)) {
            throw new IllegalArgumentException("rating cannot be empty");
        }
        return new TvContentRating(domain, ratingSystem, rating, subRatings);
    }

    public static TvContentRating unflattenFromString(String ratingString) {
        if (TextUtils.isEmpty(ratingString)) {
            throw new IllegalArgumentException("ratingString cannot be empty");
        }
        String[] strs = ratingString.split(DELIMITER);
        if (strs.length < 3) {
            throw new IllegalArgumentException("Invalid rating string: " + ratingString);
        }
        if (strs.length > 3) {
            String[] subRatings = new String[strs.length - 3];
            System.arraycopy(strs, 3, subRatings, 0, subRatings.length);
            return new TvContentRating(strs[0], strs[1], strs[2], subRatings);
        }
        return new TvContentRating(strs[0], strs[1], strs[2], null);
    }

    private TvContentRating(String domain, String ratingSystem, String rating, String[] subRatings) {
        this.mDomain = domain;
        this.mRatingSystem = ratingSystem;
        this.mRating = rating;
        if (subRatings == null || subRatings.length == 0) {
            this.mSubRatings = null;
        } else {
            Arrays.sort(subRatings);
            this.mSubRatings = subRatings;
        }
        this.mHashCode = (Objects.hash(domain, rating) * 31) + Arrays.hashCode(this.mSubRatings);
    }

    public String getDomain() {
        return this.mDomain;
    }

    public String getRatingSystem() {
        return this.mRatingSystem;
    }

    public String getMainRating() {
        return this.mRating;
    }

    public List<String> getSubRatings() {
        String[] strArr = this.mSubRatings;
        if (strArr == null) {
            return null;
        }
        return Collections.unmodifiableList(Arrays.asList(strArr));
    }

    public String flattenToString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.mDomain);
        builder.append(DELIMITER);
        builder.append(this.mRatingSystem);
        builder.append(DELIMITER);
        builder.append(this.mRating);
        String[] strArr = this.mSubRatings;
        if (strArr != null) {
            for (String subRating : strArr) {
                builder.append(DELIMITER);
                builder.append(subRating);
            }
        }
        return builder.toString();
    }

    public final boolean contains(TvContentRating rating) {
        Preconditions.checkNotNull(rating);
        if (rating.getMainRating().equals(this.mRating) && rating.getDomain().equals(this.mDomain) && rating.getRatingSystem().equals(this.mRatingSystem) && rating.getMainRating().equals(this.mRating)) {
            List<String> subRatings = getSubRatings();
            List<String> subRatingsOther = rating.getSubRatings();
            if (subRatings == null && subRatingsOther == null) {
                return true;
            }
            if (subRatings != null || subRatingsOther == null) {
                if (subRatings != null && subRatingsOther == null) {
                    return true;
                }
                return subRatings.containsAll(subRatingsOther);
            }
            return false;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj instanceof TvContentRating) {
            TvContentRating other = (TvContentRating) obj;
            if (this.mHashCode == other.mHashCode && TextUtils.equals(this.mDomain, other.mDomain) && TextUtils.equals(this.mRatingSystem, other.mRatingSystem) && TextUtils.equals(this.mRating, other.mRating)) {
                return Arrays.equals(this.mSubRatings, other.mSubRatings);
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return this.mHashCode;
    }
}
