/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package cn.com.venvy.common.image.crop.handle;

import android.graphics.RectF;
import android.support.annotation.NonNull;

import cn.com.venvy.common.image.crop.edge.Edge;
import cn.com.venvy.common.image.crop.util.AspectRatioUtil;


/**
 * HandleHelper class to handle vertical handles (i.e. left and right handles).
 */
class VerticalHandleHelper extends HandleHelper {

    // Member Variables ////////////////////////////////////////////////////////////////////////////

    private Edge mEdge;

    // Constructor /////////////////////////////////////////////////////////////////////////////////

    VerticalHandleHelper(Edge edge) {
        super(null, edge);
        mEdge = edge;
    }

    // HandleHelper Methods ////////////////////////////////////////////////////////////////////////

    @Override
    void updateCropWindow(float x,
                          float y,
                          float targetAspectRatio,
                          @NonNull RectF imageRect,
                          float snapRadius) {

        // Adjust this Edge accordingly.
        mEdge.adjustCoordinate(x, y, imageRect, snapRadius, targetAspectRatio);

        float top = Edge.TOP.getCoordinate();
        float bottom = Edge.BOTTOM.getCoordinate();

        // After this Edge is moved, our crop window is now out of proportion.
        final float targetHeight = AspectRatioUtil.calculateHeight(Edge.getWidth(), targetAspectRatio);

        // Adjust the crop window so that it maintains the given aspect ratio by
        // moving the adjacent edges symmetrically in or out.
        final float difference = targetHeight - Edge.getHeight();
        final float halfDifference = difference / 2;
        top -= halfDifference;
        bottom += halfDifference;

        Edge.TOP.initCoordinate(top);
        Edge.BOTTOM.initCoordinate(bottom);

        // Check if we have gone out of bounds on the top or bottom, and fix.
        if (Edge.TOP.isOutsideMargin(imageRect, snapRadius)
                && !mEdge.isNewRectangleOutOfBounds(Edge.TOP, imageRect, targetAspectRatio)) {

            final float offset = Edge.TOP.snapToRect(imageRect);
            Edge.BOTTOM.offset(-offset);
            mEdge.adjustCoordinate(targetAspectRatio);
        }

        if (Edge.BOTTOM.isOutsideMargin(imageRect, snapRadius)
                && !mEdge.isNewRectangleOutOfBounds(Edge.BOTTOM, imageRect, targetAspectRatio)) {

            final float offset = Edge.BOTTOM.snapToRect(imageRect);
            Edge.TOP.offset(-offset);
            mEdge.adjustCoordinate(targetAspectRatio);
        }
    }
}
