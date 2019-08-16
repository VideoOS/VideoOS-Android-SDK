require "os_config"
require "os_string"
require "os_constant"
require "os_util"
test1 = object:new()
local scale = getScale()


function show(args)

    local width, height = Applet:appletSize()
    local luaview = View()
    luaview:backgroundColor(0xff0000)
    luaview:frame(0, 0, width, height)

    local label = Label()
    label:frame(0, 60, width, 40)
    label:textColor(0xffffff)
    label:textSize(16)
    label:text("Hello World Twice")

    luaview:addView(label)

    test1.luaview = luaview
    test1.label = label

end
