## Part 3 - Hello Kotlin

 * Make sure that you have Kotlin JS installed (in `functions` sub-folder)
```
npm install --save kotlin
```

 * Open IntelliJ IDEA and create a new Kotlin project in the root folder. Use Kotlin(JavaScript) and Gradle in the wizard. As a result the folder `functions` should now be a subfolder of your Kotlin project. 

 * Add the following block to `build.gradle`:
``` groovy
compileKotlin2Js.kotlinOptions {
    moduleKind = "commonjs"
    outputFile = "functions/build/index.js"
}
```

 * Create file `src/main/kotlin/Index.kt` and write the function `testPush` in basic KotlinJS:
``` kotlin
external fun require(module: String): dynamic
external val exports: dynamic

val functions = require("firebase-functions")
val admin = require("firebase-admin")

data class Conversions(
    val eur: Float,
    val usd: Float? = null,
    val czk: Float? = null,
    val pln: Float? = null,
    val rub: Float? = null,
    val timestamp: Long? = null
)

fun main(args: Array<String>) {
    admin.initializeApp(functions.config().firebase)

    exports.testPush = functions.https.onRequest { req, res ->
        admin.database().ref("/conversions")
            .push(Conversions(req.query.eur.toString().toFloat()))
            .then { res.status(200).send("done") }
    }
}
```

 * Try to build and deploy your function:
``` sh
./gradlew build
firebase deploy --only function
```

It is possible that you get the error  `Error: Error parsing triggers: Cannot find module`.  If that is the case, run following commands to reset project dependencies:
``` sh
cd functions
npm install --save kotlin
npm install --save firebase-admin
npm install --save firebase-functions
```
And try to build-deploy again.

 * **Bonus:** add a task to build and deploy with one command:
``` groovy
import org.apache.tools.ant.taskdefs.condition.Os
task deploy(type:Exec, dependsOn: 'build') {
    def firebaseExecutable = Os.isFamily(Os.FAMILY_WINDOWS) ? "firebase.cmd" : "firebase"
    commandLine firebaseExecutable, "deploy"
}
```

 * Deploy and verify your functions:
 ```
 ./gradlew deploy
 ```


### Links:

 * https://kotlinlang.org/docs/tutorials/javascript/kotlin-to-javascript/kotlin-to-javascript.html
 * https://medium.com/@LuhmirinS/setting-up-firebase-functions-with-kotlin-1c34b2ca2427
 * https://kotlinlang.org/docs/reference/dynamic-type.html