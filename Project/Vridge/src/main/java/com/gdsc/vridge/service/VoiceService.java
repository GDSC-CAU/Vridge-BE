package com.gdsc.vridge.service;

import static io.netty.resolver.HostsFileParser.parse;

import com.gdsc.vridge.dto.VoiceDto;
import com.gdsc.vridge.dto.VoiceDto.RecordInfo;
import com.gdsc.vridge.entity.VoiceEntity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class VoiceService {

    @Value("${app.firebase-bucket}")
    private String firebaseBucket;

    private Firestore firestore;

    private StorageClient storageClient;

    public ResponseEntity<List<VoiceEntity>> getVoiceList(String uid) {
        try {
            CollectionReference voiceCollectionRef = firestore.collection("users").document(uid).collection("voice");

            List<VoiceEntity> voiceList = new ArrayList<>();

            ApiFuture<QuerySnapshot> future = voiceCollectionRef.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                VoiceEntity voice = document.toObject(VoiceEntity.class);
                voiceList.add(voice);
            }

            return ResponseEntity.ok(voiceList);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<VoiceEntity> getVoice(String uid, String vid) {
        try {
            DocumentReference voiceRef = firestore.collection("users").document(uid).collection("voice").document(vid);
            DocumentSnapshot voiceSnapshot = voiceRef.get().get();

            if (voiceSnapshot.exists()) {
                VoiceEntity voice = voiceSnapshot.toObject(VoiceEntity.class);
                return ResponseEntity.ok(voice);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> uploadRecord(VoiceDto.Upload upload) {
        try {
            DocumentReference userRef = firestore.collection("users").document(upload.getUid());
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                userRef.update("recordVid", upload.getVid());
                userRef.update("recordIndex", upload.getIndex());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> deleteRecord(String uid) {
        try {
            DocumentReference userRef = firestore.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                String recordVid = userSnapshot.getString("recordVid");

                userRef.update("recordVid", "");
                userRef.update("recordIndex", 0);

                Iterable<Blob> blobs = storageClient.bucket(firebaseBucket).list().getValues();
                for (Blob blob : blobs) {
                    if (blob.getName().startsWith(uid + "/" + recordVid + "/")) {
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

    public ResponseEntity<RecordInfo> getRecord(String uid) {
        try {
            DocumentReference userRef = firestore.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            if (userSnapshot.exists()) {
                String recordVid = userSnapshot.getString("recordVid");
                int recordIndex = userSnapshot.getLong("recordIndex").intValue();

                VoiceDto.RecordInfo recordInfo = new RecordInfo(uid, recordVid, recordIndex);
                return ResponseEntity.ok(recordInfo);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> finishRecord(VoiceDto.Finish finish) {
        String uid = finish.getUid();
        String vid = finish.getVid();
        String name = finish.getName();
        int pitch = finish.getPitch();
        String language = finish.getLanguage();

        BufferedReader br = null;
        StringBuilder sb = null;

        try {
            DocumentReference userRef = firestore.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            String fcmToken = userSnapshot.getString("fcmToken");

            if (userSnapshot.exists()) {
                userRef.update("recordVid", "");
                userRef.update("recordIndex", 0);

                VoiceEntity voice = new VoiceEntity(vid, name, pitch, language, false);
                userRef.collection("voice").add(voice);

                // AI 목소리 생성 함수 실행
                String baseURL = "http://34.28.154.7/train?";

                String param = String.format("uid=%s&vid=%s", uid, vid);
                String fullURL = baseURL + "?" + param;

                URL url = new URL(fullURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(sb.toString());
                int exitCode = jsonResponse.getInt("exit_code");

                connection.disconnect();

                if (exitCode == 0){
                    DocumentReference vidRef = userRef.collection("voice").document(vid);
                    vidRef.update("status", true);

                    userRef.update("cntVoice", FieldValue.increment(1));

                    snedNotification(fcmToken, "finishRecord", "success");

                    return ResponseEntity.ok().build();
                }
                else {

                    snedNotification(fcmToken, "finishRecord", "fail");

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> synthesizeVoice(VoiceDto.Synthesize synthesize) {
        String uid = synthesize.getUid();
        String[] vids = synthesize.getVids();
        String vid1 = vids[0];
        String vid2 = vids[1];
        String name = synthesize.getName();
        int pitch = synthesize.getPitch();
        String language = synthesize.getLanguage();

        BufferedReader br = null;
        StringBuilder sb = null;

        try {
            DocumentReference userRef = firestore.collection("users").document(uid);
            DocumentSnapshot userSnapshot = userRef.get().get();

            String fcmToken = userSnapshot.getString("fcmToken");

            if (userSnapshot.exists()) {
                String newVid = UUID.randomUUID().toString().replace("-", "");
                VoiceEntity voice = new VoiceEntity(newVid, name, pitch, language, false);

                userRef.collection("voice").add(voice);

                // AI 합성 함수 실행
                String baseURL = "http://34.28.154.7/merge?";

                String param = String.format("uid1=%s&vid1=%s&vid2=%s&vid3=%s", uid, vid1, vid2, newVid);
                String fullURL = baseURL + "?" + param;

                URL url = new URL(fullURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                JSONObject jsonResponse = new JSONObject(sb.toString());
                int exitCode = jsonResponse.getInt("exit_code");

                connection.disconnect();

                if (exitCode == 0){
                    DocumentReference vidRef = userRef.collection("voice").document(newVid);
                    vidRef.update("status", true);

                    userRef.update("cntVoice", FieldValue.increment(1));

                    snedNotification(fcmToken, "synthesizeVoice", "success");

                    return ResponseEntity.ok().build();
                }
                else {

                    snedNotification(fcmToken, "synthesizeVoice", "fail");

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public String snedNotification(String token, String title, String body) {
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(token)
            .build();
        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send notification";
        }
    }

}
