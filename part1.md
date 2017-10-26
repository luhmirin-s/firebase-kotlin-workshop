## Part 1 - A basic project setup


In this part you will create very simple Firebase Functions project with two functions: 
* `testPush` - function with `https` trigger that will be used to simulate client requests that write data into Realtime Database.
* `convert` - function with database `onWrite` trigger that will do conversion and write data back into database.


### Steps:

 * Create a project in the Firebase console
```
https://console.firebase.google.com/
```
 
 * Put the rates node into the database:
 ``` js
"rates" : {
    "usd" : 1.175,
    "czk" : 25.88,
    "pln" : 4.303,
    "rub" : 68.52
}
 ```
 
 * Install and set up the Firebase CLI tools (if not yet done):
``` sh
npm install -g firebase-tools
firebase login
```

 * Initialize the Firebase project:
``` sh
firebase init functions
```

 * Open `index.js` (this is where functions are added) and write `testPush` function for later use:
 ``` js
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.testPush = functions.https.onRequest((req, res) => {
    admin.database().ref('/conversions')
        .push({ eur: parseFloat(req.query.eur) })
        .then(snap => { res.status(200).send("done") })
})
 ```

 * Deploy this function with the CLI tools:
``` sh
firebase deploy --only functions
```

 * Trigger the test function to check if it works by calling (project link should be available in console): 
```
https://<project-link>/testPush?eur=100
```

 * Add the function that does the conversion:
``` js
exports.convert = functions.database.ref('/conversions/{conversionID}').onWrite(event => {
    const conversion = event.data.val()
    // Do not handle already converted values
    if (conversion.timestamp !== undefined) return

    const eur = conversion.eur
    admin.database().ref('/rates').once('value').then(ratesSnapshot => {
        const rates = ratesSnapshot.val()
        const result = {
            eur: eur,
            usd: eur * rates.usd,
            czk: eur * rates.czk,
            pln: eur * rates.pln,
            rub: eur * rates.rub,
            timestamp: Date.now()
        }
        admin.database().ref('/conversions').child(event.params.conversionID).set(result)
    })
})
```


### Conclusions:

By now you should have at least a basic understanding how Firebase Functions are implemented. But there is a small issue - JavaScript is very susceptible to typos and `undefined is not a function` type errors. In case of `convert` function typo in `timestamp` check might cause infinite recursive function calls and cost you a lot of money. 

To make it a bit more safe, in [Part 2](part2.md) we are going to introduce some type safety.


### Links: 

 * https://firebase.google.com/docs/functions/get-started
 * https://firebase.google.com/docs/functions/http-events
 * https://firebase.google.com/docs/functions/database-events
 * https://github.com/firebase/functions-samples