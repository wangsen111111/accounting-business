package com.ywjs.jcsy.service.impl;

import com.ywjs.jcsy.entity.Operation;
import com.ywjs.jcsy.mapper.OperationMapper;
import com.ywjs.jcsy.service.OperationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-08-13
 */
@Service
@Transactional
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation> implements OperationService {

}
