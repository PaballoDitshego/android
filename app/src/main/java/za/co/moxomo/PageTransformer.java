package za.co.moxomo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Paballo Ditshego on 8/2/15.
 */
public class PageTransformer implements ViewPager.PageTransformer {


    private int mScreenXOffset;

    public PageTransformer(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        mScreenXOffset = displayMetrics.widthPixels / 2;
    }


    @Override
    public void transformPage(View page, float position) {
        final float transformValue = Math.abs(Math.abs(position) - 1);
        // apply fade effect
        ViewHelper.setAlpha(page, transformValue);
        if (position > 0) {
            // apply zoom effect only for pages to the right
            ViewHelper.setScaleX(page, transformValue);
            ViewHelper.setScaleY(page, transformValue);
            ViewHelper.setPivotX(page, 0.5f);
            final float translateValue = position * -mScreenXOffset;
            if (translateValue > -mScreenXOffset) {
                ViewHelper.setTranslationX(page, translateValue);
            } else {
                ViewHelper.setTranslationX(page, 0);
            }
        }
    }

}

