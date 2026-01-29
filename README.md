# GoSendeet Ride Tracking API

A simple Java backend service simulating rider tracking for a logistics platform.

---

## Overview

GoSendeet Ride Tracking allows trips to be created, tracked, updated, and completed.  
It handles rider location updates, calculates total distance traveled, and provides ETA estimates.  
The system enforces business rules to ensure trips follow a valid lifecycle.

---

## Assumptions

- **Ride tracking responsibility:**  
  Tracking is shared between the mobile device and the backend. Devices send periodic updates, and the backend maintains the trip state. Which can be read and managed by an admin if necessary.

- **Distance and ETA:**  
  Updates include `distance remaining` and `current ETA`. Backend uses these to update the trip’s tracking state.
  I made this decision after studying the MAPs API and possible ways to track location. [one of my references](https://mapsplatform.google.com/resources/blog/how-calculate-distances-map-maps-javascript-api/)

- **Update frequency:**  
  Devices can send updates every 2–4 seconds to allow near-real-time tracking.

- **Trip state rules:**  
  - Trip must be assigned to a rider before it can start.  
  - Trip can only start if assigned.  
  - Trip can only be cancelled if not completed.  
  - Trip can only be completed if in progress and (optionally) parcel delivered successfully.
I left out trip cost management because this API is just supposed to handle rider tracking.

- **Storage:**  
  In-memory storage (`InMemoryTripRepository`) is used for simplicity; production would use a database.

---

## Architecture & Design

- **DDD-inspired structure**:
  - **Domain layer**: `Trip`, `TrackingState` encapsulate core business logic and rules.
  - **Application layer (UseCases)**: Encapsulate actions like `CreateTripUseCase`, `StartTripUseCase`, `AcceptRiderLocationUseCase`.
  - **Infrastructure layer**: In-memory repository (`InMemoryTripRepository`) and `DataStore`.
  - **API layer**: Exposes REST endpoints.

- **Benefits of this approach**:
  - Clear separation of concerns
  - Easier to maintain and extend
  - Business rules centralized in the domain

---

## Key Features

- **Trip Lifecycle Management**
  - Create trip (`POST /trip`)  
  - Assign rider (`PATCH /trip/{tripId}/assign/{riderId}`)  
  - Start trip (`PATCH /trip/{tripId}/start`)  
  - Cancel trip (`PATCH /trip/{tripId}/cancel`)  
  - Complete trip (`PATCH /trip/{tripId}/complete`)

- **Rider Tracking**
  - Update location (`PATCH /trip/{tripId}/tracking`)  
  - Get current tracking state (`GET /trip/{tripId}/tracking`)

- **Other**
  - Trips can only be completed if `parcelDeliveredSuccessfully = true`.
  - When a Trip is stationary the backend would find out. As long as updates keep comming.

---

## Testing

- **Unit Tests**
  - Domain logic in `TripTest`
  - Tracking updates
  - Trip lifecycle: assign → start → update → complete → cancel (including invalid operations)

> Tests cover both happy paths and invalid operations, ensuring business rules are enforced.

---


## How to Run
1. Have java install and ready for development on your machine... mine was linux...
2. Clone this repository, cd into the project and run the following commands:
```bash
# Build the project
mvn clean install

# Run the Spring Boot application
mvn spring-boot:run

# Run tests
mvn test
```
Use the postman collection to visually interact with the API.


# Key Decisions & Observations

## How often should riders send location updates?

 **2-4 second intervals** for location updates. This feels like the sweet spot between keeping the map accurate in real-time and not hammering the network or backend with unnecessary requests. 

Sending updates too frequently (every second) risk large amount of bandwidth, draining batteries, and overwhelming the server. Too infrequently (every 10+ seconds) and the tracking starts to feel laggy, making ETAs less reliable and customer experience suffer.

## What happens when the network drops?

Poor connectivity is just a reality of mobile apps, especially for riders moving through areas with less coverage. Rather than trying to build a perfect retry mechanism, I've designed the system to be **eventually consistent**.

If a location update fails to send, no problem the next successful update will catch us up. The backend doesn't care if it missed a few pings; it recalculates everything based on the latest position. This also means sending the same location twice (due to retries) won't break anything the system is idempotent by design.


## What metrics actually matter?

I looked at what makes a delivery service work well, these are the numbers that tell the real story:

- **Distance travelled vs. planned distance** — Shows us when riders are taking detours or getting lost. A 5km trip that turns into 8km might indicate routing issues or inefficient navigation. so we can actively optimise when necessary.

- **ETA accuracy** — The gap between what is promise and what we deliver.

- **Trip completion rate** — How many trips actually make it from creation to successful delivery? High cancellation rates point to operational issues.

- **Movement patterns** — Is the rider stuck in traffic, lost, or just sitting idle? Detecting stationary periods helps us identify problems before customers complain.

## How do we handle edge cases and bad data?

The domain model enforces some strict rules to keep things clean:

- **No delivery, no completion** — You can't mark a trip as complete unless the parcel was successfully delivered. This prevents accidental or fraudulent completions.

- **State transitions must make sense** — A trip goes CREATED → ASSIGNED → IN_PROGRESS → COMPLETED. You can't skip steps or go backwards (except to CANCELLED).

- **Completed means locked** — Once a trip is done, that's it. No more updates, no changes. This gives us a reliable audit trail.

These guardrails catch mistakes before they become data integrity issues, even when dealing with delayed updates, duplicate requests, or weird edge cases from flaky mobile networks.

## What I Could Improve for Ride Tracking with more time

1. Birectional streaming of location details:
Instead of using REST for frequest location updates, I would use gRPC with birectional streaming betwen rider's mobile device and the backend.
compared to periodically pooling a REST endpoint, or websockets, gRPC helps with reducing bandwidth overhead and compute costs.

2. I'll move distance and eta calculations to the backend. so there's a single source of truth for location tracking.
This way I could use dead reckoning to estimate the rider's future position, eta and distance travelled during short network outages. This would also reduce cost and dependency on external APIs like the direction API.
Also to avoid unncessary computation, recalculation can be rate-limited or skipped if new data match older predictions

## Possible scaling plans
Initially, I think we should vertically scale our compute to serve lagos as demand and user base grow...
This is because lagos is a fixed city and all requests to the server possibly hosted on AWS or GCP would be comming from there. 
Even when we expand to another Nigerian city, the network travel time between server and devices would be neglible. However, at that time we can consider deploying in multiple regions for better services only at that scale.