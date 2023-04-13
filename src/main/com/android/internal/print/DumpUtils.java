package com.android.internal.print;

import android.app.Notification;
import android.content.Context;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentInfo;
import android.print.PrintJobId;
import android.print.PrintJobInfo;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.provider.Telephony;
import android.service.print.PrinterCapabilitiesProto;
import com.android.internal.util.dump.DualDumpOutputStream;
/* loaded from: classes4.dex */
public class DumpUtils {
    public static void writePrinterId(DualDumpOutputStream proto, String idName, long id, PrinterId printerId) {
        long token = proto.start(idName, id);
        com.android.internal.util.dump.DumpUtils.writeComponentName(proto, "service_name", 1146756268033L, printerId.getServiceName());
        proto.write("local_id", 1138166333442L, printerId.getLocalId());
        proto.end(token);
    }

    public static void writePrinterCapabilities(Context context, DualDumpOutputStream proto, String idName, long id, PrinterCapabilitiesInfo cap) {
        long token = proto.start(idName, id);
        writeMargins(proto, "min_margins", 1146756268033L, cap.getMinMargins());
        int numMediaSizes = cap.getMediaSizes().size();
        for (int i = 0; i < numMediaSizes; i++) {
            writeMediaSize(context, proto, "media_sizes", 2246267895810L, cap.getMediaSizes().get(i));
        }
        int numResolutions = cap.getResolutions().size();
        for (int i2 = 0; i2 < numResolutions; i2++) {
            writeResolution(proto, "resolutions", 2246267895811L, cap.getResolutions().get(i2));
        }
        int i3 = cap.getColorModes();
        if ((i3 & 1) != 0) {
            proto.write("color_modes", 2259152797700L, 1);
        }
        if ((cap.getColorModes() & 2) != 0) {
            proto.write("color_modes", 2259152797700L, 2);
        }
        if ((cap.getDuplexModes() & 1) != 0) {
            proto.write("duplex_modes", PrinterCapabilitiesProto.DUPLEX_MODES, 1);
        }
        if ((cap.getDuplexModes() & 2) != 0) {
            proto.write("duplex_modes", PrinterCapabilitiesProto.DUPLEX_MODES, 2);
        }
        if ((cap.getDuplexModes() & 4) != 0) {
            proto.write("duplex_modes", PrinterCapabilitiesProto.DUPLEX_MODES, 4);
        }
        proto.end(token);
    }

    public static void writePrinterInfo(Context context, DualDumpOutputStream proto, String idName, long id, PrinterInfo info) {
        long token = proto.start(idName, id);
        writePrinterId(proto, "id", 1146756268033L, info.getId());
        proto.write("name", 1138166333442L, info.getName());
        proto.write("status", 1159641169923L, info.getStatus());
        proto.write("description", 1138166333444L, info.getDescription());
        PrinterCapabilitiesInfo cap = info.getCapabilities();
        if (cap != null) {
            writePrinterCapabilities(context, proto, "capabilities", 1146756268037L, cap);
        }
        proto.end(token);
    }

    public static void writeMediaSize(Context context, DualDumpOutputStream proto, String idName, long id, PrintAttributes.MediaSize mediaSize) {
        long token = proto.start(idName, id);
        proto.write("id", 1138166333441L, mediaSize.getId());
        proto.write("label", 1138166333442L, mediaSize.getLabel(context.getPackageManager()));
        proto.write("height_mils", 1120986464259L, mediaSize.getHeightMils());
        proto.write("width_mils", 1120986464260L, mediaSize.getWidthMils());
        proto.end(token);
    }

    public static void writeResolution(DualDumpOutputStream proto, String idName, long id, PrintAttributes.Resolution res) {
        long token = proto.start(idName, id);
        proto.write("id", 1138166333441L, res.getId());
        proto.write("label", 1138166333442L, res.getLabel());
        proto.write("horizontal_DPI", 1120986464259L, res.getHorizontalDpi());
        proto.write("veritical_DPI", 1120986464260L, res.getVerticalDpi());
        proto.end(token);
    }

    public static void writeMargins(DualDumpOutputStream proto, String idName, long id, PrintAttributes.Margins margins) {
        long token = proto.start(idName, id);
        proto.write("top_mils", 1120986464257L, margins.getTopMils());
        proto.write("left_mils", 1120986464258L, margins.getLeftMils());
        proto.write("right_mils", 1120986464259L, margins.getRightMils());
        proto.write("bottom_mils", 1120986464260L, margins.getBottomMils());
        proto.end(token);
    }

    public static void writePrintAttributes(Context context, DualDumpOutputStream proto, String idName, long id, PrintAttributes attributes) {
        long token = proto.start(idName, id);
        PrintAttributes.MediaSize mediaSize = attributes.getMediaSize();
        if (mediaSize != null) {
            writeMediaSize(context, proto, "media_size", 1146756268033L, mediaSize);
            proto.write("is_portrait", 1133871366146L, attributes.isPortrait());
        }
        PrintAttributes.Resolution res = attributes.getResolution();
        if (res != null) {
            writeResolution(proto, "resolution", 1146756268035L, res);
        }
        PrintAttributes.Margins minMargins = attributes.getMinMargins();
        if (minMargins != null) {
            writeMargins(proto, "min_margings", 1146756268036L, minMargins);
        }
        proto.write("color_mode", 1159641169925L, attributes.getColorMode());
        proto.write(Telephony.ServiceStateTable.DUPLEX_MODE, 1159641169926L, attributes.getDuplexMode());
        proto.end(token);
    }

    public static void writePrintDocumentInfo(DualDumpOutputStream proto, String idName, long id, PrintDocumentInfo info) {
        long token = proto.start(idName, id);
        proto.write("name", 1138166333441L, info.getName());
        int pageCount = info.getPageCount();
        if (pageCount != -1) {
            proto.write("page_count", 1120986464258L, pageCount);
        }
        proto.write("content_type", 1120986464259L, info.getContentType());
        proto.write("data_size", 1112396529668L, info.getDataSize());
        proto.end(token);
    }

    public static void writePageRange(DualDumpOutputStream proto, String idName, long id, PageRange range) {
        long token = proto.start(idName, id);
        proto.write("start", 1120986464257L, range.getStart());
        proto.write("end", 1120986464258L, range.getEnd());
        proto.end(token);
    }

    public static void writePrintJobInfo(Context context, DualDumpOutputStream proto, String idName, long id, PrintJobInfo printJobInfo) {
        long token = proto.start(idName, id);
        proto.write("label", 1138166333441L, printJobInfo.getLabel());
        PrintJobId printJobId = printJobInfo.getId();
        if (printJobId != null) {
            proto.write("print_job_id", 1138166333442L, printJobId.flattenToString());
        }
        int state = printJobInfo.getState();
        if (state >= 1 && state <= 7) {
            proto.write("state", 1159641169923L, state);
        } else {
            proto.write("state", 1159641169923L, 0);
        }
        PrinterId printer = printJobInfo.getPrinterId();
        if (printer != null) {
            writePrinterId(proto, "printer", 1146756268036L, printer);
        }
        String tag = printJobInfo.getTag();
        if (tag != null) {
            proto.write("tag", 1138166333445L, tag);
        }
        proto.write("creation_time", 1112396529670L, printJobInfo.getCreationTime());
        PrintAttributes attributes = printJobInfo.getAttributes();
        if (attributes != null) {
            writePrintAttributes(context, proto, "attributes", 1146756268039L, attributes);
        }
        PrintDocumentInfo docInfo = printJobInfo.getDocumentInfo();
        if (docInfo != null) {
            writePrintDocumentInfo(proto, "document_info", 1146756268040L, docInfo);
        }
        proto.write("is_canceling", 1133871366153L, printJobInfo.isCancelling());
        PageRange[] pages = printJobInfo.getPages();
        if (pages != null) {
            for (PageRange pageRange : pages) {
                writePageRange(proto, "pages", 2246267895818L, pageRange);
            }
        }
        proto.write("has_advanced_options", 1133871366155L, printJobInfo.getAdvancedOptions() != null);
        proto.write(Notification.CATEGORY_PROGRESS, 1108101562380L, printJobInfo.getProgress());
        CharSequence status = printJobInfo.getStatus(context.getPackageManager());
        if (status != null) {
            proto.write("status", 1138166333453L, status.toString());
        }
        proto.end(token);
    }
}
