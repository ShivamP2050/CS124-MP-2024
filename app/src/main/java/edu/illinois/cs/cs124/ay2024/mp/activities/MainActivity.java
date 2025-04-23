package edu.illinois.cs.cs124.ay2024.mp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.illinois.cs.cs124.ay2024.mp.R;
import edu.illinois.cs.cs124.ay2024.mp.adapters.SummaryListAdapter;
import edu.illinois.cs.cs124.ay2024.mp.application.JoinableApplication;
import edu.illinois.cs.cs124.ay2024.mp.models.Summary;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main activity showing RSO summaries and enabling filtering and searching.
 *
 * <p>This creates the first screen shown to the user when the app launches.
 */
public final class MainActivity extends Activity implements SearchView.OnQueryTextListener {
  /** Tag to identify the MainActivity in the logs. */
  private static final String TAG = MainActivity.class.getSimpleName();

  /** List of RSO summaries received from the server, initially empty. */
  private List<Summary> summaries = Collections.emptyList();

  /** Adapter that connects our list of summaries with the UI displayed to the user. */
  private SummaryListAdapter listAdapter;

  /** {@inheritDoc} */
  @Override
  protected void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);

    // Load this activity's layout and set the title
    setContentView(R.layout.activity_main);
    setTitle("Discover RSOs");

    // Set up the list adapter for the list of RSO summaries
    listAdapter = new SummaryListAdapter(summaries, this, summary -> {
      Log.d(TAG, "User clicked on the summary" + summary.getTitle());
      Intent  rsoActivity = new Intent(this, RSOActivity.class);
      rsoActivity.putExtra("id", summary.getId());
      startActivity(rsoActivity);
    });



    // Add the list to the layout
    RecyclerView recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(listAdapter);

    // Register this component as a callback for changes to the search view component.
    // We'll eventually use these events to perform interactive searching.
    SearchView searchView = findViewById(R.id.search);
    searchView.setOnQueryTextListener(this);

    ToggleButton orangeButton = findViewById(R.id.orangeButton);
    orangeButton.setOnCheckedChangeListener(orangeButtonListener);

    ToggleButton blueButton = findViewById(R.id.blueButton);
    blueButton.setOnCheckedChangeListener(blueButtonListener);

    // Register our toolbar
    setActionBar(findViewById(R.id.toolbar));
  }

  private final Set<Summary.Color> shownColors = new HashSet<>(Arrays.asList(
      Summary.Color.ORANGE, Summary.Color.BLUE, Summary.Color.DEPARTMENT
  ));

  private final CompoundButton.OnCheckedChangeListener orangeButtonListener = (unused, checked) -> {
    if (checked) {
      shownColors.add(Summary.Color.ORANGE);
    } else {
      shownColors.remove(Summary.Color.ORANGE);
    }
    Log.d(TAG, "Currently shown colors: " + shownColors);
    List<Summary> newList = Summary.filterColor(summaries, shownColors);
    listAdapter.setSummaries(newList);
  };

  private final CompoundButton.OnCheckedChangeListener blueButtonListener = (unused, checked) -> {
    if (checked) {
      shownColors.add(Summary.Color.BLUE);
    } else {
      shownColors.remove(Summary.Color.BLUE);
    }
    Log.d(TAG, "Currently shown colors: " + shownColors);
    List<Summary> newList = Summary.filterColor(summaries, shownColors);
    listAdapter.setSummaries(newList);
  };

  /** {@inheritDoc} */
  @Override
  protected void onStart() {
    super.onStart();

    // Initiate a request for the summary list
    JoinableApplication application = (JoinableApplication) getApplication();
    application
        .getClient()
        .getSummaries(
            (result) -> {
              // Update the list shown to the user in a callback
              try {
                summaries = result.getValue();
                Collections.sort(summaries);
                listAdapter.setSummaries(summaries);
              } catch (Exception e) {
                Log.e(TAG, "Error updating summary list", e);
              }
            });
  }

  /**
   * This fires every time the text in the search bar changes. We'll eventually handle this by
   * updating the UI summary list.
   *
   * <p>{@inheritDoc}
   *j
   * @param query the text to use to filter the summary list
   * @return true because we handled the action
   */
  @Override
  public boolean onQueryTextChange(@NonNull String query) {
    List<Summary> newList = Summary.search(summaries, query);
    listAdapter.setSummaries(newList);
    return true;
  }

  /**
   * This would correspond to the user hitting enter or a submit button next to the search bar.
   * Because we update the list on each change to the search value, we do not handle this event.
   *
   * <p>{@inheritDoc}
   *
   * @param unused current query text
   * @return false because we did not handle this action
   */
  @Override
  public boolean onQueryTextSubmit(@NonNull String unused) {
    return false;
  }
}
