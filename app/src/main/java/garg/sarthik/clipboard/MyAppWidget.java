package garg.sarthik.clipboard;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {

    Button btnWidgetStart;
    Button btnWidgetStop;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);

        List<Clip> clips = ClipApplication.getClipDb().getClipDao().getAll();
        Clip clip = clips.get(clips.size() - 1);

        String widgetText = clip.getContent();
        String widgetDate = clip.getDate();

        Log.e("Widget", "updateAppWidget: " + widgetText.length());

        String txt = widgetText;
        if (txt.length() > 121) {
            txt = txt.substring(0, 118) + "...";
        }
        views.setTextViewText(R.id.tvWidgetContent, txt);
        views.setTextViewText(R.id.tvWidgetDate, widgetDate);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}

