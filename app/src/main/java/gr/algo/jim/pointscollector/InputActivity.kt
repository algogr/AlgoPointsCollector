package gr.algo.jim.pointscollector

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.pointsinput.*
import org.json.JSONArray
import android.widget.Toast
import kotlinx.android.synthetic.main.listitem.*
import org.w3c.dom.Text
import android.text.TextWatcher
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class InputActivity : AppCompatActivity() {
    private var listProducts = ArrayList<Product>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pointsinput)
        textView.setText(intent.getStringExtra("name"))
        var pList:MutableList<Product> = mutableListOf()

        val queue = Volley.newRequestQueue(this)
        var productsAdapter=ProductsAdapter(this,pList)
        val urlP="http://192.168.0.10:8080/products"

        var jsonArrReq= JsonArrayRequest(Request.Method.GET,urlP,null,
                object : Response.Listener<JSONArray>{

                    override fun onResponse(p0: JSONArray) {
                        println("GotProduct Response")

                        for (i in 0 until p0.length())
                        {

                            val id  = p0.getJSONObject(i).getString("id")
                            val descr  = p0.getJSONObject(i).getString("description")
                            val product=Product(id,descr,"0.00")
                            pList?.add(i,product)


                        }

                        productsAdapter=ProductsAdapter( this@InputActivity,pList)
                        lvProducts.adapter=productsAdapter
                    }

                },object : Response.ErrorListener{
            override fun onErrorResponse(p0: VolleyError?) {

                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        queue.add(jsonArrReq)


        val btn_send= button2
        btn_send.setOnClickListener(){

            for (i:Int in 0 until productsAdapter.count)
            {
                val view=productsAdapter.getView(i,null,null)
                val vh= view?.tag as InputActivity.ViewHolder
                val product:String=vh.textView2.text as String
                val productid= vh.productid

                val qty= vh.editText2.text
                val position=vh.position

                var cardid:String=""

                if (qty != null)
                {

                    val urlG="http://192.168.0.10:8080/card?number=${intent.getStringExtra("card")}"
                    val jsonRequest = JsonObjectRequest(Request.Method.GET,urlG,null,
                            object : Response.Listener<JSONObject>{

                                override fun onResponse(p0: JSONObject) {

                                    val id  = p0.getString("id")
                                    val number=p0.getString("number")
                                    cardid=id

                                }},Response.ErrorListener{
                        TODO("not implemented")
                    })


                    queue.add(jsonRequest)
                    Log.i("JJProduct",product)
                    Log.i("JJProductId",productid)
                    Log.i("JJPosition",position.toString())
                    Log.i("JJQty",qty.toString())
                    Log.i("JJcardid",cardid)
                    val urlP="http://192.168.0.10:8080/insert?cardId=$cardid&productId=$productid&qty=${qty.toString()}"
                    val stringRequest = StringRequest(Request.Method.POST,urlP,
                          Response.Listener<String>{response->if (response=="200"){fun Context.toast(message: CharSequence)= Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                              this.toast("Η συναλλαγή καταχωρήθηκε επιτυχώς!!")} },Response.ErrorListener{fun Context.toast(message: CharSequence)= Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
                        this.toast("Πρόβλημα δικτύου!!")})
                    queue.add(stringRequest)

                }







            }


        }







        // lvProducts.onItemClickListener=AdapterView.OnItemClickListener(adapter)
    }


    inner class ProductsAdapter : BaseAdapter {

        private  var productsList : MutableList<Product>
        private  var context: Context? =null
        private  val mInflator: LayoutInflater
        private var hashMapTexts:  HashMap<String,String>



        constructor(context: Context, productsList: MutableList<Product>) : super(){
            this.productsList= productsList
            this.context=context
            this.hashMapTexts= HashMap()
            this.mInflator= LayoutInflater.from(context)
            Log.i("JDP","Product:")
            for ( product:Product in productsList) {
                Log.i("JDP","Product:${product.descr}")
                this.hashMapTexts.put(product.descr,"")

            }
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
            val holder: InputActivity.ViewHolder
            val view: View

            if (convertView==null){
                view= layoutInflater.inflate(R.layout.listitem,parent,false)
                holder=ViewHolder(view)
                holder.productid=productsList[position].id
                //holder.editText2=view.findViewById(R.layout.listitem.)
                view.tag=holder
                Log.i("JSA","Set log for Viewholder,position: "+position)
            }
            else {

                view = convertView
                holder = view.tag as InputActivity.ViewHolder
            }
            holder.position=position
            holder.textView2.text=productsList[position].descr
            holder.productid=productsList[position].id
            val editable = SpannableStringBuilder(productsList[position].qty.toString())
            holder.editText2.text= editable

            holder.editText2.addTextChangedListener(object:TextWatcher {
                override fun beforeTextChanged(charSequence:CharSequence, i:Int, i1:Int, i2:Int) {
                }
                override fun onTextChanged(charSequence:CharSequence, i:Int, i1:Int, i2:Int) {
                    //productsList[position].qty=holder.editText2.getText().toString()
                    //Log.i("JC$position-0",productsList[position].qty.toString())
                }
                override fun afterTextChanged(editable:Editable) {
                    hashMapTexts.put(productsList[position].descr,editable.toString())
                }
            })
            val test=view.tag as InputActivity.ViewHolder
            Log.i("JL",test.productid)
            return view
        }

        override fun getItem(position: Int): Any {
            return productsList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return productsList.size
        }

    }


    private class ViewHolder(view: View?){
        val textView2: TextView
        var editText2: EditText
        var productid: String
        var position:Int

        init{
            this.textView2=view?.findViewById<TextView>(R.id.textView2) as TextView
            this.editText2=view?.findViewById<EditText>(R.id.editText2) as EditText
            this.productid=""
            this.position=-1
        }
    }

}
