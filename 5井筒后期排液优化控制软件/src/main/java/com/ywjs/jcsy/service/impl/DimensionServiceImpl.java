package com.ywjs.jcsy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.entity.Dimension;
import com.ywjs.jcsy.mapper.DimensionMapper;
import com.ywjs.jcsy.service.DimensionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@Service
@Transactional
public class DimensionServiceImpl extends ServiceImpl<DimensionMapper, Dimension> implements DimensionService {

    @Override
    public Dimension getOneByDimensionName(String dimensionName) {
        return this.getOne(new LambdaQueryWrapper<Dimension>().eq(Dimension::getDimensionName, dimensionName));
    }
}
