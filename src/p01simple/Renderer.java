package p01simple;

import lwjglutils.OGLBuffers;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import p01simple.enums.ProjectionEnum;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
 * @author Milan Knop @ UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {
    private float dx, dy, ox, oy, ox2, oy2;
    private int shaderProgram, shaderProgram2, locTime, locModel;
    private int locView, locProjection, locType;
    private OGLBuffers buffers, buffers2;
    private Camera camera;
    private Mat4 projection;
    private ProjectionEnum projectionEnum = ProjectionEnum.ortho;
    private OGLTexture2D.Viewer textureViewer;
    private OGLTexture2D textureMosaic, textureSaturn, textureEarth, textureGalaxy, textureSun, textureHat;
    private boolean buttonHold = false;
    // keyPressed identification
    private boolean wPressed, sPressed, aPressed, dPressed, ctrlPressed, shiftPressed;
    private double cameraStep = 0.04;
    private int topology = GL_TRIANGLE_STRIP;
    private int drawTypology = GL_FILL;
    private int gridType = 0;
    private int drawType = 0;
    float time = 0;
    private int locColor;
    private Mat4RotZ rotEarth;
    private Mat4Transl sun;
    private Mat4Transl earth;
    private Mat4Transl saturn;
    private Mat4RotZ rotSaturn;
    private Mat4Transl ring;
    private int locFillMode;
    private int fillModeType;
    private String fillModeTypeValue = "Textura";
    private Mat4Transl karthesian;
    private Mat4Transl hat;
    private Mat4Transl wave;
    private OGLTexture2D textureSea;
    private Mat4Transl cylinder;
    private Mat4Transl func2;
    private OGLTexture2D textureRainbow;
    private Camera cameraLight;
    private Camera cameraLight2;
    private int locCameraPos;
    private int locCameraPos2;
    private int locLightPos;
    private int locLightPos2;
    private int locLightColor;
    private int locAmbientStrength;
    private int locDiffuseStrength;
    private int locSpecularStrength;
    private int locSpotCutOff;
    private float spotCutOff = .92f;
    private boolean button2Hold;
    private int locLightOn, lightOnValue = 1;
    private boolean lightCameraActive;
    private Camera oldCamera;
    private OGLBuffers buffers4;
    private OGLRenderTarget renderTarget;
    private int shaderProgramPostProcessing;
    private boolean postProcessing;
    private int locPostProcessing;
    private float ambientStrengthValue = 0.1f;
    private float diffuseStrengthValue = 0.1f;
    private float specularStrengthValue = 0.5f;

    public Renderer() {
        super();

        keyCallback = new GLFWKeyCallback() {

            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {

                    if (key == 87) {
                        wPressed = false;
                    }
                    if (key == 83) {
                        sPressed = false;
                    }
                    if (key == 65) {
                        aPressed = false;
                    }
                    if (key == 68) {
                        dPressed = false;
                    }
                    if (key == 70) { //F
                        gridType++;
                        if (gridType >= 2) gridType = 0;
                        switch (gridType) {
                            case 0:
                                topology = GL_TRIANGLES;
                                buffers = GridFactory.generateGrid(50, 50, topology);
                                break;
                            case 1:
                                topology = GL_TRIANGLE_STRIP;
                                buffers = GridFactory.generateGrid(50, 50, topology);
                                break;
                        }
                    }
                    if (key == 340) {
                        shiftPressed = false;
                    }
                    if (key == 341) {
                        ctrlPressed = false;
                    }

                    if (key == 89) {
                        diffuseStrengthValue += 0.1f;
                    }
                    if (key == 85) {
                        diffuseStrengthValue -= 0.1f;
                        if (diffuseStrengthValue < 0.1) {
                            diffuseStrengthValue = 0;
                        }
                    }

                    if (key == 74) {
                        specularStrengthValue += 0.1f;
                    }
                    if (key == 75) {
                        specularStrengthValue -= 0.1f;
                        if (specularStrengthValue < 0.1) {
                            specularStrengthValue = 0;
                        }
                    }

                    if (key == 73) {
                        ambientStrengthValue += 0.1f;
                    }

                    if (key == 79) {
                        ambientStrengthValue -= 0.1f;
                        if (ambientStrengthValue < 0.1) {
                            ambientStrengthValue = 0;
                        }
                    }

                    if (key == 76) { //L
                        lightOnValue = lightOnValue == 1 ? 0 : 1;
                    }
                    if (key == 77) { //M
                        fillModeType += 1;
                        if (fillModeType >= 7) {
                            fillModeType = 0;
                        }

                        switch (fillModeType) {
                            case 0:
                                fillModeTypeValue = "Textura";
                                break;
                            case 1:
                                fillModeTypeValue = "Pozice";
                                break;
                            case 2:
                                fillModeTypeValue = "Hloubka";
                                break;
                            case 3:
                                fillModeTypeValue = "Normála";
                                break;
                            case 4:
                                fillModeTypeValue = "Souřadnice";
                                break;
                            case 5:
                                fillModeTypeValue = "Vzdálenost od světla";
                                break;
                            case 6:
                                fillModeTypeValue = "Jednolitá barva";
                                break;
                        }
                    }
                    if (key == 80) { //P
                        postProcessing = !postProcessing;
                    }
                    if (key == 82) { //R
                        drawType++;
                        if (drawType >= 3) drawType = 0;
                        switch (drawType) {
                            case 0:
                                drawTypology = GL_FILL;
                                break;
                            case 1:
                                drawTypology = GL_LINE;
                                break;
                            case 2:
                                drawTypology = GL_POINT;
                                break;
                        }
                    }
                    if (key == 290) {
                        toggleCamera();
                    }

                    if (key == 291) {
                        switchProjection();
                    }
                }
                if (action == GLFW_PRESS) {

                    if (key == 87) {
                        wPressed = true;
                    }
                    if (key == 83) {
                        sPressed = true;
                    }
                    if (key == 65) {
                        aPressed = true;
                    }
                    if (key == 68) {
                        dPressed = true;
                    }
                    if (key == 340) {
                        shiftPressed = true;
                    }
                    if (key == 341) {
                        ctrlPressed = true;
                    }
                }
            }

            private void toggleCamera() {
                if (lightCameraActive) {
                    lightCameraActive = false;
                    camera = oldCamera;
                } else {
                    lightCameraActive = true;
                    oldCamera = camera;
                    camera = cameraLight;
                }
            }
        };

        mbCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = (float) x;
                    oy = (float) y;
                    buttonHold = true;
                }

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                    buttonHold = false;
                }

                if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS) {
                    ox2 = (float) x;
                    oy2 = (float) y;
                    button2Hold = true;
                }

                if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE) {
                    button2Hold = false;
                }

            }
        };

        cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (buttonHold) {
                    dx = (float) x - ox;
                    dy = (float) y - oy;
                    ox = (float) x;
                    oy = (float) y;
                    camera = camera.addAzimuth(Math.toRadians(dx / 4));
                    camera = camera.addZenith(Math.toRadians(dy / 4));
                    dx = 0;
                    dy = 0;
                }
                if (button2Hold) {
                    dx = (float) x - ox2;
                    dy = (float) y - oy2;
                    ox2 = (float) x;
                    oy2 = (float) y;
                    cameraLight = cameraLight.addAzimuth(Math.toRadians(dx / 4));
                    cameraLight = cameraLight.addZenith(Math.toRadians(dy / 4));
                    dx = 0;
                    dy = 0;
                }
            }
        };


        scrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                if (ctrlPressed) {
                    var modifier = -1.0f;
                    if (dy < 0) {
                        modifier = 1.0f;
                    }
                    spotCutOff = spotCutOff + (float) (modifier * 0.05);
                    if (spotCutOff > 1) spotCutOff = 1;
                    if (spotCutOff < 0) spotCutOff = 0;
                } else if (shiftPressed) {
                    cameraLight = cameraLight.down(dy);
                } else {
                    camera = camera.forward(dy);
                }
            }
        };

        wsCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
                if (w != width || h != height) {
                    width = w;
                    height = h;
                    resetProjection();
                }
            }
        };

    }

    /**
     * update modifiers for rotations
     */
    private void updateModifiers() {
        time += 0.1;
        rotEarth = new Mat4RotZ(time / 10);
        rotSaturn = new Mat4RotZ(time / 14);
    }

    /**
     * for switch projection
     */
    public void switchProjection() {
        if (projectionEnum == ProjectionEnum.ortho) {
            projectionEnum = ProjectionEnum.persp;
            projection = new Mat4PerspRH(
                    Math.PI / 3,
                    height / (float) width,
                    0.1,
                    200
            );
        } else {
            projectionEnum = ProjectionEnum.ortho;
            projection = new Mat4OrthoRH(width / 40.0f, height / 30.0f, 0.1, 200);
        }
    }

    /**
     * reset projection, mainly after resize window
     */
    private void resetProjection() {
        if (projectionEnum == ProjectionEnum.persp) {
            projection = new Mat4PerspRH(Math.PI / 3, height / (float) width, 0.1, 200);
        } else {
            projection = new Mat4OrthoRH(width / 40.0f, height / 30.0f, 0.1, 200);
        }
    }

    @Override
    public void init() {
        super.init();
        buffers = GridFactory.generateGrid(50, 50, topology);
        // pro post-processingový krok stačí jeden quad (= 2 trojúhelníky = 4 vrcholy)
        buffers4 = GridFactory.generateGrid(2, 2, GL_TRIANGLES);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glEnable(GL_DEPTH_TEST); // zapne z-test (z-buffer) - až po new OGLTextRenderer (uvnitř super.init())

        shaderProgram = ShaderUtils.loadProgram("/start");
        shaderProgramPostProcessing = ShaderUtils.loadProgram("/post");
        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType = glGetUniformLocation(shaderProgram, "type");
        locColor = glGetUniformLocation(shaderProgram, "elementColor");
        locTime = glGetUniformLocation(shaderProgram, "time");
        locModel = glGetUniformLocation(shaderProgram, "model");
        locFillMode = glGetUniformLocation(shaderProgram, "elementsFillMode");

        locLightOn = glGetUniformLocation(shaderProgram, "lightOn");
        locLightPos = glGetUniformLocation(shaderProgram, "lightPos");
        locLightPos2 = glGetUniformLocation(shaderProgram, "lightPos2");
        locLightColor = glGetUniformLocation(shaderProgram, "lightColor");
        locAmbientStrength = glGetUniformLocation(shaderProgram, "ambientStrength");
        locDiffuseStrength = glGetUniformLocation(shaderProgram, "diffuseStrength");
        locSpecularStrength = glGetUniformLocation(shaderProgram, "specularStrength");
        locCameraPos = glGetUniformLocation(shaderProgram, "cameraPos");
        locCameraPos2 = glGetUniformLocation(shaderProgram, "cameraPos2");
        locSpotCutOff = glGetUniformLocation(shaderProgram, "spotCutOff");

        locPostProcessing = glGetUniformLocation(shaderProgramPostProcessing, "postProcessingOn");


        camera = new Camera()
                .withPosition(new Vec3D(16, 26, 10))
                .withAzimuth(5.3 / 4f * Math.PI)
                .withZenith(-.3 / 5f * Math.PI);
        cameraLight = new Camera()
                .withPosition(new Vec3D(2, 2, 10))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-2 / 4f * Math.PI);
        cameraLight2 = new Camera()
                .withPosition(new Vec3D(-3, 7, 10))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-2 / 4f * Math.PI);
        switchProjection();
        renderTarget = new OGLRenderTarget(1000, 1000);

        textureViewer = new OGLTexture2D.Viewer();
        try {
            textureMosaic = new OGLTexture2D("textures/mosaic.jpg");
            textureEarth = new OGLTexture2D("textures/earthmab.jpg");
            textureSun = new OGLTexture2D("textures/sun.jpg");
            textureSaturn = new OGLTexture2D("textures/saturn.jpg");
            textureGalaxy = new OGLTexture2D("textures/galaxy.jpg");
            textureHat = new OGLTexture2D("textures/hat.jpg");
            textureSea = new OGLTexture2D("textures/sea.jpg");
            textureRainbow = new OGLTexture2D("textures/rainbow.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        initModelPositions();

    }

    /**
     * initial positions
     */
    private void initModelPositions() {
        karthesian = new Mat4Transl(0, 0, -5);
        wave = new Mat4Transl(-7, 6, -6);
        hat = new Mat4Transl(3, 3, 3);
        sun = new Mat4Transl(0, 0, 0);
        earth = new Mat4Transl(3, 13, 0);
        saturn = new Mat4Transl(10, 23, 0);
        ring = new Mat4Transl(10, 23, 0);
        cylinder = new Mat4Transl(-7, -3, -6);
        func2 = new Mat4Transl(5, 5, 1);
    }

    /**
     * moving with camera if you have pressed button
     */
    private void move() {
        if (shiftPressed) {
            if (wPressed) {
                cameraLight = cameraLight.forward(cameraStep);
            }
            if (sPressed) {
                cameraLight = cameraLight.backward(cameraStep);
            }
            if (aPressed) {
                cameraLight = cameraLight.left(cameraStep);
            }
            if (dPressed) {
                cameraLight = cameraLight.right(cameraStep);
            }
        } else {
            if (wPressed) {
                camera = camera.forward(cameraStep);
            }
            if (sPressed) {
                camera = camera.backward(cameraStep);
            }
            if (aPressed) {
                camera = camera.left(cameraStep);
            }
            if (dPressed) {
                camera = camera.right(cameraStep);
            }
        }

    }

    @Override
    public void display() {

        renderMainScene();
        renderPostProcessing();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);

        textureViewer.view(renderTarget.getColorTexture(), -1, -1, 0.5);

        textRenderer.addStr2D(10, 20, "Pohyb: WSAD, LMB + myš, scroll. Pohyb kamerou shift + WASD, shift + scroll, natočení RMB + myš ");
        textRenderer.addStr2D(10, 40, "Specular [ J | K ]: " + (specularStrengthValue != 0 ? specularStrengthValue : "off"));
        textRenderer.addStr2D(10, 60, "Diffuse [ Z | U ]: " + (diffuseStrengthValue != 0 ? diffuseStrengthValue : "off"));
        textRenderer.addStr2D(10, 80, "Ambient [ I | O ]: " + (ambientStrengthValue != 0 ? ambientStrengthValue : "off"));
        textRenderer.addStr2D(10, 100, "Útlum [ctrl + scroll]: " + (spotCutOff != 1.0 ? spotCutOff : "MAX"));
        textRenderer.addStr2D(10, 120, "Persp/Ortho: F2 ");
        textRenderer.addStr2D(10, 140, "Světlo jako kamera a zpět: F1 ");
        textRenderer.addStr2D(10, 160, "Wireframe: R ");
        textRenderer.addStr2D(10, 180, "TRIANGLE LIST / STRIP: F " + (topology == GL_TRIANGLE_STRIP ? "STRIP" : "LIST"));
        textRenderer.addStr2D(width - 200, 20, "Světlo [ L ]: " + (lightOnValue == 0 ? "off" : "on"));
        textRenderer.addStr2D(width - 200, 40, "Zobrazení [ M ]: " + fillModeTypeValue);
        textRenderer.addStr2D(width - 200, 60, "Postprocessing [ P ]: " + (postProcessing ? "On" : "Off"));


        textRenderer.addStr2D(width - 160, height - 3, "Milan Knop (c) PGRF UHK");

    }

    /**
     * post processing
     */
    private void renderPostProcessing() {
        glUseProgram(shaderProgramPostProcessing);
        // renderování do obrazovky (framebuffer=0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height);
        glClearColor(0.0f, 0.5f, 0.0f, 1.0f);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glUniform1i(locPostProcessing, postProcessing ? 1 : 0);

        renderTarget.bindColorTexture(shaderProgramPostProcessing, "renderTargetTexture", 0);
        buffers4.draw(topology, shaderProgramPostProcessing);
    }

    /**
     * main scene
     */
    private void renderMainScene() {
        glUseProgram(shaderProgram);
        renderTarget.bind(); // a nastaví si vlastní viewport

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        move();
        updateModifiers();

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());
        glUniform1i(locLightOn, lightOnValue);
        if (lightOnValue == 1) {
            setLight();
        }
        glUniform1i(locFillMode, fillModeType);

        glPolygonMode(GL_FRONT_AND_BACK, drawTypology);
        drawElements();
    }

    /**
     * set up light positions and switch camera to one light if lightCameraActive
     */
    private void setLight() {
        if (lightCameraActive) {
            glUniform3f(locCameraPos,
                    (float) camera.getViewVector().getX(),
                    (float) camera.getViewVector().getY(),
                    (float) camera.getViewVector().getZ());
            glUniform3f(locLightPos,
                    (float) camera.getPosition().getX(),
                    (float) camera.getPosition().getY(),
                    (float) camera.getPosition().getZ());
        } else {
            glUniform3f(locCameraPos,
                    (float) cameraLight.getViewVector().getX(),
                    (float) cameraLight.getViewVector().getY(),
                    (float) cameraLight.getViewVector().getZ());
            glUniform3f(locLightPos,
                    (float) cameraLight.getPosition().getX(),
                    (float) cameraLight.getPosition().getY(),
                    (float) cameraLight.getPosition().getZ());
            glUniform3f(locCameraPos2,
                    (float) cameraLight2.getViewVector().getX(),
                    (float) cameraLight2.getViewVector().getY(),
                    (float) cameraLight2.getViewVector().getZ());
            glUniform3f(locLightPos2,
                    (float) cameraLight2.getPosition().getX(),
                    (float) cameraLight2.getPosition().getY(),
                    (float) cameraLight2.getPosition().getZ());
        }

        glUniform1f(locSpotCutOff, spotCutOff);
        glUniform3f(locLightColor, 1.0f, 0.9f, 0.8f);
        glUniform1f(locAmbientStrength, ambientStrengthValue);
        glUniform1f(locDiffuseStrength, diffuseStrengthValue);
        glUniform1f(locSpecularStrength, specularStrengthValue);
    }

    /**
     * main draw function
     */
    private void drawElements() {
        // vykreslit první těleso
        textureSun.bind(shaderProgram, "textureSun", 0);
        glUniformMatrix4fv(locModel, false, sun.mul(new Mat4Scale(3 + Math.sin(time / 20) / 2.0).mul(new Mat4RotZ(time / 100))).floatArray());
        glUniform3f(locColor, 1f, 0.5f, 0.2f);
        glUniform1i(locType, 1);
        buffers.draw(topology, shaderProgram);


        // vykreslit druhé těleso (do stejné scény)
        textureSea.bind(shaderProgram, "textureSea", 0);
        glUniformMatrix4fv(locModel, false, wave.floatArray());
        glUniform3f(locColor, 0.5f, 0.02f, 0.8f);
        glUniform1i(locType, 2);
        buffers.draw(topology, shaderProgram);

        // vykreslit třetí těleso (do stejné scény)
        // plocha
        textureGalaxy.bind(shaderProgram, "textureGalaxy", 0);
        glUniformMatrix4fv(locModel, false, karthesian.floatArray());
        glUniform3f(locColor, 0.8f, 0.8f, 0.8f);
        glUniform1i(locType, 3);
        buffers.draw(topology, shaderProgram);


        // klobouk
        textureHat.bind(shaderProgram, "textureHat", 0);
        glUniformMatrix4fv(locModel, false, new Mat4RotY(time / 17).mul(new Mat4Scale(0.2).mul(hat.mul(rotSaturn))).floatArray());
        glUniform3f(locColor, 0.3f, 0.8f, 0.1f);
        glUniform1i(locType, 4);
        buffers.draw(topology, shaderProgram);

        textureEarth.bind(shaderProgram, "textureEarth", 0);
        glUniform3f(locColor, 0.2f, 0.6f, 0.8f);
        glUniformMatrix4fv(locModel, false, new Mat4RotZ(time / 10).mul(earth.mul(rotEarth)).floatArray());
        glUniform1f(locTime, time);
        glUniform1i(locType, 1);
        buffers.draw(topology, shaderProgram);

        textureSaturn.bind(shaderProgram, "textureSaturn", 0);
        glUniform3f(locColor, 0.1f, 0.1f, 0.5f);
        glUniformMatrix4fv(locModel, false, saturn.mul(rotSaturn).floatArray());
        glUniform1f(locTime, time);
        glUniform1i(locType, 1);
        buffers.draw(topology, shaderProgram);

        glUniformMatrix4fv(locModel, false, new Mat4RotY(Math.sin(time / 20)).mul(new Mat4RotZ(Math.sin(time / 5))).mul(ring.mul(rotSaturn)).floatArray());
        glUniform3f(locColor, 0.5f, 0.4f, 0.2f);
        glUniform1i(locType, 5);
        buffers.draw(topology, shaderProgram);

        textureRainbow.bind(shaderProgram, "textureRainbow", 0);
        glUniform3f(locColor, 0.2f, 0.8f, 0.5f);
        glUniformMatrix4fv(locModel, false, new Mat4RotY(-1.6).mul(new Mat4Scale(2)).mul(cylinder).floatArray());
        glUniform1i(locType, 6);
        buffers.draw(topology, shaderProgram);

        textureMosaic.bind(shaderProgram, "textureMosaic", 0);
        glUniform3f(locColor, 0.2f, 0.2f, 0.1f);
        glUniformMatrix4fv(locModel, false, func2.floatArray());
        glUniform1i(locType, 7);
        buffers.draw(topology, shaderProgram);

        if (!lightCameraActive) {
            glUniformMatrix4fv(locModel, false, new Mat4Transl(cameraLight.getPosition()).floatArray());
            glUniform3f(locColor, 0.8f, 0.8f, 0.8f);
            glUniform1i(locType, 8);
            buffers.draw(topology, shaderProgram);

            glUniformMatrix4fv(locModel, false, new Mat4Transl(cameraLight2.getPosition()).floatArray());
            glUniform3f(locColor, 0.8f, 0.8f, 0.8f);
            glUniform1i(locType, 8);
            buffers.draw(topology, shaderProgram);
        }
    }


}
