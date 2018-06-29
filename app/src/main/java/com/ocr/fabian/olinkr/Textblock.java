package com.ocr.fabian.olinkr;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Textblock extends LinearLayout{

    private TextView textView;
    private boolean isCollapsed;
    private boolean canCollapse;

    private CheckBox checkBox;


    private int maxLen = 40;
    private String fullText;
    private String collapsedText;


    private Context _context;

    public Textblock(Context context) {
        super(context);
        _context = context;

        final LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10,10,10,10);
        this.setLayoutParams(layoutParams);
        this.setBackgroundColor(getResources().getColor(R.color.background_white));
        this.setOrientation(LinearLayout.HORIZONTAL);

        final LayoutParams textLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textLayoutParams.setMargins(10,16,10,8);
        textView = new TextView(context);
        textView.setLayoutParams(textLayoutParams);
        textView.setBackgroundColor(getResources().getColor(R.color.background_white));
        textView.setTextColor(Color.BLACK);

        this.addView(textView);
    }

    public void setText(String text) {
        fullText = text;

        // true if text longer than maxLen
        canCollapse = false; //text.length()>maxLen;

        // add collapsible text if necessary and collapse
        if(canCollapse) {
            collapsedText = text.substring(0,maxLen-3) + "...";
            isCollapsed = false;
            collapse();
        } else {
            collapsedText = null;
            textView.setText(fullText);
        }
    }


    private void collapse() {
        if(canCollapse) {
            if (isCollapsed) {
                textView.setSingleLine(false);
                textView.setText(fullText);
                isCollapsed = false;
            } else {
                textView.setSingleLine(true);
                textView.setText(collapsedText);
                isCollapsed = true;
            }
        }
    }

    /*** https://stackoverflow.com/questions/38509419/highlight-text-inside-a-textview ***/
    public void highlightText(String input) {
        SpannableString spannableString = new SpannableString(textView.getText());
        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);

        for (BackgroundColorSpan span: backgroundSpans) {
            spannableString.removeSpan(span);
        }
        //Search for all occurrences of the keyword in the string
        int indexOfKeyword = spannableString.toString().indexOf(input);

        while (indexOfKeyword >= 0) {
            //Create a background color span on the keyword
            spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexOfKeyword, indexOfKeyword + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //Get the next index of the keyword
            indexOfKeyword = spannableString.toString().indexOf(input, indexOfKeyword + input.length());
        }
        textView.setText(spannableString);
    }


        public void onClick() {
        collapse();
    }

    public void onLongClick() {
        textView.setTextIsSelectable(true);
    }
}
