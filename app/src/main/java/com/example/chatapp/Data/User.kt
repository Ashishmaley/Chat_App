package com.example.chatapp.Data

class User {
    var uid : String?=null
    var name : String?=null
    var profileImage : String?=null
    var phoneNumber : String?=null
    constructor(){
        
    }
    constructor(uid: String?, name: String?, profileImage: String?, phoneNumber: String?)
    {
        this.phoneNumber=phoneNumber
        this.name=name
        this.uid=uid
        this.profileImage=profileImage
    }


}