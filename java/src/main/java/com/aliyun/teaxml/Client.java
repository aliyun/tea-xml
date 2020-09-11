// This file is auto-generated, don't edit it. Thanks.
package com.aliyun.teaxml;


public class Client {

    public static java.util.Map<String, Object> parseXml(String body, Class response) {
        try {
            return XmlUtil.DeserializeXml(body, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toXML(java.util.Map<String, Object> body) {
        try {
            return XmlUtil.mapToXml(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
