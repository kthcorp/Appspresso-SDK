package com.appspresso.cli.ant.android.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FirstMatchMapper;
import org.apache.tools.ant.util.GlobPatternMapper;

public class AttachAxp extends Task {

	@Override
	public void execute() throws BuildException {
		try {
			readyPluginForBuild();

			copyAxPluginResources();
			copyOverlay();
		}
		catch (FileNotFoundException e) {
			throw new BuildException(e);
		}
		catch (IOException e) {
			throw new BuildException(e);
		}
	}

	private void copyAxPluginResources() throws FileNotFoundException {
		File ax_assets = new File(getRuntimeProjectDir(), "assets");

		Project proj = getProject();
		int length = AndroidBuildUtils.getPluginLength(getProject());
		for (int i = 0; i < length; i++) {
			Copy copy = (Copy) getProject().createTask("copy");
			copy.setTodir(ax_assets);

			FileSet fileset = new FileSet();
			fileset.setDir(new File(AndroidBuildUtils.getPluginPath(proj, i)));

			fileset.createInclude().setName("axplugin.xml");
			fileset.createInclude().setName("axplugin.js");
			fileset.createInclude().setName("res/android/**");
			copy.addFileset(fileset);

			String id = AndroidBuildUtils.getPluginID(proj, i);
			FirstMatchMapper mapper = createMapperForPlugin(AndroidBuildUtils.getPluginID(proj, i));
			copy.add(mapper);

			log("copy resources of " + id);
			copy.perform();
		}
	}

	private FirstMatchMapper createMapperForPlugin(String name) {
		GlobPatternMapper script = new GlobPatternMapper();
		script.setFrom("axplugin.js");
		script.setTo("ax_scripts/" + name + ".js");

		GlobPatternMapper xml = new GlobPatternMapper();
		xml.setFrom("axplugin.xml");
		xml.setTo("ax_plugins/" + name + ".xml");

		GlobPatternMapper res = new GlobPatternMapper();
		res.setFrom("res/android/*");
		res.setTo("ax_res/" + name + "/*");

		FirstMatchMapper mapper = new FirstMatchMapper();
		mapper.add(script);
		mapper.add(xml);
		mapper.add(res);

		return mapper;
	}

	private void copyOverlay() throws FileNotFoundException {
		File runtimeProjectDir = new File(getRuntimeProjectDir());
		Project proj = getProject();
		int length = AndroidBuildUtils.getPluginLength(proj);

		for (int i = 0; i < length; i++){
			File overlay = new File(AndroidBuildUtils.getPluginPath(proj, i), "overlay/android");
			if (!overlay.exists()) {
				continue;
			}

			FileSet fileset = new FileSet();
			fileset.setDir(overlay);

			Copy copy = (Copy) getProject().createTask("copy");
			copy.setTodir(runtimeProjectDir);
			copy.setOverwrite(true);
			copy.addFileset(fileset);
			copy.perform();
		}
	}

	private String getRuntimeProjectDir() {
		return getProject().getProperty("app.runtime.project.dir");
	}

	private void readyPluginForBuild() throws IOException  {
		// project.properties, local.properties, build.xml
		Properties local = new Properties();
		local.put("sdk.dir", getProject().getProperty("android.sdk.dir"));

		Properties project = new Properties();
		File buildXml = new File(getProject().getProperty("ax.android.templates.app.dir"), "base/build.xml");

		Project proj = getProject();
		int length = AndroidBuildUtils.getPluginLength(proj);
		for (int i = 0; i < length; i++) {
			String id = AndroidBuildUtils.getPluginID(proj, i);
			log(id + " plugins...");
			File moduleFile = new File(AndroidBuildUtils.getPluginPath(proj, i), "lib/android");
			if (moduleFile == null || !moduleFile.exists()) {
				log("warning : " + id + " don't have android module");
				continue;
			}
			project.put("target", "android-" + 15);
			project.put("android.library", "true");

			OutputStream localOS = new FileOutputStream(new File(moduleFile, "local.properties"));
			local.store(localOS, "");
			localOS.close();

			OutputStream projectOS = new FileOutputStream(new File(moduleFile, "project.properties"));
			project.store(projectOS, "");
			projectOS.close();

			Copy copy  = (Copy) getProject().createTask("copy");
			copy.createFilterSet().addFilter("PACKAGE", id);
			copy.setFile(buildXml);
			copy.setTofile(new File(moduleFile, "build.xml"));
			copy.perform();
		}
	}
}
