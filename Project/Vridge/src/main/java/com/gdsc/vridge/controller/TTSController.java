package com.gdsc.vridge.controller;

import com.gdsc.vridge.dto.TTSDto;
import com.gdsc.vridge.entity.TTSEntity;
import com.gdsc.vridge.service.TTSService;
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
@RequestMapping("/api/v1/tts")
public class TTSController {

    private TTSService ttsService;

    @Autowired
    public TTSController(TTSService ttsService) {
        this.ttsService = ttsService;
    }

    @PostMapping("/create")
    public ResponseEntity<TTSEntity> createTTS(@RequestBody TTSDto ttsDto){
        return ttsService.createTTS(ttsDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<TTSEntity>> getTTSList(@RequestParam String uid, @RequestParam String vid){
        return ttsService.getTTSList(uid, vid);
    }

}
