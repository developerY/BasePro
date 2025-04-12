package com.ylabz.basepro.core.data.repository.travel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Demo implementation of CompassRepository that emits fake heading data.
 */
@Singleton
class DemoCompassRepositoryImpl @Inject constructor() : CompassRepository {
    override val headingFlow: Flow<Float> = flow {
        var heading = 0f
        while (true) {
            heading = (heading + 1f) % 360f
            emit(heading)
            delay(500L)
        }
    }
}
