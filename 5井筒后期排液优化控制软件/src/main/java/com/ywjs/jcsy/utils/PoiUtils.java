package com.ywjs.jcsy.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.ywjs.jcsy.common.constant.GlobalContstant;
import com.ywjs.jcsy.decorator.InlineImageWordToHtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.RichTextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.zwobble.mammoth.DocumentConverter;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * poi工具
 *
 * @author zhouwei
 * @create 2020-11-16 9:09
 */
@Slf4j
public class PoiUtils {

    public static String txtToStr(String path) throws Exception {
        StringBuffer content = new StringBuffer();
        FileInputStream in = new FileInputStream(path);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(in, "GBK"));

        String lineTxt = "";
        while ((lineTxt = buffer.readLine()) != null) {
            content.append(lineTxt).append("<br>");
        }
        return "<div>" + content.toString() + "</div>";
    }

    /**
     * word转化为docx
     *
     * @param wordPath
     * @return
     */
    public static String wordToDocx(String wordPath) {
        // 获取路径和文件名称
        DocumentConverter converter = new DocumentConverter();
        org.zwobble.mammoth.Result<String> result = null;
        try {
            result = converter.convertToHtml(new File(wordPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        Set<String> warnings = result.getWarnings(); // Any warnings during conversion
        return result.getValue();
    }


    /**
     * word转化为doc
     *
     * @param path
     * @param file
     * @return
     * @throws Exception
     */
    public static String wordToDoc(String path, String file) throws Exception {
        InputStream input = new FileInputStream(path + file);
        HWPFDocument wordDocument = new HWPFDocument(input);
//        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        WordToHtmlConverter wordToHtmlConverter = new InlineImageWordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
//        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
//            public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
//                return suggestedName;
//            }
//        });
        wordToHtmlConverter.processDocument(wordDocument);
        List pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                pic.writeImageContent(new FileOutputStream(path + pic.suggestFullFileName()));
            }
        }
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
//        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.ENCODING, "GBK");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();
        String content = new String(outStream.toByteArray());
        return content;
    }

    /**
     * excel的xls格式转化为html
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String excelToHtml(String path) throws Exception {
        HSSFWorkbook workBook = null;

        File file = new File(path);
        String fileName = file.getName();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        InputStream input = new FileInputStream(path);

        // 判断是xls 或 xlsx
        if (fileType.equalsIgnoreCase("xlsx")) {
            //将07版转化为03版
            Xssf2Hssf xlsx2xls = new Xssf2Hssf();
            XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(input);
            workBook = new HSSFWorkbook();
            xlsx2xls.transformXSSF(xSSFWorkbook, workBook);
        } else {
            workBook = new HSSFWorkbook(input);
        }

        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
        excelToHtmlConverter.processWorkbook(workBook);
        List pics = workBook.getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                pic.writeImageContent(new FileOutputStream(path + pic.suggestFullFileName()));
            }
        }
        Document htmlDocument = excelToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "GBK");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();
        String content = new String(outStream.toByteArray());
        return content;
    }


    /**
     * ppt 转化为 image
     *
     * @param filePath
     * @param baseServicePath
     * @return
     */
    public static List<String> doPPTtoImage(String filePath, String baseServicePath) throws Exception {
        List<String> result = new ArrayList<>();
        FileInputStream is = new FileInputStream(filePath);
        SlideShow ppt = new SlideShow(is);
        is.close();
        Dimension pgsize = ppt.getPageSize();
        org.apache.poi.hslf.model.Slide[] slide = ppt.getSlides();
        for (int i = 0; i < slide.length; i++) {
            try {
                TextRun[] truns = slide[i].getTextRuns();
                for (int k = 0; k < truns.length; k++) {
                    RichTextRun[] rtruns = truns[k].getRichTextRuns();
                    for (int l = 0; l < rtruns.length; l++) {
                        int index = rtruns[l].getFontIndex();
                        String name = rtruns[l].getFontName();
                        rtruns[l].setFontIndex(1);
                        rtruns[l].setFontName("宋体");
                    }
                }
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                graphics.setPaint(Color.BLUE);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
                slide[i].draw(graphics);
                // 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径,放入服务路径下
//            String outputPath = StrUtil.format(GlobalContstant.UPLOAD_BASE_PATH +"preview/ppt/{}/{}.jpeg", fileName, (i + 1));
//            String servicePath = StrUtil.format(baseServicePath+"preview/ppt/{}/{}.jpeg", fileName, (i + 1));
//            result.add(servicePath);
//            FileOutputStream out = new FileOutputStream(outputPath);
//            javax.imageio.ImageIO.write(img, "jpeg", out);
//            out.close();

                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                ImageIO.write(img, "jpeg", out2);
                String encodeBase64 = ImgUtil.encodeBase64(out2.toByteArray(), false);
                result.add(encodeBase64);
            } catch (Exception e) {
                String fileName = new File(filePath).getName();
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                log.error(fileName + "第" + i + 1 + "张幻灯片转化为图片失败");
            }
        }
        return result;
    }

    public static List<String> doPPTXtoImage(String filePath, String baseServicePath) {
        List<String> result = new ArrayList<>();
        FileInputStream is = null;
        XMLSlideShow ppt = null;
        try {
            is = new FileInputStream(filePath);
            ppt = new XMLSlideShow(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Dimension pgsize = ppt.getPageSize();

        for (int i = 0; i < ppt.getSlides().length; i++) {
            try {
                //防止中文乱码
                for (XSLFShape shape : ppt.getSlides()[i].getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape tsh = (XSLFTextShape) shape;
                        for (XSLFTextParagraph p : tsh) {
                            for (XSLFTextRun r : p) {
                                r.setFontFamily("宋体");
                            }
                        }
                    }
                }
                BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = img.createGraphics();
                // clear the drawing area
                graphics.setPaint(Color.white);
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

                // render
                ppt.getSlides()[i].draw(graphics);

                // save the output
//                String outputPath = StrUtil.format(GlobalContstant.UPLOAD_BASE_PATH +"preview/ppt/{}/", fileName);
//                FileUtil.mkdir(outputPath);
//                String outputFilePath = outputPath + (i + 1) + ".jpeg";
//                String servicePath = StrUtil.format(baseServicePath+"preview/ppt/{}/{}.jpeg", fileName, (i + 1));
//                result.add(servicePath);
//                FileOutputStream out = new FileOutputStream(outputFilePath);
//                javax.imageio.ImageIO.write(img, "png", out);
//                out.close();

                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                ImageIO.write(img, "jpeg", out2);
                String encodeBase64 = ImgUtil.encodeBase64(out2.toByteArray(), false);
                result.add(encodeBase64);
            } catch (Exception e) {
                File file = new File(filePath);
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                log.error(fileName + "第" + i + 1 + "张幻灯片转化为图片失败");
            }
        }
        return result;
    }

}
