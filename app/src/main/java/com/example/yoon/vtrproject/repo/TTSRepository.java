package com.example.yoon.vtrproject.repo;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

/**
 * TODO: Edit Description
 */
public class TTSRepository {
    public static final String TAG_ID = "report_speak_tag_id";
    public static final String TEXT_ID = "report_speak_text_id";
    private TextToSpeech speaker;
    private SpeakCallback callback;
    private Handler handler = new Handler(Looper.getMainLooper());

    private static class HOLDER {
        public static final TTSRepository instance = new TTSRepository();
    }

    public synchronized static TTSRepository get() {
        return TTSRepository.HOLDER.instance;
    }

    public void setCallback(SpeakCallback callback) {
        this.callback = callback;
    }

    public void init(Context context) {
        speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    speaker.setLanguage(Locale.KOREAN);
                    speaker.setOnUtteranceProgressListener(listener);
                    speaker.setSpeechRate(0.8f);    // slower
                }
            }
        });
    }

    public void destroy() {
        speaker.stop();
        speaker.shutdown();
        speaker = null;
    }

    public boolean isSpeaking() {
        return speaker != null && speaker.isSpeaking();
    }

    public void speakTag(String text) {
        speaker.speak(text, TextToSpeech.QUEUE_ADD, null, TAG_ID);
    }

    public void speakText(String text) {
        speaker.speak(text, TextToSpeech.QUEUE_ADD, null, TEXT_ID);
    }
    

    public void stopSpeak() {
        speaker.stop();
    }

    private UtteranceProgressListener listener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStart();
                }
            });
        }

        @Override
        public void onDone(String utteranceId) {
            if (utteranceId.equals(TEXT_ID)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCompleted();
                    }
                });
            }
        }

        @Override
        public void onError(String utteranceId) {
        }
    };
}

