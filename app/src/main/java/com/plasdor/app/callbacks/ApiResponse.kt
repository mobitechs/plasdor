package com.plasdor.app.callbacks

interface ApiResponse {

    fun onSuccess(data: Any, tag: String)
    fun onFailure(message: String)
//    fun onSuccess(response: JSONObject, tag: String)

}