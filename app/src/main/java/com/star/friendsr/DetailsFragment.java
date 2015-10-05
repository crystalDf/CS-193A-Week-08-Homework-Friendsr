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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DetailsFragment extends Fragment {

    private RatingBar mRatingBar;
    private ImageView mDetailsImageView;
    private TextView mDetailsTextView;

    private MediaPlayer mMediaPlayer;

    private Button mTakePhotoButton;
    private Button mChooseFromAlbumButton;

    private String mName;

    private Uri mImageUri;

    public static final int TAKE_PHOTO = 0;
    public static final int CROP_PHOTO = 1;
    public static final int PICK_PHOTO = 2;

    private LinearLayout mLinearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRatingBar = (RatingBar) getActivity().findViewById(R.id.ratingBar);
        mDetailsImageView = (ImageView) getActivity().findViewById(R.id.detailsImage);
        mDetailsTextView = (TextView) getActivity().findViewById(R.id.details);

        Intent intent = getActivity().getIntent();

        mName = intent.getStringExtra(MainFragment.NAME);

        setName(mName);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_LANDSCAPE) {
                    refreshMainFragment();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(MainFragment.NAME, mName);
                    intent.putExtra(MainFragment.STAR, rating);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                }

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                        MainFragment.RATING_PREF,
                        Context.MODE_PRIVATE).edit();

                editor.putFloat(mName, rating).commit();

                if (getResources().getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT) {
                    getActivity().finish();
                }
            }
        });

        mDetailsImageView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            @Override
            public void onSwipeLeft(float dx) {
                super.onSwipeLeft(dx);
                mRatingBar.setRating(1);
            }

            @Override
            public void onSwipeRight(float dx) {
                super.onSwipeRight(dx);
                mRatingBar.setRating(mRatingBar.getNumStars());
            }
        });

        mMediaPlayer = MediaPlayer.create(getActivity(), R.raw.friends_theme);

        mTakePhotoButton = (Button) getActivity().findViewById(R.id.take_photo);
        mChooseFromAlbumButton = (Button) getActivity().findViewById(R.id.choose_from_album);

        mTakePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), mName + ".jpg");

                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }

                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mImageUri = Uri.fromFile(outputImage);

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                i.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(i, TAKE_PHOTO);

            }
        });

        mChooseFromAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputImage = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), mName + ".jpg");

                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }

                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mImageUri = Uri.fromFile(outputImage);

                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, PICK_PHOTO);
            }
        });

        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {

            mLinearLayout = (LinearLayout) getActivity().findViewById(R.id.details_linear_layout);

            mLinearLayout.setScaleX(MainFragment.SCALE);
            mLinearLayout.setScaleY(MainFragment.SCALE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMediaPlayer != null &&
                (getActivity().getSupportFragmentManager().
                        findFragmentById(R.id.main_fragment) == null)) {
            mMediaPlayer.setLooping(true);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                    MainFragment.RATING_PREF,
                    Context.MODE_PRIVATE);

            mMediaPlayer.seekTo(sharedPreferences.getInt(MainFragment.POS, 0));

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

        if (mMediaPlayer != null &&
                (getActivity().getSupportFragmentManager().
                        findFragmentById(R.id.main_fragment) == null)) {

            mMediaPlayer.pause();
            int pos = mMediaPlayer.getCurrentPosition();

            SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                    MainFragment.RATING_PREF,
                    Context.MODE_PRIVATE).edit();

            editor.putInt(MainFragment.POS, pos).commit();

        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(mImageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            case CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getActivity().getContentResolver().openInputStream(mImageUri),
                                null, options
                        );
                        mDetailsImageView.setImageBitmap(bitmap);

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                                MainFragment.RATING_PREF,
                                Context.MODE_PRIVATE).edit();

                        editor.putString(mName + " image", mImageUri.toString()).commit();

                        if (getResources().getConfiguration().orientation ==
                                Configuration.ORIENTATION_LANDSCAPE) {
                            refreshMainFragment();
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri fromImageUri = null;
                    if (data != null) {
                        fromImageUri = data.getData();
                    }

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(fromImageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);

                    startActivityForResult(intent, CROP_PHOTO);
                }
                break;
            default:
                break;
        }
    }

    public void setName(String name) {
        mName = name;

        String[] friendNames = getResources().getStringArray(R.array.friend_names);
        String[] friendDetails = getResources().getStringArray(R.array.friend_details);

        for (int i = 0; i < friendNames.length; i++) {
            if (friendNames[i].equals(mName)) {
                mDetailsImageView.setImageResource(getResources().getIdentifier(
                        mName.toLowerCase(), "mipmap", getActivity().getPackageName()));
                mDetailsTextView.setText(friendDetails[i]);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                        MainFragment.RATING_PREF, Context.MODE_PRIVATE);

                mRatingBar.setRating(sharedPreferences.getFloat(friendNames[i], 0));

                String image = sharedPreferences.getString(friendNames[i] + " image", "");

                if (!"".equals(image)) {
                    try {
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getActivity().getContentResolver().openInputStream(Uri.parse(image)),
                                null, options
                        );
                        mDetailsImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }

                break;
            }
        }
    }

    private void refreshMainFragment() {
        Fragment main_fragment = getActivity().
                getSupportFragmentManager().findFragmentById(R.id.main_fragment);
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getActivity().getSupportFragmentManager().beginTransaction();

        fragmentTransaction.detach(main_fragment).attach(main_fragment).commit();
    }
}
