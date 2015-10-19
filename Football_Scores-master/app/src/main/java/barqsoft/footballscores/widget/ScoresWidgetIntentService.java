package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class ScoresWidgetIntentService extends IntentService implements Loader.OnLoadCompleteListener<Cursor>
{
    private static final String ACTION_REFRESH_WIDGET = "barqsoft.footballscores.widget.action.REFRESH_WIDGET";

    private CursorLoader mCursorLoader;

    /**
     * Starts this service to perform action Refresh Widget with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void launchRefreshWidget(Context context)
    {
        Intent intent = new Intent(context, ScoresWidgetIntentService.class);
        intent.setAction(ACTION_REFRESH_WIDGET);
        context.startService(intent);
    }

    public ScoresWidgetIntentService()
    {
        super("ScoresWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final String action = intent.getAction();
            if (ACTION_REFRESH_WIDGET.equals(action))
            {
                handleActionRefreshWidget();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        // Stop the cursor loader
        if (mCursorLoader != null)
        {
            mCursorLoader.unregisterListener(this);
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }

    /**
     * Handle action Refresh widget in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRefreshWidget()
    {
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
        String[] dateArray = {formater.format(today)};
        mCursorLoader = new CursorLoader(this, DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, dateArray, null);
        mCursorLoader.registerListener(1, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onLoadComplete(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        updateWidgets(cursor);
    }

    private void updateWidgets(Cursor cursor)
    {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ScoresWidgetProvider.class));

        for (int appWidgetId : appWidgetIds)
        {
            // Construct the RemoteViews object
            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.scores_widget);

            // Continue only if there is data available
            if (cursor.getCount() > 0)
            {
                // Start from last to find last updated match
                cursor.moveToLast();

                while (!cursor.isFirst())
                {
                    int home_goals = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
                    if (home_goals > -1)
                    {
                        // If there is data for goals we use this cursor
                        break;
                    }
                    // Otherwise we move to the previous one
                    cursor.moveToPrevious();
                }

                Utilies.populateWidgetView(cursor, remoteView);

                Intent widgetIntent = new Intent(this, MainActivity.class);
                PendingIntent widgetPendingIntent =
                        PendingIntent.getActivity(this, 0, widgetIntent, 0);

                remoteView.setOnClickPendingIntent(R.id.widget, widgetPendingIntent);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, remoteView);
            }
        }
    }
}