package com.aliyun.teaxml;

import com.aliyun.teaxml.GetBucketResponse.*;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XmlUtilTest {
    @Test
    public void mapToXml() throws Exception {
        Body body = new Body();
        GetBucketResponseListBucketResult result = new GetBucketResponseListBucketResult();
        GetBucketResponseListBucketResultContents contents = new GetBucketResponseListBucketResultContents();
        GetBucketResponseListBucketResultContents contents2 = new GetBucketResponseListBucketResultContents();
        GetBucketResponseListBucketResultContentsOwner owner = new GetBucketResponseListBucketResultContentsOwner();
        owner.displayName = "disName";
        contents.owner = owner;
        contents.key = "key";
        ArrayList<GetBucketResponseListBucketResultContents> list = new ArrayList<>();
        list.add(contents);
        list.add(contents2);
        result.contents = list;
        body.contents = result;
        Map<String, Object> map = body.toMap();
        String xml = XmlUtil.mapToXml(map);
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<body><Contents><Owner><DisplayName>disName</DisplayName></Owner><Key>key</Key></Contents><Contents/></body>", xml);
    }

    @Test
    public void castTypeTest() throws Exception {
        XmlUtil xmlUtil = new XmlUtil();
        Method castType = xmlUtil.getClass().getDeclaredMethod("castType", Class.class, String.class);
        castType.setAccessible(true);
        Assert.assertTrue((boolean) castType.invoke(xmlUtil, boolean.class, "true"));
        Assert.assertTrue((Boolean) castType.invoke(xmlUtil, Boolean.class, "true"));

        Assert.assertEquals(1, (int) castType.invoke(xmlUtil, int.class, "1"));
        Assert.assertEquals(1, (int) castType.invoke(xmlUtil, Integer.class, "1"));

        Assert.assertEquals(1L, (long) castType.invoke(xmlUtil, long.class, "1"));
        Assert.assertEquals(1L, (long) castType.invoke(xmlUtil, Long.class, "1"));
    }

    @Test
    public void mapToXmlTest() throws Exception{
        Assert.assertEquals("", XmlUtil.mapToXml(null));
        Map<String, Object> map = new HashMap<>();
        Assert.assertEquals("", XmlUtil.mapToXml(map));

        map.put("1", null);
        map.put("2", null);
        try {
            XmlUtil.mapToXml(map);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals("This map does not have a unique root tag", e.getMessage());
        }
    }
}
