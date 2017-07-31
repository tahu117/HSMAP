package com.godjin.hsmap.render;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Texture {

    public static final int INVALID_TEXTURE_ID = 0;

    private int[] vTextureID = new int[1];

    public Texture() {
        this.vTextureID[0] = Texture.INVALID_TEXTURE_ID;
    }

    public boolean isCreated() {
        return this.vTextureID[0] != Texture.INVALID_TEXTURE_ID;
    }

    public int textureID() {
        return this.vTextureID[0];
    }

    public void createTexture() {
        if(this.isCreated())
            return;

        GLES30.glGenTextures(1, this.vTextureID, 0);
    }

    public void destroyTexture() {
        if(!this.isCreated())
            return;

        GLES30.glDeleteTextures(1, this.vTextureID, 0);
        this.vTextureID[0] = Texture.INVALID_TEXTURE_ID;
    }

    public void loadTexture(InputStream sTextureImageStream, boolean bUseMipmap) {
        if(!this.isCreated()) {
            try {
                sTextureImageStream.close();
            } catch (Exception sException) {
                //Empty.
            }

            return;
        }

        Bitmap sTextureImage = BitmapFactory.decodeStream(sTextureImageStream);
        ByteBuffer sTexelBuffer = ByteBuffer.allocateDirect(sTextureImage.getByteCount()).order(ByteOrder.nativeOrder());

        sTextureImage.copyPixelsToBuffer(sTexelBuffer);
        sTexelBuffer.position(0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.vTextureID[0]);
        GLES30.glPixelStorei(GLES30.GL_UNPACK_ALIGNMENT, 4);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, sTextureImage.getWidth(), sTextureImage.getHeight(), 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, sTexelBuffer);

        sTextureImage.recycle();

        if(bUseMipmap) {
            GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        } else
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        try {
            sTextureImageStream.close();
        } catch (Exception sException) {
            //Empty.
        }
    }

    public void useTexture(int nTextureSlot) {
        if(!this.isCreated())
            return;

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + nTextureSlot);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.vTextureID[0]);
    }
}