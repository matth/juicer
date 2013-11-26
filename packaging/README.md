## Building the juicer debian package

### Use the Vagrant file if you're not on debian / ubuntu

    vagrant up

### Install packages required for build

    sudo apt-get install ubuntu-dev-tools dh-make javahelper
    sudo apt-get install default-jdk
    sudo apt-get install git

### Build debian package

    make clean package-debian
    sudo pbuilder --create
    sudo pbuilder --build pkg/*.dsc

### Pushing packages to launchpad

Ensure your `~/.dput.cf` has this statement in

    [launchpad]
    fqdn                   = ppa.launchpad.net
    method                 = ftp
    incoming               = ~juicer-server/juicer/ubuntu/
    login                  = anonymous
    allow_unsigned_uploads = 1

Then run

    make clean package-debian-launchpad
    debsign -k KEYID pkg/*.dsc
    debsign -k KEYID pkg/*.changes
    dput ppa:juicer-server/juicer pkg/*.changes
