#include "gtest/gtest.h"
#include <boost/any.hpp>
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
