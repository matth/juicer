NAME     := juicer
VERSION  := 0.0.6
RELEASE  := 1
PKGNAME  := $(NAME)_$(VERSION)
DPKGNAME := $(PKGNAME)-$(RELEASE)
VCSURL	 := https://github.com/matth/juicer.git
PKGDIR	 := pkg

$(PKGDIR):
	mkdir -p $(PKGDIR)

.PHONY: clean
clean:
	rm -rf $(PKGDIR)

$(PKGDIR)/$(PKGNAME).orig.tar.gz: $(PKGDIR)
	$(eval TMPDIR := $(shell mktemp -d))
	git clone $(VCSURL) $(TMPDIR)
	cd $(TMPDIR) && rm -rf .git && tar -cvz -f $(realpath $<)/$(PKGNAME).orig.tar.gz .
	rm -rf $(TMPDIR)

.PHONY: package
package: $(PKGDIR)/$(PKGNAME).orig.tar.gz

$.PHONY: package-debian
package-debian: package
	mkdir -p $(PKGDIR)/$(DPKGNAME)
	tar -C $(PKGDIR)/$(DPKGNAME) -xvzf $(PKGDIR)/$(PKGNAME).orig.tar.gz
	cp -r debian $(PKGDIR)/$(DPKGNAME)
	cd $(PKGDIR)/$(DPKGNAME) && dpkg-buildpackage -us -uc
	cd $(PKGDIR) && lintian -i -I --show-overrides *.changes

# This is what it takes to build a java package on Launchpad :) Please see
# the issue at https://answers.launchpad.net/launchpad/+question/239751
$.PHONY: package-debian-launchpad
package-debian-launchpad: package
	mkdir -p $(PKGDIR)/$(DPKGNAME)
	tar -C $(PKGDIR)/$(DPKGNAME) -xvzf $(PKGDIR)/$(PKGNAME).orig.tar.gz
	cp -r debian $(PKGDIR)/$(DPKGNAME)
	cd $(PKGDIR)/$(DPKGNAME) && mkdir debian/sbt-boot
	cd $(PKGDIR)/$(DPKGNAME) && mkdir debian/ivy-cache
	cd $(PKGDIR)/$(DPKGNAME) && ./debian/sbt update test assembly stage
	cd $(PKGDIR)/$(DPKGNAME) && find debian/sbt-boot/ | grep jar >> debian/source/include-binaries
	cd $(PKGDIR)/$(DPKGNAME) && find debian/ivy-cache/ | grep jar >> debian/source/include-binaries
	cd $(PKGDIR)/$(DPKGNAME) && mv lib_managed debian/
	cd $(PKGDIR)/$(DPKGNAME) && find debian/lib_managed/ | grep jar >> debian/source/include-binaries
	cd $(PKGDIR)/$(DPKGNAME) && find . -type d -name target | xargs rm -rf
	cd $(PKGDIR)/$(DPKGNAME) && dpkg-buildpackage -S -us -uc
	cd $(PKGDIR) && lintian -i -I --show-overrides *.changes
