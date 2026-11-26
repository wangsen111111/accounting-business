package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.Dimension;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface DimensionService extends IService<Dimension> {

    /**
     * 通过dimensionName 获取 dimension
     * @param dimensionName
     * @return
     */
    Dimension getOneByDimensionName(String dimensionName);
}
