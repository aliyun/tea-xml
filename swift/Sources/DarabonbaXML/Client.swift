import Foundation

// 使用自定义的XML实现，避免平台兼容性问题
// 不再依赖Foundation或FoundationXML的XML类
import SWXMLHash
import Tea

public class Client {
    public static func parseXml(_ body: String?, _ response: AnyObject?) -> [String: Any] {
        guard let body = body, !body.isEmpty else {
            return [:]
        }
        let xml = XMLHash.parse(body)
        return parseXMLElement(element: xml) ?? [:]
    }

    private static func parseXMLElement(element: XMLIndexer) -> [String: Any]? {
        var resultDict: [String: Any] = [:]

        for child in element.children {
            if child.children.isEmpty {
                resultDict[child.element!.name] = child.element!.text
            } else {
                if let dict = parseXMLElement(element: child) {
                    resultDict[child.element!.name] = dict
                }
            }
        }
        return resultDict.isEmpty ? nil : resultDict
    }
    
    public static func toXML(_ body: [String: Any]?) -> String {
        guard let body = body, !body.isEmpty else {
            return ""
        }
        
        guard body.count == 1, let rootName = body.keys.first else {
            return ""
        }
        
        // 使用自定义的XML生成方法，避免平台兼容性问题
        if let root = body[rootName] as? [String: Any] {
            return generateXMLString(rootName: rootName, dictionary: root)
        }
        return "<\(rootName)></\(rootName)>"
    }

    private static func generateXMLString(rootName: String, dictionary: [String: Any]) -> String {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        xml += "<\(rootName)>\n"
        xml += generateXMLContent(from: dictionary, indent: "  ")
        xml += "</\(rootName)>"
        return xml
    }
    
    private static func generateXMLContent(from dictionary: [String: Any], indent: String) -> String {
        var content = ""
        for (key, value) in dictionary {
            if let subDictionary = value as? [String: Any] {
                content += "\(indent)<\(key)>\n"
                content += generateXMLContent(from: subDictionary, indent: indent + "  ")
                content += "\(indent)</\(key)>\n"
            } else if let array = value as? [[String: Any]] {
                for subDictionary in array {
                    content += "\(indent)<\(key)>\n"
                    content += generateXMLContent(from: subDictionary, indent: indent + "  ")
                    content += "\(indent)</\(key)>\n"
                }
            } else {
                let escapedValue = escapeXMLString("\(value)")
                content += "\(indent)<\(key)>\(escapedValue)</\(key)>\n"
            }
        }
        return content
    }
    
    private static func escapeXMLString(_ string: String) -> String {
        return string
            .replacingOccurrences(of: "&", with: "&amp;")
            .replacingOccurrences(of: "<", with: "&lt;")
            .replacingOccurrences(of: ">", with: "&gt;")
            .replacingOccurrences(of: "\"", with: "&quot;")
            .replacingOccurrences(of: "'", with: "&apos;")
    }
}
