package com.lycan.stilian.lycanrssreader.models.base;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

@Root(strict = false)
public class Channel {
    @Element(name = "title")
    public String title;

//    @ElementList(entry = "link", inline = true, required = false)
//    public List<Link> links;
    @Element(name = "image")
    public ChannelImage image;

//    @Element(required = false)
//    public String link;
//    @Element(name = "link", required = false)
//    @Text
//    public String link;

    @Path("link")
    @Element(name = "link", required = false)
    @Text
    public String link;

    @Element(name = "description", required = false)
    public String description;

    @Element(name = "language", required = false)
    public String language;

    @Element(name = "copyright", required = false)
    public String copyright;

    @Element(name = "pubDate", required = false)
    public String pubDate;

    @Element(name = "lastBuildDate", required = false)
    public String lastBuildDate;

    @Element(name = "updateFrequency", required = false)
    public int updateFrequency;

    @Element(name = "generator", required = false)
    public String generator;

    @ElementList(name = "item", type = Item.class, required = false, inline = true)
    public List<Item> items;
}