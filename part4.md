## Part 4 - Even more Kotlin

 * Create the file `Admin.kt` in `src/main/kotlin/bindings`.

 * Write a basic JS binding to remove the `require` call and abstract away the module import:
``` kotlin
@JsModule("firebase-admin")
@JsNonModule
external object Admin {
    fun initializeApp(config: dynamic)
    fun database(): dynamic
}
```

 * In a similar way add another binding in `Functions.kt`:
``` kotlin
@JsModule("firebase-functions")
@JsNonModule
external object Functions {
    fun config(): dynamic
    val https: dynamic
}
```

 * Also rewrite some dynamic code in the main function with the newly written bindings:
``` kotlin
fun main(args: Array<String>) {
    Admin.initializeApp(Functions.config().firebase)

    exports.testPush = Functions.https.onRequest { req, res ->
        Admin.database().ref("/conversions")
                .push(InputConversions(req.query.eur.toString().toFloat()))
                .then { res.status(200).send("done") }
    }
}
```

 * Build, deploy and make sure it still works.

 * Copy the rest of my custom bindings from the `bindings` folder of this repo to `src/main/kotlin/bindings` 

 * **Optional:** Examine copied bindings to see how they represents their respective JavaScript APIs.

 * Rewrite the database table types to Kotlin:
``` kotlin
data class Rates(
    val usd: Float,
    val czk: Float,
    val pln: Float,
    val rub: Float
)

data class Conversions(
    val eur: Float,
    val usd: Float? = null,
    val czk: Float? = null,
    val pln: Float? = null,
    val rub: Float? = null,
    val timestamp: Long? = null
)
```

 * Rewrite the function `convert` to Kotlin:
``` kotlin
exports.convert = Functions.database.ref("/conversions/{conversionID}").onWrite<Conversions> { event ->
    val input = event.data.`val`()
    if (input.timestamp != null) return@onWrite
    Admin.database().ref("/rates").once<Rates>("value")
        .then<Conversions> { ratesSnap ->
            ratesSnap.`val`().let {
                Conversions(
                    eur = input.eur,
                    usd = input.eur * it.usd,
                    czk = input.eur * it.czk,
                    pln = input.eur * it.pln,
                    rub = input.eur * it.rub,
                    timestamp = Date.now()
                )
            }
        }
        .then { result ->
            Admin.database().ref("/conversions")
                .child(event.params.conversionID)
                .set(result)
        }
}
```

 * Build and deploy to verify that it still works.

### And a bit more of Kotlin

 * Move data classes to a separate package e.g. `domain`

 * Add functions `inNew()` and `convert(Rates, Long)` to `Conversions.kt`:
``` kotlin
fun Conversions.isNew() = timestamp == null

fun Conversions.convert(rates: Rates, timestamp: Long) = Conversions(
    eur = eur,
    usd = eur * rates.usd,
    czk = eur * rates.czk,
    pln = eur * rates.pln,
    rub = eur * rates.rub,
    timestamp = timestamp
)
```

 * Make use of the new `Conversions` code:
``` kotlin
fun main(args: Array<String>) {
    Admin.initializeApp(Functions.config().firebase)
    val database = Admin.database()

    exports.testPush = Functions.https.onRequest { req, res ->
        database.ref("/conversions")
            .push(Conversions(req.query.eur.toString().toFloat()))
            .then { res.status(200).send("done") }
    }

    exports.convert = Functions.database.ref("/conversions/{conversionID}").onWrite<Conversions> { event ->
        event.data.`val`().takeIf { it.isNew() }?.let { input ->
            database.ref("/rates").once<Rates>("value")
                .then<Conversions> { it.`val`().let { rates -> input.convert(rates, Date.now()) } }
                .then { conversions ->
                    database.ref("/conversions")
                        .child(event.params.conversionID)
                        .set(conversions)
                }
        }
    }
}
```

### Links: 

 * http://kotlinlang.org/docs/reference/js-interop.html
 * https://firebase.google.com/docs/reference/js/firebase
 * https://firebase.google.com/docs/reference/functions/functions.https