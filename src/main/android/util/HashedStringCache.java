package android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.p008os.Environment;
import android.p008os.storage.StorageManager;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import java.io.File;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
/* loaded from: classes3.dex */
public class HashedStringCache {
    private static final long DAYS_TO_MILLIS = 86400000;
    private static final boolean DEBUG = false;
    private static final int HASH_CACHE_SIZE = 100;
    private static final int HASH_LENGTH = 8;
    static final String HASH_SALT = "_hash_salt";
    static final String HASH_SALT_DATE = "_hash_salt_date";
    static final String HASH_SALT_GEN = "_hash_salt_gen";
    private static final int MAX_SALT_DAYS = 100;
    private static final String TAG = "HashedStringCache";
    private final MessageDigest mDigester;
    private byte[] mSalt;
    private int mSaltGen;
    private SharedPreferences mSharedPreferences;
    private static HashedStringCache sHashedStringCache = null;
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Object mPreferenceLock = new Object();
    private final LruCache<String, String> mHashes = new LruCache<>(100);
    private final SecureRandom mSecureRandom = new SecureRandom();

    private HashedStringCache() {
        try {
            this.mDigester = MessageDigest.getInstance(KeyProperties.DIGEST_MD5);
        } catch (NoSuchAlgorithmException impossible) {
            throw new RuntimeException(impossible);
        }
    }

    public static HashedStringCache getInstance() {
        if (sHashedStringCache == null) {
            sHashedStringCache = new HashedStringCache();
        }
        return sHashedStringCache;
    }

    public HashResult hashString(Context context, String tag, String clearText, int saltExpirationDays) {
        if (saltExpirationDays == -1 || context == null || TextUtils.isEmpty(clearText) || TextUtils.isEmpty(tag)) {
            return null;
        }
        populateSaltValues(context, tag, saltExpirationDays);
        String hashText = this.mHashes.get(clearText);
        if (hashText != null) {
            return new HashResult(hashText, this.mSaltGen);
        }
        this.mDigester.reset();
        this.mDigester.update(this.mSalt);
        this.mDigester.update(clearText.getBytes(UTF_8));
        byte[] bytes = this.mDigester.digest();
        int len = Math.min(8, bytes.length);
        String hashText2 = Base64.encodeToString(bytes, 0, len, 3);
        this.mHashes.put(clearText, hashText2);
        return new HashResult(hashText2, this.mSaltGen);
    }

    private boolean checkNeedsNewSalt(String tag, int saltExpirationDays, long saltDate) {
        int saltExpirationDays2 = saltExpirationDays;
        if (saltDate == 0 || saltExpirationDays2 < -1) {
            return true;
        }
        if (saltExpirationDays2 > 100) {
            saltExpirationDays2 = 100;
        }
        long now = System.currentTimeMillis();
        long delta = now - saltDate;
        return delta >= ((long) saltExpirationDays2) * 86400000 || delta < 0;
    }

    private void populateSaltValues(Context context, String tag, int saltExpirationDays) {
        synchronized (this.mPreferenceLock) {
            SharedPreferences hashSharedPreferences = getHashSharedPreferences(context);
            this.mSharedPreferences = hashSharedPreferences;
            long saltDate = hashSharedPreferences.getLong(tag + HASH_SALT_DATE, 0L);
            boolean needsNewSalt = checkNeedsNewSalt(tag, saltExpirationDays, saltDate);
            if (needsNewSalt) {
                this.mHashes.evictAll();
            }
            if (this.mSalt == null || needsNewSalt) {
                String saltString = this.mSharedPreferences.getString(tag + HASH_SALT, null);
                int i = this.mSharedPreferences.getInt(tag + HASH_SALT_GEN, 0);
                this.mSaltGen = i;
                if (saltString == null || needsNewSalt) {
                    this.mSaltGen = i + 1;
                    byte[] saltBytes = new byte[16];
                    this.mSecureRandom.nextBytes(saltBytes);
                    saltString = Base64.encodeToString(saltBytes, 3);
                    this.mSharedPreferences.edit().putString(tag + HASH_SALT, saltString).putInt(tag + HASH_SALT_GEN, this.mSaltGen).putLong(tag + HASH_SALT_DATE, System.currentTimeMillis()).apply();
                }
                this.mSalt = saltString.getBytes(UTF_8);
            }
        }
    }

    private SharedPreferences getHashSharedPreferences(Context context) {
        File prefsFile = new File(new File(Environment.getDataUserCePackageDirectory(StorageManager.UUID_PRIVATE_INTERNAL, context.getUserId(), context.getPackageName()), "shared_prefs"), "hashed_cache.xml");
        return context.getSharedPreferences(prefsFile, 0);
    }

    /* loaded from: classes3.dex */
    public class HashResult {
        public String hashedString;
        public int saltGeneration;

        public HashResult(String hString, int saltGen) {
            this.hashedString = hString;
            this.saltGeneration = saltGen;
        }
    }
}
