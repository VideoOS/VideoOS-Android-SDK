package cn.com.venvy.common.media.sourcestorage;

import android.content.Context;

/**
 * Simple factory for {@link SourceInfoStorage}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class SourceInfoStorageFactory {

    public static SourceInfoStorage newSourceInfoStorage(Context context) {
        return new DatabaseSourceInfoStorage(context);
    }

    public static SourceInfoStorage newEmptySourceInfoStorage() {
        return new NoSourceInfoStorage();
    }
}
