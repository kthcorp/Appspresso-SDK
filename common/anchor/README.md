
How to add a Appspresso application template
============================================

Create new template
--------------------------------------------
A application template must contain predefined files and folders to build it using Appspresso. By copying '_blank' or '_localization' folder, you can use it as a basis for a new template folder. And then change the contents to suit your new needs. 

    # project contents except for platforms folder  
    /anchor-common
      /templates
        /mytemplate
          /.metadata
            /icon.png               .... Template Icon
            /description.html       .... Template Description
          /src
            /mytemplate.html
            /...
    
    # platforms folder contents of a project
    /anchor-android(or ios)
      /templates
        /mytemplate
          /resources
            /...

Add a element about new template to app-template.xml and set template folder name and template display name.

    # anchor-common/templates/app/app-template.xml
    <template version="1.0" default="_blank">
      ..
      <outline name="mytemplate"    // Template Folder Name
               text="My Hello World"/> // Template Display Name
    </template>

![](http://appspresso.com/git/app_template.png)

Edit build script
--------------------------------------------
/anchor-common/build.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <project name="anchor-common" default="build">
        ....
        <copy.template.app template.name="_blank" />
        <copy.template.app template.name="_localization" />

        <copy.template.app template.name="mytemplate" />
        ....
    </project>

/anchor-android/build.xml, /anchor-ios/build.xml

    <project name="anchor-android" default="build">
        ....
        <copy.template.app template.name="_blank" />
        <copy.template.app template.name="_localization" />
        
        <copy.template.app template.name="mytemplate" />
        ....
    </project>