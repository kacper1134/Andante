import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";

// Initialize Firebase
const app = initializeApp({
  apiKey: "AIzaSyCNIwtyHXdG7jjwA6TYZ_ITRNzb4_inKeU",
  authDomain: "andante-362816.firebaseapp.com",
  projectId: "andante-362816",
  storageBucket: "andante-362816.appspot.com",
  messagingSenderId: "359172638030",
  appId: "1:359172638030:web:ee579bb0812f5df3484a1f"
});

// Firebase storage reference
const storage = getStorage(app);
export default storage;
