package android.hardware.camera2;

import android.hardware.camera2.params.MultiResolutionStreamInfo;
import android.media.Image;
import android.media.ImageReader;
import android.view.Surface;
import java.util.Collection;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class MultiResolutionImageReader implements AutoCloseable {
    private static final String TAG = "MultiResolutionImageReader";
    private final int mFormat;
    private final int mMaxImages;
    private final ImageReader[] mReaders;
    private final MultiResolutionStreamInfo[] mStreamInfo;

    public MultiResolutionImageReader(Collection<MultiResolutionStreamInfo> streams, int format, int maxImages) {
        this.mFormat = format;
        this.mMaxImages = maxImages;
        if (streams == null || streams.size() <= 1) {
            throw new IllegalArgumentException("The streams info collection must contain at least 2 entries");
        }
        if (maxImages < 1) {
            throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
        }
        if (format == 17) {
            throw new IllegalArgumentException("NV21 format is not supported");
        }
        int numImageReaders = streams.size();
        this.mReaders = new ImageReader[numImageReaders];
        this.mStreamInfo = new MultiResolutionStreamInfo[numImageReaders];
        int index = 0;
        for (MultiResolutionStreamInfo streamInfo : streams) {
            this.mReaders[index] = ImageReader.newInstance(streamInfo.getWidth(), streamInfo.getHeight(), format, maxImages);
            this.mStreamInfo[index] = streamInfo;
            index++;
        }
    }

    public void setOnImageAvailableListener(ImageReader.OnImageAvailableListener listener, Executor executor) {
        int i = 0;
        while (true) {
            ImageReader[] imageReaderArr = this.mReaders;
            if (i < imageReaderArr.length) {
                imageReaderArr[i].setOnImageAvailableListenerWithExecutor(listener, executor);
                i++;
            } else {
                return;
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        flush();
        int i = 0;
        while (true) {
            ImageReader[] imageReaderArr = this.mReaders;
            if (i < imageReaderArr.length) {
                imageReaderArr[i].close();
                i++;
            } else {
                return;
            }
        }
    }

    protected void finalize() {
        close();
    }

    public void flush() {
        flushOther(null);
    }

    public void flushOther(ImageReader reader) {
        int i = 0;
        while (true) {
            ImageReader[] imageReaderArr = this.mReaders;
            if (i < imageReaderArr.length) {
                if (reader == null || reader != imageReaderArr[i]) {
                    while (true) {
                        Image image = this.mReaders[i].acquireNextImageNoThrowISE();
                        if (image == null) {
                            break;
                        }
                        image.close();
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public ImageReader[] getReaders() {
        return this.mReaders;
    }

    public Surface getSurface() {
        int minReaderSize = this.mReaders[0].getWidth() * this.mReaders[0].getHeight();
        Surface candidateSurface = this.mReaders[0].getSurface();
        int i = 1;
        while (true) {
            ImageReader[] imageReaderArr = this.mReaders;
            if (i < imageReaderArr.length) {
                int readerSize = imageReaderArr[i].getWidth() * this.mReaders[i].getHeight();
                if (readerSize < minReaderSize) {
                    minReaderSize = readerSize;
                    candidateSurface = this.mReaders[i].getSurface();
                }
                i++;
            } else {
                return candidateSurface;
            }
        }
    }

    public MultiResolutionStreamInfo getStreamInfoForImageReader(ImageReader reader) {
        int i = 0;
        while (true) {
            ImageReader[] imageReaderArr = this.mReaders;
            if (i < imageReaderArr.length) {
                if (reader != imageReaderArr[i]) {
                    i++;
                } else {
                    return this.mStreamInfo[i];
                }
            } else {
                throw new IllegalArgumentException("ImageReader doesn't belong to this multi-resolution imagereader");
            }
        }
    }
}
