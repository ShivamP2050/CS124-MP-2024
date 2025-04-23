package edu.illinois.cs.cs124.ay2024.mp.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.Nullable;
import edu.illinois.cs.cs124.ay2024.mp.R;
import edu.illinois.cs.cs124.ay2024.mp.application.JoinableApplication;
import edu.illinois.cs.cs124.ay2024.mp.models.RSO;

@SuppressLint("SetTextI18n")
public class RSOActivity extends Activity {
  /** Tag to identify the MainActivity in the logs. */
  private static final String TAG = RSOActivity.class.getSimpleName();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Load this activity's layout and set the title
    setContentView(R.layout.activity_rso);

    Log.d(TAG, "RSOActivity Launched");

    TextView idText = findViewById(R.id.ID);
    String id  = getIntent().getStringExtra("id");

    TextView missionStatement = findViewById(R.id.mission);
    TextView titleText = findViewById(R.id.title);
    TextView categoryText = findViewById(R.id.category);
    TextView websiteText = findViewById(R.id.website);

    if (id == null) {
      throw new IllegalStateException();
    }

    JoinableApplication application = (JoinableApplication) getApplication();
    application
        .getClient()
        .getRSO(
            id,
            (callback) -> {

              try {
                RSO selectedRSO = callback.getValue();
                idText.setText(id);
                websiteText.setText(selectedRSO.getWebsite());
                String categories = "";
                if (selectedRSO.getCategories() != null) {
                  for (int i = 0; i < selectedRSO.getCategories().size(); i++) {
                    categories += selectedRSO.getCategories().get(i);
                    if (i != selectedRSO.getCategories().size() - 1) {
                      categories += ", ";
                    }
                  }
                }
                categoryText.setText((categories));
                titleText.setText(selectedRSO.getTitle());
                missionStatement.setText(selectedRSO.getMission());
              } catch (Exception e) {
                Log.e(TAG, "Error updating summary list", e);
              }
            });
    final CompoundButton.OnCheckedChangeListener bluListener =
        (unused, check) -> {
          if (check) {
            JoinableApplication app = (JoinableApplication) getApplication();
            app
                .getClient()
                .setFavorite(
                    id,
                    true,
                    (callback) -> {



                      try {
                        Log.e(TAG, "Good");
                      } catch (Exception e) {
                        Log.e(TAG, "Error");
                      }
                    });
          } else {
            JoinableApplication favApp = (JoinableApplication) getApplication();
            favApp
                .getClient()
                .setFavorite(
                    id,
                    false,
                    (callback) -> {
                      try {
                        Log.e(TAG, "Good");
                      } catch (Exception e) {
                        Log.e(TAG, "Error");
                      }
                    });
          }
        };

    ToggleButton favoriteButton = findViewById(R.id.buttonFav);


    favoriteButton.setOnCheckedChangeListener(bluListener);

    JoinableApplication application3 = (JoinableApplication) getApplication();
    application3
        .getClient()
        .getFavorite(
            id,
            (callback) -> {

              try {
                favoriteButton.setChecked(callback.getValue());
              } catch (Exception e) {
                Log.e(TAG, "Error favorite", e);
              }
            });






  }
}
