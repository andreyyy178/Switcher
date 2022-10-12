package com.example.switcher.models

//Example response 0 means ON
/*<response>
<out0>1</out0>
<out1>1</out1>
<out2>1</out2>
<out3>1</out3>
<out4>1</out4>
<out5>0</out5>
<out6>1</out6>
<di0>up</di0>
<di1>up</di1>
<di2>up</di2>
<di3>up</di3>
<ia0>300</ia0>
<ia1>126</ia1>
<ia2>0</ia2>
<ia3>1743</ia3>
<ia4>4326</ia4>
<ia5>0</ia5>
<ia6>26</ia6>
<ia7>105</ia7>
<ia8>-600</ia8>
<ia9>-600</ia9>
<ia10>210</ia10>
<ia11>190</ia11>
<ia12>214</ia12>
<ia13>217</ia13>
<ia14>447</ia14>
<ia15>0</ia15>
<ia16>0</ia16>
<ia17>0</ia17>
<ia18>0</ia18>
<ia19>0</ia19>
<freq>5008</freq>
<duty>500</duty>
<pwm>0</pwm>
<sec0>6</sec0>
<sec1>10</sec1>
<sec2>13</sec2>
<sec3>1</sec3>
<sec4>145759</sec4>
</response>*/

import org.simpleframework.xml.Element
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "response", strict = false)
data class Switcher  @JvmOverloads constructor (
    @field:Element(name = "out0")
    @param:Element(name = "out0")
    var out0: Int = 1,

    @field:Element(name = "out1")
    @param:Element(name = "out1")
    var out1: Int = 1,

    @field:Element(name = "out2")
    @param:Element(name = "out2")
    var out2: Int = 1,

    @field:Element(name = "out3")
    @param:Element(name = "out3")
    var out3: Int = 1,

    @field:Element(name = "out4")
    @param:Element(name = "out4")
    var out4: Int = 1,

    @field:Element(name = "out5")
    @param:Element(name = "out5")
    var out5: Int = 1,

    @field:Element(name = "ia10")
    @param:Element(name = "ia10")
    var ia0: String = "---",

    @field:Element(name = "ia12")
    @param:Element(name = "ia12")
    var ia12: String = "---",

    @field:Element(name = "ia13")
    @param:Element(name = "ia13")
    var ia13: String = "---",
    )