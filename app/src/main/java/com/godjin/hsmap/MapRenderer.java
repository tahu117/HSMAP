package com.godjin.hsmap;

import com.godjin.hsmap.render.Shader;
import com.godjin.hsmap.render.Texture;
import com.godjin.hsmap.render.VertexArray;
import com.godjin.hsmap.render.VertexBuffer;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import glm.mat4x4.Mat4;

public class MapRenderer implements GLSurfaceView.Renderer {

    private int nWidth;
    private int nHeight;
    private Context sContext;
    private Shader sMapShader = new Shader();
    private Texture sMikuTexture = new Texture();
    private VertexBuffer sMikuVertexBuffer = new VertexBuffer();
    private VertexBuffer sMikuTexCoordBuffer = new VertexBuffer();
    private VertexArray sMapShaderInput = new VertexArray();

    public MapRenderer(Context sApplicationContext) {
        this.sContext = sApplicationContext;
    }

    public void updateTransform(Mat4 sTransformMatrix) {
        this.sMapShader.setMatrix4("uniform_transform", sTransformMatrix.to(ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asFloatBuffer()));
    }

    public int width() {
        return this.nWidth;
    }

    public int height() {
        return this.nHeight;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES30.glClearColor(1f, 1f, 1f, 1f);
        GLES30.glClearDepthf(1f);

        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        GLES30.glDisable(GLES30.GL_CULL_FACE);
        GLES30.glDisable(GLES30.GL_DEPTH_TEST);

        this.sMapShader.createShader();
        this.sMapShader.attachShader(Shader.ShaderType.Vertex, this.sContext.getResources().openRawResource(R.raw.map_shader_vs));
        this.sMapShader.attachShader(Shader.ShaderType.Fragment, this.sContext.getResources().openRawResource(R.raw.map_shader_fs));

        String sLog = this.sMapShader.linkShader();

        if(sLog != null)
            Log.e("Shader Log", sLog);

        this.sMikuTexture.createTexture();
        this.sMikuTexture.loadTexture(this.sContext.getResources().openRawResource(R.raw.miku), true);

        FloatBuffer sVertex = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder()).asFloatBuffer();
        sVertex.put(new float[]{-1f, 1f});
        sVertex.put(new float[]{-1f, -1f});
        sVertex.put(new float[]{1f, 1f});
        sVertex.put(new float[]{1f, 1f});
        sVertex.put(new float[]{-1f, -1f});
        sVertex.put(new float[]{1f, -1f});
        sVertex.position(0);

        FloatBuffer sTexCoord = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder()).asFloatBuffer();
        sTexCoord.put(new float[]{0f, 0f});
        sTexCoord.put(new float[]{0f, 1f});
        sTexCoord.put(new float[]{1f, 0f});
        sTexCoord.put(new float[]{1f, 0f});
        sTexCoord.put(new float[]{0f, 1f});
        sTexCoord.put(new float[]{1f, 1f});
        sTexCoord.position(0);

        this.sMikuVertexBuffer.createBuffer();
        this.sMikuVertexBuffer.fillBuffer(sVertex, 48);

        this.sMikuTexCoordBuffer.createBuffer();
        this.sMikuTexCoordBuffer.fillBuffer(sTexCoord, 48);

        this.sMapShaderInput.createArray();
        this.sMapShaderInput.attachBuffer(0, 2, this.sMikuVertexBuffer);
        this.sMapShaderInput.attachBuffer(1, 2, this.sMikuTexCoordBuffer);

        this.sMapShader.attachTexture("uniform_texture", this.sMikuTexture, 0);

        this.updateTransform(new Mat4(1f));
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES30.glViewport(0, 0, this.nWidth = width, this.nHeight = height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        this.sMapShader.useShader();
        this.sMapShaderInput.useVertexArray();
        this.sMikuTexture.useTexture(0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        this.sMikuTexture.destroyTexture();
        this.sMapShaderInput.destroyArray();
        this.sMikuVertexBuffer.destroyBuffer();
        this.sMikuTexCoordBuffer.destroyBuffer();
        this.sMapShader.destroyShader();
    }
}