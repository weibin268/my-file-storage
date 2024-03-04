package com.zhuang.filestorage.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import cn.xuyanwu.spring.file.storage.UploadPretreatment;
import cn.xuyanwu.spring.file.storage.file.InputStreamFileWrapper;
import cn.xuyanwu.spring.file.storage.platform.FileStorage;
import cn.xuyanwu.spring.file.storage.spring.SpringFileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.reflect.misc.FieldUtil;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

@Component
public class FileStorageUtils {

    private static FileStorageUtils _this;

    @Autowired
    private FileStorageService fileStorageService;

    @PostConstruct
    private void init() {
        _this = this;
    }

    public static void download(String platform, String path, Consumer<InputStream> consumer) {
        FileStorage fileStorage = _this.fileStorageService.getFileStorage(platform);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setBasePath(ReflectUtil.getFieldValue(fileStorage, "basePath").toString());
        String fileName = FileUtil.getName(path);
        fileInfo.setFilename(fileName);
        fileInfo.setPath(StrUtil.replace(path, fileName, ""));
        fileStorage.download(fileInfo, consumer);
    }

    public static byte[] downloadBytes(String platform, String path) {
        final byte[][] bytes = new byte[1][1];
        download(platform, path, c -> {
            bytes[0] = IoUtil.readBytes(c);
        });
        return bytes[0];
    }

    public static void upload(String platform, String path, InputStream inputStream) {
        FileStorage fileStorage = _this.fileStorageService.getFileStorage(platform);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setBasePath("");
        String fileName = FileUtil.getName(path);
        fileInfo.setFilename(fileName);
        fileInfo.setPath(StrUtil.replace(path, fileName, ""));
        UploadPretreatment uploadPretreatment = new UploadPretreatment();
        InputStreamFileWrapper inputStreamFileWrapper = new InputStreamFileWrapper();
        inputStreamFileWrapper.setInputStream(inputStream);
        uploadPretreatment.setFileWrapper(inputStreamFileWrapper);
        fileStorage.save(fileInfo, uploadPretreatment);
    }

    public static void uploadBytes(String platform, String path, byte[] bytes) {
        upload(platform, path, new ByteArrayInputStream(bytes));
    }
}
