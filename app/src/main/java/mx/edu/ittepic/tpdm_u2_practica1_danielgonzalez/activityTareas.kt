package mx.edu.ittepic.tpdm_u2_practica1_danielgonzalez

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_tareas.*
import java.util.*
import kotlin.coroutines.coroutineContext

class activityTareas : AppCompatActivity() {

    lateinit var basedatos_T: BaseDatos
    var todoId: Long = -1

    var lista_tareas: MutableList<Tareas>? = null
    var adaptador_tareas: ItemAdapter? = null
    var touchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tareas)
        setSupportActionBar(cabezera_Principal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = intent.getStringExtra(INTENT_TODO_NAME)
        todoId = intent.getLongExtra(INTENT_TODO_ID, -1)
        basedatos_T = BaseDatos(this)

        lista_RecyclerView.layoutManager = LinearLayoutManager(this)

        boton_AgregarTarea.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Agregar Una Tarea")
            val ver = layoutInflater.inflate(R.layout.alerta_crearlista, null)
            val tarea_descripcion = ver.findViewById<EditText>(R.id.agregar_lista_nueva)
            dialog.setView(ver)
            dialog.setPositiveButton("Agregar") { _: DialogInterface, _: Int ->
                if (tarea_descripcion.text.isNotEmpty()) {
                    val item = Tareas()
                    item.tarea_descripcion = tarea_descripcion.text.toString()
                    item.lista_Id = todoId
                    item.realizado = false
                    basedatos_T.agregar_Tarea(item)
                    refrescar_Recycler()
                }
            }
            dialog.setNeutralButton("Cancelar") { _: DialogInterface, _: Int ->

            }
            dialog.show()
        }

        touchHelper =
            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    p0: RecyclerView,
                    p1: RecyclerView.ViewHolder,
                    p2: RecyclerView.ViewHolder
                ): Boolean {
                    val sourcePosition = p1.adapterPosition
                    val targetPosition = p2.adapterPosition
                    Collections.swap(lista_tareas, sourcePosition, targetPosition)
                    adaptador_tareas?.notifyItemMoved(sourcePosition, targetPosition)
                    return true
                }

                override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })

        touchHelper?.attachToRecyclerView(lista_RecyclerView)

    }

    fun actualizarTarea(item: Tareas) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Actualizar Actividad")
        val ver = layoutInflater.inflate(R.layout.alerta_crearlista, null)
        val tarea_descripcion = ver.findViewById<EditText>(R.id.agregar_lista_nueva)
        tarea_descripcion.setText(item.tarea_descripcion)
        dialog.setView(ver)
        dialog.setPositiveButton("Actualizar") { _: DialogInterface, _: Int ->
            if (tarea_descripcion.text.isNotEmpty()) {
                item.tarea_descripcion = tarea_descripcion.text.toString()
                item.lista_Id = todoId
                item.realizado = false
                basedatos_T.actualizar_Tarea(item)
                refrescar_Recycler()
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
        lista_tareas = basedatos_T.traer_Tareas(todoId)
        adaptador_tareas = ItemAdapter(this, lista_tareas!!)
        lista_RecyclerView.adapter = adaptador_tareas
    }

    class ItemAdapter(val activity: activityTareas, val list: MutableList<Tareas>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(activity).inflate(
                    R.layout.mini_iconos,
                    p0,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
            holder.itemName.text = list[p1].tarea_descripcion
            holder.itemName.isChecked = list[p1].realizado
            holder.itemName.setOnClickListener {
                list[p1].realizado = !list[p1].realizado
                activity.basedatos_T.actualizar_Tarea(list[p1])
            }
            holder.delete.setOnClickListener {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle("CUIDADO!!")
                dialog.setMessage("Estas Seguro De Que Quieres Eliminar Esta Tarea ?")
                dialog.setPositiveButton("Si Eliminar") { _: DialogInterface, _: Int ->
                    activity.basedatos_T.borrar_Tarea(list[p1].id)
                    activity.refrescar_Recycler()
                }
                dialog.setNeutralButton("Cancelar") { _: DialogInterface, _: Int ->

                }
                dialog.show()
            }
            holder.edit.setOnClickListener {
                activity.actualizarTarea(list[p1])
            }

            holder.move.setOnTouchListener { v, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.touchHelper?.startDrag(holder)
                }
                false
            }
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val itemName: CheckBox = v.findViewById(R.id.cb_item)
            val edit: ImageView = v.findViewById(R.id.iv_edit)
            val delete: ImageView = v.findViewById(R.id.iv_delete)
            val move: ImageView = v.findViewById(R.id.iv_move)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else
            super.onOptionsItemSelected(item)
    }
}