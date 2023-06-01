package zen.zone.ui.meditation;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.Locale;

import zen.zone.MainActivity;
import zen.zone.R;

public class MeditationTimerFragment extends Fragment {

    private TextView tvTimeRemaining;
    private Button btnPause;
    private LinearLayout llPausedControls;
    private CountDownTimer meditationTimer;
    private long timeLeftInMillis;
    private long totalTimeInMillis;
    private boolean hasHalftimePassed = false;
    private MediaPlayer mediaPlayer;
    private NotificationManager notificationManager;
    private int currentInterruptionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Check if we can force no disturb mode during meditation
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (!notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meditation_timer, container, false);

        tvTimeRemaining = view.findViewById(R.id.tv_time_remaining);
        btnPause = view.findViewById(R.id.btn_pause);
        llPausedControls = view.findViewById(R.id.ll_paused_controls);
        Button btnPlay = view.findViewById(R.id.btn_play);
        Button btnStop = view.findViewById(R.id.btn_stop);

        assert getArguments() != null;
        timeLeftInMillis = getArguments().getLong("meditationDurationMillis", 1 * 60 * 1000);
        totalTimeInMillis = timeLeftInMillis;
        boolean halfTimeNotification = getArguments().getBoolean("halfTimeNotification");
        String backgroundSound = getArguments().getString("backgroundSound");

        startMeditationTimer(timeLeftInMillis, halfTimeNotification);
        handleBackgroundSound(backgroundSound);

        btnPause.setOnClickListener(v -> {
            meditationTimer.cancel();
            btnPause.setVisibility(View.GONE);
            llPausedControls.setVisibility(View.VISIBLE);
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        btnPlay.setOnClickListener(v -> {
            startMeditationTimer(timeLeftInMillis, halfTimeNotification);
            btnPause.setVisibility(View.VISIBLE);
            llPausedControls.setVisibility(View.GONE);
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        });

        btnStop.setOnClickListener(v -> stopMeditationAndReturnToSettings());

        // Hide ActionBar and NavBar
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            ((MainActivity) getActivity()).hideBottomNav();
        }

        return view;
    }

    private void startMeditationTimer(long millisUntilFinished, boolean halfTimeNotification) {
        startNoDisturbMode();
        meditationTimer = new CountDownTimer(millisUntilFinished, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                String timeRemaining = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                tvTimeRemaining.setText(timeRemaining);

                if (!hasHalftimePassed && halfTimeNotification && (millisUntilFinished <= totalTimeInMillis / 2)) {
                    playBellSound();
                    hasHalftimePassed = true;
                }
            }

            @Override
            public void onFinish() {
                // Notify the user that the meditation has ended
                // Return to the meditation settings screen (SettingsFragment)
                stopMeditationAndReturnToSettings();
            }
        }.start();
    }

    public void stopMeditationAndReturnToSettings() {
        stopNoDisturbMode();
        if (meditationTimer != null) {
            meditationTimer.cancel();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        Navigation.findNavController(getView()).navigate(R.id.navigation_meditation);

        // Show ActionBar and NavBar
        if (getActivity() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            ((MainActivity) getActivity()).showBottomNav();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        stopMeditationAndReturnToSettings();
    }

    private void handleBackgroundSound(String backgroundSound) {
        // Determine the resource id of the selected background sound
        int soundResId = SoundResources.getSoundResource(backgroundSound);
        if (soundResId != 0) {
            // Play the selected background sound
            playBackgroundSound(soundResId);
        }
    }


    private void playBackgroundSound(int soundResId) {
        // Release any resources from previous MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        // Create a new MediaPlayer to play this sound
        mediaPlayer = MediaPlayer.create(this.getActivity(), soundResId);
        mediaPlayer.setLooping(true); // Set looping
        mediaPlayer.start();
    }

    private void playBellSound() {
        // Create a MediaPlayer and set its resource to gong sound
        MediaPlayer bellSound = MediaPlayer.create(getContext(), R.raw.bell);
        bellSound.start();
        bellSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }

    private void startNoDisturbMode() {
        currentInterruptionMode = notificationManager.getCurrentInterruptionFilter();
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
    }

    private void stopNoDisturbMode() {
        notificationManager.setInterruptionFilter(currentInterruptionMode);
    }
}
