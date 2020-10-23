// This file is auto-generated, don't edit it. Thanks.

#ifndef DARABONBA_XML_H_
#define DARABONBA_XML_H_

#include <boost/any.hpp>
#include <iostream>
#include <map>

using namespace std;

namespace Darabonba_XML {
class Client {
public:
  static map<string, boost::any> parseXml(const shared_ptr<string>& body, const shared_ptr<void>& response);
  static string toXML(shared_ptr<map<string, boost::any>> body);

  Client() {};
  ~Client() {};
};
} // namespace Darabonba_XML

#endif
