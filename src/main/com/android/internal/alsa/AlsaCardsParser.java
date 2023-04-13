package com.android.internal.alsa;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Slog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes4.dex */
public class AlsaCardsParser {
    protected static final boolean DEBUG = false;
    public static final int SCANSTATUS_EMPTY = 2;
    public static final int SCANSTATUS_FAIL = 1;
    public static final int SCANSTATUS_NOTSCANNED = -1;
    public static final int SCANSTATUS_SUCCESS = 0;
    private static final String TAG = "AlsaCardsParser";
    private static final String kAlsaFolderPath = "/proc/asound";
    private static final String kCardsFilePath = "/proc/asound/cards";
    private static final String kDeviceAddressPrefix = "/dev/bus/usb/";
    private static LineTokenizer mTokenizer = new LineTokenizer(" :[]");
    private ArrayList<AlsaCardRecord> mCardRecords = new ArrayList<>();
    private int mScanStatus = -1;

    /* loaded from: classes4.dex */
    public class AlsaCardRecord {
        private static final String TAG = "AlsaCardRecord";
        private static final String kUsbCardKeyStr = "at usb-";
        int mCardNum = -1;
        String mField1 = "";
        String mCardName = "";
        String mCardDescription = "";
        private String mUsbDeviceAddress = null;

        public AlsaCardRecord() {
        }

        public int getCardNum() {
            return this.mCardNum;
        }

        public String getCardName() {
            return this.mCardName;
        }

        public String getCardDescription() {
            return this.mCardDescription;
        }

        public void setDeviceAddress(String usbDeviceAddress) {
            this.mUsbDeviceAddress = usbDeviceAddress;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean parse(String line, int lineIndex) {
            int tokenIndex;
            if (lineIndex == 0) {
                int tokenIndex2 = AlsaCardsParser.mTokenizer.nextToken(line, 0);
                int delimIndex = AlsaCardsParser.mTokenizer.nextDelimiter(line, tokenIndex2);
                try {
                    this.mCardNum = Integer.parseInt(line.substring(tokenIndex2, delimIndex));
                    int tokenIndex3 = AlsaCardsParser.mTokenizer.nextToken(line, delimIndex);
                    int delimIndex2 = AlsaCardsParser.mTokenizer.nextDelimiter(line, tokenIndex3);
                    this.mField1 = line.substring(tokenIndex3, delimIndex2);
                    this.mCardName = line.substring(AlsaCardsParser.mTokenizer.nextToken(line, delimIndex2));
                } catch (NumberFormatException e) {
                    Slog.m96e(TAG, "Failed to parse line " + lineIndex + " of " + AlsaCardsParser.kCardsFilePath + ": " + line.substring(tokenIndex2, delimIndex));
                    return false;
                }
            } else if (lineIndex == 1 && (tokenIndex = AlsaCardsParser.mTokenizer.nextToken(line, 0)) != -1) {
                int keyIndex = line.indexOf(kUsbCardKeyStr);
                boolean isUsb = keyIndex != -1;
                if (isUsb) {
                    this.mCardDescription = line.substring(tokenIndex, keyIndex - 1);
                }
            }
            return true;
        }

        boolean isUsb() {
            return this.mUsbDeviceAddress != null;
        }

        public String textFormat() {
            return this.mCardName + " : " + this.mCardDescription + " [addr:" + this.mUsbDeviceAddress + NavigationBarInflaterView.SIZE_MOD_END;
        }

        public void log(int listIndex) {
            Slog.m98d(TAG, "" + listIndex + " [" + this.mCardNum + " " + this.mCardName + " : " + this.mCardDescription + " usb:" + isUsb());
        }
    }

    public int scan() {
        this.mCardRecords = new ArrayList<>();
        File cardsFile = new File(kCardsFilePath);
        try {
            FileReader reader = new FileReader(cardsFile);
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                AlsaCardRecord cardRecord = new AlsaCardRecord();
                cardRecord.parse(line, 0);
                String line2 = bufferedReader.readLine();
                if (line2 == null) {
                    break;
                }
                cardRecord.parse(line2, 1);
                int cardNum = cardRecord.mCardNum;
                String cardFolderPath = "/proc/asound/card" + cardNum;
                File usbbusFile = new File(cardFolderPath + "/usbbus");
                if (usbbusFile.exists()) {
                    FileReader usbbusReader = new FileReader(usbbusFile);
                    String deviceAddress = new BufferedReader(usbbusReader).readLine();
                    if (deviceAddress != null) {
                        cardRecord.setDeviceAddress(kDeviceAddressPrefix + deviceAddress);
                    }
                    usbbusReader.close();
                }
                this.mCardRecords.add(cardRecord);
            }
            reader.close();
            if (this.mCardRecords.size() > 0) {
                this.mScanStatus = 0;
            } else {
                this.mScanStatus = 2;
            }
        } catch (FileNotFoundException e) {
            this.mScanStatus = 1;
        } catch (IOException e2) {
            this.mScanStatus = 1;
        }
        return this.mScanStatus;
    }

    public int getScanStatus() {
        return this.mScanStatus;
    }

    public AlsaCardRecord findCardNumFor(String deviceAddress) {
        Iterator<AlsaCardRecord> it = this.mCardRecords.iterator();
        while (it.hasNext()) {
            AlsaCardRecord cardRec = it.next();
            if (cardRec.isUsb() && cardRec.mUsbDeviceAddress.equals(deviceAddress)) {
                return cardRec;
            }
        }
        return null;
    }

    private void Log(String heading) {
    }
}
