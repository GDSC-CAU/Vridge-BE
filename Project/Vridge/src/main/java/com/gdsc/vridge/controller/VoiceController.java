package com.gdsc.vridge.controller;

import com.gdsc.vridge.dto.VoiceDto;
import com.gdsc.vridge.entity.VoiceEntity;
import com.gdsc.vridge.service.VoiceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/voice")
public class VoiceController {

    private VoiceService voiceService;

    @Autowired
    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<VoiceEntity>> getVoiceList(@RequestParam String uid){
        return voiceService.getVoiceList(uid);
    }

    @GetMapping("/single")
    public ResponseEntity<VoiceEntity> getVoice(@RequestParam String uid, @RequestParam String vid){
        return voiceService.getVoice(uid, vid);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadRecord(@RequestBody VoiceDto.Upload upload){
        return voiceService.uploadRecord(upload);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteRecord(@RequestBody String uid){
        return voiceService.deleteRecord(uid);
    }

    @GetMapping("/record")
    public ResponseEntity getRecord(@RequestParam String uid) {
        return voiceService.getRecord(uid);
    }

    @PostMapping("/finish")
    public ResponseEntity<Void> finishRecord(@RequestBody VoiceDto.Finish finish){
        return voiceService.finishRecord(finish);
    }

    @PostMapping("/synthesize")
    public ResponseEntity<Void> synthesizeVoice(@RequestBody VoiceDto.Synthesize synthesize){
        return voiceService.synthesizeVoice(synthesize);
    }

}
