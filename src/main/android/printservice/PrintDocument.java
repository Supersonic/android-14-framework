package android.printservice;

import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.print.PrintDocumentInfo;
import android.print.PrintJobId;
import android.util.Log;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class PrintDocument {
    private static final String LOG_TAG = "PrintDocument";
    private final PrintDocumentInfo mInfo;
    private final PrintJobId mPrintJobId;
    private final IPrintServiceClient mPrintServiceClient;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PrintDocument(PrintJobId printJobId, IPrintServiceClient printServiceClient, PrintDocumentInfo info) {
        this.mPrintJobId = printJobId;
        this.mPrintServiceClient = printServiceClient;
        this.mInfo = info;
    }

    public PrintDocumentInfo getInfo() {
        PrintService.throwIfNotCalledOnMainThread();
        return this.mInfo;
    }

    public ParcelFileDescriptor getData() {
        PrintService.throwIfNotCalledOnMainThread();
        ParcelFileDescriptor sink = null;
        try {
            try {
                try {
                    try {
                        ParcelFileDescriptor[] fds = ParcelFileDescriptor.createPipe();
                        ParcelFileDescriptor source = fds[0];
                        sink = fds[1];
                        this.mPrintServiceClient.writePrintJobData(sink, this.mPrintJobId);
                        return source;
                    } finally {
                        if (sink != null) {
                            try {
                                sink.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                } catch (IOException ioe) {
                    Log.m109e(LOG_TAG, "Error calling getting print job data!", ioe);
                    if (sink != null) {
                        sink.close();
                        return null;
                    }
                    return null;
                }
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error calling getting print job data!", re);
                if (sink != null) {
                    sink.close();
                    return null;
                }
                return null;
            }
        } catch (IOException e2) {
            return null;
        }
    }
}
