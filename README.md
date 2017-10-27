# About the workshop

In this workshop we will explore what Firebase Functions are and setup project for writing these functions in Kotlin. To get there we will touch some other interesting topics like Firebase Realtime Database and TypeScript. 


### Prerequisites:

 * [Firebase account](https://console.firebase.google.com/)
 * Editor for JavaScript/TypeScript (my choice is [VS Code](https://code.visualstudio.com/))
 * [IntelliJ Idea](https://www.jetbrains.com/idea/download/) (Community edition is sufficient)


### What are we building:

> **The Ultimate Currency converter**
> 
> Let's make a backend for the ultimate currency convertion app - a user enters the price in euro and the backend converts it to other currencies.

The backend will work as follows: 
 * The client app pushes a new "conversion" object with a euro value into `conversions` table
 * A function gets triggered and picks up this object and calculates values in all other currencies
 * Data gets written back to the same object with a timestamp

Firebase Realtime Database structure:
``` js
{
    "rates" : {
        "usd" : 1.175,
        "czk" : 25.88,
        "pln" : 4.303,
        "rub" : 68.52
    },
    "conversions" : {
        "<hash1>" : {
            "eur" : 3.0
        },
        "<hash2>" : {
            "timestamp" : 1507570800,
            "eur" : 1.0,
            "usd" : 1.175,
            "czk" : 25.88,
            "pln" : 4.303,
            "rub" : 68.52
        }
    }
}
```


### Contents: 
 
 * [Part 1 - A basic project setup](part1.md)
 * [Part 2 - Sprinkle in some types](part2.md)
 * [Part 3 - Hello Kotlin](part3.md)
 * [Part 4 - Even more Kotlin](part4.md)
 * [Optional - A bit of auto-magic](part5.md)


### Disclaimer:

I am not a JavaScript expert, therefore the JS code here is far from optimal. Apologies to JS fans or experts that get eye-bleeds.