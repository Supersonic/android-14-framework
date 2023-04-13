package android.speech.tts;

import android.media.AudioFormat;
import android.speech.tts.TextToSpeechService;
import android.util.Log;
import com.android.net.module.util.NetworkStackConstants;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
/* loaded from: classes3.dex */
class FileSynthesisCallback extends AbstractSynthesisCallback {
    private static final boolean DBG = false;
    private static final int MAX_AUDIO_BUFFER_SIZE = 8192;
    private static final String TAG = "FileSynthesisRequest";
    private static final short WAV_FORMAT_PCM = 1;
    private static final int WAV_HEADER_LENGTH = 44;
    private int mAudioFormat;
    private int mChannelCount;
    private final TextToSpeechService.UtteranceProgressDispatcher mDispatcher;
    private boolean mDone;
    private FileChannel mFileChannel;
    private int mSampleRateInHz;
    private boolean mStarted;
    private final Object mStateLock;
    protected int mStatusCode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileSynthesisCallback(FileChannel fileChannel, TextToSpeechService.UtteranceProgressDispatcher dispatcher, boolean clientIsUsingV2) {
        super(clientIsUsingV2);
        this.mStateLock = new Object();
        this.mStarted = false;
        this.mDone = false;
        this.mFileChannel = fileChannel;
        this.mDispatcher = dispatcher;
        this.mStatusCode = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.speech.tts.AbstractSynthesisCallback
    public void stop() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                return;
            }
            if (this.mStatusCode == -2) {
                return;
            }
            this.mStatusCode = -2;
            cleanUp();
            this.mDispatcher.dispatchOnStop();
        }
    }

    private void cleanUp() {
        closeFile();
    }

    private void closeFile() {
        this.mFileChannel = null;
    }

    @Override // android.speech.tts.SynthesisCallback
    public int getMaxBufferSize() {
        return 8192;
    }

    @Override // android.speech.tts.SynthesisCallback
    public int start(int sampleRateInHz, int audioFormat, int channelCount) {
        if (audioFormat != 3 && audioFormat != 2 && audioFormat != 4) {
            Log.m110e(TAG, "Audio format encoding " + audioFormat + " not supported. Please use one of AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT or AudioFormat.ENCODING_PCM_FLOAT");
        }
        this.mDispatcher.dispatchOnBeginSynthesis(sampleRateInHz, audioFormat, channelCount);
        synchronized (this.mStateLock) {
            int i = this.mStatusCode;
            if (i == -2) {
                return errorCodeOnStop();
            } else if (i != 0) {
                return -1;
            } else {
                if (this.mStarted) {
                    Log.m110e(TAG, "Start called twice");
                    return -1;
                }
                this.mStarted = true;
                this.mSampleRateInHz = sampleRateInHz;
                this.mAudioFormat = audioFormat;
                this.mChannelCount = channelCount;
                this.mDispatcher.dispatchOnStart();
                FileChannel fileChannel = this.mFileChannel;
                try {
                    fileChannel.write(ByteBuffer.allocate(44));
                    return 0;
                } catch (IOException ex) {
                    Log.m109e(TAG, "Failed to write wav header to output file descriptor", ex);
                    synchronized (this.mStateLock) {
                        cleanUp();
                        this.mStatusCode = -5;
                        return -1;
                    }
                }
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int audioAvailable(byte[] buffer, int offset, int length) {
        synchronized (this.mStateLock) {
            int i = this.mStatusCode;
            if (i == -2) {
                return errorCodeOnStop();
            } else if (i != 0) {
                return -1;
            } else {
                FileChannel fileChannel = this.mFileChannel;
                if (fileChannel == null) {
                    Log.m110e(TAG, "File not open");
                    this.mStatusCode = -5;
                    return -1;
                } else if (!this.mStarted) {
                    Log.m110e(TAG, "Start method was not called");
                    return -1;
                } else {
                    byte[] bufferCopy = new byte[length];
                    System.arraycopy(buffer, offset, bufferCopy, 0, length);
                    this.mDispatcher.dispatchOnAudioAvailable(bufferCopy);
                    try {
                        fileChannel.write(ByteBuffer.wrap(buffer, offset, length));
                        return 0;
                    } catch (IOException ex) {
                        Log.m109e(TAG, "Failed to write to output file descriptor", ex);
                        synchronized (this.mStateLock) {
                            cleanUp();
                            this.mStatusCode = -5;
                            return -1;
                        }
                    }
                }
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public int done() {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                Log.m104w(TAG, "Duplicate call to done()");
                return -1;
            }
            int i = this.mStatusCode;
            if (i == -2) {
                return errorCodeOnStop();
            } else if (i != 0 && i != -2) {
                this.mDispatcher.dispatchOnError(i);
                return -1;
            } else {
                FileChannel fileChannel = this.mFileChannel;
                if (fileChannel == null) {
                    Log.m110e(TAG, "File not open");
                    return -1;
                }
                this.mDone = true;
                int sampleRateInHz = this.mSampleRateInHz;
                int audioFormat = this.mAudioFormat;
                int channelCount = this.mChannelCount;
                try {
                    fileChannel.position(0L);
                    int dataLength = (int) (fileChannel.size() - 44);
                    fileChannel.write(makeWavHeader(sampleRateInHz, audioFormat, channelCount, dataLength));
                    synchronized (this.mStateLock) {
                        closeFile();
                        this.mDispatcher.dispatchOnSuccess();
                    }
                    return 0;
                } catch (IOException ex) {
                    Log.m109e(TAG, "Failed to write to output file descriptor", ex);
                    synchronized (this.mStateLock) {
                        cleanUp();
                        return -1;
                    }
                }
            }
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error() {
        error(-3);
    }

    @Override // android.speech.tts.SynthesisCallback
    public void error(int errorCode) {
        synchronized (this.mStateLock) {
            if (this.mDone) {
                return;
            }
            cleanUp();
            this.mStatusCode = errorCode;
        }
    }

    @Override // android.speech.tts.SynthesisCallback
    public boolean hasStarted() {
        boolean z;
        synchronized (this.mStateLock) {
            z = this.mStarted;
        }
        return z;
    }

    @Override // android.speech.tts.SynthesisCallback
    public boolean hasFinished() {
        boolean z;
        synchronized (this.mStateLock) {
            z = this.mDone;
        }
        return z;
    }

    private ByteBuffer makeWavHeader(int sampleRateInHz, int audioFormat, int channelCount, int dataLength) {
        int sampleSizeInBytes = AudioFormat.getBytesPerSample(audioFormat);
        int byteRate = sampleRateInHz * sampleSizeInBytes * channelCount;
        short blockAlign = (short) (sampleSizeInBytes * channelCount);
        short bitsPerSample = (short) (sampleSizeInBytes * 8);
        byte[] headerBuf = new byte[44];
        ByteBuffer header = ByteBuffer.wrap(headerBuf);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.put(new byte[]{82, 73, 70, 70});
        header.putInt((dataLength + 44) - 8);
        header.put(new byte[]{87, 65, 86, 69});
        header.put(new byte[]{102, 109, 116, NetworkStackConstants.TCPHDR_URG});
        header.putInt(16);
        header.putShort((short) 1);
        header.putShort((short) channelCount);
        header.putInt(sampleRateInHz);
        header.putInt(byteRate);
        header.putShort(blockAlign);
        header.putShort(bitsPerSample);
        header.put(new byte[]{100, 97, 116, 97});
        header.putInt(dataLength);
        header.flip();
        return header;
    }

    @Override // android.speech.tts.SynthesisCallback
    public void rangeStart(int markerInFrames, int start, int end) {
        this.mDispatcher.dispatchOnRangeStart(markerInFrames, start, end);
    }
}
