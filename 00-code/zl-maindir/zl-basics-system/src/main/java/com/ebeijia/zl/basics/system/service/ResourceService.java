package com.ebeijia.zl.basics.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ebeijia.zl.basics.system.domain.Resource;
import com.github.pagehelper.PageInfo;


/**
 *
 * 平台资源表 Service 接口类
 *
 * @User myGen
 * @Date 2018-12-17
 */
public interface ResourceService extends IService<Resource> {

    /**
     * 根据资源key查询资源信息
     * @param key
     * @param loginType
     * @return
     */
    Resource getResourceByKey(String key, String loginType);

    //根据角色Id获取该用户的权限
    List<Resource> getRoleResourceByRoleId(String roleId);

    /***
     * 根据用户Id 查询对应的所有资源
     * @param userId
     * @return
     */
    public List<Resource> getUserResourceByUserId(String userId);

    /**
     * 查询树形结构
     * @param entity
     * @return
     */
    List<Resource> getResourceList(Resource entity);

    /**
     * 查询资源信息列表（分页）
     * @param startNum
     * @param pageSize
     * @param entity
     * @return
     */
    PageInfo<Resource> getResourcePage(int startNum, int pageSize, Resource entity);

    /**
     * 高级查询
     * @param resource
     * @return
     */
    /*List<Resource> getResourceListByResource(Resource resource);*/

    /**
     * 根据序号查询资源信息
     * @param seq
     * @return
     */
    Resource getResourceBySeq(Integer seq);

    /**
     * 查询pid为空的资源信息
     * @param entity
     * @return
     */
    List<Resource> getResourceListByPidIsNull(Resource entity);

    /**
     * 根据pid与资源类型查询
     * @param entity
     * @return
     */
    List<Resource> getResourceListByPidAndType(Resource entity);

}
