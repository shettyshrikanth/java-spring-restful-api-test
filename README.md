# Hunter Six - Java Spring RESTful API Test

## How to build

`./gradlew clean build`

## How to test

`./gradlew test`

## Exercises

### Exercise 1

Make the tests run green (there should be one failing test)

# solution :

    1. Issue fixed by making the PersonDataService.PERSON_DATA object static.

### Exercise 2

Update the existing `/persons/{lastName}/{firstName}` endpoint to return an appropriate RESTful response when the requested person does not exist in the list

- prove your results

# solution :
    1. When found return Person Object
    2. When not found then return HttpStatus.NOT_FOUND with no body

### Exercise 3

Write a RESTful API endpoint to retrieve a list of all people with a particular surname

- pay attention to what should be returned when there are no match, one match, multiple matches
- prove your results

# solution :

    1. When not found then return empty list
    2. When found one Person then return list with one person object
    3. When found nultiple then return persons list

### Exercise 4

Write a RESTful API endpoint to add a new value to the list

- pay attention to what should be returned when the record already exists
- prove your resutls

# solution :

    1. When record already exists then return HttpStatus.CONFLICT with no body
    2. When firstName or lastName or both empty then return HttpStatus.BAD_REQUEST with no body (Pl. Note: Not checking for blank as no requirement)
    3. When firstName and lastName valid then add Person and return person object.
