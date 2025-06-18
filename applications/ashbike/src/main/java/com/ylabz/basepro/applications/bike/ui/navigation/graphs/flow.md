You are absolutely correct in your thinking. Let's clarify the workflow, as this is a crucial point.

1.  **When the Cafe List is Needed**: You only need the list of cafes when the user opens the `RideDetailScreen` to view the map of a **completed ride**. You are right, it is not needed at the moment the user presses "Stop" and saves the ride.

2.  **How the `lat/lon` is known**: The `RideDetailScreen` is designed to show the details of a ride that is already finished. When it loads, the `RideDetailViewModel` uses the `rideId` to fetch the `rideWithLocs` from the database. This `rideWithLocs` object contains the full path of the completed ride, including the starting `lat/lon`.

Therefore, the architecture we discussed in the last message works exactly as you need it to. It doesn't fetch cafes for a *live* ride. It fetches them for the *historic* ride the user is viewing.

Here is the precise sequence of events with the proposed architecture:

1.  A user finishes a ride and saves it.
2.  Later, the user goes to a list of their past rides and taps on one to see the details.
3.  The app navigates to the `RideDetailScreen`, passing the `rideId`.
4.  Inside the `bikeNavGraph` composable for this screen, two things happen simultaneously:
    * `RideDetailViewModel` starts fetching the `rideWithLocs` for the completed ride.
    * `CoffeeShopViewModel` is created and waits.
5.  Our `LaunchedEffect(rideWithLocs)` is waiting. As soon as `RideDetailViewModel` successfully loads the ride data, `rideWithLocs` is no longer null.
6.  The `LaunchedEffect` triggers. It takes the starting `lat/lon` from the **completed ride's data** and sends the `FindCafesNear` event to the `CoffeeShopViewModel`.
7.  `CoffeeShopViewModel` fetches the nearby cafes from Yelp and updates its state.
8.  The UI, which is already displaying the ride path, now receives the list of cafes and draws them on the map.

This ensures that the Yelp call **only happens when a user explicitly views the detail screen** and **uses the location of the historic ride** they are looking at.

You are correct, the cafe list is purely for on-screen display on that specific map and is not saved to the database with the ride details. The architecture we've laid out perfectly supports this requirement.