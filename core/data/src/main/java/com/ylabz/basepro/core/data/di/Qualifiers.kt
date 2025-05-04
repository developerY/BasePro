package com.ylabz.basepro.core.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HighPower

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LowPower

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Demo