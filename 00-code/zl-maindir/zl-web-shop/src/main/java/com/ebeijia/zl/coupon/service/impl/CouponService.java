package com.ebeijia.zl.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ebeijia.zl.common.utils.IdUtil;
import com.ebeijia.zl.common.utils.domain.BaseResult;
import com.ebeijia.zl.common.utils.enums.*;
import com.ebeijia.zl.common.utils.exceptions.BizException;
import com.ebeijia.zl.coupon.dao.domain.TbCouponHolder;
import com.ebeijia.zl.coupon.dao.domain.TbCouponProduct;
import com.ebeijia.zl.coupon.dao.domain.TbCouponTransFee;
import com.ebeijia.zl.coupon.dao.service.ITbCouponHolderService;
import com.ebeijia.zl.coupon.dao.service.ITbCouponProductService;
import com.ebeijia.zl.coupon.dao.service.ITbCouponTransFeeService;
import com.ebeijia.zl.coupon.dao.service.ITbCouponTransLogService;
import com.ebeijia.zl.coupon.service.ICouponService;
import com.ebeijia.zl.facade.account.req.AccountRechargeReqVo;
import com.ebeijia.zl.facade.account.req.AccountTxnVo;
import com.ebeijia.zl.facade.account.service.AccountTransactionFacade;
import com.ebeijia.zl.shop.constants.PhoneValidMethod;
import com.ebeijia.zl.shop.constants.ResultState;
import com.ebeijia.zl.shop.service.pay.IPayService;
import com.ebeijia.zl.shop.service.valid.impl.ValidCodeService;
import com.ebeijia.zl.shop.utils.AdviceMessenger;
import com.ebeijia.zl.shop.utils.ShopUtils;
import com.ebeijia.zl.shop.vo.MemberInfo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponService implements ICouponService {

    @Autowired
    private ShopUtils shopUtils;

    @Autowired
    private ITbCouponProductService couponProductDao;

    @Autowired
    private ITbCouponHolderService holderDao;

    @Autowired
    private ITbCouponTransLogService transLogDao;

    @Autowired
    private ITbCouponTransFeeService feeDao;

    @Autowired
    private ValidCodeService validCodeService;

    @Autowired
    private AccountTransactionFacade accountTransactionFacade;

    @Autowired
    private IPayService payService;

    private static Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Override
    public int couponShare(String vaildCode, String couponCode, Long price, Integer amount) {
        MemberInfo memberInfo = shopUtils.getSession();
        if (memberInfo == null) {
            throw new BizException(ResultState.NOT_ACCEPTABLE, "参数异常");
        }
        String phoneNo = memberInfo.getMobilePhoneNo();
        if (!validCodeService.checkValidCode(PhoneValidMethod.PAY, phoneNo, vaildCode)) {
            throw new BizException(ResultState.NOT_ACCEPTABLE, "验证码无效");
        }
        //判断金额
        Long sumAmount = price * amount.longValue();
        if (sumAmount < 0L) {
            throw new BizException(ResultState.NOT_ACCEPTABLE, "参数异常");
        }
        String dmsKey = IdUtil.getNextId();


        List<TbCouponHolder> holders = holderDao.couponShare(memberInfo.getMemberId(), couponCode, price, amount);
        TbCouponHolder holderExample = holders.get(0);
        TbCouponTransFee queryFee = new TbCouponTransFee();
        queryFee.setBId(holderExample.getBId());
        TbCouponTransFee fee = feeDao.getOne(new QueryWrapper<>(queryFee));
        BigDecimal feeDecimal = BigDecimal.valueOf(fee.getFee());
        feeDecimal = feeDecimal.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_EVEN);
        feeDecimal = BigDecimal.ONE.add(feeDecimal.negate());
        BigDecimal txnAmt = BigDecimal.valueOf(price).multiply(feeDecimal);
        BaseResult baseResult = null;
        //提交事务，通讯远端账务系统
        try {
            AccountRechargeReqVo vo = new AccountRechargeReqVo();
            vo.setMobilePhone(memberInfo.getMobilePhoneNo());
            vo.setFromCompanyId("Coupon Trans");
            vo.setPriBId(holderExample.getBId());

            AccountTxnVo txnVo = new AccountTxnVo();
            txnVo.setTxnBId(holderExample.getBId());
            txnVo.setTxnAmt(txnAmt);
            txnVo.setUpLoadAmt(BigDecimal.valueOf(price));
            logger.info(String.format("提交卡券转让请求，金额%s，总计卡券数量%s，用户ID%s，卡券类型%s", txnAmt, amount, memberInfo.getMemberId(), couponCode));
            List<AccountTxnVo> transList = new ArrayList<>();
            transList.add(txnVo);


            vo.setTransList(transList);
            vo.setTransId(TransCode.CW50.getCode());
            vo.setTransChnl(TransChnl.CHANNEL9.toString());

            vo.setUserType(UserType.TYPE100.getCode());
            vo.setUserChnl(UserChnlCode.USERCHNL2001.getCode());
            vo.setUserChnlId(memberInfo.getOpenId());
            vo.setDmsRelatedKey(dmsKey);
            baseResult = accountTransactionFacade.executeRecharge(vo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO DMS
        if (baseResult == null || !baseResult.getCode().equals("00")) {
            holderDao.couponShareRollback(holders);
            throw new BizException(ResultState.ERROR, "网络通讯异常");
        }
        //检查结果，处理异常回滚
        return 200;
    }

    @Override
    public int buyCoupon(String vaildCode, String couponCode, Integer amount) {
        MemberInfo memberInfo = shopUtils.getSession();
        if (memberInfo == null) {
            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "参数异常");
        }
        String phoneNo = memberInfo.getMobilePhoneNo();
//        if (!validCodeService.checkValidCode(PhoneValidMethod.PAY, phoneNo, vaildCode)) {
//            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "验证码无效");
//        }
        String dmsKey = IdUtil.getNextId();
        TbCouponProduct query = new TbCouponProduct();
        query.setCouponCode(couponCode);
        query = couponProductDao.getOne(new QueryWrapper<>(query));
        long sum = query.getPrice() * amount;
        if (sum < 0) {
            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "您购买的数量有误");
        }
        AccountTxnVo accountTxnVo = new AccountTxnVo();
        accountTxnVo.setUpLoadAmt(BigDecimal.valueOf(sum));
        accountTxnVo.setTxnAmt(BigDecimal.valueOf(sum));
        accountTxnVo.setTxnBId(query.getBId());

        payService.payCoupon(accountTxnVo, memberInfo.getOpenId(), dmsKey, String.format("购买卡券%s", query.getCouponName()));
        //TODO DMS
        return 200;
    }

    @Override
    public PageInfo<TbCouponProduct> listProduct(String bId, String order, Integer start, Integer limit) {

        TbCouponProduct query = new TbCouponProduct();
        SpecAccountTypeEnum type = SpecAccountTypeEnum.findByBId(bId);
        if (type == null) {
            throw new AdviceMessenger(ResultState.NOT_FOUND, "没有数据");
        }
        query.setBId(type.getbId());
        query.setDataStat("0");
        List<TbCouponProduct> list = couponProductDao.list(new QueryWrapper<>(query));

        return new PageInfo<>(list);
    }

    @Override
    public TbCouponProduct couponDetail(String couponCode) {
        TbCouponProduct query = new TbCouponProduct();
        query.setCouponCode(couponCode);
        query.setDataStat("0");
        query = couponProductDao.getOne(new QueryWrapper<>(query));
        TbCouponTransFee queryFee = new TbCouponTransFee();
        queryFee.setBId(query.getBId());
        TbCouponTransFee fee = feeDao.getOne(new QueryWrapper<>(queryFee));
        query.setFee(fee.getFee());
        return query;
    }

    @Override
    public PageInfo<TbCouponHolder> getHolder(String bId) {

        MemberInfo memberInfo = shopUtils.getSession();
        if (memberInfo == null) {
            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "参数异常");
        }
        TbCouponHolder query = new TbCouponHolder();
        query.setMemberId(memberInfo.getMemberId());
        query.setDataStat("0");
        query.setTransStat("0");
        query.setBId(bId);
        List<TbCouponHolder> list = holderDao.listCouponHolder(query);

        return new PageInfo<>(list);
    }

    @Override
    public TbCouponHolder getHolderByCouponCode(String couponCode, Long price) {
        MemberInfo memberInfo = shopUtils.getSession();
        if (memberInfo == null) {
            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "参数异常");
        }
        TbCouponHolder query = new TbCouponHolder();
        query.setCouponCode(couponCode);
        query.setMemberId(memberInfo.getMemberId());
        query.setPrice(price);
        return holderDao.getCouponHolder(query);
    }
}
