package com.huawei.it.euler.ddd.domain.account;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("user_role_mapping_t")
public class UserRole {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 角色值
     */
    @TableField(value = "role_id")
    private String role;

    /**
     * 数据范围
     */
    @TableField(value = "data_scope")
    private Integer dataScope;

    /**
     * 用户uuid
     */
    @TableField(value = "uuid")
    private String uuid;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Data updateTime;
}
