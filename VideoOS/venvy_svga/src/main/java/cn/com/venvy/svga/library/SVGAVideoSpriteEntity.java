package cn.com.venvy.svga.library;

import com.opensource.svgaplayer.proto.FrameEntity;
import com.opensource.svgaplayer.proto.SpriteEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanjiangbo on 2018/3/26.
 * done
 */

public class SVGAVideoSpriteEntity {
    public String imageKey;
    public List<SVGAVideoSpriteFrameEntity> frames;

    public SVGAVideoSpriteEntity(SpriteEntity spriteEntity) {
        this.imageKey = spriteEntity.imageKey;
        SVGAVideoSpriteFrameEntity lastFrame = null;
        frames = new ArrayList<>();
        if (spriteEntity.frames != null) {
            for (FrameEntity frameEntity : spriteEntity.frames) {
                SVGAVideoSpriteFrameEntity frameItem = new SVGAVideoSpriteFrameEntity(frameEntity);
                if (frameItem.shapes != null && frameItem.shapes.size() > 0) {
                    SVGAVideoShapeEntity shapeEntity = frameItem.shapes.get(0);
                    if (shapeEntity != null && shapeEntity.isKeep() && lastFrame != null) {
                        frameItem.shapes = lastFrame.shapes;
                    }
                }
                lastFrame = frameItem;
                frames.add(frameItem);
            }
        }
    }
}
