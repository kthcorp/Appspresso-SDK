package com.appspresso.cli.ant.common.app;

import org.apache.tools.ant.Project;


interface AxAppProject {
	public void process(Project project) throws Exception;

	public void setConfigXML(String config);

	public void setPluginsCacheDir(String pluginsCacheDir);

	public void setBuiltInPluginsDir(String builtinPluginsDir);

	public void setProject(Project project);

	public void setAxSdkDir(String axSdkDir);

	public void setTargetPlatform(String targetPlatform);
}
