/*
 * Copyright (C) 2011 LINUXTEK, Inc.  All Rights Reserved.
 */
package com.linuxtek.kona.qrcode;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;

import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import static com.google.zxing.BarcodeFormat.QR_CODE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KQRCode {
    private static Logger logger = LoggerFactory.getLogger(KQRCode.class);

    public static byte[] encode(String s) {
        return encode(s, 100, 100);
    }

    public static byte[] encode(String s, int width, int height) {
        return encode(s, "PNG", width, height);
    }

    public static byte[] encode(String s, String format, int width, int height) {

        Charset charset = Charset.forName("ISO-8859-1");
        CharsetEncoder encoder = charset.newEncoder();
        byte[] b = null;

        try {
            // Convert a string to ISO-8859-1 bytes in a ByteBuffer
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(s));
            b = bbuf.array();
        } catch (CharacterCodingException e) {
            logger.error(e.getMessage(), e);
        }

        String data = null;
        try {
            data = new String(b, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        // get a byte matrix for the data
        BitMatrix matrix = null;

        Writer writer = new QRCodeWriter();
        try {
            matrix = writer.encode(data, QR_CODE, width, height);
        } catch (com.google.zxing.WriterException e) {
            logger.error(e.getMessage(), e);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (format.equalsIgnoreCase("svg")) {
                MatrixToSvgImageWriter.writeToStream(matrix, bos);
            } else {
                MatrixToImageWriter.writeToStream(matrix, format, bos);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return bos.toByteArray();
    }
}
