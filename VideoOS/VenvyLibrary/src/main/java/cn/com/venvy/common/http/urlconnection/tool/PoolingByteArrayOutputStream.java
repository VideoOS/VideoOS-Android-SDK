package cn.com.venvy.common.http.urlconnection.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by mac on 18/3/26.
 */

public class PoolingByteArrayOutputStream extends ByteArrayOutputStream {

    public static final int DEFAULT_SIZE = 256;

    private final ByteArrayPool mPool;

    public PoolingByteArrayOutputStream(ByteArrayPool pool) {
        this(pool, DEFAULT_SIZE);
    }


    public PoolingByteArrayOutputStream(ByteArrayPool pool, int size) {
        mPool = pool;
        buf = mPool.getBuf(Math.max(size, DEFAULT_SIZE));
    }

    public PoolingByteArrayOutputStream(int size) {
        this(new ByteArrayPool(DEFAULT_SIZE), size);
    }

    @Override
    public void close() throws IOException {
        mPool.returnBuf(buf);
        buf = null;
        super.close();
    }

    public byte[] getBuf(int size) {
        return mPool.getBuf(size);
    }

    public void returnBuf(byte[] buf) {
        mPool.returnBuf(buf);
    }

    @Override
    public void finalize() {
        mPool.returnBuf(buf);
    }


    private void expand(int i) {
        if (count + i <= buf.length) {
            return;
        }
        byte[] newbuf = mPool.getBuf((count + i) * 2);
        System.arraycopy(buf, 0, newbuf, 0, count);
        mPool.returnBuf(buf);
        buf = newbuf;
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int len) {
        expand(len);
        super.write(buffer, offset, len);
    }

    @Override
    public synchronized void write(int oneByte) {
        expand(1);
        super.write(oneByte);
    }
}
