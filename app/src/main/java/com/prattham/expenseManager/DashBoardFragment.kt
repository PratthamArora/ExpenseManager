package com.prattham.expenseManager


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import kotlinx.android.synthetic.main.dashboard_income.view.*
import kotlinx.android.synthetic.main.dashboart_expense.view.*
import kotlinx.android.synthetic.main.fragment_dash_board.*
import kotlinx.android.synthetic.main.fragment_dash_board.view.*
import kotlinx.android.synthetic.main.input_layout.*
import kotlinx.android.synthetic.main.input_layout.view.*
import org.jetbrains.anko.design.snackbar
import java.text.DateFormat
import java.util.*


class DashBoardFragment : Fragment() {

    private var isOpen: Boolean = false
    private var fadeOpen: Animation? = null
    private var fadeClose: Animation? = null

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val ref by lazy {
        FirebaseDatabase.getInstance().reference
    }

    var tot_amnt_inc = 0.0
    var tot_amnt_exp = 0.0

    private val mId = mAuth.currentUser?.uid
    private val mDatabaseExp = mId?.let { ref.child("Expenses").child(it) }
    private val mDatabaseInc = mId?.let { ref.child("Income").child(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_dash_board, container, false)
        //ANIMATION CONNECT

        fadeOpen = AnimationUtils.loadAnimation(activity, R.anim.fab_open)
        fadeClose = AnimationUtils.loadAnimation(activity, R.anim.fab_close)


        //DASHBOARD TOTAL INCOME RESULT
        mDatabaseInc?.addValueEventListener(object : ValueEventListener {


            override fun onCancelled(p0: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {


                for (snap in p0.children) {
                    val items = snap.getValue(Items::class.java)
                    tot_amnt_inc += items!!.amount

                    myView.income_set_result.text = "Rs. $tot_amnt_inc"

                }

            }
        })


        //DASHBOARD TOTAL EXPENSE RESULT
        mDatabaseExp?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {


                for (snap in p0.children) {
                    val items = snap.getValue(Items::class.java)
                    tot_amnt_exp += items!!.amount

                    myView.expense_set_result.text = "Rs. $tot_amnt_exp"
                }
            }
        })

        myView.fab_main.setOnClickListener {

            addData()
            fabAnimation()

        }


        //Dashboard Recycler income

        mDatabaseInc?.keepSynced(true)

        val incomeLayout = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
        incomeLayout.stackFromEnd = true

        myView.recycler_income.setHasFixedSize(true)
        myView.recycler_income.layoutManager = incomeLayout


        //Dashboard Recycler expense

        val expenseLayout = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
        expenseLayout.stackFromEnd = true

        myView.recycler_expense.setHasFixedSize(true)
        myView.recycler_expense.layoutManager = expenseLayout



        return myView
    }

    private fun fabAnimation() {

        if (isOpen) {

            fab_income.startAnimation(fadeClose)
            fab_expense.startAnimation(fadeClose)
            fab_income.isClickable = false
            fab_expense.isClickable = false
            fab_income_text.startAnimation(fadeClose)
            fab_expense_text.startAnimation(fadeClose)
            fab_expense_text.isClickable = false
            fab_income_text.isClickable = false
            isOpen = false

        } else {
            fab_income.startAnimation(fadeOpen)
            fab_expense.startAnimation(fadeOpen)
            fab_income.isClickable = true
            fab_expense.isClickable = true
            fab_income_text.startAnimation(fadeOpen)
            fab_expense_text.startAnimation(fadeOpen)
            fab_expense_text.isClickable = true
            fab_income_text.isClickable = true
            isOpen = true

        }
    }

    private fun addData() {

        //FAB BUTTON

        fab_income.setOnClickListener {
            customDialogIncome()

        }

        fab_expense.setOnClickListener {
            customDialogExpense()

        }


    }

    @SuppressLint("InflateParams")
    private fun customDialogIncome() {


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
            // val mDatabase = mId?.let { ref.child("Income").child(it) }
            val id = mDatabaseInc?.push()?.key
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

            id?.let { it1 -> mDatabaseInc?.ref?.child(it1)?.setValue(items) }

            dash_view.snackbar("Item Added")
            fabAnimation()
            dialog.dismiss()

        }
    }

    @SuppressLint("InflateParams")
    private fun customDialogExpense() {

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

            val id = mDatabaseExp?.push()?.key
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

            id?.let { it1 -> mDatabaseExp?.ref?.child(it1)?.setValue(items) }

            dash_view.snackbar("Item Added")
            fabAnimation()
            dialog.dismiss()

        }
    }


    //DASHBOARD ADAPTERS

    override fun onStart() {
        super.onStart()

        //INCOME ADAPTER
        val incomeOptions = FirebaseRecyclerOptions.Builder<Items>()
            .setQuery(ref.child("Income").child("/${mAuth.currentUser?.uid}"), Items::class.java)
            .setLifecycleOwner(this)
            .build()

        val incomeAdapter =
            object : FirebaseRecyclerAdapter<Items, IncomeViewHolder>(incomeOptions) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): IncomeViewHolder {

                    val itemView = LayoutInflater.from(context)
                        .inflate(R.layout.dashboard_income, parent, false)
                    return IncomeViewHolder(itemView)
                }

                override fun onBindViewHolder(incomeHolder: IncomeViewHolder, p1: Int, p2: Items) {
                    val placeid = getRef(p1).key.toString()


                    ref.child(placeid)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                //toast("Error Occurred")
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                with(incomeHolder.incomeView) {
                                    type_income_db.text = p2.type
                                    date_income_db.text = p2.date
                                    amount_income_db.text = p2.amount.toString()

                                }


                            }

                        }
                        )


                }


            }
        recycler_income.adapter = incomeAdapter
        incomeAdapter.startListening()


        //EXPENSE ADAPTER

        val expenseOptions = FirebaseRecyclerOptions.Builder<Items>()
            .setQuery(ref.child("Expenses").child("/${mAuth.currentUser?.uid}"), Items::class.java)
            .setLifecycleOwner(this)
            .build()

        val expenseAdapter =
            object : FirebaseRecyclerAdapter<Items, ExpenseViewHolder>(expenseOptions) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): ExpenseViewHolder {

                    val expenseItemView = LayoutInflater.from(context)
                        .inflate(R.layout.dashboart_expense, parent, false)
                    return ExpenseViewHolder(expenseItemView)
                }

                override fun onBindViewHolder(
                    expenseHolder: ExpenseViewHolder,
                    p1: Int,
                    p2: Items
                ) {
                    val placeid = getRef(p1).key.toString()


                    ref.child(placeid)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                //toast("Error Occurred")
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                with(expenseHolder.expenseView) {
                                    type_expense_db.text = p2.type
                                    date_expense_db.text = p2.date
                                    amount_expense_db.text = p2.amount.toString()

                                }
                            }
                        }
                        )

                }
            }
        recycler_expense.adapter = expenseAdapter
        expenseAdapter.startListening()
    }


}

class ExpenseViewHolder(internal val expenseView: View) : RecyclerView.ViewHolder(expenseView)


class IncomeViewHolder(internal val incomeView: View) : RecyclerView.ViewHolder(incomeView)
