In a **clean architecture** Android project using **Modern Android Development (MAD)**, the structure 
typically follows the principles of separating the concerns into layers:

- **Domain Layer**: Contains use cases and business logic.
- **Data Layer**: Contains repositories and data sources (APIs, local databases, etc.).
- **Presentation Layer**: Contains UI-related components (ViewModel, Composables, etc.).

Your **fake data** is usually part of the **data layer** and is used to simulate data responses for 
testing, previews, and development. Here's where and how you can add fake data in your project:

---

## **Where to Put Fake Data**

### 1. **Create a `fake` package inside `core/data`**

Since you’re using a `core/data` directory, this is the ideal place to place your fake data:
- Path: `core/data/fake/`
- Example structure:
  ```
  core/
    ├── data/
    │   ├── repository/
    │   ├── source/
    │   ├── fake/   <-- Add your fake data classes here
    │   │   ├── FakeHealthRepository.kt
    │   │   ├── FakeSleepSessionData.kt
  ```

The `fake` package can contain:
- **Fake Repositories**: For mocking repository behavior.
- **Fake Models**: For creating static or random data for testing.
- **Fake Use Cases** (if needed for previews).

## Summary:

- Place your fake data in `core/data/fake/`.
- Create a `FakeHealthRepository.kt` or `FakeSleepSessionData.kt` to generate mock data.
- Use the fake data in your previews and unit tests to simulate UI and repository behavior.

This approach keeps your project modular and aligns with **clean architecture principles** while 
enabling easier development and testing workflows.