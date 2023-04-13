package android.view.inputmethod;

import android.p008os.Parcel;
import android.util.Slog;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
/* loaded from: classes4.dex */
public class InputMethodSubtypeArray {
    private static final String TAG = "InputMethodSubtypeArray";
    private volatile byte[] mCompressedData;
    private final int mCount;
    private volatile int mDecompressedSize;
    private volatile InputMethodSubtype[] mInstance;
    private final Object mLockObject = new Object();

    public InputMethodSubtypeArray(List<InputMethodSubtype> subtypes) {
        if (subtypes == null) {
            this.mCount = 0;
            return;
        }
        int size = subtypes.size();
        this.mCount = size;
        this.mInstance = (InputMethodSubtype[]) subtypes.toArray(new InputMethodSubtype[size]);
    }

    public InputMethodSubtypeArray(Parcel source) {
        int readInt = source.readInt();
        this.mCount = readInt;
        if (readInt > 0) {
            this.mDecompressedSize = source.readInt();
            this.mCompressedData = source.createByteArray();
        }
    }

    public void writeToParcel(Parcel dest) {
        int i = this.mCount;
        if (i == 0) {
            dest.writeInt(i);
            return;
        }
        byte[] compressedData = this.mCompressedData;
        int decompressedSize = this.mDecompressedSize;
        if (compressedData == null && decompressedSize == 0) {
            synchronized (this.mLockObject) {
                compressedData = this.mCompressedData;
                decompressedSize = this.mDecompressedSize;
                if (compressedData == null && decompressedSize == 0) {
                    byte[] decompressedData = marshall(this.mInstance);
                    compressedData = compress(decompressedData);
                    if (compressedData == null) {
                        decompressedSize = -1;
                        Slog.m94i(TAG, "Failed to compress data.");
                    } else {
                        decompressedSize = decompressedData.length;
                    }
                    this.mDecompressedSize = decompressedSize;
                    this.mCompressedData = compressedData;
                }
            }
        }
        if (compressedData != null && decompressedSize > 0) {
            dest.writeInt(this.mCount);
            dest.writeInt(decompressedSize);
            dest.writeByteArray(compressedData);
            return;
        }
        Slog.m94i(TAG, "Unexpected state. Behaving as an empty array.");
        dest.writeInt(0);
    }

    public InputMethodSubtype get(int index) {
        if (index < 0 || this.mCount <= index) {
            throw new ArrayIndexOutOfBoundsException();
        }
        InputMethodSubtype[] instance = this.mInstance;
        if (instance == null) {
            synchronized (this.mLockObject) {
                instance = this.mInstance;
                if (instance == null) {
                    byte[] decompressedData = decompress(this.mCompressedData, this.mDecompressedSize);
                    this.mCompressedData = null;
                    this.mDecompressedSize = 0;
                    if (decompressedData != null) {
                        instance = unmarshall(decompressedData);
                    } else {
                        Slog.m96e(TAG, "Failed to decompress data. Returns null as fallback.");
                        instance = new InputMethodSubtype[this.mCount];
                    }
                    this.mInstance = instance;
                }
            }
        }
        return instance[index];
    }

    public int getCount() {
        return this.mCount;
    }

    private static byte[] marshall(InputMethodSubtype[] array) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.writeTypedArray(array, 0);
            return parcel.marshall();
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    private static InputMethodSubtype[] unmarshall(byte[] data) {
        Parcel parcel = null;
        try {
            parcel = Parcel.obtain();
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            return (InputMethodSubtype[]) parcel.createTypedArray(InputMethodSubtype.CREATOR);
        } finally {
            if (parcel != null) {
                parcel.recycle();
            }
        }
    }

    private static byte[] compress(byte[] data) {
        try {
            ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
            GZIPOutputStream zipper = new GZIPOutputStream(resultStream);
            try {
                zipper.write(data);
                zipper.finish();
                byte[] byteArray = resultStream.toByteArray();
                zipper.close();
                resultStream.close();
                return byteArray;
            } catch (Throwable th) {
                try {
                    zipper.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            Slog.m95e(TAG, "Failed to compress the data.", e);
            return null;
        }
    }

    private static byte[] decompress(byte[] data, int expectedSize) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            GZIPInputStream unzipper = new GZIPInputStream(inputStream);
            byte[] result = new byte[expectedSize];
            int totalReadBytes = 0;
            while (totalReadBytes < result.length) {
                int restBytes = result.length - totalReadBytes;
                int readBytes = unzipper.read(result, totalReadBytes, restBytes);
                if (readBytes < 0) {
                    break;
                }
                totalReadBytes += readBytes;
            }
            if (expectedSize != totalReadBytes) {
                unzipper.close();
                inputStream.close();
                return null;
            }
            unzipper.close();
            inputStream.close();
            return result;
        } catch (Exception e) {
            Slog.m95e(TAG, "Failed to decompress the data.", e);
            return null;
        }
    }
}
