package com.zhuang.filestorage.controller;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.baomidou.mybatisplus.extension.api.R;
import com.zhuang.filestorage.model.ApiResult;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("fileStorage")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ApiResult<FileInfo> upload(MultipartFile file, String path) {
        FileInfo upload = fileStorageService.of(file).setPath(path).upload();
        return ApiResult.success(upload);
    }

    /**
     * 上传文件（批量）
     */
    @PostMapping(value = "uploadBatch")
    public ApiResult<List<FileInfo>> uploadBatch(String path, HttpServletRequest request) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        StandardMultipartHttpServletRequest multipartRequest = (StandardMultipartHttpServletRequest) request;
        for (Map.Entry<String, List<MultipartFile>> entry : multipartRequest.getMultiFileMap().entrySet()) {
            for (MultipartFile file : entry.getValue()) {
                FileInfo upload = fileStorageService.of(file).setPath(path).upload();
                fileInfoList.add(upload);
            }
        }
        return ApiResult.success(fileInfoList);
    }

    /**
     * 上传文件 下载
     */
    @GetMapping(value = "download")
    public void download(String fileId, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(fileId);
        String fileName = new String(fileInfo.getFilename().getBytes("utf-8"), "ISO8859-1");//chrome,firefox
        //fileName=URLEncoder.encode(fileName,"utf-8");//IE
        response.setHeader("content-disposition", "attachment;filename=" + fileName);
        fileStorageService.download(fileInfo).inputStream(in -> {
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int readCount;
                while ((readCount = in.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, readCount);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
