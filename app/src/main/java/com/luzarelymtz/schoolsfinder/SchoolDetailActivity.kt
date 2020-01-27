package com.luzarelymtz.schoolsfinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.luzarelymtz.schoolsfinder.model.School
import kotlinx.android.synthetic.main.activity_school_detail.*

class SchoolDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_detail)
        val extras = intent.extras
        //val schoolList = extras?.getParcelableArrayList<School>("schools")
        //val schoolList:ArrayList<School> =getIntent().getParcelableArrayListExtra("schools")
        val txtName=findViewById<TextView>(R.id.txtName)
        val txtAddress=findViewById<TextView>(R.id.txtAddress)
        val txtOpenNow=findViewById<TextView>(R.id.txtOpenNow)
        val txtRating=findViewById<TextView>(R.id.txtRating)

        if (extras != null) {


                //Log.i("BActivity", "Escuela: ${it.getName()} Dirección: ${it.getAddress()}" )
            txtName.text = "Escuela: "+extras.getString("name")
            txtAddress.text="Dirección: "+extras.getString("address")
            if(extras.getBoolean("openNow")){
                txtOpenNow.text="Esta abierto ahora: "+"Si"
            }
            else{
                txtOpenNow.text="Esta abierto ahora: "+"No"
            }

            txtRating.text="Valoración: "+extras.getString("rating")


        }else{
            Log.i("BActivity", "PersonList in null");
        }
    }
}
