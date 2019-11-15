// /https://us-central1-uncacampusbreeze.cloudfunctions.net/addMessage?text=uppercaseme
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

let db = admin.firestore();

exports.requestNewUid = functions.https.onCall(async (data) => {
	// create a new doc with a simple placeholder value of active status.
	let users = db.collection('users');
	let FieldValue = admin.firestore.FieldValue;
	await users.doc().set({
		active: false, // user is not currently active. We only created the profile.
		timeOfCreation: FieldValue.serverTimestamp()
	})
	.then((docRef) => {
		console.log("New user document created with ID: ", docRef.id);
		return {newUid: docRef.id};
	})
	.catch((error) => {
		console.error("Error creating a new user document: ", error)
	});
});