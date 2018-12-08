package com.ebeijia.zl.shop.controller;

import com.ebeijia.zl.shop.dao.goods.domain.TbEcomGoods;
import com.ebeijia.zl.shop.dao.goods.domain.TbEcomGoodsDetail;
import com.ebeijia.zl.shop.service.goods.IGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @User J
 * @Date 2018/12/05
 */
@Api(value = "/goods",description = "用于定义商品相关接口")
@RequestMapping("/goods")
@RestController
public class GoodsController {
    @Autowired
    IGoodsService goodsService;

    @ApiOperation("商品列表，分页")
    @RequestMapping(value = "/list/{orderby}",method = RequestMethod.GET)
    public void listGoods(@PathVariable(required = false) String orderby,int start,int limit){
        List<TbEcomGoods> list = goodsService.listGoods(0,orderby,start,limit);
    }


    @ApiOperation("按类型的商品列表")
    @RequestMapping(value = "/list/cat{catid}/{orderby}",method = RequestMethod.GET)
    public void listGoods(@PathVariable int catid, @PathVariable(required = false) String orderby,int start,int limit){
        List<TbEcomGoods> list = goodsService.listGoods(catid,orderby,start,limit);
    }

    @ApiOperation("商品详情")
    @RequestMapping(value = "/detail/get/{goods}",method = RequestMethod.GET)
    public void goodsDetail(@PathVariable("goods") String goodsId){
        TbEcomGoodsDetail detail = goodsService.goodDetail(goodsId);
    }

    @ApiOperation("商品相册")
    @RequestMapping(value = "/gallery/get/{goods}",method = RequestMethod.GET)
    public void goodsGallery(@PathVariable("goods") String goodsId){}
//
//    @ApiOperation("获取图片")
//    @RequestMapping(value = "/image/get/{id}",method = RequestMethod.GET)
//    public void goodsImage(@PathVariable("id") String imageId){}

    @ApiOperation("商品库存查询")
    @RequestMapping(value = "/amount/get/{goods}",method = RequestMethod.GET)
    public void goodsAvailableAmount(@PathVariable("goods") String goodsId){
        TbEcomGoodsDetail detail = goodsService.goodDetail(goodsId);
    }
    //
    //
}