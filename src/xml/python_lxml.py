#!/usr/bin/python
# -*- coding: utf-8 -*-

from lxml import etree

tree = etree.parse('test.xml') # Парсинг файла

nodes = tree.xpath('/package/sms') # Открываем раздел
for node in nodes: # Перебираем элементы
    print node.tag,node.keys(),node.values()
    print 'id =',node.get('id') # Выводим параметр name
    print 'uniqid =',node.get('uniqid')
	print 'part =',node.get('part')
	print 'total =',node.get('total')
	print 'dst_num =',node.get('dst_num')
	print 'time_begin =',node.get('time_begin')
	print 'time_end =',node.get('time_end')
	print 'text =',[node.text] # Выводим текст элемента

# Рекурсивный перебор элементов
print 'recursiely:'
def getn(node):
    print node.tag,node.keys()
    for n in node:
        getn(n)
getn(tree.getroottree().getroot())
