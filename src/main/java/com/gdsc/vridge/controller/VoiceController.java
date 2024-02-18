package com.gdsc.vridge.controller;

import com.gdsc.vridge.dto.SynthesizeVoicesDto;
import com.gdsc.vridge.dto.VoiceListDto;
import com.gdsc.vridge.dto.response.BooleanResponseDto;
import com.gdsc.vridge.dto.CreateTTSDto;
import com.gdsc.vridge.dto.response.VoiceIdResponseDto;
import com.gdsc.vridge.dto.response.VoiceListResponseDto;
import com.gdsc.vridge.dto.uploadRecordingDto;
import com.gdsc.vridge.service.VoiceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/voice")
@RequiredArgsConstructor
public class VoiceController {

    private final VoiceService voiceService;

    @PostMapping("/upload")
    public ResponseEntity<VoiceIdResponseDto> upload(@RequestBody uploadRecordingDto uploadRecordingDto) {
        String voiceId = voiceService.uploadRecording(uploadRecordingDto);
        VoiceIdResponseDto voiceIdResponseDto = VoiceIdResponseDto.builder()
            .voiceId(voiceId)
            .build();
        return ResponseEntity.ok(voiceIdResponseDto);
    }

    @PostMapping("/synthesize")
    public ResponseEntity<VoiceIdResponseDto> synthesize(@RequestBody SynthesizeVoicesDto synthesizeVoicesDto) {
        String newVoiceId = voiceService.synthesizeVoices(synthesizeVoicesDto);
        VoiceIdResponseDto voiceIdResponseDto = VoiceIdResponseDto.builder()
            .voiceId(newVoiceId)
            .build();
        return ResponseEntity.ok(voiceIdResponseDto);
    }

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
