package barqsoft.footballscores.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Implementation of App Widget functionality.
 */
public class ScoresWidgetProvider extends AppWidgetProvider
{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        updateAppWidget(context);
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        updateAppWidget(context);
    }

    static void updateAppWidget(Context context)
    {
        ScoresWidgetIntentService.launchRefreshWidget(context);
    }

    public static void broadcastToScoresWidgetProvider(Context context)
    {
        Intent intent = new Intent(context, ScoresWidgetProvider.class);
        context.sendBroadcast(intent);
    }
}