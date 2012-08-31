package com.appspresso.cli.ant.common.app;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class ParseProjectXMLTask extends Task {

	private String path;
	private String config;
	private String pluginsCacheDir;
	private String builtinPluginsDir;
	private String axSdkDir;
	private String targetPlatform;

	public void setTargetPlatform(String targetPlatform) {
		this.targetPlatform = targetPlatform;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setAxSdkDir(String sdkdir) {
		this.axSdkDir = sdkdir;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setPluginsCacheDir(String pluginsCacheDir) {
		this.pluginsCacheDir = pluginsCacheDir;
	}

	public void setBuiltinPluginsDir(String builtinPluginsDir) {
		this.builtinPluginsDir = builtinPluginsDir;
	}

	@Override
	public void execute() throws BuildException {
		try {
			// validation check
			// ..path
			// ..config

			AxAppProject project = AxAppProject_V1_1.createInstance(this.path);
			project.setProject(getProject());
			project.setTargetPlatform(this.targetPlatform);
			project.setAxSdkDir(this.axSdkDir);
			project.setBuiltInPluginsDir(this.builtinPluginsDir);
			project.setPluginsCacheDir(this.pluginsCacheDir);
			project.setConfigXML(this.config);
			project.process(getProject());
		}
		catch (Exception e) {
			throw new BuildException(e);
		}
	}

}
