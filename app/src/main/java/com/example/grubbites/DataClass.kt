package com.example.grubbites

data class Products (
    var descrption: String? = "",
    var imageDownloadUrl: String? = "",
    var price: String? = "",
    var title: String? = "",


    ){
    constructor() : this("", "", "", "")
}