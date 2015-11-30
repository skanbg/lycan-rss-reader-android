package com.lycan.stilian.lycanrssreader.models.base;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(strict = false)
public class Item {
    @Element(name = "title", required = false)
    public String title;

    @Path("description")
    @Text
    public String description;

    @Element(name = "link", required = false)
    @Text
    public String link;
}