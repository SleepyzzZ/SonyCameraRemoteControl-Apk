package com.sleepyzzz.photo_selector.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sleepyzzz.photo_selector.R;
import com.sleepyzzz.photo_selector.fragment.PhotoPagerFragment;
import com.sleepyzzz.photo_selector.fragment.PhotoPickerFragment;

public class PhotoPickerActivity extends FragmentActivity {

    private final static String TAG = PhotoPickerActivity.class.getSimpleName();
    /**
     * 默认图片选择数量
     */
    private int mDefaultCount;
    /**
     * 默认搜索路径
     */
    private String mDefaultSearchPath;
    /**
     * 控件
     */
    private Button mCommitBtn;
    private ImageView mBackBtn;

    private PhotoPickerFragment mPickerFragment;
    private PhotoPagerFragment mPagerFragment;

    public static void actionStart(Context context, int maxSelectCount, String searchPath)
    {
        Intent intent = new Intent(context, PhotoPickerActivity.class);
        intent.putExtra(PhotoPickerFragment.EXTRA_SELECT_COUNT, maxSelectCount);
        intent.putExtra(PhotoPickerFragment.EXTRA_DEFAULT_SELECTED_LIST, searchPath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);

        mDefaultCount = getIntent().getIntExtra(PhotoPickerFragment.EXTRA_SELECT_COUNT, 9);
        mDefaultSearchPath = getIntent().getStringExtra(PhotoPickerFragment.EXTRA_DEFAULT_SELECTED_LIST);
        if(mPickerFragment == null)
        {
            DisplayMetrics outMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);

            mPickerFragment = PhotoPickerFragment.newInstance(outMetrics.heightPixels, mDefaultCount, mDefaultSearchPath);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mPickerFragment)
                    .commit();
        }

        initViews();
        initEvents();
    }

    private void initEvents() {
        mCommitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                        "Commit Button Click", Toast.LENGTH_SHORT).show();
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initViews() {
        mCommitBtn = (Button) findViewById(R.id.btn_commit);
        mBackBtn = (ImageView) findViewById(R.id.iv_back);
    }

    @Override
    public void onBackPressed() {
        if(mPagerFragment != null && mPagerFragment.isVisible()) {
            mPagerFragment.runExitAnimation(new Runnable() {

                @Override
                public void run() {
                    if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    }
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    public void addPhotoPagerFragment(PhotoPagerFragment photoPagerFragment) {

        this.mPagerFragment = photoPagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, this.mPagerFragment)
                .addToBackStack(null)
                .commit();
    }
}
