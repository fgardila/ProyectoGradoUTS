package com.fabianardila.proyectogradouts.vista.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.fabianardila.proyectogradouts.R
import com.fabianardila.proyectogradouts.modelo.Libro
import com.fabianardila.proyectogradouts.vista.adapter.LibrosAdapter
import com.fabianardila.proyectogradouts.vista.adapter.LibrosAdapterListener
import com.google.firebase.firestore.FirebaseFirestore
import com.fabianardila.proyectogradouts.network.Callback
import com.fabianardila.proyectogradouts.network.FirestoreService
import com.fabianardila.proyectogradouts.network.RealtimeDataListener
import kotlinx.android.synthetic.main.activity_listado_libros.*

class ListadoLibrosActivity : AppCompatActivity(), LibrosAdapterListener {

    private val TAG = "ListadoLibrosActivity"

    lateinit var firestoreService: FirestoreService

    private val librosAdapter: LibrosAdapter = LibrosAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_libros)

        initToolbar()
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        configureRecyclerView()

        val bundle = intent.extras
        if (bundle != null) {
            val filter = bundle.getString("filter")
            if (filter != null) {
                val dataFilter = bundle.getString("dataFilter")
                if (dataFilter != null && dataFilter != "1000")
                    cargarLibrosPorFiltros(filter, dataFilter)
                else
                    cargarLibros()
            } else {
                cargarLibros()
            }
        } else {
            cargarLibros()
        }
    }

    private fun initToolbar() {
        val toolbar =
            findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Listado de libros"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal_no_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            startActivity(Intent(this@ListadoLibrosActivity, BuscarUsuarioActivity::class.java))
            Toast.makeText(applicationContext, "Opcion no disponible", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = librosAdapter
    }

    private fun cargarLibros() {
        firestoreService.getLibros(object : Callback<List<Libro>> {
            override fun onSuccess(result: List<Libro>?) {
                Log.d(TAG, "Consulta de libros exitosa")
                addRealtimeDatabaseListener(result!!)
                this@ListadoLibrosActivity.runOnUiThread {
                    librosAdapter.librosList = result
                    librosAdapter.notifyDataSetChanged()
                    if (result.isEmpty()) {
                        imgNoData.visibility = View.VISIBLE
                        tvNoData.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailed(exception: Exception) {
                Log.d(TAG, "Error al encontrar libros")
                this@ListadoLibrosActivity.runOnUiThread {
                    imgNoData.visibility = View.VISIBLE
                    tvNoData.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun cargarLibrosPorFiltros(filter: String, dataFilter: String) {
        firestoreService.getLibrosByFilter(filter, dataFilter, object : Callback<List<Libro>> {
            override fun onSuccess(result: List<Libro>?) {
                Log.d(TAG, "Consulta de libros exitosa por categorias")
                addRealtimeDatabaseListener(result!!)
                this@ListadoLibrosActivity.runOnUiThread {
                    librosAdapter.librosList = result
                    librosAdapter.notifyDataSetChanged()
                    if (result.isEmpty()) {
                        imgNoData.visibility = View.VISIBLE
                        tvNoData.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailed(exception: Exception) {
                Log.d(TAG, "Error al encontrar libros por categorias")
                this@ListadoLibrosActivity.runOnUiThread {
                    imgNoData.visibility = View.VISIBLE
                    tvNoData.visibility = View.VISIBLE
                }
            }

        })
    }

    private fun addRealtimeDatabaseListener(libroList: List<Libro>) {
        firestoreService.listenForUpdates(libroList, object : RealtimeDataListener<Libro> {
            override fun onDataChange(updatedData: Libro) {
                var pos = 0
                for (libro in librosAdapter.librosList) {
                    if (libro.id.equals(updatedData.id)) {
                        //libro.available = updatedData.available
                        librosAdapter.notifyItemChanged(pos)
                    }
                    pos ++
                }
            }

            override fun onError(exception: Exception) {
                //TODO("Not yet implemented")
            }

        })
    }

    override fun onClickLibroListener(libro: Libro) {
        val intent = Intent(this@ListadoLibrosActivity, LibroActivity::class.java)
        intent.putExtra("Libro", libro)
        startActivity(intent)
    }
}