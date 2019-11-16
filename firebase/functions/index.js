// /https://us-central1-uncacampusbreeze.cloudfunctions.net/addMessage?text=uppercaseme
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
let db = admin.firestore();

exports.requestNewUid = functions.https.onCall(async (data) => {
	// create a new doc with a simple placeholder value of active status.
	let users = db.collection('users');
	let FieldValue = admin.firestore.FieldValue;
	return await users.add({
		active: false, // user is not currently active. We only created the profile.
		timeOfCreation: FieldValue.serverTimestamp()
	})
	.then((docRef) => {
		console.log("New user document created with ID: ", docRef.id);
		return docRef.id;
	})
	.catch((error) => {
		console.error("Error creating a new user document: ", error)
	});
});

exports.requestToken = functions.https.onCall((data) => {
	let uid = data.uid;
	let docRefWithUid = db.collection('users').doc(uid);
	let validUid = false;

	docRefWithUid.get()
		.then((docSnapshot) => {
			if (docSnapshot.exists) {
				validUid = true;
			} else {
				validUid = false;
			}
		})
		.catch((error) => {
			console.error(error);
		});

	

	docRefWithUid.get()
		.then((docSnapshot) => {
			if (docSnapshot.exists) {
				admin.auth().createCustomToken(uid)
					.then(function(customToken) {
						return customToken;
					})
					.catch(function(error) {
						console.log('Error creating custom token:', error);
					});
			} else {
				console.error('uid does not exist.', error);
			}
		})
		.catch(function(error) {
			console.log("Uh I dunno.")
		});
});