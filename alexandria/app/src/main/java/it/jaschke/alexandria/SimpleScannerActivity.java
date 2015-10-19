package it.jaschke.alexandria;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/*
 * Implemented as per https://github.com/dm77/barcodescanner
 */

public class SimpleScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler
{
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);

        // Only EAN13 is valid for us
        List<BarcodeFormat> formatList = new ArrayList();
        formatList.add(BarcodeFormat.EAN13);
        mScannerView.setFormats(formatList);
        setContentView(mScannerView);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult)
    {
        // Valid EAN13 starts with AddBook.EAN13_STARTS_WITH
        if (rawResult.getContents().startsWith(AddBook.EAN13_STARTS_WITH))
        {
            Intent intentResult = new Intent();
            intentResult.putExtra(AddBook.EAN13_KEY, rawResult.getContents());
            setResult(RESULT_OK, intentResult);
            finish();
        } else
        {
            // Not valid scan. Try again
            mScannerView.startCamera();
        }
    }
}