--
-- Created by Android studio 3.3.2.
-- Author: lucas
-- Date: 2019/7/23
--

require "os_config"
require "os_string"
require "os_constant"
require "os_util"
require "os_track"
eShopWindow = object:new()
local scale = getScale()
local OS_ICON_WEDGE_CLOSE = "iVBORw0KGgoAAAANSUhEUgAAAFEAAABRCAYAAACqj0o2AAAABHNCSVQICAgIfAhkiAAABJxJREFUeJzt2U9oHFUcB/Dvb94ku0k3sdi02pamcVPazY7MpSA0p/a44KbsFJoGBUsFkV6L4DEeioce9GDpwYsX8bCwWXdNt3/Seqh4aZESZWfDFo0VxUD9FxqT7c68n4eZ1KWY/gnJzgq/zzHszvzmm/fevN9bQAghhBBCCCGEEEIIIYQQQgghhBBCCCE6BEV140wm09/V1TXqeZ578eLFuwB4PddxHGdns9k8uLy8fGNmZuavDS7zqagobprJZPpN03wTwDtKqRHLsm66rrv4rNfJZrMDWuuzAE6ZphnbtWvXd/Pz8ysbX/HjGe2+IQB0dXWNAjgJ4GWt9THP887mcrkdz3KNsbGxPgDvAzgBIEVEbyQSiSMbX+2TRRKi53kuEd1m5mUi2sLMx3zf/zCTyfQ/zffDAD9i5gkACQANIvrWNM3ZTS18DZFM53q9vmhZ1k1m3glgH4AtAA4opSzbti9Vq9XGWt/NZDL9SqkLzDxORD3MvGIYxjQznymVSj+27SFaRBIiALiuu5hOp79m5pcAHADQDSDp+/6aQYYvo/PM7ACIA/CJ6HI8Hj89NTX1c5sf4aHIQgSAWq22lEwmryqlLABJADEAw57n2ZZlXavVakurn81mswNKqdUAexEEeD0ej0/k8/nfInoEABGHCAB37txp2LZ9yfd9C8AwgG5m3gtgt2VZt1zXve84zovMPMnMxxEE+ADAl0qp8UKhEMm2plXkIQJAtVpt2LZ9yfM8m5n3ElEcwDAzb0+lUr8y82kArwPoA7AC4LpSaqJYLP4ZaeGhyDbb/yWXy+3wff9cOGUTAO4T0T1mfh5APzMvGYZRYuZ3y+Xy3YjLfagjRuKqWq22ZFnWLWbeDmA/giC3AoiFAU4BmCyXy/NR1vmoSPaJj1MoFH4hoo+J6F7r34nod8MwPonFYj9gnS3iZum4EB3HeQHAa+EUbrVVa3282WzuQYctQx01nbPZ7AAzTyJ4iTzHzEtEtIDgn92HYIpvsyzrm/X02pulY0IMO5Hz4Tamb3UNVEq9h2D67kcQZIqZh23bvvK4zqadOiLERzqRXgArhmF8DmCyu7v7NjPPAtgGIAWgl5mTWuu0bduXOyHIyNeW8DDhAjPn8O9G+hqAt8vl8k8IRiE5jrPb87wPtNavhvvIB0Q03Ww2T1YqlUindqQjseU0ZhxhL4ygE5kolUoLrZ91XXfRtu0rWus0ws4GwH6lVCrqERlZiNlsdgDAOWY+TkQ9CHthpdT4Wp1I2Nlc9n0/jWCNVACGfN/fl06nb7T22u0USYiO4+wMT6RPEFGCmVfC05iJJ/XC1Wq1kUwmK0qpFIAhAD0AhrXWQyMjIzfn5ubaPrUj2Sc2m82DAEYRrIHLhmF8EY/H38rn8091mFCpVBZjsdgpIioC+BvBUvCK1vrQ5lW9tkhG4uDg4IJpmjEi2kNEXzHzmWc9D6xWq410On1Daz2E4I39GTN/Wq/X2/4bS2Rv58OHD29NJBJHTNOcLRaL32OdrdzRo0f3aK0Paa2vTk9P/7HBZQohhBBCCCGEEEIIIYQQQgghhBBCCCHE/8s/9e7kAC2uQe8AAAAASUVORK5CYII="
local OS_PORTRAIT_SHOP_TROLLEY = "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"
local OS_LANDSCAPE_SHOP_TROLLEY = "iVBORw0KGgoAAAANSUhEUgAAACMAAAAfCAYAAABtYXSPAAAABHNCSVQICAgIfAhkiAAAArpJREFUWIXF17+LXFUYxvHP2c2u7qpRRDtdgkSsIthYhohgJWghdloJQrBVEWzE0v/BwkJQSWOhkKASRLGIRcQfyBo34mZj/JE4cbOL68w8FvcOMyYzc0cnMz5wufeec95zv/Oec973HZDk+STn0lcnyWaSo0n2m6eSXMpwnU3y0Lw4Fur7t8iQ/jXclWRxHjD76vurOIoVLNcQd6PUzzfiyqxhCiQp2I/bcDOexXN1/zG8gT9m8P2ofuQP+GUflFKCFlpJFvANdnATHsYhdGYE08IXeHH4iOTxJOsjNvUs9FeS+xaG0nABWzPwxCj9jtYomJ+wOUeYD8bBzNsz72JvKEwpZQfnsD0HkHWcKqV0RnlGDfPrjEGC93GRfgT+v2B2cQKXmmC28NuMYT7HmVJKpwnmQn3NIthBG8dxvtcwEqaUsosfzSYndVReOa6KwOgnylE6qwpIvZrmWP0+jbqq5T+Jr+pUNBHMGdXmWqvf38bH1wFmF9ullO5gRxPMRg3T0x2llPOjBk+rJphN/U28iMeS7Ez5zW18ie9KKe3BjtJkmeQFvKSqddrYmxIGTuPlUsqHg41NnoF38KSqplnEDVOCRL+I+4caPQNJHsTTuLcGao+3uEalvroqz57Em1fvv0lhlnAQ92BVdcrWSymNpWiSA6rTuKoKcN9PYjduwiNJPkrSTvWfaj3JM0mWG+wOJnkryZUke0lOJ3kqyTVLNCnIapL36sl66ib5OsmhBtvXh5SYnyQ5PGz8uNzU0+04gKWBtoJbVXtonB4Z0ramWu7/BNNSBb7BhBn8qbka3BjSdtk0KSXJE0k26uXpJmkleSXJSoPd4SRbA0t0OclrSe4cNn7S07SC+3EEt+BTfFZKudhgt4QH8KhqWU+pMvXPgwnyX8HUExdVkCxoX53kGmyXB+xG1kd/A5Yi84yvUmB7AAAAAElFTkSuQmCC"
local adTypeName = "eShopWindow"

local function getWindowExposureTrackLink(data, index)
    if (data == nil or index == nil) then
        return nil
    end
    local infoTrackLinkTable = data.infoTrackLink
    if (infoTrackLinkTable == nil) then
        return nil
    end
    local indexInfoTrackLinkTable = infoTrackLinkTable[index]
    if (indexInfoTrackLinkTable == nil) then
        return nil
    end
    return indexInfoTrackLinkTable.exposureTrackLink
end

local function getWindowClickTrackLink(data, index)
    if (data == nil or index == nil) then
        return nil
    end
    local infoTrackLinkTable = data.infoTrackLink
    if (infoTrackLinkTable == nil) then
        return nil
    end
    local indexInfoTrackLinkTable = infoTrackLinkTable[index]
    if (indexInfoTrackLinkTable == nil) then
        return nil
    end
    return indexInfoTrackLinkTable.clickTrackLink
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

local function subPriceString(price, length)
    local pString = tostring(price)
    local subPrice = string.sub(pString, 1, length)
    if (subPrice == nil) then
        return pString
    end
    local s = string.find(subPrice, '.', 1, true)
    if s ~= nil and s == length then
        -- 最后一位为. 舍去
        subPrice = string.sub(pString, 1, length - 1)
    end
    return subPrice
end

local function closeView()
    Native:widgetEvent(eventTypeClose, eShopWindow.data.id, adTypeName, actionTypeNone, "")

    Native:destroyView()
end

local function closeViewByScreenDirection()
    local isPortrait = Native:isPortraitScreen()
    if (System.android()) then
        if (isPortrait) then
            startViewTranslationAnim(eShopWindow.windowView, 0, 480 * scale, {
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
            startViewTranslationAnim(eShopWindow.windowView, 223 * scale, 0, {
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
    else
        local screenWidth, screenHeight = Native:getVideoSize(2)
        if (isPortrait) then
            Animate(0.3,
                    function()
                        eShopWindow.windowView:y(math.max(screenWidth, screenHeight))
                    end,
                    function()
                        closeView()
                    end);
        else
            Animate(0.3,
                    function()
                        eShopWindow.windowView:x(math.max(screenWidth, screenHeight))
                    end,
                    function()
                        closeView()
                    end);
        end
    end


end

local function setLuaViewSize(luaview, isPortrait)
    --设置当前容器大小
    if (luaview == nil) then
        return
    end
    local screenWidth, screenHeight = Native:getVideoSize(2)
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

local function setWindowViewSize(view, isPortrait)
    if (view == nil) then
        return
    end
    --local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        view:frame(0, 0, eShopWindow.portraitWidth, eShopWindow.portraitHeight)
        view:align(Align.BOTTOM)
    else
        view:frame(0, 0, eShopWindow.landscapeWidth, eShopWindow.landscapeHeight)
        view:align(Align.RIGHT)
    end
end

local function createWindowView(isPortrait)
    local windowView = View()
    windowView:backgroundColor(0xFFFFFF)
    if System.ios() then
        -- 取消父view点击事件
        windowView:onClick(function()
        end)
    end
    setWindowViewSize(windowView, isPortrait)
    return windowView
end

local function setGoodsImgSize(view, isPortrait)
    if (isPortrait) then
        view:frame(22 * scale, 23 * scale, eShopWindow.portraitWidth / 375 * 330, eShopWindow.portraitWidth / 375 * 330)
    else
        view:frame(0, 0, eShopWindow.landscapeWidth, eShopWindow.landscapeWidth)
    end
    view:align(Align.H_CENTER)
end

local function createGoodsImg(data, isPortrait)
    local goodsImg = Image(Native)
    goodsImg:scaleType(ScaleType.CENTER_CORP)
    goodsImg:image(data.data.inforEdit.goodsImage)
    setGoodsImgSize(goodsImg, isPortrait)
    return goodsImg
end

local function toggleCloseImg(view, isPortrait)
    if (isPortrait) then
        view:show()
    else
        view:hide()
    end
end

local function createCloseImg(isPortrait)
    local closeImg = Image(Native)
    closeImg:scaleType(ScaleType.CENTER_CORP)
    closeImg:image(Data(OS_ICON_WEDGE_CLOSE))
    closeImg:frame(0, 0, 44 * scale, 44 * scale)
    closeImg:align(Align.RIGHT)
    toggleCloseImg(closeImg, isPortrait)
    return closeImg
end

local function setDetailViewSize(view, isPortrait)
    if (isPortrait) then
        view:frame(0, 0, eShopWindow.portraitWidth, eShopWindow.portraitHeight - 23 * scale - eShopWindow.portraitWidth / 375 * 330)
    else
        view:frame(0, 0, eShopWindow.landscapeWidth, eShopWindow.landscapeHeight - eShopWindow.landscapeWidth)
    end
    view:alignBottom()
end

local function createDetailView(isPortrait)
    local detailView = View()
    detailView:backgroundColor(0xFFFFFF, 1)
    detailView:align(Align.BOTTOM)
    setDetailViewSize(detailView, isPortrait)
    return detailView
end

local function togglePortraitTrolley(view, isPortrait)
    if (isPortrait) then
        view:frame(eShopWindow.goodsImg:right() + (12 - 68) * scale, eShopWindow.goodsImg:bottom() - (21 + 68) * scale, 68 * scale, 68 * scale)
        view:show()
    else
        view:hide()
    end
end
local function createPortraitTrolleyButton(isPortrait)
    local shopTrolley = Image()
    shopTrolley:image(Data(OS_PORTRAIT_SHOP_TROLLEY))
    if System.android() then
        shopTrolley:scale(ScaleType.FIT_XY)
    end

    togglePortraitTrolley(shopTrolley, isPortrait)

    return shopTrolley
end

local function setGoodsTitleSize(view, isPortrait)
    if (isPortrait) then
        view:textBold()
        view:frame(22 * scale, 10 * scale, 332 * scale, 48 * scale)
    else
        view:textSize(15)
        view:frame(13 * scale, 18 * scale, 197 * scale, 40 * scale)
    end
    view:align(Align.H_CENTER)
end

local function createGoodsTitle(title, isPortrait)
    local goodsTitle = Label()
    goodsTitle:align(Align.H_CENTER)
    goodsTitle:lines(2)
    goodsTitle:textSize(15)
    goodsTitle:textColor(0x000000)
    goodsTitle:text(title)
    goodsTitle:textAlign(TextAlign.LEFT)
    if System.android() then
        goodsTitle:gravity(Gravity.TOP)
    else
        if goodsTitle.textVAlign then
            goodsTitle:textVAlign(TextVAlign.TOP)
        end
    end
    setGoodsTitleSize(goodsTitle, isPortrait)
    return goodsTitle
end

local function setOriginPriceSize(view, isPortrait)
    -- if (isPortrait) then
    --     view:xy(28 * scale + eShopWindow.discountPriceWidth, 73 * scale)
    -- else
    --     view:xy(18 * scale + eShopWindow.discountPriceWidth, 75 * scale)
    -- end
    view:xy(8 * scale + eShopWindow.discountPrice:right(), eShopWindow.discountPrice:bottom() - 18 * scale)
end

local function createOriginPrice(price)
    local originPrice = Label()
    originPrice:textSize(12)
    originPrice:textColor(0xbebebe)

    local subPrice = subPriceString(price, 6)

    local text = "原价:" .. subPrice
    originPrice:text(text)
    -- 计算文本宽度
    local textWidth = Native:stringDrawLength(text, 14)

    --if System.ios() then
    -- 下划线会加大textWidth
    textWidth = textWidth * 1.1
    --end

    eShopWindow.discountPriceWidth = textWidth
    originPrice:frame(0, 0, textWidth, 16 * scale)

    if originPrice.strikeLines then
        originPrice:strikeLines(0xbebebe)
    end

    if System.android() then
        originPrice:textAlign(TextAlign.BOTTOM)
    else
        if originPrice.textVAlign then
            originPrice:textVAlign(TextVAlign.BOTTOM)
        end
    end

    setOriginPriceSize(originPrice, isPortrait)

    return originPrice
end

local function setDiscountPriceSize(view, isPortrait)
    -- 计算title高度为一行还是两行
    local titleWidth = Native:stringDrawLength(eShopWindow.goodsTitle:text(), 15)
    if (isPortrait) then
        -- title字体加粗
        titleWidth = titleWidth * 1.05
        if titleWidth > eShopWindow.goodsTitle:width() then
            -- view:xy(20 * scale, 52 * scale)
            view:xy(20 * scale, 46 * scale)
        else
            -- view:xy(20 * scale, 36 * scale)
            view:xy(20 * scale, 30 * scale)
        end
    else
        if titleWidth > eShopWindow.goodsTitle:width() then
            -- view:xy(10 * scale, 66 * scale)
            view:xy(10 * scale, 60 * scale)
        else
            -- view:xy(10 * scale, 46 * scale)
            view:xy(10 * scale, 40 * scale)
        end
    end

end

local function createDiscountPrice(price, isPortrait)
    local discountPrice = Label()
    discountPrice:textSize(25)
    discountPrice:textColor(0xFF3221)
    discountPrice:lines(1)
    discountPrice:textBold()

    local subPrice = subPriceString(price, 6)
    local text = "¥" .. subPrice
    discountPrice:text(text)
    -- 计算文本宽度
    local textWidth = Native:stringDrawLength(text, 27)
    -- 因为加粗,需要更宽一点
    textWidth = textWidth * 1.1
    eShopWindow.discountPriceWidth = textWidth
    discountPrice:frame(0, 0, textWidth, 30 * scale)

    if System.android() then
        discountPrice:textAlign(TextAlign.BOTTOM)
    else
        if discountPrice.textVAlign then
            discountPrice:textVAlign(TextVAlign.BOTTOM)
        end
    end

    setDiscountPriceSize(discountPrice, isPortrait)

    return discountPrice
end

local function setTagLabelSize(view, label, isPortrait)
    if (isPortrait) then
        local corner = 24 * scale / 2
        view:corner(corner, corner, 0, 0, 0, 0, corner, corner)
        view:centerY(eShopWindow.goodsImg:bottom() + eShopWindow.discountPrice:centerY())
        view:width(label:width() + 20 * scale)
    else
        local corner = 24 * scale / 2
        view:corner(corner, corner, 0, 0, 0, 0, corner, corner)
        view:xy(0, eShopWindow.goodsImg:bottom() - 12 * scale)
        view:width(label:width() + 18 * scale)
    end
    view:align(Align.RIGHT)
end

local function createTabLabel(tagText, isPortrait)
    local tagView = GradientView()
    tagView:frame(0, 0, 70 * scale, 24 * scale)
    tagView:gradient(0xff6245, 0xff4c86)
    local tagLabel = Label()
    tagLabel:textSize(13)
    tagLabel:textColor(0xFFFFFF)
    if System.android() then
        tagLabel:gravity(Gravity.CENTER)
    else

    end

    tagText = tostring(tagText)
    tagLabel:text(tagText)
    local textWidth = Native:stringDrawLength(tagText, 15)
    local sixTextWitdh = Native:stringDrawLength("一二三四五六", 13)
    if textWidth > sixTextWitdh then
        textWidth = sixTextWitdh
    end

    tagLabel:frame(10 * scale, 0, textWidth, 24 * scale)
    tagView:width(textWidth + 20 * scale)
    tagView:addView(tagLabel)

    setTagLabelSize(tagView, tagLabel, isPortrait)

    return tagView, tagLabel
end

local function setBottomTrolleySize(view, isPortrait)
    if (isPortrait) then
        view:hide()
    else
        view:show()
        view:align(Align.BOTTOM)
    end
end

local function createBottomTrolley(btnTxt, isPortrait)
    local bottomContent = GradientView()
    bottomContent:gradient(0xff6245, 0xFF4C86)
    bottomContent:frame(0, 0, eShopWindow.landscapeWidth, 40 * scale)

    local trolleyLogo = Image(Native)
    trolleyLogo:scaleType(ScaleType.FIT_CENTER)
    trolleyLogo:frame(eShopWindow.landscapeWidth * 0.282, 0, 20 * scale, 20 * scale)
    trolleyLogo:align(Align.V_CENTER)
    trolleyLogo:image(Data(OS_LANDSCAPE_SHOP_TROLLEY))
    bottomContent:addView(trolleyLogo)

    local addTrolley = Label()
    addTrolley:textSize(14)
    addTrolley:textBold()
    addTrolley:textColor(0xFFFFFF)
    addTrolley:text(btnTxt)
    local textWidth = Native:stringDrawLength(btnTxt, 16)
    addTrolley:frame(trolleyLogo:right() + 9 * scale, 0, textWidth, 20 * scale)
    addTrolley:align(Align.V_CENTER)
    bottomContent:addView(addTrolley)

    -- 图标和文本居中,总长(20+9)*scale+textwidth
    local contentWidth = (20 + 9) * scale + textWidth
    trolleyLogo:x((bottomContent:width() - contentWidth) / 2)
    addTrolley:x(trolleyLogo:x() + 29 * scale)

    setBottomTrolleySize(bottomContent, isPortrait)
    return bottomContent, trolleyLogo, addTrolley
end


--屏幕旋转--
local function rotationScreen(isPortrait)
    local screenWidth, screenHeight = Native:getVideoSize(2)
    local videoWidth, videoHeight, marginTop = Native:getVideoSize(0)
    eShopWindow.portraitWidth = math.min(screenWidth, screenHeight) -- 宽
    eShopWindow.portraitHeight = math.max(screenWidth, screenHeight) - videoHeight - marginTop --高

    setLuaViewSize(eShopWindow.luaView, isPortrait)
    setWindowViewSize(eShopWindow.windowView, isPortrait)
    setGoodsImgSize(eShopWindow.goodsImg, isPortrait)
    toggleCloseImg(eShopWindow.closeImg, isPortrait)
    setDetailViewSize(eShopWindow.detailView, isPortrait)
    setGoodsTitleSize(eShopWindow.goodsTitle, isPortrait)
    setDiscountPriceSize(eShopWindow.discountPrice, isPortrait)
    setOriginPriceSize(eShopWindow.originPrice, isPortrait)
    setTagLabelSize(eShopWindow.tagView, eShopWindow.tagLabel, isPortrait)
    setBottomTrolleySize(eShopWindow.bottomTrolley, isPortrait)
    togglePortraitTrolley(eShopWindow.portraitTrolley, isPortrait)
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

local function getLinkUrl(data)
    --获取linkUrl
    if (data == nil) then
        return nil
    end
    local linkData = data.linkData
    if (linkData == nil) then
        return nil
    end
    local linkUrl = linkData.linkUrl
    local deepLink = linkData.deepLink
    local selfLink = linkData.selfLink
    if (linkUrl ~= nil and string.len(linkUrl) > 0) then
        return linkUrl
    elseif (deepLink ~= nil and string.len(deepLink) > 0) then
        return deepLink
    elseif (selfLink ~= nil and string.len(selfLink) > 0) then
        return selfLink
    end
    return nil
end

local function onCreate(data)
    local exposureTrackLink = getWindowExposureTrackLink(data, 1)
    if (exposureTrackLink ~= nil) then
        Native:get(exposureTrackLink)
    end

    if (eShopWindow.launchPlanId ~= nil) then
        -- 信息层及点击位曝光
        osTrack(eShopWindow.launchPlanId, 1, 1)
        osTrack(eShopWindow.launchPlanId, 2, 1)
    end

    local isPortrait = Native:isPortraitScreen()

    eShopWindow.media = registerMedia()
    eShopWindow.luaView = createLuaView(isPortrait)
    --eShopWindow.luaView:backgroundColor(0xFC5D5D, 0.5)
    eShopWindow.windowView = createWindowView(isPortrait)
    --eShopWindow.windowView:backgroundColor(0x3998F7, 0.5)

    eShopWindow.goodsImg = createGoodsImg(data, isPortrait)

    eShopWindow.closeImg = createCloseImg(isPortrait)

    eShopWindow.detailView = createDetailView(isPortrait)
    -- 竖屏状态购物车Btn
    eShopWindow.portraitTrolley = createPortraitTrolleyButton(isPortrait)
    -- 商品名称
    eShopWindow.goodsTitle = createGoodsTitle(data.data.inforEdit.goodsTitle, isPortrait)
    -- 优惠价
    eShopWindow.discountPrice = createDiscountPrice(data.data.inforEdit.preferential, isPortrait)
    -- 原价
    eShopWindow.originPrice = createOriginPrice(data.data.inforEdit.originalPrice)
    -- tag
    eShopWindow.tagView, eShopWindow.tagLabel = createTabLabel(data.data.inforEdit.goodsTag, isPortrait)
    -- 底部加入购物车Btn
    eShopWindow.bottomTrolley, eShopWindow.trolleyLogo, eShopWindow.addTrolley = createBottomTrolley(data.data.inforEdit.btnTxt, isPortrait)

    eShopWindow.detailView:addView(eShopWindow.goodsTitle)
    eShopWindow.detailView:addView(eShopWindow.discountPrice)
    eShopWindow.detailView:addView(eShopWindow.originPrice)
    eShopWindow.detailView:addView(eShopWindow.bottomTrolley)
    eShopWindow.windowView:addView(eShopWindow.goodsImg)
    eShopWindow.windowView:addView(eShopWindow.closeImg)
    eShopWindow.windowView:addView(eShopWindow.detailView)
    eShopWindow.windowView:addView(eShopWindow.portraitTrolley)
    eShopWindow.windowView:addView(eShopWindow.tagView)
    eShopWindow.luaView:addView(eShopWindow.windowView)

    -- 仅在横屏下点击window外的区域才关闭
    eShopWindow.luaView:onClick(function()
        closeViewByScreenDirection()
    end)

    eShopWindow.windowView:onClick(function()
    end)

    eShopWindow.closeImg:onClick(function()
        closeViewByScreenDirection()
    end)

    eShopWindow.bottomTrolley:onClick(function()
        local clickTrackLink = getWindowClickTrackLink(data, 1)
        if (clickTrackLink ~= nil) then
            Native:get(clickTrackLink)
        end

        if (eShopWindow.launchPlanId ~= nil) then
            -- 信息层点击
            osTrack(eShopWindow.launchPlanId, 3, 1)
        end

        local linkUrl = getLinkUrl(data.data.inforEdit)
        if (linkUrl == nil) then
            return
        end
        local linkData = data.data.inforEdit.linkData
        widgetEvent(eventTypeClick, data.id, adTypeName, actionTypeOpenUrl, linkData.linkUrl, linkData.deepLink, linkData.selfLink)
    end)

    eShopWindow.portraitTrolley:onClick(function()

        local clickTrackLink = getWindowClickTrackLink(data, 1)
        if (clickTrackLink ~= nil) then
            Native:get(clickTrackLink)
        end

        if (eShopWindow.launchPlanId ~= nil) then
            -- 信息层点击
            osTrack(eShopWindow.launchPlanId, 3, 1)
        end

        local linkUrl = getLinkUrl(data.data.inforEdit)
        if (linkUrl == nil) then
            return
        end
        local linkData = data.data.inforEdit.linkData
        widgetEvent(eventTypeClick, data.id, adTypeName, actionTypeOpenUrl, linkData.linkUrl, linkData.deepLink, linkData.selfLink)
    end)

    local screenWidth, screenHeight = Native:getVideoSize(2)
    if (isPortrait) then
        if System.ios() then
            eShopWindow.windowView:y(math.max(screenWidth, screenHeight))
            local x, y, w, h = eShopWindow.windowView:frame()
            Animate(0.3,
                    function()
                        eShopWindow.windowView:y(math.max(screenWidth, screenHeight) - h)
                    end,
                    function()
                    end);
        else
            eShopWindow.windowView:translation(0, 480 * scale)
            startViewTranslationAnim(eShopWindow.windowView, 0, 0)
        end

    else
        if System.ios() then
            eShopWindow.windowView:x(math.max(screenWidth, screenHeight))
            local x, y, w, h = eShopWindow.windowView:frame();
            Animate(0.3,
                    function()
                        eShopWindow.windowView:x(math.max(screenWidth, screenHeight) - w)
                    end,
                    function()
                    end);
        else
            eShopWindow.windowView:translation(eShopWindow.landscapeWidths, 0)
            startViewTranslationAnim(eShopWindow.windowView, 0, 0)
        end
    end

    Native:widgetEvent(eventTypeShow, eShopWindow.data.id, adTypeName, actionTypeNone, "")
end

local function setConfig(data)
    if (data == nil) then
        return
    end
    eShopWindow.data = data
    local screenWidth, screenHeight = Native:getVideoSize(2)
    local videoWidth, videoHeight, marginTop = Native:getVideoSize(0)
    eShopWindow.portraitWidth = math.min(screenWidth, screenHeight) -- 宽
    eShopWindow.portraitHeight = math.max(screenWidth, screenHeight) - videoHeight - marginTop --高
    eShopWindow.landscapeHeight = math.min(screenWidth, screenHeight) -- 横屏高
    eShopWindow.landscapeWidth = eShopWindow.landscapeHeight / 375 * 223 -- 横屏宽
    eShopWindow.launchPlanId = data.launchPlanId
end

function show(args)
    if (args == nil or args.data == nil or eShopWindow.luaView ~= nil) then
        return
    end
    setConfig(args.data)
    onCreate(args.data)
end
