package com.ywjs.jcsy.decorator;


import java.util.Base64;

import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 装饰器，增强WordToHtmlConverter不能转化图片问题
 * @author zhouwei
 * @create 2020-12-30 10:43
 */
public class InlineImageWordToHtmlConverter extends WordToHtmlConverter {

    public InlineImageWordToHtmlConverter(Document document) {
        super(document);
    }

    @Override
    protected void processImageWithoutPicturesManager(Element currentBlock, boolean inlined, Picture picture){
        Element imgNode = currentBlock.getOwnerDocument().createElement("img");
        StringBuilder sb = new StringBuilder();
        sb.append(Base64.getMimeEncoder().encodeToString(picture.getRawContent()));
        sb.insert(0, "data:"+picture.getMimeType()+";base64,");
        imgNode.setAttribute("src", sb.toString());
        currentBlock.appendChild(imgNode);
    }

}