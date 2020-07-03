package com.hon.sunny.ui.splash;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.hon.mylogger.MyLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 2017/9/18.
 * E-mail:frank_hon@foxmail.com
 */

public class SvgHelper {
    private final Paint mSourcePaint;
    private SVG mSvg;
    private HandlerThread mLoadSvgTask;

    public SvgHelper(Paint sourcePaint) {
        mSourcePaint = sourcePaint;
    }

    public void load(Context context, int svgResource, int width, int height, OnSvgResourceLoadedListener listener) {
        mLoadSvgTask = new HandlerThread("LoadSvgTask");
        mLoadSvgTask.start();
        Handler handler = new Handler(mLoadSvgTask.getLooper());
        handler.post(() -> {
            try {
                mSvg = SVG.getFromResource(context, svgResource);
                mSvg.setDocumentPreserveAspectRatio(PreserveAspectRatio.UNSCALED);
                if (listener != null) {
                    List<SvgPath> paths = getPathsForViewport(width, height);
                    listener.onSvgResourceLoaded(paths);
                }
            } catch (SVGParseException e) {
                MyLogger.e("Could not load specified SVG resource", e);
            }
        });
    }

    public List<SvgPath> getPathsForViewport(int width, int height) {
        List<SvgPath> paths = new ArrayList<>();
        Canvas canvas = new Canvas() {
            private final Matrix mMatrix = new Matrix();

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void drawPath(Path path, Paint paint) {
                Path dst = new Path();
                //noinspection deprecation
                getMatrix(mMatrix);
                path.transform(mMatrix, dst);
                paths.add(new SvgPath(dst, new Paint(mSourcePaint)));
            }
        };

        RectF viewBox = mSvg.getDocumentViewBox();
        float scale = Math.min(width / viewBox.width(), height / viewBox.height());

        canvas.translate(
                (width - viewBox.width() * scale) / 2.0f,
                (height - viewBox.height() * scale) / 2.0f);
        canvas.scale(scale, scale);

        mSvg.renderToCanvas(canvas);

        return paths;
    }

    public void cancel(){
        mLoadSvgTask.quit();
    }

    public static class SvgPath {
        private static final Region sRegion = new Region();
        private static final Region sMaxClip = new Region(
                Integer.MIN_VALUE, Integer.MIN_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE);

        public final Path path;
        public final Paint paint;
        public final float length;
        public final Rect bounds;

        public SvgPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;

            PathMeasure measure = new PathMeasure(path, false);
            this.length = measure.getLength();

            sRegion.setPath(path, sMaxClip);
            bounds = sRegion.getBounds();
        }
    }

    interface OnSvgResourceLoadedListener {
        void onSvgResourceLoaded(List<SvgPath> paths);
    }
}
