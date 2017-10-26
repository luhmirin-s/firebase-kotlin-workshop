import bindings.Admin
import bindings.Date
import bindings.Functions
import domain.Conversions
import domain.Rates
import domain.convert
import domain.isNotNew

external val exports: dynamic

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