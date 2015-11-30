package com.lycan.stilian.lycanrssreader.models.base;


        import org.simpleframework.xml.Element;
        import org.simpleframework.xml.Root;

@Root(strict = false)
public class ChannelImage {
    @Element(name = "title")
    public String title;

    @Element(name = "url")
    public String url;

    @Element(name = "link")
    public String link;
}