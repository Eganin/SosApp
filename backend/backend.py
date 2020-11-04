from flask import Flask, request, session, g, redirect, url_for, jsonify
import sqlite3 as sq
import os

DATABASE = 'C:\\Users\\Eganin\\AndroidStudioProjects\\TestProjectSchool\\SosApplication\\backend\\template\\ssdetec.db'
SECRET_KEY = 'hzzachem'

app = Flask(__name__)
app.config.from_object(__name__)

app.config.update(DATABASE=DATABASE)


def connect_db():
    rv = sq.connect(DATABASE)
    return rv


@app.before_request
def before_request():
    g.sqlite_db = connect_db()


def get_db():
    if hasattr(g, 'sqlite_db'):
        g.sqlite_db = connect_db()
    return g.sqlite_db


# POST
@app.route('/login', methods=["POST"])
def hello_user():
    try:

        conn = sq.connect(DATABASE)
        cur = conn.cursor()
        with conn:
            json = request.get_json()
            if json["name"] != cur.execute("SELECT * FROM users WHERE name = ?", (json["name"],)).fetchall()[0][1] or \
                    json["password"] != cur.execute("SELECT * FROM users WHERE password = ?", (json["password"],)).fetchall()[0][2]:

                return jsonify({"server_answer": "false"})
            else:
                return jsonify({"server_answer": "true"})
    except Exception as e:
        print(e)
        return jsonify({"server_answer": "false"})


# POST
@app.route('/registration', methods=['POST'])
def get_text_prediction():
    try:
        conn = sq.connect(DATABASE)
        cur = conn.cursor()
        with conn:
            json = request.get_json()
            cur.execute("INSERT INTO users (name,password,phone_user,email) VALUES (?,?,?,?) ",
                        (json["name"], json["password"], json["number"], json["email"]))
            conn.commit()
            return jsonify({"server_answer": "true"})

    except Exception as e:
        print(e)
        return jsonify({"server_answer": "false"})


def close_db(error):
    if hasattr(g, 'ssdetec.db'):
        g.sqlite_db.close()


if __name__ == '__main__':
    app.run(host="25.72.37.220")
