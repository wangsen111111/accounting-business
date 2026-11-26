package com.ywjs.jcsy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-08-21
 */
public interface JsmindService extends IService<Jsmind> {

    Jsmind getJsmindBydimensionId(Long dimensionId);

    List<CountVo> countNodeFile(Long projectId);

    Object countTag(Long projectId);

    void uninstallFile(String jsmindId, String nodeId, Long fileId);

    void saveOrUpdateMindMap(Jsmind jsmind);

    Jsmind add(Jsmind jsmind);

    void deleteById(String id);

    void saveMindMapTotemplate(Jsmind jsmind,String name);

    Boolean checkMindMapName(String name);

    List<JsmindDataVo.JsMindData> getNodesByProjectId(Long projectId);


    AssociatedVo getAssociated(String jsmindId);

    /**
     * 思维导图关联项目和维度
     * @param associatedId
     */
    void associated(AssociatedIdVo associatedId);

    /**
     * 根据节点id获取节点
     * @param jsmind
     * @param nodeId
     * @return
     */
    JsmindDataVo.JsMindData getCopyNodeByNodeId(Jsmind jsmind, String nodeId);

    Jsmind pasteNode(PasteNodeVo vo);

    void exportJsmind(Jsmind jsmind, HttpServletResponse response) throws Exception;

    Jsmind importUpload(MultipartFile files);

    Jsmind importNode(MultipartFile files, String nodeId, Jsmind jsmind);

    Jsmind generateJsmind(List<String> jsmindIds, String dimensionName);
}
