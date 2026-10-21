import XCTest
@testable import DarabonbaXML

final class DarabonbaXMLTests: XCTestCase {
    var xmlStr: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<tests>\n" +
    "  <name>test</name>\n" +
    "  <value>1</value>\n" +
    "</tests>\n"
    
    func testParseXml() {
        let r = Client.parseXml(xmlStr, TestObject())
        let res = r["tests"] as! [String: Any]
        let name = res["name"] as! String
        let value = res["value"] as! String
        XCTAssertEqual("test", name)
        XCTAssertEqual("1", value)
    }
    
    func testToXML() {
        let dict: [String: Any] = [
            "tests": [
                "name": "test",
                "value": "1",
                "map": [
                    "name": "test",
                    "value": "00"
                ]
            ]
        ]
        let result = Client.toXML(dict)
        XCTAssertTrue(result.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
        XCTAssertTrue(result.contains("<tests>"))
        XCTAssertTrue(result.contains("<map>"))
        XCTAssertTrue(result.contains("<value>00</value>"))
        XCTAssertTrue(result.contains("<name>test</name>"))
        XCTAssertTrue(result.contains("<value>1</value>"))
        XCTAssertTrue(result.contains("<name>test</name>"))
        XCTAssertTrue(result.contains("</tests>"))
        
        let dict1: [String: Any] = [
            "tests": [
                "name": "test",
                "value": "1",
            ],
            "map": [
                "name": "test",
                "value": "00"
            ]
        ]
        let result1 = Client.toXML(dict1)
        XCTAssertEqual("", result1)
    }

    func testParseXmlWithMissingBody() {
        XCTAssertTrue(Client.parseXml(nil, nil).isEmpty)
        XCTAssertTrue(Client.parseXml("", nil).isEmpty)
    }

    func testParseXmlWithoutElements() {
        for body in [" \n\t", "not XML", "<!-- comment -->"] {
            XCTAssertTrue(Client.parseXml(body, nil).isEmpty, body)
        }
    }

    func testParseXmlWithEmptyElements() throws {
        let result = Client.parseXml("<root><empty/><nested><leaf/></nested></root>", nil)
        let root = try XCTUnwrap(result["root"] as? [String: Any])
        XCTAssertEqual(root["empty"] as? String, "")
        let nested = try XCTUnwrap(root["nested"] as? [String: String])
        XCTAssertEqual(nested, ["leaf": ""])
    }

    func testParseXmlDecodesEntitiesAndUnicode() {
        let result = Client.parseXml("<root>你好 &amp; &lt; &gt; &quot; &apos;</root>", nil)
        XCTAssertEqual(result["root"] as? String, "你好 & < > \" '")
    }

    func testToXMLWithMissingBody() {
        XCTAssertEqual(Client.toXML(nil), "")
        XCTAssertEqual(Client.toXML([:]), "")
    }

    func testToXMLWithEmptyOrScalarRoot() {
        XCTAssertEqual(Client.toXML(["root": [String: Any]()]),
                       "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n</root>")
        XCTAssertEqual(Client.toXML(["root": "value"]), "<root></root>")
    }

    func testToXMLWithDictionaryArray() {
        let items: [[String: Any]] = [
            ["name": "first"],
            ["nested": ["name": "second"]]
        ]
        let result = Client.toXML(["root": ["item": items]])
        XCTAssertEqual(result, """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
              <item>
                <name>first</name>
              </item>
              <item>
                <nested>
                  <name>second</name>
                </nested>
              </item>
            </root>
            """)
    }

    func testToXMLWithEmptyCollections() {
        let result = Client.toXML(["root": [
            "empty": [String: Any](),
            "item": [[String: Any]]()
        ]])
        XCTAssertEqual(result, """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
              <empty>
              </empty>
            </root>
            """)
    }

    func testToXMLEscapesSpecialCharacters() throws {
        let value = "你好 & < > \" ' &amp;"
        let result = Client.toXML(["root": ["value": value]])
        XCTAssertEqual(result, """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
              <value>你好 &amp; &lt; &gt; &quot; &apos; &amp;amp;</value>
            </root>
            """)
        let root = try XCTUnwrap(Client.parseXml(result, nil)["root"] as? [String: String])
        XCTAssertEqual(root, ["value": value])
    }

    func testToXMLWithNumericAndBooleanValues() throws {
        let result = Client.toXML(["root": ["count": 42, "enabled": true]])
        let root = try XCTUnwrap(Client.parseXml(result, nil)["root"] as? [String: String])
        XCTAssertEqual(root, ["count": "42", "enabled": "true"])
    }
}

class TestObject {
    var name: String = ""
    var value: Int = 0
}
