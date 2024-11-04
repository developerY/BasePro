package com.ylabz.basepro.core.network.api.interfaces

interface MapsAPI {
    fun getMapDirections(org: String, des: String): String
}