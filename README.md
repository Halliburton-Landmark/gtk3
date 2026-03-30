How to build
================================================
First see [related section](#prepare-build-environment) to configure build environment.

1. Clone this repository 
```
git clone https://github.com/Halliburton-Landmark/gtk3.git
```

2. Create folders under $HOME
```
cd $HOME
mkdir rpmbuild
cd rpmbuild
mkdir SOURCES
mkdir SPECS
```

Download GTK sources
```
cd SOURCES
wget http://download.gnome.org/sources/gtk+/3.24/gtk+-%{version}.tar.xz
```
3. Build RPM by running:
```
cd $HOME/rpmbuild/SPECS
rpmbuild -bb gtk3.spec
```

   RPMS will be populated in $HOME/rpmbuild/RPMS.
   
4. Execute tests from manual-tests folder to test the patch.
   Firt, unpack the following RPMs under $HOME/rpmbuild/RPMS.
   
```
gtk3
gtk3-immodules
gtk3-immodule-xim
gtk-update-icon-cache
```   
   Then run the test apps with environment variables set as follows:
   
```
gtk_usr_dir="$HOME/rpmbuild/RPMS/usr"
export LD_LIBRARY_PATH="${gtk_usr_dir}/lib64:${LD_LIBRARY_PATH}"
    
# Specify path to printbackends and immodules
export GTK_PATH="${gtk_usr_dir}/lib64/gtk-3.0/3.0.0"

export PATH="${gtk_usr_dir}/bin:${PATH}"
```

Prepare build environment
================================================
Steps below assume that build will be run on RH 9.7:

Install required packages
```
yum isntall cups-devel \
   gtk-doc \
   meson \
   avahi-gobject-devel \
   colord-devel \
   gobject-introspection-devel \
   tracker-devel \
   wayland-protocols-devel
```

See also https://docs.gtk.org/gtk3/building.html
