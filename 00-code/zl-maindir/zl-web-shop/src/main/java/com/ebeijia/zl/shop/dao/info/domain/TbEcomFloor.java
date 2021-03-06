package com.ebeijia.zl.shop.dao.info.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 *
 * 楼层信息表
 *
 * @User J
 * @Date 2018-12-03
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("tb_ecom_floor")
@ApiModel("楼层信息表")
public class TbEcomFloor extends Model<TbEcomFloor> {
 
    @TableId(value = "floor_id" ,type = IdType.UUID)
    @ApiModelProperty(value = "floor_id")
    private String floorId;
 
    @TableField("title")
    @ApiModelProperty(value = "title")
    private String title;
 
    @TableField("parent_id")
    @ApiModelProperty(value = "parent_id")
    private String parentId;
 
    /**
     * 0:首页、1：电商
     */
    @TableField("type")
    @ApiModelProperty(value = "0:首页、1：电商")
    private String type;
 
    @TableField("ecom_code")
    @ApiModelProperty(value = "ecom_code")
    private String ecomCode;
 
    @TableField("style")
    @ApiModelProperty(value = "style")
    private String style;
 
    @TableField("logo")
    @ApiModelProperty(value = "logo")
    private String logo;
 
    /**
     * 0：显示、1：隐藏
     */
    @TableField("is_display")
    @ApiModelProperty(value = "0：显示、1：隐藏")
    private String isDisplay;
 
    @TableField("sort")
    @ApiModelProperty(value = "sort")
    private Integer sort;
 
    @TableField("cat_id")
    @ApiModelProperty(value = "cat_id")
    private String catId;
 
    @TableField("data_stat")
    @ApiModelProperty(value = "data_stat")
    private String dataStat;
 
    @TableField("remarks")
    @ApiModelProperty(value = "remarks")
    private String remarks;
 
    @TableField("create_user")
    @ApiModelProperty(value = "create_user")
    private String createUser;
 
    @TableField("update_user")
    @ApiModelProperty(value = "update_user")
    private String updateUser;
 
    @TableField("create_time")
    @ApiModelProperty(value = "create_time")
    private Long createTime;
 
    @TableField("update_time")
    @ApiModelProperty(value = "update_time")
    private Long updateTime;
 
    @TableField("lock_version")
    @ApiModelProperty(value = "lock_version")
    private Integer lockVersion;


    @Override
    protected Serializable pkVal() { 
        return this.floorId;
    }
}
