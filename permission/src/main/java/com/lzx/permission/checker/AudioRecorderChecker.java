package com.lzx.permission.checker;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by Lzx on 2017/10/27.
 */

public class AudioRecorderChecker implements IChecker {
    @Override
    public boolean check(Context context, String permission) {
        return checkAudioRecordPermission();
    }

    private static boolean checkAudioRecordPermission() {
        AudioRecord mAudioRecord = null;
        int frequency = 44100;
        int bufferSize = 0;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        try {
            bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,frequency,
                    channelConfiguration, audioEncoding, bufferSize);

            if (mAudioRecord == null) {
                return false;
            }
            mAudioRecord.startRecording();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (mAudioRecord != null) {
                    mAudioRecord.release();
                }
            } catch (Exception e1) {
            }
            return false;
        }

        int readSize = 0;
        if (bufferSize > 0 && mAudioRecord != null) {
            byte[] bytes = new byte[bufferSize];
            readSize = mAudioRecord.read(bytes, 0, bytes.length);
            try {
                mAudioRecord.stop();
                mAudioRecord.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return readSize > 0;
    }
}
