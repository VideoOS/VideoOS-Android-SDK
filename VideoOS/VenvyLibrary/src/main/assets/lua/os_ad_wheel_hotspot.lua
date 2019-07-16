--
-- Created by IntelliJ IDEA.
-- User: videojj_pls
-- Date: 2018/10/29
-- Time: 2:36 PM
-- To change this template use File | Settings | File Templates.
--
-- 轮播 广告 --
require "os_config"
require "os_string"
require "os_constant"
require "os_util"
require "os_track"
adWheel = object:new()
local adTypeName = "adWheel"
local scale = getScale()
local OS_ICON_WEDGE_CLOSE = "iVBORw0KGgoAAAANSUhEUgAAAC0AAAAtCAYAAAA6GuKaAAAAAXNSR0IArs4c6QAAAAlwSFlzAAAhOAAAITgBRZYxYAAAABxpRE9UAAAAAgAAAAAAAAAXAAAAKAAAABcAAAAWAAABJuDZqwUAAADySURBVGgFxNYxDsIwDAXQDiwwcR1uwB1ghpUj5Oh8S3yE2rTYju1GsiIlqf3Swe009ccFyw/Esb+dsnpA1hvi6sku4PaJJ+YKOMGsa4L/gpkgGz4Hs64K3gMzQRZ8Dcy6m/AtMBNEw/+BWbcL14CZIAquBbPuAi5dgpuaeRQu4Lux5gvnT4jvkO4gkGYIL9z6hsUk4DNiMSrgoWDeIBOeAs6Ep4Iz4CXgSHgpOAK+C3gU7unD3bZGiHX2dJWGItpY7cNW6Px8FjwNzAtEw9PB0fAycBS8HDwK3w0scOnD1rbW8Iz37xCPjg3Ph6OhJGMY/gYAAP//68uhBAAAANxJREFU7dZNDoIwEAVgEl2gK4/lCdh4ALceoUf3DfEloIjTdt6sbDIpJHTeF34ahqFuHHH5DVU66471J5R8RIELpFZyeDRYDleBZfAW8OP16InyzGGvSiv4ArR9ZAbxgHlNN7wHDOs8UuER4FR4JDgFrgBL4UqwBJ4BDofX/kvYPmzbWuto3VXGZeAVJ8VZvWDm1sInLDxwMWcPPArMTC98E8wme/BoMDN/wXfBbLIFV4GZ+Q3uArPJEq4GM/MdXgVmE4NngZlJeBOYTc48SJxHZH3sEon5/6jVHXgCCd+jGkmWfYoAAAAASUVORK5CYII="
local OS_ICON_WEDGE_LANDSCAPE_CLOSE = "iVBORw0KGgoAAAANSUhEUgAAAFgAAABYBAMAAACDuy0HAAAAMFBMVEUAAAAAAACRkZHBwcEAAAAAAAAAAAAAAAAAAABHR0cAAAAAAAAAAAAAAAAAAAAAAACPWxS5AAAAEHRSTlMAgLLNQHprVAaUdWA6KhIMLcJxvwAAAPtJREFUSMdjGAWjYBSMAiqAN9WpYdvPEaWU300QDFI+EFbL4SoIBSENBBXfEoSDtYTUsggiAQcCjihEViyO3yHsgiigAK9iR1TFIvjU8gqigQt4FHehK16B3xVEu4M/EF2x6AcCgSwJUTeRQFCzgeUnK4JIIUswJwGn4odgeSEjEKmsCObI4VR8ECgLVgfWAQYyOBUXCkKNhhssKI5T8UZBmNEwgwWlcSpOFIQZDTNYUAynYngwKxvBA5p8xYSdQbkHCQcd4UihPLoJJyTCSZSixE84W1GeYTGLAooKGcLFF+UFI2aRS3lhTriaoLwCQlRto2AUjIJRQDkAAK7+UH2EAaaMAAAAAElFTkSuQmCC"
local function getHotspotExposureTrackLink(data, index)
    if (data == nil or index == nil) then
        return nil
    end
    local hotspotTrackLinkTable = data.hotspotTrackLink
    if (hotspotTrackLinkTable == nil) then
        return nil
    end
    local indexHotspotTrackLinkTable = hotspotTrackLinkTable[index]
    if (indexHotspotTrackLinkTable == nil) then
        return nil
    end
    return indexHotspotTrackLinkTable.exposureTrackLink
end

local function getHotspotClickTrackLink(data, index)
    if (data == nil or index == nil) then
        return nil
    end
    local hotspotTrackLinkTable = data.hotspotTrackLink
    if (hotspotTrackLinkTable == nil) then
        return nil
    end
    local indexHotspotTrackLinkTable = hotspotTrackLinkTable[index]
    if (indexHotspotTrackLinkTable == nil) then
        return nil
    end
    return indexHotspotTrackLinkTable.clickTrackLink
end

--延时回调--
local function performWithDelay(callback, delay)
    if callback ~= nil and delay ~= nil then
        local timer = Timer()
        timer:interval(delay)
        timer:repeatCount(false)
        timer:delay(delay / 1000)
        timer:callback(callback)
        timer:start()
        return timer
    end
end

--轮播个数获取--
local function getAdWhellCount(data)
    if (data == nil) then
        return 1
    end
    local dataTable = data.data
    if (dataTable == nil) then
        return nil
    end
    local hotEditTable = dataTable.hotEdit
    if (hotEditTable == nil) then
        return nil
    end
    local adArrayTable = hotEditTable.adArray
    if (adArrayTable == nil) then
        return nil
    end
    return table_leng(adArrayTable)
end

--轮播图片获取--
local function getAdImage(data, index)
    if (data == nil or index == nil) then
        return
    end
    local dataTable = data.data
    if (dataTable == nil) then
        return nil
    end
    local hotEditTable = dataTable.hotEdit
    if (hotEditTable == nil) then
        return nil
    end
    local adArrayTable = hotEditTable.adArray
    if (adArrayTable == nil) then
        return nil
    end
    local curIndexTable = adArrayTable[index]
    if (curIndexTable == nil) then
        return nil
    end
    return curIndexTable.adImage
end

--轮播广告当前外链--
local function getAdLinkUrl(data, index)
    if (data == nil or index == nil) then
        return
    end
    local dataTable = data.data
    if (dataTable == nil) then
        return nil
    end
    local hotEditTable = dataTable.hotEdit
    if (hotEditTable == nil) then
        return nil
    end
    local adArrayTable = hotEditTable.adArray
    if (adArrayTable == nil) then
        return nil
    end
    local curIndexTable = adArrayTable[index]
    if (curIndexTable == nil) then
        return nil
    end
    return curIndexTable.linkUrl
end

--轮播按钮获取--
local function getAdBtnTxt(data, index)
    if (data == nil or index == nil) then
        return
    end
    local dataTable = data.data
    if (dataTable == nil) then
        return nil
    end
    local hotEditTable = dataTable.hotEdit
    if (hotEditTable == nil) then
        return nil
    end
    local adArrayTable = hotEditTable.adArray
    if (adArrayTable == nil) then
        return nil
    end
    local curIndexTable = adArrayTable[index]
    if (curIndexTable == nil) then
        return nil
    end
    return curIndexTable.btnTxt
end

--轮播描述获取--
local function getAdDescribeTxt(data, index)
    if (data == nil or index == nil) then
        return
    end
    local dataTable = data.data
    if (dataTable == nil) then
        return nil
    end
    local hotEditTable = dataTable.hotEdit
    if (hotEditTable == nil) then
        return nil
    end
    local adArrayTable = hotEditTable.adArray
    if (adArrayTable == nil) then
        return nil
    end
    local curIndexTable = adArrayTable[index]
    if (curIndexTable == nil) then
        return nil
    end
    return curIndexTable.describe
end

--轮播标题获取--
local function getAdTitle(data)
    if (data == nil) then
        return
    end
    local dataTable = data.data
    if (dataTable == nil) then
        return nil
    end
    return dataTable.creativeName
end

local function closeView()
    if Native:getCacheData(adWheel.id) == tostring(eventTypeShow) then
        Native:widgetEvent(eventTypeClose, adWheel.id, adTypeName, actionTypePlayVideo, "")
        Native:deleteBatchCacheData({ adWheel.id })
    end
    Native:destroyView()
end

local function translationAnim(x, y)
    local anim = Animation():translation(x, y):duration(0.3)
    return anim
end

local function startViewTranslationAnim(view, x, y, table)
    if (view == nil) then
        return nil
    end
    if table ~= nil then
        return translationAnim(x, y):with(view):callback(table):start()
    else
        return translationAnim(x, y):with(view):start()
    end
end

--轮播广告Icon边框设置--
local function setCurIconBorder(index)
    if (index == nil) then
        return
    end
    if (index == 1) then
        adWheel.adWheelIconView1:borderWidth(2 * scale)
        adWheel.adWheelIconView1:borderColor(0x999999)
        if (System.android()) then
            adWheel.adWheelIconView1:padding(2 * scale, 2 * scale, 2 * scale, 2 * scale)
        end
        if (adWheel.adWheelIconView2 ~= nil) then
            adWheel.adWheelIconView2:borderWidth(0)
            if (System.android()) then
                adWheel.adWheelIconView2:padding(0, 0, 0, 0)
            end
        end
        if (adWheel.adWheelIconView3 ~= nil) then
            adWheel.adWheelIconView3:borderWidth(0)
            if (System.android()) then
                adWheel.adWheelIconView3:padding(0, 0, 0, 0)
            end
        end
    elseif (index == 2) then
        adWheel.adWheelIconView2:borderWidth(2 * scale)
        adWheel.adWheelIconView2:borderColor(0x999999)
        if (System.android()) then
            adWheel.adWheelIconView2:padding(2 * scale, 2 * scale, 2 * scale, 2 * scale)
        end
        if (adWheel.adWheelIconView1 ~= nil) then
            adWheel.adWheelIconView1:borderWidth(0)
            if (System.android()) then
                adWheel.adWheelIconView1:padding(0, 0, 0, 0)
            end
        end
        if (adWheel.adWheelIconView3 ~= nil) then
            adWheel.adWheelIconView3:borderWidth(0)
            if (System.android()) then
                adWheel.adWheelIconView3:padding(0, 0, 0, 0)
            end
        end
    elseif (index == 3) then
        adWheel.adWheelIconView3:borderWidth(2 * scale)
        adWheel.adWheelIconView3:borderColor(0x999999)
        if (System.android()) then
            adWheel.adWheelIconView3:padding(2 * scale, 2 * scale, 2 * scale, 2 * scale)
        end
        if (adWheel.adWheelIconView2 ~= nil) then
            adWheel.adWheelIconView2:borderWidth(0)
            if (System.android()) then
                adWheel.adWheelIconView2:padding(0, 0, 0, 0)
            end
        end
        if (adWheel.adWheelIconView1 ~= nil) then
            adWheel.adWheelIconView1:borderWidth(0)
            if (System.android()) then
                adWheel.adWheelIconView1:padding(0, 0, 0, 0)
            end
        end
    end
end


--设置当前容器大小
local function setLuaViewSize(luaview, isPortrait)
    if (luaview == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        luaview:backgroundColor(0x000000, 0)
        luaview:frame(0, 0, math.min(screenWidth, screenHeight), math.max(screenWidth, screenHeight))
        luaview:align(Align.BOTTOM)
    else
        luaview:backgroundColor(0x000000, 0.3)
        luaview:frame(0, 0, math.max(screenWidth, screenHeight), math.min(screenWidth, screenHeight))
        luaview:align(Align.RIGHT)
    end
end

--设置当前容器大小--
local function setAdWheelViewSize(data, adWheelView, isPortrait)
    if (data == nil or adWheelView == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        adWheelView:cornerRadius(0)
        adWheelView:frame(0, math.max(screenWidth, screenHeight) - adWheel.portraitHeight, adWheel.portraitWidth, adWheel.portraitHeight)
    else
        adWheelView:cornerRadius(4 * scale)
        adWheelView:frame((math.max(screenWidth, screenHeight) - 536 * scale) / 2, (math.min(screenWidth, screenHeight) - 225 * scale) / 2, 536 * scale, 225 * scale)
    end
end

--轮播广告详情大图大小--
local function setAdWheelDetailViewSize(data, adWheelDetailView, isPortrait)
    if (data == nil or adWheelDetailView == nil) then
        return
    end
    if (isPortrait) then
        adWheelDetailView:frame(16 * scale, 60 * scale, 343 * scale, 192 * scale)
    else
        adWheelDetailView:frame(12 * scale, 12 * scale, 201 * scale, 201 * scale)
    end
end

--轮播广告标题大小--
local function setadWheelTitleViewSize(data, adWheelTitleView, isPortrait)
    --设置当前容器大小
    if (data == nil or adWheelTitleView == nil) then
        return
    end
    if (isPortrait) then
        if (System.android()) then
            adWheelTitleView:maxLines(1)
        else
            adWheelTitleView:lines(1)
        end
        adWheelTitleView:frame(16 * scale, 0, 330 * scale, 60 * scale)
        adWheelTitleView:textSize(16)
        if System.android() then
            adWheelTitleView:gravity(Gravity.V_CENTER)
        end
    else
        adWheelTitleView:textSize(18)

        if (System.android()) then
            adWheelTitleView:maxLines(2)
            adWheelTitleView:frame(237 * scale, 18 * scale, 180 * scale, 48 * scale)
            adWheelTitleView:gravity(Gravity.LEFT)
        else
            local adTitle = getAdTitle(data)
            local labelW = Native:stringDrawLength(adTitle, 18)
            if labelW > 180 * scale then
                adWheelTitleView:lines(2)
                adWheelTitleView:frame(237 * scale, 18 * scale, 180 * scale, 36)
            else
                adWheelTitleView:lines(1)
                adWheelTitleView:frame(237 * scale, 18 * scale, 180 * scale, 18)
            end
            adWheelTitleView:textAlign(TextAlign.LEFT)
        end
    end
end

--轮播广告描述大小--
local function setadWheelDesViewSize(data, adWheelDesView, isPortrait)
    --设置当前容器大小
    if (data == nil or adWheelDesView == nil) then
        return
    end
    if (isPortrait) then
        adWheelDesView:hide()
    else
        adWheelDesView:show()
        if System.android() then
            adWheelDesView:frame(237 * scale, 86 * scale, 201 * scale, 54 * scale)
        else
            local adDes = getAdDescribeTxt(data, adWheel.hotspotOrder)
            local labelW, labelH = Native:stringSizeWithWidth(adDes, 201 * scale, 12)
            local threeLinesW, threeLinesH = Native:stringSizeWithWidth("一\n二\n三", 50, 12)
            adWheelDesView:lines(0)
            if labelH > threeLinesH then
                labelH = threeLinesH
            end
            adWheelDesView:frame(237 * scale, 86 * scale, 201 * scale, labelH)
            adWheelDesView:textAlign(TextAlign.LEFT)
        end
    end
end

--轮播广告按钮大小--
local function setadWheelBtnViewSize(data, adWheelBtnView, adWheelBtnBackView, isPortrait)
    --设置当前容器大小
    if (data == nil or adWheelBtnView == nil) then
        return
    end
    if (isPortrait) then
        adWheelBtnBackView:cornerRadius(0)
        adWheelBtnBackView:frame(0, adWheel.portraitHeight * 0.896, adWheel.portraitWidth, 50 * scale)
        adWheelBtnView:cornerRadius(0)
        adWheelBtnView:frame(0, 0, adWheel.portraitWidth, 50 * scale)

        if System.ios() then
            adWheelBtnBackView:alignBottom()
            adWheelBtnView:alignTop()
            if Native:iPhoneX() then
                adWheelBtnBackView:size(adWheel.portraitWidth, 50 * scale + 44)
            end
        end

    else
        adWheelBtnBackView:cornerRadius(4 * scale)
        adWheelBtnBackView:frame(237 * scale, 172 * scale, 116 * scale, 37 * scale)
        adWheelBtnView:cornerRadius(4 * scale)
        adWheelBtnView:frame(0, 0, 116 * scale, 37 * scale)
        adWheelBtnView:alignCenter()
    end
end

--轮播广告按钮大小--
local function setadWheelIconViewSize(index, data, adWheelIconView, isPortrait)
    --设置当前容器大小
    if (data == nil or adWheelIconView == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    -- local spacSize = (math.min(screenWidth, screenHeight) - 32 * scale - adWheel.portraitHeight * 0.708) / 2.0
    local itemWidth = 108 * scale
    local spacSize = (math.min(screenWidth, screenHeight) - 32 * scale - itemWidth * 3) / 2.0
    if (isPortrait) then
        if (index == 1) then
            -- adWheelIconView:frame(16 * scale, adWheel.seperatedView:bottom() + 16 * scale, itemWidth, itemWidth)
            adWheelIconView:frame(16 * scale, adWheel.adWheelDetailView:bottom() + 16 * scale, itemWidth, itemWidth)
        elseif (index == 2) then
            adWheelIconView:frame(16 * scale + itemWidth + spacSize, adWheel.adWheelDetailView:bottom() + 16 * scale, itemWidth, itemWidth)
        elseif (index == 3) then
            adWheelIconView:frame(16 * scale + (itemWidth + spacSize) * 2, adWheel.adWheelDetailView:bottom() + 16 * scale, itemWidth, itemWidth)
        end
    else
        if (index == 1) then
            adWheelIconView:frame(464 * scale, 11 * scale, 60 * scale, 60 * scale)
        elseif (index == 2) then
            adWheelIconView:frame(464 * scale, 81.8 * scale, 60 * scale, 60 * scale)
        elseif (index == 3) then
            adWheelIconView:frame(464 * scale, 152.7 * scale, 60 * scale, 60 * scale)
        end
    end
end

--轮播广告关闭大小--
local function setadWheelShutDownViewSize(data, shutDownView, shutDownImageView, isPortrait)
    --设置当前容器大小
    if (data == nil or shutDownView == nil or shutDownImageView == nil) then
        return
    end
    local tableData = data.data
    if (tableData == nil) then
        return
    end
    local tableHotEdit = tableData.hotEdit
    if (tableHotEdit == nil) then
        return
    end
    if not tableHotEdit.isShowClose then
        shutDownView:hide()
        return
    end
    if (isPortrait) then
        shutDownImageView:image(Data(OS_ICON_WEDGE_CLOSE))
        shutDownView:frame(adWheel.adWheelView:right() - 44 * scale, adWheel.adWheelView:top() + 8 * scale, 44 * scale, 44 * scale)
        shutDownImageView:frame(0, 0, 15 * scale, 15 * scale)
        shutDownImageView:align(Align.CENTER)
        --        shutDownView:align(Align.RIGHT)
    else
        shutDownImageView:frame(0, 0, 44 * scale, 44 * scale)
        shutDownImageView:image(Data(OS_ICON_WEDGE_LANDSCAPE_CLOSE))
        shutDownView:frame(adWheel.adWheelView:right() - 10 * scale, adWheel.adWheelView:top() - 34 * scale, 44 * scale, 44 * scale)
    end
end

-- 轮播小屏分割线大小
local function setadWheelSeperatedView(data, seperatedView, isPortrait)
    if (data == nil or seperatedView == nil) then
        return
    end
    if (isPortrait) then
        -- seperatedView:show()
        seperatedView:hide()
        seperatedView:frame(16 * scale, adWheel.adWheelDetailView:bottom() + 16 * scale, 343 * scale, 1)
        seperatedView:align(Align.H_CENTER)
    else
        seperatedView:hide()
    end
end

local function setadWheelAdView(data, adView, adLabel, isPortrait)
    if (data == nil or adView == nil or adLabel == nil) then
        return
    end
    local tableData = data.data
    if (tableData == nil) then
        return
    end
    local tableHotEdit = tableData.hotEdit
    if (tableHotEdit == nil) then
        return
    end
    if not tableHotEdit.isShowAds then
        adView:hide()
        return
    end

    adView:cornerRadius(2)
    adLabel:frame(0, 0, 34 * scale, 14 * scale)
    adLabel:cornerRadius(2)

    if (isPortrait) then
        -- adView:hide()
        adView:show()
        adView:frame(adWheel.adWheelDetailView:left() + 8 * scale, adWheel.adWheelDetailView:bottom() - 8 * scale - 14 * scale, 34 * scale, 14 * scale)

    else
        adView:show()
        adView:frame(adWheel.adWheelDetailView:left() + 4 * scale, adWheel.adWheelDetailView:bottom() - 4 * scale - 14 * scale, 34 * scale, 14 * scale)
    end
end

local function changeAdWheel(index, data)
    if (index == nil or data == nil) then
        return
    end
    setCurIconBorder(index)

    local isPortrait = Native:isPortraitScreen()

    local adImage = getAdImage(data, adWheel.hotspotOrder)
    if (adImage ~= nil) then
        adWheel.adWheelDetailView:image(adImage)
    end
    local adTitle = getAdTitle(data)
    if (adTitle ~= nil) then
        adWheel.adWheelTitleView:text(adTitle)
        setadWheelTitleViewSize(data, adWheel.adWheelTitleView, isPortrait)
    end
    local adDes = getAdDescribeTxt(data, adWheel.hotspotOrder)
    if (adDes ~= nil) then
        adWheel.adWheelDesView:text(adDes)
        setadWheelDesViewSize(data, adWheel.adWheelDesView, isPortrait)
    end
    local btnTxt = getAdBtnTxt(data, adWheel.hotspotOrder)
    if (btnTxt ~= nil) then
        adWheel.adWheelBtnView:text(btnTxt)
    end
    if (not adWheel.iconClick) then
        adWheel.timer = performWithDelay(function()
            adWheel.hotspotOrder = adWheel.hotspotOrder + 1
            if (adWheel.hotspotOrder > adWheel.adWhellCount) then
                adWheel.hotspotOrder = 1
            end
            changeAdWheel(adWheel.hotspotOrder, data)
        end, 2000)
    end
end

--屏幕旋转--
local function rotationScreen(isPortrait)
    print("rotationScreen lua")
    setLuaViewSize(adWheel.luaview, isPortrait)
    setAdWheelViewSize(adWheel.data, adWheel.adWheelView, isPortrait)
    setAdWheelDetailViewSize(adWheel.data, adWheel.adWheelDetailView, isPortrait)
    setadWheelTitleViewSize(adWheel.data, adWheel.adWheelTitleView, isPortrait)
    setadWheelDesViewSize(adWheel.data, adWheel.adWheelDesView, isPortrait)
    setadWheelBtnViewSize(adWheel.data, adWheel.adWheelBtnView, adWheel.adWheelBtnBackView, isPortrait)
    setadWheelShutDownViewSize(adWheel.data, adWheel.shutDownView, adWheel.shutDownImageView, isPortrait)
    setadWheelSeperatedView(adWheel.data, adWheel.seperatedView, isPortrait)
    setadWheelAdView(adWheel.data, adWheel.adView, adWheel.adLabel, isPortrait)
    setadWheelIconViewSize(1, adWheel.data, adWheel.adWheelIconView1, isPortrait)
    setadWheelIconViewSize(2, adWheel.data, adWheel.adWheelIconView2, isPortrait)
    setadWheelIconViewSize(3, adWheel.data, adWheel.adWheelIconView3, isPortrait)
end

local function registerMedia()
    local media = Media()
    -- body
    -- 注册window callback通知
    local callbackTable = {
        --0: 竖屏小屏幕，1 竖屏全凭，2 横屏全屏
        onPlayerSize = function(type)
            if (type == 0) then
                rotationScreen(true)
            elseif (type == 1) then
                rotationScreen(true)
            elseif (type == 2) then
                rotationScreen(false)
            end
        end
    }
    media:mediaCallback(callbackTable)
    return media
end

--全局父控件
local function createLuaView(isPortrait)
    local luaView
    luaView = View()
    setLuaViewSize(luaView, isPortrait)
    return luaView
end

--轮播广告云窗大小控件
local function createAdWheelView(data, isPortrait)
    local adWheelView = View()
    adWheelView:backgroundColor(0xffffff)
    setAdWheelViewSize(data, adWheelView, isPortrait)
    return adWheelView
end

--轮播广告详情图--
local function createDetailImageView(data, isPortrait)
    local adWheelDetailView = Image(Native)
    adWheelDetailView:scaleType(ScaleType.CENTER_CROP)
    setAdWheelDetailViewSize(data, adWheelDetailView, isPortrait)
    return adWheelDetailView
end

--轮播广告标题--
local function createAdWheelTitleView(data, isPortrait)
    local adWheelTitleView = Label()
    adWheelTitleView:textColor(0x333333)
    local adTitle = getAdTitle(data)
    if (adTitle ~= nil) then
        adWheelTitleView:text(adTitle)
    end
    setadWheelTitleViewSize(data, adWheelTitleView, isPortrait)
    return adWheelTitleView
end

--轮播广告描述--
local function createAdWheelDesView(data, isPortrait)
    local adWheelDesView = Label()
    adWheelDesView:textSize(12)
    adWheelDesView:textColor(0x999999)
    if (System.android()) then
        adWheelDesView:gravity(Gravity.LEFT)
        adWheelDesView:maxLines(3)
    else
        adWheelDesView:lines(3)
    end
    setadWheelDesViewSize(data, adWheelDesView, isPortrait)
    return adWheelDesView
end

--轮播按钮描述--
local function createAdWheelBtnView(data, isPortrait)
    local adWheelBtnBackView = View()
    adWheelBtnBackView:backgroundColor(0x333333)
    local adWheelBtnView = Label()
    adWheelBtnView:textSize(12)
    adWheelBtnView:backgroundColor(0x333333)
    adWheelBtnView:textColor(0xffffff)
    adWheelBtnView:textAlign(TextAlign.CENTER)
    adWheelBtnBackView:addView(adWheelBtnView)
    setadWheelBtnViewSize(data, adWheelBtnView, adWheelBtnBackView, isPortrait)
    return adWheelBtnView, adWheelBtnBackView
end

--轮播Icon描述--
local function createAdWheelIconView(index, data, isPortrait)
    local adWheelIconView = Image(Native)
    adWheelIconView:scaleType(ScaleType.CENTER_CROP)
    setadWheelIconViewSize(index, data, adWheelIconView, isPortrait)
    return adWheelIconView
end

--轮播广告关闭按钮--
local function createAdWheelShutDownView(data, isPortrait)
    local shutDownView = View()
    --    shutDownView:align(Align.RIGHT)
    local shutDownImageView = Image(Native)
    shutDownImageView:align(Align.CENTER)
    shutDownImageView:scaleType(ScaleType.FIT_XY)
    shutDownImageView:image(Data(OS_ICON_WEDGE_CLOSE))
    shutDownView:addView(shutDownImageView)
    setadWheelShutDownViewSize(data, shutDownView, shutDownImageView, isPortrait)
    return shutDownView, shutDownImageView
end

local function createSeperatedView(data, isPortrait)
    local seperatedView = View()
    seperatedView:frame(16 * scale, 267 * scale, 343 * scale, 1)
    seperatedView:backgroundColor(0xf0f0f0)
    setadWheelSeperatedView(data, seperatedView, isPortrait)
    return seperatedView
end

local function createAdView(data, isPortrait)
    local adView = View()
    adView:backgroundColor(0x000000, 0.5)
    local adLabel = Label()
    adLabel:text("广告")
    adLabel:textColor(0x989898)
    adLabel:textSize(10)
    adLabel:textAlign(TextAlign.CENTER)
    adView:addView(adLabel)
    setadWheelAdView(data, adView, adLabel, isPortrait)
    return adView, adLabel
end

local function onCreate(data)

    local showLinkUrl = getHotspotExposureTrackLink(data, 1)
    if (showLinkUrl ~= nil) then
        Native:get(showLinkUrl)
    end
    if (adWheel.launchPlanId ~= nil) then
        osTrack(adWheel.launchPlanId, 1, 2)
        osTrack(adWheel.launchPlanId, 2, 2)
    end

    registerMedia()

    local isPortrait = Native:isPortraitScreen()
    adWheel.luaview = createLuaView(isPortrait)
    adWheel.luaview:backgroundColor(0x0D57D5, 0.5)
    adWheel.adWheelView = createAdWheelView(data, isPortrait)
    adWheel.adWheelDetailView = createDetailImageView(data, isPortrait)
    adWheel.adWheelTitleView = createAdWheelTitleView(data, isPortrait)
    adWheel.adWheelDesView = createAdWheelDesView(data, isPortrait)
    adWheel.adWheelBtnView, adWheel.adWheelBtnBackView = createAdWheelBtnView(data, isPortrait)
    adWheel.shutDownView, adWheel.shutDownImageView = createAdWheelShutDownView(data, isPortrait)
    adWheel.seperatedView = createSeperatedView(data, isPortrait)
    adWheel.adView, adWheel.adLabel = createAdView(data, isPortrait)

    local adIconImage1 = getAdImage(data, 1)
    local adIconImage2 = getAdImage(data, 2)
    local adIconImage3 = getAdImage(data, 3)

    if (adIconImage1 ~= nil) then
        adWheel.adWheelIconView1 = createAdWheelIconView(1, data, isPortrait)
        adWheel.adWheelIconView1:image(adIconImage1)
        setCurIconBorder(adWheel.hotspotOrder)
        adWheel.adWheelIconView1:onClick(function()
            if (adWheel.timer ~= nil) then
                adWheel.timer:cancel()
            end
            adWheel.hotspotOrder = 1
            adWheel.iconClick = true
            changeAdWheel(adWheel.hotspotOrder, data)
        end)
    end
    if (adIconImage2 ~= nil) then
        adWheel.adWheelIconView2 = createAdWheelIconView(2, data, isPortrait)
        adWheel.adWheelIconView2:image(adIconImage2)
        adWheel.adWheelIconView2:onClick(function()
            if (adWheel.timer ~= nil) then
                adWheel.timer:cancel()
            end
            adWheel.hotspotOrder = 2
            adWheel.iconClick = true
            changeAdWheel(adWheel.hotspotOrder, data)
        end)
    end
    if (adIconImage3 ~= nil) then
        adWheel.adWheelIconView3 = createAdWheelIconView(3, data, isPortrait)
        adWheel.adWheelIconView3:image(adIconImage3)
        adWheel.adWheelIconView3:onClick(function()
            if (adWheel.timer ~= nil) then
                adWheel.timer:cancel()
            end
            adWheel.hotspotOrder = 3
            adWheel.iconClick = true
            changeAdWheel(adWheel.hotspotOrder, data)
        end)
    end

    adWheel.luaview:addView(adWheel.adWheelView)

    adWheel.adWheelView:addView(adWheel.adWheelDetailView)
    adWheel.adWheelView:addView(adWheel.adWheelTitleView)
    adWheel.adWheelView:addView(adWheel.adWheelDesView)
    adWheel.adWheelView:addView(adWheel.adWheelBtnBackView)
    -- adWheel.adWheelView:addView(adWheel.shutDownView)
    adWheel.adWheelView:addView(adWheel.seperatedView)
    adWheel.adWheelView:addView(adWheel.adView)
    if (adWheel.adWheelIconView1 ~= nil) then
        adWheel.adWheelView:addView(adWheel.adWheelIconView1)
    end
    if (adWheel.adWheelIconView2 ~= nil) then
        adWheel.adWheelView:addView(adWheel.adWheelIconView2)
    end
    if (adWheel.adWheelIconView3 ~= nil) then
        adWheel.adWheelView:addView(adWheel.adWheelIconView3)
    end

    adWheel.luaview:addView(shutDownView)

    Native:widgetEvent(eventTypeShow, adWheel.id, adTypeName, actionTypeNone, "")

    adWheel.shutDownView:onClick(function()
        closeView()
    end)
    adWheel.adWheelView:onClick(function()
    end)
    adWheel.luaview:onClick(function()
        closeView()
    end)
    adWheel.adWheelBtnBackView:onClick(function()
        local clickLinkUrl = getHotspotClickTrackLink(data, 1)
        if (clickLinkUrl ~= nil) then
            Native:get(clickLinkUrl)
        end
        if (adWheel.launchPlanId ~= nil) then
            osTrack(adWheel.launchPlanId, 3, 2)
        end
        local linkUrl = getAdLinkUrl(data, adWheel.hotspotOrder)
        if (linkUrl ~= nil) then
            Native:widgetEvent(eventTypeClick, adWheel.id, adTypeName, actionTypeOpenUrl, linkUrl)
        end
    end)
    changeAdWheel(adWheel.hotspotOrder, data)
end

local function setConfig(data)
    if (data == nil) then
        return
    end
    adWheel.data = data
    adWheel.id = "adWheel_hotspot" .. tostring(adWheel.data.id)
    adWheel.launchPlanId = adWheel.data.launchPlanId

    local screenWidth, screenHeight = Native:getVideoSize(2)
    local videoWidth, videoHight, marginTop = Native:getVideoSize(0)
    adWheel.portraitWidth = math.min(screenWidth, screenHeight) --宽
    adWheel.portraitHeight = math.max(screenWidth, screenHeight) - videoHight - marginTop --高
    local hotspotOrder = data.hotspotOrder
    if (hotspotOrder == nil) then
        hotspotOrder = 0
    end
    adWheel.iconClick = false
    adWheel.hotspotOrder = hotspotOrder + 1
    adWheel.adWhellCount = getAdWhellCount(data)
end

function show(args)
    if (args == nil or args.data == nil or adWheel.luaview ~= nil) then
        return
    end

    setConfig(args.data)
    onCreate(args.data)
    Native:widgetEvent(eventTypeShow, adWheel.id, adTypeName, actionTypePauseVideo, "")
    Native:saveCacheData(adWheel.id, tostring(eventTypeShow))
end
