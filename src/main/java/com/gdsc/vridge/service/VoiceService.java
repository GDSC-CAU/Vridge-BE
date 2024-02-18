package com.gdsc.vridge.service;

import com.gdsc.vridge.dto.CreateTTSDto;
import com.gdsc.vridge.dto.SynthesizeVoicesDto;
import com.gdsc.vridge.dto.response.VoiceListResponseDto;
import com.gdsc.vridge.dto.uploadRecordingDto;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoiceService {
    private static final Logger logger = LoggerFactory.getLogger(VoiceService.class);

    private final Firestore firestore = FirestoreClient.getFirestore();

    public String uploadRecording(uploadRecordingDto uploadRecordingDto) {

        // 새 목소리 ID 생성
        String voiceId = UUID.randomUUID().toString().replace("-", "");

        ///////////
        // Firebase Storage 내부에 ID(voidId)값으로 폴더 만들고, 업로드된 WAV 파일 저장
        // AI 서버의 목소리 생성 함수 실행
        //////////

        return voiceId;
    }

    public String synthesizeVoices(SynthesizeVoicesDto synthesizeVoicesDto) {
        try {
            String uid = synthesizeVoicesDto.getUid();
            String[] voiceIds = synthesizeVoicesDto.getVoiceIds();

            // 새 목소리 ID 생성
            String newVoiceId = UUID.randomUUID().toString().replace("-", "");

            //////////////
            // AI 서버의 목소리 합성 함수 실행
            //////////////

            return newVoiceId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to synthesize voices", e);
        }
    }

    public boolean createTTS(CreateTTSDto createTTSDto) {
        try {
            //////////////
            // AI 서버의 TTS 함수 실행
            /////////////

            logger.info("TTS 생성 요청");
            logger.info("uid: {}", createTTSDto.getUid());
            logger.info("voidId: {}", createTTSDto.getVoiceId());
            logger.info("text: {}", createTTSDto.getText());
            return true;
        } catch (Exception e) {
            logger.error("TTS 생성 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
