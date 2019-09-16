package com.hon.sunny.ui.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.hon.sunny.R;
import com.hon.sunny.ui.main.MainActivity;
import com.hon.sunny.utils.Constants;
import com.hon.sunny.vo.event.ChangeCityEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hon.sunny.utils.Constants.CHANGE_ICONS;

/**
 * Created by Frank Hon on 2019-09-14 12:42.
 * E-mail: frank_hon@foxmail.com
 */
public class SettingIconDialog {

    private DialogInterface.OnClickListener onClickListener;
    private AlertDialog alertDialog;

    private SharedPreferences sharedPreferences;

    @BindView(R.id.iv_type_one)
    ImageView typeOne;
    @BindView(R.id.iv_type_two)
    ImageView typeTwo;
    @BindView(R.id.rb_one)
    RadioButton buttonOne;
    @BindView(R.id.rb_two)
    RadioButton buttonTwo;

    private int checked;

    public SettingIconDialog(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.layout_icon_dialog, null, false);
        alertDialog = new AlertDialog.Builder(context)
                .setView(dialogLayout)
                .setPositiveButton("Sure", (dialog, which) -> {
                            if (isChanged()) {
                                if (onClickListener != null) {
                                    onClickListener.onClick(dialog, which);
                                }
                                sharedPreferences.edit().putInt(CHANGE_ICONS, checked).apply();
                            }

                            alertDialog.dismiss();
                        }
                )
                .create();

        ButterKnife.bind(this, dialogLayout);

        typeOne.setOnClickListener(v -> checkOne());

        typeTwo.setOnClickListener(v -> checkTwo());

        checked = sharedPreferences.getInt(CHANGE_ICONS, 1);
        if (checked == 1) {
            checkOne();
        } else if (checked == 2) {
            checkTwo();
        }

    }

    public void setOnClickListener(DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void show() {
        alertDialog.show();
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    public int getChecked() {
        return checked;
    }

    public boolean isChanged() {
        int origin = sharedPreferences.getInt(CHANGE_ICONS, 0);
        return checked != origin;
    }

    private void checkOne() {
        buttonOne.setChecked(true);
        buttonTwo.setChecked(false);
        checked = 1;
    }

    private void checkTwo() {
        buttonOne.setChecked(false);
        buttonTwo.setChecked(true);
        checked = 2;
    }

}
