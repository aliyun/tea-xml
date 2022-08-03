using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;
using AlibabaCloud.TeaXML;
using Moq;
using Tea;
using tests.Models;
using Xunit;
using static tests.Models.ListAllMyBucketsResult;
using static tests.Models.ListAllMyBucketsResult.Buckets;

namespace tests
{
    public class ClientTest
    {
        [Fact]
        public void Test_ToBody()
        {
            TeaModel modelNull = new TeaModel();
            Assert.Empty(Client.ToXML(modelNull.ToMap()));

            ToBodyModel model = new ToBodyModel();
            ListAllMyBucketsResult result = new ListAllMyBucketsResult();
            Buckets buckets = new Buckets();
            buckets.bucket = new List<Bucket>();
            buckets.bucket.Add(new Bucket { CreationDate = "2015-12-17T18:12:43.000Z", ExtranetEndpoint = "oss-cn-shanghai.aliyuncs.com", IntranetEndpoint = "oss-cn-shanghai-internal.aliyuncs.com", Location = "oss-cn-shanghai", Name = "app-base-oss", StorageClass = "Standard" });
            buckets.bucket.Add(new Bucket { CreationDate = "2014-12-25T11:21:04.000Z", ExtranetEndpoint = "oss-cn-hangzhou.aliyuncs.com", IntranetEndpoint = "oss-cn-hangzhou-internal.aliyuncs.com", Location = "oss-cn-hangzhou", Name = "atestleo23", StorageClass = "IA" });
            buckets.bucket.Add(null);
            result.buckets = buckets;
            Owner owner = new Owner { ID = 512, DisplayName = "51264" };
            result.owner = owner;
            model.listAllMyBucketsResult = result;
            model.listAllMyBucketsResult.testStrList = new List<string> { "1", "2" };
            model.listAllMyBucketsResult.owners = new List<Owner>();
            model.listAllMyBucketsResult.owners.Add(owner);
            model.listAllMyBucketsResult.TestDouble = 1;
            model.listAllMyBucketsResult.TestFloat = 2;
            model.listAllMyBucketsResult.TestLong = 3;
            model.listAllMyBucketsResult.TestShort = 4;
            model.listAllMyBucketsResult.TestUInt = 5;
            model.listAllMyBucketsResult.TestULong = 6;
            model.listAllMyBucketsResult.TestUShort = 7;
            model.listAllMyBucketsResult.TestBool = true;
            model.listAllMyBucketsResult.TestNull = null;
            model.listAllMyBucketsResult.TestListNull = null;
            string xmlStr = Client.ToXML(model.ToMap());
            Assert.NotNull(xmlStr);

            Dictionary<string, object> xmlBody = (Dictionary<string, object>)Client.ParseXml(xmlStr, typeof(ToBodyModel));
            ToBodyModel teaModel = TeaModel.ToObject<ToBodyModel>(xmlBody);
            Assert.NotNull(teaModel);
            Assert.Equal(1, teaModel.listAllMyBucketsResult.TestDouble);

            string xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<body><Contents><Owner><DisplayName>disName</DisplayName></Owner><Key>key</Key></Contents><Contents/></body>";
            Dictionary<string, object> map = Client.ParseXml(xml, null);
            Assert.False(map.ContainsKey("xml"));
            Assert.Single(map);
            Assert.True(((Dictionary<string, object>)map["body"]).ContainsKey("Contents"));
            List<object> list = (List<object>)((Dictionary<string, object>)map["body"])["Contents"];
            Assert.Equal(2, list.Count);
            Assert.Equal("key", ((Dictionary<string, object>)list[0])["Key"]);
            Assert.Equal("disName", ((Dictionary<string, object>)((Dictionary<string, object>)list[0])["Owner"])["DisplayName"]);
            Assert.Null((Dictionary<string, object>)list[1]);
        }
    }
}
