package com.example.art_guide;

import com.google.mlkit.vision.objects.DetectedObject;

@FunctionalInterface
public interface IdAnalyzer {
    void analyze(DetectedObject detectedObject);
}
