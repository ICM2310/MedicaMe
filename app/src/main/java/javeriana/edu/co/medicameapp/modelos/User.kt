package javeriana.edu.co.medicameapp.modelos

class User
{
    var name : String? = null
    var eps : String? = null
    var id : String? = null

    constructor(){}

    constructor(name: String?, eps: String?, id: String?)
    {
        this.name = name
        this.eps = eps
        this.id = id
    }


}