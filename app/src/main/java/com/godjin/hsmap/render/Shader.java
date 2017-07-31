package com.godjin.hsmap.render;

import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

public class Shader {

    public enum ShaderType {
        Vertex,
        Fragment
    }

    public static final int INVALID_PROGRAM_ID = 0;

    private int nProgramID = Shader.INVALID_PROGRAM_ID;

    public boolean isCreated() {
        return this.nProgramID != Shader.INVALID_PROGRAM_ID;
    }

    public int programID() {
        return this.nProgramID;
    }

    public void createShader() {
        if(this.isCreated())
            return;

        this.nProgramID = GLES30.glCreateProgram();
    }

    public void destroyShader() {
        if(!this.isCreated())
            return;

        GLES30.glDeleteProgram(this.nProgramID);
        this.nProgramID = Shader.INVALID_PROGRAM_ID;
    }

    public void attachShader(ShaderType eShaderType, InputStream sShaderSourceStream) {
        if(!this.isCreated()) {
            try {
                sShaderSourceStream.close();
            } catch (Exception sCloseException) {
                //Empty.
            }

            return;
        }

        try {

            BufferedReader sShaderSourceReader = new BufferedReader(new InputStreamReader(sShaderSourceStream));
            StringBuilder sShaderSourceBuilder = new StringBuilder();

            for (String sBuffer = sShaderSourceReader.readLine(); sBuffer != null; sBuffer = sShaderSourceReader.readLine()) {
                sShaderSourceBuilder.append(sBuffer);
                sShaderSourceBuilder.append('\n');
            }

            int nShaderID;

            switch (eShaderType) {
                case Vertex:
                    nShaderID = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
                    break;
                case Fragment:
                    nShaderID = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
                    break;
                default:
                    return;
            }

            GLES30.glShaderSource(nShaderID, sShaderSourceBuilder.toString());
            GLES30.glCompileShader(nShaderID);
            GLES30.glAttachShader(this.nProgramID, nShaderID);
            GLES30.glDeleteShader(nShaderID);

        } catch (Exception sException) {
            //Empty.
        } finally {
            try {
                sShaderSourceStream.close();
            } catch (Exception sCloseException) {
                //Empty.
            }
        }
    }

    public String linkShader() {
        if(!this.isCreated())
            return null;

        GLES30.glLinkProgram(this.nProgramID);
        String sLinkLog = GLES30.glGetProgramInfoLog(this.nProgramID).trim();

        return sLinkLog.isEmpty() ? null : sLinkLog;
    }

    public void useShader() {
        if(!this.isCreated())
            return;

        GLES30.glUseProgram(this.nProgramID);
    }

    public void attachTexture(String sUniformName, Texture sTexture, int nTextureSlot) {
        if(!this.isCreated())
            return;

        sTexture.useTexture(nTextureSlot);
        GLES30.glUseProgram(this.nProgramID);
        GLES30.glUniform1ui(GLES30.glGetUniformLocation(this.nProgramID, sUniformName), nTextureSlot);
    }

    public void setMatrix4(String sUniformName, FloatBuffer sMatrixBuffer) {
        if(!this.isCreated())
            return;

        GLES30.glUseProgram(this.nProgramID);
        GLES30.glUniformMatrix4fv(GLES30.glGetUniformLocation(this.nProgramID, sUniformName), 1, false, sMatrixBuffer);
    }
}