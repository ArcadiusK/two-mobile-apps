package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.R;

/**
 * Implementation of App Widget functionality.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CollectionWidgetProvider extends AppWidgetProvider
{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++)
        {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent)
    {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.collection_widget_listview);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId)
    {
        // Construct the RemoteViews object
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            setRemoteAdapterV14(context, remoteViews);
        } else
        {
            setRemoteAdapterV11(context, remoteViews);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, RemoteViews remoteViews)
    {
        Intent intent = new Intent(context, CollectionWidgetRemoteViewService.class);
        remoteViews.setRemoteAdapter(0, R.id.collection_widget_listview, intent);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapterV14(Context context, RemoteViews remoteViews)
    {
        Intent intent = new Intent(context, CollectionWidgetRemoteViewService.class);
        remoteViews.setRemoteAdapter(R.id.collection_widget_listview, intent);
    }

    public static void broadcastToCollectionWidgetProvider(Context context)
    {
        Intent intent = new Intent(context, CollectionWidgetProvider.class);
        context.sendBroadcast(intent);
    }
}