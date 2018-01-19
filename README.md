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
For now the app does not support passing `csv` data through REST API. It has to be done 
with `POST` request on `/schedules/schedule` with `path` string as a body. `path` represent 
name of the file placed in `resources/data` directory of the project. For example, given 
the file `resources/data/input1.csv` with the content
```
0, 10, 4, 2
1, 5, 2, 3
2, 10, 2, 2
3, 5, 1, 4
```
where successive columns of the `csv` file represents `id`, `period`, `duration` and `cost`
and making a call to `/schedules/schedule` with body
```
input1.csv
```
the app will respond with `201` code with URL to the created schedule in `Location` header.
For example: `/schedules/schedule/1`.

In the next step you could use the returned location of the resource to make a `GET` call 
for it. Application will return `JSON` representation of calculated schedule in the following form:
```json
{
    "scheduleTable": [
        [
            {
                "jobId": 0,
                "start": true
            },
            {
                "jobId": 1,
                "start": true
            },
            {
                "jobId": 2,
                "start": true
            },
            {
                "jobId": 3,
                "start": true
            }
        ],
        [
            {
                "jobId": 0,
                "start": false
            },
            {
                "jobId": 1,
                "start": false
            },
            {
                "jobId": 2,
                "start": false
            }
        ],
        [
            {
                "jobId": 0,
                "start": false
            }
        ],
        [
            {
                "jobId": 0,
                "start": false
            }
        ],
        [],
        [
            {
                "jobId": 1,
                "start": true
            },
            {
                "jobId": 3,
                "start": true
            }
        ],
        [
            {
                "jobId": 1,
                "start": false
            }
        ],
        [],
        [],
        []
    ]
}
```
Every element of `scheduleTable` represents point in time consisting of jobs that are 
running in this time frame. 

