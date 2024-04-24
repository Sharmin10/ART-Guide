package com.example.art_guide;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryParser {
    public static List<GalleryInfo> parseGallery(Context context) {
        List<GalleryInfo> galleries = new ArrayList<>();
        Resources resources = context.getResources();
        XmlResourceParser xmlParser = resources.getXml(R.xml.gallery);

        try {
            int eventType = xmlParser.getEventType();
            GalleryInfo gallery = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG){
                    if("gallery".equals(xmlParser.getName())) {
                        gallery = new GalleryInfo("", "", "", "","","");
                    } else if (gallery != null) {
                        String tagName = xmlParser.getName();
                        if ("name".equals(tagName)) {
                            gallery.setName(xmlParser.nextText());
                        } else if ("image".equals(tagName)) {
                            gallery.setImage(xmlParser.nextText());
                        } else if ("map".equals(tagName)) {
                            gallery.setMap(xmlParser.nextText());
                        } else if ("address".equals(tagName)) {
                            gallery.setAddress(xmlParser.nextText());
                        } else if ("open".equals(tagName)) {
                            gallery.setOpen(xmlParser.nextText());
                        } else if ("description".equals(tagName)) {
                            gallery.setDescription(xmlParser.nextText());
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && "gallery".equals(xmlParser.getName())) {
                    galleries.add(gallery);
                    gallery = null;
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException | IOException x) {
            x.printStackTrace();
        } finally {
            xmlParser.close();
        }
        return galleries;
    }
}
