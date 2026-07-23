package com.aquaguard.presentation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsumptionBezierChartView extends View {
    private Map<String, Float> usageData = new TreeMap<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path strokePath = new Path();
    private final Path fillPath = new Path();

    public ConsumptionBezierChartView(Context context) {
        super(context);
    }

    public ConsumptionBezierChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setData(Map<String, Float> data) {
        if (data != null) {
            this.usageData = new TreeMap<>(data);
        } else {
            this.usageData = new TreeMap<>();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        float paddingLeft = 60f;
        float paddingBottom = 60f;
        float paddingTop = 40f;
        float paddingRight = 40f;

        float chartWidth = width - paddingLeft - paddingRight;
        float chartHeight = height - paddingBottom - paddingTop;

        if (usageData == null || usageData.isEmpty()) {
            paint.setColor(Color.GRAY);
            paint.setTextSize(36f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("No usage data available for this week.", width / 2f, height / 2f, paint);
            return;
        }

        List<Float> values = new ArrayList<>(usageData.values());
        List<String> dates = new ArrayList<>(usageData.keySet());

        float maxVal = Collections.max(values);
        if (maxVal < 10f) maxVal = 10f;

        // Draw Y Axis Grid lines (4 divisions)
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.parseColor("#1affffff")); // Subtle grid line
        int gridLines = 4;
        for (int i = 0; i <= gridLines; i++) {
            float y = paddingTop + chartHeight - (chartHeight / gridLines) * i;
            canvas.drawLine(paddingLeft, y, width - paddingRight, y, paint);

            // Draw Y Value Label
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#80ffffff"));
            paint.setTextSize(26f);
            paint.setTextAlign(Paint.Align.RIGHT);
            String labelVal = String.format(java.util.Locale.getDefault(), "%.0fL", (maxVal / gridLines) * i);
            canvas.drawText(labelVal, paddingLeft - 10f, y + 8f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#1affffff"));
        }

        // Plot Bezier spline curve
        if (values.size() > 1) {
            float stepX = chartWidth / (values.size() - 1);
            float[] px = new float[values.size()];
            float[] py = new float[values.size()];

            for (int i = 0; i < values.size(); i++) {
                px[i] = paddingLeft + (i * stepX);
                py[i] = paddingTop + chartHeight - ((values.get(i) / maxVal) * chartHeight);
            }

            strokePath.reset();
            strokePath.moveTo(px[0], py[0]);

            for (int i = 0; i < values.size() - 1; i++) {
                float x0 = px[i];
                float y0 = py[i];
                float x1 = px[i + 1];
                float y1 = py[i + 1];

                float cx1 = x0 + (x1 - x0) / 2f;
                float cy1 = y0;
                float cx2 = x0 + (x1 - x0) / 2f;
                float cy2 = y1;

                strokePath.cubicTo(cx1, cy1, cx2, cy2, x1, y1);
            }

            // Fill under curve
            fillPath.reset();
            fillPath.addPath(strokePath);
            fillPath.lineTo(px[values.size() - 1], paddingTop + chartHeight);
            fillPath.lineTo(px[0], paddingTop + chartHeight);
            fillPath.close();

            // Gradient Fill
            Shader fillShader = new LinearGradient(
                    width / 2f, paddingTop, width / 2f, paddingTop + chartHeight,
                    Color.parseColor("#4d1976d2"), // 30% alpha primary color
                    Color.parseColor("#001976d2"), // 0% alpha primary color
                    Shader.TileMode.CLAMP
            );
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(fillShader);
            canvas.drawPath(fillPath, paint);
            paint.setShader(null); // Clear shader

            // Curve stroke drawing
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6f);
            paint.setColor(Color.parseColor("#2196f3")); // Light blue curve stroke
            canvas.drawPath(strokePath, paint);

            // Draw Dots and Labels
            for (int i = 0; i < values.size(); i++) {
                // Outer circle
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor("#2196f3"));
                canvas.drawCircle(px[i], py[i], 10f, paint);

                // Inner circle
                paint.setColor(Color.WHITE);
                canvas.drawCircle(px[i], py[i], 6f, paint);

                // Draw Date text (X label)
                paint.setColor(Color.parseColor("#80ffffff"));
                paint.setTextSize(24f);
                paint.setTextAlign(Paint.Align.CENTER);
                String displayDate = dates.get(i);
                if (displayDate.length() >= 5) {
                    displayDate = displayDate.substring(5); // Show MM-DD only
                }
                canvas.drawText(displayDate, px[i], paddingTop + chartHeight + 35f, paint);
            }
        }
    }
}
