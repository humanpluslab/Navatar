package com.navatar.common.details;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.navatar.common.TextToSpeechProvider;

import javax.inject.Inject;

public class AndroidTTSProvider implements TextToSpeechProvider, TextToSpeech.OnInitListener {


    private final TextToSpeech mTextToSpeech;

    @Inject
    public AndroidTTSProvider(Context context) {
        mTextToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void speak(String text) {
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }


}
