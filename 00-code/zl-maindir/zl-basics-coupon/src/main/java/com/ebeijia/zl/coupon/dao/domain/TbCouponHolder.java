package com.ebeijia.zl.coupon.dao.domain;

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
 * 记录用户持有的代金券商品
 *
 * @User J
 * @Date 2019-01-05
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("tb_coupon_holder")
@ApiModel("记录用户持有的代金券商品")
public class TbCouponHolder extends Model<TbCouponHolder> {

    /**
     * 代金券id
     */
    @TableId(value = "coupon_id" ,type = IdType.UUID)
    @ApiModelProperty(value = "代金券号")
    private String couponId;
 
    /**
     * 用户ID
     */
    @TableField("member_id")
    @ApiModelProperty(value = "用户ID")
    private String memberId;
 
    /**
     * 代金券商品id
     */
    @TableField("coupon_code")
    @ApiModelProperty(value = "代金券商品id")
    private String couponCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "代金券展示用详情")
    private String couponDesc;

    @TableField(exist = false)
    @ApiModelProperty(value = "代金券展示用图片")
    private String iconImage;

    @TableField(exist = false)
    @ApiModelProperty(value = "代金券单位")
    private String tagUnit;

    @TableField(exist = false)
    @ApiModelProperty(value = "代金券标价")
    private Long tagAmount;

    @TableField("recycle_chnl_id")
    @ApiModelProperty(value = "回收渠道ID")
    private String recycleChnlId;

    @TableField("price")
    @ApiModelProperty(value = "代金券实际价格")
    private Long price;
 
    /**
     * BID
     */
    @TableField("b_id")
    @ApiModelProperty(value = "BID")
    private String bId;
 
    @TableField("coupon_name")
    @ApiModelProperty(value = "coupon_name")
    private String couponName;
 
    /**
     * 起始有效期NA
     */
    @TableField("active_start_date")
    @ApiModelProperty(value = "起始有效期NA")
    private Long activeStartDate;
 
    /**
     * 结束有效期NA
     */
    @TableField("active_end_date")
    @ApiModelProperty(value = "结束有效期NA")
    private Long activeEndDate;
 
    /**
     * 0：未使用、1：已使用
     */
    @TableField("trans_stat")
    @ApiModelProperty(value = "0：未使用、1：已使用")
    private String transStat;
 
    /**
     * 数据状态 0：正常 、1：失效
     */
    @TableField("data_stat")
    @ApiModelProperty(value = "数据状态 0：正常 、1：失效")
    private String dataStat;
 
    /**
     * 备注
     */
    @TableField("remarks")
    @ApiModelProperty(value = "备注")
    private String remarks;
 
    /**
     * 创建人
     */
    @TableField("create_user")
    @ApiModelProperty(value = "创建人")
    private String createUser;
 
    /**
     * 修改人
     */
    @TableField("update_user")
    @ApiModelProperty(value = "修改人")
    private String updateUser;
 
    /**
     * 创建时间
     */
    @TableField("create_time")
    @ApiModelProperty(value = "创建时间")
    private Long createTime;
 
    /**
     * 修改时间
     */
    @TableField("update_time")
    @ApiModelProperty(value = "修改时间")
    private Long updateTime;
 
    /**
     * 乐观锁版本
     */
    @TableField("lock_version")
    @ApiModelProperty(value = "乐观锁版本")
    private Integer lockVersion;

    @TableField(exist = false)
    @ApiModelProperty(value = "持有数量")
    private Integer amount;

    @Override
    protected Serializable pkVal() { 
        return this.couponId;
    }
}
