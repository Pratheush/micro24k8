package com.mylearning.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Slf4j
public class CustomMultipartFile implements MultipartFile {

    private final byte[] fileContent;
    private final String fileName;
    private final String contentType;

    public CustomMultipartFile(File file, String contentType) throws IOException {
        log.info("CustomMultipartFile constructor called file :: {}, contentType :: {}", file, contentType);
        this.fileName = file.getName();
        this.contentType = contentType;
        this.fileContent = toByteArray(file);
    }

    @Override
    public String getName() {
        log.info("CustomMultipartFile getName() called");
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        log.info("CustomMultipartFile getOriginalFilename() called");
        return getName();
        //return fileName;
    }

    @Override
    public String getContentType() {
        log.info("CustomMultipartFile getContentType() called");
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        log.info("CustomMultipartFile isEmpty() called");
        return fileContent == null || getSize() == 0;
    }

    @Override
    public long getSize() {
        log.info("CustomMultipartFile getSize() called");
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        log.info("CustomMultipartFile getBytes() called");
        return fileContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        log.info("CustomMultipartFile getInputStream() called");
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        log.info("CustomMultipartFile transferTo() called");
        try (OutputStream out = new FileOutputStream(dest)) {
            out.write(fileContent);
        }
    }

    private byte[] toByteArray(File file) throws IOException {
        log.info("CustomMultipartFile toByteArray() called");
        try (InputStream input = new FileInputStream(file)) {
            return input.readAllBytes();
        }
    }
}
