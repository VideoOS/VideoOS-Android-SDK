package cn.com.venvy.svga.library;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.opensource.svgaplayer.proto.MovieEntity;
import com.opensource.svgaplayer.proto.MovieParams;
import com.opensource.svgaplayer.proto.SpriteEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okio.ByteString;

/**
 * Created by yanjiangbo on 2018/3/26.
 * Done
 */

public class SVGAVideoEntity {
    public int FPS = 20;
    public boolean antiAlias = true;
    public File cacheDir;
    public int frames;

    public HashMap<String, Bitmap> images = new HashMap<>();
    public List<SVGAVideoSpriteEntity> sprites = null;
    public SVGARect videoSize = new SVGARect(0.0D, 0.0D, 0.0D, 0.0D);


    public SVGAVideoEntity(MovieEntity paramMovieEntity, File paramFile) {
        this.cacheDir = paramFile;
        MovieParams params = paramMovieEntity.params;
        if (params != null) {
            videoSize = new SVGARect(0.0, 0.0, params.viewBoxWidth != null ? params.viewBoxWidth : 0.0, params.viewBoxHeight != null ? params.viewBoxHeight : 0.0);
            FPS = params.fps != null ? params.fps : 20;
            frames = params.frames != null ? params.frames : 0;
        }
        try {
            resetImages(paramMovieEntity);
        } catch (Exception e) {
        }
        resetSprites(paramMovieEntity);
    }

    private void resetImages(MovieEntity movieEntity) {
        if (movieEntity == null) {
            return;
        }
        if (movieEntity.images == null || movieEntity.images.size() <= 0) {
            return;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        for (String key : movieEntity.images.keySet()) {
            ByteString string = movieEntity.images.get(key);
            Bitmap bitmap;
            if (string != null) {
                bitmap = BitmapFactory.decodeByteArray(string.toByteArray(), 0, string.size(), options);
                if (bitmap != null) {
                    images.put(key, bitmap);
                } else {
                    String filePath = cacheDir.getAbsolutePath() + "/" + string.utf8();
                    File file = new File(filePath);
                    if (file.exists()) {
                        bitmap = BitmapFactory.decodeFile(filePath, options);
                    }
                    if (bitmap != null) {
                        images.put(key, bitmap);
                    } else {
                        String pngFilePath = cacheDir.getAbsolutePath() + "/" + key + ".png";
                        File pngFile = new File(pngFilePath);
                        if (pngFile.exists()) {
                            bitmap = BitmapFactory.decodeFile(pngFilePath);
                            if (bitmap != null) {
                                images.put(key, bitmap);
                            }
                        }
                    }
                }
            }
        }
    }

    private void resetSprites(MovieEntity movieEntity) {
        this.sprites = null;
        if (movieEntity.sprites != null) {
            for (SpriteEntity spriteEntity : movieEntity.sprites) {
                if (this.sprites == null) {
                    this.sprites = new ArrayList<>();
                }
                this.sprites.add(new SVGAVideoSpriteEntity(spriteEntity));
            }
        }

    }
}
