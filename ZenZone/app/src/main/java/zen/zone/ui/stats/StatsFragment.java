package zen.zone.ui.stats;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import zen.zone.MainActivity;
import zen.zone.R;
import zen.zone.databinding.FragmentStatsBinding;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private AppCompatTextView currentStreakTV;
    private AppCompatTextView longestStreakTV;
    private Bitmap screenshot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button screenshotButton = root.findViewById(R.id.btn_sendStats);
        currentStreakTV = root.findViewById(R.id.tv_currentStreak);
        longestStreakTV = root.findViewById(R.id.tv_longestStreak);

        screenshotButton.setOnClickListener(v -> {
            // Capture a screenshot of the fragment's root view
            screenshot = takeScreenshot(root);

            // Create a Firebase Storage reference for the screenshot
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = currentDate.format(formatter);

            StorageReference screenshotRef = storageRef.child("statistics/"+ formattedDate +".png");

            // Convert the screenshot Bitmap to a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] screenshotData = baos.toByteArray();

            // Upload the screenshot to Firebase Storage
            UploadTask uploadTask = screenshotRef.putBytes(screenshotData);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Screenshot upload successful
                Log.d("Stats", "Screenshot uploaded successfully");
                // TODO: Handle any further actions or notifications
            }).addOnFailureListener(e -> {
                // Error occurred while uploading screenshot
                Log.e("Stats", "Screenshot upload failed: " + e.getMessage());
                // TODO: Handle error case or display a message to the user
            });
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int[] streaksArray = MainActivity.getDaysStreaks();

        // Update the TextViews with the values from streaksArray
        currentStreakTV.setText(String.valueOf(streaksArray[0]));
        longestStreakTV.setText(String.valueOf(streaksArray[1]));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        currentStreakTV = null;
        longestStreakTV = null;
    }

    private Bitmap takeScreenshot(View view) {
        Bitmap screenshot = null;
        try {
            // Create a Bitmap with the same dimensions as the View
            screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(screenshot);
            view.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenshot;
    }
}