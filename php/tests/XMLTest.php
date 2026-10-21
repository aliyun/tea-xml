<?php

namespace AlibabaCloud\Tea\XML\Tests;

use AlibabaCloud\Tea\XML\XML;
use PHPUnit\Framework\TestCase;

/**
 * @internal
 */
class XMLTest extends TestCase
{
    private $xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" .
    "<tests>\n" .
    "  <name>test</name>\n" .
    "  <value>1</value>\n" .
    "</tests>\n";

    public function testParseXml()
    {
        $res   = XML::parseXml($this->xmlStr, new tests());
        $name  = $res['name'];
        $value = $res['value'];
        $this->assertEquals('test', $name);
        $this->assertEquals(1, $value);

        $res   = XML::parseXml($this->xmlStr, null);
        $name  = $res['name'];
        $value = $res['value'];
        $this->assertEquals('test', $name);
        $this->assertEquals(1, $value);
    }

    public function testArrayToXML()
    {
        $data = [
            'tests' => [
                'name'  => 'test',
                'value' => 1,
            ],
        ];
        $this->assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tests><name>test</name><value>1</value></tests>", XML::toXML($data));
    }

    public function testObjectToXML()
    {
        $obj        = new tests();
        $obj->name  = 'test';
        $obj->value = 1;
        $this->assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tests><name>test</name><value>1</value></tests>", XML::toXML($obj));
    }

    public function testParseXmlCreatesResponseFromClassNameAndFiltersProperties()
    {
        $xml = '<tests><name>test</name><value>2</value><unknown>ignored</unknown></tests>';

        $this->assertEquals(
            ['name' => 'test', 'value' => '2'],
            XML::parseXml($xml, __NAMESPACE__ . '\\tests')
        );
    }

    public function testParseXmlPreservesAttributesRepeatedElementsAndCdata()
    {
        $xml = '<tests id="42"><item>one</item><item>two</item><text><![CDATA[<hello>&world]]></text><empty/></tests>';

        $this->assertEquals(
            [
                '@attributes' => ['id' => '42'],
                'item' => ['one', 'two'],
                'text' => '<hello>&world',
                'empty' => [],
            ],
            XML::parseXml($xml, null)
        );
    }

    public function testToXmlSortsKeysAndEscapesText()
    {
        $this->assertEquals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tests><a>&lt;tag&gt;&amp;value</a><z>last</z></tests>",
            XML::toXML(['tests' => ['z' => 'last', 'a' => '<tag>&value']])
        );
    }
}

class tests
{
    public $name  = '';
    public $value = 0;
}
