package com.appspresso.w3.widget;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;

public class WidgetConfigParser {

    public static WidgetConfigParser newInstance() {
        return new WidgetConfigParser();
    }

    public DefaultWidgetConfig newWidgetConfig(Activity activity, String configXml)
            throws IOException {
        InputStream in = null;
        DefaultWidgetConfig config = null;
        try {
            in = activity.getAssets().open(configXml);
            config = new DefaultWidgetConfig(activity);
            parse(config, in);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (Exception ignored) {}
            }
        }

        return config;
    }

    private void parse(DefaultWidgetConfig config, final InputStream in) throws IOException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in, "utf-8");

            parse(parser, config);
        }
        catch (XmlPullParserException e) {
            throw new IOException(e.getMessage());
        }
    }

    private void parse(final XmlPullParser parser, final DefaultWidgetConfig config) {
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (eventType == XmlPullParser.START_TAG) {
                    if ("widget".equalsIgnoreCase(name)) {
                        visitWidget(parser, config);
                    }
                    else if ("name".equalsIgnoreCase(name)) {
                        visitName(parser, config);
                    }
                    else if ("author".equalsIgnoreCase(name)) {
                        visitAuthor(parser, config);
                    }
                    else if ("description".equalsIgnoreCase(name)) {
                        visitDescription(parser, config);
                    }
                    else if ("preference".equalsIgnoreCase(name)) {
                        visitPreference(parser, config);
                    }
                    else if ("feature".equalsIgnoreCase(name)) {
                        visitFeature(parser, config);
                    }
                    else if ("content".equalsIgnoreCase(name)) {
                        visitContent(parser, config);
                    }
                }
                eventType = parser.next();
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void visitContent(XmlPullParser parser, DefaultWidgetConfig config) {
        String src = null;
        String encoding = null;
        String type = null;

        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attr = parser.getAttributeName(i);
            if ("src".equalsIgnoreCase(attr)) {
                src = parser.getAttributeValue(i);
            }
            else if ("type".equalsIgnoreCase(attr)) {
                type = parser.getAttributeValue(i);
            }
            else if ("encoding".equalsIgnoreCase(attr)) {
                encoding = parser.getAttributeValue(i);
            }
        }

        config.setContent(src, type, encoding);
    }

    private void visitPreference(XmlPullParser parser, DefaultWidgetConfig config) {
        String name = null;
        boolean readonly = false;
        String value = null;

        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attr = parser.getAttributeName(i);
            if ("name".equalsIgnoreCase(attr)) {
                name = parser.getAttributeValue(i);
            }
            else if ("value".equalsIgnoreCase(attr)) {
                value = parser.getAttributeValue(i);
            }
            else if ("readonly".equalsIgnoreCase(attr)) {
                readonly = Boolean.parseBoolean(parser.getAttributeValue(i));
            }
        }

        config.addPreference(name, value, readonly);
    }

    private void visitFeature(XmlPullParser parser, DefaultWidgetConfig config)
            throws XmlPullParserException, IOException {
        String name = null;
        boolean required = true;

        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attr = parser.getAttributeName(i);
            if ("name".equalsIgnoreCase(attr)) {
                name = parser.getAttributeValue(i);
            }
            else if ("required".equalsIgnoreCase(attr)) {
                required = Boolean.parseBoolean(parser.getAttributeValue(i));
            }
        }

        DefaultFeature defaultFeature = new DefaultFeature(name, required);
        int type = parser.nextTag();
        String tagname = parser.getName();
        while (type != XmlPullParser.END_TAG || !"feature".equalsIgnoreCase(tagname)) {
            if (type == XmlPullParser.START_TAG && "param".equalsIgnoreCase(tagname)) {
                visitParam(parser, defaultFeature);
            }

            type = parser.nextTag();
            tagname = parser.getName();
        }

        config.addFeatues(defaultFeature);
    }

    private void visitParam(XmlPullParser parser, DefaultFeature defaultFeature) {
        String name = null;
        String value = null;
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String attr = parser.getAttributeName(i);
            if ("name".equalsIgnoreCase(attr)) {
                name = parser.getAttributeValue(i);
            }
            else if ("value".equalsIgnoreCase(attr)) {
                value = parser.getAttributeValue(i);
            }
        }

        defaultFeature.putParam(name, value);
    }

    private void visitDescription(XmlPullParser parser, DefaultWidgetConfig config)
            throws XmlPullParserException, IOException {
        config.setDescription(parser.nextText());
    }

    private void visitAuthor(XmlPullParser parser, DefaultWidgetConfig config)
            throws XmlPullParserException, IOException {
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            if ("href".equalsIgnoreCase(name)) {
                config.setAuthorHref(parser.getAttributeValue(i));
            }
            else if ("email".equalsIgnoreCase(name)) {
                config.setAuthorEmail(parser.getAttributeValue(i));
            }
        }

        config.setAuthor(parser.nextText());
    }

    private void visitName(XmlPullParser parser, DefaultWidgetConfig config)
            throws XmlPullParserException, IOException {
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            if ("short".equalsIgnoreCase(name)) {
                config.setShortName(parser.getAttributeValue(i));
            }
        }
        config.setName(parser.nextText());
    }

    private void visitWidget(final XmlPullParser parser, final DefaultWidgetConfig config) {
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            if ("width".equalsIgnoreCase(name)) {
                config.setWidth(Long.parseLong(parser.getAttributeValue(i)));
            }
            else if ("height".equalsIgnoreCase(name)) {
                config.setHeight(Long.parseLong(parser.getAttributeValue(i)));
            }
            else if ("version".equalsIgnoreCase(name)) {
                config.setVersion(parser.getAttributeValue(i));
            }
            else if ("id".equalsIgnoreCase(name)) {
                config.setId(parser.getAttributeValue(i));
            }
            else if ("defaultlocale".equalsIgnoreCase(name)) {
                config.setDefaultLocale(parser.getAttributeValue(i));
            }
        }
    }

}
