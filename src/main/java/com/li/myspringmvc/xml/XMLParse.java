package com.li.myspringmvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @author 李
 * @version 1.0
 * XMLParse用于解析spring配置文件
 */
public class XMLParse {
    public static String getBasePackage(String xmlFile) {
        SAXReader saxReader = new SAXReader();

        //maven的类路径是在target/li-springmvc/WEB-INF/classes/目录下
        //通过类的加载路径-->获取到spring配置文件[对应的资源流]
        InputStream inputStream =
                XMLParse.class.getClassLoader().getResourceAsStream(xmlFile);
        try {
            //得到配置文件的文档
            Document document = saxReader.read(inputStream);
            Element rootElement = document.getRootElement();
            Element componentScanElement = rootElement.element("component-scan");
            Attribute attribute = componentScanElement.attribute("base-package");
            String basePackage = attribute.getText();
            return basePackage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
