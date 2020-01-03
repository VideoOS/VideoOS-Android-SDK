--
-- Created by IntelliJ IDEA.
-- User: videojj_pls
-- Date: 2018/10/25
-- Time: 10:11 AM
-- To change this template use File | Settings | File Templates.
--
require "os_config"
require "os_string"
require "os_constant"
require "os_util"
require "os_track"
eShop = object:new()
local adTypeName = "eShop"
local scale = getScale()
local OS_ICON_HOT_CLOSE = "iVBORw0KGgoAAAANSUhEUgAAACgAAAAoCAYAAACM/rhtAAAAAXNSR0IArs4c6QAAA3VJREFUWAnNmV1LFFEYx9fNytKgfIvoZsGbsAu3IMMuUvJCosvwY9SNd/sdvOmL1I1diIbrhUFC2I2EIChLKJpS+JKJWf/fOGc5szuzntmdZn3gv3PmnOd5zo8zc54Zx5ZM/dap0HvSHemGJTUze5Y21P4q7UqxrSVmRIf8ByXAemPGbskf0E/SvmusK+BVJRySnkhXXJNH+B2rf0H6KP2O8Cl3uwD2y/uF1F6OSqZxoDRT0nKtdJdqDAI/IgHX6KopRZWR877fu1416ndEAV7W+EvpUVRggv055eJ+XpFOpYCFAbJywHFp07IeTdQtVV3uMMAROaaxcpomYEBia96v/1MJaDaE7ZNmO6fJtqRtM2nWNHSklLAhmm0wwOKZDUida/f7m3mAARbPDCBPCIrwRTFYYMoYwEG1nWpdW1tby9jYWHdrayvxToYvMcQ6BZyxwJQxm+S52k6Xd3R0tLtQKAwNDAx0zMzMbJyeVpWuAANwk5OTD8fHx/tLpdLO6urqYcAh+uSahhZZQd5KeqP9giOzs7Pfl5aWvuXz+btMXGslDRy+xBAbzFbzDKZOVvCB1FfT1RpkxVg5VpCJo1ayEm5iYuLzycmJlcmp+RNArvVtJ3ff6TzIhOCY7RDAx9JNzuJYFGQ2m/XuOXNZ61w5g/KLXfVK6jI9cY+Vq0V8QnCk2gGwIDmVGCLCzIZknA3R4MqZaY5NHTQdF+7IPZiXrtdLZq8eK7e5ublXa3fHnOcHgLzBxN4kTFQJx2Wdnp4+twQR62hbAPZJscoMycPgqHNRu5v+OqwE4C0JSGeLgjMJEoT8AuCBRC10svPgTJKEIKfYxfzFz1usk/Gy4FrnuOTcl+bZTazTJGdOMO2a159nOnnqEswr0/DwcBcPftdnK6sOXLFY3Dk6OvrrMo985qUPBpCXw9dSQwVb8UnZsRK9kfZNoeZbyUJS2RPIA4v3/cYAkpNvJWyYZhsMsHhmA/IhZ8rvb+YBhvJHJcqMbdv+Sc7uTLE9p7kW7fkqARlbl3jd7uEkRVvWXO8r5wsDxGdFomalBQncW+mPFLAoQB6cBGE57/f//cwpNStXBceUUYCMYWsSFT0nJV0jD5TznRS453QeMFOoA50hJ3wr4XMEf/E3CkoRps5RSsq7Ve1QcwU0wTxxBqUL9xHdANrHVP4N8Q/dhHm1tzJCegAAAABJRU5ErkJggg=="



local function getHotspotExposureTrackLink(data,index)
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

local function getHotspotClickTrackLink(data,index)
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
    if Native:getCacheData(eShop.id) == tostring(eventTypeShow) then
        Native:widgetEvent(eventTypeClose, eShop.id, adTypeName, actionTypeNone, "")
        Native:deleteBatchCacheData({ eShop.id })
    end
    Native:destroyView()
end

local function translationAnim(x, y)
    local anim = Animation():translation(x, y):duration(0.3)
    return anim
end

local function startViewTranslationAnim(view, x, y, table)
    if (view == nil) then
        return
    end
    if table ~= nil then
        translationAnim(x, y):with(view):callback(table):start()
    else
        translationAnim(x, y):with(view):start()
    end
end

local function setLogoSize(view, isPortrait)
    if (isPortrait) then
        view:frame(0, 0, 40 * scale, 40 * scale)
        view:cornerRadius(20 * scale)
    else
        view:frame(0, 0, 50 * scale, 50 * scale)
        view:cornerRadius(25 * scale)
    end
end

local function createTrolleyLogo(imageUrl, isPortrait)
    local logo = Image(Native)
    logo:image(imageUrl)
    if System.android() then
        logo:scaleType(ScaleType.CENTER_CORP)
    end
    logo:align(Align.V_CENTER)
    setLogoSize(logo, isPortrait)
    return logo
end

local function setCloseButtonSize(view, isPortrait)
    if isPortrait then
        view:xy(eShop.contentView:right() - 30 * scale, eShop.contentView:top() + (4 - 44) * scale)
    else
        view:xy(eShop.contentView:right() - 30 * scale, eShop.contentView:top() + (5 - 44) * scale)
    end
end

local function createCloseButton(marginBottom)
    local closeView = View()
    closeView:size(44 * scale, 44 * scale)
    local closeImage = Image(Native)
    closeImage:size(20 * scale, 20 * scale)
    closeImage:align(Align.CENTER)
    closeImage:image(Data(OS_ICON_HOT_CLOSE))
    closeView:addView(closeImage)

    setCloseButtonSize(closeView, isPortrait)

    return closeView, closeImage
end

local function setAdsButtonSize(adsLabel, isPortrait)
    adsLabel:frame(eShop.contentView:left(), eShop.contentView:bottom() + 8 * scale, 34 * scale, 14 * scale)
end

local function createCloudAdsButton(isPortrait)
    --创建底部'广告'标识
    local adsLabel = Label()
    adsLabel:size(34 * scale, 14 * scale)
    adsLabel:textSize(10)
    adsLabel:textAlign(TextAlign.CENTER)
    adsLabel:textColor(0xFFFFFF)
    adsLabel:backgroundColor(0x000000, 0.3)
    adsLabel:text("广告")
    setAdsButtonSize(adsLabel, isPortrait)

    return adsLabel
end

local function setGoodsTitleSize(view, label, isPortrait)
    local text = label:text()
    local textWidth
    local corner
    if (isPortrait) then
        label:textSize(12)
        textWidth = Native:stringDrawLength(text, 14)
        label:frame(30 * scale, 0, textWidth, 32 * scale)
        view:frame(20 * scale, 4 * scale, textWidth + 45 * scale, 32 * scale)
        corner = 16 * scale
        view:corner(0, 0, corner, corner, corner, corner, 0, 0)
    else
        label:textSize(16)
        textWidth = Native:stringDrawLength(text, 19)
        label:frame(35 * scale, 0, textWidth, 40 * scale)
        view:frame(25 * scale, 5 * scale, textWidth + 60 * scale, 40 * scale)
        corner = 20 * scale
        view:corner(0, 0, corner, corner, corner, corner, 0, 0)
    end
end

local function createGoodsTitle(goodsTitle, isPortrait)
    local titleBg = GradientView()
    titleBg:backgroundColor(0x000000, 0.5)

    local title = Label()
    title:textColor(0xFFFFFF)
    title:textSize(16)
    if goodsTitle ~= nil then
        title:text(goodsTitle)
    end
    title:textAlign(TextAlign.LEFT)
    --title:backgroundColor(0x3987F2,0.5)
    if System.android() then
        title:gravity(Gravity.V_CENTER)
    end

    titleBg:align(Align.V_CENTER)
    titleBg:addView(title)

    if (goodsTitle == nil or goodsTitle == "") then
        titleBg:hide()
    end

    setGoodsTitleSize(titleBg, title, isPortrait)
    return titleBg, title
end

local function setContentViewSize(view, isPortrait)

    if isPortrait then
        view:frame(25 * scale, 128 * scale, eShop.titleBg:right(), 40 * scale)
    else
        view:frame(28 * scale, 260 * scale, eShop.titleBg:right(), 50 * scale)
    end

end

local function createContentView(isPortrait)
    local contentView = View()
    setContentViewSize(contentView, isPortrait)
    return contentView
end

local function setLuaViewSize(luaView, isPortrait)
    --设置当前容器大小
    if (luaView == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        local videoWidth, videoHeight, y = Native:getVideoSize(0)
        luaView:frame(0, y, math.min(screenWidth, screenHeight), videoHeight)
    else
        luaView:frame(0, 0, math.max(screenWidth, screenHeight), math.min(screenWidth, screenHeight))
    end
end

local function createLuaView(isPortrait)
    local luaView
    if System.android() then
        luaView = View()
    else
        luaView = ThroughView()
    end
    setLuaViewSize(luaView, isPortrait)
    return luaView
end

--屏幕旋转--
local function rotationScreen(isPortrait)
    setLuaViewSize(eShop.luaView, isPortrait)

    setLogoSize(eShop.logo, isPortrait)
    setGoodsTitleSize(eShop.titleBg, eShop.title, isPortrait)
    setContentViewSize(eShop.contentView, isPortrait)
    setAdsButtonSize(eShop.adsLabel, isPortrait)

    setCloseButtonSize(eShop.closeView, isPortrait)
end

local function registerMedia()
    local media = Media()
    -- body
    -- 注册window callback通知
    local callbackTable = {
        --0: 竖屏小屏幕，1 竖屏全屏，2 横屏全屏
        onPlayerSize = function(type)
            if (type == 0) then
                rotationScreen(true)
            elseif (type == 1) then
                rotationScreen(true)
            elseif (type == 2) then
                rotationScreen(false)
            end
        end,
        onMediaPause = function()
            eShop.luaView:hide()
        end,
        onMediaPlay = function()
            eShop.luaView:show()
        end
    }
    media:mediaCallback(callbackTable)
    return media
end

local function onCreate(data)
    -- 第三方数据监控
    local exposureTrackUrl = getHotspotExposureTrackLink(data,1)
    if(exposureTrackUrl ~= nil) then
        Native:get(exposureTrackUrl)
    end


    if (eShop.launchPlanId ~= nil) then
        -- 热点及点击位曝光
        osTrack(eShop.launchPlanId, 1, 2)
        osTrack(eShop.launchPlanId, 2, 2)
    end

    local isPortrait = Native:isPortraitScreen()

    eShop.luaView = createLuaView(isPortrait)

    eShop.logo = createTrolleyLogo(data.data.hotEditInfor.hotImage, isPortrait)
    eShop.titleBg, eShop.title = createGoodsTitle(data.data.hotEditInfor.hotTitle, isPortrait)
    eShop.contentView = createContentView(isPortrait)

    eShop.adsLabel = createCloudAdsButton(isPortrait)

    if (eShop.isShowAds) then
        eShop.adsLabel:show()
    else
        eShop.adsLabel:hide()
    end



    eShop.closeView, eShop.closeImage = createCloseButton(isPortrait)
    if(eShop.isShowClose) then
        eShop.closeView:show()
    else
        eShop.closeView:hide()
    end

    eShop.contentView:addView(eShop.titleBg)
    eShop.contentView:addView(eShop.logo)
    eShop.luaView:addView(eShop.contentView)
    eShop.luaView:addView(eShop.adsLabel)
    eShop.luaView:addView(eShop.closeView)

    eShop.contentView:onClick(function()


        local clickTrackLink = getHotspotClickTrackLink(data,1)
        if(clickTrackLink ~= nil) then
            Native:get(clickTrackLink)
        end


        Native:widgetEvent(eventTypeClick, eShop.id, adTypeName, actionTypeNone, "")
        if (eShop.launchPlanId ~= nil) then
            -- 热点点击
            osTrack(eShop.launchPlanId, 3, 2)
        end

        local miniAppId = data.miniAppInfo.miniAppId;
        local sendString = "LuaView://defaultLuaView?template=" .. "os_easy_shop_window.lua" .. "&id=" .. "os_easy_shop_window1" .. tostring(eShop.id) .. "&priority=" .. tostring(osInfoViewPriority)
        if (miniAppId ~= nil) then
            sendString = sendString .. "&miniAppId=" .. miniAppId
        end
        Native:sendAction(Native:base64Encode(sendString), data)
        closeView()
    end)

    eShop.closeView:onClick(function()
        closeView()
    end)

    -- entrance animation
    eShop.titleBg:translation(-20 * scale, 0)
    startViewTranslationAnim(eShop.titleBg, 0, 0)

    registerMedia()

    Native:widgetEvent(eventTypeShow, eShop.id, adTypeName, actionTypeNone, "")
end

local function configSize(data)
    if (data == nil) then
        return
    end
    local dataTable = data.data.data
    if (dataTable == nil) then
        return
    end
    local isShowClose = dataTable.hotEditInfor.isShowClose
    if (isShowClose ~= nil) then
        eShop.isShowClose = isShowClose
    else
        eShop.isShowClose = false
    end
    local isShowAds = dataTable.hotEditInfor.isShowAds
    if (isShowAds ~= nil) then
        eShop.isShowAds = isShowAds
    else
        eShop.isShowAds = false
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    local videoWidth, videoHeight, y = Native:getVideoSize(0)
    eShop.portraitWidth = math.min(screenWidth, screenHeight)
    eShop.portraitHeight = videoHeight
    eShop.landscapeWidth = math.max(screenWidth, screenHeight)
    eShop.landscapeHeight = math.min(screenWidth, screenHeight)
end

function show(args)
    if (args == nil or args.data == nil or eShop.luaView ~= nil) then
        return
    end
    eShop.data = args.data
    eShop.launchPlanId = eShop.data.launchPlanId
    eShop.id = eShop.data.id
    configSize(args)
    onCreate(args.data)

    checkMqttHotspotToSetClose(eShop.data, function()
        closeView()
    end)

end