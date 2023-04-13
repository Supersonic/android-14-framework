package com.android.internal.telephony.gsm;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.telephony.PhoneConfigurationManager;
import com.android.internal.telephony.imsphone.ImsRttTextHandler;
import com.android.internal.telephony.uicc.AdnRecord;
import com.android.internal.telephony.uicc.AdnRecordCache;
import com.android.internal.telephony.uicc.IccConstants;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccRecords;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.util.DnsPacket;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
/* loaded from: classes.dex */
public class UsimPhoneBookManager extends Handler implements IccConstants {
    private AdnRecordCache mAdnCache;
    private ArrayList<byte[]> mEmailFileRecord;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private IccFileHandler mFh;
    private ArrayList<byte[]> mIapFileRecord;
    @UnsupportedAppUsage
    private Object mLock = new Object();
    private boolean mRefreshCache = false;
    @UnsupportedAppUsage
    private ArrayList<AdnRecord> mPhoneBookRecords = new ArrayList<>();
    private ArrayList<PbrRecord> mPbrRecords = null;
    private Boolean mIsPbrPresent = Boolean.TRUE;
    private SparseArray<ArrayList<String>> mEmailsForAdnRec = new SparseArray<>();
    private SparseIntArray mSfiEfidTable = new SparseIntArray();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class File {
        private final int mEfid;
        private final int mIndex;
        private final int mParentTag;
        private final int mSfi;

        File(int i, int i2, int i3, int i4) {
            this.mParentTag = i;
            this.mEfid = i2;
            this.mSfi = i3;
            this.mIndex = i4;
        }

        public int getParentTag() {
            return this.mParentTag;
        }

        public int getEfid() {
            return this.mEfid;
        }

        public int getSfi() {
            return this.mSfi;
        }

        public int getIndex() {
            return this.mIndex;
        }
    }

    public UsimPhoneBookManager(IccFileHandler iccFileHandler, AdnRecordCache adnRecordCache) {
        this.mFh = iccFileHandler;
        this.mAdnCache = adnRecordCache;
    }

    @UnsupportedAppUsage
    public void reset() {
        this.mPhoneBookRecords.clear();
        this.mIapFileRecord = null;
        this.mEmailFileRecord = null;
        this.mPbrRecords = null;
        this.mIsPbrPresent = Boolean.TRUE;
        this.mRefreshCache = false;
        this.mEmailsForAdnRec.clear();
        this.mSfiEfidTable.clear();
    }

    @UnsupportedAppUsage
    public ArrayList<AdnRecord> loadEfFilesFromUsim() {
        synchronized (this.mLock) {
            if (!this.mPhoneBookRecords.isEmpty()) {
                if (this.mRefreshCache) {
                    this.mRefreshCache = false;
                    refreshCache();
                }
                return this.mPhoneBookRecords;
            } else if (this.mIsPbrPresent.booleanValue()) {
                if (this.mPbrRecords == null) {
                    readPbrFileAndWait();
                }
                ArrayList<PbrRecord> arrayList = this.mPbrRecords;
                if (arrayList == null) {
                    return null;
                }
                int size = arrayList.size();
                log("loadEfFilesFromUsim: Loading adn and emails");
                for (int i = 0; i < size; i++) {
                    readAdnFileAndWait(i);
                    readEmailFileAndWait(i);
                }
                updatePhoneAdnRecord();
                return this.mPhoneBookRecords;
            } else {
                return null;
            }
        }
    }

    private void refreshCache() {
        if (this.mPbrRecords == null) {
            return;
        }
        this.mPhoneBookRecords.clear();
        int size = this.mPbrRecords.size();
        for (int i = 0; i < size; i++) {
            readAdnFileAndWait(i);
        }
    }

    public void invalidateCache() {
        this.mRefreshCache = true;
    }

    private void readPbrFileAndWait() {
        this.mFh.loadEFLinearFixedAll(IccConstants.EF_PBR, obtainMessage(1));
        try {
            this.mLock.wait();
        } catch (InterruptedException unused) {
            Rlog.e("UsimPhoneBookManager", "Interrupted Exception in readAdnFileAndWait");
        }
    }

    private void readEmailFileAndWait(int i) {
        File file;
        SparseArray sparseArray;
        File file2;
        SparseArray sparseArray2 = this.mPbrRecords.get(i).mFileIds;
        if (sparseArray2 == null || (file = (File) sparseArray2.get(202)) == null) {
            return;
        }
        if (file.getParentTag() == 169) {
            if (sparseArray2.get(193) == null) {
                Rlog.e("UsimPhoneBookManager", "Can't locate EF_IAP in EF_PBR.");
                return;
            }
            log("EF_IAP exists. Loading EF_IAP to retrieve the index.");
            readIapFileAndWait(((File) sparseArray2.get(193)).getEfid());
            if (this.mIapFileRecord == null) {
                Rlog.e("UsimPhoneBookManager", "Error: IAP file is empty");
                return;
            }
            log("EF_EMAIL order in PBR record: " + file.getIndex());
        }
        int efid = file.getEfid();
        log("EF_EMAIL exists in PBR. efid = 0x" + Integer.toHexString(efid).toUpperCase(Locale.ROOT));
        for (int i2 = 0; i2 < i; i2++) {
            if (this.mPbrRecords.get(i2) != null && (sparseArray = this.mPbrRecords.get(i2).mFileIds) != null && (file2 = (File) sparseArray.get(202)) != null && file2.getEfid() == efid) {
                log("Skipped this EF_EMAIL which was loaded earlier");
                return;
            }
        }
        this.mFh.loadEFLinearFixedAll(efid, obtainMessage(4));
        try {
            this.mLock.wait();
        } catch (InterruptedException unused) {
            Rlog.e("UsimPhoneBookManager", "Interrupted Exception in readEmailFileAndWait");
        }
        if (this.mEmailFileRecord == null) {
            Rlog.e("UsimPhoneBookManager", "Error: Email file is empty");
        } else if (file.getParentTag() == 169 && this.mIapFileRecord != null) {
            buildType2EmailList(i);
        } else {
            buildType1EmailList(i);
        }
    }

    private void buildType1EmailList(int i) {
        int efid;
        if (this.mPbrRecords.get(i) == null) {
            return;
        }
        int i2 = this.mPbrRecords.get(i).mMainFileRecordNum;
        log("Building type 1 email list. recId = " + i + ", numRecs = " + i2);
        for (int i3 = 0; i3 < i2; i3++) {
            try {
                byte[] bArr = this.mEmailFileRecord.get(i3);
                byte b = bArr[bArr.length - 2];
                byte b2 = bArr[bArr.length - 1];
                String readEmailRecord = readEmailRecord(i3);
                if (readEmailRecord != null && !readEmailRecord.equals(PhoneConfigurationManager.SSSS)) {
                    if (b == -1 || this.mSfiEfidTable.get(b) == 0) {
                        File file = (File) this.mPbrRecords.get(i).mFileIds.get(DnsPacket.DnsRecord.NAME_COMPRESSION);
                        if (file != null) {
                            efid = file.getEfid();
                        }
                    } else {
                        efid = this.mSfiEfidTable.get(b);
                    }
                    int i4 = ((b2 - 1) & 255) | ((efid & 65535) << 8);
                    ArrayList<String> arrayList = this.mEmailsForAdnRec.get(i4);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    log("Adding email #" + i3 + " list to index 0x" + Integer.toHexString(i4).toUpperCase(Locale.ROOT));
                    arrayList.add(readEmailRecord);
                    this.mEmailsForAdnRec.put(i4, arrayList);
                }
            } catch (IndexOutOfBoundsException unused) {
                Rlog.e("UsimPhoneBookManager", "Error: Improper ICC card: No email record for ADN, continuing");
                return;
            }
        }
    }

    private boolean buildType2EmailList(int i) {
        if (this.mPbrRecords.get(i) == null) {
            return false;
        }
        int i2 = this.mPbrRecords.get(i).mMainFileRecordNum;
        log("Building type 2 email list. recId = " + i + ", numRecs = " + i2);
        File file = (File) this.mPbrRecords.get(i).mFileIds.get(DnsPacket.DnsRecord.NAME_COMPRESSION);
        if (file == null) {
            Rlog.e("UsimPhoneBookManager", "Error: Improper ICC card: EF_ADN does not exist in PBR files");
            return false;
        }
        int efid = file.getEfid();
        for (int i3 = 0; i3 < i2; i3++) {
            try {
                String readEmailRecord = readEmailRecord(this.mIapFileRecord.get(i3)[((File) this.mPbrRecords.get(i).mFileIds.get(202)).getIndex()] - 1);
                if (readEmailRecord != null && !readEmailRecord.equals(PhoneConfigurationManager.SSSS)) {
                    int i4 = ((65535 & efid) << 8) | (i3 & 255);
                    ArrayList<String> arrayList = this.mEmailsForAdnRec.get(i4);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(readEmailRecord);
                    log("Adding email list to index 0x" + Integer.toHexString(i4).toUpperCase(Locale.ROOT));
                    this.mEmailsForAdnRec.put(i4, arrayList);
                }
            } catch (IndexOutOfBoundsException unused) {
                Rlog.e("UsimPhoneBookManager", "Error: Improper ICC card: Corrupted EF_IAP");
            }
        }
        return true;
    }

    private void readIapFileAndWait(int i) {
        this.mFh.loadEFLinearFixedAll(i, obtainMessage(3));
        try {
            this.mLock.wait();
        } catch (InterruptedException unused) {
            Rlog.e("UsimPhoneBookManager", "Interrupted Exception in readIapFileAndWait");
        }
    }

    private void updatePhoneAdnRecord() {
        int size = this.mPhoneBookRecords.size();
        for (int i = 0; i < size; i++) {
            AdnRecord adnRecord = this.mPhoneBookRecords.get(i);
            try {
                ArrayList<String> arrayList = this.mEmailsForAdnRec.get(((adnRecord.getEfid() & 65535) << 8) | ((adnRecord.getRecId() - 1) & 255));
                if (arrayList != null) {
                    String[] strArr = new String[arrayList.size()];
                    System.arraycopy(arrayList.toArray(), 0, strArr, 0, arrayList.size());
                    adnRecord.setEmails(strArr);
                    log("Adding email list to ADN (0x" + Integer.toHexString(this.mPhoneBookRecords.get(i).getEfid()).toUpperCase(Locale.ROOT) + ") record #" + this.mPhoneBookRecords.get(i).getRecId());
                    this.mPhoneBookRecords.set(i, adnRecord);
                }
            } catch (IndexOutOfBoundsException unused) {
            }
        }
    }

    private String readEmailRecord(int i) {
        try {
            byte[] bArr = this.mEmailFileRecord.get(i);
            return IccUtils.adnStringFieldToString(bArr, 0, bArr.length - 2);
        } catch (IndexOutOfBoundsException unused) {
            return null;
        }
    }

    private void readAdnFileAndWait(int i) {
        SparseArray sparseArray = this.mPbrRecords.get(i).mFileIds;
        if (sparseArray == null || sparseArray.size() == 0) {
            return;
        }
        int efid = sparseArray.get(194) != null ? ((File) sparseArray.get(194)).getEfid() : 0;
        if (sparseArray.get(DnsPacket.DnsRecord.NAME_COMPRESSION) == null) {
            return;
        }
        int size = this.mPhoneBookRecords.size();
        this.mAdnCache.requestLoadAllAdnLike(((File) sparseArray.get(DnsPacket.DnsRecord.NAME_COMPRESSION)).getEfid(), efid, obtainMessage(2));
        try {
            this.mLock.wait();
        } catch (InterruptedException unused) {
            Rlog.e("UsimPhoneBookManager", "Interrupted Exception in readAdnFileAndWait");
        }
        this.mPbrRecords.get(i).mMainFileRecordNum = this.mPhoneBookRecords.size() - size;
    }

    private void createPbrFile(ArrayList<byte[]> arrayList) {
        int sfi;
        if (arrayList == null) {
            this.mPbrRecords = null;
            this.mIsPbrPresent = Boolean.FALSE;
            return;
        }
        this.mPbrRecords = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i)[0] != -1) {
                this.mPbrRecords.add(new PbrRecord(arrayList.get(i)));
            }
        }
        Iterator<PbrRecord> it = this.mPbrRecords.iterator();
        while (it.hasNext()) {
            PbrRecord next = it.next();
            File file = (File) next.mFileIds.get(DnsPacket.DnsRecord.NAME_COMPRESSION);
            if (file != null && (sfi = file.getSfi()) != -1) {
                this.mSfiEfidTable.put(sfi, ((File) next.mFileIds.get(DnsPacket.DnsRecord.NAME_COMPRESSION)).getEfid());
            }
        }
    }

    @Override // android.os.Handler
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            log("Loading PBR records done");
            AsyncResult asyncResult = (AsyncResult) message.obj;
            if (asyncResult.exception == null) {
                createPbrFile((ArrayList) asyncResult.result);
            }
            synchronized (this.mLock) {
                this.mLock.notify();
            }
        } else if (i == 2) {
            log("Loading USIM ADN records done");
            AsyncResult asyncResult2 = (AsyncResult) message.obj;
            if (asyncResult2.exception == null) {
                this.mPhoneBookRecords.addAll((ArrayList) asyncResult2.result);
            }
            synchronized (this.mLock) {
                this.mLock.notify();
            }
        } else if (i == 3) {
            log("Loading USIM IAP records done");
            AsyncResult asyncResult3 = (AsyncResult) message.obj;
            if (asyncResult3.exception == null) {
                this.mIapFileRecord = (ArrayList) asyncResult3.result;
            }
            synchronized (this.mLock) {
                this.mLock.notify();
            }
        } else if (i != 4) {
        } else {
            log("Loading USIM Email records done");
            AsyncResult asyncResult4 = (AsyncResult) message.obj;
            if (asyncResult4.exception == null) {
                this.mEmailFileRecord = (ArrayList) asyncResult4.result;
            }
            synchronized (this.mLock) {
                this.mLock.notify();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PbrRecord {
        private SparseArray<File> mFileIds = new SparseArray<>();
        private int mMainFileRecordNum;

        PbrRecord(byte[] bArr) {
            UsimPhoneBookManager.this.log("PBR rec: " + IccUtils.bytesToHexString(bArr));
            parseTag(new SimTlv(bArr, 0, bArr.length));
        }

        void parseTag(SimTlv simTlv) {
            do {
                int tag = simTlv.getTag();
                switch (tag) {
                    case 168:
                    case 169:
                    case 170:
                        byte[] data = simTlv.getData();
                        parseEfAndSFI(new SimTlv(data, 0, data.length), tag);
                        break;
                }
            } while (simTlv.nextObject());
        }

        void parseEfAndSFI(SimTlv simTlv, int i) {
            int i2 = 0;
            do {
                int tag = simTlv.getTag();
                switch (tag) {
                    case DnsPacket.DnsRecord.NAME_COMPRESSION /* 192 */:
                    case 193:
                    case 194:
                    case 195:
                    case 196:
                    case 197:
                    case 198:
                    case 199:
                    case ImsRttTextHandler.MAX_BUFFERING_DELAY_MILLIS /* 200 */:
                    case IccRecords.EVENT_SET_SMSS_RECORD_DONE /* 201 */:
                    case 202:
                    case 203:
                        byte[] data = simTlv.getData();
                        if (data.length < 2 || data.length > 3) {
                            UsimPhoneBookManager.this.log("Invalid TLV length: " + data.length);
                            break;
                        } else {
                            this.mFileIds.put(tag, new File(i, ((data[0] & 255) << 8) | (data[1] & 255), data.length == 3 ? data[2] & 255 : -1, i2));
                            break;
                        }
                        break;
                }
                i2++;
            } while (simTlv.nextObject());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage
    public void log(String str) {
        Rlog.d("UsimPhoneBookManager", str);
    }
}
