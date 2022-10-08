package com.example.switcher.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "response", strict = false)
data class Status  @JvmOverloads constructor (
    @field:Element(name = "out0")
    @param:Element(name = "out0")
    var out0: Int? = null,

    @field:Element(name = "out1")
    @param:Element(name = "out1")
    var out1: Int? = null,

    @field:Element(name = "out2")
    @param:Element(name = "out2")
    var out2: Int? = null,

    @field:Element(name = "out3")
    @param:Element(name = "out3")
    var out3: Int? = null,

    @field:Element(name = "out4")
    @param:Element(name = "out4")
    var out4: Int? = null,

    @field:Element(name = "out5")
    @param:Element(name = "out5")
    var out5: Int? = null,

    @field:Element(name = "ia10")
    @param:Element(name = "ia10")
    var ia0: String? = null,

    @field:Element(name = "ia12")
    @param:Element(name = "ia12")
    var ia12: String? = null,

    @field:Element(name = "ia13")
    @param:Element(name = "ia13")
    var ia13: String? = null,
    )