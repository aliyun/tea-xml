// This file is auto-generated, don't edit it. Thanks.
package com.aliyun.teaxml;

import com.aliyun.tea.*;


public class Client {

    public static java.util.Map<String, Object> parseXml(String body, Class response) throws Exception {
        return XmlUtil.DeserializeXml(body, response);
    }

    public static String toXML(java.util.Map<String, Object> body) throws Exception {
        return XmlUtil.mapToXml(body);
    }
}
