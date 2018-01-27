package com.rhino.ripplediffusionview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rhino.ripplediffusionview.view.RippleDiffusionView;

public class MainActivity extends AppCompatActivity {

    private RippleDiffusionView mRippleDiffusionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRippleDiffusionView = findViewById(R.id.RippleDiffusionView);

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRippleDiffusionView.isAnimStarted()) {
                    mRippleDiffusionView.stopAnim();
                    ((Button)findViewById(R.id.start)).setText("start");
                } else {
                    mRippleDiffusionView.startAnim();
                    ((Button)findViewById(R.id.start)).setText("stop");
                }
            }
        });

        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RippleDiffusionView.Style.FILL == mRippleDiffusionView.getStyle()) {
                    mRippleDiffusionView.setStyle(RippleDiffusionView.Style.STROKE);
                } else {
                    mRippleDiffusionView.setStyle(RippleDiffusionView.Style.FILL);
                }
            }
        });
    }
}
