package android.media;

import android.annotation.SystemApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.MediaMetrics;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.FileUtils;
import android.p008os.IBinder;
import android.p008os.ParcelFileDescriptor;
import android.p008os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public class MediaMetadataRetriever implements AutoCloseable {
    private static final int EMBEDDED_PICTURE_TYPE_ANY = 65535;
    public static final int METADATA_KEY_ALBUM = 1;
    public static final int METADATA_KEY_ALBUMARTIST = 13;
    public static final int METADATA_KEY_ARTIST = 2;
    public static final int METADATA_KEY_AUTHOR = 3;
    public static final int METADATA_KEY_BITRATE = 20;
    public static final int METADATA_KEY_BITS_PER_SAMPLE = 39;
    public static final int METADATA_KEY_CAPTURE_FRAMERATE = 25;
    public static final int METADATA_KEY_CD_TRACK_NUMBER = 0;
    public static final int METADATA_KEY_COLOR_RANGE = 37;
    public static final int METADATA_KEY_COLOR_STANDARD = 35;
    public static final int METADATA_KEY_COLOR_TRANSFER = 36;
    public static final int METADATA_KEY_COMPILATION = 15;
    public static final int METADATA_KEY_COMPOSER = 4;
    public static final int METADATA_KEY_DATE = 5;
    public static final int METADATA_KEY_DISC_NUMBER = 14;
    public static final int METADATA_KEY_DURATION = 9;
    public static final int METADATA_KEY_EXIF_LENGTH = 34;
    public static final int METADATA_KEY_EXIF_OFFSET = 33;
    public static final int METADATA_KEY_GENRE = 6;
    public static final int METADATA_KEY_HAS_AUDIO = 16;
    public static final int METADATA_KEY_HAS_IMAGE = 26;
    public static final int METADATA_KEY_HAS_VIDEO = 17;
    public static final int METADATA_KEY_IMAGE_COUNT = 27;
    public static final int METADATA_KEY_IMAGE_HEIGHT = 30;
    public static final int METADATA_KEY_IMAGE_PRIMARY = 28;
    public static final int METADATA_KEY_IMAGE_ROTATION = 31;
    public static final int METADATA_KEY_IMAGE_WIDTH = 29;
    public static final int METADATA_KEY_IS_DRM = 22;
    public static final int METADATA_KEY_LOCATION = 23;
    public static final int METADATA_KEY_MIMETYPE = 12;
    public static final int METADATA_KEY_NUM_TRACKS = 10;
    public static final int METADATA_KEY_SAMPLERATE = 38;
    public static final int METADATA_KEY_TIMED_TEXT_LANGUAGES = 21;
    public static final int METADATA_KEY_TITLE = 7;
    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public static final int METADATA_KEY_VIDEO_CODEC_MIME_TYPE = 40;
    public static final int METADATA_KEY_VIDEO_FRAME_COUNT = 32;
    public static final int METADATA_KEY_VIDEO_HEIGHT = 19;
    public static final int METADATA_KEY_VIDEO_ROTATION = 24;
    public static final int METADATA_KEY_VIDEO_WIDTH = 18;
    public static final int METADATA_KEY_WRITER = 11;
    public static final int METADATA_KEY_XMP_LENGTH = 42;
    public static final int METADATA_KEY_XMP_OFFSET = 41;
    public static final int METADATA_KEY_YEAR = 8;
    public static final int OPTION_CLOSEST = 3;
    public static final int OPTION_CLOSEST_SYNC = 2;
    public static final int OPTION_NEXT_SYNC = 1;
    public static final int OPTION_PREVIOUS_SYNC = 0;
    private static final String[] STANDARD_GENRES = {"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Bass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A capella", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Afro-Punk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "Jpop", "Synthpop"};
    private static final String TAG = "MediaMetadataRetriever";
    private long mNativeContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface Option {
    }

    private native List<Bitmap> _getFrameAtIndex(int i, int i2, BitmapParams bitmapParams);

    private native Bitmap _getFrameAtTime(long j, int i, int i2, int i3, BitmapParams bitmapParams);

    private native Bitmap _getImageAtIndex(int i, BitmapParams bitmapParams);

    private native void _setDataSource(MediaDataSource mediaDataSource) throws IllegalArgumentException;

    private native void _setDataSource(IBinder iBinder, String str, String[] strArr, String[] strArr2) throws IllegalArgumentException;

    private native void _setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IllegalArgumentException;

    private native byte[] getEmbeddedPicture(int i);

    private native String nativeExtractMetadata(int i);

    private final native void native_finalize();

    private static native void native_init();

    private native void native_setup();

    public native Bitmap getThumbnailImageAtIndex(int i, BitmapParams bitmapParams, int i2, int i3);

    public native void release() throws IOException;

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public MediaMetadataRetriever() {
        native_setup();
    }

    public void setDataSource(String path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("null path");
        }
        Uri uri = Uri.parse(path);
        String scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            path = uri.getPath();
        } else if (scheme != null) {
            setDataSource(path, new HashMap());
            return;
        }
        try {
            FileInputStream is = new FileInputStream(path);
            try {
                FileDescriptor fd = is.getFD();
                setDataSource(fd, 0L, 576460752303423487L);
                is.close();
            } catch (Throwable th) {
                try {
                    is.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(path + " does not exist");
        } catch (IOException e2) {
            throw new IllegalArgumentException("couldn't open " + path);
        }
    }

    public void setDataSource(String uri, Map<String, String> headers) throws IllegalArgumentException {
        int i = 0;
        String[] keys = new String[headers.size()];
        String[] values = new String[headers.size()];
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            keys[i] = entry.getKey();
            values[i] = entry.getValue();
            i++;
        }
        _setDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(uri), uri, keys, values);
    }

    public void setDataSource(FileDescriptor fd, long offset, long length) throws IllegalArgumentException {
        try {
            ParcelFileDescriptor modernFd = FileUtils.convertToModernFd(fd);
            if (modernFd == null) {
                _setDataSource(fd, offset, length);
            } else {
                _setDataSource(modernFd.getFileDescriptor(), offset, length);
            }
            if (modernFd != null) {
                modernFd.close();
            }
        } catch (IOException e) {
            Log.m103w(TAG, "Ignoring IO error while setting data source", e);
        }
    }

    public void setDataSource(FileDescriptor fd) throws IllegalArgumentException {
        setDataSource(fd, 0L, 576460752303423487L);
    }

    public void setDataSource(Context context, Uri uri) throws IllegalArgumentException, SecurityException {
        if (uri == null) {
            throw new IllegalArgumentException("null uri");
        }
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("file")) {
            setDataSource(uri.getPath());
            return;
        }
        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            try {
                boolean optimize = SystemProperties.getBoolean("fuse.sys.transcode_retriever_optimize", false);
                Bundle opts = new Bundle();
                opts.putBoolean("android.provider.extra.ACCEPT_ORIGINAL_MEDIA_FORMAT", true);
                AssetFileDescriptor fd2 = optimize ? resolver.openTypedAssetFileDescriptor(uri, "*/*", opts) : resolver.openAssetFileDescriptor(uri, "r");
                if (fd2 == null) {
                    throw new IllegalArgumentException("got null FileDescriptor for " + uri);
                }
                FileDescriptor descriptor = fd2.getFileDescriptor();
                if (!descriptor.valid()) {
                    throw new IllegalArgumentException("got invalid FileDescriptor for " + uri);
                }
                if (fd2.getDeclaredLength() < 0) {
                    setDataSource(descriptor);
                } else {
                    setDataSource(descriptor, fd2.getStartOffset(), fd2.getDeclaredLength());
                }
                if (fd2 == null) {
                    return;
                }
                try {
                    fd2.close();
                } catch (IOException e) {
                }
            } catch (FileNotFoundException e2) {
                throw new IllegalArgumentException("could not access " + uri);
            }
        } catch (SecurityException e3) {
            if (0 != 0) {
                try {
                    fd.close();
                } catch (IOException e4) {
                }
            }
            setDataSource(uri.toString());
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    fd.close();
                } catch (IOException e5) {
                }
            }
            throw th;
        }
    }

    public void setDataSource(MediaDataSource dataSource) throws IllegalArgumentException {
        _setDataSource(dataSource);
    }

    public String extractMetadata(int keyCode) {
        String meta = nativeExtractMetadata(keyCode);
        if (keyCode == 6) {
            return convertGenreTag(meta);
        }
        return meta;
    }

    /* JADX WARN: Code restructure failed: missing block: B:58:0x00cb, code lost:
        return null;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private String convertGenreTag(String meta) {
        if (TextUtils.isEmpty(meta)) {
            return null;
        }
        if (Character.isDigit(meta.charAt(0))) {
            try {
                int genreIndex = Integer.parseInt(meta);
                if (genreIndex >= 0) {
                    String[] strArr = STANDARD_GENRES;
                    if (genreIndex < strArr.length) {
                        return strArr[genreIndex];
                    }
                }
            } catch (NumberFormatException e) {
            }
            return null;
        }
        StringBuilder genres = null;
        String nextGenre = null;
        while (true) {
            if (!TextUtils.isEmpty(nextGenre)) {
                if (genres == null) {
                    genres = new StringBuilder();
                }
                if (genres.length() != 0) {
                    genres.append(", ");
                }
                genres.append(nextGenre);
            }
            if (!TextUtils.isEmpty(meta)) {
                if (meta.startsWith("(RX)")) {
                    nextGenre = "Remix";
                    meta = meta.substring(4);
                } else if (meta.startsWith("(CR)")) {
                    nextGenre = "Cover";
                    meta = meta.substring(4);
                } else if (meta.startsWith("((")) {
                    int closeParenOffset = meta.indexOf(41);
                    if (closeParenOffset == -1) {
                        nextGenre = meta.substring(1);
                        meta = "";
                    } else {
                        nextGenre = meta.substring(1, closeParenOffset + 1);
                        meta = meta.substring(closeParenOffset + 1);
                    }
                } else if (meta.startsWith(NavigationBarInflaterView.KEY_CODE_START)) {
                    int closeParenOffset2 = meta.indexOf(41);
                    if (closeParenOffset2 == -1) {
                        return null;
                    }
                    String genreNumString = meta.substring(1, closeParenOffset2);
                    try {
                        int genreIndex2 = Integer.parseInt(genreNumString.toString());
                        if (genreIndex2 < 0) {
                            break;
                        }
                        String[] strArr2 = STANDARD_GENRES;
                        if (genreIndex2 >= strArr2.length) {
                            break;
                        }
                        nextGenre = strArr2[genreIndex2];
                        meta = meta.substring(closeParenOffset2 + 1);
                    } catch (NumberFormatException e2) {
                        return null;
                    }
                } else {
                    nextGenre = meta;
                    meta = "";
                }
            } else if (genres == null || genres.length() == 0) {
                return null;
            } else {
                return genres.toString();
            }
        }
    }

    public Bitmap getFrameAtTime(long timeUs, int option) {
        if (option < 0 || option > 3) {
            throw new IllegalArgumentException("Unsupported option: " + option);
        }
        return _getFrameAtTime(timeUs, option, -1, -1, null);
    }

    public Bitmap getFrameAtTime(long timeUs, int option, BitmapParams params) {
        if (option < 0 || option > 3) {
            throw new IllegalArgumentException("Unsupported option: " + option);
        }
        return _getFrameAtTime(timeUs, option, -1, -1, params);
    }

    public Bitmap getScaledFrameAtTime(long timeUs, int option, int dstWidth, int dstHeight) {
        validate(option, dstWidth, dstHeight);
        return _getFrameAtTime(timeUs, option, dstWidth, dstHeight, null);
    }

    public Bitmap getScaledFrameAtTime(long timeUs, int option, int dstWidth, int dstHeight, BitmapParams params) {
        validate(option, dstWidth, dstHeight);
        return _getFrameAtTime(timeUs, option, dstWidth, dstHeight, params);
    }

    private void validate(int option, int dstWidth, int dstHeight) {
        if (option < 0 || option > 3) {
            throw new IllegalArgumentException("Unsupported option: " + option);
        }
        if (dstWidth <= 0) {
            throw new IllegalArgumentException("Invalid width: " + dstWidth);
        }
        if (dstHeight <= 0) {
            throw new IllegalArgumentException("Invalid height: " + dstHeight);
        }
    }

    public Bitmap getFrameAtTime(long timeUs) {
        return getFrameAtTime(timeUs, 2);
    }

    public Bitmap getFrameAtTime() {
        return _getFrameAtTime(-1L, 2, -1, -1, null);
    }

    /* loaded from: classes2.dex */
    public static final class BitmapParams {
        private Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;
        private Bitmap.Config outActualConfig = Bitmap.Config.ARGB_8888;

        public void setPreferredConfig(Bitmap.Config config) {
            if (config == null) {
                throw new IllegalArgumentException("preferred config can't be null");
            }
            this.inPreferredConfig = config;
        }

        public Bitmap.Config getPreferredConfig() {
            return this.inPreferredConfig;
        }

        public Bitmap.Config getActualConfig() {
            return this.outActualConfig;
        }
    }

    public Bitmap getFrameAtIndex(int frameIndex, BitmapParams params) {
        List<Bitmap> bitmaps = getFramesAtIndex(frameIndex, 1, params);
        return bitmaps.get(0);
    }

    public Bitmap getFrameAtIndex(int frameIndex) {
        List<Bitmap> bitmaps = getFramesAtIndex(frameIndex, 1);
        return bitmaps.get(0);
    }

    public List<Bitmap> getFramesAtIndex(int frameIndex, int numFrames, BitmapParams params) {
        return getFramesAtIndexInternal(frameIndex, numFrames, params);
    }

    public List<Bitmap> getFramesAtIndex(int frameIndex, int numFrames) {
        return getFramesAtIndexInternal(frameIndex, numFrames, null);
    }

    private List<Bitmap> getFramesAtIndexInternal(int frameIndex, int numFrames, BitmapParams params) {
        if (!MediaMetrics.Value.YES.equals(extractMetadata(17))) {
            throw new IllegalStateException("Does not contain video or image sequences");
        }
        int frameCount = Integer.parseInt(extractMetadata(32));
        if (frameIndex < 0 || numFrames < 1 || frameIndex >= frameCount || frameIndex > frameCount - numFrames) {
            throw new IllegalArgumentException("Invalid frameIndex or numFrames: " + frameIndex + ", " + numFrames);
        }
        return _getFrameAtIndex(frameIndex, numFrames, params);
    }

    public Bitmap getImageAtIndex(int imageIndex, BitmapParams params) {
        return getImageAtIndexInternal(imageIndex, params);
    }

    public Bitmap getImageAtIndex(int imageIndex) {
        return getImageAtIndexInternal(imageIndex, null);
    }

    public Bitmap getPrimaryImage(BitmapParams params) {
        return getImageAtIndexInternal(-1, params);
    }

    public Bitmap getPrimaryImage() {
        return getImageAtIndexInternal(-1, null);
    }

    private Bitmap getImageAtIndexInternal(int imageIndex, BitmapParams params) {
        if (!MediaMetrics.Value.YES.equals(extractMetadata(26))) {
            throw new IllegalStateException("Does not contain still images");
        }
        String imageCount = extractMetadata(27);
        if (imageIndex >= Integer.parseInt(imageCount)) {
            throw new IllegalArgumentException("Invalid image index: " + imageCount);
        }
        return _getImageAtIndex(imageIndex, params);
    }

    public byte[] getEmbeddedPicture() {
        return getEmbeddedPicture(65535);
    }

    @Override // java.lang.AutoCloseable
    public void close() throws IOException {
        release();
    }

    protected void finalize() throws Throwable {
        try {
            native_finalize();
        } finally {
            super.finalize();
        }
    }
}
