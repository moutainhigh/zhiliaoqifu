package com.ebeijia.zl.shop.service.pay;

import com.ebeijia.zl.common.utils.domain.BaseResult;
import com.ebeijia.zl.facade.account.req.AccountTxnVo;
import com.ebeijia.zl.facade.account.vo.AccountLogVO;
import com.ebeijia.zl.facade.account.vo.AccountVO;
import com.ebeijia.zl.shop.dao.info.domain.TbEcomItxLogDetail;
import com.ebeijia.zl.shop.dao.order.domain.TbEcomPayOrder;
import com.ebeijia.zl.shop.dao.order.domain.TbEcomPayOrderDetails;
import com.ebeijia.zl.shop.utils.ShopTransactional;
import com.ebeijia.zl.shop.vo.PayDealInfo;
import com.ebeijia.zl.shop.vo.PayInfo;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

public interface IPayService {
    int transferToCard(Long dealInfo, String validCode, Double session);

    BaseResult payOrder(PayInfo payInfo, String openId, String dmsRelatedKey, String desc, String mchntCode);

    TbEcomPayOrderDetails initPayOrderDetailObject();

    TbEcomPayOrder initPayOrderObject();

    @ShopTransactional(propagation = Propagation.REQUIRES_NEW)
    BaseResult payCoupon(AccountTxnVo vo, String openId, String dmsRelatedKey, String desc);

    BaseResult payPhone(PayInfo vo, String openId, String dmsRelatedKey, String desc);

    PageInfo<AccountLogVO> listDeals(String range, String type, String method, String start, String limit);

    List<AccountVO> listAccountDetail(String openId, String session);

    PayDealInfo getDeal(String dms);

    String phoneChargeReturn(PayInfo payInfo, TbEcomItxLogDetail log, String dmsKey);

    void outerOrderProcess(String dmsKey, String orderNo);
}
