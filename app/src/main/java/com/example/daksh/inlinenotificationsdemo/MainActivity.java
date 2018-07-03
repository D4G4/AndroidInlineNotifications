package com.example.daksh.inlinenotificationsdemo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.button1) Button button1;
  @BindView(R.id.button2) Button button2;
  @BindView(R.id.button3) Button button3;
  @BindView(R.id.textView) TextView textView;

  String groupOneId = "group_one";
  String groupOneName = "Group 1";
  String groupTwoId = "group_two";
  String groupTwoName = "Group 2";

  static final String INTENT_MESSAGE = "intent_message";
  static final String INTENT_NOTIFICATION_ID = "intent_notification_id";
  static final String KEY_TEXT_REPLY = "key_text_reply";

  NotificationManager notificationManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    init();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Toast.makeText(this, "On new Intent", Toast.LENGTH_SHORT).show();
  }

  private void init() {
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createNotificaitonGroups();
      getNotificationChannelStatus();
    }
    String text = getIntent().getStringExtra(INTENT_MESSAGE);
    if (!TextUtils.isEmpty(text)) {
      textView.setText(text);
    } else {
      textView.setText("-----");
    }
    int randomNotificaitonId = getIntent().getIntExtra(INTENT_NOTIFICATION_ID, 0);
    if (notificationManager != null) {
      notificationManager.cancel(randomNotificaitonId);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.O) private void createNotificaitonGroups() {
    List<NotificationChannelGroup> notificationChannelGroupList = new ArrayList<>();
    notificationChannelGroupList.add(new NotificationChannelGroup(groupOneId, groupOneName));
    notificationChannelGroupList.add(new NotificationChannelGroup(groupTwoId, groupTwoName));
    notificationManager.createNotificationChannelGroups(notificationChannelGroupList);
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void createNotificationChannel(String channelId, String channelName, String groupId) {
    int importance = NotificationManager.IMPORTANCE_DEFAULT;
    NotificationChannel notificationChannel =
        new NotificationChannel(channelId, channelName, importance);
    notificationChannel.enableLights(true);
    notificationChannel.setLightColor(Color.RED);
    notificationChannel.enableVibration(true);
    notificationChannel.setVibrationPattern(
        new long[] {100, 200, 300, 400, 500, 400, 300, 200, 400});
    notificationChannel.setGroup(groupId);
    notificationManager.createNotificationChannel(notificationChannel);
  }

  private PendingIntent getPendingIntent(String text, int randomNotificaitonId) {
    Intent intent = new Intent(this, NotificationActivity.class);
    /*
    An activity that exists exclusively for responses to the notification.
    There's no reason the user would navigate to this activity during normal app use,
    so the activity starts a new task instead of being added to your app's existing task and back stack.
    This is the type of intent created in the sample above.
    */
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.putExtra(INTENT_MESSAGE, text);
    intent.putExtra(INTENT_NOTIFICATION_ID, randomNotificaitonId);
    //A Unique request code will help you to maintain multiple pending intents.
    //return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    return PendingIntent.getActivity(this, randomNotificaitonId, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
  }

  @OnClick(R.id.button1) public void onButton1Clicked() {
    int randomNotificationId = (int) System.currentTimeMillis() % Integer.MAX_VALUE;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      buildAndDisplayNotificaiton("channel_1", "First Channel", 111,
          "Notification First Channel",
          R.drawable.ic_flash_auto, groupOneId,
          getPendingIntent("Notification abc", randomNotificationId));
    }
  }

  @OnClick(R.id.button2) public void onButton2Clicked() {
    int randomNotificaitonId = (int) System.currentTimeMillis() % Integer.MAX_VALUE;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      buildAndDisplayNotificaiton("channel_2", "Second Channel", 222,
          "Notification 2",
          R.drawable.ic_share_send, groupTwoId,
          getPendingIntent("Notificaiton 2", randomNotificaitonId));
    }
  }

  @OnClick(R.id.button3) public void onButton3Clicked() {
    int randomNotificationId = (int) System.currentTimeMillis() % Integer.MAX_VALUE;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      buildAndDisplayNotificaiton("channel_3", "Third Channel", 333,
          "Notification 3",
          R.drawable.ic_other_loc, groupOneId,
          getPendingIntent("Notification 3", randomNotificationId));
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.O) void buildAndDisplayNotificaiton(String channelId,
      String channelName, int notificaitonId,
      String contentTitle, int smallIcon, String groupId, PendingIntent pendingIntent) {
    String longText =
        "Longer content text which wont fit in one line, so we need to append bla bla bla bla bla and bla";
    Notification.Builder builder = new Notification.Builder(this, channelId)
        .setSmallIcon(smallIcon)
        .setContentTitle(contentTitle)
        .setContentText(longText)
        .setContentIntent(pendingIntent)
        //.setAutoCancel(true)
        .setStyle(new Notification.BigTextStyle().bigText(longText))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    //if (groupId.equalsIgnoreCase(groupTwoId) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    addDirectReplyOption(builder);
    //}

    //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    createNotificationChannel(channelId, channelName, groupId);
    //}
    notificationManager.notify(notificaitonId, builder.build());
  }

  private void addDirectReplyOption(Notification.Builder builder) {
    RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
        .setLabel("Reply")
        .build();

    Intent intent = new Intent(this, NotificationActivity.class);

    PendingIntent replyPendingIntent =
        PendingIntent.getActivity(getApplicationContext(), 111, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);

    Notification.Action action = new Notification.Action.Builder(
        R.drawable.baseline_skip_next_black_18dp,
        "Action Label",
        replyPendingIntent
    ).addRemoteInput(remoteInput).build();

    builder.addAction(action);
  }

  @RequiresApi(api = Build.VERSION_CODES.O) void getNotificationChannelStatus() {
    NotificationChannel notificationChannel =
        notificationManager.getNotificationChannel("channel_1");
    if (notificationChannel != null
        && notificationChannel.getImportance() < NotificationManager.IMPORTANCE_DEFAULT) {
      Toast.makeText(this, "Please change notification priority", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
      intent.putExtra(Settings.EXTRA_CHANNEL_ID, "channel_1");
      intent.putExtra(Settings.EXTRA_APP_PACKAGE,
          getPackageName());   //otherwise: app wasn't found in the list of installed apps
      startActivity(intent);
    }
  }
}
