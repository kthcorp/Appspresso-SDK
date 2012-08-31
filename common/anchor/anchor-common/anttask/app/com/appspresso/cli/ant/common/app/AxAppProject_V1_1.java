package com.appspresso.cli.ant.common.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.taskdefs.Property;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class AxAppProject_V1_1 implements AxAppProject {

    private static final String LASTEST_BUILT_IN_AXP_VERSION = "1.1.2";

    private final List<String> builtInAxp = new ArrayList<String>(20);

    private File appdir;
    private String configXml;
    private File builtinPluginsDir;
    private File pluginsCacheDir;

    private Project project;

    private String axSdkDir;

    private String targetPlatform;

    public static AxAppProject createInstance(String appdir) {
        return new AxAppProject_V1_1(appdir);
    }

    protected AxAppProject_V1_1(String appdir) {
        this.appdir = new File(appdir);
    }

    @Override
    public void process(Project project) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(getProjectXMLFile());

        this.generateConfigXML(builder, document.getElementsByTagName("widget").item(0),
                this.configXml);
        this.parseAndroid(project, document);
        this.parseIOS(project, document);
        this.parseWAC(project, document);

        List<File> axplugins = this.resolveAxpPath(document);
        List<AxPlugin> extractPlugins = extractAxp(axplugins, pluginsCacheDir);

        int length = extractPlugins.size();
        project.setProperty("plugin.length", String.valueOf(length));
        for (int i = 0; i < length; i++) {
            AxPlugin p = extractPlugins.get(i);
            project.setProperty("plugin." + i + ".id", p.id);
            project.setProperty("plugin." + i + ".path", p.path);
        }
    }

    protected void generateConfigXML(DocumentBuilder builder, Node widget, String config)
            throws SAXException, IOException, ParserConfigurationException, TransformerException {
        File configFile = new File(config);
        File parent = configFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("can't make directory : " + parent.getAbsolutePath());
        }

        Document newDocument = builder.newDocument();

        Node newWidget = newDocument.importNode(widget, true);
        removeObsoluteFeature(newWidget);

        Node xmlns = newDocument.createAttribute("xmlns");
        xmlns.setTextContent("http://www.w3.org/ns/widgets");
        newWidget.getAttributes().setNamedItem(xmlns);

        newDocument.appendChild(newWidget);
        newDocument.setXmlStandalone(false);
        newDocument.setXmlVersion("1.0");

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(newDocument);
        StreamResult result = new StreamResult(configFile);
        transformer.transform(source, result);
    }

    private void removeObsoluteFeature(Node widget) {
        NodeList children = widget.getChildNodes();
        List<Node> obsoulteFeatures = new ArrayList<Node>();

        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node child = children.item(i);
            String nodeName = child.getNodeName();
            if (nodeName == null || !"feature".equals(nodeName)) {
                continue;
            }

            Node name = child.getAttributes().getNamedItem("name");
            String content = name.getTextContent();
            if (content.startsWith("http://waclists.org/api")) {
                obsoulteFeatures.add(child);
            }
        }

        for (Node node : obsoulteFeatures) {
            widget.removeChild(node);
        }
    }

    private List<AxPlugin> extractAxp(List<File> axplugins, File cache) throws ZipException,
            IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        final XPathExpression exprPluginId =
                XPathFactory.newInstance().newXPath().compile("axplugin/@id");

        List<AxPlugin> extractPlugins = new ArrayList<AxPlugin>();

        for (File axp : axplugins) {
            String filename = axp.getName();
            File extractPath = new File(cache, filename.substring(0, filename.lastIndexOf(".axp")));

            ZipFile zipFile = new ZipFile(axp);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                final File file = new File(extractPath, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                    continue;
                }

                final File parent = file.getParentFile();

                if (!parent.exists() && !parent.mkdirs()) {
                    final String msg = "Could not create dir: " + parent.getPath(); //$NON-NLS-1$
                    throw new IOException(msg);
                }

                InputStream in = null;
                OutputStream out = null;

                in = zipFile.getInputStream(entry);
                out = new FileOutputStream(file);

                final byte[] bytes = new byte[1024];
                int count = in.read(bytes);

                while (count != -1) {
                    out.write(bytes, 0, count);
                    count = in.read(bytes);
                }

                out.flush();

                try {
                    in.close();
                    out.close();
                } catch (Exception ignored) {}
            }

            try {
                zipFile.close();
            } catch (Exception ignored) {}

            Document pluginXml =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder()
                            .parse(new File(extractPath, "axplugin.xml"));
            String id = exprPluginId.evaluate(pluginXml);
            extractPlugins.add(new AxPlugin(id, extractPath.getAbsolutePath()));
        }

        return extractPlugins;
    }

    private File getProjectXMLFile() {
        return new File(this.appdir, "project.xml");
    }

    private List<File> resolveAxpPath(Document document) {
        List<File> axplugins = new ArrayList<File>();
        boolean isOnTheFly = isIOSRunOnDebug();
        
        // String[] plugins = this.builtinPluginsDir.list();
        NodeList plugins = document.getElementsByTagName("plugin");
        int length = plugins.getLength();
        for (int i = 0; i < length; i++) {
            Node plugin = plugins.item(i);
            NamedNodeMap attr = plugin.getAttributes();
            Node idNode = attr.getNamedItem("id");
            Node versionNode = attr.getNamedItem("version");
            Node locationNode = attr.getNamedItem("location");

            File path = null;
            if (idNode != null) {
                // built-in
                String id = idNode.getTextContent();
                System.out.println("try to resolve plugin for id : " + id);
                path = resolveBuiltInPlugin(id);
                if (path == null) {
                    path =
                            resolveAxpInPluginsDir(id,
                                    versionNode != null ? versionNode.getTextContent() : null);
                }
            } else if (locationNode != null) {
                // axp project or .axp
                File location = new File(locationNode.getTextContent());
                File potential =
                        location.isAbsolute()
                                ? location
                                : new File(this.appdir, location.getPath());

                if (potential.isDirectory()) {
                    path = new File(this.pluginsCacheDir, "axp/" + potential.getName() + ".axp");
                    Task task = createAXPBuildTask(potential, path, isOnTheFly);
                    task.perform();
                } else if (potential.isFile() && potential.getName().endsWith(".axp")) {
                    path = potential;
                }
            }
            System.out.println("resolve plugin : " + path.getAbsolutePath());
            if (path != null) {
                axplugins.add(path);
            }
        }

        return axplugins;
    }

    private Task createAXPBuildTask(File axpdir, File output, boolean isOnTheFly) {
        Ant ant = (Ant) this.project.createTask("ant");
        ant.setDir(axpdir);
        ant.setAntfile("build.xml");
        setPropertyWithAnt(ant, "ax.sdk.dir", new File(this.axSdkDir));
        setPropertyWithAnt(ant, "axp.out.path", output);
        setPropertyWithAnt(ant, "axp.target.platform", this.targetPlatform);

        if (isOnTheFly) {
            setPropertyWithAnt(ant, "ios.build.configuration", "Debug");
        } else {
            setPropertyWithAnt(ant, "ios.build.configuration", "Release");
        }

        return ant;
    }

    private void setPropertyWithAnt(Ant ant, String name, File location) {
        Property p = ant.createProperty();
        p.setName(name);
        p.setLocation(location);
    }

    private void setPropertyWithAnt(Ant ant, String name, String value) {
        Property p = ant.createProperty();
        p.setName(name);
        p.setValue(value);
    }

    private File resolveAxpInPluginsDir(final String id, final String version) {
        File pluginsDir = new File(appdir, "plugins");

        if (version != null) {
            File axp = new File(pluginsDir, id + "-" + version + ".axp");
            return axp.exists() ? axp : null;
        }

        final Pattern pattern = Pattern.compile(id + "-[0-9\\.]+.axp");
        String[] potentials = pluginsDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                Matcher m = pattern.matcher(name);
                return m.find();
            }
        });

        if (potentials.length < 1) {
            return null;
        }
        Arrays.sort(potentials);
        return new File(pluginsDir, potentials[potentials.length - 1]);
    }

    private File resolveBuiltInPlugin(String id) {
        if (!builtInAxp.contains(id)) return null;

        return new File(this.builtinPluginsDir, id + "-" + LASTEST_BUILT_IN_AXP_VERSION + ".axp");
    }

    private void parsePlatform(Project project, Node platform, String prefix) {
        NamedNodeMap attr = platform.getAttributes();
        project.setProperty(prefix + ".app.id", attr.getNamedItem("id").getTextContent());
        project.setProperty(prefix + ".app.version", attr.getNamedItem("version").getTextContent());

        NodeList children = platform.getChildNodes();
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node child = children.item(i);
            String nodeName = child.getNodeName();
            if ("preference".equals(nodeName)) {
                NamedNodeMap pref = child.getAttributes();
                String name = pref.getNamedItem("name").getTextContent();
                String value = pref.getNamedItem("value").getTextContent();
                project.setProperty(prefix + "." + name, value);
            } else if ("sign_info".equals(nodeName)) {
                // for IOS
                project.setProperty(prefix + ".codeSign", child.getTextContent());
            }
        }
    }

    private void parseAndroid(Project project, Document document) {
        Node android = document.getElementsByTagName("android").item(0);
        parsePlatform(project, android, "android");
    }

    private void parseIOS(Project project, Document document) {
        Node ios = document.getElementsByTagName("iphone").item(0);
        parsePlatform(project, ios, "ios");
    }

    private void parseWAC(Project project, Document document) {
        Node wac = document.getElementsByTagName("wac").item(0);

        NamedNodeMap attr = wac.getAttributes();
        Node minVersion = attr.getNamedItem("min-version");
        if (minVersion != null) {
            project.setProperty("wac.minVersion", minVersion.getTextContent());
        }
    }

    private static class AxPlugin {
        String id;
        String path;

        public AxPlugin(String id, String location) {
            this.id = id;
            this.path = location;
        }
    }

    @Override
    public void setConfigXML(String config) {
        this.configXml = config;
    }

    @Override
    public void setPluginsCacheDir(String pluginsCacheDir) {
        this.pluginsCacheDir = new File(pluginsCacheDir);
    }

    @Override
    public void setBuiltInPluginsDir(String builtinPluginsDir) {
        this.builtinPluginsDir = new File(builtinPluginsDir);
        Pattern pattern = Pattern.compile("(.*)-(.*)\\.(axp)");

        String[] plugins = this.builtinPluginsDir.list();
        for (String plugin : plugins) {
            Matcher m = pattern.matcher(plugin);

            if (m.find()) {
                this.builtInAxp.add(m.group(1));
            }
        }
    }

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void setAxSdkDir(String axSdkDir) {
        this.axSdkDir = axSdkDir;
    }

    @Override
    public void setTargetPlatform(String targetPlatform) {
        this.targetPlatform = targetPlatform;
    }

    public boolean isIOSRunOnDebug() {
        String proj = this.project.getProperty("ios.config.nessie.project");
        String host = this.project.getProperty("ios.config.nessie.host");
        String port = this.project.getProperty("ios.config.nessie.port");

        return proj != null && host != null && port != null;
    }
}
