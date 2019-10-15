package mx.edu.ittepic.tpdm_u2_practica1_danielgonzalez

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AlertDialog
import androidx.core.database.getStringOrNull

const val DB_NOMBRE = "Practica1"
const val DB_VERSION = 1
const val TABLE_LISTA = "ToDo"
const val LISTA_ID = "id"
const val LISTA_FECHA = "createdAt"
const val LISTA_DESCRIPCION = "name"


const val TABLE_TAREAS = "ToDoItem"
const val TAREAS_ID = "toDoId"
const val TAREAS_DESCRIPCION = "itemName"
const val TAREAS_REALIZADO = "isCompleted"


const val INTENT_TODO_ID = "TodoId"
const val INTENT_TODO_NAME = "TodoName"

//////////////////////////////////////////////////////////////////////////////////


//////////////////////////////////////////////////////////////////////////////////

class BaseDatos(val context: Context) : SQLiteOpenHelper(context, DB_NOMBRE, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val crear_TablaLista = "  CREATE TABLE $TABLE_LISTA (" +
                "$LISTA_ID integer PRIMARY KEY AUTOINCREMENT," +
                "$LISTA_FECHA DATE DEFAULT CURRENT_TIMESTAMP,"+
                "$LISTA_DESCRIPCION varchar (400));"
        val crear_TablaTareas =
            "CREATE TABLE $TABLE_TAREAS (" +
                    "$LISTA_ID integer ," +
                    "$TAREAS_ID integer PRIMARY KEY AUTOINCREMENT," +
                    "$TAREAS_DESCRIPCION varchar (400)," +
                    "$TAREAS_REALIZADO integer);"

        db.execSQL(crear_TablaLista)
        db.execSQL(crear_TablaTareas)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }


    fun agregar_Lista(toDo: Lista): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(LISTA_DESCRIPCION, toDo.descripcion)
        cv.put(LISTA_FECHA,toDo.fecha)
        val result = db.insert(TABLE_LISTA, null, cv)
        return result != (-1).toLong()
    }

    fun actualizar_Lista(toDo: Lista) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(LISTA_DESCRIPCION, toDo.descripcion)
        cv.put(LISTA_FECHA,toDo.fecha)
        db.update(
            TABLE_LISTA, cv, "$LISTA_ID=?", arrayOf(
                toDo.id
                    .toString()
            )
        )
    }

    fun borrar_Lista(todoId: Long) {
        val db = writableDatabase
        db.delete(TABLE_TAREAS, "$TAREAS_ID=?", arrayOf(todoId.toString()))
        db.delete(TABLE_LISTA, "$LISTA_ID=?", arrayOf(todoId.toString()))
    }

    fun updateToDoItemCompletedStatus(todoId: Long, isCompleted: Boolean) {
        val db = writableDatabase
        val queryResult =
            db.rawQuery("SELECT * FROM $TABLE_TAREAS WHERE $TAREAS_ID=$todoId", null)
        if (queryResult.moveToFirst()) {
            do {
                val item = Tareas()
                item.id = queryResult.getLong(queryResult.getColumnIndex(LISTA_ID))
                item.lista_Id = queryResult.getLong(queryResult.getColumnIndex(TAREAS_ID))
                item.tarea_descripcion =
                    queryResult.getString(queryResult.getColumnIndex(TAREAS_DESCRIPCION))
                item.realizado = isCompleted
                actualizar_Tarea(item)
            } while (queryResult.moveToNext())
            if (isCompleted == true) {
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("Felicidades")
                dialog.setMessage("Terminaste Todas Las Actividades")
                dialog.setPositiveButton("Ok") { DialogInterface, _: Int -> }.show()
            }
        }

        queryResult.close()
    }

    fun traer_Lista(): MutableList<Lista> {
        val result: MutableList<Lista> = ArrayList()
        val db = readableDatabase
        val queryResult = db.rawQuery("SELECT * from $TABLE_LISTA", null)
        if (queryResult.moveToFirst()) {
            do {
                val todo = Lista()
                todo.id = queryResult.getLong(queryResult.getColumnIndex(LISTA_ID))
                todo.descripcion = queryResult.getString(queryResult.getColumnIndex(LISTA_DESCRIPCION))
                todo.fecha = queryResult.getString(queryResult.getColumnIndex(LISTA_FECHA))
                result.add(todo)
            } while (queryResult.moveToNext())
        }
        queryResult.close()
        return result
    }

    fun agregar_Tarea(item: Tareas): Boolean {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(TAREAS_DESCRIPCION, item.tarea_descripcion)
        cv.put(TAREAS_ID, item.lista_Id)
        cv.put(TAREAS_REALIZADO, item.realizado)

        val result = db.insert(TABLE_TAREAS, null, cv)
        return result != (-1).toLong()
    }

    fun actualizar_Tarea(item: Tareas) {
        val db = writableDatabase
        val cv = ContentValues()
        cv.put(TAREAS_DESCRIPCION, item.tarea_descripcion)
        cv.put(TAREAS_ID, item.lista_Id)
        cv.put(TAREAS_REALIZADO, item.realizado)

        db.update(TABLE_TAREAS, cv, "$LISTA_ID=?", arrayOf(item.id.toString()))
    }

    fun borrar_Tarea(itemId: Long) {
        val db = writableDatabase
        db.delete(TABLE_TAREAS, "$LISTA_ID=?", arrayOf(itemId.toString()))
    }

    fun traer_Tareas(todoId: Long): MutableList<Tareas> {
        val result: MutableList<Tareas> = ArrayList()

        val db = readableDatabase
        val queryResult =
            db.rawQuery("SELECT * FROM $TABLE_TAREAS WHERE $TAREAS_ID=$todoId", null)

        if (queryResult.moveToFirst()) {
            do {
                val item = Tareas()
                item.id = queryResult.getLong(queryResult.getColumnIndex(LISTA_ID))
                item.lista_Id = queryResult.getLong(queryResult.getColumnIndex(TAREAS_ID))
                item.tarea_descripcion =
                    queryResult.getString(queryResult.getColumnIndex(TAREAS_DESCRIPCION))
                item.realizado =
                    queryResult.getInt(queryResult.getColumnIndex(TAREAS_REALIZADO)) == 1
                result.add(item)
            } while (queryResult.moveToNext())
        }

        queryResult.close()
        return result
    }

}