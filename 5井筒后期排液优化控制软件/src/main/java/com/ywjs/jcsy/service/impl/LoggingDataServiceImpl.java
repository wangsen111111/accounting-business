package com.ywjs.jcsy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.entity.LoggingData;
import com.ywjs.jcsy.mapper.LoggingDataMapper;
import com.ywjs.jcsy.service.LoggingDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-17
 */
@Service
public class LoggingDataServiceImpl extends ServiceImpl<LoggingDataMapper, LoggingData> implements LoggingDataService {

    @Autowired
    LoggingDataService loggingDataService;

    @Override
    public List<LoggingData> getFileNames(Integer projectId) {
        QueryWrapper<LoggingData> wrapper = new QueryWrapper<>();
        wrapper.select("distinct file_name");
        LambdaQueryWrapper<LoggingData> lambda = wrapper.lambda();
        lambda.eq(LoggingData::getProjectId, projectId);
        List<LoggingData> loggingDataList = loggingDataService.getBaseMapper().selectList(lambda);
        return loggingDataList;
    }

    @Override
    public List<LoggingData> getLoggingData(String fileName) {
        List<LoggingData> loggingDataList = loggingDataService.getBaseMapper().selectList(new LambdaQueryWrapper<LoggingData>().eq(LoggingData::getFileName, fileName));
        return loggingDataList;
    }

    @Override
    public List<LoggingData> fileAnalysis(MultipartFile[] files, Long projectId) {
        List<LoggingData> loggingDataList = new ArrayList<>();
        if (files != null && files.length > 0) {
            try {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String[] split = originalFilename.split("\\.");
                        loggingDataService.remove(new LambdaUpdateWrapper<LoggingData>().eq(LoggingData::getFileName, split[0]));
                        InputStream inputStream = file.getInputStream();
                        InputStreamReader is = new InputStreamReader(inputStream);
                        BufferedReader br = new BufferedReader(is);
                        String s;
                        List<String> parameters = new ArrayList<>();
                        List<String> values = new ArrayList<>();
                        boolean parametersFlag = false;
                        boolean valuesFlag = false;
                        while ((s = br.readLine()) != null) {
                            if (parametersFlag) {
                                int index;
                                if ((index = s.indexOf(" .m")) > -1) {
                                    String s1 = s.substring(0, 1).toLowerCase() + s.substring(1, index);
                                    if (s1.equals("dEPT")) {
                                        parameters.add(s1.toLowerCase());
                                    } else {
                                        parameters.add(s1);
                                    }
                                }
                            }
                            if (valuesFlag) {
                                if (s.trim().equals("")) {
                                    break;
                                } else {
                                    values.add(s);
                                }
                            }
                            if (s.equals("~Curve")) {
                                parametersFlag = true;
                            }
                            if (s.equals("~Parameter")) {
                                parametersFlag = false;
                            }
                            if (s.equals("~Ascii")) {
                                valuesFlag = true;
                            }
                        }
                        List<Map<String, Object>> mapList = new ArrayList<>();
                        for (String value : values) {
                            String[] valArray = value.split(" ");
                            int i = 0;
                            Map<String, Object> map = new HashMap<>(6);
                            for (String valStr : valArray) {
                                if (!valStr.equals("")) {
                                    double valDou = Double.parseDouble(valStr);
                                    if (valDou > -999) {
                                        map.put(parameters.get(i), valDou);
                                    }
                                    i++;
                                }
                                if (i >= 6) {
                                    break;
                                }
                            }
                            map.put("fileName", split[0]);
                            map.put("fileType", split[1]);
                            map.put("projectId", projectId);
                            mapList.add(map);
                        }
                        loggingDataList = JSON.parseArray(JSON.toJSONString(mapList), LoggingData.class);
                        loggingDataService.saveBatch(loggingDataList);
                        System.out.println(loggingDataList);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }
        }
        return loggingDataList;
    }
}
