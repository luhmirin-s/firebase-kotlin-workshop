import * as functions from 'firebase-functions'
import * as admin from 'firebase-admin'

admin.initializeApp(functions.config().firebase)

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

export let testPush = functions.https.onRequest((req, res) => {
    admin.database().ref('/conversions')
        .push(new Conversions(parseFloat(req.query.eur)))
        .then(snap => { res.status(200).send("done") })
})

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