package com.ylabz.basepro.applications.bike.database

import javax.inject.Qualifier

/**
 * A @Qualifier is an annotation used in dependency injection frameworks like Dagger or Hilt to
 * distinguish between different bindings (or instances) of the same type.
 * In other words, when you have two or more objects of the same type that you want to inject,
 * a qualifier tells the DI container which one to use.
 *
 * Why Use a Qualifier?
 * Without a qualifier, if you try to inject a dependency and there are multiple possible providers
 * for that type, the DI framework doesn’t know which one to choose. This can lead to ambiguity or conflicts.
 * By applying a qualifier, you explicitly mark which provider you want.
 *
 * Example Scenario
 * Suppose you have two Room databases in your app—a database for your Bike app and another for a
 * different module. Both databases are of type BikeProDB, and without additional instructions,
 * Hilt wouldn’t know which one to inject into your ViewModel.
 *
 * How Qualifiers Work
 * Custom Qualifier Annotation: You annotate your qualifier annotation with @Qualifier and typically
 * retain it at runtime or binary level.
 *
 * Marking Providers: Use your qualifier annotation on provider methods (in modules) to indicate
 * which instance it returns.
 *
 * Injecting Dependencies: Apply the same qualifier annotation on the injection site
 * (e.g., in a ViewModel's constructor parameter) so the DI container knows which instance to provide.
 *
 * Summary
 * @Qualifier helps disambiguate dependencies of the same type.
 *
 * You create custom qualifier annotations (such as @BikeDatabase and @OtherDatabase) and use them
 * in your Hilt modules and at injection points.
 *
 * This ensures that when you inject a dependency, the correct instance is provided based on the
 * qualifier you specify.
 *
 * Qualifiers are a powerful tool in managing multiple bindings in a DI system and help ensure that
 * your components receive exactly the dependency they require.
 *
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BikeDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherDatabase
