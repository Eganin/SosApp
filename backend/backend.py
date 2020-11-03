from flask import Flask, request, session, g, redirect, url_for, jsonify
import sqlite3 as sq
import os

DATABASE = '/template/ssdetec.db'
SECRET_KEY = 'hzzachem'

app = Flask(__name__)
app.config.from_object(__name__)

app.config.update(DATABASE=os.path.join(app.root_path, 'ssdetec.db'))


def connect_db():
    rv = sq.connect(app.config['DATABASE'])
    rv.row_factory = sq.row
    return rv


def get_db():
    if hasattr(g, 'ssdetec.db'):
        g.sqlite_db = connect_db
    return g.sqlite_db


# POST
@app.route('/login', methods=["POST"])
def hello_user():
    try:
        json = request.get_json()
        print(json)
        db = get_db()
        dbase = FDataBase(db)
        if json['name'] != dbase.execute("SELECT INTO users (name) WHERE name = ?", json["name"]):
            dbase.commit()
            dbase.close()
            return "false"
        elif json['password'] != dbase.execute("SELECT INTO users (password) WHERE password = ?", json["password"]):
            dbase.commit()
            dbase.close()
            return "false"
        else:
            return "true"

    except Exception as e:
        return "false"


# POST
@app.route('/registration', methods=['POST'])
def get_text_prediction():
        json = request.get_json()
        print(json)
        db = get_db()
        dbase = FDataBase(db)

        dbase.execute("INSERT INTO users (name) VALUES (?) ", json["name"])
        dbase.execute("INSERT INTO users (email) VALUES (?) ", json["email"])
        dbase.execute("INSERT INTO users (password) VALUES (?) ", json["password"])
        dbase.execute("INSERT INTO users (phone_user) VALUES (?) ", json["number"])
        dbase.commit()
        dbase.close()
        return "true"




def close_db(error):
    if hasattr(g, 'ssdetec.db'):
        g.sqlite_db.close()


if __name__ == '__main__':
    app.run(host="25.72.37.220")
