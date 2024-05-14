#if canImport(FoundationXML)
import FoundationXML
public typealias XMLDocument = FoundationXML.XMLDocument
public typealias XMLElement = FoundationXML.XMLElement
#else
import Foundation
public typealias XMLDocument = Foundation.XMLDocument
public typealias XMLElement = Foundation.XMLElement
#endif
import SWXMLHash
import Tea

public class DarabonbaXML {
    public static func parseXml(_ body: String?, _ response: AnyObject?) -> [String: Any] {
        guard let body = body, !body.isEmpty else {
            return [:]
        }
        let xml = XMLHash.parse(body)
        return parseXMLElement(element: xml) ?? [:]
    }

    public static func parseXMLElement(element: XMLIndexer) -> [String: Any]? {
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
    
    public static func toXML(_ dictionary: [String: Any]?) -> String {
        guard let dictionary = dictionary, !dictionary.isEmpty else {
            return ""
        }
        
        guard dictionary.count == 1, let rootName = dictionary.keys.first else {
            return ""
        }
        
        let xmlDoc = XMLDocument()
        xmlDoc.version = "1.0"
        xmlDoc.characterEncoding = "UTF-8"
        let rootElement = XMLElement(name: rootName)
        xmlDoc.setRootElement(rootElement)
        
        if let rootDictionary = dictionary[rootName] as? [String: Any] {
            appendElements(from: rootDictionary, to: rootElement)
        }
        
        let xmlStringWithOptions = xmlDoc.xmlString(options: [.nodePrettyPrint])
        return xmlStringWithOptions
    }

    public static func appendElements(from dictionary: [String: Any], to element: XMLElement) {
        for (key, value) in dictionary {
            if let subDictionary = value as? [String: Any] {
                let subElement = XMLElement(name: key)
                appendElements(from: subDictionary, to: subElement)
                element.addChild(subElement)
            } else if let array = value as? [[String: Any]] {
                for subDictionary in array {
                    let subElement = XMLElement(name: key)
                    appendElements(from: subDictionary, to: subElement)
                    element.addChild(subElement)
                }
            } else {
                let subElement = XMLElement(name: key)
                let textNode = XMLNode.text(withStringValue: "\(value)") as! XMLNode
                subElement.addChild(textNode)
                element.addChild(subElement)
            }
        }
    }
}
