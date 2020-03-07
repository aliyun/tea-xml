<?php

namespace AlibabaCloud\Tea\XML;

class XML
{
    public static function parseXml($xmlStr, $response)
    {
        $res    = self::parse($xmlStr);
        $prop   = get_object_vars($response);
        $target = [];

        foreach ($res as $k => $v) {
            if (isset($prop[$k])) {
                $target[$k] = $v;
            }
        }

        return $target;
    }

    public static function toXML($array)
    {
        $tmp = $array;
        reset($tmp);
        $rootName   = key($tmp);
        $arrayToXml = new ArrayToXml();
        $data       = $array[$rootName];
        ksort($data);

        return $arrayToXml->buildXML($data, $rootName);
    }

    private static function parse($xml)
    {
        libxml_disable_entity_loader(true);

        return json_decode(
            json_encode(
                simplexml_load_string($xml, 'SimpleXMLElement', LIBXML_NOCDATA)
            ),
            true
        );
    }
}
