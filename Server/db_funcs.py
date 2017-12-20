import pymysql.cursors
import sys
import math
import time
import json
from flask import jsonify
from math import cos, radians, degrees
from decimal import *
from handle_gen import getHandle

def getConnection():
    with open('cred.json') as file:
        data = json.load(file)
        return pymysql.connect(user=data['user'], password=data['password'], host=data['host'], db=data['db'])

def getMessages(lat, lng):
    connection = getConnection()
    minlat = Decimal(lat)-Decimal('0.04347826')
    maxlat = Decimal(lat)+Decimal('0.04347826')
    minlng = Decimal(lng)-Decimal(cos(radians(lat)))*Decimal('0.04347826')
    maxlng = Decimal(lng)+Decimal(cos(radians(lat)))*Decimal('0.04347826')

    with connection.cursor() as cursor:
        sql  = "SELECT messageID, message, lat, lng, rating, handle, date"
        sql += " FROM message"
        sql += " WHERE lat >= " + str(minlat)
        sql += " AND lat <= " + str(maxlat)
        sql += " AND lng >= " + str(minlng)
        sql += " AND lng <= " + str(maxlng) + ";"
        cursor.execute(sql)
        result = [dict((cursor.description[i][0], value) for i, value in enumerate(row)) for row in cursor.fetchall()]
    connection.close()
    return jsonify(result)

def insertMessage(message, lat, lng, deviceName):
    connection = getConnection()
    
    with connection.cursor() as cursor:
        
        sql1 = "SELECT handle FROM handle WHERE deviceName = %s;"
        cursor.execute(sql1, str(deviceName))
        handleResult = cursor.fetchone()
 
        if handleResult is None:
            handle = getHandle()            
            sql2 = "INSERT INTO `handle` (`handle`, `deviceName`) VALUES (%s, %s);"
            cursor.execute(sql2, (str(handle), str(deviceName)))
            connection.commit()
        else:
            handle = handleResult[0]
 
        sql = "INSERT INTO `message` (`message`, `lat`, `lng`, `handle`, `deviceName`) VALUES (%s, %s, %s, %s, %s);"
        cursor.execute(sql, (message, lat, lng, str(handle), str(deviceName)))
        connection.commit()

    connection.close()

def changeRating(ratingChanges):
    ratingJSON = json.loads(ratingChanges)
    connection = getConnection()
    with connection.cursor() as cursor:
        for entry in ratingJSON:
            tempMessageID = entry["messageID"]
            tempRating = entry["vote"]
            sql = "UPDATE message SET rating = rating + %s WHERE messageID = %s;"
            cursor.execute(sql, (tempRating, tempMessageID))
            connection.commit()
            
    connection.close()
