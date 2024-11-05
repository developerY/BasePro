package com.ylabz.basepro.core.data.api.interfaces

interface MapsAPI {
    fun getMapDirections(org: String, des: String): String
}