using System;
using System.Collections.Generic;
using System.Xml;
using System.Xml.Linq;
using AlibabaCloud.TeaXML;
using AlibabaCloud.TeaXML.Utils;
using Tea;
using Xunit;

namespace tests
{
    public class ClientEdgeCasesTest
    {
        public class TextResponse : TeaModel
        {
            [NameInMap("root")]
            public string Text { get; set; }
        }

        [Fact]
        public void ParsesTextResponseAndDecodesEntities()
        {
            var result = Client.ParseXml("<root>你好 &amp; &lt; &gt; &quot; &apos;</root>", typeof(TextResponse));
            Assert.Equal("你好 & < > \" '", result["root"]);
            Assert.Empty(Client.ParseXml("<unmatched>value</unmatched>", typeof(TextResponse)));
        }

        [Fact]
        public void PreservesThreeRepeatedScalarAndObjectElementsInOrder()
        {
            var parsed = Client.ParseXml("<root><item>first</item><item>second</item><item>third</item>"
                + "<object><name>one</name></object><object><name>two</name></object>"
                + "<object><name>three</name></object></root>", null);
            var root = Assert.IsType<Dictionary<string, object>>(parsed["root"]);
            Assert.Equal(new object[] { "first", "second", "third" }, Assert.IsType<List<object>>(root["item"]));
            var objects = Assert.IsType<List<object>>(root["object"]);
            Assert.Equal(3, objects.Count);
            Assert.Equal("one", Assert.IsType<Dictionary<string, object>>(objects[0])["name"]);
            Assert.Equal("three", Assert.IsType<Dictionary<string, object>>(objects[2])["name"]);
        }

        [Fact]
        public void EscapesTextAndOmitsNullDictionaryValues()
        {
            const string value = "你好 & < > \" ' &amp;";
            var body = new Dictionary<string, object> {
                { "root", new Dictionary<string, object> { { "value", value }, { "missing", null } } }
            };
            var xml = Client.ToXML(body);
            var root = XElement.Parse(xml);
            Assert.Equal(value, root.Element("value").Value);
            Assert.Null(root.Element("missing"));
            Assert.Contains("&amp;", xml);
            Assert.Contains("&lt;", xml);
        }

        [Fact]
        public void SerializesScalarAndEmptyRoots()
        {
            Assert.Equal("<root>42</root>", Client.ToXML(new Dictionary<string, object> { { "root", 42 } }));
            Assert.True(XElement.Parse(Client.ToXML(new Dictionary<string, object> { { "root", null } })).IsEmpty);
            Assert.Empty(Client.ToXML(new Dictionary<string, object>()));
        }

        [Fact]
        public void RejectsMalformedXmlAndUnsupportedRootLists()
        {
            Assert.Throws<XmlException>(() => Client.ParseXml("<root>", null));
            var body = new Dictionary<string, object> { { "root", new List<string> { "one", "two" } } };
            var error = Assert.Throws<ArgumentException>(() => Client.ToXML(body));
            Assert.Equal("unsupported nest list.", error.Message);
        }

        [Fact]
        public void SerializesEmptyModelsAndUnsupportedValuesAsEmptyStrings()
        {
            Assert.Empty(XmlUtil.SerializeXml(new TeaModel()));
            Assert.Empty(XmlUtil.SerializeXml(42));
        }
    }
}
