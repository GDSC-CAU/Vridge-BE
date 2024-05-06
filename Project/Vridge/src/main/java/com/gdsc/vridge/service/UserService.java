package com.gdsc.vridge.service;

import com.gdsc.vridge.dto.UserDto;
import com.gdsc.vridge.dto.UserDto.UserInfo;
import com.gdsc.vridge.entity.UserEntity;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    private FirebaseAuth firebaseAuth;

    private StorageClient storageClient;


    public ResponseEntity<Void> loginUser(UserDto.Login login) {
        String token = login.getToken();
        String fcmToken = login.getFcmToken();

        Firestore db = FirestoreClient.getFirestore();

        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(token);
            String uid = decodedToken.getUid();

            DocumentReference userRef = db.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                return ResponseEntity.ok().build();
            } else {
                String name = decodedToken.getName();
                String email = decodedToken.getEmail();

                UserEntity user = new UserEntity(uid, name, email, 0, "", 0, fcmToken);
                userRef.set(user);
                return ResponseEntity.ok().build();
            }
        } catch (FirebaseAuthException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> updateUserFcmToken(UserDto.FcmToken fcmToken) {
        String uid = fcmToken.getUid();
        String newFcmToken = fcmToken.getFcmToken();

        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference userRef = db.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                userRef.update("fcmToken", newFcmToken);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> deleteUser(String uid) {

        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference userRef = db.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                userRef.delete();

                Iterable<Blob> blobs = storageClient.bucket(firebaseBucket).list().getValues();
                for (Blob blob : blobs) {
                    if (blob.getName().startsWith(uid + "/")) {
                        blob.delete();
                    }
                }

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<UserInfo> getUser(String uid) {

        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference userRef = db.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                String name = userSnapshot.getString("name");
                String email = userSnapshot.getString("email");
                int cntVoice = userSnapshot.getLong("cntVoice").intValue();

                UserDto.UserInfo userInfo = new UserInfo(uid, name, email, cntVoice);
                return ResponseEntity.ok(userInfo);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}