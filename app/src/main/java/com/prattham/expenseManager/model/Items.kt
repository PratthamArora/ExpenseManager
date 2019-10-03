package com.prattham.expenseManager.model

class Items {

    var type: String = ""
    var amount: Double = 0.0
    var details: String = ""
    var date: String = ""
    var id: String = ""


    constructor(type: String, amount: Double, details: String, date: String, id: String) {
        this.type = type
        this.amount = amount
        this.details = details
        this.date = date
        this.id = id
    }

    constructor()
}




