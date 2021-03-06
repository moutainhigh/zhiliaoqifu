package com.ebeijia.zl.shop.controller;

import com.ebeijia.zl.common.utils.IdUtil;
import com.ebeijia.zl.common.utils.enums.SpecAccountTypeEnum;
import com.ebeijia.zl.facade.account.vo.AccountLogVO;
import com.ebeijia.zl.facade.account.vo.AccountVO;
import com.ebeijia.zl.shop.constants.PhoneValidMethod;
import com.ebeijia.zl.shop.constants.ResultState;
import com.ebeijia.zl.shop.dao.info.domain.TbEcomItxLogDetail;
import com.ebeijia.zl.shop.dao.info.service.ITbEcomItxLogDetailService;
import com.ebeijia.zl.shop.dao.member.domain.TbEcomPayCard;
import com.ebeijia.zl.shop.service.pay.ICardService;
import com.ebeijia.zl.shop.service.pay.IPayService;
import com.ebeijia.zl.shop.service.pay.IWxPayService;
import com.ebeijia.zl.shop.service.valid.impl.ValidCodeService;
import com.ebeijia.zl.shop.utils.AdviceMessenger;
import com.ebeijia.zl.shop.utils.ShopUtils;
import com.ebeijia.zl.shop.utils.TokenCheck;
import com.ebeijia.zl.shop.vo.*;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "/pay", description = "用于定义支付、信用卡相关接口")
@RequestMapping(value = "/pay")
@RestController
public class PayController {

    private static final BigDecimal oneHundred = BigDecimal.valueOf(100);

    @Autowired
    private IPayService payService;

    @Autowired
    private ICardService cardService;

    @Autowired
    private ValidCodeService validCodeService;

    @Autowired
    private ShopUtils shopUtils;

    @Autowired
    private ITbEcomItxLogDetailService logDetailService;

    @Autowired
    private IWxPayService wxPayService;

    private Logger logger = LoggerFactory.getLogger(PayController.class);

    /**
     * 聚合支付回调
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "聚合支付回调", notes = "")
    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public String callback(HttpServletRequest request) {
        String merid = request.getParameter("merid");
        String msg = request.getParameter("msg");
        String nonceonce = request.getParameter("msg");
        String sign = request.getParameter("sign");
        String payResult = request.getParameter("payResult");

        logger.info(String.format("聚合支付回调入参:%s,\n%s,\n%s,\n%s,\n%s",merid,msg,nonceonce,sign,payResult));
        // 回调成功
        if ( "true".equals(payResult)) {
            // if (status.equals("true")) {
            // String merchantOutOrderNo = "1513698761094";//request.getParameter("merchantOutOrderNo");
            String dmsKey = request.getParameter("merchantOutOrderNo");
            String orderNo = request.getParameter("orderNo");
            return wxPayService.callback(payResult, dmsKey, orderNo);
        }
        return "";
    }


    @TokenCheck(force = true)
    @ApiOperation("绑定银行卡")
    @RequestMapping(value = "/card/bind", method = RequestMethod.POST)
    public JsonResult<Integer> bindBankCard(CardBindInfo card, String validCode) {
        MemberInfo memberInfo = shopUtils.getSession();
        if (memberInfo == null) {
            throw new AdviceMessenger(406, "参数异常");
        }
        boolean valid = validCodeService.checkValidCode(PhoneValidMethod.PAY, card.getPhoneNo(), validCode);
        if (!valid) {
            throw new AdviceMessenger(403, "验证码有误");
        }
        return new JsonResult<>(cardService.bindCard(card));
    }


    @TokenCheck(force = true)
    @ApiOperation("校验银行卡号")
    @RequestMapping(value = "/card/valid", method = RequestMethod.POST)
    public JsonResult<CardInfo> validBankCard(@RequestParam("cardnum") String cardNum) {
        CardInfo cardInfo = cardService.validCardNum(cardNum);
        return new JsonResult<>(cardInfo);
    }


    @TokenCheck(force = true)
    @ApiOperation("列出银行卡")
    @RequestMapping(value = "/card/list", method = RequestMethod.GET)
    public JsonResult<TbEcomPayCard> listAccountCard() {
        TbEcomPayCard cardInfo = cardService.listAccountCard();
        return new JsonResult<>(cardInfo);
    }

    //支付接口
//    @TokenCheck(force = true)
//    @ApiOperation("支付订单")
//    @RequestMapping(value = "/deal/order/{orderid}", method = RequestMethod.POST)
//    public void payOrder(@PathVariable("orderid") String orderId, PayInfo payInfo, @RequestParam("session") String session) {
//        payService.payOrder(payInfo, session);
//    }

    @ApiOperation("列出所有专项账户类型的ID")
    @RequestMapping(value = "/billingtype/list", method = RequestMethod.GET)
    public JsonResult<List<SpecAccountTypeEnum>> listBillingType() {
        List<SpecAccountTypeEnum> list = Arrays.asList(SpecAccountTypeEnum.values());
        return new JsonResult<>(list);
    }


    @TokenCheck(force = true)
    @ApiOperation("列出可用专项账户类型与余额")
    @RequestMapping(value = "/balance/list/", method = RequestMethod.GET)
    public JsonResult<List<AccountVO>> listAccountDetail(@RequestParam("session") String session) {
        MemberInfo memberInfo = shopUtils.getSession();
        List<AccountVO> accountVOS = payService.listAccountDetail(memberInfo.getMemberId(), session);
        //处理流水金额问题
        accountVOS.stream().forEach(vo -> {
            vo.setAccBal(vo.getAccBal().multiply(oneHundred));
            vo.setCouponBal(vo.getCouponBal().multiply(oneHundred));
        });
        return new JsonResult<>(accountVOS);
    }


    //交易流水
    @TokenCheck(force = true)
    @ApiOperation("列出交易流水记录")
    @RequestMapping(value = "/deal/list/{type}", method = RequestMethod.GET)
    public JsonResult<LogDetail> listAccountDeals(@PathVariable("type") String type, @RequestParam(value = "range", required = false) String range, @RequestParam(value = "start", required = false) String start, @RequestParam(value = "limit", required = false) String limit, @RequestParam String session) {
        PageInfo<AccountLogVO> deals = payService.listDeals(range, type, null, start, limit);
        LogDetail logDetail = dumb(deals);
        return new JsonResult<>(logDetail);
    }


    @TokenCheck(force = true)
    @ApiOperation("列出交易流水记录")
    @RequestMapping(value = "/deal/list/common/{type}", method = RequestMethod.GET)
    public JsonResult<LogDetail> listAccountDealsForAType(@PathVariable("type") String type, @RequestParam(value = "range", required = false) String range, @RequestParam(value = "method", required = false) String method, @RequestParam(value = "start", required = false) String start, @RequestParam(value = "limit", required = false) String limit, @RequestParam String session) {
        PageInfo<AccountLogVO> deals = payService.listDeals(range, type, method, start, limit);
        LogDetail logDetail = dumb(deals);
        return new JsonResult<>(logDetail);
    }


    private LogDetail dumb(PageInfo<AccountLogVO> deals) {
        Map<String, TbEcomItxLogDetail> map = new HashMap<>();
        deals.getList().stream().forEach(deal -> {
            TbEcomItxLogDetail id = logDetailService.getById(deal.getItfPrimaryKey());
            if (id == null) {
                id = new TbEcomItxLogDetail();
                id.setAmount(0);
                id.setTitle("交易流水");
                id.setDescinfo("如果您对此流水有疑问，请联系HR");
                id.setImg("");
                id.setItxKey(deal.getTxnPrimaryKey());
                id.setOutId(IdUtil.getNextId());
                id.setSourceBid(deal.getPriBId());
            }
            map.put(id.getItxKey(), id);
        });
        LogDetail logDetail = new LogDetail();
        logDetail.setDeals(deals);
        logDetail.setList(map);
        return logDetail;
    }


    //交易流水
    @TokenCheck(force = true)
    @ApiOperation("列出交易流水记录")
    @RequestMapping(value = "/deal/list", method = RequestMethod.GET)
    public JsonResult<PageInfo<AccountLogVO>> listAccountDealst(@RequestParam(value = "range", required = false) String range, @RequestParam(value = "start", required = false) String start, @RequestParam(value = "limit", required = false) String limit, @RequestParam String session) {
        PageInfo<AccountLogVO> deals = payService.listDeals(range, null, null, start, limit);
        return new JsonResult<>(deals);
    }


    //交易流水
    @TokenCheck(force = true)
    @ApiOperation("获取订单DMS对应的流水记录")
    @RequestMapping(value = "/deal/get/{dms}", method = RequestMethod.GET)
    public JsonResult<PayDealInfo> getDealInfoByDms(@PathVariable("dms") String dms) {
        PayDealInfo deal = payService.getDeal(dms);
        return new JsonResult<>(deal);
    }


    /**
     * 托管账户转账
     *
     * @return
     */
    @TokenCheck(force = true)
    @ApiOperation("托管账户转出到银行卡")
    @ApiResponses({@ApiResponse(code = ResultState.OK, message = "操作成功"),
            @ApiResponse(code = ResultState.ERROR, message = "服务器内部异常"),
            @ApiResponse(code = ResultState.FORBIDDEN, message = "权限不足"),
            @ApiResponse(code = ResultState.UNAUTHORIZED, message = "请重新登录")})
    @RequestMapping(value = "/deal/transfer", method = RequestMethod.POST)
    public JsonResult transferToCard(@RequestParam("deal") Long dealInfo, @RequestParam(required = false) String vaildCode, @RequestParam(value = "session") Double session) {
        if (session == null) {
            session = 0D;
        }
        int state = payService.transferToCard(dealInfo, vaildCode, session);
        return new JsonResult(state);
    }

}
