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

/**
 * A Fragment that represents the statistics screen in the application.
 * It shows statistics related to the user's meditation streaks and provides a button
 * for capturing a screenshot of the statistics and uploading it to Firebase Storage.
 */
public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private AppCompatTextView currentStreakTV;
    private AppCompatTextView longestStreakTV;
    private Bitmap screenshot;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment using View Binding
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Find the views from the inflated layout
        Button screenshotButton = root.findViewById(R.id.btn_sendStats);
        currentStreakTV = root.findViewById(R.id.tv_currentStreak);
        longestStreakTV = root.findViewById(R.id.tv_longestStreak);

        // Set the click listener for the screenshot button
        screenshotButton.setOnClickListener(v -> {
            // Capture a screenshot of the fragment's root view
            screenshot = takeScreenshot(root);

            // Create a Firebase Storage reference for the screenshot
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Get the current date to use as part of the filename
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String formattedDate = currentDate.format(formatter);

            // Create a reference to the screenshot in Firebase Storage
            StorageReference screenshotRef = storageRef.child("statistics/" + formattedDate + ".png");

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
        // Get the streaks data from MainActivity
        int[] streaksArray = MainActivity.getDaysStreaks();

        // Update the TextViews with the values from streaksArray
        currentStreakTV.setText(String.valueOf(streaksArray[0]));
        longestStreakTV.setText(String.valueOf(streaksArray[1]));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the View Binding and TextView references when the view is destroyed
        binding = null;
        currentStreakTV = null;
        longestStreakTV = null;
    }

    /**
     * Takes a screenshot of the provided View and returns it as a Bitmap.
     * @param view The view to capture a screenshot of.
     * @return The captured screenshot as a Bitmap.
     */
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
