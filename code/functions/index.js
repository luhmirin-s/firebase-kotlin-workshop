const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.testPush = functions.https.onRequest((req, res) => {
    admin.database().ref('/conversions')
        .push({ eur: parseFloat(req.query.eur) })
        .then(snap => { res.status(200).send("done") })
})

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