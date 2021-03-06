package com.ebeijia.zl.shop.dao.order.service.impl;

import com.ebeijia.zl.shop.dao.order.domain.TbEcomOrderProductItem;
import com.ebeijia.zl.shop.dao.order.mapper.TbEcomOrderProductItemMapper;
import com.ebeijia.zl.shop.dao.order.service.ITbEcomOrderProductItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * 订单SKU明细表 Service 实现类
 *
 * @User zl_shop
 * @Date 2018-12-26
 */
@Service
public class TbEcomOrderProductItemService extends ServiceImpl<TbEcomOrderProductItemMapper, TbEcomOrderProductItem> implements ITbEcomOrderProductItemService{

    @Autowired
    private TbEcomOrderProductItemMapper ecomOrderProductItemMapper;

    @Override
    public TbEcomOrderProductItem getOrderProductItemBySOrderId(String sOrderId) {
        return ecomOrderProductItemMapper.getOrderProductItemBySOrderId(sOrderId);
    }
}
