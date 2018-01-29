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
    
## Algorithm

To minimize the maximum instant cost and idle time of the system, I implemented a solution based on
the sum of subset problem. I am processing jobs starting from the most expensive one and choosing
the right place by dynamically finding the period of time equal to the job's duration with 
a minimum cost sum of all already running jobs in that interval. You can check out the scheduling
code in [Scheduler](src/main/java/com/schedules/scheduler/Scheduler.java).

### Example

Lets assume we have a following input:

| id    | period    | duration  | cost  |
| ----- | --------- | --------- | ----- |
| 0     | 3         | 1         | 1     |
| 1     | 10        | 4         | 2     |
| 2     | 5         | 2         | 3     |
| 3     | 4         | 2         | 4     |

Jobs will be processed starting from the most expensive ones so the order will be: `3`, `2`, `1`, `0`.
Resulting schedule will have total duration of `10` time units - it is equal to the longest period 
of input jobs. I will be referring to the `cost array` as the array where each index represents
time frame of the schedule and value in that index corresponds to the instant cost of all already
running jobs in that moment.

#### Job 3

`cost array = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}` -  schedule is empty.
`job 3` has period equal to `4` so it has to be placed twice in our schedule (`occurrences = 2`). Exactly once in interval
`[0, 3]` and exactly once in interval `[4, 7]`. Looking for the starting index in `cost array` 
with the minimum sum of subset of the length `2` (`2` is our jobs duration) we have to perform
this task `occurrences` times - once for the `costs[0:3]` and once for the `costs[4:7]`. 
Algorithm always returns the first index with the minimum sum so we will be placing `job 3` starting
from `0` and then from `4`. Now `cost array = {4, 4, 0, 0, 4, 4, 0, 0, 0, 0}`

#### Job 2

Repeating the above steps for the `job 2`, algorithm will find the optimal place for it 
under the indexes `2` and `6`. For the first occurrence `{4, 4, 0, 0, 4}` part of the `costs array`
was taken into account, so its trivial to see that index `2` is the starting position of minimal
sum of `length 2` subset - it is equal to `0`. For the second occurrence, `{4, 0, 0, 0, 0}` is 
processed so algorithm returns index `1` which after adding `offset 5` - jobs period - results in
position `6` in the schedule. `cost array = {4, 4, 3, 3, 4, 4, 3, 3, 0, 0}`.

#### Job 1

In the same way as previous, `job 1` is placed under `6` what results with 
`cost array = {4, 4, 3, 3, 4, 4, 5, 5, 2, 2}.`

#### Job 0

At the very end `job 0` is placed under `2` (index `2` has the lowest sum in `{4, 4, 3}`), 
`3` (index `0` has the lowest sum in `{3, 4, 4}` and `0 + offset 3 = 3`) and `8` 
(index `2` has the lowest sum in `{5, 5, 2}` and `2 + offset 6 = 8`).

#### Result

The final result of `Scheduler::createSchedule` is the following schedule:

`[[3],[3],[0,2],[0,2],[3],[3],[1,2],[1,2],[0,1],[1]]`

The maximum instant cost = `5`.

