require "os_config"
require "os_string"
require "os_constant"
require "os_util"
test = object:new()
local scale = getScale()


function show(args)

    local width, height = Applet:appletSize()
    local luaview = View()
    luaview:backgroundColor(0x121212)
    luaview:frame(0, 0, width, height)

    local label = Label()
    label:frame(0, 0, width, 40)
    label:textColor(0xffffff)
    label:textSize(16)
    label:text("Hello World")
    label:onClick(function()
        -- body
        local tableData = {}
        tableData["type"] = "123"
        Native:sendAction(Native:base64Encode("LuaView://applets?appletId=1235&template=os_video_starContent_hotspot.lua&id=os_video_starContent_hotspot&type=1&priority=1"), tableData)
    end)

    luaview:addView(label)

    test.luaview = luaview
    test.label = label

end
