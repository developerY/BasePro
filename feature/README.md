How To Code Following MAD Best Practices
1. Unidirectional Data Flow (UDF)
   Event Flow: User interactions generate events that are sent to the ViewModel via the onEvent function.
   State Management: The ViewModel processes these events, performs any necessary business logic or side effects, and updates its internal state.
   State Observation: The UI layer observes the uiState exposed by the ViewModel and updates its composables accordingly.
2. Separation of Concerns
   ViewModel: Handles all business logic, state management, and side effects. It exposes immutable state flows to the UI.
   UI Layer: Composables are stateless and side-effect free. They render UI based on the observed state and send user events to the ViewModel.
3. Immutable State Exposure
   The ViewModel exposes an immutable StateFlow<HealthUiState> to the UI.
   Mutable state (MutableStateFlow) is kept private within the ViewModel.
4. Clear Event Handling
   Events are clearly defined in the HealthEvent sealed class.
   The ViewModel processes events in a centralized onEvent function, making the flow easy to trace and maintain.
5. Error Handling
   Errors are represented in the HealthUiState sealed class, allowing the UI to react appropriately to different error scenarios.