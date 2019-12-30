--
-- Created by IntelliJ IDEA.
-- User: videopls
-- Date: 2019/8/28
-- Time: 11:29 AM
-- To change this template use File | Settings | File Templates.
--
function toTable(data)
    local dataTable
    if (type(data) == 'string') then
        if (System.android()) then
            dataTable = Json:toTable(data)
        else
            dataTable = Native:jsonToTable(data)
        end
    else
        dataTable = data
    end
    return dataTable
end

function table_leng(t)
    if (t == nil) then
        return 0
    end
    local leng = 0
    for k, v in pairs(t) do
        leng = leng + 1
    end
    return leng;
end

object = {}
function object:new(o)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    return o
end

function getScale()
    local screenW, screenH = Native:getVideoSize(2)
    return math.min(screenW, screenH) / 375
end

desktopWindow = object:new()
local adTypeName = "desktopWindow"
local scale = getScale()
local RECENT_STATE_CLOSE = 0
local RECENT_STATE_OPEN = 1
local RECOMMEND_STATE_CLOSE = 0
local RECOMMEND_STATE_OPEN = 1

local OS_ICON_WAIT = "https://m.videojj.com/resource/os/os_desktop_more.png"
local DEFAULT_ICON_URL = "https://m.videojj.com/resource/os/os_desktop_default_icon.png"
local DEFAULT_WAIT_ICON_URL = "https://m.videojj.com/resource/os/os_desktop_default_icon.png"

local function translationAnim(x, y, duration)
    local anim = Animation():translation(x, y):duration(duration)
    return anim
end

local function startViewTranslationAnim(view, x, y, duration, table)
    if (view == nil) then
        return
    end
    if table ~= nil then
        translationAnim(x, y, duration):with(view):callback(table):start()
    else
        translationAnim(x, y, duration):with(view):start()
    end
end

local function exposureTrack(adsInfo)
    if (adsInfo == nil) then
        return
    end

    local hotspotTrackLinkTable = adsInfo.hotspotTrackLink
    if (hotspotTrackLinkTable == nil) then
        return
    end

    for i, v in ipairs(hotspotTrackLinkTable) do
        local showLinkUrl = v.exposureTrackLink
        if (showLinkUrl ~= nil) then
            -- print("luaView showLinkUrl " .. tostring(showLinkUrl))
            Native:get(showLinkUrl)
        end
    end
end

local function clickTrack(adsInfo, x, y)
    if (adsInfo == nil) then
        return
    end

    local hotspotTrackLinkTable = adsInfo.hotspotTrackLink
    if (hotspotTrackLinkTable == nil) then
        return nil
    end

    for i, v in ipairs(hotspotTrackLinkTable) do
        local clickLinkUrl = v.clickTrackLink
        if (clickLinkUrl ~= nil) then
            local value_x = "-999"
            local value_y = "-999"
            if(x ~= nil) then
                value_x = tostring(math.floor(x * 100) / 100)
            end
            if(y ~= nil) then
                value_y = tostring(math.floor(y * 100) / 100)
            end
            clickLinkUrl = string.gsub(clickLinkUrl, "__DOWN_X__", value_x)
            clickLinkUrl = string.gsub(clickLinkUrl, "__DOWN_Y__", value_y)
            clickLinkUrl = string.gsub(clickLinkUrl, "__UP_X__", value_x)
            clickLinkUrl = string.gsub(clickLinkUrl, "__UP_Y__", value_y)
            Native:get(clickLinkUrl)
        end
    end
end

local function closeView()
    Native:destroyView()
end

local function setDesktopWindowViewSize(desktopWindowView)
    if (desktopWindowView == nil) then
        return
    end
    desktopWindowView:frame(0, 0, desktopWindow.landscapeWidth, desktopWindow.landscapeHeight)
end

local function setDesktopScrollviewSize(desktopScrollview)
    if (desktopScrollview == nil) then
        return
    end
    local x, y, w, h = 0, 0, 0, 0
    y = 0
    w = desktopWindow.landscapeWidth
    h = desktopWindow.landscapeHeight
    desktopScrollview:frame(x, y, w, h)
    desktopScrollview:reload()
end

local function createDesktopWindowView()
    local desktopWindowView = View()
    desktopWindowView:backgroundColor(0x323741)
    setDesktopWindowViewSize(desktopWindowView)
    return desktopWindowView
end

local function getCurrentRecommendRowIndex(row)
    local firstIndex = nil
    local secondIndex = nil
    local thirdIndex = nil
    local waitIndex = nil
    if(desktopWindow.recommendList == nil) then
        return firstIndex,secondIndex,thirdIndex,waitIndex
    end

    local maxRowIndex = row * 3
    if(maxRowIndex <= desktopWindow.recommendCount) then
        firstIndex = maxRowIndex - 2
        secondIndex = maxRowIndex - 1
        thirdIndex = maxRowIndex
        if(maxRowIndex == desktopWindow.recommendCount) then
            waitIndex = thirdIndex
        end
    else
        local previousMaxRowIndex = (row - 1) * 3
        local currentRowCount = desktopWindow.recommendCount - previousMaxRowIndex
        if(currentRowCount == 1) then
            firstIndex = previousMaxRowIndex + 1
            waitIndex = firstIndex
        end

        if(currentRowCount == 2) then
            firstIndex = previousMaxRowIndex + 1
            secondIndex = previousMaxRowIndex + 2
            waitIndex = secondIndex
        end
    end
    return firstIndex,secondIndex,thirdIndex,waitIndex
end

local function sendAction(miniAppId, miniAppScreenType, miniAppType)
    if (miniAppId == nil or miniAppScreenType == nil or miniAppType == nil) then
        return
    end
    if (desktopWindow.miniAppInfo ~= nil and desktopWindow.miniAppInfo.appletId ~= nil and Native.commonTrack) then
        local infoDic = {}
        infoDic["originMiniAppId"] = desktopWindow.miniAppInfo.appletId
        infoDic["miniAppId"] = miniAppId
        Native:commonTrack(1, infoDic)
    end
    local dic = {}
    dic["miniAppId"] = miniAppId
    Native:sendAction(Native:base64Encode("LuaView://applets?appletId=" .. miniAppId .. "&type=" .. miniAppScreenType .. "&appType=" .. miniAppType), dic)
end

local function getRecommendTopCount()
    if(desktopWindow.recommendCount == nil or desktopWindow.recommendCount <= 0) then
        return 0
    end
    return 1
end

local function getRecommendRowCount()
    if(desktopWindow.recommendCount == nil or desktopWindow.recommendCount <= 0) then
        return 0
    end

    local count = math.ceil(desktopWindow.recommendCount / 3)
    if(desktopWindow.recommendState == RECOMMEND_STATE_CLOSE) then
        count = 2;
    else
        if(count <= 1) then
            count = 2;
        end
    end
    return count
end

local function createDesktopRecommendCell(cell, section, row)
    cell.firstImageView = Image(Native)
    cell.firstImageView:scaleType(ScaleType.FIT_CENTER)
    cell.firstImageView:cornerRadius(16 * scale)
    cell.firstImageView:image(DEFAULT_ICON_URL)

    cell.firstTitleView = Label()
    cell.firstTitleView:textSize(10 * scale)
--    cell.firstTitleView:textColor(0xFFE6E6E6)
    cell.firstTitleView:textAlign(TextAlign.CENTER)

    cell.secondImageView = Image(Native)
    cell.secondImageView:scaleType(ScaleType.FIT_CENTER)
    cell.secondImageView:cornerRadius(16 * scale)
    cell.secondImageView:image(DEFAULT_ICON_URL)

    cell.secondTitleView = Label()
    cell.secondTitleView:textSize(10 * scale)
--    cell.secondTitleView:textColor(0xFFE6E6E6)
    cell.secondTitleView:textAlign(TextAlign.CENTER)

    cell.thirdImageView = Image(Native)
    cell.thirdImageView:scaleType(ScaleType.FIT_CENTER)
    cell.thirdImageView:cornerRadius(16 * scale)
    cell.thirdImageView:image(DEFAULT_ICON_URL)

    cell.thirdTitleView = Label()
    cell.thirdTitleView:textSize(10 * scale)
--    cell.thirdTitleView:textColor(0xFFE6E6E6)
    cell.thirdTitleView:textAlign(TextAlign.CENTER)
end

local function setDesktopRecommendCellSize(cell, section, row)

    local firstImageViewX = desktopWindow.landscapeWidth * 0.0826
    local firstImageViewY = desktopWindow.landscapeHeight * 0.0187
    local firstImageViewW = desktopWindow.landscapeWidth * 0.2239
    local firstImageViewH = desktopWindow.landscapeHeight * 0.1373
    if (firstImageViewW > firstImageViewH) then
        firstImageViewW = firstImageViewH
    else
        firstImageViewH = firstImageViewW
    end
    cell.firstImageView:frame(firstImageViewX,firstImageViewY,firstImageViewW,firstImageViewH)

    local firstTitleViewX = desktopWindow.landscapeWidth * 0.0826
    local firstTitleViewY = desktopWindow.landscapeHeight * 0.1707
    local firstTitleViewW = desktopWindow.landscapeWidth * 0.2239
    local firstTitleViewH = desktopWindow.landscapeHeight * 0.0480
    cell.firstTitleView:textColor(0xFFE6E6E6)
    cell.firstTitleView:frame(firstTitleViewX,firstTitleViewY,firstTitleViewW,firstTitleViewH)

    local secondImageViewX = desktopWindow.landscapeWidth * 0.3826
    local secondImageViewY = desktopWindow.landscapeHeight * 0.0187
    local secondImageViewW = desktopWindow.landscapeWidth * 0.2239
    local secondImageViewH = desktopWindow.landscapeHeight * 0.1373
    if (secondImageViewW > secondImageViewH) then
        secondImageViewW = secondImageViewH
    else
        secondImageViewH = secondImageViewW
    end
    cell.secondImageView:frame(secondImageViewX,secondImageViewY,secondImageViewW,secondImageViewH)

    local secondTitleViewX = desktopWindow.landscapeWidth * 0.3826
    local secondTitleViewY = desktopWindow.landscapeHeight * 0.1707
    local secondTitleViewW = desktopWindow.landscapeWidth * 0.2239
    local secondTitleViewH = desktopWindow.landscapeHeight * 0.0480
    cell.secondTitleView:textColor(0xFFE6E6E6)
    cell.secondTitleView:frame(secondTitleViewX,secondTitleViewY,secondTitleViewW,secondTitleViewH)

    local thirdImageViewX = desktopWindow.landscapeWidth * 0.6826
    local thirdImageViewY = desktopWindow.landscapeHeight * 0.0187
    local thirdImageViewW = desktopWindow.landscapeWidth * 0.2239
    local thirdImageViewH = desktopWindow.landscapeHeight * 0.1373
    if (thirdImageViewW > thirdImageViewH) then
        thirdImageViewW = thirdImageViewH
    else
        thirdImageViewH = thirdImageViewW
    end
    cell.thirdImageView:frame(thirdImageViewX,thirdImageViewY,thirdImageViewW,thirdImageViewH)

    local thirdTitleViewX = desktopWindow.landscapeWidth * 0.6826
    local thirdTitleViewY = desktopWindow.landscapeHeight * 0.1707
    local thirdTitleViewW = desktopWindow.landscapeWidth * 0.2239
    local thirdTitleViewH = desktopWindow.landscapeHeight * 0.0480
    cell.thirdTitleView:textColor(0xFFE6E6E6)
    cell.thirdTitleView:frame(thirdTitleViewX,thirdTitleViewY,thirdTitleViewW,thirdTitleViewH)

    local firstIndex,secondIndex,thirdIndex,waitIndex = getCurrentRecommendRowIndex(row)
    if(firstIndex == nil) then
        cell.firstImageView:hide()
        cell.firstTitleView:hide()
    elseif(firstIndex ~= waitIndex) then
        cell.firstImageView:show()
        cell.firstImageView:image(desktopWindow.recommendList[firstIndex].icon)
        --        cell.firstImageView:image(Data(DEFAULT_ICON))
        cell.firstTitleView:show()
        cell.firstTitleView:text(desktopWindow.recommendList[firstIndex].miniAppName)
    end

    if(secondIndex == nil) then
        cell.secondImageView:hide()
        cell.secondTitleView:hide()
    elseif(secondIndex ~= waitIndex) then
        cell.secondImageView:show()
        cell.secondImageView:image(desktopWindow.recommendList[secondIndex].icon)
        --        cell.secondImageView:image(Data(DEFAULT_ICON))
        cell.secondTitleView:show()
        cell.secondTitleView:text(desktopWindow.recommendList[secondIndex].miniAppName)
    end

    if(thirdIndex == nil) then
        cell.thirdImageView:hide()
        cell.thirdTitleView:hide()
    elseif(thirdIndex ~= waitIndex) then
        cell.thirdImageView:show()
        cell.thirdImageView:image(desktopWindow.recommendList[thirdIndex].icon)
        --        cell.thirdImageView:image(Data(DEFAULT_ICON))
        cell.thirdTitleView:show()
        cell.thirdTitleView:text(desktopWindow.recommendList[thirdIndex].miniAppName)
    end

    cell.firstImageView:onClick(function()
        local recentFirstIndexBean = desktopWindow.recommendList[firstIndex]
        local miniAppId = recentFirstIndexBean.miniAppId
        local miniAppScreenType = recentFirstIndexBean.miniAppScreenType
        local miniAppType = recentFirstIndexBean.miniAppType
        sendAction(miniAppId, miniAppScreenType, miniAppType)
    end)

    cell.secondImageView:onClick(function()
        local recentSecondIndexBean = desktopWindow.recommendList[secondIndex]
        local miniAppId = recentSecondIndexBean.miniAppId
        local miniAppScreenType = recentSecondIndexBean.miniAppScreenType
        local miniAppType = recentSecondIndexBean.miniAppType
        sendAction(miniAppId,miniAppScreenType,miniAppType)
    end)

    cell.thirdImageView:onClick(function()
        local recentThirdIndexBean = desktopWindow.recommendList[thirdIndex]
        local miniAppId = recentThirdIndexBean.miniAppId
        local miniAppScreenType = recentThirdIndexBean.miniAppScreenType
        local miniAppType = recentThirdIndexBean.miniAppType
        sendAction(miniAppId,miniAppScreenType,miniAppType)
    end)

    if(waitIndex ~= nil) then
        if(waitIndex == firstIndex) then
            cell.firstImageView:show()
            cell.firstImageView:image(OS_ICON_WAIT)
            cell.firstTitleView:show()
            cell.firstTitleView:text("敬请期待")
            cell.firstTitleView:textColor(0xA5ABB3)
            cell.firstImageView:onClick(function()
            end)
        elseif(waitIndex == secondIndex) then
            cell.secondImageView:show()
            cell.secondImageView:image(OS_ICON_WAIT)
            cell.secondTitleView:show()
            cell.secondTitleView:text("敬请期待")
            cell.secondTitleView:textColor(0xA5ABB3)
            cell.secondImageView:onClick(function()
            end)
        elseif(waitIndex == thirdIndex) then
            cell.thirdImageView:show()
            cell.thirdImageView:image(OS_ICON_WAIT)
            cell.thirdTitleView:show()
            cell.thirdTitleView:text("敬请期待")
            cell.thirdTitleView:textColor(0xA5ABB3)
            cell.thirdImageView:onClick(function()
            end)
        end
    end
end

local function getCurrentRecentRowIndex(row)
    local firstIndex = nil
    local secondIndex = nil
    local thirdIndex = nil
    if(desktopWindow.recentList == nil) then
        return firstIndex,secondIndex,thirdIndex
    end

    local maxRowIndex = row * 3
    if(maxRowIndex <= desktopWindow.recentCount) then
        firstIndex = maxRowIndex - 2
        secondIndex = maxRowIndex - 1
        thirdIndex = maxRowIndex
    else
        local previousMaxRowIndex = (row - 1) * 3
        local currentRowCount = desktopWindow.recentCount - previousMaxRowIndex
        if(currentRowCount == 1) then
            firstIndex = previousMaxRowIndex + 1
        end

        if(currentRowCount == 2) then
            firstIndex = previousMaxRowIndex + 1
            secondIndex = previousMaxRowIndex + 2
        end
    end
    return firstIndex,secondIndex,thirdIndex
end

local function createDesktopRecentTopView(cell, section, row)
    cell.recentTopLayout = GradientView()
    cell.recentTopLayout:corner(8 * scale, 8 * scale, 8 * scale, 8 * scale, 0, 0, 0, 0)

    cell.recentTitleView = Label()
    cell.recentTitleView:textSize(10 * scale)
    cell.recentTitleView:textColor(0xFFA5ABB3)
    cell.recentTitleView:text("最近使用")

    cell.recentOperateView = Label()
    cell.recentOperateView:textSize(10 * scale)
    cell.recentOperateView:textColor(0xFFA5ABB3)
    cell.recentOperateView:text("更多")

    cell.recentTopLayout:addView(cell.recentTitleView)
    cell.recentTopLayout:addView(cell.recentOperateView)
end

local function setDesktopRecentTopViewSize(cell, section, row)
    local topLayoutX = 0
    local topLayoutY = desktopWindow.landscapeHeight * 0.032
    local topLayoutW = desktopWindow.landscapeWidth
    local topLayoutH = desktopWindow.landscapeHeight * 0.0693
    cell.recentTopLayout:frame(topLayoutX,topLayoutY,topLayoutW,topLayoutH)

    local topTitleX = desktopWindow.landscapeWidth * 0.0870
    local topTitleW = desktopWindow.landscapeWidth * 0.2609
    local topTitleH = desktopWindow.landscapeHeight * 0.0533
    local topTitleY = (topLayoutH - topTitleH) * 0.5
    cell.recentTitleView:frame(topTitleX,topTitleY,topTitleW,topTitleH)

    local topOperateX = desktopWindow.landscapeWidth * 0.8261
    local topOperateW = desktopWindow.landscapeWidth * 0.1043
    local topOperateH = desktopWindow.landscapeHeight * 0.0533
    local topOperateY = (topLayoutH - topOperateH) * 0.5
    cell.recentOperateView:frame(topOperateX,topOperateY,topOperateW,topOperateH)

    if(desktopWindow.isHideRecentOperateView) then
        cell.recentOperateView:hide()
    else
        cell.recentOperateView:show()
    end

    cell.recentOperateView:onClick(function()
        if(desktopWindow.recentState == RECENT_STATE_CLOSE) then
            cell.recentOperateView:text("收起")
            desktopWindow.recentState = RECENT_STATE_OPEN
        else
            cell.recentOperateView:text("更多")
            desktopWindow.recentState = RECENT_STATE_CLOSE
        end
        desktopWindow.desktopScrollview:reload()
    end)
end

local function createDesktopRecentCell(cell, section, row)
    cell.firstImageView = Image(Native)
    cell.firstImageView:scaleType(ScaleType.FIT_CENTER)
    cell.firstImageView:cornerRadius(16 * scale)
    cell.firstImageView:image(DEFAULT_ICON_URL)

    cell.firstTitleView = Label()
    cell.firstTitleView:textSize(10 * scale)
    cell.firstTitleView:textColor(0xFFE6E6E6)
    cell.firstTitleView:textAlign(TextAlign.CENTER)

    cell.secondImageView = Image(Native)
    cell.secondImageView:scaleType(ScaleType.FIT_CENTER)
    cell.secondImageView:cornerRadius(16 * scale)
    cell.secondImageView:image(DEFAULT_ICON_URL)

    cell.secondTitleView = Label()
    cell.secondTitleView:textSize(10 * scale)
    cell.secondTitleView:textColor(0xFFE6E6E6)
    cell.secondTitleView:textAlign(TextAlign.CENTER)

    cell.thirdImageView = Image(Native)
    cell.thirdImageView:scaleType(ScaleType.FIT_CENTER)
    cell.thirdImageView:cornerRadius(16 * scale)
    cell.thirdImageView:image(DEFAULT_ICON_URL)

    cell.thirdTitleView = Label()
    cell.thirdTitleView:textSize(10 * scale)
    cell.thirdTitleView:textColor(0xFFE6E6E6)
    cell.thirdTitleView:textAlign(TextAlign.CENTER)
end

local function setDesktopRecentCellSize(cell, section, row)
    local firstImageViewX = desktopWindow.landscapeWidth * 0.0826
    local firstImageViewY = desktopWindow.landscapeHeight * 0.0187
    local firstImageViewW = desktopWindow.landscapeWidth * 0.2239
    local firstImageViewH = desktopWindow.landscapeHeight * 0.1373
    if (firstImageViewW > firstImageViewH) then
        firstImageViewW = firstImageViewH
    else
        firstImageViewH = firstImageViewW
    end
    cell.firstImageView:frame(firstImageViewX,firstImageViewY,firstImageViewW,firstImageViewH)

    local firstTitleViewX = desktopWindow.landscapeWidth * 0.0826
    local firstTitleViewY = desktopWindow.landscapeHeight * 0.1707
    local firstTitleViewW = desktopWindow.landscapeWidth * 0.2239
    local firstTitleViewH = desktopWindow.landscapeHeight * 0.0480
    cell.firstTitleView:frame(firstTitleViewX,firstTitleViewY,firstTitleViewW,firstTitleViewH)

    local secondImageViewX = desktopWindow.landscapeWidth * 0.3826
    local secondImageViewY = desktopWindow.landscapeHeight * 0.0187
    local secondImageViewW = desktopWindow.landscapeWidth * 0.2239
    local secondImageViewH = desktopWindow.landscapeHeight * 0.1373
    if (secondImageViewW > secondImageViewH) then
        secondImageViewW = secondImageViewH
    else
        secondImageViewH = secondImageViewW
    end
    cell.secondImageView:frame(secondImageViewX,secondImageViewY,secondImageViewW,secondImageViewH)

    local secondTitleViewX = desktopWindow.landscapeWidth * 0.3826
    local secondTitleViewY = desktopWindow.landscapeHeight * 0.1707
    local secondTitleViewW = desktopWindow.landscapeWidth * 0.2239
    local secondTitleViewH = desktopWindow.landscapeHeight * 0.0480
    cell.secondTitleView:frame(secondTitleViewX,secondTitleViewY,secondTitleViewW,secondTitleViewH)

    local thirdImageViewX = desktopWindow.landscapeWidth * 0.6826
    local thirdImageViewY = desktopWindow.landscapeHeight * 0.0187
    local thirdImageViewW = desktopWindow.landscapeWidth * 0.2239
    local thirdImageViewH = desktopWindow.landscapeHeight * 0.1373
    if (thirdImageViewW > thirdImageViewH) then
        thirdImageViewW = thirdImageViewH
    else
        thirdImageViewH = thirdImageViewW
    end
    cell.thirdImageView:frame(thirdImageViewX,thirdImageViewY,thirdImageViewW,thirdImageViewH)

    local thirdTitleViewX = desktopWindow.landscapeWidth * 0.6826
    local thirdTitleViewY = desktopWindow.landscapeHeight * 0.1707
    local thirdTitleViewW = desktopWindow.landscapeWidth * 0.2239
    local thirdTitleViewH = desktopWindow.landscapeHeight * 0.0480
    cell.thirdTitleView:frame(thirdTitleViewX,thirdTitleViewY,thirdTitleViewW,thirdTitleViewH)

    local firstIndex,secondIndex,thirdIndex = getCurrentRecentRowIndex(row)
    if(firstIndex == nil) then
        cell.firstImageView:hide()
        cell.firstTitleView:hide()
    else
        cell.firstImageView:show()
        cell.firstImageView:image(desktopWindow.recentList[firstIndex].icon)
        --        cell.firstImageView:image(Data(DEFAULT_ICON))
        cell.firstTitleView:show()
        cell.firstTitleView:text(desktopWindow.recentList[firstIndex].miniAppName)
    end

    if(secondIndex == nil) then
        cell.secondImageView:hide()
        cell.secondTitleView:hide()
    else
        cell.secondImageView:show()
        cell.secondImageView:image(desktopWindow.recentList[secondIndex].icon)
        --        cell.secondImageView:image(Data(DEFAULT_ICON))
        cell.secondTitleView:show()
        cell.secondTitleView:text(desktopWindow.recentList[secondIndex].miniAppName)
    end

    if(thirdIndex == nil) then
        cell.thirdImageView:hide()
        cell.thirdTitleView:hide()
    else
        cell.thirdImageView:show()
        cell.thirdImageView:image(desktopWindow.recentList[thirdIndex].icon)
        --        cell.thirdImageView:image(Data(DEFAULT_ICON))
        cell.thirdTitleView:show()
        cell.thirdTitleView:text(desktopWindow.recentList[thirdIndex].miniAppName)
    end

    cell.firstImageView:onClick(function()
        local recentFirstIndexBean = desktopWindow.recentList[firstIndex]
        local miniAppId = recentFirstIndexBean.miniAppId
        local miniAppScreenType = recentFirstIndexBean.miniAppScreenType
        local miniAppType = recentFirstIndexBean.miniAppType
        sendAction(miniAppId,miniAppScreenType,miniAppType)
    end)

    cell.secondImageView:onClick(function()
        local recentSecondIndexBean = desktopWindow.recentList[secondIndex]
        local miniAppId = recentSecondIndexBean.miniAppId
        local miniAppScreenType = recentSecondIndexBean.miniAppScreenType
        local miniAppType = recentSecondIndexBean.miniAppType
        sendAction(miniAppId,miniAppScreenType,miniAppType)
    end)

    cell.thirdImageView:onClick(function()
        local recentThirdIndexBean = desktopWindow.recentList[thirdIndex]
        local miniAppId = recentThirdIndexBean.miniAppId
        local miniAppScreenType = recentThirdIndexBean.miniAppScreenType
        local miniAppType = recentThirdIndexBean.miniAppType
        sendAction(miniAppId,miniAppScreenType,miniAppType)
    end)
end

local function getDesktopRecentTopWH()
    if(desktopWindow.recentCount == nil or desktopWindow.recentCount <= 0) then
        return 1, 1
    end
    local w,h = 0,0
    local recentTopScaleH = 0.032 + 0.0693

    local count = math.ceil(desktopWindow.recentCount / 3)
    if(count > 1) then
        desktopWindow.isHideRecentOperateView = false
    end

    w = desktopWindow.landscapeWidth
    h = desktopWindow.landscapeHeight * recentTopScaleH
    return w,h
end

local function getDesktopRecommendTopWH()
    if(desktopWindow.recommendCount == nil or desktopWindow.recommendCount <= 0) then
        return 1, 1
    end

    local count = math.ceil(desktopWindow.recommendCount / 3)
    if(count <= 2) then
        desktopWindow.isHideRecommendOperateView = true
    else
        desktopWindow.isHideRecommendOperateView = false
    end

    local w,h = 0,0
    local recentScaleH = 0.032 + 0.0693
    w = desktopWindow.landscapeWidth
    h = desktopWindow.landscapeHeight * recentScaleH
    return w,h
end

local function createDesktopRecommendTopView(cell, section, row)
    cell.recommendTopLayout = GradientView()
    --    cell.recommendTopLayout:backgroundColor(0xD0D2EE,0.06)
    cell.recommendTopLayout:corner(8 * scale, 8 * scale, 8 * scale, 8 * scale, 0, 0, 0, 0)

    cell.recommendTitleView = Label()
    cell.recommendTitleView:textSize(10 * scale)
    cell.recommendTitleView:textColor(0xFFA5ABB3)
    cell.recommendTitleView:text("推荐小程序")

    cell.recommendOperateView = Label()
    cell.recommendOperateView:textSize(10 * scale)
    cell.recommendOperateView:textColor(0xFFA5ABB3)
    cell.recommendOperateView:text("更多")

    cell.recommendTopLayout:addView(cell.recommendTitleView)
    cell.recommendTopLayout:addView(cell.recommendOperateView)
end

local function setDesktopRecommendTopViewSize(cell, section, row)
    local topLayoutX = 0
    local topLayoutY = desktopWindow.landscapeHeight * 0.032
    local topLayoutW = desktopWindow.landscapeWidth
    local topLayoutH = desktopWindow.landscapeHeight * 0.0693
    cell.recommendTopLayout:frame(topLayoutX,topLayoutY,topLayoutW,topLayoutH)

    local topTitleX = desktopWindow.landscapeWidth * 0.0870
    local topTitleW = desktopWindow.landscapeWidth * 0.3043
    local topTitleH = desktopWindow.landscapeHeight * 0.0533
    local topTitleY = (topLayoutH - topTitleH) * 0.5
    cell.recommendTitleView:frame(topTitleX,topTitleY,topTitleW,topTitleH)

    local topOperateX = desktopWindow.landscapeWidth * 0.8261
    local topOperateW = desktopWindow.landscapeWidth * 0.1043
    local topOperateH = desktopWindow.landscapeHeight * 0.0533
    local topOperateY = (topLayoutH - topOperateH) * 0.5
    cell.recommendOperateView:frame(topOperateX,topOperateY,topOperateW,topOperateH)

    if(desktopWindow.isHideRecommendOperateView) then
        cell.recommendOperateView:hide()
    else
        cell.recommendOperateView:show()
    end

    cell.recommendOperateView:onClick(function()
        if(desktopWindow.recommendState == RECOMMEND_STATE_CLOSE) then
            cell.recommendOperateView:text("收起")
            desktopWindow.recommendState = RECOMMEND_STATE_OPEN
        else
            cell.recommendOperateView:text("更多")
            desktopWindow.recommendState = RECOMMEND_STATE_CLOSE
        end
        desktopWindow.desktopScrollview:reload()
    end)

end

local function getDesktopRecentWH()
    if(desktopWindow.recentCount == nil or desktopWindow.recentCount <= 0) then
        return 1, 1
    end
    local w,h = 0,0
    w = desktopWindow.landscapeWidth
    h = desktopWindow.landscapeHeight * 0.2240
    return w,h
end

local function getDesktopRecommendWH()
    if(desktopWindow.recommendCount == nil or desktopWindow.recommendCount <= 0) then
        return 0, 0
    end
    local w,h = 0,0
    local recentScaleH = 0.2240
    w = desktopWindow.landscapeWidth
    h = desktopWindow.landscapeHeight * recentScaleH
    return w,h
end

local function getRecentTopCount()
    if(desktopWindow.recentCount == nil or desktopWindow.recentCount <= 0) then
        return 0
    end
    return 1
end

local function getAdCount()
    if(desktopWindow.desktopAdInfo == nil) then
        return 0
    end
    return 1
end

local function getRecentRowCount()
    if(desktopWindow.recentCount == nil or desktopWindow.recentCount <= 0) then
        return 0
    end
    if(desktopWindow.recentState == RECENT_STATE_CLOSE) then
        return 1;
    end
    return math.ceil(desktopWindow.recentCount / 3)
end

local function getDesktopAdWH()
    local w,h = 0,0
    w = desktopWindow.landscapeWidth
    h = (desktopWindow.desktopAdInfo.height / desktopWindow.desktopAdInfo.width) * desktopWindow.landscapeWidth
    desktopWindow.adContentHeight = h
    return w,h
end

local function createDesktopAdView(cell, section, row)
    cell.adContentLayout = View()
--    cell.adContentLayout:cornerRadius(8 * scale)

    cell.adContentImageView = Image(Native)
    cell.adContentImageView:cornerRadius(4 * scale)
    cell.adContentImageView:scaleType(ScaleType.CENTER_CROP)
--    cell.adContentImageView:backgroundColor(0xFF0000)

    cell.adContentTitleBgView = GradientView()
    cell.adContentTitleBgView:backgroundColor(0x000000,0.5)
    cell.adContentTitleBgView:corner(0, 0, 0, 0, 0, 0, 4 * scale, 4 * scale)
    cell.adContentTitleBgView:alignBottom()

    cell.adContentTitleView = Label()
    cell.adContentTitleView:textSize(9 * scale)
    cell.adContentTitleView:textColor(0xFFFFFF,0.7)
    cell.adContentTitleView:text("广告")
    cell.adContentTitleView:textAlign(TextAlign.CENTER)

    cell.adContentTitleBgView:addView(cell.adContentTitleView)


    cell.adContentLayout:addView(cell.adContentImageView)
    cell.adContentLayout:addView(cell.adContentTitleBgView)
end

local function setDesktopAdViewSize(cell, section, row)

    local adContentLayoutX = desktopWindow.landscapeWidth * 0.0804
    local adContentLayoutY = desktopWindow.landscapeHeight * 0.032
    local adContentLayoutW = desktopWindow.landscapeWidth * 0.8261
    local adContentLayoutH = desktopWindow.adContentHeight
    cell.adContentLayout:frame(adContentLayoutX, adContentLayoutY, adContentLayoutW, adContentLayoutH)
--    cell.adContentLayout:backgroundColor(0xFF0000)

    local adContentImageViewX = 0
    local adContentImageViewY = 0
    local adContentImageViewW = desktopWindow.landscapeWidth * 0.8261
    local adContentImageViewH = desktopWindow.adContentHeight
    cell.adContentImageView:frame(adContentImageViewX, adContentImageViewY, adContentImageViewW, adContentImageViewH)
--    cell.adContentImageView:backgroundColor(0x00FF00)

    local adContentTitleBgViewX = 0
    local adContentTitleBgViewY = 0
    local adContentTitleBgViewW = desktopWindow.landscapeWidth * 0.1152
    local adContentTitleBgViewH = desktopWindow.landscapeHeight * 0.0347
    cell.adContentTitleBgView:frame(adContentTitleBgViewX, adContentTitleBgViewY, adContentTitleBgViewW, adContentTitleBgViewH)

    local adContentTitleViewX = 0
    local adContentTitleViewY = 0
    local adContentTitleViewW = desktopWindow.landscapeWidth * 0.1152
    local adContentTitleViewH = desktopWindow.landscapeHeight * 0.0347
    cell.adContentTitleView:frame(adContentTitleViewX, adContentTitleViewY, adContentTitleViewW, adContentTitleViewH)

    if (desktopWindow.desktopAdInfo ~= nil and desktopWindow.desktopAdInfo.resUrl ~= nil and string.len(desktopWindow.desktopAdInfo.resUrl) > 0) then
        cell.adContentImageView:image(desktopWindow.desktopAdInfo.resUrl)
    end

    if(not desktopWindow.exposureTrack) then
        desktopWindow.exposureTrack = true
        exposureTrack(desktopWindow.desktopAdInfo)
    end

    cell.adContentImageView:onClick(function(x, y, mx, my)
        clickTrack(desktopWindow.desktopAdInfo, x, y)

        if (desktopWindow.desktopAdInfo ~= nil and desktopWindow.desktopAdInfo.linkData ~= nil) then
            local linkUrl = desktopWindow.desktopAdInfo.linkData.linkUrl
            if (linkUrl ~= nil) then
                local value_x = "-999"
                local value_y = "-999"
                if(x ~= nil) then
                    value_x = tostring(math.floor(x * 100) / 100)
                end
                if(y ~= nil) then
                    value_y = tostring(math.floor(y * 100) / 100)
                end
                linkUrl = string.gsub(linkUrl, "__DOWN_X__", value_x)
                linkUrl = string.gsub(linkUrl, "__DOWN_Y__", value_y)
                linkUrl = string.gsub(linkUrl, "__UP_X__", value_x)
                linkUrl = string.gsub(linkUrl, "__UP_Y__", value_y)
                desktopWindow.desktopAdInfo.linkData.linkUrl = linkUrl
            end
            desktopWindow.desktopAdInfo.launchPlanId = desktopWindow.launchPlanId

            Applet:openAds(desktopWindow.desktopAdInfo)
        end
    end)
end

local function createDesktopScrollView()

    local desktopScrollview = CollectionView {
        Section = {
            SectionCount = function()
                -- 返回页面区块的个数（不同区块的种类数）
                return 5
            end,
            RowCount = function(section)
                -- 返回每个区块对应有的坑位数
                if(section == 1) then
                    return getRecentTopCount()
                elseif (section == 2) then
                   return getRecentRowCount()
                elseif (section == 3) then
                    return getAdCount()
                elseif (section == 4) then
                    return getRecommendTopCount()
                elseif (section == 5) then
                    return getRecommendRowCount()
                end
            end
        },
        Cell = {
            Id = function(section, row)
                -- 返回每个区块对应额坑位ID
                if(section == 1) then
                    return "DesktopRecentTop"
                elseif (section == 2) then
                    return "DesktopRecentContent"
                elseif (section == 3) then
                    return "DesktopAdContent"
                elseif (section == 4) then
                    return "DesktopRecommendTop"
                elseif (section == 5) then
                    return "DesktopRecommendContent"
                end
            end,
            DesktopRecentTop = {
                Size = function(section, row)
                    return getDesktopRecentTopWH()
                end,
                Init = function(cell, section, row)
                    createDesktopRecentTopView(cell, section, row)
                end,
                Layout = function(cell, section, row)
                    setDesktopRecentTopViewSize(cell, section, row)
                end
            },
            DesktopRecentContent = {
                Size = function(section, row)
                    return getDesktopRecentWH()
                end,
                Init = function(cell, section, row)
                    createDesktopRecentCell(cell, section, row)
                end,
                Layout = function(cell, section, row)
                    setDesktopRecentCellSize(cell, section, row)
                end
            },
            DesktopAdContent = {
                Size = function(section, row)
                    return getDesktopAdWH()
                end,
                Init = function(cell, section, row)
                    createDesktopAdView(cell, section, row)
                end,
                Layout = function(cell, section, row)
                    setDesktopAdViewSize(cell, section, row)
                end
            },
            DesktopRecommendTop = {
                Size = function(section, row)
                    return getDesktopRecommendTopWH()
                end,
                Init = function(cell, section, row)
                    createDesktopRecommendTopView(cell, section, row)
                end,
                Layout = function(cell, section, row)
                    setDesktopRecommendTopViewSize(cell, section, row)
                end
            },
            DesktopRecommendContent = {
                Size = function(section, row)
                    return getDesktopRecommendWH()
                end,
                Init = function(cell, section, row)
                    createDesktopRecommendCell(cell, section, row)
                end,
                Layout = function(cell, section, row)
                    setDesktopRecommendCellSize(cell, section, row)
                end
            }
        },
        Callback = {
            -- 整个CollectionView的事件回调
            Scrolling = function(firstVisibleSection, firstVisibleRow, visibleCellCount)
                -- 滚动中回调
            end,
            ScrollBegin = function(firstVisibleSection, firstVisibleRow, visibleCellCount)
                -- 滚动开始回调
            end,
            ScrollEnd = function(firstVisibleSection, firstVisibleRow, visibleCellCount)
                -- 滚动结束回调
            end
        }
    }
    setDesktopScrollviewSize(desktopScrollview)
    return desktopScrollview

end

--屏幕旋转--
local function rotationScreen(isPortrait)
    if (isPortrait) then
        desktopWindow.desktopWindowView:hide()
    else
        desktopWindow.desktopWindowView:show()
    end
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

local function setConfig()
    desktopWindow.recentCount = 0
    if(desktopWindow.recentList ~= nil) then
        desktopWindow.recentCount = table_leng(desktopWindow.recentList)
    end

    desktopWindow.recommendCount = 0
    if(desktopWindow.recommendList ~= nil) then
        desktopWindow.recommendCount = table_leng(desktopWindow.recommendList) + 1
    end

    desktopWindow.recentState = RECENT_STATE_CLOSE
    desktopWindow.recommendState = RECOMMEND_STATE_CLOSE

    desktopWindow.isHideRecentOperateView = true
    desktopWindow.isHideRecommendOperateView = true
end

local function onCreate()
    setConfig()
    desktopWindow.media = registerMedia()
--    desktopWindow.desktopWindowView = createDesktopWindowView()

    desktopWindow.desktopScrollview = createDesktopScrollView()
    desktopWindow.desktopWindowView:addView(desktopWindow.desktopScrollview)

    if System.ios() then

    else
        desktopWindow.desktopWindowView:translation(desktopWindow.landscapeWidth, 0)
        startViewTranslationAnim(desktopWindow.desktopWindowView, 0, 0, 0.3)
    end
end

local function getRecentRecommendDesktopInfo(callback)
    
    local identity = Native:getIdentity()
    local currentApps = Applet:getStorageData("recentMiniAppId", identity)

    local paramData = {
        commonParam = Native:commonParam(),
        recentMiniAppIds = Native:jsonToTable(currentApps)
    }
    local paramDataString = Native:tableToJson(paramData)
    local OS_HTTP_GET_COUPON_RED_PACKET = Native:videoOShost() .. "/vision/deskMiniApps/list"
    local OS_HTTP_PUBLIC_KEY = Native:appSecret()
    print("request params : ",paramDataString)
    local requestId = desktopWindow.request:post(OS_HTTP_GET_COUPON_RED_PACKET, {
        data = Native:aesEncrypt(paramDataString, OS_HTTP_PUBLIC_KEY, OS_HTTP_PUBLIC_KEY)
    }, function(response, errorInfo)
        if (response == nil) then
            return
        end
        print("response encrypt before",response.encryptData)

        print("error info ",errorInfo)
        responseData = Native:aesDecrypt(response.encryptData, OS_HTTP_PUBLIC_KEY, OS_HTTP_PUBLIC_KEY)
        print("LuaResponse:"  .. type(responseData))
        response = toTable(responseData)
        print("responseresponse:"  .. type(response))
        if (response.resCode ~= "00") then
            return
        end
        desktopWindow.recentList = response.recentUseMiniApps
        desktopWindow.recommendList = response.recommendUseMiniApps
        if callback ~= nil then
            callback()
        end
    end)
end

local function getAdDesktopInfo(callback)
    local ad_pos_id = ""
    if (System.android()) then
        ad_pos_id = "10002500"
    else
        ad_pos_id = "10002502"
    end

    local paramData = {
        videoId = Native:nativeVideoID(),
        adsType = 6,
        adsPosId = ad_pos_id,
        commonParam = Native:commonParam()
    }
    local paramDataString = Native:tableToJson(paramData)
    local OS_HTTP_GET_COUPON_RED_PACKET = Native:videoOShost() .. "/api/videoRelationAds"
    local OS_HTTP_PUBLIC_KEY = Native:appSecret()
    local requestId = desktopWindow.request:post(OS_HTTP_GET_COUPON_RED_PACKET, {
        data = Native:aesEncrypt(paramDataString, OS_HTTP_PUBLIC_KEY, OS_HTTP_PUBLIC_KEY)
    }, function(response, errorInfo)
        if (response == nil) then
            return
        end
        responseData = Native:aesDecrypt(response.encryptData, OS_HTTP_PUBLIC_KEY, OS_HTTP_PUBLIC_KEY)
--        print("AdDesktopInfo LuaResponse:" .. responseData)
        response = toTable(responseData)
        if (response.resCode ~= "00") then
            return
        end

        if (response.launchInfo == nil or response.launchInfo.data == nil or response.launchInfo.launchPlanId == nil) then
            return
        end

        local adInfoList = response.launchInfo.data.adsList

        if (adInfoList == nil or table_leng(adInfoList) <= 0) then
            return
        end

        desktopWindow.exposureTrack = false
        desktopWindow.launchPlanId = response.launchInfo.launchPlanId
        desktopWindow.desktopAdInfo = adInfoList[1]

        if callback ~= nil then
            callback()
        end
    end)
end

function show(args)
    if (desktopWindow.desktopWindowView ~= nil) then
        return
    end
    if(Native:isPortraitScreen()) then
        return
    end

    desktopWindow.miniAppInfo = args;
    local width, height = Applet:appletSize()
    desktopWindow.landscapeWidth = width
    desktopWindow.landscapeHeight = height

    desktopWindow.request = HttpRequest()
    desktopWindow.desktopWindowView = createDesktopWindowView()

    -- 获取最近使用
    getRecentRecommendDesktopInfo(function()
        onCreate()
        getAdDesktopInfo(function()
            if(desktopWindow.desktopScrollview ~= nil) then
                desktopWindow.desktopScrollview:reload()
            end
        end)
    end)
end