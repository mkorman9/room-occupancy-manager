# room-occupancy-manager

## Prerequisites

- Java 21 installed locally (project has been tested with Temurin OpenJDK 21.0.1+12 on Linux)
- (Optionally) Configured Docker client to build an image

## Run tests and build a package

```sh
./mvnw package
```

When the app is packaged, the Docker image can be built with:

```sh
docker build -t room-occupancy-manager .
```

And later started with:

```sh
docker run -it --rm -p 8080:8080 room-occupancy-manager
```

## Start the app locally

```sh
./mvnw quarkus:dev
```

After successful startup, API can be tested with the following curl command:

```sh
curl -X PUT -H "Content-Type: application/json" -d '
    {
        "guestOffers": [23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209],
        "availableRooms": {
            "premium": 3,
            "economy": 3
        }
    }
  ' \
  http://localhost:8080/api/occupancy
```

Expected response is:

```json
{
  "allocation": {
    "premium": 3,
    "economy": 3
  },
  "profit": {
    "total": 905.99,
    "premium": 738,
    "economy": 167.99
  },
  "rejectedReservations": [
    "could not allocate guest paying 115.00 to premium room",
    "could not allocate guest paying 101.00 to premium room",
    "could not allocate guest paying 100.00 to premium room",
    "not enough rooms to allocate guest paying 22.00"
  ]
}
```

## Side notes

- App assumes the currency is always EUR
- Complexity of the used algorithm is `O(n * log(n))` where `n` is the number of guest offers
- Project is integrated with GitHub Actions to build on each push 
