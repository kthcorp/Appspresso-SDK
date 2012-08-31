package com.appspresso.migration.app.v1_1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Scanner;

import com.appspresso.migration.Log;
import com.appspresso.migration.app.v1.AxProjectXML_V1;
import com.appspresso.migration.app.v1.AxProject_V1;
import com.appspresso.migration.exception.MigrationException;

public class Platforms_V1_1 {
	private File project;

	public void migrate(AxProject_V1 from) throws MigrationException {
		if (Log.isInfoEnabled()) {
			Log.info("create platforms directory(icon, splash, appname...)");
		}

		// create platforms
		File platforms = new File(this.project, "platforms");
		mkdirs(platforms);

		makeAndroid(platforms, from);
		makeIOS(platforms, from);
	}

	public void makeAndroid(File platforms, AxProject_V1 from) throws MigrationException {
		AxProjectXML_V1 xml = from.getAxProjectXML();
		try {
			File android = new File(platforms, "Android");
			mkdirs(android);

			// write manifest
			if (Log.isInfoEnabled()) {
				Log.info("create AndroidManifest.xml");
			}
			File manifest = new File(android, "AndroidManifest.axml");
			InputStream manifestInput = ClassLoader.getSystemResourceAsStream("com/appspresso/migration/app/v1_1/AndroidManifest.axml");
			write(manifest, manifestInput);

			InputStream readmeInput = ClassLoader.getSystemResourceAsStream("com/appspresso/migration/app/v1_1/README_android");
			if (readmeInput != null) {
				File readme = new File(android, "README");
				write(readme, readmeInput);
			}

			// make resources
			File resources = new File(android, "resources");
			mkdirs(resources);

			// app name
			if (Log.isInfoEnabled()) {
				Log.info("create appName.xml");
			}
			File appName = new File(resources, "appName.xml");
			String title = xml.getTitle();
			Writer writer = new PrintWriter(new FileOutputStream(appName));
			writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
				.append("<resources>\n")
				.append("<app-name>")
				.append(title)
				.append("</app-name>\n")
				.append("</resources>\n");
			writer.close();

			File appSrc = new File(from.getDir(), "src");
			Map<String, String> pref = xml.getAndroid().getPreferences();

			// icon
			if (Log.isInfoEnabled()) {
				Log.info("copy icon files...");
			}
			File iconDir = new File(resources, "icon");

			File icon = resolveFile(appSrc, xml.getWidget().getIcons().get(0).getSrc());
			copyAndroidIcon(iconDir, null, icon);
			copyAndroidIcon(iconDir, "hdpi", resolveFile(appSrc, pref.get("icon_high")));
			copyAndroidIcon(iconDir, "mdpi", resolveFile(appSrc, pref.get("icon_middle")));
			copyAndroidIcon(iconDir, "ldpi", resolveFile(appSrc, pref.get("icon_low")));

			// splash
			File splash = new File(resources, "splash");
			File splashImg = resolveFile(appSrc, pref.get("splash"));
			if (splashImg != null) {
				if (Log.isInfoEnabled()) {
					Log.info("copy Android Splash image file...");
				}

				write(new File(splash, "splash/splash." + getExtension(splashImg.getName())),
						new FileInputStream(splashImg));
			}
		}
		catch (IOException e) {
			throw new MigrationException(e);
		}
	}

	private void copyAndroidIcon(File iconDir, String suffix, File src) throws IOException, MigrationException {
		if (src == null)
			return;

		String dir = "icon";
		if (suffix != null) {
			dir = dir + "." + suffix;
		}
		File newIconFile = new File(iconDir, dir + "/icon." + getExtension(src.getName()));
		if (Log.isInfoEnabled()) {
			Log.info("copy Android icon file : " + newIconFile.getAbsolutePath());
		}
		try {
			write(newIconFile, new FileInputStream(src));
		}
		catch (FileNotFoundException e) {
			Log.info("File not found : " + src.getAbsolutePath());
		}

	}

	public void makeIOS(File platforms, AxProject_V1 from) throws MigrationException {
		AxProjectXML_V1 xml = from.getAxProjectXML();
		try {
			File ios = new File(platforms, "iOS");
			mkdirs(ios);

			// write InfoPlist.xml
			if (Log.isInfoEnabled()) {
				Log.info("create InfoPlist.xml");
			}
			File infoPlist = new File(ios, "Info.plist.axml");
			InputStream in = ClassLoader.getSystemResourceAsStream("com/appspresso/migration/app/v1_1/Info.plist.axml");
			PrintWriter plistWriter = new PrintWriter(infoPlist);
			Scanner plistScanner = new Scanner(in);
			while(plistScanner.hasNextLine()){
				String line = plistScanner.nextLine();
				if (line.contains("@APP_NAME@")) {
					line = line.replace("@APP_NAME@", from.getAxProjectXML().getTitle());
				}
				plistWriter.println(line);
			}
			try {
				plistWriter.close();
				plistScanner.close();
			}
			catch (Exception e) {
			}

			InputStream readmeInput = ClassLoader.getSystemResourceAsStream("com/appspresso/migration/app/v1_1/README_ios");
			if (readmeInput != null) {
				File readme = new File(ios, "README");
				write(readme, readmeInput);
			}

			// make resources
			File resources = new File(ios, "resources");
			mkdirs(resources);

			// app name
			if (Log.isInfoEnabled()) {
				Log.info("create appName.xml");
			}
			File appName = new File(resources, "appName.xml");
			String title = xml.getTitle();
			Writer writer = new PrintWriter(new FileOutputStream(appName));
			writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
				.append("<resources>\n")
				.append("<app-name lang=\"en\">")
				.append(title)
				.append("</app-name>\n")
				.append("</resources>\n");
			writer.close();

			File appSrc = new File(from.getDir(), "src");
			Map<String, String> pref = xml.getIOS().getPreferences();

			// icon
			if (Log.isInfoEnabled()) {
				Log.info("copy icon files...");
			}

			File iconDir = new File(resources, "icon");
			copyIOSIcons(new File(iconDir, "Icon.png"), resolveFile(appSrc, pref.get("icon1")));
			copyIOSIcons(new File(iconDir, "Icon@2x.png"), resolveFile(appSrc, pref.get("icon2")));
			copyIOSIcons(new File(iconDir, "Icon-72.png"), resolveFile(appSrc, pref.get("icon3")));

			// splash
			File splash = new File(resources, "splash");
			File splashImg = resolveFile(appSrc, pref.get("splashImg"));
			if (splashImg != null) {
				if (Log.isInfoEnabled()) {
					Log.info("copy iOS Splash image file...");
				}

				write(new File(splash, "splash.en/Default.png"), new FileInputStream(splashImg));
			}
		}
		catch (IOException e) {
			throw new MigrationException(e);
		}
	}

	private void copyIOSIcons(File to, File from) throws IOException, MigrationException {
		if (from == null) {
			return;
		}

		if (Log.isInfoEnabled()) {
			Log.info("copy iOS icon file : " + to.getAbsolutePath());
		}

		write(to, new FileInputStream(from));
	}

	private String getExtension(String filename) {
		int lastIndex = filename.lastIndexOf(".");
		return filename.substring(lastIndex + 1, filename.length());
	}

	private void write(File to, InputStream in) throws IOException, MigrationException {
		if (in == null) {
			throw new IOException("input stream is null");
		}

		if (Log.isDebugEnabled()) {
			Log.debug("write : " + to.getAbsolutePath());
		}

		mkdirs(to.getParentFile());

		OutputStream out = new BufferedOutputStream(new FileOutputStream(to));
		int read = -1;
		byte[] b = new byte[1024 * 4];
		while ((read = in.read(b)) > -1) {
			out.write(b, 0, read);
		}

		in.close();
		if (out != null)
			out.close();
	}

	private void mkdirs(File directory) throws MigrationException {
		if (!directory.exists() && !directory.mkdirs()) {
			throw new MigrationException("can't create " + directory.getName() + " directory");
		}
	}

	private File resolveFile(File based, String path) {
		if (path == null || "".equals(path.trim())) {
			return null;
		}

		File file = new File(based, path);
		return file.exists() ? file : null;
	}

	public static Platforms_V1_1 create(String project) {
		Platforms_V1_1 self = new Platforms_V1_1();
		self.project = new File(project);

		return self;
	}

}
