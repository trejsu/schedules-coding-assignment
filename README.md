# Schedules - coding assignment

This project is a coding assignment based on scheduling problem. 
Given a processing system that only accepts work as jobs, the goal is to
schedule job execution to minimize the cost at any given point of time.

## Getting Started

### Prerequisites

To get the project up and running on your local machine, you will need to get:
* `maven`
* `Java JDK`

### Running

To run the project use

```
mvn spring-boot:run
```
After that, application should be exposed at `localhost:8080/schedules`. You can check if it started
as it supposed to by performing `GET` request on `http://localhost:8080/schedules/echo`. App should 
response with
```
"hello!"
```

## Running the tests

Unit tests for the system can be run with
```
mvn test
```

## Features

The main feature of the application is creating schedules based on `csv` input with jobs information.
The app supports passing `csv` data through REST API. It has to be done with `POST` request on 
`/schedules/schedule` 

For example, making a call to `/schedules/schedule` with body
```
0, 10, 4, 2
1, 5, 2, 3
2, 10, 2, 2
3, 5, 1, 4
```
where successive columns of the `csv` file represents `id`, `period`, `duration` and `cost`,
the app will respond with `201` code with URL to the created schedule in `Location` header.
For example: `/schedules/schedule/1`.

---
**NOTE**

`/schedules/schedule` endpoint accepts `text/plain` content type.

---

In the next step you could use the returned location of the resource to make a `GET` call 
for it. Application will return `JSON` representation of calculated schedule in the following form:
```json
{
    "scheduleTable": [
        [
            0,
            1,
            2,
            3
        ],
        [
            0,
            1,
            2
        ],
        [
            0
        ],
        [
            0
        ],
        [],
        [
            1,
            3
        ],
        [
            1
        ],
        [],
        [],
        []
    ]
}
```
Every element of `scheduleTable` represents point in time consisting of jobs (referenced by `id`) that are 
running in this time frame. 

API also provides getting some info about created schedule:
* instant cost of all the running jobs in time `t` 
    * `GET` `schedule/{id}/cost?time={time}`
    * example response:
    ```json
    7
    ```
* list of all the running jobs at time `t`
    * `GET` `schedule/{id}/list?time={time}`
    * example response:
    ```json
    [
        {
            "id": 0,
            "period": 10,
            "duration": 4,
            "cost": 2
        },
        {
            "id": 1,
            "period": 5,
            "duration": 2,
            "cost": 3
        },
        {
            "id": 2,
            "period": 10,
            "duration": 2,
            "cost": 2
        },
        {
            "id": 3,
            "period": 5,
            "duration": 1,
            "cost": 4
        }
    ]
    ```
* next job that will be run and when after time `t`
    * `GET` `schedule/{id}/next?time={time}`
    * example response:
    ```json
    {
        "id": 1,
        "period": 5,
        "duration": 2,
        "cost": 3,
        "start": 5
    }
    ```
* maximum instant cost of the whole schedule
    * `GET` `schedule/{id}/max`
    * example response:
    ```json
    11
    ```