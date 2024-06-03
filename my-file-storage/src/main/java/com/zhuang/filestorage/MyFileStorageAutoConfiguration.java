package com.zhuang.filestorage;

import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
@EnableFileStorage
@MapperScan("com.zhuang.filestorage.mapper")
public class MyFileStorageAutoConfiguration {

}
