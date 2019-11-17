// /https://us-central1-uncacampusbreeze.cloudfunctions.net/addMessage?text=uppercaseme
let functions = require('firebase-functions');
let admin = require('firebase-admin');

admin.initializeApp();

let db = admin.firestore();

exports.createNewAccount = functions.https.onCall(async (data) => {
	// create a new doc with a simple placeholder value of active status.
	let users = db.collection('users');
	let FieldValue = admin.firestore.FieldValue;
	return await users.add({
		displayName: 'AnonymousBulldog',
		isActive: false, // user is not currently active. We only created the profile.
		timestampOfCreation: FieldValue.serverTimestamp()
	})
	.then((docRef) => {
		// console.log("New user document created with ID: ", docRef.id);
		uid = docRef.id;
		return uid;
	})
	.catch((error) => {
		throw new functions.https.HttpsError('server-error-registering-account', 'Function must be provided with a registered uid.');
	});
});

exports.getCustomToken = functions.https.onCall((data) => {
	const uid = data.uid;
	console.log('getCustomToken cloud function is now attempting to generate a custom token for uid ' + uid);

	// check if the uid that the device provided even exists...
	
	return db.collection('users').doc(uid).get()
		.then((documentSnapshot) => {
			if (documentSnapshot.exists) { // a registered uid was passed.
				let customToken =  createCustomToken(uid);
				return {
					customToken: customToken
				};
			} else { // invalid uid was passed
				console.error('A user doc with the uid ' + uid + ' dne.');
				throw new functions.https.HttpsError('client-error-invalid-uid', 'Function must be provided with a registered uid.');
			}
		});
});

function createCustomToken(uid) {
	try {
		// let customToken = await admin.auth().createCustomToken(uid);
		let customToken = admin.auth().createCustomToken(uid);
		return customToken;
	}
	catch (error) {
		throw new functions.https.HttpsError('server-error-custom-token', "Error creating custom token: " + error);
	}
}