require "os_config"
require "os_string"
require "os_constant"
require "os_util"
require "os_track"
local adTypeName = "webWindow"
local scale = getScale()

webWindow = object:new()


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



local function setLuaViewSize(luaview, isPortrait) --设置当前容器大小
    if (luaview == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    print("os_web_window",screenWidth,screenHeight)
    if (isPortrait) then
        luaview:frame(0, 0, math.min(screenWidth, screenHeight), math.max(screenWidth, screenHeight))
    else
        luaview:frame(0, 0, math.max(screenWidth, screenHeight), math.min(screenWidth, screenHeight))
        if (System.android()) then
            luaview:align(Align.RIGHT)
        end
    end
end




--全局父控件
local function createLuaView(isPortrait)
    local luaView = View()
    setLuaViewSize(luaView, isPortrait)
    return luaView
end



local function setWebViewSize(webView,isPortrait)
    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        if (System.android()) then
            webView:frame(0, 0, webWindow.portraitWidth, webWindow.portraitHeight)
            webView:align(Align.BOTTOM)
        else
            webView:frame(0, math.max(screenWidth, screenHeight) - webWindow.portraitHeight, webWindow.portraitWidth, webWindow.portraitHeight)
        end

    else
        if (System.android()) then
            webView:frame(0, 0, 200 * scale, math.min(screenWidth, screenHeight))
            webView:align(Align.RIGHT)
        else
            webView:frame(math.max(screenWidth, screenHeight) - 200 * scale, 0, 200 * scale, math.min(screenWidth, screenHeight))
        end
    end
end

local function createWebView(isPortrait)
    local webView = WebView()
    setWebViewSize(webView,isPortrait)
    return webView
end


--屏幕旋转--
local function rotationScreen(isPortrait)
    setLuaViewSize(webWindow.luaView, isPortrait)
    setWebViewSize(webWindow.webView,isPortrait)
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
                -- 切到横屏就close
                Native:destroyView()
            end
        end
    }
    media:mediaCallback(callbackTable)
    return media
end

local function setConfig()
    local screenWidth, screenHeight = Native:getVideoSize(2)
    local videoWidth, videoHeight, marginTop = Native:getVideoSize(0)
    webWindow.portraitWidth = math.min(screenWidth, screenHeight) --宽
    webWindow.portraitHeight = math.max(screenWidth, screenHeight) - videoHeight - marginTop --高
end



local function closeView()
    Native:destroyView()
end

local function onCreate(linkUrl)


    local isPortrait = Native:isPortraitScreen()
    webWindow.media = registerMedia()
    webWindow.luaView = createLuaView(isPortrait)

    webWindow.webWindow = createWebView(isPortrait)
    webWindow.luaView:addView(webWindow.webWindow)
    if(linkUrl ~= nil) then
        webWindow.webWindow:loadUrl(linkUrl)
    end


    webWindow.luaView:onClick(function()
        local isPortrait = Native:isPortraitScreen()
        if (isPortrait) then
            startViewTranslationAnim(webWindow.webWindow, 0, webWindow.portraitHeight, {
                onCancel = function()
                    closeView()
                end,
                onEnd = function()
                    closeView()
                end,
                onPause = function()
                    closeView()
                end
            })
        else
            startViewTranslationAnim(webWindow.webWindow, 200 * scale, 0, {
                onCancel = function()
                    closeView()
                end,
                onEnd = function()
                    closeView()
                end,
                onPause = function()
                    closeView()
                end
            })
        end
    end)

end

function show(args)
    if (args == nil or args.data == nil or args.data.data == nil) then
        return
    end
    setConfig(args.data)
    onCreate(args.data.data.url)
end