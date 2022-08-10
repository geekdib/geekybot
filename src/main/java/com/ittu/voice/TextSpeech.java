package com.ittu.voice;

import java.util.Locale;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import org.springframework.stereotype.Service;

@Service
public class TextSpeech {
  
	 Synthesizer synthesizer;
	
    public void speak(String cmd)
    {
        try {
            System.setProperty(
                "freetts.voices",
                "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
  
            Central.registerEngineCentral(
                "com.sun.speech.freetts"
                + ".jsapi.FreeTTSEngineCentral");
            

            synthesizer = Central.createSynthesizer(
                new SynthesizerModeDesc(Locale.US));
           
            synthesizer.allocate();
            synthesizer.resume();
            synthesizer.speakPlainText(
                cmd, null);
            synthesizer.waitEngineState(
                Synthesizer.QUEUE_EMPTY);
        }
  
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}