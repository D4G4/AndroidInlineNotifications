package com.example.daksh.inlinenotificationsdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.daksh.inlinenotificationsdemo.MainActivity.INTENT_NOTIFICATION_ID;

public class NotificationActivity extends AppCompatActivity {

  @BindView(R.id.textView) TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification);
    ButterKnife.bind(this);
    init();
  }

  private void init() {
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String text = getIntent().getStringExtra(MainActivity.INTENT_MESSAGE);
    if (!TextUtils.isEmpty(text)) {
      textView.setText(text);
    } else {
      textView.setText("-----");
    }
    int randomNotificaitonId = getIntent().getIntExtra(INTENT_NOTIFICATION_ID, 0);
    if (notificationManager != null) {
      notificationManager.cancel(randomNotificaitonId);
    }

    Bundle remoteInput = RemoteInput.getResultsFromIntent(getIntent());
    if (remoteInput != null) {
      CharSequence txt = remoteInput.getCharSequence(MainActivity.KEY_TEXT_REPLY);
      if (!TextUtils.isEmpty(txt)) {
        textView.setText(txt);
      }
    }

    // Build a new notification, which informs the user that the system
    // handled their interaction with the previous notification.
    Notification repliedNotification = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      repliedNotification = new Notification.Builder(getApplicationContext(), "channel_2")
          .setSmallIcon(R.drawable.ic_twitter_inactive)
          .setContentText("Replied")
          .build();
    } else {
      if (notificationManager != null) {
        notificationManager.cancel(222);
      }
    }

    // Issue the new notification.
    //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    if (notificationManager != null) {
      notificationManager.notify(222, repliedNotification);
    }
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Toast.makeText(this, "On new Intent", Toast.LENGTH_SHORT).show();
  }
}
