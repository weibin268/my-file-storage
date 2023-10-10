package com.zhuang.filestorage.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.zhuang.filestorage.enums.FileDetailStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 文件记录表
 * </p>
 *
 * @author zwb
 * @since 2023-10-10
 */
@Getter
@Setter
@TableName("sys_file_detail")
public class FileDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    private String id;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 文件大小，单位字节
     */
    private Long size;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originalFilename;

    /**
     * 基础存储路径
     */
    private String basePath;

    /**
     * 存储路径
     */
    private String path;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * MIME类型
     */
    private String contentType;

    /**
     * 存储平台
     */
    private String platform;

    /**
     * 缩略图访问路径
     */
    private String thUrl;

    /**
     * 缩略图名称
     */
    private String thFilename;

    /**
     * 缩略图大小，单位字节
     */
    private Long thSize;

    /**
     * 缩略图MIME类型
     */
    private String thContentType;

    /**
     * 文件所属对象id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String objectId;

    /**
     * 文件所属对象类型，例如用户头像，评价图片
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String objectType;

    /**
     * 附加属性
     */
    private String attr;

    /**
     * 文件ACL
     */
    private String fileAcl;

    /**
     * 缩略图文件ACL
     */
    private String thFileAcl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修该时间
     */
    private Date modifyTime;

    /**
     * 状态
     */
    private Integer status = FileDetailStatus.INTI.getValue();

}
