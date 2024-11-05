/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.file;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.it.euler.model.entity.FileModel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 附件表 持久化实现类
 *
 * @author zhaoyan
 * @since 2024-11-04
 */
@Service
public class AttachmentRepositoryImpl extends ServiceImpl<AttachmentMapper, AttachmentPO> implements IService<AttachmentPO> {

    public FileModel save(FileModel fileModel){
        AttachmentPO attachmentPO = new AttachmentPO();
        BeanUtils.copyProperties(fileModel,attachmentPO);
        this.save(attachmentPO);
        fileModel.setId(attachmentPO.getId());
        return fileModel;
    }

}
