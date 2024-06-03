package com.zhuang.filestorage.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuang.filestorage.entity.FileDetail;
import com.zhuang.filestorage.enums.FileDetailStatus;
import com.zhuang.filestorage.mapper.FileDetailMapper;
import lombok.SneakyThrows;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件记录表 服务类
 * </p>
 *
 * @author zwb
 * @since 2023-10-10
 */

/**
 * 用来将文件上传记录保存到数据库，这里使用了 MyBatis-Plus 和 Hutool 工具类
 */
@Service
public class FileDetailService extends ServiceImpl<FileDetailMapper, FileDetail> implements FileRecorder {

    /**
     * 保存文件信息到数据库
     */
    @SneakyThrows
    @Override
    public boolean save(FileInfo info) {
        FileDetail detail = BeanUtil.copyProperties(info, FileDetail.class, "attr");
        detail.setModifyTime(new Date());
        //这是手动获 取附加属性字典 并转成 json 字符串，方便存储在数据库中
        if (info.getAttr() != null) {
            detail.setAttr(new ObjectMapper().writeValueAsString(info.getAttr()));
        }
        boolean b = save(detail);
        if (b) {
            info.setId(detail.getId());
        }
        return b;
    }

    @Override
    public void update(FileInfo fileInfo) {

    }

    /**
     * 根据 url 查询文件信息
     */
    @SneakyThrows
    @Override
    public FileInfo getByUrl(String url) {
        FileDetail detail = getOneByUrl(url);
        FileInfo info = BeanUtil.copyProperties(detail, FileInfo.class, "attr");
        //这是手动获取数据库中的 json 字符串 并转成 附加属性字典，方便使用
        if (StrUtil.isNotBlank(detail.getAttr())) {
            info.setAttr(new ObjectMapper().readValue(detail.getAttr(), Dict.class));
        }
        return info;
    }

    public FileDetail getOneByUrl(String url) {
        return getOne(new LambdaQueryWrapper<FileDetail>().eq(FileDetail::getUrl, url));
    }

    /**
     * 根据 url 删除文件信息
     */
    @Override
    public boolean delete(String url) {
        return remove(new LambdaQueryWrapper<FileDetail>().eq(FileDetail::getUrl, url));
    }

    @Override
    public void saveFilePart(FilePartInfo filePartInfo) {

    }

    @Override
    public void deleteFilePartByUploadId(String s) {

    }

    public void submitObjectTypeAndObjectId(String objectType, String objectId, String fileUrl) {
        submitObjectTypeAndObjectId(objectType, objectId, Arrays.asList(fileUrl));
    }

    public void submitObjectTypeAndObjectId(String objectType, String objectId, List<String> fileUrlList) {
        List<FileDetail> list4Old = getListByObjectTypeAndObjectId(objectType, objectId);
        List<String> fileUrlList4Old = list4Old.stream().map(FileDetail::getUrl).collect(Collectors.toList());
        List<String> fileUrlList4Add = fileUrlList.stream().filter(fileUrl -> !fileUrlList4Old.stream().anyMatch(c -> c.equals(fileUrl))).collect(Collectors.toList());
        List<String> fileUrlList4Del = fileUrlList4Old.stream().filter(fileUrl -> !fileUrlList.stream().anyMatch(c -> c.equals(fileUrl))).collect(Collectors.toList());
        fileUrlList4Add.forEach(fileId -> {
            updateObjectTypeAndObjectId(fileId, objectType, objectId);
        });
        fileUrlList4Del.forEach(fileUrl -> {
            updateObjectTypeAndObjectId(fileUrl, null, null);
        });
    }

    public FileDetail getByObjectTypeAndObjectId(String objectType, String objectId) {
        List<FileDetail> list = getListByObjectTypeAndObjectId(objectType, objectId);
        return list.size() > 0 ? list.get(0) : null;
    }

    public List<FileDetail> getListByObjectTypeAndObjectId(String objectType, String objectId) {
        return list(new LambdaQueryWrapper<FileDetail>()
                .eq(FileDetail::getObjectType, objectType)
                .eq(FileDetail::getObjectId, objectId)
        );
    }

    public void updateObjectTypeAndObjectId(String url, String objectType, String objectId) {
        FileDetailStatus status = FileDetailStatus.SUBMITTED;
        if (objectType == null || objectId == null) {
            status = FileDetailStatus.INTI;
        }
        update(new LambdaUpdateWrapper<FileDetail>()
                .eq(FileDetail::getUrl, url)
                .set(FileDetail::getObjectType, objectType)
                .set(FileDetail::getObjectId, objectId)
                .set(FileDetail::getModifyTime, new Date())
                .set(FileDetail::getStatus, status.getValue())
        );
    }
}
