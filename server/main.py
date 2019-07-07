#!/usr/bin/env python3
import os.path
from flask import Flask, request, Response, send_from_directory, render_template, jsonify
from datetime import datetime
import mysql.connector

app = Flask(__name__, static_url_path='')

#@app.route("/")
#def main_page():
#    return "<h1>Hello!</h1>"

@app.route('/rest/v1/input', methods=['GET', 'POST'])
def log():
    tracking_db = mysql.connector.connect(
    host = 'localhost',
    user = 'tracking_master',
    passwd = 'kdjI97mC',
    database = 'tracking_db',
    auth_plugin='mysql_native_password')

    mycursor = tracking_db.cursor()

    tmp = request.get_json()
    dt = datetime.now()

    sql = 'INSERT INTO tracking (datetime_col, vehicleid, latitude, longitude) VALUES (%s, %s, %s, %s)'
    val = (dt, tmp['vehicleid'], tmp['latitude'], tmp['longitude'])
    try:
        mycursor.execute(sql, val)
        tracking_db.commit()
    except:
        print("Exception occured")
    return 'OK'

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/rest/v1/get')
def getData():
    tracking_db = mysql.connector.connect(
    host = 'localhost',
    user = 'tracking_master',
    passwd = 'kdjI97mC',
    database = 'tracking_db',
    auth_plugin='mysql_native_password')

    mycursor = tracking_db.cursor()

    sql = 'SELECT DISTINCT vehicleid FROM tracking'

    result = mycursor.execute(sql)
    myresult = mycursor.fetchall()


    out_data = {}
    x = 1
    for v_id in myresult:
        sql = 'select vehicleid, latitude, longitude, datetime_col from tracking where vehicleid = %s order by id desc limit 2'
        result = mycursor.execute(sql, v_id)
        tmp_result = mycursor.fetchall()
        result = [d for d in tmp_result]
        out_data.setdefault(x, result)
        x += 1
    return jsonify(out_data)


if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080)