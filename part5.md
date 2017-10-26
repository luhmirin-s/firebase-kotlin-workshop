## Optional: A bit of auto-magic

* Instal ts2kt from npm - `npm install -g ts2kt`. This utility can be used to automatically convert TypeScript definitions to kotlin bindings. Some libraries have TypeScript definitions bundled in, while many others can be found in the DefinitelyTyped repo.

 * Check `functions/node_modules/firebase-admin/lib` for `.d.ts` files. 

 * Try to convert `index.d.ts` to Kotlin:
 ``` sh
ts2kt functions/node_modules/firebase-admin/lib/index.d.ts
 ```

 * Examine the generated files. Those are helpful to understand how ts2kt works, but unfortunately generated code requires a lot of polishing.


### Links: 

 * https://github.com/Kotlin/ts2kt
 * http://definitelytyped.org/
 * https://kotlinlang.org/docs/reference/js-modules.html