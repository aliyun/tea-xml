import unittest
from xml.etree import ElementTree

from alibabacloud_tea_xml.client import Client
from Tea.model import TeaModel


class TestClient(unittest.TestCase):
    class ListAllMyBucketsResult(TeaModel):
        def __init__(self, buckets=None, owner=None, test_str_list=None, test_num=None, test_bool=None, test_null=None):
            self.buckets = buckets
            self.owner = owner
            self.test_str_list = test_str_list
            self.test_num = test_num
            self.test_bool = test_bool
            self.test_null = test_null
            self.owners = []

        def to_map(self):
            result = {}
            result['buckets'] = self.buckets
            result['owners'] = self.owners
            result['owner'] = self.owner
            result['test_str_list'] = self.test_str_list
            result['test_num'] = self.test_num
            result['test_bool'] = self.test_bool
            result['test_null'] = self.test_null
            return result

    class Owner(TeaModel):
        def __init__(self, id=None, display_name=None):
            self.id = id
            self.display_name = display_name

        def to_map(self):
            result = {}
            result['id'] = self.id
            result['display_name'] = self.display_name
            return result

    class Bucket(TeaModel):
        def __init__(self, creation_date=None, extranet_endpoint=None):
            self.creation_date = creation_date
            self.extranet_endpoint = extranet_endpoint

        def to_map(self):
            result = {}
            result['creation_date'] = self.creation_date
            result['extranet_endpoint'] = self.extranet_endpoint
            return result

    class Buckets(TeaModel):
        def __init__(self):
            self.bucket = []

        def to_map(self):
            result = {}
            result['bucket'] = self.bucket
            return result

    class ToBodyModel(TeaModel):
        def __init__(self, ListAllMyBucketsResult=None):
            self.listAllMyBucketsResult = ListAllMyBucketsResult

        def to_map(self):
            result = {}
            result['listAllMyBucketsResult'] = self.listAllMyBucketsResult
            return result

    def test_to_xml(self):
        self.assertIsNone(Client.to_xml(None))
        model = TestClient.ToBodyModel()
        result = TestClient.ListAllMyBucketsResult()
        buckets = TestClient.Buckets()
        bucket1 = TestClient.Bucket()
        bucket1.creation_date = "2015-12-17T18:12:43.000Z"
        bucket1.extranet_endpoint = "oss-cn-shanghai.aliyuncs.com"
        buckets.bucket.append(bucket1.to_map())
        bucket2 = TestClient.Bucket()
        bucket2.creation_date = "2014-12-25T11:21:04.000Z"
        bucket2.extranet_endpoint = "oss-cn-hangzhou.aliyuncs.com"
        buckets.bucket.append(bucket2.to_map())
        bucket_none = None
        buckets.bucket.append(bucket_none)
        result.buckets = buckets.to_map()
        owner = TestClient.Owner()
        owner.id = 512
        owner.display_name = "51264"
        result.owner = owner.to_map()
        result.test_str_list = ["1", "2"]
        result.owners.append(owner.to_map())
        result.test_num = 10
        result.test_bool = True
        result.test_null = None
        model.listAllMyBucketsResult = result.to_map()
        xml_str = Client.to_xml(model)
        self.assertIsNotNone(xml_str)
        re = Client.parse_xml(xml_str)
        self.assertIsNotNone(re)
        self.assertEqual(2, re["listAllMyBucketsResult"]['test_str_list'].__len__())
        self.assertEqual("10", re["listAllMyBucketsResult"]["test_num"])
        dic = {}
        self.assertEqual("", Client.to_xml(dic))
        try:
            dic = {
                'test': ['test1', 'test2']
            }
            Client.to_xml(dic)
        except Exception as e:
            self.assertEqual('Missing root tag', str(e))

    def test_parse_xml_attributes_text_and_repeated_children(self):
        body = '<root version="1"> message <item id="a"> first </item><item id="b"/><empty/></root>'
        self.assertEqual({
            'root': {
                '@version': '1',
                '#text': 'message',
                'item': [{'@id': 'a', '#text': 'first'}, {'@id': 'b'}],
                'empty': None,
            }
        }, Client.parse_xml(body))
        self.assertEqual({'root': {'item': 'value'}}, Client.parse_xml('<root> \n<item> value </item></root>'))
        self.assertEqual({'root': ''}, Client.parse_xml('<root> \n </root>'))

    def test_parse_xml_rejects_malformed_documents(self):
        for body in ('', '<root>', '<root></other>'):
            with self.assertRaises(ElementTree.ParseError):
                Client.parse_xml(body)

    def test_to_xml_dictionary_round_trip(self):
        body = {'root': {'item': ('first', 'second'), 'empty': [], 'escaped': '<&>', 'number': 0}}
        self.assertEqual({
            'root': {'item': ['first', 'second'], 'empty': None, 'escaped': '<&>', 'number': '0'}
        }, Client.parse_xml(Client.to_xml(body)))
        self.assertEqual('', Client.to_xml('unsupported'))

    def test_to_xml_requires_a_parent_for_root_sequences(self):
        with self.assertRaisesRegex(RuntimeError, 'Missing root tag'):
            Client.to_xml({'root': ['first', 'second']})

