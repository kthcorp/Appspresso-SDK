package com.appspresso.cli.ant.ios.app;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FilterSet;


public class PrepareFilterSet extends Task {
    private static FilterSet filterset = null;

    private String property;

    @Override
    public void execute() throws BuildException {
        try {
            prepare(getProject());

            getProject().addReference(this.property, getFilterSet());
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public static FilterSet getFilterSet() {
        return PrepareFilterSet.filterset;
    }

    private void prepare(Project project) throws Exception {
        if (filterset != null) { return; }
        filterset = new FilterSet();

        filterset.addFilter("AX_IOS_LIB",
                "\"" + new File(project.getProperty("ax.ios.dir"), "lib").getAbsolutePath() + "\"");
        // ////////////////////////////////////////////////////////////////////////////////////////
        // application-Info.plist
        project.log("make Info.plist file");
        filterset.addFilter("CFBundleIdentifier", getProperty(project, "ios.app.id"));

        // ////////////////////////////////////////////////////////////////////////////////////////

        filterset.addFilter("CFBundleShortVersionString", getProperty(project, "ios.app.version"));
        filterset.addFilter("CFBundleVersion", getProperty(project, "ios.buildVersionNumber"));

        // XXX ................................코드가 지저분하게 보이는 것은 기분 탓만이 아닙니다....(-O-)
        addFilterForSplashOrientation(project, filterset);

        String value = null;
        value = getProperty(project, "ios.exitOnSuspend");
        filterset.addFilter("UIApplicationExitsOnSuspend", value == null || "".equals(value)
                ? ""
                : "<key>UIApplicationExitsOnSuspend</key>\n<" + value + "/>");

        value = getProperty(project, "ios.uiStatusBarHidden");
        filterset.addFilter("UIStatusBarHidden", value == null || "".equals(value)
                ? ""
                : "<key>UIStatusBarHidden</key>\n<" + value + "/>");

        value = getProperty(project, "ios.uiStatusBarStyle");
        filterset.addFilter("UIStatusBarStyle", value == null || "".equals(value)
                ? ""
                : "<key>UIStatusBarStyle</key>\n<string>" + value + "</string>");

        value = getProperty(project, "ios.uiPrerenderedIcon");
        filterset.addFilter("UIPrerenderedIcon", value == null || "".equals(value)
                ? ""
                : "<key>UIPrerenderedIcon</key>\n<" + value + "/>");

        // ////////////////////////////////////////////////////////////////////////////////////////
        // build.xcconfig
        project.log("make build.xcconfig");

        int length = IOSBuildUtils.getPluginLength(project);

        StringBuilder libs = new StringBuilder();
        StringBuilder libDirs = new StringBuilder();
        for (int i = 0; i < length; i++) {
            File iosModuleDir = new File(IOSBuildUtils.getPluginPath(project, i), "lib/ios");
            if (iosModuleDir != null && iosModuleDir.exists()) {
                project.log("plugin : " + iosModuleDir);
                libDirs.append(" -L\"" + iosModuleDir + "\"");

                File[] files = iosModuleDir.listFiles();
                if (files == null) {
                    continue;
                }
                for (File file : files) {
                    String fileName = file.getName();
                    if (fileName.startsWith("lib") && fileName.endsWith(".a")) {
                        String lib = fileName.substring(3, fileName.length() - 2);
                        libs.append(" -l" + lib);
                    }
                }
            }
        }

        filterset.addFilter("PLUGIN_LIB", libs.toString());
        filterset.addFilter("PLUGIN_LIB_DIR", libDirs.toString());

        iteratePluginPropertiesWithFilterSet(project, filterset);
    }

    private static void addFilterForSplashOrientation(Project project, FilterSet filterset) {
        String userValue = getProperty(project, "ios.initOrientation");
        if (userValue != null && !"".equals(userValue)) {
            String value = IOSUtils.getUIInterfaceOrientation(Integer.parseInt(userValue));
            filterset.addFilter("UIInterfaceOrientation",
                    "<key>UIInterfaceOrientation</key>\n<string>" + value + "</string>");
        }
        else {
            filterset.addFilter("UIInterfaceOrientation", "");
        }
    }

    private static void iteratePluginPropertiesWithFilterSet(Project project, FilterSet filterset)
            throws Exception {
        StringBuilder fxStringBuilder = new StringBuilder();
        StringBuilder dylibStringBuilder = new StringBuilder();

        int length = IOSBuildUtils.getPluginLength(project);
        for (int i = 0; i < length; i++) {
            String[] frameworks = IOSBuildUtils.getPropertyValueWithName(project, i, "framework");
            String[] dylibs = IOSBuildUtils.getPropertyValueWithName(project, i, "dylib");

            for (String framework : frameworks) {
                for (String value : framework.split(",")) {
                    value = value.trim();
                    if (value.endsWith(".framework")) {
                        fxStringBuilder.append(" -framework "
                                + value.substring(0, value.length() - ".framework".length()));
                    }
                    else if (framework.length() > 0) {
                        fxStringBuilder.append(" -framework " + value);
                    }
                }
            }

            for (String dylib : dylibs) {
                for (String value : dylib.split(",")) {
                    value = value.trim();
                    if (value.startsWith("lib") && value.endsWith(".dylib")) {
                        dylibStringBuilder.append(" -l"
                                + value.substring(3, value.length() - ".dylib".length()));
                    }
                    else if (value.length() > 0) {
                        dylibStringBuilder.append(" -l" + value);
                    }
                }
            }
        }

        // Frameworks
        filterset.addFilter("PLUGIN_FRAMEWORK", fxStringBuilder.toString());
        filterset.addFilter("PLUGIN_DYLIB", dylibStringBuilder.toString());
    }

    protected static String getProperty(Project project, String key) {
        try {
            return (String) project.getProperties().get(key);
        }
        catch (Exception e) {
            return null;
        }
    }

    protected static String getProperty(Project project, String key, String defaultValue) {
        Object value = getProperty(project, key);
        return (value == null || "".equals(value) ? defaultValue : value.toString());
    }


}
