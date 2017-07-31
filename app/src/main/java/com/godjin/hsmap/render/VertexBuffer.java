package com.godjin.hsmap.render;

import android.opengl.GLES30;

import java.nio.Buffer;

public class VertexBuffer {

    public static final int INVALID_VERTEX_BUFFER_ID = 0;

    private int[] vVertexBufferID = new int[1];

    public VertexBuffer() {
        this.vVertexBufferID[0] = VertexBuffer.INVALID_VERTEX_BUFFER_ID;
    }

    public boolean isCreated() {
        return this.vVertexBufferID[0] != VertexBuffer.INVALID_VERTEX_BUFFER_ID;
    }

    public int vertexBufferID() {
        return this.vVertexBufferID[0];
    }

    public void createBuffer() {
        if(this.isCreated())
            return;

        GLES30.glGenBuffers(1, this.vVertexBufferID, 0);
    }

    public void destroyBuffer() {
        if(!this.isCreated())
            return;

        GLES30.glDeleteBuffers(1, this.vVertexBufferID, 0);
        this.vVertexBufferID[0] = VertexBuffer.INVALID_VERTEX_BUFFER_ID;
    }

    public void fillBuffer(Buffer sBuffer, int nBufferSize) {
        if(!this.isCreated())
            return;

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, this.vVertexBufferID[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, nBufferSize, sBuffer, GLES30.GL_STATIC_DRAW);
    }
}