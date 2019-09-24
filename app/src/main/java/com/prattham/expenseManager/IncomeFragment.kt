package com.prattham.expenseManager


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.prattham.expenseManager.Modal.Items
import kotlinx.android.synthetic.main.fragment_income.*
import kotlinx.android.synthetic.main.fragment_income.view.*
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_layout.view.*
import kotlinx.android.synthetic.main.item_data.view.*
import kotlinx.android.synthetic.main.update_data.view.*
import org.jetbrains.anko.design.snackbar
import java.text.DateFormat
import java.util.*


@Suppress("DEPRECATION")
class IncomeFragment : Fragment() {

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val ref by lazy {
        FirebaseDatabase.getInstance().reference.child("Income")
    }

    private val mId = mAuth.currentUser?.uid
    private val mDatabase = mId?.let { ref.child(it) }

    private var type: String? = null
    private var amount: Double = 0.0
    private var details: String? = null
    private var postkey: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_income, container, false)


        mDatabase?.keepSynced(true)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true
        myView.recycler_home_income.setHasFixedSize(true)
        myView.recycler_home_income.layoutManager = layoutManager

        //Total Income
        mDatabase?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                var tot_amnt = 0.0

                for (snap in p0.children) {
                    val items = snap.getValue(Items::class.java)
                    tot_amnt += items!!.amount

                    myView.total_amount_income.text = "Rs. $tot_amnt"
                }
            }
        })


        myView.btn_fab_income.setOnClickListener {
            customDialog()
        }

        return myView
    }


    @SuppressLint("InflateParams")
    private fun customDialog() {

        val myDialog = AlertDialog.Builder(context!!)
        val li = LayoutInflater.from(context)
        val myView = li.inflate(R.layout.input_layout, null)
        val dialog = myDialog.create()
        dialog.setView(myView)
        dialog.show()


        myView.btn_save.setOnClickListener {

            val type = myView.et_type.text.toString()
            val details = myView.et_details.text.toString()
            //check for empty

            //USE TEXT INPUT LAYOUT INSTEAD OF EDIT_TEXT
            val amount = myView.et_amount.text.toString().toDouble()


            if (TextUtils.isEmpty(myView.et_type.text.toString().trim())) {
                et_type.error = "Required Field"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(myView.et_amount.text.toString().trim())) {
                et_amount.error = "Required Field"
                return@setOnClickListener

            }
            if (TextUtils.isEmpty(myView.et_details.text.toString().trim())) {
                et_details.error = "Required Field"
                return@setOnClickListener

            }

            val id = mDatabase?.push()?.key
            val date = DateFormat.getDateInstance().format(Date())

            val items = id?.let { it1 ->
                Items(
                    type,
                    amount,
                    details,
                    date,
                    it1
                )
            }

            id?.let { it1 -> mDatabase?.ref?.child(it1)?.setValue(items) }


            ll_view_income.snackbar("Item Added")
            dialog.dismiss()

        }
    }

    override fun onStart() {
        super.onStart()

        val options = FirebaseRecyclerOptions.Builder<Items>()
            .setQuery(ref.child("/${mAuth.currentUser?.uid}"), Items::class.java)
            .setLifecycleOwner(this)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Items, MyViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

                val itemView = LayoutInflater.from(context)
                    .inflate(R.layout.item_data, parent, false)
                return MyViewHolder(itemView)


            }


            override fun onBindViewHolder(holder: MyViewHolder, p1: Int, p2: Items) {
                val placeid = getRef(p1).key.toString()

                //CHANGING COLOR OF INCOME AMOUNT
                holder.myview.tv_amount.setTextColor(resources.getColor(R.color.income_tv_amount))


                ref.child(placeid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            // Toast.makeText(context,"bar called",Toast.LENGTH_SHORT).show()

                        }


                        override fun onDataChange(p0: DataSnapshot) {


                            if (p0.value == null) {
                                progress_bar_income.visibility = View.GONE
                                //check here
                                Log.i(
                                    "PROBAR",
                                    "progress bar called exist:  $progress_bar_income"
                                )
                            } else {
                                Log.i(
                                    "PROBAR",
                                    "progress bar called not exists:  $progress_bar_income"
                                )

                            }


                            with(holder.myview) {
                                tv_type.text = p2.type
                                tv_date.text = p2.date
                                tv_amount.text = p2.amount.toString()
                                tv_detail.text = p2.details

                            }
                            //UPDATE ITEM
                            holder.myview.setOnClickListener {

                                postkey = getRef(p1).key
                                type = p2.type
                                details = p2.details
                                amount = p2.amount



                                updateData()
                            }


                        }

                    }
                    )


            }


        }
        recycler_home_income.adapter = adapter
        adapter.startListening()


    }

    class MyViewHolder(internal var myview: View) : RecyclerView.ViewHolder(myview)


    fun updateData() {

        val update_Dialog = AlertDialog.Builder(context!!)
        val inflater = LayoutInflater.from(context)
        val update_View = inflater.inflate(R.layout.update_data, null)
        val up_dialog = update_Dialog.create()
        up_dialog.setView(update_View)
        up_dialog.show()


        update_View.et_type_upd?.setText(type)
        update_View.et_type_upd?.setSelection(type!!.length)
        update_View.et_amount_upd?.setText(amount.toString())
        update_View.et_amount_upd?.setSelection(amount.toString().length)
        update_View.et_details_upd?.setText(details)
        update_View.et_details_upd?.setSelection(details!!.length)



        update_View.btn_Update.setOnClickListener {

            val date = DateFormat.getDateInstance().format(Date())

            type = update_View.et_type_upd.text.toString()
            details = update_View.et_details_upd.text.toString()
            amount = update_View.et_amount_upd.text.toString().toDouble()

            val items = postkey?.let { it1 -> Items(type!!, amount, details!!, date, it1) }

            postkey?.let { it1 -> mDatabase?.ref?.child(it1)?.setValue(items) }

            //toast("Item Updated")
            ll_view_income.snackbar("Item Updated")

            up_dialog.dismiss()

        }

        update_View.btn_delete.setOnClickListener {
            mDatabase?.ref?.child(postkey!!)?.removeValue()
            ll_view_income.snackbar("Item Deleted", "Undo") {
                up_dialog.show()
            }
            up_dialog.dismiss()
        }
    }

}
