import XCTest
@testable import DarabonbaXML

final class DarabonbaXMLTests: XCTestCase {
    var xmlStr: String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<tests>\n" +
    "  <name>test</name>\n" +
    "  <value>1</value>\n" +
    "</tests>\n"
    
    func testParseXml() {
        let r = DarabonbaXML.parseXml(xmlStr, TestObject())
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
        var result = DarabonbaXML.toXML(dict)
        #if os(Linux)
        XCTAssertTrue(result.contains("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>"))
        #else
        XCTAssertTrue(result.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"))
        #endif
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
        let result1 = DarabonbaXML.toXML(dict1)
        XCTAssertEqual("", result1)
    }
    
}

class TestObject {
    var name: String = ""
    var value: Int = 0
}
