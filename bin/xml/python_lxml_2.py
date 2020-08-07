#!/usr/bin/python
# -*- coding: utf-8 -*-
from lxml import etree
#import xml.etree.ElementTree as ET
listfile = ['/home/dima/java/test/src/xml/test.xml',
'/home/dima/java/test/src/xml/test_utf8_win.xml',
'/home/dima/java/test/src/xml/test_utf8_unix.xml',
'/home/dima/java/test/src/xml/test_utf8_mac.xml',
'/home/dima/java/test/src/xml/test_utf8b_mac.xml',
'/home/dima/java/test/src/xml/test_utf8b_unix.xml',
'/home/dima/java/test/src/xml/test_utf8b_win.xml',
#'/home/dima/java/test/src/xml/test_w1251_unix.xml',
#'/home/dima/java/test/src/xml/test_w1251_mac.xml',
#'/home/dima/java/test/src/xml/test_w1251_win.xml',
#'/home/dima/java/test/src/xml/test_koi8r_unix.xml',
#'/home/dima/java/test/src/xml/test_koi8r_mac.xml',
#'/home/dima/java/test/src/xml/test_koi8r_win.xml',
#'/home/dima/java/test/src/xml/test_iso8859_unix.xml',
#'/home/dima/java/test/src/xml/test_iso8859_mac.xml',
#'/home/dima/java/test/src/xml/test_iso8859_win.xml',
#'/home/dima/java/test/src/xml/test_ansi_unix.xml',
#'/home/dima/java/test/src/xml/test_ansi_mac.xml',
#'/home/dima/java/test/src/xml/test_ansi_win.xml',]
]
print ('----------- BEGIN -------------')
#from xml.parsers import expat
#parser = etree.XMLParser(encoding='UTF-8')
#parser = etree.XMLParser(decode='UTF-8')
#parser=expat.ParserCreate('UTF-8')
for file in listfile:
#file='/home/dima/java/test/src/xml/test_utf8_win.xml'
    print (file)
#tree = etree.parse(file.encode('utf-8'))
    tree = etree.parse(file)
#t = ET
#tree = etree.parse( '/home/dima/java/test/src/xml/test.xml', parser=expat.ParserCreate('UTF-8') )
#tree = etree.parse(file, parser=parser)
    root = tree.getroot()
    root.tag
    root.attrib
#for child in root:
#    print ('child.tag: ',child.tag)
#    print ('child.attrib: ',child.attrib)
#    print ('child.text: ',child.text)
    nodes = tree.xpath('/package/sms') # Открываем раздел
    for node in nodes: # Перебираем элементы
#    print (node.tag,node.keys(),node.values())
        print ('id =',node.get('id')) # Выводим параметр name
        print ('uniqid =',node.get('uniqid'))
        print ('part =',node.get('part'))
        print ('total =',node.get('total'))
        print ('dst_num =',node.get('dst_num'))
        print ('time_begin =',node.get('time_begin'))
        print ('time_end =',node.get('time_end'))
        print ('text =',node.text.strip()) # Выводим текст элемента
        print ('qnt words = ',len(node.text.strip()))

## Рекурсивный перебор элементов
#print ('recursiely:')
#def getn(node):
#    print (node.tag,node.keys())
#    for n in node:
#        getn(n)
#igetn(tree.getroottree().getroot())
    print ('----------- END ----------')
