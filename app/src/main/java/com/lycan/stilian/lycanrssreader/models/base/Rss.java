package com.lycan.stilian.lycanrssreader.models.base;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public abstract class Rss {
    @Element
    public Channel channel;

    @Attribute
    public String version;
}
