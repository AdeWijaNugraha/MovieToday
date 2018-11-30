package com.awn.app.movietoday.preference;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.awn.app.movietoday.R;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.FlagView;

public class CustomFlag extends FlagView {
    private TextView textView;
    private View view;

    public CustomFlag(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.flag_color_code);
        view = findViewById(R.id.flag_color_layout);
    }

    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {
        textView.setText("#" + colorEnvelope.getColorHtml());
        view.setBackgroundColor(colorEnvelope.getColor());
    }
}
