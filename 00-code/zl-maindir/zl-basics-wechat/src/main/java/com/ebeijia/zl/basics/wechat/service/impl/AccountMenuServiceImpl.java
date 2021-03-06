package com.ebeijia.zl.basics.wechat.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ebeijia.zl.basics.wechat.domain.AccountMenu;
import com.ebeijia.zl.basics.wechat.mapper.AccountMenuMapper;
import com.ebeijia.zl.basics.wechat.service.AccountMenuService;

/**
 *
 * 微信菜单表 Service 实现类
 *
 * @User zhuqi
 * @Date 2018-12-08
 */
@Service
public class AccountMenuServiceImpl extends ServiceImpl<AccountMenuMapper, AccountMenu> implements AccountMenuService{
	
	@Autowired
	private AccountMenuMapper accountMenuMapper;

	

	public List<AccountMenu> listForPage(AccountMenu searchEntity) {
		return accountMenuMapper.listForPage(searchEntity);
	}

	public List<AccountMenu> listParentMenu() {
		return accountMenuMapper.listParentMenu();
	}

}
