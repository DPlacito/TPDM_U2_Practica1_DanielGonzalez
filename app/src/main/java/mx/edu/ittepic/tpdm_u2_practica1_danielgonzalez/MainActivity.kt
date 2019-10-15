package mx.edu.ittepic.tpdm_u2_practica1_danielgonzalez

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var basedatos: BaseDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(cabezera_Principal)
        title = "Tus Listas"
        basedatos = BaseDatos(this)
        lista_RecyclerView.layoutManager = LinearLayoutManager(this)

        boton_AgregarLista.setOnClickListener {
            var alerta = AlertDialog.Builder(this)
            alerta.setTitle("Crear Lista")
            var ver = layoutInflater.inflate(R.layout.alerta_crearlista, null)
            val campo_DescripcionLista = ver.findViewById<EditText>(R.id.agregar_lista_nueva)
            alerta.setView(ver)
            alerta.setPositiveButton("Crear") { _: DialogInterface, _: Int ->
                if (campo_DescripcionLista.text.isNotEmpty()) {
                    val lista = Lista()
                    lista.descripcion = campo_DescripcionLista.text.toString()
                    basedatos.agregar_Lista(lista)
                    refrescar_Recycler()
                } else {
                    Toast.makeText(this, "No Escribiste Nada", Toast.LENGTH_LONG).show()
                }
            }
            alerta.setNeutralButton("Cancelar") { _: DialogInterface, _: Int ->
            }
            alerta.show()
        }
    }

    fun actualizar_Lista(lista: Lista) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Cambiar Nombre De La Lista")
        val ver = layoutInflater.inflate(R.layout.alerta_crearlista, null)
        val nombre_Lista = ver.findViewById<EditText>(R.id.agregar_lista_nueva)
        nombre_Lista.setText(lista.descripcion)
        dialog.setView(ver)
        dialog.setPositiveButton("Actualizar") { _: DialogInterface, _: Int ->
            if (nombre_Lista.text.isNotEmpty()) {
                lista.descripcion = nombre_Lista.text.toString()
                lista.fecha
                basedatos.actualizar_Lista(lista)
                refrescar_Recycler()
            } else {
                Toast.makeText(this, "No Escribiste Nada", Toast.LENGTH_LONG).show()
            }
        }
        dialog.setNeutralButton("Cancelar") { _: DialogInterface, _: Int ->

        }
        dialog.show()
    }

    override fun onResume() {
        refrescar_Recycler()
        super.onResume()
    }

    private fun refrescar_Recycler() {
        lista_RecyclerView.adapter = Adaptador(this, basedatos.traer_Lista())
    }


    class Adaptador(val activity: MainActivity, val list: MutableList<Lista>) :
        RecyclerView.Adapter<Adaptador.ver_EnLista>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ver_EnLista {
            return ver_EnLista(
                LayoutInflater.from(activity).inflate(
                    R.layout.menuchico, p0, false

                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ver_EnLista, p1: Int) {
            holder.descripcion.text = list[p1].descripcion
            var cadena = "Fecha De Creacion:  "
            holder.fecha.text = cadena + list[p1].fecha
            holder.descripcion.setOnClickListener {
                val intent = Intent(activity, activityTareas::class.java)
                intent.putExtra(INTENT_TODO_ID, list[p1].id)
                intent.putExtra(INTENT_TODO_NAME, list[p1].descripcion)

                activity.startActivity(intent)
            }

            holder.menu.setOnClickListener {
                val menu_Chico = PopupMenu(activity, holder.menu)
                menu_Chico.inflate(R.menu.opciones)
                menu_Chico.setOnMenuItemClickListener {

                    when (it.itemId) {
                        R.id.menu_editar -> {
                            activity.actualizar_Lista(list[p1])
                        }
                        R.id.menu_borrar -> {
                            val dialog = AlertDialog.Builder(activity)
                            dialog.setTitle("CUIDADO!!")
                            dialog.setMessage("Seguro Que Desear Eliminarlo ?")
                            dialog.setPositiveButton("Si Eliminar") { _: DialogInterface, _: Int ->
                                activity.basedatos.borrar_Lista(list[p1].id)
                                activity.refrescar_Recycler()
                            }
                            dialog.setNeutralButton("Cancelar") { _: DialogInterface, _: Int ->

                            }
                            dialog.show()
                        }
                        R.id.menu_marcarCompleto -> {
                            activity.basedatos.updateToDoItemCompletedStatus(list[p1].id, true)
                        }
                        R.id.menu_resetear -> {
                            activity.basedatos.updateToDoItemCompletedStatus(list[p1].id, false)
                        }
                    }

                    true
                }
                menu_Chico.show()
            }
        }

        class ver_EnLista(v: View) : RecyclerView.ViewHolder(v) {
            val descripcion: TextView = v.findViewById(R.id.tv_todo_name)
            val fecha: TextView = v.findViewById(R.id.item_date)
            val menu: ImageView = v.findViewById(R.id.iv_menu)
        }
    }
}


