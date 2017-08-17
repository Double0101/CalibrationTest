package com.zjgsu.ai.calibrationtest;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Double on 2017/4/24.
 */

public class AnnotationLab {
    private static final String TAG = "AnnotationLab";
    private ArrayList<AnnotatedImage> annotatedImages;
    private static AnnotationLab sAnnotationLab;
    private static final String FILENAME = "annotatedImages.json";
    private AnnotationSerializer mSerializer;

    private Context mAppContext;

    public static AnnotationLab get(Context c) {
        if (sAnnotationLab == null) {
            sAnnotationLab = new AnnotationLab(c.getApplicationContext());
        }

        return sAnnotationLab;
    }

    public ArrayList<AnnotatedImage> getAnnotatedImages() {
        return annotatedImages;
    }

    private AnnotationLab(Context appContext) {
        mAppContext = appContext;
        annotatedImages = new ArrayList<AnnotatedImage>();
        mSerializer = new AnnotationSerializer(mAppContext, FILENAME);
        try {
            annotatedImages = mSerializer.loadAnnotations();
        } catch (Exception e) {
            annotatedImages = new ArrayList<AnnotatedImage>();
        }
    }

    public void addAnnotation(AnnotatedImage c) {
        annotatedImages.add(c);
    }

    public boolean saveAnnotations() {
        try {
            mSerializer.saveAnnotations(annotatedImages);
            Log.d(TAG, "annotatedImages saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving annotatedImages: ", e);
            return false;
        }
    }
}
