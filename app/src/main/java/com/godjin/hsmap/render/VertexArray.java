package com.godjin.hsmap.render;

import android.opengl.GLES30;

public class VertexArray {
    public static final int INVALID_VERTEX_ARRAY_ID = 0;

    private int[] vVertexArrayID = new int[1];

    public VertexArray() {
        this.vVertexArrayID[0] = VertexArray.INVALID_VERTEX_ARRAY_ID;
    }

    public boolean isCreated() {
        return this.vVertexArrayID[0] != VertexArray.INVALID_VERTEX_ARRAY_ID;
    }

    public int vertexArrayID() {
        return this.vVertexArrayID[0];
    }

    public void createArray() {
        if(this.isCreated())
            return;

        GLES30.glGenVertexArrays(1, this.vVertexArrayID, 0);
    }

    public void destroyArray() {
        if(!this.isCreated())
            return;

        GLES30.glDeleteVertexArrays(1, this.vVertexArrayID, 0);
        this.vVertexArrayID[0] = VertexArray.INVALID_VERTEX_ARRAY_ID;
    }

    public void attachBuffer(int nLocation, int nSize, VertexBuffer sVertexBuffer) {
        if(!this.isCreated())
            return;

        GLES30.glBindVertexArray(this.vVertexArrayID[0]);
        GLES30.glEnableVertexAttribArray(nLocation);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, sVertexBuffer.vertexBufferID());
        GLES30.glVertexAttribPointer(nLocation, nSize, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glVertexAttribDivisor(nLocation, 0);
    }

    public void detachBuffer(int nLocation) {
        if(!this.isCreated())
            return;

        GLES30.glBindVertexArray(this.vVertexArrayID[0]);
        GLES30.glDisableVertexAttribArray(nLocation);
    }

    public void useVertexArray() {
        if(!this.isCreated())
            return;

        GLES30.glBindVertexArray(this.vVertexArrayID[0]);
    }
}