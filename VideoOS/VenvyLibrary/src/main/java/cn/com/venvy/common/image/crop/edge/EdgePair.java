

package cn.com.venvy.common.image.crop.edge;

/**
 * 固定宽高比新增的类
 */
public class EdgePair {


    public Edge primary;
    public Edge secondary;


    public EdgePair(Edge edge1, Edge edge2) {
        primary = edge1;
        secondary = edge2;
    }
}
