<?php

namespace AlibabaCloud\Tea\XML\Tests;

use AlibabaCloud\Tea\XML\ArrayToXml;
use PHPUnit\Framework\TestCase;

class ArrayToXmlTest extends TestCase
{
    public function testBuildXmlUsesConfiguredDocumentAndDefaultRoot()
    {
        $builder = new ArrayToXml('1.1', 'UTF-8');

        $this->assertEquals(
            "<?xml version=\"1.1\" encoding=\"UTF-8\"?>\n<data><value>text</value></data>",
            $builder->buildXML(['value' => 'text'])
        );
    }

    public function testBuildXmlRejectsNonArrayData()
    {
        $error = null;
        set_error_handler(function ($severity, $message) use (&$error) {
            $error = [$severity, $message];

            return true;
        });
        try {
            $this->assertFalse((new ArrayToXml())->buildXML('invalid'));
        } finally {
            restore_error_handler();
        }

        $this->assertEquals(E_USER_NOTICE, $error[0]);
        $this->assertTrue(strpos($error[1], 'Invalid variable type supplied, expected array') !== false);
    }

    public function testBuildXmlWritesAttributesAndText()
    {
        $this->assertXml(
            '<root id="a&amp;b">&lt;text&gt;</root>',
            ['@id' => 'a&b', '%' => '<text>']
        );
    }

    public function testBuildXmlWritesNestedAndRepeatedElements()
    {
        $this->assertXml(
            '<root><group kind="nested"><value>one</value></group><item code="a"><value>two</value></item><item code="b"><value>three</value></item><number>1</number><number>2</number><empty/></root>',
            [
                'group' => ['@kind' => 'nested', 'value' => 'one'],
                'item' => [
                    ['@code' => 'a', 'value' => 'two'],
                    ['@code' => 'b', 'value' => 'three'],
                ],
                'number' => [1, 2],
                'empty' => '',
            ]
        );
    }

    public function testBuildXmlWritesCdataInChildAndCurrentElement()
    {
        $this->assertXml(
            '<root><child><![CDATA[<tag>&text]]></child><![CDATA[<root>&text]]></root>',
            ['#child' => '<tag>&text', '!' => '<root>&text']
        );
    }

    public function testBuildXmlWritesArrayContentsForSpecialKeys()
    {
        foreach (['%', '#child', '!'] as $key) {
            $this->assertXml(
                '<root id="42"><value>nested</value></root>',
                ['@id' => '42', $key => ['value' => 'nested']]
            );
        }
    }

    public function testBuildXmlWritesEmptyArrays()
    {
        $this->assertXml('<root/>', []);
        $this->assertXml('<root/>', ['item' => []]);
    }

    private function assertXml($expected, array $data)
    {
        $this->assertXmlStringEqualsXmlString(
            $expected,
            (new ArrayToXml())->buildXML($data, 'root')
        );
    }
}
