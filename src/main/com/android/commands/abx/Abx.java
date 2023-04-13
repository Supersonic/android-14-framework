package com.android.commands.abx;

import android.util.Xml;
import com.android.modules.utils.TypedXmlPullParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Abx {
    private static final String USAGE = "usage: abx2xml [-i] input [output]\nusage: xml2abx [-i] input [output]\n\nConverts between human-readable XML and Android Binary XML.\n\nWhen invoked with the '-i' argument, the output of a successful conversion\nwill overwrite the original input file. Input can be '-' to use stdin, and\noutput can be '-' to use stdout.\n";

    private static InputStream openInput(String arg) throws IOException {
        if ("-".equals(arg)) {
            return System.in;
        }
        return new FileInputStream(arg);
    }

    private static OutputStream openOutput(String arg) throws IOException {
        if ("-".equals(arg)) {
            return System.out;
        }
        return new FileOutputStream(arg);
    }

    private static void mainInternal(String[] args) {
        TypedXmlPullParser newPullParser;
        XmlSerializer out;
        if (args.length < 2) {
            throw new IllegalArgumentException("Missing arguments");
        }
        if (args[0].endsWith("abx2xml")) {
            newPullParser = Xml.newBinaryPullParser();
            out = Xml.newSerializer();
        } else if (args[0].endsWith("xml2abx")) {
            newPullParser = Xml.newPullParser();
            out = Xml.newBinarySerializer();
        } else {
            throw new IllegalArgumentException("Unsupported conversion");
        }
        boolean inPlace = "-i".equals(args[1]);
        String inputArg = inPlace ? args[2] : args[1];
        String outputArg = inPlace ? args[2] + ".tmp" : args[2];
        try {
            InputStream is = openInput(inputArg);
            OutputStream os = openOutput(outputArg);
            try {
                newPullParser.setInput(is, StandardCharsets.UTF_8.name());
                out.setOutput(os, StandardCharsets.UTF_8.name());
                out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                Xml.copy(newPullParser, out);
                out.flush();
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                if (inPlace && !new File(outputArg).renameTo(new File(inputArg))) {
                    throw new IllegalStateException("Failed rename");
                }
            } catch (Throwable th) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Exception e) {
            if (inPlace) {
                new File(outputArg).delete();
            }
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        try {
            mainInternal(args);
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.toString());
            System.err.println();
            System.err.println(USAGE);
            System.exit(1);
        }
    }
}
