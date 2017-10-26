## Part 2 - Sprinkle in some types


In this part you will rewrite functions from previous part to TypeScript to get some easy write/compile time safety.


### Steps:

 * Make sure that TypeScript is installed:
 ```
 npm install -g typescript
 ```
 
 * Add configuration to `package.json` (note dependencies and scripts):
 ``` js
 {
  "name": "functions",
  "description": "Cloud Functions for Firebase",
  "dependencies": {
    "firebase-admin": "~5.4.0",
    "firebase-functions": "^0.7.0"
  },
  "devDependencies": {
    "typescript": "^2.3.2"
  },
  "scripts": {
    "build": "tsc",
    "deploy": "tsc && firebase deploy --only functions"
  },
  "main": "build/index.js",
  "private": true
}
  ```

 * Add `tsconfig.json` in the same folder as `package.json`:
``` js
 {
    "compilerOptions": {
        "lib": [
            "es6",
            "es2015.promise"
        ],
        "module": "commonjs",
        "noImplicitAny": false,
        "outDir": "build",
        "sourceMap": true,
        "target": "es6"
    },
    "include": [
        "src/**/*.ts"
    ]
}
```

 * Create `src/index.ts` file with Firebase imports:
``` js
import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'

admin.initializeApp(functions.config().firebase)
```

 * Add basic class definitions for tables in the database: 
``` ts
class Rates {
    usd: number
    czk: number
    pln: number
    rub: number
}

class Conversions extends Rates {
    constructor(eur: number) {
        super()
        this.eur = eur
    }

    eur: number
    timestamp: number
}
```

 * Translate the function `testPush` to TypeScript:
``` ts
export let testPush = functions.https.onRequest((req, res) => {
    admin.database().ref('/conversions')
        .push(new Conversions(parseFloat(req.query.eur)))
        .then(snap => { res.status(200).send("done") })
})
```

 * Build project and check out `build/index.js`:
``` sh
npm run-script build
```
 
 * Deploy and verify that all fuctions work the same as before:
``` sh
npm run-script deploy
```

 * Translate the function `convert` to TypeScript:
``` ts
export let convert = functions.database.ref('/conversions/{conversionID}').onWrite(event => {
    var conversion = event.data.val() as Conversions
    // Do not handle already converted values
    if (conversion.timestamp !== undefined) return

    var eur = conversion.eur
    admin.database().ref('/rates').once('value').then(ratesSnapshot => {
        let rates = ratesSnapshot.val() as Rates

        let result = new Conversions(eur)
        result.usd = eur * rates.usd
        result.czk = eur * rates.czk
        result.pln = eur * rates.pln
        result.rub = eur * rates.rub
        result.timestamp = Date.now()

        admin.database().ref('/conversions').child(event.params.conversionID).set(result)
    })
})
```


### Conclusions: 

Now if you accidentally make a typo, it will not transpile or even better - editor will highlight an error. Also in more advanced editors you get different levels of code completion that highly improve productivity.

Since we have implemented type safety, [let’s go a step further and use Kotlin](part3.md). It will not only provide same benefits as TypeScript, but also allow code reuse between platforms.


### Links

* https://www.typescriptlang.org/docs/home.html
* https://github.com/firebase/functions-samples/tree/master/typescript-getting-started
* https://kotlinlang.org/docs/reference/multiplatform.html