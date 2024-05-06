package com.gdsc.vridge.service;

import com.gdsc.vridge.dto.CreateTTSDto;
import com.gdsc.vridge.dto.SynthesizeVoicesDto;
import com.gdsc.vridge.dto.uploadRecordingDto;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoiceService {
    private static final Logger logger = LoggerFactory.getLogger(VoiceService.class);

    public Boolean uploadRecording(uploadRecordingDto uploadRecordingDto) {

        String uid = uploadRecordingDto.getUid();
        String vid = uploadRecordingDto.getVid();

        // AI 서버의 목소리 생성 함수 실행
        HttpURLConnection connection = null;
        try {
            String baseURL = "http://34.133.49.11:5000/train?";

            String param = String.format("uid1=%s&vid1=%s",
                uid, vid);
            String fullURL = baseURL + "?" + param;

            URL url = new URL(fullURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();

            // HTTP 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // http://localhost:5000/merge?uid1=123&vid1=456&vid2=789&vid3=1011
    public String synthesizeVoices(SynthesizeVoicesDto synthesizeVoicesDto) {

        String uid = synthesizeVoicesDto.getUid();
        String[] vids = synthesizeVoicesDto.getVids();
        String vid_1 = vids[0];
        String vid_2 = vids[1];

        // 새 목소리 ID 생성
        String newVid = UUID.randomUUID().toString().replace("-", "");

        // AI 서버의 목소리 합성 함수 실행
        HttpURLConnection connection = null;
        try {
            String baseURL = "http://34.133.49.11:5000/merge?";

            String param = String.format("uid1=%s&vid1=%s&vid2=%s&vid3=%s",
                uid, vid_1, vid_2, newVid);
            String fullURL = baseURL + "?" + param;

            URL url = new URL(fullURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();

            // HTTP 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return newVid;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public boolean createTTS(CreateTTSDto createTTSDto) {

        String text = createTTSDto.getText();
        String ttsId = createTTSDto.getTtsId();
        String uid = createTTSDto.getUid();
        String vid = createTTSDto.getVid();
        int pitch = createTTSDto.getPitch();

        // AI 서버의 TTS 함수 실행
        HttpURLConnection connection = null;
        try {
            String baseURL = "http://34.133.49.11:5000/tts?";
            String tts = URLEncoder.encode(text, "UTF-8");

            String param = String.format("tts=%s&ttsid=%s&uid=%s&vid=%s&pitch=%d",
                tts, ttsId, uid, vid, pitch);
            String fullURL = baseURL + "?" + param;

            URL url = new URL(fullURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            connection.connect();

            // HTTP 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}