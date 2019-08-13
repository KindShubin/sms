#!/usr/bin/python
# -*- coding: utf-8 -*-
from io import StringIO, BytesIO
#import os
import lxml
from lxml import etree
#import StringIO

xml = '\
    <a xmlns="test"><b xmlns="test"/></a>' 
print xml
root = etree.fromstring(xml)
print 'etree.tostring(root):'
etree.tostring(root)

tree = etree.parse(BytesIO(xml))
print '000etree.tostring(tree.getroot()):'
etree.tostring(tree.getroot())

parser = etree.XMLParser(ns_clean=True)
print(len(parser.error_log))
tree1 = etree.parse(BytesIO(xml), parser)
print '111etree.tostring(tree1.getroot()):'
etree.tostring(tree1.getroot())

tree2 = etree.XML(u'\
    <a xmlns="test"><b xmlns="test"/></a>', parser)
print '222etree.tostring(tree2.getroot()):'
etree.tostring(tree2)
print(len(parser.error_log))
print(error.message)
print(error.line)
print(error.column)

tree3 = etree.XML("<root>\n</b>", parser)
print(len(parser.error_log))

error = parser.error_log[0]
print(error.message)
print(error.line)
print(error.column)

#some_data = BytesIO("<root>data</root>")
#tree = etree.parse(some_data)
#etree.tostring(tree)

#nodes = tree.xpath('/package/sms') # Открываем раздел
#for node in nodes: # Перебираем элементы
#    print node.tag,node.keys(),node.values()
#    print 'id =',node.get('id') # Выводим параметр name
#    print 'uniqid =',node.get('uniqid')
#    print 'part =',node.get('part')
#    print 'total =',node.get('total')
#    print 'dst_num =',node.get('dst_num')
#    print 'time_begin =',node.get('time_begin')
#    print 'time_end =',node.get('time_end')
#    print 'text =',[node.text] # Выводим текст элемента
#
## Рекурсивный перебор элементов
#print 'recursiely:'
#def getn(node):
#    print node.tag,node.keys()
#    for n in node:
#        getn(n)
#getn(tree.getroottree().getroot())
