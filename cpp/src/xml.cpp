#include <darabonba/xml.hpp>
#include <boost/any.hpp>
#include <boost/property_tree/xml_parser.hpp>
#include <boost/property_tree/ptree.hpp>
#include <regex>
#include <map>

using namespace std;

boost::any parse_xml(boost::property_tree::ptree pt) {
  if (pt.empty()) {
    if (pt.data() == "true" || pt.data() == "false") {
      return boost::any(pt.get_value<bool>());
    } else if (regex_search(pt.data(), regex("^-?\\d+$"))) {
      long ln = atol(pt.data().c_str());
      if (ln > 2147483647 || ln < -2147483648) {
        return boost::any(ln);
      } else {
        return boost::any(atoi(pt.data().c_str()));
      }
    } else if (regex_search(pt.data(), regex(R"(^-?\d+\.{1}\d+$)"))) {
      return boost::any(atof(pt.data().c_str()));
    }
    return boost::any(pt.data());
  }
  vector<boost::any> vec;
  map<string, boost::any> m;
  string key;
  for (const auto &it : pt) {
    boost::any val = parse_xml(it.second);
    vec.push_back(val);
    if (key == it.first) {
      m[it.first] = vec;
    } else {
      m[it.first] = val;
    }
    key = it.first;
  }
  return boost::any(m);
}

map<string, boost::any> Darabonba_XML::Client::parseXml(const shared_ptr<string>& body, const shared_ptr<void>& response) {
  string v = !body ? "" : *body;
  stringstream ss(v);
  using namespace boost::property_tree;
  ptree pt;
  read_xml(ss, pt);
  return boost::any_cast<map<string, boost::any>>(parse_xml(pt));
}

template<typename T> bool can_cast(const boost::any &v) {
  return typeid(T) == v.type();
}

string to_xml(boost::any val, string key) {
  string str;
  if (can_cast<map<string, boost::any>>(val)) {
    map<string, boost::any> m = boost::any_cast<map<string, boost::any>>(val);
    if (!m.empty()) {
      for (const auto &it : m) {
        if (typeid(vector<boost::any>) == it.second.type()) {
          str.append(to_xml(it.second, it.first));
        } else {
          str.append("<").append(it.first).append(">");
          str.append(to_xml(it.second, ""));
          str.append("</").append(it.first).append(">");
        }
      }
    }

  } else if (can_cast<vector<boost::any>>(val)) {
    vector<boost::any> v = boost::any_cast<vector<boost::any>>(val);
    if (!v.empty()) {
      for (const auto &it : v) {
        str.append("<").append(key).append(">");
        str.append(to_xml(it, ""));
        str.append("</").append(key).append(">");
      }
    }
  } else if (can_cast<int>(val)) {
    int i = boost::any_cast<int>(val);
    str.append(to_string(i));
  } else if (can_cast<long>(val)) {
    long l = boost::any_cast<long>(val);
    str.append(to_string(l));
  } else if (can_cast<double>(val)) {
    auto d = boost::any_cast<double>(val);
    str.append(to_string(d));
  } else if (can_cast<string>(val)) {
    auto s = boost::any_cast<string>(val);
    str.append(s);
  } else if (can_cast<bool>(val)) {
    auto b = boost::any_cast<bool>(val);
    string c = b ? "true" : "false";
    str.append(c);
  }
  return str;
}

string Darabonba_XML::Client::toXML(shared_ptr<map<string, boost::any>> body) {
  string s;
  if (!body || body->empty()) {
    return s;
  }

  s = R"(<?xml version="1.0" encoding="utf-8"?>)" + to_xml(boost::any(*body), "");
  return s;
}
