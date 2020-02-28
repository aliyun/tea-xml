import XCTest
@testable import TeaXml

final class TeaXmlTests: XCTestCase {
    var xmlStr: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<tests>\n" +
            "  <name>test</name>\n" +
            "  <value>1</value>\n" +
            "</tests>\n"

    func testParseXml() {
        let r = TeaXml.parseXml(xmlStr, TestObject())
        let name: String = r["name"] as! String
        let value: String = r["value"] as! String
        XCTAssertEqual("test", name)
        XCTAssertEqual("1", value)
    }

    func testToXML() {
        let dict: [String: Any] = [
            "tests": [
                "name": "test",
                "value": "1"
            ]
        ]
        let result = TeaXml.toXML(dict)
        XCTAssertEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tests>\n\t<value>1</value>\n\t<name>test</name>\n</tests>\n", result)
    }

    static var allTests = [
        ("testParseXml", testParseXml),
        ("testToXML", testToXML)
    ]
}

class TestObject {
    var name: String = ""
    var value: Int = 0
}
