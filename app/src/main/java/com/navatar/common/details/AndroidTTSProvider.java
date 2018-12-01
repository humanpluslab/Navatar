package com.navatar.common.details;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.navatar.common.TextToSpeechProvider;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

public class AndroidTTSProvider implements TextToSpeechProvider, TextToSpeech.OnInitListener {

    private WeakReference<Context> mContext;

    private final TextToSpeech mTextToSpeech;

    @Inject
    public AndroidTTSProvider(Context context) {
        mContext = new WeakReference<>(context);
        mTextToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void speak(String text) {
        mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    public void speak(int resource) {
        Context context = mContext.get();
        if (context != null) {
            String str = context.getResources().getString(resource);
            mTextToSpeech.speak(str, TextToSpeech.QUEUE_ADD, null);
        }
    }


}
