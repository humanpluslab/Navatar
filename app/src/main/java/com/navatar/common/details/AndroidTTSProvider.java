package com.navatar.common.details;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.navatar.common.TextToSpeechProvider;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AndroidTTSProvider implements TextToSpeechProvider, TextToSpeech.OnInitListener {

    private final static String TAG = AndroidTTSProvider.class.getSimpleName();

    private WeakReference<Context> mContext;

    private final TextToSpeech mTextToSpeech;

    private boolean initialized = false;

    @Inject
    public AndroidTTSProvider(Context context) {
        mContext = new WeakReference<>(context);
        mTextToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        initialized = status == TextToSpeech.SUCCESS;
    }

    @Override
    public void speak(String text) {
        if (initialized)
            Log.i(TAG, text);
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, "navatar-tts");
    }

    @Override
    public void speak(int resource) {
        Context context = mContext.get();
        if (context != null && initialized) {
            String str = context.getResources().getString(resource);
            speak(str);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mTextToSpeech.shutdown();
    }
}
