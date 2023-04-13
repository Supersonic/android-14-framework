package android.telephony;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteCallback;
import android.telephony.ICellBroadcastService;
import android.telephony.cdma.CdmaSmsCbProgramData;
import com.android.internal.util.FastPrintWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public abstract class CellBroadcastService extends Service {
    public static final String CELL_BROADCAST_SERVICE_INTERFACE = "android.telephony.CellBroadcastService";
    private final ICellBroadcastService.Stub mStubWrapper = new ICellBroadcastServiceWrapper();

    public abstract CharSequence getCellBroadcastAreaInfo(int i);

    public abstract void onCdmaCellBroadcastSms(int i, byte[] bArr, int i2);

    public abstract void onCdmaScpMessage(int i, List<CdmaSmsCbProgramData> list, String str, Consumer<Bundle> consumer);

    public abstract void onGsmCellBroadcastSms(int i, byte[] bArr);

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mStubWrapper;
    }

    /* loaded from: classes3.dex */
    public class ICellBroadcastServiceWrapper extends ICellBroadcastService.Stub {
        public ICellBroadcastServiceWrapper() {
        }

        @Override // android.telephony.ICellBroadcastService
        public void handleGsmCellBroadcastSms(int slotIndex, byte[] message) {
            CellBroadcastService.this.onGsmCellBroadcastSms(slotIndex, message);
        }

        @Override // android.telephony.ICellBroadcastService
        public void handleCdmaCellBroadcastSms(int slotIndex, byte[] bearerData, int serviceCategory) {
            CellBroadcastService.this.onCdmaCellBroadcastSms(slotIndex, bearerData, serviceCategory);
        }

        @Override // android.telephony.ICellBroadcastService
        public void handleCdmaScpMessage(int slotIndex, List<CdmaSmsCbProgramData> smsCbProgramData, String originatingAddress, final RemoteCallback callback) {
            Consumer<Bundle> consumer = new Consumer() { // from class: android.telephony.CellBroadcastService$ICellBroadcastServiceWrapper$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    RemoteCallback.this.sendResult((Bundle) obj);
                }
            };
            CellBroadcastService.this.onCdmaScpMessage(slotIndex, smsCbProgramData, originatingAddress, consumer);
        }

        @Override // android.telephony.ICellBroadcastService
        public CharSequence getCellBroadcastAreaInfo(int slotIndex) {
            return CellBroadcastService.this.getCellBroadcastAreaInfo(slotIndex);
        }

        @Override // android.p008os.Binder
        protected void dump(FileDescriptor fd, PrintWriter fout, String[] args) {
            CellBroadcastService.this.dump(fd, fout, args);
        }

        @Override // android.p008os.Binder, android.p008os.IBinder
        public void dump(FileDescriptor fd, String[] args) {
            PrintWriter pw = new FastPrintWriter(new FileOutputStream(fd));
            CellBroadcastService.this.dump(fd, pw, args);
        }
    }
}
