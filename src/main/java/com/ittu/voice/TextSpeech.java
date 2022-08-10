package com.ittu.voice;

import org.springframework.stereotype.Service;

@Service
public class TextSpeech {
  
    public void speak(String cmd)
    {
    	TextToSpeech tts = new TextToSpeech();
		tts.setVoice("dfki-poppy-hsmm");
		tts.speak(cmd, 2.0f, true, true);
    }
    
    
}