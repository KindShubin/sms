#!/usr/bin/python
# -*- coding: utf-8 -*-
#import MySQLdb
import pymysql
#import mysql.connector
from lxml import etree
client_id = 10

print ('----------- OPEN DB CONNECTION  -------------')
db_smssystem = pymysql.connect(host="10.22.0.1", user="root", passwd="FEwuV32u6una", db="smssystem", charset='utf8')
#db_smssystem = mysql.connector.connect(host="localhost", user="root", passwd="FEwuV32u6una", db="smssystem")
#db_smssystem = MySQLdb.connect(host="localhost", user="root", passwd="FEwuV32u6una", db="smssystem")
cur = db_smssystem.cursor()

print ('----------- GET MIN ID IN SMSLOGS  -------------')
cur_min_max_id = db_smssystem.cursor()
cur_min_max_id.execute("""select min(id), max(id) from smslogs where date(time_entry)=date(now())""")
result_min_max_id=cur_min_max_id.fetchall()
min_id = 0
max_id = 0
for row in result_min_max_id:
    min_id=row[0]
    max_id=row[1]
cur_min_max_id.close()
print ('--- MIN_ID:%d MAX_ID:%d ---' % (min_id, max_id))

print ('----------- FIND XML -------------')
listfile = [
    '/home/dima/java/test/src/xml/test.xml'
    #'/home/dima/java/test/src/xml/test_utf8_win.xml',
]
print ('files: ', listfile)
print ('----------- BEGIN PARSE XML -------------')
for file in listfile:
    print ('file: ', file)
    tree = etree.parse(file)
    root = tree.getroot()
    #print('root.tag:%s root.attrib:%s', (root.tag, root.attrib))
    nodes = tree.xpath('/package/sms') # Открываем раздел
    for node in nodes: # Перебираем элементы
        print ('----------- RECEIVED SMS  ----------')
        id_sms_client = int(node.get('id'))
        uniqid_client = int(node.get('uniqid'))
        part = int(node.get('part'))
        total = int(node.get('total'))
        dst_num = int(node.get('dst_num'))
        time_begin = str(node.get('time_begin'))
        text = str(node.text.strip())
        qnt_words_int = len(node.text.strip())
        qnt_words = str(qnt_words_int)
        # Выводим параметр name
        print ('id = %s,\nuniqid = %s,\npart = %s,\ntotal = %s,\ndst_num = %s,\ntime_begin = %s,\ntext = %s,\nqnt words = %s' % 
        (id_sms_client, uniqid_client, part, total, dst_num, time_begin, text, qnt_words))
        print ('----------- BEGIN INSERT TO DB  -------------')
        if total > 1:
            uniqid = uniqid_client+min_id
            print ('--- CLIENT_ID=%d UNIQID_CLIENT=%d UNIQID=%d---' % (id_sms_client, uniqid_client, uniqid))   
            query = """ INSERT INTO smssystem.smslogs (uniqid, total, part, client_id, text, dst_num, status, time_begin, userfield, id_sms_client, uniqid_sms_client) VALUES ('%d', '%d', '%d', '%d', '%s', '%d', '%s', '%s', '%s', '%d', '%d') """
            data = (uniqid, total, part, client_id, text, dst_num, 'recieved', time_begin, qnt_words, id_sms_client, uniqid_client)
            print (query % data)
            cur.execute(query % data)
        else:
            query = """ INSERT INTO smssystem.smslogs (total, part, client_id, text, dst_num, status, time_begin, userfield, id_sms_client, uniqid_sms_client) VALUES ('%d', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') """
            data = (total, part, client_id, text, dst_num, 'recieved', time_begin, qnt_words, id_sms_client, uniqid_client)
            print (query % data)
            cur.execute(query % data)
            print ('----------- END INSERT SMS  ----------')
        #конец обработки одного node   
db_smssystem.commit()
cur.close()
db_smssystem.close()
