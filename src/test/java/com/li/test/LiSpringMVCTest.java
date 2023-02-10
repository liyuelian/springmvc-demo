package com.li.test;

import com.li.myspringmvc.xml.XMLParse;
import org.junit.Test;

/**
 * @author 李
 * @version 1.0
 */
public class LiSpringMVCTest {
    @Test
    public void readXML() {
        String basePackage = XMLParse.getBasePackage("myspringmvc.xml");
        System.out.println("basePackage=" + basePackage);
    }
}
