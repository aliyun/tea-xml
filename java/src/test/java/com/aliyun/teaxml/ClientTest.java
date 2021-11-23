// This file is auto-generated, don't edit it. Thanks.
package com.aliyun.teaxml;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ClientTest {

    @Test
    public void toXMLTest() throws Exception {
        new Client();
        Map<String, Object> map = new HashMap<>();
        map.put("test", "000");
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<test>000</test>", Client.toXML(map));
    }


    @Test
    public void parseXmlTest() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ListBucketResult>\n" +
                "  <Name>sdk-script</Name>\n" +
                "  <Prefix></Prefix>\n" +
                "  <Marker></Marker>\n" +
                "  <MaxKeys>100</MaxKeys>\n" +
                "  <Delimiter></Delimiter>\n" +
                "  <IsTruncated>false</IsTruncated>\n" +
                "  <Contents>\n" +
                "    <Key>run.sh</Key>\n" +
                "    <LastModified>2019-11-18T07:18:33.000Z</LastModified>\n" +
                "    <ETag>\"test\"</ETag>\n" +
                "    <Type>Normal</Type>\n" +
                "    <Size>4527</Size>\n" +
                "    <StorageClass>Standard</StorageClass>\n" +
                "    <Owner>\n" +
                "      <ID>test</ID>\n" +
                "      <DisplayName>test</DisplayName>\n" +
                "    </Owner>\n" +
                "  </Contents>\n" +
                "  <Contents>\n" +
                "    <Key>task.sh</Key>\n" +
                "    <LastModified>2019-11-18T07:18:27.000Z</LastModified>\n" +
                "    <ETag>\"test\"</ETag>\n" +
                "    <Type>Normal</Type>\n" +
                "    <Size>603</Size>\n" +
                "    <StorageClass>Standard</StorageClass>\n" +
                "    <Owner>\n" +
                "      <ID>test</ID>\n" +
                "      <DisplayName>test</DisplayName>\n" +
                "    </Owner>\n" +
                "  </Contents>\n" +
                "</ListBucketResult>";
        Map map = Client.parseXml(xml, GetBucketResponse.class);
        Assert.assertEquals(7, ((Map) map.get("ListBucketResult")).size());
        Map map1 = Client.parseXml(xml, null);
        Assert.assertEquals("sdk-script", ((Map) map1.get("ListBucketResult")).get("Name"));
        Assert.assertEquals(7, ((Map) map1.get("ListBucketResult")).size());
    }
}
