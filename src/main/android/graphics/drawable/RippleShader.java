package android.graphics.drawable;

import android.app.SearchManager;
import android.graphics.RuntimeShader;
import android.graphics.Shader;
/* loaded from: classes.dex */
final class RippleShader extends RuntimeShader {
    private static final double PI_ROTATE_LEFT = -0.02454369260617026d;
    private static final double PI_ROTATE_RIGHT = 0.02454369260617026d;
    private static final String SHADER = "uniform vec2 in_origin;\nuniform vec2 in_touch;\nuniform float in_progress;\nuniform float in_maxRadius;\nuniform vec2 in_resolutionScale;\nuniform vec2 in_noiseScale;\nuniform float in_hasMask;\nuniform float in_noisePhase;\nuniform float in_turbulencePhase;\nuniform vec2 in_tCircle1;\nuniform vec2 in_tCircle2;\nuniform vec2 in_tCircle3;\nuniform vec2 in_tRotation1;\nuniform vec2 in_tRotation2;\nuniform vec2 in_tRotation3;\nlayout(color) uniform vec4 in_color;\nlayout(color) uniform vec4 in_sparkleColor;\nuniform shader in_shader;\nfloat triangleNoise(vec2 n) {\n  n  = fract(n * vec2(5.3987, 5.4421));\n  n += dot(n.yx, n.xy + vec2(21.5351, 14.3137));\n  float xy = n.x * n.y;\n  return fract(xy * 95.4307) + fract(xy * 75.04961) - 1.0;\n}const float PI = 3.1415926535897932384626;\n\nfloat threshold(float v, float l, float h) {\n    return step(l, v) * (1.0 - step(h, v));\n}\nfloat sparkles(vec2 uv, float t) {\n  float n = triangleNoise(uv);\n  float s = 0.0;\n  for (float i = 0; i < 4; i += 1) {\n    float l = i * 0.1;\n    float h = l + 0.05;\n    float o = sin(PI * (t + 0.35 * i));\n    s += threshold(n + o, l, h);\n  }\n  return saturate(s) * in_sparkleColor.a;\n}\nfloat softCircle(vec2 uv, vec2 xy, float radius, float blur) {\n  float blurHalf = blur * 0.5;\n  float d = distance(uv, xy);\n  return 1. - smoothstep(1. - blurHalf, 1. + blurHalf, d / radius);\n}\nfloat softRing(vec2 uv, vec2 xy, float radius, float progress, float blur) {\n  float thickness = 0.05 * radius;\n  float currentRadius = radius * progress;\n  float circle_outer = softCircle(uv, xy, currentRadius + thickness, blur);\n  float circle_inner = softCircle(uv, xy, max(currentRadius - thickness, 0.),     blur);\n  return saturate(circle_outer - circle_inner);\n}\nfloat subProgress(float start, float end, float progress) {\n    float sub = clamp(progress, start, end);\n    return (sub - start) / (end - start); \n}\nmat2 rotate2d(vec2 rad){\n  return mat2(rad.x, -rad.y, rad.y, rad.x);\n}\nfloat circle_grid(vec2 resolution, vec2 coord, float time, vec2 center,\n    vec2 rotation, float cell_diameter) {\n  coord = rotate2d(rotation) * (center - coord) + center;\n  coord = mod(coord, cell_diameter) / resolution;\n  float normal_radius = cell_diameter / resolution.y * 0.5;\n  float radius = 0.65 * normal_radius;\n  return softCircle(coord, vec2(normal_radius), radius, radius * 50.0);\n}\nfloat turbulence(vec2 uv, float t) {\n  const vec2 scale = vec2(0.8);\n  uv = uv * scale;\n  float g1 = circle_grid(scale, uv, t, in_tCircle1, in_tRotation1, 0.17);\n  float g2 = circle_grid(scale, uv, t, in_tCircle2, in_tRotation2, 0.2);\n  float g3 = circle_grid(scale, uv, t, in_tCircle3, in_tRotation3, 0.275);\n  float v = (g1 * g1 + g2 - g3) * 0.5;\n  return saturate(0.45 + 0.8 * v);\n}\nvec4 main(vec2 p) {\n    float fadeIn = subProgress(0., 0.13, in_progress);\n    float scaleIn = subProgress(0., 1.0, in_progress);\n    float fadeOutNoise = subProgress(0.4, 0.5, in_progress);\n    float fadeOutRipple = subProgress(0.4, 1., in_progress);\n    vec2 center = mix(in_touch, in_origin, saturate(in_progress * 2.0));\n    float ring = softRing(p, center, in_maxRadius, scaleIn, 1.);\n    float alpha = min(fadeIn, 1. - fadeOutNoise);\n    vec2 uv = p * in_resolutionScale;\n    vec2 densityUv = uv - mod(uv, in_noiseScale);\n    float turbulence = turbulence(uv, in_turbulencePhase);\n    float sparkleAlpha = sparkles(densityUv, in_noisePhase) * ring * alpha * turbulence;\n    float fade = min(fadeIn, 1. - fadeOutRipple);\n    float waveAlpha = softCircle(p, center, in_maxRadius * scaleIn, 1.) * fade * in_color.a;\n    vec4 waveColor = vec4(in_color.rgb * waveAlpha, waveAlpha);\n    vec4 sparkleColor = vec4(in_sparkleColor.rgb * in_sparkleColor.a, in_sparkleColor.a);\n    float mask = in_hasMask == 1. ? in_shader.eval(p).a > 0. ? 1. : 0. : 1.;\n    return mix(waveColor, sparkleColor, sparkleAlpha) * mask;\n}";
    private static final String SHADER_LIB = "float triangleNoise(vec2 n) {\n  n  = fract(n * vec2(5.3987, 5.4421));\n  n += dot(n.yx, n.xy + vec2(21.5351, 14.3137));\n  float xy = n.x * n.y;\n  return fract(xy * 95.4307) + fract(xy * 75.04961) - 1.0;\n}const float PI = 3.1415926535897932384626;\n\nfloat threshold(float v, float l, float h) {\n    return step(l, v) * (1.0 - step(h, v));\n}\nfloat sparkles(vec2 uv, float t) {\n  float n = triangleNoise(uv);\n  float s = 0.0;\n  for (float i = 0; i < 4; i += 1) {\n    float l = i * 0.1;\n    float h = l + 0.05;\n    float o = sin(PI * (t + 0.35 * i));\n    s += threshold(n + o, l, h);\n  }\n  return saturate(s) * in_sparkleColor.a;\n}\nfloat softCircle(vec2 uv, vec2 xy, float radius, float blur) {\n  float blurHalf = blur * 0.5;\n  float d = distance(uv, xy);\n  return 1. - smoothstep(1. - blurHalf, 1. + blurHalf, d / radius);\n}\nfloat softRing(vec2 uv, vec2 xy, float radius, float progress, float blur) {\n  float thickness = 0.05 * radius;\n  float currentRadius = radius * progress;\n  float circle_outer = softCircle(uv, xy, currentRadius + thickness, blur);\n  float circle_inner = softCircle(uv, xy, max(currentRadius - thickness, 0.),     blur);\n  return saturate(circle_outer - circle_inner);\n}\nfloat subProgress(float start, float end, float progress) {\n    float sub = clamp(progress, start, end);\n    return (sub - start) / (end - start); \n}\nmat2 rotate2d(vec2 rad){\n  return mat2(rad.x, -rad.y, rad.y, rad.x);\n}\nfloat circle_grid(vec2 resolution, vec2 coord, float time, vec2 center,\n    vec2 rotation, float cell_diameter) {\n  coord = rotate2d(rotation) * (center - coord) + center;\n  coord = mod(coord, cell_diameter) / resolution;\n  float normal_radius = cell_diameter / resolution.y * 0.5;\n  float radius = 0.65 * normal_radius;\n  return softCircle(coord, vec2(normal_radius), radius, radius * 50.0);\n}\nfloat turbulence(vec2 uv, float t) {\n  const vec2 scale = vec2(0.8);\n  uv = uv * scale;\n  float g1 = circle_grid(scale, uv, t, in_tCircle1, in_tRotation1, 0.17);\n  float g2 = circle_grid(scale, uv, t, in_tCircle2, in_tRotation2, 0.2);\n  float g3 = circle_grid(scale, uv, t, in_tCircle3, in_tRotation3, 0.275);\n  float v = (g1 * g1 + g2 - g3) * 0.5;\n  return saturate(0.45 + 0.8 * v);\n}\n";
    private static final String SHADER_MAIN = "vec4 main(vec2 p) {\n    float fadeIn = subProgress(0., 0.13, in_progress);\n    float scaleIn = subProgress(0., 1.0, in_progress);\n    float fadeOutNoise = subProgress(0.4, 0.5, in_progress);\n    float fadeOutRipple = subProgress(0.4, 1., in_progress);\n    vec2 center = mix(in_touch, in_origin, saturate(in_progress * 2.0));\n    float ring = softRing(p, center, in_maxRadius, scaleIn, 1.);\n    float alpha = min(fadeIn, 1. - fadeOutNoise);\n    vec2 uv = p * in_resolutionScale;\n    vec2 densityUv = uv - mod(uv, in_noiseScale);\n    float turbulence = turbulence(uv, in_turbulencePhase);\n    float sparkleAlpha = sparkles(densityUv, in_noisePhase) * ring * alpha * turbulence;\n    float fade = min(fadeIn, 1. - fadeOutRipple);\n    float waveAlpha = softCircle(p, center, in_maxRadius * scaleIn, 1.) * fade * in_color.a;\n    vec4 waveColor = vec4(in_color.rgb * waveAlpha, waveAlpha);\n    vec4 sparkleColor = vec4(in_sparkleColor.rgb * in_sparkleColor.a, in_sparkleColor.a);\n    float mask = in_hasMask == 1. ? in_shader.eval(p).a > 0. ? 1. : 0. : 1.;\n    return mix(waveColor, sparkleColor, sparkleAlpha) * mask;\n}";
    private static final String SHADER_UNIFORMS = "uniform vec2 in_origin;\nuniform vec2 in_touch;\nuniform float in_progress;\nuniform float in_maxRadius;\nuniform vec2 in_resolutionScale;\nuniform vec2 in_noiseScale;\nuniform float in_hasMask;\nuniform float in_noisePhase;\nuniform float in_turbulencePhase;\nuniform vec2 in_tCircle1;\nuniform vec2 in_tCircle2;\nuniform vec2 in_tCircle3;\nuniform vec2 in_tRotation1;\nuniform vec2 in_tRotation2;\nuniform vec2 in_tRotation3;\nlayout(color) uniform vec4 in_color;\nlayout(color) uniform vec4 in_sparkleColor;\nuniform shader in_shader;\n";

    /* JADX INFO: Access modifiers changed from: package-private */
    public RippleShader() {
        super(SHADER);
    }

    public void setShader(Shader shader) {
        if (shader != null) {
            setInputShader("in_shader", shader);
        }
        setFloatUniform("in_hasMask", shader == null ? 0.0f : 1.0f);
    }

    public void setRadius(float radius) {
        setFloatUniform("in_maxRadius", 2.3f * radius);
    }

    public void setOrigin(float x, float y) {
        setFloatUniform("in_origin", x, y);
    }

    public void setTouch(float x, float y) {
        setFloatUniform("in_touch", x, y);
    }

    public void setProgress(float progress) {
        setFloatUniform(SearchManager.CURSOR_EXTRA_KEY_IN_PROGRESS, progress);
    }

    public void setNoisePhase(float phase) {
        setFloatUniform("in_noisePhase", 0.001f * phase);
        setFloatUniform("in_turbulencePhase", phase);
        setFloatUniform("in_tCircle1", (float) ((phase * 0.01d * Math.cos(0.8250000000000001d)) + 0.75d), (float) ((phase * 0.01d * Math.sin(0.8250000000000001d)) + 0.75d));
        setFloatUniform("in_tCircle2", (float) ((phase * (-0.0066d) * Math.cos(0.675d)) + 0.30000000000000004d), (float) ((phase * (-0.0066d) * Math.sin(0.675d)) + 0.30000000000000004d));
        setFloatUniform("in_tCircle3", (float) ((phase * (-0.0066d) * Math.cos(0.5249999999999999d)) + 1.5d), (float) ((phase * (-0.0066d) * Math.sin(0.5249999999999999d)) + 1.5d));
        double rotation1 = (phase * PI_ROTATE_RIGHT) + 5.340707511102648d;
        setFloatUniform("in_tRotation1", (float) Math.cos(rotation1), (float) Math.sin(rotation1));
        double rotation2 = (phase * PI_ROTATE_LEFT) + 6.283185307179586d;
        setFloatUniform("in_tRotation2", (float) Math.cos(rotation2), (float) Math.sin(rotation2));
        double rotation3 = (phase * PI_ROTATE_RIGHT) + 8.63937979737193d;
        setFloatUniform("in_tRotation3", (float) Math.cos(rotation3), (float) Math.sin(rotation3));
    }

    public void setColor(int colorInt, int sparkleColorInt) {
        setColorUniform("in_color", colorInt);
        setColorUniform("in_sparkleColor", sparkleColorInt);
    }

    public void setResolution(float w, float h) {
        setFloatUniform("in_resolutionScale", 1.0f / w, 1.0f / h);
        setFloatUniform("in_noiseScale", 2.1f / w, 2.1f / h);
    }
}
