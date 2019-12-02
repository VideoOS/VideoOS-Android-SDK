-- 商品
require "os_config"
require "os_string"
require "os_constant"
require "os_util"
require "os_track"
shopping = object:new()
local adTypeName = "shopping"
local scale = getScale()

local function updateLuaViewSize(luaView, isPortrait)
    if (luaView == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        luaView:frame(0, y, math.min(screenWidth, screenHeight), math.max(screenWidth, screenHeight))
    else
        luaView:frame(0, 0, math.max(screenWidth, screenHeight), math.min(screenWidth, screenHeight))
    end
end

local function updateWebViewSize(webView, isPortrait)
    if (webView == nil) then
        return
    end
    local w, h = shopping.luaView:size()
    if (isPortrait) then
        local _, videoHeight, y = Native:getVideoSize(0)
            if System.android() then
                y = 0.0
        end
        webView:frame(0, y + videoHeight, w, h - y - videoHeight)
    else
        local webViewWidth = h / 375 * 220
        webView:frame(w - webViewWidth, 0, webViewWidth, h)
    end
end

local function createParent(isPortrait)
    local luaView
    if System.android() then
        luaView = View()
    else
        luaView = ThroughView()
    end
    updateLuaViewSize(luaView, isPortrait)
    return luaView
end

local function createWebView(isPortrait)
    local webView = WebView()
    webView:setInitData(shopping.data)
    webView:webViewCallback({
        onClose = function()
            Native:destroyView()
        end
    })
    shopping.luaView:addView(webView)

    updateWebViewSize(webView, isPortrait)
    return webView
end

local function onScreenChange(isPortrait)
    -- body
    updateLuaViewSize(shopping.luaView, isPortrait)
    updateWebViewSize(shopping.webView, isPortrait)
end

local function registerMedia()
    local media = Media()
    -- body
    -- 注册window callback通知
    local callbackTable = {
        --0: 竖屏小屏幕，1 竖屏全凭，2 横屏全屏
        onPlayerSize = function(type)
            if (type == 0) then
                onScreenChange(true)
            elseif (type == 1) then
                onScreenChange(true)
            elseif (type == 2) then
                onScreenChange(false)
            end
        end
    }
    media:mediaCallback(callbackTable)
    return media
end

local function onCreate()
    -- body
    local isPortrait = Native:isPortraitScreen()
    shopping.luaView = createParent(isPortrait)
    shopping.webView = createWebView(isPortrait)
    shopping.media = registerMedia()

end

--入口Native调用--
function show(args)
    local dataTable = args.data
    if (dataTable == nil) then
        return
    end

    shopping.data = dataTable

    onCreate()
    shopping.webView:loadUrl(shopping.data.data.url)

end
