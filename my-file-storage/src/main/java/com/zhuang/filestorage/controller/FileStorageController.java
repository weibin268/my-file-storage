package com.zhuang.filestorage.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.zhuang.filestorage.entity.FileDetail;
import com.zhuang.filestorage.enums.FileDetailStatus;
import com.zhuang.filestorage.model.ApiResult;
import com.zhuang.filestorage.service.FileDetailService;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
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
    public void download(String fileUrl, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        FileInfo fileInfo = fileStorageService.getFileInfoByUrl(fileUrl);
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
    public ApiResult<String> delete(@RequestParam("fileUrls") List<String> fileUrls) {
        List<String> fileIds4Success = new ArrayList<>();
        List<String> fileIds4Fail = new ArrayList<>();
        for (String fileUrl : fileUrls) {
            FileDetail fileDetail = fileDetailService.getOneByUrl(fileUrl);
            if (fileDetail.getStatus().equals(FileDetailStatus.SUBMITTED.getValue())) {
                fileIds4Fail.add(fileUrl);
            } else {
                boolean delete = fileStorageService.delete(fileUrl);
                if (delete) {
                    fileIds4Success.add(fileUrl);
                } else {
                    fileIds4Fail.add(fileUrl);
                }
            }
        }
        return ApiResult.success(StrUtil.format("删除成功{}条{}，删除失败{}条{}！",
                fileIds4Success.size(),
                fileIds4Success.stream().collect(Collectors.joining(",", "(", ")")),
                fileIds4Fail.size(),
                fileIds4Fail.stream().collect(Collectors.joining(",", "(", ")"))
        ));
    }

    /**
     * 提交上传记录
     */
    @PostMapping("/submit")
    public ApiResult<String> submit(String objectType, String objectId, @RequestParam("fileUrls") List<String> fileUrls) {
        fileDetailService.submitObjectTypeAndObjectId(objectType, objectId, fileUrls);
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
