package com.gdsc.vridge.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final FirebaseAuth firebaseAuth;
    private final Firestore firestore = FirestoreClient.getFirestore();

    public boolean loginUser(String firebaseAuthToken) {
        try {
            // Firebase Auth 토큰 검증
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseAuthToken);
            String uid = decodedToken.getUid();

            // Firestore에 해당 사용자 정보가 있는지 확인
//            ApiFuture<DocumentSnapshot> future = firestore.collection("users").document(uid).get();
//            DocumentSnapshot document = future.get();

            DocumentReference userRef = firestore.collection("users").document(uid);
            ApiFuture<DocumentSnapshot> future = userRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // 사용자 정보가 있으면 로그인
                logger.info("User logged in: {}", uid);
                return true;
            } else {
                // 사용자 정보가 없으면 Firestore DB에 새 사용자 정보를 추가하고 회원가입 처리
                logger.info("New user signed up: {}", uid);
                addUser(uid);
                return true;
            }
        } catch (FirebaseAuthException | InterruptedException | ExecutionException e) {
            logger.error("Error while logging in:", e);
            return false;
        }
    }

    // Firestore DB에 새 사용자 정보를 추가
    private void addUser(String uid) throws ExecutionException, InterruptedException {
        firestore.collection("users").document(uid).set(null); // null
    }

    public boolean deleteUser(String uid) {
        try {
            // Firestore에서 사용자 정보 삭제
            firestore.collection("users").document(uid).delete();

            // Firebase Auth에서 사용자 삭제
            firebaseAuth.deleteUser(uid);
            logger.info("User deleted: {}", uid);
            return true;
        } catch (FirebaseAuthException e) {
            logger.error("Error while deleting user:", e);
            return false;
        }
    }
}