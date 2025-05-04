Thought for a second


Use **`@Binds`** whenever you have a concrete implementation class that you can inject directly (i.e. it has an `@Inject`‐annotated constructor) and you just need to tell Hilt “whenever you need interface X, use implementation Y.” It’s:

* **Abstract**: only one line in an abstract module
* **Compile-time**: no runtime factory overhead
* **Type-safe**: Hilt will validate at compile time that `Y` really implements `X`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MyModule {
  @Binds
  @Singleton
  abstract fun bindFooRepo(
    impl: FooRepositoryImpl
  ): FooRepository
}
```

Use **`@Provides`** when you can’t use constructor injection directly—e.g.:

* Third-party classes without `@Inject` constructors
* You need to pass in primitives or values computed at runtime
* You want to build the instance with some custom logic
* You need multiple parameters or qualifiers that aren’t covered by a simple binding

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  @Provides
  @Singleton
  fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
      .baseUrl("https://api.example.com")
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
  }
}
```

---

### TL;DR

* **`@Binds`** = “I already have an `@Inject` constructor—just wire interface → impl.”
* **`@Provides`** = “Write a factory method (in an `object`) because I can’t or don’t want to use constructor injection.”
