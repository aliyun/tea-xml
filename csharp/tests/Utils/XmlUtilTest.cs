﻿using System;
using System.Collections.Generic;
using System.Text;
using AlibabaCloud.TeaXML.Utils;
using Tea;
using tests.Models;
using Xunit;
using static tests.Models.ListAllMyBucketsResult;
using static tests.Models.ListAllMyBucketsResult.Buckets;

namespace tests.Utils
{
    public class XmlUtilTest
    {
        ToBodyModel model;
        public XmlUtilTest()
        {
            model = new ToBodyModel();
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
            model.listAllMyBucketsResult.TestString = "string";
            model.listAllMyBucketsResult.TestListNull = null;
            model.listAllMyBucketsResult.dict = new Dictionary<string, string> { { "key", "value" } };
        }

        [Fact]
        public void TestXml()
        {
            string xmlStr = XmlUtil.SerializeXml(model);
            Assert.NotNull(xmlStr);
            Assert.NotEmpty(xmlStr);

            Assert.Equal(xmlStr, XmlUtil.SerializeXml(model.ToMap()));

            model.listAllMyBucketsResult.dict = null;
            xmlStr = XmlUtil.SerializeXml(model);
            Dictionary<string, object> dict = XmlUtil.DeserializeXml(xmlStr, model.GetType());
            Assert.NotNull(dict);
        }
    }
}
