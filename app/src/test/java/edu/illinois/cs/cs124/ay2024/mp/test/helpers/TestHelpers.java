package edu.illinois.cs.cs124.ay2024.mp.test.helpers;

import static android.os.Looper.getMainLooper;
import static com.google.common.truth.Truth.assertWithMessage;
import static edu.illinois.cs.cs124.ay2024.mp.test.helpers.Data.SUMMARY_COUNT;
import static edu.illinois.cs.cs124.ay2024.mp.test.helpers.HTTP.testServerGet;
import static org.robolectric.Shadows.shadowOf;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import com.fasterxml.jackson.databind.JsonNode;
import edu.illinois.cs.cs124.ay2024.mp.activities.MainActivity;
import edu.illinois.cs.cs124.ay2024.mp.network.Server;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.robolectric.shadows.ShadowLog;

/*
 * This file contains helper code used by the test suites.
 * You should not need to modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 */
public class TestHelpers {

  // Extra time allowed by GET /rso and /favorite compared to baseline
  public static double GET_METHOD_EXTRA_TIME = 1.3;

  // Helper method to start the MainActivity
  public static void startMainActivity(ActivityScenario.ActivityAction<MainActivity> action) {
    try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.RESUMED);
      pause();
      scenario.onActivity(action);
    }
  }

  // Helper method to start an Activity using an Intent
  public static <T extends Activity> void startActivity(
      Intent intent, ActivityScenario.ActivityAction<T> action) {
    try (ActivityScenario<T> scenario = ActivityScenario.launch(intent)) {
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.RESUMED);
      pause();
      scenario.onActivity(action);
    }
  }

  // Pause helpers to improve the stability of our Robolectric tests
  public static void pause(int length) {
    try {
      shadowOf(getMainLooper()).runToEndOfTasks();
      Thread.sleep(length);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  // Default pause override
  public static void pause() {
    pause(100);
  }

  // Set up logging properly for testing
  public static void configureLogging() {
    if (System.getenv("OFFICIAL_GRADING") == null) {
      ShadowLog.setLoggable("LifecycleMonitor", Log.WARN);
      ShadowLog.stream = new FilteringPrintStream();
    }
  }

  public static double trimmedMean(List<Long> values, double percent) {
    List<Long> toReturn = new ArrayList<>(values);
    Collections.sort(toReturn);
    int toDrop = (int) Math.floor(toReturn.size() * percent);
    List<Long> toSum = toReturn.subList(toDrop, toReturn.size() - toDrop);
    return ((double) toSum.stream().reduce(0L, Long::sum)) / toSum.size();
  }

  public static void checkServerDesign() {
    // Check for extra public methods, fields, or inner classes
    long nonPrivateMethodCount =
        Arrays.stream(Server.class.getDeclaredMethods())
            .filter(
                method ->
                    !Modifier.isPrivate(method.getModifiers())
                        && !Modifier.isStatic(method.getModifiers())
                        && !method.getName().equals("dispatch"))
            .count();
    long nonPrivateFieldCount =
        Arrays.stream(Server.class.getDeclaredFields())
            .filter(field -> !Modifier.isPrivate(field.getModifiers()))
            .count();
    long nonPrivateClassCount =
        Arrays.stream(Server.class.getDeclaredClasses())
            .filter(klass -> !Modifier.isPrivate(klass.getModifiers()))
            .count();
    assertWithMessage("Server has visible methods, fields, or classes")
        .that(nonPrivateMethodCount + nonPrivateFieldCount + nonPrivateClassCount)
        .isEqualTo(0);
  }

  public static void testSummaryRoute() throws IOException {
    JsonNode nodes = testServerGet("/summary", JsonNode.class);
    assertWithMessage("Summary list is not the right size").that(nodes).hasSize(SUMMARY_COUNT);
    nodes.forEach(
        node -> {
          assertWithMessage("Summary node has wrong number of fields")
              .that(node.size())
              .isAtLeast(3);
          assertWithMessage("Summary node has wrong number of fields")
              .that(node.size())
              .isAtMost(4);
        });
  }
}

// md5: 330e081ccd5634bdb02a977c41e35470 // DO NOT REMOVE THIS LINE
