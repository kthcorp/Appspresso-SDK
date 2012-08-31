package com.appspresso.cli.ant.ios.app;

import java.io.File;
import java.io.FileNotFoundException;

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
			copyAxPluginResources();
			copyOverlay();
		}
		catch (FileNotFoundException e) {
			throw new BuildException(e);
		}
	}

	private void copyAxPluginResources() throws FileNotFoundException {
		String application = getProject().getProperty("app.runtime.project.name");
		File ax_assets = new File(getRuntimeProjectDir(), application + "/assets");

		Project proj = getProject();
		int length = IOSBuildUtils.getPluginLength(getProject());
		for (int i = 0; i < length; i++) {
			Copy copy = (Copy) getProject().createTask("copy");
			copy.setTodir(ax_assets);

			FileSet fileset = new FileSet();
			fileset.setDir(new File(IOSBuildUtils.getPluginPath(proj, i)));

			fileset.createInclude().setName("axplugin.xml");
			fileset.createInclude().setName("axplugin.js");
			fileset.createInclude().setName("res/ios/**");
			copy.addFileset(fileset);

			String id = IOSBuildUtils.getPluginID(proj, i);
			FirstMatchMapper mapper = createMapperForPlugin(IOSBuildUtils.getPluginID(proj, i));
			copy.add(mapper);

			log("copy resources of " + id);
			copy.perform();
		}
	}

	private String getRuntimeProjectDir() {
		return getProject().getProperty("app.runtime.project.dir");
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
		int length = IOSBuildUtils.getPluginLength(proj);

		for (int i = 0; i < length; i++){
			File overlay = new File(IOSBuildUtils.getPluginPath(proj, i), "overlay/ios");
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
}
