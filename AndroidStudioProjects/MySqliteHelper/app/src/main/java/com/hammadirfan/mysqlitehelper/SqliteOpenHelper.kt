package com.hammadirfan.mysqlitehelper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

class SqliteOpenHelper: SQLiteOpenHelper {
    var context:Context? = null
    var Create_Table = "CREATE  TABLE users (id INTEGER primary key AUTOINCREMENT , " +
                        " name TEXT , email TEXT , password TEXT , phno TEXT);"
    //lateinit var db:SQLiteDatabase
    var Drop_Table = "DROP TABLE IF EXISTS users"
    constructor(c: Context) : super(c,"mydb",null,1){
        context = c
    }


    companion object{
        var table:String="USERS"
        var ID:String = "id"
        var NAME:String = "name"
        var EMAIL:String = "email"
        var PASSWORD:String = "password"
        var PHNO:String = "phno"
    }

    var CREATE_TABLE:String = "CREATE TABLE " + table +
            "( " + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME +
            " TEXT, " + EMAIL + " TEXT, " + PASSWORD +
            " TEXT, " + PHNO + " TEXT); "\
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE)
    }
    //Will only be changed when the version of the database is changed. p0 is old version. p1 is new version of database
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

        p0?.execSQL(Drop_Table)
        onCreate(p0)
    }

    fun insert(name:String , email:String , password:String , phno:String) : Long{
        var db = writableDatabase
        var cv = ContentValues()
        cv.put("name", name)
        cv.put("email", email)
        cv.put("password", password)
        cv.put("phno", phno)
        var id = db.insert(table,null,cv)
        return id
        //var db = writableDatabase
        //var query = "INSERT INTO users (name , email , password , phno) VALUES ('$name' , '$email' , '$password' , '$phno')"
        //db.execSQL(query)
    }

    @SuppressLint("Range")
    fun ReadContacts():ArrayList<User>
    {
        var ls:ArrayList<User> = ArrayList()
        var db = readableDatabase
        var c= db.query(table,
            null,
            null,
            null,
            null,
            null,
            null)
        while(c.moveToNext()){
            var id = c.getInt(c.getColumnIndex(ID))
            var name = c.getString(c.getColumnIndex(NAME))
            var email = c.getString(c.getColumnIndex(EMAIL))
            var password = c.getString(c.getColumnIndex(PASSWORD))
            var phno = c.getString(c.getColumnIndex(PHNO))
            ls.add(User(id,name,email,password,phno))
            Toast.makeText(context,"$id $name $email $password",Toast.LENGTH_LONG).show()
        }
        var d = db.rawQuery("SELECT * FROM USERS;",null)
        return ls
        /*var query = "SELECT * FROM users"
        var result = db.rawQuery(query,null)
        if (result.moveToFirst())
        {
            do {
                var id = result.getString(result.getColumnIndex(ID))
                var name = result.getString(result.getColumnIndex(NAME))
                var email = result.getString(result.getColumnIndex(EMAIL))
                var password = result.getString(result.getColumnIndex(PASSWORD))
                var phno = result.getString(result.getColumnIndex(PHNO))
            }while (result.moveToNext())
        }
        result.close()
        db.close()*/
    }
}