package com.theconnoisseur.android.Activities;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.theconnoisseur.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment {
    public static final String TAG = TutorialFragment.class.getSimpleName();
    private int mPosition = 0;


    private TextView mNumber1;
    private TextView mNumber2;
    private TextView mNumber3;
    private TextView mNumber4;
    private TextView mTutorialText;
    private RelativeLayout mTutorialScreen1;
    private RelativeLayout mTutorialScreen2;
    private RelativeLayout mTutorialScreen3;
    private RelativeLayout mTutorialScreen4;
    private TextView mExampleWord;

    private Typeface plantin;
    private Typeface roboto;

    public TutorialFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.tutorial_fragment, container, false);

        mNumber1 = (TextView) rootView.findViewById(R.id.tutorial_1);
        mNumber2 = (TextView) rootView.findViewById(R.id.tutorial_2);
        mNumber3 = (TextView) rootView.findViewById(R.id.tutorial_3);
        mNumber4 = (TextView) rootView.findViewById(R.id.tutorial_4);
        mTutorialText = (TextView) rootView.findViewById(R.id.tutorial_text);
        mTutorialScreen1 = (RelativeLayout) rootView.findViewById(R.id.tutorial_visual_1);
        mTutorialScreen2 = (RelativeLayout) rootView.findViewById(R.id.tutorial_visual_2);
        mTutorialScreen3 = (RelativeLayout) rootView.findViewById(R.id.tutorial_visual_3);
        mTutorialScreen4 = (RelativeLayout) rootView.findViewById(R.id.tutorial_visual_4);

        mExampleWord = (TextView) rootView.findViewById(R.id.example_word);
        plantin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/PlantinMTStd_Semibold.ttf");
        roboto = Typeface.createFromAsset(getActivity().getAssets(), "fonts/RobotoCondensed-Regular.ttf");

        setScreenVisuals(mPosition);

        return rootView;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    private void setScreenVisuals(int position) {
        emptyNumberBackgroundsAndImages();
        switch (position) {
            case 0: //Word
                mNumber1.setBackgroundResource(R.drawable.ring); mNumber1.setTextColor(getResources().getColor(R.color.flag_blue));
                mTutorialText.setText(getString(R.string.tutorial_screen_1));
                mTutorialScreen1.setVisibility(View.VISIBLE);
                mExampleWord.setTypeface(plantin);
                break;
            case 1: //Lives
                mNumber2.setBackgroundResource(R.drawable.ring); mNumber2.setTextColor(getResources().getColor(R.color.flag_blue));
                mTutorialText.setText(getString(R.string.tutorial_screen_2));
                mTutorialScreen2.setVisibility(View.VISIBLE);
                break;
            case 2: //Listen
                mNumber3.setBackgroundResource(R.drawable.ring); mNumber3.setTextColor(getResources().getColor(R.color.flag_blue));
                mTutorialText.setText(getString(R.string.tutorial_screen_3));
                mTutorialScreen3.setVisibility(View.VISIBLE);
                break;
            case 3: //Record
                mNumber4.setBackgroundResource(R.drawable.ring); mNumber4.setTextColor(getResources().getColor(R.color.flag_blue));
                mTutorialText.setText(getString(R.string.tutorial_screen_4));
                mTutorialScreen4.setVisibility(View.VISIBLE);
                break;
        }
        mTutorialText.setTypeface(roboto);
    }

    private void emptyNumberBackgroundsAndImages() {
        mNumber1.setBackgroundResource(0); mNumber1.setTextColor(getResources().getColor(R.color.green));
        mNumber2.setBackgroundResource(0); mNumber2.setTextColor(getResources().getColor(R.color.green));
        mNumber3.setBackgroundResource(0); mNumber3.setTextColor(getResources().getColor(R.color.green));
        mNumber4.setBackgroundResource(0); mNumber4.setTextColor(getResources().getColor(R.color.green));
        mTutorialScreen1.setVisibility(View.GONE);
        mTutorialScreen2.setVisibility(View.GONE);
        mTutorialScreen3.setVisibility(View.GONE);
        mTutorialScreen4.setVisibility(View.GONE);
    }


}
