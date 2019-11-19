object = {}
function object:new(o)
    o = o or {}
    setmetatable(o, self)
    self.__index = self
    return o
end
helloworld = object:new()


local function setLuaViewSize(luaView, isPortrait)
    --设置当前容器大小
    if (luaView == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        local videoWidth, videoHight, y = Native:getVideoSize(0)
        if System.android() then
            y = 0.0
        end
        luaView:frame(0, y, math.min(screenWidth, screenHeight), videoHight)
    else
        luaView:frame(0, 0, math.max(screenWidth, screenHeight), math.min(screenWidth, screenHeight))
    end
end
--全局父控件
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

local function createTitle()
    local title = Label()
    title:textColor(0xFF0000)
    title:textSize(16)
    title:frame(0, 0, 100, 30)
    title:align(Align.V_CENTER)
    title:align(Align.H_CENTER)
    return title
end

local function rotationScreen(isPortrait)
    setLuaViewSize(helloworld.luaView, isPortrait)
    helloworld.titleLabel:align(Align.V_CENTER)
    helloworld.titleLabel:align(Align.H_CENTER)
end

local function registerMedia()
    local media = Media()
    -- body
    -- 注册window callback通知
    local callbackTable = {
        --0: 竖屏小屏幕，1 竖屏全屏，2 横屏全屏
        onPlayerSize = function(type)
            print("屏幕旋转通知")
            if (type == 0) then
                rotationScreen(true)
            elseif (type == 1) then
                rotationScreen(true)
            elseif (type == 2) then
                rotationScreen(false)
            end
        end,
        onMediaPause = function()
            helloworld.luaView:hide()
        end,
        onMediaPlay = function()
            helloworld.luaView:show()
        end
    }
    media:mediaCallback(callbackTable)
    return media
end

-- 小程序的入口方法
function show(args)
    if (helloworld.luaView ~= nil) then
        return
    end

    local paramDataString = Native:tableToJson(args)
    print("paramDataString:" .. paramDataString)

--    注册屏幕旋转监听
    helloworld.media = registerMedia()

    helloworld.data = args.data
    local isPortrait = Native:isPortraitScreen()
    helloworld.luaView = createLuaView(isPortrait)
    helloworld.titleLabel = createTitle()
    helloworld.titleLabel:text(helloworld.data.creativeName)
    helloworld.luaView:addView(helloworld.titleLabel)
end
