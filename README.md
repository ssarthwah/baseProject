# AEM Sample Project
This sample project showcases what the maven setup of a typical AEM project and some code samples.

# Prerequisites
Make sure you have the following installed:

- Java 7 or higher (Java 6 may work)
- Maven 3.2.3 or higher (lower versions may work)
- Running AEM 6 instance (other versions should work)


# Modules
The main parts of the template are:

- core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
- ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs as well as Hobbes-tests
- ui.content: contains sample content using the components from the ui.apps
- it.tests: Java bundle containing JUnit tests that are executed server-side. This bundle is not to be deployed onto production.
- it.launcher: contains glue code that deploys the it.tests bundle (and dependent bundles) to the server and triggers the remote JUnit execution


# How to build
To build all the modules run in the project root directory the following command with Maven 3:

- mvn clean install
If you have a running AEM instance you can build and package the whole project and deploy into AEM with

- mvn clean install -PautoInstallPackage
Or to deploy it to a publish instance, run

- mvn clean install -PautoInstallPackagePublish
Or to deploy only the bundle to the author, run

- mvn clean install -PautoInstallBundle
