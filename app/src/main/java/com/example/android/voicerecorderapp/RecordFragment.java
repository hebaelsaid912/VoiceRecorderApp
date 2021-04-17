package com.example.android.voicerecorderapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordFragment extends Fragment {
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private  boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int permissionCode = 21;
    private MediaRecorder mediaRecorder;
    private String fileName="";
    private Chronometer timer ;
    private TextView recordFileName;
    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        recordFileName = view.findViewById(R.id.titleRecord);
        timer = view.findViewById(R.id.recordTimer);
        recordBtn = view.findViewById(R.id.RecordImage);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording){
                    stopRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.record_btn_stopped,null));
                    isRecording = false;
                }else{
                    if(checkPermission()) {
                        startRecording();
                        recordBtn.setImageDrawable(getResources().getDrawable(
                                R.drawable.record_btn_recording, null));
                        isRecording = true;
                    }
                }
            }
        });
        listBtn = view.findViewById(R.id.ListRecordedImage);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.recordFragment_to_audioListFragment);
            }
        });

    }

    private void stopRecording() {
        recordFileName.setText("Recording stop, Saved File Name :\n" + fileName);
        timer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        System.out.println("Recording stop");
    }
    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordFilePath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy,MM,dd,mm,ss", Locale.CANADA);
        Date now = new Date();
        fileName = "Recorded"+ formatter.format(now) +".3gp";
        recordFileName.setText("Recording now, File Name : \n" + fileName);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordFilePath+"/"+fileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        }catch (IOException ex){
            ex.printStackTrace();
        }
        mediaRecorder.start();
        System.out.println("Recording start");
    }

    private boolean checkPermission() {
        if(ActivityCompat.checkSelfPermission(getContext(),recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{recordPermission},permissionCode);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording) {
            stopRecording();
        }
    }
}