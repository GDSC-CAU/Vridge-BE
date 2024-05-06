package com.gdsc.vridge.service;

import com.gdsc.vridge.dto.TTSDto;
import com.gdsc.vridge.entity.TTSEntity;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TTSService {

    public ResponseEntity<TTSEntity> createTTS(TTSDto ttsDto) {
        String uid = ttsDto.getUid();
        String vid = ttsDto.getVid();
        String tid = ttsDto.getTid();
        String text = ttsDto.getText();
        int pitch = ttsDto.getPitch();
        Date timestamp = new Date(ttsDto.getTimestamp());

        BufferedReader br = null;
        StringBuilder sb = null;

        Firestore db = FirestoreClient.getFirestore();

        try {
            DocumentReference userRef = db.collection("users").document(uid);
            DocumentReference voiceRef = userRef.collection("voice").document(vid);

            TTSEntity tts = new TTSEntity(tid, text, timestamp, false);
            voiceRef.collection("tts").add(tts);

            // AI TTS 함수 실행
            String baseURL = "http://34.28.154.7/tts?";

            String param = String.format("uid=%s&vid=%s&tid=%s&text=%s&pitch=%d", uid, vid, tid, text, pitch);
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
                DocumentReference tidRef = voiceRef.collection("tts").document(tid);
                tidRef.update("status", true);

                return ResponseEntity.ok(tts);
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<TTSEntity>> getTTSList(String uid, String vid) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            CollectionReference ttsCollectionRef = db.collection("users").document(uid)
                .collection("voice").document(vid)
                .collection("tts");

            List<TTSEntity> ttsList = new ArrayList<>();

            ApiFuture<QuerySnapshot> future = ttsCollectionRef.get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                TTSEntity tts = document.toObject(TTSEntity.class);
                ttsList.add(tts);
            }

            return ResponseEntity.ok(ttsList);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
