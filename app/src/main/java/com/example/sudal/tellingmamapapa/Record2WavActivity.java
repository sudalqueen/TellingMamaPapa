package com.example.sudal.tellingmamapapa;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Record2WavActivity extends AppCompatActivity {

    StorageReference storageRef;
    StorageReference audioRef;

    private int mAudioSource = MediaRecorder.AudioSource.MIC;
    private int mSampleRate = 44100;
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO;
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat);

    public AudioRecord mRecorder = null;

    public Thread recordingThread = null;
    public boolean isRecording = false;

    public AudioTrack mPlayer = null;
    public Thread mPlayThread = null;
    public boolean isPlaying = false;

    public Button recordBtn = null;
    public Button playBtn = null;

    public TextView title;
    public ImageView imageview;

    public String mFilePath = null;
    public String mFilePath2 = null;
    public String TAG = "Record2Wav";

    itemFairyTale item;
    String id, fairyName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mFilePath2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record1.wav";

        storageRef = FirebaseStorage.getInstance().getReference();

        recordBtn = (Button) findViewById(R.id.recordBtn);
        playBtn = (Button) findViewById(R.id.playBtn);

        title = (TextView) findViewById(R.id.title);
        imageview = (ImageView) findViewById(R.id.image);

        Intent intent = getIntent();
        if (intent != null) {
            item = (itemFairyTale) intent.getSerializableExtra("item");
        }

        title.setText(item.getTitle());
        imageview.setImageResource(item.getImage());


    }

    private void startRecording() {

        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS,
                Constants.RECORDER_AUDIO_ENCODING, Constants.BufferElements2Rec * Constants.BytesPerElement);

        mRecorder.startRecording();
        isRecording = true;

        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        FileOutputStream os = null;
        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record1.pcm";
        try {
            os = new FileOutputStream(mFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format
            byte bData[] = new byte[mBufferSize];
            mRecorder.read(bData, 0, Constants.BufferElements2Rec);
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer

                os.write(bData, 0, Constants.BufferElements2Rec * Constants.BytesPerElement);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startPlaying() {

        new Thread(new Runnable() {
            public void run() {
                while (isPlaying) {
                    try {
                        mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record1.pcm";
                        File file = new File(mFilePath);

                        byte[] audioData = null;

                        InputStream inputStream = new FileInputStream(mFilePath);
                        audioData = new byte[Constants.BufferElements2Rec];

                        mPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, Constants.RECORDER_SAMPLERATE,
                                AudioFormat.CHANNEL_OUT_MONO, Constants.RECORDER_AUDIO_ENCODING,
                                Constants.BufferElements2Rec * Constants.BytesPerElement, AudioTrack.MODE_STREAM);

                        final float duration = (float) file.length() / Constants.RECORDER_SAMPLERATE / 2;

                        mPlayer.setPositionNotificationPeriod(Constants.RECORDER_SAMPLERATE / 10);
                        mPlayer.setNotificationMarkerPosition(Math.round(duration * Constants.RECORDER_SAMPLERATE));

                        mPlayer.play();

                        int i = 0;
                        while ((i = inputStream.read(audioData)) != -1) {
                            try {
                                mPlayer.write(audioData, 0, i);
                            } catch (Exception e) {
                                Log.e(TAG, "Exception: " + e.getLocalizedMessage());
                            }
                        }
                    } catch (FileNotFoundException fe) {
                        Log.e(TAG, "File not found: " + fe.getLocalizedMessage());
                    } catch (IOException io) {
                        Log.e(TAG, "IO Exception: " + io.getLocalizedMessage());
                    }
                }
            }
        }).start();
    }

    public class Constants {

        final static public int RECORDER_SAMPLERATE = 44100;
        final static public int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
        final static public int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

        final static public int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
        final static public int BytesPerElement = 2; // 2 bytes in 16bit format


    }

    public void onRecord(View view) {
        if (isRecording == true) {
            isRecording = false;
            recordBtn.setText("Record");
            uploadFileToFirebase();
        } else {//false
            isRecording = true;
            recordBtn.setText("Stop");
            startRecording();
        }
    }

    public void onPlay(View view) {
        if (isPlaying == true) {
            isPlaying = false;
            playBtn.setText("Play");
        } else {
            isPlaying = true;
            playBtn.setText("Stop");
            startPlaying();
        }
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream(new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, 44100); // sample rate
            writeInt(output, Constants.RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            // Audio data (conversion big endian -> little endian)
            short[] shorts = new short[rawData.length / 2];
            ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
            for (short s : shorts) {
                bytes.putShort(s);
            }

            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
    }

    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }

    public void uploadFileToFirebase() {
        final File f1 = new File(mFilePath);
        final File f2 = new File(mFilePath2);
        try {
            rawToWave(f1, f2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("SendToFirebase","실행됌!!!!!!!!!!!!");

        id = "abbey17";
        fairyName = "temp";
        String index = "0";
        audioRef = storageRef.child("audio/" + id + "/" + fairyName + "/" + index + ".wav");
        UploadTask uploadTask = audioRef.putFile(Uri.fromFile(f2));
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                f1.delete();
                f2.delete();
            }
        });
    }

}//Activity의 끝
