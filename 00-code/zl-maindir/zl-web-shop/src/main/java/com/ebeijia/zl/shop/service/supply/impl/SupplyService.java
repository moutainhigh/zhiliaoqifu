package com.ebeijia.zl.shop.service.supply.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ebeijia.zl.common.utils.IdUtil;
import com.ebeijia.zl.common.utils.constants.Constants;
import com.ebeijia.zl.common.utils.domain.BaseResult;
import com.ebeijia.zl.common.utils.enums.SpecAccountTypeEnum;
import com.ebeijia.zl.common.utils.exceptions.BizException;
import com.ebeijia.zl.common.utils.tools.MD5SignUtils;
import com.ebeijia.zl.common.utils.tools.StringUtils;
import com.ebeijia.zl.core.redis.utils.JedisUtilsWithNamespace;
import com.ebeijia.zl.shop.constants.PhoneValidMethod;
import com.ebeijia.zl.shop.constants.ResultState;
import com.ebeijia.zl.shop.dao.info.domain.TbEcomItxLogDetail;
import com.ebeijia.zl.shop.dao.info.service.ITbEcomItxLogDetailService;
import com.ebeijia.zl.shop.dao.order.domain.TbEcomPayOrder;
import com.ebeijia.zl.shop.dao.order.domain.TbEcomPayOrderDetails;
import com.ebeijia.zl.shop.dao.order.service.ITbEcomPayOrderDetailsService;
import com.ebeijia.zl.shop.dao.order.service.ITbEcomPayOrderService;
import com.ebeijia.zl.shop.service.pay.IPayService;
import com.ebeijia.zl.shop.service.supply.ISupplyService;
import com.ebeijia.zl.shop.service.valid.IValidCodeService;
import com.ebeijia.zl.shop.utils.AdviceMessenger;
import com.ebeijia.zl.shop.utils.ShopUtils;
import com.ebeijia.zl.shop.vo.MemberInfo;
import com.ebeijia.zl.shop.vo.PayInfo;
import com.ebeijia.zl.shop.vo.TeleReqVO;
import com.ebeijia.zl.shop.vo.TeleRespVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;


@Service
public class SupplyService implements ISupplyService {

    @Autowired
    private ITbEcomItxLogDetailService logDetailDao;

    @Autowired
    private IValidCodeService validCodeService;

    @Autowired
    private ShopUtils shopUtils;

    @Autowired
    private JedisUtilsWithNamespace jedis;

    @Autowired
    private IPayService payService;

    @Autowired
    private ITbEcomPayOrderService payOrderDao;

    @Autowired
    private ITbEcomPayOrderDetailsService payOrderDetailsDao;

    private static Logger logger = LoggerFactory.getLogger(SupplyService.class);

    @Value("${phone.api.charge:http://192.168.2.110:10701/web-api/api/recharge/mobile/payment}")
    private String phoneChargeUrl;

    @Value("${phone.api.chargeCallback:http://api.happy8888.com.cn/web-api/api/recharge/notify/bmHKbCallBack}")
    private String phoneChargeCallbackUrl;


    @Override
    public Integer phoneCharge(String phone, Integer amount, String validCode, PayInfo payInfo, String session) {
        String dmsKey = IdUtil.getNextId();
        if (amount <= 0) {
            throw new BizException(ResultState.NOT_ACCEPTABLE, "充值金额有误");
        }

        MemberInfo memberInfo = shopUtils.getSession();
        if (memberInfo == null) {
            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "参数异常");
        }
        boolean valid = validCodeService.checkValidCode(PhoneValidMethod.PAY, memberInfo.getMobilePhoneNo(), validCode);
        if (!valid) {
            throw new AdviceMessenger(ResultState.NOT_ACCEPTABLE, "验证码有误");
        }
        //TODO 这里amount是分，需要注意
        checkPayment(payInfo, amount);

        //TODO INF
        String image = "手机充值";
        String descinfo = phone;
        String title = getPhoneChargeProvider();
        String outId = dmsKey;

        BigDecimal decimalAmount = BigDecimal.valueOf(amount);
        String upAmount = decimalAmount.multiply(BigDecimal.valueOf(0.01D)).toString();

        //TODO DMS
        TbEcomItxLogDetail log = new TbEcomItxLogDetail();
        log.setMemberId(memberInfo.getMemberId());
        log.setTitle(title);
        log.setPrice(amount.longValue());
        log.setDescinfo(descinfo);
        log.setOutId(outId);
        log.setImg(image);
        log.setAmount(0);
        log.setSourceBid(SpecAccountTypeEnum.B06.getbId());
        logger.info("手机直充记录日志详情：", log);

        //记录LOG ID
        BaseResult baseResult = payService.payPhone(payInfo, memberInfo.getOpenId(), dmsKey, "手机充值");

        if (!baseResult.getCode().equals("00")) {
            throw new BizException(ResultState.ERROR, "网络不稳定，请稍后再试");
        }
        log.setItxKey((String) baseResult.getObject());
        logDetailDao.save(log);

        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        TeleReqVO vo = new TeleReqVO();
        vo.setChannelId("a5a41d8e-66a7-4ebe-bac2-7c280d666666");
        paramsMap.add("channelId", vo.getChannelId());
        vo.setMethod("hkb.api.mobile.charge");
        paramsMap.add("method", vo.getMethod());
        vo.setV("1.0");
        paramsMap.add("v", vo.getV());
//       paramsMap.add("channelOrderId",vo.getChannelOrderId());
//        vo.setChannelOrderId(IdUtil.getNextId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(System.currentTimeMillis());
        vo.setTimestamp(dateTime);
        paramsMap.add("timestamp", vo.getTimestamp());
        vo.setRechargePhone(phone);
        paramsMap.add("rechargePhone", vo.getRechargePhone());
        vo.setRechargeAmount(upAmount);
        paramsMap.add("rechargeAmount", vo.getRechargeAmount());
//        vo.setOuterTid("3e7e344e-0ca9-4d4d-8aad-627daaed67eb");
        vo.setOuterTid(dmsKey);
        paramsMap.add("outerTid", vo.getOuterTid());
        vo.setCallback(phoneChargeCallbackUrl);
        paramsMap.add("callback", vo.getCallback());
        vo.setSign(MD5SignUtils.genSign(vo, "key", "a5a41d8e-66a7-4ebe-bac2-7c280d888888", new String[]{"sign", "serialVersionUID"}, null));
        paramsMap.add("sign", vo.getSign());

        logger.info("手机充值VO:[{}]", vo);
        RestTemplate template = new RestTemplate();
        BaseResult postResult = template.postForObject(phoneChargeUrl, paramsMap, BaseResult.class);
        if (postResult == null) {
            throw new BizException(ResultState.ERROR, "网络不稳定，请稍后");
        }
        log.setImg(postResult.getCode());
        if (!"00".equals(postResult.getCode())) {
            logDetailDao.updateById(log);
            payService.phoneChargeReturn(payInfo, log, dmsKey);
            //TODO 短信通知
            throw new BizException(ResultState.OK, "支付成功，等待到账！\n预计1-10分钟到账");
        }
        LinkedHashMap object = (LinkedHashMap) postResult.getObject();
        Object channelOrderId = object.get("channelOrderId");
        log.setOutId(channelOrderId.toString());
        logDetailDao.updateById(log);
        //{channelId=a5a41d8e-66a7-4ebe-bac2-7c280d666666,
        // channelToken=null, method=hkb.api.mobile.charge,
        // v=1.0, timestamp=2019-01-16 11:53:51, sign=2C86B3B1BEC7D17C19EE95ACD9DDE02C,
        // channelOrderId=0561cdd087a2f13db90cddb869e1adf1, saleAmount=0.995,
        // orderTime=2019-01-16 11:53:51, operateTime=null,
        // payState=0, rechargeState=0, facePrice=1.0000, itemNum=1,
        // outerTid=cdc5eb25-fecb-494c-8bb8-1b3df1b82803, subErrorCode=null, subErrorMsg=null}
        logger.info(String.format("充值接口返回值：%s,%s,%s", postResult.getCode(), postResult.getMsg(), postResult.getObject()));
        throw new AdviceMessenger(ResultState.OK, "支付成功，等待到账！\n预计1-10分钟到账");
    }

    private void checkPayment(PayInfo payInfo, Integer amount) {
        Long sum = 0L;
        if (payInfo.getCostA() != null && payInfo.getTypeA() != null) {
            sum += payInfo.getCostA();
        }
        if (payInfo.getCostB() != null && payInfo.getTypeB() != null) {
            SpecAccountTypeEnum byBId = SpecAccountTypeEnum.findByBId(payInfo.getTypeB());
            if (byBId == SpecAccountTypeEnum.B06) {
                sum += payInfo.getCostB();
            }
        }
        if (sum.intValue() != amount) {
            throw new BizException(ResultState.NOT_ACCEPTABLE, "您的支付信息有误");
        }
    }

    @Override
    public String getPhoneChargeProvider() {
        String provider = jedis.get("PHONE_CHARGE_PROVIDER");
        if (StringUtils.isEmpty(provider)) {
            provider = logDetailDao.getPhoneChargeProvider();
            jedis.set("PHONE_CHARGE_PROVIDER", provider, 300);
        }
        return provider;
    }

    @Override
    public Integer phoneChargeCallback(TeleRespVO respVO) {
        logger.info("收到回调信息[{}]",respVO);
        if (respVO.getRechargeState().equals(Constants.RechargeState.RechargeState09)) {
            //根据VO获取对应流水
            TbEcomItxLogDetail query = new TbEcomItxLogDetail();
            query.setOutId(respVO.getChannelOrderId());

            TbEcomItxLogDetail log = logDetailDao.getOne(new QueryWrapper<>(query));
            if (log==null){
                logger.error("充值回调接口处理异常，没有找到对应流水信息");
                return ResultState.ERROR;
            }
            String itxKey = log.getItxKey();
            query.setOutId(itxKey);

            //检测是否已经处理
            TbEcomItxLogDetail cantExist = logDetailDao.getOne(new QueryWrapper<>(query));
            if (cantExist!=null){
                return ResultState.ERROR;
            }

            //从itx获取dms
            TbEcomPayOrder payQuery = new TbEcomPayOrder();
            payQuery.setOutTransNo(itxKey);
            payQuery = payOrderDao.getOne(new QueryWrapper<>(payQuery));
            String dms = payQuery.getDmsRelatedKey();
            //从dms获取payInfo

            TbEcomPayOrderDetails payDetailQuery = new TbEcomPayOrderDetails();
            payDetailQuery.setDmsRelatedKey(dms);
            List<TbEcomPayOrderDetails> payOrderDetails = payOrderDetailsDao.list(new QueryWrapper<>(payDetailQuery));

            PayInfo payInfo = restorePayInfo(payOrderDetails);
            try {
                payService.phoneChargeReturn(payInfo, log, dms);
            }catch (Exception ex){
                logger.error("手机回调处理出错[{}]",ex);
                return ResultState.ERROR;
            }
        }
        return ResultState.OK;
    }

    private PayInfo restorePayInfo(List<TbEcomPayOrderDetails> payOrderDetails) {
        PayInfo result = new PayInfo();
        for (TbEcomPayOrderDetails p : payOrderDetails){
            if ("A".equals(p.getDebitAccountType())){
                result.setTypeA(p.getDebitAccountCode());
                result.setCostA(p.getDebitPrice());
            }else if ("B".equals(p.getDebitAccountType())){
                result.setTypeB(p.getDebitAccountCode());
                result.setCostB(p.getDebitPrice());
            }
        }
        return result;
    }

}