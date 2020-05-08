import unittest

import xml.etree.ElementTree as ET

from tea_xml import client
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
        _base_type = {int, float, bool, complex, str}
        _list_type = {list, tuple, set}
        _dict_type = {dict}

        def __init__(self):
                super().__init__()
                self.listAllMyBucketsResult = None
                self._names["listAllMyBucketsResult"] = "ListAllMyBucketsResult"

        def _entity_to_dict(self, obj):
            if type(obj) in self._dict_type:
                obj_rtn = {k: self._entity_to_dict(v) for k, v in obj.items()}
                return obj_rtn
            elif type(obj) in self._list_type:
                return [self._entity_to_dict(v) for v in obj]
            elif type(obj) in self._base_type:
                return obj
            elif isinstance(obj, TeaModel):
                prop_list = [(p, not callable(getattr(obj, p)) and p[0] != "_")
                             for p in dir(obj)]
                obj_rtn = {}
                for i in prop_list:
                    if i[1]:
                        obj_rtn[obj._names.get(i[0]) or i[0]] = self._entity_to_dict(
                            getattr(obj, i[0]))
                return obj_rtn

        def to_map(self):
            prop_list = [(p, not callable(getattr(self, p)) and p[0] != "_")
                         for p in dir(self)]
            pros = {}
            for i in prop_list:
                if i[1]:
                    pros[self._names.get(i[0]) or i[0]] = self._entity_to_dict(
                        getattr(self, i[0]))
            return pros

    def test_to_xml(self):
        self.assertIsNone(client.to_xml(None))
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
        xml_str = client.to_xml(model)
        self.assertIsNotNone(xml_str)

        root = ET.fromstring(xml_str)
        re = client.parse_xml(root)
        self.assertIsNotNone(re)
        self.assertEqual(2,re["ListAllMyBucketsResult"]["listStr"].__len__())
        self.assertEqual("10",re["ListAllMyBucketsResult"]["test_num"])
