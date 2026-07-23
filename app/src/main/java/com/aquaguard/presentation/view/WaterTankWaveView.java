package com.aquaguard.presentation.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class WaterTankWaveView extends View {
    private int levelPercentage = 50; // Default 50%
    private float waveOffset = 0f;
    private final Paint tankPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path wavePath = new Path();
    private final Path circleClipPath = new Path();
    private ValueAnimator animator;

    public WaterTankWaveView(Context context) {
        super(context);
        init();
    }

    public WaterTankWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        tankPaint.setColor(Color.parseColor("#121e2d"));
        tankPaint.setStyle(Paint.Style.FILL);

        animator = ValueAnimator.ofFloat(0f, (float) (2 * Math.PI));
        animator.setDuration(2000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            waveOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public void setLevelPercentage(int percentage) {
        this.levelPercentage = Math.max(0, Math.min(100, percentage));
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) {
            animator.cancel();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 10;
        int cx = width / 2;
        int cy = height / 2;

        // Draw Tank Outer Ring Boundary
        tankPaint.setColor(Color.parseColor("#263238"));
        tankPaint.setStyle(Paint.Style.STROKE);
        tankPaint.setStrokeWidth(8f);
        canvas.drawCircle(cx, cy, radius, tankPaint);

        // Draw Tank Inner Background
        tankPaint.setColor(Color.parseColor("#0b1218"));
        tankPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius - 4, tankPaint);

        // Save canvas state for wave clipping
        canvas.save();

        // Create Circle Clipping Boundary
        circleClipPath.reset();
        circleClipPath.addCircle(cx, cy, radius - 4, Path.Direction.CW);
        canvas.clipPath(circleClipPath);

        // Calculate Water Y-Coordinate based on level percentage
        float waterHeight = (radius * 2) * (1f - (levelPercentage / 100f));
        float waterY = (cy - radius) + waterHeight;

        // Draw Water Wave
        wavePath.reset();
        wavePath.moveTo(cx - radius, waterY);

        float waveAmplitude = 12f; // Wave height amplitude
        float waveFrequency = 0.03f; // Wave waveFrequency / frequency

        for (float x = cx - radius; x <= cx + radius; x++) {
            float y = waterY + waveAmplitude * (float) Math.sin((x * waveFrequency) + waveOffset);
            wavePath.lineTo(x, y);
        }

        wavePath.lineTo(cx + radius, cy + radius);
        wavePath.lineTo(cx - radius, cy + radius);
        wavePath.close();

        // Setup Gradient Shader
        Shader shader = new LinearGradient(
                cx, waterY, cx, cy + radius,
                Color.parseColor("#80deea"), // Top water color (Cyan)
                Color.parseColor("#01579b"), // Bottom water color (Dark Blue)
                Shader.TileMode.CLAMP
        );
        wavePaint.setShader(shader);
        wavePaint.setStyle(Paint.Style.FILL);

        canvas.drawPath(wavePath, wavePaint);

        // Restore canvas state
        canvas.restore();
    }
}
