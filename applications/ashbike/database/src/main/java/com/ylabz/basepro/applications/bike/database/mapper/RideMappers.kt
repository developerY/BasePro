package com.ylabz.basepro.applications.bike.database.mapper


import com.ylabz.basepro.applications.bike.database.BikeRideEntity

// 1) Alias your domain model to the Room entity
typealias BikeRide = BikeRideEntity

// 2) Identity conversions
fun BikeRideEntity.toDomain(): BikeRide = this
fun BikeRideEntity.toBikeRide(): BikeRide = this
fun BikeRide.toEntity(): BikeRideEntity = this