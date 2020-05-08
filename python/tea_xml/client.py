import xml.etree.ElementTree as ET
from Tea.model import TeaModel
from Tea.exceptions import RequiredArgumentException
from collections import defaultdict

_LIST_TYPE = (list, tuple, set)


def __get_xml_factory(elem, val, parent_element=None):
    if val is None:
        return

    if isinstance(val, dict):
        __get_xml_by_dict(elem, val)
    elif type(val) in _LIST_TYPE:
        if parent_element is None:
            raise RequiredArgumentException("parent_element")
        __get_xml_by_list(elem, val, parent_element)
    else:
        elem.text = str(val)


def __get_xml_by_dict(elem, val):
    for k in val:
        sub_elem = ET.SubElement(elem, k)
        __get_xml_factory(sub_elem, val[k], elem)


def __get_xml_by_list(elem, val, parent_element):
    i = 0
    tag_name = elem.tag
    if val.__len__() > 0:
        __get_xml_factory(elem, val[0], parent_element)

    for item in val:
        if i > 0:
            sub_elem = ET.SubElement(parent_element, tag_name)
            __get_xml_factory(sub_elem, item, parent_element)
        i = i + 1


def parse_xml(t):
    d = {t.tag: {} if t.attrib else None}
    children = list(t)
    if children:
        dd = defaultdict(list)
        for dc in map(parse_xml, children):
            for k, v in dc.items():
                dd[k].append(v)
        d = {t.tag: {k: v[0] if len(v) == 1 else v for k, v in dd.items()}}

    if t.attrib:
        d[t.tag].update(('@' + k, v) for k, v in t.attrib.items())

    if t.text:
        text = t.text.strip()
        if children or t.attrib:
            if text:
                d[t.tag]['#text'] = text
        else:
            d[t.tag] = text
    return d


def to_xml(body):
    if body is None:
        return

    dic = {}
    if isinstance(body, TeaModel):
        dic = body.to_map()
    elif isinstance(body, dict):
        dic = body

    if dic.__len__() == 0:
        return ""
    else:
        result_xml = ""
        for k in dic:
            elem = ET.Element(k)
            __get_xml_factory(elem, dic[k])
            result_xml += bytes.decode(ET.tostring(elem), encoding="utf-8")
        return result_xml