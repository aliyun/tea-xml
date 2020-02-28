import Foundation
import SwiftyXML

public class TeaXml {
    public static func parseXml(_ body: String, _ response: AnyObject) -> [String: Any] {
        var result: [String: Any] = [String: Any]()
        let xml = XML(string: body, encoding: .utf8)
        let mirror = Mirror(reflecting: response)
        var property: [String] = [String]()
        for item in mirror.children {
            property.append(item.label!)
        }
        for item in xml.children {
            if property.contains(item.name) {
                result[item.name] = item.value
            }
        }
        return result
    }

    public static func toXML(_ dict: [String: Any]) -> String {
        if dict.keys.count != 1 {
            print("This map does not have a unique root tag");
            return ""
        }
        let rootName: String = dict.keys.first!;
        let xml = XML(name: rootName)
        let items: [String: Any] = dict[rootName] as! [String: Any]
        for item in items {
            let node = XML(name: item.key)
            node.value = (item.value as! String)
            xml.addChild(node)
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xml.toXMLString()
    }
}