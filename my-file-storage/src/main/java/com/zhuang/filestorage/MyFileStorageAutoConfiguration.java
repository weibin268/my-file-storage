package com.zhuang.filestorage;

import cn.xuyanwu.spring.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableFileStorage
@MapperScan("com.zhuang.filestorage.mapper")
public class MyFileStorageAutoConfiguration {

}
