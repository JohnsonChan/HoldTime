package com.czs.gtd.api;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.czs.gtd.R;

/**
 * 封装了picker 顶部的确定取消按钮及背景样式
 * 
 * @author asus
 * 
 */
public abstract class PickerPopupWindow extends DimPopupWindow implements
        OnClickListener {
    private ImageButton leftButton;
    private ImageButton rightButton;
    private FrameLayout content;
    private View contentView;
    private TextView title;
    private boolean autoDismiss;

    public PickerPopupWindow(Context context) {
        super(context);
        contentView = LayoutInflater.from(context).inflate(
                R.layout.picker_popupwindow, null);
        leftButton = (ImageButton) contentView
                .findViewById(R.id.ib_popwin_select_ok);
        rightButton = (ImageButton) contentView
                .findViewById(R.id.ib_popwin_select_cancel);
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        content = (FrameLayout) contentView
                .findViewById(R.id.fl_popwin_content);
        title = (TextView) contentView.findViewById(R.id.tv_popwin_title);
        title.setText(getTitle());
        autoDismiss = true;
    }

    public void setContentView(View contentView) {
        content.addView(contentView);
        super.setContentView(this.contentView);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public boolean isAutoDismiss() {
        return autoDismiss;
    }

    public void setAutoDismiss(boolean autoDismiss) {
        this.autoDismiss = autoDismiss;
    }

    protected abstract String getTitle();

    protected void onLeftButtonClick() {
        if (isAutoDismiss()) {
            this.dismiss();
        }
    }

    protected void onRightButtonClick() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v == leftButton) {
            onLeftButtonClick();
        } else if (v == rightButton) {
            onRightButtonClick();
        }
    }

}
