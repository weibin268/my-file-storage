package com.zhuang.filestorage.controller;

import cn.hutool.core.io.FileUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import com.zhuang.filestorage.model.ApiResult;
import com.zhuang.filestorage.util.FileStorageUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("test")
public class TestController {

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ApiResult<FileInfo> upload(MultipartFile file, String platform, String path) throws IOException {
        FileStorageUtils.upload(platform, path, file.getInputStream());
        return ApiResult.success();
    }

    /**
     * 上传文件 下载
     */
    @GetMapping(value = "download")
    public void download(String platform, String path, HttpServletResponse response) throws UnsupportedEncodingException {
        String fileName = new String(FileUtil.getName(path).getBytes("utf-8"), "ISO8859-1");//chrome,firefox
        //fileName=URLEncoder.encode(fileName,"utf-8");//IE
        response.setHeader("content-disposition", "attachment;filename=" + fileName);
        FileStorageUtils.download(platform, path, in -> {
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
