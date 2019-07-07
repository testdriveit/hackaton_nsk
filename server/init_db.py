#!/usr/bin/python3

import mysql.connector


beacons_db = mysql.connector.connect(
    host = 'localhost',
    user = 'tracking_master',
    passwd = 'kdjI97mC',
    database = 'tracking_db',
    auth_plugin='mysql_native_password')

mycursor = beacons_db.cursor()

sql = "DROP TABLE IF EXISTS tracking"

mycursor.execute(sql)

sql = "CREATE TABLE tracking (id INT AUTO_INCREMENT PRIMARY KEY, datetime_col DATETIME, vehicleid INT, latitude VARCHAR(40), longitude VARCHAR(40))"

mycursor.execute(sql)

print("Table 'tracking' initialized")
