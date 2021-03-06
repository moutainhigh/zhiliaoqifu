package com.ebeijia.zl.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ApiModel("支付信息")
@Data
public class PayInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("支付的订单编号")
    String orderId;

    @ApiModelProperty("A类账户类型,目前已有通卡账户和托管账户")
    String typeA;

    @ApiModelProperty("B类账户类型，对应多个专项账户类型之一")
    String typeB;

    @ApiModelProperty("单位分，从A类账户扣款金额")
    Long costA;

    @ApiModelProperty("单位分，从B类账户扣款金额")
    Long costB;

    @ApiModelProperty("单位分，支付的邮费")
    Long shipPrice;
}
