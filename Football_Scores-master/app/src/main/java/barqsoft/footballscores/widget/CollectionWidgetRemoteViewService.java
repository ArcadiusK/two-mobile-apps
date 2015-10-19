package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Looper;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CollectionWidgetRemoteViewService extends RemoteViewsService
{
    private Cursor cursor = null;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return new RemoteViewsFactory()
        {
            private Loader.OnLoadCompleteListener<Cursor> loaderListener;

            @Override
            public void onCreate()
            {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged()
            {
                loaderListener = new Loader.OnLoadCompleteListener<Cursor>()
                {
                    @Override
                    public void onLoadComplete(Loader<Cursor> loader, Cursor data)
                    {
                        cursor = data;
                    }
                };

                // Query the DB
                Date today = new Date(System.currentTimeMillis());
                SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                String[] dateArray = {formater.format(today)};
                if (Looper.myLooper() == null)
                {
                    Looper.myLooper().prepare();
                }

                CursorLoader mCursorLoader = new CursorLoader(getApplicationContext(), DatabaseContract.scores_table.buildScoreWithDate(),
                        null, null, dateArray, null);
                mCursorLoader.registerListener(1, loaderListener);
                mCursorLoader.startLoading();

                waitForLoader();
            }

            @Override
            public void onDestroy()
            {
                if (cursor != null)
                {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount()
            {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position)
            {
                RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.collection_widget_list_item);

                if (cursor != null)
                {
                    cursor.moveToPosition(position);
                    Utilies.populateWidgetView(cursor, remoteView);
                }
                return remoteView;
            }

            @Override
            public RemoteViews getLoadingView()
            {
                return new RemoteViews(getPackageName(), R.layout.collection_widget);
            }

            @Override
            public int getViewTypeCount()
            {
                return 1;
            }

            @Override
            public long getItemId(int position)
            {
                return position;
            }

            @Override
            public boolean hasStableIds()
            {
                return false;
            }
        };
    }

    public void waitForLoader()
    {
        try
        {
            while (cursor == null)
            {
                Thread.sleep(50);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}