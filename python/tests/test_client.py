import unittest

import xml.etree.ElementTree as ET

from tea_xml.client import Client
from Tea.model import TeaModel
from collections import defaultdict

class TestClient(unittest.TestCase):
    class ListAllMyBucketsResult(TeaModel):
        def __init__(self):
                super().__init__()
                self.owner = None
                self._names["owner"] = "Owner"
                self.buckets = None
                self._names["buckets"] = "Buckets"
                self.test_str_list = []
                self._names["test_str_list"] = "listStr"
                self.owners = []
                self._names["owners"] = "Owners"
                self.test_num = 0
                self.test_null = None
                self.test_bool = False
                self.dict = {}
                
    class Owner(TeaModel):
        def __init__(self):
                super().__init__()
                self.id = 0
                self.display_name = ""

    class Bucket(TeaModel):
        def __init__(self):
                super().__init__()
                self.creation_date = ""
                self.extranet_endpoint = ""

    class Buckets(TeaModel):
        def __init__(self):
                super().__init__()
                self.bucket = []
                self._names["bucket"] = "Bucket"

    class ToBodyModel(TeaModel):
        def __init__(self):
                super().__init__()
                self.listAllMyBucketsResult = None
                self._names["listAllMyBucketsResult"] = "ListAllMyBucketsResult"


    def test_to_xml(self):
        self.assertIsNone(Client.to_xml(None))
        model = TestClient.ToBodyModel()
        result = TestClient.ListAllMyBucketsResult()
        buckets = TestClient.Buckets()
        bucket1 = TestClient.Bucket()
        bucket1.creation_date = "2015-12-17T18:12:43.000Z"
        bucket1.extranet_endpoint = "oss-cn-shanghai.aliyuncs.com"
        buckets.bucket.append(bucket1)
        bucket2 = TestClient.Bucket()
        bucket2.creation_date = "2014-12-25T11:21:04.000Z"
        bucket2.extranet_endpoint = "oss-cn-hangzhou.aliyuncs.com"
        buckets.bucket.append(bucket2)
        bucket_none = None
        buckets.bucket.append(bucket_none)
        result.buckets = buckets
        owner = TestClient.Owner()
        owner.id = 512
        owner.display_name = "51264"
        result.owner = owner
        model.listAllMyBucketsResult = result
        result.test_str_list = ["1","2"]
        result.owners.append(owner)
        result.test_num = 10
        result.test_bool = True
        result.test_null = None
        xml_str = Client.to_xml(model)
        self.assertIsNotNone(xml_str)

        root = ET.fromstring(xml_str)
        re = Client.parse_xml(root)
        self.assertIsNotNone(re)
        self.assertEqual(2,re["ListAllMyBucketsResult"]["listStr"].__len__())
        self.assertEqual("10",re["ListAllMyBucketsResult"]["test_num"])
