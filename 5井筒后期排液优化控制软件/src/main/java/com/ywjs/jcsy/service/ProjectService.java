package com.ywjs.jcsy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.entity.Project;
import com.ywjs.jcsy.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface ProjectService extends IService<Project> {

    void outputProject(Long id, HttpServletResponse response);

    Map<String, List> importProjects(MultipartFile[] files,Integer saveType) throws IOException;

    Map<String, List> importChain(List<String> projectPaths,Integer saveType);

    /**
     * 通过name获取项目
     * @param projectName
     * @return
     */
    Project getProjectByName(String projectName);

    List<Map<String,Object>> getProjectsAndFileCount();

    PageVo<ProjectVo> listPage( Long pageNo,Long pageSize,ProjectParamVo vo);

    ProjectBaseInfoVo getProjectBaseInfo(Long projectId);

    /**
     * 获取项目所有的名称
     * @return
     */
    List<String> getProjectNames();

    void deleteProjectByIds(List<Integer> ids);

    /**
     * 项目三级分类
     * @return
     */
    List<Project3LevelVo> getProject3Level();
}
