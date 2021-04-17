package com.example.android.voicerecorderapp;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick{
    private ConstraintLayout playerSeetLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView listRecordedItems;
    private File[] allfiles;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay = null;
    AudioListAdapter listAdapter;
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    public AudioListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playBtn = view.findViewById(R.id.playButton);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.recourdFilename);
        playerSeekbar = view.findViewById(R.id.player_seekbar);

        listRecordedItems = view.findViewById(R.id.RecordedFileList);
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File dir = new File(path);
        allfiles = dir.listFiles();
        listAdapter = new AudioListAdapter(allfiles, this);
        listRecordedItems.setHasFixedSize(true);
        listRecordedItems.setLayoutManager(new LinearLayoutManager(getContext()));
        listRecordedItems.setAdapter(listAdapter);
        playerSeetLayout = view.findViewById(R.id.playerSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSeetLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseAudio();
                } else {
                    if(fileToPlay != null){
                        resumeAudio();
                    }else{
                        isPlaying =false;
                        restartAudio(fileToPlay);

                    }
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });


    }
    @Override
    public void onClickListener(File file, int position){
        fileToPlay = file;
        if(isPlaying){
            stopAudio();
            playAudio(fileToPlay);
        } else {
            playAudio(fileToPlay);
        }
    }
    private void pauseAudio() {
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        isPlaying = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }
    private void restartAudio(File file) {
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        mediaPlayer.start();
        isPlaying = true;


    }
    private void stopAudio() {
        //Stop The Audio
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
        playerHeader.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null));
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        //Play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });

        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }
    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying) {
            stopAudio();
        }
    }
}