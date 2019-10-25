require "os_config"
require "os_util"
require "os_string"
require "os_constant"
require "os_track"

abdrama = object:new()
abdrama.views = {}

local adTypeName = "abdrama"

local scale = getScale()

local OS_AB_A = "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"
local OS_AB_B = "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"

--widgetEvent版本兼容
local function widgetEvent(eventType, adID, adName, actionType, linkUrl, deepLink, selfLink)

    local actionString = ""
    if (linkUrl ~= nil and string.len(linkUrl) > 0) then
        actionString = linkUrl
    elseif (deepLink ~= nil and string.len(deepLink) > 0) then
        actionString = deepLink
    elseif (selfLink ~= nil and string.len(selfLink) > 0) then
        actionString = selfLink
    end

    if Native.widgetNotify then

        local notifyTable = {}

        notifyTable["eventType"] = eventType
        notifyTable["adID"] = adID
        notifyTable["adName"] = adName
        notifyTable["actionType"] = actionType
        notifyTable["actionString"] = actionString

        if (linkUrl ~= nil) then
            notifyTable["linkUrl"] = linkUrl
        end

        if (deepLink ~= nil) then
            notifyTable["deepLink"] = deepLink
        end

        if (selfLink ~= nil) then
            notifyTable["selfLink"] = selfLink
        end

        Native:widgetNotify(notifyTable)
    else
        Native:widgetEvent(eventType, adID, adName, actionType, actionString)
    end
end

local function registerWindow()
    local nativeWindow = nil
    if System.ios() then
        nativeWindow = NativeWindow()
    else
        nativeWindow = nativeWindow
    end
    local callbackTable = {
        onShow = function()
        end,
        onHide = function()
            if (System.ios()) then
                closeView()
            end
        end,
        onHome = function()
            closeView()
        end
    }
    if (nativeWindow == nil and System.android()) then
        nativeWindow = window
    end
    if (nativeWindow == nil) then
        return
    end
    nativeWindow:callback(callbackTable)
    return nativeWindow
end

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

local function closeView()

    if Native:getCacheData(abdrama.id) == tostring(eventTypeShow) then
        widgetEvent(eventTypeClose, abdrama.id, adTypeName, actionTypeNone, "")
        Native:deleteBatchCacheData({ abdrama.id })
    end
    Native:destroyView()
end

local function onClickDramaLink(linkData)
    if(linkData == nil) then
        return
    end
    -- body
    widgetEvent(eventTypeClick, abdrama.id, adTypeName, actionTypeOpenUrl, linkData.linkUrl,linkData.deepLink,linkData.selfLink)

    abdrama.countDownTimer:cancel()
    abdrama.countDownTimer = nil

    local clickLinkUrl = getHotspotClickTrackLink(abdrama.data, 1)
    if (clickLinkUrl ~= nil) then
        Native:get(clickLinkUrl)
    end
    if (abdrama.launchPlanId ~= nil) then
        osTrack(abdrama.launchPlanId, 3, 2)
    end

    closeView()

end

local function updateViewSize(data, isPortrait)
    -- body

    local screenWidth, screenHeight = System.screenSize()

    if isPortrait then

        local videoW, videoH = Native:getVideoSize(0)
        abdrama.luaview:frame(0, 0, videoW, videoH)
        abdrama.dramaAView:frame(0, videoH - 44 * scale, screenWidth / 2, 30 * scale)
        abdrama.dramaBView:frame(videoW / 2, videoH - 44 * scale, videoW / 2, 30 * scale)

        local labelAW = Native:stringDrawLength(data.data.hotEdit.aDescribe, 12)
        if System.android() then
            labelAW = labelAW
        else
            labelAW = labelAW * 1.1
        end
        abdrama.dramaOptionALabel:frame(30 * scale + 8 * scale, 3 * scale, labelAW, 24 * scale)
        abdrama.dramaOptionALabel:fontSize(12)
        abdrama.dramaOptionABackView:frame(30 * scale / 2, 3 * scale, labelAW + 16 * scale + 30 * scale / 2, 24 * scale)
        abdrama.dramaOptionABackView:cornerRadius(12 * scale)
        abdrama.dramaOptionAImageView:frame(0, 0, 30 * scale, 30 * scale)
        abdrama.dramaOptionAView:frame(0, 0, abdrama.dramaOptionABackView:right(), 30 * scale)

        local labelBW = Native:stringDrawLength(data.data.hotEdit.bDescribe, 12)
        -- 字体不同,可能会更粗一些
        if System.android() then
            labelAW = labelAW
        else
            labelBW = labelBW * 1.1
        end
        abdrama.dramaOptionBLabel:frame(30 * scale + 8 * scale, 3 * scale, labelBW, 24 * scale)
        abdrama.dramaOptionBLabel:fontSize(12)
        abdrama.dramaOptionBBackView:frame(30 * scale / 2, 3 * scale, labelBW + 16 * scale + 30 * scale / 2, 24 * scale)
        abdrama.dramaOptionBBackView:cornerRadius(12 * scale)
        abdrama.dramaOptionBImageView:frame(0, 0, 30 * scale, 30 * scale)
        abdrama.dramaOptionBView:frame(0, 0, abdrama.dramaOptionBBackView:right(), 30 * scale)

        abdrama.dramaOptionAView:alignCenter()
        abdrama.dramaOptionBView:alignCenter()

    else

        abdrama.luaview:frame(0, 0, screenWidth, screenHeight)
        abdrama.dramaAView:frame(0, screenHeight - 80 * scale, screenWidth / 2, 40 * scale)
        abdrama.dramaBView:frame(screenWidth / 2, screenHeight - 80 * scale, screenWidth / 2, 40 * scale)

        local labelAW = Native:stringDrawLength(data.data.hotEdit.aDescribe, 14)
        -- 字体不同,可能会更粗一些
        if System.android() then
            labelAW = labelAW
        else
            labelAW = labelAW * 1.1
        end
        abdrama.dramaOptionALabel:frame(40 * scale + 16 * scale, 5 * scale, labelAW, 30 * scale)
        abdrama.dramaOptionALabel:fontSize(14)
        abdrama.dramaOptionABackView:frame(40 * scale / 2, 5 * scale, labelAW + 40 * scale + 40 * scale / 2, 30 * scale)
        abdrama.dramaOptionABackView:cornerRadius(15 * scale)
        abdrama.dramaOptionAImageView:frame(0, 0, 40 * scale, 40 * scale)
        abdrama.dramaOptionAView:frame(0, 0, abdrama.dramaOptionABackView:right(), 40 * scale)

        local labelBW = Native:stringDrawLength(data.data.hotEdit.bDescribe, 14)
        -- 字体不同,可能会更粗一些
        if System.android() then
            labelAW = labelAW
        else
            labelBW = labelBW * 1.1
        end
        abdrama.dramaOptionBLabel:frame(40 * scale + 16 * scale, 5 * scale, labelBW, 30 * scale)
        abdrama.dramaOptionBLabel:fontSize(14)
        abdrama.dramaOptionBBackView:frame(40 * scale / 2, 5 * scale, labelBW + 40 * scale + 40 * scale / 2, 30 * scale)
        abdrama.dramaOptionBBackView:cornerRadius(15 * scale)
        abdrama.dramaOptionBImageView:frame(0, 0, 40 * scale, 40 * scale)
        abdrama.dramaOptionBView:frame(0, 0, abdrama.dramaOptionBBackView:right(), 40 * scale)

        abdrama.dramaOptionAView:alignCenter()
        abdrama.dramaOptionBView:alignCenter()

    end

end


local function createParent()
    local screenWidth, screenHeight = System.screenSize()
    local luaView
    if System.android() then
        luaView = View()
    else
        luaView = ThroughView()
    end
    
    luaView:frame(0, 0, screenWidth, screenHeight)
    return luaView
end

local function createDramaView(data, isPortrait)
    local screenWidth, screenHeight = System.screenSize()
    local dramaAView, dramaBView
    if System.android() then
        dramaAView = View()
        dramaBView = View()
    else
        dramaAView = ThroughView()
        dramaBView = ThroughView()
    end

    dramaAView:frame(0, 293 * scale, screenWidth / 2, 40 * scale)
    dramaBView:frame(screenWidth / 2, 293 * scale, screenWidth / 2, 40 * scale)

    abdrama.luaview:addView(dramaAView)
    abdrama.luaview:addView(dramaBView)

    return dramaAView, dramaBView
end

local function createDramaOptionView(text, image, isPortrait)
    local dramaOptionView = View()

    local dramaOptionImageView = Image(Native)
    dramaOptionImageView:frame(0, 0, 40 * scale, 40 * scale)
    dramaOptionImageView:image(image)
    dramaOptionImageView:scaleType(2)

    local dramaOptionBackView = View()
    dramaOptionBackView:backgroundColor(0x000000, 0.5)

    local dramaOptionLabel = Label()
    dramaOptionLabel:textColor(0xFFFFFF)
    dramaOptionLabel:fontSize(14)
    dramaOptionLabel:textAlign(TextAlign.LEFT)
    dramaOptionLabel:text(text)

    local labelW = Native:stringDrawLength(text, 14)

    labelW = labelW * 1.1
    dramaOptionLabel:frame(40 * scale + 16 * scale, (30 * scale - 14) / 2 + 5 * scale, labelW, 14)

    dramaOptionBackView:frame(40 * scale / 2, 5 * scale, labelW + 40 * scale + 40 * scale / 2, 30 * scale)
    dramaOptionBackView:cornerRadius(15 * scale)

    dramaOptionView:frame(0, 0, dramaOptionBackView:right(), 40 * scale)

    dramaOptionView:addView(dramaOptionBackView)
    dramaOptionView:addView(dramaOptionLabel)
    dramaOptionView:addView(dramaOptionImageView)

    return dramaOptionView, dramaOptionImageView, dramaOptionLabel, dramaOptionBackView
end

local function getALinkData(data)
    if(data == nil) then
        return nil
    end
    local dataTable = data.data;
    if(dataTable == nil) then
        return nil
    end
    local hotEdit = dataTable.hotEdit
    if (hotEdit == nil) then
        return nil
    end
    return hotEdit.aLinkData
end

local function getBLinkData(data)
    if(data == nil) then
        return nil
    end
    local dataTable = data.data;
    if(dataTable == nil) then
        return nil
    end
    local hotEdit = dataTable.hotEdit
    if (hotEdit == nil) then
        return nil
    end
    return hotEdit.bLinkData
end

local function createDramaOptionsView(data, isPortrait)
    -- body

    local dramaOptionAView, dramaOptionAImageView, dramaOptionALabel, dramaOptionABackView = createDramaOptionView(data.data.hotEdit.aDescribe, Data(OS_AB_A), isPortrait)
    local dramaOptionBView, dramaOptionBImageView, dramaOptionBLabel, dramaOptionBBackView = createDramaOptionView(data.data.hotEdit.bDescribe, Data(OS_AB_B), isPortrait)

    abdrama.dramaAView:addView(dramaOptionAView)
    dramaOptionAView:alignCenter()

    abdrama.dramaBView:addView(dramaOptionBView)
    dramaOptionBView:alignCenter()

    dramaOptionAView:onClick(function()
        -- body
        local aLinkData = getALinkData(data)
        onClickDramaLink(aLinkData)
    end)

    dramaOptionBView:onClick(function()
        -- body
        local bLinkData = getBLinkData(data)
        onClickDramaLink(bLinkData)
    end)

    return dramaOptionAView, dramaOptionAImageView, dramaOptionALabel, dramaOptionABackView, dramaOptionBView, dramaOptionBImageView, dramaOptionBLabel, dramaOptionBBackView

end


local function linkUrl(data) --获取linkUrl
    if (data == nil) then
        return nil
    end
    local link = data.link
    if (link ~= nil and string.match(tostring(link), "http") == "http") then
        return link
    else
        return nil
    end
end

local function onCreate(data)

    -- track show
    local showLinkUrl = getHotspotExposureTrackLink(data, 1)
    if (showLinkUrl ~= nil) then
        Native:get(showLinkUrl)
    end

    if (abdrama.launchPlanId ~= nil) then
        osTrack(abdrama.launchPlanId, 1, 2)
        osTrack(abdrama.launchPlanId, 2, 2)
    end

    abdrama.luaview = createParent()

    local isPortrait = Native:isPortraitScreen()
    abdrama.dramaAView, abdrama.dramaBView = createDramaView(data, isPortrait)
    abdrama.dramaOptionAView, abdrama.dramaOptionAImageView, abdrama.dramaOptionALabel, abdrama.dramaOptionABackView, abdrama.dramaOptionBView, abdrama.dramaOptionBImageView, abdrama.dramaOptionBLabel, abdrama.dramaOptionBBackView = createDramaOptionsView(data, isPortrait)

    updateViewSize(data, isPortrait)

end

local function setDramaTime()
    
    local countDownTimer = Timer()
    if System.android() then
        countDownTimer:delay(10)
    else
        countDownTimer:interval(10)
    end
    --countDownTimer:delay(1)
    countDownTimer:repeatCount(0)
    countDownTimer:callback(function()
        -- body
        local aLinkData = getALinkData(abdrama.data)
        onClickDramaLink(aLinkData)
    end)

    abdrama.countDownTimer = countDownTimer
    countDownTimer:start()

end

local function registerMedia()
    local media = Media()
    -- body
    -- 注册window callback通知
    local callbackTable = {
        --0: 竖屏小屏幕，1 竖屏全凭，2 横屏全屏
        onPlayerSize = function(type)
            local isPortrait = false
            if type == 0 or type == 1 then
                isPortrait = true
            end
            updateViewSize(abdrama.data, isPortrait)
        end,
        onMediaPause = function()
            
        end,
        onMediaPlay = function()
        
        end
    }
    media:mediaCallback(callbackTable)
    return media
end

function show(args)
    --第二次调用show方法时，直接return
    if (args == nil or abdrama.luaview ~= nil) then
        return
    end

    local dataTable = args.data
    if (dataTable == nil) then
        return
    end
    abdrama.id = dataTable.id
    abdrama.launchPlanId = dataTable.launchPlanId

    registerMedia()

    widgetEvent(eventTypeShow, abdrama.id, adTypeName, actionTypePauseVideo, "")
    Native:saveCacheData(abdrama.id, tostring(eventTypeShow))
    abdrama.data = dataTable
    -- abdrama.window = registerWindow()

    onCreate(dataTable)
    
    setDramaTime()

    checkMqttHotspotToSetClose(dataTable, function()
        closeView()
    end)

end