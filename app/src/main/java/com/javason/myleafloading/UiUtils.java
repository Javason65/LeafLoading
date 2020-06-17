package com.javason.myleafloading;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class UiUtils {
    //获取屏幕宽度像素
    public static int getScreenWidthPixels(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    //屏幕适配
    public static int dp2px(Context context, int dp) {
        return (int)(dp*getScreenDensity(context)+0.5f);
//    return(int)(context.getResources().getDisplayMetrics().density*dp+0.5f);
    }
    //获取屏幕密度Density
    public static float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }
}
