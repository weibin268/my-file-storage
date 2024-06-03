package com.zhuang.filestorage.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.dromara.x.file.storage.core.file.InputStreamFileWrapper;
import org.dromara.x.file.storage.core.platform.FileStorage;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        path = fixPath(path);
        FileStorage fileStorage = _this.fileStorageService.getFileStorage(platform);
        FileInfo fileInfo = new FileInfo();
        Object basePath = ReflectUtil.getFieldValue(fileStorage, "basePath");
        if (basePath != null) {
            fileInfo.setBasePath(basePath.toString());
        }
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

    public static void upload(String platform, String path, InputStream inputStream, String contentType) {
        path = fixPath(path);
        FileStorage fileStorage = _this.fileStorageService.getFileStorage(platform);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setContentType(contentType);
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
        upload(platform, path, new ByteArrayInputStream(bytes), null);
    }

    /**
     * 路径需要以“/”开头
     *
     * @param path
     * @return
     */
    public static String fixPath(String path) {
        if (StrUtil.isEmpty(path)) return path;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}
