package com.example.art_guide;

import com.google.mlkit.common.model.LocalModel;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.DetectedObject;

/**
 * CustomObjectDetector class for detecting objects in a provided Bitmap image.
 */
public class CustomObjectDetector {
    public Bitmap image;
    public IdAnalyzer idAnalyzer;

    /**
     * Constructor for CustomObjectDetector.
     * @param image The Bitmap image to be processed.
     * @param idAnalyzer The IdAnalyzer instance for analyzing detected object IDs.
     */
    public CustomObjectDetector(Bitmap image, IdAnalyzer idAnalyzer) {
        this.image = image;
        this.idAnalyzer = idAnalyzer;
    }
    /**
     * LocalModel instance for loading a custom object detection model.
     */
    LocalModel localModel =  new LocalModel.Builder()
            .setAssetFilePath("ArtGuideModel_with_metadata.tflite").build();

    /**
     * CustomObjectDetectorOptions instance for configuring object detection options.
     */
    CustomObjectDetectorOptions options = new CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                .enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build();

    /**
     * ObjectDetector instance for performing object detection.
     */
    ObjectDetector objectDetector = ObjectDetection.getClient(options);

    /**
     * Performs object detection using the custom object detector.
     */
    public void useCustomObjectDetector() {
        if (!Constants.iscRunning) {
            Constants.iscRunning = true;
            InputImage inputImage = InputImage.fromBitmap(image, 0);
            objectDetector.process(inputImage)
                    .addOnSuccessListener(results -> {
                        Log.e("labels", String.valueOf(results.size()));
                        for (DetectedObject detectedObject : results) {
                            if (detectedObject.getLabels().size() > 0) {
                                idAnalyzer.analyze(detectedObject);
                            }
                        }
                        Constants.iscRunning = false;
                    }).addOnFailureListener(e -> {
                        Constants.iscRunning = false;
                        e.printStackTrace();
                    }).addOnCompleteListener(task -> Constants.iscRunning = false);
        }
    }
}
