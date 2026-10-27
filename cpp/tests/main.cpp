#include "gtest/gtest.h"
#include <boost/any.hpp>
#include <boost/property_tree/xml_parser.hpp>
#include <darabonba/xml.hpp>

using namespace std;

int main(int argc, char **argv) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}

TEST(tests_Client, test_parseXml)
{
  string xml = R"(<?xml version="1.0" encoding="utf-8"?><double>1.012300</double><int>10</int><long>9999999999</long><str>strval</str>)";
  map<string, boost::any> res = Darabonba_XML::Client::parseXml(make_shared<string>(xml), make_shared<string>());
  ASSERT_EQ("strval", boost::any_cast<string>(res["str"]));
  ASSERT_EQ(10, boost::any_cast<int>(res["int"]));
  ASSERT_EQ(9999999999, boost::any_cast<long>(res["long"]));
  ASSERT_EQ(double(1.0123), boost::any_cast<double>(res["double"]));

  string xml_str = R"(<?xml version="1.0" encoding="utf-8"?><json><xml><i>1</i><i>2</i><i>3</i><i>4</i></xml></json>)";
  vector<boost::any> v = boost::any_cast<vector<boost::any>>(
      boost::any_cast<map<string, boost::any>>(
          boost::any_cast<map<string, boost::any>>(
              Darabonba_XML::Client::parseXml(make_shared<string>(xml_str), make_shared<string>())["json"])["xml"]
      )["i"]);
  ASSERT_EQ(1, boost::any_cast<int>(v[0]));
  ASSERT_EQ(2, boost::any_cast<int>(v[1]));
  ASSERT_EQ(3, boost::any_cast<int>(v[2]));
  ASSERT_EQ(4, boost::any_cast<int>(v[3]));
}

TEST(tests_Client, test_toXML)
{
  map<string, boost::any> m({
                           {"str", string("strval")},
                           {"int", 10},
                           {"long", 9999999999},
                           {"double", double(1.0123)},
  });

  ASSERT_EQ("<?xml version=\"1.0\" encoding=\"utf-8\"?><double>1.012300</double><int>10</int><long>9999999999</long><str>strval</str>",
            Darabonba_XML::Client::toXML(make_shared<map<string, boost::any>>(m)));


  vector<boost::any> v({1, 2, 3, 4});
  map<string, boost::any> im = {
      {"i", v}
  };
  map<string, boost::any> xmlm = {
      {"xml", im}
  };
  map<string, boost::any> m1 = {
      {"json", xmlm}
  };
  string res = Darabonba_XML::Client::toXML(make_shared<map<string, boost::any>>(m1));
  ASSERT_EQ("<?xml version=\"1.0\" encoding=\"utf-8\"?><json><xml><i>1</i><i>2</i><i>3</i><i>4</i></xml></json>",
            res);
}

TEST(tests_Client, test_parseXmlScalarBoundaries)
{
  string xml = "<values><yes>true</yes><no>false</no><min>-2147483648</min>"
               "<large>2147483648</large><small>-2147483649</small>"
               "<decimal>-2.5</decimal><text>12x</text><empty/></values>";
  auto root = Darabonba_XML::Client::parseXml(make_shared<string>(xml), nullptr);
  auto values = boost::any_cast<map<string, boost::any>>(root.at("values"));

  EXPECT_TRUE(boost::any_cast<bool>(values.at("yes")));
  EXPECT_FALSE(boost::any_cast<bool>(values.at("no")));
  EXPECT_EQ(-2147483647 - 1, boost::any_cast<int>(values.at("min")));
  EXPECT_EQ(2147483648L, boost::any_cast<long>(values.at("large")));
  EXPECT_EQ(-2147483649L, boost::any_cast<long>(values.at("small")));
  EXPECT_DOUBLE_EQ(-2.5, boost::any_cast<double>(values.at("decimal")));
  EXPECT_EQ("12x", boost::any_cast<string>(values.at("text")));
  EXPECT_EQ("", boost::any_cast<string>(values.at("empty")));
}

TEST(tests_Client, test_parseXmlAttributesAndCdata)
{
  string xml = "<root id=\"42\"><text><![CDATA[<tag>&value]]></text></root>";
  auto root = Darabonba_XML::Client::parseXml(make_shared<string>(xml), nullptr);
  auto values = boost::any_cast<map<string, boost::any>>(root.at("root"));
  auto attributes = boost::any_cast<map<string, boost::any>>(values.at("<xmlattr>"));

  EXPECT_EQ(42, boost::any_cast<int>(attributes.at("id")));
  EXPECT_EQ("<tag>&value", boost::any_cast<string>(values.at("text")));
}

TEST(tests_Client, test_parseXmlRejectsMalformedAndMissingBodies)
{
  EXPECT_THROW(Darabonba_XML::Client::parseXml(make_shared<string>("<root>"), nullptr),
               boost::property_tree::xml_parser_error);
  EXPECT_THROW(Darabonba_XML::Client::parseXml(nullptr, nullptr), boost::bad_any_cast);
  EXPECT_THROW(Darabonba_XML::Client::parseXml(make_shared<string>(""), nullptr),
               boost::bad_any_cast);
}

TEST(tests_Client, test_toXmlBooleansAndEmptyContainers)
{
  map<string, boost::any> values = {
      {"emptyMap", map<string, boost::any>()},
      {"emptyVector", vector<boost::any>()},
      {"false", false},
      {"true", true},
      {"unsupported", float(1.5)},
  };

  EXPECT_EQ("<?xml version=\"1.0\" encoding=\"utf-8\"?><emptyMap></emptyMap>"
            "<false>false</false><true>true</true><unsupported></unsupported>",
            Darabonba_XML::Client::toXML(make_shared<map<string, boost::any>>(values)));
}

TEST(tests_Client, test_toXmlRepeatedObjects)
{
  vector<boost::any> items = {
      map<string, boost::any>{{"name", string("first")}, {"ready", true}},
      map<string, boost::any>{{"name", string("second")}, {"ready", false}},
  };
  map<string, boost::any> body = {{"item", items}};

  EXPECT_EQ("<?xml version=\"1.0\" encoding=\"utf-8\"?><item><name>first</name>"
            "<ready>true</ready></item><item><name>second</name><ready>false</ready></item>",
            Darabonba_XML::Client::toXML(make_shared<map<string, boost::any>>(body)));
}

TEST(tests_Client, test_toXmlMissingBodies)
{
  EXPECT_EQ("", Darabonba_XML::Client::toXML(nullptr));
  EXPECT_EQ("", Darabonba_XML::Client::toXML(make_shared<map<string, boost::any>>()));
}

TEST(tests_Client, test_clientLifecycle)
{
  Darabonba_XML::Client client;
  EXPECT_EQ("", client.toXML(nullptr));
}
