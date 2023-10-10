package com.zhuang.filestorage.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.zhuang.filestorage.entity.FileDetail;
import com.zhuang.filestorage.model.ApiResult;
import com.zhuang.filestorage.service.FileDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("fileStorage")
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private FileDetailService fileDetailService;

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

    /**
     * 删除上传文件
     */
    @PostMapping("/delete")
    public ApiResult<String> delete(@RequestParam("fileIds") List<String> fileIds) {
        for (String fileId : fileIds) {
            fileDetailService.deleteById(fileId);
        }
        return ApiResult.success("删除成功！");
    }

    /**
     * 提交上传记录
     */
    @PostMapping("/submit")
    public ApiResult<String> submit(String objectType, String objectId, @RequestParam("fileIds") List<String> fileIds) {
        fileDetailService.submitObjectTypeAndObjectId(objectType, objectId, fileIds);
        return ApiResult.success("提交成功！");
    }

    /**
     * 获取文件列表
     */
    @GetMapping("/getList")
    public ApiResult<List<FileInfo>> getList(String objectType, String objectId) {
        List<FileDetail> fileDetailList = fileDetailService.getListByObjectTypeAndObjectId(objectType, objectId);
        List<FileInfo> fileInfoList = fileDetailList.stream().map(item -> {
            FileInfo fileInfo = new FileInfo();
            BeanUtil.copyProperties(item, fileInfo, "attr");
            return fileInfo;
        }).collect(Collectors.toList());
        return ApiResult.success(fileInfoList);
    }

}
