package graphics.renderEngine.postProcessing;

/**
 * Represents a post-processing effect to apply to the rendered image.
 * (effects can be applied when rendering to a texture & using a screen quad)
 */
public enum PostProcessingEffect {
    NONE(),
    INVERT_COLOURS(),
    GREYSCALE(),
    SHARPEN(new float[]{
            -1, -1, -1,
            -1,  9, -1,
            -1, -1, -1
    }),
    BLUR(new float[]{
            1.0f / 16, 2.0f / 16, 1.0f / 16,
            2.0f / 16, 4.0f / 16, 2.0f / 16,
            1.0f / 16, 2.0f / 16, 1.0f / 16
    }),
    EDGE_DETECTION(new float[]{
            1, 1, 1,
            1, -8, 1,
            1, 1, 1
    });

    private final float[] kernel;   // kernel / convolution matrix for effect

    PostProcessingEffect(){
        kernel = null;
    }

    PostProcessingEffect(float[] kernel_m){
        kernel = kernel_m;
    }

    public float[] getKernel() {
        return kernel;
    }
}
