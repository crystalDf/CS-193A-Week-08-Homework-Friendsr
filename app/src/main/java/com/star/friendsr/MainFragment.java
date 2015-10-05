package com.star.friendsr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.FileNotFoundException;


public class MainFragment extends Fragment {

    public static final int REQUEST_CODE = 0;
    public static final String NAME = "NAME";
    public static final String STAR = "STAR";

    public static final String RATING_PREF = "rating";

    public static final String POS = "pos";

    private ImageView[] mImageViews;
    private TextView[] mTextViews;
    private RatingBar[] mRatingBars;

    private MediaPlayer mMediaPlayer;

    private LinearLayout mLinearLayout;

    public static final float SCALE = 0.6f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mImageViews = new ImageView[6];
        mTextViews = new TextView[6];
        mRatingBars = new RatingBar[6];

        mImageViews[0] = (ImageView) getActivity().findViewById(R.id.chandlerImage);
        mImageViews[1] = (ImageView) getActivity().findViewById(R.id.joeyImage);
        mImageViews[2] = (ImageView) getActivity().findViewById(R.id.monicaImage);
        mImageViews[3] = (ImageView) getActivity().findViewById(R.id.phoebeImage);
        mImageViews[4] = (ImageView) getActivity().findViewById(R.id.rachelImage);
        mImageViews[5] = (ImageView) getActivity().findViewById(R.id.rossImage);

        mTextViews[0] = (TextView) getActivity().findViewById(R.id.chandler);
        mTextViews[1] = (TextView) getActivity().findViewById(R.id.joey);
        mTextViews[2] = (TextView) getActivity().findViewById(R.id.monica);
        mTextViews[3] = (TextView) getActivity().findViewById(R.id.phoebe);
        mTextViews[4] = (TextView) getActivity().findViewById(R.id.rachel);
        mTextViews[5] = (TextView) getActivity().findViewById(R.id.ross);

        mRatingBars[0] = (RatingBar) getActivity().findViewById(R.id.chandlerRatingBar);
        mRatingBars[1] = (RatingBar) getActivity().findViewById(R.id.joeyRatingBar);
        mRatingBars[2] = (RatingBar) getActivity().findViewById(R.id.monicaRatingBar);
        mRatingBars[3] = (RatingBar) getActivity().findViewById(R.id.phoebeRatingBar);
        mRatingBars[4] = (RatingBar) getActivity().findViewById(R.id.rachelRatingBar);
        mRatingBars[5] = (RatingBar) getActivity().findViewById(R.id.rossRatingBar);

        final String[] friendNames = getResources().getStringArray(R.array.friend_names);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(RATING_PREF,
                Context.MODE_PRIVATE);

        for (int i = 0; i < mRatingBars.length; i++) {
            Float rating = sharedPreferences.getFloat(friendNames[i], 0);
            mRatingBars[i].setRating(rating);
        }

        for (int i = 0; i < mImageViews.length; i++) {
            final int finalI = i;
            mImageViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (getResources().getConfiguration().orientation ==
                            Configuration.ORIENTATION_LANDSCAPE) {

                        DetailsFragment detailsFragment = (DetailsFragment) getFragmentManager().findFragmentById(
                                R.id.details_fragment
                        );

                        detailsFragment.setName(friendNames[finalI]);

                    } else {
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(NAME, friendNames[finalI]);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            });

            String image = sharedPreferences.getString(friendNames[i] + " image", "");

            if (!"".equals(image)) {
                try {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    Bitmap bitmap = BitmapFactory.decodeStream(
                            getActivity().getContentResolver().openInputStream(Uri.parse(image)),
                            null, options
                    );
                    mImageViews[i].setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }

        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.friends_theme);

        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {

            mLinearLayout = (LinearLayout) getActivity().findViewById(R.id.main_linear_layout);

            mLinearLayout.setScaleX(SCALE);
            mLinearLayout.setScaleY(SCALE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String[] friendNames = getResources().getStringArray(R.array.friend_names);

        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String name = data.getStringExtra(NAME);
                    for (int i = 0; i < friendNames.length; i++) {
                        if (friendNames[i].equals(name)) {
                            mRatingBars[i].setRating(data.getFloatExtra(STAR, 0));
                        }
                    }
                }
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(RATING_PREF,
                Context.MODE_PRIVATE);

        for (int i = 0; i < mImageViews.length; i++) {

            String image = sharedPreferences.getString(friendNames[i] + " image", "");

            if (!"".equals(image)) {
                try {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;

                    Bitmap bitmap = BitmapFactory.decodeStream(
                            getActivity().getContentResolver().openInputStream(Uri.parse(image)),
                            null, options
                    );
                    mImageViews[i].setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(true);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(RATING_PREF,
                    Context.MODE_PRIVATE);

            mMediaPlayer.seekTo(sharedPreferences.getInt(POS, 0));

            mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mp.start();
                }
            });

        }

    }

    @Override
    public void onPause() {
        mMediaPlayer.pause();
        int pos = mMediaPlayer.getCurrentPosition();

        SharedPreferences.Editor editor = getActivity().getSharedPreferences(RATING_PREF,
                Context.MODE_PRIVATE)
                .edit();

        editor.putInt(POS, pos).commit();

        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        super.onDestroy();
    }
}
