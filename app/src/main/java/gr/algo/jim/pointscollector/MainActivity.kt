package gr.algo.jim.pointscollector

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.content.Intent
import android.os.Message
import  android.text.SpannableStringBuilder
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.pointsinput.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private var listProducts = ArrayList<Product>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val queue = Volley.newRequestQueue(this)

        button.setOnClickListener{
            //val textView =  findViewById<TextView>(R.id.textView)
            val editView = findViewById<EditText>(R.id.editText3) as EditText

            val url = "http://192.168.0.10:8080/customer?number="+editView.text.toString()


            val intent=Intent(this,InputActivity::class.java)

            val jsonRequest = JsonObjectRequest(Request.Method.GET,url,null,
                    object : Response.Listener<JSONObject>{

                        override fun onResponse(p0: JSONObject) {

                            val id  = p0.getString("id")
                            val name  = p0.getString("name")
                            intent.putExtra("name",name)
                            intent.putExtra("card",editView.text.toString())
                            startActivity(intent)}},Response.ErrorListener{
                println(it)
                fun Context.toast(message: CharSequence)= Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                this.toast("Δεν βρέθηκε ο αριθμός της κάρτας ή πρόβλημα δικτύου")
            })


            queue.add(jsonRequest)

        }

    }

}

