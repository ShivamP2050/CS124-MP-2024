package edu.illinois.cs.cs124.ay2024.mp.test.helpers;

import android.content.res.Resources;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 *
 * Fork of the RecyclerViewMatcher from https://github.com/dannyroa/espresso-samples.
 * Used to test the summary list UI component.
 */
public class RecyclerViewMatcher {

  private final int recyclerViewId;

  public RecyclerViewMatcher(int recyclerViewId) {
    this.recyclerViewId = recyclerViewId;
  }

  public Matcher<View> atPosition(final int position) {
    return atPositionOnView(position, -1);
  }

  public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

    return new TypeSafeMatcher<>() {
      Resources resources = null;
      View childView;

      public void describeTo(Description description) {
        String idDescription = Integer.toString(recyclerViewId);
        if (this.resources != null) {
          try {
            idDescription = this.resources.getResourceName(recyclerViewId);
          } catch (Resources.NotFoundException var4) {
            idDescription = String.format("%s (resource name not found)", recyclerViewId);
          }
        }

        description.appendText(
            "RecyclerView with id: " + idDescription + " at position: " + position);
      }

      public boolean matchesSafely(View view) {

        this.resources = view.getResources();

        if (childView == null) {
          RecyclerView recyclerView = view.getRootView().findViewById(recyclerViewId);
          if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
            RecyclerView.ViewHolder viewHolder =
                recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
              childView = viewHolder.itemView;
            }
          } else {
            return false;
          }
        }

        if (targetViewId == -1) {
          return view == childView;
        } else {
          View targetView = childView.findViewById(targetViewId);
          return view == targetView;
        }
      }
    };
  }

  public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
    return new RecyclerViewMatcher(recyclerViewId);
  }
}

// md5: 93d08d17468345499c0ed020bcb3c8b8 // DO NOT REMOVE THIS LINE
