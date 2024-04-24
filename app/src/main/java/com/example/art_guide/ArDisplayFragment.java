package com.example.art_guide;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.art_guide.databinding.ArFragmentBinding;
import com.example.art_guide.databinding.ViewnodeRenderBinding;
import com.google.android.filament.ColorGrading;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.EngineInstance;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Renderer;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fragment responsible for displaying augmented reality content.
 */
public class ArDisplayFragment extends Fragment implements ArFragment.OnViewCreatedListener, Scene.OnUpdateListener {
    public final String TAG = "arfragment";
    public Node info = new Node();
    public ArFragmentBinding binding;
    public HandlerThread callbackThread = new HandlerThread("callback-worker");
    public Handler callbackHandler;
    public boolean IsInitialised = false;
    public ArFragment arFragment = null;

    public ViewRenderable viewRenderable = null;

    private List<Artwork> artworksList = new ArrayList<>();
    private MediaPlayer mediaPlayer = null;
    private Artwork lastDetectedArtwork = null;
    long startTime;

    public ArDisplayFragment() {
        super(R.layout.ar_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        binding = ArFragmentBinding.bind(view);
        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.ux_fragment);
        ArtworkParser parser = new ArtworkParser();
        artworksList = parser.parseArtwork(getContext());
        if (!IsInitialised) {
            callbackThread.start();
            callbackHandler = new Handler(callbackThread.getLooper());
            IsInitialised = true;
        }

        // Set click listeners for AR controls
        binding.textView2.setOnClickListener(v -> {
                arFragment.getArSceneView().getScene().addOnUpdateListener(this);
                binding.trackingState.setVisibility(View.VISIBLE);
        });

        binding.textView3.setOnClickListener(v -> {
            if (lastDetectedArtwork != null) {
                playArtworkAudio(lastDetectedArtwork);
            }
        });

        if (arFragment != null) {
            arFragment.setOnViewCreatedListener(this);
        }
    }

    public void onViewCreated(ArFragment arFragment, ArSceneView arSceneView) {
        // Set up rendering options
        Renderer renderer = arSceneView.getRenderer();
        if(renderer != null) {
            ColorGrading colorGrading = new ColorGrading.Builder()
                    .toneMapping(ColorGrading.ToneMapping.FILMIC)
                    .build(EngineInstance.getEngine().getFilamentEngine());
            renderer.getFilamentView().setColorGrading(colorGrading);
        }
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        onUpdateFrame(frameTime);
    }

    public void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (arFragment != null) {
            arFragment.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (arFragment != null) {
            arFragment.onPause();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (arFragment != null) {
            arFragment.onDetach();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (arFragment != null) {
            arFragment.onDestroy();
        }
    }

    /**
     * Play audio associated with the artwork.
     * @param artwork The artwork for which audio should be played.
     */
    public void playArtworkAudio(Artwork artwork) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release any existing MediaPlayer to avoid memory leaks
        }
        String audioFilename = artwork.getDescription().replace(".mp3", "");
        int audioResID = getResources().getIdentifier(audioFilename, "raw", getContext().getPackageName());
        if (audioResID != 0) {
            mediaPlayer = MediaPlayer.create(getContext(), audioResID);
            mediaPlayer.start();
        } else {
            Log.e(TAG, "Audio file not found for: " + audioFilename);
        }
    }

    /**
     * Normalizes the name by converting it to lowercase and removing special characters.
     * @param name The name to normalize.
     * @return The normalized name.
     */
    private String normalizeName(String name) {
        return name.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Finds an artwork by label.
     * @param label The label to search for.
     * @return The artwork matching the label, or null if not found.
     */
    private Artwork findArtworkByLabel(String label) {
        String normalizedLabel = normalizeName(label);
        for (Artwork artwork : artworksList) {
            if (normalizeName(artwork.getName()).equals(normalizedLabel)) {
                return artwork;
            }
        }
        return null;
    }

    /**
     * Prepares the image name by removing the file extension and replacing spaces with underscores.
     * @param imageName The name of the image file.
     * @return The prepared image name.
     */
    private String prepareImageName(String imageName) {
        if (imageName.contains(".")) {
            imageName = imageName.substring(0, imageName.lastIndexOf('.'));
        }
        imageName = imageName.toLowerCase().replaceAll("\\s+", "_");
        return imageName;
    }

    /**
     * Functional interface for handling bitmap callback.
     */
    @FunctionalInterface
    public interface BitmapCallback {
        void onBitmapReady(Bitmap bitmap);
    }

    /**
     * Updates the frame with AR content.
     * @param frametime The frame time.
     */
    public void onUpdateFrame(FrameTime frametime) {
        if (arFragment == null){
            return;
        }
        startTime = System.currentTimeMillis();
        ArSceneView arSceneView = arFragment.getArSceneView();
        if (arSceneView == null) {
            return;
        }
        Frame frame = arSceneView.getArFrame();
        if (frame == null) {
            return;
        }

        copyPixelFromView(arSceneView, bitmap -> {
            Bitmap targetBitmap = Bitmap.createBitmap(bitmap);
            CustomObjectDetector dd = new CustomObjectDetector(targetBitmap, detectedObject -> {
            Toast.makeText(getContext(), "tracking" + detectedObject.getTrackingId() + " " + detectedObject.getLabels().get(0).getText(),
                    Toast.LENGTH_SHORT).show();
            String label = detectedObject.getLabels().get(0).getText();
            Artwork matchedArtwork = findArtworkByLabel(label);
            if (matchedArtwork != null) {
                lastDetectedArtwork = matchedArtwork;}
            Log.e("detecting", detectedObject.getTrackingId().toString());
            arFragment.getArSceneView().getScene().removeOnUpdateListener(ArDisplayFragment.this);
                if (matchedArtwork != null) {
                    loadModels(matchedArtwork);
                } else {
                    Log.e("ArtworkFinder", "No matching artwork found for label: " + label);
                }
        });
        dd.useCustomObjectDetector();
        });
    }

    /**
     * Copies pixels from the view.
     * @param views The surface view.
     * @param callback The bitmap callback.
     */
    public void copyPixelFromView(SurfaceView views, BitmapCallback callback){
        Bitmap bitmap = Bitmap.createBitmap(
                views.getWidth(),
                views.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        PixelCopy.request(views, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                Log.i(TAG, "Copying ArFragment view");
                callback.onBitmapReady(bitmap);
                Log.i(TAG, "Copied ArFragment view");
            } else {
                Log.e(TAG, "Failed to copy ArFragment view");
            }
        }, callbackHandler);
    }

    /**
     * Loads models for the artwork.
     * @param artwork The artwork to load models for.
     */
    public void loadModels(Artwork artwork){
        ViewnodeRenderBinding root = ViewnodeRenderBinding.inflate(getLayoutInflater());
        root.label.setText(artwork.getName());
        root.artist.setText(artwork.getArtist());
        root.size.setText(artwork.getSize());
        root.year.setText(artwork.getYear());
        root.material.setText(artwork.getMaterials());
        String imageName = prepareImageName(artwork.getImage());
        int imageResID = getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());
        if (imageResID != 0) { // Check if the resource ID is found
            root.image.setImageResource(imageResID);
        } else {
            Log.e(TAG, "Image resource not found for: " + artwork.getImage());
        }

        ViewRenderable.builder()
                .setView(requireContext(), root.getRoot())
                .build()
                .thenAccept(viewRenderable -> {
                    ArDisplayFragment.this.viewRenderable = viewRenderable;
                    ArDisplayFragment.this.viewRenderable.setShadowCaster(false);
                    ArDisplayFragment.this.viewRenderable.setShadowReceiver(false);
                    info.setRenderable(ArDisplayFragment.this.viewRenderable);
                    Toast.makeText(getContext(), "Model Loaded", Toast.LENGTH_SHORT).show();
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    Log.i(TAG, "Loading time: " + duration + "ms");
                    addDrawable();
                })
                .exceptionally(throwable -> {
                    Toast.makeText(requireContext(), "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                });
    }

    public void addDrawable(){
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            Vector3 center = screenCenter(frame);
            List<HitResult> hitTest = frame.hitTest(center.x, center.y);
            if (!hitTest.isEmpty()) {
                HitResult hitResult = hitTest.get(0);
                Session session = arFragment.getArSceneView().getSession();
                if (session != null) {
                    Anchor modelAnchor = session.createAnchor(hitResult.getHitPose());
                    AnchorNode anchorNode = new AnchorNode(modelAnchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                    transformableNode.getScaleController().setMaxScale(0.5f);
                    transformableNode.getScaleController().setMinScale(0.1f);
                    transformableNode.setParent(anchorNode);
                    transformableNode.setRenderable(viewRenderable);

                    transformableNode.setWorldPosition(new Vector3(
                            modelAnchor.getPose().tx(),
                            modelAnchor.getPose().ty(),
                            modelAnchor.getPose().tz()
                    ));
                }
            }
        }
    }

    public Vector3 screenCenter(Frame frame) {
        if (binding == null) {
            throw new IllegalStateException("Binding is not initialized.");
        }
        View vw = binding.getRoot();
        if (vw == null) {
            throw new IllegalStateException("Root view is not available.");
        }
        return new Vector3(vw.getWidth() / 2f, vw.getHeight() / 2f, 0f);
    }
}
