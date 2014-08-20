package com.cstewart.android.fozei.base;

import android.app.Activity;
import android.os.Bundle;

import com.cstewart.android.fozei.FozeiApplication;

public abstract class FozeiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FozeiApplication.get(this).inject(this);
    }
}
