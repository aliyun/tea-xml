// This file is auto-generated, don't edit it. Thanks.

using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using AlibabaCloud.TeaXML.Utils;
using Tea;


namespace AlibabaCloud.TeaXML
{
    public class Client 
    {

        public static Dictionary<string, object> ParseXml(string body, Type response)
        {
            return XmlUtil.DeserializeXml(body, response);
        }

        public static string ToXML(Dictionary<string, object> body)
        {
            return XmlUtil.SerializeXml(body);
        }

    }
}
