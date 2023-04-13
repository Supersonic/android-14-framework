package com.android.internal.org.bouncycastle.asn1;

import android.hardware.gnss.GnssSignalType;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Strings;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
/* loaded from: classes4.dex */
public class ASN1UTCTime extends ASN1Primitive {
    private byte[] time;

    public static ASN1UTCTime getInstance(Object obj) {
        if (obj == null || (obj instanceof ASN1UTCTime)) {
            return (ASN1UTCTime) obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1UTCTime) fromByteArray((byte[]) obj);
            } catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1UTCTime getInstance(ASN1TaggedObject obj, boolean explicit) {
        ASN1Object o = obj.getObject();
        if (explicit || (o instanceof ASN1UTCTime)) {
            return getInstance(o);
        }
        return new ASN1UTCTime(ASN1OctetString.getInstance(o).getOctets());
    }

    public ASN1UTCTime(String time) {
        this.time = Strings.toByteArray(time);
        try {
            getDate();
        } catch (ParseException e) {
            throw new IllegalArgumentException("invalid date string: " + e.getMessage());
        }
    }

    public ASN1UTCTime(Date time) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss'Z'", Locale.US);
        dateF.setTimeZone(new SimpleTimeZone(0, GnssSignalType.CODE_TYPE_Z));
        this.time = Strings.toByteArray(dateF.format(time));
    }

    public ASN1UTCTime(Date time, Locale locale) {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmss'Z'", Locale.US);
        dateF.setCalendar(Calendar.getInstance(locale));
        dateF.setTimeZone(new SimpleTimeZone(0, GnssSignalType.CODE_TYPE_Z));
        this.time = Strings.toByteArray(dateF.format(time));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ASN1UTCTime(byte[] time) {
        if (time.length < 2) {
            throw new IllegalArgumentException("UTCTime string too short");
        }
        this.time = time;
        if (!isDigit(0) || !isDigit(1)) {
            throw new IllegalArgumentException("illegal characters in UTCTime string");
        }
    }

    public Date getDate() throws ParseException {
        SimpleDateFormat dateF = new SimpleDateFormat("yyMMddHHmmssz", Locale.US);
        return DateUtil.epochAdjust(dateF.parse(getTime()));
    }

    public Date getAdjustedDate() throws ParseException {
        SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmssz", Locale.US);
        dateF.setTimeZone(new SimpleTimeZone(0, GnssSignalType.CODE_TYPE_Z));
        return DateUtil.epochAdjust(dateF.parse(getAdjustedTime()));
    }

    public String getTime() {
        String stime = Strings.fromByteArray(this.time);
        if (stime.indexOf(45) < 0 && stime.indexOf(43) < 0) {
            if (stime.length() == 11) {
                return stime.substring(0, 10) + "00GMT+00:00";
            }
            return stime.substring(0, 12) + "GMT+00:00";
        }
        int index = stime.indexOf(45);
        if (index < 0) {
            index = stime.indexOf(43);
        }
        String d = stime;
        if (index == stime.length() - 3) {
            d = d + "00";
        }
        return index == 10 ? d.substring(0, 10) + "00GMT" + d.substring(10, 13) + ":" + d.substring(13, 15) : d.substring(0, 12) + "GMT" + d.substring(12, 15) + ":" + d.substring(15, 17);
    }

    public String getAdjustedTime() {
        String d = getTime();
        if (d.charAt(0) < '5') {
            return "20" + d;
        }
        return "19" + d;
    }

    private boolean isDigit(int pos) {
        byte b;
        byte[] bArr = this.time;
        return bArr.length > pos && (b = bArr[pos]) >= 48 && b <= 57;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean isConstructed() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public int encodedLength() {
        int length = this.time.length;
        return StreamUtil.calculateBodyLength(length) + 1 + length;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncoded(withTag, 23, this.time);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive
    public boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1UTCTime)) {
            return false;
        }
        return Arrays.areEqual(this.time, ((ASN1UTCTime) o).time);
    }

    @Override // com.android.internal.org.bouncycastle.asn1.ASN1Primitive, com.android.internal.org.bouncycastle.asn1.ASN1Object
    public int hashCode() {
        return Arrays.hashCode(this.time);
    }

    public String toString() {
        return Strings.fromByteArray(this.time);
    }
}
