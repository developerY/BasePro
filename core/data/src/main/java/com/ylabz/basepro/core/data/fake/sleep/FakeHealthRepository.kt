package com.ylabz.basepro.core.data.fake.sleep

import com.ylabz.basepro.core.model.health.SleepSessionData

class FakeHealthRepository(){
    val fakeData = FakeSleepSessionData().getFakeSleepData()
    // Sample fake data for sleep sessions
    fun getData(): List<SleepSessionData> {
        return fakeData
    }
}