package com.android.internal.telephony.uicc.euicc.apdu;
/* loaded from: classes.dex */
class ApduCommand {
    public final int channel;
    public final int cla;
    public final String cmdHex;
    public final int ins;
    public final boolean isEs10 = true;

    /* renamed from: p1 */
    public final int f27p1;

    /* renamed from: p2 */
    public final int f28p2;

    /* renamed from: p3 */
    public final int f29p3;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApduCommand(int i, int i2, int i3, int i4, int i5, int i6, String str) {
        this.channel = i;
        this.cla = i2;
        this.ins = i3;
        this.f27p1 = i4;
        this.f28p2 = i5;
        this.f29p3 = i6;
        this.cmdHex = str;
    }

    public String toString() {
        return "ApduCommand(channel=" + this.channel + ", cla=" + this.cla + ", ins=" + this.ins + ", p1=" + this.f27p1 + ", p2=" + this.f28p2 + ", p3=" + this.f29p3 + ", cmd=" + this.cmdHex + ", isEs10=" + this.isEs10 + ")";
    }
}
