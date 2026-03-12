package com.heimhel.notes.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.ByteArrayOutputStream;

@UtilityClass
@Slf4j
public class FileUtil {

    public byte[] getByteArray(WordprocessingMLPackage wordMLPackage) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Docx4J.save(wordMLPackage, baos);
            baos.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to get byte array", e);
            throw new RuntimeException("Failed to get byte array", e);
        }
    }

    public static byte[] toPdfBytes(WordprocessingMLPackage wordMLPackage) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Docx4J.toPDF(wordMLPackage, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to convert DOCX to PDF", e);
            throw new RuntimeException("Failed to convert DOCX to PDF", e);
        }
    }

}
