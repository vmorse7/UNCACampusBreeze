// /https://us-central1-uncacampusbreeze.cloudfunctions.net/addMessage?text=uppercaseme
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
let db = admin.firestore();

/*
	What does exports.* mean? -> https://developer.mozilla.org/en-US/docs/web/javascript/reference/statements/export

	This function will be called by the app when it does not have any user associated with the 
	app instance. It will simple return a UUID to the device that will then store it. 
	Yes. It is not a very secure way of id ing, but for this particular app it will work fine.
*/     
exports.registerNewDevice = functions.https.onCall((data, context) => {
	let newDeviceDocRef = db.collection('devices').doc();
	let newId = newDeviceDocRef.id;
	return {id: newId}; // I think this would work... lol. Still learning.
}); 













// let docRef = db.collection('users').doc('alovelace');










// // let setAda = docRef.set({
// //   first: 'Ada',
// //   last: 'Lovelace',
// //   born: 1815
// // });
  

// // Take the text parameter passed to this HTTP endpoint and insert it into the
// // Realtime Database under the path /messages/:pushId/original
// exports.addMessage = functions.https.onRequest(async (req, res) => {
// 	// Grab the text parameter.
// 	const original = req.query.text;
// 	// Push the new message into the Realtime Database using the Firebase Admin SDK.
	
// 	const snapshot = await admin.firestore().ref('/messages').push({original: original});
// 	// Redirect with 303 SEE OTHER to the URL of the pushed object in the Firebase console.
// 	res.redirect(303, snapshot.ref.toString());
//   });






// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });