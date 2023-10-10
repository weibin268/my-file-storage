package com.zhuang.filestorage.controller;

import cn.xuyanwu.spring.file.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;


}
