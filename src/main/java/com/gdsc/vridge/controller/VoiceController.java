package com.gdsc.vridge.controller;

import com.gdsc.vridge.dto.SynthesizeVoicesDto;
import com.gdsc.vridge.dto.response.BooleanResponseDto;
import com.gdsc.vridge.dto.CreateTTSDto;
import com.gdsc.vridge.dto.response.VoiceIdResponseDto;
import com.gdsc.vridge.dto.uploadRecordingDto;
import com.gdsc.vridge.service.VoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

//    http://localhost:8080/api/v1/voice/upload
    @PostMapping("/upload")
    public ResponseEntity<BooleanResponseDto> upload(@RequestBody uploadRecordingDto uploadRecordingDto) {
        boolean uploadResult = voiceService.uploadRecording(uploadRecordingDto);
        BooleanResponseDto booleanResponseDto = BooleanResponseDto.builder()
            .success(uploadResult)
            .build();
        if (uploadResult) {
            return ResponseEntity.ok(booleanResponseDto);
        } else {
            return ResponseEntity.badRequest().body(booleanResponseDto);
        }
    }

//    http://localhost:8080/api/v1/voice/synthesize
    @PostMapping("/synthesize")
    public ResponseEntity<VoiceIdResponseDto> synthesize(@RequestBody SynthesizeVoicesDto synthesizeVoicesDto) {
        String newVoiceId = voiceService.synthesizeVoices(synthesizeVoicesDto);
        VoiceIdResponseDto voiceIdResponseDto = VoiceIdResponseDto.builder()
            .vid(newVoiceId)
            .build();
        if (newVoiceId != null) {
            return ResponseEntity.ok(voiceIdResponseDto);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

//    http://localhost:8080/api/v1/voice/create
    @PostMapping("/create")
    public ResponseEntity<BooleanResponseDto> create(@RequestBody CreateTTSDto createTTSDto) {
        boolean createResult = voiceService.createTTS(createTTSDto);
        BooleanResponseDto booleanResponseDto = BooleanResponseDto.builder()
            .success(createResult)
            .build();
        if (createResult) {
            return ResponseEntity.ok(booleanResponseDto);
        } else {
            return ResponseEntity.badRequest().body(booleanResponseDto);
        }
    }
}
