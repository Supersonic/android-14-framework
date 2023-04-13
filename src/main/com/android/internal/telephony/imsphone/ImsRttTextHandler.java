package com.android.internal.telephony.imsphone;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telecom.Connection;
import com.android.internal.annotations.VisibleForTesting;
import com.android.telephony.Rlog;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.CountDownLatch;
/* loaded from: classes.dex */
public class ImsRttTextHandler extends Handler {
    public static final int MAX_BUFFERED_CHARACTER_COUNT = 5;
    public static final int MAX_BUFFERING_DELAY_MILLIS = 200;
    public static final int MAX_CODEPOINTS_PER_SECOND = 30;
    private StringBuffer mBufferedTextToIncall;
    private StringBuffer mBufferedTextToNetwork;
    private int mCodepointsAvailableForTransmission;
    private final NetworkWriter mNetworkWriter;
    private CountDownLatch mReadNotifier;
    private InCallReaderThread mReaderThread;
    private Connection.RttTextStream mRttTextStream;

    /* loaded from: classes.dex */
    public interface NetworkWriter {
        void write(String str);
    }

    @VisibleForTesting
    public int getSendToIncall() {
        return 3;
    }

    /* loaded from: classes.dex */
    private class InCallReaderThread extends Thread {
        private final Connection.RttTextStream mReaderThreadRttTextStream;

        public InCallReaderThread(Connection.RttTextStream rttTextStream) {
            this.mReaderThreadRttTextStream = rttTextStream;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (true) {
                try {
                    String read = this.mReaderThreadRttTextStream.read();
                    if (read == null) {
                        Rlog.e("ImsRttTextHandler", "RttReaderThread - Stream closed unexpectedly. Attempt to reinitialize.");
                        ImsRttTextHandler.this.obtainMessage(9999).sendToTarget();
                        return;
                    } else if (read.length() != 0) {
                        ImsRttTextHandler.this.obtainMessage(2, read).sendToTarget();
                        if (ImsRttTextHandler.this.mReadNotifier != null) {
                            ImsRttTextHandler.this.mReadNotifier.countDown();
                        }
                    }
                } catch (ClosedByInterruptException unused) {
                    Rlog.i("ImsRttTextHandler", "RttReaderThread - Thread interrupted. Finishing.");
                    return;
                } catch (IOException e) {
                    Rlog.e("ImsRttTextHandler", "RttReaderThread - IOException encountered reading from in-call: ", e);
                    ImsRttTextHandler.this.obtainMessage(9999).sendToTarget();
                    return;
                }
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            if (this.mRttTextStream != null || this.mReaderThread != null) {
                Rlog.e("ImsRttTextHandler", "RTT text stream already initialized. Ignoring.");
                return;
            }
            this.mRttTextStream = (Connection.RttTextStream) message.obj;
            InCallReaderThread inCallReaderThread = new InCallReaderThread(this.mRttTextStream);
            this.mReaderThread = inCallReaderThread;
            inCallReaderThread.start();
        } else if (i == 2) {
            this.mBufferedTextToNetwork.append((String) message.obj);
            StringBuffer stringBuffer = this.mBufferedTextToNetwork;
            if (stringBuffer.codePointCount(0, stringBuffer.length()) >= 5) {
                sendMessage(obtainMessage(4));
            } else {
                sendEmptyMessageDelayed(4, 200L);
            }
        } else if (i == 3) {
            Object obj = message.obj;
            if (obj == null) {
                Rlog.e("ImsRttTextHandler", "RTT msg.obj is null. Ignoring.");
                return;
            }
            String str = (String) obj;
            Connection.RttTextStream rttTextStream = this.mRttTextStream;
            if (rttTextStream == null) {
                Rlog.e("ImsRttTextHandler", "RTT text stream is null. Writing to in-call buffer.");
                this.mBufferedTextToIncall.append(str);
                return;
            }
            try {
                rttTextStream.write(str);
            } catch (IOException e) {
                Rlog.e("ImsRttTextHandler", "IOException encountered writing to in-call: %s", e);
                obtainMessage(9999).sendToTarget();
                this.mBufferedTextToIncall.append(str);
            }
        } else if (i == 4) {
            StringBuffer stringBuffer2 = this.mBufferedTextToNetwork;
            int min = Math.min(stringBuffer2.codePointCount(0, stringBuffer2.length()), this.mCodepointsAvailableForTransmission);
            if (min == 0) {
                return;
            }
            int offsetByCodePoints = this.mBufferedTextToNetwork.offsetByCodePoints(0, min);
            String substring = this.mBufferedTextToNetwork.substring(0, offsetByCodePoints);
            this.mBufferedTextToNetwork.delete(0, offsetByCodePoints);
            this.mNetworkWriter.write(substring);
            this.mCodepointsAvailableForTransmission -= min;
            sendMessageDelayed(obtainMessage(5, min, 0), 1000L);
        } else if (i == 5) {
            int i2 = this.mCodepointsAvailableForTransmission + message.arg1;
            this.mCodepointsAvailableForTransmission = i2;
            if (i2 > 0) {
                sendMessage(obtainMessage(4));
            }
        } else if (i != 9999) {
        } else {
            try {
                InCallReaderThread inCallReaderThread2 = this.mReaderThread;
                if (inCallReaderThread2 != null) {
                    inCallReaderThread2.interrupt();
                    this.mReaderThread.join(1000L);
                }
            } catch (InterruptedException unused) {
            }
            this.mReaderThread = null;
            this.mRttTextStream = null;
        }
    }

    public ImsRttTextHandler(Looper looper, NetworkWriter networkWriter) {
        super(looper);
        this.mCodepointsAvailableForTransmission = 30;
        this.mBufferedTextToNetwork = new StringBuffer();
        this.mBufferedTextToIncall = new StringBuffer();
        this.mNetworkWriter = networkWriter;
    }

    public void sendToInCall(String str) {
        obtainMessage(3, str).sendToTarget();
    }

    public void initialize(Connection.RttTextStream rttTextStream) {
        Rlog.i("ImsRttTextHandler", "Initializing: " + this);
        obtainMessage(1, rttTextStream).sendToTarget();
    }

    public void tearDown() {
        obtainMessage(9999).sendToTarget();
    }

    @VisibleForTesting
    public void setReadNotifier(CountDownLatch countDownLatch) {
        this.mReadNotifier = countDownLatch;
    }

    @VisibleForTesting
    public StringBuffer getBufferedTextToIncall() {
        return this.mBufferedTextToIncall;
    }

    @VisibleForTesting
    public void setRttTextStream(Connection.RttTextStream rttTextStream) {
        this.mRttTextStream = rttTextStream;
    }

    public String getNetworkBufferText() {
        return this.mBufferedTextToNetwork.toString();
    }
}
