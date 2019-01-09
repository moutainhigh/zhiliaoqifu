package com.ebeijia.zl.facade.telrecharge.resp;

import com.ebeijia.zl.common.utils.IdUtil;
import com.ebeijia.zl.common.utils.tools.MD5SignUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 话费充值 业务
 * 
 * @author zhuqiuyou
 *
 */
@ApiModel("支付信息")
@Data
public class TeleReqVO extends TeleBaseDomain {

	private static final long serialVersionUID = -1005301884642435185L;

    @ApiModelProperty("充值的电话号码")
	private String rechargePhone; // 充值的电话号码

    @ApiModelProperty("充值金额")
	private String rechargeAmount; // 充值金额

    @ApiModelProperty("分销商订单Id")
	private String outerTid; // 分销商订单Id

    @ApiModelProperty("外部分销商回调地址")
	private String callback; // 外部分销商回调地址

	private String productId; // 产品编号

	private String channelOrderId; // 平台订单号

    public static void main(String[] args) {
            TeleReqVO t = new TeleReqVO();
            t.setChannelId("0e04cf948e2af629a334c7c71fa3f8888");
            t.setMethod("hkb.api.mobile.charge");
            t.setV("1.0");
            t.setTimestamp("2018-12-25 13:50:14");
            t.setRechargePhone("13501755206");
            t.setRechargeAmount("1");
            t.setOuterTid(IdUtil.getNextId());
            t.setCallback("http://zlqfwebapi.free.idcfengye.com/web-api/api/recharge/notify/bmHKbCallBack");
            System.out.println(MD5SignUtils.genSign(t, "key", "0e04cf948e2af629a334c7c71fa3f8888", new String[] { "sign", "serialVersionUID" }, null));
    }
}
