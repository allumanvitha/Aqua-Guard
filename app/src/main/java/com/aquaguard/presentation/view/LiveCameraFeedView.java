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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class LiveCameraFeedView extends View {
    private boolean valveOpen = true;
    private float flowRate = 12.5f;
    private int waterLevelPct = 68;
    private int selectedCam = 1; // 1: Tank, 2: Valve
    private String filterMode = "normal"; // "normal", "nv", "thermal"
    
    private float panX = 0f;
    private float panY = 0f;
    private float zoom = 1.0f;
    
    private float staticIntensity = 0f;
    private long animTick = 0;
    
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path wavePath = new Path();
    private final Path structurePath = new Path();
    private final Random random = new Random();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private ValueAnimator animator;

    public LiveCameraFeedView(Context context) {
        super(context);
        init();
    }

    public LiveCameraFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        animator = ValueAnimator.ofFloat(0f, 100f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            animTick++;
            if (staticIntensity > 0f) {
                staticIntensity = Math.max(0f, staticIntensity - 0.05f);
            }
            invalidate();
        });
        animator.start();
    }

    public void setTelemetry(boolean valveOpen, float flowRate, int waterLevelPct) {
        this.valveOpen = valveOpen;
        this.flowRate = flowRate;
        this.waterLevelPct = waterLevelPct;
        invalidate();
    }

    public void setSelectedCam(int cam) {
        this.selectedCam = cam;
        this.staticIntensity = 1.0f;
        invalidate();
    }

    public void setFilterMode(String mode) {
        this.filterMode = mode;
        this.staticIntensity = 0.5f;
        invalidate();
    }

    public void setPtz(float panX, float panY, float zoom) {
        this.panX = panX;
        this.panY = panY;
        this.zoom = zoom;
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

        // 1. Draw CCTV Screen Background
        canvas.drawColor(Color.parseColor("#05080c"));

        // Save state for PTZ (Pan, Tilt, Zoom)
        canvas.save();
        canvas.translate(panX, panY);
        canvas.scale(zoom, zoom, width / 2f, height / 2f);

        // 2. Draw Subtle Background Grid
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.parseColor("#05ffffff")); // Very subtle white grid
        int gridSpacing = 50;
        for (int i = 0; i < width; i += gridSpacing) {
            canvas.drawLine(i, 0, i, height, paint);
        }
        for (int j = 0; j < height; j += gridSpacing) {
            canvas.drawLine(0, j, width, j, paint);
        }

        if (selectedCam == 1) {
            drawTankScene(canvas, width, height);
        } else {
            drawValveScene(canvas, width, height);
        }

        // Restore PTZ modifications
        canvas.restore();

        // 3. Draw Scanlines & Filter Effects
        drawPostEffects(canvas, width, height);

        // 4. Draw HUD Text Overlays
        drawHudOverlay(canvas, width, height);
    }

    private void drawTankScene(Canvas canvas, int width, int height) {
        float tankWidth = 140f;
        float tankHeight = 160f;
        float tankLeft = (width - tankWidth) / 2f;
        float tankTop = (height - tankHeight) / 2f + 10f;
        float tankRight = tankLeft + tankWidth;
        float tankBottom = tankTop + tankHeight;

        // Pipe inlet
        paint.setStyle(Paint.Style.FILL);
        int pipeColor = filterMode.equals("thermal") ? Color.parseColor("#311b92") : Color.parseColor("#1e2d3b");
        paint.setColor(pipeColor);
        canvas.drawRect(tankLeft - 40f, tankTop + 10f, tankLeft, tankTop + 25f, paint);

        // Tank walls
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        int structColor = filterMode.equals("thermal") ? Color.parseColor("#1e88e5") : Color.parseColor("#4fc3f7");
        paint.setColor(structColor);
        canvas.drawLine(tankLeft, tankTop, tankLeft, tankBottom, paint);
        canvas.drawLine(tankRight, tankTop, tankRight, tankBottom, paint);
        paint.setStrokeWidth(5f);
        canvas.drawLine(tankLeft, tankBottom, tankRight, tankBottom, paint);

        // Graduation tick indicators
        paint.setStrokeWidth(1f);
        int labelColor = filterMode.equals("nv") ? Color.parseColor("#2ec471") : Color.parseColor("#80ffffff");
        paint.setColor(labelColor);
        for (int percent : new int[]{25, 50, 75, 100}) {
            float gy = tankBottom - (tankHeight * (percent / 100f));
            canvas.drawLine(tankLeft - 8f, gy, tankLeft, gy, paint);
        }

        // Water Wave rendering inside tank boundaries
        if (waterLevelPct > 0) {
            canvas.save();
            // Clip to tank boundary
            RectF tankRect = new RectF(tankLeft + 2f, tankTop, tankRight - 2f, tankBottom - 2f);
            canvas.clipRect(tankRect);

            float currentWaterHeight = tankHeight * (waterLevelPct / 100f);
            float waterY = tankBottom - currentWaterHeight;

            wavePath.reset();
            wavePath.moveTo(tankLeft, waterY);
            float amplitude = 5f;
            float freq = 0.05f;
            for (float x = tankLeft; x <= tankRight; x++) {
                float y = waterY + amplitude * (float) Math.sin((x * freq) + (animTick * 0.1f));
                wavePath.lineTo(x, y);
            }
            wavePath.lineTo(tankRight, tankBottom);
            wavePath.lineTo(tankLeft, tankBottom);
            wavePath.close();

            paint.setStyle(Paint.Style.FILL);
            Shader waterShader;
            if (filterMode.equals("thermal")) {
                waterShader = new LinearGradient(width/2f, waterY, width/2f, tankBottom,
                        Color.parseColor("#0d47a1"), Color.parseColor("#000022"), Shader.TileMode.CLAMP);
            } else if (filterMode.equals("nv")) {
                waterShader = new LinearGradient(width/2f, waterY, width/2f, tankBottom,
                        Color.parseColor("#662ec471"), Color.parseColor("#112ec471"), Shader.TileMode.CLAMP);
            } else {
                waterShader = new LinearGradient(width/2f, waterY, width/2f, tankBottom,
                        Color.parseColor("#9980deea"), Color.parseColor("#cc01579b"), Shader.TileMode.CLAMP);
            }
            paint.setShader(waterShader);
            canvas.drawPath(wavePath, paint);
            paint.setShader(null);
            canvas.restore();
        }

        // Droplets (Falling water animation)
        if (valveOpen && flowRate > 0) {
            paint.setStyle(Paint.Style.FILL);
            int dropColor = filterMode.equals("thermal") ? Color.parseColor("#ff9100") : 
                             (filterMode.equals("nv") ? Color.parseColor("#2ec471") : Color.parseColor("#80deea"));
            paint.setColor(dropColor);

            float dropletInletX = tankLeft - 8f;
            float dropletInletY = tankTop + 17f;
            float dropSpacing = 40f;
            float dropSpeed = 5f;
            float maxDropletY = tankBottom - (tankHeight * (waterLevelPct / 100f));

            for (int j = 0; j < 4; j++) {
                float dropY = dropletInletY + ((animTick * dropSpeed + j * dropSpacing) % (tankBottom - dropletInletY));
                if (dropY < maxDropletY) {
                    canvas.drawCircle(dropletInletX, dropY, 3f, paint);
                }
            }
        }
    }

    private void drawValveScene(Canvas canvas, int width, int height) {
        float pipeY = height / 2f;
        float pipeHeight = 30f;

        // Draw Pipes
        paint.setStyle(Paint.Style.FILL);
        int pipeColor = filterMode.equals("thermal") ? Color.parseColor("#15152a") : Color.parseColor("#101d28");
        paint.setColor(pipeColor);
        canvas.drawRect(0, pipeY - pipeHeight / 2f, width, pipeY + pipeHeight / 2f, paint);

        // Pipe outlines
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        int pipeOutline = filterMode.equals("thermal") ? Color.parseColor("#3f51b5") : Color.parseColor("#264f6e");
        paint.setColor(pipeOutline);
        canvas.drawLine(0, pipeY - pipeHeight / 2f, width, pipeY - pipeHeight / 2f, paint);
        canvas.drawLine(0, pipeY + pipeHeight / 2f, width, pipeY + pipeHeight / 2f, paint);

        // Valve Housing Solenoid Box
        float valveWidth = 60f;
        float valveHeight = 80f;
        float valveLeft = (width - valveWidth) / 2f;
        float valveTop = (height - valveHeight) / 2f;
        float valveRight = valveLeft + valveWidth;
        float valveBottom = valveTop + valveHeight;

        paint.setStyle(Paint.Style.FILL);
        int valveColor = filterMode.equals("thermal") ? Color.parseColor("#311b92") : Color.parseColor("#1c3144");
        paint.setColor(valveColor);
        canvas.drawRect(valveLeft, valveTop, valveRight, valveBottom, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        int valveOutline = filterMode.equals("thermal") ? Color.parseColor("#ff3d00") : Color.parseColor("#00e5ff");
        paint.setColor(valveOutline);
        canvas.drawRect(valveLeft, valveTop, valveRight, valveBottom, paint);

        // Blinking indicator light on solenoid valve
        boolean isBlinkOn = (animTick % 20) < 10;
        int statusLightColor;
        if (valveOpen) {
            statusLightColor = isBlinkOn ? Color.parseColor("#4caf50") : Color.parseColor("#1b5e20");
        } else {
            statusLightColor = isBlinkOn ? Color.parseColor("#f44336") : Color.parseColor("#b71c1c");
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(statusLightColor);
        canvas.drawCircle(width / 2f, height / 2f - 20f, 6f, paint);

        // Draw Flow particle waves inside pipes
        if (valveOpen && flowRate > 0) {
            paint.setStyle(Paint.Style.FILL);
            int flowColor = filterMode.equals("thermal") ? Color.parseColor("#ff5722") :
                            (filterMode.equals("nv") ? Color.parseColor("#332ec471") : Color.parseColor("#4080deea"));
            paint.setColor(flowColor);
            
            float dotSpacing = 80f;
            float dotSpeed = 6f;
            for (int i = 0; i < 8; i++) {
                float dotX = ((animTick * dotSpeed + i * dotSpacing) % width);
                // Draw inside the pipe
                canvas.drawCircle(dotX, pipeY, 4f, paint);
            }
        }
    }

    private void drawPostEffects(Canvas canvas, int width, int height) {
        // Night Vision (Green filter)
        if (filterMode.equals("nv")) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#2200ff00")); // Transparent green overlay
            canvas.drawRect(0, 0, width, height, paint);
        }

        // CRT Scanline Overlay
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#1a000000")); // Very subtle dark lines
        for (int y = 0; y < height; y += 4) {
            canvas.drawRect(0, y, width, y + 1, paint);
        }

        // Static Noise Overlay (random dots)
        if (staticIntensity > 0f) {
            paint.setStyle(Paint.Style.FILL);
            for (int k = 0; k < (100 * staticIntensity); k++) {
                int rx = random.nextInt(width);
                int ry = random.nextInt(height);
                int gray = random.nextInt(150) + 105;
                paint.setColor(Color.argb((int)(100 * staticIntensity), gray, gray, gray));
                canvas.drawRect(rx, ry, rx + 2, ry + 2, paint);
            }
        }
    }

    private void drawHudOverlay(Canvas canvas, int width, int height) {
        // Blinking REC Indicator
        boolean recBlink = (animTick % 30) < 15;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(recBlink ? Color.parseColor("#f44336") : Color.TRANSPARENT);
        canvas.drawCircle(25f, 25f, 5f, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(18f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("REC LIVE", 40f, 30f, paint);

        // Camera Designation
        String camName = (selectedCam == 1) ? "CAM-01 // TANK_CHAMBER_A" : "CAM-02 // INLET_VALVE_B";
        canvas.drawText(camName, 40f, 55f, paint);

        // Date Time Overlay
        String timeStr = dateFormat.format(new Date());
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timeStr, width - 20f, 30f, paint);

        // Telemetry details in HUD
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(16f);
        int telemetryY = height - 20;
        String valveStatusStr = valveOpen ? "VALVE: OPEN" : "VALVE: SHUT";
        canvas.drawText(valveStatusStr, 20f, telemetryY, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        String flowStr = String.format(Locale.getDefault(), "FLOW: %.1f L/min", flowRate);
        canvas.drawText(flowStr, width / 2f, telemetryY, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        String levelStr = "LVL: " + waterLevelPct + "%";
        canvas.drawText(levelStr, width - 20f, telemetryY, paint);
    }
}
