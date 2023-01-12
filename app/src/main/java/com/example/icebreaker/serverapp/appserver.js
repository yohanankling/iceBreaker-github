const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const firebase = require('firebase-admin');


// Parse request bodies as JSON
app.use(bodyParser.json());

// Fetch the service account key JSON file contents
// Replace this with the path to your service account key
const serviceAccount = require('./firebase.json');

// Initialize the app with a service account, granting admin privileges
firebase.initializeApp({
    credential: firebase.credential.cert(serviceAccount),
    databaseURL: 'https://icebreaker-c5542-default-rtdb.firebaseio.com'
});

// As an admin, the app has access to read and write all data, regardless of Security Rules
const db = firebase.firestore();

app.get('/', (req, res) => {
    // Get a list of all users in the "users" collection
    let email = req.query.email;
    let password = req.query.password;
    db.collection('Users').get()
        .then((snapshot) => {
            // Successfully retrieved data
            const users = [];
            snapshot.forEach((doc) => {
                users.push(doc.data());
            });
            let valid = false;
            for (let user of users) {
                    if (email === user["email"]){
                        if (password === user["password"]){
                            res.send("correct!");
                            valid = true;
                            break;
                        } else res.send("incorrect password!");
                        return;
                    }}
            if (!valid){res.send("no mail exist!");}
        })
        .catch((err) => {
            // An error occurred
            res.status(500).send(err);
        });
});

app.listen(3000, () => {
    console.log('App listening on port 3000');
});

// TO RUN:
// cd app\src\main\java\com\example\icebreaker\serverapp
// node appserver